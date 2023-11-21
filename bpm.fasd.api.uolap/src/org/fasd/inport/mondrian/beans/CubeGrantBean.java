package org.fasd.inport.mondrian.beans;

import java.util.ArrayList;
import java.util.List;

public class CubeGrantBean {
	private String cube = "";
	private String access = "";
	private List<HierarchyGrantBean> hieras = new ArrayList<HierarchyGrantBean>();
	
	public void setCube(String cube){
		this.cube = cube;
	}
	
	public void addHiera(HierarchyGrantBean h){
		hieras.add(h);
	}

	public String getCube() {
		return cube;
	}

	public List<HierarchyGrantBean> getHieras() {
		return hieras;
	}

	public String getAccess() {
		return access;
	}

	public void setAccess(String access) {
		this.access = access;
	}
	
	
	
	
}
