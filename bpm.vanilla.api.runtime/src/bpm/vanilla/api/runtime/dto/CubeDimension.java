package bpm.vanilla.api.runtime.dto;

import java.util.ArrayList;
import java.util.List;

import bpm.fa.api.olap.Dimension;
import bpm.fa.api.olap.Hierarchy;

public class CubeDimension extends CubeTreeComponent {
	public CubeDimension(Dimension dim) {
		super(dim.getUniqueName(), dim.getName(), "Dimension", dim);
	}

	@Override
	public List<CubeTreeComponent> loadChildren(Object dimObj) {

		if (dimObj instanceof Dimension) {
			Dimension dim = (Dimension) dimObj;
			List<CubeTreeComponent> hieraArray = new ArrayList<>();

			for (Hierarchy hiera : dim.getHierarchies()) {
				hieraArray.add(new CubeHierarchy(dim.getUniqueName(), hiera));
			}

			return hieraArray;
		}

		return null;
	}

}
