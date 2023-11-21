package bpm.fd.api.core.model.components.definition.chart;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class DatasLimit {
	public static final int LIMIT_NONE = 0;
	public static final int LIMIT_TOP = 1;
	public static final int LIMIT_BOTTOM = 2;
	
	public static final String[] LIMIT_TYPES =new String[]{
		"None", "Top N", "Bottom N"
	};
	
	private int size = 5;
	private int type = LIMIT_NONE;
	
	public DatasLimit(){}
	
	public int getType(){
		return type;
	}
	
	
	public void setType(int type){
		if (type >= LIMIT_NONE && type <= LIMIT_BOTTOM){
			this.type = type;
		}
		
	}
	
	public void setSize(int size){
		this.size = size;
	}
	
	public int getSize(){
		return size;
	}

	public Element getElement() {
		Element e = DocumentHelper.createElement("datasLimit");
		e.addAttribute("size", getSize() + "").addAttribute("type", getType() + "");
		return e;
	}
}
