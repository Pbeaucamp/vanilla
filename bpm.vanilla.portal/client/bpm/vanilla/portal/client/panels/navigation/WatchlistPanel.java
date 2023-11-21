package bpm.vanilla.portal.client.panels.navigation;

import bpm.gwt.commons.client.ExceptionManager;
import bpm.gwt.commons.shared.repository.PortailRepositoryDirectory;
import bpm.vanilla.portal.client.biPortal;
import bpm.vanilla.portal.client.panels.MainPanel;
import bpm.vanilla.portal.client.services.BiPortalService;
import bpm.vanilla.portal.client.tree.TreeDirectory;
import bpm.vanilla.portal.client.utils.ToolsGWT;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class WatchlistPanel extends AbstractRepositoryStackPanel {

	private PortailRepositoryDirectory watchlisted;
	
	public WatchlistPanel(MainPanel mainPanel) {
		super(mainPanel, TypeViewer.WATCH_LIST, ToolsGWT.lblCnst.MyWatchList());
	}

	public void refresh(boolean refresh, final boolean showInDocumentPanel) {
		clearSearch();
		
		showWaitPart(true);

		if (refresh) {
			BiPortalService.Connect.getInstance().getMyWatchList(new AsyncCallback<PortailRepositoryDirectory>() {

				public void onSuccess(PortailRepositoryDirectory result) {
					biPortal.get().getInfoUser().setMyWatchlist(result);
					
					if (result != null) {
						watchlisted = result;
						watchlisted.setName(ToolsGWT.lblCnst.MyWatchList());

						displayData(watchlisted, getSearchValue());

						if(showInDocumentPanel) {
							showInDirectoryView(watchlisted);
						}
						loaded = true;
					}

					showWaitPart(false);
				}

				public void onFailure(Throwable caught) {
					showWaitPart(false);

					caught.printStackTrace();

					ExceptionManager.getInstance().handleException(caught, ToolsGWT.lblCnst.Error());
				}
			});
		}
		else {
			displayData(watchlisted, "");

			if (watchlisted != null && showInDocumentPanel) {
				showInDirectoryView(watchlisted);
			}

			showWaitPart(false);
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
		displayData(watchlisted, getSearchValue());
	}
	
	@Override
	public void search() {
		setSearch(true);
		displayData(watchlisted, getSearchValue());
	}

	@Override
	public void loadChild(TreeDirectory item) { }
}
