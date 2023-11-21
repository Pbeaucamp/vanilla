package bpm.fd.api.core.model.components.definition.maps.openlayers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import bpm.vanilla.map.core.design.MapDataSet;
import bpm.vanilla.map.core.design.fusionmap.ColorRange;

public class VanillaMapDataSerie implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String POLYGON = "polygon";
	public static final String MARKER = "point";
	public static final String LINE = "line";

	private String name;
	private String type = POLYGON;

	private Integer mapDatasetId;
	private MapDataSet mapDataset;

	private Integer minMarkerSize = 10;
	private Integer maxMarkerSize = 25;

	private String minColor = "FF0000";
	private String maxColor = "00FF00";
	
	private List<ColorRange> markerColors = new ArrayList<ColorRange>();

	private boolean display = true;
	
	private boolean useColsForColors = false;

//	private transient FdModel targetPage;

	private String targetPageName;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getMapDatasetId() {
		return mapDatasetId;
	}

	public void setMapDatasetId(Integer mapDatasetId) {
		this.mapDatasetId = mapDatasetId;
	}

	public MapDataSet getMapDataset() {
		return mapDataset;
	}

	public void setMapDataset(MapDataSet mapDataset) {
		this.mapDataset = mapDataset;
		this.mapDatasetId = mapDataset.getId();
		this.type = mapDataset.getType();
	}

	public int getMinMarkerSize() {
		return minMarkerSize;
	}

	public void setMinMarkerSize(int minMarkerSize) {
		this.minMarkerSize = minMarkerSize;
	}

	public int getMaxMarkerSize() {
		return maxMarkerSize;
	}

	public void setMaxMarkerSize(int maxMarkerSize) {
		this.maxMarkerSize = maxMarkerSize;
	}

	public String getMinColor() {
		return minColor;
	}

	public void setMinColor(String minColor) {
		this.minColor = minColor;
	}

	public String getMaxColor() {
		return maxColor;
	}

	public void setMaxColor(String maxColor) {
		this.maxColor = maxColor;
	}

	public boolean isDisplay() {
		return display;
	}

	public void setDisplay(boolean display) {
		this.display = display;
	}

//	public FdModel getTargetPage() {
//		return targetPage;
//	}
//
//	public void setTargetPage(FdModel targetPage) {
//		this.targetPage = targetPage;
//	}

	public String getTargetPageName() {
		return targetPageName;
	}

	public void setTargetPageName(String targetPageName) {
		this.targetPageName = targetPageName;
	}

	public List<ColorRange> getMarkerColors() {
		return markerColors;
	}

	public void setMarkerColors(List<ColorRange> colorRanges) {
		this.markerColors = colorRanges;
	}

	public void addMarkerColor(ColorRange colorRange) {
		this.markerColors.add(colorRange);
	}
	
	public void removeMarkerColor(ColorRange colorRange) {
		this.markerColors.remove(colorRange);
	}

	public boolean isUseColsForColors() {
		return useColsForColors;
	}

	public void setUseColsForColors(boolean useColsForColors) {
		this.useColsForColors = useColsForColors;
	}
}
