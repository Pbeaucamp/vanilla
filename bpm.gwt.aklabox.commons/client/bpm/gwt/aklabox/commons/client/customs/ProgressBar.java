package bpm.gwt.aklabox.commons.client.customs;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ProgressBar extends Composite {

	private static ProgressBarUiBinder uiBinder = GWT.create(ProgressBarUiBinder.class);

	interface ProgressBarUiBinder extends UiBinder<Widget, ProgressBar> {
	}

	@UiField
	HTMLPanel progress;
	
	@UiField
	Label lblProgress;

	public ProgressBar(double size) {
		initWidget(uiBinder.createAndBindUi(this));
		
		progress.getElement().getStyle().setFontSize(size, Unit.PX);
	}

	public void setProgress(Double progress) {
		this.lblProgress.setText(progress.intValue() + "%");
		this.progress.setStyleName("c100 p" + progress.intValue() + " big");
	}
}
