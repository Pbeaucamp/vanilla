package bpm.gwt.commons.client.dialog;

import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.custom.TextHolderBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;

public class DataPreparationDialog extends AbstractDialogBox {

	private static DataPreparationDialogUiBinder uiBinder = GWT.create(DataPreparationDialogUiBinder.class);

	interface DataPreparationDialogUiBinder extends UiBinder<Widget, DataPreparationDialog> {}

	@UiField
	TextHolderBox txtName, txtSeparator;

	private IDataPreparationManager manager;
	
	public DataPreparationDialog(IDataPreparationManager manager, boolean showSeparator) {
		super(LabelsConstants.lblCnst.ShareToDataPreparation(), false, true);
		this.manager = manager;
		
		setWidget(uiBinder.createAndBindUi(this));
		
		createButtonBar(LabelsConstants.lblCnst.Confirmation(), confirmHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);
		
		if (showSeparator) {
			txtSeparator.setVisible(true);
		}
	}
	
	private ClickHandler confirmHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			String name = txtName.getText();
			String separator = txtSeparator.getText();
			
			hide();
			manager.createDataPreparation(name, separator);
		}
	};
	
	private ClickHandler cancelHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			hide();
		}
	};
	
	public interface IDataPreparationManager {
		
		public void createDataPreparation(String name, String separator);
	}
}
