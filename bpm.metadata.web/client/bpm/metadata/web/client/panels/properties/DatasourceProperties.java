package bpm.metadata.web.client.panels.properties;

import bpm.gwt.commons.client.custom.LabelTextBox;
import bpm.gwt.commons.client.custom.LabelTextBoxWithButton;
import bpm.gwt.commons.client.custom.LabelTextBoxWithButton.ButtonClickHandler;
import bpm.gwt.commons.client.dialog.D4CManagerDialog;
import bpm.gwt.commons.client.loading.IWait;
import bpm.gwt.commons.shared.fmdt.metadata.Metadata;
import bpm.metadata.web.client.panels.DatasourcePanel;
import bpm.vanilla.platform.core.beans.data.Datasource;
import bpm.vanilla.platform.core.beans.resources.D4C;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class DatasourceProperties extends Composite implements IPanelProperties {

	private static MetadataPropertiesUiBinder uiBinder = GWT.create(MetadataPropertiesUiBinder.class);

	interface MetadataPropertiesUiBinder extends UiBinder<Widget, DatasourceProperties> {
	}
	
	@UiField
	LabelTextBoxWithButton txtD4C;
	
	@UiField
	LabelTextBox txtOrganisation;
	
	@UiField(provided=true)
	DatasourcePanel datasourcePanel;
	
	private Metadata metadata;
	private D4C d4cServer;
	
	public DatasourceProperties(IWait waitPanel, int userId, Metadata metadata, Datasource item) {
		this.metadata = metadata;
		datasourcePanel = new DatasourcePanel(waitPanel, userId);
		initWidget(uiBinder.createAndBindUi(this));

		datasourcePanel.loadItem(item);
		
		this.d4cServer = metadata.getD4cServer();
		txtD4C.setText(d4cServer != null && d4cServer.getName() != null ? d4cServer.getName() : "");
		txtD4C.setVisible(d4cServer != null);
		txtD4C.setButtonClickHandler(d4cClickHandler);
		txtOrganisation.setText(metadata.getD4cOrganisation() != null ? metadata.getD4cOrganisation() : "");
		txtOrganisation.setVisible(d4cServer != null);
	}

	private ButtonClickHandler d4cClickHandler = new ButtonClickHandler() {
		
		@Override
		public void onBtnClick() {
			D4CManagerDialog dial = new D4CManagerDialog(true);
			dial.addCloseHandler(new CloseHandler<PopupPanel>() {
				
				@Override
				public void onClose(CloseEvent<PopupPanel> event) {
					if (dial.isConfirm()) {
						d4cServer = dial.getSelectedItem();
						txtD4C.setText(d4cServer.getName());
					}
				}
			});
			dial.center();
		}
	};

	@Override
	public void apply() {
		datasourcePanel.applyProperties();
		
		if (d4cServer != null) {
			metadata.setD4cServer(d4cServer);
			metadata.setD4cOrganisation(txtOrganisation.getText());
		}
	}

	@Override
	public boolean isValid() {
		return datasourcePanel.isComplete();
	}
}
