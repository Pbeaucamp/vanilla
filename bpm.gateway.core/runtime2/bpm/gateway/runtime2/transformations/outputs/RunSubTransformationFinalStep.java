package bpm.gateway.runtime2.transformations.outputs;

import bpm.gateway.core.transformations.outputs.SubTransformationFinalStep;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.internal.Row;
import bpm.gateway.runtime2.internal.RowFactory;

public class RunSubTransformationFinalStep extends RuntimeStep{

	public RunSubTransformationFinalStep(SubTransformationFinalStep transformation,	int bufferSize) {
		super(null, transformation, bufferSize);
	}

	@Override
	public void init(Object adapter) throws Exception {
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
			try {
				Thread.sleep(10);
			}
			catch (InterruptedException e) {
				Thread.currentThread().interrupt(); // restore interrupted status
			}
			return;
		}
		
		Row row = readRow();
		
		Row newRow = RowFactory.createRow(this);
		for(int i = 0; i < newRow.getMeta().getSize(); i++){
			newRow.set(i, row.get(i));
		}
		
		writeRow(newRow);
		
	}

	@Override
	public void releaseResources() {
		
		
	}

}
