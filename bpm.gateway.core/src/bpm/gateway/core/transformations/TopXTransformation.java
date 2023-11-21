package bpm.gateway.core.transformations;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.gateway.core.AbstractTransformation;
import bpm.gateway.core.DefaultStreamDescriptor;
import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.StreamElement;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.transformation.selection.RunTopX;
import bpm.vanilla.platform.core.IRepositoryContext;

public class TopXTransformation extends AbstractTransformation{
	public static final String ASC = "asc";
	public static final String DSC = "dsc";
	public static final String NONE = "none";
	
	private DefaultStreamDescriptor descriptor = new DefaultStreamDescriptor();

	private Integer fieldIndex;
	private int X = 10;
	
	private String sorting = NONE;
	
	/* (non-Javadoc)
	 * @see bpm.gateway.core.AbstractTransformation#addInput(bpm.gateway.core.Transformation)
	 */
	@Override
	public boolean addInput(Transformation stream) throws Exception {
		if (getInputs().size() > 0 && !getInputs().contains(stream)){
			throw new Exception("The Filter Transformations can only have one Input");
		}
		boolean b =  super.addInput(stream);
		
		if (b){
			refreshDescriptor();
		}
		return b;
	}

	/**
	 * @return the ascendingSort
	 */
	public String getSorting() {
		return sorting;
	}

	/**
	 * @param ascendingSort the ascendingSort to set
	 */
	public void setSorting(String ascendingSort) {
		this.sorting = ascendingSort;
	}
	
	

	@Override
	public StreamDescriptor getDescriptor(Transformation transfo) throws ServerException {
		return descriptor;
	}

	@Override
	public Element getElement() {
		Element e = DocumentHelper.createElement("topX");
		e.addElement("name").setText(name);
		e.addElement("description").setText(description);
		
		
		e.addElement("positionX").setText("" + x);
		e.addElement("positionY").setText("" + y);
		e.addElement("temporaryFileName").setText(getTemporaryFilename());
		e.addElement("temporarySeparator").setText(getTemporarySpliterChar() + "");
		
		if (getField() != null){
			e.addElement("fieldIndex").setText("" + getField());
		}
		e.addElement("topX").setText("" + getX());
		e.addElement("sorting").setText(getSorting());
		
		if (descriptor != null){
			e.add(descriptor.getElement());
		}	
		return e;
	}

	@Override
	public RuntimeStep getExecutioner(IRepositoryContext repositoryCtx, int bufferSize) {
		return new RunTopX(this, bufferSize);
	}

	@Override
	public void refreshDescriptor() {
		descriptor = new DefaultStreamDescriptor();
		if (!isInited()){
			return;
		}
		
		if (!getInputs().isEmpty()){
			try {
				for(StreamElement s : getInputs().get(0).getDescriptor(this).getStreamElements()){
					descriptor.addColumn(s.clone(getName(), getInputs().get(0).getName()));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		
		for(Transformation t : outputs){
			t.refreshDescriptor();
		}
		
	}
	public void setField(String fieldValue){
		try{
			fieldIndex = Integer.parseInt(fieldValue);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	public void setField(StreamElement e){
		int i = descriptor.getStreamElements().indexOf(e);
		if (i == -1){
			fieldIndex = null;
		}
		else{
			fieldIndex = i;
		}
	}
	
	public Integer getField(){
		return fieldIndex;
	}
	
	

	/**
	 * @return the x
	 */
	public int getX() {
		return X;
	}

	/**
	 * @param x the x to set
	 */
	public void setX(int x) {
		X = x;
	}
	
	public void setX(String xValue) {
		try{
			X = Integer.parseInt(xValue);
		}catch(Exception ex){
			
		}
	}

	public Transformation copy() {
		
		return null;
	}
	
	public String getAutoDocumentationDetails() {
		StringBuffer buf = new StringBuffer();
		try{
			buf.append("On Field " + getInputs().get(0).getDescriptor(this).getColumnName(fieldIndex) + "\n");
		}catch(Exception ex){
			buf.append("On Field number " + fieldIndex + "\n");
		}
		
		buf.append("Number of rows : " + x + "\n");
		buf.append("Type : " + sorting + "\n");
		
		
		return buf.toString();
	}
}
