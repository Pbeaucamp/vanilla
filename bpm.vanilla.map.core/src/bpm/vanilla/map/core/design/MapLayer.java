package bpm.vanilla.map.core.design;

import java.io.Serializable;

public class MapLayer implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name;
	private String title;
	private String url;
	
	private MapServer parent;
	private MapLayerOption options;
	
	private boolean selected = true;
	
	public MapLayer() {
	}

	public MapLayer(String name, String title, MapServer parent) {
		this.name = name;
		this.title = title;
		this.parent = parent;
	}

	public MapLayer(String name, String title, String url, MapServer parent) {
		this(name, title, parent);
		this.url = url;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getUrl() {
		return "https://download.data.grandlyon.com/wfs/grandlyon?SERVICE=WFS&REQUEST=GetFeature&typename=adr_voie_lieu.adrnomvoie&VERSION=1.1.0&outputFormat=geojson";
	}
	
	public MapServer getParent() {
		return parent;
	}
	
	public boolean isSelected() {
		return selected;
	}
	
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
	public MapLayerOption getOptions() {
		return options;
	}
	
	public void setOptions(MapLayerOption options) {
		this.options = options;
	}
	
	@Override
	public String toString() {
		return title != null && !title.isEmpty() ? title : name;
	}
}
