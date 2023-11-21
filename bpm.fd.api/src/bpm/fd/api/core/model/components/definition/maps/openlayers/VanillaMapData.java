package bpm.fd.api.core.model.components.definition.maps.openlayers;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.fd.api.core.model.components.definition.IComponentDatas;
import bpm.fd.api.core.model.datas.DataSet;
import bpm.vanilla.map.core.design.MapVanilla;
import bpm.vanilla.map.core.design.fusionmap.ColorRange;

public class VanillaMapData implements IComponentDatas {

	private DataSet dataset;

	private Integer zoneFieldIndex;
	private Integer valueFieldIndex;
	
	private List<VanillaMapDataSerie> series;
	
	private Integer mapId;
	private MapVanilla map;
	
	@Override
	public DataSet getDataSet() {
		return dataset;
	}

	@Override
	public Element getElement() {
		Element e = DocumentHelper.createElement("vanillaMapData");
		e.addAttribute("type", VanillaMapData.class.getName());
		if (getValueFieldIndex() != null){
			e.addAttribute("valueFieldIndex", "" + getValueFieldIndex());
		}
		
		if (getZoneFieldIndex() != null){
			e.addAttribute("zoneFieldIndex", "" + getZoneFieldIndex());
		}
		
		if (getMapId() != null){
			e.addAttribute("mapId", "" + getMapId());
		}
		
		Element elemSeries = e.addElement("series");
		if(series != null) {
			for(VanillaMapDataSerie serie : series) {
				
				Element serieElem = DocumentHelper.createElement("serie");
				serieElem.addElement("name").setText(serie.getName());
				serieElem.addElement("type").setText(serie.getType());
				
				Element colorRanges = serieElem.addElement("colorRanges");
				try {
					
					colorRanges.addElement("minColor").setText(serie.getMinColor());
					colorRanges.addElement("maxColor").setText(serie.getMaxColor());
					
				} catch (Exception e1) {
				}
				
				serieElem.addElement("datasetId").setText(serie.getMapDatasetId() + "");
				serieElem.addElement("display").setText(serie.isDisplay() + "");
				
				try {
					serieElem.addElement("minMarkerSize").setText(serie.getMinMarkerSize() + "");
					serieElem.addElement("maxMarkerSize").setText(serie.getMaxMarkerSize() + "");
				} catch(Exception e1) {
					e1.printStackTrace();
				}
				try {
					serieElem.addElement("targetModel").setText(serie.getTargetPageName());
				} catch(Exception ex) {
				}
				
				try {
					Element markerColors = serieElem.addElement("markerColors");
					for(ColorRange r : serie.getMarkerColors()) {
						Element er = markerColors.addElement("colorRange");
						er.addAttribute("color", r.getHex());
						
						er.addAttribute("legend", r.getName());
						if (r.getMax() != null){
							er.addAttribute("maxValue", r.getMax() + "");
						}
						if (r.getMin() != null){
							er.addAttribute("minValue", r.getMin() + "");
						}
					}
				} catch(Exception ex) {
				}
				
				try {
					serieElem.addElement("useColsForColors").setText(String.valueOf(serie.isUseColsForColors()));
				} catch(Exception ex) {
				}
				
				elemSeries.add(serieElem);
			}
		}
		
		if (dataset != null){
			e.addElement("dataSet-ref").setText(dataset.getName());
		}
		
		return e;
	}

	@Override
	public IComponentDatas getAdapter(Object o) {
		return this;
	}

	@Override
	public boolean isFullyDefined() {
		return true;
	}

	@Override
	public IComponentDatas copy() {
		// TODO Auto-generated method stub
		return null;
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

	public void setDataset(DataSet dataset) {
		this.dataset = dataset;
	}
	
	public void addSerie(VanillaMapDataSerie serie) {
		if(series == null) {
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
		mapId = map.getId();
	}
}
