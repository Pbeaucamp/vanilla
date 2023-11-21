package bpm.fwr.api.beans.Constants;

import java.io.Serializable;

public class Formats implements Serializable {

	public static String[] NUMBER_FORMATS = new String[]{
		"none"
		,"0"
		,"#.00"
		,"#,000"
		,"#,000.00"
		,"0 �"
		,"#.00 �"
		,"#,000 �"
		,"#,000.00 �"
		,"0.00%"
	};
	
	public static String[] NUMBER_FORMATS_DISPLAY = new String[]{
		"none"
		,"1234"
		,"1234,56"
		,"1 234"
		,"1 234,56"
		,"1234 �"
		,"1234,56 �"
		,"1 234 �"
		,"1 234,56 �"
		,"12,34%"
	};
	
	public static String[] DATE_FORMATS = new String[]{
		"none"
		,"dd/MM/yyyy"
		,"MM/dd/yyyy"
		,"yyyy/MM/dd"
		,"dd MMMM yyyy"
		,"EEEE dd MMMM yyyy"
	};
	
	public static String[] DATE_FORMATS_DISPLAY = new String[]{
		"none"
		,"20/04/2010"
		,"04/20/2010"
		,"2010/04/20"
		,"20 April 2010"
		,"Tuesday 20 April 2010"
	};
	
}
