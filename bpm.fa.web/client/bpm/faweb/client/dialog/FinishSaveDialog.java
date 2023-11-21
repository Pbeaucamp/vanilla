package bpm.faweb.client.dialog;

import bpm.faweb.client.FreeAnalysisWeb;
import bpm.faweb.client.images.FaWebImage;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class FinishSaveDialog extends AbstractDialogBox {

	private static FinishSaveDialogUiBinder uiBinder = GWT.create(FinishSaveDialogUiBinder.class);

	interface FinishSaveDialogUiBinder extends UiBinder<Widget, FinishSaveDialog> {
	}
	
	interface MyStyle extends CssResource {
		String imgResult();
	}

	@UiField
	MyStyle style;
	
	@UiField
	HTMLPanel contentPanel;
	
	@UiField
	Image imgResult;
	
	@UiField
	HTML msgResult;

	public FinishSaveDialog(String result, boolean success) {
		super(FreeAnalysisWeb.LBL.ReportSaved(), false, false);
		setWidget(uiBinder.createAndBindUi(this));
		
		if (success) {
			imgResult.setResource(FaWebImage.INSTANCE.save_sucess());
		}
		else {
			imgResult.setResource(FaWebImage.INSTANCE.save_failure());
		}
		
		imgResult.addStyleName(style.imgResult());
		
		msgResult.setHTML(result);
	}
}
