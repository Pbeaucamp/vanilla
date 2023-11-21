package bpm.gwt.commons.client.utils.alert;

import bpm.gwt.commons.client.custom.LabelTextBox;
import bpm.vanilla.platform.core.beans.parameters.VanillaParameter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class AlertParameterItem extends Composite {

	private static AlertParameterItemUiBinder uiBinder = GWT.create(AlertParameterItemUiBinder.class);

	interface AlertParameterItemUiBinder extends UiBinder<Widget, AlertParameterItem> {
	}

	interface MyStyle extends CssResource {
		
	}

	@UiField
	MyStyle style;

	@UiField
	LabelTextBox txtItem;

	@UiField
	Label lblItem;

	private VanillaParameter item;

	public AlertParameterItem(VanillaParameter item) {
		initWidget(uiBinder.createAndBindUi(this));
		this.item = item;
		
		initItem();
	}

	private void initItem() {
		lblItem.setText(item.getName());
	}

	public String getValue() {
		return txtItem.getText();
	}
	
	public void setValue(String value){
		txtItem.setText(value);
	}
	
	public VanillaParameter getParameter() {
		return item;
	}
	
	
}