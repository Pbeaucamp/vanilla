package bpm.gateway.core.transformations;

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
import bpm.gateway.core.transformations.utils.Condition;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.transformation.selection.RunSelectDistinct;
import bpm.vanilla.platform.core.IRepositoryContext;

public class SelectDistinct extends AbstractTransformation {

	protected DefaultStreamDescriptor descriptor = new DefaultStreamDescriptor();
	
		
	
	
	@Override
	public StreamDescriptor getDescriptor(Transformation transfo) throws ServerException {
		return descriptor;
	}

	@Override
	public Element getElement() {
		Element e = DocumentHelper.createElement("selectDistinct");
		e.addElement("name").setText(name);
		e.addElement("description").setText(description);
		if (getContainer() != null){
			e.addElement("container-ref").setText(getContainer());
		}
		
		e.addElement("positionX").setText("" + x);
		e.addElement("positionY").setText("" + y);
		e.addElement("temporaryFileName").setText(getTemporaryFilename());
		e.addElement("temporarySeparator").setText(getTemporarySpliterChar() + "");
		
		if (descriptor != null){
			e.add(descriptor.getElement());
		}	
		
		return e;
	}

//	@Override
//	public TransformationRuntime getExecutioner(RuntimeEngine runtimeEngine) {
//
//		return new SelectDistinctRuntime(this, runtimeEngine);
//	}
	
	@Override
	public RuntimeStep getExecutioner(IRepositoryContext repositoryCtx, int bufferSize) {
		return new RunSelectDistinct(this, bufferSize);
	}

	@Override
	public void refreshDescriptor() {
		try{
			descriptor = new DefaultStreamDescriptor();
			
			if (!isInited()){
				return;
			}
			for(Transformation t : getInputs()){
				for(StreamElement e : t.getDescriptor(this).getStreamElements()){
					descriptor.addColumn(e.clone(getName(), t.getName()));
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		for(Transformation  t : getOutputs()){
			t.refreshDescriptor();
		}

	}
	
	
	

	@Override
	public boolean addInput(Transformation stream) throws Exception {
		if (getInputs().size() > 0 && !getInputs().contains(stream)){
			throw new Exception("The Filter Transformations can only have one Input");
		}
		
		boolean b = super.addInput(stream);
		
		if(b){
			refreshDescriptor();
		}
		
		return b;
	}

	

	public Transformation copy() {
		SelectDistinct copy = new SelectDistinct();
		
		
		copy.setDescription(description);
		copy.setName("copy of " + name);
		

		return copy;
	}

	public String getAutoDocumentationDetails() {
		StringBuffer buf = new StringBuffer();
				
		return buf.toString();
	}
}
