package bpm.vanilla.portal.client.services;

import java.util.Date;
import java.util.List;

import bpm.gwt.commons.shared.repository.ReportHistoryDTO;
import bpm.gwt.commons.shared.viewer.DisplayItem;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface HistoryServiceAsync {
	
	public void getLastView(int itemId, AsyncCallback<DisplayItem> callback);

	public void getAllHistory(int directoryItemId, Date from, Date to, AsyncCallback<List<ReportHistoryDTO>> callback);

	/**
	 * Returns the history for all specified items (via dirItemIds)
	 */
	public void getHistoryForItemList(List<Integer> list, Date from, Date to, AsyncCallback<List<ReportHistoryDTO>> callback);

	public void getHistoricUrl(ReportHistoryDTO item, AsyncCallback<DisplayItem> callback);

}
