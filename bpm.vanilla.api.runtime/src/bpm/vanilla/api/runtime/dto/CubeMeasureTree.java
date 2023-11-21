package bpm.vanilla.api.runtime.dto;

import java.util.ArrayList;
import java.util.List;

import bpm.fa.api.olap.MeasureGroup;
import bpm.fa.api.olap.OLAPCube;

public class CubeMeasureTree extends CubeTreeComponent {

	public CubeMeasureTree(OLAPCube cube) {
		super(cube.getMdx().getCubeName() + ".Measures", "Measures", "Tree", cube);
	}

	@Override
	public List<CubeTreeComponent> loadChildren(Object cubeObj) {

		if (cubeObj instanceof OLAPCube) {
			OLAPCube cube = (OLAPCube) cubeObj;
			List<CubeTreeComponent> measgArray = new ArrayList<>();

			for (MeasureGroup measg : cube.getMeasures()) {
				measgArray.add(new CubeMeasureGroup(measg));
			}

			return measgArray;

		}
		return null;
	}

}
