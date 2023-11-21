package bpm.united.olap.wrapper.fa.html;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HtmlValue implements HtmlElement {

	private String label;
	private int i;
	private int j;
	
	private List<String> styles = new ArrayList<String>();
	private HashMap<String, String> properties = new HashMap<String, String>();
	
	public HtmlValue(String label, int i, int j) {
		this.label = label;
		this.i = i;
		this.j = j;
		styles.add("gridItemValue");
	}

	public String getHtml() {
		return label;
	}

	public void addStyleName(String style) {
		styles.add(style);
	}

	public String getStyleName() {
		String styleName = "";
		for(String st : styles) {
			styleName += st + " ";
		}
		return styleName;
	}

	public String getHTML() {
		return label;
	}

	public void setHTML(String string) {
		label = string;
	}
	public void setProperty(String name, String value) {
		properties.put(name, value);
	}

	public String getGridHtml() {
		StringBuffer buf = new StringBuffer();
		buf.append("<div ");
		if(styles.size() > 0) {
			buf.append("class=\"");
			for(String st : styles) {
				buf.append(st + " ");
			}
			buf.append("\"");
		}
		if(properties.size() > 0) {
			buf.append(" style=\"");
			for(String name : properties.keySet()) {
				buf.append(name + ": " + properties.get(name) + ";");
			}
			buf.append("\"");
		}
		buf.append(">" + label + "</div>");
		return buf.toString();
	}

	public List<String> getStyles() {
		return styles;
	}

	public HashMap<String, String> getProperties() {
		return properties;
	}
}
