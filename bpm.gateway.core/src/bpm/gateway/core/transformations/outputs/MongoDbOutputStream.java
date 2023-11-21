package bpm.gateway.core.transformations.outputs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.gateway.core.AbstractTransformation;
import bpm.gateway.core.DefaultStreamDescriptor;
import bpm.gateway.core.Server;
import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.StreamElement;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.Trashable;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.core.server.database.nosql.IMongoDbOutSQL;
import bpm.gateway.core.server.database.nosql.IOutputNoSQL;
import bpm.gateway.core.server.database.nosql.mongodb.MongoDbHelper;
import bpm.gateway.core.server.database.nosql.mongodb.MongoDbServer;
import bpm.gateway.core.transformations.MappingException;
import bpm.gateway.runtime2.transformations.outputs.RunMongoDbOutput;
import bpm.vanilla.platform.core.IRepositoryContext;

public class MongoDbOutputStream extends AbstractTransformation implements IMongoDbOutSQL, IOutputNoSQL, Trashable{

	public static final String PROPERTY_DEFINITION_CHANGED = "definition";
	
	private StreamDescriptor descriptor = new DefaultStreamDescriptor();
	
	private MongoDbServer server;
	
	private String definition;
	private String tableName = "";
	private String columnFamily = "";
	
	private boolean truncate;
	
	/**
	 * step used as trash to collect rows that have not been inserted for some reason
	 */
	private Transformation errorHandlerTransformation;
	private String trashName;
	
	/*
	 * contains the mapping between each input Transformation
	 * 
	 * (We let the mapping with Point for retrocompatibility)
	 */
	private HashMap<Transformation, HashMap<String, String>> mappingNames = new HashMap<Transformation, HashMap<String,String>>();
	
	/*
	 * Only for digester
	 */
	private HashMap<String, HashMap<String, String>> bufferMappingName;
	
	
	
	//those field are use to avoid refreshing 
	//the descriptor 
	private String lastDefinition;
	private String lastTableName;
	private String lastColumnFamily;
	private Server lastLoadedServer;
	
	public MongoDbOutputStream(){
		addPropertyChangeListener(this);
	}
	
