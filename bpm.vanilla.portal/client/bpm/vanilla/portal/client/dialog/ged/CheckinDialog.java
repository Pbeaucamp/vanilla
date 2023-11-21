package bpm.vanilla.portal.client.dialog.ged;

import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.loading.IWait;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.commons.shared.repository.DocumentVersionDTO;
import bpm.gwt.commons.shared.utils.CommonConstants;
import bpm.vanilla.portal.client.biPortal;
import bpm.vanilla.portal.client.panels.center.DocumentManagerPanel;
import bpm.vanilla.portal.client.utils.ToolsGWT;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class CheckinDialog extends AbstractDialogBox implements IWait {

	private static CheckinDialogUiBinder uiBinder = GWT.create(CheckinDialogUiBinder.class);

	interface CheckinDialogUiBinder extends UiBinder<Widget, CheckinDialog> {
	}

	@UiField
	HTMLPanel contentPanel;

	@UiField
	FormPanel formPanel;

	@UiField
	CheckBox checkIndex;

	@UiField
	FileUpload fileUpload;

	private DocumentManagerPanel parent;
	private DocumentVersionDTO item;

	public CheckinDialog(DocumentManagerPanel parent, DocumentVersionDTO item) {
		super(ToolsGWT.lblCnst.Indexfile(), false, true);
		this.parent = parent;
		this.item = item;

		setWidget(uiBinder.createAndBindUi(this));
		createButtonBar(LabelsConstants.lblCnst.Confirmation(), okHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);

		formPanel.setEncoding(FormPanel.ENCODING_MULTIPART);
		formPanel.setMethod(FormPanel.METHOD_POST);
		formPanel.addSubmitCompleteHandler(submitCompleteHandler);
	}

	private ClickHandler cancelHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			CheckinDialog.this.hide();
		}
	};

	private ClickHandler okHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			if (fileUpload.getFilename() != null && !fileUpload.getFilename().isEmpty()) {
				showWaitPart(true);

				boolean index = checkIndex.getValue();

				formPanel.setAction(GWT.getHostPageBaseURL() + "VanillaPortail/IndexFile?" + CommonConstants.CHECKIN + "=true" + "&" + CommonConstants.DOCUMENT_ID + "=" + item.getDocumentParent().getId() + "&" + CommonConstants.USER_ID + "=" + biPortal.get().getInfoUser().getUser().getId() + "&" + CommonConstants.INDEX + "=" + index);

				formPanel.submit();
			}
			else {
				MessageHelper.openMessageDialog(ToolsGWT.lblCnst.Information(), ToolsGWT.lblCnst.ChooseFileToCheckin());
			}
		}
	};

	private SubmitCompleteHandler submitCompleteHandler = new SubmitCompleteHandler() {

		public void onSubmitComplete(SubmitCompleteEvent event) {
			showWaitPart(false);

			CheckinDialog.this.hide();

			if (parent != null) {
				parent.loadDocument();
			}
		}
	};

	@Override
	public void showWaitPart(boolean visible) {
		biPortal.get().showWaitPart(visible);
	}
}
