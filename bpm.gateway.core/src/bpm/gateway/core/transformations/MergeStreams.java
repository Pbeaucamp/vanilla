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
import bpm.gateway.runtime2.transformation.merging.RunMerge;
import bpm.vanilla.platform.core.IRepositoryContext;

public class MergeStreams extends AbstractTransformation{

	private DefaultStreamDescriptor descriptor = new DefaultStreamDescriptor();
	
	@Override
	public StreamDescriptor getDescriptor(Transformation transfo) throws ServerException {
		return descriptor;
	}

	@Override
	public Element getElement() {
		Element e = DocumentHelper.createElement("mergeStreams");
		e.addElement("name").setText(name);
		e.addElement("description").setText(description);
		
		
		e.addElement("positionX").setText("" + x);
		e.addElement("positionY").setText("" + y);
		e.addElement("temporaryFileName").setText(getTemporaryFilename());
		e.addElement("temporarySeparator").setText(getTemporarySpliterChar() + "");
		
		if (getContainer() != null){
			e.addElement("container-ref").setText(getContainer());
		}
		
		if (descriptor != null){
			e.add(descriptor.getElement());
		}
		
		return e;
	}

//	@Override
//	public TransformationRuntime getExecutioner(RuntimeEngine runtimeEngine) {
//
//		return new MergeStreamsRuntime(this, runtimeEngine);
//	}

	@Override
	public RuntimeStep getExecutioner(IRepositoryContext repositoryCtx, int bufferSize) {
		return new RunMerge(this, bufferSize);
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

	public Transformation copy() {
		return new MergeStreams();
	}

	@Override
	public boolean addInput(Transformation stream) throws Exception {
		boolean b = false ;
		if (inputs.isEmpty()){
			 b = super.addInput(stream);
			 refreshDescriptor();
			 return b;
		}
		
		try {
			if (inputs.get(0).getDescriptor(this).getStreamElements().size() == stream.getDescriptor(this).getStreamElements().size()){
				b = super.addInput(stream);
				return b;
			}
		} catch(Exception e) {
			inputs.get(0).refreshDescriptor();
			if (inputs.get(0).getDescriptor(this).getStreamElements().size() == stream.getDescriptor(this).getStreamElements().size()){
				b = super.addInput(stream);
				return b;
			}
		}
		
		if (b && inputs.size() == 1){
			
			return b;
		}
		
		
		throw new Exception("Cannot merge Streams with different structures");
		
	}

	public String getAutoDocumentationDetails() {
		StringBuffer buf = new StringBuffer();
				
		return buf.toString();
	}
}
