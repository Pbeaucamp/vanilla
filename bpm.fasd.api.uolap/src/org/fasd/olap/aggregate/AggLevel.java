package org.fasd.olap.aggregate;

import org.fasd.datasource.DataObjectItem;
import org.fasd.olap.OLAPLevel;

public class AggLevel{
	private OLAPLevel lvl;
	private DataObjectItem column;
	private String levelId;
	private String columnId;
	
	public DataObjectItem getColumn() {
		return column;
	}
	public void setColumn(DataObjectItem column) {
		this.column = column;
	}
	public String getLevelId() {
		return levelId;
	}
	public void setLevelId(String levelId) {
		this.levelId = levelId;
	}
	public OLAPLevel getLvl() {
		return lvl;
	}
	public void setLvl(OLAPLevel lvl) {
		this.lvl = lvl;
	}
	public void setColumnId(String id){
		this.columnId = id;
	}
	public String getColumnId(){
		return columnId;
	}
	
}
