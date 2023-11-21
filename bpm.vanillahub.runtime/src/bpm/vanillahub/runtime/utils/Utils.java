package bpm.vanillahub.runtime.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import bpm.workflow.commons.beans.ActivityLog;

public class Utils {

	public static String clearName(String name) {
		name = name.replace("'", "");
		String nfdNormalizedString = Normalizer.normalize(name, Normalizer.Form.NFD);
		Pattern pattern = Pattern.compile("[^a-zA-Z0-9_-]");
		return pattern.matcher(nfdNormalizedString).replaceAll("");
	}
	
	public static List<ActivityLog> getLogsAsList(HashMap<Integer, ActivityLog> result) {
		List<ActivityLog> logs = new ArrayList<ActivityLog>();
		if (result != null) {
			for(ActivityLog log : result.values()) {
				logs.add(log);
			}
		}
		return logs;
	}
	
	public static ByteArrayInputStream getResourceAsStream(InputStream inputStream) throws MalformedURLException, IOException {
		byte[] buff = new byte[8000];
		int bytesRead = 0;

		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		while ((bytesRead = inputStream.read(buff)) != -1) {
			bao.write(buff, 0, bytesRead);
		}

		byte[] data = bao.toByteArray();

		return new ByteArrayInputStream(data);
	}
}
