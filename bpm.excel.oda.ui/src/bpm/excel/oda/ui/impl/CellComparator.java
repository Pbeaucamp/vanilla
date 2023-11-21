package bpm.excel.oda.ui.impl;

import java.util.Comparator;

import jxl.Cell;

public class CellComparator implements Comparator<Cell> {

	public int compare(Cell o1, Cell o2) {
		int i1 = o1.getColumn();
		int i2 = o2.getColumn();
		
		
		return i1 - i2;
	}

}
