package bpm.vanilla.platform.core.repository.services;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import bpm.vanilla.platform.core.beans.VanillaLogs.Level;
import bpm.vanilla.platform.core.repository.Comment;
import bpm.vanilla.platform.core.repository.LinkedDocument;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.platform.core.repository.SecuredCommentObject;
import bpm.vanilla.platform.core.xstream.IXmlActionType;

public interface IDocumentationService {
	public static enum ActionType implements IXmlActionType {
		ADD_COMMENT(Level.INFO), GET_COMMENTS(Level.DEBUG), DELETE_COMMENT(Level.INFO), DELETE_COMMENTS(Level.INFO), LINK_DOC_TO_ITEM(Level.INFO), LIST_REPORT_HISTO_DOC_IDS(Level.DEBUG), 
		LOAD_EXT_DOC(Level.DEBUG), LOAD_LINKED_DOC(Level.DEBUG), MAP_REPORT_TO_DOC(Level.INFO), UPDATE_EXT_DOC(Level.INFO), ADD_SEC_COMMENT_OBJECTS(Level.INFO), ADD_SEC_COMMENT_OBJECT(Level.INFO), 
		GET_SEC_COMMENT_OBJECTS(Level.DEBUG), REMOVE_SEC_COMMENT_OBJECTS(Level.INFO), CAN_COMMENT(Level.DEBUG), REMOVE_SEC_COMMENT_OBJECT(Level.INFO);
		
		private Level level;

		ActionType(Level level) {
			this.level = level;
		}
		
		@Override
		public Level getLevel() {
			return level;
		}
	}

	public void addOrUpdateComment(Comment comment, List<Integer> groupIds) throws Exception;

	public void delete(Comment comment) throws Exception;

	public void deleteComments(int objectId, int type) throws Exception;

	public List<Comment> getComments(int groupId, int objectId, int type) throws Exception;

	public LinkedDocument attachDocumentToItem(RepositoryItem item, InputStream is, String displayName, String comment, String format, String relativePath) throws Exception;

	/**
	 * 
	 * @param item
	 * @return
	 * @throws Exception
	 */
	public List<Integer> getReportHistoricDocumentsId(RepositoryItem item) throws Exception;

	/**
	 * @deprecated Use {@link #importExternalDocument(RepositoryItem)} instead
	 */
	public void importExternalDocument(RepositoryItem item, OutputStream outputStream) throws Exception;

	public InputStream importExternalDocument(RepositoryItem item) throws Exception;

	public InputStream importLinkedDocument(int linkedDocumentId) throws Exception;

	public void mapReportItemToReportDocument(RepositoryItem item, int docId, boolean userPrivate) throws Exception;

	public String updateExternalDocument(RepositoryItem item, InputStream datas) throws Exception;

	
	public void addSecuredCommentObjects(List<SecuredCommentObject> secs) throws Exception;

	public void addSecuredCommentObject(SecuredCommentObject secObject) throws Exception;

	public void removeSecuredCommentObject(int groupId, int objectId, int type) throws Exception;

	public List<SecuredCommentObject> getSecuredCommentObjects(int objectId, int type) throws Exception;
	
	public boolean canComment(int groupId, int objectId, int type) throws Exception;

	public void removeSecuredCommentObjects(int objectId, int type) throws Exception;
}
