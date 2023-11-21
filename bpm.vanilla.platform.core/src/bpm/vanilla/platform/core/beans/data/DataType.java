package bpm.vanilla.platform.core.beans.data;

import java.util.ArrayList;
import java.util.List;

public enum DataType {

	INT, DECIMAL, STRING, DATE;

	public static List<String> asList() {
		List<String> list = new ArrayList<String>();
		for(DataType dt : DataType.values()) {
			list.add(dt.name());
		}
		return list;
	}
}
