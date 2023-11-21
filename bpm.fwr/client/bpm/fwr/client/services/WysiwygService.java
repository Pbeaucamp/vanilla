package bpm.fwr.client.services;


import java.util.List;

import bpm.fwr.api.beans.FWRReport;
import bpm.fwr.shared.models.FusionMapDTO;
import bpm.gwt.commons.client.services.exception.ServiceException;
import bpm.vanilla.platform.core.repository.IReport;
import bpm.vanilla.platform.core.repository.Template;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("WysiwygService")
public interface WysiwygService extends RemoteService {
	
	public static class Connect{
		private static WysiwygServiceAsync instance;
		public static WysiwygServiceAsync getInstance(){
			if(instance == null){
				instance = (WysiwygServiceAsync) GWT.create(WysiwygService.class);
			}
			return instance;
		}
	}

	public String previewWysiwygReport(FWRReport report) throws ServiceException;
	
	public int saveWysiwygReport(FWRReport report, boolean update) throws ServiceException ;

	public String saveWysiwygReportAsBirtReport(FWRReport report) throws ServiceException ;
	
	public List<FusionMapDTO> getVanillaMapsAvailables() throws ServiceException ;

	public String buildMapHtml(FusionMapDTO selectedMap) throws ServiceException;
	
	public List<Template<IReport>> getTemplates() throws ServiceException;
	
	public Template<IReport> getTemplate(int templateId) throws ServiceException;
	
	public void addTemplate(Template<IReport> template) throws ServiceException;
	
	public void deleteTemplate(Template<IReport> template) throws ServiceException;
	
	public String downloadReportAsBirt(FWRReport report) throws ServiceException;
}


