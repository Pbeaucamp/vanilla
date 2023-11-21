package bpm.gwt.workflow.commons.client.resources;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;

import bpm.gwt.commons.client.InformationsDialog;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.dialog.ItemNameDialog;
import bpm.gwt.commons.client.images.CommonImages;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.utils.ButtonImageCell;
import bpm.gwt.commons.client.utils.CustomResources;
import bpm.gwt.workflow.commons.client.IManager;
import bpm.gwt.workflow.commons.client.IResourceManager;
import bpm.gwt.workflow.commons.client.NameChecker;
import bpm.gwt.workflow.commons.client.I18N.LabelsCommon;
import bpm.gwt.workflow.commons.client.dialog.ResourceDialog;
import bpm.gwt.workflow.commons.client.images.Images;
import bpm.gwt.workflow.commons.client.service.WorkflowsService;
import bpm.gwt.workflow.commons.client.tabs.HorizontalTab;
import bpm.gwt.workflow.commons.client.workflow.properties.PropertiesPanel;
import bpm.vanilla.platform.core.beans.resources.Resource;

public abstract class ResourcePanel<T extends Resource> extends HorizontalTab implements IManager<T>, NameChecker {

	private static UserPanelUiBinder uiBinder = GWT.create(UserPanelUiBinder.class);

	interface UserPanelUiBinder extends UiBinder<Widget, ResourcePanel<?>> {
	}

	interface MyStyle extends CssResource {
		String mainPanel();

		String imgGrid();
	}

	@UiField
	MyStyle style;

	@UiField
	SimplePanel gridPanel;
	
	@UiField
	Label lblTitle, lblAdd;

	private ListDataProvider<T> dataProvider;
	private ListHandler<T> sortHandler;
	
	private IResourceManager resourceManager;

	public ResourcePanel(String title, ImageResource icon, String labelAdd, IResourceManager resourceManager) {
		super(title, icon);
		this.add(uiBinder.createAndBindUi(this));
		this.resourceManager = resourceManager;

		this.addStyleName(style.mainPanel());
		
		lblTitle.setText(title);
		lblAdd.setText(labelAdd);

		DataGrid<T> datagrid = createGrid();
		gridPanel.setWidget(datagrid);

		loadResources();
	}
	
	protected IResourceManager getResourceManager() {
		return resourceManager;
	}

	@Override
	public void loadResources(List<T> result) {
		if (result != null) {
			dataProvider.setList(result);
			sortHandler.setList(dataProvider.getList());
		}
		else {
			dataProvider.setList(new ArrayList<T>());
		}
	}

