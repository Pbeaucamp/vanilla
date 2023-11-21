package bpm.vanilla.workplace.server.runtime;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

import bpm.vanilla.workplace.core.IPackage;
import bpm.vanilla.workplace.core.IProject;
import bpm.vanilla.workplace.core.IUser;
import bpm.vanilla.workplace.core.model.PlaceImportDirectory;
import bpm.vanilla.workplace.core.model.PlaceImportItem;
import bpm.vanilla.workplace.core.model.VanillaPackage;
import bpm.vanilla.workplace.server.config.PlaceConfiguration;
import bpm.vanilla.workplace.server.helper.TransformObject;
import bpm.vanilla.workplace.server.servlets.ConnectionServlet;
import bpm.vanilla.workplace.shared.exceptions.ServiceException;
import bpm.vanilla.workplace.shared.model.PlaceWebLog;
import bpm.vanilla.workplace.shared.model.PlaceWebPackage;
import bpm.vanilla.workplace.shared.model.PlaceWebProject;
import bpm.vanilla.workplace.shared.model.PlaceWebUser;
import bpm.vanilla.workplace.shared.model.PlaceWebUserPackage;
import bpm.vanilla.workplace.shared.model.PlaceWebLog.LogType;

import com.thoughtworks.xstream.XStream;

public class PlaceProjectRuntime {

	private PlaceConfiguration config;
	private Logger logger = Logger.getLogger(ConnectionServlet.class);

	public PlaceProjectRuntime() throws ServiceException {
		config = PlaceConfiguration.getInstance();
	}

	public void createProject(int userId, PlaceWebProject project) {
		config.getProjectDao().save(userId, project, config);
	}

	public List<PlaceWebUser> getUsersForPackage(int packageId) {
		return config.getUserDao().getUsersForPackage(packageId);
	}

	public void deletePackage(int userId, int projectId, int packageId) {
		config.getPackageDao().deletePackage(packageId);

		PlaceWebLog log = new PlaceWebLog(LogType.DELETE_PACKAGE, userId, new Date(), projectId, packageId);
		config.getLogDao().save(log);
	}

	public void deleteProject(int userId, int projectId) {
		List<PlaceWebPackage> packages = config.getPackageDao().getPackagesByProjectId(projectId);
		if (packages != null) {
			for (PlaceWebPackage pack : packages) {
				config.getPackageDao().deletePackage(pack.getId());
			}
		}

		PlaceWebProject proj = new PlaceWebProject();
		proj.setId(projectId);
		config.getProjectDao().delete(proj);

		PlaceWebLog log = new PlaceWebLog(LogType.DELETE_PROJECT, userId, new Date(), projectId, null);
		config.getLogDao().save(log);
	}

	public List<IProject> createPackageWithProject(String folderPath, int userId, IProject project, IPackage vanillaPackage, InputStream stream) throws ServiceException {

		Integer projectId = project.getId();
		if (projectId == null) {
			PlaceWebProject projectToSave = TransformObject.transformToWebProject(project);
			projectId = config.getProjectDao().save(userId, projectToSave, config);
		}

		PlaceWebPackage packageToSave = TransformObject.transformToWebPackage(vanillaPackage);
		return createPackage(folderPath, userId, projectId, packageToSave, stream);
	}

