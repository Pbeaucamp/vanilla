package bpm.gwt.aklabox.commons.client.viewers;

import bpm.gwt.aklabox.commons.client.utils.PathHelper;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Widget;

public class TextViewer extends Composite {

	private static TextViewerUiBinder uiBinder = GWT.create(TextViewerUiBinder.class);

	interface TextViewerUiBinder extends UiBinder<Widget, TextViewer> {
	}

	@UiField
	Frame frame;

	public TextViewer(String filePath) {
		initWidget(uiBinder.createAndBindUi(this));

		frame.setUrl(PathHelper.getRightPath(filePath));

	}
}
