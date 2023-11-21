package bpm.gateway.runtime2.transformation.mapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bpm.gateway.core.transformations.SimpleMappingTransformation;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.internal.Row;
import bpm.gateway.runtime2.internal.RowFactory;

public class RunMapping extends RuntimeStep{

	protected List<Row> secondaryInputDatas = new ArrayList<Row>();
	protected boolean isSecondaryRowFilled = false;
	
	private Integer[] masterMap;
	protected RuntimeStep primaryStep;
	protected RuntimeStep secondaryStep;
	
	public RunMapping(){}
	
	public RunMapping(SimpleMappingTransformation transformation, int bufferSize) {
		super(null, transformation, bufferSize);
		secondaryInputDatas = new ArrayList<Row>(bufferSize);
	}

	@Override
	public void init(Object adapter) throws Exception {
		if (inputs.size() != 2){
			throw new Exception(getName() + " have less than two inputs");
		}
		else if (inputs.get(0) == null || inputs.get(1) == null){
			throw new Exception(getName() + " one of two requested input is null");
		}
		
		/*
		masterMap = new ArrayList<Integer>();
		if (((SimpleMappingTransformation)getTransformation()).isMaster(inputs.get(0).getTransformation())){
			secondaryStep = inputs.get(1);
			primaryStep = inputs.get(0);
		}
		else{
			secondaryStep = inputs.get(0);
			primaryStep = inputs.get(1);
		}
		
		for(int i = 0; i < primaryStep.getTransformation().getDescriptor().getColumnCount(); i++){
			
			boolean added = false;
			HashMap<String, String> maps = ((SimpleMappingTransformation)getTransformation()).getMappings();
			for(String input : maps.keySet()){
				
				String output = maps.get(input);
				
				if (primaryStep == inputs.get(0)){
					int index = ((SimpleMappingTransformation)getTransformation()).getMappingValueForInputNum(input);
					if (index == i){
						if (output == null || output.isEmpty()){
							String s = " Bad mapping value for column(" + input + ", " + output + ")";
							Exception e = new Exception(getName() + s);
							error(s);
							throw e;
						}
						
						masterMap.add(((SimpleMappingTransformation)getTransformation()).getMappingValueForThisNum(output));
						added = true;
						break;
					}
				}
				else{
					int index = ((SimpleMappingTransformation)getTransformation()).getMappingValueForThisNum(output);
					if (index == i){
						masterMap.add(index);
						added = true;
						break;
					}
				}
			}
			if (!added){
				masterMap.add(null);
			}
		}*/
		/*
		 * read this reckless youngster before making any changes there
		 * 
		 * maps is the mapping between the 2 SimpleMappingTransformation's inputs
		 * It has the following structure key = SimpleMappingTransformation.getInput(0) streamelement name; value = SimpleMappingTransformation.getInput(1) streamelement name
		 * 
		 *  The SimpleMappingTransformation output fields are SimpleMappingTransformation's masterStep fields + SimpleMappingTransformation's other step fields
		 *  The masterStep can be either of the SimpleMappingTransformation's inputs.
		 *  
		 *  The masterMap[]  is efined as follow :
		 *  	- index : the position of the field from the primaryStep
		 *  	- value : the position of the field from the secondaryStep(lookup table) or null
		 *  
		 *  The boolean isPrimaryFirstInput is at true if the primaryStep is the one coming from SimpleTransformation.getInput(0) step definition. 
		 *  For some reason we can't assume that the this.getInputs(0).getTransformation() == this.getTransformation().getInputs().get(0)
		 *  (if you want to kno you will have to dig into the RuntimeStep tree construction) 
		 *  
		 */
		boolean isPrimaryFirstInput = false;
		
		if (((SimpleMappingTransformation)getTransformation()).isMaster(inputs.get(0).getTransformation())){
			secondaryStep = inputs.get(1);
			primaryStep = inputs.get(0);
		}
		else{
			secondaryStep = inputs.get(0);
			primaryStep = inputs.get(1);
		}
		if (getTransformation().getInputs().get(0) == primaryStep.getTransformation()){
			isPrimaryFirstInput = true;
		}
		masterMap = new Integer[primaryStep.getTransformation().getDescriptor(getTransformation()).getColumnCount()];
		HashMap<String, String> maps = ((SimpleMappingTransformation)getTransformation()).getMappings();

		for(String s : maps.keySet()){
			Integer pos = null;
			Integer value = null;
			
			if (maps.get(s) == null){
				continue;
			}
			
			String primaryColName = null;
			String secondaryName = null;
			
			if (isPrimaryFirstInput){
				primaryColName = s;
				secondaryName = maps.get(s);
			}
			else{
				primaryColName = maps.get(s);
				secondaryName = s;
			}
			
			for(int i = 0; i < primaryStep.getTransformation().getDescriptor(getTransformation()).getColumnCount(); i++){
				if (primaryStep.getTransformation().getDescriptor(getTransformation()).getColumnName(i).equals(primaryColName)){
					pos = i;
					break;
				}
			}
			for(int i = 0; i < secondaryStep.getTransformation().getDescriptor(getTransformation()).getColumnCount(); i++){
				if (secondaryStep.getTransformation().getDescriptor(getTransformation()).getColumnName(i).equals(secondaryName)){
					value = i;
					break;
				}
			}
			
			
			if (pos != null && value != null){
				masterMap[pos]= value;
			}
		}
		
		isInited = true;
		info(" inited");
	}

	
	
	
	
