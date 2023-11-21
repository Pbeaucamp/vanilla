package bpm.aklabox.workflow.core.model.activities;

import java.util.ArrayList;
import java.util.List;

public class ValidateActivity extends Activity implements IAklaBoxServer {

	private static final long serialVersionUID = 1L;

	private String successFeedBack;
	private String failureFeedBack;
//	private int aklaboxServer;
	private List<AklaBoxFiles> aklaBoxFiles = new ArrayList<AklaBoxFiles>();

	public ValidateActivity(String activityId, String activityName, int workflowId, int posX, int posY, int listIndex, String successFeedBack, String failureFeedBack/*, int aklaboxServer*/) {
		super();
		this.activityId = activityId;
		this.activityName = activityName;
		this.workflowId = workflowId;
		this.posX = posX;
		this.posY = posY;
		this.listIndex = listIndex;
		this.successFeedBack = successFeedBack;
		this.failureFeedBack = failureFeedBack;
//		this.aklaboxServer = aklaboxServer;
	}

	public ValidateActivity() {
		this.activityId = "validate";
		this.activityName = "Validate";
	}

	public String getSuccessFeedBack() {
		return successFeedBack;
	}

	public void setSuccessFeedBack(String successFeedBack) {
		this.successFeedBack = successFeedBack;
	}

	public String getFailureFeedBack() {
		return failureFeedBack;
	}

	public void setFailureFeedBack(String failureFeedBack) {
		this.failureFeedBack = failureFeedBack;
	}

//	public int getAklaboxServer() {
//		return aklaboxServer;
//	}
//
//	public void setAklaboxServer(int aklaboxServer) {
//		this.aklaboxServer = aklaboxServer;
//	}

	@Override
	public List<AklaBoxFiles> getAklaBoxFiles() {
		return aklaBoxFiles;
	}

	@Override
	public void setAklaBoxFiles(List<AklaBoxFiles> aklaBoxFiles) {
		this.aklaBoxFiles = aklaBoxFiles;
	}

}
