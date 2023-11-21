package bpm.fwr.client.action;

import bpm.fwr.client.widgets.BirtCommentWidget;

public class ActionChangeBirtComment extends Action{

	private String oldVanillaURL, newVanillaURL;
	private BirtCommentWidget birtComment;
	
	public ActionChangeBirtComment(ActionType type, BirtCommentWidget birtComment,  String newVanillaURL) {
		super(type);
		
		this.birtComment = birtComment;
	
	}

	@Override
	public void executeRedo() {
		
		birtComment.changeBirtComment(birtComment, ActionType.REDO);
	}

	@Override
	public void executeUndo() {
		
	
		birtComment.changeBirtComment(birtComment, ActionType.UNDO);
	}

	
}
