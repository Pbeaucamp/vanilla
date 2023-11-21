package bpm.fd.web.client.panels.properties;

import bpm.fd.core.IComponentOption;
import bpm.fd.core.component.JspComponent;
import bpm.gwt.commons.client.custom.CustomCheckbox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;

public class JspOptionsProperties extends CompositeProperties<IComponentOption> {

	private static JspOptionsPropertiesUiBinder uiBinder = GWT.create(JspOptionsPropertiesUiBinder.class);

	interface JspOptionsPropertiesUiBinder extends UiBinder<Widget, JspOptionsProperties> {
	}
	
	@UiField
	CustomCheckbox showBorder;

	public JspOptionsProperties(JspComponent component) {
		initWidget(uiBinder.createAndBindUi(this));
		
		showBorder.setValue(component.showBorder());
	}

	@Override
	public void buildProperties(IComponentOption component) {
		JspComponent jsp = (JspComponent) component;

		jsp.setShowBorder(showBorder.getValue());
	}

}
