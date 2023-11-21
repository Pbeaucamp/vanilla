package bpm.fd.web.client.panels;

import bpm.fd.web.client.services.DashboardService;
import bpm.gwt.commons.client.custom.LabelTextArea;
import bpm.gwt.commons.client.loading.IWait;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class CssPanel extends Composite {

	private static FeedbackPanelUiBinder uiBinder = GWT.create(FeedbackPanelUiBinder.class);

	interface FeedbackPanelUiBinder extends UiBinder<Widget, CssPanel> {
	}

	@UiField
	LabelTextArea txtCss;

	public CssPanel(IWait waitPanel) {
		initWidget(uiBinder.createAndBindUi(this));
		
		loadDefaultCss(waitPanel);
	}
	
	private void loadDefaultCss(IWait waitPanel) {
		waitPanel.showWaitPart(true);
		DashboardService.Connect.getInstance().getDefaultCss(new GwtCallbackWrapper<String>(waitPanel, true) {

			@Override
			public void onSuccess(String result) {
				String css = getCss();
				
				if ((css == null || css.isEmpty()) && result != null) {
					txtCss.setText(result);
				}
			}
		}.getAsyncCallback());
	}

	public String getCss() {
		return txtCss.getText();
	}
	
	public void setCss(String css) {
		txtCss.setText(css);
	}
}
