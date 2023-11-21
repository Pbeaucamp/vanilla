package bpm.faweb.client.dialog;

import java.util.List;

import bpm.faweb.client.FreeAnalysisWeb;
import bpm.faweb.client.MainPanel;
import bpm.faweb.client.services.FaWebService;
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
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class TopxDialog extends AbstractDialogBox {

	private static TopxDialogUiBinder uiBinder = GWT.create(TopxDialogUiBinder.class);

	interface TopxDialogUiBinder extends UiBinder<Widget, TopxDialog> {
	}

	@UiField
	HTMLPanel contentPanel;
	
	private MainPanel mainCompPanel;
	
	private String uname;

	private ListBox lstMeasures = new ListBox(false);
	private TextBox txtCount = new TextBox();

	public TopxDialog(MainPanel mainCompPanel, String uname) {
		super("Topx", false, true);
		this.mainCompPanel = mainCompPanel;
		this.uname = uname;
		
		setWidget(uiBinder.createAndBindUi(this));
		
		createButtonBar(FreeAnalysisWeb.LBL.Apply(), okHandler, FreeAnalysisWeb.LBL.Cancel(), cancelHandler);

		VerticalPanel mainPanel = new VerticalPanel();

		HorizontalPanel selectionPanel = new HorizontalPanel();

		VerticalPanel targetPanel = new VerticalPanel();
		Label lblTarget = new Label(FreeAnalysisWeb.LBL.TopxTarget());
		lblTarget.addStyleName("chartLabel");
		targetPanel.add(lblTarget);
		targetPanel.add(lstMeasures);
		fillMeasureList();
		targetPanel.setSize("100%", "50%");
		lstMeasures.setWidth("100%");
		lstMeasures.setHeight("22px");

		VerticalPanel countPanel = new VerticalPanel();
		Label lblCount = new Label(FreeAnalysisWeb.LBL.TopxCount());
		lblCount.addStyleName("chartLabel");
		countPanel.add(lblCount);
		countPanel.add(txtCount);
		countPanel.setSize("100%", "50%");
		txtCount.setWidth("97%");

		selectionPanel.add(targetPanel);
		selectionPanel.add(countPanel);
		selectionPanel.setSize("100%", "50%");

		mainPanel.add(selectionPanel);
		mainPanel.setSize("100%", "100px");

		contentPanel.add(mainPanel);
	}
	
	private ClickHandler cancelHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			TopxDialog.this.hide();
		}
	};
	
	private ClickHandler okHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			TopxDialog.this.hide();
			
			mainCompPanel.showWaitPart(true);
			
			FaWebService.Connect.getInstance().addTopx(mainCompPanel.getKeySession(), TopxDialog.this.uname, lstMeasures.getValue(lstMeasures.getSelectedIndex()), Integer.parseInt(txtCount.getText()), new AsyncCallback<InfosReport>() {
				public void onSuccess(InfosReport result) {
					mainCompPanel.showWaitPart(false);
					
					mainCompPanel.setGridFromRCP(result);
				}

				public void onFailure(Throwable caught) {
					mainCompPanel.showWaitPart(false);
					caught.printStackTrace();
				}
			});
		}
	};

	private void fillMeasureList() {
		List<ItemMes> mes = mainCompPanel.getInfosReport().getMeasures();
		for (ItemMes m : mes) {
			lstMeasures.addItem(m.getName(), m.getUname());
		}
	}

}
