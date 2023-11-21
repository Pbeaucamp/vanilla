package bpm.aklabox.workflow.core.model.activities;

import java.util.ArrayList;
import java.util.List;

public class RetrieveActivity extends Activity implements IAklaBoxServer, INamePattern {

	private static final long serialVersionUID = 1L;

	private String address;
	private int fileServerId;
//	private int aklaboxServer;
	private int aklaBoxDirectory;
	private String namePattern = "";
	private List<AklaBoxFiles> aklaBoxFiles = new ArrayList<AklaBoxFiles>();

	public RetrieveActivity(String activityId, String activityName, int workflowId, int posX, int posY, int listIndex, String address, int fileServerId/*, int aklaboxServer*/) {
		super();
		this.activityId = activityId;
		this.activityName = activityName;
		this.workflowId = workflowId;
		this.posX = posX;
		this.posY = posY;
		this.listIndex = listIndex;
		this.address = address;
		this.fileServerId = fileServerId;
//		this.aklaboxServer = aklaboxServer;
	}

	public RetrieveActivity() {
		this.activityId = "retrieve";
		this.activityName = "Retrieve";
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public int getFileServerId() {
		return fileServerId;
	}

	public void setFileServerId(int fileServerId) {
		this.fileServerId = fileServerId;
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

	@Override
	public List<AklaBoxFiles> getAklaBoxFiles() {
		return aklaBoxFiles;
	}

	@Override
	public void setAklaBoxFiles(List<AklaBoxFiles> aklaBoxFiles) {
		this.aklaBoxFiles = aklaBoxFiles;
	}

	public String getNamePattern() {
		return namePattern;
	}

	public void setNamePattern(String namePattern) {
		try {
			namePattern = namePattern.replaceAll("[,]|[, ]|[ ]", ",");
			namePattern = namePattern.replace(",,", ",");
			this.namePattern = namePattern;
		} catch (Exception e) {
			e.printStackTrace();
			this.namePattern = "";
		}
	}

}
