package bpm.fd.api.core.model.components.definition.maps.openlayers;

import org.dom4j.Element;

import bpm.fd.api.core.model.FdModel;

public class OsmDataSerieMarker extends OsmDataSerie {

	private static int number = 1;
	
	private Integer latitudeFieldIndex;
	private Integer longitudeFieldIndex;

	private int minMarkerSize = 1;
	private int maxMarkerSize = 10;
	
	private String markerUrl;
	
	private FdModel targetPage;

	private String targetPageName;
	
	public OsmDataSerieMarker(Element elem) {
		name = elem.element("name").getText();
		type = elem.element("type").getText();
		
		try {
			setMinMarkerSize(Integer.parseInt(elem.element("minMarkerSize").getText()));
		} catch (Exception e) {
			
		}
		try {
			setMaxMarkerSize(Integer.parseInt(elem.element("maxMarkerSize").getText()));
		} catch (Exception e) {
			
		}
		try {
			setMarkerUrl(elem.element("markerUrl").getText());
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
	
	public OsmDataSerieMarker() {
		name = "Marker_Serie_" + number;
		type = OsmDataSerie.MARKER;
		number++;
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
		elem.addElement("minMarkerSize").setText(getMinMarkerSize() + "");
		elem.addElement("maxMarkerSize").setText(getMaxMarkerSize() + "");
		if(getMarkerUrl() != null) {
			elem.addElement("markerUrl").setText(getMarkerUrl() + "");
		}
		
		try {
			elem.addElement("targetModel").setText(getTargetPageName());
		} catch(Exception e) {
		}
		
		return elem;
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

	public String getMarkerUrl() {
		return markerUrl;
	}

	public void setMarkerUrl(String markerUrl) {
		this.markerUrl = markerUrl;
	}

	public FdModel getTargetPage() {
		return targetPage;
	}

	public void setTargetPage(FdModel targetPage) {
		this.targetPage = targetPage;
		targetPageName = targetPage.getName();
	}
	
	public String getTargetPageName() {
		try {
			if(targetPageName == null || targetPageName.isEmpty()) {
				targetPageName = targetPage.getName();
			}
			return targetPageName;
		} catch(Exception e) {
			return null;
		}
	}
	
	public void setTargetPageName(String name) {
		targetPageName = name;
	}
}
