package bpm.gwt.commons.client.loading;

import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.images.CommonImages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class StackWaitAbsolutePanel extends Composite {
	
	private static StackWaitAbsolutePanelUiBinder uiBinder = GWT
			.create(StackWaitAbsolutePanelUiBinder.class);

	interface StackWaitAbsolutePanelUiBinder extends
			UiBinder<Widget, StackWaitAbsolutePanel> {
	}
	
	interface MyStyle extends CssResource {
		String imgWait();
	}

	@UiField MyStyle style;
	
	@UiField
	Label lblWait;
	
	@UiField
	Image imgWait;
	

	public StackWaitAbsolutePanel() {
		initWidget(uiBinder.createAndBindUi(this));
		
		imgWait.setResource(CommonImages.INSTANCE.loading());
		imgWait.addStyleName(style.imgWait());
		
		lblWait.setText(LabelsConstants.lblCnst.PleaseWait());
	}
}
