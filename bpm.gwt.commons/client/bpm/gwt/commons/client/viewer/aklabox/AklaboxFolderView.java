package bpm.gwt.commons.client.viewer.aklabox;

import java.util.List;

import bpm.document.management.core.model.Tree;
import bpm.gwt.commons.client.ExceptionManager;
import bpm.gwt.commons.client.loading.IWait;
import bpm.gwt.commons.client.services.CommonService;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class AklaboxFolderView extends Composite {

	private static ItemPropertiesViewUiBinder uiBinder = GWT.create(ItemPropertiesViewUiBinder.class);

	interface ItemPropertiesViewUiBinder extends UiBinder<Widget, AklaboxFolderView> {
	}

	@UiField
	com.google.gwt.user.client.ui.Tree aklaboxTree;

	private IWait waitPanel;

	public AklaboxFolderView(IWait waitPanel) {
		initWidget(uiBinder.createAndBindUi(this));
		this.waitPanel = waitPanel;

		loadFolderTree();
	}

	private void loadFolderTree() {
		waitPanel.showWaitPart(true);

		CommonService.Connect.getInstance().getAklaboxTree(new AsyncCallback<List<Tree>>() {

			@Override
			public void onSuccess(List<Tree> result) {
				buildTree(result);
			}

			@Override
			public void onFailure(Throwable caught) {
				waitPanel.showWaitPart(false);

				caught.printStackTrace();

				ExceptionManager.getInstance().handleException(caught, "Unable to get folders from Aklabox.");
			}
		});
	}

	private void buildTree(List<Tree> result) {
		aklaboxTree.clear();

		if (result != null) {
			for (Tree dir : result) {
				AklaboxTreeItem item = new AklaboxTreeItem(new AklaboxFolderWidget(dir));
				aklaboxTree.addItem(item);
			}
		}

		waitPanel.showWaitPart(false);
	}

	public int getSelectedFolder() {
		if(aklaboxTree.getSelectedItem() != null) {
			AklaboxTreeItem selectedItem = (AklaboxTreeItem)aklaboxTree.getSelectedItem();
			return selectedItem.getAklaboxFolder().getId();
		}
		return -1;
	}

}
