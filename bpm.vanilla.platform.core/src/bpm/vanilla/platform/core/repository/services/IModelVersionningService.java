package bpm.vanilla.platform.core.repository.services;

import java.io.InputStream;
import java.util.List;

import bpm.vanilla.platform.core.beans.VanillaLogs.Level;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.platform.core.repository.Revision;
import bpm.vanilla.platform.core.xstream.IXmlActionType;



public interface IModelVersionningService {
	public static enum ActionType implements IXmlActionType{
		UPDATE(Level.INFO), UNLOCK(Level.INFO), SHARE(Level.INFO), REVERT(Level.INFO), 
		LIST_REVISIONS(Level.DEBUG), REVISION_MODEL(Level.DEBUG), CHECK_OUT(Level.INFO), CHECK_IN(Level.INFO);
		
		private Level level;

		ActionType(Level level) {
			this.level = level;
		}
		
		@Override
		public Level getLevel() {
			return level;
		}
	}
	public void checkIn(RepositoryItem item, String comment, InputStream modelStream) throws Exception;
	public InputStream checkOut(RepositoryItem item) throws Exception;
	public InputStream getRevision(RepositoryItem item, int revisionNumber) throws Exception;
	public List<Revision> getRevisions(RepositoryItem item) throws Exception;
	public void revertToRevision(RepositoryItem item, int revisionNumber, String comment) throws Exception;
	public void share(RepositoryItem item) throws Exception;
	public boolean unlock(RepositoryItem item) throws Exception;
	public void updateRevision(Revision revision) throws Exception;
}
