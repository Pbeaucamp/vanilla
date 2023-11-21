package bpm.gwt.commons.client.viewer.dialog;

import java.util.ArrayList;
import java.util.List;

import bpm.gwt.commons.client.ExceptionManager;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.services.ReportingService;
import bpm.gwt.commons.client.viewer.FdVanillaViewer;
import bpm.gwt.commons.shared.utils.CommonConstants;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class FdExportDialog extends AbstractDialogBox {
	
	private static FdExportDialogUiBinder uiBinder = GWT.create(FdExportDialogUiBinder.class);

	interface FdExportDialogUiBinder extends UiBinder<Widget, FdExportDialog> {}

	@UiField
	HTMLPanel contentPanel;
	
	@UiField
	ListBox lstFormat;
	
	@UiField
	HTMLPanel panelCheckboxs;
	
	@UiField
	Label lblTips;
	
	@UiField
	CheckBox checkLandscape;
	
	private FdVanillaViewer viewer;
	private String uuid;
	private String url;
	
	private List<CheckBox> checkboxs = new ArrayList<CheckBox>();

	private boolean historize;
	
	public FdExportDialog(FdVanillaViewer viewer, String uuid, String url, List<String> folders, boolean historize) {
		super(LabelsConstants.lblCnst.ExportDashboard(), false, true);
		this.viewer = viewer;
		this.uuid = uuid;
		this.url = url;
		this.historize = historize;
		
		setWidget(uiBinder.createAndBindUi(this));
		
		createButtonBar(LabelsConstants.lblCnst.Export(), okHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);

		for(int i = 0 ; i < CommonConstants.FORMAT_VALUE_FD.length ; i++) {
			lstFormat.addItem(CommonConstants.FORMAT_DISPLAY_FD[i], CommonConstants.FORMAT_VALUE_FD[i]);			
		}
		
		for(String folder : folders) {
			panelCheckboxs.add(createCheckbox(folder));
		}
	}
	
	private Widget createCheckbox(String folder) {
		CheckBox checkbox = new CheckBox(folder);
		checkbox.setName(folder);
		checkbox.setValue(true);
		
		checkboxs.add(checkbox);
		
		SimplePanel panel = new SimplePanel();
		panel.add(checkbox);
		return panel;
	}
	
	@UiHandler("lstFormat")
	public void onChangeFormat(ChangeEvent event) {
		String selectedFormat = lstFormat.getValue(lstFormat.getSelectedIndex());
		if(selectedFormat.equalsIgnoreCase(CommonConstants.FORMAT_PDF_NAME)) {
			panelCheckboxs.setVisible(true);
		}
		else {
			panelCheckboxs.setVisible(false);
		}
	}

	private ClickHandler okHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			viewer.showWaitPart(true);
			
			final String selectedFormat = lstFormat.getValue(lstFormat.getSelectedIndex());
			
			List<String> selectedFolders = new ArrayList<String>();
			for(CheckBox checkbox : checkboxs) {
				if(checkbox.getValue()) {
					selectedFolders.add(checkbox.getName());
				}
			}
			
			boolean isLandscape = checkLandscape.getValue();
			
			ReportingService.Connect.getInstance().exportDashboard(uuid, url, selectedFormat, selectedFolders, isLandscape, new AsyncCallback<String>() {
				
				@Override
				public void onSuccess(String result) {
					if(historize) {
						viewer.historizeDashboard(result, selectedFormat);
					}
					else {
						viewer.exportDashboard(result, selectedFormat);
					}
					
					viewer.showWaitPart(false);

					FdExportDialog.this.hide();
				}
				
				@Override
				public void onFailure(Throwable caught) {
					caught.printStackTrace();
					
					viewer.showWaitPart(false);
					
					ExceptionManager.getInstance().handleException(caught, "Error while exporting dashboard.");
				}
			});
		}
	};

	private ClickHandler cancelHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			FdExportDialog.this.hide();
		}
	};
}
