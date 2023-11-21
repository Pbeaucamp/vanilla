package bpm.gwt.commons.client.dialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bpm.fm.api.model.FactTable;
import bpm.fm.api.model.HasItemLinked;
import bpm.fm.api.model.Level;
import bpm.fm.api.model.Metric;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.custom.LabelTextBox;
import bpm.gwt.commons.client.custom.ListBoxWithButton;
import bpm.gwt.commons.client.custom.RepositoryItemButton;
import bpm.gwt.commons.client.custom.RepositoryItemButton.RepositoryItemHandler;
import bpm.gwt.commons.client.dialog.SaveItemDialog.IRepositoryManager;
import bpm.gwt.commons.client.services.CommonService;
import bpm.gwt.commons.client.services.FmdtServices;
import bpm.gwt.commons.client.services.FreeMetricService;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.commons.shared.fmdt.metadata.Metadata;
import bpm.gwt.commons.shared.fmdt.metadata.MetadataModel;
import bpm.gwt.commons.shared.fmdt.metadata.MetadataPackage;
import bpm.gwt.commons.shared.fmdt.metadata.MetadataQueries;
import bpm.gwt.commons.shared.fmdt.metadata.MetadataQuery;
import bpm.mdm.model.supplier.Contract;
import bpm.mdm.model.supplier.Supplier;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.data.DataColumn;
import bpm.vanilla.platform.core.beans.data.DatabaseColumn;
import bpm.vanilla.platform.core.beans.data.DatabaseTable;
import bpm.vanilla.platform.core.beans.data.Dataset;
import bpm.vanilla.platform.core.beans.data.Datasource;
import bpm.vanilla.platform.core.beans.data.DatasourceArchitect;
import bpm.vanilla.platform.core.beans.data.DatasourceType;
import bpm.vanilla.platform.core.repository.RepositoryItem;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class CreateETLAxeDialog extends AbstractDialogBox implements RepositoryItemHandler {

	private static DimensionMeasureDialogUiBinder uiBinder = GWT.create(DimensionMeasureDialogUiBinder.class);

	interface DimensionMeasureDialogUiBinder extends UiBinder<Widget, CreateETLAxeDialog> {
	}

	interface MyStyle extends CssResource {
		String imgCell();

		String imgGrid();
		String lstArchitect();
		String defaultWidth();
	}

	@UiField
	MyStyle style;
	
	@UiField
	ListBoxWithButton<TypeDatasource> lstTypeDatasouce;
	
	@UiField
	HTMLPanel panelArchitect, panelMetadata;

	@UiField
	ListBoxWithButton<Supplier> lstSuppliers;
	
	@UiField
	ListBoxWithButton<Contract> lstContracts;
	
	@UiField
	LabelTextBox txtSeparator;
	
	@UiField(provided=true)
	RepositoryItemButton btnMetadata;
	
	@UiField
	ListBoxWithButton<MetadataQuery> lstQuery;

	@UiField
	HTMLPanel panelMapping;
	
	private List<ListColumn> lstColumns;
	
	private String login, password, vanillaUrl;
	
	private Group group;
	private HasItemLinked hasItemLinked;
	
	private IRepositoryManager manager;
	
	private TypeDatasource typeDatasource = TypeDatasource.ARCHITECT;

	public CreateETLAxeDialog(String login, String password, String vanillaUrl, Group group, HasItemLinked hasItemLinked, IRepositoryManager manager) {
		super(LabelsConstants.lblCnst.CreateETL(), false, true);
		btnMetadata = new RepositoryItemButton(this, IRepositoryApi.FMDT_TYPE, null, LabelsConstants.lblCnst.Metadata());
		btnMetadata.setRepositoryItemhandler(this);
		
		this.login = login;
		this.password = password;
		this.vanillaUrl = vanillaUrl;
		this.group = group;
		this.hasItemLinked = hasItemLinked;
		this.manager = manager;
		
		setWidget(uiBinder.createAndBindUi(this));

		createButtonBar(LabelsConstants.lblCnst.Confirmation(), confirmHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);
		
		lstTypeDatasouce.addItem(TypeDatasource.ARCHITECT);
		lstTypeDatasouce.addItem(TypeDatasource.METADATA);
		lstTypeDatasouce.setSelectedObject(TypeDatasource.ARCHITECT);
		
		buildColumns(hasItemLinked);
		loadSuppliers();
	}
	
	@UiHandler("lstTypeDatasouce")
	public void onTypeDatasource(ChangeEvent event) {
		this.typeDatasource = lstTypeDatasouce.getSelectedObject();
		panelArchitect.setVisible(typeDatasource == TypeDatasource.ARCHITECT);
		panelMetadata.setVisible(typeDatasource == TypeDatasource.METADATA);
	}

	private void buildColumns(HasItemLinked hasItemLinked) {
		if (hasItemLinked instanceof Level) {
			Level level = (Level) hasItemLinked;
			
			addListColumn(LabelsConstants.lblCnst.columnId(), level.getColumnId(), false);
			addListColumn(LabelsConstants.lblCnst.columnName(), level.getColumnName(), false);
			addListColumn(LabelsConstants.lblCnst.parentColumnName(), level.getParentColumnId(), true);
			addListColumn(LabelsConstants.lblCnst.geoCol(), level.getGeoColumnId(), true);
		}
		else if (hasItemLinked instanceof Metric) {
			FactTable table = (FactTable) ((Metric) hasItemLinked).getFactTable();
			final String tableName = table.getTableName();
			
			FmdtServices.Connect.getInstance().getDatabaseStructure(table.getDatasource(), true, new GwtCallbackWrapper<List<DatabaseTable>>(this, true, true) {

				@Override
				public void onSuccess(List<DatabaseTable> result) {
					if (result != null && !result.isEmpty()) {
						DatabaseTable table = null;
						for (DatabaseTable t : result) {
							if (t.getName().equals(tableName)) {
								table = t;
								break;
							}
						}
						
						if (table != null) {
							List<DatabaseColumn> columns = table.getColumns();
							if (columns != null) {
								for (DatabaseColumn column : columns) {
									addListColumn(column.getCustomName(), column.getName(), true);
								}
							}
						}
					}
				}
			}.getAsyncCallback());
		}
	}

	private void addListColumn(String columnLabel, String columnName, boolean canBeEmpty) {
		if (lstColumns == null) {
			lstColumns = new ArrayList<ListColumn>();
		}
		
		ListBoxWithButton<DataColumn> lst = new ListBoxWithButton<DataColumn>();
		lst.setLabel(columnLabel);
		lst.addStyleName(style.lstArchitect());
		lst.addStyleName(style.defaultWidth());
		lstColumns.add(new ListColumn(columnName, lst, canBeEmpty));
		panelMapping.add(lst);
	}

	@Override
	public void onItemClick(RepositoryItem item) {
		FmdtServices.Connect.getInstance().openMetadata(item, new GwtCallbackWrapper<Metadata>(this, true, true) {

			@Override
			public void onSuccess(Metadata result) {
				lstQuery.clear();
				if (result != null && result.getModels() != null) {
					for(MetadataModel model : result.getModels()) {
						if (model.getPackages() != null) {
							for (MetadataPackage pack : model.getPackages()) {
								MetadataQueries queries = pack.getQueries();
								lstQuery.setList(queries != null && queries.getQueries() != null ? queries.getQueries() : new ArrayList<MetadataQuery>());
							}
						}
					}
				}
			}
		}.getAsyncCallback());
	}

	private void loadSuppliers() {
		// Recuperation de la liste des suppliers
		CommonService.Connect.getInstance().getSuppliers(new GwtCallbackWrapper<List<Supplier>>(this, true, true) {

			@Override
			public void onFailure(Throwable caught) {
				super.onFailure(caught);
				lstSuppliers.clear();
				lstSuppliers.setEnabled(false);
				
				lstContracts.clear();
				lstContracts.setEnabled(false);
			}
			
			@Override
			public void onSuccess(List<Supplier> result) {
				lstSuppliers.setList(result, true);
				lstSuppliers.setEnabled(true);
				lstContracts.setEnabled(true);
			}
		}.getAsyncCallback());
	}

	@UiHandler("lstSuppliers")
	public void onChangeSupplier(ChangeEvent event) {
		Supplier supplier = lstSuppliers.getSelectedObject();
		loadContract(supplier);
	}
	
	private void loadContract(Supplier supplier) {
		lstContracts.clear();
		if (supplier != null) {
			lstContracts.setList(supplier.getContracts(), true);
		}
	}
	
	@UiHandler("lstContracts")
	public void onChangeContract(ChangeEvent event) {
		Contract contract = lstContracts.getSelectedObject();

		String format = getFormat(contract);
		txtSeparator.setEnabled(format.equalsIgnoreCase("csv"));
	}
	
	private String getFormat(Contract contract) {
		return contract.getFileVersions() != null ? contract.getFileVersions().getCurrentVersion(contract.getVersionId()).getFormat() : "";
	}
	
	@UiHandler("btnLoadColumns")
	public void onRefreshColumns(ClickEvent event) {
		if (typeDatasource == TypeDatasource.ARCHITECT) {
			Contract contract = lstContracts.getSelectedObject();
			
			if (contract != null) {
				String separator = txtSeparator.getText();
				
				String format = getFormat(contract);
				if (!(format.equalsIgnoreCase("csv") || format.equalsIgnoreCase("kml"))) {
					MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Information(), LabelsConstants.lblCnst.TheSupportedFormatsAreXLSXKMLCSV());
					return;
				}
				
				if (format.equalsIgnoreCase("csv") && separator.isEmpty()) {
					MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Information(), LabelsConstants.lblCnst.SeparatorIsMandatoryForCSV());
					return;
				}
				
				DatasourceArchitect datasourceArch = new DatasourceArchitect();
				datasourceArch.setSupplierId(contract.getParent().getId());
				datasourceArch.setContractId(contract.getId());
				datasourceArch.setSeparator(separator);
				datasourceArch.setUrl(vanillaUrl);
				datasourceArch.setUser(login);
				datasourceArch.setPassword(password);
				
				Datasource datasource = new Datasource();
				datasource.setName("Architect Datasource");
				datasource.setType(DatasourceType.ARCHITECT);
				datasource.setObject(datasourceArch);
				
				CommonService.Connect.getInstance().getDatasetArchitectMetadata("Architect Dataset", datasource, new GwtCallbackWrapper<List<DataColumn>>(this, true, true) {	
					@Override
					public void onSuccess(List<DataColumn> result) {
						loadColumns(result);
					}
				}.getAsyncCallback());
			}
		}
		else if (typeDatasource == TypeDatasource.METADATA) {
			MetadataQuery query = lstQuery.getSelectedObject();
			
			if (query != null) {
				MetadataPackage pack = query.getParent().getParent();
				int metadataId = pack.getParent().getParent().getItemId();
				String modelName = pack.getParent().getName();
				String packageName = pack.getName();
				final String queryName = query.getName();
	
				FmdtServices.Connect.getInstance().buildDatasetFromSavedQuery(metadataId, modelName, packageName, queryName, new GwtCallbackWrapper<Dataset>(this, true, true) {
	
					@Override
					public void onSuccess(Dataset dataset) {
						loadColumns(dataset != null ? dataset.getMetacolumns() : null);
					}
				}.getAsyncCallback());
			}
		}
	}

	private void loadColumns(List<DataColumn> columns) {
		if (columns != null && lstColumns != null) {
			for (ListColumn lst : lstColumns) {
				lst.getLst().setList(columns, lst.canBeEmpty());
			}
		}
		else if (lstColumns != null) {
			for (ListColumn lst : lstColumns) {
				lst.getLst().clear();
			}
		}
	}

	private ClickHandler confirmHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			String etlName = "ETL_" + new Object().hashCode();

			HashMap<String, Integer> columnsIndex = new HashMap<String, Integer>();
			if (lstColumns != null) {
				for (ListColumn lst : lstColumns) {
					int columnIndex = lst.getLst().getSelectedIndex();
					if (!lst.canBeEmpty() && columnIndex == -1) {
						return;
					}
					columnsIndex.put(lst.getColumnName(), columnIndex);
				}
			}
			
			if (typeDatasource == TypeDatasource.ARCHITECT) {
				Contract selectedContract = lstContracts.getSelectedObject();
				
				if (selectedContract == null) {
					return;
				}
				
				FreeMetricService.Connect.getInstance().createAxeETL(etlName, hasItemLinked, selectedContract, columnsIndex, new GwtCallbackWrapper<String>(CreateETLAxeDialog.this, true, true) {
	
					@Override
					public void onSuccess(String result) {
						List<Group> groups = new ArrayList<Group>();
						groups.add(group);
						
						RepositoryDirectoryDialog dial = new RepositoryDirectoryDialog(manager, IRepositoryApi.GTW_TYPE, groups, result);
				        dial.center();
				        hide();
					}
				}.getAsyncCallback());
			}
			else if (typeDatasource == TypeDatasource.METADATA) {
				RepositoryItem metadata = btnMetadata.getSelectedItem();
				MetadataQuery query = lstQuery.getSelectedObject();
				
				if (query == null) {
					return;
				}
				
				FreeMetricService.Connect.getInstance().createAxeETL(etlName, hasItemLinked, metadata, query.getName(), columnsIndex, new GwtCallbackWrapper<String>(CreateETLAxeDialog.this, true, true) {
	
					@Override
					public void onSuccess(String result) {
						List<Group> groups = new ArrayList<Group>();
						groups.add(group);
						
						RepositoryDirectoryDialog dial = new RepositoryDirectoryDialog(manager, IRepositoryApi.GTW_TYPE, groups, result);
				        dial.center();
				        hide();
					}
				}.getAsyncCallback());
			}
		}
	};

	private ClickHandler cancelHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			CreateETLAxeDialog.this.hide();
		}
	};
	
	private class ListColumn {
		
		private String columnName;
		private ListBoxWithButton<DataColumn> lst;
		private boolean canBeEmpty;
		
		public ListColumn(String columnName, ListBoxWithButton<DataColumn> lst, boolean canBeEmpty) {
			this.columnName = columnName;
			this.lst = lst;
			this.canBeEmpty = canBeEmpty;
		}
		
		public String getColumnName() {
			return columnName;
		}
		
		public ListBoxWithButton<DataColumn> getLst() {
			return lst;
		}
		
		public boolean canBeEmpty() {
			return canBeEmpty;
		}
	}
	
	private enum TypeDatasource {
		ARCHITECT(LabelsConstants.lblCnst.Architect()),
		METADATA(LabelsConstants.lblCnst.Metadata());
		
		private String name;
		
		private TypeDatasource(String name) {
			this.name = name;
		}
		
		@Override
		public String toString() {
			return name;
		}
	}
}
