package bpm.vanilla.platform.core.remote.impl.repository;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.ItemInstance;
import bpm.vanilla.platform.core.exceptions.VanillaException;
import bpm.vanilla.platform.core.remote.internal.HttpCommunicator;
import bpm.vanilla.platform.core.repository.IRepositoryObject;
import bpm.vanilla.platform.core.repository.LinkedDocument;
import bpm.vanilla.platform.core.repository.Parameter;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.platform.core.repository.SecuredDirectory;
import bpm.vanilla.platform.core.repository.SecuredObject;
import bpm.vanilla.platform.core.repository.services.IRepositoryAdminService;
import bpm.vanilla.platform.core.xstream.XmlAction;
import bpm.vanilla.platform.core.xstream.XmlArgumentsHolder;

import com.thoughtworks.xstream.XStream;

public class RemoteRepositoryAdmin implements IRepositoryAdminService {
	private HttpCommunicator httpCommunicator;
	private static XStream xstream;
	static {
		xstream = new XStream();

	}

	private static XmlArgumentsHolder createArguments(Object... arguments) {
		XmlArgumentsHolder args = new XmlArgumentsHolder();

		for (int i = 0; i < arguments.length; i++) {
			args.addArgument(arguments[i]);
		}
		return args;
	}

	public RemoteRepositoryAdmin(HttpCommunicator httpCommunicator) {
		this.httpCommunicator = httpCommunicator;
	}

	@Override
	public void addGroupForDirectory(int groupId, int directoryId) throws Exception {
		XmlAction op = new XmlAction(createArguments(groupId, directoryId), IRepositoryAdminService.ActionType.SECUR_DIRECTORY);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		handleError(result);

	}

	@Override
	public void addGroupForItem(int groupId, int directoryItemId) throws Exception {
		XmlAction op = new XmlAction(createArguments(groupId, directoryItemId), IRepositoryAdminService.ActionType.SECUR_ITEM);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		handleError(result);

	}

	@Override
	public void addGroupForLinkedDocument(Group group, LinkedDocument linkedDoc) throws Exception {
		XmlAction op = new XmlAction(createArguments(group, linkedDoc), IRepositoryAdminService.ActionType.SECUR_LINKED);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		handleError(result);

	}

	@Override
	public boolean canRun(int directoryItemId, int groupId) throws Exception {
		XmlAction op = new XmlAction(createArguments(directoryItemId, groupId), IRepositoryAdminService.ActionType.CAN_RUN);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);

