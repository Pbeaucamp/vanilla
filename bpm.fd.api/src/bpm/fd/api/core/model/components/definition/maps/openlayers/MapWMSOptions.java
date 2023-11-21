package bpm.fd.api.core.model.components.definition.maps.openlayers;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.fd.api.core.model.components.definition.IComponentOptions;

public class MapWMSOptions implements IOpenLayersOptions {
	
	public static final String LAYER_TYPE_WMTS = "WMTS";
	public static final String LAYER_TYPE_WFS = "WFS";
	public static final String LAYER_TYPE_OSM = "OSM";
	public static final String LAYER_TYPE_MARKERS = "MARKERS";

	public static final int KEY_BASE_LAYER_URL = 0;
	public static final int KEY_BASE_LAYER_NAME = 1;
	public static final int KEY_VECTOR_LAYER_URL = 2;
	
	public static final int KEY_VECTOR_LAYER_NAME = 3;
	public static final int KEY_PROJECTION = 4;
	public static final int KEY_TILE_ORIGIN = 5;
	
	public static final int KEY_BOUNDS = 6;
	public static final int KEY_OPACITY = 7;
	
	public static final int KEY_BASE_LAYER_TYPE = 8;
	public static final int KEY_VECTOR_LAYER_TYPE = 9;
	public static final int KEY_VECTOR_LAYER_GEO = 10;
	
	public static String BASE_LAYER_URL = "baseLayerUrl";
	public static String BASE_LAYER_NAME = "baseLayerName";
	public static String VECTOR_LAYER_URL = "vectorLayerUrl";
	public static String VECTOR_LAYER_NAME = "vectorLayerName";
	public static String PROJECTION = "latitude";
	public static String TILE_ORIGIN = "zoom";
	public static String BOUNDS = "bounds";
	public static String OPACITY = "opacity";
	public static String BASE_LAYER_TYPE = "baseLayerType";
	public static String VECTOR_LAYER_TYPE = "vectorLayerType";
	public static String VECTOR_LAYER_GEO = "vectorLayerGeo";
	
	public static String[] standardKeys = new String[]{BASE_LAYER_URL, BASE_LAYER_NAME, VECTOR_LAYER_URL, VECTOR_LAYER_NAME, PROJECTION, TILE_ORIGIN, BOUNDS, OPACITY, BASE_LAYER_TYPE, VECTOR_LAYER_TYPE, VECTOR_LAYER_GEO};
	public static String[] i18nKeys = new String[]{};
	
	private String baseLayerType = "WMTS";
	private String vectorLayerType = "WFS";
	private String baseVectorGeometry;

	private String baseLayerUrl;
	private String baseLayerName;
	private String vectorLayerUrl;
	
	private String bounds;
	private String projection;
	private String tileOrigin;
	private String vectorLayerName;
	
	private String xml;
	private String opacity;
	
	@Override
	public IComponentOptions getAdapter(Object type) {
		return this;
	}

	@Override
	public String getDefaultLabelValue(String key) {
		return null;
	}

	@Override
	public Element getElement() {
		Element e = DocumentHelper.createElement("options");
		e.addAttribute("class", MapWMSOptions.class.getName());
		
		for(int i = 0; i < standardKeys.length; i++){
			try {
				e.addElement(standardKeys[i]).setText(getValue(standardKeys[i]));
			} catch (Exception e1) {
				System.out.println("No value for " + standardKeys[i]);
			}
		}
		return e;
	}

	@Override
	public String[] getInternationalizationKeys() {
		return i18nKeys;
	}

	@Override
	public String[] getNonInternationalizationKeys() {
		return standardKeys;
	}

	@Override
	public String getValue(String key) {
		int index = -1;
		
		for(int i = 0; i < standardKeys.length; i ++){
			if (standardKeys[i].equals(key)){
				index = i;
				break;
			}
		}
		switch (index) {
		case KEY_BASE_LAYER_URL:
			return baseLayerUrl;
		case KEY_BASE_LAYER_NAME:
			return baseLayerName;
		case KEY_VECTOR_LAYER_URL:
			return vectorLayerUrl;
		case KEY_VECTOR_LAYER_NAME:
			return vectorLayerName;
		case KEY_PROJECTION:
			return projection;
		case KEY_TILE_ORIGIN:
			return tileOrigin;
		case KEY_BOUNDS:
			return bounds;
		case KEY_OPACITY:
			return opacity;
		case KEY_BASE_LAYER_TYPE:
			return baseLayerType;
		case KEY_VECTOR_LAYER_GEO:
			return baseVectorGeometry;
		case KEY_VECTOR_LAYER_TYPE:
			return vectorLayerType;
		default:
			break;
		}
		return null;
	}
	@Override
	public String getZoneXml() {
		return xml;
	}
	
	public void setZoneXml(String xml) {
		this.xml = xml;
	}

	public String getBounds() {
		return bounds;
	}

	public void setBounds(String bounds) {
		this.bounds = bounds;
	}

	public String getProjection() {
		return projection;
	}

	public void setProjection(String projection) {
		this.projection = projection;
	}

	public String getTileOrigin() {
		return tileOrigin;
	}

	public void setTileOrigin(String tileOrigin) {
		this.tileOrigin = tileOrigin;
	}

	public String getBaseLayerUrl() {
		return baseLayerUrl;
	}

	public void setBaseLayerUrl(String baseLayerUrl) {
		this.baseLayerUrl = baseLayerUrl;
	}

	public String getBaseLayerName() {
		return baseLayerName;
	}

	public void setBaseLayerName(String baseLayerName) {
		this.baseLayerName = baseLayerName;
	}

	public String getVectorLayerUrl() {
		return vectorLayerUrl;
	}

	public void setVectorLayerUrl(String vectorLayerUrl) {
		this.vectorLayerUrl = vectorLayerUrl;
	}

	public String getVectorLayerName() {
		return vectorLayerName;
	}

	public void setVectorLayerName(String vectorLayerName) {
		this.vectorLayerName = vectorLayerName;
	}

	public String getOpacity() {
		return opacity;
	}

	public void setOpacity(String opacity) {
		this.opacity = opacity;
	}
	
	public String getBaseLayerType() {
		return baseLayerType;
	}

	public void setBaseLayerType(String baseLayerType) {
		this.baseLayerType = baseLayerType;
	}

	public String getVectorLayerType() {
		return vectorLayerType;
	}

	public void setVectorLayerType(String vectorLayerType) {
		this.vectorLayerType = vectorLayerType;
	}

	public String getBaseVectorGeometry() {
		return baseVectorGeometry;
	}

	public void setBaseVectorGeometry(String baseVectorGeometry) {
		this.baseVectorGeometry = baseVectorGeometry;
	}

	@Override
	public IComponentOptions copy() {
		MapWMSOptions copy = new MapWMSOptions();
		
		copy.setBaseLayerName(baseLayerName);
		copy.setBaseLayerType(baseLayerType);
		copy.setBaseLayerUrl(baseLayerUrl);
		copy.setBaseVectorGeometry(baseVectorGeometry);
		copy.setBounds(bounds);
		copy.setOpacity(opacity);
		copy.setProjection(projection);
		copy.setTileOrigin(tileOrigin);
		copy.setVectorLayerName(vectorLayerName);
		copy.setVectorLayerType(vectorLayerType);
		copy.setVectorLayerUrl(vectorLayerUrl);
		copy.setZoneXml(xml);
		
		return copy;
	}
	
}
