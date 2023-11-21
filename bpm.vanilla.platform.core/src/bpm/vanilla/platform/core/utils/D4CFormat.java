package bpm.vanilla.platform.core.utils;

public enum D4CFormat {
	GEOJSON(0),
	ZIP_GEOJSON(1),
	SHAPE(2),
	ZIP_SHAPE(3),
	KML(4),
	ZIP_KML(5),
	WFS(6),
	CSV(7),
	XLSX(8),
	ZIP(9),
	WMS(10),
	JSON(11),
	GML(12),
	UNKNOWN(13);
	
	private int order;
	
	private D4CFormat(int order) {
		this.order = order;
	}
	
	public int getOrder() {
		return order;
	}
}
