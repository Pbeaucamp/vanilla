package bpm.architect.web.client.dialogs;

import java.util.List;

import bpm.architect.web.client.I18N.Labels;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.custom.LabelTextBox;
import bpm.gwt.commons.client.custom.ListBoxWithButton;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.services.CommonService;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.vanilla.platform.core.beans.data.Datasource;
import bpm.vanilla.platform.core.beans.forms.Form;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;

public class FormDialog extends AbstractDialogBox {

	private static FormDialogUiBinder uiBinder = GWT.create(FormDialogUiBinder.class);

	interface FormDialogUiBinder extends UiBinder<Widget, FormDialog> {}

	@UiField
	LabelTextBox txtName, txtDesc, txtTable;
	
	@UiField
	ListBoxWithButton<Datasource> lstDatasource;

	private Form form;
	
	private boolean confirm;
	
	public FormDialog(Form f) {
		super(Labels.lblCnst.CreateForm(), false, true);
		setWidget(uiBinder.createAndBindUi(this));
		
		this.form = f;
		
		txtName.setText(form.getName());
		txtDesc.setText(form.getDescription());
		txtTable.setText(form.getTableName());
		
		CommonService.Connect.getInstance().getDatasources(new GwtCallbackWrapper<List<Datasource>>(this, false, false) {
			@Override
			public void onSuccess(List<Datasource> result) {
				lstDatasource.setList(result, true);
				
				if(form.getDatasource() != null) {
					lstDatasource.setSelectedObject(form.getDatasource());
				}
			}
		}.getAsyncCallback());

		createButtonBar(LabelsConstants.lblCnst.Confirmation(), confirmHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);
	}

	private ClickHandler confirmHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			form.setName(txtName.getText());
			form.setDescription(txtDesc.getText());
			form.setTableName(txtTable.getText());
			form.setDatasource(lstDatasource.getSelectedObject());
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

	public boolean isConfirm() {
		return confirm;
	}
	
	
}
