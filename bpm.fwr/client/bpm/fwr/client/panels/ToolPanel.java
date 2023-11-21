package bpm.fwr.client.panels;

import java.util.List;

import bpm.fwr.api.beans.dataset.Column;
import bpm.fwr.api.beans.dataset.FWRFilter;
import bpm.fwr.api.beans.dataset.FwrPrompt;
import bpm.fwr.client.Bpm_fwr;
import bpm.fwr.client.WysiwygPanel;
import bpm.fwr.client.draggable.widgets.DraggablePaletteItem;
import bpm.fwr.client.services.FwrServiceConnection;
import bpm.fwr.client.tree.MetadataTree;
import bpm.fwr.client.utils.WidgetType;
import bpm.fwr.shared.models.metadata.FwrBusinessModel;
import bpm.fwr.shared.models.metadata.FwrBusinessPackage;
import bpm.fwr.shared.models.metadata.FwrBusinessTable;
import bpm.fwr.shared.models.metadata.FwrMetadata;
import bpm.fwr.shared.models.metadata.FwrSavedQuery;
import bpm.gwt.commons.client.loading.GreyAbsolutePanel;
import bpm.gwt.commons.client.loading.IWait;
import bpm.gwt.commons.client.loading.WaitAbsolutePanel;
import bpm.gwt.commons.client.utils.VanillaCSS;
import bpm.gwt.commons.client.viewer.fmdtdriller.IExpand;
import bpm.gwt.commons.client.viewer.fmdtdriller.MetadataItemDescriptionPanel;
import bpm.gwt.commons.shared.InfoUser;
import bpm.vanilla.platform.core.beans.fmdt.FmdtColumn;
import bpm.vanilla.platform.core.beans.fmdt.FmdtTableStruct;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

public class ToolPanel extends Composite implements IWait, IExpand, SelectionHandler<TreeItem> {

	private static final int DEFAULT_BOTTOM = 230;
	private static final int DEFAULT_HEIGHT = 220;
	private static final int COLLAPSE_BOTTOM = 40;
	private static final int COLLAPSE_HEIGHT = 30;

	private static ToolPanelUiBinder uiBinder = GWT.create(ToolPanelUiBinder.class);

	interface ToolPanelUiBinder extends UiBinder<Widget, ToolPanel> {
	}

	@UiField
	SimplePanel panelTree, panelDescription;

	@UiField
	HTMLPanel navigationPanel, toolbarNavigation, rightToolbarPanel;

	@UiField(provided = true)
	DraggablePaletteItem itemTable, itemChart, itemList, itemCrosstab, itemImage, itemLabel, itemMap, itemHyperlink;

	@UiField
	Image imgExpand, imgCollapse;
	
	@UiField
	Label lblTitle;
	
	@UiField
	TextBox txtSearch;
	
	private WaitAbsolutePanel waitPanel;
	private GreyAbsolutePanel greyPanel;
	private boolean isCharging = false;

	private WysiwygPanel panelParent;

	private MetadataTree datasetTree;
	private MetadataItemDescriptionPanel metadataItemDescription;

	private PickupDragController paletteDragController;
	private PickupDragController dataDragController;
	private PickupDragController resourceDragController;

	private String actualSearch = "";
	
	@UiConstructor
	public ToolPanel(WysiwygPanel panelParent, PickupDragController paletteDragController, PickupDragController dataDragController, PickupDragController resourceDragController) {
		itemTable = new DraggablePaletteItem(WidgetType.GRID);
		itemChart = new DraggablePaletteItem(WidgetType.CHART);
		itemList = new DraggablePaletteItem(WidgetType.LIST);
		itemCrosstab = new DraggablePaletteItem(WidgetType.CROSSTAB);
		itemImage = new DraggablePaletteItem(WidgetType.IMAGE);
		itemLabel = new DraggablePaletteItem(WidgetType.LABEL);
		itemMap = new DraggablePaletteItem(WidgetType.VANILLA_MAP);
		itemHyperlink = new DraggablePaletteItem(WidgetType.HYPERLINK);

		initWidget(uiBinder.createAndBindUi(this));

		this.panelParent = panelParent;
		this.paletteDragController = paletteDragController;
		this.dataDragController = dataDragController;
		this.resourceDragController = resourceDragController;

		toolbarNavigation.addStyleName(VanillaCSS.NAVIGATION_TOOLBAR);

		imgExpand.setVisible(false);

		datasetTree = new MetadataTree(this, dataDragController, resourceDragController, true);
		datasetTree.addSelectionHandler(this);
		datasetTree.setHeight("100%");
		
		metadataItemDescription = new MetadataItemDescriptionPanel(this, panelTree, panelDescription);

		panelTree.setWidget(datasetTree);
		panelDescription.setWidget(metadataItemDescription);

		panelTree.getElement().getStyle().setBottom(COLLAPSE_BOTTOM, Unit.PX);
		panelDescription.setHeight(COLLAPSE_HEIGHT + "px");

		makePaletteDraggable();
	}

	@Override
	public void onSelection(SelectionEvent<TreeItem> event) {
		TreeItem item = event.getSelectedItem();

		String description = getDescription(item);
		metadataItemDescription.updateDescription(description);
	}

