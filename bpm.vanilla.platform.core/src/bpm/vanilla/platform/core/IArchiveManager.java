package bpm.vanilla.platform.core;

import java.util.List;

import bpm.vanilla.platform.core.beans.ArchiveType;
import bpm.vanilla.platform.core.beans.ArchiveTypeItem;
import bpm.vanilla.platform.core.beans.VanillaLogs.Level;
import bpm.vanilla.platform.core.xstream.IXmlActionType;

public interface IArchiveManager {

	public static enum ActionType implements IXmlActionType {
		
		ADD_ARCHIVE_TYPE(Level.INFO), UPDATE_ARCHIVE_TYPE(Level.INFO), DELETE_ARCHIVE_TYPE(Level.INFO), GET_ARCHIVE_TYPES(Level.DEBUG), 
		LINK_ITEM_ARCHIVE_TYPE(Level.INFO), GET_LINK_BY_ITEM(Level.DEBUG), GET_LINK_BY_ARCHIVE(Level.DEBUG);
		
		private Level level;

		ActionType(Level level) {
			this.level = level;
		}
		
		@Override
		public Level getLevel() {
			return level;
		}
	}
	
	public ArchiveType addArchiveType(ArchiveType type) throws Exception;
	
	public void deleteArchiveType(ArchiveType type) throws Exception;
	
	public ArchiveType updateArchiveType(ArchiveType type) throws Exception;
	
	public List<ArchiveType> getArchiveTypes() throws Exception;
	
	public ArchiveTypeItem linkItemArchiveType(int archiveTypeId, int itemId, int repositoryId, boolean isDirectory) throws Exception;
	
	public ArchiveTypeItem getArchiveTypeByItem(int itemId, int repositoryId, boolean isDirectory) throws Exception;
	
	public List<ArchiveTypeItem> getArchiveTypeByArchive(int archiveTypeId) throws Exception;
	 
}
