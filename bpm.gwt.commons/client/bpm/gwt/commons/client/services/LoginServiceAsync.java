package bpm.gwt.commons.client.services;

import java.util.HashMap;
import java.util.List;

import bpm.gwt.commons.shared.InfoConnection;
import bpm.gwt.commons.shared.InfoUser;
import bpm.gwt.commons.shared.utils.PasswordState;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.PasswordBackup;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.Settings;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface LoginServiceAsync {
	
	public void getConnectionProperties(AsyncCallback<InfoConnection> callback);

	public void getConnectionInformations(boolean fromPortal, AsyncCallback<InfoConnection> callback);
	
	public void testConnectionCAS(AsyncCallback<InfoUser> callback);
	
	public void testAutoLogin(String keyAutoLogin, AsyncCallback<InfoUser> callback);
	
	public void login(String username, String password, AsyncCallback<InfoUser> callback);
	
	public void forgotPassword(String webappUrl, String username, boolean sendMailAgain, AsyncCallback<Boolean> callback);
	
	public void changeUserPassword(String username, String password, String newPassword, boolean withoutConfirm, AsyncCallback<Void> callback);

//	public void connectUserWithBackup(String login, String backupCode, AsyncCallback<InfoUser> asyncCallback);
	
	public void getAvailableGroups(AsyncCallback<List<Group>> callback);
	
	public void getAvailableRepositories(AsyncCallback<List<Repository>> callback);
	
	public void connect(InfoUser infoUser, Repository selectedRepository, Group selectedGroup, AsyncCallback<InfoUser> callback);
	
	public void initFromPortal(String sessionId, int groupId, int repositoryId, AsyncCallback<InfoUser> callback);
	
	public void disconnectUser(AsyncCallback<Void> callback);

	public void getUserImg(int userId, AsyncCallback<String> callback);
	
	public void getGroupImg(int groupId, AsyncCallback<String> callback);
	
	public void getAklaboxUsers(AsyncCallback<List<bpm.document.management.core.model.User>> callback);
	
	public void getAklaboxGroups(AsyncCallback<List<bpm.document.management.core.model.Group>> callback);

	public void connectToAklabox(InfoUser infoUser, String login, String password, AsyncCallback<InfoUser> asyncCallback);
	
	public void disconnectFromAklabox(InfoUser infoUser, AsyncCallback<InfoUser> asyncCallback);

	public void connectToAklabox(String aklaboxUrl, String paramLocal, AsyncCallback<String> asyncCallback);

	public void checkCookieSession(String sessionId, AsyncCallback<InfoUser> asyncCallback);
	
	public void initRepositoryConnection(Group selectedGroup, Repository selectedRepository, AsyncCallback<Void> asyncCallback);

	public void getAvailableRepositories(String vanillaUrl, String login, String password, AsyncCallback<List<Repository>> callback);

	public void getAvailableGroups(String vanillaUrl, String login, String password, AsyncCallback<List<Group>> callback);

	public void getVanillaContext(AsyncCallback<HashMap<String, String>> callback);
	
	public void changePassword(String hash, String newPassword, AsyncCallback<Void> callback);

	public void checkHashCode(String hash, AsyncCallback<Boolean> callback);

	public void checkLoginWithHash(String login, String hash, AsyncCallback<Settings> callback);

	public void checkPassword(String hash, String newPassword, AsyncCallback<PasswordState> callback);
	
	public void getPendingPasswordChangeDemands(AsyncCallback<List<PasswordBackup>> callback);

	public void acceptPasswordChangeDemand(String webappUrl, PasswordBackup passwordBackup, boolean acceptDemand, AsyncCallback<Void> callback);
	
	public void isUserChangePassword(AsyncCallback<Boolean> callback);
	
	public void getInfoUser(AsyncCallback<InfoUser> callback);

	public void initFromKeycloak(String token, AsyncCallback<InfoUser> callback);
}
