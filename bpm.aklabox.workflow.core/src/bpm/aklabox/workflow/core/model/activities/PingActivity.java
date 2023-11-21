package bpm.aklabox.workflow.core.model.activities;

public class PingActivity extends Activity implements IFileServer, IVariable {

	private static final long serialVersionUID = 1L;

	private int fileServerId;
	private String address = "";
	private int variableResource = 0;

	public PingActivity(String activityId, String activityName, int workflowId, int posX, int posY, int listIndex, int fileServerId) {
		super();
		this.activityId = activityId;
		this.activityName = activityName;
		this.workflowId = workflowId;
		this.posX = posX;
		this.posY = posY;
		this.listIndex = listIndex;
		this.fileServerId = fileServerId;
	}

	public PingActivity() {
		this.activityId = "ping";
		this.activityName = "Ping";
	}

	public PingActivity(Activity a) {
		super();
		this.activityId = a.getActivityId();
		this.activityName = a.getActivityName();
		this.workflowId = a.getWorkflowId();
		this.posX = a.getPosX();
		this.posY = a.getPosY();
		this.listIndex = a.getListIndex();
		this.fileServerId = ((PingActivity) a).getFileServerId();
		this.address = ((PingActivity) a).getAddress();
	}

	@Override
	public int getFileServerId() {
		return fileServerId;
	}

	@Override
	public void setFileServerId(int fileServerId) {
		this.fileServerId = fileServerId;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public int getVariableResource() {
		try {
			return variableResource;
		} catch (Exception e) {
			return 0;
		}

	}

	public void setVariableResource(int variableResource) {
		this.variableResource = variableResource;
	}

}
