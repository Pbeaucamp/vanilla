package bpm.gateway.core.transformations.inputs;

import java.util.LinkedHashMap;

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
import bpm.gateway.runtime2.transformations.inputs.RunLdapInput;
import bpm.vanilla.platform.core.IRepositoryContext;

public class LdapInput extends AbstractTransformation implements DataStream{

	private LdapServer server;
	private String definition;
	private DefaultStreamDescriptor descriptor = new DefaultStreamDescriptor();
	
	
	private LinkedHashMap<String, String> attributeNameMapping = new LinkedHashMap<String, String>();
	@Override
	public StreamDescriptor getDescriptor(Transformation transfo) throws ServerException {
		return descriptor;
	}

	@Override
	public Element getElement() {
		Element e = DocumentHelper.createElement("ldapInput");
		e.addElement("name").setText(name);
		e.addElement("description").setText(description);
		
		
		e.addElement("positionX").setText("" + x);
		e.addElement("positionY").setText("" + y);
		
				
		
		if (server != null){
			e.addElement("serverRef").setText(server.getName());
		}
		
		if (definition != null){
			e.addElement("definition").setText(definition);
		}
		
		Element a = e.addElement("attributeMappings");
		
		for(String s : attributeNameMapping.keySet()){
			Element m = a.addElement("attributeMapping");
			m.addElement("fieldName").setText(s);
			m.addElement("attributeName").setText(attributeNameMapping.get(s));
		}
		
		e.addElement("temporaryFileName").setText(getTemporaryFilename());
		e.addElement("temporarySeparator").setText(getTemporarySpliterChar() + "");
		return e;
	}

//	@Override
//	public TransformationRuntime getExecutioner(RuntimeEngine runtimeEngine) {
//		return new LdapInputRuntime(this, runtimeEngine);
//	}
	
	@Override
	public RuntimeStep getExecutioner(IRepositoryContext repositoryCtx, int bufferSize) {
		return new RunLdapInput(this, bufferSize);
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
		
		for(String s : attributeNameMapping.keySet()){
			descriptor.addColumn(getName(), "", s, java.sql.Types.VARCHAR, String.class.getName(), "", true, "", "", false);
		}
		
		for(Transformation t : getOutputs()){
			t.refreshDescriptor();
		}
	}

	public String getDefinition() {
		return definition;
	}

	public Server getServer() {
		return server;
	}

	public void setDefinition(String definition) {
		this.definition = definition;
		
		
	}
	public LinkedHashMap<String, String> getAttributeMapping(){
		return attributeNameMapping;
	}
	
	public void setAttributeMapping(LinkedHashMap<String, String> attributeNameMapping){
		this.attributeNameMapping = attributeNameMapping;
		refreshDescriptor();
		
	}

	public void setServer(Server s) {
		if (s instanceof LdapServer){
			this.server = (LdapServer)s;
		}
		
	}
	
	public void setServer(String serverName) {
		server = (LdapServer)ResourceManager.getInstance().getServer(serverName);
	}

	public Transformation copy() {
		LdapInput copy = new LdapInput();
		copy.setName("Copy of " + getName());
		copy.setServer(getServer());
		copy.setDefinition(getDefinition());
		copy.setAttributeMapping(new LinkedHashMap<String, String>(attributeNameMapping));
		return copy;
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
	
	public String getAutoDocumentationDetails() {
		StringBuffer buf = new StringBuffer();
		buf.append("Ldap Server Name : " + getServer().getName() + "\n");
		buf.append("Ldap Node : " + getDefinition() + "\n");
		
		buf.append("Attributes Mappings:\n");;
		for(String s : attributeNameMapping.keySet()){
			buf.append("\t- " + s + " mappped with " + attributeNameMapping.get(s) + "\n");
		}
		return buf.toString();
	}

}
