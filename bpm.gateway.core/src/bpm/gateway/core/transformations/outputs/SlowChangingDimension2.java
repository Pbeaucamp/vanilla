package bpm.gateway.core.transformations.outputs;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.gateway.core.AbstractTransformation;
import bpm.gateway.core.DataStream;
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
import bpm.gateway.runtime2.transformation.scd.RunSCD1;
import bpm.gateway.runtime2.transformation.scd.SCD2Runtime;
import bpm.vanilla.platform.core.IRepositoryContext;

public class SlowChangingDimension2 extends AbstractTransformation implements ITargetableStream, ISCD, DataStream, Trashable{

	public static final String[] SEQUENCE_TYPE = {"DataBase Auto-Increment", "Max + 1"};
	
	public static final int DB_AUTO_INC_SEQUENCE = 0;
	public static final int MAX_INC_SEQUENCE = 1;
	
	public static final int SCD_TYPE1 = 0;
	public static final int SCD_TYPE2 = 1;
	public static final String[] SCD_TYPES = new String[]{"Type I, Type II"};
	
	private int sequenceType = 1;
	
	/*
	 * target informations
	 */
	private int scdType = 0;
	private DataBaseServer targetServer;
	private String targetTableName;
	private String targetDefinitionSql;
	private DefaultStreamDescriptor descriptor = new DefaultStreamDescriptor();
	
	private String activeValue = "1";
	private String inactiveValue = "0";
	
	private Integer maxYear = 2010;
	
	/*
	 * Key : target StreamElement index
	 * Get : input StreamElement index
	 */
	private HashMap<String, String> mappingKeyNames = new HashMap<String,String>();
	private HashMap<String, String> mappingFieldNames = new HashMap<String,String>();
	protected HashMap<String, Boolean> ignoreFields = new HashMap<String, Boolean>();

	/*
	 * Buffer Mapping for digester
	 */
	private List<Point> bufferKeys = new ArrayList<Point>();
	private List<Point> bufferFields = new ArrayList<Point>();
	private HashMap<String, String> bufferKeyName = new HashMap<String, String>();
	private HashMap<String, String> bufferFieldName = new HashMap<String, String>();

	private Integer bufferTargetKeyIndex;
	private Integer bufferTargetVersionIndex;
	private Integer bufferTargetStartDateIndex;
	private Integer bufferTargetStopDateIndex;
	private Integer bufferTargetActiveIndex;
	private Integer bufferInputDateIndex;
	
	private String targetKeyField;
	private String targetVersionField;
	private String targetStartDateField;
	private String targetStopDateField;
	private String targetActiveField;
	private String inputDateField;
	
	/**
	 * step used as trash to collect rows that have not been inserted for some reason
	 */
	private Transformation errorHandlerTransformation;
	private String trashName;
	

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
	
	/**
	 * Used by digester
	 * 
	 * @param targetKeyIndex
	 */
	public void createBufferTargetKeyIndex(String targetKeyIndex){
		this.bufferTargetKeyIndex = Integer.parseInt(targetKeyIndex);
	}
	
	/**
	 * Used by digester
	 * 
	 * @param targetKeyField
	 */
	public void createTargetKeyField(String targetKeyField){
		this.targetKeyField = targetKeyField;
	}
	
	/**
	 * Used by digester
	 * 
	 * @param bufferTargetVersionIndex
	 */
	public void createBufferTargetVersionIndex(String bufferTargetVersionIndex){
		this.bufferTargetVersionIndex = Integer.parseInt(bufferTargetVersionIndex);
	}
	
	/**
	 * Used by digester
	 * 
	 * @param targetVersionField
	 */
	public void createTargetVersionField(String targetVersionField){
		this.targetVersionField = targetVersionField;
	}
	
	/**
	 * Used by digester
	 * 
	 * @param bufferTargetStartDateIndex
	 */
	public void createBufferTargetStartDateIndex(String bufferTargetStartDateIndex){
		this.bufferTargetStartDateIndex = Integer.parseInt(bufferTargetStartDateIndex);
	}
	
