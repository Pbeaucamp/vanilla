package bpm.fd.api.core.model.components.definition.filter;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.fd.api.core.model.components.definition.IComponentRenderer;

public class TypeRenderer implements IComponentRenderer{

	public static final int String = 0;
	public static final int Number = 1;
	
	public static final String[] RENDERER_NAMES = new String[]{
		"String", "Number"
	};
	
	private static final TypeRenderer[] renderers = new TypeRenderer[]{
		new TypeRenderer(String),
		new TypeRenderer(Number)
	};
	
	public int type = 0;
	
	private TypeRenderer(int type){
		this.type = type;
	}
	
	
	
	public static TypeRenderer getRenderer(int i) throws Exception{
		if (i >= renderers.length){
			throw new Exception("Unkown Type renderer with Style=" + i);
		}
		return renderers[i];
	}
	
	@Override
	public Element getElement() {
		
		Element e = DocumentHelper.createElement("typeRenderer");
		e.addAttribute("rendererStyle", ""  + getRendererStyle());
		return e;
	}

	@Override
	public int getRendererStyle() {
		
		return type;
	}

}
