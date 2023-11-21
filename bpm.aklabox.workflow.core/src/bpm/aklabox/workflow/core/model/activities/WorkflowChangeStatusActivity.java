package bpm.aklabox.workflow.core.model.activities;

import java.util.ArrayList;
import java.util.List;

public class WorkflowChangeStatusActivity extends Activity implements IAklaBoxServer, IFileName {

	private static final long serialVersionUID = 1L;

	private String address = "";
	private String fileName = "";
	private int aklaboxServer = 0;
//	private int aklaBoxDirectory = 0;

	private List<AklaBoxFiles> aklaBoxFiles = new ArrayList<AklaBoxFiles>();

	public WorkflowChangeStatusActivity(String activityId, String activityName, int workflowId, int posX, int posY, int listIndex, String address, String fileName/*, int aklaboxServer*/) {
		super();
		this.activityId = activityId;
		this.activityName = activityName;
		this.workflowId = workflowId;
		this.posX = posX;
		this.posY = posY;
		this.listIndex = listIndex;
		this.address = address;
		this.fileName = fileName;
//		this.aklaboxServer = aklaboxServer;
	}

	public WorkflowChangeStatusActivity() {
		this.activityId = "workflowChangeStatusActivity";
		this.activityName = "Change Status";
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public int getAklaboxServer() {
		return aklaboxServer;
	}

	public void setAklaboxServer(int aklaboxServer) {
		this.aklaboxServer = aklaboxServer;
	}

//	public int getAklaBoxDirectory() {
//		return aklaBoxDirectory;
//	}
//
//	public void setAklaBoxDirectory(int aklaBoxDirectory) {
//		this.aklaBoxDirectory = aklaBoxDirectory;
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
