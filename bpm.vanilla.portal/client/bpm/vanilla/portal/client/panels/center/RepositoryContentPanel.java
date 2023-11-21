package bpm.vanilla.portal.client.panels.center;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import bpm.gwt.commons.client.ExceptionManager;
import bpm.gwt.commons.client.InformationsDialog;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.panels.Tab;
import bpm.gwt.commons.client.utils.VanillaCSS;
import bpm.gwt.commons.shared.repository.PortailRepositoryDirectory;
import bpm.gwt.commons.shared.repository.PortailRepositoryItem;
import bpm.vanilla.platform.core.repository.IRepositoryObject;
import bpm.vanilla.portal.client.biPortal;
import bpm.vanilla.portal.client.Listeners.DirectoryMenu;
import bpm.vanilla.portal.client.Listeners.ItemMenu;
import bpm.vanilla.portal.client.dialog.RequestAccessDialog;
import bpm.vanilla.portal.client.panels.ContentDisplayPanel;
import bpm.vanilla.portal.client.panels.navigation.TypeViewer;
import bpm.vanilla.portal.client.popup.RepositoryDisplayPopup;
import bpm.vanilla.portal.client.services.BiPortalService;
import bpm.vanilla.portal.client.utils.ToolsGWT;
import bpm.vanilla.portal.client.widget.custom.BreadCrumb;
import bpm.vanilla.portal.client.widget.custom.CustomResources;
import bpm.vanilla.portal.client.widget.custom.ItemThumbnailPanel;

import com.google.gwt.cell.client.ImageResourceCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;

public class RepositoryContentPanel extends Tab {
	private static final int COLUMN_NAME_INDEX = 0;
	private static final int COLUMN_COMMENT_INDEX = 1;
	private static final int COLUMN_DATE_CREATION_INDEX = 2;

	private static RepositoryContentPanelUiBinder uiBinder = GWT.create(RepositoryContentPanelUiBinder.class);

	interface RepositoryContentPanelUiBinder extends UiBinder<Widget, RepositoryContentPanel> {
	}
	
	interface MyStyle extends CssResource {
		String mainPanel();
		String btnSelected();
		String focusItem();
		String flexTable();
	}
	
	@UiField
	MyStyle style;
	
	@UiField
	FlowPanel panelFilePath;

	@UiField
	SimplePanel contentPanel, infoPanel;
	
	@UiField
	HTMLPanel dataGridToolbar;
	
	@UiField
	Image btnDisplay;
	
	@UiField
	Button btnName, btnComment, btnDate;

	private ContentDisplayPanel mainPanel;
	
	private DataGrid<IRepositoryObject> dataGrid;
	private ListDataProvider<IRepositoryObject> dataProvider;
	
	private List<IRepositoryObject> directChilds;
	private boolean colNameAscending = true;
	private boolean colCommentAscending = false;
	private boolean colDateCreationAscending = false;
	
	private PortailRepositoryDirectory selectedItem;
	private ItemThumbnailPanel selectedThumbnail;
	
	private SimplePanel panelGridData;
	private FlexTable panelTableData;

	private TypeViewer typeViewer;
	private boolean displayAsGrid = false;
	
	public RepositoryContentPanel(ContentDisplayPanel mainPanel) {
		super(mainPanel, ToolsGWT.lblCnst.Repository(), true);
		this.add(uiBinder.createAndBindUi(this));
		this.mainPanel = mainPanel;

		panelGridData = createGridData();
		panelTableData = createTableData();

		contentPanel.add(panelGridData);
		
		this.addStyleName(style.mainPanel());
		
		displayContent();
		
		dataGridToolbar.addStyleName(VanillaCSS.NAVIGATION_TOOLBAR);
	}

	public void displayContent() {
		if(biPortal.get().getContentRepository() != null) {
			displayData(biPortal.get().getContentRepository(), getTypeViewer());
		}
	}

	public void changeDisplay(boolean displayAsGrid) {
		this.displayAsGrid = displayAsGrid;
		refresh();
	}

	public void refresh() {
		if (selectedItem != null) {
			if (displayAsGrid) {
				displayAsGrid(selectedItem);
				sort(-1, true);
			}
			else {
				displayAsThumbnail(selectedItem);
				sort(-1, true);
			}
		}
	}

