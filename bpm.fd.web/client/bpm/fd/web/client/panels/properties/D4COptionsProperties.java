package bpm.fd.web.client.panels.properties;

import bpm.fd.core.IComponentOption;
import bpm.fd.core.component.D4CComponent;
import bpm.gwt.commons.client.custom.CustomCheckbox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;

public class D4COptionsProperties extends CompositeProperties<IComponentOption> {

	private static D4COptionsPropertiesUiBinder uiBinder = GWT.create(D4COptionsPropertiesUiBinder.class);

	interface D4COptionsPropertiesUiBinder extends UiBinder<Widget, D4COptionsProperties> {
	}
	
	@UiField
	CustomCheckbox showBorder;

	public D4COptionsProperties(D4CComponent component) {
		initWidget(uiBinder.createAndBindUi(this));
		
//		showBorder.setValue(component.showBorder());
	}

	@Override
	public void buildProperties(IComponentOption component) {
		D4CComponent jsp = (D4CComponent) component;

//		jsp.setShowBorder(showBorder.getValue());
	}

}
