package bpm.aklabox.workflow.core.model.activities;

import java.util.ArrayList;
import java.util.List;

public class PutFileActivity extends Activity implements IFileServer, IFileName, IAklaBoxServer {

	private static final long serialVersionUID = 1L;

	private String address;
	private String fileName;
	private int fileServerId;
//	private int aklaboxServer;
	private List<AklaBoxFiles> aklaBoxFiles = new ArrayList<AklaBoxFiles>();

	public PutFileActivity(String activityId, String activityName, int workflowId, int posX, int posY, int listIndex, String address, String fileName, int fileServerId) {
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

	public PutFileActivity() {
		this.activityId = "putFile";
		this.activityName = "Put File";
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

	@Override
	public List<AklaBoxFiles> getAklaBoxFiles() {
		return aklaBoxFiles;
	}

	@Override
	public void setAklaBoxFiles(List<AklaBoxFiles> aklaBoxFiles) {
		this.aklaBoxFiles = aklaBoxFiles;
	}
//
//	@Override
//	public int getAklaboxServer() {
//		return aklaboxServer;
//	}
//
//	@Override
//	public void setAklaboxServer(int aklaboxServer) {
//		this.aklaboxServer = aklaboxServer;
//	}

}
