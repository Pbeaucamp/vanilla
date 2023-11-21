package bpm.architect.web.client.dialogs;

import java.util.ArrayList;
import java.util.List;

import bpm.architect.web.client.I18N.Labels;
import bpm.architect.web.client.images.Images;
import bpm.architect.web.client.panels.ConsultPanel;
import bpm.architect.web.client.services.ArchitectService;
import bpm.gwt.commons.client.InformationsDialog;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.custom.LabelTextBox;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.utils.ButtonImageCell;
import bpm.gwt.commons.client.utils.CustomResources;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.mdm.model.supplier.Contract;
import bpm.mdm.model.supplier.Supplier;
import bpm.vanilla.platform.core.beans.Group;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.RowStyles;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SingleSelectionModel;

public class ContractDialog extends AbstractDialogBox {

	private static RepositorySaveDialogUiBinder uiBinder = GWT.create(RepositorySaveDialogUiBinder.class);

	interface RepositorySaveDialogUiBinder extends UiBinder<Widget, ContractDialog> {
	}

	interface MyStyle extends CssResource {
		String imgGrid();
		String selectedBackground();
	}

	@UiField
	MyStyle style;

	@UiField
	LabelTextBox txtName, txtExternalSource, txtExternalIdentifier;

	@UiField
	Image btnAdd;
	
	@UiField
	HTMLPanel panelSupplier;

	@UiField
	SimplePanel panelContent;

	private ListDataProvider<Supplier> dataProvider;
	private SingleSelectionModel<Supplier> supplierSelectionModel;

	private ConsultPanel parent;
	private Contract contract;
	private List<Group> availableGroups;

	private boolean edit = false;
	
	public ContractDialog(ConsultPanel parent, Contract contract, List<Group> availableGroups) {
		super(LabelsConstants.lblCnst.SaveItem(), false, true);
		this.parent = parent;
		this.contract = contract;
		this.availableGroups = availableGroups;
		this.edit = contract != null;

		setWidget(uiBinder.createAndBindUi(this));

		createButtonBar(LabelsConstants.lblCnst.Confirmation(), confirmHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);

		txtName.setText(contract != null ? contract.getName() : "");
		txtExternalSource.setText(contract != null ? contract.getExternalSource() : "");
		txtExternalIdentifier.setText(contract != null ? contract.getExternalId() : "");

		DataGrid<Supplier> grid = buildGrid();
		panelContent.setWidget(grid);

		refreshSuppliers();
		
		if (edit) {
			btnAdd.setVisible(false);
		}
	}

	public void refreshSuppliers() {
		showWaitPart(true);

		ArchitectService.Connect.getInstance().getSuppliers(new GwtCallbackWrapper<List<Supplier>>(this, true) {

			@Override
			public void onSuccess(List<Supplier> result) {
				loadSuppliers(result);
			}
		}.getAsyncCallback());
	}

	private void loadSuppliers(List<Supplier> suppliers) {
		if (suppliers == null) {
			suppliers = new ArrayList<>();
		}

		dataProvider.setList(suppliers);
		
//		if (contract != null && contract.getParent() != null) {
//			for (Supplier supplier : dataProvider.getList()) {
//				if (supplier.getId() == contract.getParent().getId()) {
//					supplierSelectionModel.setSelected(supplier, true);
//				}
//			}
//		}
	}

