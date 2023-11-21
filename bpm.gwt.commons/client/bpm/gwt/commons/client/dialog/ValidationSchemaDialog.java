package bpm.gwt.commons.client.dialog;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;

import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.custom.ListBoxWithButton;
import bpm.gwt.commons.client.services.CommonService;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.vanilla.platform.core.beans.resources.ClassDefinition;

public class ValidationSchemaDialog extends AbstractDialogBox {

	private static ValidationSchemaDialogUiBinder uiBinder = GWT.create(ValidationSchemaDialogUiBinder.class);

	interface ValidationSchemaDialogUiBinder extends UiBinder<Widget, ValidationSchemaDialog> {
	}
	
	@UiField
	ListBoxWithButton<ClassDefinition> lstSchemas;

	private boolean isConfirm = false;
	
	public ValidationSchemaDialog() {
		super(LabelsConstants.lblCnst.ValidationSchemas(), false, true);

		setWidget(uiBinder.createAndBindUi(this));
		createButtonBar(LabelsConstants.lblCnst.Confirmation(), confirmHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);
		
		loadValidations();
	}

	private void loadValidations() {
		CommonService.Connect.getInstance().loadSchemaValidations(new GwtCallbackWrapper<List<ClassDefinition>>(null, false, false) {

			@Override
			public void onSuccess(List<ClassDefinition> result) {
				for (ClassDefinition validation : result) {
					lstSchemas.addItem(validation.getName(), validation);
				}
			}
		}.getAsyncCallback());
	}
	
	public boolean isConfirm() {
		return isConfirm;
	}
	
	public ClassDefinition getSelectedSchema() {
		return lstSchemas.getSelectedObject();
	}

	private ClickHandler confirmHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			if (lstSchemas.getSelectedObject() == null) {
				return;
			}
			
			isConfirm = true;
			hide();
		}
	};
	
	private ClickHandler cancelHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			isConfirm = false;
			hide();
		}
	};
}
