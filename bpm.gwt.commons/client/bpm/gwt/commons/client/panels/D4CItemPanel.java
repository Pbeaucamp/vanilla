package bpm.gwt.commons.client.panels;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.google.gwt.view.client.SingleSelectionModel;

import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.custom.v2.GridPanel;
import bpm.gwt.commons.client.loading.IWait;
import bpm.gwt.commons.client.services.CommonService;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.vanilla.platform.core.beans.resources.D4CItem;
import bpm.vanilla.platform.core.beans.resources.D4CItem.TypeD4CItem;
import bpm.vanilla.platform.core.beans.resources.D4CItemMap;
import bpm.vanilla.platform.core.beans.resources.D4CItemTable;
import bpm.vanilla.platform.core.beans.resources.D4CItemVisualization;

/**
 * Widget to manage a D4CItem server definition
 *
 */
public class D4CItemPanel<T extends D4CItem> extends Composite {

	private static D4CItemDefinitionPanelUiBinder uiBinder = GWT.create(D4CItemDefinitionPanelUiBinder.class);

	interface D4CItemDefinitionPanelUiBinder extends UiBinder<Widget, D4CItemPanel<?>> {
	}

	@UiField
	GridPanel<T> grid;

	private IWait waitPanel;

	private SingleSelectionModel<T> singleSelectionPanel;

	private int parentId;
	private TypeD4CItem type;

	public D4CItemPanel(IWait waitPanel, TypeD4CItem type, Handler selectionChangeHandler) {
		this.waitPanel = waitPanel;
		this.type = type;
		initWidget(uiBinder.createAndBindUi(this));

		buildGrid(selectionChangeHandler);

		Scheduler.get().scheduleDeferred(new ScheduledCommand() {

			@Override
			public void execute() {
				grid.setTopManually(30);
			}
		});
	}

	public void loadItems(int parentId) {
		this.parentId = parentId;

		CommonService.Connect.getInstance().getD4CItems(parentId, type, new GwtCallbackWrapper<HashMap<String, HashMap<String, List<D4CItem>>>>(waitPanel, true, true) {

			@Override
			@SuppressWarnings("unchecked")
			public void onSuccess(HashMap<String, HashMap<String, List<D4CItem>>> result) {
				List<T> items = new ArrayList<T>();

				if (result != null) {
					for (String organization : result.keySet()) {
						HashMap<String, List<D4CItem>> d4cItemsByDataset = result.get(organization);
						if (d4cItemsByDataset != null) {
							for (String dataset : d4cItemsByDataset.keySet()) {
								List<D4CItem> d4cItems = d4cItemsByDataset.get(dataset);
								if (d4cItems != null) {
									for (D4CItem item : d4cItems) {
										items.add((T) item);
									}
								}
							}
						}
					}
				}
				grid.loadItems(items);
			}
		}.getAsyncCallback());
	}

