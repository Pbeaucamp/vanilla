package bpm.gwt.aklabox.commons.client.loading;

import bpm.gwt.aklabox.commons.client.I18N.LabelsConstants;
import bpm.gwt.aklabox.commons.client.images.CommonImages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class WaitAbsolutePanel extends Composite {

	private static WaitAbsolutePanelUiBinder uiBinder = GWT
			.create(WaitAbsolutePanelUiBinder.class);

	interface WaitAbsolutePanelUiBinder extends
			UiBinder<Widget, WaitAbsolutePanel> {
	}
	
	interface MyStyle extends CssResource {
		String imgWait();
	}

	@UiField MyStyle style;
	
	@UiField
	Label lblWait;
	
	@UiField
	Image imgWait;
	

	public WaitAbsolutePanel() {
		initWidget(uiBinder.createAndBindUi(this));
		
		imgWait.setResource(CommonImages.INSTANCE.loadingBig());
		imgWait.addStyleName(style.imgWait());
		
		lblWait.setText(LabelsConstants.lblCnst.PleaseWait());
	}
}
