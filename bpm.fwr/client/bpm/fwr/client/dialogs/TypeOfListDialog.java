package bpm.fwr.client.dialogs;

import bpm.fwr.client.Bpm_fwr;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class TypeOfListDialog extends AbstractDialogBox {

	private static TypeOfListDialogUiBinder uiBinder = GWT.create(TypeOfListDialogUiBinder.class);

	interface TypeOfListDialogUiBinder extends UiBinder<Widget, TypeOfListDialog> {
	}
	
	@UiField
	HTMLPanel contentPanel;
	
	public TypeOfListDialog() {
		super(Bpm_fwr.LBLW.TypeList(), false, false);
		
		setWidget(uiBinder.createAndBindUi(this));
	}
	
	@UiHandler("btnClassic")
	public void onNormalClick(ClickEvent event) {
		finish(false, null, null);
		TypeOfListDialog.this.hide();
	}
	
	@UiHandler("btnAuto")
	public void onAutoClick(ClickEvent event) {
		finish(true, null, null);
		TypeOfListDialog.this.hide();
	}
}
