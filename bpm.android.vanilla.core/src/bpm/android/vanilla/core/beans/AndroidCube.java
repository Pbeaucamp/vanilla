package bpm.android.vanilla.core.beans;

import java.util.ArrayList;
import java.util.List;

import bpm.android.vanilla.core.beans.cube.AndroidDimension;
import bpm.android.vanilla.core.beans.cube.AndroidMeasureGroup;

public class AndroidCube extends AndroidItem {
	
	private static final long serialVersionUID = 1L;
	
	private List<String> cubeNames;
	
	private String selectedCubeName;
	private List<AndroidCubeView> views;
	private List<AndroidDimension> dimensions;
	private List<AndroidMeasureGroup> measures;
	
	private String html;
	
	public AndroidCube() { }
	
	public AndroidCube(int id, String name, int type, int subType, List<String> cubeNames) {
		super(id, name, type, subType, null);
		this.cubeNames = cubeNames;
	}

	public List<String> getCubeNames() {
		return cubeNames;
	}

	public String getSelectedCubeName() {
		return selectedCubeName;
	}

	public void setSelectedCubeName(String selectedCubeName) {
		this.selectedCubeName = selectedCubeName;
	}

	public List<AndroidDimension> getDimensions() {
		return dimensions;
	}

	public void setDimensions(List<AndroidDimension> dimensions) {
		this.dimensions = dimensions;
	}

	public List<AndroidMeasureGroup> getMeasures() {
		return measures;
	}

	public void setMeasures(List<AndroidMeasureGroup> measures) {
		this.measures = measures;
	}

	public String getHtml() {
		return html;
	}

	public void setHtml(String html) {
		this.html = html;
	}

	public void addView(AndroidCubeView androidView) {
		if(views == null) {
			this.views = new ArrayList<AndroidCubeView>();
		}
		this.views.add(androidView);
	}
	
	public List<AndroidCubeView> getViews() {
		return views;
	}

	public void addDimension(AndroidDimension dimension) {
		if(dimensions == null) {
			this.dimensions = new ArrayList<AndroidDimension>();
		}
		this.dimensions.add(dimension);
	}

	public void addMeasure(AndroidMeasureGroup measure) {
		if(measures == null) {
			this.measures = new ArrayList<AndroidMeasureGroup>();
		}
		this.measures.add(measure);
	}
}
