package bpm.vanilla.api.runtime.dto;

import java.util.List;

import bpm.vanilla.platform.core.beans.comments.CommentDefinition;
import bpm.vanilla.platform.core.beans.comments.CommentValue;

public class CommentRestitutionInformations extends CommentInformations {

	private List<CommentValue> comments;

	public CommentRestitutionInformations() {
	}

	public CommentRestitutionInformations(CommentDefinition definition, List<CommentValue> comments) {
		super(definition);
		this.comments = comments;
	}
	
	public List<CommentValue> getComments() {
		return comments;
	}

	public void setComments(List<CommentValue> comments) {
		this.comments = comments;
	}
}
