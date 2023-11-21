package bpm.gwt.commons.client.services;

import java.util.HashMap;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.RpcRequestBuilder;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

import bpm.gwt.commons.client.services.exception.ServiceException;
import bpm.gwt.commons.shared.InfoConnection;
import bpm.gwt.commons.shared.InfoUser;
import bpm.gwt.commons.shared.utils.PasswordState;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.PasswordBackup;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.Settings;

@RemoteServiceRelativePath("loginService")
public interface LoginService extends RemoteService {
	
	public static class Connect{
		private static LoginServiceAsync instance;
		
		public static LoginServiceAsync getInstance(){
			if (instance == null){
				instance = (LoginServiceAsync) GWT.create(LoginService.class);
			}

			ServiceDefTarget target = (ServiceDefTarget) instance;
			target.setRpcRequestBuilder(null);
			return instance;
		}
		
		public static LoginServiceAsync getInstance(String token){
			if (instance == null){
				instance = (LoginServiceAsync) GWT.create(LoginService.class);
			}

			ServiceDefTarget target = (ServiceDefTarget) instance;
			if (token != null && !token.isEmpty()) {
				RpcRequestBuilder reqBuilder = new RpcRequestBuilder() {
		            @Override
		            protected RequestBuilder doCreate(String serviceEntryPoint) {
		                RequestBuilder rb = super.doCreate(serviceEntryPoint);
		                rb.setHeader("CUSTOM_TOKEN", token);
		                return rb;
		            }
		        };
		        
				target.setRpcRequestBuilder(reqBuilder);
			}
			else {
				target.setRpcRequestBuilder(null);
			}
			
			return instance;
		}
	}

	public InfoConnection getConnectionProperties() throws ServiceException;

	public InfoConnection getConnectionInformations(boolean fromPortal) throws ServiceException;
	
	public InfoUser testConnectionCAS() throws ServiceException;
	
	public InfoUser testAutoLogin(String keyAutoLogin) throws ServiceException;
	
	public InfoUser login(String username, String password) throws ServiceException;
	
	public boolean forgotPassword(String webappUrl, String username, boolean sendMailAgain) throws ServiceException;
	
	public void changeUserPassword(String username, String password, String newPassword, boolean withoutConfirm) throws ServiceException;

//	public InfoUser connectUserWithBackup(String login, String backupCode) throws ServiceException;
	
	public List<Group> getAvailableGroups() throws ServiceException;
	
	public List<Repository> getAvailableRepositories() throws ServiceException;
	
	public InfoUser connect(InfoUser infoUser, Repository selectedRepository, Group selectedGroup) throws ServiceException;
	
	public InfoUser initFromPortal(String sessionId, int groupId, int repositoryId) throws ServiceException;

	public void disconnectUser() throws ServiceException;

	public String getUserImg(int userId) throws ServiceException;
	
	public String getGroupImg(int groupId) throws ServiceException;
	
	public List<bpm.document.management.core.model.User> getAklaboxUsers() throws ServiceException;
	
	public List<bpm.document.management.core.model.Group> getAklaboxGroups() throws ServiceException;

	public InfoUser connectToAklabox(InfoUser infoUser, String login, String password) throws ServiceException;
	
	public InfoUser disconnectFromAklabox(InfoUser infoUser) throws ServiceException;

	public String connectToAklabox(String aklaboxUrl, String paramLocal) throws ServiceException;

	public InfoUser checkCookieSession(String sessionId) throws ServiceException;
	
	public void initRepositoryConnection(Group selectedGroup, Repository selectedRepository) throws ServiceException;

	public List<Repository> getAvailableRepositories(String vanillaUrl, String login, String password) throws ServiceException;

	public List<Group> getAvailableGroups(String vanillaUrl, String login, String password) throws ServiceException;

	public HashMap<String, String> getVanillaContext() throws ServiceException;
	
	public void changePassword(String hash, String newPassword) throws ServiceException;

	public boolean checkHashCode(String hash) throws ServiceException;

	public Settings checkLoginWithHash(String login, String hash) throws ServiceException;

	public PasswordState checkPassword(String hash, String newPassword) throws ServiceException;

	public List<PasswordBackup> getPendingPasswordChangeDemands() throws ServiceException;

	public void acceptPasswordChangeDemand(String webappUrl, PasswordBackup passwordBackup, boolean acceptDemand) throws ServiceException;
	
	public boolean isUserChangePassword() throws ServiceException;
	
	public InfoUser getInfoUser() throws ServiceException;

	public InfoUser initFromKeycloak(String token) throws ServiceException;
}


