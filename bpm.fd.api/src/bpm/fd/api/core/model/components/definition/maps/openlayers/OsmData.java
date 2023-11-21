package bpm.fd.api.core.model.components.definition.maps.openlayers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.fd.api.core.model.components.definition.IComponentDatas;
import bpm.fd.api.core.model.datas.DataSet;

public class OsmData implements IComponentDatas {

	private DataSet dataSet;

	private Integer zoneFieldIndex;
	private Integer zoneFieldLabelIndex;
	private Integer valueFieldIndex;
	
	private List<OsmDataSerie> series;

	@Override
	public DataSet getDataSet() {
		return dataSet;
	}

	@Override
	public Element getElement() {
		Element e = DocumentHelper.createElement("mapData");
		e.addAttribute("type", OsmData.class.getName());
		if (getValueFieldIndex() != null){
			e.addAttribute("valueFieldIndex", "" + getValueFieldIndex());
		}
		
		if (getZoneFieldIndex() != null){
			e.addAttribute("zoneFieldIndex", "" + getZoneFieldIndex());
		}
		if (getZoneFieldLabelIndex() != null){
			e.addAttribute("zoneFieldLabelIndex", "" + getZoneFieldLabelIndex());
		}
		
		Element elemSeries = e.addElement("series");
		if(series != null) {
			for(OsmDataSerie serie : series) {
				elemSeries.add(serie.getElement());
			}
		}
		
		if (dataSet != null){
			e.addElement("dataSet-ref").setText(dataSet.getName());
		}
		
		return e;
	}

	@Override
	public IComponentDatas getAdapter(Object o) {
		return this;
	}

	@Override
	public boolean isFullyDefined() {
		return dataSet != null && valueFieldIndex != null;
	}

	@Override
	public IComponentDatas copy() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setDataSet(DataSet dataset) {
		this.dataSet = dataset;
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

	public List<OsmDataSerie> getSeries() {
		
		Collections.sort(series, new Comparator<OsmDataSerie>() {
			@Override
			public int compare(OsmDataSerie o1, OsmDataSerie o2) {
				if(o1.getType().equals(OsmDataSerie.MARKER)) {
					return 1;
				}
				else if(o1.getType().equals(OsmDataSerie.POLYGON)) {
					return -1;
				}
				else if(o2.getType().equals(OsmDataSerie.MARKER)) {
					return -1;
				}
				return 0;
			}
			
		});
		
		return series;
	}

	public void setSeries(List<OsmDataSerie> series) {
		this.series = series;
		if(this.series != null) {
	 		Collections.sort(this.series, new Comparator<OsmDataSerie>() {
				@Override
				public int compare(OsmDataSerie o1, OsmDataSerie o2) {
					if(o1.getType().equals(OsmDataSerie.MARKER)) {
						return 1;
					}
					else if(o1.getType().equals(OsmDataSerie.POLYGON)) {
						return -1;
					}
					else if(o2.getType().equals(OsmDataSerie.MARKER)) {
						return -1;
					}
					else if(o2.getType().equals(OsmDataSerie.POLYGON)) {
						return 1;
					}
					return 0;
				}
				
			});
		}
	}
	
	public void addSerie(OsmDataSerie serie) {
		if(series == null) {
			series = new ArrayList<OsmDataSerie>();
		}
		series.add(serie);
		if(this.series != null) {
	 		Collections.sort(this.series, new Comparator<OsmDataSerie>() {
				@Override
				public int compare(OsmDataSerie o1, OsmDataSerie o2) {
					if(o1.getType().equals(OsmDataSerie.MARKER)) {
						return 1;
					}
					else if(o1.getType().equals(OsmDataSerie.POLYGON)) {
						return -1;
					}
					else if(o2.getType().equals(OsmDataSerie.MARKER)) {
						return -1;
					}
					return 0;
				}
				
			});
		}
	}

	public void removeSerie(OsmDataSerie serie) {
		series.remove(serie);
	}

	public Integer getZoneFieldLabelIndex() {
		return zoneFieldLabelIndex;
	}

	public void setZoneFieldLabelIndex(Integer zoneFieldLabelIndex) {
		this.zoneFieldLabelIndex = zoneFieldLabelIndex;
	}
}
