package bpm.fd.api.core.model.tools;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import bpm.fd.api.core.model.FdModel;
import bpm.fd.api.core.model.FdProject;
import bpm.fd.api.core.model.FdProjectRepositoryDescriptor;
import bpm.fd.api.core.model.FdVanillaFormModel;
import bpm.fd.api.core.model.IBaseElement;
import bpm.fd.api.core.model.MultiPageFdProject;
import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.components.definition.chart.ComponentChartDefinition;
import bpm.fd.api.core.model.components.definition.chart.fusion.options.ChartDrillInfo;
import bpm.fd.api.core.model.components.definition.chart.fusion.options.TypeTarget;
import bpm.fd.api.core.model.components.definition.datagrid.ComponentDataGrid;
import bpm.fd.api.core.model.components.definition.maps.ComponentMap;
import bpm.fd.api.core.model.components.definition.maps.openlayers.ComponentOsmMap;
import bpm.fd.api.core.model.components.definition.maps.openlayers.OsmData;
import bpm.fd.api.core.model.components.definition.maps.openlayers.OsmDataSerie;
import bpm.fd.api.core.model.components.definition.maps.openlayers.OsmDataSerieMarker;
import bpm.fd.api.core.model.datas.DataSource;
import bpm.fd.api.core.model.parsers.DictionaryParser;
import bpm.fd.api.core.model.parsers.FdModelParser;
import bpm.fd.api.core.model.resources.Dictionary;
import bpm.fd.api.core.model.resources.FileCSS;
import bpm.fd.api.core.model.resources.FileImage;
import bpm.fd.api.core.model.resources.FileJavaScript;
import bpm.fd.api.core.model.resources.FileProperties;
import bpm.fd.api.core.model.resources.IResource;
import bpm.fd.api.core.model.structure.FactoryStructure;
import bpm.fd.api.core.model.structure.Folder;
import bpm.fd.api.core.model.structure.FolderPage;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.platform.core.utils.IOWriter;
import bpm.vanilla.platform.core.utils.MD5Helper;

public abstract class ModelLoader {
	/*
	 * internal to identify the depenciesId of the dictionary
	 */
	private static final String RESOURCE_NAME_ATTRIBUTE = "name";
	private static final String RESOURCE_CLASS_ATTRIBUTE = "class";
	private static final String RESOURCE_LOCALE_ATTRIBUTE = "locale";

	protected static ModelLoader instance;

	private static File loadExternalDocument(IRepositoryApi sock, RepositoryItem item, String projectPath, String resourceName) throws Exception {

		File _f = new File(projectPath);
		if(!_f.exists()) {
			_f.mkdirs();
		}

		File f = new File(projectPath + "/" + resourceName);
		FileOutputStream fos = new FileOutputStream(f);
		sock.getDocumentationService().importExternalDocument(item, fos);
		fos.close();

		return f;
	}

	private static File checkoutExternalDocument(IRepositoryApi sock, RepositoryItem item, String projectPath, String resourceName) throws Exception {

		File _f = new File(projectPath);
		if(!_f.exists()) {
			_f.mkdirs();
		}

		File f = new File(projectPath + "/" + resourceName);
		FileOutputStream fos = new FileOutputStream(f);

		IOWriter.write(sock.getVersioningService().checkOut(item), fos, true, true);

		return f;
	}

	protected static void setInstance(ModelLoader load) {
		instance = load;
	}

