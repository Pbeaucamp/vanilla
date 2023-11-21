package bpm.fwr.client.utils;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;

public class Space extends SimplePanel {
	private HTML s;
	
	
	public Space(String width,String height){
		super();
		s = new HTML();
		s.setHTML("&nbsp;");
		this.add(s);
		this.setWidth(width);
		this.setHeight(height);
	}
	

}
