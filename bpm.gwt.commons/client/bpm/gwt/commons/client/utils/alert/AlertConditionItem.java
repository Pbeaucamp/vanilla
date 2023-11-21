package bpm.gwt.commons.client.utils.alert;

import java.util.List;

import bpm.gwt.commons.client.custom.LabelTextBox;
import bpm.gwt.commons.client.utils.VanillaCSS;
import bpm.vanilla.platform.core.beans.alerts.AlertConstants;
import bpm.vanilla.platform.core.beans.alerts.Condition;
import bpm.vanilla.platform.core.beans.alerts.ConditionKpi;
import bpm.vanilla.platform.core.beans.alerts.ConditionRepository;
import bpm.vanilla.platform.core.beans.data.DataColumn;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.Widget;

public class AlertConditionItem extends Composite {

	private static AlertConditionItemUiBinder uiBinder = GWT.create(AlertConditionItemUiBinder.class);

	interface AlertConditionItemUiBinder extends UiBinder<Widget, AlertConditionItem> {
	}

	interface MyStyle extends CssResource {
		
	}
	
	public enum ConditionType {
		REPOSITORY, KPI;
	}

	@UiField
	MyStyle style;

	@UiField
	HTMLPanel panelRepo, panelKpi;
	
	@UiField
	LabelTextBox txtRepoValue2;

	@UiField
	ListBox lstRepoColumns1, lstRepoFilters, lstRepoColumns2,
			lstKpiType, lstKpiField1, lstKpiFilters, lstKpiField2;
	
	@UiField
	RadioButton selCol, selVal;
	
	@UiField
	Image btnDel;

	private IConditionParent parent;
	private List<DataColumn> columns;
	private ConditionType type;

	public AlertConditionItem(IConditionParent parent, List<DataColumn> columns, ConditionType type) {
		initWidget(uiBinder.createAndBindUi(this));
		this.columns = columns;
		this.parent = parent;
		this.type = type;
		
		if(type.equals(ConditionType.REPOSITORY)){
			panelRepo.setVisible(true);
			panelKpi.setVisible(false);
			initPanelRepo();
		} else {
			panelRepo.setVisible(false);
			panelKpi.setVisible(true);
			initPanelKpi();
		}
	}
	
	private void initPanelRepo(){
		lstRepoColumns1.addItem("");
		lstRepoColumns2.addItem("");
		for(DataColumn col : this.columns){
			lstRepoColumns1.addItem(col.getColumnLabel());
			lstRepoColumns2.addItem(col.getColumnLabel());
		}
		
		lstRepoFilters.addItem(AlertConstants.EQUALS);
		lstRepoFilters.addItem(AlertConstants.GREATER_THAN);
		lstRepoFilters.addItem(AlertConstants.LESS_THAN);
		
		lstRepoColumns1.addStyleName(VanillaCSS.COMMONS_LISTBOX);
		lstRepoFilters.addStyleName(VanillaCSS.COMMONS_LISTBOX);
		lstRepoColumns2.addStyleName(VanillaCSS.COMMONS_LISTBOX);
		
		selCol.setName("selCol"+ System.currentTimeMillis());
		selVal.setName("selVal"+ System.currentTimeMillis());
		selCol.setValue(true);
		lstRepoColumns2.setVisible(true);
		txtRepoValue2.setVisible(false);
		
		txtRepoValue2.addDomHandler(new KeyUpHandler() {
			
			@Override
			public void onKeyUp(KeyUpEvent event) {
				AlertConditionItem.this.parent.updateConditions();
			}
		}, KeyUpEvent.getType());
	}
	
