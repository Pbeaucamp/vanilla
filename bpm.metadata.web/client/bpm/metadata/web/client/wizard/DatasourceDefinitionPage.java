package bpm.metadata.web.client.wizard;

import bpm.gwt.commons.client.wizard.IGwtPage;
import bpm.gwt.commons.client.wizard.IGwtWizard;
import bpm.metadata.web.client.panels.DatasourcePanel;
import bpm.vanilla.platform.core.beans.data.Datasource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class DatasourceDefinitionPage extends Composite implements IGwtPage {

	private static DatasourceDefinitionPageUiBinder uiBinder = GWT.create(DatasourceDefinitionPageUiBinder.class);

	interface DatasourceDefinitionPageUiBinder extends UiBinder<Widget, DatasourceDefinitionPage> {
	}
	
	@UiField(provided=true)
	DatasourcePanel datasourcePanel;

	public DatasourceDefinitionPage(IGwtWizard parent, int userId) {
		datasourcePanel = new DatasourcePanel(parent, userId);
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public boolean canGoBack() {
		return true;
	}

	@Override
	public boolean isComplete() {
		return datasourcePanel.isComplete();
	}

	@Override
	public boolean canGoFurther() {
		return true;
	}

	@Override
	public int getIndex() {
		return 1;
	}

	public Datasource getDatasource() {
		return datasourcePanel.getDatasource();
	}
}
