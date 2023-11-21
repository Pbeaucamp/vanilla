package bpm.gwt.commons.client.dialog;

import bpm.fm.api.model.HasItemLinked;
import bpm.fm.api.model.Level;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.dialog.SaveItemDialog.IRepositoryManager;
import bpm.gwt.commons.client.services.CommonService;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.commons.client.viewer.dialog.ViewerDialog;
import bpm.gwt.commons.shared.repository.PortailRepositoryItem;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.repository.RepositoryItem;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class ItemLinkedDialog extends AbstractDialogBox implements IRepositoryManager {

	private static DimensionMeasureDialogUiBinder uiBinder = GWT.create(DimensionMeasureDialogUiBinder.class);

	interface DimensionMeasureDialogUiBinder extends UiBinder<Widget, ItemLinkedDialog> {
	}

	interface MyStyle extends CssResource {
		String imgCell();

		String imgGrid();
	}

	@UiField
	MyStyle style;

	@UiField
	Image btnAdd, btnRun;

	@UiField
	Label lblETL;
	
	private String login, password, vanillaUrl;

	private Group group;
	private HasItemLinked hasItemLinked;
	private RepositoryItem item;

	public ItemLinkedDialog(String login, String password, String vanillaUrl, Group group, HasItemLinked hasItemLinked) {
		super(LabelsConstants.lblCnst.LinkETL(), false, true);
		this.login = login;
		this.password = password;
		this.vanillaUrl = vanillaUrl;
		this.group = group;
		this.hasItemLinked = hasItemLinked;

		setWidget(uiBinder.createAndBindUi(this));

		createButton(LabelsConstants.lblCnst.Close(), closeHandler);

		refresh(hasItemLinked);
	}

	private void refresh(HasItemLinked hasItemLinked) {
		if (hasItemLinked.getLinkedItemId() != null) {
			CommonService.Connect.getInstance().getItemById(hasItemLinked.getLinkedItemId(), new GwtCallbackWrapper<RepositoryItem>(this, true, true) {

				@Override
				public void onSuccess(RepositoryItem result) {
					refreshUi(result);
				}
			}.getAsyncCallback());
		}
		else {
			refreshUi(null);
		}
	}

	@UiHandler("btnAdd")
	public void onAddClick(ClickEvent event) {
		CreateETLAxeDialog dial = new CreateETLAxeDialog(login, password, vanillaUrl, group, hasItemLinked, this);
		dial.center();
	}

	@UiHandler("btnLink")
	public void onLinkClick(ClickEvent event) {
		final RepositoryDialog dial = new RepositoryDialog(IRepositoryApi.GTW_TYPE);
		dial.center();
		dial.addCloseHandler(new CloseHandler<PopupPanel>() {

			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				if (dial.isConfirm()) {
					RepositoryItem item = dial.getSelectedItem();
					savedItem(item);
				}
			}
		});
	}
	
	@UiHandler("btnRun")
	public void onRunClick(ClickEvent event) {
		if (item != null) {
			PortailRepositoryItem itemReport = new PortailRepositoryItem(item, IRepositoryApi.BIG);

			ViewerDialog dial = new ViewerDialog(itemReport, group);
			dial.center();
		}
	}
	
	private void refreshUi(RepositoryItem item) {
		this.item = item;
		
		lblETL.setText(item != null ? item.getName() : LabelsConstants.lblCnst.NoLinkedETL());
		btnRun.setVisible(item != null);
	}

	private ClickHandler closeHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			ItemLinkedDialog.this.hide();
		}
	};

	@Override
	public void savedItem(RepositoryItem item) {
		if (item.getType() != IRepositoryApi.GTW_TYPE) {
			MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Information(), LabelsConstants.lblCnst.ThisItemCannotBeLinked());
			return;
		}
		
		hasItemLinked.setLinkedItemId(item.getId());
		refreshUi(item);
	}
}
