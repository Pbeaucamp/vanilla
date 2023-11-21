package bpm.fwr.client.dialogs;

import bpm.fwr.client.Bpm_fwr;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ConfirmDialog extends AbstractDialogBox {

	private static ConfirmDialogUiBinder uiBinder = GWT.create(ConfirmDialogUiBinder.class);

	interface ConfirmDialogUiBinder extends UiBinder<Widget, ConfirmDialog> {
	}
	
	interface MyStyle extends CssResource {
		String img();
	}
	
	@UiField
	MyStyle style;
	
	@UiField
	HTMLPanel contentPanel;
	
	@UiField
	Image imgResult;
	
	@UiField
	Label lblMessage;
	
	public ConfirmDialog(ImageResource img, String result) {
		super(Bpm_fwr.LBLW.FinishSaveDialog(), false, false);

		setWidget(uiBinder.createAndBindUi(this));
		
		imgResult.setResource(img);
		imgResult.addStyleName(style.img());
		
		lblMessage.setText(result);
		
		setAutoHideEnabled(true);
	}
}