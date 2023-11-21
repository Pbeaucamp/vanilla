package bpm.gwt.commons.shared.viewer;

import bpm.vanilla.platform.core.beans.comments.CommentDefinition;
import bpm.vanilla.platform.core.beans.comments.CommentValue;

public class CommentValidationInformations extends CommentInformations {

	private CommentValue lastComment;
	
	private boolean canComment;
	private boolean canModify;
	private boolean lastCommentUnvalidate;
	
	private int nextCommentator;
	
	public CommentValidationInformations() { }
	
	public CommentValidationInformations(CommentDefinition definition, CommentValue lastComment, boolean canComment, boolean canModify, boolean lastCommentUnvalidate, int nextCommentator) {
		super(definition);
		this.lastComment = lastComment;
		this.canComment = canComment;
		this.canModify = canModify;
		this.lastCommentUnvalidate = lastCommentUnvalidate;
		this.nextCommentator = nextCommentator;
	}
	
	public CommentValue getLastComment() {
		return lastComment;
	}
	
	public boolean canComment() {
		return canComment;
	}
	
	public boolean canModify() {
		return canModify;
	}
	
	public boolean isLastCommentUnvalidate() {
		return lastCommentUnvalidate;
	}
	
	public int getNextCommentator() {
		return nextCommentator;
	}
}
