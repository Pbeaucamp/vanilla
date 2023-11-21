package bpm.vanilla.portal.client.services;

import java.util.List;

import bpm.gwt.commons.shared.viewer.DisplayItem;
import bpm.vanilla.portal.shared.repository.LinkedDocumentDTO;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ActionsServiceAsync {
	
	public void checkRunnable(int itemId, AsyncCallback<String> callback);
	
	public void getLinkedDocuments(int itemId, AsyncCallback<List<LinkedDocumentDTO>> callback);
	
	public void getLinkedDocumentUrl(int itemId, int linkDocId, AsyncCallback<DisplayItem> callback);

}
