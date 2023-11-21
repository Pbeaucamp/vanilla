package bpm.freematrix.reborn.web.client.dialog;

import java.util.Date;

import bpm.fm.api.model.AlertRaised;
import bpm.fm.api.model.CommentAlert;
import bpm.freematrix.reborn.web.client.i18n.LabelConstants;
import bpm.freematrix.reborn.web.client.services.MetricService;
import bpm.gwt.commons.client.BaseDialogBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

public class ResolveAlertDialog extends BaseDialogBox {

	private static ResolveAlertDialogUiBinder uiBinder = GWT.create(ResolveAlertDialogUiBinder.class);

	interface ResolveAlertDialogUiBinder extends UiBinder<Widget, ResolveAlertDialog> {
	}
	
	@UiField
	HTMLPanel dialog, contentPanel, panelButton;

	@UiField
	Label lblTitle;
	
	@UiField
	TextArea txtComment;
	
	@UiField
	Image btnReduce, btnMax, btnClose;

	private AlertRaised alert;

	private boolean isConfirm;
	
	public ResolveAlertDialog(AlertRaised alert) {
		super(LabelConstants.lblCnst.resolveAlert(), 400, 150, false, true);
		this.alert = alert;
		setWidget(uiBinder.createAndBindUi(this));
		
		applyStyle(dialog, contentPanel);
		
		createButtonBar(LabelConstants.lblCnst.Ok(), okHandler, LabelConstants.lblCnst.Cancel(), cancelHandler);
	}

	private ClickHandler okHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			
			CommentAlert comment = new CommentAlert();
			comment.setComment(txtComment.getText());
			comment.setDate(new Date());
			comment.setResolutionComment(true);
			comment.setRaisedId(alert.getId());
			
			MetricService.Connection.getInstance().addAlertComment(comment, alert, true, "", new AsyncCallback<Void>() {
				
				@Override
				public void onSuccess(Void result) {
					isConfirm = true;
					ResolveAlertDialog.this.hide();
				}
				
				@Override
				public void onFailure(Throwable caught) {
					
				}
			});
		}
	};

	private ClickHandler cancelHandler = new ClickHandler() {	
		@Override
		public void onClick(ClickEvent event) {
			ResolveAlertDialog.this.hide();
		}
	};

	

	public boolean isConfirm() {
		return isConfirm;
	}
}