	public List<IProject> createPackage(String folderPath, int userId, int projectId, PlaceWebPackage packageToSave, InputStream stream) throws ServiceException {
		String path = "";
		try {
			path = writeZipFile(folderPath, packageToSave.getName(), stream);
		} catch (IOException e) {
			e.printStackTrace();
			throw new ServiceException("Error during writing zip file" + e.getMessage());
		}

		packageToSave.setProjectId(projectId);
		packageToSave.setPath(path);

		config.getPackageDao().save(userId, packageToSave, config);

		try {
			return getProjectForUser(folderPath, userId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Error during getting projects" + e.getMessage());
		}
	}

	public List<IProject> getProjectForUser(String path, int userId) throws Exception {
		List<PlaceWebProject> projects = config.getProjectDao().getAllProjects();

		List<IProject> projectsToSend = new ArrayList<IProject>();
		for (PlaceWebProject project : projects) {
			List<PlaceWebPackage> packages = config.getPackageDao().getPackagesByUserAndProjectId(userId, project.getId());

			if (!packages.isEmpty()) {
				List<IPackage> packagesTmp = new ArrayList<IPackage>();

				for (PlaceWebPackage pack : packages) {
					PlaceWebUser creatorTmp = config.getUserDao().findByPrimaryKey(pack.getCreatorId());
					IUser creator = TransformObject.transformToCoreUser(creatorTmp);

					IPackage packTmp = TransformObject.transformToCorePackage(pack, creator);
					readDescriptor(path, packTmp);
					
					if(packTmp.getPackage() != null) {
						packagesTmp.add(packTmp);
					}
				}

				PlaceWebUser creatorTmp = config.getUserDao().findByPrimaryKey(project.getCreatorId());
				IUser creator = TransformObject.transformToCoreUser(creatorTmp);

				IProject projTmp = TransformObject.transformToCoreProject(project, creator);
				projTmp.setPackages(packagesTmp);

				if(projTmp.getPackages() != null && !projTmp.getPackages().isEmpty()) {
					projectsToSend.add(projTmp);
				}
			}
		}

		return projectsToSend;
	}

	public List<PlaceWebPackage> getPackageForUser(int userId) {
		return config.getPackageDao().getPackagesByUserId(userId);
	}

	public List<PlaceWebPackage> getAllPackages() {
		return config.getPackageDao().getAllPackages();
	}

	public List<PlaceWebProject> getAllProjects() {
		List<PlaceWebProject> projects = config.getProjectDao().getAllProjects();

		for (PlaceWebProject project : projects) {
			List<PlaceWebPackage> packages = config.getPackageDao().getPackagesByProjectId(project.getId());
			project.setPackages(packages);
		}
		return projects;
	}

	public String writeZipFile(String url, String name, InputStream input) throws IOException {
		String fileName = name + "_" + new Object().hashCode() + "." + PlaceConfiguration.PACKAGE_TYPE;

		File ressDir = new File(url + PlaceConfiguration.PACKAGE_FOLDER);
		ressDir.mkdir();

		String path = url + PlaceConfiguration.PACKAGE_FOLDER + File.separator + fileName;

		FileOutputStream outputStream = new FileOutputStream(path);
		byte[] buffer = new byte[2048];
		int n;
		while ((n = input.read(buffer, 0, 2048)) > -1) {
			outputStream.write(buffer, 0, n);
		}

		outputStream.close();
		input.close();

		return PlaceConfiguration.PACKAGE_FOLDER + File.separator + fileName;
	}

	private void readDescriptor(String path, IPackage pack) throws Exception {
		if (pack.getPath().toLowerCase().contains(PlaceConfiguration.PACKAGE_TYPE)) {
			logger.info("Unzipping file at path = " + path + pack.getPath());
			
			File fileP = new File(path + pack.getPath());
			try {
				VanillaPackage vanillaPackage = getVanillaPackage(fileP);
				pack.setPackage(vanillaPackage);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
//			ZipFile zipFile = new ZipFile(path + pack.getPath());
//
//			ZipEntry zipEntry = zipFile.getEntry("descriptor");
//			if (zipEntry != null) {
//				InputStream is = zipFile.getInputStream(zipEntry);
//				try {
//					ListOfImportItem ex = new ExportDescriptorDigester(is).getModel();
//
//					PlaceImportDirectory rootDir = new PlaceImportDirectory("root_dir");
//					buildTreeImportItems(rootDir, ex.getImportItems());
//
//					pack.setRootDirectory(rootDir);
//					pack.setImportItems(ex.getImportItems());
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
		}
		else {
			logger.info("The package " + pack.getPath() + " is an old package and is not supported anymore (BPMPackage).");
		}
	}
	
	public static VanillaPackage getVanillaPackage(File pack) throws ZipException, IOException {
		ZipFile zipFile = new ZipFile(pack);
		ZipEntry descriptorEntry = zipFile.getEntry("descriptor"); //$NON-NLS-1$
		if (descriptorEntry != null) {
			InputStream is = zipFile.getInputStream(descriptorEntry);

			XStream xstream = new XStream();
			VanillaPackage vanillaPackage = (VanillaPackage) xstream.fromXML(is);
			return vanillaPackage;
		}

		return null;
	}

	private void buildTreeImportItems(PlaceImportDirectory rootDir, List<PlaceImportItem> items) {
		for (PlaceImportItem item : items) {
			String path = item.getPath();
			String[] dirs = path.split("/");

			PlaceImportDirectory currentDir = null;
			String currentPath = "";
			for (int i = 0; i < dirs.length; i++) {
				if (i == 0) {
					currentPath += dirs[i];
				}
				else {
					currentPath += "/" + dirs[i];
				}

				PlaceImportDirectory dirTmp = new PlaceImportDirectory(dirs[i]);
				dirTmp.setPath(currentPath);

				PlaceImportDirectory foundCurDir = foundCurDirIfExist(dirTmp, rootDir);
				if (currentDir == null && foundCurDir == null) {
					rootDir.addChildDir(dirTmp);
					currentDir = dirTmp;
				}
				else if (foundCurDir == null) {
					currentDir.addChildDir(dirTmp);
					currentDir = dirTmp;
				}
				else {
					currentDir = foundCurDir;
				}

				if (i == dirs.length - 1) {
					currentDir.addChildsItem(item);
				}
			}
		}
	}

	private PlaceImportDirectory foundCurDirIfExist(PlaceImportDirectory dirToCheck, PlaceImportDirectory dirReference) {
		if (dirReference.getChildsDir() != null) {
			for (PlaceImportDirectory dirChild : dirReference.getChildsDir()) {
				if (dirChild.getPath().equals(dirToCheck.getPath())) {
					return dirChild;
				}
				else {
					PlaceImportDirectory dir = foundCurDirIfExist(dirToCheck, dirChild);
					if (dir != null) {
						return dir;
					}
				}
			}
		}
		return null;
	}

	public byte[] getPackageStream(String path, IPackage packageToImport, int userId) throws Exception {
		InputStream zipFile = new FileInputStream(path + packageToImport.getPath());

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		int sz = 0;
		byte[] buf = new byte[1024];
		while ((sz = zipFile.read(buf)) >= 0) {
			bos.write(buf, 0, sz);
		}
		zipFile.close();

		byte[] streamDatas = Base64.encodeBase64(bos.toByteArray());

		PlaceWebLog log = new PlaceWebLog(LogType.IMPORT_PACKAGE, userId, new Date(), packageToImport.getProjectId(), packageToImport.getId());
		config.getLogDao().save(log);

		return streamDatas;
	}

	public void allowUserForPackage(int packageId, List<PlaceWebUser> users) {
		for (PlaceWebUser user : users) {
			PlaceWebUserPackage userPack = new PlaceWebUserPackage();
			userPack.setPackageId(packageId);
			userPack.setUserId(user.getId());
			config.getUserPackageDao().save(userPack);
		}
	}

	public void deleteLinkUserPackage(int userId, int packageId) {
		// logger.info("Deleting package " + packageId + " for user " + userId);
		config.getUserPackageDao().deletePackageForUser(userId, packageId);
	}
}
