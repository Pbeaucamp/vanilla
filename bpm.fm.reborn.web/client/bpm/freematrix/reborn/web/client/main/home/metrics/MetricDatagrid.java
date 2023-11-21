package bpm.freematrix.reborn.web.client.main.home.metrics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import bpm.fm.api.model.FactTable;
import bpm.fm.api.model.Metric;
import bpm.fm.api.model.utils.MetricValue;
import bpm.freematrix.reborn.web.client.dialog.CalendarDialog;
import bpm.freematrix.reborn.web.client.i18n.LabelConstants;
import bpm.freematrix.reborn.web.client.images.Images;
import bpm.freematrix.reborn.web.client.main.header.FreeMatrixHeader;
import bpm.freematrix.reborn.web.client.main.home.HomeView;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.custom.HasActionCell;
import bpm.gwt.commons.client.custom.v2.CompositeCellHelper;
import bpm.gwt.commons.client.utils.CustomResources;
import bpm.gwt.commons.client.utils.MessageHelper;

import com.google.gwt.cell.client.ActionCell.Delegate;
import com.google.gwt.cell.client.ImageResourceCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.i18n.shared.DateTimeFormat.PredefinedFormat;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.RowStyles;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

public class MetricDatagrid extends DataGrid<MetricValue> {
	
	private static DateTimeFormat df = DateTimeFormat.getFormat(PredefinedFormat.DATE_TIME_MEDIUM);

	private static DataGrid.Resources resources = new CustomResources();
	
	private SingleSelectionModel<MetricValue> selectionModel;
	private ListDataProvider<MetricValue> dataProvider;
	
	private ListHandler<MetricValue> tendancyHandler;
	private ListHandler<MetricValue> healthHandler;
	private ListHandler<MetricValue> metricHandler;
	private ListHandler<MetricValue> dateHandler;
	private ListHandler<MetricValue> periodicityHandler;
	private ListHandler<MetricValue> valueHandler;
	private ListHandler<MetricValue> objectiveHandler;
	private ListHandler<MetricValue> minHandler;
	private ListHandler<MetricValue> maxHandler;
	

	private HomeView homeView;
	
	private Date selectedDate;
	
