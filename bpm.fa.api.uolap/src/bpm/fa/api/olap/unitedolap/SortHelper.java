package bpm.fa.api.olap.unitedolap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import bpm.fa.api.item.Item;
import bpm.fa.api.item.ItemElement;
import bpm.fa.api.item.ItemValue;
import bpm.fa.api.olap.OLAPResult;

public class SortHelper {

	private static AlphanumComparator comparator = new AlphanumComparator();
	
	public static void sort(OLAPResult res, HashMap<String, String> sortElements, boolean isMeasure, String measureUname) {
		
		if(sortElements == null || sortElements.isEmpty()) {
			return;
		}
		
		int nbRow = res.getRaw().size();
		int nbCol = res.getRaw().get(0).size();
		
		for(String uname : sortElements.keySet()) {
			
			//sort for a measure
			if(uname.contains("[Measures]")) {
				
				int maxI = res.getRaw().size();
				int maxJ = res.getRaw().get(0).size();
				
				boolean isRow = false;
				for(int i = 0 ; i < res.getRaw().size() ; i++) {
					for(int j = 0 ; j < res.getRaw().get(i).size() ; j++) {
						Item it = res.getRaw().get(i).get(j);
						if(it instanceof ItemElement) {
							if(((ItemElement)it).getDataMember().getUniqueName().startsWith("[Measures]")) {
								isRow = true;
							}
						}
					}
				}
				if(isRow) {
					maxJ = res.getYFixed();
				}
				else {
					maxI = res.getXFixed();
				}
				
				//If the measure is asc or desc, we just sort all the available level on the grid
				if(sortElements.get(uname).equalsIgnoreCase("Asc") || sortElements.get(uname).equalsIgnoreCase("Desc")) {
					//find level unames
					//create all sortElements
					HashMap<String, String> levelToSortByMeasure = new HashMap<String, String>();
					
					for(int i = 0 ; i < maxI ; i++) {
						for(int j = 0 ; j < maxJ ; j++) {
							if(i < res.getXFixed() || j < res.getYFixed()) {
								
								Item it = res.getRaw().get(i).get(j);
								if(it instanceof ItemElement) {
									if(((ItemElement)it).getDataMember().getLevel() != null) {
										String itUname = ((ItemElement)it).getDataMember().getLevel().getUniqueName();
										if(levelToSortByMeasure.get(itUname) == null) {
											if(!itUname.startsWith("[Measures]")) {
												levelToSortByMeasure.put(itUname, sortElements.get(uname));
											}
										}
									}
									
								}
								
							}
						}
					}
					
					//sort the levels
					sort(res, levelToSortByMeasure, true, uname);
					
				}
			}
			
			//sort for a level
			else {
					
				int i = 0;
				int j = 0;
				
				int firstI = 0;
				int firstJ = 0;
				
				boolean lastMember = true;
				boolean found = false;
				boolean isRow = false;
				
				//look in rows
				LOOK:for( ; i < nbRow ; i++) {
					for(j = 0 ; j < res.getYFixed() ; j++) {
						Item item = res.getRaw().get(i).get(j);
						if(item instanceof ItemElement) {
							ItemElement it = (ItemElement) item;
							if(it.getDataMember().getLevel() != null && uname.equals(it.getDataMember().getLevel().getUniqueName())) {
								
								//look if its a last member						
								if(i < nbRow - 1) {
									Item item2 = res.getRaw().get(i + 1).get(j + 1);
									if(item2 instanceof ItemElement) {
										if(((ItemElement)item2).getDataMember().getLevel() != null && !uname.equals(((ItemElement)item2).getDataMember().getLevel().getUniqueName())) {
											lastMember = false;
										}
									}
	 							}
								
								found = true;
								isRow = true;
								
								if(firstI == 0) {
									firstI = i;
									firstJ = j;
								}
								
								if(!lastMember) {
									break LOOK;
								}
								else {
									continue LOOK;
								}
								
							}
						}
					}
				}
				
				//look in cols
				if(!found) {
					i = 0;
					j = 0;
					LOOK:for( ; j < nbCol ; j++) {
						for(i = 0 ; i < res.getXFixed() ; i++) {
							Item item = res.getRaw().get(i).get(j);
							if(item instanceof ItemElement) {
								ItemElement it = (ItemElement) item;
								if(it.getDataMember().getLevel() != null && uname.equals(it.getDataMember().getLevel().getUniqueName())) {
									
									//look if its a last member						
									if(j < nbCol - 1) {
										Item item2 = res.getRaw().get(i+1).get(j+1);
										if(item2 instanceof ItemElement) {
											if(((ItemElement)item2).getDataMember().getLevel() != null && !uname.equals(((ItemElement)item2).getDataMember().getLevel().getUniqueName())) {
												lastMember = false;
											}
										}
		 							}
									
									found = true;
									
									if(firstJ == 0) {
										firstJ = j;
										firstI = i;
									}
									
									if(!lastMember) {
										break LOOK;
									}
									else {
										continue LOOK;
									}
									
								}
							}
						}
					}
				}
				
				if(found) {
					SortHelper.sortColRow(res, uname, sortElements.get(uname), lastMember, firstI != 0 ? firstI : i, firstJ != 0 ? firstJ : j, isRow, isMeasure, measureUname);
				}
				
			}
			
		}
	}


