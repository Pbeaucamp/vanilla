package bpm.birt.osm.core.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class OsmSerie implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name;
	private boolean display;
	private int datasetId;
	
	private String type;
	
	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isDisplay() {
		return display;
	}

	public void setDisplay(boolean display) {
		this.display = display;
	}

	public int getDatasetId() {
		return datasetId;
	}

	public void setDatasetId(int datasetId) {
		this.datasetId = datasetId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
