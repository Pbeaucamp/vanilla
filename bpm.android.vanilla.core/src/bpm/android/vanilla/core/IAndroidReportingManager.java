package bpm.android.vanilla.core;

import java.util.List;

import bpm.android.vanilla.core.beans.AndroidRepository;
import bpm.android.vanilla.core.beans.metadata.AndroidMetadata;
import bpm.android.vanilla.core.xstream.IXmlActionType;
import bpm.fwr.api.beans.FWRReport;
import bpm.fwr.api.beans.dataset.FwrPrompt;

public interface IAndroidReportingManager {
	
	public enum ActionType implements IXmlActionType {
		GET_ALL_METADATA,
		LOAD_METADATA,
		PREVIEW_REPORT,
		SAVE_REPORT,
		LOAD_REPORT, 
		LOAD_REPOSITORY,
		GET_PROMPTS_RESPONSE
	}
	
	public List<AndroidMetadata> getAllMetadata() throws Exception;
	
	public AndroidMetadata loadMetadata(int metadataId) throws Exception;
	
	public String previewReport(FWRReport report) throws Exception;
	
	public void saveReport(FWRReport report) throws Exception;
	
	public FWRReport loadReport(int itemId) throws Exception;
	
	public AndroidRepository getRepositoryContent(AndroidRepository repository) throws Exception;
	
	public List<FwrPrompt> getPromptsResponse(List<FwrPrompt> prompts) throws Exception;
}
