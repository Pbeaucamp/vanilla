package bpm.fm.designer.web.client.dialog;

import bpm.fm.api.model.Metric;
import bpm.fm.designer.web.client.Messages;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.tree.RepositoryTree;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.repository.RepositoryItem;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

public class RepositoryDialog extends AbstractDialogBox {

	private static RepositoryDialogUiBinder uiBinder = GWT
			.create(RepositoryDialogUiBinder.class);

	interface RepositoryDialogUiBinder extends
			UiBinder<Widget, RepositoryDialog> {
	}
	
	@UiField
	HTMLPanel contentPanel, treePanel;

	private Metric metric;

	private RepositoryTree repositoryTree;

	private RepositoryItem item;
	
	private boolean isConfirm = false;
	
	private int itemType;

	public RepositoryDialog(Metric metric, int itemType) {	
		super(itemType == IRepositoryApi.GTW_TYPE ? Messages.lbl.gatewaySelect() : Messages.lbl.cubeSelect(), false, true);
		
		this.metric = metric;
		this.itemType = itemType;
		
		setWidget(uiBinder.createAndBindUi(this));
		createButtonBar(Messages.lbl.Ok(), okHandler, Messages.lbl.Cancel(), cancelHandler);
		
		repositoryTree = new RepositoryTree(new SelectionHandler<TreeItem>() {			
			@Override
			public void onSelection(SelectionEvent<TreeItem> event) {
				item = repositoryTree.getSelectedValue();
			}
		});
		treePanel.add(repositoryTree);
		repositoryTree.loadTree(itemType);
	}

	private ClickHandler okHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			
			if(itemType == IRepositoryApi.GTW_TYPE) {
				metric.setEtlName(item.getId() + ";" + item.getName());
			}
			isConfirm = true;
			RepositoryDialog.this.hide();
			
		}
	};
	
	private ClickHandler cancelHandler = new ClickHandler() {	
		@Override
		public void onClick(ClickEvent event) {
			RepositoryDialog.this.hide();
		}
	};
	
	public boolean isConfirm() {
		return isConfirm;
	}
}
