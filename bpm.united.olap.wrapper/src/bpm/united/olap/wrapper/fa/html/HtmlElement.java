package bpm.united.olap.wrapper.fa.html;

import java.util.HashMap;
import java.util.List;

public interface HtmlElement {

	String getHtml();
	
	void addStyleName(String style);

	String getStyleName();

	void setHTML(String string);

	String getHTML();
	
	void setProperty(String name, String value);
	
	String getGridHtml();
	
	List<String> getStyles();
	
	HashMap<String, String> getProperties();
}
