package bpm.fd.web.client.panels.properties;

import bpm.fd.core.IComponentOption;
import bpm.fd.core.component.CommentComponent;
import bpm.gwt.commons.client.custom.CustomCheckbox;
import bpm.gwt.commons.client.custom.LabelTextBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;

public class CommentOptionsProperties extends CompositeProperties<IComponentOption> {

	private static CommentOptionsPropertiesUiBinder uiBinder = GWT.create(CommentOptionsPropertiesUiBinder.class);

	interface CommentOptionsPropertiesUiBinder extends UiBinder<Widget, CommentOptionsProperties> {
	}
	
	@UiField
	CustomCheckbox chkLimitCommentNumber, chkIsValidation;
	
	@UiField
	LabelTextBox txtCommentNumber;

	public CommentOptionsProperties(CommentComponent component) {
		initWidget(uiBinder.createAndBindUi(this));
		
		chkLimitCommentNumber.setValue(component.isLimitComment());
		txtCommentNumber.setText(component.getLimit() + "");
		chkIsValidation.setValue(component.isValidation());
	}

	@Override
	public void buildProperties(IComponentOption component) {
		CommentComponent comment = (CommentComponent) component;

		comment.setLimitComment(chkLimitCommentNumber.getValue());
		comment.setLimit(Integer.parseInt(txtCommentNumber.getText()));
		comment.setValidation(chkIsValidation.getValue());
	}
}
