package bpm.gwt.commons.client.viewer;

import java.util.HashMap;
import java.util.List;

import bpm.fwr.api.beans.FWRReport;
import bpm.gwt.commons.client.ExceptionManager;
import bpm.gwt.commons.client.InformationsDialog;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.comment.CommentPanel;
import bpm.gwt.commons.client.dialog.CkanPackageDialog;
import bpm.gwt.commons.client.dialog.DataPreparationDialog;
import bpm.gwt.commons.client.dialog.DataPreparationDialog.IDataPreparationManager;
import bpm.gwt.commons.client.dialog.RepositorySaveDialog;
import bpm.gwt.commons.client.dialog.RepositorySaveDialog.SaveHandler;
import bpm.gwt.commons.client.images.CommonImages;
import bpm.gwt.commons.client.listeners.CkanManager;
import bpm.gwt.commons.client.popup.SharePopup;
import bpm.gwt.commons.client.services.CommonService;
import bpm.gwt.commons.client.services.FmdtServices;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.services.exception.ServiceException;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.commons.client.utils.ToolsGWT;
import bpm.gwt.commons.client.utils.ToolsGWT.TypeCollaboration;
import bpm.gwt.commons.client.viewer.dialog.AirShareDialog;
import bpm.gwt.commons.client.viewer.fmdtdriller.QueryFMDTDrillerPanel;
import bpm.gwt.commons.client.viewer.fmdtdriller.dialog.ExportDialog;
import bpm.gwt.commons.client.viewer.fmdtdriller.dialog.FmdtChoiceDialog;
import bpm.gwt.commons.client.viewer.fmdtdriller.dialog.PromptsValueDialog;
import bpm.gwt.commons.client.viewer.fmdtdriller.dialog.SaveInfosDialog;
import bpm.gwt.commons.shared.InfoUser;
import bpm.gwt.commons.shared.fmdt.FmdtConstant;
import bpm.gwt.commons.shared.fmdt.FmdtModel;
import bpm.gwt.commons.shared.fmdt.FmdtQueryBuilder;
import bpm.gwt.commons.shared.fmdt.FmdtQueryDriller;
import bpm.gwt.commons.shared.fmdt.FmdtRow;
import bpm.gwt.commons.shared.fmdt.FmdtTable;
import bpm.gwt.commons.shared.repository.PortailItemCubeFmdt;
import bpm.gwt.commons.shared.repository.PortailItemFmdt;
import bpm.gwt.commons.shared.repository.PortailItemFmdtChart;
import bpm.gwt.commons.shared.repository.PortailItemFmdtDriller;
import bpm.gwt.commons.shared.repository.PortailRepositoryItem;
import bpm.gwt.commons.shared.repository.SaveItemInformations;
import bpm.gwt.commons.shared.utils.TypeShare;
import bpm.gwt.commons.shared.viewer.LaunchReportInformations;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.chart.SavedChart;
import bpm.vanilla.platform.core.beans.fmdt.FmdtFilter;
import bpm.vanilla.platform.core.beans.resources.CkanPackage;
import bpm.vanilla.platform.core.beans.resources.CkanResource;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.repository.RepositoryItem;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.PopupPanel;

public class FmdtVanillaViewer extends Viewer implements CkanManager, IDataPreparationManager, SaveHandler {

	private VanillaViewer vanillaViewer;
	private InfoUser infoUser;

	private PortailRepositoryItem item;

	private LaunchReportInformations itemInfo;
	private FmdtQueryDriller fmdtDriller;
	private QueryFMDTDrillerPanel fmdtDrillerPanel;
	private Group selectedGroup;
	private List<Group> availableGroups;
	private Boolean isSave = false;
	private Boolean firstPreview = true;
	private Boolean firstCube = true;
	private Boolean firstGraph = true;
	private Boolean firstAnalysis = true;

