package org.fasd.datasource;

import java.util.ArrayList;
import java.util.List;


public class DataInline {
	private List<DataRow> datas = new ArrayList<DataRow>();
	private DataObject table;
	private String tableId = ""; //$NON-NLS-1$
	
	
	public String getTableId() {
		return tableId;
	}

	public void setTableId(String tableId) {
		this.tableId = tableId;
	}

	public void setDataObject(DataObject table){
		this.table = table;
	}
	
	public DataObject getDataObject(){
		return table;
	}
	
	
	
	public void addRow(DataRow row){
		datas.add(row);
	}
	
	public void removeRow(DataRow row){
		datas.remove(row);
	}
	
	public String getFAXML(){
		StringBuffer buf = new StringBuffer();
		
		buf.append("        <inline-datas>\n"); //$NON-NLS-1$
		for(DataRow r : datas){
			buf.append(r.getFAXML());
		}
		buf.append("        </inline-datas>\n"); //$NON-NLS-1$
		
		return buf.toString();
	}

	public String getXML(){
		StringBuffer buf = new StringBuffer();
		
		buf.append("                <InlineTable alias=\"" + table.getName() + "\">\n"); //$NON-NLS-1$ //$NON-NLS-2$
		buf.append("                    <ColumnDefs>\n"); //$NON-NLS-1$
		for(DataObjectItem i : table.getColumns()){
			buf.append("                        <ColumnDef name=\"" + i.getName() + "\" type=\"" + i.getSqlType() + "\"/>\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
		buf.append("                    </ColumnDefs>\n"); //$NON-NLS-1$
		buf.append("                    <Rows>\n"); //$NON-NLS-1$
		
		for(DataRow d : datas){
			buf.append(d.getXML());
		}
		
		buf.append("                    </Rows>\n"); //$NON-NLS-1$
		buf.append("                </InlineTable>\n"); //$NON-NLS-1$
		return buf.toString();
	}
	
	public List<DataRow> getRows(){
		return datas;
	}
	
}