	private static void sortColRow(OLAPResult res, String sortUname, String sortType, boolean lastMember, int i, int j, boolean isRow, boolean isMeasure, String measureUname) {
		if(isRow) {
			sortRow(res, sortUname, sortType, lastMember, i, j, isMeasure, measureUname);
		}
		
		else {
			sortCol(res, sortUname, sortType, lastMember, i, j, isMeasure, measureUname);
		}
	}

	static int measureIndex = 0;
	
	private static void sortCol(final OLAPResult res, String sortUname, final String sortType, boolean lastMember, final int i, int j, boolean isMeasure, String measureUname) {
		int nbRow = res.getRaw().size();
		int nbCol = res.getRaw().get(0).size();
		
		//if it's a last level member, we just order the chunks
		if(lastMember) {
			HashMap<Integer, List<ArrayList<Item>>> part = new HashMap<Integer, List<ArrayList<Item>>>();
			int firstJ = j;
			
			boolean onGoing = true;
			
			for(int col = firstJ ; col < nbCol ; col++) {
				Item item = res.getRaw().get(i).get(col);
				if(item instanceof ItemElement) {
					ItemElement it = (ItemElement) item;
					if(it.getDataMember().getLevel() != null && sortUname.equals(it.getDataMember().getLevel().getUniqueName())) {
						if(!onGoing) {
							firstJ = col;
							onGoing = true;
						}
						
						if(part.get(firstJ) == null) {
							part.put(firstJ, new ArrayList<ArrayList<Item>>());
						}
						ArrayList<Item> column = new ArrayList<Item>();
						for(int row  = 0 ; row < nbRow ; row++) {						
							column.add(res.getRaw().get(row).get(col));
						}
						
						part.get(firstJ).add(column);
					}
					else {
						onGoing = false;
					}
				}
				else {
					onGoing = false;
				}
			}
			
			for(Integer startCol : part.keySet()) {
				
				List<ArrayList<Item>> items = part.get(startCol);
				//sort this chunk
				if(isMeasure) {
					
					Collections.sort(items, new Comparator<List<Item>>() {
						@Override
						public int compare(List<Item> o1, List<Item> o2) {
							
							String val1 = ((ItemValue)o1.get(res.getXFixed())).getValue();
							String val2 = ((ItemValue)o2.get(res.getXFixed())).getValue();
							Double double1 = Double.parseDouble(val1);
							Double double2 = Double.parseDouble(val2);
							int result = double1.compareTo(double2);
							
//							int result = comparator.compare(o1.get(res.getXFixed()).getLabel(), o2.get(res.getXFixed()).getLabel());
							
							if(sortType.equals("Asc")) {
								return result;
							}
							else {
								return - result;
							}
						}
					});
					
				}
				else {
					Collections.sort(items, new Comparator<List<Item>>() {
						@Override
						public int compare(List<Item> o1, List<Item> o2) {
							int res = comparator.compare(o1.get(i).getLabel(), o2.get(i).getLabel());
							
							if(sortType.equals("Asc")) {
								return res;
							}
							else {
								return - res;
							}
						}
					});
				}
				
				//replace the chunk in the cube
				for(int r = startCol ; r < startCol + items.size(); r++) {
					
					for(int row = 0 ; row < nbRow ; row++) {
						res.getRaw().get(row).remove(r);
					}
					
					for(int row = 0 ; row < nbRow ; row++) {
						res.getRaw().get(row).add(r, items.get(r - startCol).get(row));
					}
					
				}
				
			}
			
		}
		//if it's not a last member, thing get tricky
		//we find the chunks we need to sort
		else {
			
			
			List<List<SortChunk>> chunks = new ArrayList<List<SortChunk>>();
			
			int firstJ = j;
			
			List<SortChunk> actualList = new ArrayList<SortChunk>();
			SortChunk chunk = null;
			
			for(int col = firstJ ; col < nbCol ; col++) {
				Item item = res.getRaw().get(i).get(col);
				if(item instanceof ItemElement) {
					ItemElement it = (ItemElement) item;
					if(it.getDataMember().getLevel() != null && sortUname.equals(it.getDataMember().getLevel().getUniqueName())) {
						if(actualList.isEmpty()) {
							firstJ = col;
						}
						chunk = new SortChunk();
						actualList.add(chunk);
						chunk.setUname(it.getDataMember().getUniqueName());
						chunk.setLabel(it.getLabel());
						chunk.setStartIndex(firstJ);
						
						ArrayList<Item> column = new ArrayList<Item>();
						for(int row  = 0 ; row < nbRow ; row++) {						
							column.add(res.getRaw().get(row).get(col));
						}
						chunk.getItems().add(column);
					}
					
					else if(i > 0 && res.getRaw().get(i-1).get(col) instanceof ItemElement && ((ItemElement)res.getRaw().get(i-1).get(col)).getDataMember() != null && ((ItemElement)res.getRaw().get(i-1).get(col)).getDataMember().getLevel() != null) {
						if(!actualList.isEmpty()) {
							chunks.add(actualList);
							actualList = new ArrayList<SortChunk>();
						}
					}
					else {
						ArrayList<Item> column = new ArrayList<Item>();
						for(int row  = 0 ; row < nbRow ; row++) {						
							column.add(res.getRaw().get(row).get(col));
						}
						chunk.getItems().add(column);
					}
				}
				else {
					ArrayList<Item> column = new ArrayList<Item>();
					for(int row  = 0 ; row < nbRow ; row++) {						
						column.add(res.getRaw().get(row).get(col));
					}
					chunk.getItems().add(column);
				}
			}
			if(!actualList.isEmpty()) {
				chunks.add(actualList);
			}
			
			//sort the chunks
			for(List<SortChunk> c : chunks) {
				
				if(isMeasure) {
					measureIndex = 0;
//					if(c != null && !c.isEmpty()) {
//						for(int ind = 0 ; ind < c.get(0).getItems().size() ; ind++) {
//							ItemElement mes = (ItemElement) c.get(0).getItems().get(ind).get(res.getXFixed() -1);
//							if(mes.getDataMember().getUniqueName().equalsIgnoreCase(measureUname)) {
//								measureIndex = ind;
//								break;
//							}
//						}
//					}
					Collections.sort(c, new Comparator<SortChunk>() {
						@Override
						public int compare(SortChunk o1, SortChunk o2) {				
							String val1 = ((ItemValue)o1.getItems().get(measureIndex).get(res.getXFixed())).getValue();
							String val2 = ((ItemValue)o2.getItems().get(measureIndex).get(res.getXFixed())).getValue();
							Double double1 = Double.parseDouble(val1);
							Double double2 = Double.parseDouble(val2);
							int result = double1.compareTo(double2);
							
							if(sortType.equals("Asc")) {
								return result;
							}
							else {
								return - result;
							}
						}
					});
					
				}
				else {
				
					Collections.sort(c, new Comparator<SortChunk>() {
		
						@Override
						public int compare(SortChunk o1,SortChunk o2) {
							int res = comparator.compare(o1.getLabel(), o2.getLabel());
							
							if(sortType.equals("Asc")) {
								return res;
							}
							else {
								return - res;
							}
						}
					});
				}
				
				//remove and add the sorted elements
				int startIndex = c.get(0).getStartIndex();
				for(SortChunk ch : c) {
					
					//replace the chunk in the cube
					for(int r = startIndex ; r < ch.getItems().size() + startIndex ; r++) {
						
						for(int row = 0 ; row < nbRow ; row++) {
							res.getRaw().get(row).remove(r);
						}
						
						for(int row = 0 ; row < nbRow ; row++) {
							
							res.getRaw().get(row).add(r, ch.getItems().get(r - startIndex).get(row));
						}
						
					}
					startIndex += ch.getItems().size();
					
				}
				
			}
			
		}
	}