	public void displayData(PortailRepositoryDirectory parent, TypeViewer typeViewer) {
		this.selectedItem = parent;
		this.typeViewer = typeViewer;

		if (displayAsGrid) {
			displayAsGrid(parent);
		}
		else {
			displayAsThumbnail(parent);
		}
	}

	private void displayAsGrid(PortailRepositoryDirectory parent) {
		fillGridData(parent);
		contentPanel.setWidget(panelGridData);
	}

	private void displayAsThumbnail(PortailRepositoryDirectory parent) {
		buildThumbnail(parent);
		contentPanel.setWidget(panelTableData);
	}

	public void buildThumbnail(PortailRepositoryDirectory directory) {
		panelTableData.clear();

		List<IRepositoryObject> directChilds = directory.getItems() != null ? directory.getItems() : new ArrayList<IRepositoryObject>();

		for (int i=0; i<directChilds.size(); i++) {
			ItemThumbnailPanel itemPanel = new ItemThumbnailPanel(this, directChilds.get(i));
			panelTableData.setWidget(i, 0, itemPanel);
		}

		buildFilePath(directory);
	}
	
	private void buildFilePath(PortailRepositoryDirectory directory) {
		panelFilePath.clear();

		BreadCrumb bc = new BreadCrumb(this, directory, typeViewer);
		panelFilePath.add(bc);
		buildFilePathRecurs(directory);
	}

	private void buildFilePathRecurs(PortailRepositoryDirectory directory) {
		if (directory.getParent() != null) {
			BreadCrumb bc = new BreadCrumb(this, directory.getParent(), typeViewer);
			panelFilePath.insert(bc, 0);
			buildFilePathRecurs(directory.getParent());
		}
	}

	public void fillGridData(PortailRepositoryDirectory directory) {
		directChilds = directory.getItems() != null ? directory.getItems() : new ArrayList<IRepositoryObject>();

		dataProvider.setList(directChilds);
	    
		buildFilePath(directory);
	}

	private FlexTable createTableData() {
		FlexTable flexTable = new FlexTable();
		flexTable.addStyleName(style.flexTable());
		return flexTable;
	}

	private SimplePanel createGridData() {
	    
		CustomImageCell imageCell = new CustomImageCell();
		Column<IRepositoryObject, ImageResource> iconColumn = new Column<IRepositoryObject, ImageResource>(imageCell) {

			@Override
			public ImageResource getValue(IRepositoryObject object) {
				return ToolsGWT.getImageForObject(object, false);
			}
		};

		CustomCell cell = new CustomCell();
		Column<IRepositoryObject, String> nameColumn = new Column<IRepositoryObject, String>(cell) {

			@Override
			public String getValue(IRepositoryObject object) {
				return object.getName();
			}
		};

		Column<IRepositoryObject, String> comColumn = new Column<IRepositoryObject, String>(cell) {

			@Override
			public String getValue(IRepositoryObject object) {
				if (object instanceof PortailRepositoryDirectory) {
					return ((PortailRepositoryDirectory) object).getDirectory().getComment();
				}
				else {
					return ((PortailRepositoryItem) object).getItem().getComment();
				}
			}
		};

		Column<IRepositoryObject, String> creationDateColumn = new Column<IRepositoryObject, String>(cell) {

			@Override
			public String getValue(IRepositoryObject object) {
				if (object instanceof PortailRepositoryItem) {
					return ((PortailRepositoryItem) object).getItem().getDateCreation() + "";
				}
				else if (object instanceof PortailRepositoryDirectory) {
					return ((PortailRepositoryDirectory) object).getDirectory().getDateCreation() + "";
				}
				else {
					return "";
				}
			}
		};

		DataGrid.Resources resources = new CustomResources();
		dataGrid = new DataGrid<IRepositoryObject>(10000, resources);
		dataGrid.setWidth("100%");
		dataGrid.setHeight("100%");
		dataGrid.addColumn(iconColumn);
		dataGrid.addColumn(nameColumn);
		dataGrid.addColumn(comColumn);
		dataGrid.addColumn(creationDateColumn);

		dataGrid.setColumnWidth(iconColumn, 5, Unit.PT);
		dataGrid.setColumnWidth(nameColumn, 40, Unit.PT);
		dataGrid.setColumnWidth(comColumn, 30, Unit.PT);
		dataGrid.setColumnWidth(creationDateColumn, 20, Unit.PT);
		
		dataGrid.setEmptyTableWidget(new Label(ToolsGWT.lblCnst.NoData()));

		dataProvider = new ListDataProvider<IRepositoryObject>();
		dataProvider.addDataDisplay(dataGrid);

		// Add a selection model so we can select cells.
		SelectionModel<IRepositoryObject> selectionModel = new SingleSelectionModel<IRepositoryObject>();
		selectionModel.addSelectionChangeHandler(selectionChangeHandler);

		dataGrid.setSelectionModel(selectionModel);
		
		SimplePanel panelGrid = new SimplePanel(dataGrid);
		panelGrid.addStyleName(style.mainPanel());
		return panelGrid;
	}

