package bpm.aklabox.workflow.core.model.activities;

public class XorActivity extends Activity {

	private static final long serialVersionUID = 1L;

	private String trueActivity;
	private String falseActivity;
	private String selectedCheckBox;
	private boolean value = false;

	public XorActivity(String activityId, String activityName, int workflowId, int posX, int posY, int listIndex) {
		super();
		this.activityId = activityId;
		this.activityName = activityName;
		this.workflowId = workflowId;
		this.posX = posX;
		this.posY = posY;
		this.listIndex = listIndex;
	}

	public XorActivity() {
		this.activityId = "xor";
		this.activityName = "XOR";
	}

	public String getFalseActivity() {
		return falseActivity;
	}

	public void setFalseActivity(String falseActivity) {
		if(falseActivity == null) {
			return;
		}
		this.falseActivity = falseActivity;
	}

	public String getTrueActivity() {
		return trueActivity;
	}

	public void setTrueActivity(String trueActivity) {
		if(trueActivity == null) {
			return;
		}
		this.trueActivity = trueActivity;
	}

	public boolean isValue() {
		return value;
	}

	public void setValue(boolean value) {
		this.value = value;
	}

	public String getSelectedCheckBox() {
		return selectedCheckBox;
	}

	public void setSelectedCheckBox(String selectedCheckBox) {
		this.selectedCheckBox = selectedCheckBox;
	}

}
