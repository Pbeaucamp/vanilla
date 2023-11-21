package bpm.aklabox.workflow.core.model.activities;

public class VanillaBiWorkflow extends Activity implements IVanilla{

	private static final long serialVersionUID = 1L;

	private int vanillaServer;
	private int repositoryItemId;
	
	public VanillaBiWorkflow(String activityId, String activityName, int workflowId, int posX, int posY, int listIndex) {
		super();
		this.activityId = activityId;
		this.activityName = activityName;
		this.workflowId = workflowId;
		this.posX = posX;
		this.posY = posY;
		this.listIndex = listIndex;
	}

	public VanillaBiWorkflow() {
		this.activityId = "vanillaBiWorkflow";
		this.activityName = "Bi Workflow";
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
