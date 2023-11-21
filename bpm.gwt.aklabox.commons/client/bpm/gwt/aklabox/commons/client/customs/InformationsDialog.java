package bpm.gwt.aklabox.commons.client.customs;

import bpm.gwt.aklabox.commons.client.dialogs.AbstractDialogBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class InformationsDialog extends AbstractDialogBox {
	private static final String CSS_ERROR_CONTENT = "errorContent";

	private static InformationsDialogUiBinder uiBinder = GWT
			.create(InformationsDialogUiBinder.class);

	interface InformationsDialogUiBinder extends
			UiBinder<Widget, InformationsDialog> {
	}
	
	@UiField
	HTML lblMessage;
	
	@UiField
	SimplePanel panelError;

	private boolean confirm = false;

	public InformationsDialog(String title, String labelOk, String labelCancel, String message, boolean needConfirm) {
		super(title, false, true);
		setWidget(uiBinder.createAndBindUi(this));
		
		lblMessage.setHTML(message);
		panelError.setVisible(false);

		if(needConfirm){
			createButton(labelCancel, cancelHandler);
		}
		createButton(labelOk, okHandler);
	}
	
	public InformationsDialog(String title, String labelOk, String errorDetails, String message, Exception caught) {
		super(title, false, true);
		setWidget(uiBinder.createAndBindUi(this));
		
		lblMessage.setHTML(message);

		createButton(labelOk, okHandler);
		if(caught != null){
			buildErrorContent(errorDetails, caught.getMessage());
		}
		else {
			panelError.setVisible(false);
		}
	}
	
	public InformationsDialog(String title, String labelOk, String errorDetails, String message, Throwable caught) {
		super(title, false, true);
		setWidget(uiBinder.createAndBindUi(this));
		
		lblMessage.setText(message);

		createButton(labelOk, okHandler);
		if(caught != null){
			buildErrorContent(errorDetails, caught.getMessage());
		}
		else {
			panelError.setVisible(false);
		}
	}
	
	public void buildErrorContent(String errorDetails, String errorMessage){
		HTMLPanel panelContent = new HTMLPanel(errorMessage);
		panelContent.addStyleName(CSS_ERROR_CONTENT);
		
		DisclosurePanel discloPanel = new DisclosurePanel(errorDetails);
		discloPanel.setAnimationEnabled(true);
		discloPanel.setContent(panelContent);
		
		panelError.setWidget(discloPanel);
	}
	
	public boolean isConfirm(){
		return confirm;
	}
	
	public ClickHandler cancelHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			confirm = false;
			
			InformationsDialog.this.hide();
		}
	};
	
	public ClickHandler okHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			confirm = true;

			InformationsDialog.this.hide();
		}
	};

	@Override
	public int getThemeColor() {
		return 0;
	}
}
