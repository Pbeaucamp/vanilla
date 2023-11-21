package bpm.fmloader.client.panel;

import bpm.fmloader.client.constante.Constantes;
import bpm.fmloader.client.images.ImageResources;

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
		
		imgWait.setResource(ImageResources.INSTANCE.largeloading());
		imgWait.addStyleName(style.imgWait());
		
		lblWait.setText(Constantes.LBL.loading());
	}
}
