package bpm.vanilla.platform.core.repository.services;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.ItemInstance;
import bpm.vanilla.platform.core.beans.VanillaLogs.Level;
import bpm.vanilla.platform.core.repository.IRepositoryObject;
import bpm.vanilla.platform.core.repository.LinkedDocument;
import bpm.vanilla.platform.core.repository.Parameter;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.platform.core.repository.SecuredDirectory;
import bpm.vanilla.platform.core.repository.SecuredObject;
import bpm.vanilla.platform.core.xstream.IXmlActionType;

public interface IRepositoryAdminService {
	
	public static enum ActionType implements IXmlActionType{
		SECUR_DIRECTORY(Level.INFO), SECUR_ITEM(Level.INFO), SECUR_LINKED(Level.INFO), CAN_RUN(Level.DEBUG), 
		LIST_ITEM_ALLOWED_GROUPS(Level.DEBUG), LIST_LINKED_GROUPS(Level.DEBUG), GET_GENERATED_RELATIVE_URL(Level.DEBUG), 
		LIST_DIRECTORY_GROUPS(Level.DEBUG), LIST_ITEM_GROUPS(Level.DEBUG), LIST_ITEM_LINKED_DOCS(Level.DEBUG), 
		LIST_SECURED_DIRS(Level.DEBUG), LIST_SECURED_ITEMS(Level.DEBUG), CAN_ACCESS_DIR(Level.DEBUG), CAN_ACCESS_IT(Level.DEBUG), 
		UNSECUR_DIR(Level.INFO), UNSECUR_ITEM(Level.INFO), UNSECUR_LINKED_DOC(Level.INFO), DELETE_LINKED_DOC(Level.INFO), 
		ALLOW_RUN(Level.INFO), FORBID_RUN(Level.INFO), UPDATE_ITEM(Level.INFO), LIST_DELETED_DIRECTORIES(Level.DEBUG), LIST_DELETED_ITEMS(Level.DEBUG), 
		PURGE_ALL(Level.INFO), PURGE_OBJECTS(Level.INFO), RESTORE_DIRECTORY(Level.INFO), RESTORE_ITEM(Level.INFO), UPDATE_PARAMETER(Level.INFO), 
		PURGE_HISTORIC(Level.INFO), SECUR_ELEMENTS(Level.INFO), REMOVE_GROUP(Level.INFO), GET_ITEM_INSTANCES(Level.DEBUG), 
		GET_ITEM_INSTANCES_FOR_ITEM(Level.DEBUG), GET_ITEM_INSTANCE(Level.DEBUG), ADD_ITEM_INSTANCE(Level.INFO), REMOVE_ITEM_INSTANCE(Level.INFO), 
		GET_TOP_TEN(Level.DEBUG);
		
		private Level level;

		ActionType(Level level) {
			this.level = level;
		}
		
		@Override
		public Level getLevel() {
			return level;
		}
	}
	
	public void addGroupForDirectory(int groupId, int directoryId) throws Exception;
	public void addGroupForItem(int groupId, int directoryItemId) throws Exception;
	public void addGroupForLinkedDocument(Group group, LinkedDocument linkedDoc) throws Exception;
	
	public boolean canRun(int directoryItemId, int groupId)throws Exception;
	public List<Integer> getAllowedGroupId(RepositoryItem item)throws Exception;
	public List<Group> getAuthorizedGroupsForLinkedDocument(int linkedDocId) throws Exception;
	public String getGeneratedRelativeUrlFor(RepositoryItem item, Group group) throws Exception;
	public List<Group> getGroupsForDirectory(RepositoryDirectory dir) throws Exception;
	public List<Group> getGroupsForItemId(int directoryItemId) throws Exception;
	
	/**
	 * 
	 * @param directoryItemId
	 * @return the linked documents for the given Directoryitem id
	 * @throws Exception
	 */
	public List<LinkedDocument> getLinkedDocuments(int directoryItemId) throws Exception;
	
	public List<SecuredDirectory> getSecuredDirectoriesForGroup(Group group) throws Exception;
	public List<SecuredObject> getSecuredObjectForGroup(Group group) throws Exception;
	
	public boolean isDirectoryAccessible(int directoryId, Integer groupId) throws Exception;
	public boolean isDirectoryItemAccessible(int directoryItemId, Integer groupId) throws Exception;
	public void removeGroupForDirectory(int groupId, int directoryItemId) throws Exception;
	public void removeGroupForItem(int groupId, int directoryItemId) throws Exception;
	
	public void removeGroupForLinkedDocument(Group group,LinkedDocument linkedDoc) throws Exception;
	public void removeLinkedDocument(LinkedDocument linked) throws Exception;
	public void setObjectRunnableForGroup(Integer groupId, RepositoryItem item) throws Exception;
	public void unsetObjectRunnableForGroup(Group group, RepositoryItem item)throws Exception;
	public void update(RepositoryItem di) throws Exception;
	
	public List<RepositoryDirectory> getDeletedDirectories() throws Exception;
	public List<RepositoryItem> getDeletedItems() throws Exception;
	public boolean restoreDirectory(int directoryId) throws Exception;
	public boolean restoreDirectoryItem(int directoryItemId) throws Exception;
	public boolean purgeDeletedObjects(List<RepositoryDirectory> directories, List<RepositoryItem> items) throws Exception;
	public boolean purgeAllDeletedObjects() throws Exception;
	
	public void updateParameter(Parameter parameter) throws Exception;
	
	public void purgeHistoric() throws Exception;
	
	/**
	 * A method to set the security on multiple objects
	 * @param elements
	 * @param securityGroup the base security groups
	 * @param runnableGroups the runnable groups (only for runnable items like reports, dashboards, etc...)
	 * @param commentableGroup the commentable groups
	 * @param projectionGroup the projection groups (only for FASD)
	 * @throws Exception 
	 */
	public void setSecurityForElements(List<IRepositoryObject> elements, List<Group> securityGroup, List<Group> runnableGroups, List<Group> commentableGroup, List<Group> projectionGroup) throws Exception;
	
	/**
	 * Remove all reference to the group (before deletion) in the repository
	 * 
	 * @param group
	 * @throws Exception 
	 */
	public void removeGroup(Group group) throws Exception;
	
	public List<ItemInstance> getItemInstances(int start, int end, int itemType) throws Exception;
	public List<ItemInstance> getItemInstances(int itemId, Date startDate, Date endDate, Integer groupId) throws Exception;
	public ItemInstance getItemInstance(int instanceId) throws Exception;
	public ItemInstance getWorkflowInstance(int itemId, String key) throws Exception;
	public Integer addItemInstance(ItemInstance instance) throws Exception;
	public void removeItemInstances(int itemId) throws Exception;
	
	/**
	 * 
	 * @return Top ten of document which consume the most time
	 * @throws Exception
	 */
	public HashMap<RepositoryItem, Double> getTopTenItemConsumer(Integer itemType, Date startDate, Date endDate, Integer groupId) throws Exception;
} 
