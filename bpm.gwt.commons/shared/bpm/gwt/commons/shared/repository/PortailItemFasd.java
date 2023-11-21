package bpm.gwt.commons.shared.repository;

import java.util.ArrayList;
import java.util.List;

import bpm.vanilla.platform.core.repository.RepositoryItem;

public class PortailItemFasd extends PortailRepositoryItem {

	private static final long serialVersionUID = 1L;
	
	private List<PortailItemCube> cubes = new ArrayList<PortailItemCube>();
	
	public PortailItemFasd() { }

	public PortailItemFasd(RepositoryItem item, String typeName) {
		super(item, typeName);
	}

	public List<PortailItemCube> getCubes() {
		return cubes;
	}

	public void addCube(PortailItemCube cube) {
		if(cubes == null) {
			this.cubes = new ArrayList<PortailItemCube>();
		}
		
		cube.setParent(this);
		
		this.cubes.add(cube);
	}
}
