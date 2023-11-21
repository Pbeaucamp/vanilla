package bpm.gwt.aklabox.commons.client.forms.aklabox;

import bpm.document.management.core.model.LOV;
import bpm.gwt.aklabox.commons.client.I18N.LabelsConstants;
import bpm.gwt.aklabox.commons.client.services.AklaCommonService;
import bpm.gwt.aklabox.commons.client.utils.ChildDialogComposite;
import bpm.gwt.aklabox.commons.client.utils.DefaultResultDialog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class AddListValue extends ChildDialogComposite {

	private static AddListValueUiBinder uiBinder = GWT.create(AddListValueUiBinder.class);

	interface AddListValueUiBinder extends UiBinder<Widget, AddListValue> {
	}

	@UiField
	TextBox txtName;
	
	@UiField
	Label lblList;
	
	private boolean confirm = false;

	private LOV listOfValues;
	
	public AddListValue(LOV listOfValues) {
		initWidget(uiBinder.createAndBindUi(this));
		this.listOfValues = listOfValues;
		lblList.setText(listOfValues.getValueName());
	}
	
	@UiHandler("saveDoc")
	void onSave(ClickEvent event) {
		
		String value = txtName.getText();
		
				
		AklaCommonService.Connect.getService().addListValue(listOfValues, value, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}

			@Override
			public void onSuccess(Void result) {
				confirm = true;
				new DefaultResultDialog(LabelsConstants.lblCnst.SaveTypeSuccess(), "success").show();
				closeParent();
			}
		});
		
	}

	@UiHandler("cancel")
	void onCancel(ClickEvent event) {
		closeParent();
	}

	public boolean isConfirm() {
		return confirm ;
	}

}
