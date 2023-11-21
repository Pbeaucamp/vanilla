package bpm.gwt.commons.client.services;

import bpm.gwt.commons.client.ExceptionManager;
import bpm.gwt.commons.client.loading.IWait;

import com.google.gwt.user.client.rpc.AsyncCallback;

public abstract class GwtCallbackWrapper<T> {

	private IWait waitPanel;
	
	private boolean stopLoading;
	private boolean manageException = true;
	
	public GwtCallbackWrapper(IWait waitPanel, boolean stopLoading) {
		this.waitPanel = waitPanel;
		this.stopLoading = stopLoading;
	}
	
	public GwtCallbackWrapper(IWait waitPanel, boolean startLoading, boolean stopLoading) {
		this(waitPanel, stopLoading);
		
		if (waitPanel != null && startLoading) {
			waitPanel.showWaitPart(true);
		}
	}
	
	public GwtCallbackWrapper(IWait waitPanel, boolean startLoading, boolean stopLoading, boolean manageException) {
		this(waitPanel, startLoading, stopLoading);
		this.manageException = manageException;
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
			t.printStackTrace();
			if(waitPanel != null) {
				waitPanel.showWaitPart(false);
			}
			
			if (manageException) {
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