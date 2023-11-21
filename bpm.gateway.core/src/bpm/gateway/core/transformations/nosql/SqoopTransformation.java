package bpm.gateway.core.transformations.nosql;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.gateway.core.AbstractTransformation;
import bpm.gateway.core.DefaultStreamDescriptor;
import bpm.gateway.core.ITargetableStream;
import bpm.gateway.core.Server;
import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.StreamElement;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.core.manager.ResourceManager;
import bpm.gateway.core.server.database.DataBaseHelper;
import bpm.gateway.core.server.database.DataBaseServer;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.transformation.nosql.RunSqoop;
import bpm.vanilla.platform.core.IRepositoryContext;

public class SqoopTransformation extends AbstractTransformation implements Sqoop, ITargetableStream {

	private DataBaseServer server;

	private String definitionSql = "";
	private String tableName;
	private StreamDescriptor descriptor;
	
	private String partitionColumn;
	private List<String> selectedColumns;

	private String sqoopUrl;
	private String hdfsDirectory;

	private boolean isImport;

	// those field are use to avoid refreshing
	// the descriptor
	transient private String lastLoaderSql;
	transient private String lastTableName;
	transient private Server lastLoadedServer;

	public SqoopTransformation() {
		addPropertyChangeListener(this);
	}

	@Override
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
	
	@Override
	public Server getServer() {
		return server;
	}
	
	@Override
	public String getDefinition() {
		return definitionSql;
	}

	@Override
	public String getTableName(){
		return tableName;
	}

	@Override
	public void setDefinition(String definition) {
		this.definitionSql = definition;
		if (definition.toLowerCase().contains(" from ")){
			tableName = definition.substring(definition.indexOf(" from ") + 6).trim();
		}
		
		fireProperty(PROPERTY_INPUT_CHANGED);
	}
	
	@Override
	public String getSqoopUrl() {
		return sqoopUrl;
	}
	
	@Override
	public void setSqoopUrl(String sqoopUrl) {
		this.sqoopUrl = sqoopUrl;
	}
	
	@Override
	public void setHdfsDirectory(String hdfsDirectory) {
		this.hdfsDirectory = hdfsDirectory;
	}
	
	@Override
	public String getHdfsDirectory() {
		return hdfsDirectory;
	}
	
	@Override
	public boolean isImport() {
		return isImport;
	}
	
	@Override
	public void setImport(boolean isImport) {
		this.isImport = isImport;
	}
	
