package bpm.gwt.commons.client.free.metrics;

import bpm.fm.api.model.Observatory;
import bpm.fm.api.model.Theme;
import bpm.vanilla.platform.core.beans.Group;

public interface IFilterChangeHandler {

	public void selectionChanged(Group group, Observatory obs, Theme theme);
	
}