	/**
	 * import a Fd Project from BiRepository (rebuild only Dictionary and Model for now)
	 * 
	 * @param sock
	 *            : the connection to the repository
	 * @param item
	 *            : the item of the FdModel to load
	 * @return The FdProject once built
	 * @throws Exception
	 */
	public static FdProject loadProject(IRepositoryApi sock, RepositoryItem item, String projectPath) throws Exception {
		FdProject project = null;
		Integer dictionaryId = null;
		HashMap<IResource, Integer> resourcesId = new HashMap<IResource, Integer>();
		HashMap<FdModel, Integer> secondaryPagesId = new HashMap<FdModel, Integer>();

		RepositoryItem dictionaryItem = null;

		String modelXml;
		try {
			modelXml = sock.getRepositoryService().loadModel(item);
		} catch(Exception e1) {
			throw new Exception("Error when importing FdModel XML", e1);
		}
		Document doc;
		try {
			doc = DocumentHelper.parseText(modelXml);
		} catch(DocumentException e1) {
			throw new Exception("Error when parsing FdModel XML", e1);
		}
		Element root = doc.getRootElement();
		List<IResource> resources = new ArrayList<IResource>();

		FactoryStructure factory = new FactoryStructure();
		/*
		 * evaluate if the Project is MultiPage
		 */

		boolean isMultiPageProject = false;
		for(Element e : (List<Element>) root.element("dependancies").elements("dependantDirectoryItemId")) {
			if(e.attributeValue(RESOURCE_CLASS_ATTRIBUTE).equals(FdModel.class.getName()) || e.attributeValue(RESOURCE_CLASS_ATTRIBUTE).equals(FdVanillaFormModel.class.getName())) {
				isMultiPageProject = true;
				break;
			}
		}

		for(Element e : (List<Element>) root.element("dependancies").elements("dependantDirectoryItemId")) {

			if(project == null && e.attributeValue(RESOURCE_CLASS_ATTRIBUTE).equals(Dictionary.class.getName())) {
				Integer dirItId = Integer.parseInt(e.getStringValue());

				RepositoryItem dicoIt = null;
				String dicoXml = null;
				try {
					dicoIt = sock.getRepositoryService().getDirectoryItem(dirItId);

					if(dicoIt == null) {
						throw new Exception("Unable to find dictioary with id = " + dirItId);
					}
					dictionaryItem = dicoIt;
					dicoXml = sock.getRepositoryService().loadModel(dicoIt);
					dictionaryId = dicoIt.getId();
				} catch(Exception e1) {
					throw new Exception("Error when importing Dictionary XML", e1);
				}

				Dictionary dico = null;
				try {
					dico = new DictionaryParser().parse(DocumentHelper.parseText(dicoXml));
				} catch(Exception e1) {
					throw new Exception("Error when building Dictionary XML", e1);

				}

				try {
					project = new FdModelParser(factory, dico/* , isMultiPageProject */).parse(doc);
					project.setDictionary(dico);
				} catch(Exception e1) {
					throw new Exception("Error when building FdProject", e1);
				}

			}
			else if(projectPath != null) {
				String resourceName = e.attributeValue(RESOURCE_NAME_ATTRIBUTE);
				String resourceClassName = e.attributeValue(RESOURCE_CLASS_ATTRIBUTE);
				String resourceLocaleName = null;
				String extension = resourceName.substring(resourceName.lastIndexOf(".") + 1);
				if(e.attribute(RESOURCE_LOCALE_ATTRIBUTE) != null) {
					resourceLocaleName = e.attributeValue(RESOURCE_LOCALE_ATTRIBUTE);
				}
				Integer directoryItemId = Integer.parseInt(e.getStringValue());

				RepositoryItem repitem = sock.getRepositoryService().getDirectoryItem(directoryItemId);

				IResource r = null;

				if(resourceClassName.equals(FileCSS.class.getName())) {
					r = new FileCSS(resourceName, loadExternalDocument(sock, repitem, projectPath, resourceName));
				}
				else if(resourceClassName.equals(FileProperties.class.getName())) {
					r = new FileProperties(resourceName, resourceLocaleName, loadExternalDocument(sock, repitem, projectPath, resourceName));
				}
				else if(resourceClassName.equals(FileImage.class.getName())) {
					r = new FileImage(resourceName, loadExternalDocument(sock, repitem, projectPath, resourceName));
				}
				else if(resourceClassName.equals(FileJavaScript.class.getName())) {
					r = new FileJavaScript(resourceName, loadExternalDocument(sock, repitem, projectPath, resourceName));
				}
				// Multipage project
				else if(resourceClassName.equals(FdModel.class.getName()) || resourceClassName.equals(FdVanillaFormModel.class.getName())) {
					try {
						String _pageXml = null;
						try {
							_pageXml = sock.getRepositoryService().loadModel(repitem);
						} catch(Exception e1) {
							throw new Exception("Error when importing FdModel Page XML for DirectoryItemId " + repitem.getId(), e1);
						}

						FdModel model = new FdModelParser(factory, project.getDictionary()).parse(IOUtils.toInputStream(_pageXml, "UTF-8")).getFdModel();
						((MultiPageFdProject) project).addPageModel(model);
						secondaryPagesId.put(model, repitem.getId());
					} catch(Exception e1) {
						throw new Exception("Error when building FdProject", e1);
					}
				}
				if(r != null) {
					resourcesId.put(r, repitem.getId());
					resources.add(r);
				}

			}
		}

		// XXX : trick to reset the FdModel inside the pages
		if(project instanceof MultiPageFdProject) {
			if(project.getFdModel() != null) {
				for(IBaseElement e : project.getFdModel().getContent()) {
					if(e instanceof Folder) {
						for(IBaseElement _e : ((Folder) e).getContent()) {
							if(_e instanceof FolderPage) {
								((FolderPage) _e).getContent();
							}
						}
					}
				}
			}

			for(IComponentDefinition dg : project.getDictionary().getComponents(ComponentDataGrid.class)) {
				((ComponentDataGrid) dg).getDrillInfo().setModelPage(((MultiPageFdProject) project).getPageModel(((ComponentDataGrid) dg).getDrillInfo().getModelPageName()));
			}
			for(IComponentDefinition dg : project.getDictionary().getComponents(ComponentMap.class)) {
				((ComponentMap) dg).getDrillInfo().setModelPage(((MultiPageFdProject) project).getPageModel(((ComponentMap) dg).getDrillInfo().getModelPageName()));
			}
			for(IComponentDefinition dg : project.getDictionary().getComponents(ComponentChartDefinition.class)) {
				ChartDrillInfo drill = ((ComponentChartDefinition) dg).getDrillDatas();
				if(drill.getTypeTarget() == TypeTarget.TargetPopup && drill.getUrl() != null) {
					drill.setTargetModelPage(((MultiPageFdProject) project).getPageModel(drill.getUrl()));
				}
			}
			try {
				for(IComponentDefinition dg : project.getDictionary().getComponents(ComponentOsmMap.class)) {
					for(OsmDataSerie serie : ((OsmData)((ComponentOsmMap)dg).getDatas()).getSeries()) {
						if(serie instanceof OsmDataSerieMarker) {
							if(((OsmDataSerieMarker)serie).getTargetPageName() != null) {
								String target = ((OsmDataSerieMarker)serie).getTargetPageName();
								((OsmDataSerieMarker)serie).setTargetPage(((MultiPageFdProject) project).getPageModel(target));
							}
						}
					}
				}
			} catch (Exception e1) {
			}
		}

		for(IResource r : resources) {
			project.addResource(r);
		}

		FdProjectRepositoryDescriptor repositoryDescriptor = new FdProjectRepositoryDescriptor(project.getProjectDescriptor());
		repositoryDescriptor.setDictionaryDirectoryItemId(dictionaryId);
		repositoryDescriptor.setModelDirectoryItemId(item.getId());

		for(IResource r : resources) {
			repositoryDescriptor.addResourceId(r, resourcesId.get(r));
		}

		for(FdModel r : secondaryPagesId.keySet()) {
			repositoryDescriptor.addModelPageId(r, secondaryPagesId.get(r));
		}

		repositoryDescriptor.setLoadingModelDate(item.getDateModification());
		repositoryDescriptor.setLoadingDictionaryDate(dictionaryItem.getDateModification());
		project.adaptToRepositorySource(repositoryDescriptor);
		return project;
	}

