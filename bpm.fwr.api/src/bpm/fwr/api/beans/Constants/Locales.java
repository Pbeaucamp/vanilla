package bpm.fwr.api.beans.Constants;

import java.io.Serializable;


public abstract class Locales implements Serializable {
	public final static String DEFAULT = "default";
	public final static String English = "en";
	public final static String Francais = "fr";
	public final static String Spanish = "es";
	public final static String[] locales = {DEFAULT, English,Francais,Spanish};
	
	public static int getLocalesCount() {
		return 3;
	}
	
	public static String getLocale(int index) {
		return locales[index];
	}
	
	public static String[] getLocales() {
		return locales;
	}
}
