package bpm.es.pack.manager.imp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.es.pack.manager.I18N.Messages;
import bpm.es.pack.manager.digester.ExportDescriptorDigester;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Role;
import bpm.vanilla.platform.core.beans.RoleGroup;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.repository.IRepository;
import bpm.vanilla.platform.core.repository.Repository;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.platform.core.repository.services.IRepositoryService;
import bpm.vanilla.workplace.core.model.ExportDetails;
import bpm.vanilla.workplace.core.model.ImportItem;
import bpm.vanilla.workplace.core.model.ReportBean;

public class Importer {
	private static HashMap<Integer, Integer> groupsMap = new HashMap<Integer, Integer>();
	private static HashMap<Integer, Integer> rolesMap = new HashMap<Integer, Integer>();

	private static HashMap<ImportItem, List<Integer>> newlyCreatedDirsId = new HashMap<ImportItem, List<Integer>>();
	private static StringBuffer logs = new StringBuffer();

	public static synchronized String importPackage(IVanillaAPI vanillaApi, IRepositoryApi repositoryApi, HashMap<String, File> map, File pack) throws Exception {
		logs = new StringBuffer();
		groupsMap.clear();
		newlyCreatedDirsId.clear();

		// backup the user
		IRepositoryContext backupContext = repositoryApi.getContext();

		// parse descriptor
		File descriptor = null;
		for (String s : map.keySet()) {
			if (s.equals("descriptor")) { //$NON-NLS-1$
				descriptor = map.get(s);
				break;
			}
		}

		if (descriptor == null) {
			throw new Exception(Messages.Importer_1);
		}

		FileInputStream fis = new FileInputStream(descriptor);
		ExportDetails infos = new ExportDescriptorDigester(fis).getModel();

		// for the the owner option

		if (infos.isOwnerExporter()) {
			String exporterId = infos.getExporterId();
			User u = vanillaApi.getVanillaSecurityManager().getUserById(Integer.parseInt(exporterId));
			repositoryApi = new RemoteRepositoryApi(new BaseRepositoryContext(new BaseVanillaContext(backupContext.getVanillaContext().getVanillaUrl(), u.getLogin(), u.getPassword()), backupContext.getGroup(), backupContext.getRepository()));
		}
		// push them with apply options
		HashMap<ImportItem, Integer> newItems = createItems(repositoryApi, infos.isReplaceOld(), repositoryApi, infos.getImportItems(), map, new HashMap<ImportItem, Integer>(), new HashMap<Integer, Integer>());

		// groups
		if (infos.isIncludeGroups()) {
			for (Group g : infos.getGroups()) {
				int oldId = g.getId();
				g.setId(0);

				try {
					int id = vanillaApi.getVanillaSecurityManager().addGroup(g);
					g.setId(id);
					groupsMap.put(oldId, g.getId());
					if (oldId != g.getId())
						logs.append(Messages.Importer_2 + g.getName() + "\r\n");//$NON-NLS-1$
				} catch (Exception e) {
					e.printStackTrace();
					logs.append(Messages.Importer_4 + g.getName() + "\r\n");//$NON-NLS-1$
				}

			}

			// group-items
			if (infos.isIncludeGrants() && !infos.getGroupsItem().isEmpty()) {
				for (Integer oldId : groupsMap.keySet()) {
					for (Integer old_gId : infos.getGroupsItem().get(oldId)) {
						for (ImportItem it : newItems.keySet()) {
							if (it.getId() == old_gId) {
								repositoryApi.getAdminService().addGroupForItem(groupsMap.get(oldId), newItems.get(it));
								break;
							}
						}
					}

					for (ImportItem it : newlyCreatedDirsId.keySet()) {
						for (Integer dirId : newlyCreatedDirsId.get(it)) {
							repositoryApi.getAdminService().addGroupForDirectory(groupsMap.get(oldId), dirId);
						}
					}
				}
			}
		}

		// roles
		if (infos.isIncludeRoles()) {
			for (Role r : infos.getRoles()) {
				int oldId = r.getId();
				r.setId(0);
				int rId = vanillaApi.getVanillaSecurityManager().addRole(r);
				r.setId(rId);
				rolesMap.put(oldId, r.getId());
			}
		}

		// group-roles
		for (Integer old_gId : infos.getGroupsRoles().keySet()) {
			for (Integer old_itId : infos.getGroupsRoles().get(old_gId)) {
				RoleGroup rg = new RoleGroup();
				rg.setGroupId(groupsMap.get(old_gId));
				rg.setRoleId(rolesMap.get(old_itId));
				vanillaApi.getVanillaSecurityManager().addRoleGroup(rg);
			}
		}

		// reports
		for (ReportBean r : infos.getReportsBeans()) {
			for (ImportItem ii : newItems.keySet()) {
				if (r.getDirectoryItemId() == ii.getId()) {
					int newItId = newItems.get(ii);
					RepositoryItem it = new RepositoryItem();

					it.setType(IRepositoryApi.FWR_TYPE);
					it.setItemName(r.getItemName());
					it.setId(newItId);
					for (String s : r.getFiles()) {
						try {
							// sock.sendReportFileFor(r.getItemName(), it, "",
							// map.get(s), "Default");
							// TODO
							logs.append(Messages.Importer_6 + map.get(s) + "\r\n");//$NON-NLS-1$
						} catch (Exception e) {
							e.printStackTrace();
							logs.append(Messages.Importer_8 + map.get(s) + "\r\n");//$NON-NLS-1$
						}

					}
				}
			}

		}

		// delete files
		for (String s : map.keySet()) {
			if (map.get(s) != null && map.get(s).exists()) {
				map.get(s).delete();
			}
		}

		repositoryApi = new RemoteRepositoryApi(backupContext);

		return logs.toString();
	}

