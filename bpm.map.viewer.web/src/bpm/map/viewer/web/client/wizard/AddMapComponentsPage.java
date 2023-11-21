package bpm.map.viewer.web.client.wizard;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import bpm.fm.api.model.Axis;
import bpm.fm.api.model.Metric;
import bpm.fm.api.model.Observatory;
import bpm.fm.api.model.Theme;
import bpm.gwt.commons.client.wizard.IGwtPage;
import bpm.gwt.commons.client.wizard.IGwtWizard;
import bpm.map.viewer.web.client.UserSession;
import bpm.map.viewer.web.client.I18N.LabelsConstants;
import bpm.map.viewer.web.client.utils.ThemeTreeItem;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;

public class AddMapComponentsPage extends Composite implements IGwtPage {
	private static AddMapComponentsPageUiBinder uiBinder = GWT
			.create(AddMapComponentsPageUiBinder.class);

	interface AddMapComponentsPageUiBinder extends
			UiBinder<Widget, AddMapComponentsPage> {
	}

	interface MyStyle extends CssResource {

	}

	@UiField
	SimplePanel panelMetrics, panelAxis;

	@UiField
	Tree treeObs;
	
	@UiField
	TextBox txtName;
	
	@UiField
	Label lblName;

	@UiField
	MyStyle style;

	private IGwtWizard parent;
	private int index;

	private ListDataProvider<Metric> dataProviderMetric;
	private ListDataProvider<Axis> dataProviderAxis;
	private ListHandler<Metric> sortHandlerMetric;
	private ListHandler<Axis> sortHandlerAxis;
	private MultiSelectionModel<Metric> selectionModelMetric;
	private MultiSelectionModel<Axis> selectionModelAxis;

	private List<Metric> metrics = new ArrayList<Metric>();
	private List<Axis> axis = new ArrayList<Axis>();

	public static LabelsConstants lblCnst = (LabelsConstants) GWT
			.create(LabelsConstants.class);

	public AddMapComponentsPage(IGwtWizard parent, int index) {
		initWidget(uiBinder.createAndBindUi(this));
		this.parent = parent;
		this.index = index;

		fillTreeObs();

		panelMetrics.add(MetricGridData());
		panelAxis.add(AxisGridData());
		
		this.lblName.setText(lblCnst.Name());
	}

	@Override
	public boolean canGoBack() {
		return false;
	}

	@Override
	public boolean canGoFurther() {
		return isComplete() ? true : false;
	}

	@Override
	public int getIndex() {
		return index;
	}

	@Override
	public boolean isComplete() {
		return (metrics.isEmpty() || axis.isEmpty() || txtName.getText().equals("")) ? false : true;
	}

	private DataGrid<Metric> MetricGridData() {

		TextCell cell = new TextCell();
		Column<Metric, Boolean> checkboxColumn = new Column<Metric, Boolean>(
				new CheckboxCell(true, true)) {

			@Override
			public Boolean getValue(Metric object) {
				return selectionModelMetric.isSelected(object);
			}
		};

		Column<Metric, String> nameColumn = new Column<Metric, String>(cell) {

			@Override
			public String getValue(Metric object) {
				return object.getName();
			}
		};
		nameColumn.setSortable(true);
		
		checkboxColumn.setFieldUpdater(new FieldUpdater<Metric, Boolean>() {
			
			@Override
			public void update(int index, Metric object, Boolean value) {
				
				selectionModelMetric.setSelected(object, value);	
			}
		});

		// DataGrid.Resources resources = new CustomResources();
		DataGrid<Metric> dataGrid = new DataGrid<Metric>(12);
		dataGrid.setWidth("100%");
		dataGrid.setHeight("100%");

		// Attention au label
		dataGrid.addColumn(checkboxColumn, "");
		dataGrid.addColumn(nameColumn, lblCnst.MetricName());
		dataGrid.setColumnWidth(checkboxColumn, 15.0, Unit.PCT);
		dataGrid.setColumnWidth(nameColumn, 85.0, Unit.PCT);

		dataGrid.setEmptyTableWidget(new Label(lblCnst.NoResult()));

		dataProviderMetric = new ListDataProvider<Metric>();
		dataProviderMetric.addDataDisplay(dataGrid);

		sortHandlerMetric = new ListHandler<Metric>(new ArrayList<Metric>());
		sortHandlerMetric.setComparator(nameColumn, new Comparator<Metric>() {

			@Override
			public int compare(Metric m1, Metric m2) {
				return m1.getName().compareTo(m2.getName());
			}
		});

		dataGrid.addColumnSortHandler(sortHandlerMetric);

		// Add a selection model so we can select cells.
		selectionModelMetric = new MultiSelectionModel<Metric>();
		selectionModelMetric
				.addSelectionChangeHandler(selectionChangeHandler);
		dataGrid.setSelectionModel(selectionModelMetric);
		

		return dataGrid;
	}

