package bpm.architect.web.client.workflow;

import bpm.architect.web.client.I18N.Labels;
import bpm.gwt.commons.client.panels.Tab;
import bpm.gwt.commons.client.panels.TabManager;
import bpm.gwt.commons.shared.InfoUser;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Widget;

public class Workflow extends Tab {

	public Workflow(TabManager tabManager, String title, boolean isCloseable) {
		super(tabManager, title, isCloseable);
		// TODO Auto-generated constructor stub
	}

	private static WorkflowUiBinder uiBinder = GWT.create(WorkflowUiBinder.class);

	interface WorkflowUiBinder extends UiBinder<Widget, Workflow> {	
	}

	public Workflow(InfoUser infoUser, TabManager tabManager) {
		super(tabManager, Labels.lblCnst.DataPreparation(), false);
		this.add(uiBinder.createAndBindUi(this));
	//	this.addStyleName(style.mainPanel());
		
	}
	@UiField
	Button button;

	@UiHandler("button")
	void onClick(ClickEvent e) {
		Window.alert("Hello!");
	}

	public void setText(String text) {
		button.setText(text);
	}

	public String getText() {
		return button.getText();
	}

}
