package bpm.fd.web.client.utils;

import java.util.ArrayList;
import java.util.List;

import bpm.fd.api.core.model.components.definition.chart.ChartOrderingType;
import bpm.fd.api.core.model.components.definition.chart.DataAggregation;
import bpm.fd.api.core.model.components.definition.chart.DataAggregation.MeasureRendering;
import bpm.fd.core.component.ChartComponent;
import bpm.fd.core.component.ComponentType;
import bpm.fd.core.component.FilterComponent;
import bpm.fd.web.client.ClientSession;
import bpm.fd.web.client.actions.ActionManager;
import bpm.fd.web.client.actions.MoveAction;
import bpm.fd.web.client.actions.WidgetAction;
import bpm.fd.web.client.handlers.HasComponentSelectionHandler;
import bpm.fd.web.client.panels.IDropPanel;
import bpm.fd.web.client.panels.WorkspacePanel;
import bpm.fd.web.client.panels.properties.ChartTypeProperties.TypeChart;
import bpm.fd.web.client.panels.properties.ChartTypeProperties.TypeDisplay;
import bpm.fd.web.client.services.DashboardService;
import bpm.fd.web.client.widgets.DashboardWidget;
import bpm.fd.web.client.widgets.WidgetComposite;
import bpm.gwt.commons.client.charts.ChartObject;
import bpm.gwt.commons.client.loading.IWait;
import bpm.gwt.commons.client.services.CommonService;
import bpm.gwt.commons.client.services.FmdtServices;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.tree.TreeObjectWidget;
import bpm.gwt.commons.shared.InfoUser;
import bpm.gwt.commons.shared.fmdt.metadata.MetadataChart;
import bpm.gwt.commons.shared.fmdt.metadata.MetadataPackage;
import bpm.gwt.commons.shared.fmdt.metadata.MetadataPrompt;
import bpm.gwt.commons.shared.fmdt.metadata.MetadataResource;
import bpm.vanilla.platform.core.beans.chart.ChartColumn;
import bpm.vanilla.platform.core.beans.chart.ChartType;
import bpm.vanilla.platform.core.beans.chart.SavedChart;
import bpm.vanilla.platform.core.beans.chart.Serie;
import bpm.vanilla.platform.core.beans.data.DataColumn;
import bpm.vanilla.platform.core.beans.data.Dataset;
import bpm.vanilla.platform.core.beans.data.Datasource;
import bpm.vanilla.platform.core.beans.data.DatasourceFmdt;
import bpm.vanilla.platform.core.beans.data.DatasourceType;
import bpm.vanilla.platform.core.beans.fmdt.FmdtData;

