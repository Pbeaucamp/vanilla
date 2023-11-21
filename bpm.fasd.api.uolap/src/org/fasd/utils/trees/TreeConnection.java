package org.fasd.utils.trees;

import org.fasd.datasource.DataSourceConnection;

public class TreeConnection extends TreeParent {
	private DataSourceConnection driver;
	
	public TreeConnection(DataSourceConnection driver) {
		super(driver.getName());
		this.driver = driver;
	}
	public TreeConnection(String name) {
		super(name);
	}
	
	public DataSourceConnection getDriver() {
		return driver;
	}
	
	
	@Override
	public String getName() {
		if (driver.getParent() != null){
			if (driver.getParent().getDriver() == driver){
				return driver.getName() + " (Default)";
			}
			else{
				return driver.getName();
			}
		}
		return super.getName();
	}
}