	/**
	 * save the given Project in the given repository (create Dictionary and FdModel and resources files as External_documents on the BIRepositoryServer)
	 * 
	 * @param project
	 *            : project to save
	 * @param sock
	 *            : connection to BiRepository server
	 * @param target
	 *            : target Directory of the BiRepository
	 * @param groupName
	 *            : the Vanilla Group
	 * @param itemName
	 *            : the name of the item representing the Model
	 * @param dictionaryName
	 *            : the name of the item representing the Dictionary
	 * @throws Exception
	 */
	@Deprecated
	public static void save(FdProject project, IRepositoryApi sock, RepositoryDirectory target, String groupName, String itemName, String dictionaryName) throws Exception {
		ByteArrayOutputStream b = null;
		XMLWriter writer = null;
		String xml = null;

		try {
			b = new ByteArrayOutputStream();
			writer = new XMLWriter(b, OutputFormat.createPrettyPrint());
			writer.write(project.getDictionary().getElement());
			writer.close();

			xml = b.toString("UTF-8");
		} catch(Exception e) {
			throw new Exception("Unable to generate Dictionary Xml:" + e.getMessage(), e);
		}

		int dicoModelId = -1;
		try {
			dicoModelId = sock.getRepositoryService().addDirectoryItemWithDisplay(IRepositoryApi.FD_DICO_TYPE, -1, target, dictionaryName, "", "", "", xml, false).getId();
			// dicoModelId = sock.addItem(IRepositoryApi.FD_DICO_TYPE, target,
			// groupName,
			// dictionaryName, "",
			// "", "", "", xml);
		} catch(Exception e) {
			throw new Exception("Error when uploading Dictionary on the Repository : " + e.getMessage(), e);
		}

		/*
		 * sae resources and remember their ids
		 */
		HashMap<IResource, Integer> dependenciesId = new HashMap<IResource, Integer>();
		HashMap<IResource, Integer> _ids = new HashMap<IResource, Integer>();
		for(IResource r : project.getResources()) {

			try {

				_ids.put(r, sock.getRepositoryService().addExternalDocumentWithDisplay(target, r.getName(), "", "", "", new FileInputStream(r.getFile()), false, r.getFile().getName().substring(r.getFile().getName().lastIndexOf(".") + 1)).getId());

			} catch(Exception e) {
				e.printStackTrace();
				throw new Exception("Error when uploading Component on the Repository : " + e.getMessage(), e);
			}
		}

		// int dicoId = -1;
		// for(RepositoryItem it : ((Repository)sock.getRepository()).getAllItems()){
		// for(IResource r : _ids.keySet()){
		// if (((DirectoryItem)it).getItem().getProperty().getExtDocId() != 0) {
		// if ( ((DirectoryItem)it).getItem().getProperty().getExtDocId() == _ids.get(r).intValue()){
		// dependenciesId.put(r, it.getId());
		// break;
		// }
		// }
		// }
		// }
		for(IResource r : _ids.keySet()) {

			dependenciesId.put(r, _ids.get(r));
		}

		try {
			/*
			 * add to model resources and dictionary dependancies ids and report component Id and fmdt model ids
			 */
			project.getFdModel().addDepenciesRepository(dicoModelId, Dictionary.class, project.getDictionary().getName(), null);
			for(IResource r : dependenciesId.keySet()) {
				if(r instanceof FileProperties) {
					project.getFdModel().addDepenciesRepository(dependenciesId.get(r), r.getClass(), r.getName(), ((FileProperties) r).getLocaleName());

				}
				else {
					project.getFdModel().addDepenciesRepository(dependenciesId.get(r), r.getClass(), r.getName(), null);
				}

			}

			// for(IComponentDefinition def : project.getDictionary().getComponents()){
			// if ( def instanceof ComponentReport){
			//					
			// project.getFdModel().addDepenciesRepository(((ComponentReport)def).getDirectoryItemId(), def.getClass(), def.getName(), null);
			// }
			// }

			// for(DataSource ds : project.getDictionary().getDatasources()){
			// if (ds.getOdaExtensionId().equals("bpm.metadata.birt.oda.runtime")){
			// try{
			// int dirItid = Integer.parseInt(ds.getProperties().getProperty("DIRECTORY_ITEM_ID"));
			// project.getFdModel().addDepenciesRepository(dirItid, ds.getClass(), ds.getName(), null);
			// }catch(Exception ex){
			// ex.printStackTrace();
			// }
			//					
			// }
			// }

			if(project instanceof MultiPageFdProject) {

				for(FdModel m : ((MultiPageFdProject) project).getPagesModels()) {
					try {
						b = new ByteArrayOutputStream();
						writer = new XMLWriter(b, OutputFormat.createPrettyPrint());
						writer.write(m.getElement());
						writer.close();

						xml = b.toString("UTF-8");
					} catch(Exception ex) {
						throw new Exception("Error when generating XML for FdModel " + m.getName() + " : " + ex.getMessage(), ex);
					}

					try {

						RepositoryItem modelId = sock.getRepositoryService().addDirectoryItemWithDisplay(IRepositoryApi.FD_TYPE, -1, target, itemName + "_" + m.getName(), "", "", "", xml, false);
						project.getFdModel().addDepenciesRepository(modelId.getId(), FdModel.class, m.getName(), null);
					} catch(Exception ex) {
						throw new Exception("Error when pushing on Repository FdModel " + m.getName() + " : " + ex.getMessage(), ex);
					}
				}

			}

			b = new ByteArrayOutputStream();
			writer = new XMLWriter(b, OutputFormat.createPrettyPrint());
			writer.write(project.getFdModel().getElement());
			writer.close();

			xml = b.toString("UTF-8");
		} catch(Exception e) {
			throw new Exception("Unable to generate Model Xml:" + e.getMessage(), e);
		}

		try {
			int modelId = -1;
			if(project.getFdModel() instanceof FdVanillaFormModel) {
				modelId = sock.getRepositoryService().addDirectoryItemWithDisplay(IRepositoryApi.FD_TYPE, -1, target, itemName, "", "", "", xml, false).getId();
			}
			else {
				modelId = sock.getRepositoryService().addDirectoryItemWithDisplay(IRepositoryApi.FD_TYPE, -1, target, itemName, "", "", "", xml, true).getId();
			}

		} catch(Exception e) {
			throw new Exception("Error when uploading Model on the Repository : " + e.getMessage(), e);
		}

		project.getFdModel().cleanDependancies();
		if(project instanceof MultiPageFdProject) {
			for(FdModel m : ((MultiPageFdProject) project).getPagesModels()) {
				m.cleanDependancies();
			}
		}

	}

	private static void addAccessToGroup(IRepositoryApi sock, int itemId, Group group, boolean allowRun) throws Exception {

		Integer groupId = group.getId();

		sock.getAdminService().addGroupForItem(groupId, itemId);

		if(allowRun) {

			Group dummyGroup = new Group();
			dummyGroup.setId(groupId);
			RepositoryItem dummyItem = new RepositoryItem();
			dummyItem.setId(itemId);
			sock.getAdminService().setObjectRunnableForGroup(dummyGroup.getId(), dummyItem);
		}

	}

