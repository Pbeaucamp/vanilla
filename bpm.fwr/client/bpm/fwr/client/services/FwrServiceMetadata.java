package bpm.fwr.client.services;

import java.util.HashMap;
import java.util.List;

import bpm.fwr.api.beans.FWRReport;
import bpm.fwr.api.beans.components.IReportComponent;
import bpm.fwr.api.beans.dataset.DataSet;
import bpm.fwr.api.beans.dataset.FwrPrompt;
import bpm.fwr.shared.models.metadata.FwrBusinessModel;
import bpm.gwt.commons.client.services.exception.ServiceException;
import bpm.gwt.commons.shared.InfoUser;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("fwrMetadata")
public interface FwrServiceMetadata extends RemoteService {
	
	public static class Connect{
		private static FwrServiceMetadataAsync instance;
		public static FwrServiceMetadataAsync getInstance(){
			if(instance == null){
				instance = (FwrServiceMetadataAsync) GWT.create(FwrServiceMetadata.class);
			}
			return instance;
		}
	}
	
	public List<String> getValues(String name, String tname) throws ServiceException;
	
	public void createFilter(String name, List<String> values, String operator) throws ServiceException;
	
	public List<String> getPromptList(FwrPrompt prompt, int metadataId, String pckgName, String modelName) throws ServiceException;

	public List<String> getCascadingPromptList(List<FwrPrompt> prompts, FwrPrompt prompt) throws ServiceException;
	
	public void createCustomColor(String name, int r, int g, int b) throws ServiceException;
	
	public HashMap<String,Integer> getCustomColor(String colorName) throws ServiceException;
	
	public FWRReport loadReport(int itemId, String group) throws ServiceException;
	
	public HashMap<String, String> getBirtTemplates() throws ServiceException;
	
	public List<FwrBusinessModel> getMetadataContent(int metadataId, String group, boolean load) throws ServiceException;
	
	public HashMap<String, Object> getFiltersAndPromptsForDataset(InfoUser infos, String pack, String model, String language, int metadataId) throws ServiceException;
	
	public int saveComplexReport(FWRReport report, boolean update) throws ServiceException;
	
	public boolean validateScript(String script);
	
	public void saveDataset(DataSet ds) throws ServiceException;
	
	public void saveJoinDataset(DataSet ds, List<DataSet> dss) throws ServiceException;
	
	public void saveComponentForPreview(IReportComponent component) throws ServiceException;

	public DataSet loadDatasetFromQuery(int metadataid, String modelName, String packageName, String queryName) throws Exception;
}
