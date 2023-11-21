package bpm.vanillahub.runtime.managers;

import bpm.vanillahub.core.beans.resources.FileXSD;

public class FileXSDManager extends ResourceManager<FileXSD> {

	private static final String XSD_FILE_NAME = "xsd.xml";

	public FileXSDManager(String filePath) {
		super(filePath, XSD_FILE_NAME, "XSD");
	}
	
	@Override
	protected void manageResourceForAdd(FileXSD resource) { }
	
	@Override
	protected void manageResourceForModification(FileXSD newResource, FileXSD oldResource) {
		String name = oldResource.getName();
		String file = oldResource.getFile();
		
		newResource.updateInfo(name, file);
	}
}
