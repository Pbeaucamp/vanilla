package bpm.gwt.commons.client.viewer.comments;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SingleSelectionModel;

import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.custom.CustomCheckbox;
import bpm.gwt.commons.client.custom.ListBoxWithButton;
import bpm.gwt.commons.client.custom.v2.Button;
import bpm.gwt.commons.client.custom.v2.GridPanel;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.dialog.UsersSelectionDialog;
import bpm.gwt.commons.client.images.CommonImages;
import bpm.gwt.commons.client.services.CommonService;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.services.ReportingService;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.commons.shared.InfoUser;
import bpm.gwt.commons.shared.viewer.CommentInformations;
import bpm.gwt.commons.shared.viewer.CommentValidationInformations;
import bpm.gwt.commons.shared.viewer.CommentsInformations;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.validation.UserValidation;
import bpm.vanilla.platform.core.beans.validation.Validation;
import bpm.vanilla.platform.core.beans.validation.ValidationCircuit;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class DefineValidationDialog extends AbstractDialogBox {

	private static DefineValidationDialogUiBinder uiBinder = GWT.create(DefineValidationDialogUiBinder.class);

	interface DefineValidationDialogUiBinder extends UiBinder<Widget, DefineValidationDialog> {
	}
	
	interface MyStyle extends CssResource {
		
	}
	
	@UiField
	MyStyle style;
	
	@UiField
	HTMLPanel panelValidation, panelControl, panelChangeCommentator;
	
	@UiField
	ListBoxWithButton<ValidationCircuit> lstCircuits;

	@UiField
	GridPanel<UserValidation> gridComms, gridVals;
	
	@UiField
	CustomCheckbox checkOffline;
	
	@UiField
	Label lblComms, lblVals, lblNextAction, lblStatus;
	
	@UiField
	Button btnNewValidation;
	
	private InfoUser infoUser;
	private RepositoryItem item;
	
	private Validation validation;
	
	private List<User> users;
	private List<UserValidation> commentators, validators;

	public DefineValidationDialog(InfoUser infoUser, RepositoryItem item) {
		super(LabelsConstants.lblCnst.CommentaryValidation(), false, true);
		this.infoUser = infoUser;
		this.item = item;

		setWidget(uiBinder.createAndBindUi(this));

		buildGridComms();
		buildGridVals();
		
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				gridComms.setTopManually(30);
				gridVals.setTopManually(30);
			}
		});
		
		loadValidation();
	}

	private void loadValidation() {
		CommonService.Connect.getInstance().getUsers(new GwtCallbackWrapper<List<User>>(this, true, false) {

			@Override
			public void onSuccess(List<User> result) {
				users = result;
				
				ReportingService.Connect.getInstance().getValidation(item, new GwtCallbackWrapper<CommentsInformations>(DefineValidationDialog.this, false, true) {

					@Override
					public void onSuccess(CommentsInformations result) {
						loadValidation(result);
					}
				}.getAsyncCallback());
			}
		}.getAsyncCallback());
	}
	
	private void loadValidation(CommentsInformations informations) {
		Validation validation = informations != null ? informations.getValidation() : null;
		this.validation = validation;
		if (validation != null && validation.isActif()) {
			loadValidationUsers(validation);
			
			panelValidation.setVisible(false);
			panelControl.setVisible(true);
			
			List<UserValidation> commentators = validation.getCommentators();
			List<UserValidation> validators = validation.getValidators();
			
			if (validation.isValid()) {
				StringBuffer buf = new StringBuffer();
				buf.append(LabelsConstants.lblCnst.ThePreviousProcessHasBeenValidated());
				lblComms.setText(buf.toString());
				
				btnNewValidation.setVisible(true);
			}
			else {
				StringBuffer buf = new StringBuffer();
				buf.append(LabelsConstants.lblCnst.TheProcessusHas() + " " + (commentators != null ? commentators.size() : 0) + " " + LabelsConstants.lblCnst.commentators());
				if (commentators != null && !commentators.isEmpty()) {
					buf.append(" (");
					boolean first = true;
					for (UserValidation user : commentators) {
						if (!first) {
							buf.append(", ");
						}
						first = false;
						buf.append(user.getUser() != null ? user.getUser().getName() : "");
					}
					buf.append(")");
				}
				lblComms.setText(buf.toString());
				
				buf = new StringBuffer();
				buf.append(LabelsConstants.lblCnst.TheProcessusHas() + " " + (validators != null ? validators.size() : 0) + " " + LabelsConstants.lblCnst.validators());
				if (validators != null && !validators.isEmpty()) {
					buf.append(" (");
					boolean first = true;
					for (UserValidation user : validators) {
						if (!first) {
							buf.append(", ");
						}
						first = false;
						buf.append(user.getUser().getName());
					}
					buf.append(")");
				}
				lblVals.setText(buf.toString());
				
				List<CommentInformations> comments = informations.getComments();
				
				buf = new StringBuffer();
				if (comments != null) {
					for (CommentInformations comment : comments) {
						if (comment instanceof CommentValidationInformations) {
							CommentValidationInformations validationInformation = (CommentValidationInformations) comment;
							
							int nextCommentator = validationInformation.getNextCommentator();
							if (nextCommentator > 0) {
								buf.append(LabelsConstants.lblCnst.TheComment() + " " + comment.getDefinition().getLabel() + " " + LabelsConstants.lblCnst.isWaitingFor() + " " +  findUserName(nextCommentator));
							}
							else {
								buf.append(LabelsConstants.lblCnst.TheComment() + " " + comment.getDefinition().getLabel() + " " + LabelsConstants.lblCnst.isComplete());
							}
						}
					}
				}
				else {
					buf.append(LabelsConstants.lblCnst.TheProcessusNeedToBeValidate());
				}
				lblNextAction.setText(buf.toString());
				
				lblStatus.setText(validation.isOffline() ? LabelsConstants.lblCnst.TheReportIsOffline() : "");
				
				if (informations.isAdmin() || informations.canValidate()) {
					for (CommentInformations comment : comments) {
						if (comment instanceof CommentValidationInformations) {
							CommentValidationInformations validationInformation = (CommentValidationInformations) comment;
							
							int nextCommentator = validationInformation.getNextCommentator();
							if (nextCommentator > 0) {
								Button btn = new Button(LabelsConstants.lblCnst.ChangeNextCommentator(), CommonImages.INSTANCE.viewer_burst());
								btn.addClickHandler(changeCommentatorHandler);
								btn.setHeight(30);
								btn.setWidth("245px");
								
								panelChangeCommentator.setVisible(true);
								panelChangeCommentator.add(btn);
								break;
							}
						}
					}
				}
			}
			
			createButton(LabelsConstants.lblCnst.Close(), closeHandler);
		}
		else {
			loadNewValidation(true);
		}
	}
	
	@UiHandler("btnNewValidation")
	public void onNewValidation(ClickEvent event) {
		loadNewValidation(true);
	}
	
	private void loadNewValidation(final boolean refreshUi) {
		ReportingService.Connect.getInstance().getValidationCircuits(new GwtCallbackWrapper<List<ValidationCircuit>>(this, true, true) {

			@Override
			public void onSuccess(List<ValidationCircuit> result) {
				lstCircuits.setList(result != null ? result : new ArrayList<ValidationCircuit>(), true);
				
				if (refreshUi) {
					panelValidation.setVisible(true);
					panelControl.setVisible(false);
					
					createButtonBar(LabelsConstants.lblCnst.Confirmation(), confirmationHandler, LabelsConstants.lblCnst.Cancel(), closeHandler);
				}
			}
		}.getAsyncCallback());
	}

	private String findUserName(int nextCommentator) {
		if (users != null) {
			for (User user : users) {
				if (user.getId() == nextCommentator) {
					return user.getName();
				}
			}
		}
		return LabelsConstants.lblCnst.Unknown();
	}

	private void buildGridComms() {
		gridComms.setActionPanel(LabelsConstants.lblCnst.Commentators());
		
		final SingleSelectionModel<UserValidation> selectionModel = new SingleSelectionModel<UserValidation>();
		gridComms.setSelectionModel(selectionModel);

		TextCell cell = new TextCell();
		Column<UserValidation, String> colName = new Column<UserValidation, String>(cell) {
			@Override
			public String getValue(UserValidation object) {
				return object.getUser().getName();
			}
		};
		
		Column<UserValidation, String> colOrder = new Column<UserValidation, String>(cell) {
			@Override
			public String getValue(UserValidation object) {
				return String.valueOf(object.getUserOrder());
			}
		};

		gridComms.addColumn(LabelsConstants.lblCnst.Name(), colName, null, new Comparator<UserValidation>() {

			@Override
			public int compare(UserValidation o1, UserValidation o2) {
				return gridComms.compare(o1.getUser().getName(), o2.getUser().getName());
			}
		});

		gridComms.addColumn(LabelsConstants.lblCnst.Order(), colOrder, null, null);
		gridComms.setPageVisible(false);
	}

	private void buildGridVals() {
		gridVals.setActionPanel(LabelsConstants.lblCnst.Validators());
		
		final SingleSelectionModel<UserValidation> selectionModel = new SingleSelectionModel<UserValidation>();
		gridVals.setSelectionModel(selectionModel);

		TextCell cell = new TextCell();
		Column<UserValidation, String> colName = new Column<UserValidation, String>(cell) {
			@Override
			public String getValue(UserValidation object) {
				return object.getUser().getName();
			}
		};
		
		gridVals.addColumn(LabelsConstants.lblCnst.Name(), colName, null, new Comparator<UserValidation>() {

			@Override
			public int compare(UserValidation o1, UserValidation o2) {
				return gridVals.compare(o1.getUser().getName(), o2.getUser().getName());
			}
		});
		gridVals.setPageVisible(false);
	}

	private void loadValidationUsers(Validation validation) {
		if (users != null && validation.getCommentators() != null) {
			for (UserValidation commentator : validation.getCommentators()) {
				for (User user : users) {
					if (commentator.getUserId() == user.getId()) {
						commentator.setUser(user);
						break;
					}
				}
			}
		}
		
		if (users != null && validation.getValidators() != null) {
			for (UserValidation validator : validation.getValidators()) {
				for (User user : users) {
					if (validator.getUserId() == user.getId()) {
						validator.setUser(user);
						break;
					}
				}
			}
		}
	}

	private void loadCircuitUsers(ValidationCircuit circuit) {
		if (users != null && circuit.getCommentators() != null) {
			for (UserValidation commentator : circuit.getCommentators()) {
				for (User user : users) {
					if (commentator.getUserId() == user.getId()) {
						commentator.setUser(user);
						break;
					}
				}
			}
		}
		
		if (users != null && circuit.getValidators() != null) {
			for (UserValidation validator : circuit.getValidators()) {
				for (User user : users) {
					if (validator.getUserId() == user.getId()) {
						validator.setUser(user);
						break;
					}
				}
			}
		}
	}
	
	private void loadCommentators(List<UserValidation> commentators) {
		this.commentators = commentators;
		gridComms.loadItems(commentators);
	}
	
	private void loadValidators(List<UserValidation> validators) {
		this.validators = validators;
		gridVals.loadItems(validators);
	}
	
	@UiHandler("lstCircuits")
	public void onCircuitsChange(ChangeEvent event) {
		ValidationCircuit circuit = (ValidationCircuit) lstCircuits.getSelectedObject();

		loadCircuitUsers(circuit);
		
		loadCommentators(circuit.getCommentators());
		loadValidators(circuit.getValidators());
	}

	@UiHandler("lstCircuits")
	public void onCircuitsClick(ClickEvent event) {
		ValidationCircuitManagerDialog dialog = new ValidationCircuitManagerDialog(infoUser);
		dialog.addCloseHandler(new CloseHandler<PopupPanel>() {
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				loadNewValidation(false);
			}
		});
		dialog.center();
	}
	
	private ClickHandler changeCommentatorHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			final UsersSelectionDialog dial = new UsersSelectionDialog(users, false);
			dial.center();
			dial.addCloseHandler(new CloseHandler<PopupPanel>() {

				@Override
				public void onClose(CloseEvent<PopupPanel> event) {
					if (dial.isConfirm()) {
						final User user = dial.getSelectedUser();
						ReportingService.Connect.getInstance().changeValidationNextActor(item, validation, user, new GwtCallbackWrapper<Void>(DefineValidationDialog.this, true, true) {

							@Override
							public void onSuccess(Void result) {
								MessageHelper.openMessageDialog(LabelsConstants.lblCnst.ValidationCircuit(), LabelsConstants.lblCnst.TheNextCommentatorIs() + " " + user.getName());
								
								hide();
							}
						}.getAsyncCallback());
					}
				}
			});
		}
	};

	private ClickHandler confirmationHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			if ((commentators == null || commentators.isEmpty()) && (validators == null || validators.isEmpty())) {
				return;
			}
			
			boolean isOffline = checkOffline.getValue();
			if (isOffline && (validators == null || validators.isEmpty())) {
				MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Error(), LabelsConstants.lblCnst.YouNeedToSelectAValidatorToPutTheReportOffline());
				return;
			}
			
			if (validation == null) {
				validation = new Validation();
			}
			validation.setDateBegin(new Date());
			validation.setItemId(item.getId());
			validation.setAdminUserId(infoUser.getUser().getId());
			validation.setOffline(isOffline);
			validation.setCommentators(commentators);
			validation.setValidators(validators);
			validation.setActif(true);
			validation.setValid(false);
			
			ReportingService.Connect.getInstance().setReportValidation(item, validation, true, new GwtCallbackWrapper<Void>(DefineValidationDialog.this, true, true) {

				@Override
				public void onSuccess(Void result) {
					MessageHelper.openMessageDialog(LabelsConstants.lblCnst.CommentaryValidation(), LabelsConstants.lblCnst.TheProcessusHasStarted());
					
					hide();
				}
			}.getAsyncCallback());
		}
	};

	private ClickHandler closeHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			hide();
		}
	};
}
