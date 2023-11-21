package bpm.vanilla.portal.client.wizard;

import bpm.gwt.commons.client.wizard.IGwtPage;
import bpm.gwt.commons.client.wizard.IGwtWizard;
import bpm.vanilla.portal.client.utils.ToolsGWT;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class AddTaskDefinitionPage extends Composite implements IGwtPage {
	private static AddTaskDefinitionPageUiBinder uiBinder = GWT.create(AddTaskDefinitionPageUiBinder.class);

	interface AddTaskDefinitionPageUiBinder extends UiBinder<Widget, AddTaskDefinitionPage> {}

	interface MyStyle extends CssResource {
		
	}

	@UiField
	Label lblName, lblDefinition;

	@UiField
	TextBox txtName;
	
	@UiField
	TextArea txtDefinition;

	@UiField
	MyStyle style;

	private IGwtWizard parent;
	private int index;

	public AddTaskDefinitionPage(IGwtWizard parent, int index) {
		initWidget(uiBinder.createAndBindUi(this));
		this.parent = parent;
		this.index = index;
		
		lblName.setText(ToolsGWT.lblCnst.Name());
		lblDefinition.setText(ToolsGWT.lblCnst.Description());
	}

	@Override
	public boolean canGoBack() {
		return false;
	}

	@Override
	public boolean canGoFurther() {
		return isComplete() ? true : false;
	}

	@Override
	public int getIndex() {
		return index;
	}

	@Override
	public boolean isComplete() {
		return txtName.getText().equals("") ? false : true;
	}
	
	@UiHandler("txtName")
	public void onNameChange(KeyUpEvent event) {
		parent.updateBtn();
	}

	public String getName() {
		return txtName.getText();
	}
	
	public String getDescription() {
		return txtDefinition.getText();
	}

	public void setName(String name) {
		txtName.setText(name);
	}
	
	public void setDescription(String description) {
		txtDefinition.setText(description);
	}
}
