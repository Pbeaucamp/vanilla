package bpm.sqldesigner.api.document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bpm.sqldesigner.api.model.Column;
import bpm.sqldesigner.api.model.DatabaseCluster;
import bpm.sqldesigner.api.model.LinkForeignKey;
import bpm.sqldesigner.api.model.Node;
import bpm.sqldesigner.api.model.Schema;
import bpm.sqldesigner.api.model.Table;

public class DocumentSnapshot extends Node{
	private DatabaseCluster dbCluster;
	
	private HashMap<Table, Table> tables = new HashMap<Table, Table>();
	
	private Schema schema;
	private float scale = 1.0f;
	
	
	/**
	 * @return the scale
	 */
	public float getScale() {
		return scale;
	}

	/**
	 * @param scale the scale to set
	 */
	public void setScale(float scale) {
		this.scale = scale;
	}

	public Schema getSchema(){
		return schema;
	}
	
	public void setSchema(Schema schema){
		this.schema = schema;
	}

	public void rebuildLinks(){
		for(Table table : getTables()){
			for(Column c : schema.getTable(table.getName()).getColumns().values()){
				if (!c.getSourceForeignKeys().isEmpty() ){
					for(LinkForeignKey k : c.getSourceForeignKeys()){
						Table tableSource = tables.get(k.getSource().getTable());
						if (tableSource == null){
							continue;
						}
						Column colSource = tableSource.getColumn(k.getSource().getName());
						
						if (colSource == null){
							continue;
						}
						
						Table tableTarget = tables.get(k.getTarget().getTable());
						if (tableTarget == null){
							continue;
						}
						Column colTarget = tableTarget.getColumn(k.getTarget().getName());
						if (colTarget == null){
							continue;
						}
						
						LinkForeignKey lk = new LinkForeignKey(colSource, colTarget);
						colTarget.addSourceForeignKey(lk);
						colSource.setTargetPrimaryKey(lk);
					}
				}
			}
		}
		
	}
	
	public void addTable(final Table table){
		if (table != null && !tables.keySet().contains(table)){
			final Table t = new Table(table);
			tables.put(table, t);

			
		}
		
	}
	

	@Override
	public Object[] getChildren() {
		
		return getTables().toArray(new Object[getTables().size()]);
	}
	
	public void removeTable(Table table){
		tables.remove(table);
	}
	
	public DatabaseCluster getDatabaseCluster(){
		return dbCluster;
	}
	
	public void setDatabaseCluster(DatabaseCluster cluster){
		this.dbCluster = cluster;
//		tables.clear();
	}
	
	

	public List<Table> getTables() {
		return new ArrayList<Table>(tables.values());
	}
	
	@Override
	public Node getParent() {
		
		return getDatabaseCluster();
	}
}
