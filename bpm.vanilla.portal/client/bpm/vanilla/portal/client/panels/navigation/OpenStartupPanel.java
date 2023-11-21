package bpm.vanilla.portal.client.panels.navigation;

import bpm.gwt.commons.client.ExceptionManager;
import bpm.gwt.commons.shared.repository.PortailRepositoryDirectory;
import bpm.vanilla.portal.client.biPortal;
import bpm.vanilla.portal.client.panels.MainPanel;
import bpm.vanilla.portal.client.services.BiPortalService;
import bpm.vanilla.portal.client.tree.TreeDirectory;
import bpm.vanilla.portal.client.utils.ToolsGWT;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class OpenStartupPanel extends AbstractRepositoryStackPanel {

	private PortailRepositoryDirectory openItems;
	
	public OpenStartupPanel(MainPanel mainPanel) {
		super(mainPanel, TypeViewer.OPEN_ON_STARTUP, ToolsGWT.lblCnst.OnOpen());
	}

	@Override
	public void refresh(boolean refresh, final boolean showInDocumentPanel) {
		clearSearch();
		
		showWaitPart(true);
		
		if(refresh){
			BiPortalService.Connect.getInstance().getOpenItems(new AsyncCallback<PortailRepositoryDirectory>() {
	
				public void onFailure(Throwable caught) {
					showWaitPart(false);
					
					caught.printStackTrace();

					ExceptionManager.getInstance().handleException(caught, ToolsGWT.lblCnst.Error());
				}
	
				public void onSuccess(PortailRepositoryDirectory result) {
					biPortal.get().getInfoUser().setOpenOnStartup(result);
					
					if (result != null) {
						openItems = result;
						openItems.setName(ToolsGWT.lblCnst.OnOpen());

						displayData(openItems, getSearchValue());

						if(showInDocumentPanel) {
							showInDirectoryView(openItems);
						}
						loaded = true;
					}

					showWaitPart(false);
				}
				
			});	
		}
		else {
			displayData(openItems, "");

			if (openItems != null && showInDocumentPanel) {
				showInDirectoryView(openItems);
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
		displayData(openItems, getSearchValue());
	}
	
	@Override
	public void search() {
		setSearch(true);
		displayData(openItems, getSearchValue());
	}

	@Override
	public void loadChild(TreeDirectory item) { }
}
