package bpm.fm.api.model.utils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import bpm.fm.api.model.Axis;
import bpm.fm.api.model.Level;

public class AxisQueries implements Serializable {
	
	private Axis axis;
	
	private HashMap<List<Level>, String> levelQueries = new HashMap<List<Level>, String>();

	public AxisQueries() {}
	
	public AxisQueries(Axis axis) {
		this.axis = axis;
	}
	
	public Axis getAxis() {
		return axis;
	}

	public void setAxis(Axis axis) {
		this.axis = axis;
	}

	public HashMap<List<Level>, String> getLevelQueries() {
		return levelQueries;
	}

	public void setLevelQueries(HashMap<List<Level>, String> levelQueries) {
		this.levelQueries = levelQueries;
	}
	
	public void addLevelQuery(List<Level> levels, String query) {
		if(levelQueries == null) {
			levelQueries = new HashMap<List<Level>, String>();
		}
		levelQueries.put(levels, query);
	}
	
}
