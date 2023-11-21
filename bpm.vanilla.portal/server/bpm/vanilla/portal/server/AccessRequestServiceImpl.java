package bpm.vanilla.portal.server;


import java.util.Date;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.apache.log4j.Logger;

import bpm.gwt.commons.client.services.exception.ServiceException;
import bpm.gwt.commons.server.security.CommonConfiguration;
import bpm.gwt.commons.server.security.CommonSessionHelper;
import bpm.gwt.commons.shared.repository.DocumentVersionDTO;
import bpm.gwt.commons.shared.repository.PortailRepositoryItem;
import bpm.vanilla.platform.core.IVanillaAccessRequestManager;
import bpm.vanilla.platform.core.IVanillaSecurityManager;
import bpm.vanilla.platform.core.beans.AccessRequest;
import bpm.vanilla.platform.core.beans.AccessRequest.RequestAnswer;
import bpm.vanilla.platform.core.beans.AccessRequest.RequestOp;
import bpm.vanilla.platform.core.config.ConfigurationException;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.portal.client.services.AccessRequestService;
import bpm.vanilla.portal.server.security.PortalSession;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;


public class AccessRequestServiceImpl extends RemoteServiceServlet implements AccessRequestService {

	private static final long serialVersionUID = -5940400433396852473L;
	