	/*
	 * return the newId for each items
	 */
	private static HashMap<ImportItem, Integer> createItems(IRepositoryApi repositoryApi, boolean replaceOld, IRepositoryApi sock, List<ImportItem> importItems, HashMap<String, File> map, HashMap<ImportItem, Integer> itemsId, HashMap<Integer, Integer> matching) throws Exception {

		for (ImportItem i : importItems) {
			// create the directory if it doesnt exists
			if (itemsId.get(i) != null) {
				continue;
			}

			// we first create the needed object
			for (Integer depId : i.getNeeded()) {
				for (ImportItem ii : importItems) {
					if (ii.getId() == depId) {
						if (!ii.getNeeded().isEmpty()) {
							List<ImportItem> dep = new ArrayList<ImportItem>();
							for (Integer inte : ii.getNeeded()) {
								for (ImportItem _ii : importItems) {
									if (_ii.getId() == inte) {
										dep.add(_ii);
									}
								}
							}
							createItems(repositoryApi, replaceOld, sock, dep, map, itemsId, matching);
						}
						createItem(repositoryApi, replaceOld, ii, sock, map, itemsId, matching);

					}
				}
			}

			// here we had the importItem
			createItem(repositoryApi, replaceOld, i, sock, map, itemsId, matching);

		}

		return itemsId;
	}

	public static RepositoryItem isDirectoryContains(IRepositoryApi repositoryApi, ImportItem i, RepositoryDirectory dir) throws Exception {

		// for (Object o :
		// repositoryApi.getRepositoryService().getDirectoryContent(dir)) {
		for (Object o : repositoryApi.getRepositoryService().getDirectoryContent(dir, IRepositoryService.ONLY_ITEM)) {
			if (o instanceof RepositoryItem) {
				RepositoryItem it = (RepositoryItem) o;
				if (it.getType() == i.getType() && it.getItemName().equals(i.getName())) {

					return it;
				}
			}
		}

		return null;
	}

