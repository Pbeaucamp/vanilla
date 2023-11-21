package bpm.fd.api.core.model.components.definition.gauge;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.fd.api.core.model.components.definition.IComponentRenderer;


public class GaugeRenderer implements IComponentRenderer{
	public static final int FUSION_CHART = 0;
	public static final String[] RENDERER_NAMES = new String[]{
		"FusionChart"
	};
	private static final GaugeRenderer[] renderers = new GaugeRenderer[]{
		new GaugeRenderer()
	};
	
	public static GaugeRenderer getRenderer(int i){
		return renderers[i];
	}
	
	private int style = FUSION_CHART;
	
	public Element getElement() {
		Element e = DocumentHelper.createElement("gaugeRenderer");
		e.addAttribute("rendererStyle", ""  + getRendererStyle());
		return e;
	}

	public int getRendererStyle() {
		return style;
	}

}
