package bpm.gwt.commons.shared.viewer;

import bpm.vanilla.platform.core.beans.comments.CommentDefinition;

import com.google.gwt.user.client.rpc.IsSerializable;

public class CommentInformations implements IsSerializable {

	private CommentDefinition definition;
	
	public CommentInformations() { }
	
	public CommentInformations(CommentDefinition definition) {
		this.definition = definition;
	}
	
	public CommentDefinition getDefinition() {
		return definition;
	}
}