	private static void createItem(IRepositoryApi repositoryApi, boolean replaceOld, ImportItem i, IRepositoryApi sock, HashMap<String, File> map, HashMap<ImportItem, Integer> itemsId, HashMap<Integer, Integer> matching) {

		try {
			RepositoryDirectory dir = getDirectory(i, sock);
			FileInputStream fis = new FileInputStream(map.get(i.getId() + "")); //$NON-NLS-1$
			String xml = IOUtils.toString(fis, "UTF-8"); //$NON-NLS-1$

			// we check if the directoryItem dont exists yet
			RepositoryItem oldModelId = isDirectoryContains(repositoryApi, i, dir);
			if (oldModelId != null) {
				itemsId.put(i, oldModelId.getId());
			}

			// we modify the xml for the dependancies
			xml = modifyXml(i, itemsId, xml, matching);

			if (replaceOld && oldModelId != null) {
				RepositoryItem fake = new RepositoryItem();
				fake.setId(oldModelId.getId());

				sock.getRepositoryService().updateModel(fake, xml);
				logs.append(Messages.Importer_15 + IRepositoryApi.TYPES_NAMES[i.getType()] + " " + i.getName() + Messages.Importer_17);//$NON-NLS-1$
				return;
			}
			else if (!replaceOld && oldModelId != null) {
				logs.append(Messages.Importer_18 + IRepositoryApi.TYPES_NAMES[i.getType()] + " " + i.getName() + Messages.Importer_20);//$NON-NLS-1$
				return;
			}
			int id = -1;
			if (i.getType() == IRepositoryApi.CUST_TYPE) {
				Integer subtype = null;
				Document d = DocumentHelper.parseText(xml);
				if (d.getRootElement().getName().equals("report")) { //$NON-NLS-1$
					subtype = IRepositoryApi.BIRT_REPORT_SUBTYPE;
				}
				else if (d.getRootElement().getName().equals("action-sequence")) { //$NON-NLS-1$
					subtype = IRepositoryApi.XACTION_SUBTYPE;
				}
				else if (d.getRootElement().getName().equals("jasperReport")) { //$NON-NLS-1$
					subtype = IRepositoryApi.JASPER_REPORT_SUBTYPE;
				}

				id = sock.getRepositoryService().addDirectoryItemWithDisplay(IRepositoryApi.CUST_TYPE, subtype, dir, i.getName(), "", "", "", xml, true).getId(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			}
			else if (i.getType() == IRepositoryApi.EXTERNAL_DOCUMENT) {
				id = sock.getRepositoryService().addExternalDocumentWithDisplay(dir, i.getName(), "", "", "", new FileInputStream(map.get(i.getId() + "")), true, map.get(i.getId() + "").getName().substring(map.get(i.getId() + "").getName().lastIndexOf(".") + 1)).getId(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
			}
			else {
				id = sock.getRepositoryService().addDirectoryItemWithDisplay(i.getType(), i.getSubtype(), dir, i.getName(), "", "", "", xml, true).getId(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			}

			logs.append(Messages.Importer_40 + IRepositoryApi.TYPES_NAMES[i.getType()] + " " + i.getName() + Messages.Importer_42);//$NON-NLS-1$
			i.setDirectoryId(dir.getId());
			itemsId.put(i, id);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			logs.append(Messages.Importer_43 + map.get(i.getId() + "") + "\r\n"); //$NON-NLS-1$//$NON-NLS-2$
		} catch (IOException e) {
			e.printStackTrace();
			logs.append(Messages.Importer_46 + map.get(i.getId() + "") + "\r\n"); //$NON-NLS-1$//$NON-NLS-2$
		} catch (Exception e) {
			logs.append(Messages.Importer_49 + map.get(i.getId() + "") + Messages.Importer_51);//$NON-NLS-1$
			e.printStackTrace();
		}
	}

	private static void modifyXmlId(int fdModelId, Element element) {
		element.setText(fdModelId + ""); //$NON-NLS-1$
	}

	private static String modifyXml(ImportItem item, HashMap<ImportItem, Integer> itemsId, String xml, HashMap<Integer, Integer> matching) throws DocumentException {
		for (Integer id : item.getNeeded()) {

			for (ImportItem ii : itemsId.keySet()) {
				if (ii.getId() == id) {
					matching.put(ii.getId(), itemsId.get(ii));
					break;
				}
			}
		}

		switch (item.getType()) {
		case IRepositoryApi.FD_DICO_TYPE:
			return modifyFdDico(matching, xml);
		case IRepositoryApi.FD_TYPE:
			return modifyFdXml(matching, xml);

		case IRepositoryApi.BIW_TYPE:
			return modifyBiw(matching, xml);

		case IRepositoryApi.FASD_TYPE:
			break;

		case IRepositoryApi.FWR_TYPE:
			return modifyFwrXml(matching, xml);

		case IRepositoryApi.CUST_TYPE:
			return modifyBirtXml(matching, xml);

		}

		return xml;
	}

	private static String modifyBiw(HashMap<Integer, Integer> matching, String xml) throws DocumentException {
		Document document = DocumentHelper.parseText(xml);
		Element root = document.getRootElement();

		for (Object o : root.elements("BIWActivity")) { //$NON-NLS-1$
			Element id = ((Element) o).element("BIWObject").element("id"); //$NON-NLS-1$ //$NON-NLS-2$
			for (Integer k : matching.keySet()) {
				if (k.equals(Integer.parseInt(id.getText()))) {
					id.setText("" + matching.get(Integer.parseInt(id.getStringValue()))); //$NON-NLS-1$
					break;
				}
			}
		}

		for (Object o : root.elements("interfaceActivity")) { //$NON-NLS-1$
			Element id = ((Element) o).element("interfaceObject").element("id"); //$NON-NLS-1$ //$NON-NLS-2$
			for (Integer k : matching.keySet()) {
				if (k.equals(Integer.parseInt(id.getText()))) {
					id.setText("" + matching.get(Integer.parseInt(id.getStringValue()))); //$NON-NLS-1$
					break;
				}
			}
		}

		for (Object o : root.elements("reportActivity")) { //$NON-NLS-1$
			Element e = (Element) o;
			for (Object all : e.elements()) {
				if (((Element) all).element("id") != null) { //$NON-NLS-1$
					Element id = ((Element) all).element("id"); //$NON-NLS-1$
					for (Integer k : matching.keySet()) {
						if (k.equals(Integer.parseInt(id.getText()))) {
							id.setText("" + matching.get(Integer.parseInt(id.getStringValue()))); //$NON-NLS-1$
							break;
						}
					}
				}
			}
		}

		for (Object o : root.elements("burstActivity")) { //$NON-NLS-1$
			Element e = (Element) o;
			for (Object all : e.elements()) {
				if (((Element) all).element("id") != null) { //$NON-NLS-1$
					Element id = ((Element) all).element("id"); //$NON-NLS-1$
					for (Integer k : matching.keySet()) {
						if (k.equals(Integer.parseInt(id.getText()))) {
							id.setText("" + matching.get(Integer.parseInt(id.getStringValue()))); //$NON-NLS-1$
							break;
						}
					}
				}
			}

		}

		return root.asXML();
	}

	private static String modifyFdDico(HashMap<Integer, Integer> matching, String xml) throws DocumentException {
		Document document = DocumentHelper.parseText(xml);
		Element root = document.getRootElement();

		for (Element e : (List<Element>) root.elements("dataSource")) { //$NON-NLS-1$
			if (e.element("odaExtensionDataSourceId").getStringValue().equals("bpm.metadata.birt.oda.runtime")) { //$NON-NLS-1$ //$NON-NLS-2$

				for (Object _o : e.elements("publicProperty")) { //$NON-NLS-1$
					Element _p = (Element) _o;

					if ("DIRECTORY_ITEM_ID".equals(_p.attribute("name").getText())) { //$NON-NLS-1$ //$NON-NLS-2$
						for (Integer k : matching.keySet()) {
							if (k.equals(Integer.parseInt(((Element) _p).getText()))) {
								((Element) _p).setText("" + matching.get(Integer.parseInt(((Element) _p).getStringValue()))); //$NON-NLS-1$
								break;
							}
						}

					}
				}
			}
		}

		for (Element e : (List<Element>) root.elements("report")) { //$NON-NLS-1$
			Element _e = e.element("directoryItemId"); //$NON-NLS-1$
			for (Integer k : matching.keySet()) {
				if (k.equals(Integer.parseInt(((Element) _e).getText()))) {
					((Element) _e).setText("" + matching.get(Integer.parseInt(((Element) _e).getStringValue()))); //$NON-NLS-1$
					break;
				}
			}
		}
		return root.asXML();

	}

	private static String modifyFdXml(HashMap<Integer, Integer> matching, String xml) throws DocumentException {
		Document document = DocumentHelper.parseText(xml);
		Element root = document.getRootElement();

		// look for the dependancies information
		Element dependanciesNode = null;

		// FD V1
		if (root.element("dictionary") != null) { //$NON-NLS-1$
			dependanciesNode = root.element("dictionary").element("dependancies"); //$NON-NLS-1$ //$NON-NLS-2$
			for (Object o : dependanciesNode.elements("dependantDirectoryItemId")) { //$NON-NLS-1$
				Element e = (Element) o;

				for (Integer k : matching.keySet()) {
					if (k.equals(Integer.parseInt(e.getStringValue()))) {
						e.setText("" + matching.get(Integer.parseInt(e.getStringValue()))); //$NON-NLS-1$
						break;
					}
				}

			}

			String tmp = document.asXML();
			int pos = tmp.indexOf("<directoryItemId>"); //$NON-NLS-1$

			while (pos != -1) {

				String id = tmp.substring(pos + 17, tmp.indexOf("</directoryItemId>", pos)); //$NON-NLS-1$

				for (Integer k : matching.keySet()) {
					if (k.equals(Integer.parseInt(id))) {
						tmp = tmp.substring(0, pos) + "<directoryItemId>" + matching.get(k) + tmp.substring(tmp.indexOf("</directoryItemId>", pos)); //$NON-NLS-1$ //$NON-NLS-2$
						break;
					}
				}

				pos = tmp.indexOf("<directoryItemId>", pos + 1); //$NON-NLS-1$

			}

			return tmp;
		}
		else {
			Element e = root.element("dependancies"); //$NON-NLS-1$
			for (Element _e : (List<Element>) e.elements("dependantDirectoryItemId")) { //$NON-NLS-1$
				for (Integer k : matching.keySet()) {
					if (k.equals(Integer.parseInt(_e.getStringValue()))) {
						_e.setText("" + matching.get(Integer.parseInt(_e.getStringValue()))); //$NON-NLS-1$
						break;
					}
				}
			}
			return root.asXML();
		}
	}

	private static String modifyFwrXml(HashMap<Integer, Integer> matching, String xml) throws DocumentException {

		String tmp = new String(xml);
		int pos = tmp.indexOf("<metadataId>"); //$NON-NLS-1$

		while (pos != -1) {
			String id = tmp.substring(pos + 12, tmp.indexOf("</metadataId>", pos)); //$NON-NLS-1$

			for (Integer k : matching.keySet()) {
				if (k.equals(Integer.parseInt(id))) {
					tmp = tmp.substring(0, pos) + "<metadataId>" + matching.get(k) + tmp.substring(tmp.indexOf("</metadataId>", pos)); //$NON-NLS-1$ //$NON-NLS-2$
					break;
				}
			}

			pos = tmp.indexOf("<metadataId>", pos + 1); //$NON-NLS-1$

		}

		return tmp;

	}

	private static String modifyBirtXml(HashMap<Integer, Integer> matching, String xml) throws DocumentException {

		Document document = DocumentHelper.parseText(xml);
		Element root = document.getRootElement();

		// look for the dependancies information
		Element dependanciesNode = null;

		dependanciesNode = root.element("data-sources"); //$NON-NLS-1$
		for (Object o : dependanciesNode.elements("oda-data-source")) { //$NON-NLS-1$
			if (((Element) o).attribute("extensionID").getText().equals("bpm.metadata.birt.oda.runtime")) { //$NON-NLS-1$ //$NON-NLS-2$
				for (Object p : ((Element) o).elements("property")) { //$NON-NLS-1$
					if (((Element) p).attribute("name").getText().equals("DIRECTORY_ITEM_ID")) { //$NON-NLS-1$ //$NON-NLS-2$

						for (Integer k : matching.keySet()) {
							if (k.equals(Integer.parseInt(((Element) p).getText()))) {
								((Element) p).setText("" + matching.get(Integer.parseInt(((Element) p).getStringValue()))); //$NON-NLS-1$
								break;
							}
						}

					}
				}

			}
		}

		String tmp = document.asXML();

		return tmp;

	}

	private static RepositoryDirectory getDirectory(ImportItem importItem, IRepositoryApi sock) throws Exception {
		Repository rep = new Repository(sock, importItem.getType());

		String p = importItem.getPath().replace("/", "]"); //$NON-NLS-1$ //$NON-NLS-2$
		Integer i = 0;
		String[] names = p.split("]"); //$NON-NLS-1$

		boolean found = false;
		RepositoryDirectory target = null;
		for (RepositoryDirectory d : rep.getRootDirectories()) {
			if (d.getName().equals(names[i])) {
				found = true;
				i++;
			}

			if (found && i < names.length) {
				target = lookInto(rep, importItem, sock, d, names, i);
				break;
			}
			else if (found) {
				target = d;
				break;
			}
		}

		if (target == null) {
			return createMissings(importItem, sock, null, names, i);
		}
		else {
			return target;
		}
	}

	private static RepositoryDirectory lookInto(IRepository rep, ImportItem importItem, IRepositoryApi sock, RepositoryDirectory dir, String[] names, Integer pos) throws Exception {

		for (RepositoryDirectory d : rep.getChildDirectories(dir)) {
			if (d.getName().equals(names[pos])) {
				if (pos < names.length - 1) {
					pos = pos + 1;
					return lookInto(rep, importItem, sock, d, names, pos);
				}

				return d;
			}
		}

		// create from pos
		return createMissings(importItem, sock, dir, names, pos);
	}

	private static RepositoryDirectory createMissings(ImportItem importItem, IRepositoryApi sock, RepositoryDirectory parent, String[] names, Integer pos) throws Exception {

		RepositoryDirectory dirId = null;
		for (int i = pos; i < names.length; i++) {
			RepositoryDirectory d = new RepositoryDirectory();
			d.setName(names[i]);
			if (parent == null) {
				d.setParentId(0);
			}
			else {
				d.setParentId(parent.getId());
			}

			try {
				dirId = sock.getRepositoryService().addDirectory(d.getName(), "", parent); //$NON-NLS-1$

				if (parent != null) {
					logs.append(Messages.Importer_106 + d.getName() + Messages.Importer_0 + parent.getName() + "\r\n");//$NON-NLS-1$
				}
				else {
					logs.append(Messages.Importer_109 + d.getName() + "\r\n");//$NON-NLS-1$
				}
				parent = d;
				if (newlyCreatedDirsId.get(importItem) == null) {
					newlyCreatedDirsId.put(importItem, new ArrayList<Integer>());

				}
				newlyCreatedDirsId.get(importItem).add(dirId.getId());

			} catch (Exception e) {
				e.printStackTrace();
				logs.append(Messages.Importer_111 + d.getName() + Messages.Importer_112);
				throw e;
			}

		}

		return dirId;
	}

	private static void copyInputStream(InputStream in, File f) throws IOException {
		byte[] buffer = new byte[1024];
		int len;

		OutputStream out = new FileOutputStream(f);
		;

		while ((len = in.read(buffer)) >= 0)
			out.write(buffer, 0, len);

		in.close();
		out.close();
	}

	public static HashMap<String, File> unzip(File pack, String dezipFolder) throws ZipException, IOException {
		Enumeration entries;
		ZipFile zipFile;

		HashMap<String, File> map = new HashMap<String, File>();

		zipFile = new ZipFile(pack);
		entries = zipFile.entries();

		while (entries.hasMoreElements()) {
			ZipEntry entry = (ZipEntry) entries.nextElement();

			File f = new File(dezipFolder + "/" + entry.getName()); //$NON-NLS-1$
			copyInputStream(zipFile.getInputStream(entry), f);

			map.put(entry.getName(), f);
		}

		zipFile.close();
		return map;

	}
}
