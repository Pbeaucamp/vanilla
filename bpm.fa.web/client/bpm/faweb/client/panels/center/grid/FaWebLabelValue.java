package bpm.faweb.client.panels.center.grid;

import java.util.HashMap;

import bpm.faweb.client.MainPanel;
import bpm.faweb.client.dnd.ResizablePanel;
import bpm.faweb.client.services.FaWebService;
import bpm.faweb.client.services.FaWebServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

public class FaWebLabelValue extends ResizablePanel {
	
	private MainPanel mainPanel;
	
	private int row;
	private int col;

	private float val;

	public FaWebLabelValue(MainPanel mainPanel, String label, int i, int j, float val, CubeView view) {
		super(mainPanel, view, i, j);
		this.mainPanel = mainPanel;

		if (label.contains("style")) {
			String style = label.substring(label.indexOf("style"));
			style = style.split("=")[1];
			String[] styleFields = style.split(":");
			String styleName = styleFields[0];
			String styleValue = styleFields[1];
			DOM.setStyleAttribute(this.getElement(), styleName, styleValue);
			String value = (label.substring(0, label.indexOf("style"))).replace("|", "");
			html.setHTML(value);
		}

		else {
			html.setHTML(label);
		}

		this.row = i;
		this.col = j;
		this.setVal(val);
		html.setWordWrap(false);

		html.getElement().getStyle().setBackgroundColor("#FFFFFF");
		html.getElement().getStyle().setFontSize(11, Unit.PX);
		html.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
		html.getElement().getStyle().setOverflow(Overflow.HIDDEN);
		html.getElement().getStyle().setCursor(Cursor.POINTER);
		DOM.setStyleAttribute(html.getElement(), "textAlign", "center");
		DOM.setStyleAttribute(html.getElement(), "whiteSpace", "nowrap");
//		DOM.setStyleAttribute(html.getElement(), "display", "table-cell");
		html.addStyleName("gridItemValue");

		// disableContextMenu(getElement());
		setselectedhighlight(getElement());
		sinkEvents(Event.ONDBLCLICK | Event.BUTTON_RIGHT | Event.ONMOUSEUP);
	}

	public void onBrowserEvent(Event event) {
		switch (DOM.eventGetType(event)) {
		case Event.ONMOUSEUP:
			switch (DOM.eventGetButton(event)) {
			case Event.BUTTON_RIGHT:
				drillMulitiple(row, col, DOM.eventGetClientX(event), DOM.eventGetClientY(event));
				break;
			}
			break;

		case Event.ONDBLCLICK:
			mainPanel.showTabWaitPart(true);
			mainPanel.drillthrough(row, col);
			break;
		}
	}

	private void drillMulitiple(int row, int col, final int x, final int y) {
		FaWebServiceAsync svc = (FaWebServiceAsync) GWT.create(FaWebService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) svc;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "faWebService";
		endpoint.setServiceEntryPoint(moduleRelativeURL);

		// execute the service
		svc.drillMultipleService(mainPanel.getKeySession(), row, col, new AsyncCallback<HashMap<String, String>>() {
			
			@Override
			public void onSuccess(HashMap<String, String> result) {
				if (result != null) {
					//TODO: See if it is used
//					FaWebContextuelValue cm = new FaWebContextuelValue(result);
					// cm.show();
					// cm.setPopupPosition(x+20, y+20);
				}
			}
			
			@Override
			public void onFailure(Throwable ex) {
				ex.printStackTrace();
			}
		});
	}

	private native void setselectedhighlight(Element elem) /*-{
		elem.onselectstart = function(a, b) {
			return false
		};
	}-*/;

	public void setVal(float val) {
		this.val = val;
	}

	public float getVal() {
		return val;
	}

}
