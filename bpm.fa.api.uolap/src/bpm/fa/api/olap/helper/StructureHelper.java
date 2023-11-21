package bpm.fa.api.olap.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import bpm.fa.api.item.Item;
import bpm.fa.api.item.ItemElement;
import bpm.fa.api.item.ItemNull;
import bpm.fa.api.item.ItemValue;
import bpm.fa.api.olap.OLAPStructure;


/**
 * 
 * @author ereynard
 * type :
 * 	- 1 : load on startup (TODO)
 *  - 2 : load on connection
 *  - 3 : load on query (TODO)
 */
public class StructureHelper{
	
	public static void removeTotals(ArrayList<ArrayList<Item>> xtable) {
		//find i and j for the first value
		int iFirst = -1;
		int jFirst = -1;
		for(int i = 0 ; i < xtable.size() ; i++) {
			for(int j = 0 ; j < xtable.get(i).size() ; j++) {
				if(xtable.get(i).get(j) instanceof ItemValue) {
					iFirst = i;
					jFirst = j;
					break;
				}
			}
			if(iFirst != -1) {
				break;
			}
		}
		
		//---------remove row totals-----------
		//find measure position
		ItemElement jMeasure = null;
		int measureIndex = -1;
		for(int j = 0 ; j < jFirst ; j++) {
			if(xtable.get(iFirst).get(j)instanceof ItemElement) {
				ItemElement elem = (ItemElement)xtable.get(iFirst).get(j);
//				if(elem.getDataMember().getUniqueName().equals(uname)){
				if(elem.getDataMember().getUniqueName().contains("[Measures]")) {
					if(xtable.size() > iFirst + 1) {
						if(xtable.get(iFirst + 1).get(j) instanceof ItemElement) {
							ItemElement subelem = (ItemElement)xtable.get(iFirst + 1).get(j);
							if(!elem.getDataMember().getUniqueName().equals(subelem.getDataMember().getUniqueName())) {
								jMeasure = null;
							}
							else {
								jMeasure = elem;
								measureIndex = j;
							}
						}
						else {
							jMeasure = elem;
							measureIndex = j;
						}
					}
					else {
						jMeasure = elem;
						measureIndex = j;
					}
					break;
				}
			}
		}
		//remove totals for rows
		for(int j = 0 ; j < jFirst ; j++) {
			//if its not the measure col, remove the row total
			if(!(xtable.get(iFirst).get(j) == jMeasure)) {
				removeTotalForRow(j, iFirst, jFirst, xtable);
			}
		}
		
		//add the measure to the right place
		if(measureIndex != -1) {
			xtable.get(iFirst).set(measureIndex, jMeasure);
		}
		
		//---------remove col totals---------
		//find measure position
		ItemElement iMeasure = null;
		int colMesIndex = -1;
		for(int i = 0 ; i < iFirst ; i++) {
			if(xtable.get(i).get(jFirst)instanceof ItemElement) {
				ItemElement elem = (ItemElement)xtable.get(i).get(jFirst);
//				if(elem.getDataMember().getUniqueName().equals(uname)){
				if(elem.getDataMember().getUniqueName().contains("[Measures]")) {
					if(xtable.get(i).size() > jFirst + 1) {
						if(xtable.get(i).get(jFirst + 1) instanceof ItemElement) {
							ItemElement subelem = (ItemElement)xtable.get(i).get(jFirst + 1);
							if(!elem.getDataMember().getUniqueName().equals(subelem.getDataMember().getUniqueName())) {
								iMeasure = null;
							}
							else {
								iMeasure = elem;
								colMesIndex = i;
							}
						}
						else {
							iMeasure = elem;
							colMesIndex = i;
						}
					}
					else {
						iMeasure = elem;
						colMesIndex = i;
					}
					break;
				}
			}
		}
		
		//remove totals for cols
		for(int i = 0 ; i < iFirst ; i++) {
			//if its not the measure row, remove the col total
			if(!(xtable.get(i).get(jFirst) == iMeasure)) {
				removeTotalForCol(i, iFirst, jFirst, xtable);
			}
		}
		
		//add the measure at the right place
		if(colMesIndex != -1) {
			xtable.get(colMesIndex).set(jFirst, iMeasure);
		}
		
	}

