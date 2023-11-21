package bpm.architect.web.client.dialogs;

import java.util.ArrayList;
import java.util.List;

import bpm.architect.web.client.I18N.Labels;
import bpm.architect.web.client.images.Images;
import bpm.architect.web.client.services.ArchitectService;
import bpm.data.viz.core.preparation.LinkItem;
import bpm.data.viz.core.preparation.LinkItemParam;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.custom.LabelTextBox;
import bpm.gwt.commons.client.custom.ListBoxWithButton;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.dialog.RepositoryDialog;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.services.ReportingService;
import bpm.gwt.commons.client.utils.ButtonImageCell;
import bpm.gwt.commons.client.utils.CustomResources;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.commons.shared.InfoUser;
import bpm.gwt.commons.shared.repository.PortailRepositoryItem;
import bpm.gwt.commons.shared.viewer.LaunchReportInformations;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.data.DataColumn;
import bpm.vanilla.platform.core.beans.parameters.VanillaGroupParameter;
import bpm.vanilla.platform.core.beans.parameters.VanillaParameter;
import bpm.vanilla.platform.core.repository.RepositoryItem;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.google.gwt.view.client.SingleSelectionModel;

public class ReportsDialog extends AbstractDialogBox {

	private static ReportsDialogUiBinder uiBinder = GWT.create(ReportsDialogUiBinder.class);

	interface ReportsDialogUiBinder extends UiBinder<Widget, ReportsDialog> {
	}

	interface MyStyle extends CssResource {
		String imgCell();

		String imgGrid();
		String lblInformation();
		String lstParam();
	}

	@UiField
	MyStyle style;
	
	@UiField
	Image btnAdd;

	@UiField
	SimplePanel panelContent;
	
	@UiField
	LabelTextBox txtCustomName;
	
	@UiField
	HTMLPanel panelItem, panelParameters;

	private ListDataProvider<LinkItem> dataProvider;
	private SingleSelectionModel<LinkItem> selectionModel;
	private List<ListBoxWithButton<DataColumn>> lstParams = new ArrayList<ListBoxWithButton<DataColumn>>();
	
	private List<LinkItem> linkedItems;
	private List<DataColumn> columns;

	private int repositoryId;
	private Group group;
	private boolean confirm;

	public ReportsDialog(InfoUser infoUser, List<LinkItem> linkedItems, List<DataColumn> columns) {
		super(Labels.lblCnst.ItemsLinked(), false, true);
		this.linkedItems = new ArrayList<LinkItem>(linkedItems != null ? linkedItems : new ArrayList<LinkItem>());
		this.repositoryId = infoUser.getRepository().getId();
		this.group = infoUser.getGroup();
		this.columns = columns;

		setWidget(uiBinder.createAndBindUi(this));
		
		createButtonBar(LabelsConstants.lblCnst.Confirmation(), confirmHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);

		DataGrid<LinkItem> grid = buildGrid();
		panelContent.setWidget(grid);
		
		txtCustomName.addKeyUpHandler(customNameHandler);
		
		loadLinkedItems(this.linkedItems);
		updateUi();
	}

	private void loadLinkedItems(List<LinkItem> elements) {
		if (elements == null) {
			elements = new ArrayList<>();
		}

		dataProvider.setList(elements);
	}

