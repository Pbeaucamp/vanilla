package org.fasd.utils.trees;

import org.fasd.olap.OLAPMeasure;

public class TreeMes extends TreeParent {
	private OLAPMeasure mes;
	
	public TreeMes(OLAPMeasure m) {
		super(m.getName());
		mes = m;
	}
	
	public OLAPMeasure getOLAPMeasure() {
		return mes;
	}
}

