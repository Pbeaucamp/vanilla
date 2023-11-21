package bpm.gateway.core.transformations.inputs;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.gateway.core.AbstractTransformation;
import bpm.gateway.core.DataStream;
import bpm.gateway.core.Server;
import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.core.manager.ResourceManager;
import bpm.gateway.core.server.database.DataBaseHelper;
import bpm.gateway.core.server.database.DataBaseServer;
import bpm.gateway.core.server.userdefined.Variable;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.transformations.inputs.RunDataBaseInput;
import bpm.vanilla.platform.core.IRepositoryContext;

public class DataBaseInputStream extends AbstractTransformation implements DataStream {

	/*
	 * owner
	 */
	private DataBaseServer server;

	private String definitionSql = "";
	private StreamDescriptor descriptor;

	// those field are use to avoid refreshing
	// the descriptor
	transient private String lastLoaderSql;
	transient private Server lastLoadedServer;

	public DataBaseInputStream() {
		addPropertyChangeListener(this);
	}

	/**
	 * @param server
	 *            the server to set
	 */
	public void setServer(Server server) {

		this.server = (DataBaseServer) server;

	}

	public final void setServer(String serverName) {
		if (owner != null && owner.getResourceManager() != null && owner.getResourceManager().getServer(serverName) != null) {
			server = (DataBaseServer) owner.getResourceManager().getServer(serverName);
		}
		else {
			server = (DataBaseServer) ResourceManager.getInstance().getServer(serverName);
		}
	}

	public Server getServer() {
		return server;
	}

	public String getDefinition() {
//		return definitionSql.replace("\n", " ").replace("\t", " ");
		return definitionSql;
	}

	/**
	 * define the SQL query
	 * 
	 * @param definition
	 */
	public void setDefinition(String definition) {
		this.definitionSql = definition;
		try {
			refreshDescriptor();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public StreamDescriptor getDescriptor(Transformation transfo) throws ServerException {
		return descriptor;
	}

	@Override
	public boolean addInput(Transformation stream) {
		return false;
	}

	@Override
	public RuntimeStep getExecutioner(IRepositoryContext repositoryCtx, int bufferSize) {
		return new RunDataBaseInput(this, bufferSize);
	}

	public Element getElement() {
		Element e = DocumentHelper.createElement("dataBaseInputStream");
		e.addElement("name").setText(name);
		e.addElement("description").setText(description);

		e.addElement("positionX").setText("" + x);
		e.addElement("positionY").setText("" + y);

		if (getContainer() != null) {
			e.addElement("container-ref").setText(getContainer());
		}

		if (server != null) {
			e.addElement("serverRef").setText(server.getName());
		}

		Element outputVars = e.addElement("outputVariables");
		for (Variable v : getOututVariables().keySet()) {
			Integer el = getOututVariables().get(v);
			if (el != null && descriptor != null && el < descriptor.getColumnCount()) {
				Element g = outputVars.addElement("variableInit");
				g.addElement("variableName").setText(v.getName());
				g.addElement("fieldIndex").setText(el.intValue() + "");

			}
		}

		if (definitionSql != null) {
			e.addElement("definition").addCDATA(definitionSql);
		}

		e.addElement("temporaryFileName").setText(getTemporaryFilename());
		e.addElement("temporarySeparator").setText(getTemporarySpliterChar() + "");
		return e;
	}

	@Override
	public void refreshDescriptor() {
		if (!isInited()) {
			try {
				descriptor = DataBaseHelper.getDescriptor(this);
				lastLoaderSql = definitionSql;
				lastLoadedServer = server;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return;
		}

		if (definitionSql != lastLoaderSql || server != lastLoadedServer) {
			try {
				descriptor = DataBaseHelper.getDescriptor(this);
				lastLoaderSql = definitionSql;
				lastLoadedServer = server;
			} catch (Exception e) {

				e.printStackTrace();
			}
		}

		for (Transformation t : getOutputs()) {
			t.refreshDescriptor();
		}

	}

	public void setName(String name) {
		if (name.equals(getName())) {
			return;
		}
		super.setName(name);

		if (definitionSql == null || definitionSql.equals("")) {
			return;
		}

		else {
			for (Transformation t : outputs) {
				t.refreshDescriptor();
			}
		}

	}

	public Transformation copy() {
		DataBaseInputStream copy = new DataBaseInputStream();
		copy.setServer(server);
		copy.setDefinition(getDefinition());
		copy.setDescription(description);
		copy.setName("copy of " + name);
		copy.setTemporaryFilename(getTemporaryFilename());
		copy.setTemporarySpliterChar(getTemporarySpliterChar());

		return copy;
	}

	public String getAutoDocumentationDetails() {
		StringBuffer buf = new StringBuffer();
		buf.append("SqlServer : \n" + server.getName() + "\n");
		buf.append("Sql : \n" + definitionSql + "\n\n");
		return buf.toString();
	}

}
