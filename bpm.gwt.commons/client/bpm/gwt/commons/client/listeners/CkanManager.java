package bpm.gwt.commons.client.listeners;

import bpm.vanilla.platform.core.beans.resources.CkanPackage;
import bpm.vanilla.platform.core.beans.resources.CkanResource;

public interface CkanManager {

	public void managePackage(CkanPackage pack, CkanResource resource);
}
