package bpm.gwt.commons.client.viewer.comments;

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
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SingleSelectionModel;

import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.custom.HasActionCell;
import bpm.gwt.commons.client.custom.v2.CompositeCellHelper;
import bpm.gwt.commons.client.custom.v2.GridPanel;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.images.CommonImages;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.services.ReportingService;
import bpm.gwt.commons.shared.InfoUser;
import bpm.vanilla.platform.core.beans.validation.ValidationCircuit;

public class ValidationCircuitManagerDialog extends AbstractDialogBox {

	private static ValidationCircuitManagerDialogUiBinder uiBinder = GWT.create(ValidationCircuitManagerDialogUiBinder.class);

	interface ValidationCircuitManagerDialogUiBinder extends UiBinder<Widget, ValidationCircuitManagerDialog> {
	}

	interface MyStyle extends CssResource {

	}

	@UiField
	MyStyle style;

	@UiField
	GridPanel<ValidationCircuit> gridCircuits;

	private InfoUser infoUser;
	private List<ValidationCircuit> circuits;

	public ValidationCircuitManagerDialog(InfoUser infoUser) {
		super(LabelsConstants.lblCnst.ValidationCircuit(), false, true);
		this.infoUser = infoUser;

		setWidget(uiBinder.createAndBindUi(this));

		buildGridComms();
		
		createButton(LabelsConstants.lblCnst.Close(), closeHandler);

		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				gridCircuits.setTopManually(30);
			}
		});

		loadCircuits();
	}

	private void loadCircuits() {
		ReportingService.Connect.getInstance().getValidationCircuits(new GwtCallbackWrapper<List<ValidationCircuit>>(this, true, true) {

			@Override
			public void onSuccess(List<ValidationCircuit> result) {
				circuits = result;
				gridCircuits.loadItems(circuits);
			}
		}.getAsyncCallback());
	}

	private void buildGridComms() {
		gridCircuits.setActionPanel(LabelsConstants.lblCnst.ValidationCircuits());

		final SingleSelectionModel<ValidationCircuit> selectionModel = new SingleSelectionModel<ValidationCircuit>();
		gridCircuits.setSelectionModel(selectionModel);

		TextCell cell = new TextCell();
		Column<ValidationCircuit, String> colName = new Column<ValidationCircuit, String>(cell) {
			@Override
			public String getValue(ValidationCircuit object) {
				return object.getName();
			}
		};

		Column<ValidationCircuit, String> nbCommentatorsColumn = new Column<ValidationCircuit, String>(cell) {

			@Override
			public String getValue(ValidationCircuit object) {
				return object.getCommentators() != null ? String.valueOf(object.getCommentators().size()) : "0";
			}
		};

		Column<ValidationCircuit, String> nbValidatorsColumn = new Column<ValidationCircuit, String>(cell) {

			@Override
			public String getValue(ValidationCircuit object) {
				return object.getValidators() != null ? String.valueOf(object.getValidators().size()) : "0";
			}
		};

		HasActionCell<ValidationCircuit> editCell = new HasActionCell<ValidationCircuit>(CommonImages.INSTANCE.edit_24(), LabelsConstants.lblCnst.EditValidationCircuit(), new Delegate<ValidationCircuit>() {

			@Override
			public void execute(final ValidationCircuit object) {
				manageValidationCircuit(object);
			}
		});

		HasActionCell<ValidationCircuit> deleteCell = new HasActionCell<ValidationCircuit>(CommonImages.INSTANCE.delete_24(), LabelsConstants.lblCnst.DeleteValidationCircuit(), new Delegate<ValidationCircuit>() {

			@Override
			public void execute(final ValidationCircuit object) {
				deleteCircuit(object);
			}
		});

		CompositeCellHelper<ValidationCircuit> compCell = new CompositeCellHelper<ValidationCircuit>(editCell, deleteCell);
		Column<ValidationCircuit, ValidationCircuit> colAction = new Column<ValidationCircuit, ValidationCircuit>(compCell.getCell()) {
			@Override
			public ValidationCircuit getValue(ValidationCircuit object) {
				return object;
			}
		};

		gridCircuits.addColumn(LabelsConstants.lblCnst.Name(), colName, null, new Comparator<ValidationCircuit>() {

			@Override
			public int compare(ValidationCircuit o1, ValidationCircuit o2) {
				return gridCircuits.compare(o1.getName(), o2.getName());
			}
		});
		gridCircuits.addColumn(LabelsConstants.lblCnst.NumberOfCommentators(), nbCommentatorsColumn, null, null);
		gridCircuits.addColumn(LabelsConstants.lblCnst.NumberOfValidators(), nbValidatorsColumn, null, null);
		gridCircuits.addColumn("", colAction, "100px", null);
		gridCircuits.setPageVisible(false);
	}

	private void deleteCircuit(ValidationCircuit circuit) {
		ReportingService.Connect.getInstance().deleteValidationCircuit(circuit, new GwtCallbackWrapper<ValidationCircuit>(this, true, true) {

			@Override
			public void onSuccess(ValidationCircuit result) {
				loadCircuits();
			}
		}.getAsyncCallback());
	}

	@UiHandler("btnAddCircuit")
	public void onAddCircuit(ClickEvent event) {
		manageValidationCircuit(null);
	}
	
	private void manageValidationCircuit(ValidationCircuit circuit) {
		final DefineValidationCircuitDialog dial = new DefineValidationCircuitDialog(infoUser, circuit);
		dial.center();
		dial.addCloseHandler(new CloseHandler<PopupPanel>() {

			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				if (dial.isConfirm()) {
					loadCircuits();
				}
			}
		});
	}

	private ClickHandler closeHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			hide();
		}
	};
}
