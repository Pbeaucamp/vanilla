package bpm.aklabox.workflow.core.model.activities;

import java.util.ArrayList;
import java.util.List;

public class ZipFileActivity extends Activity implements IFileDestination, IAklaBoxServer{

	private static final long serialVersionUID = 1L;

//	private int aklaboxServer;
	private int aklaBoxDirectory;
	private int fileDestination = 0;
	private List<AklaBoxFiles> aklaBoxFiles = new ArrayList<AklaBoxFiles>();

	public ZipFileActivity(String activityId, String activityName, int workflowId, int posX, int posY, int listIndex) {
		super();
		this.activityId = activityId;
		this.activityName = activityName;
		this.workflowId = workflowId;
		this.posX = posX;
		this.posY = posY;
		this.listIndex = listIndex;
	}
	
	public ZipFileActivity() {
		this.activityId = "zipFile";
		this.activityName = "Zip File";
	}
	
//	public int getAklaboxServer() {
//		return aklaboxServer;
//	}
//
//	public void setAklaboxServer(int aklaboxServer) {
//		this.aklaboxServer = aklaboxServer;
//	}

	public int getAklaBoxDirectory() {
		return aklaBoxDirectory;
	}

	public void setAklaBoxDirectory(int aklaBoxDirectory) {
		this.aklaBoxDirectory = aklaBoxDirectory;
	}

	public List<AklaBoxFiles> getAklaBoxFiles() {
		return aklaBoxFiles;
	}

	public void setAklaBoxFiles(List<AklaBoxFiles> aklaBoxFiles) {
		this.aklaBoxFiles = aklaBoxFiles;
	}

	public int getFileDestination() {
		return fileDestination;
	}

	public void setFileDestination(int fileDestination) {
		this.fileDestination = fileDestination;
	}

	

}
