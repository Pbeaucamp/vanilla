package bpm.faweb.client.panels.center;

import java.util.List;

import bpm.faweb.client.utils.DrillAsyncProvider;
import bpm.faweb.shared.drill.DrillthroughFilter;
import bpm.gwt.commons.shared.analysis.DrillInformations;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.ColumnSortEvent.AsyncHandler;
import com.google.gwt.user.cellview.client.ColumnSortList;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class DrillThroughContainer extends Composite {

	private static DrillThroughContainerUiBinder uiBinder = GWT.create(DrillThroughContainerUiBinder.class);

	interface DrillThroughContainerUiBinder extends UiBinder<Widget, DrillThroughContainer> {
	}

	interface MyStyle extends CssResource {
		String grid();

		String pager();
	}

	@UiField
	MyStyle style;

	@UiField
	SimplePanel panelGrid, panelPager;

	@UiField
	Label lblFilterLegend;

	private DrillThroughTab drillthroughTab;
	private DrillInformations drillInfo;
	
	private DataGrid<List<String>> drillThroughGrid;
	private DrillAsyncProvider drillAsyncProvider;
	

	public DrillThroughContainer(DrillThroughTab drillthroughTab, DrillInformations drillInfo) {
		initWidget(uiBinder.createAndBindUi(this));
		this.drillthroughTab = drillthroughTab;
		this.drillInfo = drillInfo;
		
		buildItems(drillInfo);
		buildFilterLegend();
	}

	public void buildItems(DrillInformations drillInfo) {
		drillThroughGrid = new DataGrid<List<String>>(Integer.MAX_VALUE);
		drillThroughGrid.addStyleName(style.grid());

		int i = 0;
		for (final String item : drillInfo.getColumns()) {
			final int index = i;

			TextColumn<List<String>> col = new TextColumn<List<String>>() {
				@Override
				public String getValue(List<String> object) {
					return object.get(index);
				}
			};
			col.setSortable(true);

			drillThroughGrid.addColumn(col, item);

			AsyncHandler columnSortHandler = new AsyncHandler(drillThroughGrid) {
				@Override
				public void onColumnSort(ColumnSortEvent event) {
					ColumnSortList sortList = drillThroughGrid.getColumnSortList();
					if(sortList.get(0) != null) {
						int sortIndex = drillThroughGrid.getColumnIndex((Column<List<String>, ?>) sortList.get(0).getColumn());
						
						if(sortIndex == index) {
							drillAsyncProvider.setColumnSort(index, event.isSortAscending());
							super.onColumnSort(event);
						}
					}
				}
			};
			drillThroughGrid.addColumnSortHandler(columnSortHandler);
			i++;
		}

		drillAsyncProvider = new DrillAsyncProvider(drillInfo.getKey());
		drillAsyncProvider.addDataDisplay(drillThroughGrid);
		drillAsyncProvider.updateRowCount(drillInfo.getSize(), true);

		// create a pager, giving it a handle to the CellTable
		SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
		SimplePager pager = new SimplePager(TextLocation.CENTER, pagerResources, true, 0, true);
		pager.addStyleName(style.pager());
		pager.setDisplay(drillThroughGrid);
		pager.setPageSize(50);

		panelGrid.setWidget(drillThroughGrid);
		panelPager.setWidget(pager);
	}

	public void resetFilter() {
		drillAsyncProvider.resetFilters();
		
		buildFilterLegend();
		
		drillAsyncProvider.refreshFilterData(this, drillInfo, drillThroughGrid);
	}

	public void buildFilterLegend() {
		lblFilterLegend.setText(drillAsyncProvider.getFiltersDefinition());
	}
	
	public void addFilter(DrillthroughFilter filter) {
		drillAsyncProvider.addFilter(filter);
		
		buildFilterLegend();
		
		drillAsyncProvider.refreshFilterData(this, drillInfo, drillThroughGrid);
	}

	public DrillInformations getDrillInfo() {
		return drillInfo;
	}

	public void setDrillInformations(DrillInformations drillInfo) {
		this.drillInfo = drillInfo;
	}

	public void showWaitPart(boolean visible) {
		this.drillthroughTab.showWaitPart(visible);
	}

}
