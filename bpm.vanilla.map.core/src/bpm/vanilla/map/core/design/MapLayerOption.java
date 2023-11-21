package bpm.vanilla.map.core.design;

import java.io.Serializable;

public class MapLayerOption implements Serializable {
	
	public static final Integer DEFAULT_OPACITY = 50;

	private static final long serialVersionUID = 1L;

	private Integer opacity;

	public MapLayerOption() {
	}

	public MapLayerOption(Integer opacity) {
		this.opacity = opacity;
	}
	
	public Integer getOpacity() {
		return opacity;
	}
	
	public void setOpacity(Integer opacity) {
		this.opacity = opacity;
	}
}
