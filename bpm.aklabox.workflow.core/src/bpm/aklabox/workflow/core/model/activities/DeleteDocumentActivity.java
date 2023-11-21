package bpm.aklabox.workflow.core.model.activities;

import java.util.ArrayList;
import java.util.List;

public class DeleteDocumentActivity extends Activity implements IAklaBoxServer, INamePattern {

	private static final long serialVersionUID = 1L;

	private String address;
	private String fileName;
//	private int aklaboxServer;
	private int aklaBoxDirectory;
	private String namePattern = "";
	private boolean permanent;
	private List<AklaBoxFiles> aklaBoxFiles = new ArrayList<AklaBoxFiles>();

	public DeleteDocumentActivity(String activityId, String activityName, int workflowId, int posX, int posY, int listIndex, String address, String fileName/*, int aklaboxServer*/) {
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

	public DeleteDocumentActivity() {
		this.activityId = "copyDocumentActivity";
		this.activityName = "Delete Document";
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

	public int getAklaBoxDirectory() {
		return aklaBoxDirectory;
	}

	public void setAklaBoxDirectory(int aklaBoxDirectory) {
		this.aklaBoxDirectory = aklaBoxDirectory;
	}

	public boolean isPermanent() {
		return permanent;
	}

	public void setPermanent(boolean permanent) {
		this.permanent = permanent;
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
