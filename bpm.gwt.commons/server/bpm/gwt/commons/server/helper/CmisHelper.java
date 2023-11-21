package bpm.gwt.commons.server.helper;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.Repository;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ContentStreamImpl;

import bpm.gwt.commons.client.services.exception.ServiceException;
import bpm.gwt.commons.shared.InfoShareCmis;
import bpm.gwt.commons.shared.cmis.CmisDocument;
import bpm.gwt.commons.shared.cmis.CmisFolder;
import bpm.gwt.commons.shared.cmis.CmisInformations;
import bpm.gwt.commons.shared.cmis.CmisItem;
import bpm.gwt.commons.shared.utils.ExportResult;

public class CmisHelper {

	public static CmisInformations getRepositories(CmisInformations cmisInfos) {
		Map<String, String> parameters = getParameters(cmisInfos);

		SessionFactory sessionFactory = SessionFactoryImpl.newInstance();
		List<Repository> repositories = sessionFactory.getRepositories(parameters);
		List<String> repositoryIds = new ArrayList<>();
		if (repositories != null) {
			for (Repository repository : repositories) {
				repositoryIds.add(repository.getId());
			}
		}

		cmisInfos.setAvailableRepositories(repositoryIds);
		return cmisInfos;
	}

	private static final List<CmisItem> getFolders(CmisInformations cmisInfos) {
		Map<String, String> parameters = getParameters(cmisInfos);

		SessionFactory sessionFactory = SessionFactoryImpl.newInstance();
		Session session = sessionFactory.createSession(parameters);

		Folder root = session.getRootFolder();
		ItemIterable<CmisObject> childrens = root.getChildren();

		return convertItems(childrens);
	}

	public static final List<CmisItem> getFolders(CmisInformations cmisInfos, String folderId) {
		if (folderId == null) {
			return getFolders(cmisInfos);
		}

		Map<String, String> parameters = getParameters(cmisInfos);

		SessionFactory sessionFactory = SessionFactoryImpl.newInstance();
		Session session = sessionFactory.createSession(parameters);

		CmisObject item = session.getObject(folderId);
		if (item instanceof Folder) {
			Folder folder = (Folder) item;
			ItemIterable<CmisObject> childrens = folder.getChildren();
			return convertItems(childrens);
		}
		return new ArrayList<>();
	}

	private static List<CmisItem> convertItems(ItemIterable<CmisObject> childrens) {
		List<CmisItem> items = new ArrayList<>();
		if (childrens != null) {
			for (CmisObject item : childrens) {
				if (item instanceof Folder) {
					String itemId = item.getId();
					String name = item.getName();

					items.add(new CmisFolder(itemId, name));
				}
				else if (item instanceof Document) {
					String itemId = item.getId();
					String name = item.getName();

					items.add(new CmisDocument(itemId, name));
				}
			}
		}
		return items;
	}

	private static final Map<String, String> getParameters(CmisInformations cmisInfos) {
		Map<String, String> parameters = new HashMap<String, String>();
		if (cmisInfos.isCustomProperties()) {
			parameters = cmisInfos.getProperties();
		}
		else {
			parameters.put(SessionParameter.USER, cmisInfos.getLogin());
			parameters.put(SessionParameter.PASSWORD, cmisInfos.getPassword());
			parameters.put(SessionParameter.BROWSER_URL, cmisInfos.getUrl());
			parameters.put(SessionParameter.BINDING_TYPE, BindingType.BROWSER.value());
			parameters.put(SessionParameter.COMPRESSION, "true");
			parameters.put(SessionParameter.COOKIES, "true");
		}

		if (cmisInfos.getSelectedRepositoryId() != null && !cmisInfos.getSelectedRepositoryId().isEmpty()) {
			parameters.put(SessionParameter.REPOSITORY_ID, cmisInfos.getSelectedRepositoryId());
		}

		return parameters;
	}

	public static ExportResult createDocument(InfoShareCmis infoShare, ByteArrayInputStream stream) throws ServiceException {
		Map<String, String> parameters = getParameters(infoShare.getCmisInfos());

		SessionFactory sessionFactory = SessionFactoryImpl.newInstance();
		Session session = sessionFactory.createSession(parameters);

		CmisObject item = session.getObject(infoShare.getSelectedFolder().getItemId());
		if (item instanceof Folder) {
			Folder parent = (Folder) item;

			String name = infoShare.getItemName();
			if (!name.contains(".")) {
				name = name + "." + infoShare.getFormat();
			}
			
			// properties
			// (minimal set: name and object type id)
			Map<String, Object> properties = new HashMap<String, Object>();
			properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
			properties.put(PropertyIds.NAME, name);

			// content
			ContentStream contentStream = new ContentStreamImpl(name, null, "text/plain", stream);

			// create a major version
			Document newDoc = parent.createDocument(properties, contentStream, VersioningState.NONE);
			return new ExportResult(newDoc.getName());
		}
		else {
			throw new ServiceException("'" + infoShare.getSelectedFolder().getItemId() + "' is not a folder. Unable to push the document");
		}
	}
}
