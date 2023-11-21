package bpm.architect.web.client.dialogs;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;

import bpm.architect.web.client.I18N.Labels;
import bpm.architect.web.client.images.Images;
import bpm.architect.web.client.services.ArchitectService;
import bpm.gwt.commons.client.InformationsDialog;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.custom.CheckState;
import bpm.gwt.commons.client.custom.EnableDisableableCheckboxCell;
import bpm.gwt.commons.client.dialog.RepositoryDialog;
import bpm.gwt.commons.client.loading.IWait;
import bpm.gwt.commons.client.services.CommonService;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.utils.ButtonImageCell;
import bpm.gwt.commons.client.utils.CustomResources;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.commons.client.viewer.dialog.ViewerDialog;
import bpm.gwt.commons.shared.InfoUser;
import bpm.gwt.commons.shared.repository.PortailRepositoryItem;
import bpm.mdm.model.supplier.Contract;
import bpm.mdm.model.supplier.DocumentItem;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class ItemLinkedComposite extends Composite {

	private static ItemLinkedCompositeUiBinder uiBinder = GWT.create(ItemLinkedCompositeUiBinder.class);

	interface ItemLinkedCompositeUiBinder extends UiBinder<Widget, ItemLinkedComposite> {
	}

	interface MyStyle extends CssResource {
		String imgCell();

		String imgGrid();
	}

	@UiField
	MyStyle style;
	
	@UiField
	Image btnAdd;

	@UiField
	SimplePanel panelContent;
	
	private IWait waitPanel;

	private ListDataProvider<DocumentItem> dataProvider;

	private InfoUser infoUser;
	private Contract contract;
	private boolean onlyEtl;
	
	private boolean refreshContracts;

	public ItemLinkedComposite(IWait waitPanel, InfoUser infoUser, Contract contract, boolean onlyEtl, boolean showAdd) {
		this.waitPanel = waitPanel;
		this.infoUser = infoUser;
		this.contract = contract;
		this.onlyEtl = onlyEtl;
		
		initWidget(uiBinder.createAndBindUi(this));

		DataGrid<DocumentItem> grid = buildGrid();
		panelContent.setWidget(grid);

		refreshLinkedItems(false);
		
		if (!showAdd) {
			btnAdd.setVisible(false);
		}
	}

	private void refreshLinkedItems(boolean refreshContracts) {
		this.refreshContracts = refreshContracts;

		CommonService.Connect.getInstance().getLinkedItems(contract.getId(), new GwtCallbackWrapper<List<DocumentItem>>(waitPanel, true, true) {

			@Override
			public void onSuccess(List<DocumentItem> result) {
				List<DocumentItem> items = new ArrayList<>();
				if (onlyEtl && result != null) {
					for (DocumentItem item : result) {
						if (item.getItem().getType() == IRepositoryApi.GTW_TYPE) {
							items.add(item);
						}
					}
				}
				else {
					items = result;
				}
				
				loadLinkedItems(items);
			}
		}.getAsyncCallback());
	}

	private void loadLinkedItems(List<DocumentItem> elements) {
		if (elements == null) {
			elements = new ArrayList<>();
		}

		dataProvider.setList(elements);
	}

	private DataGrid<DocumentItem> buildGrid() {
		TextCell txtCell = new TextCell();
		Column<DocumentItem, String> nameColumn = new Column<DocumentItem, String>(txtCell) {

			@Override
			public String getValue(DocumentItem object) {
				return object.getItem().getName();
			}
		};

		Column<DocumentItem, String> typeColumn = new Column<DocumentItem, String>(txtCell) {

			@Override
			public String getValue(DocumentItem object) {
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
				case IRepositoryApi.FMDT_TYPE:
					return "Metadata";
				default:
					break;
				}
				return Labels.lblCnst.Unknown();
			}
		};
		
		Column<DocumentItem, CheckState> colIsInput = new Column<DocumentItem, CheckState>(new EnableDisableableCheckboxCell()) {
			@Override
			public CheckState getValue(DocumentItem object) {
				Boolean isSelected = object.isInput();
				boolean enable = object.getItem().getType() == IRepositoryApi.GTW_TYPE || object.getItem().getType() == IRepositoryApi.BIW_TYPE;
				return new CheckState(enable, isSelected);
			}
		};
		colIsInput.setFieldUpdater(new FieldUpdater<DocumentItem, CheckState>() {
			public void update(int index, DocumentItem object, CheckState value) {
				object.setInput(value.isCheck());
				updateDocumentItem(object);
			}
		});

		ButtonImageCell playCell = new ButtonImageCell(Images.INSTANCE.ic_play_arrow_black_18dp(), Labels.lblCnst.RunItem(), style.imgGrid());
		Column<DocumentItem, String> colPlay = new Column<DocumentItem, String>(playCell) {

			@Override
			public String getValue(DocumentItem object) {
				return "";
			}
		};
		colPlay.setFieldUpdater(new FieldUpdater<DocumentItem, String>() {

			@Override
			public void update(int index, final DocumentItem object, String value) {
				PortailRepositoryItem itemReport = null;
				if (object.getItem().getType() == IRepositoryApi.GTW_TYPE) {
					itemReport = new PortailRepositoryItem(object.getItem(), IRepositoryApi.BIG);
				}
				else if (object.getItem().getType() == IRepositoryApi.BIW_TYPE) {
					itemReport = new PortailRepositoryItem(object.getItem(), IRepositoryApi.BIW);
				}
				else if (object.getItem().getType() == IRepositoryApi.CUST_TYPE && object.getItem().getSubtype() == IRepositoryApi.BIRT_REPORT_SUBTYPE) {
					itemReport = new PortailRepositoryItem(object.getItem(), "BIRT");
				}
				else if (object.getItem().getType() == IRepositoryApi.CUST_TYPE && object.getItem().getSubtype() == IRepositoryApi.FWR_TYPE) {
					itemReport = new PortailRepositoryItem(object.getItem(), IRepositoryApi.FWR);
				}
				else {
					return;
				}

				ViewerDialog dial = new ViewerDialog(itemReport, infoUser.getGroup());
				dial.center();
			}
		});

		ButtonImageCell histoCell = new ButtonImageCell(Images.INSTANCE.ic_view_list_black_18dp(), Labels.lblCnst.Historic(), style.imgGrid());
		Column<DocumentItem, String> colHisto = new Column<DocumentItem, String>(histoCell) {

			@Override
			public String getValue(DocumentItem object) {
				return "";
			}
		};
		colHisto.setFieldUpdater(new FieldUpdater<DocumentItem, String>() {

			@Override
			public void update(int index, final DocumentItem object, String value) {
				ItemHistoricDialog dial = new ItemHistoricDialog(object.getItem());
				dial.center();
			}
		});

		ButtonImageCell deleteCell = new ButtonImageCell(Images.INSTANCE.ic_delete_black_18dp(), Labels.lblCnst.DeleteLinkedItem(), style.imgGrid());
		Column<DocumentItem, String> colDelete = new Column<DocumentItem, String>(deleteCell) {

			@Override
			public String getValue(DocumentItem object) {
				return "";
			}
		};
		colDelete.setFieldUpdater(new FieldUpdater<DocumentItem, String>() {

			@Override
			public void update(int index, final DocumentItem object, String value) {
				final InformationsDialog dial = new InformationsDialog(LabelsConstants.lblCnst.Information(), LabelsConstants.lblCnst.Confirmation(), LabelsConstants.lblCnst.Cancel(), Labels.lblCnst.ConfirmDeleteLinkedDocument(), true);
				dial.addCloseHandler(new CloseHandler<PopupPanel>() {

					@Override
					public void onClose(CloseEvent<PopupPanel> event) {
						if (dial.isConfirm()) {
							deleteDocumentItem(object);
						}
					}
				});
				dial.center();
			}
		});

		dataProvider = new ListDataProvider<DocumentItem>(new ArrayList<DocumentItem>());

		DataGrid.Resources resources = new CustomResources();
		DataGrid<DocumentItem> dataGrid = new DataGrid<DocumentItem>(10000, resources);
		dataGrid.setSize("100%", "100%");
		dataGrid.addColumn(nameColumn, Labels.lblCnst.Name());
		dataGrid.addColumn(typeColumn, Labels.lblCnst.Type());
		dataGrid.addColumn(colIsInput, Labels.lblCnst.IsInput());
		dataGrid.setColumnWidth(colIsInput, "70px");
		dataGrid.addColumn(colPlay);
		dataGrid.setColumnWidth(colPlay, "70px");
		dataGrid.addColumn(colHisto);
		dataGrid.setColumnWidth(colHisto, "70px");
		dataGrid.addColumn(colDelete);
		dataGrid.setColumnWidth(colDelete, "70px");
		dataGrid.setEmptyTableWidget(new Label(Labels.lblCnst.NoLinkedItem()));

		dataProvider.addDataDisplay(dataGrid);

		return dataGrid;
	}

	@UiHandler("btnAdd")
	public void onAddClick(ClickEvent event) {
		final RepositoryDialog dial = new RepositoryDialog(-1);
		dial.center();
		dial.addCloseHandler(new CloseHandler<PopupPanel>() {

			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				if (dial.isConfirm()) {
					RepositoryItem item = dial.getSelectedItem();
					if ((onlyEtl && item.getType() != IRepositoryApi.GTW_TYPE) 
							|| (item.getType() != IRepositoryApi.GTW_TYPE && item.getType() != IRepositoryApi.BIW_TYPE && item.getType() != IRepositoryApi.CUST_TYPE && item.getType() != IRepositoryApi.FMDT_TYPE)) {
						MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Information(), Labels.lblCnst.ThisItemCannotBeLinked());
						return;
					}
					
					saveDocumentItem(item);
				}
			}
		});
	}

	private void saveDocumentItem(RepositoryItem item) {
		DocumentItem docItem = new DocumentItem();
		docItem.setContractId(contract.getId());
		docItem.setItem(item);

		ArchitectService.Connect.getInstance().saveOrUpdateDocumentItem(docItem, new GwtCallbackWrapper<Void>(waitPanel, true, true) {

			@Override
			public void onSuccess(Void result) {
				refreshLinkedItems(true);
			}
		}.getAsyncCallback());
	}

	private void updateDocumentItem(DocumentItem docItem) {
		ArchitectService.Connect.getInstance().saveOrUpdateDocumentItem(docItem, new GwtCallbackWrapper<Void>(waitPanel, true, true) {

			@Override
			public void onSuccess(Void result) {
				refreshLinkedItems(true);
			}
		}.getAsyncCallback());
	}

	private void deleteDocumentItem(DocumentItem docItem) {
		ArchitectService.Connect.getInstance().removeLinkedItem(docItem, new GwtCallbackWrapper<Void>(waitPanel, true, true) {

			@Override
			public void onSuccess(Void result) {
				refreshLinkedItems(true);
			}
		}.getAsyncCallback());
	}

	public boolean isRefreshContracts() {
		return refreshContracts;
	}

}
