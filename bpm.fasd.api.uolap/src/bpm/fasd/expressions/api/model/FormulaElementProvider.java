package bpm.fasd.expressions.api.model;

import java.util.ArrayList;
import java.util.List;

import org.fasd.datasource.DataObjectItem;
import org.fasd.olap.FAModel;
import org.fasd.olap.OLAPCube;
import org.fasd.olap.OLAPDimension;

import bpm.studio.expressions.core.model.IDimensionProvider;
import bpm.studio.expressions.core.model.IField;
import bpm.studio.expressions.core.model.IFieldProvider;
import bpm.studio.expressions.core.model.StructureDimension;

public class FormulaElementProvider implements IFieldProvider, IDimensionProvider{

	
	private FAModel faModel;
	
	public FormulaElementProvider(FAModel faModel){
		this.faModel = faModel;
	}
	
	@Override
	public List<IField> getFields() {
		List<IField> fields = new ArrayList<IField>();
		
		
		for(OLAPCube cube : faModel.getOLAPSchema().getCubes()){
			for(DataObjectItem col : cube.getFactDataObject().getColumns()){
				fields.add(new FasdColumnField(col));
			}
		}
		
		return fields;
	}

	@Override
	public List<StructureDimension> getDimensions() {
		
		List<StructureDimension> dims = new ArrayList<StructureDimension>();
		
		for(OLAPDimension d : faModel.getOLAPSchema().getDimensions()){
			StructureDimension sd = new FasdDimension(d);
			dims.add(sd);
		}
		return dims;
	}

}
