package bpm.gwt.commons.client;

import java.util.ArrayList;
import java.util.List;

import bpm.gwt.commons.client.listeners.FinishListener;
import bpm.gwt.commons.client.listeners.HasFinishListener;
import bpm.gwt.commons.client.loading.GreyAbsolutePanel;
import bpm.gwt.commons.client.loading.IWait;
import bpm.gwt.commons.client.loading.WaitAbsolutePanel;
import bpm.gwt.commons.client.utils.VanillaCSS;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * This class is the Base for every dialog used in a Vanilla Web App Application
 * 
 * Due to problem in UIBinder, it uses some tricks to work.
 * 
 * Don't forget when you extend this class to call applyStyle in your class (constructor).
 * Also in the ui xml you need to have the same structure and the style MyStyle in the css part. 
 * 
 * See ExampleDialog in that package to see how it works.
 * 
 * 
 * @author Sébastien
 *
 */
@Deprecated
public class BaseDialogBox extends DialogBox implements HasFinishListener, IWait {
	
	public static final String CSS_MAIN_PANEL = "custom-dialog-mainPanel";
	public static final String CSS_MIN = "custom-dialog-min";
	public static final String CSS_MAX = "custom-dialog-max";
	public static final String CSS_CONTENT_PANEL = "custom-dialog-contentPanel";
	public static final String CSS_PANEL_BUTTON = "custom-dialog-panelButton";
	public static final String CSS_BUTTON = "custom-dialog-button";
	public static final String CSS_PANEL_NO_BUTTON = "custom-dialog-contentPanel-no-button";
	public static final String CSS_GLASS = "custom-dialog-glass";
	public static final String CSS_GLASS_Z_INDEX = "custom-dialog-glass-zIndex";
	public static final String CSS_Z_INDEX = "custom-dialog-zIndex";

	private static BaseDialogBoxUiBinder uiBinder = GWT.create(BaseDialogBoxUiBinder.class);

	interface BaseDialogBoxUiBinder extends UiBinder<Widget, BaseDialogBox> {
	}
	
	@UiField
	HTMLPanel dialog, contentPanel, panelButton;

	@UiField
	Image btnReduce, btnMax, btnClose;
	
	@UiField
	Label lblTitle;
	
	private int width, height;

	private final List<FinishListener> listeners = new ArrayList<FinishListener>();

	private GreyAbsolutePanel greyPanel;
	private WaitAbsolutePanel waitPanel;
	private boolean isCharging;

	private int indexCharging = 0;
	
	public BaseDialogBox(String title, int width, int height, boolean canBeResize, boolean hasButton) {
		super.setWidget(uiBinder.createAndBindUi(this));
		this.width = width;
		this.height = height;
		
		lblTitle.setText(title);
		
		if(!canBeResize) {
			btnReduce.setVisible(false);
			btnMax.setVisible(false);
		}
		else {
			btnReduce.setVisible(false);
		}
		
		if(!hasButton) {
			contentPanel.addStyleName(CSS_PANEL_NO_BUTTON);
		}
		
		this.setGlassEnabled(true);
		this.setGlassStyleName(CSS_GLASS);
		
		dialog.addStyleName(VanillaCSS.DIALOG);
//		panelTop.addStyleName(VanillaCSS.DIALOG_TOP);
		btnReduce.addStyleName(VanillaCSS.DIALOG_TOP_BUTTON);
		btnMax.addStyleName(VanillaCSS.DIALOG_TOP_BUTTON);
		btnClose.addStyleName(VanillaCSS.DIALOG_TOP_BUTTON);
	}

	public void applyStyle(Widget childDialog, Widget contentPanel) {
		dialog.getElement().getStyle().setWidth(width, Unit.PX);
		dialog.getElement().getStyle().setHeight(height, Unit.PX);
		childDialog.addStyleName(CSS_MAIN_PANEL);
		contentPanel.addStyleName(CSS_CONTENT_PANEL);
	}
	
	public void increaseZIndex() {
		dialog.addStyleName(CSS_Z_INDEX);
		setGlassStyleName(CSS_GLASS_Z_INDEX);
	}
	
