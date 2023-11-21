package bpm.vanilla.repository.services;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.osgi.framework.ServiceException;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.ManageAction;
import bpm.vanilla.platform.core.beans.PublicParameter;
import bpm.vanilla.platform.core.beans.PublicUrl;
import bpm.vanilla.platform.core.beans.PublicUrl.TypeURL;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.VanillaSession;
import bpm.vanilla.platform.core.beans.comments.CommentDefinition;
import bpm.vanilla.platform.core.beans.comments.CommentDefinition.TypeComment;
import bpm.vanilla.platform.core.beans.comments.CommentParameter;
import bpm.vanilla.platform.core.beans.comments.CommentValue;
import bpm.vanilla.platform.core.beans.comments.CommentValue.CommentStatus;
import bpm.vanilla.platform.core.beans.validation.UserValidation;
import bpm.vanilla.platform.core.beans.validation.Validation;
import bpm.vanilla.platform.core.beans.validation.ValidationCircuit;
import bpm.vanilla.platform.core.beans.validation.UserValidation.UserValidationType;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.listeners.event.impl.RepositoryItemEvent;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.repository.Comment;
import bpm.vanilla.platform.core.repository.DirectoryItemDependance;
import bpm.vanilla.platform.core.repository.IRepositoryObject;
import bpm.vanilla.platform.core.repository.ItemMetadataTableLink;
import bpm.vanilla.platform.core.repository.LinkedDocument;
import bpm.vanilla.platform.core.repository.Parameter;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.platform.core.repository.SecuredDirectory;
import bpm.vanilla.platform.core.repository.SecuredObject;
import bpm.vanilla.platform.core.repository.Template;
import bpm.vanilla.platform.core.repository.Template.TypeTemplate;
import bpm.vanilla.platform.core.repository.services.IDocumentationService;
import bpm.vanilla.platform.core.repository.services.IRepositoryAdminService;
import bpm.vanilla.platform.core.repository.services.IRepositoryService;
import bpm.vanilla.platform.core.utils.IOWriter;
import bpm.vanilla.platform.core.utils.ImpactLevel;
import bpm.vanilla.repository.beans.GlobalDAO;
import bpm.vanilla.repository.beans.RepositoryRuntimeComponent;
import bpm.vanilla.repository.beans.comments.CommentDAO;
import bpm.vanilla.repository.beans.historique.Historic;
import bpm.vanilla.repository.beans.log.ServerLoger;
import bpm.vanilla.repository.beans.model.ItemModel;
import bpm.vanilla.repository.beans.model.TemplateDAO;
import bpm.vanilla.repository.beans.security.RunnableGroup;
import bpm.vanilla.repository.beans.validation.ValidationDAO;
import bpm.vanilla.repository.services.parsers.BIRTParser;
import bpm.vanilla.repository.services.parsers.FWRParser;
import bpm.vanilla.repository.services.parsers.FactoryModelParser;
import bpm.vanilla.repository.services.parsers.FdDictionaryParser;
import bpm.vanilla.repository.services.parsers.FdParser;
import bpm.vanilla.repository.services.parsers.IModelParser;
import bpm.vanilla.repository.services.tools.DisconnectedManager;
import bpm.vanilla.repository.services.tools.ImpactDatasourceExtractor;
import bpm.vanilla.repository.services.tools.ImpactGraphGenerator;
import bpm.vanilla.workplace.core.datasource.AbstractDatasource;

public class ServiceBrowseImpl implements IRepositoryService {

	private RepositoryRuntimeComponent component;
	private int groupId;
	private int repositoryId;
	private User user;
	private String clientIp;

	private IDocumentationService documentationService;
	private IRepositoryAdminService adminService;

	public ServiceBrowseImpl(RepositoryRuntimeComponent repositoryRuntimeComponent, int groupId, int repositoryId, User user, String clientIp) throws Exception {
		this.component = repositoryRuntimeComponent;
		this.groupId = groupId;
		this.repositoryId = repositoryId;
		this.user = user;
		this.clientIp = clientIp;
		this.documentationService = component.getServiceDocumentation(repositoryId, groupId, user, clientIp);
		this.adminService = component.getServiceAdmin(repositoryId, groupId, user);
	}

	private List<RepositoryItem> buildItems(RepositoryDirectory parent) throws Exception {
		List<RepositoryItem> items = component.getRepositoryDao(repositoryId).getDirectoryDao().getDirectoryContent(repositoryId, groupId, parent == null ? 0 : parent.getId());
		for (RepositoryItem item : items) {
			buildItem(item);
		}

		return items;
	}

	private void buildItem(RepositoryItem item) throws Exception {
		boolean canComment = documentationService.canComment(groupId, item.getId(), Comment.ITEM);
		item.setCommentable(canComment);

		boolean canRun = adminService.canRun(item.getId(), groupId);
		item.setCanRun(canRun);
		
		if (item.getType() == IRepositoryApi.CUST_TYPE || item.getType() == IRepositoryApi.FD_TYPE) {
			boolean isViewable = canViewFromValidation(item, user != null ? user.getId() : null, item.getId());
			item.setDisplay(isViewable);
		}
	}

	private List<RepositoryDirectory> buildDirectories(RepositoryDirectory parent, int userId, boolean isSuperUser) throws Exception {

		int parentId = 0;
		if (parent != null) {
			parentId = parent.getId();
		}

		List<RepositoryDirectory> dirs = component.getRepositoryDao(repositoryId).getDirectoryDao().getChildDirectories(repositoryId, groupId, parentId, userId, isSuperUser);

		for (RepositoryDirectory dir : dirs) {
			buildDirectory(dir);
		}

		return dirs;
	}

	private void buildDirectory(RepositoryDirectory dir) throws Exception {
		boolean canComment = documentationService.canComment(groupId, dir.getId(), Comment.DIRECTORY);
		dir.setCommentable(canComment);
	}

