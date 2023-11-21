package bpm.fasd.expressions.api.model;

import org.fasd.olap.OLAPDimension;
import org.fasd.olap.OLAPLevel;

import bpm.studio.expressions.core.model.IField;
import bpm.studio.expressions.core.model.StructureDimension;

public class FasdDimension extends StructureDimension {

	
	public FasdDimension(OLAPDimension fasdDim){
		build(fasdDim);
	}
	
	@Override
	public void build(Object model) {
		if (!(model instanceof OLAPDimension)){
			return;
		}
		
		OLAPDimension dimension = (OLAPDimension)model;
		setName(dimension.getName());
		for(OLAPLevel l : dimension.getHierarchies().get(0).getLevels()){
			IField fasdColumnField = new FasdColumnField(l.getItem());
			this.addLevel(fasdColumnField);
		}
		
		
	}

}
