package bpm.birt.osm.core.model;

public class OsmSerieMarker extends OsmSerie {

	private static final long serialVersionUID = 1L;

	private int minMarkerSize;
	private int maxMarkerSize;

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

}
