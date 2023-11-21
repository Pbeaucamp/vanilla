package bpm.gateway.core.transformations.outputs;

import java.awt.Point;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.gateway.core.AbstractTransformation;
import bpm.gateway.core.Activator;
import bpm.gateway.core.DataStream;
import bpm.gateway.core.DefaultStreamDescriptor;
import bpm.gateway.core.Server;
import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.StreamElement;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.core.manager.ResourceManager;
import bpm.gateway.core.server.file.AbstractFileServer;
import bpm.gateway.core.server.file.FileSystemServer;
import bpm.gateway.core.transformations.MappingException;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.transformations.outputs.RunCielComptaOutput;
import bpm.gateway.runtime2.transformations.outputs.RunDataBaseOutput;
import bpm.vanilla.platform.core.IRepositoryContext;

public class CielComptaOutput extends AbstractTransformation implements IOutput, DataStream {

	private char separator = ';';
	private Server server = FileSystemServer.getInstance();

	private String filePath;
	private String fileUrl;

	protected DefaultStreamDescriptor descriptor = new DefaultStreamDescriptor();

	private String encoding = "UTF-8";

	protected boolean fromUrl = false;
	
	private HashMap<Transformation, HashMap<String, String>> mappingNames = new HashMap<Transformation, HashMap<String,String>>();
	/*
	 * only for digester
	 */
	private HashMap<String, List<Point>> bufferMapping;
	
	/*
	 * Only for digester
	 */
	private HashMap<String, HashMap<String, String>> bufferMappingName;

	public String getDefinition() {
		if (fromUrl) {
			return fileUrl;
		}
		else {
			return filePath;
		}
	}

	public Server getServer() {
		if (server == null) {
			return FileSystemServer.getInstance();
		}
		return server;
	}

	public void setDefinition(String definition) {
		if (fromUrl) {
			this.fileUrl = definition;
		}
		else {
			this.filePath = definition;
		}
	}

	public void setServer(Server s) {
		this.server = s;
	}

	/**
	 * @return the encoding
	 */
	public String getEncoding() {
		return encoding;
	}

	/**
	 * @param encoding
	 *            the encoding to set
	 */
	public void setEncoding(String encoding) {
		this.encoding = encoding;

	}

	public void setServer(String serverName) {
		for (Server s : ResourceManager.getInstance().getServers(AbstractFileServer.class)) {
			if (s.getName().equals(serverName)) {
				setServer(s);
				return;
			}
		}

	}

	public char getSeparator() {
		return separator;
	}

	public void setSeparator(char c) {
		separator = c;
	}

	public void setSeparator(String s) {
		try {
			separator = s.charAt(0);
		} catch (IndexOutOfBoundsException e) { }
	}

