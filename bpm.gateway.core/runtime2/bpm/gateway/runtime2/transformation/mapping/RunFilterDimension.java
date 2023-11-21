package bpm.gateway.runtime2.transformation.mapping;

import java.util.ArrayList;
import java.util.List;

import bpm.gateway.core.transformations.FilterDimension;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.internal.Row;

public class RunFilterDimension extends RuntimeStep{

	protected List<Row> secondaryInputDatas = new ArrayList<Row>();
	protected boolean isSecondaryRowFilled = false;
	
	protected List<Integer> masterMap;
	protected RuntimeStep primaryStep;
	protected RuntimeStep secondaryStep;
	private RuntimeStep trashOutput;
	
	public RunFilterDimension(FilterDimension transformation, int bufferSize) {
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
		
		FilterDimension tr = (FilterDimension)getTransformation();
		
		
		masterMap = tr.getMappings();
		
		for(RuntimeStep rs : inputs){
			if (rs.getTransformation() == tr.getDimensionValidatorInput()){
				secondaryStep = rs;
			}
			else{
				primaryStep = rs;
			}
		}
		for(RuntimeStep rs : getOutputs()){
			if (rs.getTransformation() == tr.getTrashTransformation()){
				trashOutput = rs;
			}
		}
		
		if (secondaryStep == null){
			throw new Exception("Missing a Dimension Step in input to be run");
		}
		if (primaryStep == null){
			throw new Exception("Missing a non-Dimension Step in input to be run");
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
		
		for(Row _row : secondaryInputDatas){
			
			boolean match = true; 
			for(int i = 0; i < row.getMeta().getSize(); i++){
				
							
				
				if (i >= masterMap.size() || masterMap.get(i) == null){
					continue;
				}
				Object masterValue = row.get(i );
				Object slaveValue = _row.get(masterMap.get(i));
					
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
				getLogger().debug("comparison is TRUE");
				writeRow(row);
				return;
				
			}
		}
		
//		if (write){
//			getLogger().debug("comparison is TRUE");
//			writeRow(row);
//
//		}
//		else{
			trashRow(row);
//		}
		
		
	}

	@Override
	public void releaseResources() {
		secondaryInputDatas.clear();
		secondaryInputDatas = null;
		info(" resources released");
		
	}
	
	private void trashRow(Row row)throws InterruptedException{
		if (trashOutput != null){
			trashOutput.insertRow(row, this);
			writedRows++;
		}
	}

	@Override
	public void insertRow(final Row data, final RuntimeStep caller) throws InterruptedException {
		
		if (isSecondaryRowFilled == false && secondaryStep.isEnd() && secondaryStep.inputEmpty()){
			isSecondaryRowFilled = true;
			info(" secondary Stream is fully read " + secondaryInputDatas.size() + "rows");
		}
		
		if (secondaryStep == caller){
			secondaryInputDatas.add(data);
		}
		else{
			RunFilterDimension.super.insertRow(data, caller);
		}
		
	}
	
	@Override
	protected void writeRow(Row row) throws InterruptedException {
		for(RuntimeStep r : getOutputs()){
			if (r != trashOutput){
				r.insertRow(row, this);
			}
		}
		writedRows++;
	}
}
