package bpm.united.olap.api.result.impl;

import java.util.ArrayList;
import java.util.List;

import bpm.united.olap.api.result.DrillThroughIdentifier;

public class DrillThroughIdentifierImpl implements DrillThroughIdentifier {

	private List<String> intersections;
	private String measure;
	private List<String> wheres;
	
	@Override
	public List<String> getIntersections() {
		if(intersections == null) {
			intersections = new ArrayList<String>();
		}
		return intersections;
	}

	@Override
	public String getMeasure() {
		return measure;
	}

	@Override
	public List<String> getWheres() {
		if(wheres == null) {
			wheres = new ArrayList<String>();
		}
		return wheres;
	}

	@Override
	public void setIntersections(List<String> intersections) {
		this.intersections = intersections;
	}

	@Override
	public void setMeasure(String measure) {
		this.measure = measure;
	}

	@Override
	public void setWheres(List<String> wheres) {
		this.wheres = wheres;
	}

	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder();
		boolean first = true;
		if(intersections != null) {
			buf.append("intersections(");
			
			for(String s : intersections) {
				if(first) {
					first = false;
				}
				else {
					buf.append(",");
				}
				buf.append(s);
			}
			buf.append(")");
		}
		
		if(measure != null) {
			buf.append(",measures(" + measure);
			buf.append(")");
		}
		
		if(wheres != null) {
			buf.append(",wheres(");
			first = true;
			for(String s : wheres) {
				if(first) {
					first = false;
				}
				else {
					buf.append(",");
				}
				buf.append(s);
			}
			buf.append("),");
		}
		
		return buf.toString();
	}
}
