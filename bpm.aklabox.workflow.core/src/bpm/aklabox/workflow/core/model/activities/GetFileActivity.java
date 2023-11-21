package bpm.aklabox.workflow.core.model.activities;

import java.util.ArrayList;
import java.util.List;

public class GetFileActivity extends Activity implements IFileName, IFileServer, INamePattern, IAklaBoxServer {

	private static final long serialVersionUID = 1L;

	private String address;
	private String fileName;
	private int fileServerId;
//	private int aklaboxServer;
	private String namePattern;
	private List<AklaBoxFiles> aklaBoxFiles = new ArrayList<AklaBoxFiles>();

	public GetFileActivity(String activityId, String activityName, int workflowId, int posX, int posY, int listIndex, String address, String fileName, int fileServerId) {
		super();
		this.activityId = activityId;
		this.activityName = activityName;
		this.workflowId = workflowId;
		this.posX = posX;
		this.posY = posY;
		this.listIndex = listIndex;
		this.address = address;
		this.fileName = fileName;
		this.fileServerId = fileServerId;
	}

	public GetFileActivity() {
		this.activityId = "getFile";
		this.activityName = "Get File";
	}

	public int getFileServerId() {
		return fileServerId;
	}

	public void setFileServerId(int fileServerId) {
		this.fileServerId = fileServerId;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

//	public int getAklaboxServer() {
//		return aklaboxServer;
//	}
//
//	public void setAklaboxServer(int aklaboxServer) {
//		this.aklaboxServer = aklaboxServer;
//	}
//
	public List<AklaBoxFiles> getAklaBoxFiles() {
		return aklaBoxFiles;
	}

	public void setAklaBoxFiles(List<AklaBoxFiles> aklaBoxFiles) {
		this.aklaBoxFiles = aklaBoxFiles;
	}

	public String getNamePattern() {
		return namePattern;
	}

	public void setNamePattern(String namePattern) {
		this.namePattern = namePattern;
	}

}