	private void initPanelKpi(){
		for(String typ : ConditionKpi.ALERT_TYPES){
			lstKpiType.addItem(typ);
		}
		
		for(String field : ConditionKpi.FIELDS){
			lstKpiField1.addItem(field);
			lstKpiField2.addItem(field);
		}
		
		for(String op : ConditionKpi.OPERATORS){
			lstKpiFilters.addItem(op);
		}
		
		lstKpiField1.addStyleName(VanillaCSS.COMMONS_LISTBOX);
		lstKpiField2.addStyleName(VanillaCSS.COMMONS_LISTBOX);
		lstKpiFilters.addStyleName(VanillaCSS.COMMONS_LISTBOX);
		lstKpiType.addStyleName(VanillaCSS.COMMONS_LISTBOX);
	}

	@UiHandler("selCol")
	public void onColumnSelect(ClickEvent event) {
		selVal.setValue(false);
		lstRepoColumns2.setVisible(true);
		txtRepoValue2.setVisible(false);
		parent.updateConditions();
	}
	
	@UiHandler("lstRepoColumns1")
	public void onColumn1Change(ChangeEvent event) {
		parent.updateConditions();
	}
	
	@UiHandler("lstRepoColumns2")
	public void onColumn2Change(ChangeEvent event) {
		parent.updateConditions();
	}
	
	@UiHandler("lstKpiField1")
	public void onField1Change(ChangeEvent event) {
		parent.updateConditions();
	}
	
	@UiHandler("lstKpiField2")
	public void onField2Change(ChangeEvent event) {
		parent.updateConditions();
	}
	
	@UiHandler("lstKpiFilters")
	public void onKpiFiltersChange(ChangeEvent event) {
		parent.updateConditions();
	}
	
	@UiHandler("lstKpiType")
	public void onKpiTypeChange(ChangeEvent event) {
		if(lstKpiType.getSelectedIndex() == ConditionKpi.VALUE_TYPE){ //0
			lstKpiField1.clear();
			lstKpiField2.clear();
			lstKpiFilters.clear();
			
			for(String field : ConditionKpi.FIELDS){
				lstKpiField1.addItem(field);
				lstKpiField2.addItem(field);
			}
			
			for(String op : ConditionKpi.OPERATORS){
				lstKpiFilters.addItem(op);
			}
			
			lstKpiField2.setVisible(true);
			lstKpiField1.setVisible(true);
		} else if(lstKpiType.getSelectedIndex() == ConditionKpi.STATE_TYPE) { //1
			lstKpiField2.setVisible(false);
			lstKpiField1.setVisible(false);
			lstKpiFilters.clear();
			
			for(String st : ConditionKpi.STATE_TYPES){
				lstKpiFilters.addItem(st);
			}
			
		} else { //2 missing type
			lstKpiField2.setVisible(false);
			lstKpiField1.setVisible(false);
			lstKpiFilters.clear();
			
			for(String mi : ConditionKpi.MISSING_TYPES){
				lstKpiFilters.addItem(mi);
			}
		}
		
		parent.updateConditions();
	}
	
	@UiHandler("selVal")
	public void onValueSelect(ClickEvent event) {
		selCol.setValue(false);
		lstRepoColumns2.setVisible(false);
		txtRepoValue2.setVisible(true);
		parent.updateConditions();
	}
	
	@UiHandler("btnDel")
	public void onDelClick(ClickEvent event) {
		this.removeFromParent();
		parent.updateConditions();
	}

	public String getColumn1() {
		return lstRepoColumns1.getItemText(lstRepoColumns1.getSelectedIndex());
	}
	
	public String getColumn2() {
		if(selCol.getValue()){
			return lstRepoColumns1.getItemText(lstRepoColumns1.getSelectedIndex());
		} else {
			return txtRepoValue2.getText();
		}
	}
	
	public boolean isColumn2Field() {
		return selCol.getValue();
	}
	
	public String getFilterName() {
		return lstRepoFilters.getValue(lstRepoFilters.getSelectedIndex());
	}
	
	public String getKpiField1() {
		return lstKpiField1.getValue(lstKpiField1.getSelectedIndex());
	}
	
