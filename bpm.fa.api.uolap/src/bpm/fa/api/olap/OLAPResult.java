package bpm.fa.api.olap;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import bpm.fa.api.item.Item;
import bpm.fa.api.item.ItemElement;
import bpm.fa.api.item.ItemNull;
import bpm.fa.api.item.ItemValue;

public class OLAPResult {
	private ArrayList<ArrayList<Item>> table;
	private int xfix;
	private int yfix;
	
	
	public OLAPResult(ArrayList<ArrayList<Item>> table, int x, int y) {
		this.table = table;
		this.xfix = x;
		this.yfix = y;
	}

	/**
	 * 
	 * @return
	 */
	public ArrayList<ArrayList<Item>> getRaw() {
		return table;
	}
	
	public int getXFixed() {
		return xfix;
	}
	
	public int getYFixed() {
		return yfix;
	}
	
	public void dump(){
		for(ArrayList<Item> row : getRaw()){
			for(Item i : row){
				System.out.print("\t|" + i.getLabel() + "\t|");
			}
			System.out.println();
		}
	}
	
	public OLAPResult clone(){
		ArrayList<ArrayList<Item>> content = new ArrayList<ArrayList<Item>>();
		
		for(ArrayList<Item> row : getRaw()){
			ArrayList<Item> r = new ArrayList<Item>();
			r.addAll(row);
			content.add(r);
		}
		
		
		OLAPResult r = new OLAPResult(content, xfix, yfix);
		return r;
	}

	public List<OLAPMember> getLevelMembers(Hierarchy hiera, Level l) {
		List<OLAPMember> result = new ArrayList<OLAPMember>();
		
		for(ArrayList<Item> row : getRaw()){
			for(Item i : row){
				if (i instanceof ItemElement){
					
					ItemElement it = (ItemElement)i;
					int lvlNb = hiera.getLevel().indexOf(l);
					if (it.getDataMember().getUniqueName().startsWith(hiera.getUniqueName()) && lvlNb == it.getDataMember().getUniqueName().split("\\]\\.").length - 3){
						result.add(it.getDataMember());
						if (it.getDataMember().getLevel() == null){
							it.getDataMember().setLevel(l);
						}
					}
					
				}
			}
			
			
		}
		
		return result;
	}
	
	
	public void addItem(ItemElement item){
		ArrayList<Item> newRow = new ArrayList<Item>();
//		if (item.isCol()){
			for(Item i : table.get(getXFixed() - 1)){
				if (i instanceof ItemNull){
					newRow.add(new ItemNull());
				}
				else if ( i instanceof ItemElement){
					newRow.add(item);
				}
				else {
					newRow.add(new ItemValue("", "", ""));
				}
			}
			table.add(getXFixed() , newRow);
			xfix ++;
//		}
//		else{
//			for(Item i : table.get(getXFixed())){
//				if (i instanceof ItemNull){
//					newRow.add(new ItemNull());
//				}
//				else if ( i instanceof ItemElement){
//					newRow.add(item);
//				}
//				else {
//					newRow.add(new ItemValue("", "", ""));
//				}
//			}
//			table.add(getYFixed(), newRow);
//		}
	}
	
	public void removeItem(ItemElement item){
		
	}
	
	public void swapAxes(){
		
	}
	
	private List<Point> findCoordinate(ItemElement element){
		List<Point> l = new ArrayList<Point>();
		
		for(int i = 0; i < table.size(); i++){
			for(int j = 0; j < table.get(i).size(); j++){
				if (table.get(i).get(j) instanceof ItemElement){
					if (((ItemElement)table.get(i).get(j)).getDataMember().getUniqueName().equals(element.getDataMember().getUniqueName())){
						l.add(new Point(j, i));
						break;
					}
				}
			}
		}
		
		return l;
	}
	
	public void drillDown(ItemElement drilled){
		

		
		List<Point> coords = findCoordinate(drilled);
		
		int colNumberToAdd = drilled.getDataMember().getMembers().size();
		int offset = 0;
		if (drilled.isCol()){
			Point pt = coords.get(0);
			ArrayList<Item> previousRow = null;
			if (pt.y < 1){
				
				previousRow = table.get(0);
				
			}
			else{
				previousRow = table.get(pt.y - 1);
			}
			
			/*
			 * 
			 */
			ArrayList<Item> newRow = new ArrayList<Item>();
			yfix++;
			table.add(pt.y + 1, newRow);
			
			for(int i = 0; i < previousRow.size(); i++){
				if (i<=getXFixed() + 1){
					newRow.add(new ItemNull());
				}
			}
			
			for(OLAPMember m :  drilled.getDataMember().getMembersVector()){
				newRow.add(new ItemElement(m, true, false));
				xfix++;
			}
			
			
			offset = colNumberToAdd * (previousRow.size() - pt.x);
//			
//		
			for(int i = 0; i < table.size(); i++){
				if (table.get(i) != newRow){
					for(int k = 0; k < offset; k++){
						table.get(i).add(new ItemNull());
					}
					
				}
				
			}
			
			
		}
//		dump();
	}
	
	
	public void drillUp(ItemElement drilled){
		
	}
	
}
