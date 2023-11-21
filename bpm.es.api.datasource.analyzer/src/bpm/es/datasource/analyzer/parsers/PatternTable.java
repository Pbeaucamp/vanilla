package bpm.es.datasource.analyzer.parsers;

import java.util.List;

import bpm.vanilla.platform.core.repository.DataSource;

public class PatternTable extends PatternDataSource {

	private List<String> tableName;

	public PatternTable(DataSource dataSource, List<String> tableName) throws Exception {
		super(dataSource);
		this.tableName = tableName;
	}

	public List<String> getTableName() {
		return tableName;
	}

}
