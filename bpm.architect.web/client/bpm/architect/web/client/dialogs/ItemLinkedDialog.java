package bpm.architect.web.client.dialogs;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;

import bpm.architect.web.client.I18N.Labels;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.shared.InfoUser;
import bpm.mdm.model.supplier.Contract;

public class ItemLinkedDialog extends AbstractDialogBox {

	private static ItemLinkedDialogUiBinder uiBinder = GWT.create(ItemLinkedDialogUiBinder.class);

	interface ItemLinkedDialogUiBinder extends UiBinder<Widget, ItemLinkedDialog> {
	}
	
	@UiField(provided=true)
	ItemLinkedComposite itemLinkedComposite;

	public ItemLinkedDialog(InfoUser infoUser, Contract contract, boolean onlyEtl, boolean showAdd) {
		super(Labels.lblCnst.ItemsLinked(), false, true);

		itemLinkedComposite = new ItemLinkedComposite(this, infoUser, contract, onlyEtl, showAdd);
		setWidget(uiBinder.createAndBindUi(this));

		createButton(LabelsConstants.lblCnst.Close(), closeHandler);
	}
	
	public boolean needRefreshContracts() {
		return itemLinkedComposite.isRefreshContracts();
	}

	private ClickHandler closeHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			ItemLinkedDialog.this.hide();
		}
	};
}