	private DataGrid<T> createGrid() {
		sortHandler = new ListHandler<T>(new ArrayList<T>());
		
		TextCell cell = new TextCell();
		Column<T, String> colName = new Column<T, String>(cell) {

			@Override
			public String getValue(T object) {
				return object.getName();
			}

		};
		colName.setSortable(true);
		sortHandler.setComparator(colName, new Comparator<T>() {

			@Override
			public int compare(T o1, T o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		
		List<ColumnWrapper<T>> columns = buildCustomColumns(cell, sortHandler);

		ButtonImageCell editCell = new ButtonImageCell(Images.INSTANCE.ic_edit_black_18dp(), style.imgGrid());
		Column<T, String> colEdit = new Column<T, String>(editCell) {

			@Override
			public String getValue(T object) {
				return "";
			}
		};
		colEdit.setFieldUpdater(new FieldUpdater<T, String>() {

			@Override
			public void update(int index, T object, String value) {
				ResourceDialog dial = new ResourceDialog(ResourcePanel.this, object, buildPropertiesPanel(object));
				dial.center();
			}
		});

		ButtonImageCell duplicateCell = new ButtonImageCell(CommonImages.INSTANCE.ic_control_point_duplicate_black_18dp(), style.imgGrid());
		Column<T, String> colDuplicate = new Column<T, String>(duplicateCell) {

			@Override
			public String getValue(T object) {
				return "";
			}
		};
		colDuplicate.setFieldUpdater(new FieldUpdater<T, String>() {

			@Override
			public void update(int index, final T object, String value) {
				final ItemNameDialog dial = new ItemNameDialog(LabelsConstants.lblCnst.DuplicateResource(), false);
				dial.center();
				dial.addCloseHandler(new CloseHandler<PopupPanel>() {
					
					@Override
					public void onClose(CloseEvent<PopupPanel> event) {
						String name = dial.getName();
						if (!name.isEmpty() && !checkIfNameTaken(-1, name)) {
							duplicate(object, name);
						}
					}
				});
			}
		});

		ButtonImageCell deleteCell = new ButtonImageCell(Images.INSTANCE.ic_delete_black_18dp(), style.imgGrid());
		Column<T, String> colDelete = new Column<T, String>(deleteCell) {

			@Override
			public String getValue(T object) {
				return "";
			}
		};
		colDelete.setFieldUpdater(new FieldUpdater<T, String>() {

			@Override
			public void update(int index, final T object, String value) {
				final InformationsDialog dial = new InformationsDialog(LabelsCommon.lblCnst.Information(), LabelsCommon.lblCnst.Confirmation(), LabelsCommon.lblCnst.Cancel(), getDeleteConfirmMessage(), true);
				dial.addCloseHandler(new CloseHandler<PopupPanel>() {

					@Override
					public void onClose(CloseEvent<PopupPanel> event) {
						if (dial.isConfirm()) {
							delete(object);
						}
					}
				});
				dial.center();
			}
		});

		DataGrid.Resources resources = new CustomResources();
		DataGrid<T> dataGrid = new DataGrid<T>(9999, resources);
		dataGrid.setSize("100%", "100%");
		dataGrid.addColumn(colName, LabelsCommon.lblCnst.Name());
		dataGrid.setColumnWidth(colName, "150px");
		for (ColumnWrapper<T> column : columns) {
			dataGrid.addColumn(column.getColumn(), column.getTitle());
			if(column.getWidth() != null) {
				dataGrid.setColumnWidth(column.getColumn(), "150px");
			}
		}
		dataGrid.addColumn(colEdit);
		dataGrid.setColumnWidth(colEdit, "70px");
		dataGrid.addColumn(colDuplicate);
		dataGrid.setColumnWidth(colDuplicate, "70px");
		dataGrid.addColumn(colDelete);
		dataGrid.setColumnWidth(colDelete, "70px");
		dataGrid.addColumnSortHandler(sortHandler);

		dataProvider = new ListDataProvider<T>();
		dataProvider.addDataDisplay(dataGrid);

		SelectionModel<T> selectionModel = new SingleSelectionModel<T>();
		dataGrid.setSelectionModel(selectionModel);

		return dataGrid;
	}

	@UiHandler("btnAdd")
	public void onAddClick(ClickEvent event) {
		ResourceDialog dial = new ResourceDialog(this, null, buildPropertiesPanel(null));
		dial.center();
	}

	private void delete(T item) {
		showWaitPart(true);

		WorkflowsService.Connect.getInstance().removeResource(item, new GwtCallbackWrapper<Void>(this, true) {

			@Override
			public void onSuccess(Void result) {
				loadResources();
			}
		}.getAsyncCallback());
	}

	private void duplicate(T item, String name) {
		WorkflowsService.Connect.getInstance().duplicateResource(item.getId(), name, new GwtCallbackWrapper<Resource>(this, true, true) {

			@Override
			public void onSuccess(Resource result) {
				loadResources();
			}
		}.getAsyncCallback());
	}
	
	@Override
	public boolean checkIfNameTaken(int id, String value) {
		List<T> resources = dataProvider.getList();
		if (resources != null) {
			for(Resource resource : resources) {
				if(!(id > 0 && resource.getId() == id) && resource.getName().equals(value)) {
					return true;
				}
			}
		}
		return false;
	}
	
	protected abstract String getDeleteConfirmMessage();

	protected abstract List<ColumnWrapper<T>> buildCustomColumns(TextCell cell, ListHandler<T> sortHandler);
	
	protected abstract PropertiesPanel<Resource> buildPropertiesPanel(Resource resource);
	
	public class ColumnWrapper<E> {
		
		private Column<E, ?> column;
		private String title;
		private String width;
		
		public ColumnWrapper(Column<E, ?> column, String title, String width) {
			this.column = column;
			this.title = title;
			this.width = width;
		}
		
		public Column<E, ?> getColumn() {
			return column;
		}
		
		public String getTitle() {
			return title;
		}
		
		public String getWidth() {
			return width;
		}
	}
}
