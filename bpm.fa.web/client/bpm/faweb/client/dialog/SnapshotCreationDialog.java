package bpm.faweb.client.dialog;

import bpm.faweb.client.FreeAnalysisWeb;
import bpm.faweb.client.MainPanel;
import bpm.faweb.client.services.FaWebService;
import bpm.faweb.client.utils.Space;
import bpm.faweb.shared.ChartParameters;
import bpm.faweb.shared.MapOptions;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class SnapshotCreationDialog extends AbstractDialogBox {

	private static SnapshotCreationDialogUiBinder uiBinder = GWT.create(SnapshotCreationDialogUiBinder.class);

	interface SnapshotCreationDialogUiBinder extends UiBinder<Widget, SnapshotCreationDialog> {
	}
	
	interface MyStyle extends CssResource {
		String lblName();
		String txtName();
	}
	
	@UiField
	MyStyle style;

	@UiField
	HTMLPanel contentPanel;
	
	private TextBox txtName;

	public SnapshotCreationDialog(final MainPanel mainCompParent) {
		super(FreeAnalysisWeb.LBL.CreateSnapshot(), false, true);
		setWidget(uiBinder.createAndBindUi(this));

		VerticalPanel mainPanel = new VerticalPanel();

		Label lblTitle = new Label(FreeAnalysisWeb.LBL.EnterNameSnapshot());

		HorizontalPanel namePanel = new HorizontalPanel();
		namePanel.setWidth("100%");

		Label lblName = new Label(FreeAnalysisWeb.LBL.Name());
		lblName.addStyleName(style.lblName());
		
		txtName = new TextBox();
		txtName.addStyleName(style.txtName());

		namePanel.add(lblName);
		namePanel.add(txtName);

		namePanel.setCellHorizontalAlignment(lblName, HorizontalPanel.ALIGN_CENTER);
		namePanel.setCellHorizontalAlignment(txtName, HorizontalPanel.ALIGN_CENTER);

		mainPanel.add(new Space("1px", "5px"));
		mainPanel.add(lblTitle);
		mainPanel.add(new Space("1px", "5px"));
		mainPanel.add(namePanel);

		mainPanel.setCellHorizontalAlignment(lblTitle, HorizontalPanel.ALIGN_CENTER);

		contentPanel.add(mainPanel);

		createButton(LabelsConstants.lblCnst.Cancel(), new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				SnapshotCreationDialog.this.hide();
			}
		});
		
		createButton(LabelsConstants.lblCnst.Confirmation(), new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				mainCompParent.showWaitPart(true);
				
				ChartParameters chartParams = null;
				if(mainCompParent.getDisplayPanel().getChartTab() != null) {
					mainCompParent.getDisplayPanel().getChartTab().getChartParameters();
				}
				
				MapOptions mapOptions = null;
				if (mainCompParent.getDisplayPanel().getMapTab() != null && mainCompParent.getDisplayPanel().getMapTab().getMapOptions() != null) {
					mapOptions = mainCompParent.getDisplayPanel().getMapTab().getMapOptions();
				}

				FaWebService.Connect.getInstance().createSnapshot(mainCompParent.getKeySession(), txtName.getText(), chartParams, mainCompParent.getCalculs(), mainCompParent.getDisplayPanel().getGridHtml(), mainCompParent.getInfosReport(), mapOptions, new AsyncCallback<Void>() {
					public void onSuccess(Void result) {
						mainCompParent.showWaitPart(false);
						
						showFinishSaveDialog("The snapshot was saved with success", true);
					}

					public void onFailure(Throwable caught) {
						mainCompParent.showWaitPart(false);
						
						caught.printStackTrace();
						
						showFinishSaveDialog(caught.getMessage(), false);
					}
				});

				SnapshotCreationDialog.this.hide();
			}
		});
	}

	private void showFinishSaveDialog(String result, boolean success) {
		FinishSaveDialog dial = new FinishSaveDialog(result, success);
		dial.center();
	}

}