	@Override
	public List<IRepositoryObject> getDirectoryContent(RepositoryDirectory parent, int type) throws Exception {
		int dirId = parent != null ? parent.getId() : 0;
		Date start = new Date();
		Date end = null;
		List<IRepositoryObject> content = new ArrayList<IRepositoryObject>();

		int userId = user != null ? user.getId() : 0;
		boolean isSuperUser = user != null ? user.isSuperUser() : false;

		if (type == IRepositoryService.ONLY_DIRECTORY || type == IRepositoryService.BOTH) {
			content.addAll(buildDirectories(parent, userId, isSuperUser));
			end = new Date();
			Logger.getLogger(this.getClass()).trace("buildDirectories for " + dirId + " took : " + (end.getTime() - start.getTime()));
		}
		if (type == IRepositoryService.ONLY_ITEM || type == IRepositoryService.BOTH) {
			content.addAll(buildItems(parent));
			end = new Date();
			Logger.getLogger(this.getClass()).trace("buildDirectories + buildItems for " + dirId + " took : " + (end.getTime() - start.getTime()));

		}

		end = new Date();
		Logger.getLogger(this.getClass()).trace("Total getContent for " + dirId + " took : " + (end.getTime() - start.getTime()));

		return content;
	}

	@Override
	public RepositoryDirectory getDirectory(int id) throws Exception {
		RepositoryDirectory d = component.getRepositoryDao(repositoryId).getDirectoryDao().getDirectory(id);
		if (d == null) {
			return null;
		}
		if (!component.getSecurityTool().isDirectoryAccessibleToGroup(id, groupId, repositoryId)) {
			throw new Exception("The group " + component.getVanillaRootApi().getVanillaSecurityManager().getGroupById(groupId).getName() + " cannot access to this Directory");
		}
		buildDirectory(d);
		return d;
	}

	@Override
	public RepositoryDirectory addDirectory(String directoryName, String comment, RepositoryDirectory parent) throws Exception {
		if (!user.isSuperUser()) {
			if (!component.getSecurityTool().isUserInGroup(user, groupId)) {
				throw new Exception(user.getLogin() + " do not belong to the given group (group.id=" + groupId + ") and is not a SuperUser");
			}
		}

		if (parent != null && !parent.isVisible()) {
			throw new Exception("The specified directory parent do not exists (Directory.parentId=" + parent.getId() + ")");
		}
		RepositoryDirectory directory = new RepositoryDirectory();
		try {
			directory.setName(directoryName);
			directory.setComment(comment);
			if (parent != null) {
				directory.setParentId(parent.getId());
			}
			directory.setId(component.getRepositoryDao(repositoryId).getDirectoryDao().save(directory));
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new Exception("Unable to save Directory in Repository Database\n" + ex.getMessage(), ex);
		}

		/*
		 * allow access for the given group
		 */
		if (groupId > 0) {
			SecuredDirectory sd = new SecuredDirectory();
			sd.setDirectoryId(directory.getId());
			sd.setGroupId(groupId);
			component.getRepositoryDao(repositoryId).getSecuredDirectoryDao().save(sd);
		}

		return directory;
	}

	private RepositoryItem _getRepositoryItem(int directoryItemId) throws Exception {
		RepositoryItem directoryItem = component.getRepositoryDao(repositoryId).getItemDao().findByPrimaryKeyNonDeleted(directoryItemId);
		if (directoryItem == null) {
			return null;
		}
		buildItem(directoryItem);
		return directoryItem;
	}

	@Override
	public RepositoryItem addDirectoryItemWithDisplay(int type, int subType, RepositoryDirectory target, String name, String comment, String internalVersion, String publicVersion, String xmlDefinition, boolean display) throws Exception {
		return addDirectoryItemWithDisplay(type, subType, target, name, comment, internalVersion, publicVersion, xmlDefinition, display, null);
	}

