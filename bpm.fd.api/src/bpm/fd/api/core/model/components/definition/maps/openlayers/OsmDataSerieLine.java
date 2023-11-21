package bpm.fd.api.core.model.components.definition.maps.openlayers;

import org.dom4j.Element;

import bpm.fd.api.core.model.datas.DataSet;

public class OsmDataSerieLine extends OsmDataSerie {
	private static int number = 1;
	
	private DataSet dataset;

	private Integer zoneFieldIndex;
	private Integer latitudeFieldIndex;
	private Integer longitudeFieldIndex;
	
	public OsmDataSerieLine(Element elem) {
		name = elem.element("name").getText();
		type = elem.element("type").getText();
		
		try {
			zoneFieldIndex = Integer.parseInt(elem.element("zoneFieldIndex").getText());
		} catch (Exception e) {
			
		}
		try {
			latitudeFieldIndex = Integer.parseInt(elem.element("latitudeFieldIndex").getText());
		} catch (Exception e) {
			
		}
		try {
			longitudeFieldIndex = Integer.parseInt(elem.element("longitudeFieldIndex").getText());
		} catch (Exception e) {
			
		}
	}
	
	public OsmDataSerieLine() {
		name = "Line_Serie_" + number;
		type = OsmDataSerie.LINE;
		number++;
	}

	public DataSet getDataset() {
		return dataset;
	}

	public void setDataset(DataSet dataset) {
		this.dataset = dataset;
	}
	
	public Integer getZoneFieldIndex() {
		return zoneFieldIndex;
	}

	public void setZoneFieldIndex(Integer zoneFieldIndex) {
		this.zoneFieldIndex = zoneFieldIndex;
	}

	public Integer getLatitudeFieldIndex() {
		return latitudeFieldIndex;
	}

	public void setLatitudeFieldIndex(Integer latitudeFieldIndex) {
		this.latitudeFieldIndex = latitudeFieldIndex;
	}

	public Integer getLongitudeFieldIndex() {
		return longitudeFieldIndex;
	}

	public void setLongitudeFieldIndex(Integer longitudeFieldIndex) {
		this.longitudeFieldIndex = longitudeFieldIndex;
	}

	@Override
	public Element getElement() {
		Element elem = super.getElement();
		
		if(getLatitudeFieldIndex() != null) {
			elem.addElement("latitudeFieldIndex").setText(getLatitudeFieldIndex() + "");
		}
		if(getLongitudeFieldIndex() != null) {
			elem.addElement("longitudeFieldIndex").setText(getLongitudeFieldIndex() + "");
		}
		if(getZoneFieldIndex() != null) {
			elem.addElement("zoneFieldIndex").setText(getZoneFieldIndex() + "");
		}
		
		if (dataset != null){
			elem.addElement("dataSet-ref").setText(dataset.getName());
		}
		
		return elem;
	}
	
	
}
