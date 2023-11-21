package bpm.gwt.commons.client.viewer.comments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bpm.gwt.commons.client.InformationsDialog;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.custom.ListBox;
import bpm.gwt.commons.client.images.CommonImages;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.services.ReportingService;
import bpm.gwt.commons.client.utils.VanillaCSS;
import bpm.gwt.commons.client.viewer.IReportViewer;
import bpm.gwt.commons.client.viewer.widget.ICanExpand;
import bpm.gwt.commons.shared.viewer.CommentInformations;
import bpm.gwt.commons.shared.viewer.CommentRestitutionInformations;
import bpm.gwt.commons.shared.viewer.CommentValidationInformations;
import bpm.gwt.commons.shared.viewer.CommentsInformations;
import bpm.gwt.commons.shared.viewer.LaunchReportInformations;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.comments.CommentDefinition;
import bpm.vanilla.platform.core.beans.comments.CommentDefinition.TypeComment;
import bpm.vanilla.platform.core.beans.comments.CommentValue;
import bpm.vanilla.platform.core.beans.validation.UserValidation;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class CommentDisplayPanel extends Composite implements ICanExpand {

	private static CommentDisplayPanelUiBinder uiBinder = GWT.create(CommentDisplayPanelUiBinder.class);

	interface CommentDisplayPanelUiBinder extends UiBinder<Widget, CommentDisplayPanel> {
	}

	interface MyStyle extends CssResource {
		String imgExpend();

		String button();

		String btnSizeUno();

		String btnSizeDuo();

		String buttonDisable();

		String fullPanel();
	}

	@UiField
	HTMLPanel parentPanel, panelBtn;

	@UiField
	SimplePanel mainPanel;

	@UiField
	ListBox<CommentDefinition> lstCommentComposant;
	
//	@UiField
//	com.google.gwt.user.client.ui.ListBox lstCommentComposant;

	@UiField
	Button btnManageComment, btnAdminValidation, btnAdminUnvalidation;

	@UiField
	Image imgExpend;

	@UiField
	MyStyle style;

	private IReportViewer viewer;
	private LaunchReportInformations itemInfo;
	private User user;

	private HashMap<Integer, Widget> commentPanels = new HashMap<Integer, Widget>();

	private boolean isExpend = true;
	private boolean canComment, canModify, isLastCommentUnvalidate;

	public CommentDisplayPanel(IReportViewer viewer, User user) {
		initWidget(uiBinder.createAndBindUi(this));
		this.viewer = viewer;
		this.user = user;

		parentPanel.addStyleName(VanillaCSS.TAB_TOOLBAR);
	}

	public void buildContent(LaunchReportInformations itemInfo) {
		this.itemInfo = itemInfo;

		if (itemInfo.getCommentsInformations() == null || itemInfo.getCommentsInformations().getComments().isEmpty()) {
			if (isExpend) {
				viewer.expendComment();
				setImageExpendVisible(false);
			}
		}
		else {
			if (!isExpend) {
				viewer.expendComment();
				setImageExpendVisible(false);
			}

			if (itemInfo.getCommentsInformations().getTypeComment() == TypeComment.VALIDATION) {

				boolean canSeeComments = false;
				if (itemInfo.getValidation() != null) {
					List<UserValidation> commentators = itemInfo.getValidation().getCommentators();
					List<UserValidation> validators = itemInfo.getValidation().getValidators();

					canSeeComments = itemInfo.getValidation().getAdminUserId() == user.getId();

					if (!canSeeComments) {
						for (UserValidation comment : commentators) {
							if (comment.getUserId() == user.getId()) {
								canSeeComments = true;
								break;
							}
						}
					}

					if (!canSeeComments) {
						for (UserValidation validator : validators) {
							if (validator.getUserId() == user.getId()) {
								canSeeComments = true;
								break;
							}
						}
					}
				}

				if (canSeeComments) {
					refreshCommentsValidation(itemInfo.getCommentsInformations());
				}
				else {
					viewer.expendComment();
					setImageExpendVisible(false);
				}
			}
			else if (itemInfo.getCommentsInformations().getTypeComment() == TypeComment.RESTITUTION) {
				refreshCommentsRestitution(itemInfo.getCommentsInformations());
			}
		}
	}

	private void refreshCommentsValidation(CommentsInformations commentsInformations) {
		commentPanels = new HashMap<Integer, Widget>();
		lstCommentComposant.clear();

		boolean first = true;
		boolean canComment = false;
		boolean canModify = false;
		boolean isLastCommentUnvalidate = false;
		boolean canValidate = commentsInformations.canValidate();
		boolean isAdmin = commentsInformations.isAdmin();
		for (CommentInformations comment : commentsInformations.getComments()) {
			if (comment instanceof CommentValidationInformations) {
				lstCommentComposant.addItem(comment.getDefinition().getLabel(), comment.getDefinition());
//				lstCommentComposant.addItem(comment.getDefinition().getLabel(), comment.getDefinition().getId() + "");

				if (((CommentValidationInformations) comment).canComment()) {
					canComment = true;
				}

				if (((CommentValidationInformations) comment).canModify()) {
					canModify = true;
				}

				if (((CommentValidationInformations) comment).isLastCommentUnvalidate()) {
					isLastCommentUnvalidate = true;
				}

				NewCommentPanel newCommentPanel = new NewCommentPanel((CommentValidationInformations) comment, user, itemInfo.getGroupParameters());
				commentPanels.put(comment.getDefinition().getId(), newCommentPanel);
				if (first) {
					mainPanel.setWidget(newCommentPanel);
					first = false;
				}
			}
		}

		boolean enableBtn = canComment || canModify;
		boolean enableAdminBtn = isAdmin || canValidate;
		String validationLabel = canComment ? LabelsConstants.lblCnst.SaveComments() : LabelsConstants.lblCnst.Modify();
		updateValidationButton(validationLabel, enableBtn, enableAdminBtn);

		this.canComment = canComment;
		this.canModify = canModify;
		this.isLastCommentUnvalidate = isLastCommentUnvalidate;
	}

	private void updateValidationButton(String validationLabel, boolean enabled, boolean enabledAdmin) {
		btnManageComment.setText(validationLabel);
		btnManageComment.setVisible(enabled);

		btnAdminValidation.setVisible(enabledAdmin);
		btnAdminUnvalidation.setVisible(enabledAdmin);
	}

	private void refreshCommentsRestitution(CommentsInformations commentsInformations) {
		lstCommentComposant.clear();

		boolean first = true;
		for (CommentInformations comment : commentsInformations.getComments()) {
			if (comment instanceof CommentRestitutionInformations) {
				lstCommentComposant.addItem(comment.getDefinition().getLabel(), comment.getDefinition());
//				lstCommentComposant.addItem(comment.getDefinition().getLabel(), comment.getDefinition().getId() + "");

				CommentsRestitutionPanel commentsPanel = new CommentsRestitutionPanel(viewer, (CommentRestitutionInformations) comment, user, itemInfo.getGroupParameters());
				commentPanels.put(comment.getDefinition().getId(), commentsPanel);
				if (first) {
					mainPanel.setWidget(commentsPanel);
					first = false;
				}
			}
		}

		mainPanel.addStyleName(style.fullPanel());
		panelBtn.setVisible(false);
	}

	@UiHandler("lstCommentComposant")
	public void onCommentSelected(ChangeEvent event) {
		CommentDefinition def = lstCommentComposant.getSelectedItem();
		Widget selectedPanel = commentPanels.get(def.getId());
		
//		int commentDefinitionId = Integer.parseInt(lstCommentComposant.getValue(lstCommentComposant.getSelectedIndex()));
//		Widget selectedPanel = commentPanels.get(commentDefinitionId);
		mainPanel.setWidget(selectedPanel);
	}

	@UiHandler("btnManageComment")
	public void onValidationClick(ClickEvent event) {
		if (canComment) {
			boolean allCommentValid = true;

			final List<CommentValue> commentValues = new ArrayList<CommentValue>();
			for (Widget widget : commentPanels.values()) {
				if (widget instanceof NewCommentPanel) {
					NewCommentPanel commentPanel = (NewCommentPanel) widget;
					if (commentPanel.canComment()) {
						if (!commentPanel.isValid()) {
							allCommentValid = false;
						}

						commentValues.add(commentPanel.getCommentValue());
					}
				}
			}

			if (allCommentValid) {
				addComments(commentValues);
			}
			else {
				final InformationsDialog dial = new InformationsDialog(LabelsConstants.lblCnst.Information(), LabelsConstants.lblCnst.Confirmation(), LabelsConstants.lblCnst.Cancel(), LabelsConstants.lblCnst.SaveCommentsConfirm(), true);
				dial.addCloseHandler(new CloseHandler<PopupPanel>() {

					@Override
					public void onClose(CloseEvent<PopupPanel> event) {
						if (dial.isConfirm()) {
							addComments(commentValues);
						}
					}
				});
				dial.center();
			}
		}
		else if (canModify) {
			boolean allCommentValid = true;
			
			final List<CommentValue> commentValues = new ArrayList<CommentValue>();
			for (Widget widget : commentPanels.values()) {
				if (widget instanceof NewCommentPanel) {
					NewCommentPanel commentPanel = (NewCommentPanel) widget;
					if (commentPanel.canComment() || commentPanel.canModify()) {
						if (!commentPanel.isValid()) {
							allCommentValid = false;
						}

						commentValues.add(commentPanel.getCommentValue());
					}
				}
			}
			
			if (allCommentValid) {
				modifyComments(commentValues);
			}
			else {
				final InformationsDialog dial = new InformationsDialog(LabelsConstants.lblCnst.Information(), LabelsConstants.lblCnst.Confirmation(), LabelsConstants.lblCnst.Cancel(), LabelsConstants.lblCnst.SaveCommentsConfirm(), true);
				dial.addCloseHandler(new CloseHandler<PopupPanel>() {

					@Override
					public void onClose(CloseEvent<PopupPanel> event) {
						if (dial.isConfirm()) {
							modifyComments(commentValues);
						}
					}
				});
				dial.center();
			}
		}
	}

	private void addComments(List<CommentValue> commentValues) {
		viewer.showWaitPart(true);

		ReportingService.Connect.getInstance().addComments(itemInfo.getItem().getItem(), itemInfo.getValidation(), commentValues, new GwtCallbackWrapper<CommentsInformations>(viewer, true) {

			@Override
			public void onSuccess(CommentsInformations result) {
				itemInfo.setCommentsInformations(result);

				refreshCommentsValidation(result);
			}
		}.getAsyncCallback());
	}

	private void modifyComments(List<CommentValue> commentValues) {
		viewer.showWaitPart(true);

		ReportingService.Connect.getInstance().modifyComments(itemInfo.getItem().getItem(), itemInfo.getValidation(), commentValues, isLastCommentUnvalidate, new GwtCallbackWrapper<CommentsInformations>(viewer, true) {

			@Override
			public void onSuccess(CommentsInformations result) {
				itemInfo.setCommentsInformations(result);

				refreshCommentsValidation(result);
			}
		}.getAsyncCallback());
	}

	@UiHandler("btnAdminValidation")
	public void onAdminValidationClick(ClickEvent event) {
		final InformationsDialog dial = new InformationsDialog(LabelsConstants.lblCnst.Information(), LabelsConstants.lblCnst.Confirmation(), LabelsConstants.lblCnst.Cancel(), LabelsConstants.lblCnst.ValidateConfirm(), true);
		dial.addCloseHandler(new CloseHandler<PopupPanel>() {

			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				if (dial.isConfirm()) {
					viewer.showWaitPart(true);

					ReportingService.Connect.getInstance().validate(itemInfo.getItem().getItem(), itemInfo.getValidation(), true, new GwtCallbackWrapper<CommentsInformations>(viewer, true) {

						@Override
						public void onSuccess(CommentsInformations result) {
							itemInfo.setCommentsInformations(result);

							refreshCommentsValidation(result);
						}
					}.getAsyncCallback());
				}
			}
		});
		dial.center();
	}

	@UiHandler("btnAdminUnvalidation")
	public void onAdminUnvalidationClick(ClickEvent event) {
		final InformationsDialog dial = new InformationsDialog(LabelsConstants.lblCnst.Information(), LabelsConstants.lblCnst.Confirmation(), LabelsConstants.lblCnst.Cancel(), LabelsConstants.lblCnst.UnvalidateConfirm(), true);
		dial.addCloseHandler(new CloseHandler<PopupPanel>() {

			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				if (dial.isConfirm()) {
					viewer.showWaitPart(true);

					ReportingService.Connect.getInstance().validate(itemInfo.getItem().getItem(), itemInfo.getValidation(), false, new GwtCallbackWrapper<CommentsInformations>(viewer, true) {

						@Override
						public void onSuccess(CommentsInformations result) {
							itemInfo.setCommentsInformations(result);

							refreshCommentsValidation(result);
						}
					}.getAsyncCallback());
				}
			}
		});
		dial.center();
	}

	public boolean isExpend() {
		return isExpend;
	}

	public void setExpend(boolean isExpend) {
		this.isExpend = isExpend;
	}

	public void setImageExpendVisible(boolean visible) {
		this.imgExpend.setVisible(visible);
	}

	public void setImgExpendLeft(int progressValue) {
		DOM.setStyleAttribute(imgExpend.getElement(), "right", progressValue + "px");
	}

	@UiHandler("imgExpend")
	public void onImgExpendClick(ClickEvent event) {
		if (isExpend) {
			viewer.expendComment();
			imgExpend.setResource(CommonImages.INSTANCE.imgCollapse());
		}
		else {
			viewer.expendComment();
			imgExpend.setResource(CommonImages.INSTANCE.imgExpend());
		}
	}
}
