package bpm.fd.api.core.model.components.definition.filter.validator;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class Validator {
	public static final int DATE = 0;
	public static final int OTHER = 1;
	private String format;
	
	private int type;
	
	public Validator(int type, String format){
		this.format = format;
		this.type = type;
	}
	/**
	 * @return the format
	 */
	public String getFormat() {
		return format;
	}
	/**
	 * @param format the format to set
	 */
	public void setFormat(String format) {
		this.format = format;
	}

	
	public Element getElement() {
		Element e = DocumentHelper.createElement("validator");
		e.addElement("format", format);
		return e;
	}
	
	public String getRegEx(){
		if (type == DATE){
						
			return "\\d{4}(\\-|\\/|\\.)\\d{1,2}(\\-|\\/|\\.)\\d{1,2}";
			
		}
		return ".*";
	}
}
