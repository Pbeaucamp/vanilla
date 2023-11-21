package bpm.gateway.core.transformations.outputs;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.gateway.core.AbstractTransformation;
import bpm.gateway.core.DefaultStreamDescriptor;
import bpm.gateway.core.ISCD;
import bpm.gateway.core.ITargetableStream;
import bpm.gateway.core.Server;
import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.StreamElement;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.Trashable;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.core.manager.ResourceManager;
import bpm.gateway.core.server.database.DataBaseHelper;
import bpm.gateway.core.server.database.DataBaseServer;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.transformation.scd.RunInsertOrUpdate;
import bpm.vanilla.platform.core.IRepositoryContext;


/**
 * perform an insert if the row doesnt exist in the Targer or a insert
 * @author LCA
 *
 */
public class InsertOrUpdate extends AbstractTransformation implements ITargetableStream, Trashable, ISCD{

	/*
	 * target informations
	 */
	protected DataBaseServer targetServer;
	protected String targetTableName;
	protected String targetDefinitionSql;
	protected DefaultStreamDescriptor descriptor = new DefaultStreamDescriptor();
	
	/*
	 * Key : target StreamElement index
	 * Get : input StreamElement index
	 */
	protected HashMap<String, String> mappingKeyNames = new HashMap<String,String>();
	protected HashMap<String, String> mappingFieldNames = new HashMap<String,String>();
	protected HashMap<String, Boolean> ignoreFields = new HashMap<String, Boolean>();

	/**
	 * Buffer Mapping for digester
	 */
	protected List<Point> bufferKeys = new ArrayList<Point>();
	protected List<Point> bufferFields = new ArrayList<Point>();
	protected HashMap<String, String> bufferKeyName = new HashMap<String, String>();
	protected HashMap<String, String> bufferFieldName = new HashMap<String, String>();
	
	protected Transformation trashTransformation;
	protected String trashName;

	
	/**
	 * Used by digester
	 * 
	 * @param inputIndex
	 * @param outputIndex
	 */
	public void createBufferKeys(String inputIndex, String outputIndex){
		if (bufferKeys == null){
			bufferKeys = new ArrayList<Point>();
		}
		
		try{
			Integer x = Integer.parseInt(inputIndex);
			Integer y = Integer.parseInt(outputIndex);
			
			bufferKeys.add(new Point(x, y));
		}catch(NumberFormatException e){
			
		}
	}	
	
	/**
	 * Used by digester
	 * 
	 * @param inputIndex
	 * @param outputIndex
	 */
	public void createBufferFields(String inputIndex, String outputIndex){
		if (bufferFields == null){
			bufferFields = new ArrayList<Point>();
		}
		
		try{
			Integer x = Integer.parseInt(inputIndex);
			Integer y = Integer.parseInt(outputIndex);
			
			bufferFields.add(new Point(x, y));
		}catch(NumberFormatException e){
			
		}
	}
	
	/**
	 * Used by digester
	 * 
	 * @param inputCol
	 * @param outputCol
	 */
	public void createBufferKeyName(String inputCol, String outputCol){
		if (bufferKeyName == null){
			bufferKeyName = new HashMap<String,String>();
		}

		bufferKeyName.put(inputCol, outputCol);
	}
	
	/**
	 * Used by digester
	 * 
	 * @param inputCol
	 * @param outputCol
	 */
	public void createBufferFieldName(String inputCol, String outputCol){
		if (bufferFieldName == null){
			bufferFieldName = new HashMap<String,String>();
		}

		bufferFieldName.put(inputCol, outputCol);
	}
	
	@Override
	public void createKeyMapping(Integer input, Integer value) {
		StreamElement inputElement = null;
		try {
			if(inputs.get(0) != null){
				inputElement = inputs.get(0).getDescriptor(this).getStreamElements().get(input);
			}
		} catch (ServerException e) {
			e.printStackTrace();
		}

		if(inputElement != null){
			createKeyMapping(inputElement, value);
		}
	}
	
	@Override
	public void createKeyMapping(StreamElement element, Integer value) {
		if(value != -1){
			StreamElement outputElement = null;
			try {
				if(inputs.get(0) != null){
					outputElement  = inputs.get(0).getDescriptor(this).getStreamElements().get(value);
				}
			} catch (ServerException e) {
				e.printStackTrace();
			}
	
			if(element != null && outputElement != null){
				String inputName = element.name;
				String ouputName = outputElement.originTransfo + "::" + outputElement.name;
				
				createKeyMapping(inputName, ouputName);
			}
		}
		else {
			String inputName = element.name;
			mappingKeyNames.remove(inputName);
		}
	}
	
	@Override
	public void createKeyMapping(String inputColumn, String outputColumn) {
		if(mappingKeyNames == null){
			mappingKeyNames = new HashMap<String, String>();
		}
		
		mappingKeyNames.put(inputColumn, outputColumn);
	}

	@Override
	public void createFieldMapping(Integer input, Integer value) {
		StreamElement inputElement = null;
		try {
			if(inputs.get(0) != null){
				inputElement = inputs.get(0).getDescriptor(this).getStreamElements().get(input);
			}
		} catch (ServerException e) {
			e.printStackTrace();
		}

		if(inputElement != null){
			createFieldMapping(inputElement, value);
		}
	}
	
