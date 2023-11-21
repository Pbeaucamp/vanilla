package bpm.gateway.core.transformations;

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
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.transformation.mapping.RunMapping;
import bpm.vanilla.platform.core.IRepositoryContext;

/**
 * 
 * @author LCA
 *
 */
public class SimpleMappingTransformation extends AbstractTransformation implements Transformation {
	
	/**
	 * P.x : input(0)
	 * P.y: input(1)
	 */
	protected List<Point> mappings = new ArrayList<Point>();
	protected HashMap<String, String> mappingNames = new HashMap<String, String>();
	
	/**
	 * Buffer Mapping for digester
	 */
	protected List<Point> bufferMapping = new ArrayList<Point>();
	protected HashMap<String, String> bufferMappingName = new HashMap<String, String>();

	protected DefaultStreamDescriptor descriptor = new DefaultStreamDescriptor();
	private boolean inputsOrdered = false;

	protected String firstInputName;
	
	private Transformation master;
	protected String  masterName = "";
	
	/**
	 * Used by digester
	 * 
	 * @param inputIndex
	 * @param outputIndex
	 */
	public void createBufferMapping(String inputIndex, String outputIndex){
		if (bufferMapping == null){
			bufferMapping = new ArrayList<Point>();
		}
		
		try{
			Integer x = Integer.parseInt(inputIndex);
			Integer y = Integer.parseInt(outputIndex);
			
			bufferMapping.add(new Point(x, y));
		}catch(NumberFormatException e){
			
		}
	}
	
	/**
	 * Used by digester
	 * 
	 * @param inputCol
	 * @param outputCol
	 */
	public void createBufferMappingName(String inputCol, String outputCol){
		if (bufferMappingName == null){
			bufferMappingName = new HashMap<String,String>();
		}

		bufferMappingName.put(inputCol, outputCol);
	}
	
	public void setFirstInputName(String name){
		firstInputName = name;
	}
	/**
	 * set the Transfo as master 
	 * @param t
	 */
	public void setAsMaster(Transformation t){
		if (inputs.contains(t)){
			master = t;
			masterName = null;
		}
		else if (t == null){
			master = t;
			masterName = null;
		}
		refreshDescriptor();
	}
	
	/**
	 * only for digester
	 * @param number
	 */
	public void setMasterInput(String name){
		masterName = name;	
	}
	
	public boolean isMaster(Transformation t){
		return master == t || (t!= null && t.getName().equals(masterName));
	}
	
	
	/**
	 * specify that inputs are ordered to improve runtime
	 * @param ordered
	 */
	public void setInputsOrdered(boolean ordered){
		this.inputsOrdered = ordered;
	}
	
	public void setInputsOrdered(String ordered){
		this.inputsOrdered = Boolean.parseBoolean(ordered);
	}
	/**
	 * 
	 * @return true if the inputs must be ordered
	 */
	public boolean areInputsOrdered(){
		return inputsOrdered;
	}
	

	/**
	 * map the intColNum from in to the outColNum from out
	 * @param in
	 * @param inColNum
	 * @param out
	 * @param outColNum
	 */
	public void createMapping(int inputColumnNumber,  int outputColumnNumber) throws MappingException{		
		StreamElement inputElement = null;
		StreamElement outputElement = null;
		try {
			if(inputs.get(0) != null){
				inputElement = inputs.get(0).getDescriptor(this).getStreamElements().get(inputColumnNumber);
			}
			if(inputs.get(1) != null){
				outputElement  = inputs.get(1).getDescriptor(this).getStreamElements().get(outputColumnNumber);
			}
		} catch (ServerException e) {
			e.printStackTrace();
		}

		if(inputElement != null && outputElement != null){
			String inputName = inputElement.name;
			String ouputName = outputElement.name;
			
			createMapping(inputName, ouputName);
		}
	}
	
	protected void createMapping(String inputColumn, String outputColumn) throws MappingException{
		if(mappingNames == null){
			mappingNames = new HashMap<String, String>();
		}
		
		/*
		 * verify that this mapping not already exist
		 */
		for(String in : mappingNames.keySet()){
			if(in.equals(inputColumn) && mappingNames.get(in).equals(outputColumn)){
				return;
			}
			
			if(in.equals(inputColumn)){
				throw new MappingException("A mapping already exists for that Input Stream Element");
			}
			
			if (mappingNames.get(in).equals(outputColumn)){
				throw new MappingException("A mapping already exists for that DataBaseOuput Stream Element");
			}
		}
		
		mappingNames.put(inputColumn, outputColumn);
	}
	
	@Override
	public RuntimeStep getExecutioner(IRepositoryContext repositoryCtx, int bufferSize) {
		return new RunMapping(this, bufferSize);
	}


