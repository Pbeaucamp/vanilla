package metadata.client.wizards.measure;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.DocumentEvent;

import bpm.metadata.MetaData;
import bpm.metadata.layer.logical.IDataSource;
import bpm.metadata.layer.logical.IDataStream;
import bpm.metadata.layer.logical.IDataStreamElement;
import bpm.metadata.resource.IResource;
import bpm.metadata.resource.complex.FmdtDimension;
import bpm.metadata.resource.complex.measures.IOperand;
import bpm.metadata.resource.complex.measures.impl.Dimension;
import bpm.metadata.resource.complex.measures.impl.DimensionLevel;
import bpm.metadata.resource.complex.measures.impl.Measure;
import bpm.metadata.resource.complex.measures.impl.MeasureParser;

public class DocumentMeasure extends Document{
	private List<Measure> mes = new ArrayList<Measure>();
	private List<DimensionLevel> lvls = new ArrayList<DimensionLevel>();
	private List<Dimension> dims = new ArrayList<Dimension>();
	
	private String error;
	private String result;
	
	public DocumentMeasure(MetaData model, IDataSource da, String script) {
		super(script);
		for(IDataStream t : da.getDataStreams()){
			for(IDataStreamElement e : t.getElements()){
				if (e.getType().getParentType() == IDataStreamElement.Type.MEASURE){
					mes.add(new Measure(e));
				}
			}
		}
		
		
		for(IResource r : model.getResources()){
			if (r instanceof FmdtDimension){
				if (((FmdtDimension)r).getDataSource() == da){
					dims.add(new Dimension((FmdtDimension)r));
					for(int i = 0; i < ((FmdtDimension)r).getLevels().size(); i++){
						lvls.add(new DimensionLevel((FmdtDimension)r, i));
					}
					
				}
			}
		}
	}
	
	public DocumentMeasure(MetaData model, IDataSource da){
		
		for(IDataStream t : da.getDataStreams()){
			for(IDataStreamElement e : t.getElements()){
				if (e.getType().getParentType() == IDataStreamElement.Type.MEASURE){
					mes.add(new Measure(e));
				}
			}
		}
		
		
		for(IResource r : model.getResources()){
			if (r instanceof FmdtDimension){
				if (((FmdtDimension)r).getDataSource() == da){
					dims.add(new Dimension((FmdtDimension)r));
					for(int i = 0; i < ((FmdtDimension)r).getLevels().size(); i++){
						lvls.add(new DimensionLevel((FmdtDimension)r, i));
					}
					
				}
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
			MeasureParser p = new MeasureParser(mes, lvls, dims);
			String s = get();
			IOperand op = p.readChunk(IOUtils.toInputStream(s));

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
