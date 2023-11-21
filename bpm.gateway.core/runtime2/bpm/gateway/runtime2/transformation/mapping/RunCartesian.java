package bpm.gateway.runtime2.transformation.mapping;

import java.util.ArrayList;
import java.util.List;

import bpm.gateway.core.transformations.CartesianProduct;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.internal.Row;
import bpm.gateway.runtime2.internal.RowFactory;

public class RunCartesian extends RuntimeStep{
	private List<Row> secondaryInputDatas = new ArrayList<Row>();
	private boolean isSecondaryRowFilled = false;
	private RuntimeStep primaryStep;
	private RuntimeStep secondaryStep;
	
	public RunCartesian(CartesianProduct transformation, int bufferSize) {
		super(null, transformation, bufferSize);
		
	}

	@Override
	public void init(Object adapter) throws Exception {
		try{
			
			if (inputs.size() != 2){
				throw new Exception("Cartesian Product need 2 inputs to be run");
			}
			
			for(RuntimeStep r : inputs){
				if (r.getTransformation() == getTransformation().getInputs().get(0)){
					primaryStep = r;
				}
				else if (r.getTransformation() == getTransformation().getInputs().get(1)){
					secondaryStep = r;
				}
			}
			
			

		}catch(Exception e){
			throw new Exception("CartesianProduct must have 2 Input Streams", e);
		}
		
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
			Row newRow = RowFactory.createRow(this);
			for(int i = 0; i < row.getMeta().getSize(); i++){
				newRow.set(i, row.get(i));
			}
			for(int i = row.getMeta().getSize(); i < _row.getMeta().getSize() + row.getMeta().getSize(); i++){
				newRow.set(i, _row.get(i - row.getMeta().getSize() ));
			}
			
			
			writeRow(newRow);

		}
		
		
		
		
	}
	
	
	@Override
	public void insertRow(final Row data, final RuntimeStep caller) throws InterruptedException {
		
		if (isSecondaryRowFilled == false && secondaryStep.isEnd() && secondaryStep.inputEmpty()){
			isSecondaryRowFilled = true;
			info(" secondary Stream is fully read " + secondaryInputDatas.size() + "rows");
		}
		
		if (caller == secondaryStep){
			secondaryInputDatas.add(data);
		}
		else{
			super.insertRow(data, caller);
		}
		
	}
	

	@Override
	public void releaseResources() {
		secondaryInputDatas.clear();
		secondaryInputDatas = null;
		info("Resources released");
		
	}

}
