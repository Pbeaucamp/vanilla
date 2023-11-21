package bpm.gwt.commons.client.services;

import java.util.HashMap;
import java.util.List;

import bpm.fm.api.model.HasItemLinked;
import bpm.fm.api.model.Observatory;
import bpm.mdm.model.supplier.Contract;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.repository.RepositoryItem;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface FreeMetricServiceAsync {

	public void getObservatories(AsyncCallback<HashMap<Group, List<Observatory>>> callback);
	
	void createAxeETL(String etlName, HasItemLinked hasItemLinked, Contract selectedContract, HashMap<String, Integer> columnsIndex, AsyncCallback<String> asyncCallback);
	
	void createAxeETL(String etlName, HasItemLinked hasItemLinked, RepositoryItem metadata, String queryName, HashMap<String, Integer> columnsIndex, AsyncCallback<String> asyncCallback);

}
