package bpm.gwt.commons.client.dialog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.panels.upload.FileUploadWidget;
import bpm.gwt.commons.shared.utils.CommonConstants;

public class UploadDialog extends AbstractDialogBox {

	private static UploadDialogUiBinder uiBinder = GWT.create(UploadDialogUiBinder.class);

	interface UploadDialogUiBinder extends UiBinder<Widget, UploadDialog> {
	}
	
//	@UiField
//	LabelTextBox txtName;

	@UiField
	SimplePanel mainPanel;

	public UploadDialog(String title) {
		this(title, CommonConstants.COMMON_UPLOAD_SERVLET);
	}

	public UploadDialog(String title, String servlet) {
		super(title, true, true);

		setWidget(uiBinder.createAndBindUi(this));
		createButton(LabelsConstants.lblCnst.Close(), closeHandler);

		mainPanel.setWidget(new FileUploadWidget(servlet, null));
	}

	private ClickHandler closeHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			hide();
		}
	};
}
