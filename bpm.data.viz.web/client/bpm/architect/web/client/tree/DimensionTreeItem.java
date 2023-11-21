package bpm.architect.web.client.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;

import bpm.architect.web.client.I18N.Labels;
import bpm.architect.web.client.panels.DataVizMapPanel;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.vanilla.platform.core.beans.data.DataColumn;
import bpm.vanilla.platform.core.beans.data.DataColumn.FunctionalType;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TreeItem;

public class DimensionTreeItem extends TreeItem {

	private DataVizMapPanel.MyStyle style;
	private List<Map.Entry<String, Integer>> values;
	private int c = 0;
	private DataColumn dimension;
	private List<String> filteredValues;
	
	public DimensionTreeItem(HTMLPanel panel, DataColumn dimension, String filteredValues) {
		super(panel);
		this.dimension = dimension;
		this.filteredValues = filteredValues != null ? Arrays.asList(filteredValues.split("_;_")) : new ArrayList<String>();
	}
	
	public DimensionTreeItem(HTMLPanel panel, List<Map.Entry<String, Integer>> values, DataColumn dimension, String filteredValues) {
		super(panel);
		this.dimension = dimension;
		this.filteredValues = filteredValues != null ? Arrays.asList(filteredValues.split("_;_")) : new ArrayList<String>();
		setValues(values);
		
	}
	
	public void setValues(List<Map.Entry<String, Integer>> values){
		this.values = values;
	}
	
	public void showValues(){
		c = c+5;
		int max = values.size() > c ? c : values.size();
		for(int i=c-5; i<max; i++){
		//for (String val : values.keySet()) {
			Map.Entry<String, Integer> val = values.get(i);
			HTMLPanel panSub = new HTMLPanel("");
			panSub.addStyleName(style.subItems());
			Label lblVal = new Label(val.getKey());
			Label lblValCount = new Label(val.getValue() + "");
			panSub.add(lblVal);
			panSub.add(lblValCount);

			lblVal.addStyleName(style.filterLbl());
			lblValCount.addStyleName(style.filterLblCount());
			if(lblVal.getText().equals(LabelsConstants.lblCnst.None())){
				lblVal.getElement().getStyle().setColor("#4f6991");
			}
			
			if(dimension.getFt() == FunctionalType.NON_EXCLUSIF){
				if(filteredValues.contains(lblVal.getText()) || (lblVal.getText().equals(LabelsConstants.lblCnst.None()) && filteredValues.contains("null"))){
					panSub.addStyleName(style.disjNeg());
				} else {
					panSub.addStyleName(style.disjPos());
				}
			}

			TreeItem itSub = new TreeItem(panSub);
			this.addItem(itSub);
			itSub.setUserObject(val.getKey());
			
			if(i == max-1 && values.size() > max){
				HTMLPanel panExpend = new HTMLPanel("");
				panExpend.addStyleName(style.expendItems());
				Label lblExpand = new Label("+ " + Labels.lblCnst.ShowMore());
				panExpend.add(lblExpand);
				final TreeItem ex = new TreeItem(panExpend);
				panExpend.addDomHandler(new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent event) {
						showValues();
						DimensionTreeItem.this.removeItem(ex);
					}
				}, ClickEvent.getType());
				
				
				this.addItem(ex);
			}
		}
	}
	
	public void setStyle(DataVizMapPanel.MyStyle style){
		this.style = style;
	}
}