	private Logger logger = Logger.getLogger(this.getClass());
	private CommonConfiguration portalConfig;
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		
		logger.info("Initing Portal's AccessRequestService...");
		try {
			portalConfig = CommonConfiguration.getInstance();
			logger = Logger.getLogger(this.getClass());
			
		} catch (ConfigurationException ex) {
			String errMsg = "Failed to init Portal's ParameterService : " + ex.getMessage();
			logger.fatal(errMsg);
			throw new ServletException(errMsg, ex);
		}
		logger.info("Portal's AccessRequestService is ready.");
	}

	private PortalSession getSession() throws ServiceException {
		return CommonSessionHelper.getCurrentSession(getThreadLocalRequest(), PortalSession.class);
	}
	
	@Override
	public void addAccessRequestOnRepositoryItem(PortailRepositoryItem dto, String message) throws ServiceException {
		logger.info("Trying to add access request");
		
		PortalSession session = getSession();
		
		IVanillaAccessRequestManager requestManager = session.getAccessRequestManager();
		
		try {
			//get owner
			RepositoryItem item = session.getRepository().getItem(dto.getId());
			int ownerId = item.getOwnerId();
			
			AccessRequest request = new AccessRequest(
						session.getCurrentRepository().getId(), 
						dto.getId(), 
						session.getCurrentGroup().getId(), 
						session.getUser().getId(), 
						new Date(),
						ownerId,
						message,
						RequestOp.RUN,
						null, //answer date
						null, //answer info
						RequestAnswer.PENDING, //answer operation
						0, //answer user id
						true, //is userUpdated
						false //is adminUpdated
					);

			requestManager.addAccessRequest(request);
		} catch (Exception e) {
			String errmsg = "Failed to add access request : " + e.getMessage();
			logger.error(errmsg, e);
			throw new ServiceException(errmsg);
		}
		
		logger.info("Access Request sent for object with id = " + dto.getId());
	}

	@Override
	public void addAccessRequestGedDocument(DocumentVersionDTO dto, String message) throws ServiceException {
		logger.info("Trying to add access request");
		
		PortalSession session = getSession();
		
		IVanillaAccessRequestManager requestManager = session.getAccessRequestManager();
		
		try {
			
			AccessRequest request = new AccessRequest(
						session.getCurrentRepository().getId(), 
						dto.getId(), 
						session.getCurrentGroup().getId(), 
						session.getUser().getId(), 
						new Date(),
						dto.getDocumentParent().getCreatorId(),
						message,
						RequestOp.VIEW_DOC_SEARCH,
						null, //answer date
						null, //answer info
						RequestAnswer.PENDING, //answer operation
						0, //answer user id
						true, //is userUpdated
						false //is adminUpdated
					);

			requestManager.addAccessRequest(request);
		} catch (Exception e) {
			String errmsg = "Failed to add access request : " + e.getMessage();
			logger.error(errmsg, e);
			throw new ServiceException(errmsg);
		}
		
		logger.info("Access Request sent for document version with id = " + dto.getId());
	}
	
	/**
	 * the requests i need to answer
	 * @param showAllDemands
	 * @param callback
	 */
	@Override
	public List<AccessRequest> getMyAccessRequests(boolean showAllDemands) throws ServiceException {
	
		logger.info("Trying to get my access requests");
		
		PortalSession session = getSession();
		
		IVanillaAccessRequestManager requestManager = session.getAccessRequestManager();
		
		try {
			List<AccessRequest> requests;
			if(session.getUser().isSuperUser() && !showAllDemands) {
				requests = requestManager.listAdminPendingAccessRequest();
			}
			else if(session.getUser().isSuperUser()) {
				requests = requestManager.listAdminAllAccessRequest();
			}
			else if (!showAllDemands) {
				requests = requestManager.listUserPendingAccessRequest(session.getUser().getId());
			}
			else {
				requests = requestManager.listUserAllAccessRequest(session.getUser().getId());
			}
			
			IVanillaSecurityManager secManager = portalConfig.getRootVanillaApi().getVanillaSecurityManager();
			//parse dtos and insert values (name instead of id)
			for (AccessRequest request : requests) {
				
				try {
					if(request.getUserId() > 0) {
						String userName = secManager.getUserById(request.getUserId()).getName();
						request.setUserName(userName);
					}
					else {
						request.setUserName("Unknown");
					}
				} catch (Exception e) {
					e.printStackTrace();
					request.setUserName("Unknown");
				}

				try {
					if(request.getGroupId() > 0) {
						String groupName = secManager.getGroupById(request.getGroupId()).getName();
						request.setGroupName(groupName);
					}
					else {
						request.setGroupName("Unknown");
					}
				} catch (Exception e) {
					e.printStackTrace();
					request.setGroupName("Unknown");
				}
				
				if(request.getRequestOp() == RequestOp.VIEW_DOC_SEARCH){
					try {
						if(request.getItemId() > 0) {
							String objectName = session.getGedComponent().getDocumentVersionById(request.getItemId()).getParent().getName();
							request.setItemName(objectName);
						}
						else {
							request.setItemName("Unknown");
						}
					} catch (Exception e) {
						e.printStackTrace();
						request.setItemName("Unknown");
					}
				}
				else {
					try {
						if(request.getItemId() > 0) {
							String objectName = session.getRepository().getItem(request.getItemId()).getItemName();
							request.setItemName(objectName);
						}
						else {
							request.setItemName("Unknown");
						}
					} catch (Exception e) {
						e.printStackTrace();
						request.setItemName("Unknown");
					}
				}
			}
			
			return requests;
		} catch (Exception e) {
			String errmsg = "Failed to list access request : " + e.getMessage();
			logger.error(errmsg, e);
			throw new ServiceException(errmsg);
		}
	}
	
	/**
	 * the requests i have made
	 * @param showAllDemands
	 * @param callback
	 */
	@Override
	public List<AccessRequest> getMyAccessDemands(boolean showAllDemands) throws ServiceException {
		logger.info("Trying to get my access demands");
		
		PortalSession session = getSession();
		
		IVanillaAccessRequestManager requestManager = session.getAccessRequestManager();
		
		try {
			List<AccessRequest> requests;
			if (!showAllDemands)
				requests = requestManager.listUserPendingAccessDemands(session.getUser().getId());
			else 
				requests = requestManager.listUserAllAccessDemands(session.getUser().getId());
			
			IVanillaSecurityManager secManager = portalConfig.getRootVanillaApi().getVanillaSecurityManager();
			//parse dtos and insert values (name instead of id)
			for (AccessRequest request : requests) {
				
				try {
					if(request.getUserId() > 0) {
						String userName = secManager.getUserById(request.getUserId()).getName();
						request.setUserName(userName);
					}
					else {
						request.setUserName("Unknown");
					}
				} catch (Exception e) {
					e.printStackTrace();
					request.setUserName("Unknown");
				}
				
				try {
					if(request.getRequestUserId() > 0) {
						String requestUserName = secManager.getUserById(request.getRequestUserId()).getName();
						request.setRequestUserName(requestUserName);
					}
					else {
						request.setRequestUserName("Unknown");
					}
				} catch (Exception e) {
					e.printStackTrace();
					request.setRequestUserName("Unknown");
				}
				
				try {
					if(request.getGroupId() > 0) {
						String groupName = secManager.getGroupById(request.getGroupId()).getName();
						request.setGroupName(groupName);
					}
					else {
						request.setGroupName("Unknown");
					}
				} catch (Exception e) {
					e.printStackTrace();
					request.setGroupName("Unknown");
				}
					
				if(request.getRequestOp() == RequestOp.VIEW_DOC_SEARCH){
					try {
						if(request.getItemId() > 0) {
							String objectName = session.getGedComponent().getDocumentVersionById(request.getItemId()).getParent().getName();
							request.setItemName(objectName);
						}
						else {
							request.setItemName("Unknown");
						}
					} catch (Exception e) {
						e.printStackTrace();
						request.setItemName("Unknown");
					}
				}
				else {
					try {
						if(request.getItemId() > 0) {
							String objectName = session.getRepository().getItem(request.getItemId()).getItemName();
							request.setItemName(objectName);
						}
						else {
							request.setItemName("Unknown");
						}
					} catch (Exception e) {
						e.printStackTrace();
						request.setItemName("Unknown");
					}
				}
			}
			
			return requests;
		} catch (Exception e) {
			String errmsg = "Failed to list access request : " + e.getMessage();
			logger.error(errmsg, e);
			throw new ServiceException(errmsg);
		}
	}
	
	@Override
	public void approveRequest(AccessRequest request) throws ServiceException {
	
		logger.info("Trying to approve access request");
		
		PortalSession session = getSession();
		
		IVanillaAccessRequestManager requestManager = session.getAccessRequestManager();
		
		try {
			request.setAnswerDate(new Date());
			request.setAnswerUserId(session.getUser().getId());
			request.setAnswerOpId(RequestAnswer.ACCEPTED.getAnswerId());
			
			requestManager.approveAccessRequest(request);
		} catch (Exception e) {
			String errmsg = "Failed to approve access request : " + e.getMessage();
			logger.error(errmsg, e);
			throw new ServiceException(errmsg);
		}
	}
	
	@Override
	public void refuseRequest(AccessRequest request) throws ServiceException {
	
		logger.info("Trying to refuse access request");
		
		PortalSession session = getSession();
		
		IVanillaAccessRequestManager requestManager = session.getAccessRequestManager();
		
		try {
			request.setAnswerDate(new Date());
			request.setAnswerUserId(session.getUser().getId());
			request.setAnswerOpId(RequestAnswer.REFUSED.getAnswerId());
			
			requestManager.refuseAccessRequest(request);
		} catch (Exception e) {
			String errmsg = "Failed to refuse access request : " + e.getMessage();
			logger.error(errmsg, e);
			throw new ServiceException(errmsg);
		}
	}
}