	public TypeViewer getTypeViewer() {
		return typeViewer;
	}
	
	public void displayInfoItem(IRepositoryObject item) {
		ItemInformationPanel itemInfoPanel = new ItemInformationPanel(mainPanel, item, typeViewer);
		infoPanel.setWidget(itemInfoPanel);
	}

	private Handler selectionChangeHandler = new Handler() {

		@Override
		@SuppressWarnings("unchecked")
		public void onSelectionChange(SelectionChangeEvent event) {
			SingleSelectionModel<IRepositoryObject> selectionModel = (SingleSelectionModel<IRepositoryObject>) event.getSource();

			IRepositoryObject obj = selectionModel.getSelectedObject();
			displayInfoItem(obj);
		}
	};

	public void dispatchAction(IRepositoryObject obj, NativeEvent event) {
		if (event.getButton() == NativeEvent.BUTTON_RIGHT) {
			if (obj instanceof PortailRepositoryItem) {
				PortailRepositoryItem item = (PortailRepositoryItem) obj;
				showContextMenu(item, event.getClientX(), event.getClientY(), typeViewer);
			}
			else if (obj instanceof PortailRepositoryDirectory) {
				PortailRepositoryDirectory dir = (PortailRepositoryDirectory) obj;
				showDirectoryContextMenu(dir, event.getClientX(), event.getClientY());
			}
		}
		else if (event.getType().equals("dblclick")) {
			if (obj instanceof PortailRepositoryDirectory) {
				PortailRepositoryDirectory dir = (PortailRepositoryDirectory) obj;
				if (!dir.isChildLoad()) {
					loadChild(dir);
				}
				else {
					displayData(dir, typeViewer);
				}
			}
			else if (obj instanceof PortailRepositoryItem) {
				PortailRepositoryItem item = (PortailRepositoryItem) obj;
				doDblClickAction(item);
			}
		}
	}

	private void loadChild(final PortailRepositoryDirectory dir) {
		showWaitPart(true);

		BiPortalService.Connect.getInstance().getDirectoryContent(dir, new AsyncCallback<List<IRepositoryObject>>() {

			@Override
			public void onFailure(Throwable caught) {
				showWaitPart(false);

				caught.printStackTrace();

				ExceptionManager.getInstance().handleException(caught, ToolsGWT.lblCnst.Error());
			}

			@Override
			public void onSuccess(List<IRepositoryObject> result) {
				if (result != null && !result.isEmpty()) {
					dir.setItems(result);
				}
				else {
					dir.setItems(new ArrayList<IRepositoryObject>());
				}
				
				displayData(dir, typeViewer);
				dir.setChildLoad(true);
				
				mainPanel.refreshTreeRepository();

				showWaitPart(false);
			}
		});
	}

	private void showDirectoryContextMenu(PortailRepositoryDirectory dir, int x, int y) {
		DirectoryMenu contextuel = new DirectoryMenu(mainPanel, dir);
		contextuel.setPopupPosition(x, y);
		contextuel.setAutoHideEnabled(true);
		contextuel.show();
	}

	private void showContextMenu(PortailRepositoryItem item, int x, int y, TypeViewer typeViewer) {
		ItemMenu itemMenu = new ItemMenu(mainPanel, item, typeViewer);
		itemMenu.setAutoHideEnabled(true);
		itemMenu.setPopupPosition(x, y);
		itemMenu.show();
	}

