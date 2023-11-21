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
import bpm.gateway.core.server.database.nosql.IColumnFamilyDefinition;
import bpm.gateway.core.server.database.nosql.IFieldsDefinition;
import bpm.gateway.core.server.database.nosql.hbase.HBaseHelper;
import bpm.gateway.core.server.database.nosql.hbase.HBaseServer;
import bpm.gateway.core.server.userdefined.Variable;
import bpm.gateway.core.server.userdefined.Variable.SQLTYPE;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.transformations.inputs.RunHBaseInput;
import bpm.vanilla.platform.core.IRepositoryContext;

public class HbaseInputStream extends AbstractTransformation implements IFieldsDefinition, IColumnFamilyDefinition {
	
	public static final String KEY_DEFINITION = "KEY";
	
	private HBaseServer server; 
	private HBaseServer lastLoadedServer;
	
	private String tableName = "";
	private String lastLoadedTableName;
	
	private String columnFamily = "";
	private String lastLoadedColumnFamily;

	private StreamDescriptor descriptor;
	
	private HashMap<String, String> columnTypes = new HashMap<String, String>();
	
	public HbaseInputStream() {
		addPropertyChangeListener(this);
	}
	
	@Override
	public StreamDescriptor getDescriptor(Transformation transfo) throws ServerException {
		return descriptor;
	}

	@Override
	public Element getElement() {
		Element e = DocumentHelper.createElement("hbaseInputStream");
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
		
		if (tableName != null){
			e.addElement("tableName").setText(tableName);
		}
		
		if (columnFamily != null){
			e.addElement("columnFamily").setText(columnFamily);
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
	public RuntimeStep getExecutioner(IRepositoryContext repositoryCtx, int bufferSize) {
		return new RunHBaseInput(this, bufferSize);
	}

	@Override
	public void refreshDescriptor() {
		if (!isInited()){
			descriptor = new DefaultStreamDescriptor();
			return;
		}
		
		if (tableName != lastLoadedTableName || server != lastLoadedServer || columnFamily != lastLoadedColumnFamily){
			try {
				descriptor = HBaseHelper.buildDescriptor(tableName, columnFamily, this);
				lastLoadedTableName = tableName;
				lastLoadedServer = server;
				lastLoadedColumnFamily = columnFamily;
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
					else {
						SQLTYPE type = SQLTYPE.VARCHAR;
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

	@Override
	public Transformation copy() {
		HbaseInputStream copy = new HbaseInputStream();
		copy.setTableName(tableName);
		return copy;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	public String getTableName() {
		return tableName;
	}

	@Override
	public String getAutoDocumentationDetails() {
		StringBuffer buf = new StringBuffer();
		buf.append("Table Name : \n" + tableName + "\n\n");
		return buf.toString();
	}

	@Override
	public String getDefinition() {
		return tableName;
	}

	@Override
	public Server getServer() {
		return server;
	}

	@Override
	public void setDefinition(String definition) {
		this.tableName = definition;
	}

	@Override
	public void setServer(Server s) {
		this.server = (HBaseServer)s;
	}
	
	public void setServer(String serverName){
		server = (HBaseServer)ResourceManager.getInstance().getServer(serverName);
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

	@Override
	public String getColumnFamily() {
		return columnFamily;
	}

	@Override
	public void setColumnFamily(String columnFamily) {
		this.columnFamily = columnFamily;
		try {
			refreshDescriptor();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

}
