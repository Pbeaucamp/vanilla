package bpm.fm.designer.web.client.panel;

import java.util.List;

import bpm.fm.api.model.Axis;
import bpm.fm.api.model.Metric;
import bpm.fm.api.model.Observatory;
import bpm.fm.designer.web.client.ClientSession;
import bpm.fm.designer.web.client.services.MetricService;
import bpm.fm.designer.web.server.MetricServiceImpl;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class MainPanel extends Composite {

	public static MainPanel instance;

	private static MainPanelUiBinder uiBinder = GWT.create(MainPanelUiBinder.class);

	@UiField
	TopToolbar toolbar;
	
	@UiField
	AxisPanel axesPanel;
	
	@UiField
	MetricPanel metricPanel;
	
	@UiField
	HTMLPanel propertiesPanel;

	private int selectedTheme;

	interface MainPanelUiBinder extends UiBinder<Widget, MainPanel> {
	}

	public MainPanel() {
		initWidget(uiBinder.createAndBindUi(this));

		instance = this;

		propertiesPanel.add(new MetricPropertiesPanel(null, metricPanel));
	}

	public void selectionChanged(Object actualSelection) {
		if(actualSelection == null) {
			propertiesPanel.clear();
			propertiesPanel.add(new MetricPropertiesPanel(null, metricPanel));
		}
		else {
			if (actualSelection instanceof Metric) {
				propertiesPanel.clear();
				propertiesPanel.add(new MetricPropertiesPanel((Metric) actualSelection, metricPanel));
			}
			else if (actualSelection instanceof Axis) {
			}
		}
	}

	public static MainPanel getInstance() {
		return instance;
	}

	public void reloadMetricAxes(int themeId) {
		selectedTheme = themeId;
		metricPanel.refresh();
		propertiesPanel.clear();
		axesPanel.refresh();
	}
	
	public int getSelectedTheme() {
		return selectedTheme;
	}

	public void reloadObservatories() {
		MetricService.Connection.getInstance().getObservatories(new AsyncCallback<List<Observatory>>() {
			
			@Override
			public void onSuccess(List<Observatory> result) {
				ClientSession.getInstance().setObservatories(result);
				toolbar.loadObservatories();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				
			}
		});
		
	}

	public int getSelectedObservatory() {
		return toolbar.getSelectedObservatory();
	}
}
