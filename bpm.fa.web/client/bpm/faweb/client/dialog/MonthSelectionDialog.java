package bpm.faweb.client.dialog;

import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class MonthSelectionDialog extends AbstractDialogBox {

	private static MonthSelectionDialogUiBinder uiBinder = GWT.create(MonthSelectionDialogUiBinder.class);

	interface MonthSelectionDialogUiBinder extends UiBinder<Widget, MonthSelectionDialog> {}

	private boolean isConfirm; 
	private int month;
	
	@UiField
	ListBox lstMonth;
	
	public MonthSelectionDialog() {
		super("Month selection", false, true);
		
		setWidget(uiBinder.createAndBindUi(this));
		
		createButton(LabelsConstants.lblCnst.Cancel(), cancelHandler);
		createButton(LabelsConstants.lblCnst.Confirmation(), okHandler);
		
		lstMonth.addItem(LabelsConstants.lblCnst.January());
		lstMonth.addItem(LabelsConstants.lblCnst.February());
		lstMonth.addItem(LabelsConstants.lblCnst.March());
		lstMonth.addItem(LabelsConstants.lblCnst.April());
		lstMonth.addItem(LabelsConstants.lblCnst.May());
		lstMonth.addItem(LabelsConstants.lblCnst.June());
		lstMonth.addItem(LabelsConstants.lblCnst.July());
		lstMonth.addItem(LabelsConstants.lblCnst.August());
		lstMonth.addItem(LabelsConstants.lblCnst.September());
		lstMonth.addItem(LabelsConstants.lblCnst.October());
		lstMonth.addItem(LabelsConstants.lblCnst.November());
		lstMonth.addItem(LabelsConstants.lblCnst.December());
	}

	private ClickHandler okHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			isConfirm = true;
			month = lstMonth.getSelectedIndex() + 1;
			MonthSelectionDialog.this.hide();
		}
	};
	
	private ClickHandler cancelHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			MonthSelectionDialog.this.hide();
		}
	};
	
	public int getMonth() {
		return month;
	}

	public boolean isConfirm() {
		return isConfirm;
	}
	
}
