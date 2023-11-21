package bpm.fd.web.client.panels.properties;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;

import bpm.fd.core.IComponentOption;
import bpm.fd.core.component.FlourishComponent;
import bpm.gwt.commons.client.custom.CustomCheckbox;

public class FlourishOptionsProperties extends CompositeProperties<IComponentOption> {

	private static FlourishOptionsPropertiesUiBinder uiBinder = GWT.create(FlourishOptionsPropertiesUiBinder.class);

	interface FlourishOptionsPropertiesUiBinder extends UiBinder<Widget, FlourishOptionsProperties> {
	}
	
	@UiField
	CustomCheckbox showBorder;

	public FlourishOptionsProperties(FlourishComponent component) {
		initWidget(uiBinder.createAndBindUi(this));
		
//		showBorder.setValue(component.showBorder());
	}

	@Override
	public void buildProperties(IComponentOption component) {
		FlourishComponent jsp = (FlourishComponent) component;

//		jsp.setShowBorder(showBorder.getValue());
	}

}
