package bpm.vanilla.api.runtime.dto;

import java.util.ArrayList;
import java.util.List;

import bpm.fa.api.olap.Dimension;
import bpm.fa.api.olap.OLAPCube;

public class CubeDimensionTree extends CubeTreeComponent {
	public CubeDimensionTree(OLAPCube cube) {
		super(cube.getMdx().getCubeName() + ".Dimensions", "Dimensions", "Tree", cube);
	}

	@Override
	public List<CubeTreeComponent> loadChildren(Object cubeObj) {

		if (cubeObj instanceof OLAPCube) {
			OLAPCube cube = (OLAPCube) cubeObj;
			List<CubeTreeComponent> dimArray = new ArrayList<>();

			for (Dimension dim : cube.getDimensions()) {
				dimArray.add(new CubeDimension(dim));
			}

			return dimArray;
		}

		return null;
	}
}
