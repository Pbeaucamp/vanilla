package bpm.metadata.client.dwh.importer.converters;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import bpm.metadata.MetaData;
import bpm.metadata.layer.business.BusinessModel;
import bpm.metadata.layer.business.BusinessPackage;
import bpm.metadata.layer.business.IBusinessModel;
import bpm.metadata.layer.business.IBusinessPackage;
import bpm.metadata.layer.business.IBusinessTable;
import bpm.metadata.layer.business.SQLBusinessTable;
import bpm.metadata.layer.logical.AbstractDataSource;
import bpm.metadata.layer.logical.IDataStream;
import bpm.metadata.layer.logical.IDataStreamElement;
import bpm.metadata.layer.logical.Join;
import bpm.metadata.layer.logical.Relation.Cardinality;
import bpm.metadata.layer.logical.sql.FactorySQLDataSource;
import bpm.metadata.layer.logical.sql.SQLDataSource;
import bpm.metadata.layer.logical.sql.SQLRelation;
import bpm.metadata.layer.physical.IConnection;
import bpm.metadata.layer.physical.sql.FactorySQLConnection;
import bpm.metadata.layer.physical.sql.SQLColumn;
import bpm.metadata.layer.physical.sql.SQLConnection;
import bpm.metadata.layer.physical.sql.SQLTable;
import bpm.model.converter.core.IModelConverter;
import bpm.sqldesigner.api.document.DocumentSnapshot;
import bpm.sqldesigner.api.model.LinkForeignKey;
import bpm.sqldesigner.api.model.SchemaNull;

public class DwhMetaDataConverter implements IModelConverter<DocumentSnapshot> {

	private static final String name = "SqlDesigner DwhView to Vanilla MetaData";
	private static final String description = "Create a Vanilla MetaData until the Business Package";
	
	
	public static final String PROP_BUSINESS_MODEL_NAME = "bpm.metadata.client.dwh.importer.converters.DwhMetaDataConverter.businessModelName";
	public static final String PROP_BUSINESS_PACKAGE_NAME = "bpm.metadata.client.dwh.importer.converters.DwhMetaDataConverter.businessPackageName";
	public static final String PROP_VANILLA_GROUP_NAMES = "bpm.metadata.client.dwh.importer.converters.DwhMetaDataConverter.groupNames";
	
