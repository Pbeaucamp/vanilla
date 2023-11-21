package bpm.es.web.client.panels.fiches;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class EvaluationPanel extends Composite {

	private static EvaluationPanelUiBinder uiBinder = GWT.create(EvaluationPanelUiBinder.class);

	interface EvaluationPanelUiBinder extends UiBinder<Widget, EvaluationPanel> {
	}

	public EvaluationPanel() {
		initWidget(uiBinder.createAndBindUi(this));
		
	}
}
