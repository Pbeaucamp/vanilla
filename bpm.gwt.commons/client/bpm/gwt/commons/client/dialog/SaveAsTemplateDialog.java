package bpm.gwt.commons.client.dialog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.custom.LabelTextBox;
import bpm.gwt.commons.client.panels.upload.DocumentManager.DocumentUploadHandler;
import bpm.gwt.commons.client.panels.upload.FileUploadWidget;
import bpm.gwt.commons.client.services.CommonService;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.shared.utils.CommonConstants;
import bpm.vanilla.platform.core.beans.VanillaImage;

public class SaveAsTemplateDialog extends AbstractDialogBox implements DocumentUploadHandler {

	private static SaveAsTemplateDialogUiBinder uiBinder = GWT.create(SaveAsTemplateDialogUiBinder.class);

	interface SaveAsTemplateDialogUiBinder extends UiBinder<Widget, SaveAsTemplateDialog> {
	}

	@UiField
	LabelTextBox txtName;
	
	@UiField
	SimplePanel uploadPanel;

	private FileUploadWidget uploadWidget;
	private VanillaImage image;
	
	private TemplateHandler handler;
	
	public SaveAsTemplateDialog(TemplateHandler handler) {
		super(LabelsConstants.lblCnst.SaveAsTemplate(), true, true);
		this.handler = handler;

		setWidget(uiBinder.createAndBindUi(this));
		createButtonBar(LabelsConstants.lblCnst.Confirmation(), confirmHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);

		uploadWidget = new FileUploadWidget(CommonConstants.COMMON_UPLOAD_SERVLET, null);
		uploadWidget.setUploadHandler(this);
		uploadPanel.setWidget(uploadWidget);
	}
	
	@Override
	public void onUploadSuccess(Integer documentId, String filename) {
		CommonService.Connect.getInstance().getImage(documentId, new GwtCallbackWrapper<VanillaImage>(this, true, true) {

			@Override
			public void onSuccess(VanillaImage result) {
				image = result;
			}
		}.getAsyncCallback());
	}

	private ClickHandler confirmHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			String name = txtName.getText();
			if (name == null || name.isEmpty()) {
				return;
			}
			
			if (image == null) {
				return;
			}
			
			handler.saveTemplate(name, image);
			
			hide();
		}
	};

	private ClickHandler cancelHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			hide();
		}
	};

	public interface TemplateHandler {

		public void saveTemplate(String name, VanillaImage img);
	}
}