	public MetricDatagrid(HomeView home) {
		super(99999, resources);
		
		this.homeView = home;
		
		this.setHeight("100%");
		this.setWidth("100%");
		
		TextCell cell = new TextCell();
		Column<MetricValue, ImageResource> colTendancy = new Column<MetricValue, ImageResource>(new ImageResourceCell()) {
			@Override
			public ImageResource getValue(MetricValue object) {
				if(object.getTendancy() < 0) {
					return Images.INSTANCE.tend_down();
				}
				else if(object.getTendancy() == 0) {
					return Images.INSTANCE.tend_equal();
				}
				else {
					return Images.INSTANCE.tend_up();
				}
			}
		};
		
		Column<MetricValue, ImageResource> colHealth = new Column<MetricValue, ImageResource>(new ImageResourceCell()) {
			@Override
			public ImageResource getValue(MetricValue object) {
				if(object.getHealth() < 0) {
					return Images.INSTANCE.thumb_down();
				}
				else if(object.getHealth() == 0) {
					return Images.INSTANCE.thumb_equal();
				}
				else {
					return Images.INSTANCE.thumb_up();
				}
			}
		};
		

		HasActionCell<MetricValue> calendarCell = new HasActionCell<MetricValue>(Images.INSTANCE.calendar_20(), LabelConstants.lblCnst.ShowCalendar(), new Delegate<MetricValue>() {

			@Override
			public void execute(MetricValue object) {
				showCalendar(object);
			}
		});

		CompositeCellHelper<MetricValue> compCell = new CompositeCellHelper<MetricValue>(calendarCell);
		Column<MetricValue, MetricValue> colCalendar = new Column<MetricValue, MetricValue>(compCell.getCell()) {
			@Override
			public MetricValue getValue(MetricValue object) {
				return object;
			}
		};
		
		Column<MetricValue, String> colPeriodicity = new Column<MetricValue, String>(cell) {
			@Override
			public String getValue(MetricValue object) {
				if (object.getMetric().getFactTable() != null && object.getMetric().getFactTable() instanceof FactTable) {
					return getPeriodicity((FactTable) object.getMetric().getFactTable());
				}
				return LabelConstants.lblCnst.Unknown();
			}
		};
		
		Column<MetricValue, String> colName = new Column<MetricValue, String>(cell) {
			@Override
			public String getValue(MetricValue object) {
				return object.getMetric().getName();
			}
		};
		
		Column<MetricValue, String> colDate = new Column<MetricValue, String>(cell) {
			@Override
			public String getValue(MetricValue object) {
				return object.getDate() != null ? df.format(object.getDate()) : LabelConstants.lblCnst.Unknown();
			}
		};
		
		Column<MetricValue, String> colMaximum = new Column<MetricValue, String>(cell) {
			@Override
			public String getValue(MetricValue object) {
				if(object.isDummy()) {
					return "No value";
				}
				return object.getMaximum() + "";
			}
		};
		
		
		Column<MetricValue, String> colMinimum = new Column<MetricValue, String>(cell) {
			@Override
			public String getValue(MetricValue object) {
				if(object.isDummy()) {
					return "No value";
				}
				return object.getMinimum() + "";
			}
		};
		
		Column<MetricValue, String> colObjective = new Column<MetricValue, String>(cell) {
			@Override
			public String getValue(MetricValue object) {
				if(object.isDummy()) {
					return "No value";
				}
				return object.getObjective() + "";
			}
		};
		
		Column<MetricValue, String> colValue = new Column<MetricValue, String>(cell) {
			@Override
			public String getValue(MetricValue object) {
				if(object.isDummy()) {
					return "No value";
				}
				return object.getValue() + "";
			}
		};
		
		colHealth.setSortable(true);
		colDate.setSortable(true);
		colPeriodicity.setSortable(true);
		colMaximum.setSortable(true);
		colMinimum.setSortable(true);
		colName.setSortable(true);
		colObjective.setSortable(true);
		colTendancy.setSortable(true);
		colValue.setSortable(true);
		
		dataProvider = new ListDataProvider<MetricValue>();
		dataProvider.addDataDisplay(this);
		
		this.addColumn(colTendancy, LabelConstants.lblCnst.Tendancy());
		this.addColumn(colHealth, LabelConstants.lblCnst.Health());
		this.addColumn(colCalendar, LabelConstants.lblCnst.Calendar());
		this.addColumn(colPeriodicity, LabelConstants.lblCnst.periodicity());
		this.addColumn(colName, LabelConstants.lblCnst.Metric());
		this.addColumn(colValue, LabelConstants.lblCnst.Value());
		this.addColumn(colDate, LabelConstants.lblCnst.Date());
		this.addColumn(colObjective, LabelConstants.lblCnst.Objective());
		this.addColumn(colMinimum, LabelConstants.lblCnst.Minimum());
		this.addColumn(colMaximum, LabelConstants.lblCnst.Maximum());
		
		
		tendancyHandler = new ListHandler<MetricValue>(dataProvider.getList());
		tendancyHandler.setComparator(colTendancy, new Comparator<MetricValue>() {	
			@Override
			public int compare(MetricValue o1, MetricValue o2) {
				return o1.getTendancy() - o2.getTendancy();
			}
		});
		
		healthHandler = new ListHandler<MetricValue>(dataProvider.getList());
		healthHandler.setComparator(colHealth, new Comparator<MetricValue>() {	
			@Override
			public int compare(MetricValue o1, MetricValue o2) {
				return o1.getHealth() - o2.getHealth();
			}
		});
		
		metricHandler = new ListHandler<MetricValue>(dataProvider.getList());
		metricHandler.setComparator(colName, new Comparator<MetricValue>() {	
			@Override
			public int compare(MetricValue o1, MetricValue o2) {
				return o1.getMetric().getName().compareTo(o2.getMetric().getName());
			}
		});
		
		dateHandler = new ListHandler<MetricValue>(dataProvider.getList());
		dateHandler.setComparator(colDate, new Comparator<MetricValue>() {	
			@Override
			public int compare(MetricValue o1, MetricValue o2) {
				return o1.getDate().compareTo(o2.getDate());
			}
		});
		
		periodicityHandler = new ListHandler<MetricValue>(dataProvider.getList());
		periodicityHandler.setComparator(colPeriodicity, new Comparator<MetricValue>() {	
			@Override
			public int compare(MetricValue o1, MetricValue o2) {
				return ((FactTable) o1.getMetric().getFactTable()).getPeriodicity().compareTo(((FactTable) o2.getMetric().getFactTable()).getPeriodicity());
			}
		});
		
		valueHandler = new ListHandler<MetricValue>(dataProvider.getList());
		valueHandler.setComparator(colValue, new Comparator<MetricValue>() {	
			@Override
			public int compare(MetricValue o1, MetricValue o2) {
				return (int) (o1.getValue() - o2.getValue());
			}
		});
		
		objectiveHandler = new ListHandler<MetricValue>(dataProvider.getList());
		objectiveHandler.setComparator(colObjective, new Comparator<MetricValue>() {	
			@Override
			public int compare(MetricValue o1, MetricValue o2) {
				return (int) (o1.getObjective() - o2.getObjective());
			}
		});
		
		minHandler = new ListHandler<MetricValue>(dataProvider.getList());
		minHandler.setComparator(colMinimum, new Comparator<MetricValue>() {	
			@Override
			public int compare(MetricValue o1, MetricValue o2) {
				return (int) (o1.getMinimum() - o2.getMinimum());
			}
		});
		
		maxHandler = new ListHandler<MetricValue>(dataProvider.getList());
		maxHandler.setComparator(colMaximum, new Comparator<MetricValue>() {	
			@Override
			public int compare(MetricValue o1, MetricValue o2) {
				return (int) (o1.getMaximum() - o2.getMaximum());
			}
		});
		
		this.addColumnSortHandler(healthHandler);
		this.addColumnSortHandler(tendancyHandler);
		this.addColumnSortHandler(metricHandler);
		this.addColumnSortHandler(dateHandler);
		this.addColumnSortHandler(periodicityHandler);
		this.addColumnSortHandler(valueHandler);
		this.addColumnSortHandler(objectiveHandler);
		this.addColumnSortHandler(minHandler);
		this.addColumnSortHandler(maxHandler);

		this.setColumnWidth(colCalendar, "70px");
		this.setColumnWidth(colTendancy, "80px");
		this.setColumnWidth(colHealth, "80px");
		this.setColumnWidth(colName, "250px");
		
		selectionModel = new SingleSelectionModel<MetricValue>();
		this.setSelectionModel(selectionModel);
		
		selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {		
			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
				if(!selectionModel.getSelectedObject().isDummy()) {
					homeView.setMetric(selectionModel.getSelectedObject().getMetric());
					homeView.generateInformationPanel(false,selectionModel.getSelectedObject().getMetric(),selectionModel.getSelectedObject());
				}
			}
		});
		
		this.getElement().getStyle().setOverflow(Overflow.AUTO);
		
		this.setEmptyTableWidget(new Label());
		
		this.setRowStyles(new RowStyles<MetricValue>() {		
			@Override
			public String getStyleNames(MetricValue row, int rowIndex) {
				if(row.isDummy()) {
					return "noValueRow";
				}
				return "";
			}
		});
	}

	private void showCalendar(MetricValue object) {
		int groupId = FreeMatrixHeader.getInstance().getSelectedGroup();
		if (!(object.getMetric().getFactTable() instanceof FactTable) 
				|| ((FactTable) object.getMetric().getFactTable()).getPeriodicity().equals(FactTable.PERIODICITY_HOURLY) 
				|| ((FactTable) object.getMetric().getFactTable()).getPeriodicity().equals(FactTable.PERIODICITY_MINUTE)) {
			MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Information(), LabelConstants.lblCnst.TheCalendarViewIsNotAvailableForThisPeriodicity());
			return;
		}
		
		CalendarDialog dial = new CalendarDialog(object, selectedDate, groupId);
		dial.center();
	}

	private String getPeriodicity(FactTable factTable) {
		String periodicity = factTable.getPeriodicity();
		if (periodicity.equals(FactTable.PERIODICITY_YEARLY)) {
			return LabelConstants.lblCnst.Yearly();
		}
		else if (periodicity.equals(FactTable.PERIODICITY_BIANNUAL)) {
			return LabelConstants.lblCnst.Biannual();
		}
		else if (periodicity.equals(FactTable.PERIODICITY_QUARTERLY)) {
			return LabelConstants.lblCnst.Quaterly();
		}
		else if (periodicity.equals(FactTable.PERIODICITY_MONTHLY)) {
			return LabelConstants.lblCnst.Monthly();
		}
		else if (periodicity.equals(FactTable.PERIODICITY_WEEKLY)) {
			return LabelConstants.lblCnst.Weekly();
		}
		else if (periodicity.equals(FactTable.PERIODICITY_DAILY)) {
			return LabelConstants.lblCnst.Daily();
		}
		else if (periodicity.equals(FactTable.PERIODICITY_HOURLY)) {
			return LabelConstants.lblCnst.Hourly();
		}
		else if (periodicity.equals(FactTable.PERIODICITY_MINUTE)) {
			return LabelConstants.lblCnst.Minute();
		}
		return null;
	}


	public void fill(HashMap<Metric, MetricValue> result, Date date) {
		this.selectedDate = date;
		
		List<Metric> metrics = new ArrayList<Metric>(result.keySet());
		Collections.sort(metrics, new Comparator<Metric>() {
			@Override
			public int compare(Metric o1, Metric o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		
		List<MetricValue> list = new ArrayList<MetricValue>();
		for(Metric m : metrics){
			list.add(result.get(m));
		}
		dataProvider.setList(list);
		tendancyHandler.setList(dataProvider.getList());
		healthHandler.setList(dataProvider.getList());
		valueHandler.setList(dataProvider.getList());
		metricHandler.setList(dataProvider.getList());
		objectiveHandler.setList(dataProvider.getList());
		minHandler.setList(dataProvider.getList());
		maxHandler.setList(dataProvider.getList());
	}

	public ListDataProvider<MetricValue> getDataProvider() {
		return dataProvider;
	}
	
}
