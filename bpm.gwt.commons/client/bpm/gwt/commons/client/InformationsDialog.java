package bpm.gwt.commons.client;

import bpm.gwt.commons.client.dialog.AbstractDialogBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class InformationsDialog extends AbstractDialogBox {
	
	private static InformationsDialogUiBinder uiBinder = GWT
			.create(InformationsDialogUiBinder.class);

	interface InformationsDialogUiBinder extends
			UiBinder<Widget, InformationsDialog> {
	}
	
	interface MyStyle extends CssResource {
		String errorContent();
	}
	
	@UiField
	HTMLPanel contentPanel;
	
	@UiField
	MyStyle style;
	
	@UiField
	HTML lblMessage;
	
	@UiField
	SimplePanel panelError;

	private boolean confirm = false;

	public InformationsDialog(String title, String labelOk, String labelCancel, String message, boolean needConfirm) {
		super(title, false, true);
		setWidget(uiBinder.createAndBindUi(this));
		
		increaseZIndex();
		
		if(needConfirm){
			createButton(labelCancel, cancelHandler);
		}
		createButton(labelOk, okHandler);
		
		lblMessage.setHTML(message);
		panelError.setVisible(false);
	}

	public InformationsDialog(String title, String labelOk, String labelCancel, SafeHtml message, boolean needConfirm) {
		super(title, false, true);
		setWidget(uiBinder.createAndBindUi(this));
		
		increaseZIndex();
		
		if(needConfirm){
			createButton(labelCancel, cancelHandler);
		}
		createButton(labelOk, okHandler);
		
		lblMessage.setHTML(message);
		panelError.setVisible(false);
	}
	
	public InformationsDialog(String title, String labelOk, String errorDetails, String message, Exception caught) {
		super(title, false, false);
		setWidget(uiBinder.createAndBindUi(this));
		
		lblMessage.setHTML(message);

		if(caught != null){
			buildErrorContent(errorDetails, caught.getMessage());
		}
		else {
			panelError.setVisible(false);
		}
	}
	
	public InformationsDialog(String title, String labelOk, String errorDetails, String message, Throwable caught) {
		super(title, false, false);
		setWidget(uiBinder.createAndBindUi(this));
		
		lblMessage.setText(message);

		if(caught != null){
			buildErrorContent(errorDetails, caught.getMessage());
		}
		else {
			panelError.setVisible(false);
		}
	}
	
	public void buildErrorContent(String errorDetails, String errorMessage){
		HTMLPanel panelContent = new HTMLPanel(errorMessage);
		panelContent.addStyleName(style.errorContent());
		
		DisclosurePanel discloPanel = new DisclosurePanel(errorDetails);
		discloPanel.setAnimationEnabled(true);
		discloPanel.setContent(panelContent);
		
		panelError.setWidget(discloPanel);
	}

	public void setWidth(int width) {
		contentPanel.setWidth(width + "px");
	}
	
	public boolean isConfirm(){
		return confirm;
	}
	
	public ClickHandler cancelHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			confirm = false;
			
			hide();
		}
	};
	
	public ClickHandler okHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			confirm = true;

			hide();
		}
	};
}
