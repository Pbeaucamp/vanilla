package bpm.gwt.aklabox.commons.client.utils;

import bpm.gwt.aklabox.commons.client.customs.CustomDialog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class DefaultDialog extends CustomDialog {

	private static DefaultDialogUiBinder uiBinder = GWT.create(DefaultDialogUiBinder.class);

	interface DefaultDialogUiBinder extends UiBinder<Widget, DefaultDialog> {
	}
	
	interface MyStyle extends CssResource{
		String maximize();
		String defaultDialog();
	}

	@UiField
	Image btnRestore, btnMaximize, btnClose;
	@UiField
	HTMLPanel dialog, content;
	public HTMLPanel getContent() {
		return content;
	}

	public void setContent(HTMLPanel content) {
		this.content = content;
	}

	@UiField
	public HTMLPanel defaultDialogHeaderPanel;
	@UiField
	Label lblTitle;
	@UiField
	MyStyle style;

	public DefaultDialog(String title) {
		super("");
		setWidget(uiBinder.createAndBindUi(this));

		lblTitle.setText(title);
		btnRestore.setVisible(false);
	}

	public DefaultDialog(String title, ChildDialogComposite child, int width, int height, int marginTop) {
		super("");
		setWidget(uiBinder.createAndBindUi(this));
		setStyleDialog(width, height, marginTop);
		lblTitle.setText(title);
		content.add(child);
		btnRestore.setVisible(false);
		
		child.setParent(this);
	}

	private void setStyleDialog(int width, int height, int marginTop) {
		dialog.getElement().setAttribute("style", "width: " + String.valueOf(width) + "px ;" + " margin-left: -" + String.valueOf(width / 2) + "px ; " + " left: 50% ; " + " top:" + String.valueOf(marginTop) + "% ; ");
	}

	@UiHandler("btnMaximize")
	void onMaximize(ClickEvent e) {
		btnRestore.setVisible(true);
		btnMaximize.setVisible(false);
		dialog.addStyleName(style.maximize());
	}

	@UiHandler("btnRestore")
	void onRestore(ClickEvent e) {
		btnRestore.setVisible(false);
		btnMaximize.setVisible(true);
		dialog.removeStyleName(style.maximize());
		dialog.addStyleName(style.defaultDialog());
	}

	@UiHandler("btnClose")
	void onClose(ClickEvent e) {
		hideDialog();
	}

	public void setDialogWidth(String width, String left) {
		dialog.getElement().setAttribute("style", "width: " + width + " !important; left: " + left + " !important ");
	}

	public void hideDialog() {
		dialog.getElement().setAttribute("style", "width: 0px; height: 0px;");
		Timer t = new Timer() {
			@Override
			public void run() {
				DefaultDialog.this.hide();
			}
		};
		t.schedule(1000);
	}

	public void setChildContent(ChildDialogComposite composite) {
		content.add(composite);
		composite.setParent(this);
	}

}
