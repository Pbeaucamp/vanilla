package bpm.es.pack.manager.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;

import bpm.vanilla.platform.core.IObjectIdentifier;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.RoleGroup;
import bpm.vanilla.platform.core.beans.ged.DocumentVersion;
import bpm.vanilla.platform.core.beans.ged.GedDocument;
import bpm.vanilla.platform.core.components.ReportHistoricComponent;
import bpm.vanilla.platform.core.impl.ObjectIdentifier;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.remote.impl.components.RemoteHistoricReportComponent;
import bpm.vanilla.platform.core.repository.Repository;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.platform.core.repository.SecuredDirectory;
import bpm.vanilla.platform.core.utils.IOWriter;
import bpm.vanilla.workplace.core.model.Dependency;
import bpm.vanilla.workplace.core.model.PlaceImportDirectory;
import bpm.vanilla.workplace.core.model.PlaceImportItem;
import bpm.vanilla.workplace.core.model.VanillaPackage;

import com.thoughtworks.xstream.XStream;

public class PackageCreator {

	public static final String VANILLA_PACKAGE_EXTENSION = ".vanillapackage"; //$NON-NLS-1$

	public static void getChilds(IRepositoryApi repositoryApi, VanillaPackage vanillaPackage, PlaceImportDirectory directoryParent, List<RepositoryDirectory> currentDirectories, Repository repositoryContent, List<PlaceImportDirectory> selectedDirectories, List<PlaceImportItem> selectedItems, boolean isFullExport, HashMap<Integer, Object> mapDependencies) throws Exception {

		for (RepositoryDirectory dir : currentDirectories) {

			PlaceImportDirectory newParent = null;
			if (isFullExport) {
				PlaceImportDirectory p = null;
				if (directoryParent == null) {
					p = new PlaceImportDirectory(dir);
					vanillaPackage.addDirectory(p);
				}
				else {
					p = new PlaceImportDirectory(dir);
					directoryParent.addChildDir(p);
				}
				newParent = p;
			}
			else if (selectedDirectories != null) {
				for (PlaceImportDirectory tempDir : selectedDirectories) {
					if (tempDir.getRepositoryDirectory().getId() == dir.getId()) {
						PlaceImportDirectory p = null;
						if (directoryParent == null) {
							p = new PlaceImportDirectory(dir);
							vanillaPackage.addDirectory(p);
						}
						else {
							p = new PlaceImportDirectory(dir);
							directoryParent.addChildDir(p);
						}
						newParent = p;
						break;
					}
				}
			}

			for (RepositoryItem item : repositoryContent.getItems(dir)) {

				if (isFullExport) {

					PlaceImportItem pItem = new PlaceImportItem(item);
					findDependencies(repositoryApi, pItem, newParent, mapDependencies, vanillaPackage, isFullExport);
					if (newParent == null) {
						vanillaPackage.addItem(pItem);
						mapDependencies.put(item.getId(), vanillaPackage);
					}
					else {
						newParent.addChildsItem(pItem);
						mapDependencies.put(item.getId(), newParent);
					}

				}
				else if (selectedItems != null) {
					for (PlaceImportItem tempItem : selectedItems) {
						if (tempItem.getItem().getId() == item.getId()) {

							if (mapDependencies.get(tempItem.getItem().getId()) != null) {
								Object parent = mapDependencies.get(tempItem.getItem().getId());
								if (parent instanceof VanillaPackage) {
									VanillaPackage pack = (VanillaPackage) parent;
									PlaceImportItem toRm = null;
									for (PlaceImportItem it : pack.getItems()) {
										if (it.getItem().getId() == tempItem.getItem().getId()) {
											toRm = it;
											break;
										}
									}
									pack.getItems().remove(toRm);
								}
								else if (parent instanceof PlaceImportDirectory) {
									PlaceImportDirectory pack = (PlaceImportDirectory) parent;
									PlaceImportItem toRm = null;
									for (PlaceImportItem it : pack.getChildsItems()) {
										if (it.getItem().getId() == tempItem.getItem().getId()) {
											toRm = it;
											break;
										}
									}
									pack.getChildsItems().remove(toRm);
								}
							}

							findDependencies(repositoryApi, tempItem, newParent, mapDependencies, vanillaPackage, isFullExport);
							if (newParent == null) {
								vanillaPackage.addItem(tempItem);
								mapDependencies.put(item.getId(), vanillaPackage);
							}
							else {
								newParent.addChildsItem(tempItem);
								mapDependencies.put(item.getId(), newParent);
							}
							break;
						}
					}
				}
			}

			getChilds(repositoryApi, vanillaPackage, newParent, repositoryContent.getChildDirectories(dir), repositoryContent, selectedDirectories, selectedItems, isFullExport, mapDependencies);
		}
	}