		return (Boolean) handleError(result);
	}

	@Override
	public List<Integer> getAllowedGroupId(RepositoryItem item) throws Exception {
		XmlAction op = new XmlAction(createArguments(item), IRepositoryAdminService.ActionType.LIST_ITEM_ALLOWED_GROUPS);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		if (result.isEmpty()) {
			return Collections.EMPTY_LIST;
		}
		return (List<Integer>) handleError(result);
	}

	@Override
	public List<Group> getAuthorizedGroupsForLinkedDocument(int linkedDocId) throws Exception {
		XmlAction op = new XmlAction(createArguments(linkedDocId), IRepositoryAdminService.ActionType.LIST_LINKED_GROUPS);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		if (result.isEmpty()) {
			return Collections.EMPTY_LIST;
		}
		return (List<Group>) handleError(result);
	}

	@Override
	public String getGeneratedRelativeUrlFor(RepositoryItem item, Group group) throws Exception {
		XmlAction op = new XmlAction(createArguments(item, group), IRepositoryAdminService.ActionType.GET_GENERATED_RELATIVE_URL);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (String) handleError(result);
	}

	@Override
	public List<Group> getGroupsForDirectory(RepositoryDirectory dir) throws Exception {
		XmlAction op = new XmlAction(createArguments(dir), IRepositoryAdminService.ActionType.LIST_DIRECTORY_GROUPS);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		if (result.isEmpty()) {
			return Collections.EMPTY_LIST;
		}
		return (List<Group>) handleError(result);
	}

	@Override
	public List<Group> getGroupsForItemId(int directoryItemId) throws Exception {
		XmlAction op = new XmlAction(createArguments(directoryItemId), IRepositoryAdminService.ActionType.LIST_ITEM_GROUPS);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		if (result.isEmpty()) {
			return Collections.EMPTY_LIST;
		}
		return (List<Group>) handleError(result);
	}

	@Override
	public List<LinkedDocument> getLinkedDocuments(int directoryItemId) throws Exception {
		XmlAction op = new XmlAction(createArguments(directoryItemId), IRepositoryAdminService.ActionType.LIST_ITEM_LINKED_DOCS);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		if (result.isEmpty()) {
			return Collections.EMPTY_LIST;
		}
		return (List<LinkedDocument>) handleError(result);
	}

	@Override
	public List<SecuredDirectory> getSecuredDirectoriesForGroup(Group group) throws Exception {
		XmlAction op = new XmlAction(createArguments(group), IRepositoryAdminService.ActionType.LIST_SECURED_DIRS);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		if (result.isEmpty()) {
			return Collections.EMPTY_LIST;
		}
		return (List<SecuredDirectory>) handleError(result);
	}

	@Override
	public List<SecuredObject> getSecuredObjectForGroup(Group group) throws Exception {
		XmlAction op = new XmlAction(createArguments(group), IRepositoryAdminService.ActionType.LIST_SECURED_ITEMS);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		if (result.isEmpty()) {
			return Collections.EMPTY_LIST;
		}
		return (List<SecuredObject>) handleError(result);
	}

	@Override
	public boolean isDirectoryAccessible(int directoryId, Integer groupId) throws Exception {
		XmlAction op = new XmlAction(createArguments(directoryId, groupId), IRepositoryAdminService.ActionType.CAN_ACCESS_DIR);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		if (result.isEmpty()) {
			return false;
		}
		return (Boolean) handleError(result);
	}

	@Override
	public boolean isDirectoryItemAccessible(int directoryItemId, Integer groupId) throws Exception {
		XmlAction op = new XmlAction(createArguments(directoryItemId, groupId), IRepositoryAdminService.ActionType.CAN_ACCESS_IT);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		if (result.isEmpty()) {
			return false;
		}
		return (Boolean) handleError(result);
	}

	@Override
	public void removeGroupForDirectory(int groupId, int directoryId) throws Exception {
		XmlAction op = new XmlAction(createArguments(groupId, directoryId), IRepositoryAdminService.ActionType.UNSECUR_DIR);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		handleError(result);
	}

	@Override
	public void removeGroupForItem(int groupId, int directoryItemId) throws Exception {
		XmlAction op = new XmlAction(createArguments(groupId, directoryItemId), IRepositoryAdminService.ActionType.UNSECUR_ITEM);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		handleError(result);

	}

	@Override
	public void removeGroupForLinkedDocument(Group group, LinkedDocument linkedDoc) throws Exception {
		XmlAction op = new XmlAction(createArguments(group, linkedDoc), IRepositoryAdminService.ActionType.UNSECUR_LINKED_DOC);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		handleError(result);

	}

	@Override
	public void removeLinkedDocument(LinkedDocument linked) throws Exception {
		XmlAction op = new XmlAction(createArguments(linked), IRepositoryAdminService.ActionType.DELETE_LINKED_DOC);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		handleError(result);

	}

	@Override
	public void setObjectRunnableForGroup(Integer groupId, RepositoryItem item) throws Exception {
		XmlAction op = new XmlAction(createArguments(groupId, item), IRepositoryAdminService.ActionType.ALLOW_RUN);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		handleError(result);

	}

	@Override
	public void unsetObjectRunnableForGroup(Group group, RepositoryItem item) throws Exception {
		XmlAction op = new XmlAction(createArguments(group, item), IRepositoryAdminService.ActionType.FORBID_RUN);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		handleError(result);

	}

	@Override
	public void update(RepositoryItem di) throws Exception {
		XmlAction op = new XmlAction(createArguments(di), IRepositoryAdminService.ActionType.UPDATE_ITEM);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		handleError(result);

	}

	private Object handleError(String responseMessage) throws Exception {
		if (responseMessage.isEmpty()) {
			return null;
		}
		Object o = xstream.fromXML(responseMessage);
		if (o != null && o instanceof VanillaException) {
			throw (VanillaException) o;
		}
		return o;
	}

	@Override
	public List<RepositoryDirectory> getDeletedDirectories() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IRepositoryAdminService.ActionType.LIST_DELETED_DIRECTORIES);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List<RepositoryDirectory>) handleError(result);
	}

	@Override
	public List<RepositoryItem> getDeletedItems() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IRepositoryAdminService.ActionType.LIST_DELETED_ITEMS);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List<RepositoryItem>) handleError(result);
	}

	@Override
	public boolean purgeAllDeletedObjects() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IRepositoryAdminService.ActionType.PURGE_ALL);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (Boolean) handleError(result);
	}

	@Override
	public boolean purgeDeletedObjects(List<RepositoryDirectory> directories, List<RepositoryItem> items) throws Exception {
		XmlAction op = new XmlAction(createArguments(directories, items), IRepositoryAdminService.ActionType.PURGE_OBJECTS);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (Boolean) handleError(result);
	}

	@Override
	public boolean restoreDirectory(int directoryId) throws Exception {
		XmlAction op = new XmlAction(createArguments(directoryId), IRepositoryAdminService.ActionType.RESTORE_DIRECTORY);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (Boolean) handleError(result);
	}

	@Override
	public boolean restoreDirectoryItem(int directoryItemId) throws Exception {
		XmlAction op = new XmlAction(createArguments(directoryItemId), IRepositoryAdminService.ActionType.RESTORE_ITEM);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (Boolean) handleError(result);
	}

	@Override
	public void updateParameter(Parameter parameter) throws Exception {
		XmlAction op = new XmlAction(createArguments(parameter), IRepositoryAdminService.ActionType.UPDATE_PARAMETER);
		httpCommunicator.executeAction(op, xstream.toXML(op), false);
	}

	@Override
	public void purgeHistoric() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IRepositoryAdminService.ActionType.PURGE_HISTORIC);
		httpCommunicator.executeAction(op, xstream.toXML(op), false);
	}

	@Override
	public void setSecurityForElements(List<IRepositoryObject> elements, List<Group> securityGroup, List<Group> runnableGroups, List<Group> commentableGroup, List<Group> projectionGroup) throws Exception {
		XmlAction op = new XmlAction(createArguments(elements, securityGroup, runnableGroups, commentableGroup, projectionGroup), IRepositoryAdminService.ActionType.SECUR_ELEMENTS);
		httpCommunicator.executeAction(op, xstream.toXML(op), false);
	}

	@Override
	public void removeGroup(Group group) throws Exception {
		XmlAction op = new XmlAction(createArguments(group), IRepositoryAdminService.ActionType.REMOVE_GROUP);
		httpCommunicator.executeAction(op, xstream.toXML(op), false);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<ItemInstance> getItemInstances(int start, int end, int itemType) throws Exception {
		XmlAction op = new XmlAction(createArguments(start, end, itemType), IRepositoryAdminService.ActionType.GET_ITEM_INSTANCES);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List<ItemInstance>) handleError(result);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<ItemInstance> getItemInstances(int itemId, Date startDate, Date endDate, Integer groupId) throws Exception {
		XmlAction op = new XmlAction(createArguments(itemId, startDate, endDate, groupId), IRepositoryAdminService.ActionType.GET_ITEM_INSTANCES_FOR_ITEM);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List<ItemInstance>) handleError(result);
	}

	@Override
	public Integer addItemInstance(ItemInstance instance) throws Exception {
		XmlAction op = new XmlAction(createArguments(instance), IRepositoryAdminService.ActionType.ADD_ITEM_INSTANCE);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (Integer) handleError(result);
	}

	@Override
	public void removeItemInstances(int itemId) throws Exception {
		XmlAction op = new XmlAction(createArguments(itemId), IRepositoryAdminService.ActionType.REMOVE_ITEM_INSTANCE);
		httpCommunicator.executeAction(op, xstream.toXML(op), false);
	}

	@Override
	public ItemInstance getItemInstance(int instanceId) throws Exception {
		XmlAction op = new XmlAction(createArguments(instanceId), IRepositoryAdminService.ActionType.GET_ITEM_INSTANCE);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (ItemInstance) handleError(result);
	}

	@Override
	public ItemInstance getWorkflowInstance(int itemId, String key) throws Exception {
		XmlAction op = new XmlAction(createArguments(itemId, key), IRepositoryAdminService.ActionType.GET_ITEM_INSTANCE);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (ItemInstance) handleError(result);
	}

	@Override
	@SuppressWarnings("unchecked")
	public HashMap<RepositoryItem, Double> getTopTenItemConsumer(Integer itemType, Date startDate, Date endDate, Integer groupId) throws Exception {
		XmlAction op = new XmlAction(createArguments(itemType, startDate, endDate, groupId), IRepositoryAdminService.ActionType.GET_TOP_TEN);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (HashMap<RepositoryItem, Double>) handleError(result);
	}
}