	private String businessModelName;
	private String businessPackageName;
	private List<String> securityGroupNames;
	
	
	public DwhMetaDataConverter() {}

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
		return MetaData.class;
	}

	

	@Override
	public void configure(Object configurationObject) throws Exception {
		if (!(configurationObject instanceof Map)){
			throw new Exception("A Map is expected to configure the Converter");
		}
		
		Map map = (Map)configurationObject;
		
		try{
			businessModelName = (String) map.get(PROP_BUSINESS_MODEL_NAME);
		}catch(Exception e){
			throw new Exception("Unable to find BusinessModel name in configurationObject : " + e.getMessage(), e);
		}
		

		try{
			businessPackageName = (String) map.get(PROP_BUSINESS_PACKAGE_NAME);
		}catch(Exception e){
			throw new Exception("Unable to find BusinessPackage name in configurationObject : " + e.getMessage(), e);
		}
		
		try{
			securityGroupNames = (List)map.get(PROP_VANILLA_GROUP_NAMES);
		}catch(Exception e){
			throw new Exception("Unable to find VanillaGroup names in configurationObject : " + e.getMessage(), e);
		}
		
	}
	
	@Override
	public Object convert(DocumentSnapshot dwhView) throws Exception {
		try{
			MetaData fmdtModel = new MetaData();
			fmdtModel.getProperties().setDescription("Automatically generated from a DwhView");
			
			/*
			 * create the Connection and Link it to its DataSource
			 */
			SQLConnection connection = FactorySQLConnection.getInstance().createConnection(
					dwhView.getDatabaseConnection().getDriverName(), 
					dwhView.getDatabaseConnection().getHost(), 
					dwhView.getDatabaseConnection().getPort(), 
					dwhView.getDatabaseConnection().getDataBaseName(), 
					dwhView.getDatabaseConnection().getLogin(), 
					dwhView.getDatabaseConnection().getPassword(), 
					dwhView.getSchema() instanceof SchemaNull ? null : dwhView.getSchema().getName(),
					false, null);
			
			SQLDataSource dataSource = FactorySQLDataSource.getInstance().createDataSource(connection);
			dataSource.setName(dwhView.getName());
			
			fmdtModel.addDataSource(dataSource);
			
			/*
			 * add the tables
			 */
			List<LinkForeignKey> foreignKeys = new ArrayList<LinkForeignKey>();
			List<IDataStream> dataStreams = new ArrayList<IDataStream>();
			
			for(bpm.sqldesigner.api.model.Table table : dwhView.getTables()){
				
				SQLTable t = new SQLTable(connection, SQLTable.TABLE);
				t.setName(table.getName());
				if (!(dwhView.getSchema() instanceof SchemaNull)){
					t.setSchemaName(dwhView.getSchema().getName());
				}
				
				/*
				 * add the columns
				 */
				for(bpm.sqldesigner.api.model.Column column : table.getColumns().values()){
					SQLColumn col = new SQLColumn(t);
					col.setName(table.getName() + "." + column.getName());
					col.setSqlTypeCode(column.getType().getId());
					col.setSqlType(column.getTypeString());
					col.setClassName("java.lang.Object");
					
					t.addColumn(col);
					foreignKeys.addAll(column.getSourceForeignKeys());
				}
				IDataStream s = dataSource.add(t);
				s.setPositionX(table.getLayout()[0]);
				s.setPositionY(table.getLayout()[1]);
				dataStreams.add(s);
			}
			/*
			 * add Relations
			 */
			
			List<SQLRelation> relations = new ArrayList<SQLRelation>();
			for(LinkForeignKey lk : foreignKeys){
				IDataStream sourceDataStream = dataSource.getDataStreamNamed(lk.getSource().getTable().getName());
				IDataStreamElement sourceColumn = sourceDataStream.getElementNamed(lk.getSource().getTable().getName() + "." + lk.getSource().getName());
				
				IDataStream targetDataStream = dataSource.getDataStreamNamed(lk.getTarget().getTable().getName());
				IDataStreamElement targetColumn = targetDataStream.getElementNamed(lk.getTarget().getTable().getName() + "." + lk.getTarget().getName());
				
				SQLRelation r = null;
				
				for(SQLRelation _r : relations){
					if (_r.isUsingTable(sourceDataStream) && _r.isUsingTable(targetDataStream)){
						r = _r;
						break;
					}
				}
				
				if (r == null){
					r = new SQLRelation();
					relations.add(r);
					dataSource.addRelation(r);
					r.setCardinality(Cardinality.C_0_n);
				}
				
				boolean joinExists = false;
						
				for(Join j : r.getJoins()){
					if (j.getLeftElement() == targetColumn && j.getRightElement() == sourceColumn){
						joinExists = true;
						break;
					}
					
					if (j.getLeftElement() == sourceColumn && j.getRightElement() == targetColumn){
						joinExists = true;
						break;
					}
					
					
				}
				
				if (!joinExists){
					if (r.getLeftTable() == sourceDataStream){
						
						r.add(sourceColumn, targetColumn, Join.INNER);
					}
					else{
						r.add(targetColumn, sourceColumn, Join.INNER);
					}
					
				}
				
			}
			/*
			 * create BusinessModel 
			 */
			BusinessModel model = new BusinessModel();
			model.setName(businessModelName);
			
			
			for(IDataStream t : dataSource.getDataStreams()){
				SQLBusinessTable bt = new SQLBusinessTable(t.getName());
				for(IDataStreamElement c : t.getElements()){
					bt.addColumn(c);
				}
				
				model.addBusinessTable(bt);
				
			}
			
			BusinessPackage pack = new BusinessPackage();
			pack.setName(businessPackageName);
			
			for(IBusinessTable t : model.getBusinessTables()){
				pack.addBusinessTable(t);
			}
			model.addBusinessPackage(pack);
			
			fmdtModel.addBusinessModel(model);
			
			securizeFmdtModel(fmdtModel);
			return fmdtModel;
		}catch(Throwable ex){
			ex.printStackTrace();
			throw new Exception("Error when converting:" + ex.getMessage(), ex);
		}
		
	}
	
	private void securizeFmdtModel(MetaData model){
		for(String g : securityGroupNames){
			
			for(AbstractDataSource ds : model.getDataSources()){
				for(IConnection c : ds.getConnections()){
					ds.securizeConnection(c.getName(), g, true);
				}
				
				for(IDataStream st : ds.getDataStreams()){
					for(IDataStreamElement c : st.getElements()){
						c.setGranted(g, true);
						
					}
				}
			}
			
			for(IBusinessModel m : model.getBusinessModels()){
				((BusinessModel)m).setGranted(g, true);
				
				for(IBusinessTable t : ((BusinessModel)m).getBusinessTables()){
					((SQLBusinessTable)t).setGranted(g, true);
				}
				
				for(IBusinessPackage p : ((BusinessModel)m).getBusinessPackages("none")){
					((BusinessPackage)p).setGranted(g, true);
					
					
					
				}
			}
			
		}
	}

}
