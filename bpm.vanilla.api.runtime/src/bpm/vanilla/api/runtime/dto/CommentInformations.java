package bpm.vanilla.api.runtime.dto;

import bpm.vanilla.platform.core.beans.comments.CommentDefinition;


public class CommentInformations {

	private CommentDefinition definition;
	
	public CommentInformations() { }
	
	public CommentInformations(CommentDefinition definition) {
		this.definition = definition;
	}
	
	public CommentDefinition getDefinition() {
		return definition;
	}
}
