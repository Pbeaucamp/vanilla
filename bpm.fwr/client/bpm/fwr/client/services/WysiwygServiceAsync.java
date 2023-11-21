package bpm.fwr.client.services;

import java.util.List;

import bpm.fwr.api.beans.FWRReport;
import bpm.fwr.shared.models.FusionMapDTO;
import bpm.vanilla.platform.core.repository.IReport;
import bpm.vanilla.platform.core.repository.Template;

import com.google.gwt.user.client.rpc.AsyncCallback;


public interface WysiwygServiceAsync {

	public void previewWysiwygReport(FWRReport report, AsyncCallback<String> asyncCallback);

	public void saveWysiwygReport(FWRReport report, boolean update, AsyncCallback<Integer> asyncCallback);

	public void saveWysiwygReportAsBirtReport(FWRReport report, AsyncCallback<String> asyncCallback);
	
	public void getVanillaMapsAvailables(AsyncCallback<List<FusionMapDTO>> callback);

	public void buildMapHtml(FusionMapDTO selectedMap, AsyncCallback<String> asyncCallback);
	
	public void getTemplates(AsyncCallback<List<Template<IReport>>> callback);
	
	public void getTemplate(int templateId, AsyncCallback<Template<IReport>> callback);
	
	public void addTemplate(Template<IReport> template, AsyncCallback<Void> callback);
	
	public void deleteTemplate(Template<IReport> template, AsyncCallback<Void> callback);
	
	public void downloadReportAsBirt(FWRReport report, AsyncCallback<String> callback);
}
