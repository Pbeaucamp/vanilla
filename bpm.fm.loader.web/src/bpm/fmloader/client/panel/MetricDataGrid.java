package bpm.fmloader.client.panel;

import java.util.Comparator;
import java.util.List;

import bpm.fm.api.model.FactTable;
import bpm.fm.api.model.Metric;
import bpm.fmloader.client.constante.Constantes;
import bpm.fmloader.client.dialog.MetricValuesDialog;
import bpm.fmloader.client.infos.InfosUser;
import bpm.fmloader.client.table.MetricValuesPanel;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.custom.HasActionCell;
import bpm.gwt.commons.client.custom.v2.CompositeCellHelper;
import bpm.gwt.commons.client.custom.v2.GridPanel;
import bpm.gwt.commons.client.dialog.ItemLinkedDialog;
import bpm.gwt.commons.client.images.CommonImages;
import bpm.gwt.commons.shared.InfoUser;
import bpm.vanilla.platform.core.beans.Group;

import com.google.gwt.cell.client.ActionCell.Delegate;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.google.gwt.view.client.SingleSelectionModel;

public class MetricDataGrid extends GridPanel<Metric> {

	private InfoUser infoUser;
	
	private SingleSelectionModel<Metric> selectionModel = new SingleSelectionModel<Metric>();
	// private ListDataProvider<Metric> dataProvider = new
	// ListDataProvider<Metric>();
	private MetricValuesPanel valuePanel;
	private FMDataPanel mainPanel;
//	private ListHandler<Metric> nameHandler;
//	private ListHandler<Metric> periodHandler;
//	private static DataGrid.Resources resources = new CustomResources();

	public MetricDataGrid(InfoUser infoUser, MetricValuesPanel valuePanel, FMDataPanel fmDataPanel) {
		super();
		this.infoUser = infoUser;
		this.valuePanel = valuePanel;
		this.mainPanel = fmDataPanel;

		this.setWidth("100%");
		this.setHeight("100%");

		Column<Metric, String> colName = new Column<Metric, String>(new TextCell()) {
			@Override
			public String getValue(Metric object) {
				return object.getName();
			}
		};
		Column<Metric, String> colPeriod = new Column<Metric, String>(new TextCell()) {
			@Override
			public String getValue(Metric object) {
				return ((FactTable) object.getFactTable()).getPeriodicity();
			}
		};
		
		HasActionCell<Metric> optionsCell = new HasActionCell<Metric>(CommonImages.INSTANCE.metadataexplorer_results(), LabelsConstants.lblCnst.Values(), new Delegate<Metric>() {

			@Override
			public void execute(final Metric object) {
				loadValues(object);
			}
		});
		
		HasActionCell<Metric> linkCell = new HasActionCell<Metric>(CommonImages.INSTANCE.assos(), LabelsConstants.lblCnst.LinkETL(), new Delegate<Metric>() {

			@Override
			public void execute(final Metric object) {
				onLink(object);
			}
		});
		
		CompositeCellHelper<Metric> compCell = new CompositeCellHelper<Metric>(optionsCell, linkCell);
		Column<Metric, Metric> colAction = new Column<Metric, Metric>(compCell.getCell()) {
			@Override
			public Metric getValue(Metric object) {
				return object;
			}
		};

		addColumn(Constantes.LBL.name(), colName, null, new Comparator<Metric>() {
			@Override
			public int compare(Metric o1, Metric o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		addColumn(Constantes.LBL.periode(), colPeriod, null, new Comparator<Metric>() {
			@Override
			public int compare(Metric o1, Metric o2) {
				return ((FactTable) o1.getFactTable()).getPeriodicity().compareTo(((FactTable) o2.getFactTable()).getPeriodicity());
			}
		});
		addColumn("", colAction, "110px", null);

		setEmptyTableWidget(new Label(Constantes.LBL.noValues()));

		selectionModel.addSelectionChangeHandler(new Handler() {
			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
				MetricDataGrid.this.valuePanel.fillTable(mainPanel.getSelectedDate(), selectionModel.getSelectedObject());
			}
		});
		setSelectionModel(selectionModel);

		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				setTopManually(0);
			}
		});
	}

	private void loadValues(Metric object) {
		MetricValuesDialog dial = new MetricValuesDialog(infoUser, object);
		dial.center();
	}
	
	public void fill(List<Metric> metrics) {
		// dataProvider.setList(metrics);
		// nameHandler.setList(dataProvider.getList());
		// periodHandler.setList(dataProvider.getList());
		loadItems(metrics);
	}
	
	private void onLink(Metric metric) {
		if (metric != null && metric.getFactTable() != null && metric.getFactTable() instanceof FactTable) {
			InfosUser infosUser = InfosUser.getInstance();
			String login = infosUser.getUser().getLogin();
			String password = infosUser.getUser().getPassword();
			String vanillaUrl = infoUser.getVanillaRuntimeUrl();
			
			//TODO: To change with the real group
			Group group = infosUser.getSelectedGroup();
			
			ItemLinkedDialog dial = new ItemLinkedDialog(login, password, vanillaUrl, group, metric);
			dial.center();
		}
	}
}
