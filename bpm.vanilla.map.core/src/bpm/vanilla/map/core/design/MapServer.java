package bpm.vanilla.map.core.design;

import java.io.Serializable;
import java.util.List;

public class MapServer implements Serializable {
	
	public enum TypeServer {
		WMS,
		WFS,
		WMTS,
		ARCGIS
	}

	private static final long serialVersionUID = 1L;
	
	private int id;
	private String name;
	private String url;
	private TypeServer type;
	
	private List<MapLayer> layers;

	public MapServer() {
	}

	public MapServer(String name, String url) {
		this.name = name;
		this.url = url;
	}

	public MapServer(String name, String url, TypeServer type) {
		this.name = name;
		this.url = url;
		this.type = type;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	public TypeServer getType() {
		return type != null ? type : TypeServer.WMS;
	}
	
	public void setType(TypeServer type) {
		this.type = type;
	}
	
	public List<MapLayer> getLayers() {
		return layers;
	}
	
	public void setLayers(List<MapLayer> layers) {
		this.layers = layers;
	}

	@Override
	public String toString() {
		return name + (type != null ? " - (" + type.toString() + ")" : "");
	}
}
