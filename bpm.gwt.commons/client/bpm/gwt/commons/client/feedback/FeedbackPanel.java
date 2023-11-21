package bpm.gwt.commons.client.feedback;

import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.custom.TextAreaHolderBox;
import bpm.gwt.commons.client.custom.TextHolderBox;
import bpm.gwt.commons.client.loading.IWait;
import bpm.gwt.commons.client.services.CommonService;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.vanilla.platform.core.beans.User;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class FeedbackPanel extends Composite {
	
	private static final int RIGHT_OPEN = 0;
	private static final int RIGHT_CLOSE = -476;

	private static FeedbackPanelUiBinder uiBinder = GWT.create(FeedbackPanelUiBinder.class);

	interface FeedbackPanelUiBinder extends UiBinder<Widget, FeedbackPanel> {
	}

	@UiField
	Label lblMessage;
	
	@UiField
	HTMLPanel mainPanel;

	@UiField
	TextHolderBox txtMail;

	@UiField
	TextAreaHolderBox txtMessage;
	
	@UiField
	CheckBox checkSupport;

	@UiField
	Button btnSubmit;

	private User user;
	private boolean isOpen = false;
	
	private IWait waitPanel;
	
	private int rightClose;

	public FeedbackPanel(IWait waitPanel, User user) {
		buildContent(waitPanel, user, 0);
	}
	
	public FeedbackPanel(IWait waitPanel, User user, int rightMargin) {
		buildContent(waitPanel, user, rightMargin);
	}
	
	private void buildContent(IWait waitPanel, User user, int rightMargin) {
		initWidget(uiBinder.createAndBindUi(this));
		this.waitPanel = waitPanel;
		this.user = user;
		this.rightClose = RIGHT_CLOSE + rightMargin;
		
		mainPanel.getElement().getStyle().setRight(rightClose, Unit.PX);

		if (user.getBusinessMail() != null && !user.getBusinessMail().isEmpty()) {
			txtMail.setVisible(false);
		}
	}

	@UiHandler("lblMessage")
	public void onMessageClick(ClickEvent event) {
		updateUi();
	}

	private void updateUi() {
		if (isOpen) {
			lblMessage.setVisible(true);
			mainPanel.getElement().getStyle().setRight(rightClose, Unit.PX);
		}
		else {
			lblMessage.setVisible(false);
			mainPanel.getElement().getStyle().setRight(RIGHT_OPEN, Unit.PX);
		}
		this.isOpen = !isOpen;
	}

	@UiHandler("btnCancel")
	public void onCancelClick(ClickEvent event) {
		updateUi();
	}

	@UiHandler("btnSubmit")
	public void onSubmitClick(ClickEvent event) {
		String mail = "";
		if (user.getBusinessMail() != null && !user.getBusinessMail().isEmpty()) {
			mail = user.getBusinessMail();
		}
		else {
			mail = txtMail.getText();
		}

		String message = txtMessage.getText();
		
		boolean isSupport = checkSupport.getValue();

		if (mail.isEmpty() || message.isEmpty()) {
			MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Information(), LabelsConstants.lblCnst.AllFieldsAreMandatory());
			return;
		}

		waitPanel.showWaitPart(true);
		
		CommonService.Connect.getInstance().sendFeedBackMessage(mail, message, isSupport, new GwtCallbackWrapper<Void>(waitPanel, true) {

			@Override
			public void onSuccess(Void result) {
				MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Information(), LabelsConstants.lblCnst.SuccessSendFeedback());
				updateUi();
			}
		}.getAsyncCallback());
	}
}
