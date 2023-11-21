package bpm.gwt.commons.client.viewer.dialog;

import java.util.ArrayList;
import java.util.List;

import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.commons.client.viewer.Viewer;
import bpm.gwt.commons.shared.utils.CommonConstants;
import bpm.gwt.commons.shared.viewer.LaunchReportInformations;
import bpm.vanilla.platform.core.IRepositoryApi;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class RunDialog extends AbstractDialogBox {

	private static BirtOptionRunDialogBoxUiBinder uiBinder = GWT.create(BirtOptionRunDialogBoxUiBinder.class);

	interface BirtOptionRunDialogBoxUiBinder extends UiBinder<Widget, RunDialog> {
	}

	interface MyStyle extends CssResource {
	}

	@UiField
	MyStyle style;
	
	@UiField
	CaptionPanel fieldSetFormat;
	
	@UiField
	ListBox lstReports;

	@UiField(provided = true)
	ListBox lstFormat = new ListBox(true);

	@UiField
	SimplePanel panelReportRun;

	private Viewer viewer;
	private LaunchReportInformations itemInfo;
	
	private List<RunComposite> runComposites = new ArrayList<RunComposite>();

	public RunDialog(Viewer viewer, LaunchReportInformations itemInfo) {
		super(getDialogName(itemInfo), false, true);
		this.viewer = viewer;
		this.itemInfo = itemInfo;

		setWidget(uiBinder.createAndBindUi(this));
		createButtonBar(LabelsConstants.lblCnst.Ok(), okHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);
		
		if(itemInfo.isGroupReport()) {
			boolean first = true;
			for(int index = 0; index<itemInfo.getReports().size(); index++) {
				lstReports.addItem(itemInfo.getReports().get(index).getItem().getName(), String.valueOf(index));
				
				if(first) {
					RunComposite runComposite = new RunComposite(itemInfo.getReports().get(index));
					runComposites.add(runComposite);
					panelReportRun.setWidget(runComposite);
					
					first = false;
				}
				else {
					runComposites.add(new RunComposite(itemInfo.getReports().get(index)));
				}
			}
		}
		else {
			lstReports.setVisible(false);
			
			RunComposite runComposite = new RunComposite(itemInfo);
			runComposites.add(runComposite);
			panelReportRun.setWidget(runComposite);
			

			if(!itemInfo.getItem().isReport()) {
				fieldSetFormat.setVisible(false);
			}
		}

		int indexDefaultFormat = -1;
		for (int i = 0; i < CommonConstants.FORMAT_DISPLAY.length; i++) {
			if (i < 2 || (!(itemInfo.getItem().getType() == IRepositoryApi.CUST_TYPE && itemInfo.getItem().getSubType() == IRepositoryApi.JASPER_REPORT_SUBTYPE))) {
				lstFormat.addItem(CommonConstants.FORMAT_DISPLAY[i], CommonConstants.FORMAT_VALUE[i]);
			}

			if (itemInfo.getItem().hasDefaultFormat() && itemInfo.getItem().getItem().getDefaultFormat().equalsIgnoreCase(CommonConstants.FORMAT_VALUE[i])) {
				indexDefaultFormat = i;
			}
		}

		if(indexDefaultFormat != -1) {
			lstFormat.setSelectedIndex(indexDefaultFormat);
		}
		else {
			lstFormat.setSelectedIndex(0);
		}
	}

	private static String getDialogName(LaunchReportInformations itemInfo) {
		if(itemInfo.getItem().isReport()) {
			return LabelsConstants.lblCnst.RunReport();
		}
		else if(itemInfo.getItem().getType() == IRepositoryApi.GTW_TYPE) {
			return LabelsConstants.lblCnst.RunGateway();
		}
		else {
			return LabelsConstants.lblCnst.RunWorkflow();
		}
	}
	
	@UiHandler("lstReports")
	public void onReportChange(ChangeEvent event) {
		int selectedIndex = Integer.parseInt(lstReports.getValue(lstReports.getSelectedIndex()));
		RunComposite composite = runComposites.get(selectedIndex);
		panelReportRun.setWidget(composite);
	}

	private ClickHandler okHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			List<String> outputs = new ArrayList<String>();
			if (lstFormat.isVisible()) {
				for (int i = 0; i < CommonConstants.FORMAT_DISPLAY.length; i++) {
					if (lstFormat.getItemCount() > i && lstFormat.isItemSelected(i)) {
						outputs.add(CommonConstants.FORMAT_VALUE[i]);
					}
				}
			}
			else {
				outputs.add(itemInfo.getItem().getItem().getDefaultFormat());
			}

			if (outputs.isEmpty()) {
				MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Error(), itemInfo.getItem().getName() + " : " + LabelsConstants.lblCnst.ChooseFormat());
				return;
			}
			
			if(itemInfo.isGroupReport()) {
				List<LaunchReportInformations> reports = new ArrayList<LaunchReportInformations>();
				for(RunComposite runComposite : runComposites) {
					try {
						LaunchReportInformations reportInfo = runComposite.getItemInfo();
						reportInfo.setOutputs(outputs);
						reports.add(reportInfo);
					} catch (Exception e) {
						MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Error(), e.getMessage());
						return;
					}
				}
				
				itemInfo.setReports(reports);
			}
			else {
				try {
					itemInfo = runComposites.get(0).getItemInfo();
				} catch (Exception e) {
					MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Error(), e.getMessage());
					return;
				}
			}
			itemInfo.setOutputs(outputs);
			
			viewer.runItem(itemInfo);
			
			RunDialog.this.hide();
		}
	};

	private ClickHandler cancelHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			viewer.setItemInfo(itemInfo);
			
			RunDialog.this.hide();
		}
	};
}
