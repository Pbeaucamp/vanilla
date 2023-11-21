package bpm.fa.ui.composite.chart;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import bpm.fa.api.item.Item;
import bpm.fa.api.item.ItemElement;
import bpm.fa.api.item.ItemNull;
import bpm.fa.api.item.ItemValue;
import bpm.fa.api.olap.Measure;
import bpm.fa.api.olap.OLAPMember;
import bpm.fa.api.olap.OLAPQuery;
import bpm.fa.api.olap.OLAPResult;

public class ResultDataExtractor {
	private OLAPQuery query;
	private OLAPResult res;
	
	public ResultDataExtractor(OLAPQuery query, OLAPResult res){
		this.query = query;
		this.res = res;
	}
	
	
	
	private List<Integer> getMeasureCoordinate(Measure m){
		List<Integer> lp = new ArrayList<Integer>();
		
		for(int i = res.getXFixed(); i < res.getRaw().size(); i++){
			for(int j = 0; j < res.getYFixed(); j++){
				Item itm = res.getRaw().get(i).get(j);
				if (itm instanceof ItemElement){
					if (m.getUniqueName().equals(((ItemElement)itm).getDataMember().getUniqueName())){
						if (!lp.contains(i)){
							lp.add(i);
						}
					}
				}
				else if (itm instanceof ItemNull){
					for(int k = j -1; k > 0; k--){
						Item _itm = res.getRaw().get(i).get(k);
						if (_itm instanceof ItemElement && !lp.contains(j)){
							
							if (!(res.getRaw().get(i - 1).get(j ) instanceof ItemElement)){
								lp.add(j);
								break;
							}
								
							
						}
					}
				}
				
			}
		}
		
		return lp;
		
	}
	
	private HashMap<Point, String> getCoordinates2(boolean isCol, List<OLAPMember> mbs){
		HashMap<String, List<Integer>> pos = new HashMap<String, List<Integer>>();
		List<Integer> otherAxe = new ArrayList<Integer>();
		
		
		if (!isCol){
			for(int i = res.getYFixed(); i < res.getRaw().get(0).size(); i++){
				otherAxe.add(i);
			}
			
			
			
			for(int i = res.getXFixed(); i < res.getRaw().size(); i++){
				for(int j = 0; j < res.getYFixed(); j++){
					Item itm = res.getRaw().get(i).get(j);
					if (itm instanceof ItemElement){
						for(OLAPMember m : mbs){
							if (m.getUniqueName().equals(((ItemElement)itm).getDataMember().getUniqueName())){
								
								if (pos.get(m.getCaption()) == null){
									pos.put(m.getCaption(), new ArrayList<Integer>());
								}
								
								if (!((List)pos.get(m.getCaption())).contains(i)){
									((List)pos.get(m.getCaption())).add(i);
								}
							}
						}
					}
					else if (itm instanceof ItemNull){
						for(int k = i -1; k > 0; k--){
							Item _itm = res.getRaw().get(k).get(j);
							if (_itm instanceof ItemElement){
								if (pos.get(((ItemElement)_itm).getDataMember().getCaption()) != null){
									if (!(res.getRaw().get(i).get(j - 1) instanceof ItemElement)){
										((List)pos.get(((ItemElement)_itm).getDataMember().getCaption())).add(i);
										break;
									}
									
								}
							}
						}
					}
				}
			}
			
		}
		else{
			for(int i = res.getXFixed(); i < res.getRaw().size(); i++){
				otherAxe.add(i);
			}
			for(int i = 0; i < res.getXFixed(); i++){
				for(int j = res.getYFixed(); j < res.getRaw().get(0).size(); j++){
					Item itm = res.getRaw().get(i).get(j);
					if (itm instanceof ItemElement){
						for(OLAPMember m : mbs){
							if (m.getUniqueName().equals(((ItemElement)itm).getDataMember().getUniqueName())){
								
								if (pos.get(m.getCaption()) == null){
									pos.put(m.getCaption(), new ArrayList<Integer>());
								}
								
								if (!((List)pos.get(m.getCaption())).contains(j)){
									((List)pos.get(m.getCaption())).add(j);
								}
							}
						}
					}
					else if (itm instanceof ItemNull){
						for(int k = j -1; k > 0; k--){
							Item _itm = res.getRaw().get(i).get(k);
							if (_itm instanceof ItemElement){
								if (pos.get(((ItemElement)_itm).getDataMember().getCaption()) != null){
									if (!(res.getRaw().get(i - 1).get(j ) instanceof ItemElement)){
										((List)pos.get(((ItemElement)_itm).getDataMember().getCaption())).add(j);
										break;
									}
									
								}
							}
						}
					}
				}
			}
			
			
			
			
		}
		
		
		
		HashMap<Point, String> pts = new HashMap<Point, String>();
		
		for(Integer i : otherAxe){
			for(String s : pos.keySet()){
				
				for(Integer k : pos.get(s)){
					Point p = new Point();
					if (isCol){
						p.x = k;
						p.y = i;
					}
					else{
						p.x = i;
						p.y = k;
					}
					
					if (!pts.containsKey(p)){
						pts.put(p, s);
					}
				}
			}
			
		}
		
		for(String s : pos.keySet()){
			for(Integer k : pos.get(s)){
				for(Integer i : otherAxe){
					Point p = new Point();
					if (isCol){
						p.x = k;
						p.y = i;
					}
					else{
						p.x = i;
						p.y = k;
					}
					
					if (!pts.containsKey(p)){
						pts.put(p, s);
					}
				}
			}
		}
		
		
		
		return pts;
	}
	
	
	
	
	public LinkedHashMap<String, CategoryDataset> extractDatas(List<OLAPMember> xLvlMbs, List<OLAPMember> yLvlMbs, List<OLAPMember> scrollLvlMbs, Measure measure){
		boolean isXcol = query.colHasHiera(xLvlMbs.get(0).getHiera().getUniqueName());
		boolean isYcol = query.colHasHiera(yLvlMbs.get(0).getHiera().getUniqueName());
		boolean isScrollCol = query.colHasHiera(scrollLvlMbs.get(0).getHiera().getUniqueName());
		
		
		HashMap<Point, String> ptX = getCoordinates2(isXcol, xLvlMbs);
		HashMap<Point, String> ptY = getCoordinates2(isYcol, yLvlMbs);
		HashMap<Point, String> scrolls = getCoordinates2(isScrollCol, scrollLvlMbs);
		List<Integer> rowIndex = null;
		if (measure != null){
			rowIndex = getMeasureCoordinate(measure);
		}
		
		
		List<Point> pt = new ArrayList<Point>(CollectionUtils.intersection(ptX.keySet(), ptY.keySet()));

		HashMap<String, List<Point>> reverted = revertMap(scrolls);
		
		
		LinkedHashMap<String, CategoryDataset> map = new LinkedHashMap<String, CategoryDataset>();
		
		for(String key : reverted.keySet()){
			List<Point> _pt = new ArrayList<Point>(CollectionUtils.intersection(pt, reverted.get(key)));
			
			if (rowIndex != null){
				List<Point> toRemove = new ArrayList<Point>();
				
				for(Point p : _pt){
					if (!rowIndex.contains(p.y)){
						toRemove.add(p);
					}
				}
				_pt.removeAll(toRemove);
			}
			
			
			
			CategoryDataset ds = createDataSet(_pt, ptX, ptY);
			
			map.put(key, ds);
		}
		
		return map;
		
	}
	
	
	private HashMap<String, List<Point>> revertMap(	HashMap<Point, String> scrolls) {
		HashMap<String, List<Point>> res = new HashMap<String, List<Point>>();
		
		for(Point p : scrolls.keySet()){
			if (res.get(scrolls.get(p)) == null){
				res.put(scrolls.get(p), new ArrayList<Point>());
			}
			
			if (!res.get(scrolls.get(p)).contains(p)){
				res.get(scrolls.get(p)).add(p);
			}
		}
		
		//reorder
		List<String> keys = new ArrayList<String>(res.keySet());
		Collections.sort(keys);
		
		LinkedHashMap<String, List<Point>> ordered = new LinkedHashMap<String, List<Point>>();
		for(String s : keys){
			ordered.put(s, res.get(s));
		}
		
		return ordered;
	}

