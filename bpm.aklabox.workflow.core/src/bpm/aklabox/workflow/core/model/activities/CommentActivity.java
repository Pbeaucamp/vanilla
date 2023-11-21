package bpm.aklabox.workflow.core.model.activities;


public class CommentActivity extends Activity implements IComment{

	private static final long serialVersionUID = 1L;

	private String comment = "";
	private String commentType = "info";
	
	public CommentActivity(String activityId, String activityName, int workflowId, int posX, int posY, int listIndex) {
		super();
		this.activityId = activityId;
		this.activityName = activityName;
		this.workflowId = workflowId;
		this.posX = posX;
		this.posY = posY;
		this.listIndex = listIndex;
	}

	public CommentActivity() {
		this.activityId = "commentActivity";
		this.activityName = "Comment";
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getCommentType() {
		return commentType;
	}

	public void setCommentType(String commentType) {
		this.commentType = commentType;
	}

	

}
