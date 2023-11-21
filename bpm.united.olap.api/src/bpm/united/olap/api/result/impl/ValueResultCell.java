package bpm.united.olap.api.result.impl;

import bpm.united.olap.api.data.IExternalQueryIdentifier;
import bpm.united.olap.api.result.DrillThroughIdentifier;

public class ValueResultCell extends ResultCellImpl {

	private String formatedValue;
	private Double value;
	private String drillthroughId;
	private DrillThroughIdentifier dataCellIdentifier;
	private IExternalQueryIdentifier externalIdentifier;
	
	public ValueResultCell(Double value, String formatedValue, String id) {
		this.value = value;
		this.formatedValue = formatedValue;
		this.drillthroughId = id;
	}
	
	public ValueResultCell(Double value, String formatedValue, DrillThroughIdentifier dataCellIdentifier, String drillId) {
		this.formatedValue = formatedValue;
		this.value = value;
		this.drillthroughId = drillId;
		this.dataCellIdentifier = dataCellIdentifier;
	}
	
	public ValueResultCell(Double value, String formatedValue, DrillThroughIdentifier drillThroughIdentifier, String drillId, IExternalQueryIdentifier externalQueryIdentifier) {
		this.formatedValue = formatedValue;
		this.value = value;
		this.drillthroughId = drillId;
		this.dataCellIdentifier = drillThroughIdentifier;
		this.externalIdentifier = externalQueryIdentifier;
	}

	public ValueResultCell(Double value, String formatedValue, String id, IExternalQueryIdentifier externalQueryIdentifier) {
		this.formatedValue = formatedValue;
		this.value = value;
		this.drillthroughId = id;
		this.externalIdentifier = externalQueryIdentifier;
	}

	public Double getValue() {
		return value;
	}

	public String getFormatedValue(){
		return formatedValue;
	}
	
	@Override
	public String getHtml() {
		if(getFormatedValue() != null && !getFormatedValue().equalsIgnoreCase("")) {
			return "<td class=\"gridItem gridItemValue\">" + getFormatedValue() + "</td>\n";
		}
		return "<td class=\"gridItem gridItemValue\">&nbsp;</td>\n"; 
	}

	@Override
	public String getType() {
		return "value";
	}

	public void setDrillthroughId(String drillthroughId) {
		this.drillthroughId = drillthroughId;
	}

	public String getDrillthroughId() {
		return drillthroughId;
	}
	
	public DrillThroughIdentifier getDataCellId(){
		return dataCellIdentifier;
	}

	public void setExternalIdentifier(IExternalQueryIdentifier externalIdentifier) {
		this.externalIdentifier = externalIdentifier;
	}

	public IExternalQueryIdentifier getExternalIdentifier() {
		return externalIdentifier;
	}
}
