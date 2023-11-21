package bpm.vanilla.platform.core;

import java.util.List;

import bpm.vanilla.platform.core.beans.VanillaLogs.Level;
import bpm.vanilla.platform.core.beans.comments.CommentDefinition;
import bpm.vanilla.platform.core.beans.comments.CommentParameter;
import bpm.vanilla.platform.core.beans.comments.CommentValue;
import bpm.vanilla.platform.core.beans.validation.Validation;
import bpm.vanilla.platform.core.xstream.IXmlActionType;

public interface ICommentService {
	
	public static enum ActionType implements IXmlActionType {
		ADD_COMMENT_VALUE(Level.INFO), ADD_COMMENT_VALUES(Level.INFO), MODIFY_COMMENT_VALUES(Level.INFO), 
		GET_COMMENT_DEFINITION(Level.DEBUG), GET_COMMENTS(Level.DEBUG), GET_COMMENTS_FOR_USER(Level.DEBUG), GET_COMMENTS_DEFINITION(Level.DEBUG), GET_COMMENT_NOT_VALIDATE(Level.DEBUG), 
		VALIDATE(Level.INFO), UNVALIDATE(Level.INFO), STOP_VALIDATION_PROCESS(Level.INFO);
		
		private Level level;

		ActionType(Level level) {
			this.level = level;
		}
		
		@Override
		public Level getLevel() {
			return level;
		}
	}

	public void addComment(int userId, CommentValue comment, int repId) throws Exception;
	public void addComments(Validation validation, int userId, List<CommentValue> comments, int repId) throws Exception;
	public void modifyComments(Validation validation, int userId, List<CommentValue> comments, int repId, boolean isLastCommentUnvalidate) throws Exception;
	
	public List<CommentValue> getComments(int itemId, int repId, String commentName, List<CommentParameter> parameters) throws Exception;
	public List<CommentValue> getComments(int commentDefinitionId, int repId, int userId) throws Exception;
	
	public List<CommentDefinition> getCommentDefinitions(int itemId, int repId) throws Exception;
	public CommentDefinition getCommentDefinition(int itemId, int repId, String commentName) throws Exception;
	public CommentValue getCommentNotValidate(int commentDefinitionId, int repId) throws Exception;
	
	public void validate(Validation validation, int userId, int groupId, int repId) throws Exception;
	public void unvalidate(Validation validation, int userId, int groupId, int repId) throws Exception;
	public void stopValidationProcess(Validation validation, int userId, int groupId, int repId) throws Exception;
}
