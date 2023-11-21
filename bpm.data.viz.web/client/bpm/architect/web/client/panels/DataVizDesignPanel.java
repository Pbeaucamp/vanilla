package bpm.architect.web.client.panels;

import java.util.ArrayList;
import java.util.List;

import bpm.architect.web.client.MainPanel;
import bpm.architect.web.client.I18N.Labels;
import bpm.architect.web.client.dialogs.DataVizPropertiesDialog;
import bpm.architect.web.client.dialogs.PreparationExportDialog;
import bpm.architect.web.client.dialogs.PreparationExportInLibreOfficeDailog;
import bpm.architect.web.client.dialogs.ReportsDialog;
import bpm.architect.web.client.dialogs.TypesDialog;
import bpm.architect.web.client.services.ArchitectService;
import bpm.data.viz.core.preparation.DataPreparation;
import bpm.data.viz.core.preparation.LinkItem;
import bpm.gwt.commons.client.InformationsDialog;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.datasource.DatasourceDatasetManager;
import bpm.gwt.commons.client.dialog.DatacolumnD4CDialog;
import bpm.gwt.commons.client.dialog.MapServersDialog;
import bpm.gwt.commons.client.panels.CollapsePanel;
import bpm.gwt.commons.client.panels.Tab;
import bpm.gwt.commons.client.panels.TabManager;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.commons.shared.InfoUser;
import bpm.vanilla.platform.core.beans.data.DataColumn;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class DataVizDesignPanel extends Tab {

	private static DataVizDesignPanelUiBinder uiBinder = GWT.create(DataVizDesignPanelUiBinder.class);

	interface DataVizDesignPanelUiBinder extends UiBinder<Widget, DataVizDesignPanel> {
	}

	interface MyStyle extends CssResource {
		String mainPanel();

		String btnSelected();
	}

	@UiField
	MyStyle style;

	@UiField(provided = true)
	CollapsePanel collapsePanel;

	@UiField
	HTMLPanel mainPanel;

	private InfoUser infoUser;
	private DataPreparation dataPrep;

	private DataVizRulePanel rulePanel;
	private DataVizDataPanel dataPanel;
	private String ruleTitle = "";

	public DataVizDesignPanel(InfoUser infoUser, TabManager tabManager, DataPreparation dataPrep) {
		super(tabManager, dataPrep != null ? dataPrep.getName() : "Nouvelle DataPreparation", true);
		if (dataPrep == null) {
			dataPrep = new DataPreparation();
		}

		this.dataPrep = dataPrep;
		if (tabManager == null) {
			collapsePanel = new CollapsePanel(300, 50);
			this.add(uiBinder.createAndBindUi(this));
			this.addStyleName(style.mainPanel());
			DataVizMapPanel map = new DataVizMapPanel(this);
			mainPanel.clear();
			mainPanel.add(map);
			return;
		}
		else {

			collapsePanel = new CollapsePanel(300, 50);
			this.rulePanel = new DataVizRulePanel(collapsePanel, this);
			collapsePanel.setLeftPanel(rulePanel);
			dataPanel = new DataVizDataPanel(this);
			collapsePanel.setCenterPanel(dataPanel);
			this.add(uiBinder.createAndBindUi(this));
		}

		this.addStyleName(style.mainPanel());
		this.infoUser = infoUser;

		if (dataPrep.getDataset() == null) {
			onProps(null);
		}

		mainPanel.add(new DataVizSummaryTabPanel(this));
	}

	@UiHandler("btnDatasource")
	public void onDatasource(ClickEvent event) {
		DatasourceDatasetManager dial = new DatasourceDatasetManager(infoUser.getUser());
		dial.center();
	}

	@UiHandler("btnMapServer")
	public void onMapServers(ClickEvent event) {
		MapServersDialog dial = new MapServersDialog();
		dial.center();
	}


	@UiHandler("btnSave")
	public void onSave(ClickEvent event) {
		ArchitectService.Connect.getInstance().saveDataPreparation(dataPrep, new GwtCallbackWrapper<DataPreparation>(this, false, false) {
			@Override
			public void onSuccess(DataPreparation result) {
				dataPrep = result;
				MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Information(), Labels.lblCnst.DataPreparationSaved());
			}
		}.getAsyncCallback());
	}

	@UiHandler("btnExport")
	public void onExport(ClickEvent event) {
		PreparationExportDialog dial = new PreparationExportDialog(dataPrep, infoUser);
		dial.center();
	}

	@UiHandler("btnExportLibreOffice")
	public void onExportLibreOffice(ClickEvent event) {
		PreparationExportInLibreOfficeDailog dialogue = new PreparationExportInLibreOfficeDailog(dataPrep, infoUser);
		dialogue.center();
	}

	@UiHandler("btnProps")
	public void onProps(ClickEvent event) {
		final DataVizPropertiesDialog dial = new DataVizPropertiesDialog(dataPrep);
		dial.addCloseHandler(new CloseHandler<PopupPanel>() {
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				if (dial.isConfirm()) {
					dataPanel.refresh(dataPrep);
					rulePanel.refreshRulePanel();
				}
			}
		});
		dial.center();
	}

	@UiHandler("btnTyper")
	public void onTyper(ClickEvent event) {
		TypesDialog dial = new TypesDialog(dataPrep);
		dial.center();
	}

	@UiHandler("btnOptions")
	public void onOptions(ClickEvent event) {
		List<DataColumn> columns = dataPrep.getDataset().getMetacolumns();
		
		final DatacolumnD4CDialog dial = new DatacolumnD4CDialog(columns);
		dial.center();
		dial.addCloseHandler(new CloseHandler<PopupPanel>() {

			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				if (dial.hasChange()) {
					ArchitectService.Connect.getInstance().updateDataset(dataPrep.getDataset(), new GwtCallbackWrapper<Void>(DataVizDesignPanel.this, true, true) {

						@Override
						public void onSuccess(Void result) { }
					}.getAsyncCallback());
				}
			}
		});
	}

	@UiHandler("btnReports")
	public void onReports(ClickEvent event) {
		List<DataColumn> columns = new ArrayList<DataColumn>();
		if (dataPanel.getLastResult() != null && dataPanel.getLastResult().getValues() != null && !dataPanel.getLastResult().getValues().isEmpty() && dataPanel.getLastResult().getValues().get(0) != null) {
			columns = new ArrayList<>(dataPanel.getLastResult().getValues().get(0).keySet());
		}
		
		final ReportsDialog dial = new ReportsDialog(infoUser, dataPrep.getLinkedItems(), columns);
		dial.center();
		dial.addCloseHandler(new CloseHandler<PopupPanel>() {

			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				if (dial.isConfirm()) {
					List<LinkItem> items = dial.getLinkedItems();
					dataPrep.setLinkedItems(items);
				}
			}
		});
	}

	
	public static void openReportInTab(String reportUrl) {
		DataVizTabPanel tabPanel = MainPanel.getInstance().getDatavizPanel();
		if (tabPanel != null && tabPanel.getSelectedPanel() != null && tabPanel.getSelectedPanel() instanceof DataVizDesignPanel) {
			((DataVizDesignPanel) tabPanel.getSelectedPanel()).getDataPanel().openReport(reportUrl, true);
		}
	}

	public DataPreparation getDataPreparation() {
		return dataPrep;
	}

	public DataVizDataPanel getDataPanel() {
		return dataPanel;
	}

	public DataVizRulePanel getRulePanel() {
		return rulePanel;
	}

	@Override
	public void close() {

		final InformationsDialog dial = new InformationsDialog(LabelsConstants.lblCnst.Information(), LabelsConstants.lblCnst.Confirmation(), LabelsConstants.lblCnst.Cancel(), Labels.lblCnst.SaveDataPrepConfirm(), true);
		dial.addCloseHandler(new CloseHandler<PopupPanel>() {
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				if (dial.isConfirm()) {
					onSave(null);
				}
			}
		});
		dial.center();
		super.close();
	}

	@UiHandler("btnUndo")
	public void onUndo(ClickEvent event) {
		ArchitectService.Connect.getInstance().undo(dataPrep, new GwtCallbackWrapper<DataPreparation>(this, false, false) {
			@Override
			public void onSuccess(DataPreparation result) {
				dataPrep = result;
				dataPanel.refresh(dataPrep);
				rulePanel.refreshRulePanel();
			}
		}.getAsyncCallback());
	}

	@UiHandler("btnRedo")
	public void onRedo(ClickEvent event) {
		ArchitectService.Connect.getInstance().redo(dataPrep, new GwtCallbackWrapper<DataPreparation>(this, false, false) {
			@Override
			public void onSuccess(DataPreparation result) {
				dataPrep = result;
				dataPanel.refresh(dataPrep);
				rulePanel.refreshRulePanel();
			}
		}.getAsyncCallback());
	}

	public String getRuleTitle() {
		return ruleTitle;
	}

	public void setRuleTitle(String ruleTitle) {
		this.ruleTitle = ruleTitle;
	}

	public InfoUser getInfoUser() {
		return infoUser;
	}

	public void setInfoUser(InfoUser infoUser) {
		this.infoUser = infoUser;
	}

	public static native void exportStaticMethod() /*-{
    	$wnd.openReportInTab = @bpm.architect.web.client.panels.DataVizDesignPanel::openReportInTab(Ljava/lang/String;);
 	}-*/;
}
