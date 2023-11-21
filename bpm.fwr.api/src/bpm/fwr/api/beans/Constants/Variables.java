package bpm.fwr.api.beans.Constants;

import java.io.Serializable;
import java.util.Properties;

public class Variables implements Serializable {

	public static String VAR_DATE_VALUE = "%currdate%";
	public static String VAR_GROUP_VALUE = "%usergroup%";
	
	public static String[] VARS_VALUE = {VAR_DATE_VALUE, VAR_GROUP_VALUE};
	
	public static String getHeaderFooterText(String text, Properties properties) {
		
		for (Object oKey : properties.keySet()) {
			text = text.replace(oKey.toString(), "" + properties.get(oKey));
		}
		
		return text;
	}
	
}
