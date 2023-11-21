package org.fasd.utils.trees;

import org.fasd.datasource.DataSource;

public class TreeDatabase extends TreeParent {
	private DataSource driver;
	
	public TreeDatabase(DataSource tab) {
		super(tab.getDSName());
		driver = tab;
	}
	public TreeDatabase(String name) {
		super(name);
	}
	
	public DataSource getDriver() {
		return driver;
	}
}
