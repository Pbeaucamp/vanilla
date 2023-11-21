package bpm.vanilla.portal.client.services;

import java.util.List;

import bpm.gwt.commons.shared.repository.DocumentVersionDTO;
import bpm.gwt.commons.shared.repository.PortailRepositoryItem;
import bpm.vanilla.platform.core.beans.AccessRequest;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface AccessRequestServiceAsync {
	
	public void addAccessRequestOnRepositoryItem(PortailRepositoryItem dto, String message, AsyncCallback<Void> callback);
	
	public void addAccessRequestGedDocument(DocumentVersionDTO dto, String message, AsyncCallback<Void> callback);

	/**
	 * the requests i have to answer
	 * @param showAllDemands
	 * @param callback
	 */
	public void getMyAccessRequests(boolean showAllDemands, AsyncCallback<List<AccessRequest>> callback);
	
	/**
	 * the requests i have made
	 * @param showAllDemands
	 * @param callback
	 */
	public void getMyAccessDemands(boolean showAllDemands, AsyncCallback<List<AccessRequest>> callback);
	
	public void approveRequest(AccessRequest request, AsyncCallback<Void> callback);

	public void refuseRequest(AccessRequest request, AsyncCallback<Void> callback);
}

