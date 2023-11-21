package bpm.fwr.api.beans.template;

import java.io.Serializable;


public interface ITemplate extends Serializable {
	
	public static final String BACKGROUND_COLOR = "backgroundColor";
	public static final String TEXT_COLOR = "textColor";
	public static final String FONT_TYPE = "fontType";
	public static final String FONT_SIZE = "fontSize";
	
	public static final String DATA_STYLE = "dataStyle";
	public static final String HEADER_STYLE = "headerStyle";
	public static final String TITLE_STYLE = "titleStyle";
	public static final String SUBTITLE_STYLE = "subtitleStyle";
	
	public Style getTitleStyle();
	
	public Style getSubTitleStyle();
	
	public Style getHeaderStyle();
	
	public Style getDataStyle();
	
	public String getOddRowsBackgroundColor();

}
