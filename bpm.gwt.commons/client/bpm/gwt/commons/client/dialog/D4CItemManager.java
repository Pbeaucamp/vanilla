package bpm.gwt.commons.client.dialog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;

import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.panels.D4CDefinitionPanel;
import bpm.gwt.commons.client.panels.D4CItemPanel;
import bpm.vanilla.platform.core.beans.resources.D4C;
import bpm.vanilla.platform.core.beans.resources.D4CItem;
import bpm.vanilla.platform.core.beans.resources.D4CItem.TypeD4CItem;
import bpm.vanilla.platform.core.beans.resources.D4CItemVisualization;

public class D4CItemManager extends AbstractDialogBox implements Handler {

	private static D4CItemManagerManagerUiBinder uiBinder = GWT.create(D4CItemManagerManagerUiBinder.class);

	interface D4CItemManagerManagerUiBinder extends UiBinder<Widget, D4CItemManager> {
	}

	interface MyStyle extends CssResource {
		String overflowHidden();
	}

	@UiField
	MyStyle style;
	
	@UiField(provided=true)
	D4CDefinitionPanel d4cDefinitionPanel;
	
	@UiField(provided=true)
	D4CItemPanel<D4CItemVisualization> d4cItemPanel;

//	@UiField
//	Image btnClearRepository, btnClearD4CItem;
//
//	@UiField
//	TextHolderBox txtSearchRepository, txtSearchD4CItem;
	
	private boolean isConfirm = false;
	
	public D4CItemManager() {
		super(LabelsConstants.lblCnst.D4CManager(), true, true);
		
		d4cDefinitionPanel = new D4CDefinitionPanel(this, this, false);
		d4cItemPanel = new D4CItemPanel<D4CItemVisualization>(this, TypeD4CItem.FROM_D4C, null);
		setWidget(uiBinder.createAndBindUi(this));
		
		createButtonBar(LabelsConstants.lblCnst.Confirmation(), confirmHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);
	}

//	private List<Repository> filterRepositorys(List<Repository> repositorys) {
//		if (searchRepository) {
//			String query = txtSearchRepository.getText();
//
//			List<Repository> filterRepositorys = new ArrayList<>();
//			for (Repository repository : repositorys) {
//				if (repository.getName().startsWith(query)) {
//					filterRepositorys.add(repository);
//				}
//			}
//			return filterRepositorys;
//		}
//		else {
//			return repositorys;
//		}
//	}
//
//	private List<D4CItem> filterD4CItems(List<D4CItem> d4cItems) {
//		if (searchD4CItem) {
//			String query = txtSearchD4CItem.getText();
//
//			List<D4CItem> filterD4CItems = new ArrayList<>();
//			for (D4CItem d4cItem : d4cItems) {
//				if (d4cItem.getName().startsWith(query)) {
//					filterD4CItems.add(d4cItem);
//				}
//			}
//			return filterD4CItems;
//		}
//		else {
//			return d4cItems;
//		}
//	}
//
//	@UiHandler("btnClearRepository")
//	public void onClearRepositoryClick(ClickEvent event) {
//		this.searchRepository = false;
//		txtSearchRepository.setText("");
//		btnClearRepository.setVisible(false);
//		loadRepositorys(repositorys);
//	}
//
//	@UiHandler("btnSearchRepository")
//	public void onSearchRepositoryClick(ClickEvent event) {
//		this.searchRepository = true;
//		btnClearRepository.setVisible(true);
//		loadRepositorys(repositorys);
//	}
//
//	@UiHandler("btnClearD4CItem")
//	public void onClearD4CItemClick(ClickEvent event) {
//		this.searchD4CItem = false;
//		txtSearchD4CItem.setText("");
//		btnClearD4CItem.setVisible(false);
//		loadD4CItems(d4cItems);
//	}
//
//	@UiHandler("btnSearchD4CItem")
//	public void onSearchD4CItemClick(ClickEvent event) {
//		this.searchD4CItem = true;
//		btnClearD4CItem.setVisible(true);
//		loadD4CItems(d4cItems);
//	}

	private ClickHandler confirmHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			if (isComplete()) {
				isConfirm = true;
				hide();
			}
		}
	};

	private ClickHandler cancelHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			hide();
		}
	};

	@Override
	public void onSelectionChange(SelectionChangeEvent event) {
		D4C item = d4cDefinitionPanel.getSelectedItem();
		if (item != null) {
			d4cItemPanel.loadItems(item.getId());
		}
	}
	
	private boolean isComplete() {
		return getSelectedItem() != null;
	}
	
	public boolean isConfirm() {
		return isConfirm;
	}
	
	public D4CItem getSelectedItem() {
		return d4cItemPanel.getSelectedItem();
	}
}