	/**
	 * save the given Project in the given repository for all the given Groups (create Dictionary and FdModel and resources files as External_documents on the BIRepositoryServer)
	 * 
	 * @param project
	 *            : project to save
	 * @param sock
	 *            : connection to BiRepository server
	 * @param target
	 *            : target Directory of the BiRepository
	 * @param groupName
	 *            : the Vanilla Group
	 * @param itemName
	 *            : the name of the item representing the Model
	 * @param dictionaryName
	 *            : the name of the item representing the Dictionary
	 * @throws Exception
	 */
	public static Integer save(FdProject project, IRepositoryApi sock, RepositoryDirectory target, List<Group> groups, String itemName, String dictionaryName) throws Exception {
		ByteArrayOutputStream b = null;
		XMLWriter writer = null;
		String xml = null;

		try {
			b = new ByteArrayOutputStream();
			writer = new XMLWriter(b, OutputFormat.createPrettyPrint());
			writer.write(project.getDictionary().getElement());
			writer.close();

			xml = b.toString("UTF-8");
		} catch(Exception e) {
			throw new Exception("Unable to generate Dictionary Xml:" + e.getMessage(), e);
		}

		int dicoModelId = -1;
		try {
			dicoModelId = sock.getRepositoryService().addDirectoryItemWithDisplay(IRepositoryApi.FD_DICO_TYPE, -1, target, dictionaryName, "", "", "", xml, false).getId();

			// grant access to the otherGroups
			StringBuilder accessGrantedErrors = new StringBuilder();

			for(int i = 0; i < groups.size(); i++) {
				try {
					addAccessToGroup(sock, dicoModelId, groups.get(i), false);
				} catch(Exception ex) {
					accessGrantedErrors.append("Unable to grant access to Dictionary Item for group " + groups.get(i) + " : " + ex.getMessage() + "\n");
				}
			}
			if(accessGrantedErrors.toString().length() > 0) {
				throw new Exception(accessGrantedErrors.toString());
			}

		} catch(Exception e) {
			throw new Exception("Error when uploading Dictionary on the Repository : " + e.getMessage(), e);
		}

		/*
		 * sae resources and remember their ids
		 */
		HashMap<IResource, Integer> dependenciesId = new HashMap<IResource, Integer>();
		HashMap<IResource, Integer> _ids = new HashMap<IResource, Integer>();
		for(IResource r : project.getResources()) {

			try {

				RepositoryItem p = sock.getRepositoryService().addExternalDocumentWithDisplay(target, r.getName(), "", "", "", new FileInputStream(r.getFile()), false, r.getSmallNameType());
				_ids.put(r, p.getId());

				// grant access to the otherGroups
				StringBuilder accessGrantedErrors = new StringBuilder();

				for(int i = 0; i < groups.size(); i++) {
					try {
						addAccessToGroup(sock, p.getId(), groups.get(i), false);
					} catch(Exception ex) {
						accessGrantedErrors.append("Unable to grant access to Dictionary Item for group " + groups.get(i).getName() + " : " + ex.getMessage() + "\n");
					}
				}
				if(accessGrantedErrors.toString().length() > 0) {
					throw new Exception(accessGrantedErrors.toString());
				}

			} catch(Exception e) {
				e.printStackTrace();
				throw new Exception("Error when uploading Component on the Repository : " + e.getMessage(), e);
			}
		}

		// for(RepositoryItem it : ((Repository)sock.getRepository()).getAllItems()){
		//			
		// }
		for(IResource r : _ids.keySet()) {
			// if (((DirectoryItem)it).getItem().getProperty().getExtDocId() != 0) {
			// if ( ((DirectoryItem)it).getItem().getProperty().getExtDocId() == _ids.get(r).intValue()){
			//					
			// break;
			// }
			// }
			dependenciesId.put(r, _ids.get(r));
		}

		try {
			/*
			 * add to model resources and dictionary dependancies ids and report component Id and fmdt model ids
			 */
			project.getFdModel().addDepenciesRepository(dicoModelId, Dictionary.class, project.getDictionary().getName(), null);
			for(IResource r : dependenciesId.keySet()) {
				if(r instanceof FileProperties) {
					project.getFdModel().addDepenciesRepository(dependenciesId.get(r), r.getClass(), r.getName(), ((FileProperties) r).getLocaleName());

				}
				else {
					project.getFdModel().addDepenciesRepository(dependenciesId.get(r), r.getClass(), r.getName(), null);
				}

			}

			if(project instanceof MultiPageFdProject) {

				for(FdModel m : ((MultiPageFdProject) project).getPagesModels()) {
					try {
						b = new ByteArrayOutputStream();
						writer = new XMLWriter(b, OutputFormat.createPrettyPrint());
						writer.write(m.getElement());
						writer.close();

						xml = b.toString("UTF-8");
					} catch(Exception ex) {
						throw new Exception("Error when generating XML for FdModel " + m.getName() + " : " + ex.getMessage(), ex);
					}

					try {

						RepositoryItem modelId = sock.getRepositoryService().addDirectoryItemWithDisplay(IRepositoryApi.FD_TYPE, -1, target, itemName + "_" + m.getName(), "", "", "", xml, false);
						project.getFdModel().addDepenciesRepository(modelId.getId(), FdModel.class, m.getName(), null);

						// grant access to the otherGroups
						StringBuilder accessGrantedErrors = new StringBuilder();

						for(int i = 0; i < groups.size(); i++) {
							try {
								addAccessToGroup(sock, modelId.getId(), groups.get(i), true);
							} catch(Exception ex) {
								accessGrantedErrors.append("Unable to grant access to Dictionary Item for group " + groups.get(i).getName() + " : " + ex.getMessage() + "\n");
							}
						}
						if(accessGrantedErrors.toString().length() > 0) {
							throw new Exception(accessGrantedErrors.toString());
						}

					} catch(Exception ex) {
						throw new Exception("Error when pushing on Repository FdModel " + m.getName() + " : " + ex.getMessage(), ex);
					}
				}

			}

			b = new ByteArrayOutputStream();
			writer = new XMLWriter(b, OutputFormat.createPrettyPrint());
			writer.write(project.getFdModel().getElement());
			writer.close();

			xml = b.toString("UTF-8");
		} catch(Exception e) {
			throw new Exception("Unable to generate Model Xml:" + e.getMessage(), e);
		}

		Integer itemId = 0;
		try {
			RepositoryItem point = sock.getRepositoryService().addDirectoryItemWithDisplay(IRepositoryApi.FD_TYPE, -1, target, itemName, "", "", "", xml, (project instanceof MultiPageFdProject) ? true : false);
			itemId = point.getId();
			
			// grant access to the otherGroups
			StringBuilder accessGrantedErrors = new StringBuilder();

			for(int i = 0; i < groups.size(); i++) {
				try {
					addAccessToGroup(sock, point.getId(), groups.get(i), true);
				} catch(Exception ex) {
					accessGrantedErrors.append("Unable to grant access to Dictionary Item for group " + groups.get(i).getName() + " : " + ex.getMessage() + "\n");
				}
			}
			if(accessGrantedErrors.toString().length() > 0) {
				throw new Exception(accessGrantedErrors.toString());
			}

		} catch(Exception e) {
			throw new Exception("Error when uploading Model on the Repository : " + e.getMessage(), e);
		}

		project.getFdModel().cleanDependancies();
		if(project instanceof MultiPageFdProject) {
			for(FdModel m : ((MultiPageFdProject) project).getPagesModels()) {
				m.cleanDependancies();
			}
		}

		return itemId;
	}

	/**
	 * save Dictionary On Repository
	 * 
	 * @param dictionary
	 * @param sock
	 * @param target
	 * @param groupName
	 * @param name
	 */
	@Deprecated
	public static void save(Dictionary dictionary, IRepositoryApi sock, RepositoryDirectory target, String groupName, String name) throws Exception {
		ByteArrayOutputStream b = null;
		XMLWriter writer = null;
		String xml = null;

		try {
			b = new ByteArrayOutputStream();
			writer = new XMLWriter(b, OutputFormat.createPrettyPrint());
			writer.write(dictionary.getElement());
			writer.close();

			xml = b.toString("UTF-8");
		} catch(Exception e) {
			throw new Exception("Unable to generate Dictionary Xml:" + e.getMessage(), e);
		}

		try {
			sock.getRepositoryService().addDirectoryItemWithDisplay(IRepositoryApi.FD_DICO_TYPE, -1, target, name, "", "", "", xml, false);

		} catch(Exception e) {
			throw new Exception("Error when uploading Dictionary on the Repository : " + e.getMessage(), e);
		}

	}

	/**
	 * save Dictionary On Repository
	 * 
	 * @param dictionary
	 * @param sock
	 * @param target
	 * @param groupName
	 * @param name
	 */
	public static void save(Dictionary dictionary, IRepositoryApi sock, RepositoryDirectory target, List<Group> groups, String name) throws Exception {
		ByteArrayOutputStream b = null;
		XMLWriter writer = null;
		String xml = null;

		try {
			b = new ByteArrayOutputStream();
			writer = new XMLWriter(b, OutputFormat.createPrettyPrint());
			writer.write(dictionary.getElement());
			writer.close();

			xml = b.toString("UTF-8");
		} catch(Exception e) {
			throw new Exception("Unable to generate Dictionary Xml:" + e.getMessage(), e);
		}

		try {
			int dicoId = sock.getRepositoryService().addDirectoryItemWithDisplay(IRepositoryApi.FD_DICO_TYPE, -1, target, name, "", "", "", xml, false).getId();

			// grant access to the otherGroups
			StringBuilder accessGrantedErrors = new StringBuilder();

			for(int i = 0; i < groups.size(); i++) {
				try {
					addAccessToGroup(sock, dicoId, groups.get(i), false);
				} catch(Exception ex) {
					accessGrantedErrors.append("Unable to grant access to Dictionary Item for group " + groups.get(i).getName() + " : " + ex.getMessage() + "\n");
				}
			}
			if(accessGrantedErrors.toString().length() > 0) {
				throw new Exception(accessGrantedErrors.toString());
			}

		} catch(Exception e) {
			throw new Exception("Error when uploading Dictionary on the Repository : " + e.getMessage(), e);
		}

	}

