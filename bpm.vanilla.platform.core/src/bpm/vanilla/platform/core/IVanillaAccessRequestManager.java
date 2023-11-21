package bpm.vanilla.platform.core;

import java.util.List;

import bpm.vanilla.platform.core.beans.AccessRequest;
import bpm.vanilla.platform.core.beans.VanillaLogs.Level;
import bpm.vanilla.platform.core.xstream.IXmlActionType;

public interface IVanillaAccessRequestManager {

	public static enum ActionType implements IXmlActionType{
		ADD_REQUEST(Level.INFO), UPDATE_REQUESTS(Level.INFO), DELETE_REQUEST(Level.INFO),
		LIST_ADMIN_PENDING_REQUESTS(Level.DEBUG), LIST_ADMIN_ALL_REQUESTS(Level.DEBUG),
		LIST_USER_PENDING_REQUESTS(Level.DEBUG), LIST_USER_ALL_REQUESTS(Level.DEBUG),
		LIST_USER_PENDING_DEMANDS(Level.DEBUG), LIST_USER_ALL_DEMANDS(Level.DEBUG),
		APPROVE_REQUEST(Level.INFO), REFUSE_REQUEST(Level.INFO);
		
		private Level level;

		ActionType(Level level) {
			this.level = level;
		}
		
		@Override
		public Level getLevel() {
			return level;
		}
	}
	
	public void addAccessRequest(AccessRequest request) throws Exception;
	
	public void updateAccessRequest(List<AccessRequest> requests) throws Exception;
	
	public void deleteAccessRequest(AccessRequest request) throws Exception;
	
	/**
	 * Lists access requests to process for admin
	 * 
	 * @return the list of pending requests
	 * @throws Exception
	 */
	public List<AccessRequest> listAdminPendingAccessRequest() throws Exception;
	
	/**
	 * Lists all requests for admin
	 * @return the list of requests (including processed ones)
	 * @throws Exception
	 */
	public List<AccessRequest> listAdminAllAccessRequest() throws Exception;
	
	/**
	 * Lists user pending requests
	 * 
	 * @return the list of the user's group pending requests
	 * @throws Exception
	 */
	public List<AccessRequest> listUserPendingAccessRequest(int userId) throws Exception;
	
	/**
	 * Lists all user's requests
	 * 
	 * @return the list of all (including processed ones) the user's group requests
	 * @throws Exception
	 */
	public List<AccessRequest> listUserAllAccessRequest(int userId) throws Exception;
	
	public List<AccessRequest> listUserPendingAccessDemands(int userId) throws Exception;
	
	public List<AccessRequest> listUserAllAccessDemands(int userId) throws Exception;
	
	public void approveAccessRequest(AccessRequest request) throws Exception;
	
	public void refuseAccessRequest(AccessRequest request) throws Exception;
}
