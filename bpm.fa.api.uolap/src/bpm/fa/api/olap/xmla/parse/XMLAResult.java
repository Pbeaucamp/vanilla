package bpm.fa.api.olap.xmla.parse;

import java.util.ArrayList;
import java.util.List;

public class XMLAResult {
	private ArrayList<XMLACell> cells = new ArrayList<XMLACell>();
	
	private ArrayList<XMLAAxis> axes = new ArrayList<XMLAAxis>();
	
	public XMLAResult() {
		
	}
	
	public void addCell(XMLACell c) {
		cells.add(c);
	}
	
	public ArrayList<XMLACell> getCells() {
		return cells;
	}
	
	public void addAxis(XMLAAxis ax) {
		axes.add(ax);
	}
	
	public ArrayList<XMLAAxis> getAxes() {
		return axes;
	}
	
	public void brasseMerde() {
		//to keep the cells that arent presnet, must generate ItemNull in the OLAPResult
		List<Integer> ind = new ArrayList<Integer>();
		for (int i=0; i < cells.size(); i++) {
			//System.out.println("Doing nb=" + i + " value=" + cells.get(i).getNb());
			if (cells.get(i).getNb() > i) {
				//System.out.println("WE ADDED ONE§§§§§");
				//normally we can t have empty first, indicates duplicates
				cells.add(i + 1, new XMLACell(cells.get(i).getOrdinal(), cells.get(i).getValue(), cells.get(i).getFormattedValue()));
				ind.add(i);
			}
		}
		
		for(Integer i : ind){
			cells.get(i).setValue(null);
		}
	}
}
