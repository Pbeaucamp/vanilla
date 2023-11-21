package bpm.aklabox.workflow.core.model.activities;

public class VanillaReport extends Activity implements IVanilla{

	private static final long serialVersionUID = 1L;

	private int vanillaServer;
	private int repositoryItemId;

	public VanillaReport(String activityId, String activityName, int workflowId, int posX, int posY, int listIndex) {
		super();
		this.activityId = activityId;
		this.activityName = activityName;
		this.workflowId = workflowId;
		this.posX = posX;
		this.posY = posY;
		this.listIndex = listIndex;
	}
	
	public VanillaReport() {
		this.activityId = "vanillaReport";
		this.activityName = "Report";
	}


	public int getVanillaServer() {
		return vanillaServer;
	}


	public void setVanillaServer(int vanillaServer) {
		this.vanillaServer = vanillaServer;
	}

	public int getRepositoryItemId() {
		return repositoryItemId;
	}

	public void setRepositoryItemId(int repositoryItemId) {
		this.repositoryItemId = repositoryItemId;
	}

	
	
	
}
