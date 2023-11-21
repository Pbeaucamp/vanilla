package bpm.vanilla.portal.client.wizard;

import bpm.gwt.commons.client.custom.LabelTextArea;
import bpm.gwt.commons.client.custom.LabelTextBox;
import bpm.gwt.commons.client.utils.VanillaCSS;
import bpm.gwt.commons.client.wizard.IGwtPage;
import bpm.gwt.commons.client.wizard.IGwtWizard;
import bpm.vanilla.platform.core.beans.alerts.Alert.TypeEvent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class AddAlertDefinitionPage extends Composite implements IGwtPage {
	private static AddAlertDefinitionPageUiBinder uiBinder = GWT.create(AddAlertDefinitionPageUiBinder.class);

	interface AddAlertDefinitionPageUiBinder extends UiBinder<Widget, AddAlertDefinitionPage> {}

	interface MyStyle extends CssResource {
		
	}

	@UiField
	ListBox lstTypeEvent;

	@UiField
	LabelTextBox txtName;
	
	@UiField
	LabelTextArea txtDescription;

	@UiField
	MyStyle style;

	private IGwtWizard parent;
	private int index;

	public AddAlertDefinitionPage(IGwtWizard parent, int index) {
		initWidget(uiBinder.createAndBindUi(this));
		this.parent = parent;
		this.index = index;
		lstTypeEvent.addStyleName(VanillaCSS.COMMONS_LISTBOX);
		
		for(TypeEvent ev : TypeEvent.values()){
			lstTypeEvent.addItem(ev.getLabel(), ev.toString());
		}
		
		txtName.addDomHandler(new KeyUpHandler() {
			
			@Override
			public void onKeyUp(KeyUpEvent event) {
				AddAlertDefinitionPage.this.parent.updateBtn();
			}
		}, KeyUpEvent.getType());
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
	
//	@UiHandler("txtName")
//	public void onNameChange(KeyUpEvent event) {
//		parent.updateBtn();
//	}

	public String getName() {
		return txtName.getText();
	}
	
	public String getDescription() {
		return txtDescription.getText();
	}

	public void setName(String name) {
		txtName.setText(name);
	}
	
	public void setDescription(String description) {
		txtDescription.setText(description);
	}
	
	public TypeEvent getTypeEvent() {
		for(TypeEvent ev : TypeEvent.values()){
			if(lstTypeEvent.getValue(lstTypeEvent.getSelectedIndex()).equals(ev.toString())){
				return ev;
			}
		}
		return null;
	}
	
	public void setTypeEvent(TypeEvent typeEvent) {
		for(int i=0; i<lstTypeEvent.getItemCount(); i++){
			if(lstTypeEvent.getValue(i).equals(typeEvent.toString())){
				lstTypeEvent.setSelectedIndex(i);
			}
		}
	}
}
