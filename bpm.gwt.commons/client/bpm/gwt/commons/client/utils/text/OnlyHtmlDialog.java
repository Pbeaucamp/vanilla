package bpm.gwt.commons.client.utils.text;

import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.custom.TextAreaHolderBox;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;

public class OnlyHtmlDialog extends AbstractDialogBox {

	private static AdminDialogUiBinder uiBinder = GWT.create(AdminDialogUiBinder.class);

	interface AdminDialogUiBinder extends UiBinder<Widget, OnlyHtmlDialog> {
	}

	interface MyStyle extends CssResource {
	}

	@UiField
	MyStyle style;

	@UiField
	TextAreaHolderBox txtArea;

	private IHtmlChangeHandler htmlChangeHandler;

	public OnlyHtmlDialog(IHtmlChangeHandler htmlChangeHandler, String html) {
		super(LabelsConstants.lblCnst.HtmlEditor(), false, true);
		this.htmlChangeHandler = htmlChangeHandler;

		setWidget(uiBinder.createAndBindUi(this));

		createButtonBar(LabelsConstants.lblCnst.Ok(), okHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);

		txtArea.setText(html);
	}

	private ClickHandler okHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			String html = txtArea.getText();
			htmlChangeHandler.changeHtml(html);
			OnlyHtmlDialog.this.hide();
		}
	};

	private ClickHandler cancelHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			OnlyHtmlDialog.this.hide();
		}
	};

	public interface IHtmlChangeHandler {

		public void changeHtml(String html);

	}
}
