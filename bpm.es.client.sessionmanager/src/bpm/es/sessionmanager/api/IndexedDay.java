package bpm.es.sessionmanager.api;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import bpm.vanilla.platform.core.beans.VanillaLogs;

public class IndexedDay implements IIndexedLog {

	private Map<String, List<VanillaLogs>> indexedLogs;
	
	private SimpleDateFormat dayFormat = new SimpleDateFormat("EEE, d MMM yyyy"); //$NON-NLS-1$
	
	public IndexedDay() {
		indexedLogs = new TreeMap<String, List<VanillaLogs>>();
	}
	
	public void indexSessionLog(VanillaLogs log) {
		//log.
		String key = null;

		Date date = log.getDate();
		
		key = date == null ? "unknown" : dayFormat.format(date); //$NON-NLS-1$
		
		if (indexedLogs.containsKey(key)) {
			indexedLogs.get(key).add(log);
		}
		else {
			List<VanillaLogs> logs = new ArrayList<VanillaLogs>();
			logs.add(log);
			indexedLogs.put(key, logs);
		}
	}
	
	public List<String> getIndexKeys() {
		List<String> list = new ArrayList<String>(indexedLogs.keySet());
		Collections.sort(list, new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				try {
					return dayFormat.parse(o1).compareTo(dayFormat.parse(o2));
				} catch (ParseException e) {
					e.printStackTrace();
				}
				return 0;
			}
		});		
		return list;
	}
	
	public List<VanillaLogs> getSessionLogForKey(String key) {
		return indexedLogs.get(key);
	}
}
