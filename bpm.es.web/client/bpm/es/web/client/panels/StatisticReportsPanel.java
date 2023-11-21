package bpm.es.web.client.panels;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class StatisticReportsPanel extends Composite {

	private static StatisticReportsPanelUiBinder uiBinder = GWT.create(StatisticReportsPanelUiBinder.class);

	interface StatisticReportsPanelUiBinder extends UiBinder<Widget, StatisticReportsPanel> {
	}

	public StatisticReportsPanel() {
		initWidget(uiBinder.createAndBindUi(this));
	}

}
