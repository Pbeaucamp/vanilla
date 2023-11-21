package bpm.gateway.core.transformations.vanilla;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.gateway.core.AbstractTransformation;
import bpm.gateway.core.DefaultStreamDescriptor;
import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class FreedashFormInput extends AbstractTransformation {

	private Integer directoryItemId;
	private RepositoryItem item;
	
	private DefaultStreamDescriptor desc = new DefaultStreamDescriptor();
	@Override
	public StreamDescriptor getDescriptor(Transformation transfo) throws ServerException {
		return desc;
	}

	@Override
	public Element getElement() {
		Element e = DocumentHelper.createElement("freedashFormInput");

		e.addElement("name").setText(name);
		e.addElement("description").setText(description);
		
		
		e.addElement("positionX").setText("" + x);
		e.addElement("positionY").setText("" + y);
		
		e.addElement("temporaryFileName").setText(getTemporaryFilename());
		e.addElement("temporarySeparator").setText(getTemporarySpliterChar() + "");
		
		
		if (getDirectoryItemId() != null){
			e.addElement("directoryItemId").setText(getDirectoryItemId() + "");
		}
		
		
		return e;
	}

	@Override
	public RuntimeStep getExecutioner(IRepositoryContext repositoryCtx, int bufferSize) {
		
		return null;
	}

	@Override
	public void refreshDescriptor() {
		if (!isInited()){
			return;
		}
		
		try{
			getDocument().getFreeDashHelper().createDescriptor(this);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		for(Transformation  t : getOutputs()){
			t.refreshDescriptor();
		}
		
	}


	
	public void setDirectoryItemId(Integer id) {
		this.directoryItemId = id;
		
	}
	
	public void setDirectoryItemId(String id) {
		this.directoryItemId = Integer.parseInt(id);
		refreshDescriptor();
		
		
	}
	
	public Integer getDirectoryItemId() {
		return directoryItemId;
	}

	public Transformation copy() {
		
		return null;
	}
	
	protected void setDescriptor(DefaultStreamDescriptor desc){
		
		this.desc = desc;
	}

	public RepositoryItem getDirectoryItem(){
		return item;
	}
	
	public void setDirectoryItem(RepositoryItem item){
		this.item = item;
	}
	
	public String getAutoDocumentationDetails() {
		StringBuffer buf = new StringBuffer();
		
		
		//XXX toDo
		return buf.toString();
	}

}
