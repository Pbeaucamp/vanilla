package bpm.gateway.core.transformations;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.gateway.core.AbstractTransformation;
import bpm.gateway.core.DataStream;
import bpm.gateway.core.DefaultStreamDescriptor;
import bpm.gateway.core.Server;
import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.core.manager.ResourceManager;
import bpm.gateway.core.server.database.DataBaseServer;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.transformations.database.RunSqlScript;
import bpm.vanilla.platform.core.IRepositoryContext;

public class SqlScript extends AbstractTransformation implements DataStream{

	private DefaultStreamDescriptor descriptor = new DefaultStreamDescriptor();
	private String scriptSql;
	private DataBaseServer server;
	
	
	@Override
	public StreamDescriptor getDescriptor(Transformation transfo) throws ServerException {
		return descriptor;
	}

	@Override
	public Element getElement() {
		Element e = DocumentHelper.createElement("sqlScript");
		e.addElement("name").setText(name);
		e.addElement("description").setText(description);
		
		
		e.addElement("positionX").setText("" + x);
		e.addElement("positionY").setText("" + y);
		
		if (getContainer() != null){
			e.addElement("container-ref").setText(getContainer());
		}
		
		
		if (server != null){
			e.addElement("serverRef").setText(server.getName());
		}
		
		if (scriptSql != null){
			e.addElement("definition").setText(scriptSql);
		}
		
		e.addElement("temporaryFileName").setText(getTemporaryFilename());
		e.addElement("temporarySeparator").setText(getTemporarySpliterChar() + "");
		return e;
	}
	@Override
	public RuntimeStep getExecutioner(IRepositoryContext repositoryCtx, int bufferSize) {
		return new RunSqlScript(this, bufferSize);
	}

	@Override
	public void refreshDescriptor() {
		if (!isInited()){
			return;
		}
	}

	public Transformation copy() {
		SqlScript copy = new SqlScript();
		copy.setServer(server);
		copy.setDefinition(getDefinition());
		copy.setDescription(description);
		copy.setName("copy of " + name);
		

		return copy;
	}

	public String getDefinition() {
		return scriptSql;
	}

	public Server getServer() {
		return server;
	}

	public void setDefinition(String definition) {
		this.scriptSql = definition;
		
	}

	public void setServer(Server s) {
		if (s instanceof DataBaseServer){
			server = (DataBaseServer)s;
		}
		
	}
	
	public final void setServer(String serverName){
		server = (DataBaseServer)ResourceManager.getInstance().getServer(serverName);
	}

	public String getAutoDocumentationDetails() {
		StringBuffer buf = new StringBuffer();
		buf.append("Executed Sql Sript : \n");
		buf.append(scriptSql);
		return buf.toString();
	}
}
