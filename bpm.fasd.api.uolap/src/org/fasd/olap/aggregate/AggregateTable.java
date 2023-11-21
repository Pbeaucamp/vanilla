package org.fasd.olap.aggregate;

import java.util.ArrayList;
import java.util.List;

import org.fasd.datasource.DataObject;
import org.fasd.datasource.DataObjectItem;
import org.fasd.olap.OLAPElement;
import org.fasd.olap.OLAPRelation;

public class AggregateTable extends OLAPElement{
	private DataObject table;
	private List<AggMeasure> aggMes = new ArrayList<AggMeasure>();
	private List<AggLevel> aggLvl = new ArrayList<AggLevel>();
	private List<AggPattern> aggPat = new ArrayList<AggPattern>();
	
	private String tableId; 
	private DataObjectItem factCountColumn;
	private String factCountItemId;
	
	private static int counter = 0;
	
	public static void resetCounter(){
		counter =0;
	}
	
	public AggregateTable(){
		counter++;
		id = Integer.toString(counter);
	}

	

	public DataObject getTable() {
		return table;
	}

	public void setTable(DataObject table) {
		this.table = table;
	}

	public List<AggLevel> getAggLvl() {
		return aggLvl;
	}

	public List<AggMeasure> getAggMes() {
		return aggMes;
	}
	
	
	public void addAggMeasure(AggMeasure aggM){
		if (!aggMes.contains(aggM))
			aggMes.add(aggM);
	}
	
	public void removeAggMeasure(AggMeasure aggM){
		aggMes.remove(aggM);
	}
	
	public void addAggLevel(AggLevel aggL){
		if (!aggLvl.contains(aggL))
			aggLvl.add(aggL);
	}
	
	public void removeAggLevel(AggLevel aggL){
		aggLvl.remove(aggL);
	}
	
	
	public String getXML(List<OLAPRelation> foreignKeys){
		StringBuffer buf = new StringBuffer();
		buf.append("            <AggName name=\"" + table.getName() + "\">\n");
		buf.append("                <AggFactCount column=\"" + factCountColumn.getOrigin() +"\"/>\n");
		//TODO : foreign from relation
		
		for(OLAPRelation r : foreignKeys){
			if (r.getRightObject() == table){
				buf.append("                <AggForeignKey factColumn=\"" + r.getLeftObjectItem().getOrigin() + "\" aggColumn=\"" + r.getRightObjectItem().getOrigin() +"\"/>\n");
			}
			else if (r.getLeftObject() == table){
				buf.append("                <AggForeignKey factColumn=\"" + r.getRightObjectItem().getOrigin() + "\" aggColumn=\"" + r.getLeftObjectItem().getOrigin() +"\"/>\n");
			}
		}
		
		
		for(AggMeasure m : aggMes){
			buf.append("                <AggMeasure name=\"[Measures].[" + m.getMes().getName() + "]\" column=\"" +m.getColumn().getOrigin() + "\"/>\n");
		}
		for(AggLevel l : aggLvl){
			String mondrianName= "[" + l.getLvl().getParent().getParent().getName() + "].[";
			
			if (!l.getLvl().getParent().getName().trim().equals(""))
				mondrianName += l.getLvl().getParent().getName() + "].[";
			mondrianName += l.getLvl().getName() + "]";
			
			buf.append("                <AggLevel name=\"" + mondrianName + "\" column=\"" +l.getColumn().getOrigin() + "\"/>\n");
		}
		buf.append("            </AggName>\n");
		return buf.toString();
	}
	
	public String getFAXML(){
		StringBuffer buf = new StringBuffer();
		buf.append("        <AggregateTable>\n");
		buf.append("            <id>" + getId() + "</id>\n");
		buf.append("            <dataobject-id>" + table.getId() + "</dataobject-id>\n");
		buf.append("            <factCountItem-id>" + factCountColumn.getId() + "</factCountItem-id>\n");
		
		for(AggMeasure m : aggMes){
			buf.append("            <AggMeasure>\n");
			buf.append("                <measure-id>" + m.getMes().getId() + "</measure-id>\n");
			buf.append("                <column-id>" + m.getColumn().getId() + "</column-id>\n");
			buf.append("            </AggMeasure>\n");
		}
		
		for(AggLevel l : aggLvl){
			buf.append("            <AggLevel>\n");
			buf.append("                <level-id>" + l.getLvl().getId() + "</level-id>\n");
			buf.append("                <column-id>" + l.getColumn().getId() + "</column-id>\n");
			buf.append("            </AggLevel>\n");
		}
		
		buf.append("        </AggregateTable>\n");
		return buf.toString();
	}

	public String getTableId() {
		return tableId;
	}

	public void setTableId(String tableId) {
		this.tableId = tableId;
	}


	public void setId(String id) {
		this.id = id;
	}

	public DataObjectItem getFactCountColumn() {
		return factCountColumn;
	}

	public void setFactCountColumn(DataObjectItem factCountColumn) {
		this.factCountColumn = factCountColumn;
	}

	public String getFactCountItemId() {
		return factCountItemId;
	}

	public void setFactCountItemId(String factCountItemId) {
		this.factCountItemId = factCountItemId;
	}
	
	public List<AggPattern> getPatterns(){
		return aggPat;
	}
	
	public void addAggPattern(AggPattern p){
		aggPat.add(p);
	}
}
