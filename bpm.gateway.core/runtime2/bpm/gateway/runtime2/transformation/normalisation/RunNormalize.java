package bpm.gateway.runtime2.transformation.normalisation;

import java.util.ArrayList;
import java.util.List;

import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.transformations.Normalize;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.internal.Row;
import bpm.gateway.runtime2.internal.RowFactory;

public class RunNormalize extends RuntimeStep{
	
	private List<Integer> levelsInputIndex;
	private List<Row> writedRows = new ArrayList<Row>();
	
	public RunNormalize(Normalize transformation, int bufferSize) {
		super(null, transformation, bufferSize);
		
	}

	@Override
	public void init(Object adapter) throws Exception {
		Normalize norm = (Normalize)getTransformation();
		
		levelsInputIndex = norm.getLevelsIndex();
		
		/*
		 * check if the levels have been rightly defined
		 */
		
		if (norm.getInputs().isEmpty()){
			throw new Exception(" Normalize step needs an Input");
		}
		StreamDescriptor inputDesc = norm.getInputs().get(0).getDescriptor(norm);
		
		for(int i = 0; i < levelsInputIndex.size(); i++){
			if (levelsInputIndex.get(i) == -1 || levelsInputIndex.get(i) == null){
				throw new Exception("Level " + i + " have not been defined");
			}
			
			if (levelsInputIndex.get(i) >= inputDesc.getColumnCount()){
				throw new Exception("The Level " + i + " is defined on Field that do not exists in its input (fieldIndex=" + levelsInputIndex.get(i) + " > input Number of Fields = " + inputDesc.getColumnCount() + ")");
			}
			
			
		}
		
		isInited = true;
		info(" inited");
		
	}

	@Override
	public void performRow() throws Exception {
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
		
		Row newRow = RowFactory.createRow(this);
		
		for(int i = 0; i < levelsInputIndex.size(); i++){
			newRow.set(i, row.get(levelsInputIndex.get(i)));
		}
		
		if (checkRow(newRow)){
			writedRows.add(newRow);
			writeRow(newRow);
		}
		
	}

	
	/**
	 * 
	 * @param row
	 * @return true if writed rows do not contains a row with the same values
	 */
	private boolean checkRow(Row row){
		for(Row r : writedRows){
			if (r.equals(row)){
				return false;
			}
		}
		return true;
	}
	
	@Override
	public void releaseResources() {
		writedRows.clear();
		writedRows = null;
		info(" resources released");
	}

}
