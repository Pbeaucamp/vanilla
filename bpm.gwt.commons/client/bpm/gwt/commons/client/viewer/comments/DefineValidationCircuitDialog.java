package bpm.gwt.commons.client.viewer.comments;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.google.gwt.cell.client.ActionCell.Delegate;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
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
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SingleSelectionModel;

import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.custom.HasActionCell;
import bpm.gwt.commons.client.custom.LabelTextBox;
import bpm.gwt.commons.client.custom.v2.CompositeCellHelper;
import bpm.gwt.commons.client.custom.v2.GridPanel;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.dialog.UsersSelectionDialog;
import bpm.gwt.commons.client.images.CommonImages;
import bpm.gwt.commons.client.services.CommonService;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.services.ReportingService;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.commons.shared.InfoUser;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.validation.UserValidation;
import bpm.vanilla.platform.core.beans.validation.UserValidation.UserValidationType;
import bpm.vanilla.platform.core.beans.validation.ValidationCircuit;

public class DefineValidationCircuitDialog extends AbstractDialogBox {

	private static DefineValidationDialogUiBinder uiBinder = GWT.create(DefineValidationDialogUiBinder.class);

	interface DefineValidationDialogUiBinder extends UiBinder<Widget, DefineValidationCircuitDialog> {
	}
	
	interface MyStyle extends CssResource {
		
	}
	
	@UiField
	MyStyle style;
	
	@UiField
	HTMLPanel toolbarComs, toolbarVals;
	
	@UiField
	LabelTextBox txtName;

	@UiField
	GridPanel<UserValidation> gridComms, gridVals;
	
	private InfoUser infoUser;
	private ValidationCircuit circuit;
	
	private List<User> users;
	private List<UserValidation> commentators, validators;
	
	private boolean isConfirm = false;
	
	private SingleSelectionModel<UserValidation> selectionModelCommentators;

	public DefineValidationCircuitDialog(InfoUser infoUser, ValidationCircuit circuit) {
		super(LabelsConstants.lblCnst.ValidationCircuit(), false, true);
		this.infoUser = infoUser;
		this.circuit = circuit;
		
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
		
		loadUsers(circuit);
	}

	private void loadUsers(final ValidationCircuit circuit) {
		CommonService.Connect.getInstance().getUsers(new GwtCallbackWrapper<List<User>>(this, true, true) {

			@Override
			public void onSuccess(List<User> result) {
				users = result;
				loadCircuit(circuit);
			}
		}.getAsyncCallback());
	}
	
	private void loadCircuit(ValidationCircuit circuit) {
		if (circuit != null) {
			loadCommentators(circuit);
			loadValidators(circuit);
			
			txtName.setText(circuit.getName());
			
			List<UserValidation> commentators = circuit.getCommentators();
			gridComms.loadItems(commentators);
			
			List<UserValidation> validators = circuit.getValidators();
			gridVals.loadItems(validators);
			
			createButtonBar(LabelsConstants.lblCnst.Confirmation(), confirmationHandler, LabelsConstants.lblCnst.Cancel(), closeHandler);
		}
		else {
			createButtonBar(LabelsConstants.lblCnst.Confirmation(), confirmationHandler, LabelsConstants.lblCnst.Cancel(), closeHandler);
		}
	}

//	private String findUserName(int nextCommentator) {
//		if (users != null) {
//			for (User user : users) {
//				if (user.getId() == nextCommentator) {
//					return user.getName();
//				}
//			}
//		}
//		return LabelsConstants.lblCnst.Unknown();
//	}

	private void buildGridComms() {
		gridComms.setActionPanel(LabelsConstants.lblCnst.Commentators());
		
		this.selectionModelCommentators = new SingleSelectionModel<UserValidation>();
		gridComms.setSelectionModel(selectionModelCommentators);

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
		
		HasActionCell<UserValidation> deleteCell = new HasActionCell<UserValidation>(CommonImages.INSTANCE.delete_24(), LabelsConstants.lblCnst.DeleteCommentator(), new Delegate<UserValidation>() {

			@Override
			public void execute(final UserValidation object) {
				removeCommentator(object);
			}
		});
		
		CompositeCellHelper<UserValidation> compCell = new CompositeCellHelper<UserValidation>(deleteCell);
		Column<UserValidation, UserValidation> colAction = new Column<UserValidation, UserValidation>(compCell.getCell()) {
			@Override
			public UserValidation getValue(UserValidation object) {
				return object;
			}
		};

		gridComms.addColumn(LabelsConstants.lblCnst.Name(), colName, null, new Comparator<UserValidation>() {

			@Override
			public int compare(UserValidation o1, UserValidation o2) {
				return gridComms.compare(o1.getUser().getName(), o2.getUser().getName());
			}
		});

		gridComms.addColumn(LabelsConstants.lblCnst.Order(), colOrder, null, null);
		gridComms.addColumn("", colAction, "100px", null);
		gridComms.setPageVisible(false);
	}

