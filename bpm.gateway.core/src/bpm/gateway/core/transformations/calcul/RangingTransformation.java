package bpm.gateway.core.transformations.calcul;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.gateway.core.AbstractTransformation;
import bpm.gateway.core.DefaultStreamDescriptor;
import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.StreamElement;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.transformation.calculation.RunRanging;
import bpm.vanilla.platform.core.IRepositoryContext;

public class RangingTransformation extends AbstractTransformation{

	private DefaultStreamDescriptor descriptor = new DefaultStreamDescriptor();
	private Integer streamElementTarget = null;
	private List<Range> ranges = new ArrayList<Range>();
	
	private String outputFieldName;
	
	/*
	 * come from Variable TYPES constants
	 */
	private int type;
	
	
	
	
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
		
		for(Transformation t : outputs){
			t.refreshDescriptor();
		}
	}
	
	public void setType(String type) {
		try{
			this.type = Integer.parseInt(type);
		}finally{
			
		}
		
	}

	public String getOutputFieldName() {
		return outputFieldName;
	}

	public void setOutputFieldName(String outputFieldName) {
		if (outputFieldName == null || outputFieldName.equals(this.outputFieldName)){
			return;
		}
		
		this.outputFieldName = outputFieldName;
		
		if (descriptor.getStreamElements().size() > 0){
			StreamElement s = descriptor.getStreamElements().get(descriptor.getStreamElements().size() - 1);
			s.name = outputFieldName;
			
			for(Transformation t : outputs){
				t.refreshDescriptor();
			}
		}
	}

	public List<Range> getRanges(){
		return new ArrayList<Range>(ranges);
	}
	
	public void addRange(Range range){
		ranges.add(range);
	}
	
	public void removeRange(Range range){
		ranges.remove(range);
	}
	
	public void setTarget(StreamElement target){
		int i  = descriptor.getStreamElements().indexOf(target);
		if ( i >= 0){
			streamElementTarget = i;
		}
		
	}
	
	
	public void setTarget(String targetIndice){
		try{
			streamElementTarget = Integer.parseInt(targetIndice);
		}finally{
			
		}
		
	}
	public Integer getTargetIndex(){
		return streamElementTarget;
	}
	
	public StreamElement getTarget(){
		if (streamElementTarget == null){
			return null;
		}
		
		return descriptor.getStreamElements().get(streamElementTarget);
	}
	
	
	@Override
	public StreamDescriptor getDescriptor(Transformation transfo) throws ServerException {
		return descriptor;
	}

	@Override
	public Element getElement() {
		Element e = DocumentHelper.createElement("rangingTransformation");
		e.addElement("name").setText(name);
		e.addElement("description").setText(description);
		
		
		e.addElement("positionX").setText("" + x);
		e.addElement("positionY").setText("" + y);
		
		e.addElement("type").setText(type + "");
		e.addElement("temporaryFileName").setText(getTemporaryFilename());
		e.addElement("temporarySeparator").setText(getTemporarySpliterChar() + "");
		if (getOutputFieldName() != null){
			e.addElement("outputFieldName").setText(getOutputFieldName());
		}
		
		
		if (streamElementTarget != null){
			e.addElement("targetIndex").setText(this.streamElementTarget + "");
		}
		
		
		for(Range s : ranges){
			Element c = e.addElement("range");
			if (s.getFirstValue() != null){
				c.addElement("firstValue").setText(s.getFirstValue());
			}
			if (s.getSecondValue() != null){
				c.addElement("secondValue").setText(s.getSecondValue());
			}
			
			c.addElement("intervalType").setText(s.getIntervalType() + "");
			
			if (s.getOutput() != null){
				c.addElement("output").setText(s.getOutput());
			}
			
			
		}
		
		return e;
	}

//	@Override
//	public TransformationRuntime getExecutioner(RuntimeEngine runtimeEngine) {
//		return new RangingRuntime(this, runtimeEngine);
//	}
	
	@Override
	public RuntimeStep getExecutioner(IRepositoryContext repositoryCtx, int bufferSize) {
		return new RunRanging(this, bufferSize);
	}

	@Override
	public void refreshDescriptor() {
		if (!isInited()){
			return;
		}
		descriptor = new DefaultStreamDescriptor();
		try{
			for(Transformation t : getInputs()){
				for (StreamElement e : t.getDescriptor(this).getStreamElements()){
					descriptor.addColumn(e.clone(getName(), t.getName()));
				}
			}
		}catch(Exception e){
			
		}
		
		StreamElement e = new StreamElement();
		e.name = getOutputFieldName() == null || "".equals(getOutputFieldName()) ? getName() : getOutputFieldName();
		e.typeName = "STRING";
		e.className = "java.lang.String";
		e.transfoName = getName();
		e.originTransfo = getName();
		
		descriptor.addColumn(e);
		
		for(Transformation t : outputs){
			t.refreshDescriptor();
		}
	}

	public Transformation copy() {
		
		return null;
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
	public void removeInput(Transformation transfo) {
		super.removeInput(transfo);
		streamElementTarget = null;
	}
	
	public String getAutoDocumentationDetails() {
		StringBuffer buf = new StringBuffer();
		buf.append("Output Field : " + outputFieldName + "\n");
		try{
			buf.append("Tested Field : " + getInputs().get(0).getDescriptor(this).getColumnName(streamElementTarget) + "\n");
		}catch(Exception ex){
			buf.append("Tested Field : " + streamElementTarget + "\n");
		}

		for(Range sc : ranges){
			switch(sc.getIntervalType()){
			case 0:
				buf.append("]" + sc.getFirstValue() + "," + sc.getSecondValue() + "[ in " + sc.getOutput() + "\n");
				break;
			case 1:
				buf.append("]" + sc.getFirstValue() + "," + sc.getSecondValue() + "] in " + sc.getOutput() + "\n");
				break;
			case 2:
				buf.append("[" + sc.getFirstValue() + "," + sc.getSecondValue() + "] in " + sc.getOutput() + "\n");
				break;
			case 3:
				buf.append("[" + sc.getFirstValue() + "," + sc.getSecondValue() + "[ in " + sc.getOutput() + "\n");
				break;
			case 4:
				buf.append("others in " + sc.getOutput() + "\n");
				break;
			}
			
		}
		
	
		return buf.toString();
	}
}
