package bpm.office.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.mdm.model.api.IMdmProvider;
import bpm.mdm.remote.MdmRemote;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.ged.ComProperties;
import bpm.vanilla.platform.core.beans.ged.DocumentVersion;
import bpm.vanilla.platform.core.beans.ged.GedDocument;
import bpm.vanilla.platform.core.beans.ged.constant.RuntimeFields;
import bpm.vanilla.platform.core.components.IGedComponent;
import bpm.vanilla.platform.core.components.ged.GedIndexRuntimeConfig;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.remote.impl.components.RemoteGedComponent;
import bpm.vanilla.platform.core.repository.IRepositoryObject;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.platform.core.repository.services.IDocumentationService;
import bpm.vanilla.platform.core.repository.services.IRepositoryService;

public class ExcelFunctionsUtils {

	private static String[] excelTypes = new String[] { "xls", "xlsx", "ods", "ods" };

	public static String UpdateDocument(String directoryId, String fileName, InputStream stream, IRepositoryApi repositoryApi, IRepositoryContext repoCtx) {
		try {
			int dirId = Integer.parseInt(directoryId);

			IRepositoryService repoService = repositoryApi.getRepositoryService();
			// RepositoryDirectory repoDir= repoService.getDirectory(dirId);

			RepositoryItem item = repoService.getDirectoryItem(dirId);

			ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
			IOUtils.copy(stream, byteOut);
			ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());

			if (item != null) {
				IDocumentationService docService = repositoryApi.getDocumentationService();
				if (docService.updateExternalDocument(item, byteIn) != null) {

					byteIn.reset();

					updateGedDocument(dirId, item.getName(), byteIn, repositoryApi, repoCtx);
					return "success";
				}
			}
			if (item != null) {
				byteIn.reset();

				updateGedDocument(dirId, item.getName(), byteIn, repositoryApi, repoCtx);
				return "success";
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return e.getMessage();
		}
		return "failed";
	}

	private static void updateGedDocument(int dirId, String fileName, InputStream in, IRepositoryApi repositoryApi, IRepositoryContext repoCtx) throws Exception {

		IVanillaContext ctx = repoCtx.getVanillaContext();
		IGedComponent gedcomponent = getGedComponent(ctx);

		int repoId = repoCtx.getRepository().getId();
		int groupId = repoCtx.getGroup().getId();

		GedDocument gedDocument = searchDocument(gedcomponent, fileName, groupId, repoId);

		try {
			DocumentVersion vers = gedcomponent.addVersionToDocument(gedDocument, fileName.substring(fileName.lastIndexOf("."), fileName.length()), in);

		} catch (Exception e) {
			DocumentVersion vers = gedcomponent.addVersionToDocument(gedDocument, "", in);
			// contract.getFileVersions().addDocumentVersion(vers);
			e.printStackTrace();
		}

	}

	public static GedDocument searchDocument(IGedComponent gedcomponent, String fileName, int groupId, int repoId) throws Exception {
		List<Integer> groupIds = new ArrayList<Integer>();
		groupIds.add(groupId);

		List<GedDocument> gedDocuments = gedcomponent.getDocuments(groupIds, repoId);
		GedDocument doc = null;
		for (GedDocument gedDocument : gedDocuments) {
			if (gedDocument.getName().equals(fileName)) {
				doc = gedDocument;
				break;
			}
		}
		return doc;
	}