	@Override
	public void performRow() throws Exception {
		while(!(secondaryStep.isEnd() && secondaryStep.inputEmpty())){
			try{
				Thread.sleep(50);
			}catch(Exception e){
				
			}
		
		}
		if (areInputStepAllProcessed()){
			if (inputEmpty()){
				setEnd();
			}
		}
		
		if (isEnd() && inputEmpty()){
			return;
		}
		
		if (!isEnd() && inputEmpty()){
			try{
				Thread.sleep(10);
				return;
			}catch(Exception e){
				
			}
		}
				
		Row row = readRow();

		boolean somethingMatched = false;
		
		for(Row _row : secondaryInputDatas){
			boolean match = true; 
			
			for(int i = 0; i < row.getMeta().getSize(); i++){
				
							
				
				if (masterMap[i] == null){
					continue;
				}
				Object masterValue = row.get(i );
				Object slaveValue = _row.get(masterMap[i]);
					
				if ( (masterValue == null && slaveValue == null)|| 
						 (masterValue != null && (slaveValue != null && masterValue.toString().equals(slaveValue.toString())))){
						match = match && true;
					
					}
				else{
					
						match = false;
						break;
				}
			}
			if (match){
				somethingMatched = true;
				getLogger().debug("comparison is TRUE");
//				Row newRow = RowFactory.createRow(this);
//				
//				if (primaryStep.getTransformation() == getTransformation().getInputs().get(0)){
//					for(int i = 0; i < row.getMeta().getSize(); i++){
//						newRow.set(i, row.get(i));
//					}
//					for(int i = row.getMeta().getSize(); i < _row.getMeta().getSize() + row.getMeta().getSize(); i++){
//						newRow.set(i, _row.get(i - row.getMeta().getSize() ));
//					}
//				}
//				else{
//					for(int i = 0; i < _row.getMeta().getSize(); i++){
//						newRow.set(i, _row.get(i));
//					}
//					for(int i = _row.getMeta().getSize(); i < _row.getMeta().getSize() + row.getMeta().getSize(); i++){
//						newRow.set(i, row.get(i - _row.getMeta().getSize() ));
//					}
//				}
				
				Row newRow = RowFactory.createRow(this);
				
				int counter = 0;
				for(int i = 0; i < row.getMeta().getSize(); i++){
					newRow.set(counter++, row.get(i));
				}
				for(int i = 0; i < _row.getMeta().getSize(); i++){
					newRow.set(counter++, _row.get(i));
				}
				writeRow(newRow);
//				return;
			}
			
			
		}
		
		if(!somethingMatched) {
			Row newRow = RowFactory.createRow(this);
			
			int counter = 0;
			for(int i = 0; i < row.getMeta().getSize(); i++){
				newRow.set(counter++, row.get(i));
			}
			writeRow(newRow);
		}
		
	}

	@Override
	public void releaseResources() {
		secondaryInputDatas.clear();
		secondaryInputDatas = null;
		info(" resources released");
		
	}

	@Override
	public void insertRow(final Row data, final RuntimeStep caller) throws InterruptedException {
		SimpleMappingTransformation smf = (SimpleMappingTransformation)getTransformation();
		if (isSecondaryRowFilled == false && secondaryStep.isEnd() && secondaryStep.inputEmpty()){
			isSecondaryRowFilled = true;
			info(" secondary Stream is fully read " + secondaryInputDatas.size() + "rows");
		}
		
		if (!smf.isMaster(caller.getTransformation())){
			secondaryInputDatas.add(data);
		}
		else{
			RunMapping.super.insertRow(data, caller);
		}
		
	}
}
