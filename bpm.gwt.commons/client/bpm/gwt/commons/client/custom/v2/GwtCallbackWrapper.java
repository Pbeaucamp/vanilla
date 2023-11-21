package bpm.gwt.commons.client.custom.v2;

import bpm.gwt.commons.client.ExceptionManager;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Helper to call the server to make a RCP call.
 * It automatically manage the Failure part.
 */
public abstract class GwtCallbackWrapper<T> {

	private IWait waitPanel;
	private boolean stopLoading;
	private boolean handleError = true;
	
	public GwtCallbackWrapper(IWait waitPanel, boolean startLoading, boolean stopLoading) {
		this.waitPanel = waitPanel;
		this.stopLoading = stopLoading;
		
		if (waitPanel != null && startLoading) {
			waitPanel.showWaitPart(true);
		}
	}
	
	public GwtCallbackWrapper(IWait waitPanel, boolean startLoading, boolean stopLoading, boolean handleError) {
		this(waitPanel, startLoading, stopLoading);
		this.handleError = handleError;
	}
	
	private AsyncCallback<T> asyncCallback = new AsyncCallback<T>() {
		
		@Override
		public void onSuccess(T result) {
			if(waitPanel != null && stopLoading) {
				waitPanel.showWaitPart(false);
			}
			GwtCallbackWrapper.this.onSuccess(result);
		}

		@Override
		public void onFailure(Throwable t) {
			if(waitPanel != null) {
				waitPanel.showWaitPart(false);
			}
			
			if (handleError) {
				ExceptionManager.getInstance().handleException(t, t.getMessage());
			}

			GwtCallbackWrapper.this.onFailure(t);
		}
	};

	public AsyncCallback<T> getAsyncCallback() {
		return asyncCallback;
	}
	
	//Override if you want to do something when an error occured
	public void onFailure(Throwable t) { };

	public abstract void onSuccess(T result);
}