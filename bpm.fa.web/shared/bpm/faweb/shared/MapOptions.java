package bpm.faweb.shared;

import java.util.List;

import bpm.vanilla.map.core.design.MapInformation;

import com.google.gwt.user.client.rpc.IsSerializable;

public class MapOptions implements IsSerializable {

	private String uname;
	private String element;
	
	private int datasetId;
	private String selectedMeasure;
	private String selectedDimension;
	private List<List<String>> colors;
	
	private MapInformation mapInfo;
	
	public MapOptions() {
	}
	
	public MapOptions(String uname, String element, int datasetId, String selectedMeasure, String selectedDimension, List<List<String>> colors, MapInformation mapInfo) {
		this.uname = uname;
		this.element = element;
		this.datasetId = datasetId;
		this.selectedMeasure = selectedMeasure;
		this.selectedDimension = selectedDimension;
		this.colors = colors;
		this.mapInfo = mapInfo;
	}

	public void updateMap(int datasetId, String selectedMeasure, String selectedDimension, List<List<String>> colors) {
		this.datasetId = datasetId;
		this.selectedMeasure = selectedMeasure;
		this.selectedDimension = selectedDimension;
		this.colors = colors;
	}
	
	public String getUname() {
		return uname;
	}
	
	public String getElement() {
		return element;
	}
	
	public int getDatasetId() {
		return datasetId;
	}
	
	public MapInformation getMapInfo() {
		return mapInfo;
	}
	
	public void setMapInfo(MapInformation mapInfo) {
		this.mapInfo = mapInfo;
	}
	
	public String getSelectedMeasure() {
		return selectedMeasure;
	}
	
	public String getSelectedDimension() {
		return selectedDimension;
	}
	
	public List<List<String>> getColors() {
		return colors;
	}
}
