package bpm.aklabox.workflow.core.model.activities;

import java.util.ArrayList;
import java.util.List;

public class AssignTaskActivity extends Activity implements IAklaBoxServer {

	private static final long serialVersionUID = 1L;

	private String address = "";
	private String fileName = "";
//	private int aklaboxServer = 0;
	private int aklaBoxDirectory = 0;

	private List<AklaBoxFiles> aklaBoxFiles = new ArrayList<AklaBoxFiles>();
	
	private String usersID;
	private int typeTask;
	private String titleTask;
	private String descriptionTask;
	private int documentId;
	

	public AssignTaskActivity(String activityId, String activityName, int workflowId, int posX, int posY, int listIndex/*, int aklaboxServer*/) {
		super();
		this.activityId = activityId;
		this.activityName = activityName;
		this.workflowId = workflowId;
		this.posX = posX;
		this.posY = posY;
		this.listIndex = listIndex;
//		this.aklaboxServer = aklaboxServer;
	}

	public AssignTaskActivity() {
		this.activityId = "assignTaskActivity";
		this.activityName = "Assign Task";
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

	public String getUsersID() {
		return usersID;
	}

	public void setUsersID(String usersID) {
		this.usersID = usersID;
	}

	public int getTypeTask() {
		return typeTask;
	}

	public void setTypeTask(int type) {
		this.typeTask = type;
	}

	public String getTitleTask() {
		return titleTask;
	}

	public void setTitleTask(String title) {
		this.titleTask = title;
	}

	public String getDescriptionTask() {
		return descriptionTask;
	}

	public void setDescriptionTask(String description) {
		this.descriptionTask = description;
	}

	public int getDocumentId() {
		documentId = (!getAklaBoxFiles().isEmpty()) ? getAklaBoxFiles().get(0).getFileId() : 0;
		return documentId;
	}

	public void setDocumentId(int documentId) {
		this.documentId = documentId;
	}

}