	// Import Export Excel
	public static String addDocument(String directoryId, String fileName, InputStream stream, IRepositoryApi repositoryApi, IRepositoryContext ctx) {
		try {
			// IRepositoryApi repositoryApi =getRepositoryApi(session);
			int dirId = Integer.parseInt(directoryId);
			ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
			IOUtils.copy(stream, byteOut);
			ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());

			// InputStream copyStream;
			IRepositoryService repoService = repositoryApi.getRepositoryService();
			RepositoryDirectory repoDir = repoService.getDirectory(dirId);

			String nameDoc = searchName(repoService, fileName);

			RepositoryItem item = repoService.addExternalDocumentWithDisplay(repoDir, nameDoc, null, nameDoc, nameDoc, byteIn, true, fileName.substring(fileName.lastIndexOf(".") + 1));
			byteIn.reset();

			List<Group> groups = repositoryApi.getAdminService().getGroupsForDirectory(repoDir);
			List<Integer> listIds = new ArrayList<Integer>();
			for (Group group : groups) {
				listIds.add(group.getId());
			}
			// if()

			addGedDocument(dirId, nameDoc, listIds, byteIn, repositoryApi, ctx);

			if (item != null)
				return "success";

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return e.getMessage();
		}
		return "failed";
	}

	private static String searchName(IRepositoryService repoService, String fileName) throws Exception {

		String name = fileName;
		Boolean exist = true;
		int i = 1;
		while (exist) {
			if (repoService.getItems(name).size() > 0) {
				name = fileName.substring(0, fileName.lastIndexOf(".")) + "_" + i + fileName.substring(fileName.lastIndexOf("."));
				i++;
			}
			else
				exist = false;
		}
		return name;
	}

	private static void addGedDocument(int dirId, String fileName, List<Integer> listIds, InputStream in, IRepositoryApi repositoryApi, IRepositoryContext repoCtx) throws Exception {

		IVanillaContext ctx = repoCtx.getVanillaContext();
		IGedComponent gedcomponent = getGedComponent(ctx);

		GedDocument doc = new GedDocument();

		doc.setDirectoryId(0);
		doc.setName(fileName);
		doc.setCreationDate(new Date());
		int userId = getVanillaApi().getVanillaSecurityManager().getUserByLogin(ctx.getLogin()).getId();
		doc.setCreatedBy(userId);
		doc.setMdmAttached(true);

		String format = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());

		// List<Integer> groupIds = new ArrayList<Integer>();
		// groupIds.add(repositoryApi.getContext().getGroup().getId());

		ComProperties com = new ComProperties();

		com.setSimpleProperty(RuntimeFields.TITLE.getName(), fileName);

		GedIndexRuntimeConfig config = new GedIndexRuntimeConfig(com, userId, -1, listIds, 1, format, null, -1);
		config.setMdmAttached(true);

		int id = gedcomponent.index(config, in);
		IRepositoryApi sock = new RemoteRepositoryApi(repoCtx);

		for (Integer groupId : listIds) {

			gedcomponent.addAccess(id, groupId, sock.getContext().getRepository().getId());
		}
		doc = gedcomponent.getDocumentDefinitionById(id);

	}

	public static String createDir(String directoryId, String name, IRepositoryApi repositoryApi) {
		try {
			// IRepositoryApi repositoryApi =getRepositoryApi(session);

			IRepositoryService repoService = repositoryApi.getRepositoryService();
			RepositoryDirectory parent = null;

			if (directoryId != null && directoryId.length() > 0) {
				int dirId = Integer.parseInt(directoryId);
				parent = repoService.getDirectory(dirId);
			}

			RepositoryDirectory dir = repoService.addDirectory(name, name, parent);
			if (dir != null)
				return "success";

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return e.getMessage();
		}
		return "failed";
	}

	public static String deleteDir(String directoryId, IRepositoryApi repositoryApi) {
		try {
			// IRepositoryApi repositoryApi =getRepositoryApi(session);

			IRepositoryService repoService = repositoryApi.getRepositoryService();

			int dirId = Integer.parseInt(directoryId);

			RepositoryDirectory repoDir = repoService.getDirectory(dirId);

			repoService.delete(repoDir);

			return "success";

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return e.getMessage();
		}
	}

	public static String deleteItem(String directoryId, IRepositoryApi repositoryApi, IRepositoryContext repoCtx) {
		try {
			// IRepositoryApi repositoryApi =getRepositoryApi(session);

			IRepositoryService repoService = repositoryApi.getRepositoryService();

			int dirId = Integer.parseInt(directoryId);

			RepositoryItem repoDir = repoService.getDirectoryItem(dirId);

			IVanillaContext ctx = repoCtx.getVanillaContext();
			IGedComponent gedcomponent = getGedComponent(ctx);

			int repoId = repoCtx.getRepository().getId();
			int groupId = repoCtx.getGroup().getId();

			GedDocument gedDocument = searchDocument(gedcomponent, repoDir.getName(), groupId, repoId);
			gedcomponent.deleteGedDocument(gedDocument.getId());

			repoService.delete(repoDir);

			return "success";

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return e.getMessage();
		}
	}

	public static InputStream downloadFile(String directoryId, IRepositoryApi repositoryApi) {
		try {

			IRepositoryService repoService = repositoryApi.getRepositoryService();
			IDocumentationService docService = repositoryApi.getDocumentationService();

			int dirId = Integer.parseInt(directoryId);

			RepositoryItem item = repoService.getDirectoryItem(dirId);

			InputStream stream = docService.importExternalDocument(item);
			return stream;

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * generateXML
	 * 
	 * @param List
	 *            <IObject> listDocument
	 * @param User
	 *            user
	 * @param String
	 *            type
	 * @return Document
	 * 
	 *         Function used to generate XML from list of Tree. Use for all
	 *         views except group view. Function use AddElement() to look
	 *         through the tree.
	 * 
	 */
	public static String generateXML(String type, IRepositoryApi repositoryApi) {
		try {
			Document xmlDocument = DocumentHelper.createDocument();
			Element root = xmlDocument.addElement("root");
			// IRepositoryApi repositoryApi;
			// repositoryApi = getRepositoryApi(session);
			IRepositoryService repoService = repositoryApi.getRepositoryService();
			List<IRepositoryObject> listDir = repoService.getDirectoryContent(null, IRepositoryService.BOTH);

			for (IRepositoryObject obj : listDir) {
				addElement(obj, root, repoService, type);
			}
			return xmlDocument.asXML();

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * treeToXML
	 * 
	 * @param Tree
	 *            treeEntry
	 * @param org
	 *            .dom4j.Element currentElement
	 * @param User
	 *            user
	 * @param String
	 *            type
	 * 
	 *            Recursive function used for look into the tree. Use
	 *            AddElement() to make the difference between tree type and
	 *            added node.
	 * 
	 */
	private static void treeToXML(RepositoryDirectory treeEntry, Element currentElement, IRepositoryService repoService, String type) {
		try {
			List<IRepositoryObject> listDir = repoService.getDirectoryContent(treeEntry, IRepositoryService.BOTH);
			for (IRepositoryObject obj : listDir) {
				addElement(obj, currentElement, repoService, type);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * AddElement
	 * 
	 * @param IObject
	 *            currentObject
	 * @param org
	 *            .dom4j.Element currentElement
	 * @param User
	 *            user
	 * @param String
	 *            type Recursive function Used to add node on the XML document
	 *            according to the tree type. Use treeToXML to look into the
	 *            tree.
	 * 
	 */
	private static void addElement(IRepositoryObject currentObject, Element currentElement, IRepositoryService repoService, String type) {
		try {

			org.dom4j.Element childElement = null;

			if (currentObject instanceof RepositoryDirectory) {
				childElement = currentElement.addElement("folder");
				RepositoryDirectory currentDirectory = (RepositoryDirectory) currentObject;
				treeToXML(currentDirectory, childElement, repoService, type);
				childElement.setText(currentDirectory.getName());
				childElement.addAttribute("name", currentDirectory.getName());
				childElement.addAttribute("id", String.valueOf(currentDirectory.getId()));

			}
			else {
				if (type.equals("excel")) {
					if (isExcelDocument(currentObject.getName())) {
						childElement = currentElement.addElement("document");
						RepositoryItem currentItem = (RepositoryItem) currentObject;
						childElement.setText(currentItem.getItemName());
						childElement.addAttribute("name", currentItem.getName());
						childElement.addAttribute("id", String.valueOf(currentItem.getId()));
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static boolean isExcelDocument(String fileName) {
		String extension = fileName.replace(fileName.substring(0, fileName.lastIndexOf(".") + 1), "").toLowerCase();
		for (String e : excelTypes) {
			if (e.equals(extension)) {
				return true;
			}
		}
		return false;
	}

	public static HashMap<String, String> objectToString(HashMap<String, Object> map) {
		HashMap<String, String> documentParameters = new HashMap<String, String>();

		for (String key : map.keySet()) {
			if (map.get(key) instanceof String)
				if (map.get(key) != null)
					documentParameters.put(key, map.get(key).toString());
				else
					documentParameters.put(key, null);
		}
		return documentParameters;
	}

	public static HashMap<String, String> mapToHashMap(Map<String, String[]> map) {
		HashMap<String, String> documentParameters = new HashMap<String, String>();

		for (String key : map.keySet()) {
			if (map.get(key) != null)
				documentParameters.put(key, map.get(key)[0]);
			else
				documentParameters.put(key, null);
		}
		return documentParameters;
	}

	private static IGedComponent getGedComponent(IVanillaContext ctx) {

		IGedComponent gedComponent = new RemoteGedComponent(ctx);

		return gedComponent;
	}

	private static IVanillaAPI getVanillaApi() {

		IVanillaAPI vanillaApi = new RemoteVanillaPlatform(ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_URL), ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN), ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD));

		return vanillaApi;
	}

	private static IMdmProvider resetMdmProvider(IVanillaContext ctx) {
		try {
			IMdmProvider provider = new MdmRemote(ctx.getLogin(), ctx.getPassword(), ctx.getVanillaUrl(), null, null);
			return provider;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
			// MessageDialog.openError(getWorkbench().getActiveWorkbenchWindow().getShell(),
			// "Loading Mdm Model", "Unable to load model : " + e.getMessage());
		}
	}

}