	private DataGrid<LinkItem> buildGrid() {
		TextCell txtCell = new TextCell();
		Column<LinkItem, String> nameColumn = new Column<LinkItem, String>(txtCell) {

			@Override
			public String getValue(LinkItem object) {
				return object.getItem().getName();
			}
		};
		
		Column<LinkItem, String> customNameColumn = new Column<LinkItem, String>(txtCell) {

			@Override
			public String getValue(LinkItem object) {
				return object.getCustomName();
			}
		};

		Column<LinkItem, String> typeColumn = new Column<LinkItem, String>(txtCell) {

			@Override
			public String getValue(LinkItem object) {
				switch (object.getItem().getType()) {
				case IRepositoryApi.GTW_TYPE:
					return "ETL";
				case IRepositoryApi.BIW_TYPE:
					return "Workflow";
				case IRepositoryApi.CUST_TYPE:
					if (object.getItem().getSubtype() == IRepositoryApi.BIRT_REPORT_SUBTYPE) {
						return "Birt";
					}
					else if (object.getItem().getSubtype() == IRepositoryApi.FWR_TYPE) {
						return "Web Report";
					}
					break;
				case IRepositoryApi.FASD_TYPE:
					return "Cube";
				default:
					break;
				}
				return Labels.lblCnst.Unknown();
			}
		};

		ButtonImageCell deleteCell = new ButtonImageCell(Images.INSTANCE.ic_delete_black_18dp(), Labels.lblCnst.DeleteLinkedItem(), style.imgGrid());
		Column<LinkItem, String> colDelete = new Column<LinkItem, String>(deleteCell) {

			@Override
			public String getValue(LinkItem object) {
				return "";
			}
		};
		colDelete.setFieldUpdater(new FieldUpdater<LinkItem, String>() {

			@Override
			public void update(int index, final LinkItem object, String value) {
				deleteLinkItem(object);
			}
		});

		dataProvider = new ListDataProvider<LinkItem>(new ArrayList<LinkItem>());
		selectionModel = new SingleSelectionModel<LinkItem>();
		selectionModel.addSelectionChangeHandler(new Handler() {
			
			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
				updateUi();
			}
		});

		DataGrid.Resources resources = new CustomResources();
		DataGrid<LinkItem> dataGrid = new DataGrid<LinkItem>(10000, resources);
		dataGrid.setSize("100%", "100%");
		dataGrid.addColumn(nameColumn, Labels.lblCnst.Name());
		dataGrid.addColumn(customNameColumn, Labels.lblCnst.CustomName());
		dataGrid.addColumn(typeColumn, Labels.lblCnst.Type());
		dataGrid.addColumn(colDelete);
		dataGrid.setColumnWidth(colDelete, "70px");
		dataGrid.setEmptyTableWidget(new Label(Labels.lblCnst.NoLinkedItem()));
		dataGrid.setSelectionModel(selectionModel);

		dataProvider.addDataDisplay(dataGrid);

