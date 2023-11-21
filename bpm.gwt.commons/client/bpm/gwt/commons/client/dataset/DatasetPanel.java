package bpm.gwt.commons.client.dataset;

import java.util.List;

import com.google.gwt.user.client.ui.Composite;

import bpm.vanilla.platform.core.beans.data.DataColumn;

public abstract class DatasetPanel extends Composite {
	
	public abstract List<DataColumn> getMetaColumns();
	
	public abstract String getQuery(String datasetName);
	
}
