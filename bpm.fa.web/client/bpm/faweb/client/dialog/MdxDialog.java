package bpm.faweb.client.dialog;

import bpm.faweb.client.FreeAnalysisWeb;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

public class MdxDialog extends AbstractDialogBox {

	private static MdxDialogUiBinder uiBinder = GWT.create(MdxDialogUiBinder.class);

	interface MdxDialogUiBinder extends UiBinder<Widget, MdxDialog> {
	}

	@UiField
	HTMLPanel contentPanel;

	public MdxDialog(String mdxStr){
		super(FreeAnalysisWeb.LBL.MDX(), false, false);
		setWidget(uiBinder.createAndBindUi(this));
		
		TextArea mdx = new TextArea();
		mdx.setText(mdxStr);
		mdx.setSize("600px", "300px");
		
		HTMLPanel mainPanel = new HTMLPanel("");
		mainPanel.setStyleName("blueExport");
		mainPanel.add(mdx);
		
		contentPanel.add(mainPanel);
	}
}