	public String getKpiField2() {
		return lstKpiField2.getValue(lstKpiField2.getSelectedIndex());
	}
	
	public String getKpiType() {
		return  lstKpiType.getValue(lstKpiType.getSelectedIndex());
	}
	
	public String getKpiFilter() {
		return lstKpiFilters.getValue(lstKpiFilters.getSelectedIndex());
	}
	
	public void setColumn1(String name) {
		for(int i=0; i<lstRepoColumns1.getItemCount(); i++){
			if(lstRepoColumns1.getValue(i).equals(name)){
				lstRepoColumns1.setSelectedIndex(i);
			}
		}
	}
	
	public void setColumn2(boolean isField, String name) {
		if(isField){
			selCol.setValue(true);
			
			for(int i=0; i<lstRepoColumns2.getItemCount(); i++){
				if(lstRepoColumns2.getValue(i).equals(name)){
					lstRepoColumns2.setSelectedIndex(i);
				}
			}
		} else {
			selVal.setValue(true);
			txtRepoValue2.setText(name);
		}
		onValueSelect(null);
	}
	
	public void setFilterName(String name) {
		for(int i=0; i<lstRepoFilters.getItemCount(); i++){
			if(lstRepoFilters.getValue(i).equals(name)){
				lstRepoFilters.setSelectedIndex(i);
			}
		}
	}
	
	public void setKpiType(String type) {
		for(int i=0; i<lstKpiType.getItemCount(); i++){
			if(lstKpiType.getValue(i).equals(type)){
				lstKpiType.setSelectedIndex(i);
			}
		}
		onKpiTypeChange(null);
	}
	
	public void setKpiFilter(String op) {
		for(int i=0; i<lstKpiFilters.getItemCount(); i++){
			if(lstKpiFilters.getValue(i).equals(op)){
				lstKpiFilters.setSelectedIndex(i);
			}
		}
		onKpiFiltersChange(null);
	}
	
	public void setKpiField1(String field) {
		for(int i=0; i<lstKpiField1.getItemCount(); i++){
			if(lstKpiField1.getValue(i).equals(field)){
				lstKpiField1.setSelectedIndex(i);
			}
		}
		onField1Change(null);
	}
	
	public void setKpiField2(String field) {
		for(int i=0; i<lstKpiField2.getItemCount(); i++){
			if(lstKpiField2.getValue(i).equals(field)){
				lstKpiField2.setSelectedIndex(i);
			}
		}
		onField2Change(null);
	}
	
	public Condition getCondition() {
		Condition cond = new Condition();
		switch(type){
		case KPI:
			if(getKpiType().equals(ConditionKpi.ALERT_TYPES[ConditionKpi.VALUE_TYPE])){
				cond.setLeftOperand(getKpiField1());
				cond.setRightOperand(getKpiField2());
				cond.setOperator(getKpiFilter());
				ConditionKpi ck = new ConditionKpi();
				cond.setConditionObject(ck);
				ck.setType(getKpiType());
			} else if(getKpiType().equals(ConditionKpi.ALERT_TYPES[ConditionKpi.MISSING_TYPE])){
				ConditionKpi ck = new ConditionKpi();
				ck.setType(getKpiType());
				ck.setMissingType(getKpiFilter());
				cond.setConditionObject(ck);
			} else if(getKpiType().equals(ConditionKpi.ALERT_TYPES[ConditionKpi.STATE_TYPE])){
				ConditionKpi ck = new ConditionKpi();
				ck.setType(getKpiType());
				ck.setStateType(getKpiFilter());
				cond.setConditionObject(ck);
			}
			
			break;
		case REPOSITORY:
			cond.setLeftOperand(getColumn1());
			cond.setRightOperand(getColumn2());
			cond.setOperator(getFilterName());
			ConditionRepository cr = new ConditionRepository();
			cr.setRightOperandField(isColumn2Field());
			cond.setConditionObject(cr);
			break;
		}
		
		return cond;
	}
}