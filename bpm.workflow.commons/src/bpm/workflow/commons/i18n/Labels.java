package bpm.workflow.commons.i18n;

import java.util.Locale;
import java.util.ResourceBundle;

public class Labels {
	public static String getLabel(Locale currentLocale, String key) {
		if (currentLocale == null) {
			currentLocale = new Locale("en");
		}

		ResourceBundle res = ResourceBundle.getBundle("bpm.workflow.commons.i18n.Labels", currentLocale);
		return res.getString(key);
	}
	
	public static final String DatePatternNotDefine = "DatePatternNotDefine";
	public static final String ScriptNotValid = "ScriptNotValid";
	public static final String ProcessNotFoundInRunning = "ProcessNotFoundInRunning";
	
}
