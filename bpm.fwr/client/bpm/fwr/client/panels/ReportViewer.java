package bpm.fwr.client.panels;

import java.util.ArrayList;
import java.util.List;

import bpm.fwr.api.beans.FWRReport;
import bpm.fwr.api.beans.dataset.DataSet;
import bpm.fwr.client.WysiwygPanel;
import bpm.gwt.commons.client.listeners.IClose;
import bpm.gwt.commons.client.panels.AbstractCenterPanel;
import bpm.gwt.commons.client.panels.AbstractTabHeader;
import bpm.gwt.commons.client.panels.Tab;
import bpm.gwt.commons.client.panels.TabManager;
import bpm.vanilla.platform.core.repository.IRepositoryObject;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class ReportViewer extends AbstractCenterPanel implements TabManager {

	private static final double DEFAULT_PERCENTAGE = 9.1;
	private static final double MIN_DEFAULT_PERCENTAGE = 0.5;

	private static ReportViewerUiBinder uiBinder = GWT.create(ReportViewerUiBinder.class);

	interface ReportViewerUiBinder extends UiBinder<Widget, ReportViewer> {
	}

	interface MyStyle extends CssResource {
		String displayNone();

		String parentPanel();
	}

	@UiField
	MyStyle style;

	@UiField
	HTMLPanel tabHeaderPanel, contentPanel;

	private WysiwygPanel mainPanel;

	private AbstractTabHeader selectedBtn;
	private Tab selectedPanel;

	private List<AbstractTabHeader> openTabs = new ArrayList<AbstractTabHeader>();

	public ReportViewer(WysiwygPanel mainPanel) {
		super("");
		this.add(uiBinder.createAndBindUi(this));

		this.mainPanel = mainPanel;

		this.addStyleName(style.parentPanel());
	}

	public void openViewer(WysiwygPanel wysiwygPanel, String nameReport, FWRReport report, PickupDragController reportWidgetDragController, PickupDragController paletteDragController, PickupDragController dragController, PickupDragController dataDragController, PickupDragController groupDragController, PickupDragController detailDragController, PickupDragController resourceDragController, PickupDragController cellsDragController) {

		Viewer viewer = new Viewer(wysiwygPanel, this, nameReport, reportWidgetDragController, paletteDragController, dragController, dataDragController, groupDragController, detailDragController, resourceDragController, cellsDragController);
		if (report != null) {
			viewer.openReport(report);
		}
		changeTab(viewer);
	}
	
	public void openViewerFromQuery(WysiwygPanel wysiwygPanel, String nameReport, FWRReport report, PickupDragController reportWidgetDragController, PickupDragController paletteDragController, PickupDragController dragController, PickupDragController dataDragController, PickupDragController groupDragController, PickupDragController detailDragController, PickupDragController resourceDragController, PickupDragController cellsDragController, DataSet dataset, Boolean formatted) {

		Viewer viewer = new Viewer(wysiwygPanel, this, nameReport, reportWidgetDragController, paletteDragController, dragController, dataDragController, groupDragController, detailDragController, resourceDragController, cellsDragController);
		if (report == null) {
			report = viewer.getReportPanel().getReportSheet().generateReport();
			//report=new FWRReport();
		}
		viewer.openReportFromQuery(report, dataset, formatted);
		changeTab(viewer);
	}

	private void changeTab(Tab selectedPanel) {
		if (selectedBtn != null) {
			this.selectedBtn.setSelected(false);
		}

		if (this.selectedPanel != null) {
			this.selectedPanel.addStyleName(style.displayNone());
		}

		AbstractTabHeader header = selectedPanel.buildTabHeader();
		if (!header.isOpen()) {
			tabHeaderPanel.add(header);
			openTabs.add(header);

			updateSize(openTabs);
		}

		this.selectedBtn = header;
		this.selectedBtn.setSelected(true);

		this.selectedPanel = selectedPanel;
		this.selectedPanel.removeStyleName(style.displayNone());

		if (!selectedPanel.isOpen()) {
			header.setOpen(true);
			this.contentPanel.add(selectedPanel);
		}

		if (selectedPanel instanceof Viewer) {
			String selectedLanguage = ((Viewer) selectedPanel).getReportPanel().getReportSheet().getReportParameters().getSelectedLanguage();
			mainPanel.updateDatasetTreePart(selectedLanguage);
		}
	}

	private void updateSize(List<AbstractTabHeader> openTabs) {
		double percentage = calcPercentage(openTabs.size());

		for (AbstractTabHeader tabHeader : openTabs) {
			tabHeader.applySize(percentage);
		}
	}

	private double calcPercentage(int tabNumber) {
		if (tabNumber <= 10) {
			return DEFAULT_PERCENTAGE;
		}
		else {
			double value = (100 / tabNumber) - (0.05 * tabNumber);
			return value < MIN_DEFAULT_PERCENTAGE ? MIN_DEFAULT_PERCENTAGE : value;
		}
	}

	@Override
	public void closeTab(AbstractTabHeader tabHeader) {
		Tab tab = tabHeader.getTab();
		if(tab instanceof IClose) {
			((IClose) tab).close();
		}
		
		int index = openTabs.indexOf(tabHeader);
		openTabs.remove(index);
		tabHeaderPanel.remove(tabHeader);
		contentPanel.remove(tabHeader.getTab());

		updateSize(openTabs);

		if (selectedBtn == tabHeader && !openTabs.isEmpty()) {
			if (index > 0) {
				changeTab(openTabs.get(index - 1).getTab());
			}
			else {
				changeTab(openTabs.get(0).getTab());
			}
		}
	}

	@Override
	public void selectTab(AbstractTabHeader tabHeader) {
		changeTab(tabHeader.getTab());
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
