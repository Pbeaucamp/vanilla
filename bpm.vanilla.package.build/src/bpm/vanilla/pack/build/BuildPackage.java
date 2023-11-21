package bpm.vanilla.pack.build;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.apache.commons.io.IOUtils;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.workplace.core.model.PlaceImportDirectory;
import bpm.vanilla.workplace.core.model.PlaceImportItem;
import bpm.vanilla.workplace.core.model.VanillaPackage;

import com.thoughtworks.xstream.XStream;

public class BuildPackage {

	// private static final String PACKAGE_PATH =
	// "C:/Users/Sébastien/Desktop/My_Samples.vanillapackage";
	// private static final String PATH_TO_DEPLOY =
	// "C:/Users/Sébastien/Desktop";

	public static void main(String[] args) throws Exception {
		String packagePath = "";
		try {
			packagePath = args[0];
		} catch (Exception e) {
			throw new Exception("First Argument should be the Path to the Package");
		}

		String pathToDeploy = "";
		try {
			pathToDeploy = args[1];
		} catch (Exception e) {
			throw new Exception("Second Argument should be the Path to deploy the package");
		}

		// String packagePath = PACKAGE_PATH;
		// String pathToDeploy = PATH_TO_DEPLOY;

		File fileP = new File(packagePath);
		try {
			VanillaPackage vanillaPackage = getVanillaPackage(fileP);
			createPackage(packagePath, vanillaPackage, pathToDeploy);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static VanillaPackage getVanillaPackage(File pack) throws ZipException, IOException {
		ZipFile zipFile = new ZipFile(pack);
		ZipEntry descriptorEntry = zipFile.getEntry("descriptor"); //$NON-NLS-1$
		if (descriptorEntry != null) {
			InputStream is = zipFile.getInputStream(descriptorEntry);

			XStream xstream = new XStream();
			VanillaPackage vanillaPackage = (VanillaPackage) xstream.fromXML(is);
			zipFile.close();
			return vanillaPackage;
		}

		zipFile.close();
		return null;
	}

	private static void createPackage(String packagePath, VanillaPackage vanillaPackage, String pathToDeploy) throws Exception {
		File mainFolder = new File(pathToDeploy + "/" + vanillaPackage.getName());
		mainFolder.mkdirs();
		if (mainFolder.isDirectory()) {

			if (vanillaPackage.getDirectories() != null) {
				for (PlaceImportDirectory d : vanillaPackage.getDirectories()) {
					File newDirectory = new File(mainFolder + "/" + d.getName());
					newDirectory.mkdir();
					createStructure(packagePath, newDirectory, d);
				}
			}

			if (vanillaPackage.getItems() != null) {
				for (PlaceImportItem item : vanillaPackage.getItems()) {
					File newFile = new File(mainFolder + "/" + item.getItem().getName() + findExtension(item));
					writeFile(packagePath, newFile, item);
				}
			}
		}
	}

	private static void createStructure(String packagePath, File parentDirectory, PlaceImportDirectory dir) throws Exception {
		if (dir.getChildsDir() != null) {
			for (PlaceImportDirectory childDir : dir.getChildsDir()) {
				File newDirectory = new File(parentDirectory + "/" + childDir.getName());
				newDirectory.mkdir();
				createStructure(packagePath, newDirectory, childDir);
			}
		}

		if (dir.getChildsItems() != null) {
			for (PlaceImportItem item : dir.getChildsItems()) {
				File newFile = new File(parentDirectory + "/" + item.getItem().getName() + findExtension(item));
				writeFile(packagePath, newFile, item);
			}
		}
	}

	private static String findExtension(PlaceImportItem item) {
		switch (item.getItem().getType()) {
		case IRepositoryApi.FASD_TYPE:
			return ".fasd";
		case IRepositoryApi.FD_TYPE:
			return ".fd";
		case IRepositoryApi.FD_DICO_TYPE:
			return ".dico";
		case IRepositoryApi.FMDT_TYPE:
			return ".freemetadata";
		case IRepositoryApi.GTW_TYPE:
			return ".gateway";
		case IRepositoryApi.GED_TYPE:
			return ".ged";
		case IRepositoryApi.BIW_TYPE:
			return ".biw";
		case IRepositoryApi.EXTERNAL_DOCUMENT:
			return ".ext_document";
		case IRepositoryApi.FWR_TYPE:
			return ".fwr";
		case IRepositoryApi.FAV_TYPE:
			return ".fav";
		case IRepositoryApi.CUST_TYPE:
			if (item.getItem().getSubtype() == IRepositoryApi.BIRT_REPORT_SUBTYPE) {
				return ".rptdesign";
			}
		}
		return ".item";
	}

	private static void writeFile(String packagePath, File newFile, PlaceImportItem item) throws Exception {
		newFile.createNewFile();

		String xml = readItemXML(packagePath, item.getItem().getId());

		PrintWriter out = new PrintWriter(newFile);
		out.println(xml);
		out.close();
	}

	private static String readItemXML(String packagePath, int itemId) throws Exception {
		ZipFile zipFile = new ZipFile(packagePath);

		ZipEntry zipEntry = zipFile.getEntry(String.valueOf(itemId));
		if (zipEntry != null) {
			InputStream is = zipFile.getInputStream(zipEntry);
			String xml = IOUtils.toString(is, "UTF-8"); //$NON-NLS-1$
			zipFile.close();
			return xml;
		}
		zipFile.close();
		return null; //$NON-NLS-1$
	}
}
