package bpm.fwr.client.panels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bpm.fwr.api.beans.Agregate;
import bpm.fwr.api.beans.FWRReport;
import bpm.fwr.api.beans.PageOptions;
import bpm.fwr.api.beans.SaveOptions;
import bpm.fwr.api.beans.WysiwygReportHeader;
import bpm.fwr.api.beans.components.BirtCommentComponents;
import bpm.fwr.api.beans.components.ChartComponent;
import bpm.fwr.api.beans.components.CrossComponent;
import bpm.fwr.api.beans.components.GridComponent;
import bpm.fwr.api.beans.components.IChart;
import bpm.fwr.api.beans.components.IReportComponent;
import bpm.fwr.api.beans.components.ImageComponent;
import bpm.fwr.api.beans.components.TextHTMLComponent;
import bpm.fwr.api.beans.components.VanillaChartComponent;
import bpm.fwr.api.beans.components.VanillaMapComponent;
import bpm.fwr.api.beans.dataset.Column;
import bpm.fwr.api.beans.dataset.DataSet;
import bpm.fwr.api.beans.dataset.FWRFilter;
import bpm.fwr.api.beans.dataset.FwrPrompt;
import bpm.fwr.api.beans.dataset.FwrRelationStrategy;
import bpm.fwr.api.beans.dataset.GroupPrompt;
import bpm.fwr.api.beans.dataset.IResource;
import bpm.fwr.api.beans.dataset.JoinDataSet;
import bpm.fwr.client.action.Action;
import bpm.fwr.client.action.ActionAddWidget;
import bpm.fwr.client.action.ActionMoveReportWidget;
import bpm.fwr.client.action.ActionTrashWidget;
import bpm.fwr.client.action.ActionType;
import bpm.fwr.client.dialogs.TableOptionDialogBox;
import bpm.fwr.client.dialogs.TypeOfListDialog;
import bpm.fwr.client.draggable.HasBin;
import bpm.fwr.client.draggable.dropcontrollers.ReportSheetVerticalPanelDropController;
import bpm.fwr.client.draggable.widgets.DraggablePaletteItem;
import bpm.fwr.client.utils.ChartUtils;
import bpm.fwr.client.utils.SizeComponentConstants;
import bpm.fwr.client.utils.WidgetType;
import bpm.fwr.client.widgets.BirtCommentWidget;
import bpm.fwr.client.widgets.ChartWidget;
import bpm.fwr.client.widgets.CrossTabWidget;
import bpm.fwr.client.widgets.GridOptions;
import bpm.fwr.client.widgets.GridWidget;
import bpm.fwr.client.widgets.ImageWidget;
import bpm.fwr.client.widgets.LabelWidget;
import bpm.fwr.client.widgets.ListWidget;
import bpm.fwr.client.widgets.MapWidget;
import bpm.fwr.client.widgets.ReportWidget;
import bpm.fwr.client.wizard.chart.ChartCreationWizard;
import bpm.fwr.client.wizard.map.VanillaMapsWizard;
import bpm.fwr.shared.models.report.wysiwyg.ReportParameters;
import bpm.fwr.shared.models.report.wysiwyg.ReportParameters.ReportParametersType;
import bpm.gwt.commons.client.listeners.FinishListener;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ReportSheet extends AbsolutePanel implements HasBin, CloseHandler<PopupPanel> {
	private static final String CSS_CROSSTAB = "crosstabPostion";
	private static final String CSS_WIDGET = "widgetMargin";

	private PickupDragController groupDragController, detailDragController, paletteDragController;
	private PickupDragController dataDragController, thirdBinDragC, reportWidgDC;
	private ReportSheetVerticalPanelDropController dropController;

	private HeadReportDefault headReport;
	private List<ReportWidget> components = new ArrayList<ReportWidget>();
	private ReportParameters reportParameters = new ReportParameters(ReportParametersType.DEFAULT);
	private ReportPanel panelParent;
	private VerticalPanel panelDropReporWidget;
	private Integer directoryItemId;

	public ReportSheet(PickupDragController reportWidgDC, PickupDragController dataDragController, PickupDragController groupDragController, PickupDragController detailDragController, PickupDragController thirdBinDragC, PickupDragController paletteDragController, ReportPanel panelParent) {
		this.setPanelParent(panelParent);

		headReport = new HeadReportDefault(panelParent, SizeComponentConstants.WIDTH_WORKING_AREA / 2);
		this.add(headReport);

		panelDropReporWidget = new VerticalPanel();
		panelDropReporWidget.setWidth("96%");
		this.add(panelDropReporWidget);

		dropController = new ReportSheetVerticalPanelDropController(this, panelDropReporWidget);
		reportWidgDC.registerDropController(dropController);

		this.dataDragController = dataDragController;
		this.groupDragController = groupDragController;
		this.detailDragController = detailDragController;
		this.thirdBinDragC = thirdBinDragC;
		this.reportWidgDC = reportWidgDC;
		this.paletteDragController = paletteDragController;

		setSize(SizeComponentConstants.WIDTH_REPORT, SizeComponentConstants.HEIGHT_REPORT);
	}

	public void setSize(int width, int height) {
		setSize(width + "px", height + "px");
	}

	@Override
	protected void onDetach() {
		reportWidgDC.unregisterDropController(dropController);
		super.onDetach();
	}

	public void manageWidget(Widget widget) {
		if (widget instanceof DraggablePaletteItem) {
			DraggablePaletteItem dragHtml = (DraggablePaletteItem) widget;
			switch (dragHtml.getType()) {
			case GRID:
				showTableOptionDialog();
				break;
			case CHART:
				showChartCreationWizard();
				break;
			case LIST:
				showDialogList();
				break;
			case CROSSTAB:
				addCrossTab();
				break;
			case IMAGE:
				addImage(null);
				break;
			case LABEL:
				addLabel(null);
				break;
			case VANILLA_MAP:
				showVanillaMapCreationWizard();
				break;
			case BIRTCOMMENT:
				addBirtComment(null);
				break;
			default:
				showUnknownComponentDialog();
				break;
			}
		}
	}

	public void switchWidget(Widget widget, int index) {
		if (widget instanceof ReportWidget) {
			ReportWidget reportWidget = (ReportWidget) widget;
			int oldIndex = 0;
			for (int i = 0; i < components.size(); i++) {
				if (components.get(i).equals(reportWidget)) {
					oldIndex = i;
					break;
				}
			}

			components.remove(widget);
			components.add(index, reportWidget);

			ActionMoveReportWidget action = new ActionMoveReportWidget(ActionType.MOVE_REPORT_WIDGET, this, oldIndex, index);
			panelParent.addActionToUndoAndClearRedo(action);
		}
	}

	public void switchWidget(int oldIndex, int newIndex) {
		Widget widg = panelDropReporWidget.getWidget(oldIndex);
		ReportWidget widget = components.get(oldIndex);

		panelDropReporWidget.remove(oldIndex);
		panelDropReporWidget.insert(widg, newIndex);
		components.remove(oldIndex);
		components.add(newIndex, widget);
	}

	private void showTableOptionDialog() {
		TableOptionDialogBox dial = new TableOptionDialogBox();
		dial.addFinishListener(finishListener);
		dial.center();
	}

	private void showChartCreationWizard() {
		List<DataSet> dsAvailable = getAvailableDatasets();

		ChartCreationWizard chartCreationWizard = new ChartCreationWizard(this, null, dsAvailable);
		chartCreationWizard.addFinishListener(finishListener);
		chartCreationWizard.center();
	}

	private void showDialogList() {
		TypeOfListDialog dial = new TypeOfListDialog();
		dial.addFinishListener(new FinishListener() {

			@Override
			public void onFinish(Object result, Object source, String result1) {
				addList((Boolean) result);
			}
		});
		dial.center();
	}

	private void showVanillaMapCreationWizard() {
		List<DataSet> dsAvailable = getAvailableDatasets();

		VanillaMapsWizard mapCreationWizard = new VanillaMapsWizard(this, null, dsAvailable);
		mapCreationWizard.addFinishListener(finishListener);
		mapCreationWizard.center();
	}

	private void addTable(GridOptions options) {
		try {
			GridWidget table = new GridWidget(paletteDragController, dataDragController, groupDragController, detailDragController, thirdBinDragC, options, SizeComponentConstants.WIDTH_WORKING_AREA, SizeComponentConstants.HEIGHT_WORKING_AREA, this);
			panelDropReporWidget.add(table);
			table.addStyleName(CSS_WIDGET);
			this.reportWidgDC.makeDraggable(table);
			addComponent(table);

			int index = panelDropReporWidget.getWidgetIndex(table);
			ActionAddWidget action = new ActionAddWidget(ActionType.ADD_GRID, this, table, index);
			panelParent.addActionToUndoAndClearRedo(action);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void addChart(IChart comp) {
		ChartWidget chartWidget = null;

		if (comp instanceof VanillaChartComponent) {
			chartWidget = new ChartWidget(this, comp, ChartUtils.getImageForChart(((VanillaChartComponent) comp).getChartType().getType(), ((VanillaChartComponent) comp).getOptions()), SizeComponentConstants.WIDTH_WORKING_AREA, null);
		} else {
			chartWidget = new ChartWidget(this, comp, ChartUtils.getImageForChart(((ChartComponent) comp).getChartType()), SizeComponentConstants.WIDTH_WORKING_AREA, null);
		}

		panelDropReporWidget.add(chartWidget);
		chartWidget.addStyleName(CSS_WIDGET);
		this.reportWidgDC.makeDraggable(chartWidget);
		addComponent(chartWidget);

		int index = panelDropReporWidget.getWidgetIndex(chartWidget);
		ActionAddWidget action = new ActionAddWidget(ActionType.ADD_CHART, this, chartWidget, index);
		panelParent.addActionToUndoAndClearRedo(action);
	}

	private void addList(boolean automaticGroupingList) {
		ListWidget list = new ListWidget(groupDragController, dataDragController, detailDragController, this, automaticGroupingList);
		panelDropReporWidget.add(list);
		list.addStyleName(CSS_WIDGET);
		this.reportWidgDC.makeDraggable(list);
		addComponent(list);

		int index = panelDropReporWidget.getWidgetIndex(list);
		ActionAddWidget action = new ActionAddWidget(ActionType.ADD_LIST, this, list, index);
		panelParent.addActionToUndoAndClearRedo(action);
	}

	private void addList(GridComponent comp) {
		ListWidget list = new ListWidget(groupDragController, dataDragController, detailDragController, this, comp.isAutomaticGroupingList());
		panelDropReporWidget.add(list);
		list.addStyleName(CSS_WIDGET);
		this.reportWidgDC.makeDraggable(list);
		addComponent(list);

		list.loadComp(comp);
	}

	private void addCrossTab() {
		CrossTabWidget crosstab = new CrossTabWidget(dataDragController, groupDragController, detailDragController, thirdBinDragC, SizeComponentConstants.WIDTH_WORKING_AREA, this, null);
		crosstab.addStyleName(CSS_CROSSTAB);
		crosstab.addStyleName(CSS_WIDGET);
		panelDropReporWidget.add(crosstab);
		this.reportWidgDC.makeDraggable(crosstab);
		addComponent(crosstab);

		int index = panelDropReporWidget.getWidgetIndex(crosstab);
		ActionAddWidget action = new ActionAddWidget(ActionType.ADD_CROSSTAB, this, crosstab, index);
		panelParent.addActionToUndoAndClearRedo(action);
	}

	private void addCrossTab(CrossComponent comp) {
		CrossTabWidget crosstab = new CrossTabWidget(dataDragController, groupDragController, detailDragController, thirdBinDragC, SizeComponentConstants.WIDTH_WORKING_AREA, this, null);
		crosstab.addStyleName(CSS_CROSSTAB);
		crosstab.addStyleName(CSS_WIDGET);
		panelDropReporWidget.add(crosstab);
		this.reportWidgDC.makeDraggable(crosstab);
		addComponent(crosstab);

		crosstab.loadComp(comp);
	}

	private void addImage(ImageComponent image) {
		ImageWidget imgWidget = new ImageWidget(this, SizeComponentConstants.WIDTH_WORKING_AREA, null, null);
		panelDropReporWidget.add(imgWidget);
		imgWidget.addStyleName(CSS_WIDGET);
		this.reportWidgDC.makeDraggable(imgWidget);
		addComponent(imgWidget);

		if (image != null) {
			imgWidget.setUrlImg(image.getUrl());
		} else {
			imgWidget.showDialogUrl(false);
		}

		int index = panelDropReporWidget.getWidgetIndex(imgWidget);
		ActionAddWidget action = new ActionAddWidget(ActionType.ADD_IMAGE, this, imgWidget, index);
		panelParent.addActionToUndoAndClearRedo(action);
	}

	private void addLabel(TextHTMLComponent label) {
		LabelWidget lblWidget = new LabelWidget(this, SizeComponentConstants.WIDTH_WORKING_AREA, null);
		panelDropReporWidget.add(lblWidget);
		lblWidget.addStyleName(CSS_WIDGET);
		this.reportWidgDC.makeDraggable(lblWidget);
		addComponent(lblWidget);

		if (label != null) {
			lblWidget.setText(label.getTextContent());
		} else {
			lblWidget.showDialogText();
		}

		int index = panelDropReporWidget.getWidgetIndex(lblWidget);
		ActionAddWidget action = new ActionAddWidget(ActionType.ADD_LABEL, this, lblWidget, index);
		panelParent.addActionToUndoAndClearRedo(action);
	}

	private void showUnknownComponentDialog() {

	}

	private void addVanillaMap(VanillaMapComponent map) {
		MapWidget mapWidget = new MapWidget(this, map, SizeComponentConstants.WIDTH_WORKING_AREA, null);
		panelDropReporWidget.add(mapWidget);
		mapWidget.addStyleName(CSS_WIDGET);
		this.reportWidgDC.makeDraggable(mapWidget);
		addComponent(mapWidget);

		int index = panelDropReporWidget.getWidgetIndex(mapWidget);
		ActionAddWidget action = new ActionAddWidget(ActionType.ADD_VANILLA_MAP, this, mapWidget, index);
		panelParent.addActionToUndoAndClearRedo(action);
	}

	private void addBirtComment(BirtCommentComponents commentBirt) {
		BirtCommentWidget birtComment = new BirtCommentWidget(this, WidgetType.BIRTCOMMENT, SizeComponentConstants.WIDTH_WORKING_AREA);
		panelDropReporWidget.add(birtComment);
		birtComment.addStyleName(CSS_WIDGET);
		this.reportWidgDC.makeDraggable(birtComment);

		if (commentBirt != null) {
			birtComment.setTitle(commentBirt.getTitle());
			birtComment.setCommentDisplayed(commentBirt.getNumberDisplayed());
			birtComment.setTitle(commentBirt.getTitle());
			birtComment.setCommentDisplayed(commentBirt.getNumberDisplayed());
			birtComment.setName(commentBirt.getName());
		}

		addComponent(birtComment);

		if (commentBirt != null) {

		} else {
			birtComment.showDialogText();
		}

		int index = panelDropReporWidget.getWidgetIndex(birtComment);
		ActionAddWidget action = new ActionAddWidget(ActionType.ADD_BIRT_COMMENT, this, birtComment, index);
		panelParent.addActionToUndoAndClearRedo(action);

	}

	@Override
	public void widgetToTrash(Object widget) {
		if (widget instanceof ReportWidget) {
			ReportWidget reportWidget = (ReportWidget) widget;

			int index = panelDropReporWidget.getWidgetIndex(reportWidget);
			Action action = new ActionTrashWidget(ActionType.TRASH_WIDGET, this, reportWidget, index);
			panelParent.addActionToUndoAndClearRedo(action);

			panelDropReporWidget.remove(reportWidget);
			removeWidget(reportWidget);
		}
	}

	public void setReportParameters(ReportParameters reportParameters) {
		for (ReportWidget widg : getComponents()) {
			if (widg instanceof ListWidget) {
				((ListWidget) widg).setDefaultLanguage(reportParameters.getSelectedLanguage());
			} else if (widg instanceof CrossTabWidget) {
				((CrossTabWidget) widg).setDefaultLanguage(reportParameters.getSelectedLanguage());
			}
		}
		this.reportParameters = reportParameters;
	}

	public ReportParameters getReportParameters() {
		return reportParameters;
	}

	public FWRReport generateReport() {
		FWRReport report = new FWRReport();
		report.setWidth(reportParameters.getWidth());
		report.setHeight(reportParameters.getHeight());
		report.setMargins(reportParameters.getMargins());
		report.setOutput(reportParameters.getOutputType());
		report.setOrientation(reportParameters.getOrientation());
		report.setPageSize(reportParameters.getPageSize());

		report.setWysiwygReportHeader(createReportHeader());
		report.setHeader(createHeader(reportParameters));
		report.setFooter(createFooter(reportParameters));

		if (reportParameters.getSelectedLanguage().isEmpty()) {
			reportParameters.setSelectedLanguage(panelParent.getPanelparent().getLanguageDefaultForMetadataWithId());
		}

		report.setSelectedLanguage(reportParameters.getSelectedLanguage());

		// We set the titles and subtitles
		String title = headReport.getTitle();
		HashMap<String, String> titles = new HashMap<String, String>();
		titles.put("en", title);
		report.setTitle(titles);

		String subtitle = headReport.getSubtitle();
		HashMap<String, String> subtitles = new HashMap<String, String>();
		subtitles.put("en", subtitle);
		report.setSubtitle(subtitles);

		addingComponentToReport(report);

		SaveOptions saveOptions = new SaveOptions();
		saveOptions.setName("saveOption");
		saveOptions.setDirectoryItemid(directoryItemId);
		report.setSaveOptions(saveOptions);

		return report;
	}

	public void addingComponentToReport(FWRReport report) {
		List<IReportComponent> reportComponents = new ArrayList<IReportComponent>();

		int index = 0;
		for (ReportWidget widg : getComponents()) {
			if (widg instanceof ImageWidget) {
				addingImageComponent(reportComponents, (ImageWidget) widg, index, 0);

				index++;
			} else if (widg instanceof LabelWidget) {
				addingLabelComponent(reportComponents, (LabelWidget) widg, index, 0);

				index++;
			} else if (widg instanceof ListWidget) {
				addingListComponent(report, reportComponents, (ListWidget) widg, index, 0);

				index++;
			} else if (widg instanceof MapWidget) {
				addingMapComponent(report, reportComponents, (MapWidget) widg, index, 0);

				index++;
			} else if (widg instanceof ChartWidget) {
				addingChartComponent(report, reportComponents, (ChartWidget) widg, index, 0);

				index++;
			} else if (widg instanceof CrossTabWidget) {
				addingCrossTabComponent(report, reportComponents, (CrossTabWidget) widg, index, 0);

				index++;
			} else if (widg instanceof GridWidget) {
				GridWidget grid = (GridWidget) widg;
				for (int i = 0; i < grid.getGridComponent().size(); i++) {
					for (int j = 0; j < grid.getGridComponent().get(i).size(); j++) {
						ReportWidget gridWidget = grid.getGridComponent().get(i).get(j).getWidget();
						if (gridWidget instanceof ImageWidget) {
							addingImageComponent(reportComponents, (ImageWidget) gridWidget, index, j);
						} else if (gridWidget instanceof LabelWidget) {
							addingLabelComponent(reportComponents, (LabelWidget) gridWidget, index, j);
						} else if (gridWidget instanceof ListWidget) {
							addingListComponent(report, reportComponents, (ListWidget) gridWidget, index, j);
						} else if (gridWidget instanceof MapWidget) {
							addingMapComponent(report, reportComponents, (MapWidget) gridWidget, index, j);
						} else if (gridWidget instanceof ChartWidget) {
							addingChartComponent(report, reportComponents, (ChartWidget) gridWidget, index, j);
						} else if (gridWidget instanceof CrossTabWidget) {
							addingCrossTabComponent(report, reportComponents, (CrossTabWidget) gridWidget, index, j);
						} else if (gridWidget instanceof BirtCommentWidget) {
							addingBirtComment(reportComponents, (BirtCommentWidget) gridWidget, index, j);
						}
					}

					index++;
				}
			} else if (widg instanceof BirtCommentWidget) {
				addingBirtComment(reportComponents, (BirtCommentWidget) widg, index, 0);

				index++;
			}
		}

		report.setComponents(reportComponents);
	}

	private void addingBirtComment(List<IReportComponent> reportComponents, BirtCommentWidget widg, int row, int col) {
		reportComponents.add(createBirtComponent(widg, row, col));
	}

	private void addingImageComponent(List<IReportComponent> reportComponents, ImageWidget widg, int row, int col) {
		reportComponents.add(createImageComponent(widg, row, col));
	}

	private void addingLabelComponent(List<IReportComponent> reportComponents, LabelWidget widg, int row, int col) {
		reportComponents.add(createLabelComponent(widg, row, col));
	}

	private void addingCrossTabComponent(FWRReport report, List<IReportComponent> reportComponents, CrossTabWidget widg, int row, int col) {
		List<IResource> prompts = widg.getPrompts();
		List<FWRFilter> filters = widg.getFilters();
		List<FwrRelationStrategy> strats = widg.getRelations();

		DataSet ds = widg.getDataset();
		addPromptAndFilters(report, ds, prompts, filters, strats);

		if (!widg.getSelectedCells().isEmpty() || !widg.getSelectedCols().isEmpty() || !widg.getSelectedRows().isEmpty()) {
			reportComponents.add(createCrosstabComponent(widg, row, col));
		}
	}

	private void addPromptAndFilters(FWRReport report, DataSet ds, List<IResource> prompts, List<FWRFilter> filters, List<FwrRelationStrategy> strats) {
		if (ds != null) {
			if (ds instanceof JoinDataSet) {
				for (DataSet dataset : ((JoinDataSet) ds).getChilds()) {
					dataset.getPrompts().clear();
					dataset.getFilters().clear();
					dataset.getFwrFilters().clear();
					dataset.getRelationStrategies().clear();

					for (IResource prt : prompts) {
						if (dataset.getDatasource() != null) {
							if (prt instanceof FwrPrompt) {
								FwrPrompt prompt = (FwrPrompt) prt;
								if (dataset.getDatasource().getItemId() == prompt.getMetadataId() && dataset.getDatasource().getBusinessModel().equals(prompt.getModelParent()) && dataset.getDatasource().getBusinessPackage().equals(prompt.getPackageParent())) {
									dataset.addPrompt(prompt);
									report.addFwrPromptResource(prompt);
								}
							} else if (prt instanceof GroupPrompt) {
								boolean canBeAdd = true;
								for (FwrPrompt prompt : ((GroupPrompt) prt).getCascadingPrompts()) {
									if (dataset.getDatasource().getItemId() == prompt.getMetadataId() && dataset.getDatasource().getBusinessModel().equals(prompt.getModelParent()) && dataset.getDatasource().getBusinessPackage().equals(prompt.getPackageParent())) {
										dataset.addPrompt(prompt);
									} else {
										canBeAdd = false;
										break;
									}
								}
								if (canBeAdd) {
									report.addFwrPromptResource(prt);
								}
							}
						}
					}

					for (FWRFilter filter : filters) {
						if (dataset.getDatasource() != null) {
							if (dataset.getDatasource().getItemId() == filter.getMetadataId() && dataset.getDatasource().getBusinessModel().equals(filter.getModelParent()) && dataset.getDatasource().getBusinessPackage().equals(filter.getPackageParent())) {
								dataset.addFilter(filter.getName());
								report.addFilter(filter.getName());
							}
						}
					}

					for (FwrRelationStrategy strat : strats) {
						if (dataset.getDatasource() != null) {
							if (dataset.getDatasource().getItemId() == strat.getMetadataId() && dataset.getDatasource().getBusinessModel().equals(strat.getModelParent())) {
								dataset.addRelationStrategy(strat);
								report.addRelation(strat);
							}
						}
					}
				}
			} else {
				ds.getPrompts().clear();
				ds.getFilters().clear();
				ds.getFwrFilters().clear();
				ds.getRelationStrategies().clear();

				for (IResource prt : prompts) {
					if (ds.getDatasource() != null) {
						if (prt instanceof FwrPrompt) {
							FwrPrompt prompt = (FwrPrompt) prt;
							if (ds.getDatasource().getItemId() == prompt.getMetadataId() && ds.getDatasource().getBusinessModel().equals(prompt.getModelParent()) && ds.getDatasource().getBusinessPackage().equals(prompt.getPackageParent())) {
								ds.addPrompt(prompt);
								report.addFwrPromptResource(prompt);
							}
						} else if (prt instanceof GroupPrompt) {
							boolean canBeAdd = true;
							for (FwrPrompt prompt : ((GroupPrompt) prt).getCascadingPrompts()) {
								if (ds.getDatasource().getItemId() == prompt.getMetadataId() && ds.getDatasource().getBusinessModel().equals(prompt.getModelParent()) && ds.getDatasource().getBusinessPackage().equals(prompt.getPackageParent())) {
									ds.addPrompt(prompt);
								} else {
									canBeAdd = false;
									break;
								}
							}
							if (canBeAdd) {
								report.addFwrPromptResource(prt);
							}
						}
					}
				}

				for (FWRFilter filter : filters) {
					if (ds.getDatasource() != null) {
						if (ds.getDatasource().getItemId() == filter.getMetadataId() && ds.getDatasource().getBusinessModel().equals(filter.getModelParent()) && ds.getDatasource().getBusinessPackage().equals(filter.getPackageParent())) {
							ds.addFilter(filter.getName());
							report.addFilter(filter.getName());
						}
					}
				}

				for (FwrRelationStrategy strat : strats) {
					if (ds.getDatasource() != null) {
						if (ds.getDatasource().getItemId() == strat.getMetadataId() && ds.getDatasource().getBusinessModel().equals(strat.getModelParent())) {
							ds.addRelationStrategy(strat);
							report.addRelation(strat);
						}
					}
				}
			}
		}
	}

	private void addingChartComponent(FWRReport report, List<IReportComponent> reportComponents, ChartWidget widg, int row, int col) {
		List<IResource> prompts = widg.getPrompts();
		List<FWRFilter> filters = widg.getFilters();
		List<FwrRelationStrategy> strats = widg.getRelations();

		DataSet ds = widg.getChartComponent(row, col).getDataset();
		addPromptAndFilters(report, ds, prompts, filters, strats);

		reportComponents.add((IReportComponent) widg.getChartComponent(row, col));
	}

	private void addingMapComponent(FWRReport report, List<IReportComponent> reportComponents, MapWidget widg, int row, int col) {
		List<IResource> prompts = widg.getPrompts();
		List<FWRFilter> filters = widg.getFilters();
		List<FwrRelationStrategy> strats = widg.getRelations();

		DataSet ds = widg.getMapComponent(row, col).getDataset();
		addPromptAndFilters(report, ds, prompts, filters, strats);

		reportComponents.add(widg.getMapComponent(row, col));
	}

	private void addingListComponent(FWRReport report, List<IReportComponent> reportComponents, ListWidget widg, int row, int col) {
		List<IResource> prompts = widg.getPrompts();
		List<FWRFilter> filters = widg.getFilters();
		List<FwrRelationStrategy> strats = widg.getRelations();

		DataSet ds = ((ListWidget) widg).getDataset();
		addPromptAndFilters(report, ds, prompts, filters, strats);

		if (!widg.getDetailColumns().isEmpty() || !widg.getGroupColumns().isEmpty()) {
			reportComponents.add(createListComponent(widg, row, col));
		}
	}

	public Integer getDirectoryItemId() {
		return directoryItemId;
	}

	private WysiwygReportHeader createReportHeader() {
		TextHTMLComponent topLeft = new TextHTMLComponent(headReport.getLblTopLeft());
		TextHTMLComponent topRight = new TextHTMLComponent(headReport.getLblTopRight());

		String imgTopLeftUrl = headReport.getImgTopLeft();
		String imgTopRightUrl = headReport.getImgTopRight();

		ImageComponent imageLeft = null;
		if (!imgTopLeftUrl.equals("")) {
			imageLeft = new ImageComponent();
			imageLeft.setName("imageLeft");
			imageLeft.setType("jpg");
			imageLeft.setUrl(imgTopLeftUrl);
		}

		ImageComponent imageRight = null;
		if (!imgTopRightUrl.equals("")) {
			imageRight = new ImageComponent();
			imageRight.setName("imageRight");
			imageRight.setType("jpg");
			imageRight.setUrl(imgTopRightUrl);
		}

		WysiwygReportHeader wysiwygReportHeader = new WysiwygReportHeader();
		wysiwygReportHeader.setLblTopLeft(topLeft);
		wysiwygReportHeader.setLblTopRight(topRight);
		wysiwygReportHeader.setImagesTopLeft(imageLeft);
		wysiwygReportHeader.setImagesTopRight(imageRight);

		return wysiwygReportHeader;
	}

	private PageOptions createHeader(ReportParameters reportParameters) {
		PageOptions header = new PageOptions();
		header.setNumber(reportParameters.isNbPageTopLeft());
		header.setNumberRight(reportParameters.isNbPageTopRight());

		if (reportParameters.getTopLeft() != null) {
			header.setText(reportParameters.getTopLeft());
		} else {
			header.setText("");
		}

		if (reportParameters.getTopRight() != null) {
			header.setTextRight(reportParameters.getTopRight());
		} else {
			header.setTextRight("");
		}

		return header;
	}

	private PageOptions createFooter(ReportParameters reportParameters) {
		PageOptions footer = new PageOptions();
		footer.setNumber(reportParameters.isNbPageBottomLeft());
		footer.setNumberRight(reportParameters.isNbPageBottomRight());

		if (reportParameters.getBottomLeft() != null) {
			footer.setText(reportParameters.getBottomLeft());
		} else {
			footer.setText("");
		}

		if (reportParameters.getBottomRight() != null) {
			footer.setTextRight(reportParameters.getBottomRight());
		} else {
			footer.setTextRight("");
		}

		return footer;
	}

	private GridComponent createListComponent(ListWidget listWidget, int row, int col) {
		GridComponent component = new GridComponent();
		component.setDataset(listWidget.getDataset());
		component.setColumns(listWidget.getDetailColumns());
		component.setGroups(listWidget.getGroupColumns());
		component.setPrompts(listWidget.getPrompts());
		component.setFwrFilters(listWidget.getFilters());
		component.setRelations(listWidget.getRelations());
		component.setShowHeader(listWidget.showHeader());
		component.setAutomaticGroupingList(listWidget.isAutomaticGroupingList());
		// component.setTemplate(new DefaultTemplate());
		component.setX(col);
		component.setY(row);

		return component;
	}

	private CrossComponent createCrosstabComponent(CrossTabWidget crosstabWidget, int row, int col) {
		CrossComponent component = new CrossComponent();
		component.setDataset(crosstabWidget.getDataset());
		component.setCrossCols(crosstabWidget.getSelectedCols());
		component.setCrossCells(crosstabWidget.getSelectedCells());
		component.setCrossRows(crosstabWidget.getSelectedRows());
		component.setPrompts(crosstabWidget.getPrompts());
		component.setFwrFilters(crosstabWidget.getFilters());
		component.setRelations(crosstabWidget.getRelations());
		// component.setxTemplate(Templates.COLOR_SCHEMA_BLUE_VALUE[1]);
		component.setX(col);
		component.setY(row);

		return component;
	}

	private ImageComponent createImageComponent(ImageWidget imgWidget, int row, int col) {
		ImageComponent image = new ImageComponent();
		image.setUrl(imgWidget.getUrlImg());
		image.setX(col);
		image.setY(row);
		return image;
	}

	private TextHTMLComponent createLabelComponent(LabelWidget lblWidget, int row, int col) {
		TextHTMLComponent label = new TextHTMLComponent();
		label.setTextContent(lblWidget.getText());
		label.setX(col);
		label.setY(row);
		return label;
	}

	private BirtCommentComponents createBirtComponent(BirtCommentWidget birtComWidget, int row, int col) {
		BirtCommentComponents comment = new BirtCommentComponents();
		comment.setTitle(birtComWidget.getTitle());
		comment.setNumberDisplayed(birtComWidget.getCommentDisplayed());
		comment.setX(col);
		comment.setY(row);
		comment.setName(birtComWidget.getName());
		return comment;
	}

	// Load report part
	public void loadReport(FWRReport report) {
		this.directoryItemId = report.getSaveOptions() != null ? report.getSaveOptions().getDirectoryItemid() : null;

		if (report.getWysiwygReportHeader() != null) {
			loadReportHeader(report.getWysiwygReportHeader());
		}

		loadReportParameters(report, report.getHeader(), report.getFooter());

		// We set the titles and subtitles
		headReport.setLblTitle(report.getTitle("en"));
		headReport.setLblSubtitle(report.getSubtitle("en"));

		for (IReportComponent comp : report.getComponents()) {
			if (comp instanceof ImageComponent) {
				addImage((ImageComponent) comp);
			} else if (comp instanceof TextHTMLComponent) {
				addLabel((TextHTMLComponent) comp);
			} else if (comp instanceof GridComponent) {
				addList((GridComponent) comp);
			} else if (comp instanceof CrossComponent) {
				addCrossTab((CrossComponent) comp);
			} else if (comp instanceof IChart) {
				addChart((IChart) comp);
			} else if (comp instanceof VanillaMapComponent) {
				addVanillaMap((VanillaMapComponent) comp);
			} else if (comp instanceof BirtCommentComponents) {
				addBirtComment((BirtCommentComponents) comp);
			}
		}
	}

	public void loadReportFromQuery(FWRReport report, DataSet dataset, Boolean formatted) {
		if (report.getSaveOptions() != null)
			loadReport(report);

		ListWidget list = new ListWidget(groupDragController, dataDragController, detailDragController, this, false);
		GridComponent comp = new GridComponent();
		comp.setDataset(dataset);
		comp.setFilters(dataset.getFilters());
		comp.setFwrFilters(dataset.getFwrFilters());

		for (Column col : dataset.getColumns()) {
			if (col.isAgregate()) {
				comp.addAgregate(new Agregate(col.getName(), col.getFormat()));
			}
			else
				col.setSorted(true);
		}
		List<IResource> prompts = new ArrayList<IResource>();
		for (FwrPrompt prompt : dataset.getPrompts()) {
			prompts.add((IResource) prompt);
		}
		comp.setPrompts(prompts);
		
		if(!formatted)
			comp.setColumns(new ArrayList<Column>(dataset.getColumns()));
		else{
			List<Column> columns= new ArrayList<Column>();
			List<Column> groups= new ArrayList<Column>();
			for(Column col : dataset.getColumns()){
				if(col.isAgregate() || col.isCalculated()){
					columns.add(col);
				}
				else
					groups.add(col);
			}
			comp.setColumns(columns);
			comp.setGroups(groups);
		}
		
		list.loadComp(comp);
		panelDropReporWidget.add(list);
		list.addStyleName(CSS_WIDGET);
		this.reportWidgDC.makeDraggable(list);
		addComponent(list);
	}

	private void loadReportHeader(WysiwygReportHeader header) {
		headReport.setLblTopLeft(header.getLblTopLeft().getTextContent());
		headReport.setLblTopRight(header.getLblTopRight().getTextContent());

		if (header.getImagesTopLeft() != null) {
			headReport.setImgTopLeft(header.getImagesTopLeft());
		}
		if (header.getImagesTopRight() != null) {
			headReport.setImgTopRight(header.getImagesTopRight());
		}
	}

	private void loadReportParameters(FWRReport report, PageOptions header, PageOptions footer) {
		if (header != null) {
			reportParameters.setNbPageTopLeft(header.isNumber());
			reportParameters.setNbPageTopRight(header.isNumberRight());
			reportParameters.setTopLeft(header.getText());
			reportParameters.setTopRight(header.getTextRight());
		}

		if (footer != null) {
			reportParameters.setNbPageBottomLeft(footer.isNumber());
			reportParameters.setNbPageBottomRight(footer.isNumberRight());
			reportParameters.setBottomLeft(footer.getText());
			reportParameters.setBottomRight(footer.getTextRight());
		}

		reportParameters.setWidth(report.getWidth());
		reportParameters.setHeight(report.getHeight());
		reportParameters.setMargins(report.getMargins());
		reportParameters.setOutputType(report.getOutput());
		reportParameters.setOrientation(report.getOrientation());
	}

	public void setPanelParent(ReportPanel panelParent) {
		this.panelParent = panelParent;
	}

	public ReportPanel getPanelParent() {
		return panelParent;
	}

	public List<DataSet> getAvailableDatasets() {
		List<DataSet> ds = new ArrayList<DataSet>();
		for (ReportWidget widget : getComponents()) {
			if (widget instanceof ListWidget) {
				DataSet dataset = ((ListWidget) widget).getDataset();
				if (dataset != null && dataset instanceof JoinDataSet) {
					ds.addAll(((JoinDataSet) ds).getChilds());
				} else if (dataset != null) {
					ds.add(dataset);
				}
			} else if (widget instanceof ChartWidget) {
				DataSet dataset = ((ChartWidget) widget).getChartComponent(0, 0).getDataset();
				if (dataset != null && dataset instanceof JoinDataSet) {
					ds.addAll(((JoinDataSet) ds).getChilds());
				} else if (dataset != null) {
					ds.add(dataset);
				}
			} else if (widget instanceof CrossTabWidget) {
				DataSet dataset = ((CrossTabWidget) widget).getDataset();
				if (dataset != null && dataset instanceof JoinDataSet) {
					ds.addAll(((JoinDataSet) ds).getChilds());
				} else if (dataset != null) {
					ds.add(dataset);
				}
			}
		}
		return ds;
	}

	public List<ReportWidget> getComponents() {
		return components;
	}

	public void addComponent(ReportWidget widget) {
		this.components.add(widget);
		panelParent.refreshListBoxComponents();
	}

	public void removeWidget(ReportWidget widget) {
		this.components.remove(widget);
		panelParent.refreshListBoxComponents();
	}

	public List<ReportWidget> getReportComponentsForFilterPrompt() {
		List<ReportWidget> reportWidgets = new ArrayList<ReportWidget>();
		for (ReportWidget widg : getComponents()) {
			if (widg instanceof ListWidget || widg instanceof CrossTabWidget || widg instanceof ChartWidget || widg instanceof MapWidget) {
				reportWidgets.add(widg);
			}
		}
		return reportWidgets;
	}

	public void restoreReportWidget(ActionType type, ReportWidget widget, int indexInReport, boolean addAction) {
		panelDropReporWidget.insert(widget, indexInReport);
		addComponent(widget);

		if (addAction) {
			ActionAddWidget action = new ActionAddWidget(type, this, widget, indexInReport);
			panelParent.replaceLastActionToUndo(action);
		}
	}

	public void deleteReportWidget(ReportWidget widget, int indexInReport, boolean addAction) {
		if (addAction) {
			Action action = new ActionTrashWidget(ActionType.TRASH_WIDGET, this, widget, indexInReport);
			panelParent.replaceLastActionToUndo(action);
		}

		panelDropReporWidget.remove(widget);
		removeWidget(widget);
	}

	private FinishListener finishListener = new FinishListener() {

		@Override
		public void onFinish(Object result, Object source, String result1) {
			if (source instanceof ChartCreationWizard) {
				if (result instanceof IChart) {
					addChart((IChart) result);
				}
			} else if (source instanceof TableOptionDialogBox) {
				if (result instanceof GridOptions) {
					addTable((GridOptions) result);
				}
			} else if (source instanceof VanillaMapsWizard) {
				if (result instanceof VanillaMapComponent) {
					addVanillaMap((VanillaMapComponent) result);
				}
			}

		}
	};

	@Override
	public void onClose(CloseEvent<PopupPanel> event) {
		if (event.getSource() instanceof VanillaMapsWizard) {
			System.out.println("OK");
			addVanillaMap(null);
		}
	}
}