	private void buildGrid(Handler selectionChangeHandler) {
//		Button btnAdd = new Button(LabelsConstants.lblCnst.AddItem(), CommonImages.INSTANCE.add_24());
//		btnAdd.addClickHandler(addHandler);
//
//		grid.setActionPanel("", btnAdd);

		TextCell cell = new TextCell();
		Column<T, String> colName = new Column<T, String>(cell) {
			@Override
			public String getValue(T object) {
				return object.getName();
			}
		};
		Column<T, String> colOrganization = new Column<T, String>(cell) {
			@Override
			public String getValue(T object) {
				return object.getOrganization();
			}
		};
		Column<T, String> colDataset = new Column<T, String>(cell) {
			@Override
			public String getValue(T object) {
				return object.getDatasetName();
			}
		};
		Column<T, String> colType = new Column<T, String>(cell) {
			@Override
			public String getValue(T object) {
				if (object instanceof D4CItemTable) {
					return LabelsConstants.lblCnst.Table();
				}
				else if (object instanceof D4CItemMap) {
					return LabelsConstants.lblCnst.Map();
				}
				else if (object instanceof D4CItemVisualization) {
					return LabelsConstants.lblCnst.Analyze();
				}
				return "";
			}
		};
		Column<T, String> colUrl = new Column<T, String>(cell) {
			@Override
			public String getValue(T object) {
				if (object instanceof D4CItemVisualization) {
					return ((D4CItemVisualization) object).getUrl();
				}
				else if (object instanceof D4CItemMap) {
					return ((D4CItemMap) object).getUrl();
				}
				else if (object instanceof D4CItemTable) {
					return ((D4CItemTable) object).getUrl();
				}
				return "";
			}
		};

		// HasActionCell<T> editCell = new
		// HasActionCell<T>(CommonImages.INSTANCE.edit_24(),
		// LabelsConstants.lblCnst.Edit(), new Delegate<T>() {
		//
		// @Override
		// public void execute(T object) {
		// manageItem(object);
		// }
		// });
		//
		// HasActionCell<T> deleteCell = new
		// HasActionCell<T>(CommonImages.INSTANCE.delete_24(),
		// LabelsConstants.lblCnst.Delete(), new Delegate<T>() {
		//
		// @Override
		// public void execute(T object) {
		// deleteItem(object);
		// }
		// });
		//
		// CompositeCellHelper<T> compCell = new
		// CompositeCellHelper<T>(editCell, deleteCell);
		// Column<T, T> colAction = new Column<T, T>(compCell.getCell()) {
		// @Override
		// public T getValue(T object) {
		// return object;
		// }
		// };

		grid.addColumn(LabelsConstants.lblCnst.Name(), colName, "100px", new Comparator<T>() {

			@Override
			public int compare(T o1, T o2) {
				return grid.compare(o1.getName(), o2.getName());
			}
		});
		grid.addColumn(LabelsConstants.lblCnst.Organization(), colOrganization, "100px", new Comparator<T>() {

			@Override
			public int compare(T o1, T o2) {
				return grid.compare(o1.getOrganization(), o2.getOrganization());
			}
		});
		grid.addColumn(LabelsConstants.lblCnst.Dataset(), colDataset, "100px", new Comparator<T>() {

			@Override
			public int compare(T o1, T o2) {
				return grid.compare(o1.getDatasetName(), o2.getDatasetName());
			}
		});
		grid.addColumn(LabelsConstants.lblCnst.Type(), colType, "100px", new Comparator<T>() {

			@Override
			public int compare(T o1, T o2) {
				return 0;
			}
		});
		grid.addColumn(LabelsConstants.lblCnst.Url(), colUrl, "150px", new Comparator<T>() {

			@Override
			public int compare(T o1, T o2) {
				if (o1 instanceof D4CItemVisualization && o2 instanceof D4CItemVisualization) {
					return grid.compare(((D4CItemVisualization) o1).getUrl(), ((D4CItemVisualization) o2).getUrl());
				}
				return 0;
			}
		});
		// grid.addColumn("", colAction, "70px", null);

		singleSelectionPanel = new SingleSelectionModel<>();
		if (selectionChangeHandler != null) {
			singleSelectionPanel.addSelectionChangeHandler(selectionChangeHandler);
		}
		grid.setSelectionModel(singleSelectionPanel);
		grid.setPageVisible(false);
	}

//	private ClickHandler addHandler = new ClickHandler() {
//
//		@Override
//		public void onClick(ClickEvent event) {
//			manageItem(null);
//		}
//	};

	// private void manageItem(T item) {
	// if (parentId <= 0) {
	// MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Information(),
	// LabelsConstants.lblCnst.PleaseSelectAD4CServer());
	// return;
	// }
	//
	// final AddD4CItemDialog<T> dial = new AddD4CItemDialog<T>(item, parentId,
	// type);
	// dial.center();
	// dial.addCloseHandler(new CloseHandler<PopupPanel>() {
	//
	// @Override
	// public void onClose(CloseEvent<PopupPanel> event) {
	// if (dial.isConfirm()) {
	// D4CItem item = dial.getItem();
	// CommonService.Connect.getInstance().manageD4CItem(item,
	// ManageAction.SAVE_OR_UPDATE, new GwtCallbackWrapper<D4CItem>(waitPanel,
	// true, true) {
	//
	// @Override
	// public void onSuccess(D4CItem result) {
	// loadItems(parentId);
	// }
	// }.getAsyncCallback());
	// }
	// }
	// });
	// }
	//
	// private void deleteItem(T item) {
	// CommonService.Connect.getInstance().manageD4CItem(item,
	// ManageAction.DELETE, new GwtCallbackWrapper<D4CItem>(waitPanel, true,
	// true) {
	//
	// @Override
	// public void onSuccess(D4CItem result) {
	// loadItems(parentId);
	// }
	// }.getAsyncCallback());
	// }

	public T getSelectedItem() {
		return singleSelectionPanel.getSelectedObject();
	}
}
