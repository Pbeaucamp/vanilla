package bpm.aklabox.workflow.core.model.activities;


public class RenameFormActivity extends Activity implements IStandardForm {

	private static final long serialVersionUID = 1L;

	private int formEngine;
	private int cellId;

	public RenameFormActivity(String activityId, String activityName, int workflowId, int posX, int posY, int listIndex) {
		super();
		this.activityId = activityId;
		this.activityName = activityName;
		this.workflowId = workflowId;
		this.posX = posX;
		this.posY = posY;
		this.listIndex = listIndex;
	}

	public RenameFormActivity() {
		this.activityId = "renameForm";
		this.activityName = "Rename Form";
	}

	public int getFormEngine() {
		return formEngine;
	}

	public void setFormEngine(int formEngine) {
		this.formEngine = formEngine;
	}

	public int getCellId() {
		return cellId;
	}

	public void setCellId(int cellId) {
		this.cellId = cellId;
	}


}
