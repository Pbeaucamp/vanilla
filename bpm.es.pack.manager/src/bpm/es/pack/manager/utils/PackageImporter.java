package bpm.es.pack.manager.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.dom4j.DocumentException;
import org.eclipse.core.runtime.IProgressMonitor;

import adminbirep.Activator;
import bpm.es.pack.manager.I18N.Messages;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.RoleGroup;
import bpm.vanilla.platform.core.beans.ged.DocumentVersion;
import bpm.vanilla.platform.core.beans.ged.GedDocument;
import bpm.vanilla.platform.core.components.ReportHistoricComponent;
import bpm.vanilla.platform.core.components.historic.HistoricRuntimeConfiguration;
import bpm.vanilla.platform.core.components.historic.HistorizationConfig.HistorizationTarget;
import bpm.vanilla.platform.core.impl.ObjectIdentifier;
import bpm.vanilla.platform.core.remote.impl.components.RemoteHistoricReportComponent;
import bpm.vanilla.platform.core.repository.Repository;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.workplace.api.datasource.replacement.BIGDatasourceReplacement;
import bpm.vanilla.workplace.api.datasource.replacement.BIRTDatasourceReplacement;
import bpm.vanilla.workplace.api.datasource.replacement.BIWDatasourceReplacement;
import bpm.vanilla.workplace.api.datasource.replacement.FASDDatasourceReplacement;
import bpm.vanilla.workplace.api.datasource.replacement.FAVDatasourceReplacement;
import bpm.vanilla.workplace.api.datasource.replacement.FDDatasourceReplacement;
import bpm.vanilla.workplace.api.datasource.replacement.FDDicoDatasourceReplacement;
import bpm.vanilla.workplace.api.datasource.replacement.FMDTDatasourceReplacement;
import bpm.vanilla.workplace.api.datasource.replacement.FWRDatasourceReplacement;
import bpm.vanilla.workplace.core.datasource.IDatasource;
import bpm.vanilla.workplace.core.datasource.IDatasourceReplacement;
import bpm.vanilla.workplace.core.datasource.ModelDatasourceFD;
import bpm.vanilla.workplace.core.datasource.ModelDatasourceRepository;
import bpm.vanilla.workplace.core.model.Dependency;
import bpm.vanilla.workplace.core.model.PlaceImportDirectory;
import bpm.vanilla.workplace.core.model.PlaceImportItem;
import bpm.vanilla.workplace.core.model.VanillaPackage;

import com.thoughtworks.xstream.XStream;

public class PackageImporter {

	private IVanillaAPI vanillaApi;
	private IRepositoryApi repositoryApi;

	private VanillaPackage vanillaPackage;
	private boolean replaceOld;
	private String packageFolder;

	private StringBuffer logs = new StringBuffer();

	private HashMap<Integer, Group> groupsMap = new HashMap<Integer, Group>();
	private HashMap<Integer, PlaceImportItem> itemsMap = new HashMap<Integer, PlaceImportItem>();

	private ReportHistoricComponent historicComponent;

	public PackageImporter(IRepositoryApi repositoryApi, IVanillaAPI vanillaApi, VanillaPackage vanillaPackage, boolean replaceOld, String packageFolder) {
		this.repositoryApi = repositoryApi;
		this.vanillaApi = vanillaApi;
		this.vanillaPackage = vanillaPackage;
		this.replaceOld = replaceOld;
		this.packageFolder = packageFolder;

		historicComponent = new RemoteHistoricReportComponent(repositoryApi.getContext().getVanillaContext());
	}

