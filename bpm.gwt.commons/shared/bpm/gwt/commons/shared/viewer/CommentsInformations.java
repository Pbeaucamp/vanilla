package bpm.gwt.commons.shared.viewer;

import java.util.ArrayList;
import java.util.List;

import bpm.vanilla.platform.core.beans.comments.CommentDefinition.TypeComment;
import bpm.vanilla.platform.core.beans.validation.Validation;

import com.google.gwt.user.client.rpc.IsSerializable;

public class CommentsInformations implements IsSerializable {
	
	private Validation validation;
	private TypeComment typeComment;
	
	private List<CommentInformations> comments;
	private boolean canValidate;
	private boolean isAdmin;

	public CommentsInformations() { }
	
	public CommentsInformations(Validation validation, TypeComment typeComment, List<CommentInformations> comments, boolean canValidate, boolean isAdmin) {
		this.validation = validation;
		this.typeComment = typeComment;
		this.comments = comments;
		this.canValidate = canValidate;
		this.isAdmin = isAdmin;
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
	
	public boolean canValidate() {
		return canValidate;
	}
	
	public boolean isAdmin() {
		return isAdmin;
	}
}
