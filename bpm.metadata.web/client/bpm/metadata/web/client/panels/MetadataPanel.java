package bpm.metadata.web.client.panels;

import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.dialog.RepositoryDialog;
import bpm.gwt.commons.client.panels.Tab;
import bpm.gwt.commons.client.panels.TabManager;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.tree.MetadataTree;
import bpm.gwt.commons.client.tree.MetadataTree.IPropertiesListener;
import bpm.gwt.commons.client.tree.MetadataTreeItem;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.commons.client.utils.Splitter;
import bpm.gwt.commons.shared.fmdt.metadata.Metadata;
import bpm.metadata.web.client.I18N.Labels;
import bpm.metadata.web.client.panels.properties.PanelProperties;
import bpm.metadata.web.client.popup.SaveTypePopup;
import bpm.metadata.web.client.services.MetadataService;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class MetadataPanel extends Tab implements IPropertiesListener {
	private static final int DEFAULT_WIDTH = 420;
	private static final int DEFAULT_LEFT = 440;

	private static CreationPanelUiBinder uiBinder = GWT.create(CreationPanelUiBinder.class);

	interface CreationPanelUiBinder extends UiBinder<Widget, MetadataPanel> {
	}
	
	interface MyStyle extends CssResource {
		String splitter();
	}
	
	@UiField
	MyStyle style;
	
	@UiField
	HTMLPanel panelStructure, panelPropertiesParent;
	
	@UiField
	SimplePanel panelTree;
	
	@UiField
	SimplePanel panelSplitter, panelPropertiesContent;

	private MetadataTree metadataTree;
	private PanelProperties panelProperties;
	
	private Metadata metadata;
	
	private boolean isLoaded;
	
	public MetadataPanel(TabManager tabManager, int userId) {
		super(tabManager, Labels.lblCnst.MyMetadata(), true);
		buildContent(userId);
	}

	public MetadataPanel(TabManager tabManager, int userId, Metadata metadata) {
		super(tabManager, metadata.getName(), true);
		buildContent(userId);
		loadMetadata(metadata, true);
	}
	
	public boolean isLoaded() {
		return isLoaded;
	}
	
	private void buildContent(int userId) {
		this.add(uiBinder.createAndBindUi(this));
		
		panelStructure.setWidth(DEFAULT_WIDTH + "px");
		panelPropertiesParent.getElement().getStyle().setLeft(DEFAULT_LEFT, Unit.PX);
		
		panelSplitter.setWidget(new Splitter(panelStructure, panelPropertiesParent, style.splitter(), false));
		
		metadataTree = new MetadataTree(this, true);
		panelTree.add(metadataTree);
		
		panelProperties = new PanelProperties(this, userId);
		panelPropertiesContent.setWidget(panelProperties);
	}
	
	public void refreshTree() {
		metadataTree.refresh(metadata);
	}
	
	public void loadMetadata(Metadata metadata, boolean newTab) {
		this.metadata = metadata;
		this.isLoaded = true;
		
		if (!newTab) {
			setTabHeaderTitle(metadata.getName());
			refreshTitle();
		}
		metadataTree.buildItem(null, metadata, true);
	}

	@Override
	public void loadProperties(MetadataTreeItem<?> treeItem) {
		panelProperties.buildContent(treeItem);
	}

	public Metadata getMetadata() {
		return metadata;
	}

	@UiHandler("btnSave")
	public void onSaveClick(ClickEvent event) {
		if (metadata.getItemId() != null) {
			SaveTypePopup popup = new SaveTypePopup(this);
			popup.setPopupPosition(event.getClientX(), event.getClientY());
			popup.show();
		}
		else {
			saveMetadata();
		}
	}
	
	public void saveMetadata() {
		final RepositoryDialog dial = new RepositoryDialog(IRepositoryApi.FMDT_TYPE);
		dial.center();
		dial.addCloseHandler(new CloseHandler<PopupPanel>() {
			
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				if (!dial.isConfirm() || !dial.folderSelected()) {
					//TODO: Message
					return;
				}
				
				RepositoryDirectory target = dial.getSelectedDirectory();
				saveMetadata(target, false);
			}
		});
	}
	
	public void saveMetadata(RepositoryDirectory target, final boolean update) {
		showWaitPart(true);
		
		MetadataService.Connect.getInstance().save(target, metadata, update, new GwtCallbackWrapper<Integer>(MetadataPanel.this, true) {

			@Override
			public void onSuccess(Integer result) {
				if (update) {
					MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Information(), Labels.lblCnst.MetadataUpdatedSuccess());
				}
				else {
					metadata.setItemId(result);
					MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Information(), Labels.lblCnst.MetadataSavedSuccess());
				}
			}
		}.getAsyncCallback());
	}
}
