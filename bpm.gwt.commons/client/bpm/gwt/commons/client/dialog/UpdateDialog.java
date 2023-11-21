package bpm.gwt.commons.client.dialog;

import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.loading.IWait;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Widget;

public class UpdateDialog extends AbstractDialogBox {

	private static TextEmailDialogUiBinder uiBinder = GWT.create(TextEmailDialogUiBinder.class);

	interface TextEmailDialogUiBinder extends UiBinder<Widget, UpdateDialog> {
	}

	@UiField
	Frame frame;

	public UpdateDialog(IWait waitPanel, String updateManagerUrl) {
		super(LabelsConstants.lblCnst.UpdateApplication(), true, true);

		setWidget(uiBinder.createAndBindUi(this));
		createButton(LabelsConstants.lblCnst.Close(), closeHandler);

		frame.setUrl(updateManagerUrl);
	}

	private ClickHandler closeHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			hide();
		}
	};

	/**
	 * This method aim to dynamically change the current URL
	 * 
	 * @param url
	 *            the new URL string
	 */
	public static native void changeCurrURL(String url)/*-{
		$wnd.location.replace(url);
	}-*/;
}
