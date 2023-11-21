package bpm.fd.web.client.panels.properties;

import bpm.vanilla.platform.core.beans.data.Dataset;

public interface IComponentDataProperties {

	public void setDataset(Dataset dataset);

	public void setDataset(Dataset dataset, boolean refresh);
	
}
