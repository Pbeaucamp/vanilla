package bpm.metadata.lightweight;

import java.util.HashMap;
import java.util.List;

import bpm.metadata.layer.logical.IDataStreamElement;
import bpm.metadata.layer.physical.IColumn;

public class LightWeightDataStreamElement extends IDataStreamElement{

	@Override
	public List<String> getDistinctValues() throws Exception {
		return null;
	}

	@Override
	public String getJavaClassName() throws Exception {
		return null;
	}

	@Override
	/**
	 * @return null
	 */
	public IColumn getOrigin() {
		return null;
	}

	@Override
	public boolean isCalculated() {
		return false;
	}

	@Override
	public List<String> getDistinctValues(HashMap<String, String> parentValues) throws Exception {
		// not implemented
		return null;
	}

}
