package bpm.aklabox.workflow.core.model.activities;

import java.util.ArrayList;
import java.util.List;

public class ScanActivity extends Activity implements IAklaBoxServer, IVariable {

	private static final long serialVersionUID = 1L;

//	private int aklaboxServer;
	private int aklaBoxDirectory;
	private List<AklaBoxFiles> aklaBoxFiles = new ArrayList<AklaBoxFiles>();
	private int variableResource;

	public ScanActivity(String activityId, String activityName, int workflowId, int posX, int posY, int listIndex, /*int aklaboxServer,*/ int aklaBoxDirectory) {
		super();
		this.activityId = activityId;
		this.activityName = activityName;
		this.workflowId = workflowId;
		this.posX = posX;
		this.posY = posY;
		this.listIndex = listIndex;
//		this.aklaboxServer = aklaboxServer;
		this.aklaBoxDirectory = aklaBoxDirectory;
	}

	public ScanActivity() {
		this.activityId = "scan";
		this.activityName = "Scan";
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

	public int getVariableResource() {
		return variableResource;
	}

	public void setVariableResource(int variableResource) {
		this.variableResource = variableResource;
	}

}
