package bpm.faweb.client.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bpm.faweb.shared.infoscube.GridCube;
import bpm.faweb.shared.infoscube.ItemCube;

import com.google.gwt.i18n.client.NumberFormat;

public class CumulateValues {

	private static int ifirst;
	private static int jfirst;
	
	public static GridCube cumulateValues(int i, int j, GridCube gc) {
		
		findIJFirst(gc);
		
		HashMap<Integer, List<ItemCube>> values = null;
		if(i > ifirst) {
			values = findRowsElements(i, j, gc);
		}
		else {
			values = findColumnsElements(i, j, gc);
			
		}
		cumulate(values);
		
		return gc;
	}
	
	private static HashMap<Integer, List<ItemCube>> findRowsElements(int i, int j, GridCube gc) {
		//find the first and last row
		List<Integer> exclude = new ArrayList<Integer>();
		
		int minRow = 0;
		for(int row = i ; row > 0 ; row--) {
			if(gc.getIJ(row, j).getType().equals("ItemNull") || (gc.getIJ(row, j).getType().equals("ItemElement") && gc.getIJ(row, j).getLabel().isEmpty())) {
				if(j > 0) {
					if(gc.getIJ(row, j - 1).getType().equals("ItemNull") || (gc.getIJ(row, j - 1).getType().equals("ItemElement") && gc.getIJ(row, j - 1).getLabel().isEmpty())) {
						exclude.add(row);
						continue;
					}
				}
				minRow = row + 1;
				break;
			}
		}
		
		int maxRow = i;
		
		int index = 0;
		
		HashMap<Integer, List<ItemCube>> result = new HashMap<Integer, List<ItemCube>>();
		
		for(int actualCol = jfirst ; actualCol < gc.getNbOfCol() ; actualCol ++) {
			List<ItemCube> items = new ArrayList<ItemCube>();
			
			for(int actual = minRow ; actual <= maxRow ; actual++) {
				if(!exclude.contains(actual)) {
					items.add(gc.getIJ(actual, actualCol));
				}
			}
			
			result.put(index, items);
			index++;
		}
		
		return result;
	}

	private static HashMap<Integer, List<ItemCube>> findColumnsElements(int i, int j, GridCube gc) {
		//find the first and last row
		List<Integer> exclude = new ArrayList<Integer>();
		
		int minCol = 0;
		for(int col = j ; col > 0 ; col--) {
			if(gc.getIJ(i, col).getType().equals("ItemNull") || (gc.getIJ(i, col).getType().equals("ItemElement") && gc.getIJ(i, col).getLabel().isEmpty())) {
				if(i > 0) {
					if(gc.getIJ(i - 1, col).getType().equals("ItemNull") || (gc.getIJ(i - 1, col).getType().equals("ItemElement") && gc.getIJ(i - 1, col).getLabel().isEmpty())) {
						exclude.add(col);
						continue;
					}
				}
				minCol = col + 1;
				break;
			}
		}
		
		int maxCol = j;
		
		int index = 0;
		
		HashMap<Integer, List<ItemCube>> result = new HashMap<Integer, List<ItemCube>>();
		
		for(int actualRow = ifirst ; actualRow < gc.getNbOfRow() ; actualRow ++) {
			List<ItemCube> items = new ArrayList<ItemCube>();
			
			for(int actual = minCol ; actual <= maxCol ; actual++) {
				if(!exclude.contains(actual)) {
					items.add(gc.getIJ(actualRow, actual));
				}
			}
			
			result.put(index, items);
			index++;
		}
		
		return result;
	}
	
	private static void cumulate(HashMap<Integer, List<ItemCube>> values) {
		for(int i = 0 ; i < values.keySet().size() ; i++) {
			for(int j = 0 ; j < values.get(i).size() ; j++) {
				ItemCube actual = values.get(i).get(j);
				if(j > 0) {
					actual.setValue(actual.getValue() + values.get(i).get(j - 1).getValue());
				}
				actual.setLabel(NumberFormat.getDecimalFormat().format(actual.getValue()));
			}
		}
		
	}

	private static void findIJFirst(GridCube cube) {
		boolean find = false;
		for (int i = 0; i < cube.getItems().size(); i++) {
			for (int j = 0; j < cube.getLigne(i).size(); j++) {
				if (((ItemCube) cube.getIJ(i, j)).getType().equalsIgnoreCase("ItemValue")) {
					ifirst = i;
					jfirst = j;
					find = true;
					break;
				}
			}
			if (find) {
				break;
			}
		}
	}
}
