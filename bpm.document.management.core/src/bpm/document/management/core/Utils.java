package bpm.document.management.core;

import java.text.Normalizer;
import java.util.regex.Pattern;

public class Utils {
	public static String deAccent(String str) {
		str = str.replace("'","");
	    String nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD); 
	    Pattern pattern = Pattern.compile("[^a-zA-Z0-9_-]");
	    return pattern.matcher(nfdNormalizedString).replaceAll("");
	}	
}