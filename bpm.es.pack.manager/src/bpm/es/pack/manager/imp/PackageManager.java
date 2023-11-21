package bpm.es.pack.manager.imp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.zip.ZipException;

import bpm.es.pack.manager.digester.ExportDescriptorDigester;
import bpm.es.pack.manager.digester.ImportHistoricDigester;
import bpm.es.pack.manager.utils.PackageCreator;
import bpm.es.pack.manager.utils.PackageImporter;
import bpm.vanilla.workplace.core.model.ExportDetails;
import bpm.vanilla.workplace.core.model.ImportHistoric;
import bpm.vanilla.workplace.core.model.VanillaPackage;

public class PackageManager {

	private String packageFolder;
	private String temporaryFolder;
	private String historicFileName;

	private HashMap<ExportDetails, File> oldPackages = new HashMap<ExportDetails, File>();
	private HashMap<VanillaPackage, File> newPackages = new HashMap<VanillaPackage, File>();

	private List<ImportHistoric> packagesHistorics = new ArrayList<ImportHistoric>();

	public PackageManager(String historicFileName, String pakagesFolder, String temporaryFolder) throws Exception {
		this.packageFolder = pakagesFolder;

		File f = new File(this.packageFolder);
		if (!f.exists()) {
			f.mkdirs();
		}
		this.temporaryFolder = temporaryFolder;
		f = new File(this.temporaryFolder);
		if (!f.exists()) {
			f.mkdirs();
		}

		this.historicFileName = historicFileName;
		if (new File(historicFileName).exists()) {
			ImportHistoricDigester dig = new ImportHistoricDigester(historicFileName);
			for (Object o : dig.getModel()) {
				packagesHistorics.add((ImportHistoric) o);
			}
		}

		loadPackages(new File(packageFolder));
	}

	private void loadPackages(File folder) throws ZipException, IOException {

		File _d = new File(temporaryFolder);
		if (!_d.exists()) {
			_d.mkdirs();
		}

		oldPackages.clear();
		newPackages.clear();

		for (String fileName : folder.list()) {
			if (fileName.toLowerCase().contains("bpmpackage")) { //$NON-NLS-1$
				File fileP = new File(folder.getAbsolutePath() + File.separator + fileName);
				HashMap<String, File> map = Importer.unzip(fileP, temporaryFolder);
				for (String s : map.keySet()) {
					if (s.endsWith("descriptor")) { //$NON-NLS-1$
						try {
							oldPackages.put(new ExportDescriptorDigester(temporaryFolder + File.separator + s).getModel(), fileP);
						} catch (Exception e) {
							e.printStackTrace();
						}

						break;
					}
					new File(s).delete();
				}
			}
			else if (fileName.toLowerCase().contains(PackageCreator.VANILLA_PACKAGE_EXTENSION)) {
				File fileP = new File(folder.getAbsolutePath() + File.separator + fileName);
				try {
					VanillaPackage vanillaPackage = PackageImporter.getVanillaPackage(fileP);
					newPackages.put(vanillaPackage, fileP);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void addImportToHistoric(ExportDetails exportName, String fileName, String logs) throws Exception {
		addImportToHistoric(exportName.getName(), fileName, logs);
	}

	public void addImportToHistoric(VanillaPackage vanillaPackage, String fileName, String logs) throws Exception {
		addImportToHistoric(vanillaPackage.getName(), fileName, logs);
	}

	private void addImportToHistoric(String packageName, String fileName, String logs) throws Exception {
		ImportHistoric i = new ImportHistoric();
		i.setDate(Calendar.getInstance().getTime());
		i.setFileName(fileName);
		i.setPackageName(packageName);
		i.setLogs(logs);

		packagesHistorics.add(i);
		saveHistoric();
	}

	private void saveHistoric() throws Exception {
		StringBuffer buf = new StringBuffer();
		buf.append("<import>"); //$NON-NLS-1$
		for (ImportHistoric e : packagesHistorics) {
			buf.append(e.getXml());
		}
		buf.append("</import>"); //$NON-NLS-1$

		FileOutputStream fos = null;
		File file;

		try {

			file = new File(historicFileName);
			fos = new FileOutputStream(file);

			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			// get the content in bytes
			byte[] contentInBytes = buf.toString().getBytes("UTF-8"); //$NON-NLS-1$

			fos.write(contentInBytes);
			fos.flush();
			fos.close();

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public List<ImportHistoric> getHistoric(ExportDetails e) {
		List<ImportHistoric> l = new ArrayList<ImportHistoric>();

		for (ImportHistoric i : packagesHistorics) {
			if (i.getPackageName().equals(e.getName())) {
				l.add(i);
				break;
			}
		}
		return l;
	}

	public List<ImportHistoric> getHistoric(VanillaPackage vanillaPack) {
		List<ImportHistoric> l = new ArrayList<ImportHistoric>();

		for (ImportHistoric i : packagesHistorics) {
			if (i.getPackageName().equals(vanillaPack.getName())) {
				l.add(i);
			}
		}
		return l;
	}

	public void refresh() throws Exception {
		loadPackages(new File(packageFolder));
		packagesHistorics.clear();

		if (new File(historicFileName).exists()) {
			ImportHistoricDigester dig = new ImportHistoricDigester(historicFileName);
			for (Object o : dig.getModel()) {
				packagesHistorics.add((ImportHistoric) o);
			}
		}
	}

	public HashMap<Object, File> getAllPackages() {
		LinkedHashMap<Object, File> packages = new LinkedHashMap<Object, File>();
		packages.putAll(oldPackages);
		packages.putAll(newPackages);

		List<Object> keys = new ArrayList<Object>(packages.keySet());

		Collections.sort(keys, new Comparator<Object>() {

			@Override
			public int compare(Object obj1, Object obj2) {
				String name1 = null;
				String name2 = null;

				if (obj1 instanceof ExportDetails) {
					name1 = ((ExportDetails) obj1).getName();
				}
				else if (obj1 instanceof VanillaPackage) {
					name1 = ((VanillaPackage) obj1).getName();
				}

				if (obj2 instanceof ExportDetails) {
					name2 = ((ExportDetails) obj2).getName();
				}
				else if (obj2 instanceof VanillaPackage) {
					name2 = ((VanillaPackage) obj2).getName();
				}

				return name1.compareTo(name2);
			}
		});
		

		if(keys != null && !keys.isEmpty()){
			HashMap<Object, File> sortedPackages = new LinkedHashMap<Object, File>();
			for(Object key : keys){
				File file = packages.get(key);
				if(file != null){
					sortedPackages.put(key, file);
				}
			}
			
			return sortedPackages;
		}
		else {
			return new HashMap<Object, File>();
		}
	}
}
