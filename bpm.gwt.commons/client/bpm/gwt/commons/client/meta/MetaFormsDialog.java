package bpm.gwt.commons.client.meta;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.vanilla.platform.core.beans.meta.MetaForm;

public class MetaFormsDialog extends AbstractDialogBox {

	private static MetaFormsDialogUiBinder uiBinder = GWT.create(MetaFormsDialogUiBinder.class);

	interface MetaFormsDialogUiBinder extends UiBinder<Widget, MetaFormsDialog> {
	}

	@UiField
	HTMLPanel mainPanel;

	private boolean confirm;
	
	private MetaFormsPanel metaFormsPanel;

	public MetaFormsDialog() {
		super(LabelsConstants.lblCnst.MetaForm(), true, true);
		
		setWidget(uiBinder.createAndBindUi(this));

		createButtonBar(LabelsConstants.lblCnst.Confirmation(), confirmHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);
		
		this.metaFormsPanel = new MetaFormsPanel(this);
		mainPanel.add(metaFormsPanel);
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
			confirm = true;
			hide();
		}
	};

	public boolean isConfirm() {
		return confirm;
	}
	
	public MetaForm getSelectedForm() {
		return metaFormsPanel.getSelectedForm();
	}
}
