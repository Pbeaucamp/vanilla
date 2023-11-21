package bpm.vanilla.api.runtime.dto;

import java.util.List;

import bpm.fa.api.olap.Hierarchy;
import bpm.fa.api.olap.Level;

import java.util.ArrayList;

public class CubeHierarchy extends CubeTreeComponent {
	private List<CubeLevel> cubeLvl;
	private String dimID;

	public CubeHierarchy(String _dimID, Hierarchy hiera) {
		super(hiera.getUniqueName(), hiera.getName(), "Hierarchy", null);

		dimID = _dimID;

		children = loadChildren(hiera);

		cubeLvl = loadLevels(hiera);
	}

	public List<CubeLevel> getCubeLvl() {
		return cubeLvl;
	}

	public String getDimID() {
		return dimID;
	}

	@Override
	public List<CubeTreeComponent> loadChildren(Object hieraObj) {
		if (hieraObj instanceof Hierarchy) {
			Hierarchy hiera = (Hierarchy) hieraObj;
			List<CubeTreeComponent> memArray = new ArrayList<>();

			memArray.add(new CubeOLAPMember(dimID, hiera.getDefaultMember(), true));

			return memArray;
		}

		return null;
	}

	public List<CubeLevel> loadLevels(Hierarchy hiera) {
		List<CubeLevel> cubelevels = new ArrayList<>();

		for (Level lvl : hiera.getLevel()) {
			cubelevels.add(new CubeLevel(lvl));
		}

		return cubelevels;
	}

}