	public Button createButton(String label, ClickHandler clickHandler){
		Button btn = new Button(label);
		btn.setStyleName(CSS_BUTTON);
		btn.addClickHandler(clickHandler);
		btn.getElement().getStyle().setMarginLeft(5, Unit.PX);
		
		panelButton.addStyleName(CSS_PANEL_BUTTON);
		panelButton.add(btn);
		
		return btn;
	}

	public void createButtonBar(String lblConfirm, ClickHandler confirmHandler, String lblCancel, ClickHandler cancelHandler) {
		createButton(lblConfirm, confirmHandler);
		createButton(lblCancel, cancelHandler);
	}
	
	@Override
	public void setWidget(Widget w) {
		contentPanel.add(w);
	}
	
	@UiHandler("btnReduce")
	public void onReduceClick(ClickEvent event) {
		changeDisplay(true);
	}
	
	@UiHandler("btnMax")
	public void onMaximiseClick(ClickEvent event) {
		changeDisplay(false);
	}

	private void changeDisplay(boolean reduce) {
		btnMax.setVisible(reduce);
		btnReduce.setVisible(!reduce);
		if(reduce) {
			dialog.removeStyleName(CSS_MAX);
			dialog.addStyleName(CSS_MIN);
		}
		else {
			dialog.removeStyleName(CSS_MIN);
			dialog.addStyleName(CSS_MAX);
		}
	}
	
	@UiHandler("btnClose")
	public void onCloseClick(ClickEvent event) {
		hide();
	}
	
	@Override
	public void center() {
		super.center();
		
		int clientWidth = Window.getClientWidth();
		int offsetWidth = dialog.getOffsetWidth();
		
		int clientHeight = Window.getClientHeight();
		int offsetHeight = dialog.getOffsetHeight();
		
		int left = (clientWidth - offsetWidth) >> 1;
	    int top = (clientHeight - offsetHeight) >> 1;
	    
	    left -= Document.get().getBodyOffsetLeft();
	    top -= Document.get().getBodyOffsetTop();

	    Element elem = dialog.getElement();
	    elem.getStyle().setPropertyPx("left", left);
	    elem.getStyle().setPropertyPx("top", top);
	}
    
    protected void finish(Object result, Object source, String result1) {
		fireFinishClicked(result, source,  result1);
    }
    
	@Override
	public void addFinishListener(FinishListener listener) {
        listeners.add(listener);
    }
    
    public void removeFinishListener(FinishListener listener) {
        listeners.remove(listener);
    }
    
    public List<FinishListener> getFinishListeners() {
        return listeners;
    }
    
    private void fireFinishClicked(Object result, Object source, String result1) {
    	for(FinishListener listener : getFinishListeners()){
    		if(listener != null){
    			listener.onFinish(result, source, result1);
    		}
    	}
    }

	@Override
	public void showWaitPart(boolean visible) {

		if (visible) {
			indexCharging++;

			if (!isCharging) {
				isCharging = true;

				greyPanel = new GreyAbsolutePanel();
				waitPanel = new WaitAbsolutePanel();

				dialog.add(greyPanel);
				dialog.add(waitPanel);

				int height = this.getOffsetHeight();
				int width = this.getOffsetWidth();

				if (height != 0) {
					DOM.setStyleAttribute(waitPanel.getElement(), "top", ((height / 2) - 50) + "px");
				}
				else {
					DOM.setStyleAttribute(waitPanel.getElement(), "top", 42 + "%");
				}

				if (width != 0) {
					DOM.setStyleAttribute(waitPanel.getElement(), "left", ((width / 2) - 100) + "px");
				}
				else {
					DOM.setStyleAttribute(waitPanel.getElement(), "left", 35 + "%");
				}
			}
		}
		else if (!visible) {
			indexCharging--;

			if (isCharging && indexCharging == 0) {
				isCharging = false;

				dialog.remove(greyPanel);
				dialog.remove(waitPanel);
			}
		}
	}
	
	public void updateTitle(String title) {
		lblTitle.setText(title);
	}
}
