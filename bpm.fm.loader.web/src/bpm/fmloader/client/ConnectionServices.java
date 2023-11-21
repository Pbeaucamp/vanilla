package bpm.fmloader.client;

import bpm.fmloader.client.exceptions.ServiceException;
import bpm.fmloader.client.infos.InfosUser;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ConnectionServices")
public interface ConnectionServices extends RemoteService {

	public InfosUser initFmSession(InfosUser infos) throws Exception;

	public void initSession() throws ServiceException;
}
