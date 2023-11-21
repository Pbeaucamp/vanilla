package bpm.csv.oda.ui.impl;

import java.util.Comparator;

import bpm.csv.oda.runtime.datas.CsvColumn;

public class ColumnComparator implements Comparator<CsvColumn> {

	public int compare(CsvColumn o1, CsvColumn o2) {
		int i1 = o1.getPosition();
		int i2 = o2.getPosition();
		
		return i1 - i2;
	}

}
