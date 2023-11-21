package bpm.vanilla.api.runtime.dto;

import java.util.ArrayList;
import java.util.List;

import bpm.fa.api.olap.Measure;
import bpm.fa.api.olap.MeasureGroup;

public class CubeMeasureGroup extends CubeTreeComponent {

	public CubeMeasureGroup(MeasureGroup measg) {
		super(measg.getUniqueName(), measg.getName(), "MeasureGroup", measg);
	}

	public List<CubeTreeComponent> loadChildren(Object measgObj) {
		if (measgObj instanceof MeasureGroup) {
			MeasureGroup measg = (MeasureGroup) measgObj;
			List<CubeTreeComponent> measArray = new ArrayList<>();

			for (Measure meas : measg.getMeasures()) {
				measArray.add(new CubeMeasure(meas));
			}

			return measArray;
		}

		return null;
	}
}