	private String getDescription(TreeItem selectedItem) {
		Object item = selectedItem != null ? selectedItem.getUserObject() : null;
		if (item == null) {
			return "";
		}

		if (item instanceof FwrMetadata) {
			return ((FwrMetadata) item).getDescription();
		}
		else if (item instanceof FwrBusinessModel) {
			return ((FwrBusinessModel) item).getDescription();
		}
		else if (item instanceof FwrBusinessPackage) {
			return ((FwrBusinessPackage) item).getDescription();
		}
		else if (item instanceof FwrBusinessTable) {
			return ((FwrBusinessTable) item).getDescription();
		}
		else if (item instanceof Column) {
			return ((Column) item).getDescription();
		}
		else if (item instanceof FwrPrompt) {
//			return ((FwrPrompt) item).getDescription();
			return "";
		}
		else if (item instanceof FWRFilter) {
//			return ((FWRFilter) item).getDescription();
			return "";
		}
		else if (item instanceof FwrSavedQuery) {
			return ((FwrSavedQuery) item).getDescription();
		}

		return "";
	}

	@Override
	public void expand(boolean expand) {
		if (expand) {
			panelTree.getElement().getStyle().setBottom(DEFAULT_BOTTOM, Unit.PX);
			panelDescription.setHeight(DEFAULT_HEIGHT + "px");
		}
		else {
			panelTree.getElement().getStyle().setBottom(COLLAPSE_BOTTOM, Unit.PX);
			panelDescription.setHeight(COLLAPSE_HEIGHT + "px");
		}
	}

	private void makePaletteDraggable() {
		paletteDragController.makeDraggable(itemTable);
		paletteDragController.makeDraggable(itemChart);
		paletteDragController.makeDraggable(itemList);
		paletteDragController.makeDraggable(itemCrosstab);
		paletteDragController.makeDraggable(itemImage);
		paletteDragController.makeDraggable(itemLabel);
		paletteDragController.makeDraggable(itemMap);

		dataDragController.makeDraggable(itemHyperlink);
	}

	public MetadataTree getDatasetTree() {
		return datasetTree;
	}
	
	@UiHandler("btnSearch")
	public void onSearch(ClickEvent e) {
		actualSearch = txtSearch.getText();
		datasetTree.filter(actualSearch);
	}

	@UiHandler("imgRefresh")
	public void onRefreshClick(ClickEvent event) {
		datasetTree = new MetadataTree(this, dataDragController, resourceDragController, true);
		datasetTree.setHeight("100%");
		panelTree.setWidget(datasetTree);

		showWaitPart(true);

		InfoUser infoUser = Bpm_fwr.getInstance().getInfoUser();

		FwrServiceConnection.Connect.getInstance().getMetadatas(infoUser.getGroup().getName(), new AsyncCallback<List<FwrMetadata>>() {
			public void onSuccess(List<FwrMetadata> metadatas) {
				showWaitPart(false);

				panelParent.setMetadatas(metadatas);
				datasetTree.setMetadatas(metadatas);
			}

			public void onFailure(Throwable caught) {
				showWaitPart(false);

				caught.printStackTrace();

				panelParent.setMetadatas(null);
				datasetTree.setMetadatas(null);
			}
		});
	}

	@UiHandler("imgCollapse")
	public void onCollapseClick(ClickEvent event) {
		panelParent.collapseNavigationPanel(false);
	}

	@UiHandler("imgExpand")
	public void onExpandClick(ClickEvent event) {
		panelParent.collapseNavigationPanel(true);
	}

	private void managePanel(boolean isCollapse) {
		if (isCollapse) {
			toolbarNavigation.setVisible(true);
			rightToolbarPanel.setVisible(true);
			panelTree.setVisible(true);
			metadataItemDescription.setVisible(true);

			imgExpand.setVisible(false);

			this.removeStyleName(VanillaCSS.RIGHT_TOOLBAR);
		}
		else {
			toolbarNavigation.setVisible(false);
			rightToolbarPanel.setVisible(false);
			panelTree.setVisible(false);
			metadataItemDescription.setVisible(false);

			imgExpand.setVisible(true);

			this.addStyleName(VanillaCSS.RIGHT_TOOLBAR);
		}
	}

	public void adaptSize(int navigationPanelWidth, boolean isCollapse) {
		navigationPanel.setWidth(navigationPanelWidth + "px");
		lblTitle.setVisible(isCollapse);
		managePanel(isCollapse);
	}

	@Override
	public void showWaitPart(boolean visible) {
		if (visible && !isCharging) {
			isCharging = true;

			int height = navigationPanel.getOffsetHeight();
			int width = navigationPanel.getOffsetWidth();

			greyPanel = new GreyAbsolutePanel();
			waitPanel = new WaitAbsolutePanel();

			navigationPanel.add(greyPanel);
			navigationPanel.add(waitPanel);

			if (height != 0) {
				DOM.setStyleAttribute(waitPanel.getElement(), "top", ((height / 2) - 50) + "px");
			}
			else {
				DOM.setStyleAttribute(waitPanel.getElement(), "top", 150 + "px");
			}

			if (width != 0) {
				DOM.setStyleAttribute(waitPanel.getElement(), "left", ((width / 2) - 100) + "px");
			}
			else {
				DOM.setStyleAttribute(waitPanel.getElement(), "left", 95 + "px");
			}
		}
		else if (!visible && isCharging) {
			isCharging = false;

			navigationPanel.remove(greyPanel);
			navigationPanel.remove(waitPanel);
		}
	}
}