	private void removeCommentator(UserValidation user) {
		commentators.remove(user);
		refreshUserOrder();
		gridComms.loadItems(commentators);
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
		
		HasActionCell<UserValidation> deleteCell = new HasActionCell<UserValidation>(CommonImages.INSTANCE.delete_24(), LabelsConstants.lblCnst.DeleteValidator(), new Delegate<UserValidation>() {

			@Override
			public void execute(final UserValidation object) {
				removeValidator(object);
			}
		});
		
		CompositeCellHelper<UserValidation> compCell = new CompositeCellHelper<UserValidation>(deleteCell);
		Column<UserValidation, UserValidation> colAction = new Column<UserValidation, UserValidation>(compCell.getCell()) {
			@Override
			public UserValidation getValue(UserValidation object) {
				return object;
			}
		};

		gridVals.addColumn(LabelsConstants.lblCnst.Name(), colName, null, new Comparator<UserValidation>() {

			@Override
			public int compare(UserValidation o1, UserValidation o2) {
				return gridVals.compare(o1.getUser().getName(), o2.getUser().getName());
			}
		});
		gridVals.addColumn("", colAction, "100px", null);
		gridVals.setPageVisible(false);
	}

	private void removeValidator(UserValidation user) {
		validators.remove(user);
		gridVals.loadItems(validators);
	}
	
	@UiHandler("btnAddCom")
	public void onAddCom(ClickEvent event) {
		addUsers(true);
	}
	
	@UiHandler("btnAddVal")
	public void onAddVal(ClickEvent event) {
		addUsers(false);
	}
	
	private void addUsers(final boolean addCommentator) {
		final UsersSelectionDialog dial = new UsersSelectionDialog(users);
		dial.center();
		dial.addCloseHandler(new CloseHandler<PopupPanel>() {

			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				if (dial.isConfirm()) {
					List<User> selectedUsers = dial.getSelectedUsers();
					if (addCommentator) {
						addCommentators(selectedUsers);
					}
					else {
						addValidators(selectedUsers);
					}
				}
			}
		});
	}
	
	private void addCommentators(List<User> users) {
		if (commentators == null) {
			commentators = new ArrayList<UserValidation>();
		}
		
		for (User user : users) {
			boolean found = false;
			for (UserValidation userValidation : commentators) {
				if (userValidation.getUserId() == user.getId()) {
					found = true;
					break;
				}
			}
			
			if (!found) {
				commentators.add(new UserValidation(user, UserValidationType.COMMENTATOR, commentators.size()));
			}
		}
		gridComms.loadItems(commentators);
	}

	private void loadCommentators(ValidationCircuit circuit) {
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
	}

	private void loadValidators(ValidationCircuit circuit) {
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
	
	private void addValidators(List<User> users) {
		if (validators == null) {
			validators = new ArrayList<UserValidation>();
		}
		
		for (User user : users) {
			boolean found = false;
			for (UserValidation userValidation : validators) {
				if (userValidation.getUserId() == user.getId()) {
					found = true;
					break;
				}
			}
			
			if (!found) {
				validators.add(new UserValidation(user, UserValidationType.VALIDATOR, validators.size()));
			}
		}
		gridVals.loadItems(validators);
	}
	
	@UiHandler("btnDown")
	public void onDown(ClickEvent event) {
		UserValidation column = selectionModelCommentators.getSelectedObject();
		if (column != null) {
			updateOrder(column, true);
		}
	}
	
	@UiHandler("btnUp")
	public void onUp(ClickEvent event) {
		UserValidation column = selectionModelCommentators.getSelectedObject();
		if (column != null) {
			updateOrder(column, false);
		}
	}
	
	private void updateOrder(UserValidation updateCommentator, boolean isDown) {
		if (commentators != null) {
			int index = 0;
			for (int i = 0; i < commentators.size(); i++) {
				UserValidation commentator = commentators.get(i);
				if (commentator.equals(updateCommentator)) {
					index = i;
					break;
				}
			}
			
			int newIndex = isDown ? index + 1 : index - 1;
			
			if (newIndex >= 0 && newIndex <= commentators.size() - 1) {
				commentators.remove(updateCommentator);
					
				if (newIndex >= commentators.size()) {
					commentators.add(updateCommentator);
				}
				else {
					commentators.add(newIndex, updateCommentator);
				}
				
				refreshUserOrder();
				
				gridComms.refresh();
			}
		}
	}
	
	private void refreshUserOrder() {
		for (int i=0; i < commentators.size(); i++) {
			UserValidation commentator = commentators.get(i);
			commentator.setUserOrder(i);
		}
	}
	
	public boolean isConfirm() {
		return isConfirm;
	}

	private ClickHandler confirmationHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			String name = txtName.getText();
			if (name == null || name.isEmpty()) {
				return;
			}
			
			if ((commentators == null || commentators.isEmpty()) && (validators == null || validators.isEmpty())) {
				return;
			}
			
			if (circuit == null) {
				circuit = new ValidationCircuit();
			}
			circuit.setName(name);
			circuit.setUserId(infoUser.getUser().getId());
			circuit.setCommentators(commentators);
			circuit.setValidators(validators);
			
			ReportingService.Connect.getInstance().manageValidationCircuit(circuit, new GwtCallbackWrapper<ValidationCircuit>(DefineValidationCircuitDialog.this, true, true) {

				@Override
				public void onSuccess(ValidationCircuit result) {
					isConfirm = true;
					
					MessageHelper.openMessageDialog(LabelsConstants.lblCnst.CommentaryValidation(), LabelsConstants.lblCnst.TheValidationCircuitHasBeenCreatedOrModified());
					
					hide();
				}
			}.getAsyncCallback());
		}
	};

	private ClickHandler closeHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			isConfirm = false;
			hide();
		}
	};
}