	private DataGrid<Supplier> buildGrid() {
		TextCell txtCell = new TextCell();
		Column<Supplier, String> nameColumn = new Column<Supplier, String>(txtCell) {

			@Override
			public String getValue(Supplier object) {
				return object.getName();
			}
		};

		Column<Supplier, String> sourceColumn = new Column<Supplier, String>(txtCell) {

			@Override
			public String getValue(Supplier object) {
				return object.getExternalSource();
			}
		};

		Column<Supplier, String> identifierColumn = new Column<Supplier, String>(txtCell) {

			@Override
			public String getValue(Supplier object) {
				return object.getExternalId();
			}
		};

		ButtonImageCell editCell = new ButtonImageCell(Images.INSTANCE.ic_edit_black_18dp(), Labels.lblCnst.EditSupplier(), style.imgGrid());
		Column<Supplier, String> colEdit = new Column<Supplier, String>(editCell) {

			@Override
			public String getValue(Supplier object) {
				return "";
			}
		};
		colEdit.setFieldUpdater(new FieldUpdater<Supplier, String>() {

			@Override
			public void update(int index, final Supplier object, String value) {
				SupplierDialog dial = new SupplierDialog(ContractDialog.this, object, availableGroups);
				dial.center();
			}
		});

		ButtonImageCell deleteCell = new ButtonImageCell(Images.INSTANCE.ic_delete_black_18dp(), Labels.lblCnst.DeleteSupplier(), style.imgGrid());
		Column<Supplier, String> colDelete = new Column<Supplier, String>(deleteCell) {

			@Override
			public String getValue(Supplier object) {
				return "";
			}
		};
		colDelete.setFieldUpdater(new FieldUpdater<Supplier, String>() {

			@Override
			public void update(int index, final Supplier object, String value) {
				final InformationsDialog dial = new InformationsDialog(LabelsConstants.lblCnst.Information(), LabelsConstants.lblCnst.Confirmation(), LabelsConstants.lblCnst.Cancel(), Labels.lblCnst.ConfirmDeleteLinkedDocument(), true);
				dial.addCloseHandler(new CloseHandler<PopupPanel>() {

					@Override
					public void onClose(CloseEvent<PopupPanel> event) {
						if (dial.isConfirm()) {
							deleteSupplier(object);
						}
					}
				});
				dial.center();
			}
		});

		DataGrid.Resources resources = new CustomResources();
		DataGrid<Supplier> dataGrid = new DataGrid<Supplier>(10000, resources);
		dataGrid.setSize("100%", "100%");
		dataGrid.addColumn(nameColumn, Labels.lblCnst.Name());
		dataGrid.addColumn(sourceColumn, Labels.lblCnst.ExternalSource());
		dataGrid.addColumn(identifierColumn, Labels.lblCnst.ExternalIdentifier());
		dataGrid.addColumn(colEdit);
		dataGrid.setColumnWidth(colEdit, "70px");
		dataGrid.addColumn(colDelete);
		dataGrid.setColumnWidth(colDelete, "70px");
		dataGrid.setEmptyTableWidget(new Label(Labels.lblCnst.NoSupplier()));
		dataGrid.setRowStyles(new RowStyles<Supplier>() {

			@Override
			public String getStyleNames(Supplier supplier, int rowIndex) {
				if (edit && contract.getParent() != null && contract.getParent().getId() == supplier.getId()) {
					return style.selectedBackground();
				}
				return null;
			}
		});
		
		dataProvider = new ListDataProvider<Supplier>(new ArrayList<Supplier>());
		dataProvider.addDataDisplay(dataGrid);

		if (!edit) {
			this.supplierSelectionModel = new SingleSelectionModel<Supplier>();
			dataGrid.setSelectionModel(supplierSelectionModel);
		}
			
		return dataGrid;
	}

	private void deleteSupplier(Supplier object) {
		ArchitectService.Connect.getInstance().removeSupplier(object, new GwtCallbackWrapper<Void>(this, true) {

			@Override
			public void onSuccess(Void result) {
				refreshSuppliers();
			}
		}.getAsyncCallback());
	}

	@UiHandler("btnAdd")
	public void onAddSupplier(ClickEvent event) {
		SupplierDialog dial = new SupplierDialog(this, null, availableGroups);
		dial.center();
	}

	private ClickHandler confirmHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			String name = txtName.getText();
			String externalSource = txtExternalSource.getText();
			String externalId = txtExternalIdentifier.getText();

			if (name.isEmpty()) {
				MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Information(), LabelsConstants.lblCnst.NeedName());
				return;
			}

			Contract contract = ContractDialog.this.contract;
			if (edit) {
				contract.setName(name);
				contract.setExternalId(externalId);
				contract.setExternalSource(externalSource);
			}
			else {
				contract = new Contract(0, name, externalId, externalSource);
				contract.setUserId(parent.getInfoUser().getUser().getId());

				Supplier supplier = supplierSelectionModel.getSelectedObject();

				if (supplier == null) {
					MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Information(), Labels.lblCnst.NeedSelectSupplier());
					return;
				}
				supplier.addContract(contract);
				contract.setParent(supplier);
			}

			showWaitPart(true);

			ArchitectService.Connect.getInstance().saveOrUpdateSupplier(contract.getParent(), null, new GwtCallbackWrapper<Void>(ContractDialog.this, true) {

				@Override
				public void onSuccess(Void result) {
					parent.refresh(null);
					ContractDialog.this.hide();
				}
			}.getAsyncCallback());
		}
	};

	private ClickHandler cancelHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			ContractDialog.this.hide();
		}
	};
}
