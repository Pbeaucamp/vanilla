package bpm.vanillahub.runtime.managers;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import bpm.vanillahub.runtime.i18N.Labels;

public class FileManager {
	
	private static final String FILES = "Files/";

	private String fileFolder;
	
	public FileManager(String filePath) {
		this.fileFolder = filePath.endsWith("/") ? filePath + FILES : filePath + "/" + FILES;

		File folder = new File(fileFolder);
		if (!folder.exists()) {
			folder.mkdirs();
		}
	}
	
	public File addFile(Locale locale, String itemName) throws Exception {
		File file = new File(fileFolder + cleanName(itemName));
		if (!file.exists()) {
			try {
				if (!file.createNewFile()) {
					throw new Exception("'" + itemName + "' " + Labels.getLabel(locale, Labels.CannotBeCreated));
				}
			} catch (IOException e) {
				e.printStackTrace();
				throw new Exception("'" + itemName + "' " + Labels.getLabel(locale, Labels.CannotBeCreated) + " : " + e.getMessage());
			}
		}
		
		return file;
	}
	
	private String cleanName(String fileName) {
		return fileName.replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
	}

	public File getFile(String filePath) {
		return new File(fileFolder + filePath);
	}
}
