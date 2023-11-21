package bpm.gwt.commons.client.dialog;

import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.services.CommonService;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class FeedbackDialog extends AbstractDialogBox {

	private static AboutDialogUiBinder uiBinder = GWT.create(AboutDialogUiBinder.class);

	interface AboutDialogUiBinder extends UiBinder<Widget, FeedbackDialog> {
	}
	
	interface MyStyle extends CssResource {
		String mainPanel();
	}
	
	@UiField
	MyStyle style;
	
	@UiField
	HTMLPanel contentPanel;
	
	@UiField
	HTML txtAbout;

	public FeedbackDialog() {
		super(LabelsConstants.lblCnst.Information(), false, true);
		setWidget(uiBinder.createAndBindUi(this));
		
		txtAbout.setText(LabelsConstants.lblCnst.WouldYouLikeToContribute());
		
		createButtonBar(LabelsConstants.lblCnst.Yes(), confirmHandler, LabelsConstants.lblCnst.No(), cancelHandler);
	}
	
	private ClickHandler confirmHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			hide();
			
			CommonService.Connect.getInstance().setFeedback(true, new AsyncCallback<Void>() {
				
				@Override
				public void onSuccess(Void result) { }
				
				@Override
				public void onFailure(Throwable caught) { }
			});
		}
	};
	
	private ClickHandler cancelHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			hide();
			
			CommonService.Connect.getInstance().setFeedback(false, new AsyncCallback<Void>() {
				
				@Override
				public void onSuccess(Void result) { }
				
				@Override
				public void onFailure(Throwable caught) { }
			});
		}
	};
}
