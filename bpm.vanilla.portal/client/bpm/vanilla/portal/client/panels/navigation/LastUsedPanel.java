package bpm.vanilla.portal.client.panels.navigation;

import bpm.gwt.commons.client.ExceptionManager;
import bpm.gwt.commons.shared.repository.PortailRepositoryDirectory;
import bpm.vanilla.portal.client.biPortal;
import bpm.vanilla.portal.client.panels.MainPanel;
import bpm.vanilla.portal.client.services.BiPortalService;
import bpm.vanilla.portal.client.tree.TreeDirectory;
import bpm.vanilla.portal.client.utils.ToolsGWT;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class LastUsedPanel extends AbstractRepositoryStackPanel {

	private PortailRepositoryDirectory lastConsulted;
	
	public LastUsedPanel(MainPanel mainPanel) {
		super(mainPanel, TypeViewer.LAST_USED, ToolsGWT.lblCnst.LastUsed());
	}

	public void refresh(boolean refresh, final boolean showInDocumentPanel) {
		clearSearch();
		
		showWaitPart(true);

		if (refresh) {
			BiPortalService.Connect.getInstance().getLastConsulted(biPortal.get().getInfoUser().getGroup().getName(), new AsyncCallback<PortailRepositoryDirectory>() {

				public void onSuccess(PortailRepositoryDirectory result) {
					if (result != null) {
						lastConsulted = result;
						lastConsulted.setName(ToolsGWT.lblCnst.LastUsed());

						displayData(lastConsulted, getSearchValue());

						if(showInDocumentPanel) {
							showInDirectoryView(lastConsulted);
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
			displayData(lastConsulted, "");

			if (lastConsulted != null && showInDocumentPanel) {
				showInDirectoryView(lastConsulted);
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
		displayData(lastConsulted, getSearchValue());
	}
	
	@Override
	public void search() {
		setSearch(true);
		displayData(lastConsulted, getSearchValue());
	}

	@Override
	public void loadChild(TreeDirectory item) { }
}
