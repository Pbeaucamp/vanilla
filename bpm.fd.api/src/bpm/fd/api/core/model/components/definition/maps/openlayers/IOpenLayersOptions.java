package bpm.fd.api.core.model.components.definition.maps.openlayers;

import bpm.fd.api.core.model.components.definition.IComponentOptions;

public interface IOpenLayersOptions extends IComponentOptions {
	
	public static final String TYPE_WMS = "WMS";
	public static final String TYPE_SHAPEFILE = "SHAPE FILE";
	
	public String getZoneXml();
}