	public void setTableName(String tableName) {
		
		this.tableName = tableName;
		try{
			refreshDescriptor();
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	@Override
	public StreamDescriptor getDescriptor(Transformation transfo) throws ServerException {
		return descriptor;
	}

	@Override
	public Element getElement() {
		Element e = DocumentHelper.createElement("mongoDbOutputStream");
		e.addElement("name").setText(name);
		e.addElement("description").setText(description);
		
		
		e.addElement("positionX").setText("" + x);
		e.addElement("positionY").setText("" + y);
		
		e.addElement("truncate").setText("" + truncate);
		
		if (server != null){
			e.addElement("serverRef").setText(server.getName());
		}
		
		if (tableName != null){
			e.addElement("definition").setText(tableName);
		}
		
		if (columnFamily != null){
			e.addElement("columnFamily").setText(columnFamily);
		}
		
		e.addElement("temporaryFileName").setText(getTemporaryFilename());
		e.addElement("temporarySeparator").setText(getTemporarySpliterChar() + "");
		
		if (getTrashTransformation() != null){
			e.addElement("trashOuput").setText(errorHandlerTransformation.getName());
		}
		
		/*
		 * for the mapping
		 */
		for(Transformation t : inputs){
		
			HashMap<String, String> maps = mappingNames.get(t);
			if(maps != null){
				for(String input : maps.keySet()) {
					Element transfoMap = e.addElement("inputMappingName");
					transfoMap.addElement("transformationRef").setText(t.getName());
					
					transfoMap.addElement("inputName").setText(input);
					transfoMap.addElement("outputName").setText(maps.get(input));
				}
			}
		}
		
		if (descriptor != null){
			e.add(descriptor.getElement());
		}
		return e;
	}

	@Override
	public RunMongoDbOutput getExecutioner(IRepositoryContext repositoryCtx,
			int bufferSize) {
		return new RunMongoDbOutput(repositoryCtx, this, bufferSize);
	}

	@Override
	public void refreshDescriptor() {
		if (!isInited()){
			return;
		}
		
		if (lastTableName != tableName || server != lastLoadedServer 
				|| lastDefinition != definition ){
			try {
				if(getTableName() != null && getTableName() != ""){
					descriptor = MongoDbHelper.buildDescriptor(getTableName(), getColumnFamily(), this);
				}
				else {
					descriptor = new DefaultStreamDescriptor();
				}
				lastTableName = tableName;
				lastDefinition = definition;
				lastLoadedServer = server;
				lastColumnFamily = columnFamily;
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		
		//updates the mapping (remove them associated with columns that dont exists anymore
		for(Transformation t : inputs){

			if(bufferMappingName != null){
				//look for the key
				String key = null;
				for(String s : bufferMappingName.keySet()){
					if (s.equals(t.getName())){
						key = s;
						break;
					}
				}
				
				if (key != null){
					HashMap<String, String> maps = bufferMappingName.get(key);
					for(String input : maps.keySet()){
						createMapping(t, input, maps.get(input));
					}
					bufferMappingName.remove(key);
				}
				if (bufferMappingName.isEmpty()){
					bufferMappingName = null;
				}
			}
			
			
			List<String> toRemove = new ArrayList<String>();
			HashMap<String, String> maps = mappingNames.get(t);
			
			if (maps != null){
				for(String input : maps.keySet()){
					boolean found = false;
					try {
						for(StreamElement el : t.getDescriptor(this).getStreamElements()){
							if(input.equals(el.name)){
								found = true;
								break;
							}
						}
					} catch (ServerException e) {
						toRemove.add(input);
					}
					
					if(!found){
						toRemove.add(input);
					}
				}
				
				for(String in : toRemove){
					mappingNames.get(t).remove(in);
				}
			}
		}
		

		try {
			if(getTableName() != null && getTableName() != ""){
				descriptor = MongoDbHelper.buildDescriptor(getTableName(), getColumnFamily(), this);
			}
			else {
				descriptor = new DefaultStreamDescriptor();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}

	@Override
	public String getColumnFamily() {
		return columnFamily;
	}

	@Override
	public String getTableName() {
		return tableName;
	}

	@Override
	public boolean isTruncate() {
		return truncate;
	}

	@Override
	public void setColumnFamily(String columnFamily) {
		this.columnFamily = columnFamily;		
	}

	@Override
	public void setTruncate(boolean truncate) {
		this.truncate = truncate;		
	}

	@Override
	public String getDefinition() {
		return definition;
	}

	@Override
	public Server getServer() {
		return server;
	}

	@Override
	public void setDefinition(String definition) {
		this.definition = createCqlQuery(definition);
		this.tableName = definition;
		
		fireProperty(PROPERTY_INPUT_CHANGED);
		
	}

	private String createCqlQuery(String tableName) {
		return "select * from " + tableName;
	}
	
	@Override
	public void setServer(Server s) {
		this.server = (MongoDbServer)s;		
	}

	@Override
	public Transformation copy() {
		MongoDbOutputStream copy = new MongoDbOutputStream();
		
		copy.setDefinition(getDefinition());
		copy.setDescription(description);
		copy.setName("copy of " + name);
		copy.setServer(server);
		copy.setTruncate(truncate);
		copy.setColumnFamily(columnFamily);
		copy.setDefinition(tableName);
		
		return copy;
	}

	@Override
	public String getAutoDocumentationDetails() {
		StringBuffer buf = new StringBuffer();
		 
		buf.append("MongoDbServer : " + getServer().getName() + "\n");
		buf.append("Target Table : " + getDefinition() + "\n");
		buf.append("Target Column Family : " + getColumnFamily() + "\n");
		buf.append("Truncate Table First : " + truncate + "\n");
		
		return buf.toString();
	}

	public void createBufferMappingName(String transfoName, String inputCol, String outputCol){
		
		if (bufferMappingName == null){
			bufferMappingName = new HashMap<String, HashMap<String,String>>();
		}
		
		/*
		 * look for the key
		 */
		String k = null;
		for(String key : bufferMappingName.keySet()){
			if (key.equals(transfoName)){
				k = key;
				break;
			}
		}
		if (k == null){
			k = transfoName;
			
			bufferMappingName.put(k, new HashMap<String, String>());
		}
		
		bufferMappingName.get(k).put(inputCol, outputCol);
	}
	
	public void setTruncate(String truncate) {
		this.truncate = Boolean.parseBoolean(truncate);
	}

	public void setTrashTransformation(String name){
		this.trashName = name;
	}
	
	@Override
	public void createMapping(Transformation input, String inputCol,
			String outputCol) {
		if(mappingNames.get(input) == null){
			mappingNames.put(input, new HashMap<String, String>());
		}
		
		mappingNames.get(input).put(inputCol, outputCol);
		
	}

	@Override
	public void deleteMapping(Transformation input, String inputCol) {
		HashMap<String, String> maps = mappingNames.get(input);
		if(maps.get(inputCol) != null){
			maps.remove(inputCol);
		}
		
	}

	@Override
	public int getIndexKey(Transformation input) throws MappingException,
			Exception {
//		HashMap<String, String> maps = mappingNames.get(input);
//		for(String col : maps.keySet()){
//			if(maps.get(col).equals(ID_DEFINITION)){
//				int i = 0;
//				for(StreamElement el : input.getDescriptor().getStreamElements()){
//					if(col.equals(el.name)){
//						return i;
//					}
//					i++;
//				}
//				break;
//			}
//		}
//
//		throw new MappingException("The column index 'KEY' is not mapped. Please, do it before running.");
	return -2;
	}

	@Override
	public HashMap<String, String> getMappingsFor(Transformation t) {
		if(mappingNames.get(t) != null){
			return new HashMap<String, String>(mappingNames.get(t));
		}
		else {
			return new HashMap<String, String>();
		}
	}

	@Override
	public int getMappingsIndexFor(Transformation input, String column)
			throws Exception {
		int i = 0;
		for(StreamElement el : input.getDescriptor(this).getStreamElements()){
			if(column.equals(el.name)){
				return i;
			}
			i++;
		}
		
		return -1;
	}

	@Override
	public boolean isIndexMap(Transformation input) throws Exception {
		
		HashMap<String, String> maps = mappingNames.get(input);
		for(String col : maps.keySet()){
			if(maps.get(col).equals(ID_DEFINITION)){
				for(StreamElement el : input.getDescriptor(this).getStreamElements()){
					if(col.equals(el.name)){
						return true;
					}
				}
				break;
			}
		}
		
		return false;
	}

	@Override
	public boolean isMapped(Transformation t, String colName) {
	
		if(mappingNames.get(t).get(colName) != null && !mappingNames.get(t).get(colName).isEmpty()){
			return true;
		}
		return false;
	}

	@Override
	public Transformation getTrashTransformation() {
		if (trashName != null && getDocument() != null){
			errorHandlerTransformation = getDocument().getTransformation(trashName);
			trashName = null;
		}
		return errorHandlerTransformation;
	}

	@Override
	public void setTrashTransformation(Transformation transfo) {
		this.errorHandlerTransformation = transfo;
		
	}

}
