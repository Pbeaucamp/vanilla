package bpm.freematrix.reborn.web.client.main.cube;

import java.util.ArrayList;
import java.util.List;

import bpm.fm.api.model.Metric;
import bpm.fm.api.model.MetricLinkedItem;
import bpm.freematrix.reborn.web.client.main.header.FreeMatrixHeader;
import bpm.freematrix.reborn.web.client.services.MetricService;
import bpm.gwt.commons.client.ExceptionManager;
import bpm.gwt.commons.client.loading.CompositeWaitPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class CubeView extends CompositeWaitPanel {

	private static CubeViewUiBinder uiBinder = GWT.create(CubeViewUiBinder.class);

	interface CubeViewUiBinder extends UiBinder<Widget, CubeView> {
	}
	
	@UiField
	HTMLPanel metricPanel, framePanel;

	public CubeView() {
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
				
				//filter metrics by themes
				List<Metric> toRm = new ArrayList<Metric>();
				for(Metric raised : result) {
					if(!FreeMatrixHeader.getInstance().isAllowed(raised)) {
						toRm.add(raised);
					}
				}
				result.removeAll(toRm);
				
				for(Metric m : result) {
					for(MetricLinkedItem item : m.getLinkedItems()) {
						if(item.getType().equals(MetricLinkedItem.TYPE_CUBE)) {
							MetricElement metric = new MetricElement(m, item, CubeView.this, i % 2 == 0);
							metricPanel.add(metric);
							i++;
						}
					}
				}
				
				showWaitPart(false);
			}
		});
		
	}

	public void showCube(MetricLinkedItem item) {
		
		showWaitPart(true);
		MetricService.Connection.getInstance().generateCubeUrl(item, new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				showWaitPart(false);
				ExceptionManager.getInstance().handleException(caught, caught.getMessage());
			}

			@Override
			public void onSuccess(String result) {
				showWaitPart(false);
				
				String url = result + "&locale=" + LocaleInfo.getCurrentLocale().getLocaleName();
				
				framePanel.clear();
				Frame frame = new Frame(url);
				frame.setSize("99%", "99%");
				frame.getElement().getStyle().setBorderWidth(0, Unit.PX);
				framePanel.add(frame);
			}
		});

	}

}
