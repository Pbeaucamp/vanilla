package bpm.fd.api.core.model.components.definition.text;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.fd.api.core.model.components.definition.IComponentRenderer;

public enum LabelRenderer implements IComponentRenderer{
	PARAGRAPH(0, "p"), DIV(1, "div"), SPAN(2, "span"),
	H1(3,"h1"),H2(4,"h2"),
	H3(5,"h3"),H4(6,"h4"),H5(7,"h5"),H6(8,"h6"),HR(9, "hr")
	
	
	;

	private static final String L = "<";
	private static final String G = ">";
	private static final String B = " ";
	private static final String EL = "</";
	private int rendererType = 0;
	private String tag = "p";
	
	private LabelRenderer(int rendererType, String tag){
		this.rendererType = rendererType;
		this.tag = tag;
	}
	@Override
	public Element getElement() {
		Element e = DocumentHelper.createElement("labelRenderer");
		e.addAttribute("rendererStyle", ""  + getRendererStyle());
		return e;
	}

	@Override
	public int getRendererStyle() {
		return rendererType;
	}
	
	public String getOpenTag(){
		return L + tag + B;
	}
	public String getCloseTag(){
		return EL + tag + G;
	}
	
}