	private static void removeTotalForCol(int i, int iFirst, int jFirst, ArrayList<ArrayList<Item>> xtable) {
		//if its a total
		if(xtable.get(i + 1).get(jFirst) instanceof ItemNull || (xtable.get(i + 1).get(jFirst) instanceof ItemElement && ((ItemElement)xtable.get(i + 1).get(jFirst)).getLabel().equalsIgnoreCase(""))) {
			//test if the element has children
			boolean itemElementFinded = false;
			for(int jt = jFirst + 1 ; jt < xtable.get(i).size() ; jt++) {
				if(xtable.get(i + 1).get(jt) instanceof ItemElement) {
					itemElementFinded = true;
					if(xtable.get(i).get(jt) instanceof ItemElement && !((ItemElement)xtable.get(i).get(jt)).getLabel().equalsIgnoreCase("")) {
						return;
					}
				}
			}
			if(!itemElementFinded) {
				return;
			}
			
			//find if there are subItems
			int index = -1;
			for(int j = jFirst + 1 ; j < xtable.get(i).size() ; j++) {
				if(xtable.get(i).get(j) instanceof ItemElement && !((ItemElement)xtable.get(i).get(j)).getLabel().equalsIgnoreCase("")) {
					index = j;
					break;
				}
			}
			//if there are subItems, remove there totals
			if(index != -1) {
				removeTotalForSubCol(i, iFirst, jFirst, xtable);
			}
			
			//else just remove the total
			else {
				int decalage = 0;
				//find how many cols to remove
				for(int j = jFirst ; j < xtable.get(i).size() ; j++) {
					if(xtable.get(i + 1).get(j) instanceof ItemElement) {
						decalage = j;
						break;
					}
				}
				//remove lines and put the Elements at the right place
				//keep elements to re-put them in the table after lines remove
				HashMap<Integer, ItemElement> elemIndex = new HashMap<Integer, ItemElement>();
				for(int it = 0 ; it < i ; it++) {
					if(xtable.get(it).get(jFirst) instanceof ItemElement) {
						ItemElement elem = (ItemElement)xtable.get(it).get(jFirst);
						elemIndex.put(it, elem);
					}
				}
				for(int it = 0 ; it < iFirst ; it++) {
					if(xtable.get(it + 1).get(jFirst) instanceof ItemElement) {
						if(((ItemElement)xtable.get(it + 1).get(jFirst)).getDataMember().getUniqueName().contains("[Measures]")) {
							if(xtable.get(it + 2).get(jFirst) instanceof ItemElement) {
								ItemElement elem = (ItemElement)xtable.get(it + 2).get(jFirst);
								elemIndex.put(it + 2, elem);
							}
						}
						else {
							ItemElement elem = (ItemElement)xtable.get(it + 1).get(jFirst);
							elemIndex.put(it + 1, elem);
						}
					}
				}
				ItemElement el = (ItemElement)xtable.get(i).get(jFirst);
				
				//remove lines
				for(int j = jFirst ; j < decalage ; j++) {
					for(int it = 0 ; it < xtable.size() ; it++) {
						xtable.get(it).remove(jFirst);
					}
				}
				
				//re-put elements in the table
				for(int ind : elemIndex.keySet()) {
					xtable.get(ind).set(jFirst, elemIndex.get(ind));
				}
				xtable.get(i).set(jFirst, el);
			}
		}
	}