	public void doDblClickAction(final PortailRepositoryItem item) {
		if (ToolsGWT.isRunnable(item) && item.getItem().canRun() && item.getItem().isOn()) {
			mainPanel.openViewer(item);
		}
		else if(ToolsGWT.isRunnable(item) && item.getItem().isOn() && !item.getItem().canRun()) {
			final InformationsDialog dial = new InformationsDialog(ToolsGWT.lblCnst.Access(), LabelsConstants.lblCnst.Confirmation(), LabelsConstants.lblCnst.Cancel(), "You don't have the right to access this item, would you like to ask for an access ?", true);
			dial.addCloseHandler(new CloseHandler<PopupPanel>() {
				
				@Override
				public void onClose(CloseEvent<PopupPanel> event) {
					if(dial.isConfirm()) {
						RequestAccessDialog dialog = new RequestAccessDialog(item);
						dialog.center();
					}
				}
			});
			dial.center();
		}
	}

	public void selectItem(ItemThumbnailPanel thumbnailPanel) {
		if(selectedThumbnail != null) {
			selectedThumbnail.removeStyleName(style.focusItem());
		}
		
		thumbnailPanel.addStyleName(style.focusItem());
		
		this.selectedThumbnail = thumbnailPanel;
	}
	
	@UiHandler("btnDisplay")
	public void onChangeDisplayClick(ClickEvent event) {
		RepositoryDisplayPopup displayPopup = new RepositoryDisplayPopup(this);
		displayPopup.setPopupPosition(event.getClientX(), event.getClientY());
		displayPopup.addCloseHandler(closeMenu);
		displayPopup.show();
		
		btnDisplay.addStyleName(style.btnSelected());
	}
	
	@UiHandler("btnName")
	public void onName(ClickEvent event) {
		sort(COLUMN_NAME_INDEX, colNameAscending);
		colNameAscending = !colNameAscending;
	}
	
	@UiHandler("btnComment")
	public void onComment(ClickEvent event) {
		sort(COLUMN_COMMENT_INDEX, colCommentAscending);
		colCommentAscending = !colCommentAscending;
	}
	
	@UiHandler("btnDate")
	public void onDate(ClickEvent event) {
		sort(COLUMN_DATE_CREATION_INDEX, colDateCreationAscending);
		colDateCreationAscending = !colDateCreationAscending;
	}
	
	private void sort(int columnIndex, boolean ascending) {
		if(columnIndex == COLUMN_NAME_INDEX 
				|| columnIndex == COLUMN_COMMENT_INDEX
				|| columnIndex == COLUMN_DATE_CREATION_INDEX) {
			Collections.sort(directChilds, new RepositoryComparator(columnIndex, ascending));
			
			dataProvider.refresh();
		}
		
		refreshUi(columnIndex, ascending);
	}
	
	private void refreshUi(int columnNameIndex, boolean ascending) {
		if(columnNameIndex == COLUMN_NAME_INDEX) {
			if(ascending) {
				btnName.setText(ToolsGWT.lblCnst.Name() + " " + ToolsGWT.lblCnst.ArrowUp());
			}
			else {
				btnName.setText(ToolsGWT.lblCnst.Name() + " " + ToolsGWT.lblCnst.ArrowDown());
			}
			btnComment.setText(ToolsGWT.lblCnst.Comment());
			btnDate.setText(ToolsGWT.lblCnst.CreationDate());
		}
		else if(columnNameIndex == COLUMN_COMMENT_INDEX) {
			btnName.setText(ToolsGWT.lblCnst.Name());
			if(ascending) {
				btnComment.setText(ToolsGWT.lblCnst.Comment() + " " + ToolsGWT.lblCnst.ArrowUp());
			}
			else {
				btnComment.setText(ToolsGWT.lblCnst.Comment() + " " + ToolsGWT.lblCnst.ArrowDown());
			}
			btnDate.setText(ToolsGWT.lblCnst.CreationDate());
		}
		else if(columnNameIndex == COLUMN_DATE_CREATION_INDEX) {
			btnName.setText(ToolsGWT.lblCnst.Name());
			btnComment.setText(ToolsGWT.lblCnst.Comment());
			if(ascending) {
				btnDate.setText(ToolsGWT.lblCnst.CreationDate() + " " + ToolsGWT.lblCnst.ArrowUp());
			}
			else {
				btnDate.setText(ToolsGWT.lblCnst.CreationDate() + " " + ToolsGWT.lblCnst.ArrowDown());
			}
		}
		else {
			btnName.setText(ToolsGWT.lblCnst.Name());
			btnComment.setText(ToolsGWT.lblCnst.Comment());
			btnDate.setText(ToolsGWT.lblCnst.CreationDate());
			
			colNameAscending = true;
			colCommentAscending = false;
			colDateCreationAscending = false;
		}
	}

