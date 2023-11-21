package bpm.gwt.commons.client.dialog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;

import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.panels.D4CDefinitionPanel;
import bpm.vanilla.platform.core.beans.resources.D4C;

public class D4CManagerDialog extends AbstractDialogBox  {

	private static D4CManagerDialogUiBinder uiBinder = GWT.create(D4CManagerDialogUiBinder.class);

	interface D4CManagerDialogUiBinder extends UiBinder<Widget, D4CManagerDialog> {
	}

	interface MyStyle extends CssResource {
		String overflowHidden();
	}

	@UiField
	MyStyle style;
	
	@UiField(provided=true)
	D4CDefinitionPanel d4cDefinitionPanel;
	
	private boolean isConfirm = false;
	
	public D4CManagerDialog(boolean full) {
		super(LabelsConstants.lblCnst.D4CManager(), true, true);
		
		d4cDefinitionPanel = new D4CDefinitionPanel(this, null, full);
		setWidget(uiBinder.createAndBindUi(this));
		
		createButtonBar(LabelsConstants.lblCnst.Confirmation(), confirmHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);
	}

	private ClickHandler confirmHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			if (isComplete()) {
				isConfirm = true;
				hide();
			}
		}
	};

	private ClickHandler cancelHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			hide();
		}
	};

	private boolean isComplete() {
		return getSelectedItem() != null;
	}
	
	public boolean isConfirm() {
		return isConfirm;
	}
	
	public D4C getSelectedItem() {
		return d4cDefinitionPanel.getSelectedItem();
	}
}
