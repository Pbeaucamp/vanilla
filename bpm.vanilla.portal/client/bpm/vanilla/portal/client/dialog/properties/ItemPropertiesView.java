package bpm.vanilla.portal.client.dialog.properties;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.shared.repository.PortailRepositoryDirectory;
import bpm.gwt.commons.shared.repository.PortailRepositoryItem;
import bpm.vanilla.platform.core.beans.ArchiveType;
import bpm.vanilla.platform.core.beans.ArchiveTypeItem;
import bpm.vanilla.platform.core.repository.IRepositoryObject;
import bpm.vanilla.portal.client.services.AdminService;
import bpm.vanilla.portal.client.utils.ToolsGWT;

public class ItemPropertiesView extends Composite {

	private static ItemPropertiesViewUiBinder uiBinder = GWT.create(ItemPropertiesViewUiBinder.class);

	interface ItemPropertiesViewUiBinder extends UiBinder<Widget, ItemPropertiesView> {
	}

	@UiField
	Label lblItemName, lblItemId, lblItemType, lblCreatedBy, lblCreatedDate, lblLastModDate, lblListArchiveTypes;

	@UiField
	TextBox txtItemName, txtItemId, txtItemType, txtCreatedBy, txtCreatedDate, txtLastModDate;
	
	@UiField
	ListBox lstArchiveTypes;
	
	@UiField
	Button btnArchive;
	
	private IRepositoryObject item;
	
	private int previousType = -1;

	public ItemPropertiesView(PortailRepositoryItem item) {
		initWidget(uiBinder.createAndBindUi(this));
		
		this.item = item;
		
		buildContent();

		txtItemName.setText(item.getName());
		txtItemId.setText(String.valueOf(item.getId()));
		txtItemType.setText(item.getTypeName());
		txtCreatedBy.setText(item.getCreatedBy());
		txtCreatedDate.setText(item.getItem().getDateCreation() + "");
		txtLastModDate.setText(item.getItem().getDateModification() + "");
		
		btnArchive.setVisible(false);
	}

	public ItemPropertiesView(PortailRepositoryDirectory directory) {
		initWidget(uiBinder.createAndBindUi(this));
		
		this.item = directory;
		
		buildContent();
		
		txtItemName.setText(directory.getName());
		txtItemId.setText(String.valueOf(directory.getId()));
		
		lblItemType.setVisible(false);
		txtItemType.setVisible(false);
		
		lblCreatedBy.setVisible(false);
		txtCreatedBy.setVisible(false);
		
		lblCreatedDate.setVisible(false);
		txtCreatedDate.setVisible(false);
		
		lblLastModDate.setVisible(false);
		txtLastModDate.setVisible(false);
		
		lblListArchiveTypes.getElement().getStyle().setTop(55, Unit.PX);
		lstArchiveTypes.getElement().getStyle().setTop(51, Unit.PX);
		btnArchive.getElement().getStyle().setTop(85, Unit.PX);
	}
	
	private void buildContent() {
		
		lblListArchiveTypes.setText("Archive type");
		
		lblItemName.setText(ToolsGWT.lblCnst.Name());
		txtItemName.setEnabled(false);
		
		lblItemId.setText("ID");
		txtItemId.setEnabled(false);
		
		lblItemType.setText(ToolsGWT.lblCnst.Type());
		txtItemType.setEnabled(false);
		
		lblCreatedBy.setText(ToolsGWT.lblCnst.CreatedBy());
		txtCreatedBy.setEnabled(false);
		
		lblCreatedDate.setText(ToolsGWT.lblCnst.CreationDate());
		txtCreatedDate.setEnabled(false);
		
		lblLastModDate.setText(ToolsGWT.lblCnst.LastUpdateDate());
		txtLastModDate.setEnabled(false);
		AdminService.Connect.getInstance().getArchiveTypes(new GwtCallbackWrapper<List<ArchiveType>>(null, false) {
			public void onSuccess(List<ArchiveType> result) {
				
				lstArchiveTypes.clear();
				lstArchiveTypes.addItem("", "-1");
				for(ArchiveType type : result) {
					lstArchiveTypes.addItem(type.getName(), type.getId() + "");
				}
				
				AdminService.Connect.getInstance().getArchiveTypeForItem(item.getId(), item instanceof PortailRepositoryDirectory, new GwtCallbackWrapper<ArchiveTypeItem>(null, false) {
					public void onSuccess(ArchiveTypeItem result) {
						if(result != null) {
							previousType = result.getArchiveTypeId();
							for(int i = 1 ; i < lstArchiveTypes.getItemCount() ; i++) {
								if(lstArchiveTypes.getValue(i).equals(result.getArchiveTypeId() + "")) {
									lstArchiveTypes.setSelectedIndex(i);
									break;
								}
							}
						}
					};
				}.getAsyncCallback());
			};			
		}.getAsyncCallback());
		
	}
	
	@Override
	protected void onUnload() {
		if(Integer.parseInt(lstArchiveTypes.getValue(lstArchiveTypes.getSelectedIndex())) != previousType) {
			AdminService.Connect.getInstance().addArchiveTypeToItem(item.getId(), Integer.parseInt(lstArchiveTypes.getValue(lstArchiveTypes.getSelectedIndex())), item instanceof PortailRepositoryDirectory, false, new GwtCallbackWrapper<ArchiveTypeItem>(null, false) {
				@Override
				public void onSuccess(ArchiveTypeItem result) {
					// TODO Auto-generated method stub
					
				}
			}.getAsyncCallback());
		}
		
		super.onUnload();
	}
	
	@UiHandler("btnArchive")
	public void onArchive(ClickEvent event) {
		AdminService.Connect.getInstance().addArchiveTypeToItem(item.getId(), Integer.parseInt(lstArchiveTypes.getValue(lstArchiveTypes.getSelectedIndex())), item instanceof PortailRepositoryDirectory, true, new GwtCallbackWrapper<ArchiveTypeItem>(null, false) {
			@Override
			public void onSuccess(ArchiveTypeItem result) {
				previousType = result.getArchiveTypeId();
			}
		}.getAsyncCallback());
	}

}
