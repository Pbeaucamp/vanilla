package bpm.fasd.client.dwh.importer.converter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.fasd.datasource.DataObject;
import org.fasd.datasource.DataObjectItem;
import org.fasd.datasource.DataSource;
import org.fasd.datasource.DataSourceConnection;
import org.fasd.olap.FAModel;
import org.fasd.olap.OLAPRelation;
import org.fasd.olap.OLAPSchema;

import bpm.fasd.client.dwh.importer.Messages;
import bpm.model.converter.core.IModelConverter;
import bpm.sqldesigner.api.document.DocumentSnapshot;
import bpm.sqldesigner.api.model.LinkForeignKey;
import bpm.sqldesigner.api.model.SchemaNull;
import bpm.sqldesigner.api.model.Table;
import bpm.studio.jdbc.management.model.DriverInfo;
import bpm.studio.jdbc.management.model.ListDriver;

public class DwhFasdConverter implements IModelConverter<DocumentSnapshot> {

	private static final String name = Messages.DwhFasdConverter_0;
	private static final String description = Messages.DwhFasdConverter_1;
	
	
	private HashMap<Table, String> tablesType;
	
	
	public DwhFasdConverter() {}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Class<?> getTargetClass() {
		return FAModel.class;
	}

	

	@Override
	public void configure(Object configurationObject) throws Exception {
		if (configurationObject instanceof HashMap){
			tablesType = (HashMap<Table, String>)configurationObject;
		}
		
		
		
		
	}
	
	@Override
	public Object convert(DocumentSnapshot dwhView) throws Exception {
		FAModel fasdModel = new FAModel();
		
		fasdModel.setOLAPSchema(new OLAPSchema());
		
		fasdModel.getDocumentProperties().setDescription(Messages.DwhFasdConverter_2);
		
		/*
		 * create the Connection and Link it to its DataSource
		 */
		
		
		DriverInfo inf = ListDriver.getInstance(Platform.getInstallLocation().getURL().getPath() + "/resources/driverjdbc.xml").getInfo(dwhView.getDatabaseConnection().getDriverName()); //$NON-NLS-1$
		
		StringBuffer buf = new StringBuffer();
		buf.append(inf.getUrlPrefix());
		buf.append(dwhView.getDatabaseConnection().getHost());
		
		if (dwhView.getDatabaseConnection().getPort() != null && !dwhView.getDatabaseConnection().getPort().isEmpty()){
			buf.append(":"); //$NON-NLS-1$
			buf.append(dwhView.getDatabaseConnection().getPort());
		}
		buf.append("/"); //$NON-NLS-1$
		
		buf.append(dwhView.getDatabaseConnection().getDataBaseName());
		String schemaName = null;
		if (!(dwhView.getSchema() instanceof SchemaNull)){
			schemaName = dwhView.getSchema().getName();
		}
		
		DataSourceConnection connection = new DataSourceConnection();
		connection.setName("Default"); //$NON-NLS-1$
		connection.setDriver(inf.getClassName());
		connection.setSchemaName(schemaName);
		connection.setPass(dwhView.getDatabaseConnection().getPassword());
		connection.setUrl(buf.toString());
		connection.setUser(dwhView.getDatabaseConnection().getLogin());
//		connection.setDriverFile(driverFile)
		DataSource dataSource = new DataSource(dwhView.getName(), connection);
		connection.setParent(dataSource);
		
		fasdModel.addDataSource(dataSource);

		List<DataObject> tables = connection.getTables(schemaName);
		
		List<DataObject> toRemove = new ArrayList<DataObject>();
		for(DataObject o : dataSource.getDataObjects()){
			boolean found = false;
			for(Table t : dwhView.getTables()){
				if (t.getName().equals(o.getName())){
					found = true;
				}
			}
			if (! found){
				toRemove.add(o);
			}
		}
		for(DataObject o : toRemove){
			dataSource.removeDataObject(o);
		}
		
		tables = dataSource.getDataObjects();
		/*
		 * apply tablesTypes
		 */
		for(DataObject o : tables){
			
			o.setDataObjectType(getTableType(o.getName()));
			
		}
		
		/*
		 * add the tables
		 */
		List<LinkForeignKey> foreignKeys = new ArrayList<LinkForeignKey>();
		
		for(bpm.sqldesigner.api.model.Table table : dwhView.getTables()){
			for(bpm.sqldesigner.api.model.Column column : table.getColumns().values()){
				foreignKeys.addAll(column.getSourceForeignKeys());
			}
			
		}
		/*
		 * add Relations
		 */
		
		
		for(LinkForeignKey lk : foreignKeys){
			DataObject sourceDataStream = dataSource.findDataObjectNamed(lk.getSource().getTable().getName());
			DataObjectItem sourceColumn = sourceDataStream.findItemNamed( lk.getSource().getName());
			
			DataObject targetDataStream = dataSource.findDataObjectNamed(lk.getTarget().getTable().getName());
			DataObjectItem targetColumn = targetDataStream.findItemNamed(lk.getTarget().getName());
			
			OLAPRelation rel = new OLAPRelation();
			
			if (DataObject.TYPES[1].equals(sourceDataStream.getDataObjectType())){
				rel.setLeftObjectItem(sourceColumn);
				rel.setRightObjectItem(targetColumn);
			}
			else if (DataObject.TYPES[1].equals(sourceDataStream.getDataObjectType())){
				rel.setRightObjectItem(sourceColumn);
				rel.setLeftObjectItem(targetColumn);
			}
			else{
				rel.setLeftObjectItem(sourceColumn);
				rel.setRightObjectItem(targetColumn);
			}
			
			
			
			fasdModel.addRelation(rel);
			
		}
	
		return fasdModel;
	}
	
	
	private String getTableType(String tableName){
		if (tablesType != null){
			for(Table t : tablesType.keySet()){
				if (t.getName().equals(tableName)){
					return tablesType.get(t);
				}
			}
		}
		
		
		return ""; //$NON-NLS-1$
	}
}
