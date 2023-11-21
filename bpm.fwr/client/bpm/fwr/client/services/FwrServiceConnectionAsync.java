package bpm.fwr.client.services;

import java.util.List;

import bpm.fwr.shared.models.TreeParentDTO;
import bpm.fwr.shared.models.metadata.FwrMetadata;
import bpm.vanilla.platform.core.beans.Group;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface FwrServiceConnectionAsync {
	
	public void initSession(AsyncCallback<Void> callback);
		
	public void browseRepositoryService(AsyncCallback<TreeParentDTO> callback);

	public void getGroupsService(AsyncCallback<List<Group>> callback);

	public void getMetadatas(String group, AsyncCallback<List<FwrMetadata>> callback);
}
