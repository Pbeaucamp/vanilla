package bpm.vanilla.workplace.core.disco;

import java.util.ArrayList;
import java.util.List;

public class DisconnectedBackupConnection {

	private int itemId;

	private String datasourcesXML;
	private List<DisconnectedDataset> datasets;

	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	public String getDatasourcesXML() {
		return datasourcesXML;
	}

	public void setDatasourcesXML(String datasourcesXML) {
		this.datasourcesXML = datasourcesXML;
	}

	public List<DisconnectedDataset> getDatasets() {
		return datasets;
	}

	public void setDatasets(List<DisconnectedDataset> datasets) {
		this.datasets = datasets;
	}

	public void addDataset(DisconnectedDataset discoDataset) {
		if (datasets == null) {
			this.datasets = new ArrayList<DisconnectedDataset>();
		}
		this.datasets.add(discoDataset);
	}

}
