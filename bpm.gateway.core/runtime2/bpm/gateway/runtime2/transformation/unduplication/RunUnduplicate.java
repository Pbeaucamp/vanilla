package bpm.gateway.runtime2.transformation.unduplication;

import java.util.ArrayList;
import java.util.List;

import bpm.gateway.core.transformations.UnduplicateRows;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.internal.Row;

public class RunUnduplicate extends RuntimeStep{

	private List<Row> distinctRows = new ArrayList<Row>();
	private List<Integer> fieldsindex ;
	private RuntimeStep trash;
	
	public RunUnduplicate(UnduplicateRows transformation, int bufferSize) {
		super(null, transformation, bufferSize);
	}

	@Override
	public void init(Object adapter) throws Exception {
		UnduplicateRows unduplication = (UnduplicateRows)getTransformation();
		fieldsindex = unduplication.getFields();
		
		for(RuntimeStep rs : getOutputs()){
			if (rs.getTransformation() == unduplication.getTrashTransformation()){
				trash = rs;
				break;
			}
		}
		
		info(" inited");
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
		
		Row r = readRow();
		
		if (r == null){
			return;
		}
		
		writeRow(r);
		
	}

	@Override
	public void releaseResources() {
		distinctRows = null;
		fieldsindex = null;
		info(" resources released");
		
	}

	@Override
	protected void writeRow(Row row) throws InterruptedException {
		boolean match = false;
		for(Row _r : distinctRows){
			match = false;
			for(Integer i : fieldsindex){
				if ((_r.get(i) ==  null && row.get(i) == null) ||
					(_r.get(i) !=  null && _r.get(i).equals(row.get(i))) ||
					(row.get(i) !=  null && row.get(i).equals(_r.get(i))) ){
					match = true;
					continue;
				}
				else{
					match = false;
					break;
				}
			}
			if (match){
				break;
			}
		}
		
		if (! match){
			for(RuntimeStep r : getOutputs()){
				if (r != trash){
					distinctRows.add(row);
					r.insertRow(row, this);
				}
			}
		}
		else if (trash != null){
			trash.insertRow(row, this);
		}
		
		writedRows ++;
	}

	
}