	/**
	 * import a Fd Project from BiRepository the FMDT dataSources are modified to use the sock User/password and the given Group
	 * 
	 * @param group
	 *            : the group from the context
	 * @param sock
	 *            : the connection to the repository
	 * @param item
	 *            : the item of the FdModel to load
	 * @return The FdProject once built
	 * @throws Exception
	 */
	public static FdProject loadProjectFromVanilla(IRepositoryApi sock, String group, RepositoryItem item, String projectPath) throws Exception {
		FdProject project = null;
		String modelXml;

		try {
			modelXml = sock.getRepositoryService().loadModel(item);
		} catch(Exception e1) {
			throw new Exception("Error when importing FdModel XML", e1);
		}
		Document doc;
		try {
			doc = DocumentHelper.parseText(modelXml);
		} catch(DocumentException e1) {
			throw new Exception("Error when parsing FdModel XML", e1);
		}
		Element root = doc.getRootElement();
		List<IResource> resources = new ArrayList<IResource>();

		FactoryStructure factory = new FactoryStructure();

		/*
		 * evaluate if the Project is MultiPage
		 */

		// boolean isMultiPageProject = false;
		for(Element e : (List<Element>) root.element("dependancies").elements("dependantDirectoryItemId")) {
			if(e.attributeValue(RESOURCE_CLASS_ATTRIBUTE).equals(FdModel.class.getName()) || e.attributeValue(RESOURCE_CLASS_ATTRIBUTE).equals(FdVanillaFormModel.class.getName())) {
				// isMultiPageProject = true;
				break;
			}
		}

		for(Element e : (List<Element>) root.element("dependancies").elements("dependantDirectoryItemId")) {
			if(project == null && e.attributeValue(RESOURCE_CLASS_ATTRIBUTE).equals(Dictionary.class.getName())) {
				Integer dirItId = Integer.parseInt(e.getStringValue());

				RepositoryItem dicoIt = null;
				String dicoXml = null;
				try {
					dicoIt = sock.getRepositoryService().getDirectoryItem(dirItId);

					if(dicoIt == null) {
						throw new Exception("Unable to find dictioary with id = " + dirItId);
					}
					dicoXml = sock.getRepositoryService().loadModel(dicoIt);
				} catch(Exception e1) {
					throw new Exception("Error when importing Dictionary XML", e1);
				}

				Dictionary dico;
				try {

					Document docDico = DocumentHelper.parseText(dicoXml);

					// XXX manu
					if(instance != null)
						instance.overrideDictionary(docDico);

					// update the dictionary Login/Password/Group in the XML
					for(Element eDs : (List<Element>) docDico.getRootElement().elements("dataSource")) {
						if("bpm.metadata.birt.oda.runtime".equals(eDs.element("odaExtensionDataSourceId").getStringValue()) || "bpm.fwr.oda.runtime".equals(eDs.element("odaExtensionDataSourceId").getStringValue())) {
							Element passwordElement = null;
							Element encryptedElement = null;
							boolean hasEncryptionProperty = false;
							for(Element eProp : (List<Element>) eDs.elements("publicProperty")) {
								if(eProp.attributeValue("name").equals("GROUP_NAME")) {
									eProp.setText(group);
								}
								if(eProp.attributeValue("name").equals("USER")) {
									eProp.setText(sock.getContext().getVanillaContext().getLogin());
								}
								if(eProp.attributeValue("name").equals("PASSWORD")) {
									passwordElement = eProp;
									eProp.setText(sock.getContext().getVanillaContext().getPassword());
								}
								if(eProp.attributeValue("name").equals("IS_ENCRYPTED")) {
									encryptedElement = eProp;
									hasEncryptionProperty = true;
									eProp.setText("true");
								}
								/*
								 * ere, added support to replace url's
								 */
								else if(eProp.attributeValue("name").equals("URL")) {
									eProp.setText(sock.getContext().getRepository().getUrl());
								}
							}
							// XXX ERE, should fix the password problem
							// guarantees that the password will be encrypted and that the encryption property is set to true;
							String password = passwordElement.getText();

							if(hasEncryptionProperty) {
								// verify that the password is encrypted
								if(password.matches("[0-9a-f]{32}")) {
									// encrypted, good
								}
								else {
									// not encrypted, encrypt
									passwordElement.setText(MD5Helper.encode(passwordElement.getStringValue()));
								}
							}
							else { // shouldnt be encryted, but verify
								if(password.matches("[0-9a-f]{32}")) {
									// actually encrypted, set encryption to true;
									eDs.addElement("publicProperty").addAttribute("name", "IS_ENCRYPTED").setText("true");
								}
								else {
									// not encrypted, encrypt and set to true
									passwordElement.setText(MD5Helper.encode(passwordElement.getStringValue()));
									eDs.addElement("publicProperty").addAttribute("name", "IS_ENCRYPTED").setText("true");
								}
							}
						}

					}

					Logger.getLogger(ModelLoader.class).info("parse dictionary id=" + dirItId + " for item=" + item.getId());
					dico = new DictionaryParser().parse(docDico);
				} catch(Exception e1) {
					throw new Exception("Error when building Dictionary XML", e1);

				}

				try {
					Logger.getLogger(ModelLoader.class).info("parse model id=" + dirItId + " for item=" + item.getId());
					project = new FdModelParser(factory, dico/* , isMultiPageProject */).parse(doc);
					project.setDictionary(dico);
				} catch(Exception e1) {
					throw new Exception("Error when building FdProject", e1);
				}

			}
			else if(projectPath != null) {
				String resourceName = e.attributeValue(RESOURCE_NAME_ATTRIBUTE);
				String resourceClassName = e.attributeValue(RESOURCE_CLASS_ATTRIBUTE);
				String resourceLocaleName = null;
				String extension = resourceName.substring(resourceName.lastIndexOf(".") + 1);
				if(e.attribute(RESOURCE_LOCALE_ATTRIBUTE) != null) {
					resourceLocaleName = e.attributeValue(RESOURCE_LOCALE_ATTRIBUTE);
				}
				Integer directoryItemId = Integer.parseInt(e.getStringValue());

				RepositoryItem repitem = sock.getRepositoryService().getDirectoryItem(directoryItemId);

				IResource r = null;

				if(resourceClassName.equals(FileCSS.class.getName())) {
					r = new FileCSS(resourceName, loadExternalDocument(sock, repitem, projectPath, resourceName));
				}
				else if(resourceClassName.equals(FileProperties.class.getName())) {
					r = new FileProperties(resourceName, resourceLocaleName, loadExternalDocument(sock, repitem, projectPath, resourceName));
				}
				else if(resourceClassName.equals(FileImage.class.getName())) {
					r = new FileImage(resourceName, loadExternalDocument(sock, repitem, projectPath, resourceName));
				}
				else if(resourceClassName.equals(FileJavaScript.class.getName())) {
					r = new FileJavaScript(resourceName, loadExternalDocument(sock, repitem, projectPath, resourceName));
				}
				// Multipage project
				else if(resourceClassName.equals(FdModel.class.getName()) || resourceClassName.equals(FdVanillaFormModel.class.getName())) {
					try {
						String _pageXml = null;
						try {
							_pageXml = sock.getRepositoryService().loadModel(repitem);
						} catch(Exception e1) {
							throw new Exception("Error when importing FdModel Page XML for DirectoryItemId " + repitem.getId(), e1);
						}

						FdModel model = new FdModelParser(factory, project.getDictionary()).parse(IOUtils.toInputStream(_pageXml, "UTF-8")).getFdModel();
						((MultiPageFdProject) project).addPageModel(model);
					} catch(Exception e1) {
						throw new Exception("Error when building FdProject", e1);
					}
				}

				resources.add(r);

			}
		}

		for(IResource r : resources) {
			project.addResource(r);
		}

		// XXX : trick to reset the FdModel inside the pages
		if(project instanceof MultiPageFdProject) {
			if(project.getFdModel() != null) {
				for(IBaseElement e : project.getFdModel().getContent()) {
					if(e instanceof Folder) {
						for(IBaseElement _e : ((Folder) e).getContent()) {
							if(_e instanceof FolderPage) {
								((FolderPage) _e).getContent();
							}
						}
					}
				}
			}
			for(IComponentDefinition dg : project.getDictionary().getComponents(ComponentDataGrid.class)) {
				((ComponentDataGrid) dg).getDrillInfo().setModelPage(((MultiPageFdProject) project).getPageModel(((ComponentDataGrid) dg).getDrillInfo().getModelPageName()));
			}
			for(IComponentDefinition dg : project.getDictionary().getComponents(ComponentMap.class)) {
				((ComponentMap) dg).getDrillInfo().setModelPage(((MultiPageFdProject) project).getPageModel(((ComponentMap) dg).getDrillInfo().getModelPageName()));
			}
			for(IComponentDefinition dg : project.getDictionary().getComponents(ComponentChartDefinition.class)) {
				ChartDrillInfo drill = ((ComponentChartDefinition) dg).getDrillDatas();
				if(drill.getTypeTarget() == TypeTarget.TargetPopup && drill.getUrl() != null) {
					drill.setTargetModelPage(((MultiPageFdProject) project).getPageModel(drill.getUrl()));
				}
			}
			try {
				for(IComponentDefinition dg : project.getDictionary().getComponents(ComponentOsmMap.class)) {
					for(OsmDataSerie serie : ((OsmData)((ComponentOsmMap)dg).getDatas()).getSeries()) {
						if(serie instanceof OsmDataSerieMarker) {
							if(((OsmDataSerieMarker)serie).getTargetPageName() != null) {
								String target = ((OsmDataSerieMarker)serie).getTargetPageName();
								((OsmDataSerieMarker)serie).setTargetPage(((MultiPageFdProject) project).getPageModel(target));
							}
						}
					}
				}
			} catch(Exception e1) {
			}
		}

		return project;
	}

