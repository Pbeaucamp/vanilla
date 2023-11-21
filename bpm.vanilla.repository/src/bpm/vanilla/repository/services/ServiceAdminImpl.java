package bpm.vanilla.repository.services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.GroupProjection;
import bpm.vanilla.platform.core.beans.ItemInstance;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.VanillaSession;
import bpm.vanilla.platform.core.beans.alerts.Subscriber;
import bpm.vanilla.platform.core.listeners.IVanillaEvent;
import bpm.vanilla.platform.core.listeners.event.impl.RepositoryItemEvent;
import bpm.vanilla.platform.core.repository.Comment;
import bpm.vanilla.platform.core.repository.DirectoryItemDependance;
import bpm.vanilla.platform.core.repository.ILinkedParameter;
import bpm.vanilla.platform.core.repository.IRepositoryObject;
import bpm.vanilla.platform.core.repository.LinkedDocument;
import bpm.vanilla.platform.core.repository.Parameter;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.platform.core.repository.SecuredCommentObject;
import bpm.vanilla.platform.core.repository.SecuredDirectory;
import bpm.vanilla.platform.core.repository.SecuredObject;
import bpm.vanilla.platform.core.repository.services.IRepositoryAdminService;
import bpm.vanilla.repository.beans.RepositoryDaoComponent;
import bpm.vanilla.repository.beans.RepositoryRuntimeComponent;
import bpm.vanilla.repository.beans.alert.SubscriberDAO;
import bpm.vanilla.repository.beans.datasprovider.ItemsDP;
import bpm.vanilla.repository.beans.historique.ReportHisto;
import bpm.vanilla.repository.beans.historique.SecurityReportHisto;
import bpm.vanilla.repository.beans.security.RunnableGroup;
import bpm.vanilla.repository.beans.security.SecuredLinked;

public class ServiceAdminImpl implements IRepositoryAdminService {

	private RepositoryRuntimeComponent component;
	private RepositoryDaoComponent repositoryDaoComponent;
	private int repositoryId;
	private int groupId;
	private User user;

	public ServiceAdminImpl(RepositoryRuntimeComponent repositoryRuntimeComponent, int repositoryId, int groupId, User user) throws Exception {
		this.component = repositoryRuntimeComponent;
		this.repositoryId = repositoryId;
		this.groupId = groupId;
		this.user = user;
		this.repositoryDaoComponent = component.getRepositoryDao(repositoryId);
	}

	@Override
	public void addGroupForDirectory(int groupId, int directoryId) throws Exception {
		SecuredDirectory secu = repositoryDaoComponent.getSecuredDirectoryDao().getSecuredDirectory(directoryId, groupId);

		if (secu != null) {
			return;
		}

		secu = new SecuredDirectory();
		secu.setDirectoryId(directoryId);
		secu.setGroupId(groupId);

		repositoryDaoComponent.getSecuredDirectoryDao().save(secu);
	}

	@Override
	public void addGroupForItem(int groupId, int directoryItemId) throws Exception {
		SecuredObject secu = repositoryDaoComponent.getSecuredObjectDao().getForItemAndGroup(directoryItemId, groupId);

		if (secu != null) {
			return;
		}

		secu = new SecuredObject();
		secu.setDirectoryItemId(directoryItemId);
		secu.setGroupId(groupId);

		repositoryDaoComponent.getSecuredObjectDao().save(secu);
	}

	@Override
	public List<Integer> getAllowedGroupId(RepositoryItem item) throws Exception {
		List<SecuredObject> l = repositoryDaoComponent.getSecuredObjectDao().getFor(item.getId());

		List<Integer> groupIds = new ArrayList<Integer>();
		for (SecuredObject sec : l) {
			groupIds.add(sec.getGroupId());
		}
		return groupIds;
	}

	@Override
	public List<Group> getAuthorizedGroupsForLinkedDocument(int linkedDocId) throws Exception {
		List<Group> groups = new ArrayList<Group>();

		for (SecuredLinked l : repositoryDaoComponent.getSecuredLinkedDao().getByLinkedDocId(linkedDocId)) {
			Group g = component.getVanillaRootApi().getVanillaSecurityManager().getGroupById(l.getGroupId());
			groups.add(g);
		}
		return groups;
	}

