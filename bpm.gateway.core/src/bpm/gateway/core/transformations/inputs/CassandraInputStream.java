package bpm.gateway.core.transformations.inputs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.gateway.core.AbstractTransformation;
import bpm.gateway.core.DefaultStreamDescriptor;
import bpm.gateway.core.Server;
import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.StreamElement;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.core.manager.ResourceManager;
import bpm.gateway.core.server.database.nosql.ICassandraCQL;
import bpm.gateway.core.server.database.nosql.IFieldsDefinition;
import bpm.gateway.core.server.database.nosql.cassandra.CassandraHelper;
import bpm.gateway.core.server.database.nosql.cassandra.CassandraServer;
import bpm.gateway.core.server.userdefined.Variable;
import bpm.gateway.core.server.userdefined.Variable.SQLTYPE;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.transformations.inputs.RunCassandraInput;
import bpm.vanilla.platform.core.IRepositoryContext;

public class CassandraInputStream extends AbstractTransformation implements IFieldsDefinition, ICassandraCQL {

	private CassandraServer server;
	private CassandraServer lastLoadedServer;
	
	private String cqlDefinition = "";
	private String lastLoaderCql = "";
	
	private StreamDescriptor descriptor;
	
	private HashMap<String, String> columnTypes = new HashMap<String, String>();
	
	
	public CassandraInputStream(){
		addPropertyChangeListener(this);
	}

	@Override
	public String getCQLDefinition() {
		return cqlDefinition;
	}

	@Override
	public void setCQLDefinition(String cqlDefinition) {
		this.cqlDefinition = cqlDefinition;
		try {
			refreshDescriptor();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public StreamDescriptor getDescriptor(Transformation transfo) throws ServerException{
		return descriptor;
	}

	@Override
	public boolean addInput(Transformation stream) {
		return false;
	}

	@Override
	public RuntimeStep getExecutioner(IRepositoryContext repositoryCtx, int bufferSize) {
		return new RunCassandraInput(this, bufferSize);
	}

	public Element getElement() {
		Element e = DocumentHelper.createElement("cassandraInputStream");
		e.addElement("name").setText(name);
		e.addElement("description").setText(description);
		
		
		e.addElement("positionX").setText("" + x);
		e.addElement("positionY").setText("" + y);
		
		if (getContainer() != null){
			e.addElement("container-ref").setText(getContainer());
		}
		
		Element outputVars = e.addElement("outputVariables");
		for(Variable v : getOututVariables().keySet()){
			Integer el = getOututVariables().get(v);
			if (el != null && descriptor != null && el < descriptor.getColumnCount()){
				Element g = outputVars.addElement("variableInit");
				g.addElement("variableName").setText(v.getName());
				g.addElement("fieldIndex").setText(el.intValue() + "");
				
			}
		}
		
		if(server != null){
			e.addElement("serverRef").setText(server.getName());
		}
		
		if (cqlDefinition != null){
			e.addElement("cqlDefinition").setText(cqlDefinition);
		}
		
		Element g = e.addElement("columnTypes");
		for(Entry<String, String> type : columnTypes.entrySet()){
			Element v = g.addElement("columnType");
			v.addElement("column").setText(type.getKey());
			v.addElement("value").setText(type.getValue());
		}
		
		e.addElement("temporaryFileName").setText(getTemporaryFilename());
		e.addElement("temporarySeparator").setText(getTemporarySpliterChar() + "");
		return e;
	}


	@Override
	public void refreshDescriptor() {
		if (!isInited()){
			descriptor = new DefaultStreamDescriptor();
			return;
		}
		
		if (cqlDefinition != lastLoaderCql || server != lastLoadedServer){
			try {
				descriptor = CassandraHelper.buildDescriptor(this);
				lastLoaderCql = cqlDefinition;
				lastLoadedServer = server;
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if(descriptor.getStreamElements() != null){
				for(StreamElement el : descriptor.getStreamElements()){
					if(columnTypes.get(el.name) != null){
						SQLTYPE type = SQLTYPE.getTypeFromValue(columnTypes.get(el.name));
						el.typeName = type.getTypeName();
						el.className = type.getJavaClassName();
					}
				}
				
				removeNotUsedColumn();
			}
		}

		for(Transformation  t : getOutputs()){
			t.refreshDescriptor();
		}
		
	}

	public  void setName(String name){
		if (name.equals(getName())){
			return;
		}
		super.setName(name);
		
		if (cqlDefinition == null || cqlDefinition.equals("")){
			return;
		}
		
		else{
			for(Transformation t : outputs){
				t.refreshDescriptor();
			}
		}
		
	}

	public Transformation copy() {
		CassandraInputStream copy = new CassandraInputStream();
		copy.setCQLDefinition(getCQLDefinition());
		copy.setDescription(description);
		copy.setName("copy of " + name);
		

		return copy;
	}	
	
	public String getAutoDocumentationDetails() {
		StringBuffer buf = new StringBuffer();
		buf.append("Sql : \n" + cqlDefinition + "\n\n");
		return buf.toString();
	}

	@Override
	public Server getServer() {
		return server;
	}

	@Override
	public void setServer(Server s) {
		this.server = (CassandraServer)s;
	}
	
	public void setServer(String serverName){
		server = (CassandraServer)ResourceManager.getInstance().getServer(serverName);
	}

	@Override
	public String getDefinition() {
		return cqlDefinition;
	}

	@Override
	public void setDefinition(String definition) {
		this.cqlDefinition = definition;
	}
	
	@Override
	public void addColumnType(String key, String value){
		columnTypes.put(key, value);
	}
	
	@Override
	public void removeNotUsedColumn(){
		if(columnTypes != null){
			
			List<String> colToRemove = new ArrayList<String>();
			for(String col : columnTypes.keySet()){
				
				boolean found = false;
				if(descriptor.getStreamElements() != null){
					for(StreamElement el : descriptor.getStreamElements()){
						if(col.equals(el.name)){
							found = true;
							break;
						}
					}
				}
				
				if(!found){
					colToRemove.add(col);
				}
			}
			
			for(String col : colToRemove){
				columnTypes.remove(col);
			}
		}
	}
}
