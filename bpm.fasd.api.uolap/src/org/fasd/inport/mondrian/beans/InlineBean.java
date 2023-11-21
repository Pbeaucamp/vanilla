package org.fasd.inport.mondrian.beans;

import java.util.ArrayList;
import java.util.List;

import org.fasd.datasource.DataInline;

public class InlineBean {
	private DataInline datas;
	private String alias;
	private List<ColDef> cols = new ArrayList<ColDef>();
	
	public void addColDef(ColDef def){
		cols.add(def);
	}
	public List<ColDef> getColDef(){
		return cols;
	}





	public String getAlias() {
		return alias;
	}



	public void setAlias(String alias) {
		this.alias = alias;
	}



	public DataInline getDatas() {
		return datas;
	}



	public void setDatas(DataInline datas) {
		this.datas = datas;
	}

}
