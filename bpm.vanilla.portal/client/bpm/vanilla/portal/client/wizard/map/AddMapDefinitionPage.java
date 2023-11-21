package bpm.vanilla.portal.client.wizard.map;

import bpm.gwt.commons.client.wizard.IGwtPage;
import bpm.gwt.commons.client.wizard.IGwtWizard;
import bpm.vanilla.portal.client.utils.ToolsGWT;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class AddMapDefinitionPage extends Composite implements IGwtPage {
	private static AddMapDefinitionPageUiBinder uiBinder = GWT.create(AddMapDefinitionPageUiBinder.class);

	interface AddMapDefinitionPageUiBinder extends UiBinder<Widget, AddMapDefinitionPage> {}

	interface MyStyle extends CssResource {
		
	}

	@UiField
	Label lblName, lblDescription, lblType;

	@UiField
	ListBox lstType;
	
	@UiField
	TextBox txtName;
	
	@UiField
	TextArea txtDescription;

	@UiField
	MyStyle style;
	
//	@UiField
//	RadioButton btnSql, btnKml, btnWfs;

	private IGwtWizard parent;
	private int index;

	public AddMapDefinitionPage(IGwtWizard parent, int index) {
		initWidget(uiBinder.createAndBindUi(this));
		this.parent = parent;
		this.index = index;
		lblName.setText(ToolsGWT.lblCnst.Name());
		lblDescription.setText(ToolsGWT.lblCnst.Description());
		lblType.setText(ToolsGWT.lblCnst.Type());
		lstType.addItem("SQL", "SQL");
		lstType.addItem("KML", "KML");
		lstType.addItem("WFS", "WFS");
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
		return txtDescription.getText();
	}

	public void setName(String name) {
		txtName.setText(name);
	}
	
	public void setDescription(String description) {
		txtDescription.setText(description);
	}
	
	@UiHandler("lstType")
	public void onType(ChangeEvent event) {
		((AddMapWizard)parent).setType(lstType.getValue(lstType.getSelectedIndex()));
	}
	
//	@UiHandler("btnSql")
//	public void onSql(ClickEvent event) {
//		((AddMapWizard)parent).setType("SQL");
//	}
//	@UiHandler("btnKml")
//	public void onKml(ClickEvent event) {
//		((AddMapWizard)parent).setType("KML");
//	}
//	@UiHandler("btnWfs")
//	public void onWfs(ClickEvent event) {
//		((AddMapWizard)parent).setType("WFS");
//	}
	
//	public void setKml() {
//		btnSql.setValue(false);
//		btnWfs.setValue(false);
//		btnKml.setValue(true);
//	}
//
//	public void setWfs() {
//		btnSql.setValue(false);
//		btnKml.setValue(false);
//		btnWfs.setValue(true);
//	}

	public void setType(String type) {
		for(int i = 0 ; i < lstType.getItemCount() ; i++) {
			if(lstType.getValue(i).equals(type)) {
				lstType.setSelectedIndex(i);
				break;
			}
		}
		
	}
}
