package bpm.gwt.commons.client.dialog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class Notification extends PopupPanel {

	private static NotificationUiBinder uiBinder = GWT.create(NotificationUiBinder.class);

	interface NotificationUiBinder extends UiBinder<Widget, Notification> {
	}
	
	@UiField
	SimplePanel panelContent;
	
	@UiField
	HTML txt;

	public Notification(String notification) {
		setWidget(uiBinder.createAndBindUi(this));
		
		txt.setHTML(notification);
	}

	public Notification(NotificationPanel notificationPanel) {
		setWidget(uiBinder.createAndBindUi(this));
		notificationPanel.setParent(this);
		
		panelContent.setWidget(notificationPanel);
	}

	public void show(int duration) {
		show();
		
	    Element elem = getElement();
	    elem.getStyle().setPropertyPx("right", 50);
	    elem.getStyle().setPropertyPx("top", 62);
		
	    if (duration > 0) {
		    Timer t = new Timer() {
				@Override
				public void run() {
					hide();
				}
			};
			t.schedule(duration);
	    }
	}
	
	@UiHandler("btnClose")
	public void onClose(ClickEvent event) {
		hide();
	}
}
