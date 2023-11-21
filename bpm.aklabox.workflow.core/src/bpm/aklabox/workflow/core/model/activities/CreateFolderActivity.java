package bpm.aklabox.workflow.core.model.activities;

import java.util.ArrayList;
import java.util.List;

public class CreateFolderActivity extends Activity implements IAklaBoxServer, IFileName {

	private static final long serialVersionUID = 1L;

	private String fileName = "New Folder";
//	private int aklaboxServer;
	private List<AklaBoxFiles> aklaBoxFiles = new ArrayList<AklaBoxFiles>();

	public CreateFolderActivity(String activityId, String activityName, int workflowId, int posX, int posY, int listIndex, String fileName/*, int aklaboxServer*/) {
		super();
		this.activityId = activityId;
		this.activityName = activityName;
		this.workflowId = workflowId;
		this.posX = posX;
		this.posY = posY;
		this.listIndex = listIndex;
		this.setFileName(fileName);
//		this.aklaboxServer = aklaboxServer;
	}

	public CreateFolderActivity() {
		this.activityId = "createFolder";
		this.activityName = "Create Folder";
	}

//	@Override
//	public int getAklaboxServer() {
//		return aklaboxServer;
//	}
//
//	@Override
//	public void setAklaboxServer(int aklaboxServer) {
//		this.aklaboxServer = aklaboxServer;
//	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	@Override
	public List<AklaBoxFiles> getAklaBoxFiles() {
		return aklaBoxFiles;
	}

	@Override
	public void setAklaBoxFiles(List<AklaBoxFiles> aklaBoxFiles) {
		this.aklaBoxFiles = aklaBoxFiles;
	}

}
