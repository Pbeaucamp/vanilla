package org.fasd.olap;

public abstract class Drill {

	protected String drillName;

	public void setDrillName(String drillName) {
		this.drillName = drillName;
	}

	public String getDrillName() {
		return drillName;
	}

	public abstract String getXml();
}