	public FmdtVanillaViewer(VanillaViewer vanillaViewer, PortailRepositoryItem item, Group selectedGroup, List<Group> availableGroups, InfoUser infoUser) {
		super(vanillaViewer);
		this.vanillaViewer = vanillaViewer;
		this.availableGroups = availableGroups;
		this.item = item;
		this.selectedGroup = selectedGroup;
		this.infoUser = infoUser;

		btnGraphe.setVisible(true);
		btnPreview.setVisible(true);
		btnDesigner.setVisible(true);
		btnCube.setVisible(true);
		btnAnalysis.setVisible(true);

		vanillaViewer.launchReport(this, item, selectedGroup, false, false);
	}

	@Override
	public void defineToolbar(LaunchReportInformations itemInfo) {
		if (itemInfo.getItem().getItem().isCommentable()) {
			btnComment.setVisible(true);
		}
		btnSave.setVisible(true);
		btnShare.setVisible(true);
	}

	@Override
	public void runItem(final LaunchReportInformations itemInfo) {
		this.itemInfo = itemInfo;

		defineToolbar(itemInfo);

		if (item instanceof PortailItemFmdt) {
			FmdtServices.Connect.getInstance().getMetadataInfos(itemInfo.getItem().getId(), new GwtCallbackWrapper<List<FmdtModel>>(this, true, true) {

				@Override
				public void onSuccess(List<FmdtModel> models) {
					FmdtQueryDriller fmdtDriller = buildFmdtDriller(itemInfo);
					loadFmdtChoice(fmdtDriller, models);
				}
			}.getAsyncCallback());
		}
		else if (item instanceof PortailItemFmdtDriller || item instanceof PortailItemFmdtChart) {
			FmdtServices.Connect.getInstance().openView(item.getItem(), new GwtCallbackWrapper<FmdtQueryDriller>(this, true, true) {

				@Override
				public void onSuccess(FmdtQueryDriller fmdtDriller) {
					SavedChart selectedChart = null;
					if (item instanceof PortailItemFmdtChart) {
						SavedChart myChart = ((PortailItemFmdtChart) item).getChart();
						if (fmdtDriller.getCharts() != null) {
							for (SavedChart chart : fmdtDriller.getCharts()) {
								if (chart.equals(myChart)) {
									selectedChart = chart;
									break;
								}
							}
						}
					}
					
					loadFmdtDrillerPart(fmdtDriller, selectedChart);
				}
			}.getAsyncCallback());
		}
	}

	private FmdtQueryDriller buildFmdtDriller(LaunchReportInformations itemInfo) {
		FmdtQueryDriller fmdtDriller = new FmdtQueryDriller();
		fmdtDriller.setMetadataId(itemInfo.getItem().getId());
		return fmdtDriller;
	}

	private void loadFmdtChoice(FmdtQueryDriller fmdtDriller, List<FmdtModel> models) {
		FmdtChoiceDialog dial = new FmdtChoiceDialog(this, fmdtDriller, models);
		dial.center();
	}

	public void loadFmdtDrillerPart(FmdtQueryDriller fmdtQueryDriller, SavedChart selectedChart) {
		this.fmdtDriller = fmdtQueryDriller;
		if (!fmdtQueryDriller.getBuilders().isEmpty()) {
			btnUpdate.setVisible(true);
			isSave = true;
		}
		else
			fmdtQueryDriller.setName(item.getName());

		fmdtDrillerPanel = new QueryFMDTDrillerPanel(this, fmdtQueryDriller, selectedChart, availableGroups, this, infoUser);

		reportPanel.clear();
		reportPanel.add(fmdtDrillerPanel);
		
		if (fmdtQueryDriller.hasChart()) {
			firstGraph = false;
		}
		
		updateUi(selectedChart == null);
	}

	//We hide the toolbar if we load a chart
	private void updateUi(boolean showToolbar) {
		if (itemInfo.getItem().getItem().isCommentable()) {
			btnComment.setVisible(showToolbar);
		}
		btnSave.setVisible(showToolbar);
		
		btnGraphe.setVisible(showToolbar);
		btnPreview.setVisible(showToolbar);
		btnDesigner.setVisible(showToolbar);
		btnCube.setVisible(showToolbar);
		btnAnalysis.setVisible(showToolbar);
	}

