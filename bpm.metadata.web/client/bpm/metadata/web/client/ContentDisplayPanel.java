package bpm.metadata.web.client;

import java.util.ArrayList;
import java.util.List;

import bpm.gwt.commons.client.loading.IWait;
import bpm.gwt.commons.client.panels.AbstractTabHeader;
import bpm.gwt.commons.client.panels.Tab;
import bpm.gwt.commons.client.panels.TabManager;
import bpm.gwt.commons.shared.InfoUser;
import bpm.gwt.commons.shared.fmdt.metadata.Metadata;
import bpm.metadata.web.client.panels.MetadataPanel;
import bpm.vanilla.platform.core.repository.IRepositoryObject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class ContentDisplayPanel extends Composite implements TabManager {

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

	private InfoUser infoUser;

	private AbstractTabHeader selectedBtn;
	private Tab selectedPanel;

	private List<AbstractTabHeader> openTabs = new ArrayList<AbstractTabHeader>();

	public ContentDisplayPanel(IWait waitPanel, InfoUser infoUser) {
		initWidget(uiBinder.createAndBindUi(this));
		this.infoUser = infoUser;
		
		displayCreation();
	}

	public void displayCreation() {
		MetadataPanel creationPanel = new MetadataPanel(this, infoUser.getUser().getId());
		changeTab(creationPanel, true);
	}

	public void openCreation(Metadata metadata) {
		if (selectedPanel instanceof MetadataPanel) {
			MetadataPanel panel = (MetadataPanel) selectedPanel;
			if (!panel.isLoaded()) {
				panel.loadMetadata(metadata, false);
				return;
			}
		}
		
		MetadataPanel creationPanel = new MetadataPanel(this, infoUser.getUser().getId(), metadata);
		changeTab(creationPanel, true);
	}

	private void changeTab(Tab selectedPanel, boolean select) {
		if (select && selectedBtn != null) {
			this.selectedBtn.setSelected(false);
		}

		if (select && this.selectedPanel != null) {
			this.selectedPanel.addStyleName(style.displayNone());
		}

		AbstractTabHeader header = selectedPanel.buildTabHeader();
		if (!header.isOpen()) {
			tabHeaderPanel.add(header);
			openTabs.add(header);

			updateSize(openTabs);
		}

		if (select) {
			this.selectedBtn = header;
			this.selectedBtn.setSelected(true);

			this.selectedPanel = selectedPanel;
			this.selectedPanel.removeStyleName(style.displayNone());

			if (!selectedPanel.isOpen()) {
				header.setOpen(true);
				this.contentPanel.add(selectedPanel);
			}
			
			selectedPanel.doActionAfterSelection();
		}
	}

	private void updateSize(List<AbstractTabHeader> openTabs) {
		double percentage = calcPercentage(openTabs.size());

		for (AbstractTabHeader tabHeader : openTabs) {
			tabHeader.applySize(percentage);
		}
	}

	private double calcPercentage(int tabNumber) {
		if (tabNumber > 0) {
			double value = (100 / tabNumber) - (0.05 * tabNumber);
			return value < MIN_DEFAULT_PERCENTAGE ? MIN_DEFAULT_PERCENTAGE : value;
		}
		else {
			return 0;
		}
	}

	@Override
	public void closeTab(AbstractTabHeader tabHeader) {
		int index = openTabs.indexOf(tabHeader);
		openTabs.remove(index);
		tabHeaderPanel.remove(tabHeader);
		contentPanel.remove(tabHeader.getTab());

		updateSize(openTabs);

		if (selectedBtn == tabHeader && !openTabs.isEmpty()) {
			if (index > 0) {
				changeTab(openTabs.get(index - 1).getTab(), true);
			}
			else {
				changeTab(openTabs.get(0).getTab(), true);
			}
		}
	}

	@Override
	public void selectTab(AbstractTabHeader tabHeader) {
		changeTab(tabHeader.getTab(), true);
	}

	@Override
	public void openViewer(IRepositoryObject item) { }

	@Override
	public int getIndex(AbstractTabHeader tabHeader) {
		return -1;
	}

	@Override
	public void updatePosition(String tabId, int index) { }

	@Override
	public void postProcess() {
		// TODO Auto-generated method stub
		
	}
}
