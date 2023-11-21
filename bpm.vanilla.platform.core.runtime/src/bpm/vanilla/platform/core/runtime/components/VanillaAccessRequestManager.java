package bpm.vanilla.platform.core.runtime.components;

import java.util.List;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IVanillaAccessRequestManager;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.beans.AccessRequest;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.AccessRequest.RequestOp;
import bpm.vanilla.platform.core.beans.ged.GedDocument;
import bpm.vanilla.platform.core.components.IGedComponent;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.remote.impl.components.RemoteGedComponent;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.platform.core.runtime.dao.security.AccessRequestDao;

/**
 * @see IVanillaAccessRequestManager
 * 
 * @author manu
 *
 */
public class VanillaAccessRequestManager extends AbstractVanillaManager implements IVanillaAccessRequestManager {

	private AccessRequestDao requestAccessDao;

	@Override
	public String getComponentName() {
		return getClass().getName();
	}
	@Override
	protected void init() throws Exception {
		this.requestAccessDao = getDao().getAccessRequestDao();
		if (this.requestAccessDao == null){
			throw new Exception("Missing RequestAccessDao!");
		}
		
		getLogger().info("Init done for " + getComponentName());
	}
	
	@Override
	public void addAccessRequest(AccessRequest request) throws Exception {
		getLogger().info("Adding an access request...");
		requestAccessDao.save(request);
	}
	@Override
	public void deleteAccessRequest(AccessRequest request) throws Exception {
		getLogger().info("Deleting an access request...");
		requestAccessDao.delete(request);
	}
	
	@Override
	public void updateAccessRequest(List<AccessRequest> requests) throws Exception {
		getLogger().info("Updating access requests...");
		
		int count = 0;
		for (AccessRequest req : requests) {
			requestAccessDao.update(req);
			count++;
		}
		getLogger().info("Updated " + count + " access requests");
	}
	
	@Override
	public List<AccessRequest> listAdminAllAccessRequest() throws Exception {
		getLogger().info("Listing Admin All access requests...");
		return (List<AccessRequest>) requestAccessDao.findAdminAllRequests();
	}
	@Override
	public List<AccessRequest> listAdminPendingAccessRequest() throws Exception {
		getLogger().info("Listing Admin Pending access requests...");
		return requestAccessDao.findAdminPendingRequests();
	}
	@Override
	public List<AccessRequest> listUserAllAccessRequest(int userId) throws Exception {
		getLogger().info("Listing user All access requests...");
		return requestAccessDao.findUserAllRequests(userId);
	}
	@Override
	public List<AccessRequest> listUserPendingAccessRequest(int userId) throws Exception {
		getLogger().info("Listing user Pending access requests...");
		return requestAccessDao.findUserPendingRequests(userId);
	}
	
	@Override
	public List<AccessRequest> listUserAllAccessDemands(int userId)
			throws Exception {
		getLogger().info("Listing user all access demands...");
		return requestAccessDao.findUserAllDemands(userId);
	}
	
	@Override
	public List<AccessRequest> listUserPendingAccessDemands(int userId)
			throws Exception {
		getLogger().info("Listing user Pending access demands...");
		return requestAccessDao.findUserPendingDemands(userId);
	}
	
	@Override
	public void approveAccessRequest(AccessRequest request) throws Exception {
		getLogger().info("Approving access request...");
		
		VanillaConfiguration vanillaConfig = ConfigurationManager.getInstance().getVanillaConfiguration();
		Repository repository = getDao().getRepositoryDao().findByPrimaryKey(request.getRepositoryId());
		Group group = getDao().getGroupDao().findByPrimaryKey(request.getGroupId()).get(0);
		
		if(request.getRequestOp() == RequestOp.RUN){
			IVanillaContext vCtx = new BaseVanillaContext(vanillaConfig.getVanillaServerUrl(), 
					vanillaConfig.getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN), 
					vanillaConfig.getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD));
			
			Group dummy = new Group();dummy.setId(-1);
			IRepositoryApi repSock = new RemoteRepositoryApi(
					new BaseRepositoryContext(vCtx, dummy, repository));
			
			RepositoryItem item = repSock.getRepositoryService().getDirectoryItem(request.getItemId());
			
			getLogger().info("Authorising group " + group.getName() + " to run object " + item.getItemName());
			repSock.getAdminService().setObjectRunnableForGroup(group.getId(), item);
		}
		else if(request.getRequestOp() == RequestOp.VIEW_DOC_SEARCH){
			IGedComponent gedComponent = new RemoteGedComponent(vanillaConfig.getVanillaServerUrl(), 
					vanillaConfig.getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN), 
					vanillaConfig.getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD));
			
			GedDocument gedDocument = gedComponent.getDocumentVersionById(request.getItemId()).getParent();
			
			getLogger().info("Authorising group " + group.getName() + " to access the document " + gedDocument.getName() + ".");
			gedComponent.addAccess(gedDocument.getId(), group.getId(), repository.getId());
		}
		else {
			throw new Exception("The request type " + request.getRequestOp().getOperationName() + " is not supported yet.");
		}
	
		//already been updated by the client side
		requestAccessDao.update(request);
	}
	
	@Override
	public void refuseAccessRequest(AccessRequest request) throws Exception {
		getLogger().info("Refusing access request...");
		
		requestAccessDao.update(request);
	}
}