	@Override
	public void setItemInfo(LaunchReportInformations itemInfo) {
		this.itemInfo = itemInfo;
	}

	@Override
	public void onSaveClick(ClickEvent event) {
		boolean checkChart = tab == GRAPHE;
		
		fmdtDrillerPanel.getDesignerPanel().refreshBuilder();
		SavedChart chart = fmdtDrillerPanel.getChart();

		final SaveInfosDialog dial = new SaveInfosDialog(FmdtVanillaViewer.this, LabelsConstants.lblCnst.SaveQueryTitle(), fmdtDriller, chart, checkChart, availableGroups, item.getDirectoryId(), false);
		dial.addCloseHandler(new CloseHandler<PopupPanel>() {
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				if (dial.getConfirm()) {
					btnUpdate.setVisible(true);
					isSave = true;
					vanillaViewer.setTabHeaderTitle(fmdtDriller.getName());
					vanillaViewer.refreshTitle();
				}
			}
		});
		dial.center();
	}

	@Override
	public void onExportClick(ClickEvent event) {

		if (fmdtDrillerPanel.getResultPanel() == null) {
			return;
		}

		ExportDialog dial = new ExportDialog(this, fmdtDrillerPanel.getColumnNames(), fmdtDrillerPanel.getResultPanel().getFormattedTable(), fmdtDrillerPanel.getResultPanel().getFlatTable());
		dial.center();

	}

	@Override
	public void onCommentClick(ClickEvent event) {
		CommentPanel commentsPanel = new CommentPanel(this, itemInfo.getItem().getId(), TypeCollaboration.ITEM_NOTE, itemInfo.getSelectedGroup(), itemInfo.getAvailableGroups());
		commentsPanel.show();
		commentsPanel.setPopupPosition(event.getClientX() - 332, event.getClientY() + 20);
	}

	protected void loadResult(Boolean initBuilder, Boolean change) {
//		if (change) {
			if (initBuilder) {
				final FmdtQueryBuilder builder = fmdtDrillerPanel.getDesignerPanel().refreshBuilder();
				if (!builder.getPromptFilters().isEmpty()) {
					final PromptsValueDialog promptDialog = new PromptsValueDialog(builder.getPromptFilters());

					promptDialog.addCloseHandler(new CloseHandler<PopupPanel>() {
						@Override
						public void onClose(CloseEvent<PopupPanel> event) {

							if (promptDialog.isConfirm()) {
								builder.setPromptFilters(promptDialog.getPrompts());
								fmdtDrillerPanel.getDriller().setBuilder(builder);
								fmdtDrillerPanel.loadRequestValues();
							}
							else
								switchView(DESIGNER);
						}
					});

					promptDialog.center();
				}
				else {
					fmdtDrillerPanel.getDriller().setBuilder(builder);
					fmdtDrillerPanel.loadRequestValues();
				}
			}
			else
				fmdtDrillerPanel.loadRequestValues();
//		}
//		else
//			fmdtDrillerPanel.getResult();
	}

	protected void displayDesigner() {
		fmdtDrillerPanel.loadDesigner();
	}

	protected void displayGraphe(Boolean initBuilder, Boolean change) {
		if (change) {
			if (initBuilder) {
				final FmdtQueryBuilder builder = fmdtDrillerPanel.getDesignerPanel().refreshBuilder();
				if (!builder.getPromptFilters().isEmpty()) {
					final PromptsValueDialog promptDialog = new PromptsValueDialog(builder.getPromptFilters());

					promptDialog.addCloseHandler(new CloseHandler<PopupPanel>() {
						@Override
						public void onClose(CloseEvent<PopupPanel> event) {

							if (promptDialog.isConfirm()) {
								builder.setPromptFilters(promptDialog.getPrompts());
								fmdtDrillerPanel.getDriller().setBuilder(builder);
								fmdtDrillerPanel.showGraphePanel(true);
							}
							else
								switchView(DESIGNER);
						}
					});
					promptDialog.center();
				}
				else {
					fmdtDrillerPanel.getDriller().setBuilder(builder);
					fmdtDrillerPanel.showGraphePanel(true);
				}
			}
			else
				fmdtDrillerPanel.showGraphePanel(true);
		}
		else {
			fmdtDrillerPanel.showGraphePanel(false);
		}
	}

	protected void displayCube(Boolean initBuilder, Boolean change) {
		if (change) {
			if (initBuilder) {
				final FmdtQueryBuilder builder = fmdtDrillerPanel.getDesignerPanel().refreshBuilder();
				if (!builder.getPromptFilters().isEmpty()) {
					final PromptsValueDialog promptDialog = new PromptsValueDialog(builder.getPromptFilters());

					promptDialog.addCloseHandler(new CloseHandler<PopupPanel>() {
						@Override
						public void onClose(CloseEvent<PopupPanel> event) {

							if (promptDialog.isConfirm()) {
								builder.setPromptFilters(promptDialog.getPrompts());
								fmdtDrillerPanel.getDriller().setBuilder(builder);
								fmdtDrillerPanel.showCubePanel();
							}
							else
								switchView(DESIGNER);
						}
					});

					promptDialog.center();
				}
				else {
					fmdtDrillerPanel.getDriller().setBuilder(builder);
					fmdtDrillerPanel.showCubePanel();
				}
			}
			else
				fmdtDrillerPanel.showCubePanel();
		}
		else
			fmdtDrillerPanel.getCubePanel();
	}

	protected void displayAnalysis(Boolean initBuilder, Boolean change) {
		if (change) {
			if (initBuilder) {
				final FmdtQueryBuilder builder = fmdtDrillerPanel.getDesignerPanel().refreshBuilder();
				if (!builder.getPromptFilters().isEmpty()) {
					final PromptsValueDialog promptDialog = new PromptsValueDialog(builder.getPromptFilters());

					promptDialog.addCloseHandler(new CloseHandler<PopupPanel>() {
						@Override
						public void onClose(CloseEvent<PopupPanel> event) {

							if (promptDialog.isConfirm()) {
								builder.setPromptFilters(promptDialog.getPrompts());
								fmdtDrillerPanel.getDriller().setBuilder(builder);
								fmdtDrillerPanel.showAnalysisPanel();
							}
							else
								switchView(DESIGNER);
						}
					});
					promptDialog.center();
				}
				else {
					fmdtDrillerPanel.getDriller().setBuilder(builder);
					fmdtDrillerPanel.showAnalysisPanel();
				}
			}
			else
				fmdtDrillerPanel.showAnalysisPanel();
		}
		else
			fmdtDrillerPanel.getAnalysisPanel();
	}

	@Override
	protected void update() {
		boolean checkChart = tab == GRAPHE;
		
		fmdtDrillerPanel.getDesignerPanel().refreshBuilder();
		SavedChart chart = fmdtDrillerPanel.getChart();

		SaveInfosDialog dial = new SaveInfosDialog(FmdtVanillaViewer.this, LabelsConstants.lblCnst.UpdateQueryTitle(), fmdtDriller, chart, checkChart, availableGroups, item.getDirectoryId(), true);
		dial.center();
	}

	@Override
	public void onReportClick(ClickEvent event) {
		final SaveInfosDialog dial;
		fmdtDrillerPanel.getDesignerPanel().refreshBuilder();

		Boolean displayMes = false;
		for (FmdtFilter filter : fmdtDrillerPanel.getDesignerPanel().getBuilder().getFilters()) {
			if (filter.isCreate()) {
				displayMes = true;
				break;
			}
		}
		if (displayMes)
			MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Warning(), LabelsConstants.lblCnst.FilterNoSupportMessage());

		String message;
		if (isSave)
			message = LabelsConstants.lblCnst.updateReportTitle();
		else
			message = LabelsConstants.lblCnst.saveReportTitle();

		dial = new SaveInfosDialog(FmdtVanillaViewer.this, message, fmdtDriller, null, false, availableGroups, item.getDirectoryId(), isSave);
		dial.addCloseHandler(new CloseHandler<PopupPanel>() {
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				if (dial.getConfirm()) {
					if (!isSave) {
						btnUpdate.setVisible(true);
						isSave = true;
						vanillaViewer.setTabHeaderTitle(fmdtDriller.getName());
						vanillaViewer.refreshTitle();
					}

					final InformationsDialog infoFormat = new InformationsDialog(LabelsConstants.lblCnst.FormatReportTitle(), LabelsConstants.lblCnst.Formatted(), LabelsConstants.lblCnst.Flat(), LabelsConstants.lblCnst.FormatReportMessage(), true);

					infoFormat.addCloseHandler(new CloseHandler<PopupPanel>() {
						@Override
						public void onClose(CloseEvent<PopupPanel> event) {
							HashMap<String, String> parameters = new HashMap<String, String>();

							parameters.put(FmdtConstant.METADATAID, String.valueOf(fmdtDriller.getMetadataId()));
							parameters.put(FmdtConstant.MODEL_NAME, fmdtDriller.getModelName());
							parameters.put(FmdtConstant.PACKAGE_NAME, fmdtDriller.getPackageName());
							parameters.put(FmdtConstant.QUERY_NAME, fmdtDriller.getName());
							if (infoFormat.isConfirm()) {
								parameters.put(FmdtConstant.FORMAT_REPORT, "true");
							}

							FmdtServices.Connect.getInstance().generateCubeUrl(LocaleInfo.getCurrentLocale().getLocaleName(), parameters, FmdtConstant.DEST_FWR, new AsyncCallback<String>() {
								@Override
								public void onSuccess(String result) {
									ToolsGWT.doRedirect(result);
								}

								@Override
								public void onFailure(Throwable caught) {
									caught.printStackTrace();
									ExceptionManager.getInstance().handleException(caught, LabelsConstants.lblCnst.Error());
									MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Error(), caught.getMessage());
								}
							});

						}
					});
					infoFormat.center();
				}
			}
		});
		dial.center();
	}

	@Override
	public void onCubefawebClick(ClickEvent event) {
		fmdtDrillerPanel.launchFreeAnalysis();
	}

	@Override
	public void onDesigner(ClickEvent event) {
		if (tab != DESIGNER) {
			switchView(DESIGNER);
		}
	}

	@Override
	public void onPreview(ClickEvent event) {
		if (tab != PREVIEW) {
			switchView(PREVIEW);
		}
	}

	@Override
	public void onGraphe(ClickEvent event) {
		if (tab != GRAPHE) {
			switchView(GRAPHE);
		}
	}

	@Override
	public void onCube(ClickEvent event) {
		if (tab != CUBE) {
			switchView(CUBE);
		}
	}

	public void onAnalysis(ClickEvent event) {
		if (tab != ANALYSIS) {
			switchView(ANALYSIS);
		}
	}

	@Override
	protected void switchView(int newtab) {
		try {
			if (tab == DESIGNER) {
				if (fmdtDrillerPanel.getDesignerPanel().isChange()) {
					firstPreview = true;
					firstCube = true;
					firstGraph = true;
					firstAnalysis = true;
				}
			}
			if (newtab == PREVIEW) {
				if (tab == DESIGNER)
					loadResult(true, firstPreview);
				else
					loadResult(false, firstPreview);

			}
			else if (newtab == DESIGNER) {
				displayDesigner();

			}
			else if (newtab == GRAPHE) {
				if (tab == DESIGNER)
					displayGraphe(true, firstGraph);
				else
					displayGraphe(false, firstGraph);
			}
			if (newtab == CUBE) {
				if (tab == DESIGNER)
					displayCube(true, firstCube);
				else
					displayCube(false, firstCube);
			}
			if (newtab == ANALYSIS) {
				if (tab == DESIGNER)
					displayAnalysis(true, firstAnalysis);
				else
					displayAnalysis(false, firstAnalysis);
			}
			tab = newtab;
			removeStyle();
			if (tab == DESIGNER) {
				btnDesigner.addStyleName(style.focus());
				btnDesigner.setResource(CommonImages.INSTANCE.metadataexplorer_conception());
				btnExport.setVisible(false);
				btnShare.setVisible(false);
				btnSave.setVisible(true);
				btnUpdate.setVisible(isSave);
				btnReport.setVisible(false);
				btnCubefaweb.setVisible(false);
			}
			else if (tab == PREVIEW) {
				btnPreview.addStyleName(style.focus());
				btnPreview.setResource(CommonImages.INSTANCE.metadataexplorer_results());
				btnExport.setVisible(true);
				btnShare.setVisible(true);
				btnSave.setVisible(true);
				btnUpdate.setVisible(isSave);
				btnReport.setVisible(true);
				btnCubefaweb.setVisible(false);
				firstPreview = false;
			}
			else if (tab == CUBE) {
				btnCube.addStyleName(style.focus());
				btnCube.setResource(CommonImages.INSTANCE.metadataexplorer_cube());
				btnExport.setVisible(false);
				btnShare.setVisible(false);
				btnSave.setVisible(false);
				btnUpdate.setVisible(false);
				btnReport.setVisible(false);
				btnCubefaweb.setVisible(true);
				firstCube = false;
			}
			else if (tab == GRAPHE) {
				btnGraphe.addStyleName(style.focus());
				btnGraphe.setResource(CommonImages.INSTANCE.metadataexplorer_chart());
				btnExport.setVisible(false);
				btnShare.setVisible(true);
				btnSave.setVisible(true);
				btnUpdate.setVisible(isSave);
				btnReport.setVisible(false);
				btnCubefaweb.setVisible(false);
				firstGraph = false;
			}
			else if (tab == ANALYSIS) {
				btnAnalysis.addStyleName(style.focus());
				btnAnalysis.setResource(CommonImages.INSTANCE.ic_analysis_black());
				btnExport.setVisible(false);
				btnShare.setVisible(false);
				btnSave.setVisible(false);
				btnUpdate.setVisible(false);
				btnReport.setVisible(false);
				btnCubefaweb.setVisible(false);
				firstAnalysis = false;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void removeStyle() {
		btnDesigner.removeStyleName(style.focus());
		btnPreview.removeStyleName(style.focus());
		btnCube.removeStyleName(style.focus());
		btnGraphe.removeStyleName(style.focus());
		btnAnalysis.removeStyleName(style.focus());

		btnDesigner.setResource(CommonImages.INSTANCE.metadataexplorer_conception_clair());
		btnPreview.setResource(CommonImages.INSTANCE.metadataexplorer_results_clair());
		btnCube.setResource(CommonImages.INSTANCE.metadataexplorer_cube_clair());
		btnGraphe.setResource(CommonImages.INSTANCE.metadataexplorer_chart_clair());
		btnAnalysis.setResource(CommonImages.INSTANCE.ic_analysis_gray());
	}

	public void callFrame(String url, String name) {
		RepositoryItem repo = new RepositoryItem();
		repo.setType(IRepositoryApi.FASD_TYPE);
		if (name != null)
			repo.setItemName(name);
		else
			repo.setItemName(item.getName() + "_cube");

		if (vanillaViewer.getTabManager() != null) {
			vanillaViewer.getTabManager().openViewer(new PortailItemCubeFmdt(url, repo));
		}
	}

	public Group getSelectedGroup() {
		return selectedGroup;
	}

	@Override
	public void onShareClick(ClickEvent event) {
		if (tab == GRAPHE) {
			SharePopup sharePopup = new SharePopup(this, TypeShare.WEB_REPORT);
			sharePopup.setPopupPosition(event.getClientX(), event.getClientY());
			sharePopup.show();
		}
		else {
			fmdtDrillerPanel.getDesignerPanel().refreshBuilder();
			boolean isConnectedToCkan = infoUser.canAccess(VanillaConfiguration.P_CKAN_URL);

			SharePopup sharePopup = null;
			if (isConnectedToCkan) {
				sharePopup = new SharePopup(this, TypeShare.AIR, TypeShare.DATAPREPARATION, TypeShare.CKAN);
			}
			else {
				sharePopup = new SharePopup(this, TypeShare.AIR, TypeShare.DATAPREPARATION);
			}
			sharePopup.setPopupPosition(event.getClientX(), event.getClientY());
			sharePopup.show();
		}
	}

	@Override
	public void openShare(TypeShare typeShare) {
		switch (typeShare) {
		case AIR:
			AirShareDialog airDialog = new AirShareDialog(FmdtVanillaViewer.this, fmdtDriller.getBuilder(), fmdtDriller);
			airDialog.center();
			break;
		case CKAN:
			CkanPackageDialog dial = new CkanPackageDialog(this, null, null, true, null);
			dial.center();
			break;
		case DATAPREPARATION:
			DataPreparationDialog dataPrepDial = new DataPreparationDialog(this, false);
			dataPrepDial.center();
			break;
		case WEB_REPORT:
			FWRReport item;
			try {
				item = fmdtDrillerPanel.getGraphePanel().buildWebReport();
			} catch (ServiceException e) {
				MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Error(), e.getMessage());
				return;
			}

			RepositorySaveDialog<FWRReport> saveDial = new RepositorySaveDialog<FWRReport>(this, item, IRepositoryApi.FWR_TYPE, "", "", availableGroups, true);
			saveDial.center();
			break;
		default:
			break;
		}
	}

	@Override
	public void managePackage(CkanPackage pack, CkanResource resource) {
		
		pack.setSelectedResource(resource);

		FmdtQueryBuilder builder = fmdtDrillerPanel.getDesignerPanel().getBuilder();
		try {
			String ressourceName = resource.getName();
			FmdtRow columnNames = fmdtDrillerPanel.getColumnNames();
			FmdtTable table = fmdtDrillerPanel.getResultPanel().getFlatTable();

			FmdtServices.Connect.getInstance().exportToCkan(ressourceName, pack, columnNames, table, ";", builder, new GwtCallbackWrapper<Void>(this, true, true) {
		
				@Override
				public void onSuccess(Void result) {
					MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Information(), LabelsConstants.lblCnst.DatasetUploadedToCkan());
				}
			}.getAsyncCallback());
		} catch (Exception e) {
			e.printStackTrace();
			MessageHelper.openErrorDialog(LabelsConstants.lblCnst.Information(), LabelsConstants.lblCnst.Error() + ": " + e.getMessage(), LabelsConstants.lblCnst.Close(), LabelsConstants.lblCnst.Close(), false, null);
		}
	}

	@Override
	public void createDataPreparation(String name, String separator) {
		FmdtServices.Connect.getInstance().createDataPreparation(name, fmdtDriller, new GwtCallbackWrapper<Void>(this, true, true) {

			@Override
			public void onSuccess(Void result) {
				MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Information(), LabelsConstants.lblCnst.ThePushToDataPreparationWasSuccessful());
			}

		}.getAsyncCallback());
	}

	@Override
	public void saveItem(SaveItemInformations itemInfos, boolean close) {
		CommonService.Connect.getInstance().saveItem(itemInfos, new GwtCallbackWrapper<RepositoryItem>(FmdtVanillaViewer.this, true, true) {
			@Override
			public void onSuccess(RepositoryItem result) {
				MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Information(), LabelsConstants.lblCnst.TheReportHasBeenCreated());
			}
		}.getAsyncCallback());
	}
}
