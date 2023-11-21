package bpm.vanilla.portal.client.panels.navigation;

import java.util.List;

import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.services.ReportingService;
import bpm.gwt.commons.shared.repository.PortailRepositoryDirectory;
import bpm.vanilla.platform.core.repository.IRepositoryObject;
import bpm.vanilla.portal.client.panels.MainPanel;
import bpm.vanilla.portal.client.tree.TreeDirectory;
import bpm.vanilla.portal.client.utils.ToolsGWT;

public class ReportBackgroundPanel extends AbstractRepositoryStackPanel {

	private PortailRepositoryDirectory reportBackgrounds;
	
	public ReportBackgroundPanel(MainPanel mainPanel) {
		super(mainPanel, TypeViewer.REPORT_BACKGROUND, ToolsGWT.lblCnst.ReportBackgrounds());
	}

	public void refresh(boolean refresh, final boolean showInDocumentPanel) {
		clearSearch();

		if (refresh) {
			ReportingService.Connect.getInstance().getBackgroundReports(15, new GwtCallbackWrapper<List<IRepositoryObject>>(this, true, true) {

				@Override
				public void onSuccess(List<IRepositoryObject> result) {
					reportBackgrounds = new PortailRepositoryDirectory(ToolsGWT.lblCnst.ReportBackgrounds());

					if (result != null) {
						reportBackgrounds.setItems(result);

						displayData(reportBackgrounds, getSearchValue());

//						if(showInDocumentPanel) {
//							showInDirectoryView(reportBackgrounds);
//						}
						loaded = true;
					}
				}
			}.getAsyncCallback());
		}
		else {
			displayData(reportBackgrounds, "");

//			if (reportBackgrounds != null && showInDocumentPanel) {
//				showInDirectoryView(reportBackgrounds);
//			}
		}
	}

	@Override
	public String getSearchValue() {
		String search = txtSearch.getText();
		return search;
	}

	@Override
	public void removeSearch() {
		clearSearch();
		displayData(reportBackgrounds, getSearchValue());
	}
	
	@Override
	public void search() {
		setSearch(true);
		displayData(reportBackgrounds, getSearchValue());
	}

	@Override
	public void loadChild(TreeDirectory item) { }
}
