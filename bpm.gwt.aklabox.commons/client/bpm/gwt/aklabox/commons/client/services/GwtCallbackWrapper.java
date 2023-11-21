package bpm.gwt.aklabox.commons.client.services;

import bpm.gwt.aklabox.commons.client.customs.ExceptionManager;
import bpm.gwt.aklabox.commons.client.loading.IWait;
import bpm.gwt.aklabox.commons.client.utils.WaitDialog;

import com.google.gwt.user.client.rpc.AsyncCallback;

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
		this.waitPanel = waitPanel;
		this.stopLoading = stopLoading;
		this.handleError = handleError;
		
		if (waitPanel != null && startLoading) {
			waitPanel.showWaitPart(true);
		}
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
			WaitDialog.showWaitPart(false);
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