package bpm.fwr.api.beans.Constants;

import java.io.Serializable;

public final class FontTypes implements Serializable {
	public static final String ARIAL = "Arial";
	public static final String TIMES = "Times New Roman";
	public static final String COURIER ="Courier New";
	public static final String COMIC = "Comic Sans MS";
	public static final String GEORGIA = "Georgia";
	public static final String VERDANA = "Verdana";
	
	public static final String[] getFontTypes() {
		return new String[]{ARIAL,TIMES, COURIER, COMIC, GEORGIA, VERDANA};
	}
	


}
