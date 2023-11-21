package bpm.fm.designer.web.client.panel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import bpm.fm.api.model.Metric;
import bpm.fm.designer.web.client.ClientSession;
import bpm.fm.designer.web.client.Messages;
import bpm.fm.designer.web.client.services.MetricService;
import bpm.gwt.commons.client.InformationsDialog;
import bpm.gwt.commons.client.utils.CustomResources;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SingleSelectionModel;

/**
 * A panel used to display metrics as a table
 * @author Marc Lanquetin
 *
 */
public class MetricPanel extends Composite {

	private static MetricPanelUiBinder uiBinder = GWT.create(MetricPanelUiBinder.class);
	private static DateTimeFormat dateFormatter = DateTimeFormat.getFormat("dd/MM/yyyy");

	interface MetricPanelUiBinder extends UiBinder<Widget, MetricPanel> {
	}
	
	private DataGrid<Metric> dataGrid;
	private ListDataProvider<Metric> dataProvider;

	@UiField
	HTMLPanel mainPanel;
	
	@UiField(provided=true)
	AxisMetricsToolbar toolbar;
	private SingleSelectionModel<Metric> selectionModel;
	
	private ListHandler<Metric> nameHandler;
	private ListHandler<Metric> dateHandler;

	public @UiConstructor MetricPanel() {
		toolbar = new AxisMetricsToolbar(AxisMetricsToolbar.METRIC_TOOLBAR, this);
		
		Widget w = uiBinder.createAndBindUi(this);
		initWidget(w);
		
		createGrid();
		mainPanel.add(dataGrid);
		
		dataProvider.setList(ClientSession.getInstance().getMetrics());
	    nameHandler.setList(dataProvider.getList());
	    dateHandler.setList(dataProvider.getList());
		
	}

	/**
	 * Called the first time to create the dataGrid with columns
	 * After it's created, just call refresh
	 */
	private void createGrid() {
		DataGrid.Resources resources = new CustomResources();
		
		dataGrid = new DataGrid<Metric>(99999, resources);
		dataGrid.setWidth("100%");
		dataGrid.setHeight("92%");
		
		TextCell cell = new TextCell();
		
		Column<Metric, String> colName = new Column<Metric, String>(cell) {
			@Override
			public String getValue(Metric object) {
				return object.getName();
			}
		};
		Column<Metric, String> colCreationDate = new Column<Metric, String>(cell) {
			@Override
			public String getValue(Metric object) {
				if(object.getCreationDate() != null) {
					return dateFormatter.format(object.getCreationDate());
				}
				return "";
			}
		};
		
		Column<Metric, String> colType = new Column<Metric, String>(cell) {
			@Override
			public String getValue(Metric object) {
				return object.getMetricType();
			}
		};
		
		colName.setSortable(true);
		colCreationDate.setSortable(true);
		
		dataGrid.addColumn(colName, Messages.lbl.name());
		dataGrid.addColumn(colCreationDate, Messages.lbl.CreationDate());
		dataGrid.addColumn(colType, "Type");
		dataGrid.setEmptyTableWidget(new Label(Messages.lbl.NoMetric()));
		
		dataProvider = new ListDataProvider<Metric>();
		dataProvider.addDataDisplay(dataGrid);
		
		nameHandler = new ListHandler<Metric>(dataProvider.getList());
		nameHandler.setComparator(colName, new Comparator<Metric>() {			
			@Override
			public int compare(Metric o1, Metric o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		
		dateHandler = new ListHandler<Metric>(dataProvider.getList());
		dateHandler.setComparator(colCreationDate, new Comparator<Metric>() {			
			@Override
			public int compare(Metric o1, Metric o2) {
				try {
					if(o1.getCreationDate().before(o2.getCreationDate())) {
						return -1;
					}
					else {
						return 1;
					}
				} catch (Exception e) {
					return 0;
				}
			}
		});
		
		dataGrid.addColumnSortHandler(nameHandler);
		dataGrid.addColumnSortHandler(dateHandler);
		
	    selectionModel = new SingleSelectionModel<Metric>();
	    dataGrid.setSelectionModel(selectionModel);
	    
//	    selectionModel.addSelectionChangeHandler(new Handler() {
//			@Override
//			public void onSelectionChange(SelectionChangeEvent event) {
//				Metric metrci = ((SingleSelectionModel<Metric>) event.getSource()).getSelectedObject();
//				MainPanel.instance.selectionChanged(metrci);
//			}
//		});
	    
	    dataGrid.addCellPreviewHandler(new CellPreviewEvent.Handler<Metric>() {
			@Override
			public void onCellPreview(CellPreviewEvent<Metric> event) {
				boolean isClick = BrowserEvents.CLICK.equals(event.getNativeEvent().getType());
				if(isClick) {
					Metric metrci = selectionModel.getSelectedObject();
					MainPanel.instance.selectionChanged(metrci);
				}
			}
		});
	    
	    SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
	    SimplePager pager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0, true);
	    pager.addStyleName("pageGrid");
	    pager.setDisplay(dataGrid);
	    
	    nameHandler.setList(dataProvider.getList());
	    dateHandler.setList(dataProvider.getList());
	}
	
	public Metric getSelectedMetric() {
		return selectionModel.getSelectedObject();
	}
	
	public void refresh() {
		int themeId = -1;
		try {
			themeId = MainPanel.getInstance().getSelectedTheme();
		} catch(Exception e) {
			
		}
		
		int obsId = -1;
		try {
			obsId = MainPanel.getInstance().getSelectedObservatory();
		} catch(Exception e) {
			
		}
		
		MetricService.Connection.getInstance().getMetrics(obsId, themeId, new AsyncCallback<List<Metric>>() {

			@Override
			public void onFailure(Throwable caught) {
				InformationsDialog dial = new InformationsDialog(Messages.lbl.Error(), Messages.lbl.Ok(), Messages.lbl.GetMetricError(), caught.getMessage(), caught);
				dial.center();
			}
			@Override
			public void onSuccess(List<Metric> result) {
				ClientSession.getInstance().setMetrics(result);
				dataProvider.setList(result);
			    nameHandler.setList(dataProvider.getList());
			    dateHandler.setList(dataProvider.getList());
			    
//			    MainPanel.getInstance().reloadObservatories();
			}
		});
	}

	public void refreshMetric(Metric metric) {
		List<Metric> metrics = new ArrayList<Metric>(dataProvider.getList());
		metrics.remove(metric);
		metrics.add(metric);
		dataProvider.setList(metrics);
	    nameHandler.setList(dataProvider.getList());
	    dateHandler.setList(dataProvider.getList());
		
	}
	
}
