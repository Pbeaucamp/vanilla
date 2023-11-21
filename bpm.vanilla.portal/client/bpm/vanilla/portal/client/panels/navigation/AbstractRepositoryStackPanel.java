package bpm.vanilla.portal.client.panels.navigation;

import bpm.gwt.commons.client.custom.CustomHTML;
import bpm.gwt.commons.client.loading.StackLayoutWaitPanel;
import bpm.gwt.commons.shared.repository.PortailRepositoryDirectory;
import bpm.gwt.commons.shared.repository.PortailRepositoryItem;
import bpm.vanilla.platform.core.repository.IRepositoryObject;
import bpm.vanilla.platform.core.repository.ReportBackground;
import bpm.vanilla.portal.client.Listeners.DirectoryContextMenuHandler;
import bpm.vanilla.portal.client.panels.MainPanel;
import bpm.vanilla.portal.client.panels.center.RepositoryContentPanel;
import bpm.vanilla.portal.client.tree.TreeDirectory;
import bpm.vanilla.portal.client.tree.TreeDirectoryItem;
import bpm.vanilla.portal.client.tree.TreeItemOk;
import bpm.vanilla.portal.client.tree.TreeReportBackground;
import bpm.vanilla.portal.client.utils.ToolsGWT;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

public abstract class AbstractRepositoryStackPanel extends StackLayoutWaitPanel {

	private static AbstractRepositoryStackPanelUiBinder uiBinder = GWT.create(AbstractRepositoryStackPanelUiBinder.class);

	interface AbstractRepositoryStackPanelUiBinder extends UiBinder<Widget, AbstractRepositoryStackPanel> {
	}

	interface MyStyle extends CssResource {
		String parentPanel();
	}

	@UiField
	MyStyle style;

	@UiField
	SimplePanel panelContent;

	@UiField
	HTMLPanel panelSearch;

	@UiField
	TextBox txtSearch;

	@UiField
	Button btnSearch;

	@UiField
	Image btnClear;

	protected MainPanel mainPanel;

	private HandlerRegistration handlerRegistration;
	private Tree treePanel;

	private TypeViewer typeViewer;
	private String header;

	private PortailRepositoryDirectory selectedItem;

	private boolean isSearch = false;
	protected boolean loaded = false;

	public AbstractRepositoryStackPanel(MainPanel mainPanel, TypeViewer typeViewer, String header) {
		super("");
		add(uiBinder.createAndBindUi(this));
		this.mainPanel = mainPanel;
		this.typeViewer = typeViewer;
		this.header = header;

		treePanel = new Tree();
//		treePanel.addSelectionHandler(treeSelectionHandler);

		panelContent.setWidget(treePanel);

		this.addStyleName(style.parentPanel());
		btnClear.setVisible(false);
	}

	public void setSearchVisible(boolean visible) {
		this.isSearch = visible;
		panelSearch.setVisible(visible);
		removeSearch();
	}

	/**
	 * Once we ve switched to this view (watch list), the directory viewer
	 * should show us (the list of last used items that is).
	 * 
	 */
	protected void showInDirectoryView(PortailRepositoryDirectory parent) {
		RepositoryContentPanel repositoryPanel = mainPanel.getRepositoryContentPanel();
		if (repositoryPanel != null) {
			repositoryPanel.displayData(parent, getTypeViewer());
		}
	}

	public void displayData(PortailRepositoryDirectory parent, String search) {
		this.selectedItem = parent;

		TreeItemOk root = new TreeItemOk(new CustomHTML(header), typeViewer);
		buildChildDir(selectedItem, root, search);

		treePanel.clear();
		if (root.getChildCount() > 0) {
			int itemNumber = root.getChildCount();
			for (int i = 0; i < itemNumber; i++) {
				treePanel.addItem(root.getChild(0));
			}
		}
	}

	protected void buildChildDir(PortailRepositoryDirectory directory, TreeItemOk app, String search) {
		if (directory.getItems() != null) {
			for (IRepositoryObject item : directory.getItems()) {
				if (item instanceof PortailRepositoryItem) {
					PortailRepositoryItem element = (PortailRepositoryItem) item;
					if (!isSearch || search.isEmpty() || element.getName().toLowerCase().contains(search.toLowerCase())) {
						TreeDirectoryItem leaf = new TreeDirectoryItem(mainPanel.getContentDisplayPanel(), element, typeViewer, search);
						leaf.setParent(app);
//						leaf.addContextMenuHandler(new DirectoryItemContextMenuHandler(mainPanel.getContentDisplayPanel(), leaf, typeViewer));
//						leaf.addDoubleClickHandler(new DirectoryItemDoubleClickHandler(mainPanel.getContentDisplayPanel(), leaf));

						app.addItem(leaf);
					}
				}
				else if (item instanceof PortailRepositoryDirectory) {
					PortailRepositoryDirectory dir = (PortailRepositoryDirectory) item;

					TreeDirectory child = new TreeDirectory(dir, typeViewer);
					child.addContextMenuHandler(new DirectoryContextMenuHandler(mainPanel.getContentDisplayPanel(), dir));
					child.addClickHandler(new TreeSelectionHandler(child));

					if (dir.isChildLoad() && dir.hasChild()) {
						buildChildDir(dir, child, search);
					}
					else if (!dir.isChildLoad()) {
						TreeItem treeItem = new TreeItem(new Label(ToolsGWT.lblCnst.Loading()));
						child.addItem(treeItem);
					}

					child.setParent(app);
					app.addItem(child);
				}
				else if (item instanceof ReportBackground) {
					ReportBackground element = (ReportBackground) item;
					if (!isSearch || search.isEmpty() || element.getName().toLowerCase().contains(search.toLowerCase())) {
						TreeReportBackground leaf = new TreeReportBackground(mainPanel, mainPanel.getContentDisplayPanel(), element, typeViewer, search);
						leaf.setParent(app);

						app.addItem(leaf);
					}
				}
			}
		}
	}

	public void setOpenHandler(OpenHandler<TreeItem> handler) {
		if (handlerRegistration != null) {
			handlerRegistration.removeHandler();
		}
		handlerRegistration = treePanel.addOpenHandler(handler);
	}

	public TypeViewer getTypeViewer() {
		return typeViewer;
	}

	public void setSearchWidth(int width) {
		txtSearch.setWidth(width + "px");
	}

	public void clearSearch() {
		this.isSearch = false;

		txtSearch.setText("");

		btnClear.setVisible(false);
	}

	public void setSearch(boolean isSearch) {
		this.isSearch = isSearch;

		btnClear.setVisible(true);
	}
	
	private class TreeSelectionHandler implements ClickHandler {

		private TreeDirectory treeDirectory;
		
		public TreeSelectionHandler(TreeDirectory treeDirectory) {
			this.treeDirectory = treeDirectory;
		}
		
		@Override
		public void onClick(ClickEvent event) {
			loadChild(treeDirectory);
			
			treeDirectory.setState(true);
			
			PortailRepositoryDirectory dir = treeDirectory.getDirectory();
			
			RepositoryContentPanel d = mainPanel.openRepositoryContentPanel();
			if (d != null) {
				d.displayData(dir, typeViewer);
			}
		}
	}

	@UiHandler("btnClear")
	public void onClearSearchClick(ClickEvent event) {
		removeSearch();
	}

	@UiHandler("btnSearch")
	public void onSearchClick(ClickEvent event) {
		search();
	}

	public abstract void refresh(boolean refresh, boolean showInDocumentPanel);

	public abstract String getSearchValue();

	public abstract void search();

	public abstract void removeSearch();
	
	public abstract void loadChild(TreeDirectory item);
}
