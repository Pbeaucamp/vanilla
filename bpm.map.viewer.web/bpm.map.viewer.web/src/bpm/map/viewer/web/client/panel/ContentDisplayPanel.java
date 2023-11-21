package bpm.map.viewer.web.client.panel;

import java.util.ArrayList;
import java.util.List;

import bpm.gwt.commons.client.panels.Tab;
import bpm.gwt.commons.client.panels.TabHeader;
import bpm.gwt.commons.client.panels.TabManager;
import bpm.vanilla.platform.core.repository.IRepositoryObject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class ContentDisplayPanel extends Composite implements TabManager {

//	private static final double DEFAULT_PERCENTAGE = 9.1;
	private static final double MIN_DEFAULT_PERCENTAGE = 0.5;

	private static ContentDisplayPanelUiBinder uiBinder = GWT.create(ContentDisplayPanelUiBinder.class);

	interface ContentDisplayPanelUiBinder extends UiBinder<Widget, ContentDisplayPanel> {
	}

	interface MyStyle extends CssResource {
		String displayNone();
	}

	@UiField
	MyStyle style;

	@UiField
	HTMLPanel tabHeaderPanel;

	@UiField
	HTMLPanel contentPanel;

	private MainPanel mainPanel;

	private ViewerPanel viewerPanel;
	private DesignerPanel designerPanel;
	
	private TabHeader selectedBtn;
	private Tab selectedPanel;
	
	private List<TabHeader> openTabs = new ArrayList<TabHeader>();

	public ContentDisplayPanel(MainPanel mainPanel) {
		initWidget(uiBinder.createAndBindUi(this));
		this.mainPanel = mainPanel;

		buildContentPanel();
		
	}

	private void buildContentPanel() {
		if (viewerPanel == null) {
			viewerPanel = new ViewerPanel(this);
		}
		
		if (designerPanel == null) {
			designerPanel = new DesignerPanel(this);
		}
		
		changeTab(viewerPanel);
		changeTab(designerPanel);
		changeTab(viewerPanel);
		updateSize(openTabs);
	}


	private void changeTab(Tab selectedPanel) {
		if (selectedBtn != null) {
			this.selectedBtn.setSelected(false);
		}

		if (this.selectedPanel != null) {
			this.selectedPanel.addStyleName(style.displayNone());
		}

		TabHeader header = selectedPanel.buildTabHeader();
		if (!header.isOpen()) {
			tabHeaderPanel.add(header);
			openTabs.add(header);
			
			updateSize(openTabs);
		}

		this.selectedBtn = header;
		this.selectedBtn.setSelected(true);

		this.selectedPanel = selectedPanel;
		this.selectedPanel.removeStyleName(style.displayNone());
		designerPanel.getDatagrid().redraw();
		if(this.selectedPanel.equals(viewerPanel)){
			viewerPanel.refreshMap();
		}
		
		if (!selectedPanel.isOpen()) {
			header.setOpen(true);
			this.contentPanel.add(selectedPanel);
		}
	}

	private void updateSize(List<TabHeader> openTabs) {
		double percentage = calcPercentage(openTabs.size());
		
		for(TabHeader tabHeader : openTabs) {
			tabHeader.applySize(percentage);
		}
	}

	private double calcPercentage(int tabNumber) {
		if(tabNumber > 0) {
			double value = (100 / tabNumber) - (0.05 * tabNumber);
			return value < MIN_DEFAULT_PERCENTAGE ? MIN_DEFAULT_PERCENTAGE : value;
		}
		else {
			return 0;
		}
	}

	@Override
	public void closeTab(TabHeader tabHeader) {
		int index = openTabs.indexOf(tabHeader);
		openTabs.remove(index);
		tabHeaderPanel.remove(tabHeader);
		contentPanel.remove(tabHeader.getTab());
		
		updateSize(openTabs);
		
		if(selectedBtn == tabHeader && !openTabs.isEmpty()) {
			if(index > 0) {
				changeTab(openTabs.get(index - 1).getTab());
			}
			else {
				changeTab(openTabs.get(0).getTab());
			}
		}
	}

	@Override
	public void selectTab(TabHeader tabHeader) {
		changeTab(tabHeader.getTab());
	}


	public void openViewer(IRepositoryObject item) {
		
	}

	public ViewerPanel getViewerPanel() {
		return viewerPanel;
	}

	public void setViewerPanel(ViewerPanel viewerPanel) {
		this.viewerPanel = viewerPanel;
	}

	public DesignerPanel getDesignerPanel() {
		return designerPanel;
	}

	public void setDesignerPanel(DesignerPanel designerPanel) {
		this.designerPanel = designerPanel;
	}
	
	public void updateViewerPanel() {
		this.viewerPanel.refresh();
	}

	public MainPanel getMainPanel() {
		return mainPanel;
	}

	public void setMainPanel(MainPanel mainPanel) {
		this.mainPanel = mainPanel;
	}
	
	
	
}
