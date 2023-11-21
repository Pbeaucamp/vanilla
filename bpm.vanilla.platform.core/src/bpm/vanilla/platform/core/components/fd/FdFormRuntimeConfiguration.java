package bpm.vanilla.platform.core.components.fd;

import java.util.ArrayList;
import java.util.HashMap;

import bpm.vanilla.platform.core.IObjectIdentifier;
import bpm.vanilla.platform.core.beans.parameters.VanillaGroupParameter;
import bpm.vanilla.platform.core.impl.RuntimeConfiguration;

public class FdFormRuntimeConfiguration extends RuntimeConfiguration implements IFdFormRuntimeConfig{

	private String submissionUrl;
	private HashMap<String, String> hiddenFields;
	public FdFormRuntimeConfiguration(int groupId,
			IObjectIdentifier identifier, String submissionUrl, HashMap<String, String> hiddenFields) {
		super(groupId, identifier, new ArrayList<VanillaGroupParameter>());
		this.submissionUrl = submissionUrl;
		this.hiddenFields = hiddenFields;
	}

	@Override
	public String getSubmissionUrl() {
		return submissionUrl;
	}

	@Override
	public HashMap<String, String> getHiddenFields() {
		return hiddenFields;
	}
	
	

}
