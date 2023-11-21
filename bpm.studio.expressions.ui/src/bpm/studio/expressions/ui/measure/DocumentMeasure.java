package bpm.studio.expressions.ui.measure;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.DocumentEvent;

import bpm.studio.expressions.core.measures.IOperand;
import bpm.studio.expressions.core.measures.impl.Dimension;
import bpm.studio.expressions.core.measures.impl.DimensionLevel;
import bpm.studio.expressions.core.measures.impl.Measure;
import bpm.studio.expressions.core.measures.impl.MeasureParser;
import bpm.studio.expressions.core.model.IDimensionProvider;
import bpm.studio.expressions.core.model.IField;
import bpm.studio.expressions.core.model.IFieldProvider;
import bpm.studio.expressions.core.model.StructureDimension;



public class DocumentMeasure extends Document{
	private List<Measure> mes = new ArrayList<Measure>();
	private List<DimensionLevel> lvls = new ArrayList<DimensionLevel>();
	private List<Dimension> dims = new ArrayList<Dimension>();
	
	private String error;
	private String result;
	
	private MeasureParser parser;
	
	public DocumentMeasure(MeasureParser parser, IDimensionProvider dimensionStructureProvider, IFieldProvider fieldMeasureProvider, String script) {
		super(script);
		for(IField t : fieldMeasureProvider.getFields()){
			mes.add(new Measure(t));
		}
		
		
		for(StructureDimension r : dimensionStructureProvider.getDimensions()){
			dims.add(new Dimension(r));
			for(int i = 0; i < r.getLevels().size(); i++){
				lvls.add(new DimensionLevel(r, i));
			}
		}
		
		this.parser = parser;
	}
	
	public DocumentMeasure(MeasureParser parser, IDimensionProvider dimensionStructureProvider, IFieldProvider fieldMeasureProvider){
		this.parser = parser;
		for(IField t : fieldMeasureProvider.getFields()){
			mes.add(new Measure(t));
		}
		
		
		for(StructureDimension r : dimensionStructureProvider.getDimensions()){
			dims.add(new Dimension(r));
			for(int i = 0; i < r.getLevels().size(); i++){
				lvls.add(new DimensionLevel(r, i));
			}
		}
	}
	
	
	


	public String getResult(){
		return result;
	}
	
	@Override
	protected void fireDocumentAboutToBeChanged(DocumentEvent event) {
		super.fireDocumentAboutToBeChanged(event);
		
		
	}
	
	@Override
	protected void fireDocumentChanged(DocumentEvent event) {
		
		
		
		try{
			
			String s = get();
			IOperand op = parser.readChunk(IOUtils.toInputStream(s, "UTF-8"));

			op.validate();
			result =  op.toString() + "";
			error = null;
			
		}catch(Exception ex){
			ex.printStackTrace();
			error = ex.getMessage();
			result = null;
		}
		super.fireDocumentChanged(event);
	}

	public String getError() {
		return error;
	}
	
	
}