	private static void removeTotalForSubCol(int i, int iFirst, int jFirst, ArrayList<ArrayList<Item>> xtable) {
		ConcurrentMap<Integer, Integer> removed = new ConcurrentHashMap<Integer, Integer>();
		//find lines to delete
		int colToDelete = -1;
		for(int j = jFirst ; j < xtable.get(i).size() ; j++) {
			if(xtable.get(i).get(j) instanceof ItemElement && !((ItemElement)xtable.get(i).get(j)).getLabel().equalsIgnoreCase("")) {
				if(colToDelete == -1) {
					colToDelete = j;
				}
				else {
					if(j > colToDelete + 1) {
						int nbCol = 0;
						boolean hasChildren = false;
						for(int jt = colToDelete ; jt <= j ; jt++) {
							if(xtable.get(i + 1).get(jt) instanceof ItemElement) {
								hasChildren = true;
								break;
							}
							else {
								nbCol++;
							}
						}
						if(hasChildren) {
							removed.put(colToDelete, nbCol);
						}
					}
					colToDelete = j;
				}
			}
			else {
				if(j == xtable.get(i).size() - 1 && j > colToDelete + 1) {
					int nbCol = 0;
					boolean hasChildren = false;
					for(int jt = colToDelete ; jt <= j ; jt++) {
						if(xtable.get(i + 1).get(jt) instanceof ItemElement) {
							hasChildren = true;
							break;
						}
						else {
							nbCol++;
						}
					}
					if(hasChildren) {
						removed.put(colToDelete, nbCol);
					}
				}
			}
		}
		//delete items and put the labels at the right place
		boolean itemExist = true;
		while(itemExist) {
			int max = -1;
			for(int key : removed.keySet()) {
				if(key > max) {
					max = key;
				}
			}
			int nbCols = removed.get(max);
			removed.remove(max);
			
			//keep elements to re-put them in the table after lines remove
			HashMap<Integer, ItemElement> elemIndex = new HashMap<Integer, ItemElement>();
			for(int it = 0 ; it < i ; it++) {
				if(xtable.get(it).get(max) instanceof ItemElement) {
					ItemElement elem = (ItemElement)xtable.get(it).get(max);
					elemIndex.put(it, elem);
				}
			}
			for(int it = 0 ; it < iFirst ; it++) {
				if(xtable.get(it + 1).get(max) instanceof ItemElement) {
					if(((ItemElement)xtable.get(it + 1).get(max)).getDataMember().getUniqueName().contains("[Measures]")) {
						if(xtable.get(it + 2).get(max) instanceof ItemElement) {
							ItemElement elem = (ItemElement)xtable.get(it + 2).get(max);
							elemIndex.put(it + 2, elem);
						}
					}
					else {
						ItemElement elem = (ItemElement)xtable.get(it + 1).get(max);
						elemIndex.put(it + 1, elem);
					}
				}
			}
			ItemElement el = (ItemElement)xtable.get(i).get(max);
			
			//remove elements
			ArrayList<Item> last = null;
			for(int ind = max ; ind < max + nbCols ; ind++) {
				if(max + nbCols == xtable.size() && ind == max) {
					last = new ArrayList<Item>();
					for(int it = 0 ; it < xtable.size() ; it++) {
						last.add(xtable.get(it).remove(max));
					}
				}
				else {
					for(int it = 0 ; it < xtable.size() ; it++) {
						xtable.get(it).remove(max);
					}
				}
			}
			
			//re-put elements in the table
			for(int ind : elemIndex.keySet()) {
				if(max == xtable.get(i).size() && last != null) {
					last.set(ind, elemIndex.get(ind));
					for(int it = 0 ; it < xtable.size() ; it++) {
						xtable.get(it).add(last.get(it));
					}
				}
				xtable.get(ind).set(max, elemIndex.get(ind));
			}
			xtable.get(i).set(max, el);
			
			if(removed.size() <= 0) {
				itemExist = false;
			}
		}
	}



	private static int removeTotalForRow(int j, int iFirst, int jFirst, ArrayList<ArrayList<Item>> xtable) {
		int decalageAdded = 0;
		//if total
		if(xtable.get(iFirst).get(j + 1) instanceof ItemNull) {
			//test if the element has children
			boolean itemElementFinded = false;
			for(int it = iFirst + 1 ; it < xtable.size() ; it++) {
				if(xtable.get(it).get(j + 1) instanceof ItemElement) {
					itemElementFinded = true;
					if(xtable.get(it).get(j) instanceof ItemElement && !((ItemElement)xtable.get(it).get(j)).getLabel().equalsIgnoreCase("")) {
						return 0;
					}
				}
			}
			if(!itemElementFinded) {
				return 0;
			}
			
			//find if there are subItems
			int index = -1;
			for(int i = iFirst + 1 ; i < xtable.size() ; i++) {
				if(xtable.get(i).get(j) instanceof ItemElement && !((ItemElement)xtable.get(i).get(j)).getLabel().equalsIgnoreCase("")) {
					index = i;
					break;
				}
			}
			//if there are subItems, remove there totals
			if(index != -1) {
				decalageAdded += removeTotalForSubRow(j, iFirst, jFirst, xtable);
			}
			//else just remove the total
			else {
				//find how many lines to remove
				for(int i = iFirst ; i < xtable.size() ; i++) {
					if(xtable.get(i).get(j + 1) instanceof ItemElement) {
						decalageAdded = i;
						break;
					}
				}
				//remove lines and put the Elements at the right place
				//keep elements to re-put them in the table after lines remove
				HashMap<Integer, ItemElement> elemIndex = new HashMap<Integer, ItemElement>();
				for(int jt = 0 ; jt < j ; jt++) {
					if(xtable.get(iFirst).get(jt) instanceof ItemElement) {
						ItemElement elem = (ItemElement)xtable.get(iFirst).get(jt);
						elemIndex.put(jt, elem);
					}
				}
				for(int jt = j ; jt < jFirst ; jt++) {
					if(xtable.get(iFirst).get(jt + 1) instanceof ItemElement) {
						if(((ItemElement)xtable.get(iFirst).get(jt + 1)).getDataMember().getUniqueName().contains("[Measures]")) {
							if(xtable.get(iFirst).get(jt + 2) instanceof ItemElement) {
								ItemElement elem = (ItemElement)xtable.get(iFirst).get(jt + 2);
								elemIndex.put(jt + 2, elem);
							}
						}
						else {
							ItemElement elem = (ItemElement)xtable.get(iFirst).get(jt + 1);
							elemIndex.put(jt + 1, elem);
						}
					}
				}
				Item el = (ItemElement)xtable.get(iFirst).get(j);
				
				//remove lines
				for(int i = iFirst ; i < decalageAdded ; i++) {
					xtable.remove(iFirst);
				}
				
				//re-put elements in the table
				for(int ind : elemIndex.keySet()) {
					xtable.get(iFirst).set(ind, elemIndex.get(ind));
				}
				xtable.get(iFirst).set(j, el);
			}
		}
		return decalageAdded;
	}