	@Override
	public boolean addInput(Transformation stream) throws Exception{		
		if (!inputs.contains(stream) ){
			if (isInited()){
				if (inputs.size() >= 2 && firstInputName == null){
					throw new Exception("Cannot have more than Two inputs for this Transformation");
				}
				
				if (stream.getDescriptor(this) == null){
					throw new Exception("The Transformation " + stream.getName() + " is not yet defined");
				}
				
				
				if (firstInputName != null ){
					
					if (firstInputName.equals(stream.getName())){
						
						if (inputs.isEmpty()){
							inputs.add(stream);
						}
						else if (inputs.get(0) == null){
							inputs.set(0, stream);
						}
						else{
							if (inputs.size() == 1){
								inputs.add(inputs.get(0));
								inputs.set(0, stream);
							}
							else{
								inputs.add(stream);
							}
						}
						
					}
					else{
						if (inputs.isEmpty()){
							inputs.add(null);
							inputs.add(stream);
						}else {
							inputs.add(stream);
						}
					}
					return true;	
					
				}
			}

			boolean result = super.addInput(stream);

			if (result== false){
				return result;
			}
			
			if (masterName == null || masterName.equals("")){
				masterName = stream.getName();
			}
			
			refreshDescriptor();
			return result;
		}
		
		return false;
	}
	



	@Override
	public Element getElement() {
		Element e = DocumentHelper.createElement("simpleMappingTransformation");
		e.addElement("name").setText(name);
		e.addElement("description").setText(description);
		
		
		e.addElement("positionX").setText("" + x);
		e.addElement("positionY").setText("" + y);
		e.addElement("inputOrdered").setText("" + inputsOrdered);
		
		e.addElement("temporaryFileName").setText(getTemporaryFilename());
	
		if (getContainer() != null){
			e.addElement("container-ref").setText(getContainer());
		}
		
		e.addElement("temporarySeparator").setText(getTemporarySpliterChar() + "");
		
		/*
		 * for the mapping
		 */
		for(String input : mappingNames.keySet()) {
			Element transfoMap = e.addElement("inputMappingName");
			transfoMap.addElement("inputName").setText(input);
			transfoMap.addElement("outputName").setText(mappingNames.get(input));
		}
		
		if (!getInputs().isEmpty() && getInputs().get(0) != null){
			e.addElement("firstInput").setText(getInputs().get(0).getName());
		}
		
		if (master != null){
			e.addElement("masterName").setText(master.getName());
		}
		else{
			if (masterName != null){
				e.addElement("masterName").setText(masterName + "");
			}
		}
		
		
		if (descriptor != null){
			e.add(descriptor.getElement());
		}
		
		return e;
	}

	@Override
	public void removeInput(Transformation transfo) {
		if (inputs.contains(transfo)){
			mappingNames = new HashMap<String, String>();
			
			if (transfo.getName().equals(masterName)){
				setAsMaster(null);
			}
			
			if (transfo.getName().equals(firstInputName)){
				firstInputName = null;
			}
			
			super.removeInput(transfo);
			refreshDescriptor();
		}
	}

	@Override
	public StreamDescriptor getDescriptor(Transformation transfo) throws ServerException {
		if (isInited() && (descriptor == null || descriptor.getColumnCount() == 0)){
			refreshDescriptor();
		}
		return descriptor;
	}


