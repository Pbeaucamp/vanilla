package bpm.gwt.commons.client.viewer.fmdtdriller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import bpm.gwt.commons.client.ExceptionManager;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.charts.ChartJsPanel;
import bpm.gwt.commons.client.charts.ChartObject;
import bpm.gwt.commons.client.charts.ChartValue;
import bpm.gwt.commons.client.charts.ValueMultiSerie;
import bpm.gwt.commons.client.charts.ValuePointSerie;
import bpm.gwt.commons.client.charts.ValueSimpleSerie;
import bpm.gwt.commons.client.loading.IWait;
import bpm.gwt.commons.client.services.FmdtServices;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.commons.shared.fmdt.FmdtNode;
import bpm.gwt.commons.shared.fmdt.FmdtQueryBuilder;
import bpm.gwt.commons.shared.fmdt.FmdtQueryDriller;
import bpm.gwt.commons.shared.fmdt.FmdtRow;
import bpm.gwt.commons.shared.fmdt.FmdtTable;
import bpm.gwt.commons.shared.fmdt.HtmlFocusPanel;
import bpm.gwt.commons.shared.fmdt.Score;
import bpm.vanilla.platform.core.beans.fmdt.FmdtAggregate;
import bpm.vanilla.platform.core.beans.fmdt.FmdtColumn;
import bpm.vanilla.platform.core.beans.fmdt.FmdtData;
import bpm.vanilla.platform.core.beans.fmdt.FmdtFilter;
import bpm.vanilla.platform.core.beans.fmdt.FmdtFormula;

import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DragLeaveEvent;
import com.google.gwt.event.dom.client.DragLeaveHandler;
import com.google.gwt.event.dom.client.DragOverEvent;
import com.google.gwt.event.dom.client.DragOverHandler;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.event.dom.client.DropHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ValueListBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;

public class AnalysisDesignerPanel extends Composite {

	private static AnalysisDesignerPanelUiBinder uiBinder = GWT.create(AnalysisDesignerPanelUiBinder.class);

	interface AnalysisDesignerPanelUiBinder extends UiBinder<Widget, AnalysisDesignerPanel> {
	}

	interface MyStyle extends CssResource {
		String lstVal();

		String green();

		String blue();

		String red();

		String cellEmpty();

		String datagrid();

		String cellNoEmpty();

		String container();

		String chartfd();
	}

	public interface DataGridResource extends DataGrid.Resources {
		@Source({ "Datagrid.css" })
		com.google.gwt.user.cellview.client.DataGrid.Style dataGridStyle();
	};

	DataGridResource ressource = GWT.create(DataGridResource.class);;

	@UiField
	static MyStyle style;

	@UiField
	HTMLPanel panelColumns, pColumns;

	@UiField
	SimplePanel lstMethod, lstAlgos;

	@UiField
	HtmlFocusPanel txtCorreX, txtVariablex, txtVariabley, panelVariablex, panelVariabley, dockPanel;

	@UiField
	HTMLPanel panelParameters, panelContentParameter, panelVariables, panelX, panelY, panelClass, panelCorrelation, panelTrain;

	@UiField
	static HTMLPanel chart;

	@UiField
	TextBox nbClass, percTrain;

	@UiField
	Button btnLaunch;

	private static final String DIV_CONTAINER = "chartContainer";
	private String container;

	private ValueListBox<MethodsType> methods = new ValueListBox<MethodsType>(new Renderer<MethodsType>() {
		@Override
		public String render(MethodsType object) {
			return object != null ? object.getValue() : "";
		}

		@Override
		public void render(MethodsType object, Appendable appendable) throws IOException {
			appendable.append(render(object));
		}
	});

	private List<MethodsType> listMethod = new ArrayList<MethodsType>(Arrays.asList(new MethodsType("Clustering", "Clustering"), new MethodsType(LabelsConstants.lblCnst.ColumnsDependance(), "dependence"), new MethodsType("Classification", "Classification")));

	private ValueListBox<MethodsType> algos = new ValueListBox<MethodsType>(new Renderer<MethodsType>() {
		@Override
		public String render(MethodsType object) {
			return object != null ? object.getValue() : "";
		}

		@Override
		public void render(MethodsType object, Appendable appendable) throws IOException {
			appendable.append(render(object));
		}
	});