	private static int removeTotalForSubRow(int j, int iFirst, int jFirst, ArrayList<ArrayList<Item>> xtable) {
		int decalageAdded = 0;
		ConcurrentMap<Integer, Integer> removed = new ConcurrentHashMap<Integer, Integer>();
		//find lines to delete
		int rowToDelete = -1;
		for(int i = iFirst ; i < xtable.size() ; i++) {
			if(xtable.get(i).get(j) instanceof ItemElement && !((ItemElement)xtable.get(i).get(j)).getLabel().equalsIgnoreCase("")) {
				if(rowToDelete == -1) {
					rowToDelete = i;
				}
				else {
					if(i > rowToDelete + 1) {
						int nbRow = 0;
						boolean hasChildren = false;
						for(int it = rowToDelete ; it <= i ; it++) {
							if(xtable.get(it).get(j + 1) instanceof ItemElement) {
								hasChildren = true;
								break;
							}
							else {
								nbRow++;
							}
						}
						if(hasChildren) {
							decalageAdded += nbRow;
							removed.put(rowToDelete, nbRow);
						}
					}
					rowToDelete = i;
				}
			}
			else {
				if(i == xtable.size() - 1 && i > rowToDelete + 1) {
					int nbRow = 0;
					boolean hasChildren = false;
					for(int it = rowToDelete ; it <= i ; it++) {
						if(xtable.get(it).get(j + 1) instanceof ItemElement) {
							hasChildren = true;
							break;
						}
						else {
							nbRow++;
						}
					}
					if(hasChildren) {
						decalageAdded += nbRow;
						removed.put(rowToDelete, nbRow);
					}
				}
			}
		}
		//delete items and put the labels at the right place
		boolean itemExist = true;
		while(itemExist) {
			int max = -1;
			for(int key : removed.keySet()) {
				if(key > max) {
					max = key;
				}
			}
			int nbLines = removed.get(max);
			removed.remove(max);
			
			//keep elements to re-put them in the table after lines remove
			HashMap<Integer, ItemElement> elemIndex = new HashMap<Integer, ItemElement>();
			for(int jt = 0 ; jt < j ; jt++) {
				if(xtable.get(max).get(jt) instanceof ItemElement) {
					ItemElement elem = (ItemElement)xtable.get(max).get(jt);
					elemIndex.put(jt, elem);
				}
			}
			for(int jt = j ; jt < jFirst ; jt++) {
				if(xtable.get(max).get(jt + 1) instanceof ItemElement) {
					if(((ItemElement)xtable.get(max).get(jt + 1)).getDataMember().getUniqueName().contains("[Measures]")) {
						if(xtable.get(max).get(jt + 2) instanceof ItemElement) {
							ItemElement elem = (ItemElement)xtable.get(max).get(jt + 2);
							elemIndex.put(jt + 2, elem);
						}
					}
					else {
						ItemElement elem = (ItemElement)xtable.get(max).get(jt + 1);
						elemIndex.put(jt + 1, elem);
					}
				}
			}
			
			ItemElement elem = (ItemElement) xtable.get(max).get(j);
			
			//remove elements
			ArrayList<Item> last = null;
			for(int ind = max ; ind < max + nbLines ; ind++) {
				if(max + nbLines == xtable.size() && ind == max) {
					last = xtable.remove(max);
				}
				else {
					xtable.remove(max);
				}
			}
			
			//re-put elements in the table
			for(int ind : elemIndex.keySet()) {
				if(max == xtable.size() && last != null) {
					last.set(ind, elemIndex.get(ind));
					xtable.add(last);
				}
				xtable.get(max).set(ind, elemIndex.get(ind));
			}
			xtable.get(max).set(j, elem);
			
			if(removed.size() <= 0) {
				itemExist = false;
			}
		}
		return decalageAdded;
	}



	
}
