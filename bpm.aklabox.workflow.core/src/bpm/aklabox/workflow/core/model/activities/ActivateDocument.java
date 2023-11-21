package bpm.aklabox.workflow.core.model.activities;

import java.util.ArrayList;
import java.util.List;

public class ActivateDocument extends Activity implements IAklaBoxServer, INamePattern {

	private static final long serialVersionUID = 1L;

//	private int aklaboxServer;
	private String namePattern = "";
	private List<AklaBoxFiles> aklaBoxFiles = new ArrayList<AklaBoxFiles>();

	public ActivateDocument(String activityId, String activityName, int workflowId, int posX, int posY, int listIndex/*, int aklaboxServer*/) {
		super();
		this.activityId = activityId;
		this.activityName = activityName;
		this.workflowId = workflowId;
		this.posX = posX;
		this.posY = posY;
		this.listIndex = listIndex;
//		this.aklaboxServer = aklaboxServer;
	}

	public ActivateDocument() {
		this.activityId = "activateDocFolder";
		this.activityName = "Activate Document";
	}

//	public int getAklaboxServer() {
//		return aklaboxServer;
//	}
//
//	public void setAklaboxServer(int aklaboxServer) {
//		this.aklaboxServer = aklaboxServer;
//	}

	public String getNamePattern() {
		try {
			return namePattern;
		} catch (Exception e) {
			return "";
		}
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

	@Override
	public List<AklaBoxFiles> getAklaBoxFiles() {
		return aklaBoxFiles;
	}

	@Override
	public void setAklaBoxFiles(List<AklaBoxFiles> aklaBoxFiles) {
		this.aklaBoxFiles = aklaBoxFiles;
	}

}