	@Override
	public StreamDescriptor getDescriptor(Transformation transfo) throws ServerException {
		return descriptor;
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
	
	public void createBufferMapping(String transfoName, String transfoIndex, String index){
		
		if (bufferMapping == null){
			bufferMapping = new HashMap<String, List<Point>>();
		}
		
		/*
		 * look for the key
		 */
		String k = null;
		for(String key : bufferMapping.keySet()){
			if (key.equals(transfoName)){
				k = key;
				break;
			}
		}
		if (k == null){
			k = transfoName;
			
			bufferMapping.put(k, new ArrayList<Point>());
		}
		
		try{
			Integer x = Integer.parseInt(transfoIndex);
			Integer y = Integer.parseInt(index);
			
			bufferMapping.get(k).add(new Point(x, y));
		}catch(NumberFormatException e){
			
		}
	}
	
	public Element getElement() {
		Element e = DocumentHelper.createElement("cielComptaOutput");
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
		
		e.addElement("temporaryFileName").setText(getTemporaryFilename());
		e.addElement("temporarySeparator").setText(getTemporarySpliterChar() + "");
		
		/*
		 * for the mapping
		 */
		for(Transformation t : inputs){
		
			try {
				HashMap<String, String> maps = mappingNames.get(t);
				for(String input : maps.keySet()) {
					Element transfoMap = e.addElement("inputMappingName");
					transfoMap.addElement("transformationRef").setText(t.getName());
					
					transfoMap.addElement("inputName").setText(input);
					transfoMap.addElement("outputName").setText(maps.get(input));
				}
			} catch (Exception e1) {
			}
		}
		
		if (descriptor != null){
			e.add(descriptor.getElement());
		}
		return e;
	}

	// public abstract TransformationRuntime getExecutioner(RuntimeEngine
	// runtimeEngine);
	public RuntimeStep getExecutioner(IRepositoryContext repositoryCtx, int bufferSize) {
		return new RunCielComptaOutput(this, bufferSize);
	}

	protected void setDescriptor(DefaultStreamDescriptor desc) {
		this.descriptor = desc;
		refreshDescriptor();
		this.fireChangedProperty();
	}

	@Override
	public Transformation copy() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAutoDocumentationDetails() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * if the current mapping does not exist already create it 
	 * @param input
	 * @param transfoColNum
	 * @param colNum
	 */
	public void createMapping(Transformation input, int transfoColNum, int colNum) throws MappingException{
		if (!getInputs().contains(input)){
			return;
		}
		
		StreamElement inputElement = null;
		try {
			inputElement = input.getDescriptor(this).getStreamElements().get(transfoColNum);
		} catch (ServerException e) {
			e.printStackTrace();
		}
		
		StreamElement outputElement = descriptor.getStreamElements().get(colNum);

		String inputName = inputElement.originTransfo + "::" + inputElement.name;
		String ouputName = outputElement.name;
		
		createMapping(input, inputName, ouputName);
	}
	
	private void createMapping(Transformation input, String inputCol, String outputCol) throws MappingException{
		if (!getInputs().contains(input)){
			return;
		}
		
		if(mappingNames.get(input) == null){
			mappingNames.put(input, new HashMap<String, String>());
		}
		
		/*
		 * verify that this mapping not already exist
		 */
		HashMap<String, String> maps = mappingNames.get(input);
		for(String in : maps.keySet()){
			if(in.equals(inputCol) && maps.get(in).equals(outputCol)){
				return;
			}
			
			if(in.equals(inputCol)){
				throw new MappingException("A mapping already exists for that Input Stream Element");
			}
			
			if (maps.get(in).equals(outputCol)){
				throw new MappingException("A mapping already exists for that DataBaseOuput Stream Element");
			}
		}
		
		mappingNames.get(input).put(inputCol, outputCol);
	}


	/**
	 * remove the specified point from the mapping
	 * @param input
	 * @param transfoColNum
	 * @param colNum
	 */
	public void deleteMapping(Transformation input, int transfoColNum, int colNum){
		StreamElement inputElement = null;
		try {
			inputElement = input.getDescriptor(this).getStreamElements().get(transfoColNum);
		} catch (ServerException e) {
			e.printStackTrace();
		}
		
		StreamElement outputElement = descriptor.getStreamElements().get(colNum);

		String inputName = inputElement.originTransfo + "::" + inputElement.name;
		String ouputName = outputElement.name;
		
		deleteMapping(input, inputName, ouputName);
	}
	
	/**
	 * Remove the specified colonne from the mapping
	 * 
	 * @param input
	 * @param transfoColNum
	 * @param colNum
	 */
	private void deleteMapping(Transformation input, String inputCol, String outputCol){
		HashMap<String, String> maps = mappingNames.get(input);
		for(String key : maps.keySet()){
			if (key.equals(inputCol) && maps.get(key).equals(outputCol)){
				mappingNames.get(input).remove(key);
				return;
			}
		}
	}
	
	@Override
	public void clearMapping(Transformation input){
		HashMap<String, String> maps = mappingNames.get(input);
		if(maps != null){
			maps.clear();
		}
	}

	/**
	 * 
	 * Test for the input column
	 * 
	 * @param t
	 * @param colName
	 * @return
	 */
	public boolean isMapped(Transformation t, String colName){
		if(mappingNames.get(t) == null){
			return false;
		}
		if(mappingNames.get(t).get(colName) != null && !mappingNames.get(t).get(colName).isEmpty()){
			return true;
		}
		return false;
	}
	
	/**
	 * 
	 * Test for the output column
	 * 
	 * @param colName
	 * @return
	 */
	public boolean isMapped(String colName){
		for(Transformation t : inputs){

			HashMap<String, String> maps = mappingNames.get(t);
			
			if(maps == null){
				maps = new HashMap<String, String>();
			}
			for(String key : maps.keySet()){
				if(maps.get(key).equals(colName)){
					return true;
				}
			}
		}
		return false;
	}
	
	public void setInited(){
		
		refreshDescriptor();
		this.inited = true;
		Activator.getLogger().debug(getName() + " is Inited");
		try{
			Activator.getLogger().debug(getName() + " is refreshed , descriptor size = " + getDescriptor(this).getColumnCount());
		}catch(Exception ex){
			
		}
	}
	
	@Override
	public void refreshDescriptor() {
		if (!isInited()){
			return;
		}
		
		descriptor = new DefaultStreamDescriptor();
		
//		//compte
//		descriptor.addColumn(name, "compte", "compte_date", Types.DATE, "java.util.Date", "", true, "DATE", null, false);
//		descriptor.addColumn(name, "compte", "compte_numero", Types.INTEGER, "java.lang.Integer", "", true, "INT", null, false);
//		descriptor.addColumn(name, "compte", "compte_libelle", Types.VARCHAR, "java.lang.String", "", true, "VARCHAR", null, false);
//		
//		//ligne
//		descriptor.addColumn(name, "ligne", "ligne_date", Types.DATE, "java.util.DATE", "", true, "DATE", null, false);
//		descriptor.addColumn(name, "ligne", "ligne_journal", Types.VARCHAR, "java.lang.String", "", true, "VARCHAR", null, false);
//		descriptor.addColumn(name, "ligne", "ligne_compte", Types.INTEGER, "java.lang.Integer", "", true, "INT", null, false);
//		descriptor.addColumn(name, "ligne", "ligne_debit", Types.DOUBLE, "java.lang.Double", "", true, "DOUBLE", null, false);
//		descriptor.addColumn(name, "ligne", "ligne_credit", Types.DOUBLE, "java.lang.Double", "", true, "DOUBLE", null, false);
//		descriptor.addColumn(name, "ligne", "ligne_libelle", Types.VARCHAR, "java.lang.String", "", true, "VARCHAR", null, false);
//		descriptor.addColumn(name, "ligne", "ligne_piece", Types.VARCHAR, "java.lang.String", "", true, "VARCHAR", null, false);
//		descriptor.addColumn(name, "ligne", "ligne_monnaie", Types.VARCHAR, "java.lang.String", "", true, "VARCHAR", null, false);
		
		descriptor.addColumn(name, "ligne", "date", Types.DATE, "java.util.Date", "", true, "DATE", null, false);
		
		descriptor.addColumn(name, "ligne", "compte_debit", Types.VARCHAR, "java.lang.String", "", true, "VARCHAR", null, false);
		descriptor.addColumn(name, "ligne", "compte_debit_lib", Types.VARCHAR, "java.lang.String", "", true, "VARCHAR", null, false);
		descriptor.addColumn(name, "ligne", "compte_credit", Types.VARCHAR, "java.lang.String", "", true, "VARCHAR", null, false);
		descriptor.addColumn(name, "ligne", "compte_credit_lib", Types.VARCHAR, "java.lang.String", "", true, "VARCHAR", null, false);
		descriptor.addColumn(name, "ligne", "compte_tva", Types.VARCHAR, "java.lang.String", "", true, "VARCHAR", null, false);
		descriptor.addColumn(name, "ligne", "compte_tva_lib", Types.VARCHAR, "java.lang.String", "", true, "VARCHAR", null, false);
		
		descriptor.addColumn(name, "ligne", "journal", Types.VARCHAR, "java.lang.String", "", true, "VARCHAR", null, false);
		
		descriptor.addColumn(name, "ligne", "HT", Types.VARCHAR, "java.lang.String", "", true, "VARCHAR", null, false);
		descriptor.addColumn(name, "ligne", "TTC", Types.VARCHAR, "java.lang.String", "", true, "VARCHAR", null, false);
		descriptor.addColumn(name, "ligne", "TVA", Types.VARCHAR, "java.lang.String", "", true, "VARCHAR", null, false);
		
		descriptor.addColumn(name, "ligne", "libelle", Types.VARCHAR, "java.lang.String", "", true, "VARCHAR", null, false);
		descriptor.addColumn(name, "ligne", "piece", Types.VARCHAR, "java.lang.String", "", true, "VARCHAR", null, false);
		descriptor.addColumn(name, "ligne", "monnaie", Types.VARCHAR, "java.lang.String", "", true, "VARCHAR", null, false);
		
		//updates the mapping (remove them associated with columns that dont exists anymore
		for(Transformation t : inputs){

			if (bufferMapping != null){
				//look for the key
				String key = null;
				for(String s : bufferMapping.keySet()){
					if (s.equals(t.getName())){
						key = s;
						break;
					}
				}
				
				if (key != null){
					for(Point p : bufferMapping.get(key)){
						try {
							createMapping(t, p.x, p.y);
						} catch (MappingException e) {
							e.printStackTrace();
						}
					}
					bufferMapping.remove(key);
				}
				if (bufferMapping.isEmpty()){
					bufferMapping = null;
				}
			
			}
			else if(bufferMappingName != null){
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
						try {
							createMapping(t, input, maps.get(input));
						} catch (MappingException e) {
							e.printStackTrace();
						}
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
							if(input.equals(el.originTransfo + "::" + el.name)){
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
					else {
						found = false;
						for(StreamElement el : descriptor.getStreamElements()){
							if(maps.get(input).equals(el.name)){
								found = true;
								break;
							}
						}
						
						if (!found){
							toRemove.add(input);
						}
					}
				}
				
				for(String in : toRemove){
					mappingNames.get(t).remove(in);
				}
			}
			
		}
		for(Transformation  t : getOutputs()){
			t.refreshDescriptor();
		}
	}

	/**
	 * return the mappings for the given transformation
	 * Point.x : the transformation t column number
	 * Point.y : the current object column number
	 * @param t
	 * @return
	 */
	public HashMap<String, String> getMappingsFor(Transformation t){
		if(mappingNames.get(t) == null) {
			mappingNames.put(t, new HashMap<String, String>());
		}
		return new HashMap<String, String>(mappingNames.get(t));
	}
	
	@Override
	public Integer getMappingValueForInputNum(Transformation t, int colNum){
		if (mappingNames.get(t) == null){
			return null;
		}
		
		StreamElement element = null;
		try {
			element = t.getDescriptor(this).getStreamElements().get(colNum);
		} catch (ServerException e) {
			e.printStackTrace();
			return null;
		}
		
		if(element != null){
			String output = mappingNames.get(t).get(element.originTransfo + "::" + element.name);
			
			if(output != null){
				for(int i=0; i<descriptor.getStreamElements().size(); i++){
					if(output.equals(descriptor.getStreamElements().get(i).name)){
						return i;
					}
				}
			}
		}

		return null;
	}
	
	@Override
	public Integer getMappingValueForThisNum(Transformation t, int colNum){
		if (mappingNames.get(t) == null){
			return null;
		}
		
		StreamElement element = descriptor.getStreamElements().get(colNum);
		
		if(element != null){
			HashMap<String, String> maps = mappingNames.get(t);
			for(String key : maps.keySet()){
				if (maps.get(key).equals(element.name)){
					
					StreamDescriptor transfoDescriptor = null;
					try {
						transfoDescriptor = t.getDescriptor(this);
					} catch (Exception e) {
						
					}
					
					if(transfoDescriptor != null){
						for(int i=0; i<transfoDescriptor.getStreamElements().size(); i++){
							if(key.equals(transfoDescriptor.getStreamElements().get(i).originTransfo 
									+ "::" + transfoDescriptor.getStreamElements().get(i).name)){
								return i;
							}
						}
					}
				}
			}
		}
		
		return null;
	}

}
