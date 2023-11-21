package bpm.gwt.commons.client.calendar;

import java.io.Serializable;
import java.util.Date;

import com.google.gwt.user.client.ui.Widget;

/**
 * Cette classe réprésente un événement de l'agenda d'un individu
 * 
 */
public class CalendarEvent implements Serializable {

	private static final long serialVersionUID = 1L;

	public enum TypeEvent {
		KPI_METRIC_VALUE
	}
	
	private TypeEvent type;
	
	private String libelle;
	private Widget widget;
	
	private Date beginDate;
	private Date endDate;
	private long duration;
	
	public CalendarEvent() {
	}
	
	public CalendarEvent(TypeEvent type, long itemId, String libelle, Date beginDate, Date endDate, long duration) {
		this.type = type;
		this.libelle = libelle;
		
		this.beginDate = beginDate;
		this.endDate = endDate;
		this.duration = duration;
	}
	
	public CalendarEvent(TypeEvent type, long itemId, Widget widget, Date beginDate, Date endDate, long duration) {
		this(type, itemId, "", beginDate, endDate, duration);
		this.widget = widget;
	}
	
	public TypeEvent getType() {
		return type;
	}
	
	public String getLibelle() {
		return libelle;
	}
	
	public boolean hasWidget() {
		return widget != null;
	}
	
	public Widget getWidget() {
		return widget;
	}
	
	public Date getBeginDate() {
		return beginDate;
	}
	
	public Date getEndDate() {
		return endDate;
	}

	public long getDuration() {
		return duration;
	}
}
