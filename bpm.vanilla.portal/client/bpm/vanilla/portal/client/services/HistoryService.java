package bpm.vanilla.portal.client.services;

import java.util.Date;
import java.util.List;

import bpm.gwt.commons.client.services.exception.ServiceException;
import bpm.gwt.commons.shared.repository.ReportHistoryDTO;
import bpm.gwt.commons.shared.viewer.DisplayItem;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("HistoryService")
public interface HistoryService extends RemoteService {
	public static class Connect{
		private static HistoryServiceAsync instance;
		public static HistoryServiceAsync getInstance(){
			if(instance == null){
				instance = (HistoryServiceAsync) GWT.create(HistoryService.class);
			}
			return instance;
		}
	}
	
	public DisplayItem getLastView(int itemId) throws ServiceException;
	
	public List<ReportHistoryDTO> getAllHistory(int directoryItemId, Date from, Date to) throws ServiceException;
	
	public List<ReportHistoryDTO> getHistoryForItemList(List<Integer> list, Date from, Date to) throws ServiceException ;
	
	public DisplayItem getHistoricUrl(ReportHistoryDTO item) throws ServiceException;
}