	private static void findDependencies(IRepositoryApi repositoryApi, PlaceImportItem pItem, PlaceImportDirectory directoryParent, HashMap<Integer, Object> mapDependencies, VanillaPackage vanillaPackage, boolean isFullExport) throws Exception {

		List<RepositoryItem> dependencies = repositoryApi.getRepositoryService().getNeededItems(pItem.getItem().getId());
		if (dependencies != null) {
			for (RepositoryItem dep : dependencies) {

				Dependency finalDep = new Dependency(dep.getId());
				pItem.addDependency(finalDep);

				if (mapDependencies.get(dep.getId()) == null) {
					PlaceImportItem depPItem = new PlaceImportItem(dep);
					findDependencies(repositoryApi, depPItem, directoryParent, mapDependencies, vanillaPackage, isFullExport);

					if (!isFullExport) {
						if (directoryParent != null) {
							directoryParent.addChildsItem(depPItem);
							mapDependencies.put(dep.getId(), directoryParent);
						}
						else {
							vanillaPackage.addItem(depPItem);
							mapDependencies.put(dep.getId(), vanillaPackage);
						}
					}

				}

			}
		}

	}

	// private static void manageDependencies(IRepositoryApi repositoryApi,
	// RepositoryItem item, VanillaPackage vanillaPackage, HashMap<Integer,
	// Object> mapDependencies) throws Exception {
	// List<RepositoryItem> dependencies = new ArrayList<RepositoryItem>();
	// findDepedencies(repositoryApi, item, dependencies);
	// if(dependencies != null) {
	// List<Dependency> packageDependencies = new ArrayList<Dependency>();
	// for(RepositoryItem dep : dependencies) {
	// Dependency packageDep = new Dependency(dep.getDirectoryItemid());
	// packageDependencies.add(packageDep);
	//
	// if(mapDependencies.get(dep.getDirectoryItemid()) == null) {
	// mapDependencies.put(dep.getDirectoryItemid(), vanillaPackage);
	// vanillaPackage.addItem(dep);
	// }
	// }
	// vanillaPackage.addDependencies(item.getDirectoryItemid(),
	// packageDependencies);
	// }
	// }
	//
	// private static void manageDependencies(VanillaPackage vanillaPackage,
	// IRepositoryApi repositoryApi, PlaceImportItem item, PlaceImportDirectory
	// directoryParent, HashMap<Integer, Object> mapDependencies) throws
	// Exception {
	// List<PlaceImportItem> dependencies = new ArrayList<PlaceImportItem>();
	// findDepedencies(repositoryApi, item, dependencies);
	// if(dependencies != null) {
	// List<Dependency> packageDependencies = new ArrayList<Dependency>();
	// for(PlaceImportItem dep : dependencies) {
	// Dependency packageDep = new
	// Dependency(dep.getItem().getDirectoryItemid());
	// packageDependencies.add(packageDep);
	//
	// item.addDependency(packageDep);
	//				
	// // if(mapDependencies.get(dep.getDirectoryItemid()) == null) {
	// // mapDependencies.put(dep.getDirectoryItemid(), directoryParent);
	// // directoryParent.addItem(dep);
	// // }
	// }
	// // vanillaPackage.addDependencies(item.getDirectoryItemid(),
	// packageDependencies);
	// }
	// }
	//
	// public static void findDepedencies(IRepositoryApi repositoryApi,
	// PlaceImportItem item, List<RepositoryItem> dependencies) throws Exception
	// {
	// List<RepositoryItem> deps =
	// repositoryApi.getRepositoryService().getNeededItems(item.getItem().getDirectoryItemid());
	// if(deps != null) {
	// for(RepositoryItem dep : deps) {
	// dependencies.add(dep);
	// findDepedencies(repositoryApi, dep, dependencies);
	// }
	// }
	// }

