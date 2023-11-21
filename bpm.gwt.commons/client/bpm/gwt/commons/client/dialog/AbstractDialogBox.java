package bpm.gwt.commons.client.dialog;

import bpm.gwt.commons.client.listeners.FinishListener;
import bpm.gwt.commons.client.loading.IWait;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class AbstractDialogBox implements IWait {

	private BaseDialogBox dialog;

	public AbstractDialogBox(String title, boolean canBeResize, boolean hasButton) {
		dialog = new BaseDialogBox(this, title, canBeResize, hasButton);
	}

	public void setTitle(String title) {
		dialog.setTitle(title);
	}

	public void increaseZIndex() {
		dialog.increaseZIndex();
	}

	public void increaseZIndex(int zIndex) {
		dialog.increaseZIndex(zIndex);
	}

	public void setWidget(Widget widget) {
		dialog.setWidget(widget);
	}

	public Button createButton(String label, ClickHandler clickHandler) {
		return dialog.createButton(label, clickHandler);
	}

	public void createButtonBar(String lblConfirm, ClickHandler confirmHandler, String lblCancel, ClickHandler cancelHandler) {
		dialog.createButtonBar(lblConfirm, confirmHandler, lblCancel, cancelHandler);
	}

	public void center() {
		dialog.center();
	}

	public void hide() {
		dialog.hide();
	}

	public boolean isShowing() {
		return dialog.isShowing();
	}

	public void addCloseHandler(CloseHandler<PopupPanel> closeHandler) {
		dialog.addCloseHandler(closeHandler);
	}

	public void addFinishListener(FinishListener finishHandler) {
		dialog.addFinishListener(finishHandler);
	}

	public void setAutoHideEnabled(boolean autoHide) {
		dialog.setAutoHideEnabled(autoHide);
	}

	public void setGlassEnabled(boolean enabled) {
		dialog.setGlassEnabled(enabled);
	}

	/**
	 * 
	 * Override if you want to support dialog maximizing.
	 * 
	 * @param maximize
	 */
	public void maximize(boolean maximize) {
		// DO Nothing
	}

	/**
	 * 
	 * Override if you want to support on detach method.
	 * 
	 * @param maximize
	 */
	public void onDetach() {
		// DO Nothing
	}

	/**
	 * 
	 * Override if you want to support on attach method.
	 * 
	 * @param maximize
	 */
	public void onAttach() {
		// DO Nothing
	}

	public void finish(Object result, Object source, String result1) {
		dialog.finish(result, source, result1);
	}

	@Override
	public void showWaitPart(boolean visible) {
		dialog.showWaitPart(visible);
	}

	public void show() {
		dialog.show();
	}

	public boolean isClose() {
		return dialog.isClose();
	}

	public void setPopupPosition(int x, int y) {
		dialog.setPopupPosition(x, y);
	}

	public void setModal(boolean modal) {
		dialog.setModal(modal);
	}
	
	public void addStyleName(String styleName) {
		dialog.addStyleName(styleName);
	}
}
