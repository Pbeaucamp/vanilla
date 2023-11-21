package bpm.gateway.runtime2.transformations.inputs;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

import bpm.gateway.core.Transformation;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.internal.Row;
import bpm.gateway.runtime2.internal.RowFactory;
import bpm.vanilla.platform.core.beans.data.DataColumn;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.data.viz.core.IDataVizComponent;
import bpm.data.viz.core.RemoteDataVizComponent;
import bpm.data.viz.core.preparation.DataPreparation;
import bpm.data.viz.core.preparation.DataPreparationResult;

public class RunDataPreparationInput  extends RuntimeStep{

	private int bufferSize;
	private DataPreparation dataPrep;
	private DataPreparationResult result;

	public RunDataPreparationInput(Transformation transformation, int bufferSize){
		super(null, transformation, bufferSize);
		this.bufferSize = bufferSize;
	}

	public RunDataPreparationInput(Transformation transformation, int bufferSize, DataPreparation dataPrep){
		super(null, transformation, bufferSize);
		this.bufferSize = bufferSize;
		this.dataPrep = dataPrep;
	}

	@Override
	public void performRow() throws Exception {
		
		if(result.getValues().size() <= (int) writedRows) {
			setEnd();
			return;
		}
		
		Map<DataColumn, Serializable> map = result.getValues().get((int) writedRows);
		Row row = RowFactory.createRow(this);
		
		Iterator<Serializable> i = map.values().iterator();
		int index=0;
		while(i.hasNext()) {
			row.set(index, i.next());
			index++;
		}
		writeRow(row);
	}

	@Override
	public void releaseResources() {

	}

	@Override
	public void init(Object adapter) throws Exception {
		
		IDataVizComponent datavizRemote = new RemoteDataVizComponent(new RemoteRepositoryApi(getTransformation().getDocument().getRepositoryContext()));
		result = datavizRemote.executeDataPreparation(dataPrep);
	}

}
