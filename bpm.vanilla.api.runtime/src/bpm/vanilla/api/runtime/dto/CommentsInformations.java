package bpm.vanilla.api.runtime.dto;

import java.util.ArrayList;
import java.util.List;

import bpm.vanilla.platform.core.beans.comments.CommentDefinition.TypeComment;
import bpm.vanilla.platform.core.beans.validation.Validation;


public class CommentsInformations  {
	
	private Validation validation;
	private TypeComment typeComment;
	
	private List<CommentInformations> comments;
	private boolean canValidate;
	private boolean admin;

	public CommentsInformations() { }
	
	public CommentsInformations(Validation validation, TypeComment typeComment, List<CommentInformations> comments, boolean canValidate, boolean admin) {
		this.validation = validation;
		this.typeComment = typeComment;
		this.comments = comments;
		this.canValidate = canValidate;
		this.admin = admin;
	}
	
	public Validation getValidation() {
		return validation;
	}
	
	public TypeComment getTypeComment() {
		return typeComment;
	}
	
	public List<CommentInformations> getComments() {
		return comments != null ? comments : new ArrayList<CommentInformations>();
	}
	
	
	public boolean isCanValidate() {
		return canValidate;
	}

	public boolean isAdmin() {
		return admin;
	}
}