	private List<MethodsType> listAlgos1 = new ArrayList<MethodsType>(Arrays.asList(new MethodsType("K-means", "classKmean")));
	private List<MethodsType> listAlgos2 = new ArrayList<MethodsType>(Arrays.asList(new MethodsType(LabelsConstants.lblCnst.CorrelationMatrix(), "dependenceCorrelation")));
	private List<MethodsType> listAlgos3 = new ArrayList<MethodsType>(Arrays.asList(new MethodsType(LabelsConstants.lblCnst.DecisionTree(), "decisionTree")));

	private static List<String> colors = Arrays.asList("0075c2,1aaf5d,f2c500,f45b00,8e0000,0e948c,8cbb2c,f3de00,c02d00,5b0101");

	private IWait waitPanel;
	private QueryFMDTDrillerPanel drillerPanel;

	private FmdtQueryBuilder builder;
	private FmdtQueryDriller driller;

	private List<FmdtData> columns = new ArrayList<FmdtData>();
	private List<FmdtData> allcolumns = new ArrayList<FmdtData>();
	private List<FmdtData> variablesX = new ArrayList<FmdtData>();
	private List<FmdtData> variablesY = new ArrayList<FmdtData>();
	private Composite selected = null;
	private static List<FmdtNode> listNodes = new ArrayList<FmdtNode>();
	private static HTMLPanel displayValues;

