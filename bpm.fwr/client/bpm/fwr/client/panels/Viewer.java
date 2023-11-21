package bpm.fwr.client.panels;

import java.util.Date;
import java.util.List;

import bpm.fwr.api.beans.FWRReport;
import bpm.fwr.api.beans.SaveOptions;
import bpm.fwr.api.beans.dataset.DataSet;
import bpm.fwr.api.beans.dataset.IResource;
import bpm.fwr.client.Bpm_fwr;
import bpm.fwr.client.WysiwygPanel;
import bpm.fwr.client.dialogs.ConfirmDialog;
import bpm.fwr.client.dialogs.FwrPromptDialog;
import bpm.fwr.client.dialogs.FwrRepositoryDialog;
import bpm.fwr.client.dialogs.SaveOptionsDialogBox;
import bpm.fwr.client.dialogs.UpdateOrCreateNewReportDialogBox;
import bpm.fwr.client.draggable.dropcontrollers.BinDropController;
import bpm.fwr.client.draggable.dropcontrollers.ReportDropController;
import bpm.fwr.client.images.WysiwygImage;
import bpm.fwr.client.services.FwrServiceConnection;
import bpm.fwr.client.services.WysiwygService;
import bpm.fwr.client.utils.ServletURL;
import bpm.fwr.client.utils.SizeComponentConstants;
import bpm.fwr.client.widgets.BinWidget;
import bpm.fwr.shared.models.TreeParentDTO;
import bpm.fwr.shared.models.metadata.FwrMetadata;
import bpm.fwr.shared.models.report.wysiwyg.ReportParameters;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.dialog.SaveAsTemplateDialog;
import bpm.gwt.commons.client.dialog.SaveAsTemplateDialog.TemplateHandler;
import bpm.gwt.commons.client.listeners.FinishListener;
import bpm.gwt.commons.client.listeners.IClose;
import bpm.gwt.commons.client.panels.Tab;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.commons.client.utils.ToolsGWT;
import bpm.gwt.commons.client.utils.VanillaCSS;
import bpm.gwt.commons.shared.utils.CommonConstants;
import bpm.vanilla.platform.core.beans.VanillaImage;
import bpm.vanilla.platform.core.repository.IReport;
import bpm.vanilla.platform.core.repository.Template;
import bpm.vanilla.platform.core.repository.Template.TypeTemplate;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.drop.DropController;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class Viewer extends Tab implements IClose, TemplateHandler {

	private static ViewerUiBinder uiBinder = GWT.create(ViewerUiBinder.class);

	interface ViewerUiBinder extends UiBinder<Widget, Viewer> {
	}

	interface MyStyle extends CssResource {
		String parentPanel();

		String frame();

		String emptyBin();

		String focus();
	}

	@UiField
	MyStyle style;

	@UiField
	HTMLPanel toolbarPanel;

	@UiField
	SimplePanel reportPanelContent, framePanel;

	@UiField
	Frame reportFrame;

	@UiField
	Image btnSave, btnPreferences;

	@UiField
	Label btnPreview, btnDesigner;

	@UiField
	BinWidget imgBin;

	private WysiwygPanel panelParent;
	// private ReportViewer reportViewer;

	private ReportPanel reportPanel;

	private PickupDragController paletteDragController;
	private DropController reportDropController;

	private boolean isPreview = false;

	public Viewer(WysiwygPanel panelParent, ReportViewer reportViewer, String nameReport, PickupDragController reportWidgetDragController, PickupDragController paletteDragController, PickupDragController dragController, PickupDragController dataDragController, PickupDragController groupDragController, PickupDragController detailDragController, PickupDragController resourceDragController, PickupDragController cellsDragController) {
		super(reportViewer, nameReport, true);
		this.add(uiBinder.createAndBindUi(this));

		this.panelParent = panelParent;
		// this.reportViewer = reportViewer;
		this.paletteDragController = paletteDragController;

		toolbarPanel.addStyleName(VanillaCSS.TAB_TOOLBAR);
		this.addStyleName(style.parentPanel());
		this.imgBin.addStyleName(style.emptyBin());

		reportPanel = new ReportPanel(panelParent, reportWidgetDragController, paletteDragController, dragController, dataDragController, groupDragController, detailDragController, resourceDragController, cellsDragController);
		reportPanelContent.setWidget(reportPanel);

		framePanel.setVisible(false);
		reportFrame.setWidth(SizeComponentConstants.WIDTH_REPORT + "px");
		reportFrame.getElement().getStyle().setMarginLeft(-(SizeComponentConstants.WIDTH_REPORT / 2), Unit.PX);

		reportDropController = new ReportDropController(reportPanel.getReportSheet());
		paletteDragController.registerDropController(reportDropController);

		// add drop controller for trash bin
		DropController binDropController = new BinDropController(imgBin, false);
		dragController.registerDropController(binDropController);
		groupDragController.registerDropController(binDropController);
		detailDragController.registerDropController(binDropController);
		cellsDragController.registerDropController(binDropController);
		reportWidgetDragController.registerDropController(binDropController);

		btnDesigner.addStyleName(style.focus());
	}

	@UiHandler("btnSave")
	public void onSaveClick(ClickEvent event) {
		if (canUpdateReport()) {
			UpdateOrCreateNewReportDialogBox dial = new UpdateOrCreateNewReportDialogBox();
			dial.addFinishListener(updateOrSaveFinishListener);
			dial.center();
		} else {
			openSaveDialog();
		}
	}

	@UiHandler("btnSaveAsTemplate")
	public void onSaveAsTemplate(ClickEvent event) {
		SaveAsTemplateDialog dial = new SaveAsTemplateDialog(this);
		dial.center();
	}

	@UiHandler("btnDownloadAsBirt")
	public void onDownloadAsBirt(ClickEvent event) {
		FWRReport report = reportPanel.getReportSheet().generateReport();
		
		WysiwygService.Connect.getInstance().downloadReportAsBirt(report, new GwtCallbackWrapper<String>(this, true, true) {

			@Override
			public void onSuccess(String result) {
				String fullUrl = GWT.getHostPageBaseURL() + CommonConstants.PREVIEW_STREAM_SERVLET + "?" + CommonConstants.STREAM_HASHMAP_NAME + "=" + result + "&" + CommonConstants.STREAM_HASHMAP_FORMAT + "=" + CommonConstants.FORMAT_RPT_DESIGN;
				ToolsGWT.doRedirect(fullUrl);
			}
		}.getAsyncCallback());
	}

	@Override
	public void saveTemplate(String name, VanillaImage img) {
		showWaitPart(true);

		Template<IReport> template = buildTemplate(name, img);
		WysiwygService.Connect.getInstance().addTemplate(template, new GwtCallbackWrapper<Void>(this, true) {

			@Override
			public void onSuccess(Void result) {
				MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Information(), LabelsConstants.lblCnst.TemplateSavedSuccess());
			}
		}.getAsyncCallback());
	}

	public Template<IReport> buildTemplate(String name, VanillaImage img) {
		FWRReport report = reportPanel.getReportSheet().generateReport();

		Template<IReport> template = new Template<>(name, TypeTemplate.WEB_REPORT);
		template.setCreatorId(panelParent.getInfoUser().getUser().getId());
		template.setDateCreation(new Date());
		template.setImage(img);
		template.setItem(report);
		return template;
	}

	@UiHandler("btnUndo")
	public void onUndoClick(ClickEvent event) {
		reportPanel.undo();
	}

	@UiHandler("btnRedo")
	public void onRedoClick(ClickEvent event) {
		reportPanel.redo();
	}

	@UiHandler("btnPreferences")
	public void onPreferencesClick(ClickEvent event) {
		ReportParameters reportParam = reportPanel.getReportSheet().getReportParameters();
		List<FwrMetadata> metadatas = panelParent.getMetadatas();

		SaveOptionsDialogBox dial = new SaveOptionsDialogBox(reportParam, metadatas);
		dial.addFinishListener(finishListener);
		dial.center();
	}

	@Override
	public void close() {
		paletteDragController.unregisterDropController(reportDropController);
	}

	// @UiHandler("checkboxPanel")
	// public void onPreview(ClickEvent event) {
	// this.isPreview = !isPreview;
	//
	// switchView(isPreview);
	//
	// if(isPreview) {
	// off.setWidth("0px");
	// on.setWidth("50px");
	//
	// loadPreview();
	// }
	// else {
	// on.setWidth("0px");
	// off.setWidth("50px");
	// }
	// }

	@UiHandler("btnDesigner")
	public void onDesigner(ClickEvent event) {
		if (isPreview) {
			switchView(isPreview);
		}
	}

	@UiHandler("btnPreview")
	public void onPreview(ClickEvent event) {
		if (!isPreview) {
			switchView(isPreview);
		}
	}

	private void switchView(boolean preview) {
		this.isPreview = !preview;

		framePanel.setVisible(isPreview);

		btnPreferences.setVisible(!isPreview);
		imgBin.setVisible(!isPreview);
		reportPanel.setVisible(!isPreview);

		if (isPreview) {
			btnDesigner.removeStyleName(style.focus());
			btnPreview.addStyleName(style.focus());

			loadPreview();
		} else {
			btnPreview.removeStyleName(style.focus());
			btnDesigner.addStyleName(style.focus());
		}
	}

	public void openReport(FWRReport report) {
		reportPanel.getReportSheet().loadReport(report);
	}

	public void openReportFromQuery(FWRReport report, DataSet dataset, Boolean formatted) {
		reportPanel.getReportSheet().loadReportFromQuery(report, dataset, formatted);
	}

	public boolean canUpdateReport() {
		if (reportPanel.getReportSheet().getDirectoryItemId() != null && reportPanel.getReportSheet().getDirectoryItemId() != -1) {
			return true;
		} else {
			return false;
		}
	}

	public void loadPreview() {
		final FWRReport report = reportPanel.getReportSheet().generateReport();
		if (report.getPrompts().isEmpty()) {
			previewReport(report);
		} else {
			FwrPromptDialog dial = new FwrPromptDialog(Bpm_fwr.LBLW.Prompt(), report.getPrompts());
			dial.addFinishListener(new FinishListener() {

				@Override
				@SuppressWarnings("unchecked")
				public void onFinish(Object result, Object source, String result1) {
					if (result != null) {
						List<IResource> prompts = (List<IResource>) result;
						report.setPrompts(prompts);

						previewReport(report);
					} else {
						switchView(true);
					}
				}
			});

			dial.center();
		}
	}

	private void previewReport(final FWRReport report) {
		showWaitPart(true);

		WysiwygService.Connect.getInstance().previewWysiwygReport(report, new AsyncCallback<String>() {

			@Override
			public void onSuccess(String result) {
				showWaitPart(false);

				String fullUrl = GWT.getHostPageBaseURL() + ServletURL.RUN_URL + "?" + CommonConstants.REPORT_HASHMAP_NAME + "=" + result + "&" + CommonConstants.REPORT_OUTPUT + "=" + report.getOutput();
				reportFrame.setUrl(fullUrl);
			}

			@Override
			public void onFailure(Throwable caught) {
				showWaitPart(false);

				caught.printStackTrace();
			}
		});
	}

	public void saveWysiwygReport(boolean update, SaveOptions saveOptions) {
		FWRReport report = reportPanel.getReportSheet().generateReport();

		if (!update) {
			report.setSaveOptions(saveOptions);
		}

		WysiwygService.Connect.getInstance().saveWysiwygReport(report, update, new AsyncCallback<Integer>() {
			public void onSuccess(Integer result) {
				if (result != null && result != -1) {
					String message = Bpm_fwr.LBLW.SaveSuccess();

					showFinishSaveDialog(message, true);
				} else {
					String message = "An error happend during the save.";

					showFinishSaveDialog(message, false);
				}
			}

			public void onFailure(Throwable caught) {
				caught.printStackTrace();

				showFinishSaveDialog("Error: " + caught.getMessage(), false);
			}
		});
	}

	public void saveWysiwygReportAsBirtReport(SaveOptions saveOptions) {
		FWRReport report = reportPanel.getReportSheet().generateReport();
		report.setSaveOptions(saveOptions);

		WysiwygService.Connect.getInstance().saveWysiwygReportAsBirtReport(report, new AsyncCallback<String>() {
			public void onSuccess(String result) {

				if (result.equals("")) {
					result = Bpm_fwr.LBLW.SaveSuccess();

					showFinishSaveDialog(result, true);
				} else {
					showFinishSaveDialog(result, false);
				}
			}

			public void onFailure(Throwable caught) {
				caught.printStackTrace();

				showFinishSaveDialog("Error: " + caught.getMessage(), false);
			}
		});
	}

	private void showFinishSaveDialog(String result, boolean success) {
		ImageResource imgResource = null;
		if (success) {
			imgResource = WysiwygImage.INSTANCE.FWR_Sucess();
		} else {
			imgResource = WysiwygImage.INSTANCE.FWR_Failure();
		}

		ConfirmDialog dial = new ConfirmDialog(imgResource, result);
		dial.center();
	}

	private FinishListener updateOrSaveFinishListener = new FinishListener() {

		@Override
		public void onFinish(Object update, Object source, String result1) {
			if (update instanceof Boolean) {
				if ((Boolean) update) {
					saveWysiwygReport(true, null);
				} else {
					openSaveDialog();
				}
			}
		}
	};

	private void openSaveDialog() {
		showWaitPart(true);

		FwrServiceConnection.Connect.getInstance().browseRepositoryService(new AsyncCallback<TreeParentDTO>() {

			public void onFailure(Throwable e) {
				e.printStackTrace();

				showWaitPart(false);
			}

			public void onSuccess(TreeParentDTO metadatas) {
				showWaitPart(false);

				FwrRepositoryDialog repositoryDialog = new FwrRepositoryDialog(Viewer.this, metadatas);
				repositoryDialog.center();
			}
		});
	}

	private FinishListener finishListener = new FinishListener() {

		@Override
		public void onFinish(Object result, Object source, String result1) {
			if (result instanceof ReportParameters) {
				reportPanel.getReportSheet().setReportParameters((ReportParameters) result);

				// HashMap<Integer, String> languages =
				// reportPanel.getReportSheet().getReportParameters().getMetadataLanguages();
				String selectedLanguage = reportPanel.getReportSheet().getReportParameters().getSelectedLanguage();
				panelParent.updateDatasetTreePart(selectedLanguage);
			}
		}
	};

	public ReportPanel getReportPanel() {
		return reportPanel;
	}
}
