package bpm.gateway.core.transformations;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.gateway.core.AbstractTransformation;
import bpm.gateway.core.DefaultStreamDescriptor;
import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.StreamElement;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.core.transformations.utils.Aggregate;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.transformation.mapping.RunCartesian;
import bpm.vanilla.platform.core.IRepositoryContext;

public class CartesianProduct extends AbstractTransformation{

	private DefaultStreamDescriptor descriptor = new DefaultStreamDescriptor();
	
	@Override
	public StreamDescriptor getDescriptor(Transformation transfo) throws ServerException {
		return descriptor;
	}

	@Override
	public Element getElement() {
		Element e = DocumentHelper.createElement("cartesianProduct");
		e.addElement("name").setText(name);
		e.addElement("description").setText(description);
		
		
		e.addElement("positionX").setText("" + x);
		e.addElement("positionY").setText("" + y);
		e.addElement("temporaryFileName").setText(getTemporaryFilename());
		e.addElement("temporarySeparator").setText(getTemporarySpliterChar() + "");

		return e;
	}

//	@Override
//	public TransformationRuntime getExecutioner(RuntimeEngine runtimeEngine) {
//		return new CartesianProductRuntime(this, runtimeEngine);
//	}

	@Override
	public RuntimeStep getExecutioner(IRepositoryContext repositoryCtx, int bufferSize) {
		return new RunCartesian(this, bufferSize);
	}
	
	@Override
	public void refreshDescriptor() {
		descriptor = new DefaultStreamDescriptor();
		
		if (!isInited()){
			return;
		}
		
		for(Transformation t : getInputs()){
			try{
				for(StreamElement e : t.getDescriptor(this).getStreamElements()){
					
					StreamElement nE = e.clone(getName(), t.getName());
					descriptor.addColumn(nE);
					
				}
			}catch(Exception e ){
				e.printStackTrace();
			}
		}
		
		for(Transformation t : outputs){
			t.refreshDescriptor();
		}
		
	}

	public Transformation copy() {
		CartesianProduct copy = new CartesianProduct();
		
		
		copy.setDescription(description);
		copy.setName("copy of " + name);
		

		return copy;
	}
	
	
	@Override
	public boolean addInput(Transformation t)throws Exception{
		if (inputs.size() >= 2 && !inputs.contains(t)){
			throw new Exception("Cannot have more than 2 Stream input");
		}
		
		boolean b =  super.addInput(t);
		if (b){
			refreshDescriptor();
		}
		return b;
	}
	
	@Override
	public void removeInput(Transformation t){
		super.removeInput(t);
		refreshDescriptor();
	}

	public String getAutoDocumentationDetails() {
		StringBuffer buf = new StringBuffer();
		return buf.toString();
	}
}