	public static void createPackage(VanillaPackage vanillaPackage, IRepositoryApi repositoryApi, String packageLocation) throws Exception {

		String packageName = vanillaPackage.getName();

		File folder = new File(packageLocation);
		if (!folder.exists()) {
			folder.mkdirs();
		}

		File zipFile = new File(packageLocation + File.separator + packageName + VANILLA_PACKAGE_EXTENSION);
		if (!zipFile.exists()) {
			zipFile.createNewFile();
		}

		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile));

		XStream xstream = new XStream();
		String modelXml = xstream.toXML(vanillaPackage);

		InputStream is = IOUtils.toInputStream(modelXml, "UTF-8"); //$NON-NLS-1$

		ZipEntry entry = new ZipEntry("descriptor"); //$NON-NLS-1$
		out.putNextEntry(entry);
		IOWriter.write(is, out, true, false);

		if (vanillaPackage.getItems() != null) {
			for (PlaceImportItem item : vanillaPackage.getItems()) {
				createZipEntry(out, repositoryApi, item.getItem());
			}
		}

		if (vanillaPackage.getDirectories() != null) {
			for (PlaceImportDirectory dir : vanillaPackage.getDirectories()) {
				buildFiles(out, repositoryApi, dir);
			}
		}

		if (vanillaPackage.isIncludeHistorics()) {
			findHistorics(vanillaPackage.getDirectories(), vanillaPackage.getItems(), out);
		}

		out.close();
	}

	private static void findHistorics(List<PlaceImportDirectory> directories, List<PlaceImportItem> items, ZipOutputStream out) throws Exception {
		for (PlaceImportItem i : items) {
			if (i.getHistoric() != null && !i.getHistoric().isEmpty()) {
				for (GedDocument doc : i.getHistoric()) {
					for (DocumentVersion v : doc.getDocumentVersions()) {
						FileInputStream iss = new FileInputStream("temp/" + v.getDocumentPath()); //$NON-NLS-1$
						ZipEntry e = new ZipEntry(v.getDocumentPath());
						out.putNextEntry(e);
						IOWriter.write(iss, out, true, false);
					}
				}
			}
		}

		for (PlaceImportDirectory directory : directories) {
			findHistorics(directory.getChildsDir(), directory.getChildsItems(), out);
		}
	}

	private static void buildFiles(ZipOutputStream out, IRepositoryApi repositoryApi, PlaceImportDirectory dir) {
		if (dir.getChildsItems() != null) {
			for (PlaceImportItem item : dir.getChildsItems()) {
				try {
					createZipEntry(out, repositoryApi, item.getItem());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		if (dir.getChildsDir() != null) {
			for (PlaceImportDirectory newDir : dir.getChildsDir()) {
				buildFiles(out, repositoryApi, newDir);
			}
		}
	}

	private static void createZipEntry(ZipOutputStream out, IRepositoryApi repositoryApi, RepositoryItem item) throws Exception {
		RepositoryItem di = repositoryApi.getRepositoryService().getDirectoryItem(item.getId());
		if (item.getType() == IRepositoryApi.EXTERNAL_DOCUMENT) {
			
			InputStream is = repositoryApi.getDocumentationService().importExternalDocument(di);

			ZipEntry entry = new ZipEntry(String.valueOf(item.getId()));
			out.putNextEntry(entry);
			IOWriter.write(is, out, true, false);
		}
		else {
			String xml = repositoryApi.getRepositoryService().loadModel(di);

			InputStream is = IOUtils.toInputStream(xml, "UTF-8"); //$NON-NLS-1$

			ZipEntry entry = new ZipEntry(String.valueOf(item.getId()));
			out.putNextEntry(entry);
			IOWriter.write(is, out, true, false);
		}
	}

	public static void getHistorics(IRepositoryApi repositoryApi, VanillaPackage vanillaPackage, List<PlaceImportItem> selectedItems) throws Exception {
		ReportHistoricComponent histo = new RemoteHistoricReportComponent(repositoryApi.getContext().getVanillaContext());

		for (PlaceImportItem item : selectedItems) {
			if (item.getItem().getType() == IRepositoryApi.CUST_TYPE || item.getItem().getType() == IRepositoryApi.FWR_TYPE) {
				IObjectIdentifier identifier = new ObjectIdentifier(repositoryApi.getContext().getRepository().getId(), item.getItem().getId());
				List<GedDocument> doc = histo.getReportHistoric(identifier, -1);
				if (doc != null && !doc.isEmpty()) {
					for (GedDocument d : doc) {
						File f = new File("temp"); //$NON-NLS-1$
						if (!f.exists()) {
							f.mkdir();
						}

						for (DocumentVersion v : d.getDocumentVersions()) {
							InputStream is = histo.loadHistorizedDocument(v);

							v.setDocumentPath("histo_" + v.getId() + "." + v.getFormat()); //$NON-NLS-1$ //$NON-NLS-2$

							FileOutputStream out = new FileOutputStream(new File("temp/" + v.getDocumentPath())); //$NON-NLS-1$
							int read = 0;
							byte[] bytes = new byte[1024];

							while ((read = is.read(bytes)) != -1) {
								out.write(bytes, 0, read);
							}

							out.close();
							is.close();
						}
						item.addHistoric(d);
					}

				}
			}
		}
	}

	public static void getSecurity(IRepositoryApi repositoryApi, List<PlaceImportDirectory> selectedDirectories, List<PlaceImportItem> selectedItems, List<Group> grps) throws Exception {

		if (selectedDirectories != null && !selectedDirectories.isEmpty()) {

			for (PlaceImportDirectory imp : selectedDirectories) {
				for (Group group : grps) {
					List<SecuredDirectory> secDirs = repositoryApi.getAdminService().getSecuredDirectoriesForGroup(group);
					for (SecuredDirectory sec : secDirs) {
						if (imp.getRepositoryDirectory().getId() == sec.getDirectoryId().intValue() && group.getId().intValue() == sec.getGroupId().intValue()) {
							imp.addAvailableGroupId(sec.getGroupId());
						}
					}
				}
				getSecurity(repositoryApi, imp.getChildsDir(), imp.getChildsItems(), grps);
			}
		}

		for (PlaceImportItem imp : selectedItems) {
			List<Integer> ids = repositoryApi.getAdminService().getAllowedGroupId(imp.getItem());
			for (int i : ids) {
				imp.addAvailableGroupId(i);
			}
			for (Group g : grps) {
				if (repositoryApi.getAdminService().canRun(imp.getItem().getId(), g.getId().intValue())) {
					imp.addRunnableGroupId(g.getId().intValue());
				}
			}
		}
	}

	public static void getRoles(IRepositoryApi repositoryApi, List<Group> grps, VanillaPackage vanillaPackage) throws Exception {
		IVanillaAPI vanillaApi = new RemoteVanillaPlatform(repositoryApi.getContext().getVanillaContext());
		if (vanillaPackage.isIncludeRoles()) {
			for (Group group : grps) {
				for (RoleGroup role : vanillaApi.getVanillaSecurityManager().getRoleGroups(group)) {
					vanillaPackage.addGroupRole(group, role.getRoleId());
				}
			}
		}
	}
}