	private CategoryDataset createDataSet(List<Point> pt, final HashMap<Point, String> ptX , final HashMap<Point, String> ptY){
		DefaultCategoryDataset dataSet = new DefaultCategoryDataset();
		
		
		Collections.sort(pt, new Comparator<Point>() {
			@Override
			public int compare(Point o1, Point o2) {
				int i = ptX.get(o1).compareTo(ptX.get(o2));
				
				if (i == 0){
					int j = ptY.get(o1).compareTo(ptY.get(o2));
					return j;
				}
				return i;
			}
		});
		
		
		for(Point p : pt){
			byte[] b = res.getRaw().get(p.y).get(p.x).getLabel().getBytes();
			
			//XXX 
			for(int i = 0; i < b.length; i++){
				if (b[i]  == - 96){
					b[i] = ' ';
				}
			}
			
			String val = new String(b).replaceAll("\\s*", "").replace(",", ".");
			try{
				dataSet.addValue(Double.parseDouble(val), ptX.get(p), ptY.get(p));
			}catch(Exception ex){
				dataSet.addValue(0, ptX.get(p), ptY.get(p));
			}
			
		}
		
		return dataSet;
	}
	
	public CategoryDataset extractDatas(List<OLAPMember> xLvlMbs,  List<OLAPMember> yLvlMbs, Measure measure){
		boolean isXcol = query.colHasHiera(xLvlMbs.get(0).getHiera().getUniqueName());
		boolean isYcol = query.colHasHiera(yLvlMbs.get(0).getHiera().getUniqueName());
		
		HashMap<Point, String> ptX = getCoordinates2(isXcol, xLvlMbs);
		HashMap<Point, String> ptY = getCoordinates2(isYcol, yLvlMbs);
		List<Integer> rowIndex = null;
		if (measure != null){
			rowIndex = getMeasureCoordinate(measure);
		}
		
	
		
		List<Point> pt = new ArrayList<Point>(CollectionUtils.intersection(ptX.keySet(), ptY.keySet()));
		
		if (measure != null){
			List<Point> toRemove = new ArrayList<Point>();
			
			for(Point p : pt){
				if (!rowIndex.contains(p.y)){
					toRemove.add(p);
				}
			}
			pt.removeAll(toRemove);
		}
		
	
				
		return createDataSet(pt, ptX, ptY);
		
	}
}
