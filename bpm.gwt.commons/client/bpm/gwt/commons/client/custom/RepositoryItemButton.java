package bpm.gwt.commons.client.custom;

import bpm.gwt.commons.client.dialog.RepositoryDialog;
import bpm.gwt.commons.client.loading.IWait;
import bpm.gwt.commons.client.services.CommonService;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.vanilla.platform.core.repository.RepositoryItem;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class RepositoryItemButton extends Composite {

	private static RepositoryItemButtonUiBinder uiBinder = GWT.create(RepositoryItemButtonUiBinder.class);

	interface RepositoryItemButtonUiBinder extends UiBinder<Widget, RepositoryItemButton> {
	}

	@UiField
	LabelTextBox itemName;

	@UiField
	CustomButton btnBrowse;

	private RepositoryItem item;
	private int typeRepository;
	
	private RepositoryItemHandler repositoryItemhandler;

	/**
	 * 
	 * @param waitPanel - Can be null
	 * @param typeRepository - Use -1 for all
	 * @param itemId - Can be null
	 * @param labelItem - Can be null
	 */
	public RepositoryItemButton(IWait waitPanel, int typeRepository, Integer itemId, String labelItem) {
		initWidget(uiBinder.createAndBindUi(this));
		this.typeRepository = typeRepository;
		
		if (labelItem != null && !labelItem.isEmpty()) {
			itemName.setPlaceHolder(labelItem);
		}
		itemName.setEnabled(false);

		if (itemId != null && itemId > 0) {
			CommonService.Connect.getInstance().getItemById(itemId, new GwtCallbackWrapper<RepositoryItem>(waitPanel, true, true) {
				@Override
				public void onSuccess(RepositoryItem result) {
					item = result;
					itemName.setText(item.getName());
				}

			}.getAsyncCallback());
		}
	}

	@UiHandler("btnBrowse")
	public void onBrowse(ClickEvent event) {
		final RepositoryDialog dialog = new RepositoryDialog(typeRepository);
		dialog.addCloseHandler(new CloseHandler<PopupPanel>() {
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				if (dialog.isConfirm()) {
					item = dialog.getSelectedItem();
					itemName.setText(item.getName());
					
					if (repositoryItemhandler != null) {
						repositoryItemhandler.onItemClick(item);
					}
				}
			}
		});
		dialog.center();
	}
	
	public void setRepositoryItemhandler(RepositoryItemHandler repositoryItemhandler) {
		this.repositoryItemhandler = repositoryItemhandler;
	}

	public RepositoryItem getSelectedItem() {
		return item;
	}

	public interface RepositoryItemHandler {
		
		public void onItemClick(RepositoryItem item);
	}
}
