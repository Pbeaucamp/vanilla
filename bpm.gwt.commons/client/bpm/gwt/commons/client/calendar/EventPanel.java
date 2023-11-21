package bpm.gwt.commons.client.calendar;

import bpm.gwt.commons.client.calendar.CalendarEvent.TypeEvent;
import bpm.gwt.commons.client.calendar.CalendarPanel.CalendarManager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * Panel d'un événement
 * 
 * Boutons pour visualiser ou éditer l'événements
 */
public class EventPanel extends Composite {

	private static EventPanelUiBinder uiBinder = GWT.create(EventPanelUiBinder.class);

	interface EventPanelUiBinder extends UiBinder<Widget, EventPanel> {
	}
	
	interface MyStyle extends CssResource {
		String firstEvent();
		String lastEvent();
	}
	
	@UiField
	MyStyle style;
	
	@UiField
	HTMLPanel panelEvent;
	
	@UiField
	Label lblEvent;
	
//	@UiField
//	Image btnView, btnEdit, btnDelete;
	
//	private CalendarManager manager;
//	
//	private Date date;
//	private CalendarEvent event;
		
	private boolean isFirst;

	public EventPanel(CalendarManager manager, CalendarEvent event, boolean edit, boolean isFirst, boolean isLast) {
		initWidget(uiBinder.createAndBindUi(this));
//		this.manager = manager;
//		this.date = date;
//		this.event = event;
		this.isFirst = isFirst;

		if (event.hasWidget()) {
			panelEvent.clear();
			panelEvent.add(event.getWidget());
		}
		else {
			String label = getLabel(event);
			lblEvent.setText(label);
			setTitle(label);
			
			if (isFirst) {
				panelEvent.addStyleName(style.firstEvent());
				lblEvent.setVisible(true);
			}
			
			if (isLast) {
				panelEvent.addStyleName(style.lastEvent());
			}
		}
		
		applyTheme(event.getType());
		
		addDomHandler(new MouseOverHandler() {
			@Override
			public void onMouseOver(MouseOverEvent event) {
				updateUi(true);
			}
		}, MouseOverEvent.getType());
		
		addDomHandler(new MouseOutHandler() {
			
			@Override
			public void onMouseOut(MouseOutEvent event) {
				updateUi(false);
			}
		}, MouseOutEvent.getType());
	}

	private void updateUi(boolean visible) {
		if (!isFirst) {
			lblEvent.setVisible(visible);
		}
	}

	public static String getLabel(CalendarEvent event) {
		return event.getLibelle();
	}
	
	private void applyTheme(TypeEvent event) {
//		switch (event) {
//		case AGENDA_EVENT:
//			panelEvent.addStyleName(ThemeCSS.EVENT_AGENDA_EVENT);
//			break;
//		case PRISE_EN_CHARGE:
//			panelEvent.addStyleName(ThemeCSS.EVENT_PEC);
//			break;
//		case AUTORISATION_SORTIE:
//			panelEvent.addStyleName(ThemeCSS.EVENT_AUT_SORTIE);
//			break;
//		case ACCUEIl_RELAIS:
//			panelEvent.addStyleName(ThemeCSS.EVENT_ACCUEIL_RELAIS);
//			break;
//		case ABSENCE:
//			panelEvent.addStyleName(ThemeCSS.EVENT_ABSENCE);
//			break;
//		case ARRET_ACTIVITE:
//			panelEvent.addStyleName(ThemeCSS.EVENT_ARRET_ACTIVITE);
//			break;
//		default:
//			break;
//		}
	}
}
