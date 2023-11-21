package org.fasd.inport.mondrian.beans;

import java.util.ArrayList;
import java.util.List;

public class SchemaGrantBean {
	private String access;
	private List<CubeGrantBean> cubes = new ArrayList<CubeGrantBean>();
	public String getAccess() {
		return access;
	}
	public void setAccess(String access) {
		this.access = access;
	}
	public List<CubeGrantBean> getCubes() {
		return cubes;
	}
	public void setCubes(List<CubeGrantBean> cubes) {
		this.cubes = cubes;
	}
	public void addCubeGrant(CubeGrantBean cubeGrant){
		cubes.add(cubeGrant);
	}
}
