package bpm.fd.api.core.model.components.definition.datagrid;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.fd.api.core.model.components.definition.IComponentRenderer;

public class DataGridRenderer implements IComponentRenderer{

	public static final int RENDERER_HORIZONTAL = 0;
	public static final int RENDERER_VERTICAL = 1;
	
	public static final String[] RENDERER_NAMES = new String[]{
		"Horizontal", "Vertical"
	};
	
	private static final DataGridRenderer[] renderers = new DataGridRenderer[]{
		new DataGridRenderer(RENDERER_HORIZONTAL), new DataGridRenderer(RENDERER_VERTICAL)
	};
	
	public static DataGridRenderer getRenderer(int i) {
		
		if (i < renderers.length){
			return renderers[i];	
		}
		return null;
	}
	
	private int style = RENDERER_HORIZONTAL;
	
	private DataGridRenderer(int style){
		this.style = style;
	}
	public Element getElement() {
		Element e = DocumentHelper.createElement("renderer");
		
		e.addAttribute("rendererStyle", ""  + getRendererStyle());
		return e;
	}

	public int getRendererStyle() {
		return this.style;
	}

}
