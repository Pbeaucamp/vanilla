package bpm.vanillahub.runtime.managers;

import bpm.vanilla.platform.core.beans.resources.VariableString;
import bpm.vanillahub.core.beans.resources.Certificat;
import bpm.vanillahub.core.beans.resources.Certificat.TypeCertificat;

public class CertificatManager extends ResourceManager<Certificat> {

	private static final String CERTIFICAT_FILE_NAME = "certificats.xml";

	public CertificatManager(String filePath) {
		super(filePath, CERTIFICAT_FILE_NAME, "Certificats");
	}
	
	@Override
	protected void manageResourceForAdd(Certificat resource) { }
	
	@Override
	protected void manageResourceForModification(Certificat newResource, Certificat oldResource) {
		String name = oldResource.getName();
		TypeCertificat type = oldResource.getTypeCertificat();
		String file = oldResource.getFile();
		VariableString technicalName = oldResource.getTechnicalNameVS();
		String password = oldResource.getPassword();
		
		newResource.updateInfo(name, type, file, technicalName, password);
	}
}
