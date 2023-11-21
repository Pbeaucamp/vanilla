package bpm.gwt.commons.client.datasource;

import bpm.gwt.commons.client.wizard.IGwtPage;
import bpm.vanilla.platform.core.beans.data.IDatasourceObject;

public interface IDatasourceObjectPage extends IGwtPage {

	public IDatasourceObject getDatasourceObject();
	
}
