package bpm.metadata.web.server;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

import bpm.connection.manager.connection.ConnectionManager;
import bpm.connection.manager.connection.jdbc.VanillaJdbcConnection;
import bpm.connection.manager.connection.jdbc.VanillaPreparedStatement;
import bpm.gwt.commons.client.services.exception.ServiceException;
import bpm.gwt.commons.server.helper.MetadataHelper;
import bpm.gwt.commons.server.security.CommonSessionHelper;
import bpm.gwt.commons.server.security.CommonSessionManager;
import bpm.gwt.commons.shared.fmdt.metadata.Metadata;
import bpm.gwt.commons.shared.fmdt.metadata.MetadataData;
import bpm.gwt.commons.shared.fmdt.metadata.Row;
import bpm.metadata.MetaData;
import bpm.metadata.web.client.services.MetadataService;
import bpm.metadata.web.server.security.MetadataSession;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.data.DatabaseColumn;
import bpm.vanilla.platform.core.beans.data.DatabaseTable;
import bpm.vanilla.platform.core.beans.data.Datasource;
import bpm.vanilla.platform.core.beans.data.DatasourceJdbc;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class MetadataServiceImpl extends RemoteServiceServlet implements MetadataService {

	private static final long serialVersionUID = 1L;

	private MetadataSession getSession() throws ServiceException {
		return CommonSessionHelper.getCurrentSession(getThreadLocalRequest(), MetadataSession.class);
	}

	@Override
	public void initSession() throws ServiceException {
		// We create an empty session
		try {
			String sessionId = CommonSessionManager.createSession(MetadataSession.class);
			CommonSessionHelper.setCurrentSessionId(sessionId, getThreadLocalRequest());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to create a session.", e);
		}
	}

	@Override
	public Integer save(RepositoryDirectory target, Metadata metadata, boolean update) throws ServiceException {
		MetadataSession session = getSession();

		try {
			List<String> groupNames = getGroupNames(session);
			MetaData toSave = MetadataHelper.convertMetadata(metadata, groupNames);
			String modelXml = toSave.getXml(false);

			if (update) {
				RepositoryItem item = session.getRepositoryConnection().getRepositoryService().getDirectoryItem(metadata.getItemId());
				if (item == null) {
					throw new ServiceException("Unable to update item with id " + metadata.getItemId());
				}
				item.setItemName(metadata.getName());
				item.setComment(metadata.getDescription());
				session.getRepositoryConnection().getRepositoryService().updateModel(item, modelXml);
				
				return null;
			}
			else {
				RepositoryItem item = session.getRepositoryConnection().getRepositoryService().addDirectoryItemWithDisplay(IRepositoryApi.FMDT_TYPE, -1, target, metadata.getName(), "", "", "", modelXml, true);
				return item.getId();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to save the metadata : " + e.getMessage());
		}
	}

	private List<String> getGroupNames(MetadataSession session) {
		List<String> groupNames = new ArrayList<String>();
		try {
			List<Group> groups = session.getVanillaApi().getVanillaSecurityManager().getGroups();
			for (Group group : groups) {
				groupNames.add(group.getName());
			}
		} catch (Exception e) {
		}

		return groupNames;
	}
}