	@Override
	public List<Group> getGroupsForDirectory(RepositoryDirectory directory) throws Exception {
		List<SecuredDirectory> c = repositoryDaoComponent.getSecuredDirectoryDao().getFor(directory.getId());

		List<Group> groups = new ArrayList<Group>();
		if (c != null) {
			for (SecuredDirectory sec : c) {
				Group g = component.getVanillaRootApi().getVanillaSecurityManager().getGroupById(sec.getGroupId());
				groups.add(g);
			}
		}
		return groups;
	}

	@Override
	public List<SecuredDirectory> getSecuredDirectoriesForGroup(Group group) throws Exception {
		List<SecuredDirectory> c = repositoryDaoComponent.getSecuredDirectoryDao().getForGroup(group.getId());
		return c;
	}

	@Override
	public List<Group> getGroupsForItemId(int directoryItemId) throws Exception {
		List<SecuredObject> c = repositoryDaoComponent.getSecuredObjectDao().getFor(directoryItemId);

		List<Group> groups = new ArrayList<Group>();
		if (c != null) {
			for (SecuredObject sec : c) {
				Group g = component.getVanillaRootApi().getVanillaSecurityManager().getGroupById(sec.getGroupId());
				groups.add(g);
			}
		}
		return groups;
	}

	@Override
	public List<SecuredObject> getSecuredObjectForGroup(Group group) throws Exception {
		List<SecuredObject> c = repositoryDaoComponent.getSecuredObjectDao().getForGroup(group.getId());
		return c;
	}

	@Override
	public boolean isDirectoryAccessible(int directoryId, Integer groupId) throws Exception {
		SecuredDirectory c = repositoryDaoComponent.getSecuredDirectoryDao().getSecuredDirectory(directoryId, groupId);
		return c != null;
	}

	@Override
	public boolean isDirectoryItemAccessible(int directoryItemId, Integer groupId) throws Exception {
		SecuredObject c = repositoryDaoComponent.getSecuredObjectDao().getForItemAndGroup(directoryItemId, groupId);
		return c != null;
	}

	@Override
	public void removeGroupForDirectory(int groupId, int directoryId) throws Exception {
		for (SecuredDirectory d : repositoryDaoComponent.getSecuredDirectoryDao().getSecuredDirectory4UserDirectory(directoryId, groupId)) {
			repositoryDaoComponent.getSecuredDirectoryDao().delete(d);
		}
	}

	@Override
	public void removeGroupForItem(int groupId, int directoryId) throws Exception {
		SecuredObject d = repositoryDaoComponent.getSecuredObjectDao().getForItemAndGroup(directoryId, groupId);
		if (d != null) {
			repositoryDaoComponent.getSecuredObjectDao().delete(d);
		}
	}

	@Override
	public void setObjectRunnableForGroup(Integer groupId, RepositoryItem item) throws Exception {
		RunnableGroup rg = repositoryDaoComponent.getRunnableGroupDao().find(item.getId(), groupId);
		if (rg != null) {
			return;
		}
		else {
			rg = new RunnableGroup();
			rg.setDirectoryItemId(item.getId());
			rg.setGroupId(groupId);

			repositoryDaoComponent.getRunnableGroupDao().addRunnableGroup(rg);
		}
	}

	@Override
	public void addGroupForLinkedDocument(Group group, LinkedDocument linkedDoc) throws Exception {
		if (!repositoryDaoComponent.getSecuredLinkedDao().getByGroupIdAndLinkedId(group.getId(), linkedDoc.getId()).isEmpty()) {
			return;
		}

		SecuredLinked secu = new SecuredLinked();
		secu = new SecuredLinked();
		secu.setGroupId(group.getId());
		secu.setLinkedDocumentId(linkedDoc.getId());

		repositoryDaoComponent.getSecuredLinkedDao().save(secu);
	}

