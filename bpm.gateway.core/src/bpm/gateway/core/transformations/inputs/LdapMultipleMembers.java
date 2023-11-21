package bpm.gateway.core.transformations.inputs;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.gateway.core.AbstractTransformation;
import bpm.gateway.core.DataStream;
import bpm.gateway.core.DefaultStreamDescriptor;
import bpm.gateway.core.Server;
import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.StreamElement;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.core.manager.ResourceManager;
import bpm.gateway.core.server.ldap.LdapServer;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.transformations.inputs.RunLdapMultipleMembers;
import bpm.vanilla.platform.core.IRepositoryContext;

public class LdapMultipleMembers extends AbstractTransformation  implements DataStream{
	private LdapServer server;
	private String node;
	private String attributeName;
	
	private DefaultStreamDescriptor descriptor = new DefaultStreamDescriptor();
	
	
	
	/* (non-Javadoc)
	 * @see bpm.gateway.core.AbstractTransformation#addInput(bpm.gateway.core.Transformation)
	 */
	@Override
	public boolean addInput(Transformation stream) throws Exception {
		if (getInputs().size() > 0 && !getInputs().contains(stream)){
			throw new Exception("The Selection Transformations can only have one Input");
		}
		return super.addInput(stream); 
	}

	@Override
	public StreamDescriptor getDescriptor(Transformation transfo) throws ServerException {
		return descriptor;
	}

	/**
	 * @return the attributeName
	 */
	public String getAttributeName() {
		return attributeName;
	}

	/**
	 * @param attributeName the attributeName to set
	 */
	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
		refreshDescriptor();
	}

	@Override
	public Element getElement() {
		Element e = DocumentHelper.createElement("ldapMultipleMembers");
		e.addElement("name").setText(name);
		e.addElement("description").setText(description);
		
		
		e.addElement("positionX").setText("" + x);
		e.addElement("positionY").setText("" + y);
		
				
		
		if (server != null){
			e.addElement("serverRef").setText(server.getName());
		}
		
		if (getDefinition() != null){
			e.addElement("definition").setText(getDefinition());
		}
		if (getAttributeName() != null){
			e.addElement("attributeName").setText(getAttributeName());
		}
		
		e.addElement("temporaryFileName").setText(getTemporaryFilename());
		e.addElement("temporarySeparator").setText(getTemporarySpliterChar() + "");
		return e;
	}

	@Override
	public RuntimeStep getExecutioner(IRepositoryContext repositoryCtx, int bufferSize) {
		return new RunLdapMultipleMembers(this, bufferSize);
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
		descriptor.addColumn(getName(), "", attributeName, java.sql.Types.VARCHAR, String.class.getName(), "", false, "String", "", false);
		
		for(Transformation t : getOutputs()){
			t.refreshDescriptor();
		}
	}

	public String getDefinition() {
		return node;
	}

	public Server getServer() {
		return server;
	}

	public void setDefinition(String definition) {
		this.node = definition;
		
	}

	public void setServer(String serverName) {
		server = (LdapServer)ResourceManager.getInstance().getServer(serverName);
	}

	public void setServer(Server s) {
		if (s instanceof LdapServer){
			this.server = (LdapServer)s;
		}
		
	}

	public Transformation copy() {
		
		return null;
	}
	
	public String getAutoDocumentationDetails() {
		StringBuffer buf = new StringBuffer();
		buf.append("Ldap Server Name : " + getServer().getName() + "\n");
		buf.append("Ldap Node : " + getDefinition() + "\n");
		buf.append("Node's Extracted Attribute : " + attributeName + "\n");
		
		
		return buf.toString();
	}

}
