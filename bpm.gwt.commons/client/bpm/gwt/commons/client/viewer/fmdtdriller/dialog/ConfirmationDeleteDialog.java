package bpm.gwt.commons.client.viewer.fmdtdriller.dialog;

import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.loading.IWait;
import bpm.gwt.commons.client.services.FmdtServices;
import bpm.gwt.commons.client.utils.MessageHelper;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ConfirmationDeleteDialog extends AbstractDialogBox {

	private static ConfirmationDeleteDialogUiBinder uiBinder = GWT.create(ConfirmationDeleteDialogUiBinder.class);

	interface ConfirmationDeleteDialogUiBinder extends UiBinder<Widget, ConfirmationDeleteDialog> {
	}

	@UiField
	HTMLPanel contentPanel;

	private IWait waitPanel;
	private int selectedDirectoryItemId;

	public ConfirmationDeleteDialog(IWait waitPanel, String title, String text, int selectedDirectoryItemId) {
		super(title, true, true);
		this.waitPanel = waitPanel;
		this.selectedDirectoryItemId = selectedDirectoryItemId;

		setWidget(uiBinder.createAndBindUi(this));

		Label lbl = new Label(text);

		VerticalPanel mainPanel = new VerticalPanel();
		mainPanel.setPixelSize(250, 100);
		mainPanel.add(lbl);

		contentPanel.add(mainPanel);
		createButton(LabelsConstants.lblCnst.Ok(), okHandler);
	}

	private ClickHandler okHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			waitPanel.showWaitPart(true);

			FmdtServices.Connect.getInstance().delete(selectedDirectoryItemId, new AsyncCallback<Void>() {

				@Override
				public void onFailure(Throwable caught) {
					caught.printStackTrace();

					waitPanel.showWaitPart(false);
				}

				@Override
				public void onSuccess(Void result) {
					waitPanel.showWaitPart(false);

					hide();

					MessageHelper.openMessageDialog(LabelsConstants.lblCnst.DeleteOkTitle(), LabelsConstants.lblCnst.DeleteOk());
				}
			});
		}
	};
}