	public RepositoryItem addDirectoryItemWithDisplay(int type, int subType, RepositoryDirectory target, String name, String comment, String internalVersion, String publicVersion, String xmlDefinition, boolean display, String format) throws Exception {
		long start = Calendar.getInstance().getTime().getTime();

		/*
		 * We don't allowed users to save items in the main 'My Documents'
		 * folder
		 */
		if (target.getId() == 1) {
			throw new Exception("Vanilla Object can not be saved in this directory. You need to first create a folder in the Vanilla Portal and then you should be able to save it.");
		}

		/*
		 * check User grants
		 */
		if (!user.isSuperUser()) {
			if (!component.getSecurityTool().isUserInGroup(user, groupId)) {
				throw new Exception(user.getLogin() + " do not belong to the given group (group.id=" + groupId + ") and is not a SuperUser");
			}

			if (!component.getSecurityTool().canCreateItem(user, groupId, type)) {
				throw new Exception(user.getLogin() + " has no Creation Grant on object type " + IRepositoryApi.TYPES_NAMES[type]);
			}
		}

		IModelParser parser = FactoryModelParser.getModelParser(type, subType, xmlDefinition);

		List<Parameter> parameters = parser.getParameters();
		List<Integer> dependanciesId = parser.getDependanciesDirectoryItemId();
		List<Integer> dataSourcesRefId = parser.getDataSourcesReferences();

		if (parser instanceof BIRTParser) {

			((BIRTParser) parser).setRepId(repositoryId);

		}
		else if (parser instanceof FWRParser) {

			((FWRParser) parser).setRepId(repositoryId);
		}

		/*
		 * save the item
		 */
		RepositoryItem item = new RepositoryItem();
		item.setItemName(name);
		item.setComment(comment);
		item.setType(type);
		item.setSubtype(subType);
		item.setDirectoryId(target.getId());
		item.setInternalVersion(internalVersion);
		item.setPublicVersion(publicVersion);
		item.setDisplay(display);

		item.setDateCreation(new Date());
		item.setOn(true);
		item.setOwnerId(user.getId());
		item.setVisible(true);
		item.setAvailableGed(true);
		item.setRealtimeGed(true);
		item.setAndroidSupported(false);
		if (format != null) {
			item.setModelType(format);
		}

		item.setDefaultFormat(null);
		item.setNbMaxHisto(100);

		item.setId(component.getRepositoryDao(repositoryId).getItemDao().save(item));

		/*
		 * save the Model
		 */
		ItemModel model = new ItemModel();
		model.setCreationDate(new Date());
		model.setXml(xmlDefinition);
		model.setItemId(item.getId());
		model.setUserId(user.getId());
		component.getRepositoryDao(repositoryId).getItemModelDao().save(model);

		/*
		 * add the depencies
		 */
		for (Integer i : dependanciesId) {
			DirectoryItemDependance dep = new DirectoryItemDependance();
			dep.setDirItemId(item.getId());
			dep.setDependantDirItemId(i);
			component.getRepositoryDao(repositoryId).getDependanciesDao().add(dep);
		}

		/*
		 * add the link with the DataSources
		 */
		for (Integer i : dataSourcesRefId) {
			component.getRepositoryDao(repositoryId).getDatasourceImpactDao().create(item.getId(), i);
		}

		/*
		 * saveParameters
		 */
		for (Parameter p : parameters) {
			p.setDirectoryItemId(item.getId());
			int id = component.getRepositoryDao(repositoryId).getParameterDao().save(p);
			p.setId(id);
		}
		if(type == IRepositoryApi.R_MARKDOWN_TYPE){
			for (Parameter p : parameters) {
				if(p.getDataProviderId() == 0 && p.getDataProviderName() != null && ! p.getDataProviderName().isEmpty()){
					for (Parameter p2 : parameters) {
						if(p2.getName().equals(p.getDataProviderName())){
							p.setDataProviderId(p2.getId());
							component.getRepositoryDao(repositoryId).getParameterDao().update(p);
						}
					}
				}
			}
		}

		/*
		 * allow access for the given group
		 */
		if (groupId > 0) {
			SecuredObject so = new SecuredObject();
			so.setDirectoryItemId(item.getId());
			so.setGroupId(groupId);
			component.getRepositoryDao(repositoryId).getSecuredObjectDao().save(so);

			/*
			 * allow thee creator's group to run the object
			 */

			try {
				ServiceAdminImpl adminService = new ServiceAdminImpl(component, repositoryId, groupId, user);
				adminService.setObjectRunnableForGroup(groupId, item);
			} catch (Exception ex) {
				ex.printStackTrace();
				throw new Exception("Unable to set the created item as Runnable : " + ex.getMessage(), ex);
			}

			try {
				ServiceReportHistoric histoService = new ServiceReportHistoric(component, groupId, repositoryId, user, clientIp);
				histoService.createHistoricAccess(item.getId(), groupId);
			} catch (Exception ex) {
				ex.printStackTrace();
				throw new Exception("Unable to set the created item as historic : " + ex.getMessage(), ex);
			}

		}

		// parse the model to find the datasources and save them in the db

		try {
			List<AbstractDatasource> datasources = ImpactDatasourceExtractor.extractDatasources(type, new Integer(subType), xmlDefinition, item.getId());
			component.getRepositoryDao(repositoryId).getRelationalImpactDao().saveOrUpdateAll(datasources);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			if (parser instanceof BIRTParser) {
				for (CommentDefinition comDef : ((BIRTParser) parser).getCommentDefinitions(item.getId())) {
					component.getRepositoryDao(repositoryId).getCommentDefinitionDAO().save(comDef);
				}
			}
			else if (parser instanceof FdDictionaryParser) {
				for (CommentDefinition comDef : ((FdDictionaryParser) parser).getCommentDefinitions(item.getId())) {
					component.getRepositoryDao(repositoryId).getCommentDefinitionDAO().save(comDef);
				}
			}
			else if (parser instanceof FdParser && display) {

				int dicoId = ((FdParser) parser).getDictionaryId();

				List<CommentDefinition> defs = component.getRepositoryDao(repositoryId).getCommentDefinitionDAO().getCommentDefinitions(dicoId);
				for (CommentDefinition def : defs) {
					def.setItemId(item.getId());
					component.getRepositoryDao(repositoryId).getCommentDefinitionDAO().update(def);
				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		long end = Calendar.getInstance().getTime().getTime();

		if (groupId > 0)
			ServerLoger.log(clientIp, type, ServerLoger.CREATE_OPERATION, item.getId(), user, groupId, end - start, repositoryId);

		//Default public url
		if(item.getType() == IRepositoryApi.CUST_TYPE ||
				item.getType() == IRepositoryApi.BIW_TYPE ||
				item.getType() == IRepositoryApi.FASD_TYPE ||
				item.getType() == IRepositoryApi.FD_TYPE ||
				item.getType() == IRepositoryApi.GTW_TYPE) {
			
			PublicUrl publicUrl = new PublicUrl();
			publicUrl.setActive(1);
			publicUrl.setCreationDate(new Date());
			Date endDate = new Date();
			endDate.setYear(endDate.getYear() + 10);
			publicUrl.setEndDate(endDate);
			publicUrl.setGroupId(groupId > 0 ? groupId : 1);
			publicUrl.setRepositoryId(repositoryId);
			publicUrl.setDatasourceId(0);
			publicUrl.setTypeUrl(TypeURL.REPOSITORY_ITEM);
			publicUrl.setUserId(item.getOwnerId());
			publicUrl.setItemId(item.getId());
			
			
			
			int urlId = component.getVanillaRootApi().getVanillaExternalAccessibilityManager().savePublicUrl(publicUrl);
			
			for(Parameter p : parser.getParameters()) {
				PublicParameter pp = new PublicParameter();
				pp.setPublicUrlId(urlId);
				pp.setParameterName(p.getName());
				pp.setParameterValue("");

				int ppId = component.getVanillaRootApi().getVanillaExternalAccessibilityManager().addPublicParameter(pp);
			}
		}
		
		
		return item;
	}

	@Override
	public RepositoryItem addExternalDocumentWithDisplay(RepositoryDirectory target, String name, String comment, String internalVersion, String publicVersion, InputStream extDocStream, boolean display, String format) throws Exception {
		// CALL ADD DIRECTORY ITEM WITH DISPLAY FROM THE SERVLET.
		// NEED TO LOOK WHAT WE CAN DO FOR THIS.
		return null;
	}

	@Override
	public RepositoryItem addExternalUrl(RepositoryDirectory target, String name, String comment, String internalVersion, String publicVersion, String url) throws Exception {
		// CALL ADD DIRECTORY ITEM WITH DISPLAY FROM THE CLIENT.
		// NEED TO LOOK WHAT WE CAN DO FOR THIS.
		return null;
	}

	@Override
	public boolean checkItemUpdate(RepositoryItem it, Date date) throws Exception {
		for (RepositoryItem item : component.getRepositoryDao(repositoryId).getItemDao().getItem(groupId, it.getId())) {
			buildItem(item);
			if (item.getDateModification() != null) {
				
				Logger.getLogger(getClass()).debug("Item " + it.getId() + " : " + item.getDateModification().getTime() + " -- " + date.getTime());
				
				return item.getDateModification().after(date);
			}
		}


		return false;
	}

	@Override
	public void delete(RepositoryDirectory dir) throws Exception {
		if (dir == null) {
			throw new Exception("This Directory does not exist");
		}
		dir.setVisible(false);
		dir.setDateDeletion(Calendar.getInstance().getTime());
		dir.setDeletedBy(user.getId());

		component.getRepositoryDao(repositoryId).getDirectoryDao().delete(dir);

		// remove security
		for (SecuredDirectory secu : component.getRepositoryDao(repositoryId).getSecuredDirectoryDao().getFor(dir.getId())) {
			component.getRepositoryDao(repositoryId).getSecuredDirectoryDao().delete(secu);
		}
	}

	@Override
	public void delete(RepositoryItem item) throws Exception {
		long start = Calendar.getInstance().getTime().getTime();
		if (!item.isVisible()) {
			throw new Exception("This Item has already been deleted");
		}

		if (!user.isSuperUser()) {
			if (!component.getSecurityTool().canDeleteItem(user, item.getType(), item.getId(), repositoryId)) {
				throw new Exception(user.getLogin() + " cannot delete this Item");
			}
		}

		component.getRepositoryDao(repositoryId).getItemDao().delete(item);

		// remove security
		for (SecuredObject secu : component.getRepositoryDao(repositoryId).getSecuredObjectDao().getFor(item.getId())) {
			component.getRepositoryDao(repositoryId).getSecuredObjectDao().delete(secu);
		}

		// remove run grants
		for (RunnableGroup rg : component.getRepositoryDao(repositoryId).getRunnableGroupDao().getAllowedGroups(item.getId())) {
			component.getRepositoryDao(repositoryId).getRunnableGroupDao().delete(rg);
		}

		// delete the datasources
		try {
			component.getRepositoryDao(repositoryId).getRelationalImpactDao().deleteByItemId(item.getId());
		} catch (Exception e) {
			e.printStackTrace();
		}

		Validation validation = component.getRepositoryDao(repositoryId).getValidationDAO().findValidation(item.getId());
		if (validation != null) {
			component.getRepositoryDao(repositoryId).getValidationDAO().delete(validation);
		}

		long end = Calendar.getInstance().getTime().getTime();
		ServerLoger.log(clientIp, item.getType(), ServerLoger.DELETE_OPERATION, item.getId(), user, -1, end - start, repositoryId);
	}

	@Override
	public List<String> getCubeNames(RepositoryItem fasdItem) throws Exception {

		ItemModel model = component.getRepositoryDao(repositoryId).getItemModelDao().getLastVersion(fasdItem.getId());
		List<String> l = new ArrayList<String>();
		try {
			org.dom4j.Document document = DocumentHelper.parseText(model.getXml());

			Element root = document.getRootElement();

			try {
				for (Object o : root.element("olap").element("Cube").elements("Cube-item")) {
					Element e = (Element) o;
					l.add(e.element("name").getText());
				}
			} catch (Exception e) {

			}
			try {
				for (Object o : root.element("olap").element("VirtualCube").elements("VirtualCube-item")) {
					Element e = (Element) o;
					l.add(e.element("name").getText());
				}
			} catch (Exception e) {
			}
		} catch (Exception ex) {
			Logger.getLogger(getClass()).error("Could not parse FASD model xml - " + ex.getMessage(), ex);
			throw new Exception("Error when extracting cubeNames from FASD model");
		}

		return l;
	}

	@Override
	public List<RepositoryItem> getCubeViews(String cubeName, RepositoryItem fasdItem) throws Exception {

		List<RepositoryItem> l = new ArrayList<RepositoryItem>();

		for (DirectoryItemDependance dep : component.getRepositoryDao(repositoryId).getDependanciesDao().getForDirectoryItemId(fasdItem.getId())) {
			RepositoryItem view = _getRepositoryItem(dep.getDirItemId());
			if (view != null && view.getType() == IRepositoryApi.FAV_TYPE) {
				ItemModel model = component.getRepositoryDao(repositoryId).getItemModelDao().getLastVersion(view.getId());
				String s = model.getXml();
				if (s.contains("<cubename>" + cubeName + "</cubename>")) {
					buildItem(view);
					l.add(view);
				}
			}
		}

		Collections.sort(l, new Comparator<RepositoryItem>() {
			@Override
			public int compare(RepositoryItem item1, RepositoryItem item2) {
				return item1.getName().compareTo(item2.getName());
			}
		});

		return l;
	}

	@Override
	public List<RepositoryItem> getFmdtDrillers(RepositoryItem fmdtItem) throws Exception {
		List<RepositoryItem> l = new ArrayList<RepositoryItem>();

		for (DirectoryItemDependance dep : component.getRepositoryDao(repositoryId).getDependanciesDao().getForDirectoryItemId(fmdtItem.getId())) {
			RepositoryItem view = _getRepositoryItem(dep.getDirItemId());
			if (view != null && view.getType() == IRepositoryApi.FMDT_DRILLER_TYPE) {
				buildItem(view);
				l.add(view);
			}
		}

		return l;
	}

	@Override
	public HashMap<RepositoryItem, byte[]> getCubeViewsWithImageBytes(String cube, RepositoryItem fasdItem) throws Exception {
		List<RepositoryItem> views = new ArrayList<>();
		for (DirectoryItemDependance dep : component.getRepositoryDao(repositoryId).getDependanciesDao().getForDirectoryItemId(fasdItem.getId())) {
			RepositoryItem view = _getRepositoryItem(dep.getDirItemId());
			if (view != null && view.getType() == IRepositoryApi.FAV_TYPE) {
				views.add(view);
			}
		}

		Collections.sort(views, new Comparator<RepositoryItem>() {
			@Override
			public int compare(RepositoryItem item1, RepositoryItem item2) {
				return item1.getName().toLowerCase().compareTo(item2.getName().toLowerCase());
			}
		});

		HashMap<RepositoryItem, byte[]> l = new LinkedHashMap<RepositoryItem, byte[]>();
		if (views != null && !views.isEmpty()) {
			for (RepositoryItem view : views) {
				ItemModel model = component.getRepositoryDao(repositoryId).getItemModelDao().getLastVersion(view.getId());
				String s = model.getXml();
				if (s.contains("<cubename>" + cube + "</cubename>")) {
					byte[] imgBytes = null;
					try {
						// Try to get back the view image
						String img = s.subSequence(s.indexOf("<rolodeximage>") + 14, s.indexOf("</rolodeximage>")).toString();
						String[] stBytes = img.split(":");
						imgBytes = new byte[stBytes.length];
						int ig = 0;
						for (String r : stBytes) {
							imgBytes[ig] = Byte.valueOf(r);
							ig++;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

					l.put(view, imgBytes);
				}
			}
		}

		return l;
	}

	@Override
	public List<RepositoryItem> getDependantItems(RepositoryItem directoryItem) throws Exception {
		List<DirectoryItemDependance> l = component.getRepositoryDao(repositoryId).getDependanciesDao().getForDirectoryItemId(directoryItem.getId());

		List<RepositoryItem> items = new ArrayList<RepositoryItem>();
		for (DirectoryItemDependance dep : l) {
			for (RepositoryItem o : component.getRepositoryDao(repositoryId).getItemDao().getItem(groupId, dep.getDirItemId())) {
				buildItem(o);
				items.add(o);
			}
		}

		return items;
	}

	@Override
	public RepositoryItem getDirectoryItem(int directoryItemId) throws Exception {
		for (RepositoryItem item : component.getRepositoryDao(repositoryId).getItemDao().getItem(groupId, directoryItemId)) {
			buildItem(item);
			return item;
		}

		return null;
	}

	@Override
	public List<LinkedDocument> getLinkedDocumentsForGroup(int directoryItemId, int groupId) throws Exception {
		List<bpm.vanilla.platform.core.repository.LinkedDocument> l = new ArrayList<bpm.vanilla.platform.core.repository.LinkedDocument>();

		for (LinkedDocument d : component.getRepositoryDao(repositoryId).getLinkedDocumentDao().getLinkedDocuments(directoryItemId, groupId)) {
			l.add(d);
		}
		return l;
	}

	@Override
	public List<RepositoryItem> getNeededItems(int directoryItemId) throws Exception {
		List<DirectoryItemDependance> l = component.getRepositoryDao(repositoryId).getDependanciesDao().getNeededForDirectoryItemId(directoryItemId);

		List<RepositoryItem> items = new ArrayList<RepositoryItem>();
		for (DirectoryItemDependance dep : l) {
			for (RepositoryItem o : component.getRepositoryDao(repositoryId).getItemDao().getItem(groupId, dep.getDependantDirItemId())) {
				buildItem(o);
				items.add(o);
			}
		}

		return items;
	}

	@Override
	public List<Parameter> getParameters(RepositoryItem item) throws Exception {

		List<Parameter> l = component.getRepositoryDao(repositoryId).getParameterDao().findDirectoryItemId(item.getId());

		List<Parameter> r = new ArrayList<Parameter>();
		for (Parameter mp : l) {
			r.add(mp);
		}
		return r;
	}

	@Override
	public String loadModel(RepositoryItem repItem) throws Exception {
		if (!user.isSuperUser() && !component.getSecurityTool().isUserInGroup(user, groupId)) {
			throw new Exception(user.getLogin() + " not in the gievn group");
		}

		/*
		 * check if the DirIt is available for the given group
		 */
		SecuredObject so = component.getRepositoryDao(repositoryId).getSecuredObjectDao().getForItemAndGroup(repItem.getId(), groupId);
		if (groupId > 0 && so == null) {
			throw new Exception("The requested directory Item is not available for the given group");
		}
		ItemModel model = component.getRepositoryDao(repositoryId).getItemModelDao().getLastVersion(repItem.getId());
		String xml = model.getXml();

		/*
		 * keep track in lastConsulted object for the User
		 */
		if (repItem.getType() == IRepositoryApi.FASD_TYPE || repItem.getType() == IRepositoryApi.FD_TYPE || repItem.getType() == IRepositoryApi.FWR_TYPE || repItem.getType() == IRepositoryApi.CUST_TYPE || repItem.getType() == IRepositoryApi.BIW_TYPE || repItem.getType() == IRepositoryApi.GTW_TYPE || repItem.getType() == IRepositoryApi.EXTERNAL_DOCUMENT) {
			Historic consultation = new Historic();
			consultation.setDirectoryItemId(repItem.getId());
			consultation.setModelId(model.getId());
			consultation.setType(IRepositoryApi.TYPES_NAMES[repItem.getType()]);
			consultation.setUserId(user.getId());

			try {
				component.getRepositoryDao(repositoryId).getHistoricDao().save(consultation);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		String result = xml.trim();

		return result;
	}

	@Override
	public String loadUrl(RepositoryItem item) throws Exception {
		// Not Used
		return null;
	}

	@Override
	public void update(RepositoryDirectory directory) throws Exception {
		if (!user.isSuperUser()) {
			if (!component.getSecurityTool().isUserInGroup(user, groupId)) {
				throw new Exception(user.getLogin() + " do not belong to the given group (group.id=" + groupId + ") and is not a SuperUser");
			}
		}

		if (directory == null || !directory.isVisible()) {
			throw new Exception("Directory with id = " + directory.getId() + " do not exists");
		}

		component.getRepositoryDao(repositoryId).getDirectoryDao().update(directory);
	}

	@Override
	public void updateModel(RepositoryItem item, String xml) throws Exception {
		long start = Calendar.getInstance().getTime().getTime();
		if (!user.isSuperUser()) {
			if (!component.getSecurityTool().canUpdateItem(user, groupId, item.getType())) {
				throw new Exception(user.getLogin() + " has no update Grants on this Item");
			}
		}
		IModelParser parser = null;

		if (item.getType() == IRepositoryApi.CUST_TYPE) {
			// ere bug fix
			// if (xmlDefinition.startsWith("<report")){
			if (item.getSubtype() == IRepositoryApi.BIRT_REPORT_SUBTYPE) {
				parser = FactoryModelParser.getModelParser(IRepositoryApi.CUST_TYPE, IRepositoryApi.BIRT_REPORT_SUBTYPE, xml);
			}
			else if (item.getSubtype() == IRepositoryApi.JASPER_REPORT_SUBTYPE) {
				parser = FactoryModelParser.getModelParser(IRepositoryApi.CUST_TYPE, IRepositoryApi.JASPER_REPORT_SUBTYPE, xml);
			}
			else if (item.getSubtype() == IRepositoryApi.ORBEON_XFORMS) {
				parser = FactoryModelParser.getModelParser(IRepositoryApi.CUST_TYPE, IRepositoryApi.ORBEON_XFORMS, xml);
			}
		}
		else {
			parser = FactoryModelParser.getModelParser(item.getType(), null, xml);
		}

		if (item.getLockId() != null && item.getLockId() > 0) {
			throw new Exception("The model cannot be updated because it has been locked by a Checkout");
		}

		List<Parameter> parameters = parser.getParameters();
		List<Integer> dependanciesId = parser.getDependanciesDirectoryItemId();

		// String overridedXml = parser.overrideXml(xml);
		ItemModel model = new ItemModel();
		model.setItemId(item.getId());
		model.setXml(xml);
		model.setCreationDate(new Date());
		model.setUserId(user.getId());

		model = component.getRepositoryDao(repositoryId).getItemModelDao().save(model);

		/*
		 * updateDependancies
		 */
		// remove the ones that are no more needed
		List<DirectoryItemDependance> deps = component.getRepositoryDao(repositoryId).getDependanciesDao().getNeededForDirectoryItemId(item.getId());
		for (DirectoryItemDependance dep : deps) {
			boolean found = false;
			for (Integer i : dependanciesId) {
				if (dep.getDependantDirItemId().intValue() == i) {
					found = true;
					break;
				}

			}
			if (!found) {
				component.getRepositoryDao(repositoryId).getDependanciesDao().delete(dep);
			}
		}
		// add the new ones
		for (Integer i : dependanciesId) {
			boolean found = false;
			for (DirectoryItemDependance dep : deps) {
				if (dep.getDependantDirItemId().intValue() == i) {
					found = true;
					break;
				}
			}
			if (!found) {
				DirectoryItemDependance d = new DirectoryItemDependance();
				d.setDirItemId(item.getId());
				d.setDependantDirItemId(i);
				component.getRepositoryDao(repositoryId).getDependanciesDao().add(d);
			}
		}

		/*
		 * updateParameters
		 */
		List<Parameter> existingParameters = component.getRepositoryDao(repositoryId).getParameterDao().findDirectoryItemId(item.getId());

		// delete
		for (Parameter p : existingParameters) {
			boolean found = false;
			for (Parameter _p : parameters) {

				if (p.getName().equals(_p.getName())) {
					found = true;
					break;
				}
			}
			if (!found) {
				component.getRepositoryDao(repositoryId).getParameterDao().delete(p);
			}
		}

		// add new ones
		for (Parameter _p : parameters) {
			boolean found = false;
			for (Parameter p : existingParameters) {
				if (p.getName().equals(_p.getName())) {
					found = true;
					break;
				}
			}
			if (!found) {
				if (_p.getDirectoryItemId() <= 0) {
					_p.setDirectoryItemId(item.getId());
				}
				component.getRepositoryDao(repositoryId).getParameterDao().save(_p);
			}
		}

		/*
		 * update Item for modifier and modificationDate
		 */
		item.setDateModification(model.getCreationDate());
		item.setModifiedBy(user.getId());

		/*
		 * update Item
		 */
		component.getRepositoryDao(repositoryId).getItemDao().update(item);

		String sessionId = null;
		for (VanillaSession s : component.getVanillaRootApi().getVanillaSystemManager().getActiveSessions()) {
			if (s.getUser().getId().equals(user.getId())) {
				sessionId = s.getUuid();
				break;
			}
		}

		RepositoryItemEvent updateEvent = new RepositoryItemEvent(component.getVanillaComponentIdentifier(repositoryId), null, null, RepositoryItemEvent.EVENT_REPOSITORY_ITEM_UPDATED, repositoryId, groupId, item.getId(), item.getType(), item.getSubtype(), sessionId);
		try {
			component.getVanillaRootApi().getListenerService().fireEvent(updateEvent);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		// Update validation comments
		if (parser instanceof BIRTParser) {
			component.getRepositoryDao(repositoryId).getCommentDefinitionDAO().updateAll(((BIRTParser) parser).getCommentDefinitions(item.getId()), item.getId());
		}
		else if (parser instanceof FdDictionaryParser) {
			component.getRepositoryDao(repositoryId).getCommentDefinitionDAO().updateAll(((FdDictionaryParser) parser).getCommentDefinitions(item.getId()), item.getId());
		}
		else if (parser instanceof FdParser && item.isDisplay()) {

			int dicoId = ((FdParser) parser).getDictionaryId();

			List<CommentDefinition> defs = component.getRepositoryDao(repositoryId).getCommentDefinitionDAO().getCommentDefinitions(item.getId());
			for (CommentDefinition def : defs) {
				def.setItemId(item.getId());
				// component.getRepositoryDao(repositoryId).getCommentDefinitionDAO().update(def);
			}
			component.getRepositoryDao(repositoryId).getCommentDefinitionDAO().updateAll(defs, item.getId());
		}

		// Update the datasources
		try {
			component.getRepositoryDao(repositoryId).getRelationalImpactDao().deleteByItemId(item.getId());
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			List<AbstractDatasource> datasources = ImpactDatasourceExtractor.extractDatasources(item.getType(), new Integer(item.getSubtype()), xml, item.getId());
			component.getRepositoryDao(repositoryId).getRelationalImpactDao().saveOrUpdateAll(datasources);
		} catch (Exception e) {
			e.printStackTrace();
		}

		long end = Calendar.getInstance().getTime().getTime();
		ServerLoger.log(clientIp, item.getType(), ServerLoger.UPDATE_OPERATION, item.getId(), user, groupId, end - start, repositoryId);
	}

	@Override
	public byte[] createDisconnectedPackage(String projectName, int limitRows, List<RepositoryItem> items) throws Exception {
		String groupName = component.getSecurityTool().getGroupName(groupId);

		IVanillaContext vanillaCtx = component.getVanillaRootApi().getVanillaContext();
		IRepositoryContext repositoryCtx = new BaseRepositoryContext(vanillaCtx, null, null);
		IRepositoryApi repository = new RemoteRepositoryApi(repositoryCtx);

		ByteArrayOutputStream bOut = new ByteArrayOutputStream();

		DisconnectedManager manager = new DisconnectedManager(this, documentationService, repository, projectName, groupName, limitRows, items);
		manager.createOfflinePackage(bOut);

		byte[] buf = bOut.toByteArray();
		return Base64.encodeBase64(buf);
	}

	@Override
	public List<ImpactLevel> getImpactGraph(int itemId) throws Exception {
		return ImpactGraphGenerator.generateImpactGraph(itemId, component.getRepositoryDao(repositoryId), this);
	}

	@Override
	public List<RepositoryItem> getItems(String search) throws Exception {
		List<RepositoryItem> items = component.getRepositoryDao(repositoryId).getItemDao().getItems(groupId, search);
		for (RepositoryItem item : items) {
			buildItem(item);
		}

		return items;
	}
	
	/**
	 * We check if the item has a validation which hide the item from all users except admin, commentator and validator
	 * @param item 
	 * 
	 * @return true if viewable by all
	 * @throws Exception 
	 */
	private boolean canViewFromValidation(RepositoryItem item, Integer userId, int itemId) throws Exception {
		if (userId == null) {
			return item.isDisplay();
		}
		
		Validation validation = getValidation(itemId);
		if (validation != null && validation.isActif() && validation.isOffline()) {
			if (validation.getAdminUserId() == userId) {
				return true;
			}
			
			if (validation.getCommentators() != null) {
				for (UserValidation commentator : validation.getCommentators()) {
					if (commentator.getUserId() == userId) {
						return true;
					}
				}
			}
			
			if (validation.getValidators() != null) {
				for (UserValidation validator : validation.getValidators()) {
					if (validator.getUserId() == userId) {
						return true;
					}
				}
			}
			
			return false;
		}
			
		return item.isDisplay();
	}

	@Override
	public Validation addOrUpdateValidation(Validation validation) throws Exception {
		ValidationDAO validationDao = component.getRepositoryDao(repositoryId).getValidationDAO();
		return validationDao.save(validation);
	}

	@Override
	public CommentDefinition addOrUpdateCommentDefinition(CommentDefinition commentDefinition) throws Exception {
		CommentDAO commentDao = component.getRepositoryDao(repositoryId).getCommentDefinitionDAO();
		return commentDao.save(commentDefinition);
	}

	@Override
	public CommentValue addOrUpdateCommentValue(CommentValue comment) throws Exception {
		CommentDAO commentDao = component.getRepositoryDao(repositoryId).getCommentDefinitionDAO();
		return commentDao.save(comment);
	}

	@Override
	public void deleteCommentDefinition(CommentDefinition comment) throws Exception {
		CommentDAO commentDao = component.getRepositoryDao(repositoryId).getCommentDefinitionDAO();
		commentDao.delete(comment);
	}

	@Override
	public void deleteCommentValue(CommentValue comment) throws Exception {
		CommentDAO commentDao = component.getRepositoryDao(repositoryId).getCommentDefinitionDAO();
		commentDao.delete(comment);
	}
	
	@Override
	public List<Validation> getValidations(boolean includeInactive) throws Exception {
		List<Validation> validations = component.getRepositoryDao(repositoryId).getValidationDAO().getValidations(includeInactive);
		if (validations != null) {
			for (Validation validation : validations) {
				RepositoryItem item = getDirectoryItem(validation.getItemId());
				validation.setItem(item);
			}
		}
		return validations;
	}

	@Override
	public Validation getValidation(int itemId) throws Exception {
		return component.getRepositoryDao(repositoryId).getValidationDAO().findValidation(itemId);
	}

	@Override
	public List<Validation> getValidationByStartEtl(int itemId) throws Exception {
		return component.getRepositoryDao(repositoryId).getValidationDAO().getValidationByStartEtl(itemId);
	}

	@Override
	public CommentDefinition getCommentDefinition(int itemId, String commentName) throws Exception {
		CommentDAO commentDao = component.getRepositoryDao(repositoryId).getCommentDefinitionDAO();
		return commentDao.getCommentDefinition(itemId, commentName);
	}

	@Override
	public List<CommentDefinition> getCommentDefinitions(int itemId) throws Exception {
		CommentDAO commentDao = component.getRepositoryDao(repositoryId).getCommentDefinitionDAO();
		return commentDao.getCommentDefinitions(itemId);
	}

	@Override
	public CommentValue getCommentNotValidate(int commentDefinitionId) throws Exception {
		CommentDAO commentDao = component.getRepositoryDao(repositoryId).getCommentDefinitionDAO();
		return commentDao.getCommentNotValidate(commentDefinitionId);
	}

	@Override
	public List<CommentValue> getComments(int itemId, String commentName, List<CommentParameter> parameters) throws Exception {
		CommentDAO commentDao = component.getRepositoryDao(repositoryId).getCommentDefinitionDAO();
		return commentDao.getComments(itemId, commentName, parameters);
	}

	@Override
	public List<CommentValue> getComments(int commentDefinitionId, int repId, int userId) throws Exception {
		CommentDAO commentDao = component.getRepositoryDao(repositoryId).getCommentDefinitionDAO();
		return commentDao.getComments(commentDefinitionId, repId, userId);
	}

	@Override
	public void addSharedFile(String fileName, InputStream sharedFileStream) throws Exception {
		String resourceFolder = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_BIRT_RESOURCE_PATH);
		if (resourceFolder != null && !resourceFolder.isEmpty()) {
			File file = new File(resourceFolder);
			if (!file.exists()) {
				file.mkdirs();
			}

			try (FileOutputStream fos = new FileOutputStream(resourceFolder + "/" + fileName)) {
				IOWriter.write(sharedFileStream, fos, true, false);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else {
			throw new Exception("The Birt resource folder is misconfigured on the server.");
		}
	}

	@Override
	public List<RepositoryItem> getItems(List<Integer> ids) throws Exception {
		return component.getRepositoryDao(repositoryId).getItemDao().getItems(ids);
	}

	@Override
	public <T> List<Template<T>> getTemplates(boolean lightWeight, TypeTemplate type) throws Exception {
		TemplateDAO dao = component.getRepositoryDao(repositoryId).getTemplateDAO();
		return dao.getTemplates(component.getVanillaRootApi(), lightWeight, type);
	}

	@Override
	public <T> Template<T> getTemplate(int templateId) throws Exception {
		TemplateDAO dao = component.getRepositoryDao(repositoryId).getTemplateDAO();
		return dao.getTemplate(component.getVanillaRootApi(), templateId);
	}

	@Override
	public void addTemplate(Template<?> template) throws Exception {
		TemplateDAO dao = component.getRepositoryDao(repositoryId).getTemplateDAO();
		dao.saveTemplate(template);
	}

	@Override
	public void deleteTemplate(Template<?> template) throws Exception {
		TemplateDAO dao = component.getRepositoryDao(repositoryId).getTemplateDAO();
		dao.delete(template);
	}
	
	@Override
	public void addItemMetadataTableLink(ItemMetadataTableLink link) throws Exception {
		GlobalDAO dao = component.getRepositoryDao(repositoryId).getGlobalDAO();
		dao.manageItem(link, ManageAction.SAVE);
	}

	@Override
	public void deleteItemMetadataTableLink(ItemMetadataTableLink link) throws Exception {
		GlobalDAO dao = component.getRepositoryDao(repositoryId).getGlobalDAO();
		dao.manageItem(link, ManageAction.DELETE);
	}

	@Override
	public List<ItemMetadataTableLink> getMetadataLinks(int itemId) throws Exception {
		GlobalDAO dao = component.getRepositoryDao(repositoryId).getGlobalDAO();
		return dao.getMetadataLinks(itemId);
	}
	
	@Override
	public List<RepositoryItem> getPendingItemsToComment(int userId) throws Exception {
		List<RepositoryItem> itemsToComment = new ArrayList<RepositoryItem>();
		
		List<Validation> validations = component.getRepositoryDao(repositoryId).getValidationDAO().getValidationToComment(userId);
		if (validations != null) {
			for (Validation validation : validations) {
				List<CommentDefinition> definitions = component.getRepositoryDao(repositoryId).getCommentDefinitionDAO().getCommentDefinitions(validation.getItemId());

				if (definitions != null) {
					for (CommentDefinition definition : definitions) {
						CommentValue comment = component.getRepositoryDao(repositoryId).getCommentDefinitionDAO().getCommentNotValidate(definition.getId());
						int nextUser = getNextUser(validation, comment);
						if (nextUser == userId) {
							int itemId = validation.getItemId();
							
							boolean found = false;
							for (RepositoryItem item : itemsToComment) {
								if (item.getId() == itemId) {
									found = true;
									break;
								}
							}
							
							if (!found) {
								RepositoryItem item = component.getRepositoryDao(repositoryId).getItemDao().findByPrimaryKeyNonDeleted(itemId);
								if (item != null) {
									buildItem(item);
									itemsToComment.add(item);
								}
							}
						}
					}
				}
			}
		}
		
		return itemsToComment;
	}	
		
	private int getNextUser(Validation validation, CommentValue comment) {
		if (comment == null) {
			return validation.getCommentators().size() > 0 ? validation.getCommentators().get(0).getUserId() : -1;
		}

		boolean found = false;
		for (UserValidation commentator : validation.getCommentators()) {
			if (found) {
				return commentator.getUserId();
			}

			if (commentator.getUserId() == comment.getUserId()) {
				found = true;
			}
		}

		return -1;
	}
	
	@Override
	public List<ValidationCircuit> getValidationCircuits() throws Exception {
		return component.getRepositoryDao(repositoryId).getValidationDAO().getValidationCircuits();
	}
	
	@Override
	public ValidationCircuit manageValidationCircuit(ValidationCircuit circuit, ManageAction action) throws Exception {
		return component.getRepositoryDao(repositoryId).getValidationDAO().manageValidationCircuit(circuit, action);
	}

	@Override
	public void updateUserValidation(int validationId, int oldUserId, int newUserId, UserValidationType type) throws Exception {
		component.getRepositoryDao(repositoryId).getValidationDAO().updateUserValidation(validationId, oldUserId, newUserId, type);
	}
}
