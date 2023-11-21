package bpm.architect.web.client.dialogs;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import bpm.architect.web.client.I18N.Labels;
import bpm.architect.web.client.panels.ConsultPanel;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.dialog.ButtonTab;
import bpm.gwt.commons.client.dialog.ITabDialog;
import bpm.gwt.commons.client.listeners.TabSwitchHandler;
import bpm.gwt.commons.shared.InfoUser;
import bpm.mdm.model.supplier.Contract;

public class IntegrationManagerDialog extends AbstractDialogBox implements ITabDialog {
	
	private static IntegrationManagerDialogUiBinder uiBinder = GWT.create(IntegrationManagerDialogUiBinder.class);

	interface IntegrationManagerDialogUiBinder extends UiBinder<Widget, IntegrationManagerDialog> {
	}

	interface MyStyle extends CssResource {
		String contentPanelMax();
	}

	@UiField
	MyStyle style;

	@UiField
	HTMLPanel contentPanel;

	@UiField
	HTMLPanel panelTab;

	@UiField
	SimplePanel panelContent;

	private ButtonTab selectedButton;

	public IntegrationManagerDialog(ConsultPanel parent, InfoUser infoUser, Contract contract) {
		super(Labels.lblCnst.IntegrationManager(), true, false);
		setWidget(uiBinder.createAndBindUi(this));

		addTab(Labels.lblCnst.Process(), new IntegrationInformationsComposite(this, infoUser, contract), true);
		addTab(LabelsConstants.lblCnst.ValidationSchemas(), new SchemaLinkedComposite(this, contract), false);
		addTab(Labels.lblCnst.ItemsLinked(), new ItemLinkedComposite(this, infoUser, contract, false, true), false);
	}

	public void addTab(String buttonName, Widget widget, boolean selectTab) {
		ButtonTab button = new ButtonTab(buttonName);
		button.addClickHandler(new TabSwitchHandler(this, button, widget));
		panelTab.add(button);

		if (selectTab) {
			switchViewer(button, widget);
		}
	}

	public boolean needRefreshContracts() {
		return false;
	}

	@Override
	public void switchViewer(ButtonTab button, Widget widget) {
		if (selectedButton != null) {
			this.selectedButton.select(false);
		}
		button.select(true);

		this.selectedButton = button;

		panelContent.setWidget(widget);
	}

	@Override
	public void maximize(boolean maximize) {
		if (maximize) {
			contentPanel.addStyleName(style.contentPanelMax());
		}
		else {
			contentPanel.removeStyleName(style.contentPanelMax());
		}
	}

}
