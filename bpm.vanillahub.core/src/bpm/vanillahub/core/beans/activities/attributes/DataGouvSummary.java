package bpm.vanillahub.core.beans.activities.attributes;

import java.io.Serializable;
import java.util.List;

public class DataGouvSummary implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int nbResult;
	
	private List<DataGouvDataset> datasets;
	
	public DataGouvSummary() { }
	
	public DataGouvSummary(int nbResult, List<DataGouvDataset> datasets) {
		this.nbResult = nbResult;
		this.datasets = datasets;
	}

	public int getNbResult() {
		return nbResult;
	}
	
	public List<DataGouvDataset> getDatasets() {
		return datasets;
	}
}