import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public class WidgetDropHelper {

	public static void dropWidget(DropEvent event, Panel contentPanel, HasComponentSelectionHandler hasSelectionHandler, IDropPanel dropPanel, ActionManager actionManager, WidgetManager widgetManager, IWait waitPanel, Widget parentPanel, boolean isHeader, boolean isContentWidget) {
		int x = event.getNativeEvent().getClientX() - contentPanel.getAbsoluteLeft();
		int y = event.getNativeEvent().getClientY() - contentPanel.getAbsoluteTop();
		hasSelectionHandler.setModified(true);

		String dataWidget = event.getData(TreeObjectWidget.TREE_OBJECT_WIDGET);
		if (dataWidget != null && Boolean.parseBoolean(dataWidget)) {
			// test if not a component

			WorkspacePanel wpanel = (WorkspacePanel) hasSelectionHandler;

			Object item = wpanel.getCreationPanel().getMeatadataDragListener().getDragItem();
			if (item instanceof MetadataResource) {
				MetadataResource fmdtResource = (MetadataResource) item;

				if (fmdtResource instanceof MetadataPrompt) {
					MetadataPrompt prompt = (MetadataPrompt) fmdtResource;
					createComponentForPrompt(prompt, hasSelectionHandler, waitPanel, widgetManager, actionManager, dropPanel, x, y);
					return;
				}

				else {

				}
			}
			else if (item instanceof MetadataChart) {
				MetadataChart chart = (MetadataChart) item;

				createComponentForChart(chart, hasSelectionHandler, waitPanel, widgetManager, actionManager, dropPanel, x, y);
				return;
			}
		}
		else {

			String data = event.getData(ToolHelper.TYPE_TOOL);
			if (data != null && !data.isEmpty()) {
				Integer typeToolIndex = Integer.parseInt(data);
				ComponentType typeTool = ComponentType.valueOf(typeToolIndex);

				if (isHeader && !isAllowedInHeader(typeTool)) {
					return;
				}

				WidgetAction action = new WidgetAction(waitPanel, hasSelectionHandler, actionManager, widgetManager, dropPanel, typeTool, x, y);
				actionManager.launchAction(action, true);
			}

			String generatedId = event.getData(DashboardWidget.WIDGET_ID);
			if (generatedId != null && !generatedId.isEmpty()) {
				int absoluteLeft = extractValue(event, DashboardWidget.ABSOLUTE_LEFT);
				int absoluteTop = extractValue(event, DashboardWidget.ABSOLUTE_TOP);

				x = event.getNativeEvent().getClientX() - contentPanel.getAbsoluteLeft() - absoluteLeft;
				y = event.getNativeEvent().getClientY() - contentPanel.getAbsoluteTop() - absoluteTop;

				WidgetComposite widget = widgetManager.getWidget(generatedId);
				ComponentType typeTool = widget.getComponent().getType();
				if (isHeader && !isAllowedInHeader(typeTool)) {
					return;
				}
				Widget parentWidget = widget.getParent();

				int offsetX = x - widget.getLeft();
				int offsetY = y - widget.getTop();

				if (isContentWidget) {
					if (ToolHelper.isContentWidget(typeTool)) {
						return;
					}
					parentWidget = widget.getParent().getParent();
					event.stopPropagation();
				}

				if (parentWidget != parentPanel) {
					IDropPanel oldPanel = widget.getDropPanel();

					MoveAction action = new MoveAction(dropPanel, oldPanel, widget, x, y, widget.getLeft(), widget.getTop(), widgetManager.getSelectedWidgets(), offsetX, offsetY);
					actionManager.launchAction(action, true);
				}
				else {
					MoveAction action = new MoveAction(widget, x, y, widget.getLeft(), widget.getTop(), widgetManager.getSelectedWidgets(), offsetX, offsetY);
					actionManager.launchAction(action, true);
				}

				return;
			}
		}
	}

	private static void createComponentForPrompt(final MetadataPrompt prompt, final HasComponentSelectionHandler hasSelectionHandler, final IWait waitPanel, final WidgetManager widgetManager, final ActionManager actionManager, final IDropPanel dropPanel, final int x, final int y) {
		waitPanel.showWaitPart(true);

		WorkspacePanel wpanel = (WorkspacePanel) hasSelectionHandler;

		String datasourceName = prompt.getParent().getParent().getName();
		Datasource datasource = createDatasource(datasourceName, prompt.getParent().getParent(), wpanel.getCreationPanel().getInfoUser());
		Dataset dataset = createDataset(prompt, datasource);

		CommonService.Connect.getInstance().addDataset(dataset, new GwtCallbackWrapper<Dataset>(waitPanel, true) {
			@Override
			public void onSuccess(Dataset result) {

				refreshDatasourceDataset();

				FilterComponent filter = new FilterComponent();
				filter.setDataset(result);
				filter.setColumnLabelIndex(0);
				filter.setColumnOrderIndex(0);
				filter.setColumnValueIndex(0);
				filter.setName("filter_" + String.valueOf(new Object().hashCode()));
				filter.setTitle(prompt.getName());

				WidgetAction action = new WidgetAction(waitPanel, hasSelectionHandler, widgetManager, dropPanel, filter, x, y);
				actionManager.launchAction(action, true);
			}
		}.getAsyncCallback());
	}

	private static void createComponentForChart(final MetadataChart metadataChart, final HasComponentSelectionHandler hasSelectionHandler, final IWait waitPanel, final WidgetManager widgetManager, final ActionManager actionManager, final IDropPanel dropPanel, final int x, final int y) {
		WorkspacePanel wpanel = (WorkspacePanel) hasSelectionHandler;

		MetadataPackage pack = metadataChart.getParent().getParent().getParent();
		int metadataId = pack.getParent().getParent().getItemId();
		String modelName = pack.getParent().getName();
		String packageName = pack.getName();
		final String queryName = metadataChart.getParent().getName();

		String datasourceName = metadataChart.getParent().getName();

		final Datasource datasource = createDatasource(datasourceName, pack, wpanel.getCreationPanel().getInfoUser());

		FmdtServices.Connect.getInstance().buildDatasetFromSavedQuery(metadataId, modelName, packageName, queryName, new GwtCallbackWrapper<Dataset>(waitPanel, true, true) {

			@Override
			public void onSuccess(Dataset dataset) {
				dataset.setName(queryName + "_dataset");
				dataset.setDatasource(datasource);

				CommonService.Connect.getInstance().addDataset(dataset, new GwtCallbackWrapper<Dataset>(waitPanel, true, true) {
					@Override
					public void onSuccess(Dataset dataset) {

						refreshDatasourceDataset();

						SavedChart chart = metadataChart.getChart();

						ChartComponent chartComp = new ChartComponent();
						chartComp.setDataset(dataset);
						chartComp.setName("chart_" + String.valueOf(new Object().hashCode()));
						chartComp.setTitle(chart.getTitle());

						// We set the chart type
						TypeChart typeChart = getSelectedTypeChart(chart.getChartType());
						TypeDisplay typeDisplay = chart.getChartType().getValue().equals(ChartObject.RENDERER_MULTI_STACKEDCOLUMN) || chart.getChartType().getValue().equals(ChartObject.RENDERER_MULTI_STACKEDAREA) ? TypeDisplay.STACKED : TypeDisplay.CLASSIC;

						chartComp.setNature(ChartTypeHelper.getChartNature((ChartComponent) chartComp, typeChart, typeDisplay, false));

						// We set the data
						int groupFieldIndex = findFieldIndex(dataset, chart.getAxeX());

						chartComp.setGroupFieldIndex(groupFieldIndex);
						chartComp.setGroupFieldLabelIndex(groupFieldIndex);
						// We are not using subgroup for now
						// if (subGroupField.getSelectedIndex() > -1) {
						// chart.setSubGroupFieldIndex(subGroupField.getSelectedIndex());
						// }

						chartComp.setOrderType(ChartOrderingType.CATEGORY_ASC);

						List<DataAggregation> aggregations = new ArrayList<DataAggregation>();
						if (chart.getSeries() != null) {
							for (Serie<FmdtData> serie : chart.getSeries()) {
								DataAggregation agg = new DataAggregation();
								agg.setMeasureName(serie.getName());
								agg.setApplyOnDistinctSerie(false);
								agg.setOrderType(ChartOrderingType.CATEGORY_LABEL_ASC);
								agg.setPrimaryAxis(false);
								agg.setRendering(MeasureRendering.Column);
								agg.setAggregator(getAggregator(serie.getAggregation()));
								agg.setValueFieldIndex(findFieldIndex(dataset, serie.getChartColumn()) + 1);
								agg.setColorCode(DataAggregation.COLORS[aggregations.size() % DataAggregation.COLORS.length], 0);

								aggregations.add(agg);
							}
						}
						chartComp.setAggregations(aggregations);

						WidgetAction action = new WidgetAction(waitPanel, hasSelectionHandler, widgetManager, dropPanel, chartComp, x, y);
						actionManager.launchAction(action, true);
					}
				}.getAsyncCallback());
			}
		}.getAsyncCallback());
	}

	private static TypeChart getSelectedTypeChart(ChartType chartType) {
		if (chartType.getSubType().equals(ChartObject.TYPE_SIMPLE)) {
			switch (chartType.getValue()) {
			case ChartObject.RENDERER_PIE:
				return TypeChart.PIE;
			case ChartObject.RENDERER_DONUT:
				return TypeChart.PIE;
			case ChartObject.RENDERER_BAR:
				return TypeChart.BAR;
			case ChartObject.RENDERER_COLUMN:
				return TypeChart.COLUMN;
			case ChartObject.RENDERER_LINE:
				return TypeChart.LINE;
			case ChartObject.RENDERER_AREA:
				return TypeChart.AREA;
			case ChartObject.RENDERER_SPLINE:
				return TypeChart.LINE;// TODO
			case ChartObject.RENDERER_PARETO:
				return TypeChart.PIE;// TODO
			case ChartObject.RENDERER_RADAR:
				return TypeChart.RADAR;
			default:
				break;
			}
		}
		else if (chartType.getSubType().equals(ChartObject.TYPE_MULTI)) {
			switch (chartType.getValue()) {
			case ChartObject.RENDERER_MULTI_BAR:
				return TypeChart.BAR;
			case ChartObject.RENDERER_MULTI_COLUMN:
				return TypeChart.COLUMN;
			case ChartObject.RENDERER_MULTI_LINE:
				return TypeChart.LINE;
			case ChartObject.RENDERER_MULTI_AREA:
				return TypeChart.AREA;
			case ChartObject.RENDERER_MULTI_STACKEDCOLUMN:
				return TypeChart.COLUMN;
			case ChartObject.RENDERER_MULTI_STACKEDAREA:
				return TypeChart.AREA;
			case ChartObject.RENDERER_MULTI_STEPLINE:
				return TypeChart.LINE;// TODO
			case ChartObject.RENDERER_RADAR:
				return TypeChart.RADAR;
			default:
				break;
			}
		}

		return TypeChart.BAR;
	}
	
	private static String getAggregator(String selectedAgg) {
		for (String agg : DataAggregation.AGGREGATORS_NAME) {
			if (agg.equalsIgnoreCase(selectedAgg)) {
				return agg;
			}
		}
		return null;
	}

	private static int findFieldIndex(Dataset dataset, ChartColumn<FmdtData> selectedColumn) {
		List<DataColumn> columns = dataset.getMetacolumns();
		if (columns != null) {
			for (int i = 0; i < columns.size(); i++) {
				DataColumn column = columns.get(i);
				if (column.getColumnName().equals(selectedColumn.getItem().getName())) {
					return i;
				}
			}
		}
		return -1;
	}

	private static void refreshDatasourceDataset() {
		DashboardService.Connect.getInstance().getDatasources(new GwtCallbackWrapper<List<Datasource>>(null, false) {
			@Override
			public void onSuccess(List<Datasource> result) {
				ClientSession.getInstance().setDatasources(result);
			}
		}.getAsyncCallback());

	}

	private static Dataset createDataset(MetadataPrompt prompt, Datasource datasource) {
		Dataset dataset = new Dataset();
		dataset.setName(prompt.getName() + "_dataset");
		dataset.setDatasource(datasource);

		dataset.setRequest("");
		return dataset;
	}

	private static Datasource createDatasource(String datasourceName, MetadataPackage pack, InfoUser infoUser) {
		Datasource datasource = new Datasource();
		datasource.setName(datasourceName);
		datasource.setType(DatasourceType.FMDT);

		DatasourceFmdt fmdt = new DatasourceFmdt();
		datasource.setObject(fmdt);

		fmdt.setUser(infoUser.getUser().getLogin());
		fmdt.setPassword(infoUser.getUser().getPassword());
		fmdt.setRepositoryId(infoUser.getRepository().getId());
		fmdt.setItemId(pack.getParent().getParent().getItemId());
		fmdt.setBusinessModel(pack.getParent().getName());
		fmdt.setBusinessPackage(pack.getName());
		fmdt.setGroupId(infoUser.getGroup().getId());
		fmdt.setUrl(infoUser.getVanillaRuntimeUrl());

		return datasource;
	}

	private static boolean isAllowedInHeader(ComponentType typeTool) {
		if (typeTool != ComponentType.IMAGE && typeTool != ComponentType.LABEL && typeTool != ComponentType.BUTTON && typeTool != ComponentType.FILTER) {
			return false;
		}
		return true;
	}

	private static Integer extractValue(DropEvent event, String property) {
		try {
			String value = event.getData(property);
			return Integer.parseInt(value);
		} catch (Exception e) {
			return 0;
		}
	}

}
