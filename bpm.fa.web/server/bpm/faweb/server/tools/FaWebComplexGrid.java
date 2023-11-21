package bpm.faweb.server.tools;

import java.util.ArrayList;
import java.util.List;

import bpm.faweb.server.beans.InfosOlap;
import bpm.faweb.server.security.FaWebSession;
import bpm.faweb.shared.infoscube.GridComplex;
import bpm.faweb.shared.infoscube.GridCube;
import bpm.faweb.shared.infoscube.InfosReport;
import bpm.faweb.shared.infoscube.ItemCube;

public class FaWebComplexGrid {
	private static int lastCol = -1;
	
	public static GridComplex getComplexGrid(FaWebSession session, InfosOlap infos) {
		InfosReport report = infos.getInfosReport();
		int jFirst = report.getJFirst();
		
		Integer[] spanOnCol = new Integer[jFirst];
		
		GridCube grid = report.getGrid();
		List<ArrayList<ItemCube>> items = grid.getItems();
		
		Integer[] firstNotNull = new Integer[grid.getItems().size()];
		
		lastCol = jFirst;

		//Boucle de lecture
		for (int i=0; i < items.size(); i++) {//row
			for (int j=0; j < items.get(i).size(); j++) { //col
				ItemCube curr = items.get(i).get(j);
				
				if(curr.getType().equals("ItemElement")){
					if(curr.getLabel().equals("")){
						curr.setType("null");
					}
					else if (j < lastCol) {
						int span = countRowSpan(curr, i, j, grid);
						curr.setRowSpan(span);
						if (span > 1) {
							if (spanOnCol[j] == null) {
								spanOnCol[j] = new Integer(span);
							}
						}
					}						
				}
			}
		}
		
		for (int i = 0; i < items.size(); i++) {
			int nbOfNull = 0;
			for (int j=0; j < items.get(i).size(); j++) {
				ItemCube curr = items.get(i).get(j);
				if( curr.getType().equalsIgnoreCase("spaned") ) {
					nbOfNull++;
				}
				curr.setSpanBefore(nbOfNull);
			}
			firstNotNull[i] = new Integer(nbOfNull);
		}
		
		GridComplex g = new GridComplex();
		
		g.setItems(items);
		g.setFirstNotNull(firstNotNull);
		g.setProperties(infos.getCube().getShowProperties());
		g.setZero(infos.getCube().getShowEmpty());
		
		return g;
	}

	private static int countRowSpan(ItemCube cellule, int i, int j, GridCube grid) {
		int res = 1;
		List<ItemCube> colonne = grid.getCol(j);
		List<ItemCube> sublist = colonne.subList(i+1, colonne.size());
	
		int k = i+1;
		for(ItemCube item : sublist) {
			if (item.getType().equals("ItemNull") || item.getLabel().equals("")) {
				if (j > 0) {
					ItemCube ic = grid.getIJ(k, j-1);
					if (ic.getType().equalsIgnoreCase("ItemElement")) {
						return res;
					}
				}
				item.setType("spaned");
				res ++;
				k++;
			}
			else if (!item.getLabel().equals("")) {
				return res;
			}
		}
		return res;
	}
}
