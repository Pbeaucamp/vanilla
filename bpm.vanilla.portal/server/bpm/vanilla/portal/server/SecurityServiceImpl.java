package bpm.vanilla.portal.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.apache.log4j.Logger;

import bpm.gwt.commons.client.services.exception.ServiceException;
import bpm.gwt.commons.server.security.CommonConfiguration;
import bpm.gwt.commons.server.security.CommonSessionHelper;
import bpm.gwt.commons.server.security.CommonSessionManager;
import bpm.gwt.commons.shared.ILoginManager;
import bpm.gwt.commons.shared.InfoConnection;
import bpm.gwt.commons.shared.InfoUser;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.IVanillaSecurityManager;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.GroupProjection;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.components.ReportHistoricComponent;
import bpm.vanilla.platform.core.config.ConfigurationException;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.remote.impl.components.RemoteHistoricReportComponent;
import bpm.vanilla.platform.core.repository.Comment;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.platform.core.repository.SecuredCommentObject;
import bpm.vanilla.portal.client.services.SecurityService;
import bpm.vanilla.portal.server.security.PortalSession;
import bpm.vanillahub.remote.RemoteAdminManager;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * This is the server side service. For usage and external use, check interface,
 * here s only dev' explanation
 * 
 * @author manu
 * 
 */
public class SecurityServiceImpl extends RemoteServiceServlet implements SecurityService, ILoginManager {

	private static final long serialVersionUID = 7072320953553321097L;

