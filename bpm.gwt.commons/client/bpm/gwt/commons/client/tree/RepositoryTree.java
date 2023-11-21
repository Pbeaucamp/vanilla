package bpm.gwt.commons.client.tree;

import java.util.List;

import bpm.gwt.commons.client.ExceptionManager;
import bpm.gwt.commons.client.loading.GreyAbsolutePanel;
import bpm.gwt.commons.client.loading.IWait;
import bpm.gwt.commons.client.loading.WaitAbsolutePanel;
import bpm.gwt.commons.client.services.CommonService;
import bpm.gwt.commons.shared.repository.PortailRepositoryItem;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.repository.IRepositoryObject;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

public class RepositoryTree extends Composite implements IWait {
	
	private static RepositoryTreeUiBinder uiBinder = GWT.create(RepositoryTreeUiBinder.class);

	interface RepositoryTreeUiBinder extends UiBinder<Widget, RepositoryTree> {
	}
	
	@UiField
	HTMLPanel repositoryPanel;
	
	@UiField
	Tree repositoryTree;
	
	private boolean isCharging = false;
	private GreyAbsolutePanel greyPanel;
	private WaitAbsolutePanel waitPanel;
	private int indexCharging = 0;

	private int selectedItem = -1;

	public RepositoryTree(SelectionHandler<TreeItem> repositoryHandler) {
		initWidget(uiBinder.createAndBindUi(this));
		
		repositoryTree.addSelectionHandler(repositoryHandler);
	}
	
	public void loadTree(int typeRepository) {
		showWaitPart(true);
		
		CommonService.Connect.getInstance().getRepositoryTree(typeRepository, new AsyncCallback<List<RepositoryDirectory>>() {
			
			@Override
			public void onSuccess(List<RepositoryDirectory> result) {
				buildTree(result);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				showWaitPart(false);
				
				caught.printStackTrace();
				
				ExceptionManager.getInstance().handleException(caught, "Unable to get repository content.");
			}
		});
	}
	
	public void loadTree(int typeRepository, int itemToSelect) {
		selectedItem  = itemToSelect;
		loadTree(typeRepository);
	}
	
	public void loadTree(String url, String login, String pass, Group group, Repository repo, int typeRepository) {
		showWaitPart(true);
		
		CommonService.Connect.getInstance().getRepositoryTree(url, login , pass, group, repo, typeRepository, new AsyncCallback<List<RepositoryDirectory>>() {
			
			@Override
			public void onSuccess(List<RepositoryDirectory> result) {
				buildTree(result);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				showWaitPart(false);
				
				caught.printStackTrace();
				
				ExceptionManager.getInstance().handleException(caught, "Unable to get repository content.");
			}
		});
	}
	
	private void buildTree(List<RepositoryDirectory> result) {
		repositoryTree.clear();
		
		if(result != null) {
			for(RepositoryDirectory dir : result) {
				RepositoryTreeItem item = new RepositoryTreeItem(new RepositoryObjectWidget(dir, false), false, selectedItem, repositoryTree);
				repositoryTree.addItem(item);
			}
		}
		
		showWaitPart(false);
	}
	
	public RepositoryItem getSelectedValue() {
		RepositoryTreeItem item = repositoryTree.getSelectedItem() != null ? (RepositoryTreeItem)repositoryTree.getSelectedItem() : null;
		if(item == null || item.getRepositoryObject() instanceof RepositoryDirectory) {
			return null;
		}
		return (RepositoryItem)item.getRepositoryObject();
	}
	
	public RepositoryDirectory getSelectedDirectory() {
		RepositoryTreeItem dir = repositoryTree.getSelectedItem() != null ? (RepositoryTreeItem)repositoryTree.getSelectedItem() : null;
		if(dir == null || dir.getRepositoryObject() instanceof RepositoryItem) {
			return null;
		}
		return (RepositoryDirectory)dir.getRepositoryObject();
	}
	
	public PortailRepositoryItem getSelectedFmdt(){
		RepositoryTreeItem dir = repositoryTree.getSelectedItem() != null ? (RepositoryTreeItem)repositoryTree.getSelectedItem() : null;
		if(dir != null && dir.getRepositoryObject() instanceof PortailRepositoryItem) {
			PortailRepositoryItem portailItem = (PortailRepositoryItem) dir.getRepositoryObject();;
			if (portailItem.getType() == IRepositoryApi.FMDT_TYPE || portailItem.getType() == IRepositoryApi.FMDT_DRILLER_TYPE || portailItem.getType() == IRepositoryApi.FMDT_CHART_TYPE) {
				return portailItem;
			}
		}
		return null;
	}
	
	@Override
	public void showWaitPart(boolean visible) {
		if (visible) {
			indexCharging++;

			if (!isCharging) {
				isCharging = true;

				greyPanel = new GreyAbsolutePanel();
				waitPanel = new WaitAbsolutePanel();

				repositoryPanel.add(greyPanel);
				repositoryPanel.add(waitPanel);
				
				int width = repositoryPanel.getOffsetWidth();

				DOM.setStyleAttribute(waitPanel.getElement(), "top", 50 + "px");
				if(width > 0) {
					DOM.setStyleAttribute(waitPanel.getElement(), "left", ((width / 2) - 100) + "px");
				}
				else {
					DOM.setStyleAttribute(waitPanel.getElement(), "left", 120 + "px");
				}
			}
		}
		else if (!visible) {
			indexCharging--;

			if (isCharging && indexCharging == 0) {
				isCharging = false;

				repositoryPanel.remove(greyPanel);
				repositoryPanel.remove(waitPanel);
			}
		}
	}

	public Tree getTree() {
		return repositoryTree;
	}
}
