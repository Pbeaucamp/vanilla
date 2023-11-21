package bpm.faweb.client.dialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bpm.faweb.client.FreeAnalysisWeb;
import bpm.faweb.client.MainPanel;
import bpm.faweb.client.panels.PercentPanel;
import bpm.faweb.client.services.FaWebService;
import bpm.faweb.client.utils.Space;
import bpm.faweb.shared.infoscube.InfosReport;
import bpm.faweb.shared.infoscube.ItemMes;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class PercentDialog extends AbstractDialogBox {

	private static PercentDialogUiBinder uiBinder = GWT.create(PercentDialogUiBinder.class);

	interface PercentDialogUiBinder extends UiBinder<Widget, PercentDialog> {
	}

	@UiField
	HTMLPanel contentPanel;
	
	private MainPanel mainCompPanel;

	private List<PercentPanel> panels = new ArrayList<PercentPanel>();

	public PercentDialog(MainPanel mainCompPanel) {
		super(FreeAnalysisWeb.LBL.Percent(), false, true);
		this.mainCompPanel = mainCompPanel;
		
		setWidget(uiBinder.createAndBindUi(this));

		createButtonBar(FreeAnalysisWeb.LBL.Apply(), okHandler, FreeAnalysisWeb.LBL.Cancel(), cancelHandler);

		VerticalPanel mainPanel = new VerticalPanel();
		mainPanel.setSize("100%", "100%");

		Label lblTitle = new Label(FreeAnalysisWeb.LBL.PercentTitle());
		lblTitle.addStyleName("titleLabel");
		mainPanel.add(lblTitle);
		mainPanel.add(new Space("1px", "10px"));

		addMeasuresPanels(mainPanel);

		contentPanel.add(mainPanel);
	}

	private ClickHandler okHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			HashMap<String, Boolean> percentMeasures = new HashMap<String, Boolean>();
			for (PercentPanel pan : panels) {
				if (pan.isPercent()) {
					percentMeasures.put(pan.getMeasure(), pan.isShowMeasure());
				}
			}
			FaWebService.Connect.getInstance().addPercentMeasures(mainCompPanel.getKeySession(), percentMeasures, new AsyncCallback<InfosReport>() {

				@Override
				public void onSuccess(InfosReport result) {
					mainCompPanel.setGridFromRCP(result);
					PercentDialog.this.hide();
				}

				@Override
				public void onFailure(Throwable caught) {
					caught.printStackTrace();

					PercentDialog.this.hide();
				}
			});
		}
	};

	private ClickHandler cancelHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			PercentDialog.this.hide();
		}
	};

	private void addMeasuresPanels(VerticalPanel mainPanel) {
		HashMap<String, Boolean> percents = mainCompPanel.getInfosReport().getGrid().getPercentMeasures();
		HashMap<String, String> measures = mainCompPanel.getAllMeasureAdded();

		for (String p : measures.keySet()) {
			PercentPanel mesPanel = new PercentPanel(p, measures.get(p));
			if (percents.containsKey(p)) {
				mesPanel.setPercent(true);
				mesPanel.setShowMes(percents.get(p));
			}
			boolean isMeasure = false;
			for (ItemMes mes : mainCompPanel.getInfosReport().getMeasures()) {
				if (mes.getUname().equals(p)) {
					isMeasure = true;
					break;
				}
			}
			mesPanel.setEnabled();
			if (isMeasure) {
				panels.add(mesPanel);
				mainPanel.add(mesPanel);
				mainPanel.add(new Space("1px", "10px"));
			}
		}
	}

}
