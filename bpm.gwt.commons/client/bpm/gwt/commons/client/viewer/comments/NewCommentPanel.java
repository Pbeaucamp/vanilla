package bpm.gwt.commons.client.viewer.comments;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import bpm.gwt.commons.shared.viewer.CommentInformations;
import bpm.gwt.commons.shared.viewer.CommentValidationInformations;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.comments.CommentDefinition;
import bpm.vanilla.platform.core.beans.comments.CommentParameter;
import bpm.vanilla.platform.core.beans.comments.CommentValue;
import bpm.vanilla.platform.core.beans.comments.CommentValue.CommentStatus;
import bpm.vanilla.platform.core.beans.parameters.VanillaGroupParameter;
import bpm.vanilla.platform.core.beans.parameters.VanillaParameter;
import bpm.vanilla.platform.core.beans.comments.CommentValueParameter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

public class NewCommentPanel extends Composite {

	private static NewCommentPanelUiBinder uiBinder = GWT.create(NewCommentPanelUiBinder.class);

	interface NewCommentPanelUiBinder extends UiBinder<Widget, NewCommentPanel> {
	}

	interface MyStyle extends CssResource {
		String panelCommentValidation();

		String panelCommentRestitution();

		String panelCommentWithParam();

		String panelCommentWithoutParam();
	}

	@UiField
	MyStyle style;

	@UiField
	HTMLPanel panelParam;

	@UiField
	SimplePanel panelTxt;

	@UiField
	TextArea txtValue;

	private CommentInformations comment;
	private User user;

	private List<ParameterComposite> parameterComps;

	private CommentValue currentCommentValue;
	private boolean validation;

	public NewCommentPanel(CommentValidationInformations comment, User user, List<VanillaGroupParameter> groupParameters) {
		initWidget(uiBinder.createAndBindUi(this));
		this.comment = comment;
		this.user = user;
		this.validation = true;

		buildParamPart(comment.getDefinition(), comment.getLastComment(), groupParameters);

		if (comment.getLastComment() != null) {
			txtValue.setText(comment.getLastComment().getValue());
		}

		boolean enabled = comment.canComment() || comment.canModify();
		setEnabled(enabled);

		panelTxt.addStyleName(style.panelCommentValidation());
	}

	public NewCommentPanel(CommentInformations comment, CommentValue commentValue, User user, List<VanillaGroupParameter> groupParameters) {
		initWidget(uiBinder.createAndBindUi(this));
		this.comment = comment;
		this.currentCommentValue = commentValue;
		this.user = user;
		this.validation = false;

		buildParamPart(comment.getDefinition(), commentValue, groupParameters);

		if (commentValue != null) {
			txtValue.setText(commentValue.getValue());
		}

		setEnabled(true);

		panelTxt.addStyleName(style.panelCommentRestitution());
	}

	private void buildParamPart(CommentDefinition commentDefinition, CommentValue commentValue, List<VanillaGroupParameter> groupParameters) {
		if (parameterComps == null) {
			parameterComps = new ArrayList<ParameterComposite>();
		}

		if (commentDefinition.getParameters() != null && !commentDefinition.getParameters().isEmpty()) {
			for (CommentParameter param : commentDefinition.getParameters()) {

				VanillaParameter parameter = null;
				if (groupParameters != null) {
					for (VanillaGroupParameter group : groupParameters) {
						if (group.getParameters() != null) {
							for (VanillaParameter vanillaParam : group.getParameters()) {
								if (vanillaParam.getName().equals(param.getParameterIdentifier())) {
									parameter = vanillaParam;
									break;
								}
							}
						}
					}
				}

				ParameterComposite paramComp = new ParameterComposite(param, commentValue, parameter);
				parameterComps.add(paramComp);
				panelParam.add(paramComp);
			}

			panelTxt.addStyleName(style.panelCommentWithParam());
		}
		else {
			panelTxt.addStyleName(style.panelCommentWithoutParam());
			panelParam.setVisible(false);
		}
	}

	private String getValue() {
		return txtValue.getText();
	}

	public boolean isValid() {
		return !getValue().isEmpty() && paramsAreValid();
	}

	public boolean canComment() {
		return comment instanceof CommentValidationInformations && ((CommentValidationInformations) comment).canComment();
	}

	public boolean canModify() {
		return comment instanceof CommentValidationInformations && ((CommentValidationInformations) comment).canModify();
	}

	private void setEnabled(boolean enabled) {
		txtValue.setEnabled(enabled);
	}

	private boolean paramsAreValid() {
		if (parameterComps != null) {
			for (ParameterComposite comp : parameterComps) {
				if (!comp.isValid()) {
					return false;
				}
			}
		}

		return true;
	}

	public CommentValue getCommentValue() {
		if (validation) {
			CommentValue value = new CommentValue();
			value.setCommentId(comment.getDefinition().getId());
			value.setCreationDate(new Date());
			value.setStatus(CommentStatus.NOT_VALIDATE);
			value.setUserId(user.getId());
			value.setUserName(user.getLogin());
			value.setValue(getValue());

			if (parameterComps != null) {
				for (ParameterComposite comp : parameterComps) {
					CommentValueParameter valueParam = comp.getValue();
					if (valueParam != null) {
						value.addCommentValueParameter(valueParam);
					}
				}
			}

			return value;
		}
		else {
			CommentValue value = null;
			if (currentCommentValue != null) {
				value = currentCommentValue;
				value.setValue(getValue());
			}
			else {
				value = new CommentValue();
				value.setCommentId(comment.getDefinition().getId());
				value.setCreationDate(new Date());
				value.setStatus(CommentStatus.VALIDATE);
				value.setUserId(user.getId());
				value.setUserName(user.getLogin());
				value.setValue(getValue());

				if (parameterComps != null) {
					for (ParameterComposite comp : parameterComps) {
						CommentValueParameter valueParam = comp.getValue();
						if (valueParam != null) {
							value.addCommentValueParameter(valueParam);
						}
					}
				}
			}

			return value;
		}
	}
}
