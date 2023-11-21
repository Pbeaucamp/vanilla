package bpm.gateway.core.transformations.kml;

import bpm.gateway.core.DataStream;

public interface Kml extends DataStream{
	public KmlObjectType getKmlObjectType();
	public void setKmlObjectType(KmlObjectType type);
}
