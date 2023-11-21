package bpm.fa.api.olap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import bpm.fa.api.item.Item;
import bpm.fa.api.item.ItemElement;
import bpm.fa.api.item.ItemValue;

public class OLAPResultFilter {
	private static class Filter{
		private String lvlId;
		private String dimName;
		private int lvlNb;
		public Filter(String dimName,String lvlId, int lvlNb) {
			this.lvlId = lvlId;
			this.lvlNb = lvlNb;
			this.dimName = dimName;
		}
		
	}
	
	private List<Filter> filters;
	
	List<Integer> rowIndex = new ArrayList<Integer>();
	List<Integer> colIndex = new ArrayList<Integer>();

	
	
	private List<Filter> getFilterForDim(String dimName){
		List<Filter> l = new ArrayList<Filter>();
		
		for(Filter f : filters){
			if (f.dimName.equals(dimName)){
				l.add(f);
			}
		}
		
		return l;
	}
	
	
	private List<String> createFilters(int lvlNb, String prec, List<Filter> filters){
		
		List<String> l = new ArrayList<String>();
		boolean found = false;		
		int max = 0;
		for(Filter f : filters){
			String last = null;
			if (max< f.lvlNb){
				max = f.lvlNb;
			}
			if (f.lvlNb == lvlNb){
				
				if (prec == null){
					last = "[" + f.lvlId + "]";
				}
				else{
					last = prec + "." + "[" + f.lvlId + "]";
				}
				l.add(last);
				found = true;
				l.addAll(createFilters(lvlNb+1, last, filters));
				
			}
		}
		
		if (!found && lvlNb < max){
			if (prec == null){
				l.addAll(createFilters(lvlNb+1, "*", filters));
			}
			else{
				l.addAll(createFilters(lvlNb+1, prec + ".[*]", filters));
			}
			
		}
		return l;
		
		
		
	}
	
	private String[] extractParts(String uname){
		String parts[] = uname.split("\\]\\.\\[");
		for(int i = 0; i < parts.length; i++){
			parts[i] = parts[i].replace("[", "").replace("]", "");
		}
		return parts;
		
	}
	
	private boolean doesRemove(OLAPMember member){
		String parts[] = extractParts(member.getUniqueName());
		List<Filter> fs = getFilterForDim( parts[0]);
		
		if (fs.isEmpty()){
			return false;
		}
		
		String rootName = "[" + parts[0] + "].[" + parts[1] + "]"; 
		
		
		List<String> filters = createFilters(3, rootName, fs);
		filters.add(rootName);
		
		for(String s : filters){
			
			String[] fParts = extractParts(s);
			boolean match = true;
			for(int i = 0; i < parts.length; i++){
				if (i >= fParts.length ){
//					if (!member.getUniqueName().startsWith(s)){
//						match = false;
//					}
					match = false;
					break;
				}
				if (!fParts[i].equals("*")){
					if (!fParts[i].equals(parts[i])){
						match = false;
						break;
					}
				}
			}
			
			if (match){
				return false;
			}
		}
		
		return true;
		

	}
	
	
	
	


	public OLAPResult filter(OLAPResult res, List<OLAPMember> members){
		OLAPResult result = res.clone();
		ArrayList<ArrayList<Item>> datas = result.getRaw(); 
		
		filters = new ArrayList<Filter>();
		
		for(OLAPMember m : members){
			String[] sp = extractParts(m.getUniqueName());//.split("\\]\\.");
			filters.add(new Filter( sp[0], sp[sp.length - 1], sp.length));
		}
		
		
		// detect index To Remove
		for(int i = 0; i < datas.size(); i++){
			for(int j = 0; j < datas.get(0).size(); j++){
				
				if (datas.get(i).get(j) instanceof ItemElement){
					ItemElement e = (ItemElement)datas.get(i).get(j);

					if (doesRemove(e.getDataMember())){
						if (e.isRow()){
							addRowIndex(i);
						}
						else{
							addColIndex(j);
						}
					}
				}
			}
		}
		
		//remove cols
		Collections.sort(colIndex);
		for(ArrayList<Item> row : datas){
			
			int offset = 0;
			for(Integer i : colIndex){
				row.remove(i - offset);
				offset++;
			}
			
		}
		
		//removeRows
		Collections.sort(rowIndex);
		int offset = 0;
		for(Integer k : rowIndex){
			datas.remove(k - offset);
			offset++;
		}
		
//		result.dump();
		
		
		
		int ifirst = 0;
		int jfirst = 0;
		
		for(int i = 0; i < datas.size(); i++){
			for(int j = 0; j < datas.get(0).size(); j++){
				if (datas.get(i).get(j) instanceof ItemValue){
					if (ifirst == 0 || i<ifirst){
						ifirst = i;
					}
					
					if (jfirst == 0 || j<jfirst){
						jfirst = j;
					}
					
//					break;
				}
			}
		}
		
		
		return new OLAPResult(datas, ifirst, jfirst);
	}
	
	private void addRowIndex(int i ){
		for(Integer k : rowIndex){
			if (i == k.intValue()){
				return;
			}
		}
		rowIndex.add(i);
	}
	
	private void addColIndex(int i ){
		for(Integer k : colIndex){
			if (i == k.intValue()){
				return;
			}
		}
		colIndex.add(i);
	}
}