	/**
	 * update the given element of the given FdProject on the repository
	 * 
	 * @param elementToUpdate
	 *            : list of element to Update(Iresource, FdModel, Dictionary)
	 * @param elementToUpdate
	 *            : list of element to create(Iresource, FdModel, Dictionary)
	 * @param project
	 *            : project to update, it must come from repository with a FdProjectRepositoryDescriptor
	 * @param sock
	 *            : the repository connection
	 * @param rep
	 *            : a fully loaded repository (must contains FD, FD_DICO, and EXTERNAL_OBJECT
	 * @throws Exception
	 */
	public static void update(List<Object> elementToUpdate, List<Object> elementToAdd, FdProject project, IRepositoryApi sock, String groupName) throws Exception {
		FdProjectRepositoryDescriptor desc = (FdProjectRepositoryDescriptor) project.getProjectDescriptor();

		String mainModelDirectoryItemName = null;

		try {
			mainModelDirectoryItemName = sock.getRepositoryService().getDirectoryItem(desc.getModelDirectoryItemId()).getItemName();
		} catch(Exception ex) {
			ex.printStackTrace();
			mainModelDirectoryItemName = project.getFdModel().getName();
		}
		/*
		 * we only push the Resources to have their ids to be able to add them different FDModels
		 */
		for(Object o : elementToAdd) {
			if(o instanceof Dictionary) {
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				XMLWriter xmlWr = null;
				try {
					new XMLWriter(out, OutputFormat.createPrettyPrint());
				} catch(UnsupportedEncodingException e1) {
					e1.printStackTrace();
				}
				xmlWr.write(project.getDictionary().getElement());
				xmlWr.close();

				RepositoryItem it = sock.getRepositoryService().getDirectoryItem(desc.getModelDirectoryItemId());

				int dicoId = sock.getRepositoryService().addDirectoryItemWithDisplay(IRepositoryApi.FD_DICO_TYPE, -1, sock.getRepositoryService().getDirectory(it.getDirectoryId()), desc.getDictionaryName(), "", "", "", out.toString("UTF-8"), false).getId();
				desc.setDictionaryDirectoryItemId(dicoId);

			}
			else if(o instanceof IResource) {
				RepositoryItem i = sock.getRepositoryService().getDirectoryItem(desc.getModelDirectoryItemId());
				int rId = sock.getRepositoryService().addExternalDocumentWithDisplay(sock.getRepositoryService().getDirectory(i.getDirectoryId()), ((IResource) o).getName(), "", "", "", new FileInputStream(((IResource) o).getFile()), false, ((IResource) o).getSmallNameType()).getId();
				desc.addResourceId((IResource) o, rId);
			}

		}

		HashMap<String, Exception> errors = new HashMap<String, Exception>();
		for(Object o : elementToUpdate) {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			XMLWriter xmlWr = null;
			try {
				xmlWr = new XMLWriter(out, OutputFormat.createPrettyPrint());
			} catch(UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
			if(o instanceof FdModel) {
				try {
					// add dependancies in XML
					if(desc.getDictionaryDirectoryItemId() != null) {

						project.getFdModel().addDepenciesRepository(desc.getDictionaryDirectoryItemId(), Dictionary.class, desc.getDictionaryName(), null);
					}
					for(IResource r : project.getResources()) {
						if(desc.getResourceId(r) != null) {
							if(r instanceof FileProperties) {
								project.getFdModel().addDepenciesRepository(desc.getResourceId(r), r.getClass(), r.getName(), ((FileProperties) r).getLocaleName());

							}
							else {
								project.getFdModel().addDepenciesRepository(desc.getResourceId(r), r.getClass(), r.getName(), null);
							}
						}
					}

					if(desc.getModelPageId((FdModel) o) != null) {
						project.getFdModel().addDepenciesRepository(desc.getModelPageId((FdModel) o), o.getClass(), ((FdModel) o).getName(), null);
					}

					// for(IComponentDefinition def : project.getDictionary().getComponents()){
					// if ( def instanceof ComponentReport){
					//							
					// project.getFdModel().addDepenciesRepository(((ComponentReport)def).getDirectoryItemId(), def.getClass(), def.getName(), null);
					// }
					// }

					for(DataSource ds : project.getDictionary().getDatasources()) {
						if(ds.getOdaExtensionId().equals("bpm.metadata.birt.oda.runtime")) {
							try {
								int dirItid = Integer.parseInt(ds.getProperties().getProperty("DIRECTORY_ITEM_ID"));
								project.getFdModel().addDepenciesRepository(dirItid, ds.getClass(), ds.getName(), null);
							} catch(Exception ex) {
								ex.printStackTrace();
							}

						}
					}

					xmlWr.write(((FdModel) o).getElement());
					xmlWr.close();
					if(desc.getModelPageId((FdModel) o) != null) {
						sock.getRepositoryService().updateModel(sock.getRepositoryService().getDirectoryItem(desc.getModelPageId((FdModel) o)), out.toString("UTF-8"));
					}

				} catch(Exception e) {
					errors.put(((FdModel) o).getName(), e);
				}
			}
			else if(o instanceof Dictionary) {
				try {
					xmlWr.write(project.getDictionary().getElement());
					xmlWr.close();
					sock.getRepositoryService().updateModel(sock.getRepositoryService().getDirectoryItem(desc.getDictionaryDirectoryItemId()), out.toString("UTF-8"));
				} catch(Exception e) {
					e.printStackTrace();
					errors.put(((Dictionary) o).getName(), e);

				}
			}
			else if(o instanceof IResource) {
				try {
					sock.getDocumentationService().updateExternalDocument(sock.getRepositoryService().getDirectoryItem(desc.getResourceId((IResource) o)), new FileInputStream(((IResource) o).getFile()));
				} catch(Exception e) {
					e.printStackTrace();
					errors.put(((IResource) o).getName(), e);

				}
			}

		}

		/*
		 * push the new FdModels
		 */
		for(Object o : elementToAdd) {
			if(o instanceof FdModel) {
				for(IResource r : desc.getResourcesKeys()) {
					if(desc.getResourceId(r) != null) {
						((FdModel) o).addDepenciesRepository(desc.getResourceId(r), r.getClass(), r.getName(), null);
					}
				}

				((FdModel) o).addDepenciesRepository(desc.getDictionaryDirectoryItemId(), Dictionary.class, project.getDictionary().getName(), null);

				ByteArrayOutputStream out = new ByteArrayOutputStream();
				XMLWriter xmlWr = null;
				try {
					xmlWr = new XMLWriter(out, OutputFormat.createPrettyPrint());
				} catch(UnsupportedEncodingException e1) {
					e1.printStackTrace();
				}
				xmlWr.write(((FdModel) o).getElement());
				xmlWr.close();
				RepositoryItem i = sock.getRepositoryService().getDirectoryItem(desc.getModelDirectoryItemId());
				int modelId = sock.getRepositoryService().addDirectoryItemWithDisplay(IRepositoryApi.FD_TYPE, -1, sock.getRepositoryService().getDirectory(i.getDirectoryId()), mainModelDirectoryItemName + "_" + ((FdModel) o).getName(), "", "", "", out.toString("UTF-8"), false).getId();
				desc.addModelPageId((FdModel) o, modelId);
			}
		}

		// if (elementToUpdate.contains(project.getFdModel())){

		for(IResource r : desc.getResourcesKeys()) {
			project.getFdModel().addDepenciesRepository(desc.getResourceId(r), r.getClass(), r.getName(), null);
		}
		for(FdModel r : desc.getPagesKeys()) {
			project.getFdModel().addDepenciesRepository(desc.getModelPageId(r), FdModel.class, r.getName(), null);
		}

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		XMLWriter xmlWr = null;
		try {
			xmlWr = new XMLWriter(out, OutputFormat.createPrettyPrint());
			xmlWr.write(project.getFdModel().getElement());
		} catch(UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		try {
			sock.getRepositoryService().updateModel(sock.getRepositoryService().getDirectoryItem(desc.getModelDirectoryItemId()), out.toString("UTF-8"));
		} catch(Exception e) {
			e.printStackTrace();
			errors.put(project.getFdModel().getName(), e);

		}

		// }

		StringBuffer buf = new StringBuffer();
		if(!errors.isEmpty()) {
			for(String s : errors.keySet()) {
				buf.append("Prolem on object " + s + ":\n");
				buf.append("\t-" + errors.get(s));
			}

			throw new Exception(buf.toString());
		}
	}

	/**
	 * import a Fd Project from BiRepository (rebuild only Dictionary and Model for now)
	 * 
	 * @param sock
	 *            : the connection to the repository
	 * @param item
	 *            : the item of the FdModel to load
	 * @return The FdProject once built
	 * @throws Exception
	 */
	public static FdProject checkoutProject(IRepositoryApi sock, RepositoryItem item, String projectPath) throws Exception {
		FdProject project = null;
		Integer dictionaryId = null;
		HashMap<IResource, Integer> resourcesId = new HashMap<IResource, Integer>();

		HashMap<FdModel, Integer> secondaryPagesId = new HashMap<FdModel, Integer>();

		RepositoryItem dictionaryItem = null;

		String modelXml;
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			IOWriter.write(sock.getVersioningService().checkOut(item), bos, true, true);
			modelXml = bos.toString("UTF-8");
		} catch(Exception e1) {
			throw new Exception("Error when importing FdModel XML", e1);
		}
		Document doc;
		try {
			doc = DocumentHelper.parseText(modelXml);
		} catch(DocumentException e1) {
			throw new Exception("Error when parsing FdModel XML", e1);
		}
		Element root = doc.getRootElement();
		List<IResource> resources = new ArrayList<IResource>();

		FactoryStructure factory = new FactoryStructure();
		/*
		 * evaluate if the Project is MultiPage
		 */

		boolean isMultiPageProject = false;
		for(Element e : (List<Element>) root.element("dependancies").elements("dependantDirectoryItemId")) {
			if(e.attributeValue(RESOURCE_CLASS_ATTRIBUTE).equals(FdModel.class.getName()) || e.attributeValue(RESOURCE_CLASS_ATTRIBUTE).equals(FdVanillaFormModel.class.getName())) {
				isMultiPageProject = true;
				break;
			}
		}

		for(Element e : (List<Element>) root.element("dependancies").elements("dependantDirectoryItemId")) {

			if(project == null && e.attributeValue(RESOURCE_CLASS_ATTRIBUTE).equals(Dictionary.class.getName())) {
				Integer dirItId = Integer.parseInt(e.getStringValue());

				RepositoryItem dicoIt = null;
				String dicoXml = null;
				try {
					dicoIt = sock.getRepositoryService().getDirectoryItem(dirItId);

					if(dicoIt == null) {
						throw new Exception("Unable to find dictioary with id = " + dirItId);
					}
					dictionaryItem = dicoIt;
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					IOWriter.write(sock.getVersioningService().checkOut(dicoIt), bos, true, true);
					dicoXml = bos.toString("UTF-8");
					dictionaryId = dicoIt.getId();
				} catch(Exception e1) {
					throw new Exception("Error when importing Dictionary XML", e1);
				}

				Dictionary dico = null;
				try {
					dico = new DictionaryParser().parse(DocumentHelper.parseText(dicoXml));
				} catch(Exception e1) {
					throw new Exception("Error when building Dictionary XML", e1);

				}

				try {
					project = new FdModelParser(factory, dico/* , isMultiPageProject */).parse(doc);
					project.setDictionary(dico);
				} catch(Exception e1) {
					throw new Exception("Error when building FdProject", e1);
				}

			}
			else if(projectPath != null) {
				String resourceName = e.attributeValue(RESOURCE_NAME_ATTRIBUTE);
				String resourceClassName = e.attributeValue(RESOURCE_CLASS_ATTRIBUTE);
				String resourceLocaleName = null;
				String extension = resourceName.substring(resourceName.lastIndexOf(".") + 1);
				if(e.attribute(RESOURCE_LOCALE_ATTRIBUTE) != null) {
					resourceLocaleName = e.attributeValue(RESOURCE_LOCALE_ATTRIBUTE);
				}
				Integer directoryItemId = Integer.parseInt(e.getStringValue());

				RepositoryItem repitem = sock.getRepositoryService().getDirectoryItem(directoryItemId);

				IResource r = null;
				// XXX : call a checkou insteadof loadExternalDocument
				if(resourceClassName.equals(FileCSS.class.getName())) {
					r = new FileCSS(resourceName, checkoutExternalDocument(sock, repitem, projectPath, resourceName));
				}
				else if(resourceClassName.equals(FileProperties.class.getName())) {
					r = new FileProperties(resourceName, resourceLocaleName, checkoutExternalDocument(sock, repitem, projectPath, resourceName));
				}
				else if(resourceClassName.equals(FileImage.class.getName())) {
					r = new FileImage(resourceName, checkoutExternalDocument(sock, repitem, projectPath, resourceName));
				}
				else if(resourceClassName.equals(FileJavaScript.class.getName())) {
					r = new FileJavaScript(resourceName, checkoutExternalDocument(sock, repitem, projectPath, resourceName));
				}
				// Multipage project
				else if(resourceClassName.equals(FdModel.class.getName()) || resourceClassName.equals(FdVanillaFormModel.class.getName())) {
					try {
						String _pageXml = null;
						try {
							_pageXml = sock.getRepositoryService().loadModel(repitem);
						} catch(Exception e1) {
							throw new Exception("Error when importing FdModel Page XML for DirectoryItemId " + repitem.getId(), e1);
						}

						FdModel model = new FdModelParser(factory, project.getDictionary()).parse(IOUtils.toInputStream(_pageXml, "UTF-8")).getFdModel();
						((MultiPageFdProject) project).addPageModel(model);
						secondaryPagesId.put(model, repitem.getId());

					} catch(Exception e1) {
						throw new Exception("Error when building FdProject", e1);
					}
				}
				if(r != null) {
					resourcesId.put(r, repitem.getId());
					resources.add(r);
				}

			}
		}

		// XXX : trick to reset the FdModel inside the pages
		if(project instanceof MultiPageFdProject) {
			if(project.getFdModel() != null) {
				for(IBaseElement e : project.getFdModel().getContent()) {
					if(e instanceof Folder) {
						for(IBaseElement _e : ((Folder) e).getContent()) {
							if(_e instanceof FolderPage) {
								((FolderPage) _e).getContent();
							}
						}
					}
				}
			}

			for(IComponentDefinition dg : project.getDictionary().getComponents(ComponentDataGrid.class)) {
				((ComponentDataGrid) dg).getDrillInfo().setModelPage(((MultiPageFdProject) project).getPageModel(((ComponentDataGrid) dg).getDrillInfo().getModelPageName()));
			}
			for(IComponentDefinition dg : project.getDictionary().getComponents(ComponentMap.class)) {
				((ComponentMap) dg).getDrillInfo().setModelPage(((MultiPageFdProject) project).getPageModel(((ComponentMap) dg).getDrillInfo().getModelPageName()));
			}
			for(IComponentDefinition dg : project.getDictionary().getComponents(ComponentChartDefinition.class)) {
				ChartDrillInfo drill = ((ComponentChartDefinition) dg).getDrillDatas();
				if(drill.getTypeTarget() == TypeTarget.TargetPopup && drill.getUrl() != null) {
					drill.setTargetModelPage(((MultiPageFdProject) project).getPageModel(drill.getUrl()));
				}
			}
			for(IComponentDefinition dg : project.getDictionary().getComponents(ComponentOsmMap.class)) {
				for(OsmDataSerie serie : ((OsmData)((ComponentOsmMap)dg).getDatas()).getSeries()) {
					if(serie instanceof OsmDataSerieMarker) {
						if(((OsmDataSerieMarker)serie).getTargetPageName() != null) {
							String target = ((OsmDataSerieMarker)serie).getTargetPageName();
							((OsmDataSerieMarker)serie).setTargetPage(((MultiPageFdProject) project).getPageModel(target));
						}
					}
				}
			}
		}

		for(IResource r : resources) {
			project.addResource(r);
		}

		FdProjectRepositoryDescriptor repositoryDescriptor = new FdProjectRepositoryDescriptor(project.getProjectDescriptor());
		repositoryDescriptor.setDictionaryDirectoryItemId(dictionaryId);
		repositoryDescriptor.setModelDirectoryItemId(item.getId());

		for(IResource r : resources) {
			repositoryDescriptor.addResourceId(r, resourcesId.get(r));
		}

		for(FdModel r : secondaryPagesId.keySet()) {
			repositoryDescriptor.addModelPageId(r, secondaryPagesId.get(r));
		}

		repositoryDescriptor.setLoadingModelDate(item.getDateModification());
		repositoryDescriptor.setLoadingDictionaryDate(dictionaryItem.getDateModification());
		project.adaptToRepositorySource(repositoryDescriptor);
		return project;
	}

	abstract public void overrideDictionary(Document docDico);

	public static boolean checkinProject(IRepositoryApi sock, int directoryItemId, FdProject project) throws Exception {

		List<RepositoryItem> dependancies = sock.getRepositoryService().getNeededItems(directoryItemId);

		HashMap<Object, Integer> dependanciesId = new HashMap<Object, Integer>();

		for(RepositoryItem its : dependancies) {
			switch(its.getType()) {
				case IRepositoryApi.FD_DICO_TYPE:
					try {
						dependanciesId.put(project.getDictionary(), its.getId());
						sock.getVersioningService().checkIn(its, "", new ByteArrayInputStream(project.getDictionaryAsXML().getBytes("UTF-8")));
					} catch(Exception ex) {
						ex.printStackTrace();
					}

					break;
				case IRepositoryApi.FD_TYPE:

					for(FdModel m : ((MultiPageFdProject) project).getPagesModels()) {
						if(m.getName().equals(its.getItemName())) {
							try {
								dependanciesId.put(m, its.getId());
								sock.getVersioningService().checkIn(its, "", new ByteArrayInputStream(project.getFdModelAsXML(m).getBytes("UTF-8")));
							} catch(Exception ex) {
								ex.printStackTrace();
							}

							break;
						}
					}

					break;
				case IRepositoryApi.EXTERNAL_DOCUMENT:
					for(IResource r : project.getResources()) {
						if(r.getName().equals(its.getItemName())) {
							// checkin
							try {
								dependanciesId.put(r, its.getId());
								sock.getVersioningService().checkIn(its, "", new FileInputStream(r.getFile()));
							} catch(Exception ex) {
								ex.printStackTrace();
							}

							break;
						}
					}

					break;
			}
		}

		/*
		 * add model dependancies in XML
		 */
		for(Object r : dependanciesId.keySet()) {
			if(r instanceof IResource) {
				if(r instanceof FileProperties) {
					project.getFdModel().addDepenciesRepository(dependanciesId.get(r), r.getClass(), ((IResource) r).getName(), ((FileProperties) r).getLocaleName());

				}
				else {
					project.getFdModel().addDepenciesRepository(dependanciesId.get(r), r.getClass(), ((IResource) r).getName(), null);
				}
			}
			else if(r instanceof FdModel) {
				project.getFdModel().addDepenciesRepository(dependanciesId.get(r), FdModel.class, ((FdModel) r).getName(), null);
			}
			else if(r instanceof Dictionary) {
				project.getFdModel().addDepenciesRepository(dependanciesId.get(r), Dictionary.class, ((Dictionary) r).getName(), null);
			}

		}

		/*
		 * checkin Main Model
		 */
		try {
			sock.getVersioningService().checkIn(sock.getRepositoryService().getDirectoryItem(directoryItemId), "", new ByteArrayInputStream(project.getFdModelAsXML().getBytes("UTF-8")));
		} catch(Exception ex) {
			ex.printStackTrace();
		}

		project.getFdModel().cleanDependancies();
		if(project instanceof MultiPageFdProject) {
			for(FdModel m : ((MultiPageFdProject) project).getPagesModels()) {
				m.cleanDependancies();
			}

		}

		return true;

	}
}
