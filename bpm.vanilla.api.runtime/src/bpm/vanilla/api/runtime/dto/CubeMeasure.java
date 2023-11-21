package bpm.vanilla.api.runtime.dto;

import java.util.List;

import bpm.fa.api.olap.Measure;
import bpm.fa.api.olap.MeasureGroup;
import bpm.fa.api.olap.OLAPCube;

public class CubeMeasure extends CubeTreeComponent {
	public CubeMeasure(Measure meas) {
		super(meas.getUniqueName(), meas.getName(), "Measure", null);
	}

	public static CubeMeasure findCubeMeasure(OLAPCube cube, String measUname) {

		for (MeasureGroup measg : cube.getMeasures()) {
			for (Measure meas : measg.getMeasures()) {
				if (meas.getUniqueName().equals(measUname)) {
					return new CubeMeasure(meas);
				}
			}
		}

		return null;
	}

	public List<CubeTreeComponent> loadChildren(Object measObj) {
		return null;
	}
}
