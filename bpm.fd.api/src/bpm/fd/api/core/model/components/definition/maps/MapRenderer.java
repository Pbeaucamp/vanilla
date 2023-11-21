package bpm.fd.api.core.model.components.definition.maps;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.fd.api.core.model.components.definition.IComponentRenderer;

public class MapRenderer implements IComponentRenderer{
	public static final int VANILLA_FUSION_MAP = 0;
	public static final int VANILLA_GOOGLE_MAP = 1;
	public static final int VANILLA_FLASH_MAP = 2;
	public static final int VANILLA_OPENLAYERS_MAP = 3;
	
	public static final String[] RENDERER_NAMES = new String[]{
		"Vanilla Fusion Map", "Vanilla Google Map", "Vanilla Flash Map", "OpenLayers Map"
	};
	
	private static final MapRenderer[] renderers = new MapRenderer[]{
		new MapRenderer(VANILLA_FUSION_MAP),new MapRenderer(VANILLA_GOOGLE_MAP), new MapRenderer(VANILLA_FLASH_MAP), new MapRenderer(VANILLA_OPENLAYERS_MAP)
	};
	
	public static MapRenderer getRenderer(int i) throws Exception{
		if (i >= renderers.length){
			throw new Exception("Unkown Filter renderer with Style=" + i);
		}
		return renderers[i];
	}
	
	private int type = 0;
	
	private MapRenderer(int type){
		this.type = type;
	}
	public Element getElement() {
		Element e = DocumentHelper.createElement("mapRenderer");
		e.addAttribute("rendererStyle", ""  + getRendererStyle());
		return e;
	}

	public int getRendererStyle() {
		return type;
	}

}