		return dataGrid;
	}

	private void updateUi() {
		LinkItem item = selectionModel.getSelectedObject();
		
		txtCustomName.setText(item != null ? (item.getCustomName() != null ? item.getCustomName() : item.getItem().getName()) : "");
		txtCustomName.setEnabled(item != null);
		
		List<LinkItemParam> parameters = item != null ? item.getParameters() : null;
		loadParameters(item, parameters, true);
	}
	
	@UiHandler("btnLoadParameters")
	public void onLoadParameters(ClickEvent event) {
		final LinkItem item = selectionModel.getSelectedObject();
		
		if (item != null) {
			PortailRepositoryItem portailItem = new PortailRepositoryItem(item.getItem(), "");
			
			LaunchReportInformations itemInfo = new LaunchReportInformations();
			itemInfo.setItem(portailItem);
			itemInfo.setSelectedGroup(group);
	
			ReportingService.Connect.getInstance().getParameters(itemInfo, new GwtCallbackWrapper<List<VanillaGroupParameter>>(this, true, true) {
	
				@Override
				public void onSuccess(List<VanillaGroupParameter> groupParams) {
					panelParameters.clear();

					List<LinkItemParam> parameters = new ArrayList<LinkItemParam>();
					if (groupParams != null && !groupParams.isEmpty()) {
						for (VanillaGroupParameter group : groupParams) {
							if (group.getParameters() != null) {
								for (VanillaParameter parameter : group.getParameters()) {
									parameters.add(new LinkItemParam(parameter.getName()));
								}
							}
						}
					}
					
					loadParameters(item, parameters, false);
				}
			}.getAsyncCallback());
		}
	}
	
	private void loadParameters(LinkItem item, List<LinkItemParam> parameters, boolean existing) {
		panelParameters.clear();
		
		if (parameters != null) {
			for (final LinkItemParam linkParam : parameters) {
				final ListBoxWithButton<DataColumn> list = new ListBoxWithButton<DataColumn>();
				list.setLabel(linkParam.getParamName());
				list.setList(columns, true);
				list.addStyleName(style.lstParam());
				list.addChangeHandler(new ChangeHandler() {
					
					@Override
					public void onChange(ChangeEvent event) {
						DataColumn column = list.getSelectedObject();
						linkParam.setDataPrepColumnName(column != null ? column.getColumnName() : null);
					}
				});
				
				if (linkParam.getDataPrepColumnName() != null && columns != null) {
					int index = -1;
					
					int i = 0;
					for (DataColumn column : columns) {
						if (column.getColumnName().equals(linkParam.getDataPrepColumnName())) {
							index = i + 1;
						}
						i++;
					}
					
					list.setSelectedIndex(index);
				}
				
				lstParams.add(list);
				panelParameters.add(list);
			}
			
			if (!existing) {
				item.setParameters(parameters);
			}
		}
		else {
			Label lblInformations = new Label(existing ? Labels.lblCnst.PleaseLoadParameters() : Labels.lblCnst.NoParameters());
			lblInformations.setStyleName(style.lblInformation());
			panelParameters.add(lblInformations);
		}
	}

	@UiHandler("btnAdd")
	public void onAddClick(ClickEvent event) {
		final RepositoryDialog dial = new RepositoryDialog(IRepositoryApi.CUST_TYPE);
		dial.center();
		dial.addCloseHandler(new CloseHandler<PopupPanel>() {

			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				if (dial.isConfirm()) {
					final RepositoryItem item = dial.getSelectedItem();
					if (/*item.getType() != IRepositoryApi.FASD_TYPE &&*/ item.getType() != IRepositoryApi.CUST_TYPE) {
						MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Information(), Labels.lblCnst.ThisItemCannotBeLinked());
						return;
					}
					
					if (item.getType() == IRepositoryApi.CUST_TYPE) {
						ArchitectService.Connect.getInstance().getPublicUrl(item.getId(), new GwtCallbackWrapper<String>(ReportsDialog.this, true, true) {

							@Override
							public void onSuccess(String result) {
								if (result == null || result.isEmpty()) {
									MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Information(), Labels.lblCnst.ThisItemDoesNotHaveAPublicURL());
								}
								else {
									saveLinkItem(item, result);
								}
							}
						}.getAsyncCallback());
					}
					else {
						saveLinkItem(item, "");
					}
				}
			}
		});
	}

	private void saveLinkItem(RepositoryItem item, String publicUrl) {
		if (linkedItems == null) {
			this.linkedItems = new ArrayList<LinkItem>();
		}
		this.linkedItems.add(new LinkItem(item, publicUrl, repositoryId, group.getId()));
		dataProvider.refresh();
	}

	private void deleteLinkItem(LinkItem object) {
		if (linkedItems != null) {
			this.linkedItems.remove(object);
		}
		loadLinkedItems(linkedItems);
		selectionModel.clear();
		updateUi();
	}
	
	public boolean isConfirm() {
		return confirm;
	}
	
	public List<LinkItem> getLinkedItems() {
		return linkedItems;
	}
	
	private KeyUpHandler customNameHandler = new KeyUpHandler() {
		
		@Override
		public void onKeyUp(KeyUpEvent event) {
			String customName = txtCustomName.getText();
			
			LinkItem item = selectionModel.getSelectedObject();
			if (item != null) {
				item.setCustomName(customName);
			}
		}
	};
	
	private ClickHandler confirmHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			confirm = true;
			hide();
		}
	};
	
	private ClickHandler cancelHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			hide();
		}
	};
}
