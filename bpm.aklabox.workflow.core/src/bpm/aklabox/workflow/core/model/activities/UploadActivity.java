package bpm.aklabox.workflow.core.model.activities;

import java.util.ArrayList;
import java.util.List;

public class UploadActivity extends Activity implements IAklaBoxServer , IVariable {

	private static final long serialVersionUID = 1L;

//	private int aklaboxServer;
	private List<AklaBoxFiles> aklaBoxFiles = new ArrayList<AklaBoxFiles>();
	private int variableResource;

	public UploadActivity(String activityId, String activityName, int workflowId, int posX, int posY, int listIndex,/* int aklaboxServer,*/ int aklaBoxDirectory) {
		super();
		this.activityId = activityId;
		this.activityName = activityName;
		this.workflowId = workflowId;
		this.posX = posX;
		this.posY = posY;
		this.listIndex = listIndex;
//		this.aklaboxServer = aklaboxServer;
	}

	public UploadActivity() {
		this.activityId = "upload";
		this.activityName = "Upload";
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

	public int getVariableResource() {
		return variableResource;
	}

	public void setVariableResource(int variableResource) {
		this.variableResource = variableResource;
	}

}
