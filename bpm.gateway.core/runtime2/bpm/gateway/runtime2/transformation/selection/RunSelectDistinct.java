package bpm.gateway.runtime2.transformation.selection;

import java.util.ArrayList;
import java.util.List;

import bpm.gateway.core.transformations.SelectDistinct;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.StepEndedException;
import bpm.gateway.runtime2.internal.Row;

public class RunSelectDistinct extends RuntimeStep{
	
	private List<Row> writedRows = new ArrayList<Row>();
	
	public RunSelectDistinct(SelectDistinct transformation, int bufferSize) {
		super(null, transformation, bufferSize);

	}

	@Override
	public void init(Object adapter) throws Exception {
		info(" is inited");;
		isInited = true;
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
		
		Row row = null;
		
		try{
			row = readRow();
		}catch(StepEndedException ex){
			return;
		}
		
		if (row == null){
			return;
		}
		
		
		if (!hasBeenWriten(row)){
			writeRow(row);
			writedRows.add(row);
		}
	}

	private boolean hasBeenWriten(Row row){
		if (writedRows.isEmpty()){
			return false;
		}
		for(Row r : writedRows){
			boolean match = true;
			for(int i = 0; i < r.getMeta().getSize(); i++){
				if (r.get(i) == null && row.get(i) == null){
					continue;
				}
				if (r.get(i).equals(row.get(i))){
					continue;
				}
				match = false;
				break;
			}
			if (match){
				return true;
			}
			
		}
		
		return false;
	}
	
	
	@Override
	public void releaseResources() {
		writedRows.clear();
		writedRows = null;
		info(" resources released");
		
	}

}
