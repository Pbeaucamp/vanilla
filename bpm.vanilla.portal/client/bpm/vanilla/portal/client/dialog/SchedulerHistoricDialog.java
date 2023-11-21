package bpm.vanilla.portal.client.dialog;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.vanilla.platform.core.beans.scheduler.Job;
import bpm.vanilla.platform.core.beans.scheduler.JobInstance;
import bpm.vanilla.platform.core.beans.scheduler.JobInstance.Result;
import bpm.vanilla.portal.client.images.PortalImage;
import bpm.vanilla.portal.client.utils.ToolsGWT;
import bpm.vanilla.portal.client.widget.custom.ButtonImageCell;
import bpm.vanilla.portal.client.widget.custom.CustomResources;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;

public class SchedulerHistoricDialog extends AbstractDialogBox {
	private static final String DATE_FORMAT = "HH:mm:ss - dd/MM/yyyy";

	private static SchedulerHistoricDialogUiBinder uiBinder = GWT.create(SchedulerHistoricDialogUiBinder.class);

	interface SchedulerHistoricDialogUiBinder extends UiBinder<Widget, SchedulerHistoricDialog> {
	}
	
	interface MyStyle extends CssResource {
		String pager();
		String imgGrid();
	}
	
	@UiField
	MyStyle style;
	
	@UiField
	HTMLPanel contentPanel;
	
	@UiField
	SimplePanel panelContent;
	
	private ListDataProvider<JobInstance> dataProvider;

	public SchedulerHistoricDialog(Job selectedJob, List<JobInstance> jobRuns) {
		super(ToolsGWT.lblCnst.Properties(), false, true);
		setWidget(uiBinder.createAndBindUi(this));
		createButton(ToolsGWT.lblCnst.Close(), closeHandler);
		
		panelContent.setWidget(createGridData());
		
		setResult(jobRuns);
	}

	private void setResult(List<JobInstance> jobRuns) {
		dataProvider.setList(jobRuns);
	}

	private HTMLPanel createGridData() {
		final DateTimeFormat dateFormatter = DateTimeFormat.getFormat(DATE_FORMAT);

		CustomCell cell = new CustomCell();
		Column<JobInstance, String> dateStartColumn = new Column<JobInstance, String>(cell) {

			@Override
			public String getValue(JobInstance object) {
				Date date = object.getStartDate();
				if (date == null || date.equals(new Date(0))) {
					return ToolsGWT.lblCnst.Never();
				}
				return dateFormatter.format(date);
			}
		};

		Column<JobInstance, String> dateEndColumn = new Column<JobInstance, String>(cell) {

			@Override
			public String getValue(JobInstance object) {
				Date date = object.getEndDate();
				if (date == null || date.equals(new Date(0))) {
					return ToolsGWT.lblCnst.Never();
				}
				return dateFormatter.format(date);
			}
		};
		
		Column<JobInstance, String> colDuration = new Column<JobInstance, String>(cell) {

			@Override
			public String getValue(JobInstance object) {
				if(object.getDuration() == -1) {
					return ToolsGWT.lblCnst.Unknown();
				}
				else {
					return object.getDurationAsString();
				}
			}
		};

		Column<JobInstance, String> statusColumn = new Column<JobInstance, String>(cell) {

			@Override
			public String getValue(JobInstance object) {
				if(object.getResult() == Result.RUNNING) {
					return ToolsGWT.lblCnst.RunningTask();
				}
				else if(object.getResult() == Result.SUCCESS) {
					return ToolsGWT.lblCnst.Success();
				}
				else if(object.getResult() == Result.ERROR) {
					return ToolsGWT.lblCnst.Error();
				}
				return ToolsGWT.lblCnst.Never();
			}
		};

		final ButtonImageCell detailCell = new ButtonImageCell(style.imgGrid());
		Column<JobInstance, String> colDetail = new Column<JobInstance, String>(detailCell) {

			@Override
			public String getValue(JobInstance object) {
				return PortalImage.INSTANCE.ic_view_list_black_18dp().getSafeUri().asString();
			}
		};
		colDetail.setFieldUpdater(new FieldUpdater<JobInstance, String>() {

			@Override
			public void update(int index, JobInstance object, String value) {
				showMessage(object);
			}
		});

		DataGrid.Resources resources = new CustomResources();
		DataGrid<JobInstance> dataGrid = new DataGrid<JobInstance>(12, resources);
		dataGrid.setWidth("100%");
		dataGrid.setHeight("100%");
		dataGrid.addColumn(dateStartColumn, ToolsGWT.lblCnst.Start());
		dataGrid.addColumn(dateEndColumn, ToolsGWT.lblCnst.EndDate());
		dataGrid.addColumn(colDuration, ToolsGWT.lblCnst.Duration());
		dataGrid.addColumn(statusColumn, ToolsGWT.lblCnst.Status());
		dataGrid.addColumn(colDetail, ToolsGWT.lblCnst.Information());
		dataGrid.setEmptyTableWidget(new Label(ToolsGWT.lblCnst.NoResult()));

		dataProvider = new ListDataProvider<JobInstance>();
		dataProvider.addDataDisplay(dataGrid);

		// Add a selection model so we can select cells.
		SelectionModel<JobInstance> selectionModel = new SingleSelectionModel<JobInstance>();
		dataGrid.setSelectionModel(selectionModel);

		// Create a Pager to control the table.
		SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
		SimplePager pager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0, true);
		pager.addStyleName(style.pager());
		pager.setDisplay(dataGrid);

		SimplePanel panelDataGrid = new SimplePanel();
		panelDataGrid.setSize("900px", "350px");
		panelDataGrid.setWidget(dataGrid);

		SimplePanel panelPager = new SimplePanel();
		panelPager.setWidget(pager);

		HTMLPanel panel = new HTMLPanel("");
		panel.add(panelDataGrid);
		panel.add(panelPager);

		return panel;
	}

	private void showMessage(JobInstance object) {
		DialogLogDetails dial = new DialogLogDetails(ToolsGWT.lblCnst.Log(), object.getMessage());
		dial.center();
	}

	private ClickHandler closeHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			SchedulerHistoricDialog.this.hide();
		}
	};

	private class CustomCell extends TextCell {

		public CustomCell() {
			super();
		}

		@Override
		public Set<String> getConsumedEvents() {
			Set<String> consumedEvents = new HashSet<String>();
			consumedEvents.add("dblclick");
			return consumedEvents;
		}

		@Override
		public void onBrowserEvent(com.google.gwt.cell.client.Cell.Context context, Element parent, String value, NativeEvent event, ValueUpdater<String> valueUpdater) {
			JobInstance item = (JobInstance) context.getKey();
			if (event.getType().equals("dblclick") && item.getResult() == Result.ERROR) {
//				MessageHelper.openMessageDialog(ToolsGWT.lblCnst.Error(), item.getLog());
			}
			super.onBrowserEvent(context, parent, value, event, valueUpdater);
		}
	}
}
