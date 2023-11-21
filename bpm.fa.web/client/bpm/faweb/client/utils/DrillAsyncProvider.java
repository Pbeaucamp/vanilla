package bpm.faweb.client.utils;

import java.util.ArrayList;
import java.util.List;

import bpm.faweb.client.FreeAnalysisWeb;
import bpm.faweb.client.panels.center.DrillThroughContainer;
import bpm.faweb.client.services.FaWebService;
import bpm.faweb.shared.drill.DrillthroughFilter;
import bpm.gwt.commons.client.ExceptionManager;
import bpm.gwt.commons.shared.analysis.DrillInformations;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;

public class DrillAsyncProvider extends AsyncDataProvider<List<String>> {

	private List<DrillthroughFilter> filters;
	private Integer indexSort;
	private boolean ascending;

	private int key;
	
	private boolean changed;

	public DrillAsyncProvider(int key) {
		this.key = key;
	}

	public void setColumnSort(int indexSort, boolean ascending) {
		this.indexSort = indexSort;
		this.ascending = ascending;
		
		this.changed = true;
	}

	public void addFilter(DrillthroughFilter filter) {
		if (filters == null) {
			filters = new ArrayList<DrillthroughFilter>();
		}
		filters.add(filter);
	}
	
	public void resetFilters() {
		this.filters = new ArrayList<DrillthroughFilter>();
	}
	
	public String getFiltersDefinition() {
		StringBuffer buf = new StringBuffer();
		
		boolean first = true;
		if(filters != null) {
			for(DrillthroughFilter filter : filters) {
				if(first) {
					buf.append(filter.getName());
					first = false;
				}
				else {
					buf.append(", " + filter.getName());
				}
			}
		}
		else {
			buf.append(FreeAnalysisWeb.LBL.NoFilter());
		}
		return buf.toString();
	}
	
	public void refreshFilterData(final DrillThroughContainer drillContainer, DrillInformations drillInfo, final HasData<List<String>> display) {
		drillContainer.showWaitPart(true);
		
		FaWebService.Connect.getInstance().applyDrillFilter(drillInfo, filters, new AsyncCallback<DrillInformations>() {

			@Override
			public void onFailure(Throwable caught) {
				drillContainer.showWaitPart(false);
				
				caught.printStackTrace();
				
				ExceptionManager.getInstance().handleException(caught, "Unable to apply filters.");
			}

			@Override
			public void onSuccess(DrillInformations result) {
				drillContainer.showWaitPart(false);
				
				drillContainer.setDrillInformations(result);
				updateRowCount(result.getSize(), true);
				
				changed = true;
				
				onRangeChanged(display);
				
			}
		});
	}

	@Override
	protected void onRangeChanged(HasData<List<String>> display) {
		// Get the new range.
		final Range range = display.getVisibleRange();

		/*
		 * Query the data asynchronously. If you are using a database, you can
		 * make an RPC call here. We'll use a Timer to simulate a delay.
		 */

		final int start = range.getStart();
		int length = range.getLength();

		FaWebService.Connect.getInstance().getPartOfDrills(key, start, length, indexSort, ascending, changed, new AsyncCallback<List<List<String>>>() {

			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();

				ExceptionManager.getInstance().handleException(caught, "Unable to fetch the next data");
			}

			@Override
			public void onSuccess(List<List<String>> result) {
				changed = false;
				
				if (result != null) {
					updateRowData(start, result);
				}
			}
		});
	}
}