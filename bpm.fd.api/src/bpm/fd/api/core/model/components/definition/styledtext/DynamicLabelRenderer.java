package bpm.fd.api.core.model.components.definition.styledtext;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.fd.api.core.model.components.definition.IComponentRenderer;

public class DynamicLabelRenderer implements IComponentRenderer{

	
	private int rendererType = 0;
	
	@Override
	public Element getElement() {
		Element e = DocumentHelper.createElement("commentRenderer");
		e.addAttribute("rendererStyle", ""  + getRendererStyle());
		return e;
	}

	@Override
	public int getRendererStyle() {
		return rendererType;
	}

}
