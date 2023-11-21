package bpm.gwt.commons.client.custom.v2;

import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;

public class CommonAsyncProvider<T> extends AsyncDataProvider<T> {

	private ILoadDataHandler loadDataHandler;
	
	public CommonAsyncProvider(ILoadDataHandler loadDataHandler) {
		this.loadDataHandler = loadDataHandler;
	}
	
//	public void refreshFilterData(final DrillThroughContainer drillContainer, DrillInformations drillInfo, final HasData<List<String>> display) {
//		drillContainer.showWaitPart(true);
//		
//		FaWebService.Connect.getInstance().applyDrillFilter(drillInfo, filters, new AsyncCallback<DrillInformations>() {
//
//			@Override
//			public void onFailure(Throwable caught) {
//				drillContainer.showWaitPart(false);
//				
//				caught.printStackTrace();
//				
//				ExceptionManager.getInstance().handleException(caught, "Unable to apply filters.");
//			}
//
//			@Override
//			public void onSuccess(DrillInformations result) {
//				drillContainer.showWaitPart(false);
//				
//				drillContainer.setDrillInformations(result);
//				updateRowCount(result.getSize(), true);
//				
//				changed = true;
//				
//				onRangeChanged(display);
//				
//			}
//		});
//	}

	@Override
	protected void onRangeChanged(HasData<T> display) {
		// Get the new range.
		final Range range = display.getVisibleRange();

		final int start = range.getStart();
		int length = range.getLength();

		loadDataHandler.loadData(start, length);
	}
	
	public interface ILoadDataHandler {
		
		public void loadData(int start, int length);
	}
}