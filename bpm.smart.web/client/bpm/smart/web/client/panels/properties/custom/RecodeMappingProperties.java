package bpm.smart.web.client.panels.properties.custom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bpm.smart.core.model.workflow.activity.RecodeActivity;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class RecodeMappingProperties extends Composite {

	private static RecodeMappingPropertiesUiBinder uiBinder = GWT.create(RecodeMappingPropertiesUiBinder.class);
	
	@UiField
	HTMLPanel contentPanel;
	
	private List<MappingPanel> panels = new ArrayList<MappingPanel>();

	interface RecodeMappingPropertiesUiBinder extends UiBinder<Widget, RecodeMappingProperties> {
	}

	public RecodeMappingProperties() {
		initWidget(uiBinder.createAndBindUi(this));
		contentPanel.getElement().getStyle().setOverflow(Overflow.AUTO);
	}
	
	public void fillData(List<String> values, RecodeActivity activity) {
		contentPanel.clear();
		panels.clear();
		for(String val : values) {
			MappingPanel p = new MappingPanel(val);
			if(activity.getValueMapping() != null && !activity.getValueMapping().isEmpty() && activity.getValueMapping().get(val) != null) {
				p.setMappedValue(activity.getValueMapping().get(val));
			}
			p.getElement().getStyle().setMargin(4, Unit.PX);
			contentPanel.add(p);
			panels.add(p);
		}
	}
	
	public HashMap<String, String> getMappingValues() {
		
		HashMap<String, String> values = new HashMap<String, String>();
		
		for(MappingPanel panel : panels) {
			if(panel.getMappedValue() != null && !panel.getMappedValue().isEmpty()) {
				values.put(panel.getValue(), panel.getMappedValue());
			}
		}
		
		return values;
	}
	
	private class MappingPanel extends HorizontalPanel {
		
		private String value;
		private TextBox txt;
		
		public MappingPanel(String value) {
			this.value = value;
			
			Label lbl = new Label();
			lbl.setText(value);
			lbl.getElement().getStyle().setWidth(80, Unit.PX);
			
			txt = new TextBox();
			
			this.add(lbl);
			this.add(txt);
		}
		
		public String getValue() {
			return value;
		}
		
		public String getMappedValue() {
			return txt.getValue();
		}
		
		public void setMappedValue(String val) {
			txt.setText(val);
		}
	} 

}
