package bpm.fm.designer.web.client.dialog;

import bpm.fm.api.model.Metric;
import bpm.fm.api.model.MetricLinkedItem;
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
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

public class LinkedObjectDialog extends AbstractDialogBox {

	private static LinkedObjectDialogUiBinder uiBinder = GWT.create(LinkedObjectDialogUiBinder.class);

	interface LinkedObjectDialogUiBinder extends UiBinder<Widget, LinkedObjectDialog> {
	}
	
	@UiField
	HTMLPanel contentPanel, treePanel;
	
	@UiField(provided = true)
	ListBox lstItems;

	private Metric metric;

	private RepositoryTree repositoryTree;

	private RepositoryItem item;
	
	private boolean isConfirm = false;

	public LinkedObjectDialog(Metric metric) {
		super("Link item to metric", false, true);
		lstItems = new ListBox(true);
		this.metric = metric;
		
		setWidget(uiBinder.createAndBindUi(this));
		createButtonBar(Messages.lbl.Ok(), okHandler, Messages.lbl.Cancel(), cancelHandler);
		
		repositoryTree = new RepositoryTree(new SelectionHandler<TreeItem>() {			
			@Override
			public void onSelection(SelectionEvent<TreeItem> event) {
				item = repositoryTree.getSelectedValue();
			}
		});
		treePanel.add(repositoryTree);
		repositoryTree.loadTree(IRepositoryApi.FASD_TYPE);
		
		lstItems.clear();
		for(MetricLinkedItem it : metric.getLinkedItems()) {
			lstItems.addItem(it.getItemName(), it.getItemId() + "");
		}
	}
	
	@UiHandler("btnAdd")
	public void onAdd(ClickEvent event) {
		boolean inThelist = false;
		for(int i = 0 ; i < lstItems.getItemCount() ; i++) {
			if(((String)lstItems.getValue(i)).equals(String.valueOf(item.getId()))) {
				inThelist = true;
				break;
			}
		}
		if(!inThelist) {
			lstItems.addItem(item.getName(), String.valueOf(item.getId()));
		}
	}
	
	@UiHandler("btnDel")
	public void onDel(ClickEvent event) {
		lstItems.removeItem(lstItems.getSelectedIndex());
	}
	
	private ClickHandler okHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			
			metric.getLinkedItems().clear();
			for(int i = 0 ; i < lstItems.getItemCount() ; i++) {
				int itemId = Integer.parseInt(lstItems.getValue(i));
				String itemName = lstItems.getItemText(i);
				MetricLinkedItem item = new MetricLinkedItem();
				item.setItemId(itemId);
				item.setItemName(itemName);
				item.setMetricId(metric.getId());
				item.setType(MetricLinkedItem.TYPE_CUBE);
				metric.getLinkedItems().add(item);
			}
			
			isConfirm = true;
			LinkedObjectDialog.this.hide();
			
		}
	};
	
	private ClickHandler cancelHandler = new ClickHandler() {	
		@Override
		public void onClick(ClickEvent event) {
			LinkedObjectDialog.this.hide();
		}
	};
	
	public boolean isConfirm() {
		return isConfirm;
	}

}
