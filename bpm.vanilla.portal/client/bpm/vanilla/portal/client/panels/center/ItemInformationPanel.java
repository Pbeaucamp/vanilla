package bpm.vanilla.portal.client.panels.center;

import java.util.List;

import bpm.gwt.commons.client.comment.CommentPanel;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.commons.client.utils.VanillaCSS;
import bpm.gwt.commons.client.utils.ToolsGWT.TypeCollaboration;
import bpm.gwt.commons.shared.InfoUser;
import bpm.gwt.commons.shared.repository.PortailRepositoryDirectory;
import bpm.gwt.commons.shared.repository.PortailRepositoryItem;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.repository.IRepositoryObject;
import bpm.vanilla.portal.client.biPortal;
import bpm.vanilla.portal.client.panels.ContentDisplayPanel;
import bpm.vanilla.portal.client.panels.navigation.TypeViewer;
import bpm.vanilla.portal.client.utils.ToolsGWT;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ItemInformationPanel extends Composite {

	private static ItemInformationPanelUiBinder uiBinder = GWT.create(ItemInformationPanelUiBinder.class);

	interface ItemInformationPanelUiBinder extends UiBinder<Widget, ItemInformationPanel> {
	}
	
	interface MyStyle extends CssResource {
		String imgItem();
	}
	
	@UiField
	MyStyle style;
	
	@UiField
	Label lblGeneralInformations, txtItemName, txtInfoName, txtInfoCreationDate, txtModificationDate;
	
	@UiField
	Image imgItem;
	
	@UiField
	Image btnComment, btnRun, btnView, btnHistory, btnRunFaWeb, btnBirtViewer, btnProperties, btnDelete;
	
	@UiField
	Image btnSubscribe, btnAddWatchlist, btnRemoveWatchlist, btnAddOnOpen, btnRemoveOnOpen;

	private ContentDisplayPanel mainPanel;
	
	private IRepositoryObject item;
	private TypeViewer typeViewer;
	
	public ItemInformationPanel(ContentDisplayPanel mainPanel, IRepositoryObject item, TypeViewer typeViewer) {
		initWidget(uiBinder.createAndBindUi(this));
		this.mainPanel = mainPanel;
		this.item = item;
		this.typeViewer = typeViewer;
		
		buildToolbar(item, typeViewer);
		buildContent(item);
		
		lblGeneralInformations.addStyleName(VanillaCSS.NAVIGATION_TOOLBAR);
	}

	private void buildToolbar(IRepositoryObject repositoryObject, TypeViewer typeViewer) {
		if(repositoryObject instanceof PortailRepositoryDirectory) {
			btnComment.setVisible(false);
			btnRun.setVisible(false);
			btnView.setVisible(false);
			btnHistory.setVisible(false);
			btnRunFaWeb.setVisible(false);
			btnBirtViewer.setVisible(false);
			btnDelete.setVisible(false);
			btnSubscribe.setVisible(false);
			btnAddWatchlist.setVisible(false);
			btnRemoveWatchlist.setVisible(false);
			btnAddOnOpen.setVisible(false);
			btnRemoveOnOpen.setVisible(false);
		}
		else if(repositoryObject instanceof PortailRepositoryItem) {
			InfoUser infoUser = biPortal.get().getInfoUser();
			PortailRepositoryItem item = (PortailRepositoryItem) repositoryObject;
		
			if(!item.getItem().isCommentable()) {
				btnComment.setVisible(false);
			}
			
			if (ToolsGWT.isRunnable(item)) {
				if (!item.getItem().canRun()) {
					btnRun.setVisible(false);
				}
			}
	
			if (!(item.isReport() && item.isViewable())) {
				btnView.setVisible(false);
				btnHistory.setVisible(false);
			}
	
			if (!ToolsGWT.isCube(item)) {
				btnRunFaWeb.setVisible(false);
			}
	
			if (!(ToolsGWT.isBirt(item) && biPortal.get().getInfoUser().isConnected(IRepositoryApi.BIRT_VIEWER))) {
				btnBirtViewer.setVisible(false);
			}
			
			if (!((item.getType() == IRepositoryApi.FAV_TYPE || item.getType() == IRepositoryApi.FWR_TYPE
					|| item.getType() == IRepositoryApi.PROJECTION_TYPE || item.getType() == IRepositoryApi.FMDT_DRILLER_TYPE || item.getType() == IRepositoryApi.FMDT_CHART_TYPE) && item.isOwned())) {
				btnDelete.setVisible(false);
			}
	
			if (!(item.isReport()/*  || item.getType() == IRepositoryApi.GTW_TYPE*/)) {
				btnSubscribe.setVisible(false);
			}
	
			if (typeViewer == TypeViewer.WATCH_LIST || infoUser.isOnWatchlist(item.getItem())) {
				btnAddWatchlist.setVisible(false);
			}
			else {
				btnRemoveWatchlist.setVisible(false);
			}
	
			if (typeViewer == TypeViewer.OPEN_ON_STARTUP || infoUser.isOpenOnStartup(item.getItem())) {
				btnAddOnOpen.setVisible(false);
			}
			else if (item.isReport() || item.getType() == IRepositoryApi.FD_TYPE || item.getType() == IRepositoryApi.FMDT_TYPE) {
				btnRemoveOnOpen.setVisible(false);
			}
			else {
				btnAddOnOpen.setVisible(false);
				btnRemoveOnOpen.setVisible(false);
			}
		}
	}
	
	public void refreshButtons() {
		btnComment.setVisible(true);
		btnRun.setVisible(true);
		btnView.setVisible(true);
		btnHistory.setVisible(true);
		btnRunFaWeb.setVisible(true);
		btnBirtViewer.setVisible(true);
		btnProperties.setVisible(true);
		btnDelete.setVisible(true);
		btnSubscribe.setVisible(true);
		btnAddWatchlist.setVisible(true);
		btnRemoveWatchlist.setVisible(true);
		btnAddOnOpen.setVisible(true);
		btnRemoveOnOpen.setVisible(true);
		
		buildToolbar(item, typeViewer);
	}

	private void buildContent(IRepositoryObject repositoryObject) {
		imgItem.setResource(ToolsGWT.getImageForObject(repositoryObject, true));
		imgItem.addStyleName(style.imgItem());
		
		txtItemName.setText(repositoryObject.getName());
		txtItemName.setTitle(repositoryObject.getName());
		txtInfoName.setText(repositoryObject.getName());
		
		if(repositoryObject instanceof PortailRepositoryDirectory) {
			PortailRepositoryDirectory dir = (PortailRepositoryDirectory) repositoryObject;

			txtInfoCreationDate.setText(dir.getDirectory().getDateCreation() + "");
		}
		else if(repositoryObject instanceof PortailRepositoryItem) {
			PortailRepositoryItem item = (PortailRepositoryItem) repositoryObject;

			txtInfoCreationDate.setText(item.getItem().getDateCreation() + "");
			txtModificationDate.setText(item.getItem().getDateModification() + "");
//			txtType.setText(IRepositoryApi.TYPES_NAMES[item.getType()]);
//			
//			if(item.getType() == IRepositoryApi.CUST_TYPE) {
//				txtSubtype.setText(IRepositoryApi.SUBTYPES_NAMES[item.getSubType()]);
//			}
		}
	}
	
	@UiHandler("btnComment")
	public void onCommentClick(ClickEvent event) {
		Group selectedGroup = biPortal.get().getInfoUser().getGroup();
		List<Group> availableGroups = biPortal.get().getInfoUser().getAvailableGroups();

		CommentPanel commentPanel = null;
		if (item instanceof  PortailRepositoryItem) {
			commentPanel = new CommentPanel(biPortal.get(), ((PortailRepositoryItem) item).getId(), TypeCollaboration.ITEM_NOTE, selectedGroup, availableGroups);
		}
		else if (item instanceof PortailRepositoryDirectory) {
			commentPanel = new CommentPanel(biPortal.get(), ((PortailRepositoryDirectory) item).getId(), TypeCollaboration.DIRECTORY_NOTE, selectedGroup, availableGroups);
		}
		commentPanel.show();
		commentPanel.setPopupPosition(event.getClientX() - 332, event.getClientY() + 20);
	}

	@UiHandler("btnRun")
	public void onRunClick(ClickEvent event) {
		mainPanel.openViewer(item);
	}

	@UiHandler("btnView")
	public void onViewClick(ClickEvent event) {
		mainPanel.openView(biPortal.get(), (PortailRepositoryItem) item);
	}

	@UiHandler("btnHistory")
	public void onHistoryClick(ClickEvent event) {
		mainPanel.openItemHistory((PortailRepositoryItem) item);
	}

	@UiHandler("btnRunFaWeb")
	public void onRunFaWebClick(ClickEvent event) {
		mainPanel.openFasd(biPortal.get(), (PortailRepositoryItem) item);
	}

	@UiHandler("btnBirtViewer")
	public void onBirtViewerClick(ClickEvent event) {
		PortailRepositoryItem item = (PortailRepositoryItem) this.item;
		if (item.getItem().isOn()) {
			mainPanel.openBirtViewer(biPortal.get(), item);
		}
		else {
			MessageHelper.openMessageDialog(ToolsGWT.lblCnst.Error(), ToolsGWT.lblCnst.BiObject() + " " + item.getName() + " " + ToolsGWT.lblCnst.NotOn());
		}
	}

	@UiHandler("btnProperties")
	public void onPropertiesClick(ClickEvent event) {
		mainPanel.showProperties(biPortal.get(), item);
	}

	@UiHandler("btnDelete")
	public void onDeleteClick(ClickEvent event) {
		mainPanel.deleteItem(biPortal.get(), (PortailRepositoryItem) item);
	}

	@UiHandler("btnSubscribe")
	public void onSubscribeClick(ClickEvent event) {
		mainPanel.subscribe(biPortal.get(), (PortailRepositoryItem) item);
	}

	@UiHandler("btnAddWatchlist")
	public void onAddWatchListClick(ClickEvent event) {
		mainPanel.addToWatchlist(biPortal.get(), (PortailRepositoryItem) item, this);
	}

	@UiHandler("btnRemoveWatchlist")
	public void onRemoveWatchListClick(ClickEvent event) {
		mainPanel.removeFromWatchlist(biPortal.get(), (PortailRepositoryItem) item, this);
	}

	@UiHandler("btnAddOnOpen")
	public void onAddOnOpenClick(ClickEvent event) {
		mainPanel.addToOnOpen(biPortal.get(), (PortailRepositoryItem) item, this);
	}

	@UiHandler("btnRemoveOnOpen")
	public void onRemoveOnOpenClick(ClickEvent event) {
		mainPanel.removeFromOnOpen(biPortal.get(), (PortailRepositoryItem) item, this);
	}
}
