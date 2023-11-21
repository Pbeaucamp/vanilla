package bpm.aklabox.workflow.core.model.activities;

import java.util.ArrayList;
import java.util.List;

public class CopyDocumentActivity extends Activity implements IFileDestination, IAklaBoxServer, INamePattern {

	private static final long serialVersionUID = 1L;

	private String address;
	private String fileName;
	private int fileDestination = 0;
//	private int aklaboxServer;
	private String namePattern = "";
	private List<AklaBoxFiles> aklaBoxFiles = new ArrayList<AklaBoxFiles>();

	public CopyDocumentActivity(String activityId, String activityName, int workflowId, int posX, int posY, int listIndex, String address, String fileName/*, int aklaboxServer*/) {
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

	public CopyDocumentActivity() {
		this.activityId = "copyDocumentActivity";
		this.activityName = "Copy Document";
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
	@Override
	public List<AklaBoxFiles> getAklaBoxFiles() {
		return aklaBoxFiles;
	}

	@Override
	public void setAklaBoxFiles(List<AklaBoxFiles> aklaBoxFiles) {
		this.aklaBoxFiles = aklaBoxFiles;
	}

	public int getFileDestination() {
		return fileDestination;
	}

	public void setFileDestination(int fileDestination) {
		this.fileDestination = fileDestination;
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
