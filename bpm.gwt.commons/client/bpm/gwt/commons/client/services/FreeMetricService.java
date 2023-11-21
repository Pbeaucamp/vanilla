package bpm.gwt.commons.client.services;

import java.util.HashMap;
import java.util.List;

import bpm.fm.api.model.HasItemLinked;
import bpm.fm.api.model.Observatory;
import bpm.mdm.model.supplier.Contract;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.repository.RepositoryItem;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("freeMetricService")
public interface FreeMetricService extends RemoteService {

	public static class Connect{
		private static FreeMetricServiceAsync instance;
		public static FreeMetricServiceAsync getInstance(){
			if(instance == null){
				instance = (FreeMetricServiceAsync) GWT.create(FreeMetricService.class);
			}
			return instance;
		}
	}
	
	public HashMap<Group, List<Observatory>> getObservatories() throws Exception;
	
	public String createAxeETL(String etlName, HasItemLinked hasItemLinked, Contract selectedContract, HashMap<String, Integer> columnsIndex) throws Exception;
	
	public String createAxeETL(String etlName, HasItemLinked hasItemLinked, RepositoryItem metadata, String queryName, HashMap<String, Integer> columnsIndex) throws Exception;
	
}