	public AnalysisDesignerPanel(FmdtQueryBuilder builder, FmdtQueryDriller driller, final IWait waitPanel, QueryFMDTDrillerPanel drillerPanel) {
		initWidget(uiBinder.createAndBindUi(this));
		this.waitPanel = waitPanel;
		this.drillerPanel = drillerPanel;
		this.builder = initBuilder(builder);
		this.columns = new ArrayList<FmdtData>(builder.getListColumns());
		this.allcolumns = new ArrayList<FmdtData>(builder.getListColumns());
		this.driller = driller;
		initPanel();
		if (!builder.getPromptFilters().isEmpty())
			MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Information(), LabelsConstants.lblCnst.CubePromptWarning());
	}

	private FmdtQueryBuilder initBuilder(FmdtQueryBuilder builder) {
		FmdtQueryBuilder newBuilder = new FmdtQueryBuilder();
		newBuilder.setDistinct(builder.isDistinct());
		newBuilder.setFilters(new ArrayList<FmdtFilter>(builder.getFilters()));
		newBuilder.setLimit(builder.isLimit());
		newBuilder.setName(builder.getName());
		newBuilder.setNbLimit(builder.getNbLimit());
		newBuilder.setPromptFilters(builder.getPromptFilters());
		return newBuilder;
	}

	private void initPanel() {
		refreshColumnPanel();

		methods.addStyleName(style.lstVal());
		methods.setValue(listMethod.get(0));
		methods.setAcceptableValues(listMethod);
		lstMethod.add(methods);

		methods.addValueChangeHandler(new ValueChangeHandler<MethodsType>() {
			@Override
			public void onValueChange(ValueChangeEvent<MethodsType> event) {
				if (((MethodsType) methods.getValue()).getKey().equals("Clustering")) {
					algos.setValue(listAlgos1.get(0));
					algos.setAcceptableValues(listAlgos1);
				} else if (((MethodsType) methods.getValue()).getKey().equals("dependence")) {
					algos.setValue(listAlgos2.get(0));
					algos.setAcceptableValues(listAlgos2);
				} else
					algos.setValue(listAlgos3.get(0));
				algos.setAcceptableValues(listAlgos3);

				refreshPanel();
			}
		});

		algos.addStyleName(style.lstVal());
		algos.setValue(listAlgos1.get(0));
		algos.setAcceptableValues(listAlgos1);

		algos.addValueChangeHandler(new ValueChangeHandler<MethodsType>() {

			@Override
			public void onValueChange(ValueChangeEvent<MethodsType> event) {
				refreshPanel();
			}
		});

		lstAlgos.add(algos);

		// txtVariablex.setText(allcolumns.get(0).getLabel());
		// txtVariabley.setText(allcolumns.get(1).getLabel());

		variablesX = new ArrayList<FmdtData>();
		variablesX.add(allcolumns.get(0));
		variablesY = new ArrayList<FmdtData>();
		variablesY.add(allcolumns.get(1));

		panelCorrelation.setVisible(false);
		panelTrain.setVisible(false);
		/*
		 * panelVariablex.addDragOverHandler(dragOverHandler);
		 * panelVariablex.addDragLeaveHandler(dragLeaveHandler);
		 * panelVariablex.addDropHandler(dropHandlerX);
		 * 
		 * panelVariabley.addDragOverHandler(dragOverHandler);
		 * panelVariabley.addDragLeaveHandler(dragLeaveHandler);
		 * panelVariabley.addDropHandler(dropHandlerY);
		 */
		dockPanel.addDragOverHandler(dragOverHandler);
		dockPanel.addDragLeaveHandler(dragLeaveHandler);
		dockPanel.addDropHandler(dropHandlerColumn);

		txtVariablex.addDragOverHandler(dragOverHandler);
		txtVariablex.addDragLeaveHandler(dragLeaveHandler);
		txtVariablex.addDropHandler(dropHandlertxtX);

		txtVariabley.addDragOverHandler(dragOverHandler);
		txtVariabley.addDragLeaveHandler(dragLeaveHandler);
		txtVariabley.addDropHandler(dropHandlertxtY);

		txtCorreX.addDragOverHandler(dragOverHandler);
		txtCorreX.addDragLeaveHandler(dragLeaveHandler);
		txtCorreX.addDropHandler(dropHandlerAreaX);

	}

	private void refreshPanel() {
		clearPanel();
		refreshColumnPanel();
		if (((MethodsType) algos.getValue()).getKey().equals("dependenceCorrelation")) {
			panelX.setVisible(false);
			panelY.setVisible(false);
			panelClass.setVisible(false);
			panelTrain.setVisible(false);
			// txtCorreX.setVisibleLines(5);
			panelCorrelation.setVisible(true);

//			String text = "";
//			for (FmdtData col : allcolumns) {
//				text += col.getLabel() + "\n";
//			}
			// txtCorreX.setText(text);
			variablesX = new ArrayList<FmdtData>(allcolumns);
		} else if (((MethodsType) algos.getValue()).getKey().equals("classKmean")) {
			panelX.setVisible(true);
			panelY.setVisible(true);
			panelClass.setVisible(true);
			panelTrain.setVisible(false);
			panelCorrelation.setVisible(false);

			variablesX = new ArrayList<FmdtData>();
			variablesX.add(allcolumns.get(0));
			variablesY = new ArrayList<FmdtData>();
			variablesY.add(allcolumns.get(1));

			// txtVariablex.setText(allcolumns.get(0).getLabel());
			// txtVariabley.setText(allcolumns.get(1).getLabel());
		} else if (((MethodsType) algos.getValue()).getKey().equals("decisionTree")) {
			panelX.setVisible(false);
			panelY.setVisible(true);
			panelClass.setVisible(false);
			panelTrain.setVisible(true);

			// txtCorreX.setVisibleLines(5);
			panelCorrelation.setVisible(true);
			variablesX = new ArrayList<FmdtData>();
//			String text = "";
			for (int i = 0; i < allcolumns.size() - 1; i++) {
//				text += allcolumns.get(i).getLabel() + "\n";
				variablesX.add(allcolumns.get(i));
			}
			// txtCorreX.setText(text);
			variablesY = new ArrayList<FmdtData>();
			variablesY.add(allcolumns.get(allcolumns.size() - 1));

			// txtVariabley.setText(allcolumns.get(allcolumns.size() -
			// 1).getLabel());
		}
	}

	private void clearPanel() {
		List<Widget> widgets = new ArrayList<Widget>();

		for (int i = 0; i < txtVariabley.getWidgetCount(); i++) {
			if (txtVariabley.getWidget(i) instanceof ColumnDraggable) {
				widgets.add(txtVariabley.getWidget(i));
			}
		}
		for (int i = 0; i < txtVariablex.getWidgetCount(); i++) {
			if (txtVariablex.getWidget(i) instanceof ColumnDraggable) {
				widgets.add(txtVariablex.getWidget(i));
			}
		}
		for (int i = 0; i < txtCorreX.getWidgetCount(); i++) {
			if (txtCorreX.getWidget(i) instanceof ColumnDraggable) {
				widgets.add(txtCorreX.getWidget(i));
			}
		}
		for (Widget widget : widgets) {
			widget.removeFromParent();
		}
	}

	public void refreshColumnPanel() {
		pColumns.clear();
		for (FmdtData col : columns) {
			ColumnDraggable colDrag = new ColumnDraggable(col, AnalysisDesignerPanel.this);
			if (col.getJavaType().equals("java.lang.String")) {
				colDrag.getLblcolumn().addStyleName(style.green());
			} else if (col.getJavaType().equals("java.sql.Date") || col.getJavaType().equals("java.sql.Time") || col.getJavaType().equals("java.sql.Timestamp")) {
				colDrag.getLblcolumn().addStyleName(style.red());
			} else if (col.getJavaType().equals("java.lang.Integer") || col.getJavaType().equals("java.lang.Long") || col.getJavaType().equals("java.lang.Float") || col.getJavaType().equals("java.lang.Double") || col.getJavaType().equals("java.math.BigDecimal"))
				colDrag.getLblcolumn().addStyleName(style.blue());
			pColumns.add(colDrag);
		}
	}

	public Composite getSelected() {
		return selected;
	}

	public void setSelected(Composite selected) {
		this.selected = selected;
	}

	@UiHandler("btnLaunch")
	public void onlaunchClick(ClickEvent event) {
		getVariables();
		String function = ((MethodsType) algos.getValue()).getKey();

		checkIfRemove(variablesX);
		if (variablesX.isEmpty()) {
			MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Warning(), LabelsConstants.lblCnst.AnalysisVariablesWarning());
			return;
		}

		if (function.equals("classKmean")) {
			checkIfRemove(variablesY);
			if (variablesY.isEmpty()) {
				MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Warning(), LabelsConstants.lblCnst.AnalysisVariablesWarning());
				return;
			}
		}
		if (function.equals("classKmean")) {
			try {
				Integer.parseInt(nbClass.getText());
			} catch (Exception e) {
				MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Warning(), LabelsConstants.lblCnst.ClassNotInteger());
				return;
			}
		}
		if (function.equals("decisionTree")) {
			Boolean perc = false;
			try {
				Double nbTrain = Double.parseDouble(percTrain.getText());
				if (nbTrain < 1 && nbTrain > 0)
					perc = true;
			} catch (Exception e) {

			}
			if (!perc) {
				MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Warning(), LabelsConstants.lblCnst.TrainInvalidFormat());
				return;
			}
		}

		List<FmdtData> variables = new ArrayList<FmdtData>();
		variables.addAll(variablesX);
		variables.addAll(variablesY);
		refreshBuilder(variables);

		waitPanel.showWaitPart(true);
		FmdtServices.Connect.getInstance().getRequestValue(builder, driller, false, new AsyncCallback<List<FmdtTable>>() {
			@Override
			public void onFailure(Throwable caught) {
				waitPanel.showWaitPart(false);
				caught.printStackTrace();
				ExceptionManager.getInstance().handleException(caught, LabelsConstants.lblCnst.Error());
			}

			@Override
			public void onSuccess(List<FmdtTable> table) {
				List<List<Double>> listX = new ArrayList<List<Double>>();
				List<List<Double>> listY = new ArrayList<List<Double>>();

				List<String> names = table.get(0).getDatas().get(0).getValues();
				table.get(0).getDatas().remove(0);

				parseVars(table.get(0).getDatas(), listX, listY, variablesX.size());
				if (!((MethodsType) algos.getValue()).getKey().equals("decisionTree")) {
					parseVars(table.get(0).getDatas(), listX, listY, variablesX.size());
					generateR(listX, listY);
				} else {
					listX = parseList(table.get(0).getDatas(), variablesX.size());
					List<String> varY = getListValue(table.get(0).getDatas(), variablesX.size());
					generateDecisionTree(listX, varY, names);
				}
			}

		});
	}

	private void checkIfRemove(List<FmdtData> datas) {
		List<FmdtData> dataremoved = new ArrayList<FmdtData>();
		for (FmdtData col : datas) {
			if (!col.getJavaType().equals("java.lang.Integer") && !col.getJavaType().equals("java.lang.Long") && !col.getJavaType().equals("java.lang.Float") && !col.getJavaType().equals("java.lang.Double") && !col.getJavaType().equals("java.math.BigDecimal")) {
				dataremoved.add(col);
			}
		}
		for (FmdtData col : dataremoved) {
			datas.remove(col);
		}
	}

	private void refreshBuilder(List<FmdtData> var) {
		List<FmdtColumn> cols = new ArrayList<FmdtColumn>();
		List<FmdtFormula> formulas = new ArrayList<FmdtFormula>();
		List<FmdtAggregate> aggregates = new ArrayList<FmdtAggregate>();

		List<FmdtData> ordonable = new ArrayList<FmdtData>(var);

		for (FmdtData col : var) {
			if (col instanceof FmdtColumn)
				cols.add((FmdtColumn) col);
			else if (col instanceof FmdtFormula)
				formulas.add((FmdtFormula) col);
			else if (col instanceof FmdtAggregate)
				aggregates.add((FmdtAggregate) col);
		}
		builder.setColumns(cols);
		builder.setFormulas(formulas);
		builder.setAggregates(aggregates);
		builder.setListColumns(ordonable);
	}

	private void generateR(List<List<Double>> listX, List<List<Double>> listY) {
		int nbCluster = 0;
		final String function = ((MethodsType) algos.getValue()).getKey();

		if (function.equals("classKmean"))
			nbCluster = Integer.parseInt(nbClass.getText());

		FmdtServices.Connect.getInstance().launchRFunctions(listX, listY, function, nbCluster, new AsyncCallback<Double[][]>() {
			@Override
			public void onFailure(Throwable caught) {
				waitPanel.showWaitPart(false);
				caught.printStackTrace();
				ExceptionManager.getInstance().handleException(caught, LabelsConstants.lblCnst.Error());
			}

			@Override
			public void onSuccess(Double[][] result) {
				waitPanel.showWaitPart(false);
				if (function.equals("dependenceCorrelation"))
					initCorrelation(result);
				else if (function.equals("classKmean"))
					initChart(result);
			}
		});
	}

	private void generateDecisionTree(List<List<Double>> listX, List<String> listY, List<String> names) {
		Double nTrain = 0.0;
		nTrain = Double.parseDouble(percTrain.getText());
		FmdtServices.Connect.getInstance().launchDecisionTree(listX, listY, nTrain, names, new AsyncCallback<List<FmdtNode>>() {
			@Override
			public void onFailure(Throwable caught) {
				waitPanel.showWaitPart(false);
				caught.printStackTrace();
				ExceptionManager.getInstance().handleException(caught, LabelsConstants.lblCnst.Error());
			}

			@Override
			public void onSuccess(List<FmdtNode> result) {
				// result.get(0);
				waitPanel.showWaitPart(false);
				createDecisionTree(result);

			}
		});
	}

	private void initChart(Double[][] result) {
		ChartJsPanel previewChart = new ChartJsPanel(createChartObject(result), ChartObject.TYPE_SCATTER_BUBBLE, ChartObject.RENDERER_SCATTER);
//		FusionChartsPanel previewChart = new FusionChartsPanel(createChartObject(result), ChartObject.TYPE_SCATTER_BUBBLE, ChartObject.RENDERER_SCATTER, false);
		chart.clear();
		chart.add(previewChart);
//		previewChart.renderCharts();
	}

	private void initCorrelation(Double[][] result) {
		List<String> listName = new ArrayList<String>();
		for (FmdtData var : variablesX) {
			listName.add(var.getLabel());
		}
		List<List<String>> valuesResult = parseDoubleToString(result, listName);

		DataGrid<List<String>> displayValue = createGrid(valuesResult);
		ListDataProvider<List<String>> dataprovider = new ListDataProvider<List<String>>();
		dataprovider.addDataDisplay(displayValue);

		chart.clear();
		chart.add(displayValue);

		dataprovider.setList(valuesResult);
	}

	private ChartObject createChartObject(Double[][] result) {
		ChartObject chart = new ChartObject();
		List<ChartValue> multiseries = new ArrayList<ChartValue>();
		for (int i = 0; i < result.length; i++) {
			Boolean added = false;
			for (ChartValue value : multiseries) {
				if (value.getCategory().equals(String.valueOf(result[i][2]))) {
					ValuePointSerie point = new ValuePointSerie();
					point.setCategory(String.valueOf(result[i][2]));
					point.setX(result[i][0]);
					point.setY(result[i][1]);
					((ValueMultiSerie) value).addValue(point);
					added = true;
					break;
				}
			}
			if (!added) {
				ValueMultiSerie m = new ValueMultiSerie();
				m.setCategory(String.valueOf(result[i][2]));
				m.setSerieName(String.valueOf(result[i][2]));
				ValuePointSerie point = new ValuePointSerie();
				point.setCategory(String.valueOf(result[i][2]));
				point.setX(result[i][0]);
				point.setY(result[i][1]);
				m.addValue(point);
				multiseries.add(m);
			}
		}

		chart.setValues(multiseries);

		chart.setTitle(((MethodsType) algos.getValue()).getValue());

		chart.setColors(colors);

		chart.setxAxisName(variablesX.get(0).getLabel());
		chart.setyAxisName(variablesY.get(0).getLabel());

		return chart;
	}

	private void createDecisionTree(List<FmdtNode> nodes) {
		listNodes = nodes;
		chart.clear();
		JSONObject obj = convertToJSON(nodes);
		container = DIV_CONTAINER + new Object().hashCode();
		HTMLPanel contentPanel = new HTMLPanel("");
		contentPanel.addStyleName(style.container());
		chart.add(contentPanel);
		contentPanel.getElement().setId(container);

		loadDecisionTree(container, obj.toString(), String.valueOf(chart.getOffsetHeight()), String.valueOf(chart.getOffsetWidth()));
	}

	private JSONObject convertToJSON(List<FmdtNode> nodes) {

		JSONObject obj = new JSONObject();
		obj = createJSONNode(nodes.get(0));
		return obj;
	}

	private JSONObject createJSONNode(FmdtNode node) {
		JSONObject jsonNode = new JSONObject();
		jsonNode.put("name", new JSONString(node.getId()));
		jsonNode.put("id", new JSONString(node.getId()));
		jsonNode.put("defaultchild", new JSONString(node.getDefaultChildId() != null ? node.getDefaultChildId() : ""));
		jsonNode.put("field", new JSONString(node.getField()));
		jsonNode.put("operator", new JSONString(checkOperator(node.getOperator())));
		jsonNode.put("value", new JSONString(node.getValue()));
		int count = 0;
		for (int i = 0; i < node.getContent().size() && i < 3; i++) {
			count += Integer.parseInt(node.getContent().get(i).getCount());
		}
		jsonNode.put("score", new JSONString(String.valueOf(count)));

		int i = 0;
		JSONArray array = new JSONArray();
		for (FmdtNode child : node.getChilds()) {
			array.set(i, createJSONNode(child));
			i++;
		}
		if (array.size() > 0)
			jsonNode.put("children", array);

		return jsonNode;
	}

	private String checkOperator(String operator) {
		if (operator != null && !operator.isEmpty()) {
			if (operator.equals("greaterOrEqual"))
				return ">=";
			else if (operator.equals("lessOrEqual")) {
				return "<=";
			} else if (operator.equals("equal")) {
				return "=";
			} else if (operator.equals("lessThan")) {
				return "<";
			} else
				return ">";
		}
		return "";
	}

	private DataGrid<List<String>> createGrid(List<List<String>> values) {
		DataGrid.Resources resources = ressource;

		DataGrid<List<String>> displayValue = new DataGrid<List<String>>(100, resources);
		displayValue.setSize("94%", "50%");

		for (int i = 0; i < values.get(0).size(); i++) {
			final int index = i;

			Column<List<String>, String> col = new Column<List<String>, String>(new TextCell()) {
				@Override
				public String getValue(List<String> object) {
					return object.get(index);
				}

				@Override
				public String getCellStyleNames(Context context, List<String> object) {
					if (object.get(index).isEmpty() || object.get(index).equals("")) {
						return style.cellEmpty();
					} else
						return style.cellNoEmpty();
				}
			};
			displayValue.addColumn(col);
			displayValue.setColumnWidth(col, "80px");
		}

		displayValue.addStyleName(style.datagrid());

		return displayValue;
	}

	private List<List<Double>> parseList(List<FmdtRow> values, int nb) {
		List<List<Double>> parseValues = new ArrayList<List<Double>>();
		for (FmdtRow val : values) {
			List<Double> parseVal = new ArrayList<Double>();
			try {
				for (int i = 0; i < nb; i++) {
					parseVal.add(Double.parseDouble(val.getValues().get(i)));
				}
				parseValues.add(parseVal);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return parseValues;
	}

	private List<String> getListValue(List<FmdtRow> values, int index) {
		List<String> parseValues = new ArrayList<String>();
		for (FmdtRow val : values) {
			parseValues.add(val.getValues().get(index));
		}
		return parseValues;
	}

	private void parseVars(List<FmdtRow> values, List<List<Double>> listX, List<List<Double>> listY, int nbx) {
		for (FmdtRow val : values) {
			List<Double> parseValx = new ArrayList<Double>();
			List<Double> parseValy = new ArrayList<Double>();
			int nb = 0;
			try {
				for (String value : val.getValues()) {
					if (nb < nbx)
						parseValx.add(Double.parseDouble(value));
					else
						parseValy.add(Double.parseDouble(value));
					nb++;
				}
				listX.add(parseValx);
				listY.add(parseValy);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private List<List<String>> parseDoubleToString(Double[][] values, List<String> listname) {
		List<List<String>> valuesParse = new ArrayList<List<String>>();
		for (int i = 0; i < values.length; i++) {
			try {
				List<String> valueparse = new ArrayList<String>();
				valueparse.add(listname.get(i));
				for (int j = 0; j < values[i].length; j++) {
					if (values[i][j] != 0.0)
						valueparse.add(String.valueOf(values[i][j]));
					else
						valueparse.add("");
				}
				valuesParse.add(valueparse);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		List<String> value = new ArrayList<String>();
		value.add("");
		for (String name : listname)
			value.add(name);

		valuesParse.add(value);

		return valuesParse;
	}

	private void getVariables() {
		variablesX = new ArrayList<FmdtData>();
		variablesY = new ArrayList<FmdtData>();

		if (!((MethodsType) algos.getValue()).getKey().equals("classKmean")) {
			for (int i = 0; i < txtCorreX.getWidgetCount(); i++) {
				if (txtCorreX.getWidget(i) instanceof ColumnDraggable) {
					variablesX.add(((ColumnDraggable) txtCorreX.getWidget(i)).getColumn());
				}
			}
		} else {
			for (int i = 0; i < txtVariablex.getWidgetCount(); i++) {
				if (txtVariablex.getWidget(i) instanceof ColumnDraggable) {
					variablesX.add(((ColumnDraggable) txtVariablex.getWidget(i)).getColumn());
					break;
				}
			}
		}
		if (!((MethodsType) algos.getValue()).getKey().equals("dependenceCorrelation")) {
			for (int i = 0; i < txtVariabley.getWidgetCount(); i++) {
				if (txtVariabley.getWidget(i) instanceof ColumnDraggable) {
					variablesY.add(((ColumnDraggable) txtVariabley.getWidget(i)).getColumn());
					break;
				}
			}
		}
	}

	private DropHandler dropHandlerColumn = new DropHandler() {

		@Override
		public void onDrop(DropEvent event) {
			event.stopPropagation();
			event.preventDefault();

			String data = event.getData(ColumnDraggable.VARIABLE_ID);
			FmdtData column = getData(data);
			if (selected != null)
				selected.removeFromParent();

			pColumns.add(createDragCol(column));

		}
	};
	private DropHandler dropHandlertxtX = new DropHandler() {

		@Override
		public void onDrop(DropEvent event) {
			event.stopPropagation();
			event.preventDefault();

			String data = event.getData(ColumnDraggable.VARIABLE_ID);
			FmdtData column = getData(data);
			if (selected != null)
				selected.removeFromParent();

			for (int i = 0; i < txtVariablex.getWidgetCount(); i++) {
				if (txtVariablex.getWidget(i) instanceof ColumnDraggable) {
					pColumns.add(createDragCol(((ColumnDraggable) txtVariablex.getWidget(i)).getColumn()));
					txtVariablex.getWidget(i).removeFromParent();
					break;
				}
			}
			txtVariablex.add(createDragCol(column));

		}
	};

	private DropHandler dropHandlertxtY = new DropHandler() {

		@Override
		public void onDrop(DropEvent event) {
			event.stopPropagation();
			event.preventDefault();

			String data = event.getData(ColumnDraggable.VARIABLE_ID);
			FmdtData column = getData(data);
			if (selected != null)
				selected.removeFromParent();

			for (int i = 0; i < txtVariabley.getWidgetCount(); i++) {
				if (txtVariabley.getWidget(i) instanceof ColumnDraggable) {
					pColumns.add(createDragCol(((ColumnDraggable) txtVariabley.getWidget(i)).getColumn()));
					txtVariabley.getWidget(i).removeFromParent();
					break;
				}
			}
			txtVariabley.add(createDragCol(column));

		}
	};

	private DropHandler dropHandlerAreaX = new DropHandler() {

		@Override
		public void onDrop(DropEvent event) {
			event.stopPropagation();
			event.preventDefault();

			String data = event.getData(ColumnDraggable.VARIABLE_ID);
			FmdtData column = getData(data);
			if (selected != null)
				selected.removeFromParent();

			txtCorreX.add(createDragCol(column));
		}
	};

	public ColumnDraggable createDragCol(FmdtData col) {
		ColumnDraggable colDrag = new ColumnDraggable(col, AnalysisDesignerPanel.this);
		if (col.getJavaType().equals("java.lang.String")) {
			colDrag.getLblcolumn().addStyleName(style.green());
		} else if (col.getJavaType().equals("java.sql.Date") || col.getJavaType().equals("java.sql.Time") || col.getJavaType().equals("java.sql.Timestamp")) {
			colDrag.getLblcolumn().addStyleName(style.red());
		} else if (col.getJavaType().equals("java.lang.Integer") || col.getJavaType().equals("java.lang.Long") || col.getJavaType().equals("java.lang.Float") || col.getJavaType().equals("java.lang.Double") || col.getJavaType().equals("java.math.BigDecimal"))
			colDrag.getLblcolumn().addStyleName(style.blue());

		return colDrag;
	}

	private DragOverHandler dragOverHandler = new DragOverHandler() {

		@Override
		public void onDragOver(DragOverEvent event) {
		}
	};

	private DragLeaveHandler dragLeaveHandler = new DragLeaveHandler() {

		@Override
		public void onDragLeave(DragLeaveEvent event) {
		}
	};

	public FmdtData getData(String data) {
		FmdtData column = null;
		if (data != null && !data.isEmpty()) {
			Integer id = Integer.parseInt(data);
			for (FmdtData col : allcolumns) {
				if (col.getId() == id) {
					column = col;
					break;
				}
			}
		}
		return column;
	}

	static void handleMirrorOver(String id, String x, String y) {
		// String id1 =id;
		Double posX = Double.parseDouble(x);
		Double posY = Double.parseDouble(y);

		FmdtNode node = getNode(listNodes.get(0), id);
		ChartJsPanel pieChart = new ChartJsPanel(createChartScore(node.getContent()), ChartObject.TYPE_SIMPLE, ChartObject.RENDERER_PIE);
//		FusionChartsPanel pieChart = new FusionChartsPanel(createChartScore(node.getContent()), ChartObject.TYPE_SIMPLE, ChartObject.RENDERER_PIE, false);
		pieChart.addStyleName(style.chartfd());
		displayValues = new HTMLPanel("");

		Style style = displayValues.getElement().getStyle();

		style.setPosition(Position.ABSOLUTE);

		style.setHeight(200, Unit.PX);
		style.setWidth(300, Unit.PX);
		style.setTop(posY, Unit.PX);
//		int posleft = chart.getAbsoluteLeft();
		style.setLeft((posX + chart.getAbsoluteLeft() - 700), Unit.PX);

		displayValues.add(pieChart);
		chart.add(displayValues);
	}

	static void handleMirrorOut() {
		displayValues.removeFromParent();
	}

	private static ChartObject createChartScore(List<Score> scores) {

		ChartObject chart = new ChartObject();

		ValueMultiSerie m1 = new ValueMultiSerie();
		m1.setSerieName("");

		List<ChartValue> values = new ArrayList<ChartValue>();

		for (Score score : scores) {
			if (Integer.parseInt(score.getCount()) > 0) {
				ValueSimpleSerie s1 = new ValueSimpleSerie();
				s1.setCategory(score.getValue());
				s1.setValue(new Integer(Integer.parseInt(score.getCount())).doubleValue());
				values.add(s1);
			}
		}
		chart.setValues(values);
		chart.setTitle(LabelsConstants.lblCnst.Distribution());
		chart.setColors(colors);

		return chart;
	}

	private static FmdtNode getNode(FmdtNode node, String id) {
		if (node.getId().equals(id))
			return node;
		else {
			for (FmdtNode child : node.getChilds()) {
				FmdtNode result = getNode(child, id);
				if (result != null)
					return result;
			}
		}
		return null;
	}

	private class MethodsType {
		private String value;
		private String key;

		public MethodsType(String value, String key) {
			this.value = value;
			this.key = key;
		}

		public String getValue() {
			return value;
		}

//		public void setValue(String value) {
//			this.value = value;
//		}

		public String getKey() {
			return key;
		}

//		public void setKey(String key) {
//			this.key = key;
//		}
	}

	private final native void loadDecisionTree(String container, String obj, String height, String width) /*-{
		$wnd.decisionTree(container, obj, height, width);
		$wnd.mouseOverMirror = function(id, x, y) {
			$wnd.handleMirrorOver = @bpm.gwt.commons.client.viewer.fmdtdriller.AnalysisDesignerPanel::handleMirrorOver(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)(id, x, y);
		};
		$wnd.mouseOutMirror = function() {
			$wnd.handleMirrorOut = @bpm.gwt.commons.client.viewer.fmdtdriller.AnalysisDesignerPanel::handleMirrorOut()();
		};
	}-*/;

}
