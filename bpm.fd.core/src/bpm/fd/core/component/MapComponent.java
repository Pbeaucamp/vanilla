package bpm.fd.core.component;

import java.util.ArrayList;
import java.util.List;

import bpm.fd.api.core.model.components.definition.maps.openlayers.VanillaMapDataSerie;
import bpm.fd.core.DashboardComponent;
import bpm.fd.core.IComponentData;
import bpm.fd.core.IComponentOption;
import bpm.vanilla.map.core.design.MapVanilla;
import bpm.vanilla.platform.core.beans.data.Dataset;

public class MapComponent extends DashboardComponent implements IComponentData, IComponentOption {

	public static final String ORIENTATION_HORIZONTAL = "Horizontal";
	public static final String ORIENTATION_VERTICAL = "Vertical";

	public static final String LAYOUT_TOP = "Top";
	public static final String LAYOUT_BOTTOM = "Bottom";
	public static final String LAYOUT_LEFT = "Left";
	public static final String LAYOUT_RIGHT = "Right";
	
	public static List<String> orientations;
	public static List<String> layouts;
	
	static {
		orientations = new ArrayList<String>();
		orientations.add(ORIENTATION_HORIZONTAL);
		orientations.add(ORIENTATION_VERTICAL);
		
		layouts = new ArrayList<String>();
		layouts.add(LAYOUT_BOTTOM);
		layouts.add(LAYOUT_TOP);
		layouts.add(LAYOUT_LEFT);
		layouts.add(LAYOUT_RIGHT);
	}

	private static final long serialVersionUID = 1L;

	private Dataset dataset;

	private Integer zoneFieldIndex;
	private Integer valueFieldIndex;

	private List<VanillaMapDataSerie> series;

	private Integer mapId;
	private MapVanilla map;

	private boolean showLegend;
	private boolean showBaseLayer = true;

	private String legendOrientation = ORIENTATION_HORIZONTAL;
	private String legendLayout = LAYOUT_BOTTOM;
	private String numberFormat = "";

	@Override
	public Dataset getDataset() {
		return dataset;
	}

	@Override
	public void setDataset(Dataset dataset) {
		this.dataset = dataset;
	}

	public Integer getZoneFieldIndex() {
		return zoneFieldIndex;
	}

	public void setZoneFieldIndex(Integer zoneFieldIndex) {
		this.zoneFieldIndex = zoneFieldIndex;
	}

	public Integer getValueFieldIndex() {
		return valueFieldIndex;
	}

	public void setValueFieldIndex(Integer valueFieldIndex) {
		this.valueFieldIndex = valueFieldIndex;
	}

	public List<VanillaMapDataSerie> getSeries() {
		return series;
	}

	public void setSeries(List<VanillaMapDataSerie> series) {
		this.series = series;
	}

	public void addSerie(VanillaMapDataSerie serie) {
		if (series == null) {
			series = new ArrayList<VanillaMapDataSerie>();
		}
		series.add(serie);
	}

	public void removeSerie(VanillaMapDataSerie serie) {
		series.remove(serie);
	}

	public Integer getMapId() {
		return mapId;
	}

	public void setMapId(Integer mapId) {
		this.mapId = mapId;
	}

	public MapVanilla getMap() {
		return map;
	}

	public void setMap(MapVanilla map) {
		this.map = map;
		this.mapId = map.getId();
	}

	public boolean isShowLegend() {
		return showLegend;
	}

	public void setShowLegend(boolean showLegend) {
		this.showLegend = showLegend;
	}

	public boolean isShowBaseLayer() {
		return showBaseLayer;
	}

	public void setShowBaseLayer(boolean showBaseLayer) {
		this.showBaseLayer = showBaseLayer;
	}

	public String getLegendOrientation() {
		return legendOrientation;
	}

	public void setLegendOrientation(String legendOrientation) {
		this.legendOrientation = legendOrientation;
	}

	public String getLegendLayout() {
		return legendLayout;
	}

	public void setLegendLayout(String legendLayout) {
		this.legendLayout = legendLayout;
	}

	public String getNumberFormat() {
		return numberFormat;
	}

	public void setNumberFormat(String numberFormat) {
		this.numberFormat = numberFormat;
	}

	@Override
	public ComponentType getType() {
		return ComponentType.MAP;
	}

	@Override
	protected void clearData() {
		this.dataset = null;

		this.zoneFieldIndex = null;
		this.valueFieldIndex = null;

		this.series = null;

		this. mapId = null;
		this.map = null;
	}

}
