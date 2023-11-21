package bpm.gwt.aklabox.commons.client.upload;

import org.moxieapps.gwt.uploader.client.Uploader;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class FakeUploadComposite extends PopupPanel {

	private static FakeUploadCompositeUiBinder uiBinder = GWT.create(FakeUploadCompositeUiBinder.class);

	interface FakeUploadCompositeUiBinder extends UiBinder<Widget, FakeUploadComposite> {
	}
	
	@UiField
	HTMLPanel panelContent;

	public FakeUploadComposite(Uploader uploader) {
		setWidget(uiBinder.createAndBindUi(this));
	
		panelContent.add(uploader);
	}

}