	@Override
	public void createFieldMapping(StreamElement element, Integer value) {
		if(value != -1){
			StreamElement outputElement = null;
			try {
				if(inputs.get(0) != null){
					outputElement  = inputs.get(0).getDescriptor(this).getStreamElements().get(value);
				}
			} catch (ServerException e) {
				e.printStackTrace();
			}
	
			if(element != null && outputElement != null){
				String inputName = element.name;
				String ouputName = outputElement.originTransfo + "::" + outputElement.name;
				
				createFieldMapping(inputName, ouputName);
			}
		}
		else {
			String inputName = element.name;
			mappingFieldNames.remove(inputName);
		}
	}
	
	@Override
	public void createFieldMapping(String inputColumn, String outputColumn) {
		if(mappingFieldNames == null){
			mappingFieldNames = new HashMap<String, String>();
		}
		
		mappingFieldNames.put(inputColumn, outputColumn);
	}
	
	public void setTrashTransformation(String name){
		this.trashName = name;
	}
	
	public void setTrashTransformation(Transformation t){
		if (t == null){
			trashTransformation = null;
		}
		if (getOutputs().contains(t)){
			trashTransformation = t;
		}
	}
	
	public Transformation getTrashTransformation(){
		if (trashName != null && getDocument() != null){
			trashTransformation = getDocument().getTransformation(trashName);
			trashName = null;
		}
		return trashTransformation;
	}

	/**
	 * 
	 * @return the targettable name
	 */
	public String getTableName(){
		return targetTableName;
	}
	
	public void setServer(Server s) {
		this.targetServer = (DataBaseServer)s;
	}
	
	public final void setServer(String serverName){
		targetServer = (DataBaseServer)ResourceManager.getInstance().getServer(serverName);
	}
	
	public Server getServer() {
		return targetServer;
	}
	
	@Override
	public boolean addInput(Transformation stream) throws Exception {
		if (getInputs().size() > 0 && !getInputs().contains(stream)){
			throw new Exception("The Filter Transformations can only have one Input");
		}
		return super.addInput(stream);
	}
	
	@Override
	public StreamDescriptor getDescriptor(Transformation transfo) throws ServerException {
		return descriptor;
	}

	@Override
	public Element getElement() {
		Element e = DocumentHelper.createElement("insertOrUpdate");
		e.addElement("name").setText(name);
		e.addElement("description").setText(description);
		
		
		e.addElement("positionX").setText("" + x);
		e.addElement("positionY").setText("" + y);
		
		e.addElement("temporaryFileName").setText(getTemporaryFilename());
		e.addElement("temporarySeparator").setText(getTemporarySpliterChar() + "");
		
		
		if (targetServer != null){
			e.addElement("serverRef").setText(targetServer.getName());
		}
		
		if (targetDefinitionSql != null){
			e.addElement("definition").setText(targetDefinitionSql);
		}
		
		
		
		if (trashTransformation != null){
			e.addElement("trashRef").setText(trashTransformation.getName());
		}
		
		for(String input : mappingKeyNames.keySet()){
			Element k = e.addElement("mappingKeyName");
			k.addElement("inputName").setText(input);
			k.addElement("outputName").setText(mappingKeyNames.get(input));
		}
		
		for(String input : mappingFieldNames.keySet()){
			Element k = e.addElement("mappingFieldsName");
			k.addElement("inputName").setText(input);
			k.addElement("outputName").setText(mappingFieldNames.get(input));
		}
		
		for(String input : ignoreFields.keySet()){
			Element k = e.addElement("ignoreFields");
			k.addElement("inputName").setText(input);
			k.addElement("ignore").setText(ignoreFields.get(input)+"");
		}
		
		if (descriptor != null){
			e.add(descriptor.getElement());
		}
		return e;
	}
	
	@Override
	public RuntimeStep getExecutioner(IRepositoryContext repositoryCtx, int bufferSize) {
		return new RunInsertOrUpdate(this, bufferSize);
	}

