package bpm.aklabox.workflow.core.model.activities;

import java.util.ArrayList;
import java.util.List;

public class DispatchActivity extends Activity implements IAklaBoxServer {

	private static final long serialVersionUID = 1L;

//	private int aklaboxServer;
	private List<AklaBoxFiles> aklaBoxFiles = new ArrayList<AklaBoxFiles>();
	private String aklaboxUsers;

	public DispatchActivity(String activityId, String activityName, int workflowId, int posX, int posY, int listIndex/*, int aklaboxServer*/) {
		super();
		this.activityId = activityId;
		this.activityName = activityName;
		this.workflowId = workflowId;
		this.posX = posX;
		this.posY = posY;
		this.listIndex = listIndex;
//		this.aklaboxServer = aklaboxServer;
	}

	public DispatchActivity() {
		this.activityId = "dispatch";
		this.activityName = "Dispatch";
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

	public String getAklaboxUsers() {
		return aklaboxUsers;
	}

	public void setAklaboxUsers(String aklaboxUsers) {
		this.aklaboxUsers = aklaboxUsers;
	}

}
