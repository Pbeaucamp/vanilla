package bpm.odata.service.data;

import bpm.metadata.layer.business.IBusinessPackage;
import bpm.metadata.query.SavedQuery;

public class MetadataDataset {

	private IBusinessPackage pack;
	private SavedQuery dataset;
	
	public MetadataDataset(IBusinessPackage pack, SavedQuery dataset) {
		this.pack = pack;
		this.dataset = dataset;
	}
	
	public String getDatasetName() {
		return dataset.getName();
	}
	
	public IBusinessPackage getPack() {
		return pack;
	}
	
	public SavedQuery getDataset() {
		return dataset;
	}
}
