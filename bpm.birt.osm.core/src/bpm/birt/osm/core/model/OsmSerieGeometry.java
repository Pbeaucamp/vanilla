package bpm.birt.osm.core.model;

import java.util.ArrayList;
import java.util.List;

public class OsmSerieGeometry extends OsmSerie {

	private static final long serialVersionUID = 1L;
	
	private List<ColorRange> colorRanges = new ArrayList<ColorRange>();

	public List<ColorRange> getColorRanges() {
		return colorRanges;
	}

	public void setColorRanges(List<ColorRange> colorRanges) {
		this.colorRanges = colorRanges;
	}
}
