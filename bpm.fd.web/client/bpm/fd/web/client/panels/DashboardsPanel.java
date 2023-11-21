package bpm.fd.web.client.panels;

import java.util.ArrayList;
import java.util.List;

import bpm.fd.core.Dashboard;
import bpm.fd.web.client.I18N.Labels;
import bpm.gwt.commons.client.InformationsDialog;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.loading.IWait;
import bpm.gwt.commons.client.panels.AbstractTabHeader;
import bpm.gwt.commons.client.panels.Tab;
import bpm.gwt.commons.client.panels.TabManager;
import bpm.gwt.commons.client.tree.TreeObjectWidget.IDragListener;
import bpm.gwt.commons.shared.InfoUser;
import bpm.vanilla.platform.core.repository.IDashboard;
import bpm.vanilla.platform.core.repository.IRepositoryObject;
import bpm.vanilla.platform.core.repository.Template;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class DashboardsPanel extends Composite implements TabManager {

	private static final double MIN_DEFAULT_PERCENTAGE = 0.5;

	private static ContentDisplayPanelUiBinder uiBinder = GWT.create(ContentDisplayPanelUiBinder.class);

	interface ContentDisplayPanelUiBinder extends UiBinder<Widget, DashboardsPanel> {
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
	private IDragListener meatadataDragListener;

	private AbstractTabHeader selectedBtn;
	private Tab selectedPanel;

	private List<AbstractTabHeader> openTabs = new ArrayList<AbstractTabHeader>();

	public DashboardsPanel(IWait waitPanel, InfoUser infoUser, IDragListener meatadataDragListener) {
		initWidget(uiBinder.createAndBindUi(this));
		this.infoUser = infoUser;
		this.meatadataDragListener = meatadataDragListener;
		displayCreation();
	}

	public void displayCreation() {
		CreationPanel creationPanel = new CreationPanel(this, infoUser, meatadataDragListener);
		changeTab(creationPanel, true);
	}

	public void openCreation(Dashboard dashboard) {
		if (selectedPanel instanceof CreationPanel) {
			CreationPanel panel = (CreationPanel) selectedPanel;
			if (!panel.isLoaded()) {
				panel.loadDashboard(dashboard, false);
				return;
			}
		}

		CreationPanel creationPanel = new CreationPanel(this, infoUser, dashboard, meatadataDragListener);
		changeTab(creationPanel, true);
	}

	public void openTemplate(Template<IDashboard> template) {
		if (selectedPanel instanceof CreationPanel) {
			CreationPanel panel = (CreationPanel) selectedPanel;
			if (!panel.isLoaded()) {
				panel.loadTemplate(template, false);
				return;
			}
		}

		CreationPanel creationPanel = new CreationPanel(this, infoUser, template, meatadataDragListener);
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
	public void closeTab(final AbstractTabHeader tabHeader) {
		boolean isSaved = ((CreationPanel) tabHeader.getTab()).getWorkspacePanel().dashboardIsSaved();
		boolean isModified = ((CreationPanel) tabHeader.getTab()).isModified();
		
		if (!isModified) {
			close(tabHeader);
			return;
		}
		
		String messageSave = Labels.lblCnst.SaveDashboardBeforeExit();
		if (isSaved) {
			messageSave = Labels.lblCnst.UpdateDashboardBeforeExit();
		}

		final InformationsDialog dial = new InformationsDialog(Labels.lblCnst.SaveDashboard(), LabelsConstants.lblCnst.Yes(), LabelsConstants.lblCnst.No(), messageSave, true);
		dial.addCloseHandler(new CloseHandler<PopupPanel>() {
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				if (dial.isClose()) {
					return;
				}
				
				if (dial.isConfirm()) {
					((CreationPanel) tabHeader.getTab()).getWorkspacePanel().onSave(null, false, true);
				}
				else {
					close(tabHeader);
				}
			}
		});
		dial.center();
	}
	
	public void close(AbstractTabHeader tabHeader) {
		int index = openTabs.indexOf(tabHeader);
		
		openTabs.remove(index);
		tabHeaderPanel.remove(tabHeader);
		contentPanel.remove(tabHeader.getTab());

		updateSize(openTabs);

		if (selectedBtn == tabHeader && !openTabs.isEmpty()) {
			if (index > 0) {
				System.out.println(index);
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
	public void openViewer(IRepositoryObject item) {
	}

	@Override
	public int getIndex(AbstractTabHeader tabHeader) {
		return -1;
	}

	@Override
	public void updatePosition(String tabId, int index) {
	}

	@Override
	public void postProcess() {
		// TODO Auto-generated method stub
		
	}
}
