package bpm.fwr.client.services;

import java.util.HashMap;
import java.util.List;

import bpm.fwr.api.beans.FWRReport;
import bpm.fwr.api.beans.components.IReportComponent;
import bpm.fwr.api.beans.dataset.DataSet;
import bpm.fwr.api.beans.dataset.FwrPrompt;
import bpm.fwr.shared.models.metadata.FwrBusinessModel;
import bpm.gwt.commons.shared.InfoUser;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface FwrServiceMetadataAsync {

	public void getValues(String name, String tname, AsyncCallback<List<String>> asyncCallback);

	public void createFilter(String name, List<String> values, String operator, AsyncCallback<Void> asyncCallback);

	public void getPromptList(FwrPrompt prompt, int metadataId, String pckgName, String modelName, AsyncCallback<List<String>> asyncCallback);

	public void getCascadingPromptList(List<FwrPrompt> prompts, FwrPrompt prompt, AsyncCallback<List<String>> asyncCallback);
	
	public void createCustomColor(String name, int r, int g, int b, AsyncCallback<Void> callback);
	
	public void getCustomColor(String colorName, AsyncCallback<HashMap<String,Integer>> callback);
	
	public void loadReport(int itemId, String group, AsyncCallback<FWRReport> callback);

	public void getBirtTemplates(AsyncCallback<HashMap<String, String>> callback);

	public void getMetadataContent(int metadataId, String group, boolean load, AsyncCallback<List<FwrBusinessModel>> callback);

	public void getFiltersAndPromptsForDataset(InfoUser infos, String pack, String model, String language, int metadataId,
			AsyncCallback<HashMap<String, Object>> callback);

	public void saveComplexReport(FWRReport report, boolean update, AsyncCallback<Integer> callback);

	public void validateScript(String script, AsyncCallback<Boolean> callback);

	public void saveDataset(DataSet ds, AsyncCallback<Void> callback);

	public void saveJoinDataset(DataSet ds, List<DataSet> dss, AsyncCallback<Void> callback);

	public void saveComponentForPreview(IReportComponent component, AsyncCallback<Void> callback);

	public void loadDatasetFromQuery(int metadataid, String modelName, String packageName, String queryName, AsyncCallback<DataSet> callback);
}
