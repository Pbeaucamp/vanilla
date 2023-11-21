package bpm.smart.web.client.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import bpm.gwt.commons.client.custom.LabelTextBox;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.utils.VanillaCSS;
import bpm.gwt.commons.client.wizard.IGwtWizard;
import bpm.smart.core.model.RScriptModel;
import bpm.smart.web.client.I18N.LabelsConstants;
import bpm.smart.web.client.services.SmartAirService;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class RFilter extends Composite {

	private static RFilterUiBinder uiBinder = GWT.create(RFilterUiBinder.class);

	interface RFilterUiBinder extends UiBinder<Widget, RFilter> {
	}

	interface MyStyle extends CssResource {
		
	}
	
	public enum Filter {
		EQUALNUM(LabelsConstants.lblCnst.Equals()), SUP(LabelsConstants.lblCnst.IsSuperior()), INF(LabelsConstants.lblCnst.IsInferior()),
		EQUAL(LabelsConstants.lblCnst.Equals()), CONTAIN(LabelsConstants.lblCnst.Contains()), NOTCONTAIN(LabelsConstants.lblCnst.NotContains());

	    private final String name;       

	    private Filter(String s) {
	        name = s;
	    }

	    public boolean equalsName(String otherName) {
	        return (otherName == null) ? false : this.toString().equals(otherName);
	    }

	    public String getLabel() {
	       return this.name;
	    }
		
	}

	@UiField
	MyStyle style;

	@UiField
	LabelTextBox txtValue;

	@UiField
	ListBox lstColumns, lstFilters;
	
	@UiField
	Image btnDel;

	private IGwtWizard parent;
	private List<String> columns;
	private String dataset;

	public RFilter(IGwtWizard parent, String dataset, List<String> columns) {
		initWidget(uiBinder.createAndBindUi(this));
		this.columns = columns;
		this.parent = parent;
		this.dataset = dataset;
		
		lstColumns.addItem("");
		for(String col : this.columns){
			lstColumns.addItem(col);
		}
		
		lstColumns.addStyleName(VanillaCSS.COMMONS_LISTBOX);
		lstFilters.addStyleName(VanillaCSS.COMMONS_LISTBOX);
	}

	@UiHandler("lstColumns")
	public void onColumnChange(ChangeEvent event) {
		if(lstColumns.getSelectedIndex() == 0){
			lstFilters.clear();
		} else {
			String column = lstColumns.getItemText(lstColumns.getSelectedIndex());
			
			RScriptModel box = new RScriptModel();
			String script = "manual_result <- is.numeric("+dataset+"$"+column+") | is.integer("+dataset+"$"+column+")\n";
			box.setScript(script);
			box.setOutputs("manual_result".split(" "));

			parent.showWaitPart(true);
			SmartAirService.Connect.getInstance().executeScript(box, new GwtCallbackWrapper<RScriptModel>(parent, true) {
				@Override
				public void onSuccess(RScriptModel result) {

					List<String> list = new ArrayList<String>(Arrays.asList(result.getOutputVarstoString().get(0).split("\t")));
					lstFilters.clear();
					if(list.get(0).trim().equals("1")){
						for(Filter filter : getNumFilters()){
							lstFilters.addItem(filter.getLabel(), filter.toString());
						}
					} else {
						for(Filter filter : getQualFilters()){
							lstFilters.addItem(filter.getLabel(), filter.toString());
						}
					}
				}
			}.getAsyncCallback());
		}
		
	}
	
	@UiHandler("btnDel")
	public void onDelClick(ClickEvent event) {
		this.removeFromParent();
	}

	public String getValue() {
		return txtValue.getText();
	}
	
	public String getColumn() {
		return lstColumns.getItemText(lstColumns.getSelectedIndex());
	}
	
	public String getFilterName() {
		return lstFilters.getValue(lstFilters.getSelectedIndex());
	}
	
	public String getRequest() {
		String result ="";
		switch(getSelectedFilter()){
		case EQUAL:
			result = getColumn() + "== '" + getValue() + "'";
			break;
		case CONTAIN:
			result = "grepl('"+ getValue() +"', " + getColumn() + ")";
			break;
		case NOTCONTAIN:
			result = "!grepl('"+ getValue() +"', " + getColumn() + ")";
			break;
		case EQUALNUM:
			result = getColumn() + "== " + getValue();
			break;
		case INF:
			result = getColumn() + "< " + getValue();
			break;
		case SUP:
			result = getColumn() + "> " + getValue();
			break;
		}
		return result;
	}
	
	 public List<Filter> getNumFilters() {
    	List<Filter> result = new ArrayList<Filter>();
    	result.add(Filter.EQUALNUM);
    	result.add(Filter.INF);
    	result.add(Filter.SUP);
        return result;
    }
	    
    public List<Filter> getQualFilters() {
    	List<Filter> result = new ArrayList<Filter>();
    	result.add(Filter.EQUAL);
    	result.add(Filter.CONTAIN);
    	result.add(Filter.NOTCONTAIN);
        return result;
    }
    
    public Filter getSelectedFilter() {
    	for(Filter filter : Filter.values()){
    		if(filter.equalsName(getFilterName())){
    			return filter;
    		}
    	}
        return null;
    }
    
    public boolean isComplete(){
    	return lstColumns.getSelectedIndex() !=0 && !txtValue.equals("");
    }
}