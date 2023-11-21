package bpm.fwr.api.beans.Constants;

import java.io.Serializable;

public class FontSizes implements Serializable {
	
	public static final String T8 = "8";
	public static final String T10 = "10";
	public static final String T14 = "14";
	
	public static final String[] getFontTypes() {
		return new String[]{T8, T10, T14};
	}

}
