package bpm.metadata.web.client.wizard;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

import bpm.gwt.commons.client.custom.LabelTextBox;
import bpm.gwt.commons.client.custom.LabelTextBoxWithButton;
import bpm.gwt.commons.client.custom.LabelTextBoxWithButton.ButtonClickHandler;
import bpm.gwt.commons.client.dialog.D4CManagerDialog;
import bpm.gwt.commons.client.wizard.IGwtPage;
import bpm.gwt.commons.client.wizard.IGwtWizard;
import bpm.metadata.web.client.panels.DatasourcePanel;
import bpm.vanilla.platform.core.beans.data.Datasource;
import bpm.vanilla.platform.core.beans.data.DatasourceJdbc;
import bpm.vanilla.platform.core.beans.data.DatasourceType;
import bpm.vanilla.platform.core.beans.resources.D4C;

public class DatasourceD4CDefinitionPage extends Composite implements IGwtPage {

	private static DatasourceDefinitionPageUiBinder uiBinder = GWT.create(DatasourceDefinitionPageUiBinder.class);

	interface DatasourceDefinitionPageUiBinder extends UiBinder<Widget, DatasourceD4CDefinitionPage> {
	}
	
	@UiField
	LabelTextBoxWithButton txtD4C;
	
	@UiField
	LabelTextBox txtOrganisation;
	
	@UiField(provided=true)
	DatasourcePanel datasourcePanel;
	
	private D4C item;

	public DatasourceD4CDefinitionPage(IGwtWizard parent, int userId) {
		datasourcePanel = new DatasourcePanel(parent, userId);
		datasourcePanel.loadItem(buildD4CDatasource());
		datasourcePanel.simplifyParameters();
		
		initWidget(uiBinder.createAndBindUi(this));
		
		txtD4C.setButtonClickHandler(d4cClickHandler);
	}
	
	private Datasource buildD4CDatasource() {
		DatasourceJdbc jdbc = new DatasourceJdbc();
		jdbc.setDriver("org.postgresql.Driver");
		
		Datasource datasource = new Datasource();
		datasource.setType(DatasourceType.JDBC);
		datasource.setObject(jdbc);
		return datasource;
	}

	private ButtonClickHandler d4cClickHandler = new ButtonClickHandler() {
		
		@Override
		public void onBtnClick() {
			D4CManagerDialog dial = new D4CManagerDialog(true);
			dial.addCloseHandler(new CloseHandler<PopupPanel>() {
				
				@Override
				public void onClose(CloseEvent<PopupPanel> event) {
					if (dial.isConfirm()) {
						item = dial.getSelectedItem();
						txtD4C.setText(item.getName());
					}
				}
			});
			dial.center();
		}
	};

	@Override
	public boolean canGoBack() {
		return true;
	}

	@Override
	public boolean isComplete() {
		return item != null && datasourcePanel.isComplete();
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

	public D4C getD4C() {
		return item;
	}

	public String getD4COrganisation() {
		return txtOrganisation.getText();
	}
}
