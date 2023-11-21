package bpm.gwt.commons.client.viewer.comments;

import bpm.vanilla.platform.core.beans.comments.CommentParameter;
import bpm.vanilla.platform.core.beans.comments.CommentValue;
import bpm.vanilla.platform.core.beans.comments.CommentValueParameter;
import bpm.vanilla.platform.core.beans.parameters.VanillaParameter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class ParameterComposite extends Composite {

	private static ParameterCompositeUiBinder uiBinder = GWT.create(ParameterCompositeUiBinder.class);

	interface ParameterCompositeUiBinder extends UiBinder<Widget, ParameterComposite> {
	}

	@UiField
	Label lblParam;

	@UiField
	TextBox txtParam;

	private CommentParameter parameter;

	public ParameterComposite(CommentParameter parameter, CommentValue commentValue, VanillaParameter vanillaParameter) {
		initWidget(uiBinder.createAndBindUi(this));
		this.parameter = parameter;

		lblParam.setText(parameter.getPrompt());

		txtParam.setName(parameter.getParameterIdentifier());
		txtParam.setValue(parameter.getDefaultValue() != null ? parameter.getDefaultValue() : "");

		if (commentValue != null && commentValue.getParameterValues() != null) {
			for (CommentValueParameter valueParam : commentValue.getParameterValues()) {
				if (valueParam.getCommentParamId() == parameter.getId()) {
					txtParam.setValue(valueParam.getValue() != null ? valueParam.getValue() : "");
				}
			}
			txtParam.setEnabled(false);
		}
		else if (vanillaParameter != null && vanillaParameter.getSelectedValues() != null && !vanillaParameter.getSelectedValues().isEmpty()) {
			txtParam.setValue(vanillaParameter.getSelectedValues().get(0) != null ? vanillaParameter.getSelectedValues().get(0) : "");
		}
		txtParam.setEnabled(false);
	}

	public boolean isValid() {
		return !txtParam.getText().isEmpty();
	}

	public CommentValueParameter getValue() {
		CommentValueParameter valueParam = new CommentValueParameter();
		valueParam.setCommentParamId(parameter.getId());
		valueParam.setValue(txtParam.getText());
		return valueParam;
	}

}
