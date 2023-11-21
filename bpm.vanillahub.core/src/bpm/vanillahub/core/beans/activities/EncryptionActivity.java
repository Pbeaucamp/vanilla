package bpm.vanillahub.core.beans.activities;

import bpm.vanillahub.core.beans.resources.Certificat;
import bpm.workflow.commons.beans.TypeActivity;

public class EncryptionActivity extends ActivityWithResource<Certificat> {

	private boolean encryption;
	
	public EncryptionActivity() { }
	
	public EncryptionActivity(String name) {
		super(TypeActivity.ENCRYPTAGE, name);
	}
	
	public boolean isEncryption() {
		return encryption;
	}
	
	public void setEncryption(boolean encryption) {
		this.encryption = encryption;
	}

	@Override
	public boolean isValid() {
		return getResourceId() > 0;
	}
}