	public static VanillaPackage getVanillaPackage(File pack) throws ZipException, IOException {
		ZipFile zipFile = new ZipFile(pack);
		try {
			ZipEntry descriptorEntry = zipFile.getEntry("descriptor"); //$NON-NLS-1$
			if (descriptorEntry != null) {
				InputStream is = zipFile.getInputStream(descriptorEntry);

				XStream xstream = new XStream();
				VanillaPackage vanillaPackage = (VanillaPackage) xstream.fromXML(is);
				return vanillaPackage;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (zipFile != null) {
				zipFile.close();
			}
		}

		return null;
	}

	public String importPackage(RepositoryDirectory targetDir, IProgressMonitor monitor) throws Exception {
		logs = new StringBuffer();

		monitor.worked(2);

		// Create Groups
		if (vanillaPackage.isIncludeGroups()) {
			for (Group g : vanillaPackage.getGroups()) {
				int oldId = g.getId();
				g.setId(0);

				try {
					Group existingGroup = vanillaApi.getVanillaSecurityManager().getGroupByName(g.getName());
					if (existingGroup != null) {
						g.setId(existingGroup.getId());
					}
					else {
						int id = vanillaApi.getVanillaSecurityManager().addGroup(g);
						g.setId(id);
					}
					groupsMap.put(oldId, g);
					if (oldId != g.getId())
						logs.append(Messages.PackageImporter_1 + g.getName() + "\r\n");//$NON-NLS-1$
				} catch (Exception e) {
					e.printStackTrace();
					logs.append(Messages.PackageImporter_3 + g.getName() + "\r\n");//$NON-NLS-1$
				}

			}
		}

		monitor.worked(2);

		// Create Roles
		if (vanillaPackage.isIncludeRoles()) {
			for (Group oldGroup : vanillaPackage.getGroupsRole().keySet()) {
				for (Integer roleId : vanillaPackage.getGroupsRole().get(oldGroup)) {
					if (groupsMap.get(oldGroup.getId()) == null) {
						continue;
					}

					RoleGroup rg = new RoleGroup();
					rg.setGroupId(groupsMap.get(oldGroup.getId()).getId());
					rg.setRoleId(roleId);
					vanillaApi.getVanillaSecurityManager().addRoleGroup(rg);
				}
			}
		}

		monitor.worked(2);

		// Create Items
		createItems(targetDir, monitor);

		monitor.worked(2);

		// Import report's historics
		// for (ReportBean r : infos.getReportsBeans()) {
		// for (ImportItem ii : newItems.keySet()) {
		// if (r.getDirectoryItemId() == ii.getId()) {
		// int newItId = newItems.get(ii);
		// RepositoryItem it = new RepositoryItem();
		//
		// it.setRepositoryModelTypeConstant(IRepositoryApi.FWR_TYPE);
		// it.setItemName(r.getItemName());
		// it.setDirectoryItemid(newItId);
		// for (String s : r.getFiles()) {
		// try {
		// // sock.sendReportFileFor(r.getItemName(), it, "",
		// // map.get(s), "Default");
		// logs.append("SUCCEED : pushing report from file " + map.get(s) +
		// "\r\n");
		// } catch (Exception e) {
		// e.printStackTrace();
		// logs.append("ERROR : pushing report from file " + map.get(s) +
		// "\r\n");
		// }
		//
		// }
		// }
		// }
		// }

		monitor.worked(2);

		return logs.toString();
	}

	private void createItems(RepositoryDirectory targetDir, IProgressMonitor monitor) throws Exception {

		Repository rep = new Repository(repositoryApi);

		// We create the tree of folders
		if (vanillaPackage.getDirectories() != null) {
			for (PlaceImportDirectory d : vanillaPackage.getDirectories()) {

				List<RepositoryDirectory> dirs = null;
				if (targetDir != null) {
					dirs = rep.getChildDirectories(targetDir);
				}
				else {
					dirs = rep.getRootDirectories();
				}

				boolean found = false;
				if (dirs != null) {
					for (RepositoryDirectory tmpDir : dirs) {
						if (tmpDir.getName().equals(d.getRepositoryDirectory().getName())) {
							d.setRepositoryDirectory(tmpDir);
							found = true;
							break;
						}
					}
				}

				try {
					if (!found) {
						RepositoryDirectory newDir = repositoryApi.getRepositoryService().addDirectory(d.getName(), d.getRepositoryDirectory().getComment(), targetDir);
						d.setRepositoryDirectory(newDir);
						logs.append(Messages.PackageImporter_5 + d.getRepositoryDirectory().getName() + "\r\n");//$NON-NLS-1$

						for (Integer oldGroupId : d.getAvailableGroupsId()) {
							if (groupsMap.get(oldGroupId) != null) {
								Integer newGroupId = groupsMap.get(oldGroupId).getId();
								repositoryApi.getAdminService().addGroupForDirectory(newGroupId, newDir.getId());
							}
						}
					}
					else {
						logs.append(Messages.PackageImporter_7 + d.getRepositoryDirectory().getName() + Messages.PackageImporter_8);
					}
				} catch (Exception e) {
					e.printStackTrace();
					logs.append(Messages.PackageImporter_9 + d.getRepositoryDirectory().getName() + "\r\n");//$NON-NLS-1$
				}

				createDirectories(rep, d);
			}
		}

		monitor.worked(2);

		// We start to create the items
		for (PlaceImportItem item : vanillaPackage.getItems()) {
			if (itemsMap.get(item.getItem().getId()) != null) {
				continue;
			}

			manageItem(item, targetDir);
		}

		monitor.worked(2);

		for (PlaceImportDirectory d : vanillaPackage.getDirectories()) {
			manageItems(d);
		}
	}

	private void manageItems(PlaceImportDirectory d) {
		if (d.getChildsItems() != null) {
			for (PlaceImportItem item : d.getChildsItems()) {
				if (itemsMap.get(item.getItem().getId()) != null) {
					continue;
				}

				manageItem(item, d.getRepositoryDirectory());
			}
		}

		if (d.getChildsDir() != null) {
			for (PlaceImportDirectory dir : d.getChildsDir()) {
				manageItems(dir);
			}
		}
	}

	private int manageItem(PlaceImportItem item, RepositoryDirectory targetDir) {
		boolean errorInDependency = false;
		for (Dependency dependency : item.getDependencies()) {

			if (itemsMap.get(dependency.getOldId()) == null) {
				Integer newId = findDependencyAndCreateIt(targetDir, dependency.getOldId());
				if (newId == -1) {
					errorInDependency = true;
					logs.append(Messages.PackageImporter_11 + item.getItem().getItemName() + "\r\n");//$NON-NLS-1$
				}
				else {
					dependency.setNewId(newId);
					logs.append(Messages.PackageImporter_13 + item.getItem().getItemName() + Messages.PackageImporter_14 + newId + "\r\n");//$NON-NLS-1$
				}
			}
			else {
				dependency.setNewId(itemsMap.get(dependency.getOldId()).getNewId());
				logs.append(Messages.PackageImporter_16 + item.getItem().getItemName() + "\r\n");//$NON-NLS-1$
			}
		}

		if (errorInDependency) {
			logs.append(Messages.PackageImporter_18 + item.getItem().getItemName() + Messages.PackageImporter_19 + "\r\n");//$NON-NLS-1$
			return -1;
		}
		else {
			Integer newId = insertItem(item, targetDir, vanillaPackage.isIncludeGrants());
			logs.append(Messages.PackageImporter_21 + item.getItem().getItemName() + Messages.PackageImporter_22 + newId + "\r\n");//$NON-NLS-1$
			return newId;
		}
	}

	private Integer findDependencyAndCreateIt(RepositoryDirectory targetDir, Integer oldId) {
		if (vanillaPackage.getItems() != null) {
			for (PlaceImportItem it : vanillaPackage.getItems()) {
				if (it.getItem().getId() == oldId) {
					return manageItem(it, targetDir);
				}
			}
		}

		if (vanillaPackage.getDirectories() != null) {
			for (PlaceImportDirectory d : vanillaPackage.getDirectories()) {
				Integer itemId = findDependencyAndCreateIt(d, oldId);
				if (itemId != null && itemId != -1) {
					return itemId;
				}
			}
		}

		return -1;
	}

	private Integer findDependencyAndCreateIt(PlaceImportDirectory dir, Integer oldId) {
		if (dir.getChildsItems() != null) {
			for (PlaceImportItem it : dir.getChildsItems()) {
				if (it.getItem().getId() == oldId) {
					return manageItem(it, dir.getRepositoryDirectory());
				}
			}
		}

		if (dir.getChildsDir() != null) {
			for (PlaceImportDirectory d : dir.getChildsDir()) {
				Integer itemId = findDependencyAndCreateIt(d, oldId);
				if (itemId != null && itemId != -1) {
					return itemId;
				}
			}
		}

		return -1;
	}

	private void createDirectories(Repository rep, PlaceImportDirectory dir) throws Exception {
		// Create directories recursively
		if (dir.getChildsDir() != null) {
			for (PlaceImportDirectory d : dir.getChildsDir()) {

				List<RepositoryDirectory> dirs = rep.getChildDirectories(dir.getRepositoryDirectory());
				boolean found = false;
				if (dirs != null) {
					for (RepositoryDirectory tmpDir : dirs) {
						if (tmpDir.getName().equals(d.getRepositoryDirectory().getName())) {
							d.setRepositoryDirectory(tmpDir);
							found = true;
							break;
						}
					}
				}

				try {
					if (!found) {
						RepositoryDirectory newDir = repositoryApi.getRepositoryService().addDirectory(d.getName(), d.getRepositoryDirectory().getComment(), dir.getRepositoryDirectory());
						d.setRepositoryDirectory(newDir);
						logs.append(Messages.PackageImporter_24 + d.getRepositoryDirectory().getName() + "\r\n");//$NON-NLS-1$

						for (Integer oldGroupId : d.getAvailableGroupsId()) {
							if (groupsMap.get(oldGroupId) != null) {
								Integer newGroupId = groupsMap.get(oldGroupId).getId();
								repositoryApi.getAdminService().addGroupForDirectory(newGroupId, newDir.getId());
							}
						}
					}
					else {
						logs.append(Messages.PackageImporter_26 + d.getRepositoryDirectory().getName() + Messages.PackageImporter_27);
					}
				} catch (Exception e) {
					e.printStackTrace();
					logs.append(Messages.PackageImporter_28 + d.getRepositoryDirectory().getName() + "\r\n");//$NON-NLS-1$
				}

				createDirectories(rep, d);
			}
		}
	}

	private Integer insertItem(PlaceImportItem item, RepositoryDirectory targetDir, boolean includeGrants) {
		try {
			// we modify the xml for the dependancies
			String xml = modifyXml(repositoryApi, item);

			int id = -1;

			boolean isReplace = false;
			if (replaceOld) {
				RepositoryItem itemToUpdate = null;
				if (targetDir != null) {
					Repository rep = new Repository(repositoryApi);
					List<RepositoryItem> items = rep.getItems(targetDir);
					if (items != null) {
						for (RepositoryItem tmpItem : items) {
							if (tmpItem.getItemName().equals(item.getItem().getItemName())) {
								itemToUpdate = tmpItem;
								break;
							}
						}
					}
				}

				if (itemToUpdate != null) {
					isReplace = true;

					if (itemToUpdate.getType() == IRepositoryApi.EXTERNAL_DOCUMENT) {
						InputStream is = writeExternalFile(String.valueOf(item.getItem().getId()));

						repositoryApi.getDocumentationService().updateExternalDocument(itemToUpdate, is);
					}
					else {
						// We update the model of the already existing item
						repositoryApi.getRepositoryService().updateModel(itemToUpdate, xml);
					}

					id = itemToUpdate.getId();

					// We set the old ID to keep it for dependencies
					itemToUpdate.setId(item.getItem().getId());

					item.setItem(itemToUpdate);
				}
			}

			if (!isReplace) {
				if (item.getItem().getType() == IRepositoryApi.CUST_TYPE) {
					id = repositoryApi.getRepositoryService().addDirectoryItemWithDisplay(item.getItem().getType(), item.getItem().getSubtype(), targetDir, item.getItem().getItemName(), item.getItem().getComment(), item.getItem().getInternalVersion(), item.getItem().getPublicVersion(), xml, true).getId();
				}
				else if (item.getItem().getType() == IRepositoryApi.EXTERNAL_DOCUMENT) {
					
					File folder = new File(packageFolder);

					InputStream is = null;
					
					if (folder.exists()) {
						ZipFile zipFile = new ZipFile(folder.getAbsolutePath() + File.separator + vanillaPackage.getName() + PackageCreator.VANILLA_PACKAGE_EXTENSION);
						try {
							ZipEntry descriptorEntry = zipFile.getEntry(String.valueOf(item.getItem().getId()));
							if (descriptorEntry != null) {
								logs.append(Messages.PackageImporter_42 + String.valueOf(item.getItem().getId()) + "\r\n");//$NON-NLS-1$
								is = zipFile.getInputStream(descriptorEntry);
								
								if (is != null) {
									String format = ""; //$NON-NLS-1$
									try {
										format = item.getItem().getItemName().lastIndexOf("." + 1) != -1 ? item.getItem().getItemName().substring(item.getItem().getItemName().lastIndexOf("." + 1)) : ""; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
									} catch (Exception e) {
										format = ""; //$NON-NLS-1$
									}

									id = repositoryApi.getRepositoryService().addExternalDocumentWithDisplay(targetDir, item.getItem().getItemName(), item.getItem().getComment(), item.getItem().getInternalVersion(), item.getItem().getPublicVersion(), is, false, format).getId();

									is.close();
								}
								
							}
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							if (zipFile != null) {
								zipFile.close();
							}
						}
					}

//					logs.append(Messages.PackageImporter_44 + String.valueOf(item.getItem().getId()) + " \r\n");//$NON-NLS-1$
					
//					InputStream is = writeExternalFile(String.valueOf(item.getItem().getId()));


				}
				else if (item.getItem().getType() == IRepositoryApi.FAV_TYPE) {

					id = repositoryApi.getRepositoryService().addDirectoryItemWithDisplay(IRepositoryApi.FAV_TYPE, -1, targetDir, item.getItem().getItemName(), item.getItem().getComment(), item.getItem().getInternalVersion(), item.getItem().getPublicVersion(), xml, true).getId();
				}
				else {
					if (item.getItem().getType() == IRepositoryApi.FD_TYPE) {
						if (item.isMainModel()) {
							id = repositoryApi.getRepositoryService().addDirectoryItemWithDisplay(item.getItem().getType(), -1, targetDir, item.getItem().getItemName(), item.getItem().getComment(), item.getItem().getInternalVersion(), item.getItem().getPublicVersion(), xml, true).getId();
						}
						else {
							id = repositoryApi.getRepositoryService().addDirectoryItemWithDisplay(item.getItem().getType(), -1, targetDir, item.getItem().getItemName(), item.getItem().getComment(), item.getItem().getInternalVersion(), item.getItem().getPublicVersion(), xml, false).getId();
						}
					}
					else {
						id = repositoryApi.getRepositoryService().addDirectoryItemWithDisplay(item.getItem().getType(), -1, targetDir, item.getItem().getItemName(), item.getItem().getComment(), item.getItem().getInternalVersion(), item.getItem().getPublicVersion(), xml, true).getId();
					}
				}
			}

			// if(includeGrants){
			if (item.getAvailableGroupsId() != null) {
				for (Integer groupId : item.getAvailableGroupsId()) {
					if (groupsMap.get(groupId) != null) {
						Integer newGroupId = groupsMap.get(groupId).getId();
						repositoryApi.getAdminService().addGroupForItem(newGroupId, id);
					}
				}
			}

			if (item.getRunnableGroupsId() != null) {
				for (Integer groupId : item.getRunnableGroupsId()) {
					if (groupsMap.get(groupId) != null) {
						Group gr = new Group();
						gr.setId(groupsMap.get(groupId).getId());

						RepositoryItem it = new RepositoryItem();
						it.setId(id);
						repositoryApi.getAdminService().setObjectRunnableForGroup(gr.getId(), it);
					}
				}
			}
			// }

			if (id != -1) {

				item.setNewId(id);
				if (item.getItem().getType() == IRepositoryApi.CUST_TYPE || item.getItem().getType() == IRepositoryApi.FWR_TYPE) {
					if (item.getHistoric() != null && !item.getHistoric().isEmpty()) {
						for (GedDocument doc : item.getHistoric()) {

							Integer docId = null;

							for (DocumentVersion v : doc.getDocumentVersions()) {
								HistoricRuntimeConfiguration conf = new HistoricRuntimeConfiguration(new ObjectIdentifier(repositoryApi.getContext().getRepository().getId(), id), item.getAvailableGroupsId().get(0), HistorizationTarget.Group, item.getAvailableGroupsId(), doc.getName(), v.getFormat(), doc.getCreatedBy(), docId);

								InputStream is = null;
				
								File folder = new File(packageFolder);

								if (folder.exists()) {
									ZipFile zipFile = new ZipFile(folder.getAbsolutePath() + File.separator + vanillaPackage.getName() + PackageCreator.VANILLA_PACKAGE_EXTENSION);
									try {
										ZipEntry descriptorEntry = zipFile.getEntry(v.getDocumentPath());
										if (descriptorEntry != null) {
											logs.append(Messages.PackageImporter_42 + v.getDocumentPath() + "\r\n");//$NON-NLS-1$
											is = zipFile.getInputStream(descriptorEntry);
											
											Integer newId = historicComponent.historizeReport(conf, is);

											for (int i : item.getAvailableGroupsId()) {
												historicComponent.grantHistoricAccess(i, id, repositoryApi.getContext().getRepository().getId());
											}

											if (docId == null) {
												docId = newId;
											}
											
										}
									} catch (Exception e) {
										e.printStackTrace();
									} finally {
										if (zipFile != null) {
											zipFile.close();
										}
									}
								}
							}

						}
					}
				}

				itemsMap.put(item.getItem().getId(), item);
			}

			return id;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			logs.append(Messages.PackageImporter_35 + "\r\n");//$NON-NLS-1$
		} catch (IOException e) {
			e.printStackTrace();
			logs.append(Messages.PackageImporter_37 + "\r\n");//$NON-NLS-1$
		} catch (Exception e) {
			e.printStackTrace();
			logs.append(Messages.PackageImporter_39 + item.getItem().getItemName() + Messages.PackageImporter_40 + "\r\n");//$NON-NLS-1$
		}

		return -1;
	}

	private static String modifyXml(IRepositoryApi repositoryApi, PlaceImportItem item) throws DocumentException {

		IDatasourceReplacement remplacement = null;

		int type = item.getItem().getType();
		Integer subtype = item.getItem().getSubtype();
		switch (type) {
		case IRepositoryApi.FASD_TYPE:
			remplacement = new FASDDatasourceReplacement();
			break;
		case IRepositoryApi.FD_TYPE:
			remplacement = new FDDatasourceReplacement();
			break;
		case IRepositoryApi.FD_DICO_TYPE:
			remplacement = new FDDicoDatasourceReplacement();
			break;
		case IRepositoryApi.FMDT_TYPE:
			remplacement = new FMDTDatasourceReplacement();
			break;
		case IRepositoryApi.GTW_TYPE:
			remplacement = new BIGDatasourceReplacement();
			break;
		case IRepositoryApi.GED_TYPE:
			break;
		case IRepositoryApi.BIW_TYPE:
			remplacement = new BIWDatasourceReplacement();
			break;
		case IRepositoryApi.EXTERNAL_DOCUMENT:
			break;
		case IRepositoryApi.FAV_TYPE:
			remplacement = new FAVDatasourceReplacement();
			break;
		case IRepositoryApi.FWR_TYPE:
			remplacement = new FWRDatasourceReplacement();
			break;
		case IRepositoryApi.CUST_TYPE:
			if (subtype != null && subtype == IRepositoryApi.BIRT_REPORT_SUBTYPE) {
				remplacement = new BIRTDatasourceReplacement();
				break;
			}
		}

		if (remplacement != null) {
			String user = repositoryApi.getContext().getVanillaContext().getLogin();
			String password = repositoryApi.getContext().getVanillaContext().getPassword();
			int groupId = 1;
			String groupName = ""; //$NON-NLS-1$
			try {
				groupName = Activator.getDefault().getVanillaApi().getVanillaSecurityManager().getGroupById(groupId).getName();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
			String vanillaUrl = ""; //$NON-NLS-1$
			try {
				vanillaUrl = repositoryApi.getContext().getVanillaContext().getVanillaUrl();
			} catch (Exception e1) {
				e1.printStackTrace();
			}

			String repositoryUrl = Activator.getDefault().getCurrentRepository().getUrl();
			String repositoryId = String.valueOf(Activator.getDefault().getCurrentRepository().getId());

			for (IDatasource ds : item.getDatasources()) {
				if (ds instanceof ModelDatasourceRepository) {
					ModelDatasourceRepository dsRepo = (ModelDatasourceRepository) ds;
					if (dsRepo.getDirId() != null) {
						int itemId = Integer.parseInt(dsRepo.getDirId());
						if (item.getDependencies() != null) {
							for (Dependency dep : item.getDependencies()) {
								if (dep.getOldId() == itemId) {
									dsRepo.setDirId(String.valueOf(dep.getNewId()));
								}
							}
						}
					}

					dsRepo.setGroupId(String.valueOf(groupId));
					dsRepo.setGroupName(groupName);
					dsRepo.setUser(user);
					dsRepo.setPassword(password);
					dsRepo.setRepositoryId(repositoryId);
					dsRepo.setRepositoryUrl(repositoryUrl);
					dsRepo.setVanillaRuntimeUrl(vanillaUrl);

					try {
						String xml = item.getXml();
						item.setXml(remplacement.replaceElement(xml, ds));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				else if (ds instanceof ModelDatasourceFD) {
					ModelDatasourceFD dsFD = (ModelDatasourceFD) ds;

					if (dsFD.getDictionaryRepositoryItemId() != null) {
						for (Dependency dep : item.getDependencies()) {
							if (dep.getOldId() == Integer.parseInt(dsFD.getDictionaryRepositoryItemId())) {
								dsFD.setDictionaryRepositoryItemId(String.valueOf(dep.getNewId()));
							}
						}
					}

					HashMap<Integer, Integer> dependancies = new HashMap<Integer, Integer>();
					for (Integer key : dsFD.getDependancies().keySet()) {

						for (Dependency dep : item.getDependencies()) {
							if (dep.getOldId().equals(key)) {
								dsFD.setDictionaryRepositoryItemId(String.valueOf(dep.getNewId()));
								dependancies.put(key, dep.getNewId());
							}
						}
					}
					dsFD.setDependancies(dependancies);

					try {
						String xml = item.getXml();
						item.setXml(remplacement.replaceElement(xml, ds));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

		}

		return item.getXml();
	}

	private InputStream writeExternalFile(String fileName) throws Exception {
		File folder = new File(packageFolder);

		if (folder.exists()) {
			ZipFile zipFile = new ZipFile(folder.getAbsolutePath() + File.separator + vanillaPackage.getName() + PackageCreator.VANILLA_PACKAGE_EXTENSION);
			try {
				ZipEntry descriptorEntry = zipFile.getEntry(fileName);
				if (descriptorEntry != null) {
					logs.append(Messages.PackageImporter_42 + fileName + "\r\n");//$NON-NLS-1$
					return zipFile.getInputStream(descriptorEntry);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (zipFile != null) {
					zipFile.close();
				}
			}
		}

		logs.append(Messages.PackageImporter_44 + fileName + " \r\n");//$NON-NLS-1$

		return null;
	}
}
