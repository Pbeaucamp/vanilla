package bpm.vanillahub.core.beans.activities;

import bpm.vanillahub.core.beans.resources.FileXSD;
import bpm.workflow.commons.beans.TypeActivity;

public class ValidationActivity extends ActivityWithResource<FileXSD> {

	public ValidationActivity() { }
	
	public ValidationActivity(String name) {
		super(TypeActivity.VALIDATION, name);
	}

	@Override
	public boolean isValid() {
		return getResourceId() > 0;
	}

}