	private Logger logger = Logger.getLogger(SecurityServiceImpl.class);
	private CommonConfiguration portalConfig;
	private String hubRuntimeUrl;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		logger.info("Initing Portal's SecurityService...");
		try {
			portalConfig = CommonConfiguration.getInstance();
			
			VanillaConfiguration configuration = ConfigurationManager.getInstance().getVanillaConfiguration();
			logger = Logger.getLogger(this.getClass());
			this.hubRuntimeUrl = configuration.getProperty(VanillaConfiguration.P_HUB_RUNTIME_URL);
		} catch (ConfigurationException ex) {
			String errMsg = "Failed to init Portal's SecurityService : " + ex.getMessage();
			logger.fatal(errMsg);
			throw new ServletException(errMsg, ex);
		}
		logger.info("Portal's SecurityService is ready.");
	}

	@Override
	public void initSession() throws ServiceException {
		// We create an empty session
		try {
			String sessionId = CommonSessionManager.createSession(PortalSession.class);
			CommonSessionHelper.setCurrentSessionId(sessionId, getThreadLocalRequest());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to create a session.", e);
		}
	}

	private PortalSession getSession() throws ServiceException {
		return CommonSessionHelper.getCurrentSession(getThreadLocalRequest(), PortalSession.class);
	}

	@Override
	public void initHubSession(InfoUser infoUser) throws ServiceException {
		PortalSession session = getSession();
		try {
			String serverSessionId = getServerSessionId(infoUser.getUser());
			session.initSession(infoUser.getUser(), serverSessionId, getLocale());
		} catch (Throwable e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		}
	}

	private Locale getLocale() {
		return getThreadLocalRequest() != null ? getThreadLocalRequest().getLocale() : null;
	}

	@Override
	public InfoUser changeCurrentGroup(InfoUser infoUser, Group group) throws ServiceException {
		PortalSession session = getSession();

		List<Group> availableGroups;
		try {
			User user = portalConfig.getRootVanillaApi().getVanillaSecurityManager().getUserById(session.getUser().getId());
			availableGroups = portalConfig.getRootVanillaApi().getVanillaSecurityManager().getGroups(user);

			Group target = null;

			for (Group gr : availableGroups) {
				if (gr.getId().equals(group.getId())) {
					target = gr;
					break;
				}
			}

			if (target == null) {
				throw new ServiceException("User was not allowed to change to group : " + group.getName());
			}
			else {
				session.changeCurrentGroup(infoUser, target);
				
				infoUser.clearWebapps();
				session.setWebapplicationRights(infoUser);
				
				return infoUser;
			}
		} catch (Exception e) {
			String msg = "Error changing groups : " + e.getMessage();
			logger.error(msg, e);
			throw new ServiceException(msg);
		}
	}

	@Override
	public List<Group> getAvailableGroupsForDisplay(int itemId, boolean isDirectory) throws ServiceException {
		PortalSession session = getSession();
		List<Group> allGroups = getAllGroups();

		if(!isDirectory) {
			RepositoryItem item;
			try {
				item = session.getRepository().getItem(itemId);
			} catch (Exception e) {
				e.printStackTrace();
				throw new ServiceException("Unable to get the item with id = " + itemId + ": " + e.getMessage());
			}
	
			List<Integer> groupIds;
			try {
				groupIds = session.getRepositoryConnection().getAdminService().getAllowedGroupId(item);
			} catch (Exception e) {
				e.printStackTrace();
				throw new ServiceException("Unable to get the allowed group for item with id = " + itemId + ": " + e.getMessage());
			}
	
			List<Group> availableGroups = new ArrayList<Group>();
			for (Group gr : allGroups) {
				for (Integer grId : groupIds) {
					if (gr.getId().equals(grId)) {
						availableGroups.add(gr);
						break;
					}
				}
			}
	
			return availableGroups;
		}
		else {
			RepositoryDirectory directory;
			try {
				directory = session.getRepository().getDirectory(itemId);
			} catch (Exception e) {
				e.printStackTrace();
				throw new ServiceException("Unable to get the directory with id = " + itemId + ": " + e.getMessage());
			}
	
			List<Group> groups;
			try {
				groups = session.getRepositoryConnection().getAdminService().getGroupsForDirectory(directory);
			} catch (Exception e) {
				e.printStackTrace();
				throw new ServiceException("Unable to get the allowed group for directory with id = " + itemId + ": " + e.getMessage());
			}

			List<Group> availableGroups = new ArrayList<Group>();
			for (Group userGroup : allGroups) {
				for (Group gr : groups) {
					if (userGroup.getId().equals(gr.getId())) {
						availableGroups.add(userGroup);
						break;
					}
				}
			}
	
			return availableGroups;
		}
	}

	@Override
	public void udpateTextGroup(List<Integer> grIds, String groupText) throws ServiceException {
		PortalSession session = getSession();
		IVanillaSecurityManager manager = session.getVanillaApi().getVanillaSecurityManager();

		for (Integer grId : grIds) {
			Group gr;
			try {
				gr = manager.getGroupById(grId);
			} catch (Exception e) {
				e.printStackTrace();
				throw new ServiceException("Unable to get group with id = " + grId, e);
			}

			if (gr == null) {
				throw new ServiceException("Unable to get group with id = " + grId);
			}

			gr.setCustom1(groupText);

			try {
				manager.updateGroup(gr);
			} catch (Exception e) {
				e.printStackTrace();
				throw new ServiceException("Unable to update group with id = " + grId, e);
			}
		}
	}

	@Override
	public List<Group> getAllGroups() throws ServiceException {
		PortalSession session = getSession();
		try {
			return session.getVanillaApi().getVanillaSecurityManager().getGroups();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to get all the groups.", e);
		}
	}

	@Override
	public List<Group> getCommentableGroups(int itemId, boolean isDirectory) throws ServiceException {
		PortalSession session = getSession();

		List<Group> allGroups = getAllGroups();
		List<SecuredCommentObject> secObjects;
		try {
			if(isDirectory) {
				secObjects = session.getRepositoryConnection().getDocumentationService().getSecuredCommentObjects(itemId, Comment.DIRECTORY);
			}
			else {
				secObjects = session.getRepositoryConnection().getDocumentationService().getSecuredCommentObjects(itemId, Comment.ITEM);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to get groups for comments.", e);
		}

		List<Group> commentableGroups = new ArrayList<Group>();
		if (secObjects != null && allGroups != null) {
			for (SecuredCommentObject sec : secObjects) {
				for(Group gr : allGroups) {
					if(sec.getGroupId().equals(gr.getId())) {
						commentableGroups.add(gr);
						break;
					}
				}
			}
		}

		return commentableGroups;
	}

	@Override
	public List<Group> getGroupProjections(int itemId) throws ServiceException {
		PortalSession session = getSession();

		List<Group> allGroups = getAllGroups();
		List<GroupProjection> actualGroupProjections;
		try {
			actualGroupProjections = session.getVanillaApi().getVanillaSecurityManager().getGroupProjectionsByFasdId(itemId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to get groups for comments.", e);
		}

		List<Group> commentableGroups = new ArrayList<Group>();
		if (actualGroupProjections != null && allGroups != null) {
			for (GroupProjection sec : actualGroupProjections) {
				for(Group gr : allGroups) {
					if(sec.getGroupId() == gr.getId().intValue()) {
						commentableGroups.add(gr);
						break;
					}
				}
			}
		}

		return commentableGroups;
	}

	@Override
	public void updateGroupRights(int itemId, List<Group> toAdd, List<Group> toRemove, boolean isDirectory, TypeGroupRights type) throws ServiceException {
		PortalSession session = getSession();
		
		try {
			IRepositoryApi repositoryApi = session.getRepositoryConnection();
			
			if(type == TypeGroupRights.DISPLAY) {
				for(Group gr : toAdd) {
					if(isDirectory) {
						repositoryApi.getAdminService().addGroupForDirectory(gr.getId(), itemId);
					}
					else {
						repositoryApi.getAdminService().addGroupForItem(gr.getId(), itemId);
					}
				}
				
				for(Group gr : toRemove) {
					if(isDirectory) {
						repositoryApi.getAdminService().removeGroupForDirectory(gr.getId(), itemId);
					}
					else {
						repositoryApi.getAdminService().removeGroupForItem(gr.getId(), itemId);
					}
				}
			}
			else if(type == TypeGroupRights.EXECUTE) {
				RepositoryItem dummy = new RepositoryItem();
				dummy.setId(itemId);
				
				for(Group gr : toAdd) {
					repositoryApi.getAdminService().setObjectRunnableForGroup(gr.getId(), dummy);
				}
				
				for(Group gr : toRemove) {
					repositoryApi.getAdminService().unsetObjectRunnableForGroup(gr, dummy);
				}
			}
			else if(type == TypeGroupRights.COMMENT) {
				if(isDirectory) {
					for(Group gr : toAdd) {
						SecuredCommentObject secObject = new SecuredCommentObject();
						secObject.setGroupId(gr.getId());
						secObject.setObjectId(itemId);
						secObject.setType(Comment.DIRECTORY);
						
						repositoryApi.getDocumentationService().addSecuredCommentObject(secObject);
					}
					
					for(Group gr : toRemove) {
						repositoryApi.getDocumentationService().removeSecuredCommentObject(gr.getId(), itemId, Comment.DIRECTORY);
					}
				}
				else {
					for(Group gr : toAdd) {
						SecuredCommentObject secObject = new SecuredCommentObject();
						secObject.setGroupId(gr.getId());
						secObject.setObjectId(itemId);
						secObject.setType(Comment.ITEM);
						
						repositoryApi.getDocumentationService().addSecuredCommentObject(secObject);
					}
					
					for(Group gr : toRemove) {
						repositoryApi.getDocumentationService().removeSecuredCommentObject(gr.getId(), itemId, Comment.ITEM);
					}
				}
			}
			else if(type == TypeGroupRights.PROJECTION) {
				IVanillaAPI vanillaApi = session.getVanillaApi();
				
				for(Group gr : toAdd) {
					GroupProjection gp = new GroupProjection();
					gp.setFasdId(itemId);
					gp.setGroupId(gr.getId());
					
					vanillaApi.getVanillaSecurityManager().addGroupProjection(gp);
				}
				
				for(Group gr : toRemove) {
					GroupProjection gp = new GroupProjection();
					gp.setFasdId(itemId);
					gp.setGroupId(gr.getId());
					
					vanillaApi.getVanillaSecurityManager().deleteGroupProjection(gp);
				}
			}
			else if(type == TypeGroupRights.HISTORIC) {
				IVanillaAPI vanillaApi = session.getVanillaApi();
				
				IVanillaContext context = new BaseVanillaContext(vanillaApi.getVanillaUrl(), session.getUser().getLogin(), session.getUser().getPassword());
				ReportHistoricComponent component = new RemoteHistoricReportComponent(context);
				
				int repositoryId = session.getCurrentRepository().getId();
				
				for(Group gr : toAdd) {
					component.grantHistoricAccess(gr.getId(), itemId, repositoryId);
				}
				
				for(Group gr : toRemove) {
					component.removeHistoricAccess(gr.getId(), itemId, repositoryId);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to update group's rights", e);
		}
	}

	@Override
	public void updateItemGed(int itemId, boolean value, TypeItemGed typeItemGed) throws ServiceException {
		PortalSession session = getSession();

		try {
			IRepositoryApi repositoryApi = session.getRepositoryConnection();
			RepositoryItem item = repositoryApi.getRepositoryService().getDirectoryItem(itemId);
			
			if(item != null) {
				if(typeItemGed == TypeItemGed.GED_AVAILABLE) {
					item.setAvailableGed(value);
					
					if(!value) {
						item.setRealtimeGed(false);
						item.setCreateEntry(false);
					}
				}
				else if(typeItemGed == TypeItemGed.REALTIME) {
					item.setRealtimeGed(value);
					
					if(!value) {
						item.setCreateEntry(false);
					}
				}
				else if(typeItemGed == TypeItemGed.CREATE_ENTRY) {
					item.setCreateEntry(value);
				}
				
				repositoryApi.getAdminService().update(item);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to update item's Document Manager informations.", e);
		}
	}

	@Override
	public boolean isConnectedToVanilla() throws Exception {
		return true;
	}

	@Override
	public User authentify(String login, String password) throws Exception {
		//Not used
		return null;
	}

	@Override
	public String getServerSessionId(User user) throws Exception {
		RemoteAdminManager manager = new RemoteAdminManager(hubRuntimeUrl, null, getLocale());
		return manager.connect(user);
	}

	@Override
	public InfoConnection getConnectionInformations() {
		//Not used
		return null;
	}
}
