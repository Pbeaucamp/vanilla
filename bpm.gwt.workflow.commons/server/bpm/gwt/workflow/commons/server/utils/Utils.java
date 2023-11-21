package bpm.gwt.workflow.commons.server.utils;

import java.text.Normalizer;
import java.util.regex.Pattern;

public class Utils {

	public static String clearName(String name) {
		name = name.replace("'", "");
		String nfdNormalizedString = Normalizer.normalize(name, Normalizer.Form.NFD);
		Pattern pattern = Pattern.compile("[^a-zA-Z0-9_-]");
		return pattern.matcher(nfdNormalizedString).replaceAll("");
	}
}
