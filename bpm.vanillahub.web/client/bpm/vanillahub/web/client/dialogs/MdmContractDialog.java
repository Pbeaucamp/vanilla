package bpm.vanillahub.web.client.dialogs;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.utils.CustomResources;
import bpm.gwt.workflow.commons.client.I18N.LabelsCommon;
import bpm.mdm.model.supplier.Contract;
import bpm.vanilla.platform.core.beans.ged.DocumentVersion;
import bpm.vanillahub.core.beans.resources.VanillaServer;
import bpm.vanillahub.web.client.I18N.Labels;
import bpm.vanillahub.web.server.ResourcesServiceImpl;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SingleSelectionModel;

public class MdmContractDialog extends AbstractDialogBox {

	private static RepositoryDialogUiBinder uiBinder = GWT.create(RepositoryDialogUiBinder.class);

	interface RepositoryDialogUiBinder extends UiBinder<Widget, MdmContractDialog> {
	}

	@UiField
	SimplePanel panelGrid;
	
	private VanillaServer vanillaServer;
	
	private DataGrid<Contract> dataGrid;
	private ListDataProvider<Contract> dataProvider;
	private SingleSelectionModel<Contract> selectionModel;
	private ListHandler<Contract> sortHandler;

	private List<Contract> contracts;
	
	private boolean confirm;

	public MdmContractDialog(VanillaServer vanillaServer) {
		super("", true, true);
		this.vanillaServer = vanillaServer;
		setWidget(uiBinder.createAndBindUi(this));
		
		createButtonBar(LabelsCommon.lblCnst.Confirmation(), confirmHandler, LabelsCommon.lblCnst.Cancel(), cancelHandler);

		DataGrid<Contract> datagrid = createGridContracts();
		panelGrid.setWidget(datagrid);
		
		refresh();
	}

	private void refresh() {
		ResourcesServiceImpl.Connect.getInstance().getContracts(vanillaServer, new GwtCallbackWrapper<List<Contract>>(this, true, true) {

			@Override
			public void onSuccess(List<Contract> result) {
				loadData(result);
			}
		}.getAsyncCallback());
	}

	private void loadData(List<Contract> result) {
		if (result == null) {
			result = new ArrayList<>();
		}
		this.contracts = result;

		dataProvider.setList(contracts);
		sortHandler.setList(dataProvider.getList());
	}

	private DataGrid<Contract> createGridContracts() {
		TextCell cell = new TextCell();
		Column<Contract, String> colName = new Column<Contract, String>(cell) {

			@Override
			public String getValue(Contract object) {
				if (object.getFileVersions() != null) {
					return object.getFileVersions().getName();
				}
				return Labels.lblCnst.NoDocument();
			}
		};
		colName.setSortable(true);

		Column<Contract, String> colContractName = new Column<Contract, String>(cell) {

			@Override
			public String getValue(Contract object) {
				return object.getName();
			}
		};
		colContractName.setSortable(true);

		Column<Contract, String> colSupplierName = new Column<Contract, String>(cell) {

			@Override
			public String getValue(Contract object) {
				return object.getParent().getName();
			}
		};
		colSupplierName.setSortable(true);

		Column<Contract, String> colFormat = new Column<Contract, String>(cell) {

			@Override
			public String getValue(Contract object) {
				if (object.getFileVersions() != null) {
					return object.getFileVersions().getCurrentVersion(object.getVersionId()).getFormat();
				}
				return Labels.lblCnst.NoDocument();
			}
		};
		colFormat.setSortable(true);

		sortHandler = new ListHandler<Contract>(new ArrayList<Contract>());
		sortHandler.setComparator(colName, new Comparator<Contract>() {

			@Override
			public int compare(Contract o1, Contract o2) {
				if (o1.getFileVersions() == null) {
					return -1;
				}
				else if (o2.getFileVersions() == null) {
					return 1;
				}
				return o1.getFileVersions().getName().compareTo(o2.getFileVersions().getName());
			}
		});
		sortHandler.setComparator(colContractName, new Comparator<Contract>() {

			@Override
			public int compare(Contract o1, Contract o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		sortHandler.setComparator(colSupplierName, new Comparator<Contract>() {

			@Override
			public int compare(Contract o1, Contract o2) {
				return o1.getParent().getName().compareTo(o2.getParent().getName());
			}
		});
		sortHandler.setComparator(colFormat, new Comparator<Contract>() {

			@Override
			public int compare(Contract o1, Contract o2) {
				if (o1.getFileVersions() == null) {
					return -1;
				}
				else if (o2.getFileVersions() == null) {
					return 1;
				}
				
				DocumentVersion lv1 = o1.getFileVersions().getCurrentVersion(o1.getVersionId());
				DocumentVersion lv2 = o2.getFileVersions().getCurrentVersion(o2.getVersionId());

				return lv1.getFormat().compareTo(lv2.getFormat());
			}
		});

		DataGrid.Resources resources = new CustomResources();
		dataGrid = new DataGrid<Contract>(1500, resources);
		dataGrid.setSize("100%", "100%");
		dataGrid.addColumn(colName, LabelsCommon.lblCnst.Name());
		dataGrid.setColumnWidth(colName, "250px");
		dataGrid.addColumn(colContractName, Labels.lblCnst.Contract());
		dataGrid.setColumnWidth(colContractName, "300px");
		dataGrid.addColumn(colSupplierName, Labels.lblCnst.Supplier());
		dataGrid.addColumn(colFormat, Labels.lblCnst.Format());
		dataGrid.setColumnWidth(colFormat, "100px");
		dataGrid.addColumnSortHandler(sortHandler);

		dataProvider = new ListDataProvider<Contract>();
		dataProvider.addDataDisplay(dataGrid);

		this.selectionModel = new SingleSelectionModel<Contract>();
		dataGrid.setSelectionModel(selectionModel);

		return dataGrid;
	}

	private ClickHandler cancelHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			hide();
		}
	};
	
	private ClickHandler confirmHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			confirm = true;			
			hide();
		}
	};
	
	public boolean isConfirm() {
		return confirm;
	}
	
	public Contract getSelectedItem() {
		return selectionModel.getSelectedObject();
	}
}
