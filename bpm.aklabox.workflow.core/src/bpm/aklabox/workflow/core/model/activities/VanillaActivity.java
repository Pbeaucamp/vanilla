package bpm.aklabox.workflow.core.model.activities;

public class VanillaActivity extends Activity implements IFormEngine {

	private static final long serialVersionUID = 1L;

	private int formEngine;
	private String sendMessage;
	private String receiveMessage;
	private String performers;

	public VanillaActivity(String activityId, String activityName, int workflowId, int posX, int posY, int listIndex, int formEngine, String sendMessage, String receiveMessage) {
		super();
		this.activityId = activityId;
		this.activityName = activityName;
		this.workflowId = workflowId;
		this.posX = posX;
		this.posY = posY;
		this.listIndex = listIndex;
		this.formEngine = formEngine;
		this.sendMessage = sendMessage;
		this.receiveMessage = receiveMessage;
	}

	public VanillaActivity() {
		this.activityId = "vanillaInterface";
		this.activityName = "Vanilla Interface";
	}

	public int getFormEngine() {
		return formEngine;
	}

	public void setFormEngine(int formEngine) {
		this.formEngine = formEngine;
	}

	public String getSendMessage() {
		return sendMessage;
	}

	public void setSendMessage(String sendMessage) {
		this.sendMessage = sendMessage;
	}

	public String getReceiveMessage() {
		return receiveMessage;
	}

	public void setReceiveMessage(String receiveMessage) {
		this.receiveMessage = receiveMessage;
	}

	public String getPerformers() {
		return performers;
	}

	public void setPerformers(String performers) {
		this.performers = performers;
	}

}
