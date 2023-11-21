package bpm.gwt.aklabox.commons.client.customs;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import bpm.document.management.core.model.Tree;
import bpm.gwt.aklabox.commons.client.utils.CustomResources;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;

public class CustomDatagrid<T> extends SimplePanel {

	private SelectionModel<T> selectionModel;
	private ListDataProvider<T> dataProvider;
	private ListHandler<T> sortHandler;
	
	public CustomDatagrid(SingleSelectionModel<T> selectionModel, int height, String emptyMessage, String header) {
		this.selectionModel = selectionModel;
		setWidget(buildGrid(null, height + "px", false, emptyMessage, header));
	}

	public CustomDatagrid(SingleSelectionModel<T> selectionModel, String height, String emptyMessage, String header) {
		this.selectionModel = selectionModel;
		setWidget(buildGrid(null, height, false, emptyMessage, header));
	}

	public CustomDatagrid(List<T> groups, MultiSelectionModel<T> multiSelectionModel, int height, String emptyMessage, String header) {
		this.selectionModel = multiSelectionModel;
		setWidget(buildGrid(groups, height + "px", true, emptyMessage, header));
	}

	public void loadItems(List<T> items) {
		loadItems(items, null);
	}

	public void loadItems(List<T> items, List<T> selectedItems) {
		if (dataProvider != null) {

			clearSelection(dataProvider.getList());

			dataProvider.setList(items);
			sortHandler.setList(dataProvider.getList());

			dataProvider.refresh();

			if ((selectedItems == null || selectedItems.isEmpty()) && !items.isEmpty()) {
				selectionModel.setSelected(items.get(0), true);
			}
			else if (selectedItems != null && !selectedItems.isEmpty()) {
				for(T obj : selectedItems) {
					selectionModel.setSelected(obj, true);
				}
			}
		}
	}

	private void clearSelection(List<T> list) {
		if (list != null && selectionModel != null) {
			for (T obj : list) {
				selectionModel.setSelected(obj, false);
			}
		}
	}

	private DataGrid<T> buildGrid(List<T> groups, String height, boolean isShowOption, String emptyMessage, final String header) {
		if (groups == null) {
			groups = new ArrayList<T>();
		}

		CheckboxCell cell = new CheckboxCell();
		Column<T, Boolean> modelNameColumn = new Column<T, Boolean>(cell) {

			@Override
			public Boolean getValue(T object) {
				return selectionModel.isSelected(object);
			}
		};

		TextCell txtCell = new TextCell();
		Column<T, String> nameColumn = new Column<T, String>(txtCell) {

			@Override
			public String getValue(T object) {
				if (object instanceof Group) {
					return ((Group) object).getName();
				}
				else if (object instanceof Repository) {
					return ((Repository) object).getName();
				}
				else if (object instanceof Tree) {
					return ((Tree) object).getFullPath();
				}

				return object.toString();
			}
		};
		nameColumn.setSortable(true);

		Header<Boolean> headerCheck = new Header<Boolean>(new CustomCheckboxCell<T>(groups, selectionModel)) {

			@Override
			public Boolean getValue() {
				return false;
			}
		};

		Header<String> headerName = new Header<String>(new TextCell()) {

			@Override
			public String getValue() {
				return header;
			}
		};

		DataGrid.Resources resources = new CustomResources();
		DataGrid<T> dataGrid = new DataGrid<T>(10000, resources);
		dataGrid.setWidth("100%");
		dataGrid.setHeight(height);
		if (isShowOption) {
			dataGrid.addColumn(modelNameColumn, headerCheck);
			dataGrid.setColumnWidth(modelNameColumn, "40px");
			dataGrid.addColumn(nameColumn, headerName);
		}
		else {
			if (header != null) {
				dataGrid.addColumn(nameColumn, headerName);
			}
			else {
				dataGrid.addColumn(nameColumn);
			}
			nameColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		}
		dataGrid.setEmptyTableWidget(new Label(emptyMessage));

		dataProvider = new ListDataProvider<T>(groups);
		dataProvider.addDataDisplay(dataGrid);

		sortHandler = new ListHandler<T>(groups);
		sortHandler.setComparator(nameColumn, new Comparator<T>() {

			@Override
			public int compare(T o1, T o2) {
				if (o1 instanceof Group && o2 instanceof Group) {
					return ((Group) o1).getName().compareTo(((Group) o2).getName());
				}
				else if (o1 instanceof Repository && o2 instanceof Repository) {
					return ((Repository) o1).getName().compareTo(((Repository) o2).getName());
				}
				else {
					return o1.toString().compareTo(o2.toString());
				}
			}
		});
		dataGrid.addColumnSortHandler(sortHandler);

		if (isShowOption) {
			dataGrid.setSelectionModel(selectionModel, DefaultSelectionEventManager.<T> createCheckboxManager());
		}
		else {
			dataGrid.setSelectionModel(selectionModel);
		}

		sortHandler.setList(dataProvider.getList());

		return dataGrid;
	}
}