	/**
	 * Used by digester
	 * 
	 * @param targetStartDateField
	 */
	public void createTargetStartDateField(String targetStartDateField){
		this.targetStartDateField = targetStartDateField;
	}
	
	/**
	 * Used by digester
	 * 
	 * @param bufferTargetStopDateIndex
	 */
	public void createBufferTargetStopDateIndex(String bufferTargetStopDateIndex){
		this.bufferTargetStopDateIndex = Integer.parseInt(bufferTargetStopDateIndex);
	}
	
	/**
	 * Used by digester
	 * 
	 * @param targetStopDateField
	 */
	public void createTargetStopDateField(String targetStopDateField){
		this.targetStopDateField = targetStopDateField;
	}
	
	/**
	 * Used by digester
	 * 
	 * @param bufferTargetActiveIndex
	 */
	public void createBufferTargetActiveIndex(String bufferTargetActiveIndex){
		this.bufferTargetActiveIndex = Integer.parseInt(bufferTargetActiveIndex);
	}
	
	/**
	 * Used by digester
	 * 
	 * @param targetActiveField
	 */
	public void createTargetActiveField(String targetActiveField){
		this.targetActiveField = targetActiveField;
	}
	
	/**
	 * Used by digester
	 * 
	 * @param bufferInputDateIndex
	 */
	public void createBufferInputDateIndex(String bufferInputDateIndex){
		this.bufferInputDateIndex = Integer.parseInt(bufferInputDateIndex);
	}
	