	public void setImport(String isImport) {
		this.isImport = Boolean.parseBoolean(isImport);
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
	public void addOutput(Transformation stream) {
		//Output not supported
	}
	
	@Override
	public void addOutput(Transformation stream, List<Point> outputBendPoints) {
		//Output not supported
	}

	@Override
	public RuntimeStep getExecutioner(IRepositoryContext repositoryCtx, int bufferSize) {
		return new RunSqoop(this, bufferSize);
	}

	@Override
	public Element getElement() {
		Element e = DocumentHelper.createElement("sqoop");
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

		if (definitionSql != null) {
			e.addElement("definition").addCDATA(definitionSql);
		}

		if (sqoopUrl != null) {
			e.addElement("sqoopUrl").setText(sqoopUrl);
		}

		if (hdfsDirectory != null) {
			e.addElement("hdfsDirectory").setText(hdfsDirectory);
		}

		if (partitionColumn != null) {
			e.addElement("partitionColumn").setText(partitionColumn);
		}

		if(selectedColumns != null) {
			for(String key : selectedColumns){
				e.addElement("selectedColunm").setText(key);
			}
		}
		
		e.addElement("isImport").setText(String.valueOf(isImport));

		e.addElement("temporaryFileName").setText(getTemporaryFilename());
		e.addElement("temporarySeparator").setText(getTemporarySpliterChar() + "");
		return e;
	}

	@Override
	public void refreshDescriptor() {
		if (!isInited()){
			try {
				descriptor = DataBaseHelper.getDescriptor(this);
				lastLoaderSql = definitionSql;
				lastLoadedServer = server;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return;
		}
		
		if (lastTableName != tableName || server != lastLoadedServer || lastLoaderSql != definitionSql){
			try {
				descriptor = (DefaultStreamDescriptor)DataBaseHelper.getDescriptor(this);
				lastTableName = tableName;
				lastLoaderSql = definitionSql;
				lastLoadedServer = server;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void setName(String name) {
		if (name.equals(getName())) {
			return;
		}
		super.setName(name);

		if (definitionSql == null || definitionSql.equals("")) {
			return;
		}
	}

	@Override
	public Transformation copy() {
		SqoopTransformation copy = new SqoopTransformation();
		copy.setServer(server);
		copy.setDefinition(getDefinition());
		copy.setDescription(description);
		copy.setName("copy of " + name);
		copy.setTemporaryFilename(getTemporaryFilename());
		copy.setTemporarySpliterChar(getTemporarySpliterChar());
		copy.setSqoopUrl(sqoopUrl);
		copy.setHdfsDirectory(hdfsDirectory);
		copy.setImport(isImport);
		copy.setPartitionColumn(partitionColumn);
		copy.setSelectedColumns(selectedColumns);

		return copy;
	}

	@Override
	public String getAutoDocumentationDetails() {
		StringBuffer buf = new StringBuffer();
		buf.append("SqlServer : \n" + server.getName() + "\n");
		buf.append("Sql : \n" + definitionSql + "\n\n");
		buf.append("Sqoop URL : \n" + sqoopUrl + "\n\n");
		buf.append("HDFS Directory : \n" + hdfsDirectory + "\n\n");
		buf.append("Partition Column : \n" + partitionColumn + "\n\n");
		
		if(selectedColumns != null) {
			buf.append("Selected Columns : \n");
			for(String selectedColumn : selectedColumns) {
				buf.append(selectedColumn + "\n");
			}
		}
		
		return buf.toString();
	}

	@Override
	public boolean isTruncate() {
		return false;
	}

	@Override
	public void setTruncate(boolean value) {
		//Not used
	}

	public void setPartitionColumn(String partitionColumn) {
		this.partitionColumn = partitionColumn;
	}
	
	public String getPartitionColumn() {
		return partitionColumn;
	}
	
	public List<String> getSelectedColumns() {
		return selectedColumns;
	}
	
	public void addSelectedColumn(StreamElement field){
		if(selectedColumns == null) {
			this.selectedColumns = new ArrayList<String>();
		}
		
		for(String key : selectedColumns){
			if (key.equals(field.getFullName())){
				return ;
			}
		}
		selectedColumns.add(field.getFullName());
	}
	
	public void addSelectedColumn(String selectedColumn) {
		if(selectedColumns == null) {
			this.selectedColumns = new ArrayList<String>();
		}
		this.selectedColumns.add(selectedColumn);
	}
	
	public void setSelectedColumns(List<String> selectedColumns) {
		this.selectedColumns = selectedColumns;
	}
	
	public void removeFieldKeyIndex(StreamElement field){
		try{
			for(String key : selectedColumns){
				if(field.getFullName().equals(key)){
					selectedColumns.remove(key);
					break;
				}
			}
		}catch(Exception ex){ }
	}
	
	public List<StreamElement> getColumns(){
		List<StreamElement> l = new ArrayList<StreamElement>();
		
		try{
			List<StreamElement> f = getDescriptor(this).getStreamElements();
			for(String key : selectedColumns){
				for(StreamElement el : f){
					if(el.getFullName().equals(key)){
						l.add(el);
						break;
					}
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}

		return l;
	}

	public void clearColumns() {
		if(selectedColumns != null) {
			this.selectedColumns.clear();
		}
	}

	public StreamElement getPartitionColumnStream() {
		try {
			if(partitionColumn != null && getDescriptor(this) != null && getDescriptor(this).getStreamElements() != null) {
				List<StreamElement> f = getDescriptor(this).getStreamElements();
				for(StreamElement el : f){
					if(el.getFullName().equals(partitionColumn)){
						return el;
					}
				}
			}
		} catch (Exception e) { }
		return null;
	}

	public String getColumnsAsString() {
		List<StreamElement> columns = getColumns();
		
		StringBuffer buf = new StringBuffer();
		if(columns != null) {
			boolean first = true;
			for(StreamElement el : columns) {
				if(first) {
					buf.append(el.name);
					first = false;
				}
				else {
					buf.append("," + el.name);
				}
			}
		}
		return buf.toString();
	}
}
