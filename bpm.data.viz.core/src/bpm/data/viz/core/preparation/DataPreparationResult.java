package bpm.data.viz.core.preparation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import bpm.vanilla.platform.core.beans.data.DataColumn;

public class DataPreparationResult implements Serializable {

	private static final long serialVersionUID = 1L;

	private DataPreparation preparation;

	private List<Map<DataColumn, Serializable>> values = new ArrayList<>();

	public DataPreparation getPreparation() {
		return preparation;
	}

	public void setPreparation(DataPreparation preparation) {
		this.preparation = preparation;
	}

	public List<Map<DataColumn, Serializable>> getValues() {
		return values;
	}

	public void setValues(List<Map<DataColumn, Serializable>> values) {
		this.values = values;
	}

}