	/**
	 * Used by digester
	 * 
	 * @param inputDateField
	 */
	public void createInputDateField(String inputDateField){
		this.inputDateField = inputDateField;
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
	
	/**
	 * @return the scdType
	 */
	public final int getScdType() {
		return scdType;
	}

	/**
	 * @param scdType the scdType to set
	 */
	public final void setScdType(int scdType) {
		this.scdType = scdType;
	}
	
	/**
	 * @param scdType the scdType to set
	 */
	public final void setScdType(String scdType) {
		this.scdType = Integer.parseInt(scdType);
	}

	/**
	 * @return the sequenceType
	 */
	public int getSequenceType() {
		return sequenceType;
	}

	/**
	 * @param sequenceType the sequenceType to set
	 */
	public void setSequenceType(int sequenceType) {
		this.sequenceType = sequenceType;
	}
	
	/**
	 * @param sequenceType the sequenceType to set
	 */
	public void setSequenceType(String sequenceType) {
		this.sequenceType = Integer.parseInt(sequenceType);
	}

	/**
	 * 
	 * @return return the Target element index for the Row Key
	 */
	public Integer getTargetKeyIndex() {
		if(targetKeyField != null){
			for(int i=0; i<descriptor.getStreamElements().size(); i++){
				if(targetKeyField.equals(descriptor.getStreamElements().get(i).name)){
					return i;
				}
			}
		}
		return null;
	}

	/**
	 * 
	 * set the Target element index for the Row Key
	 */
	public void setTargetKeyIndex(Integer targetKeyIndex) {
		StreamElement element = null;
		try {
			element = descriptor.getStreamElements().get(targetKeyIndex);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(element != null){
			targetKeyField = element.name;
		}
		else {
			targetKeyField = null;
		}
	}

	/**
	 * 
	 * @return return the Target element index for the Row Version
	 */
	public Integer getTargetVersionIndex() {
		if(targetVersionField != null){
			for(int i=0; i<descriptor.getStreamElements().size(); i++){
				if(targetVersionField.equals(descriptor.getStreamElements().get(i).name)){
					return i;
				}
			}
		}
		return null;
	}

	/**
	 *set the Target element index for the Row Version
	 */
	public void setTargetVersionIndex(Integer targetVersionIndex) {
		StreamElement element = null;
		try {
			element = descriptor.getStreamElements().get(targetVersionIndex);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(element != null){
			targetVersionField = element.name;
		}
		else {
			targetVersionField = null;
		}
	}

	/**
	 * 
	 * @return return the Target element index for the Active Field
	 */
	public Integer getTargetActiveIndex() {
		if(targetActiveField != null){
			for(int i=0; i<descriptor.getStreamElements().size(); i++){
				if(targetActiveField.equals(descriptor.getStreamElements().get(i).name)){
					return i;
				}
			}
		}
		return null;
	}

	/**
	 * 
	 * set the Target element index for the Active Field
	 */
	public void setTargetActiveIndex(Integer targetActiveIndex) {
		StreamElement element = null;
		try {
			element = descriptor.getStreamElements().get(targetActiveIndex);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(element != null){
			targetActiveField = element.name;
		}
		else {
			targetActiveField = null;
		}
	}

	/**
	 * @return the targetStartDateIndex
	 */
	public Integer getTargetStartDateIndex() {
		if(targetStartDateField != null){
			for(int i=0; i<descriptor.getStreamElements().size(); i++){
				if(targetStartDateField.equals(descriptor.getStreamElements().get(i).name)){
					return i;
				}
			}
		}
		return null;
	}

	/**
	 * @param targetStartDateIndex the targetStartDateIndex to set
	 */
	public void setTargetStartDateIndex(Integer targetStartDateIndex) {
		StreamElement element = null;
		try {
			element = descriptor.getStreamElements().get(targetStartDateIndex);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(element != null){
			targetStartDateField = element.name;
		}
		else {
			targetStartDateField = null;
		}
	}


	/**
	 * @return the targetStopDateIndex
	 */
	public Integer getTargetStopDateIndex() {
		if(targetStopDateField != null){
			for(int i=0; i<descriptor.getStreamElements().size(); i++){
				if(targetStopDateField.equals(descriptor.getStreamElements().get(i).name)){
					return i;
				}
			}
		}
		return null;
	}

	/**
	 * @param targetStopDateIndex the targetStopDateIndex to set
	 */
	public void setTargetStopDateIndex(Integer targetStopDateIndex) {
		StreamElement element = null;
		try {
			element = descriptor.getStreamElements().get(targetStopDateIndex);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(element != null){
			targetStopDateField = element.name;
		}
		else {
			targetStopDateField = null;
		}
	}

	/**
	 * 
	 * @return return the Target element index for the Date Field
	 */
	public Integer getInputDateField() {
		if(inputDateField != null){
			StreamDescriptor transfoDescriptor = null;
			try {
				transfoDescriptor = inputs.get(0).getDescriptor(this);
			} catch (Exception e) {
				e.printStackTrace();
			}
		
			if(transfoDescriptor != null){
				for(int i=0; i<transfoDescriptor.getStreamElements().size(); i++){
					if(inputDateField.equals(transfoDescriptor.getStreamElements().get(i).name)){
						return i;
					}
				}
			}
		
		}
		return null;
	}

	/**
	 * 
	 * set the Input element index for the Date Field
	 */
	public void setInputDateField(Integer targetDateField) {
		StreamDescriptor transfoDescriptor = null;
		try {
			transfoDescriptor = inputs.get(0).getDescriptor(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		if(transfoDescriptor != null){
			StreamElement element = transfoDescriptor.getStreamElements().get(targetDateField);
			
			if(element != null){
				this.inputDateField = element.name;
			}
			else {
				this.inputDateField = null;
			}
		}
	}

	/**
	 * 
	 * @return return active Value for the Active Field
	 */
	public String getActiveValue() {
		return activeValue;
	}

	/**
	 * 
	 * set the active Value for the Active Field
	 */
	public void setActiveValue(String activeValue) {
		this.activeValue = activeValue;
	}

	/**
	 * 
	 * @return the inactive Value for the Active Field
	 */
	public String getInactiveValue() {
		return inactiveValue;
	}

	/**
	 * 
	 * set the inactive Value for the Active Field
	 */
	public void setInactiveValue(String inactiveValue) {
		this.inactiveValue = inactiveValue;
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
		Element e = DocumentHelper.createElement("slowChangingDimension2");
		e.addElement("name").setText(name);
		e.addElement("description").setText(description);
		
		
		e.addElement("positionX").setText("" + x);
		e.addElement("positionY").setText("" + y);
		
		e.addElement("temporaryFileName").setText(getTemporaryFilename());
		e.addElement("temporarySeparator").setText(getTemporarySpliterChar() + "");
		e.addElement("sequenceType").setText(getSequenceType() + "");
		e.addElement("maxYear").setText(maxYear + "");
		e.addElement("inactiveVal").setText(inactiveValue);
		e.addElement("activeVal").setText(activeValue);
		
		e.addElement("scdType").setText(scdType + "");
		
		if (targetServer != null){
			e.addElement("serverRef").setText(targetServer.getName());
		}
		
		if (targetDefinitionSql != null){
			e.addElement("definition").setText(targetDefinitionSql);
		}
		
		
		if (inputDateField != null){
			e.addElement("inputDateField").setText(inputDateField + "");
		}
		
		if (targetKeyField != null){
			e.addElement("targetKeyField").setText(targetKeyField + "");
		}
		
		if (targetStartDateField != null){
			e.addElement("targetStartDateField").setText(targetStartDateField + "");
		}
		
		
		if (targetStopDateField != null){
			e.addElement("targetStopDateField").setText(targetStopDateField + "");
		}
		
		if (targetVersionField != null){
			e.addElement("targetVersionField").setText(targetVersionField + "");
		}
		
		if (targetActiveField != null){
			e.addElement("targetActiveField").setText(targetActiveField + "");
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
		
		if (getTrashTransformation() != null){
			e.addElement("trashTransformation").setText(errorHandlerTransformation.getName());
		}
		
		if (descriptor != null){
			e.add(descriptor.getElement());
		}
		
		return e;
	}
	
	@Override
	public RuntimeStep getExecutioner(IRepositoryContext repositoryCtx, int bufferSize) {
		if (scdType == SCD_TYPE1){
			return new RunSCD1(this, bufferSize);
		}
		else{
			return new SCD2Runtime(this, bufferSize);
		}
		
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
				createKeyMapping(p.x, p.y);
			}
				
			bufferKeys = null;
		}
		else if(bufferKeyName != null){
			for(String input : bufferKeyName.keySet()){
				createKeyMapping(input, bufferKeyName.get(input));
			}
			
			bufferKeyName = null;
		}
		
		if (bufferFields != null){
			for(Point p : bufferFields){
				createFieldMapping(p.x, p.y);
			}
				
			bufferFields = null;
		}
		else if(bufferFieldName != null){
			for(String input : bufferFieldName.keySet()){
				createFieldMapping(input, bufferFieldName.get(input));
			}
			
			bufferFieldName = null;
		}
		
		if(bufferTargetKeyIndex != null){
			setTargetKeyIndex(bufferTargetKeyIndex);
			bufferTargetKeyIndex = null;
		}
		if(bufferTargetVersionIndex != null){
			setTargetKeyIndex(bufferTargetVersionIndex);
			bufferTargetVersionIndex = null;
		}
		if(bufferTargetStartDateIndex != null){
			setTargetKeyIndex(bufferTargetStartDateIndex);
			bufferTargetStartDateIndex = null;
		}
		if(bufferTargetStopDateIndex != null){
			setTargetKeyIndex(bufferTargetStopDateIndex);
			bufferTargetStopDateIndex = null;
		}
		if(bufferTargetActiveIndex != null){
			setTargetKeyIndex(bufferTargetActiveIndex);
			bufferTargetActiveIndex = null;
		}
		if(bufferInputDateIndex != null){
			setTargetKeyIndex(bufferInputDateIndex);
			bufferInputDateIndex = null;
		}
		
		for(Transformation  t : getOutputs()){
			t.refreshDescriptor();
		}

	}

	public Transformation copy() {
		SlowChangingDimension2 copy = new SlowChangingDimension2();
		
		copy.setDefinition(getDefinition());
		copy.setDescription(description);
		copy.setName("copy of " + name);
		return copy;
	}

	public String getDefinition() {
		return targetDefinitionSql;
	}

	

	public void setDefinition(String definition) {
		this.targetDefinitionSql = definition;
		if (definition.contains(" from ")){
			targetTableName = definition.substring(definition.indexOf(" from ") + 6).trim();
		}
		else if (definition.contains(" FROM ")){
			targetTableName = definition.substring(definition.indexOf(" FROM ") + 6).trim();
		}
		
		
		/*
		 * clean all informations because input has changed
		 */
		mappingFieldNames = new HashMap<String, String>();
		mappingKeyNames = new HashMap<String, String>();
		
		targetActiveField = null;
		targetStartDateField = null;
		targetStopDateField = null;
		targetVersionField = null;
		targetKeyField = null;
		
		
		refreshDescriptor();
		fireProperty(PROPERTY_INPUT_CHANGED);

	}


	@Override
	public void removeInput(Transformation transfo) {
		/*
		 * clean all informations because input has changed
		 */
		mappingFieldNames = new HashMap<String, String>();
		mappingKeyNames = new HashMap<String, String>();
		
		inputDateField = null;
		
		super.removeInput(transfo);
	}
	
	public Integer getMappingFieldNumber() {
		return mappingFieldNames.size();
	}

	public String getDebugMappingFields() {
		StringBuilder buf = new StringBuilder();
		
		for(String input : mappingFieldNames.keySet()){
			buf.append("mapping : \n");
			buf.append("  inputy " + input + "\n");
			buf.append("  inputx " + mappingFieldNames.get(input) + "\n");
		}
		
		return buf.toString();
	}

	public Integer getInputIndexFieldFortargetIndex(int colNum) {
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
						if(output.split("::")[1].equals(transfoDescriptor.getStreamElements().get(i).name)){
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
	
	public Integer getInputIndexKeyFortargetIndex(int colNum) {
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
						if(output.split("::")[1].equals(transfoDescriptor.getStreamElements().get(i).name)){
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

	public Integer getTargetIndexFieldForInputIndex(int colNum) {
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
					if (mappingFieldNames.get(key).split("::")[1].equals(element.name)){
						
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
	
	public Integer getTargetIndexKeyForInputIndex(int colNum) {
		if (mappingKeyNames == null){
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
				for(String key : mappingKeyNames.keySet()){
					if (mappingKeyNames.get(key).split("::")[1].equals(element.name)){
						
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

	public boolean isTruncate() {
		return false;
	}

	public void setTruncate(boolean value) {
				
	}

	public Integer getMaximumYear() {
		return maxYear;
	}

	public void setMaximumYear(int i) {
		this.maxYear = i;
	}
	
	public void setMaximumYear(String maxYear) {
		try {
			this.maxYear = Integer.parseInt(maxYear);
		} catch (Exception e) { }
	}
	
	public Transformation getTrashTransformation() {
		if (trashName != null && getDocument() != null){
			errorHandlerTransformation = getDocument().getTransformation(trashName);
			trashName = null;
		}
		return errorHandlerTransformation;
	}

	public void setTrashTransformation(Transformation transfo) {
		this.errorHandlerTransformation = transfo;
	}
	
	public void setTrashTransformation(String name){
		this.trashName = name;
	}

	@Override
	public void addOutput(Transformation stream) {
		if (!outputs.contains(stream)){
			outputs.add(stream);
			if (trashName != null && trashName.equals(stream.getName())){
				setTrashTransformation(stream);
				trashName = null;
			}
		}
	}
	
	public String getAutoDocumentationDetails() {
		StringBuffer buf = new StringBuffer();
		buf.append("DataBaseServer : " + getServer().getName() + "\n");
		
		buf.append("Input Date Field : " + inputDateField + "\n");
		buf.append("Target Table : " + targetTableName + "\n");
		buf.append("Target Active Field : " + targetActiveField + "\n");
		buf.append("Target Key Field : " + targetKeyField + "\n");
		buf.append("Target StartDate Field : " + targetStartDateField + "\n");
		buf.append("Target StopDate Field : " + targetStopDateField + "\n");
		buf.append("Target StopDate Field : " + targetStopDateField + "\n");
		
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
			transfoDescriptor = inputs.get(0).getDescriptor(this);
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
