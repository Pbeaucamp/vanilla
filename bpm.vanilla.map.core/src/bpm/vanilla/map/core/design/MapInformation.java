package bpm.vanilla.map.core.design;

import java.io.Serializable;
import java.util.List;

public class MapInformation implements Serializable {

	private static final long serialVersionUID = 1L;

	private MapServer wmts;
	private List<MapLayer> layers;
	
	public MapInformation() {
	}
	
	public MapInformation(MapServer wmts, List<MapLayer> layers) {
		this.wmts = wmts;
		this.layers = layers;
	}

	public MapServer getWmts() {
		return wmts;
	}
	
	public void setWmts(MapServer wmts) {
		this.wmts = wmts;
	}
	
	public List<MapLayer> getLayers() {
		return layers;
	}
	
	public void setLayers(List<MapLayer> layers) {
		this.layers = layers;
	}
}