	private CloseHandler<PopupPanel> closeMenu = new CloseHandler<PopupPanel>() {

		@Override
		public void onClose(CloseEvent<PopupPanel> event) {
			btnDisplay.removeStyleName(style.btnSelected());
		}
	};

	private class CustomCell extends TextCell {

		public CustomCell() {
			super();
		}

		@Override
		public Set<String> getConsumedEvents() {
			Set<String> consumedEvents = new HashSet<String>();
			consumedEvents.add("dblclick");
			consumedEvents.add("contextmenu");
			return consumedEvents;
		}

		@Override
		public void onBrowserEvent(com.google.gwt.cell.client.Cell.Context context, Element parent, String value, NativeEvent event, ValueUpdater<String> valueUpdater) {
			IRepositoryObject obj = (IRepositoryObject) context.getKey();
			dispatchAction(obj, event);
			super.onBrowserEvent(context, parent, value, event, valueUpdater);
		}
	}

	private class CustomImageCell extends ImageResourceCell {

		public CustomImageCell() {
			super();
		}

		@Override
		public Set<String> getConsumedEvents() {
			Set<String> consumedEvents = new HashSet<String>();
			consumedEvents.add("dblclick");
			consumedEvents.add("contextmenu");
			return consumedEvents;
		}

		@Override
		public void onBrowserEvent(com.google.gwt.cell.client.Cell.Context context, Element parent, ImageResource value, NativeEvent event, ValueUpdater<ImageResource> valueUpdater) {

			IRepositoryObject obj = (IRepositoryObject) context.getKey();
			dispatchAction(obj, event);
			super.onBrowserEvent(context, parent, value, event, valueUpdater);
		}
	}

	private class RepositoryComparator implements Comparator<IRepositoryObject> {
		
		private int columnIndex;
		private boolean ascending;
		
		public RepositoryComparator(int columnIndex, boolean ascending) {
			this.columnIndex = columnIndex;
			this.ascending = ascending;
		}

		@Override
		public int compare(IRepositoryObject o1, IRepositoryObject o2) {
			if(columnIndex == COLUMN_NAME_INDEX) {
				if(ascending) {
					return o1.getName().compareTo(o2.getName());
				}
				else {
					return o2.getName().compareTo(o1.getName());
				}
			}
			else if(columnIndex == COLUMN_COMMENT_INDEX) {
				String str1 = "";
				String str2 = "";
				if(o1 instanceof PortailRepositoryItem) {
					str1 = ((PortailRepositoryItem) o1).getItem().getComment();
				}
				else if (o1 instanceof PortailRepositoryDirectory) {
					str1 = ((PortailRepositoryDirectory) o1).getDirectory().getComment();
				}
				
				if(o2 instanceof PortailRepositoryItem) {
					str2 = ((PortailRepositoryItem) o2).getItem().getComment();
				}
				else if (o2 instanceof PortailRepositoryDirectory) {
					str2 = ((PortailRepositoryDirectory) o2).getDirectory().getComment();
				}

				if(ascending) {
					return str1.compareTo(str2);
				}
				else {
					return str2.compareTo(str1);
				}
			}
			else if(columnIndex == COLUMN_DATE_CREATION_INDEX) {
				Date date1 = null;
				Date date2 = null;
				if(o1 instanceof PortailRepositoryItem) {
					date1 = ((PortailRepositoryItem) o1).getItem().getDateCreation();
				}
				else if (o1 instanceof PortailRepositoryDirectory) {
					date1 = ((PortailRepositoryDirectory) o1).getDirectory().getDateCreation();
				}
				
				if(o2 instanceof PortailRepositoryItem) {
					date2 = ((PortailRepositoryItem) o2).getItem().getDateCreation();
				}
				else if (o2 instanceof PortailRepositoryDirectory) {
					date2 = ((PortailRepositoryDirectory) o2).getDirectory().getDateCreation();
				}

				if(date1 == null) {
					return -1;
				}
				else if(date2 == null) {
					return 1;
				}
				
				if(ascending) {
					return date2.before(date1) ? -1 : date2.after(date1) ? 1 : 0;
				}
				else {
					return date1.before(date2) ? -1 : date1.after(date2) ? 1 : 0;
				}
			}
			return 0;
		}
		
	}
}
