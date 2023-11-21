package bpm.smart.web.client.dialogs;

import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.smart.web.client.I18N.LabelsConstants;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class CustomDialog extends AbstractDialogBox {
	private static CustomUiBinder uiBinder = GWT
			.create(CustomUiBinder.class);

	interface CustomUiBinder extends
			UiBinder<Widget, CustomDialog> {
	}

	@UiField
	HTMLPanel panelContent, dialog;
	
	private Widget widget;

	public static LabelsConstants lblCnst = (LabelsConstants) GWT
			.create(LabelsConstants.class);

	public CustomDialog(String title, Widget widget) {
		super(title, false, false);
		setWidget(uiBinder.createAndBindUi(this));
		panelContent.add(widget);
		this.widget = widget;
		
		dialog.getParent().getElement().getStyle().setOverflow(Overflow.HIDDEN);
	}
	
	public CustomDialog(String title, Widget widget, String height, String width) {
		this(title, widget);
		
		dialog.setHeight(height);
		dialog.setWidth(width);
	}
	
	public CustomDialog(String title, Widget widget, String okLabel, String cancelLabel, ClickHandler okHandler, ClickHandler cancelHandler) {
		super(title, false, true);
		setWidget(uiBinder.createAndBindUi(this));
		panelContent.add(widget);
		this.widget = widget;
		createButtonBar(okLabel, okHandler, cancelLabel, cancelHandler);
	}
	
	public CustomDialog(String title, Widget widget, String okLabel, String cancelLabel, ClickHandler okHandler, ClickHandler cancelHandler, String height, String width) {
		this(title, widget, okLabel, cancelLabel, okHandler, cancelHandler);
		
		dialog.setHeight(height);
		dialog.setWidth(width);
	}

	public Widget getWidget() {
		return widget;
	}
	
	
}
