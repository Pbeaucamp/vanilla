package bpm.fmloader.client.panel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import bpm.fm.api.model.utils.AxisInfo;
import bpm.fm.api.model.utils.LevelMember;
import bpm.fm.api.model.utils.LoaderDataContainer;
import bpm.fm.api.model.utils.LoaderMetricValue;
import bpm.fmloader.client.constante.Constantes;
import bpm.fmloader.client.table.MetricDataPanel;
import bpm.fmloader.client.tools.AlphanumComparator;

import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TextBox;

public class MetricNewValuePanel extends HTMLPanel {

	private static AlphanumComparator comparator = new AlphanumComparator();
	
	private ElementPanel txtValue;
	private ElementPanel txtObjectif;
	private ElementPanel txtMin;
	private ElementPanel txtMax;
	
	private HashMap<Integer, ElementPanel> txtAxis = new HashMap<Integer, ElementPanel>();
	
	private LoaderDataContainer dataContainer;

	private MetricDataPanel parent;
	
	public MetricNewValuePanel(LoaderDataContainer dataContainer, MetricDataPanel parent) {
		super("");
		this.parent = parent;
		this.dataContainer = dataContainer;
		
//		this.setSize("100%", "50px");
		this.setHeight("50px");
		
		this.getElement().getStyle().setDisplay(Display.BLOCK);
		this.getElement().getStyle().setPadding(10, Unit.PX);
		this.getElement().getStyle().setBorderColor("#399d93");
		this.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
		this.getElement().getStyle().setBorderWidth(1, Unit.PX);
		this.getElement().getStyle().setProperty("width", "auto");
		this.getElement().getStyle().setPaddingLeft(35, Unit.PX);
//		this.getElement().getStyle().setPosition(Position.FIXED);
		
		txtValue = new ElementPanel(new TextBox(), Constantes.LBL.value());
		txtObjectif = new ElementPanel(new TextBox(), Constantes.LBL.objectives());
		txtMin = new ElementPanel(new TextBox(), "Min");
		txtMax = new ElementPanel(new TextBox(), "Max");
		
		this.add(txtValue);
		this.add(txtObjectif);
		this.add(txtMin);
		this.add(txtMax);
		
		int i =0;
		for(AxisInfo info : dataContainer.getAxisInfos()) {
			MultiWordSuggestOracle oracle = new MultiWordSuggestOracle();
			
			List<String> axisValues = getAxisValuesAsString(info);
			oracle.addAll(axisValues);
			
			SuggestBox box = new SuggestBox(oracle);
			
			ElementPanel element = new ElementPanel(box, info.getAxis().getName());
			txtAxis.put(i, element);
			
			this.add(element);
			
			i++;
		}
		
		txtValue.setWidth("100px");
		txtObjectif.setWidth("100px");
		txtMin.setWidth("100px");
		txtMax.setWidth("100px");
		for(ElementPanel panel : txtAxis.values()) {
			panel.setWidth("250px");
		}
		
		Button addLine = new Button(Constantes.LBL.add());
//		addLine.getElement().getStyle().setPosition(Position.ABSOLUTE);
//		addLine.getElement().getStyle().setRight(10, Unit.PX);
		addLine.getElement().getStyle().setMargin(4, Unit.PX);
		
		addLine.getElement().getStyle().setBorderColor("#555555");
		addLine.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
		addLine.getElement().getStyle().setBorderWidth(1, Unit.PX);
		addLine.getElement().getStyle().setColor("#FFFFFF");
		addLine.getElement().getStyle().setHeight(40, Unit.PX);
		addLine.getElement().getStyle().setWidth(60, Unit.PX);
		addLine.getElement().getStyle().setProperty("background", "none repeat scroll 0 0 #399d93");
		addLine.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		
		this.add(addLine);
		
		addLine.addClickHandler(new ClickHandler() {		
			@Override
			public void onClick(ClickEvent event) {
				MetricNewValuePanel.this.parent.addLine(getValue());
			}
		});
	}

	private List<String> getAxisValuesAsString(AxisInfo axis) {
		List<LevelMember> members = new ArrayList<LevelMember>(axis.getMemberKeys().values());
		Collections.sort(members, new Comparator<LevelMember>() {
			@Override
			public int compare(LevelMember o1, LevelMember o2) {
				return comparator.compare(o1.getLabel(), o2.getLabel());
			}
		});
		
		List<String> result = new ArrayList<String>();
		for(LevelMember member : members) {
			result.add(member.getLabel() + " : " + member.getValue());
		}
		return result;
	}
	
	public LoaderMetricValue getValue() {
		
		LoaderMetricValue value = new LoaderMetricValue();
		value.setNew(true);
		
		double val = Double.parseDouble(txtValue.getValue());
		double obj = Double.parseDouble(txtObjectif.getValue());
		double min = Double.parseDouble(txtMin.getValue());
		double max = Double.parseDouble(txtMax.getValue());
		
		value.setMaximum(max);
		value.setMinimum(min);
		value.setObjective(obj);
		value.setValue(val);
		
		for(int i : txtAxis.keySet()) {
			ElementPanel panel = txtAxis.get(i);
			
			String member = panel.getValue().split(":")[1].trim();
			
			LevelMember mem = dataContainer.getAxisInfos().get(i).getMemberKeys().get(member);
			
			value.addMember(mem);
		}
		
		return value;
	}
	
	private class ElementPanel extends HTMLPanel {
		
		private HasText txtBox;
		
		public ElementPanel(HasText txtBox, String label) {
			super("");
			this.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
			this.txtBox = txtBox;
			
			Label lbl = new Label(label);
			this.add(lbl);
			
			if(txtBox instanceof SuggestBox) {
				((SuggestBox)txtBox).setWidth("240px");
				this.add((SuggestBox)txtBox);
			}
			else {
				((TextBox)txtBox).setWidth("90px");
				this.add((TextBox)txtBox);
			}
		}
		
		public String getValue() {
			return txtBox.getText();
		}
	}
	
	public String getWidth() {
		return ((400 + (txtAxis.size() * 250 )) + 50 + 70) + "px";
	}
	
}