	@Override
	public void refreshDescriptor() {
		if (!isInited()){
			return;
		}
		
		try {
			descriptor = (DefaultStreamDescriptor)DataBaseHelper.getDescriptor(this);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		if (bufferKeys != null){
			for(Point p : bufferKeys){
				try{
					createKeyMapping(p.x, p.y);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
				
			bufferKeys = null;
		}
		else if(bufferKeyName != null){
			for(String input : bufferKeyName.keySet()){
				try {
					createKeyMapping(input, bufferKeyName.get(input));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			bufferKeyName = null;
		}
		
		if (bufferFields != null){
			for(Point p : bufferFields){
				try{
					createFieldMapping(p.x, p.y);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
				
			bufferFields = null;
		}
		else if(bufferFieldName != null){
			for(String input : bufferFieldName.keySet()){
				try {
					createFieldMapping(input, bufferFieldName.get(input));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			bufferFieldName = null;
		}
		
		for(Transformation  t : getOutputs()){
			t.refreshDescriptor();
		}

	}

	public Transformation copy() {
		InsertOrUpdate copy = new InsertOrUpdate();
		
		copy.setDefinition(getDefinition());
		copy.setDescription(description);
		copy.setName("copy of " + name);
		return copy;
	}

	public String getDefinition() {
		return targetDefinitionSql;
	}
	
	/**
	 * set the target table name
	 */
	public void setDefinition(String definition) {
		this.targetDefinitionSql = definition;
		if (definition.contains(" from ")){
			targetTableName = definition.substring(definition.indexOf(" from ") + 6).trim();
		}
		
		/*
		 * clean all informations because input has changed
		 */
		mappingKeyNames = new HashMap<String, String>();
		mappingFieldNames = new HashMap<String, String>();
		
		refreshDescriptor();
		fireProperty(PROPERTY_INPUT_CHANGED);

	}


	@Override
	public void removeInput(Transformation transfo) {
		/*
		 * clean all informations because input has changed
		 */
		mappingKeyNames = new HashMap<String, String>();
		mappingFieldNames = new HashMap<String, String>();
		
		super.removeInput(transfo);
	}
	
	@Override
	public Integer getInputIndexFieldFortargetIndex(int colNum){
		if (mappingFieldNames == null){
			return null;
		}
		
		StreamElement element = descriptor.getStreamElements().get(colNum);
		
		if(element != null){
			String output = mappingFieldNames.get(element.name);
			
			StreamDescriptor transfoDescriptor = null;
			try {
				transfoDescriptor = inputs.get(0).getDescriptor(this);
			} catch (Exception e) {
				
			}
			
			if(output != null && transfoDescriptor != null){
				try {
					for(int i=0; i<transfoDescriptor.getStreamElements().size(); i++){
						if(output.endsWith("::" + transfoDescriptor.getStreamElements().get(i).name)){
							return i;
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			}
		}

		return null;
	}
	
	@Override
	public Integer getInputIndexKeyFortargetIndex(int colNum){
		if (mappingKeyNames == null){
			return null;
		}
		
		StreamElement element = descriptor.getStreamElements().get(colNum);
		
		if(element != null){
			String output = mappingKeyNames.get(element.name);
			
			StreamDescriptor transfoDescriptor = null;
			try {
				transfoDescriptor = inputs.get(0).getDescriptor(this);
			} catch (Exception e) {
				
			}
			
			if(output != null && transfoDescriptor != null){
				try {
					for(int i=0; i<transfoDescriptor.getStreamElements().size(); i++){
						if(output.endsWith("::" + transfoDescriptor.getStreamElements().get(i).name)){
							return i;
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			}
		}

		return null;
	}
	
	@Override
	public Integer getTargetIndexFieldForInputIndex(int colNum){
		if (mappingFieldNames == null){
			return -1;
		}
		
		StreamDescriptor transfoDescriptor = null;
		try {
			transfoDescriptor = inputs.get(0).getDescriptor(this);
		} catch (Exception e) {
			return -1;
		}

		if(transfoDescriptor != null){
			StreamElement element = transfoDescriptor.getStreamElements().get(colNum);
			
			if(element != null){
				for(String key : mappingFieldNames.keySet()){
					if (mappingFieldNames.get(key).endsWith("::" + element.name)){
							
						for(int i=0; i<descriptor.getStreamElements().size(); i++){
							if(key.equals(descriptor.getStreamElements().get(i).name)){
								return i;
							}
						}
					}
				}
			}
		}
		
		return -1;
	}
	
	@Override
	public Integer getTargetIndexKeyForInputIndex(int colNum){
		//Not needed
		return -1;
	}

	@Override
	public boolean isTruncate() {
		return false;
	}

	@Override
	public void setTruncate(boolean value) {
				
	}

	@Override
	public void addOutput(Transformation stream) {
		super.addOutput(stream);
		
		if (trashName != null && trashName.equals(stream.getName())){
			setTrashTransformation(stream);
			trashName = null;
		}
	}

	public String getAutoDocumentationDetails() {
		StringBuffer buf = new StringBuffer();
		buf.append("DataBaseServer : " + getServer().getName() + "\n");
		buf.append("Target Table : " + targetTableName + "\n");
		if (trashTransformation != null){
			buf.append("Trash  Output Name: " + trashTransformation.getName()+ "\n");
		}
		
		return buf.toString();
	}

	@Override
	public void createIgnoreField(String inputColumn, boolean ignore) {
		ignoreFields.put(inputColumn, ignore);
		
	}
	
	public void createIgnoreField(String inputColumn, String ignore) {
		ignoreFields.put(inputColumn, Boolean.parseBoolean(ignore));
		
	}

	@Override
	public boolean isIgnoreField(int i) {
		
		StreamDescriptor transfoDescriptor = null;
		try {
			transfoDescriptor = this.getInputs().get(0).getDescriptor(this);
		} catch (Exception e) {
			return false;
		}

		if(transfoDescriptor != null){
			StreamElement element = transfoDescriptor.getStreamElements().get(i);
			return ignoreFields.get(element.name) != null ? ignoreFields.get(element.name) : false;
		}
		return false;
	}
}
