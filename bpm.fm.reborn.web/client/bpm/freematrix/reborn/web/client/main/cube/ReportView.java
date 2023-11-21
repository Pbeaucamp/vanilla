package bpm.freematrix.reborn.web.client.main.cube;

import java.util.List;

import bpm.fm.api.model.Metric;
import bpm.fm.api.model.MetricLinkedItem;
import bpm.freematrix.reborn.web.client.services.MetricService;
import bpm.gwt.commons.client.ExceptionManager;
import bpm.gwt.commons.client.loading.CompositeWaitPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class ReportView extends CompositeWaitPanel {

	private static ReportViewUiBinder uiBinder = GWT
			.create(ReportViewUiBinder.class);

	interface ReportViewUiBinder extends UiBinder<Widget, ReportView> {
	}

	@UiField
	HTMLPanel metricPanel, framePanel;

	public ReportView() {
		initWidget(uiBinder.createAndBindUi(this));
		
		showWaitPart(true);
		
		MetricService.Connection.getInstance().getMetrics(new AsyncCallback<List<Metric>>() {

			@Override
			public void onFailure(Throwable caught) {
				showWaitPart(false);
				ExceptionManager.getInstance().handleException(caught, caught.getMessage());
			}

			@Override
			public void onSuccess(List<Metric> result) {
				int i = 1;
				for(Metric m : result) {
					for(MetricLinkedItem item : m.getLinkedItems()) {
						if(item.getType().equals(MetricLinkedItem.TYPE_REPORT)) {
							MetricElement metric = new MetricElement(m, item, ReportView.this, i % 2 == 0);
							metricPanel.add(metric);
							i++;
						}
					}
				}
				
				showWaitPart(false);
			}
		});
		
	}

	public void showReport(MetricLinkedItem linkedItem) {
		
		showWaitPart(true);
		//TODO report
		
		
	}
	
}