	@Override
	public void removeLinkedDocument(LinkedDocument linkedDoc) throws Exception {
		LinkedDocument d = repositoryDaoComponent.getLinkedDocumentDao().findByPrimaryKey(linkedDoc.getId());

		repositoryDaoComponent.getLinkedDocumentDao().delete(d);

		for (SecuredLinked s : repositoryDaoComponent.getSecuredLinkedDao().getByLinkedDocId(d.getId())) {
			repositoryDaoComponent.getSecuredLinkedDao().delete(s);
		}

	}

	@Override
	public List<LinkedDocument> getLinkedDocuments(int directoryItemId) throws Exception {
		return repositoryDaoComponent.getLinkedDocumentDao().getLinkedDocuments(directoryItemId);
	}

	@Override
	public void removeGroupForLinkedDocument(Group group, LinkedDocument linkedDocument) throws Exception {
		for (SecuredLinked l : repositoryDaoComponent.getSecuredLinkedDao().getByGroupIdAndLinkedId(group.getId(), linkedDocument.getId())) {
			repositoryDaoComponent.getSecuredLinkedDao().delete(l);
		}
	}

	@Override
	public void update(RepositoryItem newItem) throws Exception {
		if (newItem.getDirectoryId() == 1) {
			throw new Exception("This item cannot be updated/deplaced into that folder. Create a personnal folder using Vanilla Portal first.");
		}

		List<IVanillaEvent> events = new ArrayList<IVanillaEvent>();

		RepositoryItem item = repositoryDaoComponent.getItemDao().findByPrimaryKey(newItem.getId());

		String sessionId = null;
		for (VanillaSession s : component.getVanillaRootApi().getVanillaSystemManager().getActiveSessions()) {
			if (s.getUser().getId().equals(user.getId())) {
				sessionId = s.getUuid();
				break;
			}
		}

		if ((!item.isOn() && newItem.isOn()) || (item.isOn() && !newItem.isOn())) {
			RepositoryItemEvent event = new RepositoryItemEvent(component.getVanillaComponentIdentifier(repositoryId), item.isOn(), newItem.isOn(), RepositoryItemEvent.ITEM_STATE, repositoryId, groupId, item.getId(), item.getType(), item.getSubtype(), sessionId);
			events.add(event);
		}

		if (!newItem.isVisible() && item.isVisible()) {
			newItem.setDeletedBy(user.getId());
		}

		newItem.setDateModification(Calendar.getInstance().getTime());
		newItem.setModifiedBy(user.getId());

		repositoryDaoComponent.getItemDao().update(newItem);

		// RepositoryItemEvent updateEvent = new
		// RepositoryItemEvent(component.getVanillaComponentIdentifier(repositoryId),
		// null,
		// null, RepositoryItemEvent.EVENT_REPOSITORY_ITEM_UPDATED,
		// repositoryId, groupId, item.getId(), item.getType(),
		// item.getSubtype(), sessionId);
		// events.add(updateEvent);

		for (IVanillaEvent event : events) {
			try {
				component.getVanillaRootApi().getListenerService().fireEvent(event);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	@Override
	public boolean purgeDeletedObjects(List<RepositoryDirectory> dirsToPurge, List<RepositoryItem> itemsToPurge) throws Exception {
		if (!user.isSuperUser()) {
			throw new Exception(user.getLogin() + " cannot purge deleted directories and items");
		}

		List<RepositoryDirectory> directoryWithNoParentList = new ArrayList<RepositoryDirectory>();
		List<RepositoryItem> itemWithNoParentList = new ArrayList<RepositoryItem>();

		for (RepositoryDirectory directory : dirsToPurge) {
			if (directory != null) {
				component.getRepositoryDao(repositoryId).getCommentDao().deleteComments(directory.getId(), Comment.DIRECTORY);
				repositoryDaoComponent.getDirectoryDao().purge(directory);

				directoryWithNoParentList = repositoryDaoComponent.getDirectoryDao().findByParentId(repositoryId, directory.getId(), user.getId(), user.isSuperUser());
				for (RepositoryDirectory directoryWithoutParent : directoryWithNoParentList) {
					component.getRepositoryDao(repositoryId).getCommentDao().deleteComments(directoryWithoutParent.getId(), Comment.DIRECTORY);
					List<RepositoryDirectory> subs = new  ArrayList<>();
					subs.add(directoryWithoutParent);
					purgeDeletedObjects(subs, new ArrayList<RepositoryItem>());
//					repositoryDaoComponent.getDirectoryDao().purge(directoryWithoutParent);
				}

				itemWithNoParentList = repositoryDaoComponent.getItemDao().findByDirectoryId(directory.getId());
				for (RepositoryItem itemWithoutParent : itemWithNoParentList) {
					component.getRepositoryDao(repositoryId).getCommentDao().deleteComments(itemWithoutParent.getId(), Comment.ITEM);
					repositoryDaoComponent.getItemDao().purge(itemWithoutParent);
				}
			}
		}

		for (RepositoryItem dirItem : itemsToPurge) {
			component.getRepositoryDao(repositoryId).getCommentDao().deleteComments(dirItem.getId(), Comment.ITEM);
			repositoryDaoComponent.getItemDao().purge(dirItem);

			int key = dirItem.getId();

			List<ReportHisto> historiqueReport = repositoryDaoComponent.getReportHistoDao().getForItemId(key);
			for (ReportHisto rp : historiqueReport) {
				repositoryDaoComponent.getReportHistoDao().delete(rp);
			}

			List<ItemsDP> dpItems = repositoryDaoComponent.getItemsDpDao().findByItemId(key);
			for (ItemsDP itDp : dpItems) {
				repositoryDaoComponent.getItemsDpDao().delete(itDp);
			}

			List<Parameter> paramDao = repositoryDaoComponent.getParameterDao().findDirectoryItemId(key);
			for (Parameter mp : paramDao) {
				List<ILinkedParameter> linkedParam = mp.getRequestecParameters();
				for (ILinkedParameter lk : linkedParam) {
					repositoryDaoComponent.getParameterDao().delete(lk);
				}
				repositoryDaoComponent.getParameterDao().delete(mp);
			}

			List<DirectoryItemDependance> diDependance = repositoryDaoComponent.getDependanciesDao().getNeededForDirectoryItemId(key);
			if (!diDependance.isEmpty()) {
				List<DirectoryItemDependance> allDep = repositoryDaoComponent.getDependanciesDao().getAllReferenceTo(key);

				for (DirectoryItemDependance dip : allDep) {
					repositoryDaoComponent.getDependanciesDao().delete(dip);
				}
			}

			repositoryDaoComponent.getItemModelDao().deleteByItemId(key);
			repositoryDaoComponent.getLockDao().removeLock(key);
		}
		return true;
	}

	@Override
	public boolean restoreDirectory(int directoryId) throws Exception {
		if (!user.isSuperUser()) {
			throw new Exception(user.getLogin() + " cannot restore this RepositoryDirectory");
		}

		RepositoryDirectory dir = repositoryDaoComponent.getDirectoryDao().getDirectoryDelete(directoryId);

		if (dir == null) {
			throw new Exception("This RepositoryDirectory does not exist");
		}

		Date newDate = null;

		dir.setVisible(true);
		dir.setDateDeletion(newDate);
		dir.setDeletedBy(-1);

		repositoryDaoComponent.getDirectoryDao().restore(dir);
		return true;
	}

	@Override
	public boolean restoreDirectoryItem(int directoryItemId) throws Exception {
		if (!user.isSuperUser()) {
			throw new Exception(user.getLogin() + " cannot restore this Item");
		}

		RepositoryItem item = repositoryDaoComponent.getItemDao().findByPrimaryKey(directoryItemId);

		if (item == null) {
			throw new Exception("This Item does not exist");
		}

		if (item.isVisible()) {
			throw new Exception("RepositoryDirectory with id = " + directoryItemId + " is already restore");
		}

		item.setVisible(true);
		item.setDateDeletion(null);
		item.setDeletedBy(null);

		repositoryDaoComponent.getItemDao().restore(item);
		return true;
	}

	@Override
	public boolean canRun(int repositoryDirectoryItemId, int groupId) throws Exception {
		return repositoryDaoComponent.getRunnableGroupDao().get(repositoryId, repositoryDirectoryItemId, groupId);
		// return !c.isEmpty();
	}

	@Override
	public List<RepositoryDirectory> getDeletedDirectories() throws Exception {
		if (!user.isSuperUser()) {
			throw new Exception(user.getLogin() + " cannot get deleted directories");
		}
		List<RepositoryDirectory> directoriesList = repositoryDaoComponent.getDirectoryDao().findAllDelete();
		if (directoriesList.isEmpty()) {
			return null;
		}
		return directoriesList;
	}

	@Override
	public List<RepositoryItem> getDeletedItems() throws Exception {
		if (!user.isSuperUser()) {
			throw new Exception(user.getLogin() + " cannot get deleted directories");
		}

		List<RepositoryItem> directoryItems = repositoryDaoComponent.getItemDao().findAllDelete();
		return directoryItems;
	}

	@Override
	public void purgeHistoric() throws Exception {
		repositoryDaoComponent.getHistoricDao().purgeForUser(user.getId());
	}

	@Override
	public void updateParameter(Parameter parameter) throws Exception {
		repositoryDaoComponent.getParameterMgr().update(parameter);
	}

	@Override
	public String getGeneratedRelativeUrlFor(RepositoryItem item, Group group) throws Exception {
		// XXX NEVER CALLED BY ANYONE
		return null;
	}

	@Override
	public boolean purgeAllDeletedObjects() throws Exception {
		if (!user.isSuperUser()) {
			throw new Exception(user.getLogin() + " cannot purge deleted directories and items");
		}

		List<RepositoryDirectory> directoryWithNoParentList = new ArrayList<RepositoryDirectory>();
		List<RepositoryItem> itemWithNoParentList = new ArrayList<RepositoryItem>();

		List<RepositoryDirectory> dirList = repositoryDaoComponent.getDirectoryDao().findAllDelete();
		List<RepositoryItem> itemList = repositoryDaoComponent.getItemDao().findAllDelete();

		for (RepositoryDirectory directory : dirList) {
			component.getRepositoryDao(repositoryId).getCommentDao().deleteComments(directory.getId(), Comment.DIRECTORY);
			repositoryDaoComponent.getDirectoryDao().purge(directory);

			directoryWithNoParentList = repositoryDaoComponent.getDirectoryDao().findByParentId(repositoryId, directory.getId(), user.getId(), user.isSuperUser());
			for (RepositoryDirectory directoryWithoutParent : directoryWithNoParentList) {
				component.getRepositoryDao(repositoryId).getCommentDao().deleteComments(directoryWithoutParent.getId(), Comment.DIRECTORY);

				repositoryDaoComponent.getDirectoryDao().purge(directoryWithoutParent);
			}

			itemWithNoParentList = repositoryDaoComponent.getItemDao().findByDirectoryId(directory.getId());
			for (RepositoryItem itemWithoutParent : itemWithNoParentList) {
				component.getRepositoryDao(repositoryId).getCommentDao().deleteComments(itemWithoutParent.getId(), Comment.ITEM);

				repositoryDaoComponent.getItemDao().purge(itemWithoutParent);
			}
		}

		for (RepositoryItem item : itemList) {
			int key = item.getId();
			List<ReportHisto> historiqueReport = repositoryDaoComponent.getReportHistoDao().getForItemId(key);
			for (ReportHisto rp : historiqueReport) {
				repositoryDaoComponent.getReportHistoDao().delete(rp);
			}
			component.getRepositoryDao(repositoryId).getCommentDao().deleteComments(item.getId(), Comment.ITEM);
			repositoryDaoComponent.getItemDao().purge(item);

			List<ItemsDP> dpItems = repositoryDaoComponent.getItemsDpDao().findByItemId(key);
			for (ItemsDP itDp : dpItems) {
				repositoryDaoComponent.getItemsDpDao().delete(itDp);
			}

			List<Parameter> paramDao = repositoryDaoComponent.getParameterDao().findDirectoryItemId(key);
			for (Parameter mp : paramDao) {
				List<ILinkedParameter> linkedParam = mp.getRequestecParameters();
				for (ILinkedParameter lk : linkedParam) {
					repositoryDaoComponent.getParameterDao().delete(lk);
				}
				repositoryDaoComponent.getParameterDao().delete(mp);
			}

			List<DirectoryItemDependance> diDependance = repositoryDaoComponent.getDependanciesDao().getNeededForDirectoryItemId(key);
			if (!diDependance.isEmpty()) {
				List<DirectoryItemDependance> allDep = repositoryDaoComponent.getDependanciesDao().getAllReferenceTo(key);

				for (DirectoryItemDependance dip : allDep) {
					repositoryDaoComponent.getDependanciesDao().delete(dip);
				}
			}

			repositoryDaoComponent.getItemModelDao().deleteByItemId(key);
			repositoryDaoComponent.getLockDao().removeLock(key);
		}

		if (dirList.size() == 0 && itemList.size() == 0) {
			return false;
		}
		else {
			return true;
		}
	}

	@Override
	public void unsetObjectRunnableForGroup(Group group, RepositoryItem item) throws Exception {
		RunnableGroup rg = repositoryDaoComponent.getRunnableGroupDao().find(item.getId(), group.getId());
		if (rg != null) {
			repositoryDaoComponent.getRunnableGroupDao().delete(rg);
		}
	}

	@Override
	public void setSecurityForElements(List<IRepositoryObject> elements, List<Group> securityGroup, List<Group> runnableGroups, List<Group> commentableGroup, List<Group> projectionGroup) throws Exception {
		for (IRepositoryObject object : elements) {
			if (object instanceof RepositoryDirectory) {
				RepositoryDirectory dir = (RepositoryDirectory) object;

				removeSecurityForDirectory(dir);

				for (Group g : securityGroup) {
					addGroupForDirectory(g.getId(), dir.getId());
				}
				for (Group g : commentableGroup) {
					SecuredCommentObject secObject = new SecuredCommentObject();
					secObject.setGroupId(g.getId());
					secObject.setObjectId(dir.getId());
					secObject.setType(Comment.DIRECTORY);

					component.getServiceDocumentation(repositoryId, groupId, user, null).addSecuredCommentObject(secObject);
				}

			}
			else {
				RepositoryItem item = (RepositoryItem) object;

				removeSecurityForItem(item);

				for (Group g : securityGroup) {
					addGroupForItem(g.getId(), item.getId());
				}
				for (Group g : commentableGroup) {
					SecuredCommentObject secObject = new SecuredCommentObject();
					secObject.setGroupId(g.getId());
					secObject.setObjectId(item.getId());
					secObject.setType(Comment.ITEM);

					component.getServiceDocumentation(repositoryId, groupId, user, null).addSecuredCommentObject(secObject);
				}
				for (Group g : runnableGroups) {
					setObjectRunnableForGroup(g.getId(), item);
				}
				for (Group g : projectionGroup) {
					GroupProjection gp = new GroupProjection();
					gp.setFasdId(item.getId());
					gp.setGroupId(g.getId());
					component.getVanillaRootApi().getVanillaSecurityManager().addGroupProjection(gp);
				}

			}
		}

	}

	private void removeSecurityForItem(RepositoryItem item) throws Exception {
		try {
			List<Group> groups = getGroupsForItemId(item.getId());

			for (Group g : groups) {
				removeGroupForItem(g.getId(), item.getId());
			}
		} catch (Exception e) {
		}

		try {
			component.getServiceDocumentation(repositoryId, groupId, user, null).removeSecuredCommentObjects(item.getId(), Comment.ITEM);
		} catch (Exception e1) {
		}

		try {
			Collection<RunnableGroup> runGroup = repositoryDaoComponent.getRunnableGroupDao().getAllowedGroups(item.getId());
			Iterator<RunnableGroup> it = runGroup.iterator();
			while (it.hasNext()) {
				repositoryDaoComponent.getRunnableGroupDao().removeRunnableGroup(it.next());
			}
		} catch (Exception e) {
		}

		try {
			List<GroupProjection> gps = component.getVanillaRootApi().getVanillaSecurityManager().getGroupProjectionsByFasdId(item.getId());
			for (GroupProjection gp : gps) {
				component.getVanillaRootApi().getVanillaSecurityManager().deleteGroupProjection(gp);
			}
		} catch (Exception e) {
		}
	}

	private void removeSecurityForDirectory(RepositoryDirectory dir) throws Exception {
		try {
			List<Group> groups = getGroupsForDirectory(dir);

			for (Group g : groups) {
				removeGroupForDirectory(g.getId(), dir.getId());
			}
		} catch (Exception e) {
		}

		try {
			component.getServiceDocumentation(repositoryId, groupId, user, null).removeSecuredCommentObjects(dir.getId(), Comment.DIRECTORY);
		} catch (Exception e) {
		}

	}

	@Override
	public void removeGroup(Group group) throws Exception {

		SubscriberDAO subscriberDao = repositoryDaoComponent.getSubscriberDao();

		List<Subscriber> subs = subscriberDao.findByGroupId(group.getId());
		if (subs != null) {
			subscriberDao.deleteAll(subs);
		}

		List<SecuredDirectory> d = getSecuredDirectoriesForGroup(group);
		if (d != null) {
			repositoryDaoComponent.getSecuredDirectoryDao().deleteAll(d);
		}

		List<SecurityReportHisto> secReportHistos = repositoryDaoComponent.getSecurityReportHistoDao().getForGroupId(group.getId());
		if (secReportHistos != null) {
			repositoryDaoComponent.getSecurityReportHistoDao().deleteAll(secReportHistos);
		}

		List<SecuredLinked> securedLinkeds = repositoryDaoComponent.getSecuredLinkedDao().getByGroupId(group.getId());
		if (securedLinkeds != null) {
			repositoryDaoComponent.getSecuredLinkedDao().deleteAll(securedLinkeds);
		}

		List<SecuredObject> securedObjects = getSecuredObjectForGroup(group);
		if (securedObjects != null) {
			repositoryDaoComponent.getSecuredObjectDao().deleteAll(securedObjects);
		}

		Collection<RunnableGroup> runnablesGroup = repositoryDaoComponent.getRunnableGroupDao().getByGroupId(group.getId());
		if (runnablesGroup != null) {
			repositoryDaoComponent.getRunnableGroupDao().deleteAll(runnablesGroup);
		}
	}

	@Override
	public List<ItemInstance> getItemInstances(int start, int end, int itemType) throws Exception {
		return repositoryDaoComponent.getItemInstanceDAO().getItemInstances(start, end, itemType);
	}

	@Override
	public List<ItemInstance> getItemInstances(int itemId, Date startDate, Date endDate, Integer groupId) throws Exception {
		return repositoryDaoComponent.getItemInstanceDAO().getItemInstances(itemId, startDate, endDate, groupId);
	}

	@Override
	public Integer addItemInstance(ItemInstance instance) throws Exception {
		return repositoryDaoComponent.getItemInstanceDAO().save(instance);
	}

	@Override
	public void removeItemInstances(int itemId) throws Exception {
		List<ItemInstance> instances = repositoryDaoComponent.getItemInstanceDAO().getItemInstances(itemId, true);
		repositoryDaoComponent.getItemInstanceDAO().delete(instances);
	}

	@Override
	public ItemInstance getItemInstance(int instanceId) throws Exception {
		return repositoryDaoComponent.getItemInstanceDAO().getItemInstance(instanceId);
	}

	@Override
	public ItemInstance getWorkflowInstance(int itemId, String key) throws Exception {
		return repositoryDaoComponent.getItemInstanceDAO().getWorkflowInstance(itemId, key);
	}
	
	@Override
	public HashMap<RepositoryItem, Double> getTopTenItemConsumer(Integer itemType, Date startDate, Date endDate, Integer groupId) throws Exception {
		return repositoryDaoComponent.getItemInstanceDAO().getTopTenItemConsumer(itemType, startDate, endDate, groupId);
	}
}
