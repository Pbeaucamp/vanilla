package bpm.vanillahub.web.client.dialogs;

import bpm.document.management.core.model.IObject.ItemTreeType;
import bpm.gwt.aklabox.commons.client.dialogs.AklaboxDialog;
import bpm.gwt.aklabox.commons.shared.AklaboxConnection;
import bpm.gwt.commons.client.custom.TextHolderBox;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.workflow.commons.client.I18N.LabelsCommon;
import bpm.vanillahub.core.beans.activities.attributes.AklaboxDispatch;
import bpm.vanillahub.web.client.I18N.Labels;
import bpm.vanillahub.web.client.properties.parameters.AklaboxDispatchPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class AddDispatchRuleDialog extends AbstractDialogBox {

	private static AddDispatchRuleDialogUiBinder uiBinder = GWT.create(AddDispatchRuleDialogUiBinder.class);

	interface AddDispatchRuleDialogUiBinder extends UiBinder<Widget, AddDispatchRuleDialog> {
	}

	@UiField
	TextHolderBox txtPattern, txtFolderDestination;

	@UiField
	Button btnBrowse;

	private AklaboxDispatchPanel parent;
	private AklaboxConnection server;
	
	private Integer selectedFolderId;
	private String selectedFolderName;

	public AddDispatchRuleDialog(AklaboxDispatchPanel parent, AklaboxConnection server) {
		super(Labels.lblCnst.AddDispatchRule(), false, true);
		this.parent = parent;
		this.server = server;

		setWidget(uiBinder.createAndBindUi(this));
		createButtonBar(LabelsCommon.lblCnst.Confirmation(), confirmHandler, LabelsCommon.lblCnst.Cancel(), cancelHandler);
	}

	@UiHandler("btnBrowse")
	public void onBrowse(ClickEvent event) {
		final AklaboxDialog dial = new AklaboxDialog(server, server.getLogin(), ItemTreeType.ENTERPRISE, null);
		dial.addCloseHandler(new CloseHandler<PopupPanel>() {
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				selectedFolderId = dial.getSelectedItem().getId();
				selectedFolderName = dial.getSelectedItem().getName();

				txtFolderDestination.setText(selectedFolderName);
			}
		});
		dial.center();
	}

	private ClickHandler cancelHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			hide();
		}
	};

	private ClickHandler confirmHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			String pattern = txtPattern.getText();
			if (pattern.isEmpty() || selectedFolderId == null) {
				return;
			}

			parent.addAklaboxDispatch(new AklaboxDispatch(pattern, selectedFolderId, selectedFolderName));
			hide();
		}
	};
}
