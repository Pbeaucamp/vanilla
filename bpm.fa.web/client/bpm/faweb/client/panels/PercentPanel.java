package bpm.faweb.client.panels;

import bpm.faweb.client.FreeAnalysisWeb;
import bpm.faweb.client.utils.Space;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class PercentPanel extends VerticalPanel{

	private CheckBox cbPercent;
	private CheckBox cbShowMes;
	
	private String measure;
	
	public PercentPanel(String measure, String mesName) {
		super();
		this.measure = measure;
	
		Label lblMes = new Label(mesName);
		lblMes.addStyleName("chartLabel");
		
		cbPercent = new CheckBox(FreeAnalysisWeb.LBL.Percent());
		cbShowMes = new CheckBox(FreeAnalysisWeb.LBL.ShowMeasure());
		
		HorizontalPanel cbPanel = new HorizontalPanel();
		
		cbPanel.add(cbPercent);
		cbPanel.add(cbShowMes);
		
		cbPanel.setWidth("350px");
		cbPanel.setCellHorizontalAlignment(cbPercent, ALIGN_CENTER);
		cbPanel.setCellHorizontalAlignment(cbShowMes, ALIGN_CENTER);
		
		this.add(new Space("1px", "10px"));
		this.add(lblMes);
		this.add(new Space("1px", "10px"));
		this.add(cbPanel);
		
		this.addStyleName("percentPanelBorder");
		
		cbPercent.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if(!cbShowMes.isEnabled()) {
					cbShowMes.setEnabled(true);
				}
				else if(!cbPercent.getValue()) {
					cbShowMes.setEnabled(false);
					cbShowMes.setValue(true);
				}
			}
		});
	}
	
	public boolean isPercent() {
		return cbPercent.getValue();
	}
	public boolean isShowMeasure() {
		return cbShowMes.getValue();
	}
	public String getMeasure() {
		return measure;
	}
	public void setPercent(boolean isPercent) {
		cbPercent.setValue(isPercent);
	}
	public void setShowMes(boolean isShowMes) {
		cbShowMes.setValue(isShowMes);
	}

	public void setEnabled() {
		if(cbPercent.getValue()) {
			cbShowMes.setEnabled(true);
		}
		else {
			cbShowMes.setEnabled(false);
			cbShowMes.setValue(true);
		}
	}
}
