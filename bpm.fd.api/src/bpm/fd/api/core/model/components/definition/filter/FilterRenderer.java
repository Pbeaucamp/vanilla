package bpm.fd.api.core.model.components.definition.filter;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.fd.api.core.model.components.definition.IComponentRenderer;

public class FilterRenderer implements IComponentRenderer{

	public static final int DROP_DOWN_LIST_BOX = 0;
	public static final int SLIDER = 1;
	public static final int CHECKBOX = 2;
	public static final int RADIOBUTTON = 3;
	public static final int LIST = 4;
	public static final int TEXT_FIELD = 5;
	public static final int DATE_PIKER = 6;
	public static final int MENU = 7;
	public static final int DYNAMIC_TEXT = 8;
	
	public static final String[] RENDERER_NAMES = new String[]{
		"Drop Down List Box", "Slider", "CheckBox", "RadioButton", "List", "TextField", "DatePicker", "Menu", "DynamicTextField"
	};
	
	private static final FilterRenderer[] renderers = new FilterRenderer[]{
		new FilterRenderer(DROP_DOWN_LIST_BOX),
		new FilterRenderer(SLIDER),
		new FilterRenderer(CHECKBOX),
		new FilterRenderer(RADIOBUTTON),
		new FilterRenderer(LIST),
		new FilterRenderer(TEXT_FIELD),
		new FilterRenderer(DATE_PIKER),
		new FilterRenderer(MENU),
		new FilterRenderer(DYNAMIC_TEXT)
	};
	
	
	public int type = 0;
	
	private FilterRenderer(int type){
		this.type = type;
	}

	public int getRendererStyle() {
		return type;
	}
	
	public Element getElement(){
		Element e = DocumentHelper.createElement("filterRenderer");
		e.addAttribute("rendererStyle", ""  + getRendererStyle());
		return e;
	}

	public static FilterRenderer getRenderer(int i) throws Exception{
		if (i >= renderers.length){
			throw new Exception("Unkown Filter renderer with Style=" + i);
		}
		return renderers[i];
	}
	
}
