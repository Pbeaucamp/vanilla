package bpm.fwr.server;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import bpm.fwr.client.services.FwrServiceConnection;
import bpm.fwr.server.actions.FwrServerActions;
import bpm.fwr.server.security.FwrSession;
import bpm.fwr.shared.models.TreeParentDTO;
import bpm.fwr.shared.models.metadata.FwrMetadata;
import bpm.gwt.commons.client.services.exception.ServiceException;
import bpm.gwt.commons.server.security.CommonSessionHelper;
import bpm.gwt.commons.server.security.CommonSessionManager;
import bpm.gwt.commons.shared.fmdt.FmdtQueryDriller;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.repository.IRepository;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class FwrServiceConnectionImpl extends RemoteServiceServlet implements FwrServiceConnection {

	private static final long serialVersionUID = 532304739491498235L;

	@Override
	public void initSession() throws ServiceException {
		// We create an empty session
		try {
			String sessionId = CommonSessionManager.createSession(FwrSession.class);
			CommonSessionHelper.setCurrentSessionId(sessionId, getThreadLocalRequest());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to create a session.", e);
		}

		getSession().setLocation(getServletContext().getRealPath(File.separator));
	}

	private FwrSession getSession() throws ServiceException {
		return CommonSessionHelper.getCurrentSession(getThreadLocalRequest(), FwrSession.class);
	}

	@Override
	public TreeParentDTO browseRepositoryService() throws ServiceException {
		FwrSession session = getSession();

		TreeParentDTO datas = null;
		try {
			session.initRepository(IRepositoryApi.FWR_TYPE);
			datas = FwrServerActions.getContentsByType(session);
		} catch (Exception e) {
			throw new ServiceException("Unable to browse repository: " + e.getMessage());
		}

		return datas;
	}

	@Override
	public List<Group> getGroupsService() throws ServiceException {
		FwrSession session = getSession();

		try {
			return session.getVanillaApi().getVanillaSecurityManager().getGroups();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to get groups.", e);
		}
	}

	@Override
	public List<FwrMetadata> getMetadatas(String group) throws ServiceException {
		FwrSession session = getSession();

		List<FwrMetadata> metadatas = new ArrayList<FwrMetadata>();

		try {
			session.initRepository(IRepositoryApi.FMDT_TYPE);

			for (RepositoryDirectory dir : session.getRepository().getRootDirectories()) {
				getDirectoryContent(dir, metadatas, session.getRepository());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return metadatas;
	}

	public void getDirectoryContent(RepositoryDirectory directory, List<FwrMetadata> metadatas, IRepository rep) {

		List<RepositoryItem> items = null;
		try {

			items = rep.getItems(directory);

		} catch (Exception e) {
			e.printStackTrace();
		}

		for (RepositoryItem item : items) {
			FwrMetadata metadata = new FwrMetadata();
			metadata.setId(item.getId());
			metadata.setName(item.getItemName());
			metadatas.add(metadata);
		}

		List<RepositoryDirectory> dirs = null;
		try {
			dirs = rep.getChildDirectories(directory);

		} catch (Exception e) {
			e.printStackTrace();
		}

		for (RepositoryDirectory dir : dirs) {
			getDirectoryContent(dir, metadatas, rep);
		}
	}

	// @Override
	// public String test() {
	// IVanillaContext ctx = new BaseVanillaContext(securityServer, "system",
	// "system");
	// RemoteVanillaPlatform remote = new RemoteVanillaPlatform(ctx);
	// try {
	// remote.getVanillaRepositoryManager().getRepositories();
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	//
	// String session =
	// ((RemoteVanillaSystemManager)remote.getVanillaSystemManager()).getCurrentSessionId();
	//
	// StringBuilder url = new StringBuilder("?bpm.vanilla.sessionId=");
	// url.append(session);
	// url.append("&bpm.vanilla.groupId=");
	// url.append("1");
	// url.append("&bpm.vanilla.repositoryId=");
	// url.append("1");
	//
	// return url.toString();
	// }
}
