package bpm.fd.core.component;

import bpm.fd.core.DashboardComponent;
import bpm.fd.core.IComponentOption;

public class CommentComponent extends DashboardComponent implements IComponentOption {

	private static final long serialVersionUID = 1L;
	private boolean showComments = true;
	private boolean allowAddComments = true;

	private boolean limitComment;
	private int limit;
	private boolean validation;

	public boolean isShowComments() {
		return showComments;
	}

	public void setShowComments(boolean showComments) {
		this.showComments = showComments;
	}

	public boolean isAllowAddComments() {
		return allowAddComments;
	}

	public void setAllowAddComments(boolean allowAddComments) {
		this.allowAddComments = allowAddComments;
	}

	public boolean isLimitComment() {
		return limitComment;
	}

	public void setLimitComment(boolean limitComment) {
		this.limitComment = limitComment;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public boolean isValidation() {
		return validation;
	}

	public void setValidation(boolean validation) {
		this.validation = validation;
	}

	@Override
	public ComponentType getType() {
		return ComponentType.COMMENT;
	}

	@Override
	protected void clearData() { }

}
