package bpm.fa.api.olap.ktree;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.StringTokenizer;

import bpm.fa.api.olap.ktree.Item.Operator;


public class ItemAxis {
	private LinkedHashMap<String, List<Item>> items = new LinkedHashMap<String, List<Item>>();
	
	
	public void add(String uname, Object action){
			
		
		StringTokenizer st = new StringTokenizer("" + uname, "]");
		String hiera = st.nextToken() + "]";

		if (items.get(hiera) == null){
			items.put(hiera, new ArrayList<Item>());
		}
		
		boolean found = false;
		for(Item s : items.get(hiera)){
			if (s.getUname().equals(uname)){
				found = true;
				break;
			}
		}
		if (!found){
			items.get(hiera).add(new Item(uname, action));
		}
		
		
	}
	
	public String getMdx(String axisName, boolean showEmpty){
		Item root = new Item(showEmpty, axisName);
		Item cross = null;
		if (items.size() > 1){
			cross = new Item(Operator.Crossjoin);
			root.addChild(cross);
			
		}
		for(String s : items.keySet()){

			List<Item> l = items.get(s);
			if (l == null){
				continue;
			}
			if (l.size() > 1){
				
				Item union  = new Item(Operator.Union);
				Item crossjoin = null;
				
				//FIXME : crossjoin with level items
				List<Item> crossItems = new ArrayList<Item>();
				for(Item item : l) {
					String uname = item.getUname();
					if(uname.toLowerCase().endsWith(".members")) {
//						if(crossjoin == null) {
//							crossjoin = new Item(Operator.Crossjoin);
//						}
//						crossjoin.addChild(item);
						crossItems.add(item);
					}
					else {
						union.addChild(item);
					}
				}
				
				if(crossItems.size() > 0) {
					crossjoin = new Item(Operator.Crossjoin);
					for(Item it : crossItems) {
						crossjoin.addChild(it);
					}
				}
				
				if(!(crossItems.size() == l.size())) {
					if(crossjoin != null) {
						crossjoin.insertChild(0,union);
					}
				}
				
//				union.addAll(l);
				
				if (cross != null){
					if(crossjoin != null) {
						cross.addChild(crossjoin);
					}
					else {
						cross.addChild(union);
					}
				}
				else{
					if(crossjoin != null) {
						root.addChild(crossjoin);
					}
					else {
						root.addChild(union);
					}
				}
			}
			else if (l.size() == 1){
				if (cross != null){
					cross.addChild(l.get(0));
				}
				else{
					root.addChild(l.get(0));
				}
				
			}
		}
		
		return root.getMdx();
		
	}
}
