package bpm.vanillahub.runtime.managers;

import bpm.vanilla.platform.core.beans.resources.CkanPackage;
import bpm.vanilla.platform.core.beans.resources.VariableString;
import bpm.vanillahub.core.beans.resources.Source;
import bpm.vanillahub.core.beans.resources.Source.TypeSource;
import bpm.vanillahub.core.beans.resources.attributes.WebServiceMethodDefinition;

public class SourceManager extends ResourceManager<Source> {

	private static final String SOURCE_FILE_NAME = "sources.xml";

	public SourceManager(String filePath) {
		super(filePath, SOURCE_FILE_NAME, "Sources");
	}
	
	@Override
	protected void manageResourceForAdd(Source resource) { }
	
	@Override
	protected void manageResourceForModification(Source newResource, Source oldResource) {
		String name = oldResource.getName();
		TypeSource type = oldResource.getType();
		VariableString folderPath = oldResource.getFolderPathVS();
		VariableString filter = oldResource.getFilterVS();
		VariableString attachmentFilter = oldResource.getAttachmentFilterVS();
		boolean isNetworkFolder = oldResource.isNetworkFolder();
		boolean includeSubfolder = oldResource.includeSubfolder();
		boolean isSecured = oldResource.isSecured();
		boolean useOutputName = oldResource.useOutputName();
		VariableString outputName = oldResource.getOutputNameVS();
		VariableString url = oldResource.getUrlVS();
		VariableString port = oldResource.getPortVS();
		WebServiceMethodDefinition method = oldResource.getMethod();
		String login = oldResource.getLogin();
		String password = oldResource.getPassword();
		CkanPackage ckanPackage = oldResource.getCkanPackage();
		VariableString treatedFolderPath = oldResource.getTreatedFolderPathVS();
		boolean rejectNonMatchingMail = oldResource.isRejectNonMatchingMail();
		VariableString rejectedFolderPath = oldResource.getRejectedFolderPathVS();
		boolean copyFile = oldResource.isCopyFile();

		newResource.updateInfo(name, type, folderPath, filter, attachmentFilter, isNetworkFolder, includeSubfolder, isSecured, useOutputName, 
				outputName, url, port, method, login, password, ckanPackage, treatedFolderPath, rejectNonMatchingMail, rejectedFolderPath, copyFile);
	}
}
