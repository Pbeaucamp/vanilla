package bpm.gateway.core.transformations.olap;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.gateway.core.AbstractTransformation;
import bpm.gateway.core.DefaultStreamDescriptor;
import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.StreamElement;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.core.transformations.MappingException;
import bpm.gateway.core.transformations.outputs.IOutput;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.transformation.mapping.RunOlapOutput;
import bpm.vanilla.platform.core.IRepositoryContext;

public class OlapOutput extends AbstractTransformation implements IOlap, IOutput{
	private Integer diretcoryItemId;
	private String cubeName;
	
	private DefaultStreamDescriptor descriptor;
	
	/*
	 * contains the mapping between each input Transformation
	 * 
	 * (We let the mapping with Point for retrocompatibility)
	 */
	private HashMap<Transformation, HashMap<String, String>> mappingNames = new HashMap<Transformation, HashMap<String,String>>();
	
	/*
	 * only for digester
	 */
	private HashMap<String, List<Point> > bufferMapping ;
	
	/*
	 * Only for digester
	 */
	private HashMap<String, HashMap<String, String>> bufferMappingName;
	private String directoryItemName;


	/**
	 * never use
	 * @param transfoName
	 * @param transfoIndex
	 * @param index
	 */
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
			
			bufferMapping.get(k).add(new Point(x,y));
			
		}catch(NumberFormatException e){ }
	}
	
	/**
	 * Used by digester
	 * 
	 * @param transfoName
	 * @param inputCol
	 * @param outputCol
	 */
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
	
	public Integer getDirectoryItemId() {
		return diretcoryItemId;
	}

	public void setDirectoryItemId(Integer diretcoryItemId) {
		this.diretcoryItemId = diretcoryItemId;
		cubeName = null;
		
	}
	
	public void setDirectoryItemId(String itemId){
		try{
			this.diretcoryItemId  = Integer.parseInt(itemId);
		}catch(Exception e){
			
		}
		
	}

	public String getCubeName() {
		return cubeName;
	}

	public void setCubeName(String cubeName) {
		this.cubeName = cubeName;
		try {
			descriptor = (DefaultStreamDescriptor)getDocument().getOlapHelper().getDescriptor(this);
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}

	@Override
	public StreamDescriptor getDescriptor(Transformation transfo) throws ServerException {
		return descriptor;
	}

	@Override
	public Element getElement() {
		Element e = DocumentHelper.createElement("olapOutput");

		e.addElement("name").setText(name);
		e.addElement("description").setText(description);
		
		
		e.addElement("positionX").setText("" + x);
		e.addElement("positionY").setText("" + y);
		
		e.addElement("temporaryFileName").setText(getTemporaryFilename());
		e.addElement("temporarySeparator").setText(getTemporarySpliterChar() + "");
		
		
		if (getDirectoryItemId() != null){
			e.addElement("directoryItemId").setText(getDirectoryItemId() + "");
			e.addElement("directoryItemName").setText(getDirectoryItemName() + "");
		}
		
		if (getCubeName() != null){
			e.addElement("cubeName").setText(getCubeName());
		}
		
		/*
		 * for the mapping
		 */
		for(Transformation t : inputs){
		
			HashMap<String, String> maps = mappingNames.get(t);
			for(String input : maps.keySet()) {
				Element transfoMap = e.addElement("inputMappingName");
				transfoMap.addElement("transformationRef").setText(t.getName());
				
				transfoMap.addElement("inputName").setText(input);
				transfoMap.addElement("outputName").setText(maps.get(input));
			}
		}
		
		if (descriptor != null){
			e.add(descriptor.getElement());
		}
		return e;
	}

	@Override
	public RuntimeStep getExecutioner(IRepositoryContext repositoryCtx, int bufferSize) {
		return new RunOlapOutput(this, bufferSize);
	}
	
	@Override
	public void refreshDescriptor() {
		if (!isInited()){
			return;
		}
		
		try {
			descriptor = (DefaultStreamDescriptor)getDocument().getOlapHelper().getDescriptor(this);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		
		
		//updates the mapping (remove them associated with columns that dont exists anymore
		for(Transformation t : inputs){

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

	public Transformation copy() {
		return null;
	}

	public void setDescriptor(DefaultStreamDescriptor desc) {
		descriptor = desc;
	}

	public void createMapping(Transformation input, int transfoColNum, int colNum) throws MappingException {
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

	public void deleteMapping(Transformation input, int transfoColNum, int colNum) {
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
	public void clearMapping(Transformation input) {
		HashMap<String, String> maps = mappingNames.get(input);
		if(maps != null){
			maps.clear();
		}
	}

	@Override
	public Integer getMappingValueForInputNum(Transformation t, int colNum) {
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
			String output = mappingNames.get(t).get(element.getFullName());
			
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
	public Integer getMappingValueForThisNum(Transformation t, int colNum) {
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

	@Override
	public HashMap<String, String> getMappingsFor(Transformation t) {
		return new HashMap<String, String>(mappingNames.get(t));
	}
	
	@Override
	public boolean isMapped(Transformation t, String colName){
		if(mappingNames.get(t).get(colName) != null && !mappingNames.get(t).get(colName).isEmpty()){
			return true;
		}
		return false;
	}
	
	@Override
	public boolean isMapped(String colName){
		for(Transformation t : inputs){

			HashMap<String, String> maps = mappingNames.get(t);
			for(String key : maps.keySet()){
				if(maps.get(key).equals(colName)){
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public boolean addInput(Transformation stream) throws Exception {
		boolean result = super.addInput(stream);
		if (result == false){
			return result;
		}

		if (mappingNames.get(stream) == null){
			mappingNames.put(stream, new HashMap<String, String>());
		}

		/*
		 *look if the mappingBuffered is not empty 
		 */
		if (bufferMapping != null){
			//look for the key
			String key = null;
			for(String s : bufferMapping.keySet()){
				if (s.equals(stream.getName())){
					key = s;
					break;
				}
			}
			
			if (key == null){
				return result;
			}
			
			for(Point p : bufferMapping.get(key)){
				createMapping(stream, p.x, p.y);
			}
			
			bufferMapping.remove(key);
			
			if (bufferMapping.isEmpty()){
				bufferMapping = null;
			}
			return result;
		}
		else if(bufferMappingName != null){
			//look for the key
			String key = null;
			for(String s : bufferMappingName.keySet()){
				if (s.equals(stream.getName())){
					key = s;
					break;
				}
			}
			
			if (key != null){
				HashMap<String, String> maps = bufferMappingName.get(key);
				for(String input : maps.keySet()){
					try {
						createMapping(stream, input, maps.get(input));
					} catch (MappingException e) {
						e.printStackTrace();
					}
				}
				bufferMappingName.remove(key);
			}
			else {
				return result;
			}
			
			if (bufferMappingName.isEmpty()){
				bufferMappingName = null;
			}
			return result;
		}
		else {
			return result;
		}
	}

	@Override
	public void removeInput(Transformation transfo) {
		super.removeInput(transfo);
		mappingNames.remove(transfo);
	}
	
	public String getAutoDocumentationDetails() {
		StringBuffer buf = new StringBuffer();
		buf.append("DirectoryItem Id : " + diretcoryItemId + "\n");
		buf.append("CubeName : " + cubeName + "\n");
		
		
		//TODO : write maping datas
		return buf.toString();
	}

	@Override
	public String getDirectoryItemName() {
		return directoryItemName;
	}

	@Override
	public void setDirectoryItemName(String name) {
		directoryItemName = name;
	}
}