	@Override
	public void refreshDescriptor() {
		descriptor = new DefaultStreamDescriptor();
		if (!isInited()){
			return;
		}
		
//		if (getInputs().size() > 1){			
//			descriptor = new DefaultStreamDescriptor();
//			
//			if (bufferMapping != null){
//				for(Point p : bufferMapping){
//					try{
//						createMapping(p.x, p.y);
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//					
//				bufferMapping = null;
//			}
//			else if(bufferMappingName != null){
//				for(String input : bufferMappingName.keySet()){
//					try {
//						createMapping(input, bufferMappingName.get(input));
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//				
//				bufferMappingName = null;
//			}
//			
//			try{
//				for(StreamElement e : getInputs().get(0).getDescriptor().getStreamElements()){
//					descriptor.addColumn(e.clone(getName(), getInputs().get(0).getName()));
//				}
//				
//				for(StreamElement e : getInputs().get(1).getDescriptor().getStreamElements()){
//					descriptor.addColumn(e.clone(getName(), getInputs().get(1).getName()));
//				}
//				if (firstInputName != null){
//					return;
//				}
//				
//				//TODO: SVI Remove with names mapping not point
//				List<Point> remove = new ArrayList<Point>();
//				for(Point p : mappings){
//					if (isInited()){
//						if (getInputs().get(0) != null && p.x >= getInputs().get(0).getDescriptor().getColumnCount()){
//							p.x = -1;
//						}
//						if (getInputs().get(1) != null && p.y >= getInputs().get(1).getDescriptor().getColumnCount()){
//							p.y = -1;
//						}
//					}
//					
//					if (p.x == -1 && p.y == -1){
//						remove.add(p);
//					}
//				}
//				
//				mappings.removeAll(remove);
//				Activator.getLogger().debug("MAPPER REMOVE POINTS " + remove.size());
//			}catch(Exception e){
//				
//			}
//		}
//
//		for(Transformation  t : getOutputs()){
//			t.refreshDescriptor();
//		}
		try{
			Transformation master = null;
			Transformation lookup = null;
			
			for(Transformation t : getInputs()){
				if (isMaster(t)){
					master = t;
					
				}
				else{
					lookup = t;
				}
			}
			
			if (master == null && getInputs().size() == 2){
				if (getInputs().get(0) == lookup){
					master = getInputs().get(1); 
				}
				else{
					master = getInputs().get(0);
				}
			}
			
			
			if (master != null){
				for(StreamElement e : master.getDescriptor(this).getStreamElements()){
					descriptor.addColumn(e.clone(getName(), master.getName()));
				}
			}
			
			if (lookup != null){
				for(StreamElement e : lookup.getDescriptor(this).getStreamElements()){
					descriptor.addColumn(e.clone(getName(), lookup.getName()));
				}
			}
			
			if (bufferMapping != null){
				for(Point p : bufferMapping){
					createMapping(p.x, p.y);
				}
					
				bufferMapping = null;
			}
			else if(bufferMappingName != null){
				for(String input : bufferMappingName.keySet()){
					createMapping(input, bufferMappingName.get(input));
				}
				
				bufferMappingName = null;
			}
			
			for(Transformation  t : getOutputs()){
				t.refreshDescriptor();
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}


	public void deleteMapping(int inputColumnNumber,  int outputColumnNumber) {
		StreamElement inputElement = null;
		StreamElement outputElement = null;
		try {
			if(inputs.get(0) != null){
				inputElement = inputs.get(0).getDescriptor(this).getStreamElements().get(inputColumnNumber);
			}
			if(inputs.get(1) != null){
				outputElement  = inputs.get(1).getDescriptor(this).getStreamElements().get(outputColumnNumber);
			}
		} catch (ServerException e) {
			e.printStackTrace();
		}

		if(inputElement != null && outputElement != null){
			String inputName = inputElement.name;
			String ouputName = outputElement.name;
		
			deleteMapping(inputName, ouputName);
		}
	}
	
	/**
	 * Remove the specified colonne from the mapping
	 * 
	 * @param input
	 * @param transfoColNum
	 * @param colNum
	 */
	public void deleteMapping(String inputCol, String outputCol){
		for(String input : mappingNames.keySet()){
			if (input.equals(inputCol) && mappingNames.get(input).equals(outputCol)){
				mappingNames.remove(input);
				return;
			}
		}
	}

	@Override
	public void initDescriptor() {
		if (firstInputName != null){
			try{
				if (!getInputs().get(0).getName().equals(firstInputName)){
					Transformation t1  = getInputs().get(0);
					Transformation t2  = getInputs().get(1);
					inputs.set(0, t2);
					inputs.set(1, t1);
					
					if (inputs.get(0) != null && inputs.get(1) != null){
						firstInputName = null;
					}
				}
			}catch(Exception e){
				
			}
			
			for(Transformation t : inputs){
				if (t.getName().equals(masterName)){
					setAsMaster(t);
					break;
				}
			}
			
			setInited();
			refreshDescriptor();
		}
		//this make sure that this just has been created
		else if (getInputs().isEmpty() && mappings.isEmpty() && mappingNames.isEmpty()){
			setInited();	
		}
	}
	
	public Transformation copy() {
		SimpleMappingTransformation copy = new SimpleMappingTransformation();
		copy.setInited();
		
		copy.setDescription(description);
		copy.setName("copy of " + name);

		return copy;
	}

	public HashMap<String, String> getMappings(){
		return new HashMap<String, String>(mappingNames);
	}

	public List<Integer> getInputMap() {
		List<Integer> l = new ArrayList<Integer>();
		for(Point p : mappings){
			l.add(p.x);
		}
		return l;
	}
	
	/**
	 * 
	 * Test for the input column
	 * 
	 * @param t
	 * @param colName
	 * @return
	 */
	public boolean isInputMapped(String colName){
		if(mappingNames.get(colName) != null && !mappingNames.get(colName).isEmpty()){
			return true;
		}
		return false;
	}
	
	public List<Integer> getOutputMap() {
		List<Integer> l = new ArrayList<Integer>();
		for(Point p : mappings){
			l.add(p.y);
		}
		return l;
	}
	
	/**
	 * 
	 * Test for the output column
	 * 
	 * @param colName
	 * @return
	 */
	public boolean isOutputMapped(String colName){
		for(String input : mappingNames.keySet()){
			if(mappingNames.get(input).equals(colName)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 
	 * This method allows to get the column map to the input column at index = @param colNum
	 * 
	 * @param colNum
	 * @return
	 */
	public Integer getMappingValueForInputNum(int colNum){
		if (mappingNames == null){
			return null;
		}
		
		StreamElement element = null;
		try {
			element = inputs.get(0).getDescriptor(this).getStreamElements().get(colNum);
		} catch (ServerException e) {
			e.printStackTrace();
			return null;
		}
		
		if(element != null){
			String output = mappingNames.get(element.name);
			
			if(output != null){
				try {
					for(int i=0; i<inputs.get(1).getDescriptor(this).getStreamElements().size(); i++){
						if(output.equals(inputs.get(1).getDescriptor(this).getStreamElements().get(i).name)){
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
	
	/**
	 * 
	 * This method allows to get the column map to the input column with name = @param colName
	 * 
	 * @param colName
	 * @return
	 */
	public Integer getMappingValueForInputNum(String colName){
		if (mappingNames == null){
			return null;
		}
		
		StreamElement element = null;
		try {
			for(StreamElement el : inputs.get(0).getDescriptor(this).getStreamElements()){
				if(el.name.equals(colName)){
					element = el;
					break;
				}
			}
		} catch (ServerException e) {
			e.printStackTrace();
			return null;
		}
		
		if(element != null){
			String output = mappingNames.get(element.name);
			
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
	
	/**
	 * 
	 * This method allows to get the column map to the output column at index = @param colNum
	 * 
	 * @param colNum
	 * @return
	 */
	public Integer getMappingValueForThisNum(int colNum){
		if (mappingNames == null){
			return null;
		}
		
		StreamElement element = null;
		try {
			element = inputs.get(1).getDescriptor(this).getStreamElements().get(colNum);
		} catch (ServerException e) {
			e.printStackTrace();
			return null;
		}
		
		if(element != null){
			for(String key : mappingNames.keySet()){
				if (mappingNames.get(key).equals(element.name)){
					
					StreamDescriptor transfoDescriptor = null;
					try {
						transfoDescriptor = inputs.get(0).getDescriptor(this);
					} catch (Exception e) {
						
					}
					
					if(transfoDescriptor != null){
						for(int i=0; i<transfoDescriptor.getStreamElements().size(); i++){
							if(key.equals(transfoDescriptor.getStreamElements().get(i).name)){
								return i;
							}
						}
					}
				}
			}
		}
		
		return null;
	}
	
	/**
	 * 
	 * This method allows to get the column map to the output column with name = @param colName
	 * 
	 * @param colNum
	 * @return
	 */
	public Integer getMappingValueForThisNum(String colName){
		if (mappingNames == null){
			return null;
		}
		
		StreamElement element = null;
		try {
			for(StreamElement el : inputs.get(1).getDescriptor(this).getStreamElements()){
				if(el.name.equals(colName)){
					element = el;
					break;
				}
			}
		} catch (ServerException e) {
			e.printStackTrace();
			return null;
		}
		
		if(element != null){
			for(String key : mappingNames.keySet()){
				if (mappingNames.get(key).equals(element.name)){
					
					StreamDescriptor transfoDescriptor = null;
					try {
						transfoDescriptor = inputs.get(0).getDescriptor(this);
					} catch (Exception e) {
						
					}
					
					if(transfoDescriptor != null){
						for(int i=0; i<transfoDescriptor.getStreamElements().size(); i++){
							if(key.equals(transfoDescriptor.getStreamElements().get(i).name)){
								return i;
							}
						}
					}
				}
			}
		}
		
		return null;
	}
	
	public String getAutoDocumentationDetails() {
		StringBuffer buf = new StringBuffer();
		buf.append("Inputs Ordered :" + inputsOrdered + "\n");
		buf.append("Mappings :\n");
		for(Integer i = 0; i < getInputMap().size(); i++){
			try{
				buf.append("\t- " + getInputs().get(0).getDescriptor(this).getColumnName(i) + " mapped with " + getInputs().get(1).getDescriptor(this).getColumnName(getInputMap().get(i)) +  "\n");
			}catch(Exception ex){
				buf.append("\t- Field number " + i + "\n");
			}
		}
		
		return buf.toString();
	}
}
