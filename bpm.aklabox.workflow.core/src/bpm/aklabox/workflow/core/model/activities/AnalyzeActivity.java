package bpm.aklabox.workflow.core.model.activities;

public class AnalyzeActivity extends Activity implements IAnalyze, IInteractive {

	private static final long serialVersionUID = 1L;

	private String fileType;
	private String content;
	private String owner;
	private String date;
	private boolean OCR;
	private boolean interactive;

	public AnalyzeActivity(String activityId, String activityName, int workflowId, int posX, int posY, int listIndex, String fileType, String content, String owner, String date) {
		super();
		this.activityId = activityId;
		this.activityName = activityName;
		this.workflowId = workflowId;
		this.posX = posX;
		this.posY = posY;
		this.listIndex = listIndex;
		this.fileType = fileType;
		this.content = content;
		this.owner = owner;
		this.date = date;
	}

	public AnalyzeActivity() {
		this.activityId = "analyze";
		this.activityName = "Analyze";
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public boolean isOCR() {
		try {
			return OCR;
		} catch (Exception e) {
			return false;
		}
	}

	public void setOCR(boolean oCR) {
		OCR = oCR;
	}

	public boolean isInteractive() {
		try {
			return interactive;
		} catch (Exception e) {
			return false;
		}
	}

	public void setInteractive(boolean interactive) {
		this.interactive = interactive;
	}

}