	private DataGrid<Axis> AxisGridData() {

		TextCell cell = new TextCell();
		Column<Axis, Boolean> checkboxColumn = new Column<Axis, Boolean>(
				new CheckboxCell(true, true)) {

			@Override
			public Boolean getValue(Axis object) {
				return selectionModelAxis.isSelected(object);
			}
		};
		
		Column<Axis, String> nameColumn = new Column<Axis, String>(cell) {

			@Override
			public String getValue(Axis object) {
				return object.getName();
			}
		};
		nameColumn.setSortable(true);

		checkboxColumn.setFieldUpdater(new FieldUpdater<Axis, Boolean>() {
			
			@Override
			public void update(int index, Axis object, Boolean value) {
				
				selectionModelAxis.setSelected(object, value);	
			}
		});
		
		// DataGrid.Resources resources = new CustomResources();
		DataGrid<Axis> dataGrid = new DataGrid<Axis>(12);
		dataGrid.setWidth("100%");
		dataGrid.setHeight("100%");
		
		// Attention au label
		dataGrid.addColumn(checkboxColumn, "");
		dataGrid.addColumn(nameColumn, lblCnst.AxisName());
		dataGrid.setColumnWidth(checkboxColumn, 15.0, Unit.PCT);
		dataGrid.setColumnWidth(nameColumn, 85.0, Unit.PCT);

		dataGrid.setEmptyTableWidget(new Label(lblCnst.NoResult()));

		dataProviderAxis = new ListDataProvider<Axis>();
		dataProviderAxis.addDataDisplay(dataGrid);

		sortHandlerAxis = new ListHandler<Axis>(new ArrayList<Axis>());
		sortHandlerAxis.setComparator(nameColumn, new Comparator<Axis>() {

			@Override
			public int compare(Axis m1, Axis m2) {
				return m1.getName().compareTo(m2.getName());
			}
		});

		dataGrid.addColumnSortHandler(sortHandlerMetric);

		// Add a selection model so we can select cells.
		selectionModelAxis = new MultiSelectionModel<Axis>();
		selectionModelAxis
				.addSelectionChangeHandler(selectionChangeHandler);
		dataGrid.setSelectionModel(selectionModelAxis);

		// // Create a Pager to control the table.
		// SimplePager.Resources pagerResources =
		// GWT.create(SimplePager.Resources.class);
		// SimplePager pager = new SimplePager(TextLocation.CENTER,
		// pagerResources, false, 0, true);
		// //pager.addStyleName(style.pager());
		// pager.setDisplay(dataGrid);
		//
		// panelPager.setWidget(pager);

		return dataGrid;
	}

	private Handler selectionChangeHandler = new Handler() {

		@Override
		public void onSelectionChange(SelectionChangeEvent event) {
			metrics.clear();
			metrics.addAll(selectionModelMetric.getSelectedSet());
			axis.clear();
			axis.addAll(selectionModelAxis.getSelectedSet());
			parent.updateBtn();
		}
	};


	public void fillTreeObs() {
		List<Observatory> observatories = UserSession.getInstance()
				.getObservatories();
		treeObs.clear();
		for (Observatory obs : observatories) {
			TreeItem item = new TreeItem();
			item.setHTML(obs.getName());
			treeObs.addItem(item);
			for(Theme th : obs.getThemes()){
				ThemeTreeItem branch = new ThemeTreeItem(this, th, false);
				item.addItem(branch);
			}

		}
	}

	public void onTreeSelection(boolean checked, Theme theme){
		if(checked){
			Set<Axis> listAxis = new HashSet<Axis>(dataProviderAxis.getList());
			listAxis.addAll(theme.getAxis());
			dataProviderAxis.setList(new ArrayList<Axis>(listAxis));
			
			Set<Metric> listMetric = new HashSet<Metric>(dataProviderMetric.getList());
			listMetric.addAll(theme.getMetrics());
			dataProviderMetric.setList(new ArrayList<Metric>(listMetric));
		}else {
			dataProviderAxis.setList(new ArrayList<Axis>());
			dataProviderMetric.setList(new ArrayList<Metric>());
			for(int i = 0; i<treeObs.getItemCount(); i++){
				TreeItem obs = treeObs.getItem(i);
				for(int j=0; j<obs.getChildCount(); j++){
					
					ThemeTreeItem item = (ThemeTreeItem) obs.getChild(j);

					if(item.getCheckBoxState()){
						Set<Axis> listAxis = new HashSet<Axis>(dataProviderAxis.getList());
						listAxis.addAll(item.getTheme().getAxis());
						dataProviderAxis.setList(new ArrayList<Axis>(listAxis));
						
						Set<Metric> listMetric = new HashSet<Metric>(dataProviderMetric.getList());
						listMetric.addAll(item.getTheme().getMetrics());
						dataProviderMetric.setList(new ArrayList<Metric>(listMetric));
					}
				}
			}
		}
	}

	public List<Metric> getMetrics() {
		//return metrics;
		return new ArrayList<Metric>(selectionModelMetric.getSelectedSet());
	}

	public void setMetrics(List<Metric> metrics) {
		this.metrics = new ArrayList<Metric>(metrics);
	}

	public List<Axis> getAxis() {
		//return axis;
		return new ArrayList<Axis>(selectionModelAxis.getSelectedSet());
	}

	public void setAxis(List<Axis> axis) {
		this.axis = axis;
	}
	
	public String getName() {
		return txtName.getText();
	}

	public void setName(String name) {
		txtName.setText(name);
	}
	
	@UiHandler("txtName")
	public void onNameChange(ValueChangeEvent<String> event) {
		parent.updateBtn();
	}

	public void loadValues() {
		
		for(int i = 0; i<treeObs.getItemCount(); i++){
			TreeItem obs = treeObs.getItem(i);
			for(int j=0; j<obs.getChildCount(); j++){
				
				ThemeTreeItem item = (ThemeTreeItem) obs.getChild(j);

				for(Axis axe : axis){
					if(item.getTheme().getAxis().contains(axe)){
						item.setCheckBoxState(true);
						onTreeSelection(true, item.getTheme());
					}		
				}
				for(Metric met : metrics){
					if(item.getTheme().getMetrics().contains(met)){
						item.setCheckBoxState(true);
						onTreeSelection(true, item.getTheme());
					}		
				}
				
			}
		}
		
		for(Axis axe : axis){
			selectionModelAxis.setSelected(axe, true);
		}
		for(Metric met : metrics){
			selectionModelMetric.setSelected(met, true);
		}
	}
}
