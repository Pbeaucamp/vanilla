package bpm.aklabox.workflow.core.model.activities;

import java.util.ArrayList;
import java.util.List;

import bpm.aklabox.workflow.core.IAklaflowConstant;

public class AggregateActivity extends Activity implements IAklaBoxServer, IFileDestination {

	private static final long serialVersionUID = 1L;

	private String aggregateName = "";
//	private int aklaboxServer;
	private int fileDestination = 0;
	private String fileType = "";
	private String orientation = IAklaflowConstant.PORTRAIT;
	private List<AklaBoxFiles> aklaBoxFiles = new ArrayList<AklaBoxFiles>();

	public AggregateActivity(String activityId, String activityName, int workflowId, int posX, int posY, int listIndex/*, int aklaboxServer*/) {
		super();
		this.activityId = activityId;
		this.activityName = activityName;
		this.workflowId = workflowId;
		this.posX = posX;
		this.posY = posY;
		this.listIndex = listIndex;
//		this.aklaboxServer = aklaboxServer;
	}

	public AggregateActivity() {
		this.activityId = "aggregate";
		this.activityName = "Aggregate Document";
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
//
	@Override
	public List<AklaBoxFiles> getAklaBoxFiles() {
		return aklaBoxFiles;
	}

	@Override
	public void setAklaBoxFiles(List<AklaBoxFiles> aklaBoxFiles) {
		this.aklaBoxFiles = aklaBoxFiles;
	}

	public String getAggregateName() {
		return aggregateName;
	}

	public void setAggregateName(String aggregateName) {
		this.aggregateName = aggregateName;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getOrientation() {
		return orientation;
	}

	public void setOrientation(String orientation) {
		this.orientation = orientation;
	}

	public int getFileDestination() {
		return fileDestination;
	}

	public void setFileDestination(int fileDestination) {
		this.fileDestination = fileDestination;
	}

}
