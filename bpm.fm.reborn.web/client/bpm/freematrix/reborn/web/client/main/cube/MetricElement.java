package bpm.freematrix.reborn.web.client.main.cube;

import bpm.fm.api.model.Metric;
import bpm.fm.api.model.MetricLinkedItem;
import bpm.gwt.commons.client.loading.CompositeWaitPanel;

import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;

public class MetricElement extends HTML {

	private Metric metrics;
	private CompositeWaitPanel cubeView;
	private MetricLinkedItem items;

	public MetricElement(Metric metric, MetricLinkedItem item, CompositeWaitPanel parent, boolean oddRow) {
		super(metric.getName() + " - " + item.getItemName());
		
		this.metrics = metric;
		this.items = item;
		this.cubeView = parent;
		
		this.setWidth("250px");
		if(oddRow) {
			this.getElement().getStyle().setBackgroundColor("#EEEEEE");
		}
	    this.getElement().getStyle().setCursor(Cursor.POINTER);
	   	this.getElement().getStyle().setFontSize(14, Unit.PX);
	   	this.getElement().getStyle().setPadding(5, Unit.PX);
		
		this.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if (cubeView instanceof CubeView)
					((CubeView)cubeView).showCube(items);
				else
					((ReportView)cubeView).showReport(items);
			}
		});
	}
	
}
