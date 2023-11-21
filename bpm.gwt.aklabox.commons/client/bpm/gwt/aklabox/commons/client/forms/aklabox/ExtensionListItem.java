package bpm.gwt.aklabox.commons.client.forms.aklabox;


import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ExtensionListItem extends Composite {

	private static ExtensionListItemUiBinder uiBinder = GWT
			.create(ExtensionListItemUiBinder.class);

	interface ExtensionListItemUiBinder extends
			UiBinder<Widget, ExtensionListItem> {
	}
	
	@UiField Label lblExt;

	public ExtensionListItem(String s) {
		initWidget(uiBinder.createAndBindUi(this));
		lblExt.setText(s);
	}
	
	@UiHandler("btnRemove")
	void onRemove(ClickEvent e){
		this.removeFromParent();
	}

	
}
