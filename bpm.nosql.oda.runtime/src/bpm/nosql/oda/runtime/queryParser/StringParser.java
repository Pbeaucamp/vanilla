package bpm.nosql.oda.runtime.queryParser;

import java.util.ArrayList;
import java.util.List;

public class StringParser {

	private String tableName = "";
	private List<String> columns = new ArrayList();

	public String getTableName() {
		return this.tableName;
	}

	public List<String> getColumns() {
		return this.columns;
	}

	public void parse(String s) {
		try {
			int start = s.indexOf("(");
			int end = s.lastIndexOf(")");

			if((start == -1) || (end == -1)) {
				throw new Exception();
			}

			this.tableName = s.substring(0, start).trim();

			String[] columnNameArray = s.substring(start + 1, end).split(",");
			for(String columnName : columnNameArray)
				this.columns.add(columnName.trim());

		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