	private static void sortRow(final OLAPResult res, String sortUname, final String sortType, boolean lastMember, int i, final int j, boolean isMeasure, String measureUname) {
		
		int nbRow = res.getRaw().size();
		
		//if it's a last level member, we just order the chunks
		if(lastMember) {
			HashMap<Integer, List<ArrayList<Item>>> part = new HashMap<Integer, List<ArrayList<Item>>>();
			int firstI = i;
			
			boolean onGoing = true;
			
			for(int row = firstI ; row < nbRow ; row++) {
				Item item = res.getRaw().get(row).get(j);
				if(item instanceof ItemElement) {
					ItemElement it = (ItemElement) item;
				
					if(it.getDataMember().getLevel() != null && sortUname.equals(it.getDataMember().getLevel().getUniqueName())) {				
						if(!onGoing) {
							firstI = row;
							onGoing = true;
						}
						
						if(part.get(firstI) == null) {
							part.put(firstI, new ArrayList<ArrayList<Item>>());
						}
						
						part.get(firstI).add(res.getRaw().get(row));
					}
					else {
						onGoing = false;
					}
				}
				else {
					onGoing = false;
				}
			}
			
			for(Integer startRow : part.keySet()) {
				
				List<ArrayList<Item>> items = part.get(startRow);
				//sort this chunk
				
				if(isMeasure) {
					
					Collections.sort(items, new Comparator<List<Item>>() {
						@Override
						public int compare(List<Item> o1, List<Item> o2) {
							
							
							String val1 = ((ItemValue)o1.get(res.getYFixed())).getValue();
							String val2 = ((ItemValue)o2.get(res.getYFixed())).getValue();
							Double double1 = Double.parseDouble(val1);
							Double double2 = Double.parseDouble(val2);
							int result = double1.compareTo(double2);
							
							
//							int result = comparator.compare(o1.get(res.getYFixed()).getLabel(), o2.get(res.getYFixed()).getLabel());
							
							if(sortType.equals("Asc")) {
								return result;
							}
							else {
								return - result;
							}
						}
					});
					
				}
				else {
				
					Collections.sort(items, new Comparator<List<Item>>() {
						@Override
						public int compare(List<Item> o1, List<Item> o2) {
							int res = comparator.compare(o1.get(j).getLabel(), o2.get(j).getLabel());
							
							if(sortType.equals("Asc")) {
								return res;
							}
							else {
								return - res;
							}
						}
					});
				}
				
				//replace the chunk in the cube
				for(int r = startRow ; r < startRow + items.size(); r++) {
					res.getRaw().remove(r);
					res.getRaw().add(r, items.get(r - startRow));
				}
				
			}
			
		}
		//if it's not a last member, thing get tricky
		//we find the chunks we need to sort
		else {
			
			List<List<SortChunk>> chunks = new ArrayList<List<SortChunk>>();
			
			int firstI = i;
			
			List<SortChunk> actualList = new ArrayList<SortChunk>();
			SortChunk chunk = null;
			
			for(int row = firstI ; row < nbRow ; row++) {
				Item item = res.getRaw().get(row).get(j);
				if(item instanceof ItemElement) {
					ItemElement it = (ItemElement) item;
					if(it.getDataMember().getLevel() != null && sortUname.equals(it.getDataMember().getLevel().getUniqueName())) {
						if(actualList.isEmpty()) {
							firstI = row;
						}
						chunk = new SortChunk();
						actualList.add(chunk);
						chunk.setUname(it.getDataMember().getUniqueName());
						chunk.setLabel(it.getLabel());
						chunk.setStartIndex(firstI);
						chunk.getItems().add(res.getRaw().get(row));
					}
					
					else if(j > 0 && res.getRaw().get(row).get(j-1) instanceof ItemElement && ((ItemElement)res.getRaw().get(row).get(j-1)).getDataMember() != null && ((ItemElement)res.getRaw().get(row).get(j-1)).getDataMember().getLevel() != null) {
						if(!actualList.isEmpty()) {
							chunks.add(actualList);
							actualList = new ArrayList<SortChunk>();
						}
					}
					else {
						chunk.getItems().add(res.getRaw().get(row));
					}
				}
				else {
					chunk.getItems().add(res.getRaw().get(row));
				}
			}
			if(!actualList.isEmpty()) {
				chunks.add(actualList);
			}
			
			//sort the chunks
			for(List<SortChunk> c : chunks) {
				
				if(isMeasure) {
					measureIndex = 0;
//					if(c != null && !c.isEmpty()) {
//						for(int ind = 0 ; ind < c.get(0).getItems().size() ; ind++) {
//							ItemElement mes = (ItemElement) c.get(0).getItems().get(ind).get(res.getYFixed() -1);
//							if(mes.getDataMember().getUniqueName().equalsIgnoreCase(measureUname)) {
//								measureIndex = ind;
//								break;
//							}
//						}
//					}
					Collections.sort(c, new Comparator<SortChunk>() {
						@Override
						public int compare(SortChunk o1, SortChunk o2) {
							
							String val1 = ((ItemValue)o1.getItems().get(measureIndex).get(res.getYFixed())).getValue();
							String val2 = ((ItemValue)o2.getItems().get(measureIndex).get(res.getYFixed())).getValue();
							Double double1 = Double.parseDouble(val1);
							Double double2 = Double.parseDouble(val2);
							int result = double1.compareTo(double2);
							
							if(sortType.equals("Asc")) {
								return result;
							}
							else {
								return - result;
							}
						}
					});
					
				}
				else {
				
					Collections.sort(c, new Comparator<SortChunk>() {
		
						@Override
						public int compare(SortChunk o1,SortChunk o2) {
							int res = comparator.compare(o1.getLabel(), o2.getLabel());
							
							if(sortType.equals("Asc")) {
								return res;
							}
							else {
								return - res;
							}
						}
					});
				}
				
				//remove and add the sorted elements
				int startIndex = c.get(0).getStartIndex();
				for(SortChunk ch : c) {
					
					for(int actual = startIndex ; actual < ch.getItems().size() + startIndex ; actual++) {
						res.getRaw().remove(actual);
					}
					int index = ch.getItems().size() - 1;
					for(int actual = ch.getItems().size() + startIndex ; actual > startIndex ; actual--) {
						res.getRaw().add(startIndex, ch.getItems().get(index));
						index--;
					}
 					
					startIndex += ch.getItems().size();
				}
				
			}
			
		}
	}

}
