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
import bpm.gateway.runtime2.transformation.spliter.RunRowSpliter;
import bpm.vanilla.platform.core.IRepositoryContext;


/**
 * This class split a field value in multiple Rows
 * 
 *  A | B | 1,2,3,4
 *  
 *  will provide
 *  A | B | 1,2,3,4 | 1
 *  A | B | 1,2,3,4 | 2
 *  A | B | 1,2,3,4 | 3
 *  A | B | 1,2,3,4 | 4
 *  
 * @author LCA
 *
 */
public class RowsFieldSplitter extends AbstractTransformation {

	
	 
	private Integer inputFieldIndexToSplit;
	private String splitSequence = ";";
	private boolean trim = true;
	
	private boolean keepOriginalFieldInOutput = false;
	
	private DefaultStreamDescriptor descriptor = new DefaultStreamDescriptor();
	
	
	@Override
	public StreamDescriptor getDescriptor(Transformation transfo) throws ServerException {
		return descriptor;
	}

	@Override
	public Element getElement() {
		Element e = DocumentHelper.createElement("rowsFieldSpliter");
		e.addElement("name").setText(name);
		e.addElement("description").setText(description);
		
		
		e.addElement("positionX").setText("" + x);
		e.addElement("positionY").setText("" + y);

		e.addElement("temporaryFileName").setText(getTemporaryFilename());
		e.addElement("temporarySeparator").setText(getTemporarySpliterChar() + "");
		
		
		if (inputFieldIndexToSplit != null){
			e.addElement("inputFieldIndexToSplit").setText(inputFieldIndexToSplit + "");
		}
		
		e.addElement("splitSequence").setText(splitSequence);
		
		e.addElement("keepOrignalFieldInOutput").setText(keepOriginalFieldInOutput + "");
		e.addElement("trim").setText(trim + "");
		
		
		if (descriptor != null){
			e.add(descriptor.getElement());
		}
		return e;
	}

//	@Override
//	public TransformationRuntime getExecutioner(RuntimeEngine runtimeEngine) {
//		return new RowsFieldSpliterRuntime(this, runtimeEngine);
//	}

	
	@Override
	public RuntimeStep getExecutioner(IRepositoryContext repositoryCtx, int bufferSize) {
		return new RunRowSpliter(this, bufferSize);
	}

	

	
	
	
	
	/**
	 * @return the inputFieldIndexToSplit
	 */
	public final Integer getInputFieldIndexToSplit() {
		return inputFieldIndexToSplit;
	}

	/**
	 * @param inputFieldIndexToSplit the inputFieldIndexToSplit to set
	 */
	public final void setInputFieldIndexToSplit(Integer inputFieldIndexToSplit) {
		this.inputFieldIndexToSplit = inputFieldIndexToSplit;
		refreshDescriptor();
	}
	
	/**
	 * @param inputFieldIndexToSplit the inputFieldIndexToSplit to set
	 */
	public final void setInputFieldIndexToSplit(String inputFieldIndexToSplit) {
		this.inputFieldIndexToSplit = Integer.parseInt(inputFieldIndexToSplit);
	}

	/**
	 * @return the splitSequence
	 */
	public final String getSplitSequence() {
		return splitSequence;
	}

	/**
	 * @param splitSequence the splitSequence to set
	 */
	public final void setSplitSequence(String splitSequence) {
		this.splitSequence = splitSequence;
	}

	/**
	 * @return return true of the generated values will be trimed
	 */
	public final boolean isTrim() {
		return trim;
	}

	/**
	 * @param trim : indicates if the values will be trimed or not
	 */
	public final void setTrim(boolean trim) {
		this.trim = trim;
	}
	
	/**
	 * @param trim : indicates if the values will be trimed or not
	 */
	public final void setTrim(String trim) {
		this.trim = Boolean.parseBoolean(trim);
	}

	/**
	 * @return the keepOriginalFieldInOuput : true if the original splitted field will be outputed
	 */
	public final boolean isKeepOriginalFieldInOuput() {
		return keepOriginalFieldInOutput;
	}

	/**
	 * @param keepOriginalFieldInOuput : set if the original splitted field will be outputed
	 */
	public final void setKeepOriginalFieldInOuput(boolean keepOriginalFieldInOuput) {
		this.keepOriginalFieldInOutput = keepOriginalFieldInOuput;
		refreshDescriptor();
		
	}
	
	/**
	 * @param keepOriginalFieldInOuput : set if the original splitted field will be outputed
	 */
	public final void setKeepOriginalFieldInOuput(String keepOriginalFieldInOuput) {
		this.keepOriginalFieldInOutput = Boolean.parseBoolean(keepOriginalFieldInOuput);
	}

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
	
	
	

	@Override
	public void refreshDescriptor() {
	
		String actualSplit = null;
		
		try{
			descriptor = new DefaultStreamDescriptor();
			if (!isInited()){
				return;
			}
			for(Transformation t : getInputs()){
				for (StreamElement e : t.getDescriptor(this).getStreamElements()){
					
					if (!keepOriginalFieldInOutput  && inputFieldIndexToSplit != null && t.getDescriptor(this).getStreamElements().indexOf(e) == inputFieldIndexToSplit){
						actualSplit = e.clone(getName(), t.getName()).name;
					}
					else if(inputFieldIndexToSplit != null && t.getDescriptor(this).getStreamElements().indexOf(e) == inputFieldIndexToSplit){
						actualSplit = e.clone(getName(), t.getName()).name;
						descriptor.addColumn(e.clone(getName(), t.getName()));
					}
					else{
						descriptor.addColumn(e.clone(getName(), t.getName()));
					}
					
					
				}
			}
			StreamElement se = new StreamElement();
			se.className = "java.lang.String";
			se.transfoName = this.getName();
			se.originTransfo = this.getName();
			if(actualSplit != null) {
				se.name = "splitedField_" + actualSplit;
			}
			else {
				se.name = "splitedField";
			}
			descriptor.addColumn(se);
			
			for(Transformation  t : getOutputs()){
				t.refreshDescriptor();
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		

	}


	
		
	
	public Transformation copy() {
		RowsFieldSplitter copy = new RowsFieldSplitter();
		
		
		copy.setDescription(description);
		copy.setName("copy of " + name);
		

		return copy;
	}

	/* (non-Javadoc)
	 * @see bpm.gateway.core.AbstractTransformation#removeOutput(bpm.gateway.core.Transformation)
	 */
	@Override
	public void removeOutput(Transformation transfo) {
		super.removeOutput(transfo);
		inputFieldIndexToSplit = null;
	}

	public String getAutoDocumentationDetails() {
		StringBuffer buf = new StringBuffer();
		try{
			buf.append("Split Field : " + getInputs().get(0).getDescriptor(this).getColumnName(inputFieldIndexToSplit)+ "\n");
		}catch(Exception ex){
			buf.append("Split Field : " + inputFieldIndexToSplit+ "\n");
		}
		buf.append("Split On : "+ getSplitSequence() + "\n");
		buf.append("Trim Splited Values : "+ trim + "\n");
		buf.append("Keep Base Field Value in Outputs : "+ keepOriginalFieldInOutput + "\n");
		return buf.toString();
	}
}

