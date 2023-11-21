package bpm.gateway.core.transformations.mdm;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.gateway.core.AbstractTransformation;
import bpm.gateway.core.DefaultStreamDescriptor;
import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.transformation.mdm.MdmInputRuntime;
import bpm.vanilla.platform.core.IRepositoryContext;

public class MdmInput extends AbstractTransformation implements IMdm{
	private String entityName;
	private String entityUuid;
	/**
	 * @return the entityUuid
	 */
	public String getEntityUuid() {
		return entityUuid;
	}
	/**
	 * @param entityUuid the entityUuid to set
	 */
	public void setEntityUuid(String entityUuid) {
		this.entityUuid = entityUuid;
		try {
			refreshDescriptor();
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}

	private DefaultStreamDescriptor descriptor;
	

	@Override
	public boolean addInput(Transformation stream) {
		return false;
	}
	/**
	 * @return the entityName
	 */
	public String getEntityName() {
		return entityName;
	}

	/**
	 * @param entityName the entityName to set
	 */
	public void setEntityName(String entityName) {
		this.entityName = entityName;
		
	}

	@Override
	public StreamDescriptor getDescriptor(Transformation transfo) throws ServerException {
		return descriptor;
	}

	@Override
	public Element getElement() {
		Element e = DocumentHelper.createElement("mdmInput");
		e.addElement("name").setText(name);
		e.addElement("description").setText(description);
		
		e.addElement("positionX").setText("" + x);
		e.addElement("positionY").setText("" + y);

		e.addElement("temporaryFileName").setText(getTemporaryFilename());
		e.addElement("temporarySeparator").setText(getTemporarySpliterChar() + "");

		if (getEntityName() != null){
			e.addElement("entityName").setText(getEntityName());
		}
		if (getEntityUuid() != null){
			e.addElement("entityUuid").setText(getEntityUuid());
		}
		

		return e;
	}

	@Override
	public RuntimeStep getExecutioner(IRepositoryContext repositoryCtx,
			int bufferSize) {
		return new MdmInputRuntime(repositoryCtx, this, bufferSize);
	}

	@Override
	public void refreshDescriptor() {
		if (!isInited()){
			return;
		}
		try {
			descriptor = (DefaultStreamDescriptor)getDocument().getMdmHelper().getDescriptor(this);
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
		for(Transformation  t : getOutputs()){
			t.refreshDescriptor();
		}
		
	}

	@Override
	public Transformation copy() {
		
		return null;
	}

	@Override
	public String getAutoDocumentationDetails() {
		StringBuffer buf = new StringBuffer();
		buf.append("entityName : " + getEntityName() + "\n");
	
		//TODO : write maping datas
		return buf.toString();
	}

}
