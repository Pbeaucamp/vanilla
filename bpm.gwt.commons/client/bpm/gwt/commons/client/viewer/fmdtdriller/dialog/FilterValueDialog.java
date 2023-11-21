package bpm.gwt.commons.client.viewer.fmdtdriller.dialog;

import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class FilterValueDialog extends AbstractDialogBox {

	private static FilterValueDialogUiBinder uiBinder = GWT.create(FilterValueDialogUiBinder.class);

	interface FilterValueDialogUiBinder extends UiBinder<Widget, FilterValueDialog> {
	}

	interface MyStyle extends CssResource {
	}

	@UiField
	TextBox txtValue;

	Boolean confirm = false;

	public FilterValueDialog() {
		super(LabelsConstants.lblCnst.Filter(), false, true);
		setWidget(uiBinder.createAndBindUi(this));
		createButtonBar(LabelsConstants.lblCnst.Ok(), okHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);
	}

	private ClickHandler okHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			confirm = true;
			hide();
		}
	};

	private ClickHandler cancelHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			hide();
		}
	};

	public Boolean isConfirm() {
		return confirm;
	}

	public String getValue() {
		return txtValue.getText();
	}

}
