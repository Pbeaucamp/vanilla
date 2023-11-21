package bpm.gwt.aklabox.commons.client.utils;

import com.google.gwt.core.client.GWT;

public class PathHelper {
	
	private static boolean hostedMode = !GWT.isScript() && GWT.isClient();
	
	public static String getRightPath(String path) {
		if(path == null) {
			path = "";
		}
		path = path.replace("\\", "/");

		String res = "";
		if (hostedMode) {
			res = path;
		}
		else {
			res = path.replace("webapps/", "../");
		}

		if (res.endsWith(".doc") || res.endsWith(".docx")) {
			res += ".pdf";
		}

		return res;
	}
}
