package bpm.faweb.shared.infoscube;

import java.util.ArrayList;
import java.util.List;

import bpm.vanilla.map.core.design.MapVanilla;

import com.google.gwt.user.client.rpc.IsSerializable;

public class MapInfo implements IsSerializable {

	private List<MapVanilla> maps;
	private List<String> dimensions;
	private List<String> measures;

	public List<MapVanilla> getMaps() {
		if(this.maps == null) {
			maps = new ArrayList<MapVanilla>();
		}
		return maps;
	}

	public void setMaps(List<MapVanilla> maps) {
		this.maps = maps;
	}

	public void addMap(MapVanilla map) {
		if(this.maps == null) {
			maps = new ArrayList<MapVanilla>();
		}
		this.maps.add(map);
	}

	public List<String> getDimensions() {
		if(this.dimensions == null) {
			dimensions = new ArrayList<String>();
		}
		return dimensions;
	}

	public void setDimensions(List<String> dimensions) {
		this.dimensions = dimensions;
	}

	public void addDimension(String dimension) {
		if(this.dimensions == null) {
			dimensions = new ArrayList<String>();
		}
		dimensions.add(dimension);
	}

	public List<String> getMeasures() {
		if(this.measures == null) {
			measures = new ArrayList<String>();
		}
		return measures;
	}

	public void setMeasures(List<String> measures) {
		this.measures = measures;
	}

	public void addMeasure(String measure) {
		if(this.measures == null) {
			measures = new ArrayList<String>();
		}
		measures.add(measure);
	}

}
