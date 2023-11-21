package bpm.fd.core;

import java.io.Serializable;

import bpm.vanilla.platform.core.beans.data.Dataset;

public interface IComponentData extends Serializable {

	public Dataset getDataset();
	
	public void setDataset(Dataset dataset);
	
}
