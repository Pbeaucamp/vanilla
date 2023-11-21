package bpm.vanilla.platform.core.beans.data;

import java.util.HashMap;
import java.util.Map;


public enum DatasourceType {
	JDBC("JDBC"), FMDT("Vanilla MetaData"), R("R"), CSV("File CSV/XLS"), CSVVanilla("File CSV/XLS Vanilla"), HBase("Connection Hbase"), 
	SOCIAL("Social Network"), KPI("KPI"), ARCHITECT("Vanilla Architect");
	
	private String type;

	private static Map<String, DatasourceType> map = new HashMap<String, DatasourceType>();
	static {
		for (DatasourceType actionType : DatasourceType.values()) {
			map.put(actionType.getType(), actionType);
		}
	}

	private DatasourceType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

}
