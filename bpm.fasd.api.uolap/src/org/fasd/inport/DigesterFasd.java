package org.fasd.inport;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.digester3.Digester;
import org.fasd.datasource.DataInline;
import org.fasd.datasource.DataObject;
import org.fasd.datasource.DataObjectItem;
import org.fasd.datasource.DataObjectOda;
import org.fasd.datasource.DataRow;
import org.fasd.datasource.DataSource;
import org.fasd.datasource.DataSourceConnection;
import org.fasd.datasource.DataView;
import org.fasd.datasource.DatasourceOda;
import org.fasd.inport.converter.MondrianToUOlapConverter;
import org.fasd.olap.CubeView;
import org.fasd.olap.DocumentProperties;
import org.fasd.olap.DrillCube;
import org.fasd.olap.DrillReport;
import org.fasd.olap.DrillReportParameter;
import org.fasd.olap.FAModel;
import org.fasd.olap.NamedSet;
import org.fasd.olap.OLAPCube;
import org.fasd.olap.OLAPDimension;
import org.fasd.olap.OLAPDimensionGroup;
import org.fasd.olap.OLAPHierarchy;
import org.fasd.olap.OLAPLevel;
import org.fasd.olap.OLAPMeasure;
import org.fasd.olap.OLAPMeasureGroup;
import org.fasd.olap.OLAPRelation;
import org.fasd.olap.OLAPSchema;
import org.fasd.olap.OlapDynamicMeasure;
import org.fasd.olap.Parameter;
import org.fasd.olap.PreloadConfig;
import org.fasd.olap.Property;
import org.fasd.olap.SecurityProvider;
import org.fasd.olap.UserDefinedFunction;
import org.fasd.olap.aggregate.AggExclude;
import org.fasd.olap.aggregate.AggLevel;
import org.fasd.olap.aggregate.AggMeasure;
import org.fasd.olap.aggregate.AggPattern;
import org.fasd.olap.aggregate.AggregateTable;
import org.fasd.olap.aggregation.CalculatedAggregation;
import org.fasd.olap.aggregation.ClassicAggregation;
import org.fasd.olap.aggregation.IMeasureAggregation;
import org.fasd.olap.aggregation.LastAggregation;
import org.fasd.olap.virtual.VirtualCube;
import org.fasd.olap.virtual.VirtualDimension;
import org.fasd.olap.virtual.VirtualMeasure;
import org.fasd.security.SecurityDim;
import org.fasd.security.SecurityGroup;
import org.fasd.security.View;
import org.fasd.xmla.XMLACube;
import org.fasd.xmla.XMLADataSourceConnection;
import org.fasd.xmla.XMLASchema;
import org.xml.sax.SAXException;





public class DigesterFasd {
	private FAModel model;

	
	private void createCallbacks(Digester dig){
		String root = "freeanalysis-schema";
		dig.addObjectCreate(root, FAModel.class);
		dig.addCallMethod(root + "/id", "setId", 0);
		//XXX
//		dig.addCallMethod(root + "/uuid", "setUuid", 0);
		dig.addCallMethod(root + "/isSynchronized", "setSynchronized", 0);
		
	//document properties
	dig.addObjectCreate(root + "/document-properties", DocumentProperties.class);
	
		dig.addCallMethod(root + "/document-properties/name", "setName", 0);
		dig.addCallMethod(root + "/document-properties/author", "setAuthor", 0);
		dig.addCallMethod(root + "/document-properties/description", "setDescription", 0);
		dig.addCallMethod(root + "/document-properties/creation-date", "setCreation", 0);
		dig.addCallMethod(root + "/document-properties/modification-date", "setModification", 0);
		dig.addCallMethod(root + "/document-properties/version", "setVersion", 0);
		dig.addCallMethod(root + "/document-properties/icon", "setIconPath", 0);
		dig.addCallMethod(root + "/document-properties/flyover-icon", "setFlyOverIconPath", 0);
	
	dig.addSetNext(root + "/document-properties","setDocumentProperties");
	
	//XMLA
	dig.addObjectCreate(root + "/xmla", XMLASchema.class);
		dig.addObjectCreate(root + "/xmla/XMLAConnection", XMLADataSourceConnection.class);
			dig.addCallMethod(root + "/xmla/XMLAConnection/url", "setUrl", 0);
			dig.addCallMethod(root + "/xmla/XMLAConnection/user", "setUser", 0);
			dig.addCallMethod(root + "/xmla/XMLAConnection/password", "setPass", 0);
			dig.addCallMethod(root + "/xmla/XMLAConnection/schema", "setSchema", 0);
			dig.addCallMethod(root + "/xmla/XMLAConnection/catalog", "setCatalog", 0);
			dig.addCallMethod(root + "/xmla/XMLAConnection/type", "setType", 0);
		dig.addSetNext(root + "/xmla/XMLAConnection","setConnection");
		
		dig.addObjectCreate(root + "/xmla/cube", XMLACube.class);
		dig.addCallMethod(root + "/xmla/cube/name", "setName", 0);
		dig.addSetNext(root + "/xmla/cube","addCube");
		
	dig.addSetNext(root + "/xmla","setXMLASchema");
	
	createPreloadConfigCallbacks(dig,root);
	
	createDatasourceOdaCallbacks(dig,root);
	
	//datasources
	dig.addObjectCreate(root + "/datasources/datasource-item", DataSource.class);
	dig.addCallMethod(root + "/datasources/datasource-item/id", "setId", 0);
	dig.addCallMethod(root + "/datasources/datasource-item/name", "setDSName", 0);
	dig.addCallMethod(root + "/datasources/datasource-item/defaultConnectionId", "setDefaultDriverId", 0);
		dig.addObjectCreate(root + "/datasources/datasource-item/connection", DataSourceConnection.class);
			dig.addCallMethod(root + "/datasources/datasource-item/connection/name", "setName", 0);
			dig.addCallMethod(root + "/datasources/datasource-item/connection/id", "setId", 0);
			dig.addCallMethod(root + "/datasources/datasource-item/connection/schema-name", "setSchemaName", 0);
			dig.addCallMethod(root + "/datasources/datasource-item/connection/driver", "setDriver", 0);
			dig.addCallMethod(root + "/datasources/datasource-item/connection/url", "setUrl", 0);
			dig.addCallMethod(root + "/datasources/datasource-item/connection/user", "setUser", 0);
			dig.addCallMethod(root + "/datasources/datasource-item/connection/password", "setPass", 0);
			
			dig.addCallMethod(root + "/datasources/datasource-item/connection/type", "setType", 0);
			dig.addCallMethod(root + "/datasources/datasource-item/connection/data-source-location", "setDataSourceLocation", 0);
			dig.addCallMethod(root + "/datasources/datasource-item/connection/server-id", "setServerId", 0);
			dig.addCallMethod(root + "/datasources/datasource-item/connection/transformation-url", "setTransUrl", 0);
			dig.addCallMethod(root + "/datasources/datasource-item/connection/file-location", "setFileLocation", 0);
			dig.addCallMethod(root + "/datasources/datasource-item/connection/description", "setDesc", 0);
			
			//for repository JNDI
			dig.addCallMethod(root + "/datasources/datasource-item/connection/repositoryDataSourceId", "setRepositoryDataSourceId", 0);
			dig.addCallMethod(root + "/datasources/datasource-item/connection/repositoryUrl", "setRepositoryDataSourceUrl", 0);
			dig.addCallMethod(root + "/datasources/datasource-item/connection/directoryItemId", "setDirectoryItemId", 0);
			
		dig.addSetNext(root + "/datasources/datasource-item/connection", "addConnection");
		
		
		dig.addObjectCreate(root + "/datasources/datasource-item/dataobject-item", DataObject.class);
			dig.addCallMethod(root + "/datasources/datasource-item/dataobject-item/id", "setId", 0);
			dig.addCallMethod(root + "/datasources/datasource-item/dataobject-item/name", "setName", 0);
			dig.addCallMethod(root + "/datasources/datasource-item/dataobject-item/positionX", "setPositionX", 0);
			dig.addCallMethod(root + "/datasources/datasource-item/dataobject-item/positionY", "setPositionY", 0);
			
			dig.addCallMethod(root + "/datasources/datasource-item/dataobject-item/server-id", "setServerId", 0);
			dig.addCallMethod(root + "/datasources/datasource-item/dataobject-item/data-object-type", "setDataObjectType", 0);
			dig.addCallMethod(root + "/datasources/datasource-item/dataobject-item/select-statment-definition", "setSelectStatement", 0);
			dig.addCallMethod(root + "/datasources/datasource-item/dataobject-item/transformation-name", "setTransName", 0);
			dig.addCallMethod(root + "/datasources/datasource-item/dataobject-item/file-name", "setFileName", 0);
			dig.addCallMethod(root + "/datasources/datasource-item/dataobject-item/description", "setDesc", 0);
			dig.addCallMethod(root + "/datasources/datasource-item/dataobject-item/isInline", "setInline", 0);
			dig.addCallMethod(root + "/datasources/datasource-item/dataobject-item/isView", "setView", 0);
			dig.addCallMethod(root + "/datasources/datasource-item/dataobject-item/creationStatement", "setCreationStatement", 0);
			dig.addCallMethod(root + "/datasources/datasource-item/dataobject-item/fillingStatement", "setFillingStatement", 0);
			
			dig.addObjectCreate(root + "/datasources/datasource-item/dataobject-item/item", DataObjectItem.class);
			dig.addCallMethod(root + "/datasources/datasource-item/dataobject-item/item/id", "setId", 0);
				dig.addCallMethod(root + "/datasources/datasource-item/dataobject-item/item/name", "setName", 0);
				dig.addCallMethod(root + "/datasources/datasource-item/dataobject-item/isInline", "setInline", 0);
				dig.addCallMethod(root + "/datasources/datasource-item/dataobject-item/item/origin", "setOrigin", 0);
				dig.addCallMethod(root + "/datasources/datasource-item/dataobject-item/item/type", "setType", 0);
				dig.addCallMethod(root + "/datasources/datasource-item/dataobject-item/item/classe", "setClasse", 0);
				dig.addCallMethod(root + "/datasources/datasource-item/dataobject-item/item/sql-type", "setSqlType", 0);
				dig.addCallMethod(root + "/datasources/datasource-item/dataobject-item/item/attribut", "setAttribut", 0);
				dig.addCallMethod(root + "/datasources/datasource-item/dataobject-item/item/description", "setDesc", 0);
				
				
			dig.addSetNext(root + "/datasources/datasource-item/dataobject-item/item", "addDataObjectItem");
			
			

			
			
			
			//inline table
			dig.addObjectCreate(root + "/datasources/datasource-item/dataobject-item/inline-datas", DataInline.class);
				dig.addObjectCreate(root + "/datasources/datasource-item/dataobject-item/inline-datas/row", DataRow.class);
					dig.addCallMethod(root + "/datasources/datasource-item/dataobject-item/inline-datas/row/cell","addValue", 2);
					dig.addCallParam(root + "/datasources/datasource-item/dataobject-item/inline-datas/row/cell/column-id", 0);
					dig.addCallParam(root + "/datasources/datasource-item/dataobject-item/inline-datas/row/cell/value", 1);
				
				dig.addSetNext(root + "/datasources/datasource-item/dataobject-item/inline-datas/row", "addRow");
			dig.addSetNext(root + "/datasources/datasource-item/dataobject-item/inline-datas", "setDatas");
		
		dig.addSetNext(root + "/datasources/datasource-item/dataobject-item", "addDataObject");
		
		//SQL VIEWS
		dig.addObjectCreate(root + "/datasources/datasource-item/dataview-item", DataView.class);
		dig.addCallMethod(root + "/datasources/datasource-item/dataview-item/id", "setId", 0);
		dig.addCallMethod(root + "/datasources/datasource-item/dataview-item/name", "setName", 0);
		
		dig.addCallMethod(root + "/datasources/datasource-item/dataview-item/server-id", "setServerId", 0);
		dig.addCallMethod(root + "/datasources/datasource-item/dataview-item/data-object-type", "setDataObjectType", 0);
		dig.addCallMethod(root + "/datasources/datasource-item/dataview-item/select-statment-definition", "setSelectStatement", 0);
		dig.addCallMethod(root + "/datasources/datasource-item/dataview-item/transformation-name", "setTransName", 0);
		dig.addCallMethod(root + "/datasources/datasource-item/dataview-item/file-name", "setFileName", 0);
		dig.addCallMethod(root + "/datasources/datasource-item/dataview-item/description", "setDesc", 0);
		dig.addCallMethod(root + "/datasources/datasource-item/dataview-item/isInline", "setInline", 0);

		dig.addCallMethod(root + "/datasources/datasource-item/dataview-item/dialect", "addDialect", 2);
		dig.addCallParam(root + "/datasources/datasource-item/dataview-item/dialect/name", 0);
		dig.addCallParam(root + "/datasources/datasource-item/dataview-item/dialect/query", 1);
		
		
		dig.addObjectCreate(root + "/datasources/datasource-item/dataview-item/item", DataObjectItem.class);
		dig.addCallMethod(root + "/datasources/datasource-item/dataview-item/item/id", "setId", 0);
			dig.addCallMethod(root + "/datasources/datasource-item/dataview-item/item/name", "setName", 0);
			dig.addCallMethod(root + "/datasources/datasource-item/dataview-item/isInline", "setInline", 0);
			dig.addCallMethod(root + "/datasources/datasource-item/dataview-item/item/origin", "setOrigin", 0);
			dig.addCallMethod(root + "/datasources/datasource-item/dataview-item/item/type", "setType", 0);
			dig.addCallMethod(root + "/datasources/datasource-item/dataview-item/item/classe", "setClasse", 0);
			dig.addCallMethod(root + "/datasources/datasource-item/dataview-item/item/sql-type", "setSqlType", 0);
			dig.addCallMethod(root + "/datasources/datasource-item/dataview-item/item/attribut", "setAttribut", 0);
			dig.addCallMethod(root + "/datasources/datasource-item/dataview-item/item/description", "setDesc", 0);
			
			
		dig.addSetNext(root + "/datasources/datasource-item/dataview-item/item", "addDataObjectItem");

		
	dig.addSetNext(root + "/datasources/datasource-item/dataview-item", "addDataObject");	
	
	dig.addSetNext(root + "/datasources/datasource-item", "addDataSource");			
	
	
	
	//relations
	dig.addObjectCreate(root + "/object-relation/object-relation-item", OLAPRelation.class);
		dig.addCallMethod(root + "/object-relation/object-relation-item/id", "setId", 0);
		dig.addCallMethod(root + "/object-relation/object-relation-item/name", "setName", 0);
		dig.addCallMethod(root + "/object-relation/object-relation-item/left-object-id", "setLeftObjectId", 0);
		dig.addCallMethod(root + "/object-relation/object-relation-item/right-object-id", "setRightObjectId", 0);
		dig.addCallMethod(root + "/object-relation/object-relation-item/relation-formula/left-object-column-id", "setLeftItemId", 0);
		dig.addCallMethod(root + "/object-relation/object-relation-item/relation-formula/right-object-column-id", "setRightItemId", 0);
		dig.addCallMethod(root + "/object-relation/object-relation-item/relation-formula/object-columns-relation", "setOperator", 0);
	dig.addSetNext(root + "/object-relation/object-relation-item", "addRelation");
	
	//olap 
	dig.addObjectCreate(root + "/olap", OLAPSchema.class);
	dig.addCallMethod(root + "/olap/defaultRoleName", "setDefaultRole", 0);
	dig.addCallMethod(root + "/olap/measureCaption", "setMeasureCaption", 0);
	//userdefinedfunction
	dig.addObjectCreate(root + "/olap/UserDefinedFunctions/UserDefinedFunction-item", UserDefinedFunction.class);
		dig.addCallMethod(root + "/olap/UserDefinedFunctions/UserDefinedFunction-item/name", "setName", 0);
		dig.addCallMethod(root + "/olap/UserDefinedFunctions/UserDefinedFunction-item/className", "setClassName", 0);
	dig.addSetNext(root + "/olap/UserDefinedFunctions/UserDefinedFunction-item", "addUserDefinedFunction");
	
//		security-group
		dig.addObjectCreate(root + "/olap/Security-group/Security-group-item", SecurityGroup.class);
			dig.addCallMethod(root + "/olap/Security-group/Security-group-item/id", "setId", 0);
			dig.addCallMethod(root + "/olap/Security-group/Security-group-item/name", "setName", 0);
			dig.addCallMethod(root + "/olap/Security-group/Security-group-item/description", "setDesc", 0);
			dig.addCallMethod(root + "/olap/Security-group/Security-group-item/level", "setLevel", 0);
			dig.addCallMethod(root + "/olap/Security-group/Security-group-item/parent-id", "setParentId", 0);
		dig.addSetNext(root + "/olap/Security-group/Security-group-item", "addSecurityGroup");

//aggregate table
		dig.addObjectCreate(root + "/olap/Aggregate/AggregateTable", AggregateTable.class);
			dig.addCallMethod(root + "/olap/Aggregate/AggregateTable/id", "setId", 0);
			dig.addCallMethod(root + "/olap/Aggregate/AggregateTable/dataobject-id", "setTableId", 0);
			dig.addCallMethod(root + "/olap/Aggregate/AggregateTable/factCountItem-id", "setFactCountItemId", 0);
			
			dig.addObjectCreate(root + "/olap/Aggregate/AggregateTable/AggMeasure", AggMeasure.class);
				dig.addCallMethod(root + "/olap/Aggregate/AggregateTable/AggMeasure/measure-id", "setMeasureId", 0);
				dig.addCallMethod(root + "/olap/Aggregate/AggregateTable/AggMeasure/column-id", "setColumnId", 0);
			dig.addSetNext(root + "/olap/Aggregate/AggregateTable/AggMeasure", "addAggMeasure");
			
			dig.addObjectCreate(root + "/olap/Aggregate/AggregateTable/AggLevel", AggLevel.class);
				dig.addCallMethod(root + "/olap/Aggregate/AggregateTable/AggLevel/level-id", "setLevelId", 0);
				dig.addCallMethod(root + "/olap/Aggregate/AggregateTable/AggLevel/column-id", "setColumnId", 0);
			dig.addSetNext(root + "/olap/Aggregate/AggregateTable/AggLevel", "addAggLevel");
		
		
		dig.addSetNext(root + "/olap/Aggregate/AggregateTable", "addAggregate");
		//dimension-group
		dig.addObjectCreate(root + "/olap/Dimension-group/Dimension-group-item", OLAPDimensionGroup.class);
			dig.addCallMethod(root + "/olap/Dimension-group/Dimension-group-item/id", "setId", 0);
			dig.addCallMethod(root + "/olap/Dimension-group/Dimension-group-item/name", "setName", 0);
			dig.addCallMethod(root + "/olap/Dimension-group/Dimension-group-item/description", "setDesc", 0);
			dig.addCallMethod(root + "/olap/Dimension-group/Dimension-group-item/level", "setLevel", 0);
			dig.addCallMethod(root + "/olap/Dimension-group/Dimension-group-item/parent-id", "setParentId", 0);
		dig.addSetNext(root + "/olap/Dimension-group/Dimension-group-item", "addDimensionGroup");
		
		//measure-group
		dig.addObjectCreate(root + "/olap/Measure-group/Measure-group-item", OLAPMeasureGroup.class);
			dig.addCallMethod(root + "/olap/Measure-group/Measure-group-item/id", "setId", 0);
			dig.addCallMethod(root + "/olap/Measure-group/Measure-group-item/name", "setName", 0);
			dig.addCallMethod(root + "/olap/Measure-group/Measure-group-item/description", "setDesc", 0);
			dig.addCallMethod(root + "/olap/Measure-group/Measure-group-item/level", "setLevel", 0);
			dig.addCallMethod(root + "/olap/Measure-group/Measure-group-item/parent-id", "setParentId", 0);
		dig.addSetNext(root + "/olap/Measure-group/Measure-group-item", "addMeasureGroup");

		
		//normal dims
		
		dig.addObjectCreate(root + "/olap/Dimension/Dimension-item", OLAPDimension.class);
			dig.addCallMethod(root + "/olap/Dimension/Dimension-item/id", "setId", 0);
			dig.addCallMethod(root + "/olap/Dimension/Dimension-item/name", "setName", 0);
			dig.addCallMethod(root + "/olap/Dimension/Dimension-item/caption", "setCaption", 0);
			
			dig.addCallMethod(root + "/olap/Dimension/Dimension-item/description", "setDesc", 0);
			dig.addCallMethod(root + "/olap/Dimension/Dimension-item/dimension-group-id", "setGroupId", 0);
			dig.addCallMethod(root + "/olap/Dimension/Dimension-item/IsDate", "setDate", 0);
			dig.addCallMethod(root + "/olap/Dimension/Dimension-item/LoadMethod", "setLoadMethod", 0);
			dig.addCallMethod(root + "/olap/Dimension/Dimension-item/order", "setOrder", 0);
//			dig.addCallMethod(root + "/olap/Dimension/Dimension-item/Widget", "setWidget", 0);
//			dig.addCallMethod(root + "/olap/Dimension/Dimension-item/properties", "setProperties", 0);
			dig.addCallMethod(root + "/olap/Dimension/Dimension-item/geolicalisable", "setGeolocalisable", 0);
			dig.addCallMethod(root + "/olap/Dimension/Dimension-item/isOneColumnDate", "setOneColumnDate", 0);
		
			
			dig.addObjectCreate(root + "/olap/Dimension/Dimension-item/Hierarchy", OLAPHierarchy.class);
				dig.addCallMethod(root + "/olap/Dimension/Dimension-item/Hierarchy/id", "setId", 0);
				dig.addCallMethod(root + "/olap/Dimension/Dimension-item/Hierarchy/caption", "setCaption", 0);
//				dig.addCallMethod(root + "/olap/Dimension/Dimension-item/Hierarchy/allMemberCaption", "setAllMemberCaption", 0);
//				dig.addCallMethod(root + "/olap/Dimension/Dimension-item/Hierarchy/memberReaderClass", "setMemberReaderClass", 0);
				dig.addCallMethod(root + "/olap/Dimension/Dimension-item/Hierarchy/name", "setName", 0);
				dig.addCallMethod(root + "/olap/Dimension/Dimension-item/Hierarchy/description", "setDesc", 0);
//				dig.addCallMethod(root + "/olap/Dimension/Dimension-item/Hierarchy/hasAll", "setHasAll", 0);
				dig.addCallMethod(root + "/olap/Dimension/Dimension-item/Hierarchy/allMember", "setAllMember", 0);
//				dig.addCallMethod(root + "/olap/Dimension/Dimension-item/Hierarchy/defaultMember", "setDefaultMember", 0);
			
				dig.addObjectCreate(root + "/olap/Dimension/Dimension-item/Hierarchy/Level", OLAPLevel.class);
					dig.addCallMethod(root + "/olap/Dimension/Dimension-item/Hierarchy/Level/id", "setId", 0);
					dig.addCallMethod(root + "/olap/Dimension/Dimension-item/Hierarchy/Level/name", "setName", 0);
					dig.addCallMethod(root + "/olap/Dimension/Dimension-item/Hierarchy/Level/type", "setType", 0);
					dig.addCallMethod(root + "/olap/Dimension/Dimension-item/Hierarchy/Level/levelType", "setLevelType", 0);
//					dig.addCallMethod(root + "/olap/Dimension/Dimension-item/Hierarchy/Level/hideMemberIf", "setHideMemberIf", 0);
					dig.addCallMethod(root + "/olap/Dimension/Dimension-item/Hierarchy/Level/ordinalColumn", "setOrdinalColumn", 0);
					dig.addCallMethod(root + "/olap/Dimension/Dimension-item/Hierarchy/Level/ordinalItemId", "setOrderItemId", 0);
					dig.addCallMethod(root + "/olap/Dimension/Dimension-item/Hierarchy/Level/caption", "setCaption", 0);
					dig.addCallMethod(root + "/olap/Dimension/Dimension-item/Hierarchy/Level/captionColumn", "setColumnLabelId", 0);
					dig.addCallMethod(root + "/olap/Dimension/Dimension-item/Hierarchy/Level/formatter", "setFormatter", 0);
					dig.addCallMethod(root + "/olap/Dimension/Dimension-item/Hierarchy/Level/level-nb", "setNb", 0);
//					dig.addCallMethod(root + "/olap/Dimension/Dimension-item/Hierarchy/Level/approxRowCount", "setApproxRowCount", 0);
					dig.addCallMethod(root + "/olap/Dimension/Dimension-item/Hierarchy/Level/description", "setDesc", 0);
//					dig.addCallMethod(root + "/olap/Dimension/Dimension-item/Hierarchy/Level/IsReal", "setIsReal", 0);
//					dig.addCallMethod(root + "/olap/Dimension/Dimension-item/Hierarchy/Level/uniqueMembers", "setUniquemb", 0);
					dig.addCallMethod(root + "/olap/Dimension/Dimension-item/Hierarchy/Level/dataobjectitem-code-id", "setItemId", 0);
					
//					dig.addCallMethod(root + "/olap/Dimension/Dimension-item/Hierarchy/Level/parent-dataobjectitem-id", "setParentColumnId", 0);
					dig.addCallMethod(root + "/olap/Dimension/Dimension-item/Hierarchy/Level/null-parent-value", "setNullParentValue", 0);
					
					dig.addCallMethod(root + "/olap/Dimension/Dimension-item/Hierarchy/Level/closure-dataobject-id", "setClosureTableId", 0);
					dig.addCallMethod(root + "/olap/Dimension/Dimension-item/Hierarchy/Level/closure-parent-dataobjectitem-id", "setClosureParentId", 0);
					dig.addCallMethod(root + "/olap/Dimension/Dimension-item/Hierarchy/Level/closure-child-dataobjectitem-id", "setClosureChildId", 0);
					
					dig.addCallMethod(root + "/olap/Dimension/Dimension-item/Hierarchy/Level/isOneColumnDate", "setOneColumnDate", 0);
					dig.addCallMethod(root + "/olap/Dimension/Dimension-item/Hierarchy/Level/dateColumnType", "setDateColumnType", 0);
					dig.addCallMethod(root + "/olap/Dimension/Dimension-item/Hierarchy/Level/dateColumnPart", "setDateColumnPart", 0);
					dig.addCallMethod(root + "/olap/Dimension/Dimension-item/Hierarchy/Level/dateColumnPattern", "setDateColumnPattern", 0);
					dig.addCallMethod(root + "/olap/Dimension/Dimension-item/Hierarchy/Level/dateColumnOrderPart", "setDateColumnOrderPart", 0);
					
				
					dig.addObjectCreate(root + "/olap/Dimension/Dimension-item/Hierarchy/Level/Property", Property.class);
						dig.addCallMethod(root + "/olap/Dimension/Dimension-item/Hierarchy/Level/Property/id", "setId", 0);
						dig.addCallMethod(root + "/olap/Dimension/Dimension-item/Hierarchy/Level/Property/name", "setName", 0);
						dig.addCallMethod(root + "/olap/Dimension/Dimension-item/Hierarchy/Level/Property/caption", "setCaption", 0);
						dig.addCallMethod(root + "/olap/Dimension/Dimension-item/Hierarchy/Level/Property/dataobjectitem-id", "setColumnId", 0);
						dig.addCallMethod(root + "/olap/Dimension/Dimension-item/Hierarchy/Level/Property/type", "setType", 0);
						dig.addCallMethod(root + "/olap/Dimension/Dimension-item/Hierarchy/Level/Property/formatter", "setFormatter", 0);
					dig.addSetNext(root + "/olap/Dimension/Dimension-item/Hierarchy/Level/Property", "addProperty");
					
				dig.addSetNext(root + "/olap/Dimension/Dimension-item/Hierarchy/Level", "addLevel");
			
				dig.addObjectCreate(root + "/olap/Dimension/Dimension-item/Hierarchy/Level/Parameter", Parameter.class);
					dig.addCallMethod(root + "/olap/Dimension/Dimension-item/Hierarchy/Level/Parameter/name", "setname", 0);
					dig.addCallMethod(root + "/olap/Dimension/Dimension-item/Hierarchy/Level/Parameter/type", "setType", 0);
					dig.addCallMethod(root + "/olap/Dimension/Dimension-item/Hierarchy/Level/Parameter/description", "setDescription", 0);
					dig.addCallMethod(root + "/olap/Dimension/Dimension-item/Hierarchy/Level/Parameter/defaultValue", "setDefaultValue", 0);
					dig.addCallMethod(root + "/olap/Dimension/Dimension-item/Hierarchy/Level/Parameter/modifiable", "setModifable", 0);
				dig.addSetNext(root + "/olap/Dimension/Dimension-item/Hierarchy/Level/Parameter", "addParameter");

				
			dig.addSetNext(root + "/olap/Dimension/Dimension-item/Hierarchy", "addHierarchy");
			
		
		dig.addSetNext(root + "/olap/Dimension/Dimension-item", "addDimension");
	
		//normal measures
		dig.addObjectCreate(root + "/olap/Measure/Measure-item", OLAPMeasure.class);
			dig.addCallMethod(root + "/olap/Measure/Measure-item/id", "setId", 0);
			dig.addCallMethod(root + "/olap/Measure/Measure-item/name", "setName", 0);
			dig.addCallMethod(root + "/olap/Measure/Measure-item/caption", "setCaption", 0);
			dig.addCallMethod(root + "/olap/Measure/Measure-item/visible", "setVisible", 0);
			dig.addCallMethod(root + "/olap/Measure/Measure-item/description", "setDesc", 0);
			dig.addCallMethod(root + "/olap/Measure/Measure-item/measure-group-id", "setGroupId", 0);
			dig.addCallMethod(root + "/olap/Measure/Measure-item/type", "setType", 0);
			dig.addCallMethod(root + "/olap/Measure/Measure-item/order", "setOrder", 0);
			dig.addCallMethod(root + "/olap/Measure/Measure-item/origin-id", "setOriginId", 0);
			dig.addCallMethod(root + "/olap/Measure/Measure-item/formula", "setFormula", 0);
			dig.addCallMethod(root + "/olap/Measure/Measure-item/label", "setLabelId", 0);
			dig.addCallMethod(root + "/olap/Measure/Measure-item/aggregator", "setAggregator", 0);
			dig.addCallMethod(root + "/olap/Measure/Measure-item/lastTimeDimensionName", "setLastTimeDimensionName", 0);
			dig.addCallMethod(root + "/olap/Measure/Measure-item/formatString", "setFormatstr", 0);
			dig.addCallMethod(root + "/olap/Measure/Measure-item/dimensionName", "setDimensionName", 0);
			dig.addCallMethod(root + "/olap/Measure/Measure-item/color-rules", "setColorScript", 0);
		
			dig.addCallMethod(root + "/olap/Measure/Measure-item/property", "addPropExpression", 2);
			dig.addCallParam(root + "/olap/Measure/Measure-item/property/name", 0);
			dig.addCallParam(root + "/olap/Measure/Measure-item/property/expression", 1);
			
			
		dig.addSetNext(root + "/olap/Measure/Measure-item", "addMeasure");
		
		//DynamicMeasure
		createDynamicMeasureCallbacks(dig, root + "/olap/Measure/Dynamic-Measure-item");
		
		
		//cubes
		dig.addObjectCreate(root + "/olap/Cube/Cube-item", OLAPCube.class);
			dig.addCallMethod(root + "/olap/Cube/Cube-item/id", "setId", 0);
			dig.addCallMethod(root + "/olap/Cube/Cube-item/name", "setName", 0);
			dig.addCallMethod(root + "/olap/Cube/Cube-item/enabled", "setEnable", 0);
			dig.addCallMethod(root + "/olap/Cube/Cube-item/cached", "setCache", 0);
			dig.addCallMethod(root + "/olap/Cube/Cube-item/defaultMeasure", "setDefaultMeasure", 0);
			dig.addCallMethod(root + "/olap/Cube/Cube-item/description", "setDescription", 0);
//			dig.addCallMethod(root + "/olap/Cube/Cube-item/type", "setType", 0);
//			dig.addCallMethod(root + "/olap/Cube/Cube-item/physical-name", "setPhysicalName", 0);
//			dig.addCallMethod(root + "/olap/Cube/Cube-item/cube-location", "setLocation", 0);
			dig.addCallMethod(root + "/olap/Cube/Cube-item/fact-dataobject-id", "setFactDataObjectId", 0);
			dig.addCallMethod(root + "/olap/Cube/Cube-item/datasource-id", "setDataSourceId", 0);
			dig.addCallMethod(root + "/olap/Cube/Cube-item/dimension-item-id", "addDimId", 0);
			dig.addCallMethod(root + "/olap/Cube/Cube-item/dimension-group-id", "addDimGroup", 0);
			dig.addCallMethod(root + "/olap/Cube/Cube-item/measure-group-id", "addMesGroup", 0);
			dig.addCallMethod(root + "/olap/Cube/Cube-item/measure-item-id", "addMesId", 0);
//			dig.addCallMethod(root + "/olap/Cube/Cube-item/security-group-item-id", "addSecuId", 0);
			dig.addCallMethod(root + "/olap/Cube/Cube-item/aggregate-item-id", "addAggtableId", 0);
			dig.addCallMethod(root + "/olap/Cube/Cube-item/namedSet-id", "addNamedSetId", 0);
			dig.addCallMethod(root + "/olap/Cube/Cube-item/defaultMeasure", "setDefaultMeasure", 0);
			dig.addCallMethod(root + "/olap/Cube/Cube-item/startupDimension", "addOpenedDimension", 0);
			
			//aggpattern
			dig.addObjectCreate(root + "/olap/Cube/Cube-item/AggPattern", AggPattern.class);
				dig.addCallMethod(root + "/olap/Cube/Cube-item/AggPattern/pattern", "setPattern", 0);
				dig.addObjectCreate(root + "/olap/Cube/Cube-item/AggPattern/AggExclude", AggExclude.class);
					dig.addCallMethod(root + "/olap/Cube/Cube-item/AggPattern/AggExclude/name", "setname", 0);
					dig.addCallMethod(root + "/olap/Cube/Cube-item/AggPattern/AggExclude/pattern", "setPattern", 0);
					dig.addCallMethod(root + "/olap/Cube/Cube-item/AggPattern/AggExclude/ignoreCase", "setIgnoreCase", 0);
				dig.addSetNext(root + "/olap/Cube/Cube-item/AggPattern/AggExclude", "addExcluded");
			dig.addSetNext(root + "/olap/Cube/Cube-item/AggPattern", "addAggPattern");
			
		
			//cubeview
			dig.addObjectCreate(root + "/olap/Cube/Cube-item/view", CubeView.class);
				dig.addCallMethod(root + "/olap/Cube/Cube-item/view/name", "setName", 0);
				dig.addCallMethod(root + "/olap/Cube/Cube-item/view/col", "addCol", 0);
				dig.addCallMethod(root + "/olap/Cube/Cube-item/view/row", "addRow", 0);
			dig.addSetNext(root + "/olap/Cube/Cube-item/view", "addCubeView");

			
			//drill
			dig.addObjectCreate(root + "/olap/Cube/Cube-item/drillcube", DrillCube.class);
				dig.addCallMethod(root + "/olap/Cube/Cube-item/drillcube/name", "setDrillName", 0);
				dig.addCallMethod(root + "/olap/Cube/Cube-item/drillcube/fasd", "setFasdId", 0);
				dig.addCallMethod(root + "/olap/Cube/Cube-item/drillcube/cubename", "setCubeName", 0);
				
			dig.addSetNext(root + "/olap/Cube/Cube-item/drillcube", "addDrill");
			
			dig.addObjectCreate(root + "/olap/Cube/Cube-item/drillreport", DrillReport.class);
				dig.addCallMethod(root + "/olap/Cube/Cube-item/drillreport/name", "setDrillName", 0);
				dig.addCallMethod(root + "/olap/Cube/Cube-item/drillreport/item", "setItemId", 0);
				dig.addCallMethod(root + "/olap/Cube/Cube-item/drillreport/reportname", "setName", 0);
				
				dig.addObjectCreate(root + "/olap/Cube/Cube-item/drillreport/parameters/parameter", DrillReportParameter.class);
					dig.addCallMethod(root + "/olap/Cube/Cube-item/drillreport/parameters/parameter/name", "setName", 0);
					dig.addCallMethod(root + "/olap/Cube/Cube-item/drillreport/parameters/parameter/dimension", "setDimension", 0);
					dig.addCallMethod(root + "/olap/Cube/Cube-item/drillreport/parameters/parameter/level", "setLevel", 0);
				dig.addSetNext(root + "/olap/Cube/Cube-item/drillreport/parameters/parameter", "addParameter");
				
			dig.addSetNext(root + "/olap/Cube/Cube-item/drillreport", "addDrill");
			
		dig.addSetNext(root + "/olap/Cube/Cube-item", "addCube");
		
		
//		VirtualCubes
		dig.addObjectCreate(root + "/olap/VirtualCube/VirtualCube-item", VirtualCube.class);
			dig.addCallMethod(root + "/olap/VirtualCube/VirtualCube-item/id", "setId", 0);
			dig.addCallMethod(root + "/olap/VirtualCube/VirtualCube-item/name", "setName", 0);
			dig.addCallMethod(root + "/olap/VirtualCube/VirtualCube-item/enabled", "setEnable", 0);
			dig.addCallMethod(root + "/olap/Cube/VirtualCube/VirtualCube-item/cached", "setCache", 0);
			dig.addCallMethod(root + "/olap/VirtualCube/VirtualCube-item/caption", "setCaption", 0);
			dig.addCallMethod(root + "/olap/VirtualCube/VirtualCube-item/datasource-id", "setDataSourceId", 0);
			dig.addCallMethod(root + "/olap/VirtualCube/VirtualCube-item/description", "setDescription", 0);
			
			dig.addObjectCreate(root + "/olap/VirtualCube/VirtualCube-item/VirtualDimension-item", VirtualDimension.class);
				dig.addCallMethod(root + "/olap/VirtualCube/VirtualCube-item/VirtualDimension-item/Dimension-id", "setDimName", 0);
				dig.addCallMethod(root + "/olap/VirtualCube/VirtualCube-item/VirtualDimension-item/visible", "setVisible", 0);
			dig.addSetNext(root + "/olap/VirtualCube/VirtualCube-item/VirtualDimension-item", "addVirtualDimension");
			
			dig.addObjectCreate(root + "/olap/VirtualCube/VirtualCube-item/VirtualMeasure-item", VirtualMeasure.class);
				dig.addCallMethod(root + "/olap/VirtualCube/VirtualCube-item/VirtualMeasure-item/Cube-id", "setCubeName", 0);
				dig.addCallMethod(root + "/olap/VirtualCube/VirtualCube-item/VirtualMeasure-item/Measure-id", "setMeasureName", 0);
				dig.addCallMethod(root + "/olap/VirtualCube/VirtualCube-item/VirtualMeasure-item/namedSet-id", "addNamedSetId", 0);
			dig.addSetNext(root + "/olap/VirtualCube/VirtualCube-item/VirtualMeasure-item", "addVirtualMeasure");
			
			dig.addCallMethod(root + "/olap/VirtualCube/VirtualCube-item/VirtualMeasure-item/security-group-item-id", "addSecurityId", 0);
		
		dig.addSetNext(root + "/olap/VirtualCube/VirtualCube-item", "addVirtualCube");
		
		
//		dimensionsviews
//		dig.addObjectCreate(root + "/olap/DimensionViews/DimensionView-item", SecurityDim.class);
//			//dig.addCallMethod(root + "/olap/DimensionViews/DimensionView-item/id", "setId", 0);
//			dig.addCallMethod(root + "/olap/DimensionViews/DimensionView-item/name", "setName", 0);
//			dig.addCallMethod(root + "/olap/DimensionViews/DimensionView-item/description", "setDesc", 0);
//			dig.addCallMethod(root + "/olap/DimensionViews/DimensionView-item/dimension-id", "setDimId", 0);
//			
//			dig.addObjectCreate(root + "/olap/DimensionViews/DimensionView-item/groupdefinition-item", View.class);
//				dig.addCallMethod(root + "/olap/DimensionViews/DimensionView-item/groupdefinition-item/id", "setId", 0);
//				dig.addCallMethod(root + "/olap/DimensionViews/DimensionView-item/groupdefinition-item/name", "setName", 0);
//				dig.addCallMethod(root + "/olap/DimensionViews/DimensionView-item/groupdefinition-item/description", "setDesc", 0);
//				dig.addCallMethod(root + "/olap/DimensionViews/DimensionView-item/groupdefinition-item/securitygroup-id", "setSecurityGroupId", 0);
//				dig.addCallMethod(root + "/olap/DimensionViews/DimensionView-item/groupdefinition-item/hierarchy-id", "setHierarchyId", 0);
//				dig.addCallMethod(root + "/olap/DimensionViews/DimensionView-item/groupdefinition-item/AllowAcces", "setAllowAccess", 0);
//				dig.addCallMethod(root + "/olap/DimensionViews/DimensionView-item/groupdefinition-item/AllowFullAcces", "setAllowFullAccess", 0);
//				dig.addCallMethod(root + "/olap/DimensionViews/DimensionView-item/groupdefinition-item/RestrictionMemberList", "addMember", 0);
//			dig.addSetNext(root + "/olap/DimensionViews/DimensionView-item/groupdefinition-item", "addView");
//				
//		dig.addSetNext(root + "/olap/DimensionViews/DimensionView-item", "addDimView");


	//named set	
		dig.addObjectCreate(root + "/olap/NamedSet/NamedSet-item", NamedSet.class);
			dig.addCallMethod(root + "/olap/NamedSet/NamedSet-item/id", "setId", 0);
			dig.addCallMethod(root + "/olap/NamedSet/NamedSet-item/name", "setName", 0);
			dig.addCallMethod(root + "/olap/NamedSet/NamedSet-item/global", "setGlobal", 0);
			dig.addCallMethod(root + "/olap/NamedSet/NamedSet-item/Formula", "setFormula", 0);
		dig.addSetNext(root + "/olap/NamedSet/NamedSet-item", "addNamedSet");
	dig.addSetNext(root + "/olap", "setOLAPSchema");
	}
	
	
	private void createPreloadConfigCallbacks(Digester dig, String root) {
		dig.addObjectCreate(root + "/preloadconfig", PreloadConfig.class);
		
		dig.addCallMethod(root + "/preloadconfig/levels/level", "setHierarchyLevel", 2);
		dig.addCallParam(root + "/preloadconfig/levels/level/hierarchy", 0);
		dig.addCallParam(root + "/preloadconfig/levels/level/levelnumber", 1);
		
		dig.addSetNext(root + "/preloadconfig", "setPreloadConfig");
	}


	private void createDynamicMeasureCallbacks(Digester dig, String root) {
		dig.addObjectCreate(root, OlapDynamicMeasure.class);
			dig.addCallMethod(root + "/id", "setId", 0);
			dig.addCallMethod(root + "/name", "setName", 0);
			dig.addCallMethod(root + "/caption", "setCaption", 0);
			dig.addCallMethod(root + "/visible", "setVisible", 0);
			dig.addCallMethod(root + "/description", "setDesc", 0);
			dig.addCallMethod(root + "/measure-group-id", "setGroupId", 0);
			dig.addCallMethod(root + "/type", "setType", 0);
			dig.addCallMethod(root + "/order", "setOrder", 0);
			dig.addCallMethod(root + "/origin-id", "setOriginId", 0);
			dig.addCallMethod(root + "/formula", "setFormula", 0);
			dig.addCallMethod(root + "/label", "setLabelId", 0);
			dig.addCallMethod(root + "/aggregator", "setAggregator", 0);
			dig.addCallMethod(root + "/lastTimeDimensionName", "setLastTimeDimensionName", 0);
			dig.addCallMethod(root + "/formatString", "setFormatstr", 0);
			dig.addCallMethod(root + "/dimensionName", "setDimensionName", 0);
			dig.addCallMethod(root + "/color-rules", "setColorScript", 0);
			
			dig.addCallMethod(root + "/property", "addPropExpression", 2);
				dig.addCallParam(root + "/property/name", 0);
				dig.addCallParam(root + "/property/expression", 1);
			
			dig.addObjectCreate(root + "/calculated-aggregation", CalculatedAggregation.class);
				dig.addCallMethod(root + "/calculated-aggregation/formula", "setFormula", 0);
				dig.addCallMethod(root + "/calculated-aggregation/level", "setLevel", 0);
			dig.addSetNext(root + "/calculated-aggregation", "addAggregation");
			
			dig.addObjectCreate(root + "/classic-aggregation", ClassicAggregation.class);
				dig.addCallMethod(root + "/classic-aggregation/aggregator", "setAggregator", 0);
				dig.addCallMethod(root + "/classic-aggregation/originId", "setOriginId", 0);
				dig.addCallMethod(root + "/classic-aggregation/level", "setLevel", 0);
			dig.addSetNext(root + "/classic-aggregation", "addAggregation");
			
			dig.addObjectCreate(root + "/last-aggregation", LastAggregation.class);
				dig.addCallMethod(root + "/last-aggregation/aggregator", "setAggregator", 0);
				dig.addCallMethod(root + "/last-aggregation/originId", "setOriginId", 0);
				dig.addCallMethod(root + "/last-aggregation/relatedDimension", "setRelatedDimension", 0);
				dig.addCallMethod(root + "/last-aggregation/level", "setLevel", 0);	
			dig.addSetNext(root + "/last-aggregation", "addAggregation");
			
			
		dig.addSetNext(root, "addMeasure");
	}


	private void createDatasourceOdaCallbacks(Digester dig, String root) {
		dig.addObjectCreate(root + "/datasources/datasource-oda", DatasourceOda.class);
			dig.addCallMethod(root + "/datasources/datasource-oda/id", "setId", 0);
			dig.addCallMethod(root + "/datasources/datasource-oda/name", "setName", 0);
			dig.addCallMethod(root + "/datasources/datasource-oda/odadatasourceextensionid", "setOdaDatasourceExtensionId", 0);
			dig.addCallMethod(root + "/datasources/datasource-oda/odaextensionid", "setOdaExtensionId", 0);
			
			dig.addCallMethod(root + "/datasources/datasource-oda/public-properties/property", "addPublicProperty",2);
				dig.addCallParam(root + "/datasources/datasource-oda/public-properties/property/name", 0);
				dig.addCallParam(root + "/datasources/datasource-oda/public-properties/property/value", 1);
				
			dig.addCallMethod(root + "/datasources/datasource-oda/private-properties/property", "addPrivateProperty",2);
				dig.addCallParam(root + "/datasources/datasource-oda/private-properties/property/name", 0);
				dig.addCallParam(root + "/datasources/datasource-oda/private-properties/property/value", 1);
				
			dig.addObjectCreate(root + "/datasources/datasource-oda/dataobject-oda", DataObjectOda.class);
				dig.addCallMethod(root + "/datasources/datasource-oda/dataobject-oda/id", "setId", 0);
				dig.addCallMethod(root + "/datasources/datasource-oda/dataobject-oda/name", "setName", 0);
				dig.addCallMethod(root + "/datasources/datasource-oda/dataobject-oda/querytext", "setQueryText", 0);
				dig.addCallMethod(root + "/datasources/datasource-oda/dataobject-oda/dataobjecttype", "setDataObjectType", 0);
				dig.addCallMethod(root + "/datasources/datasource-oda/dataobject-oda/datasetextensionid", "setOdaDatasetExtensionId", 0);
				
				dig.addCallMethod(root + "/datasources/datasource-oda/dataobject-oda/public-properties/property", "addPublicProperty",2);
				dig.addCallParam(root + "/datasources/datasource-oda/dataobject-oda/public-properties/property/name", 0);
				dig.addCallParam(root + "/datasources/datasource-oda/dataobject-oda/public-properties/property/value", 1);
				
			dig.addCallMethod(root + "/datasources/datasource-oda/dataobject-oda/private-properties/property", "addPrivateProperty",2);
				dig.addCallParam(root + "/datasources/datasource-oda/dataobject-oda/private-properties/property/name", 0);
				dig.addCallParam(root + "/datasources/datasource-oda/dataobject-oda/private-properties/property/value", 1);
			
			dig.addSetNext(root + "/datasources/datasource-oda/dataobject-oda", "addDataObject");
			
				dig.addObjectCreate(root + "/datasources/datasource-oda/dataobject-oda/item", DataObjectItem.class);
				dig.addCallMethod(root + "/datasources/datasource-oda/dataobject-oda/item/id", "setId", 0);
					dig.addCallMethod(root + "/datasources/datasource-oda/dataobject-oda/item/name", "setName", 0);
					dig.addCallMethod(root + "/datasources/datasource-oda/dataobject-oda/isInline", "setInline", 0);
					dig.addCallMethod(root + "/datasources/datasource-oda/dataobject-oda/item/origin", "setOrigin", 0);
					dig.addCallMethod(root + "/datasources/datasource-oda/dataobject-oda/item/type", "setType", 0);
					dig.addCallMethod(root + "/datasources/datasource-oda/dataobject-oda/item/classe", "setClasse", 0);
					dig.addCallMethod(root + "/datasources/datasource-oda/dataobject-oda/item/sql-type", "setSqlType", 0);
					dig.addCallMethod(root + "/datasources/datasource-oda/dataobject-oda/item/attribut", "setAttribut", 0);
					dig.addCallMethod(root + "/datasources/datasource-oda/dataobject-oda/item/description", "setDesc", 0);
					
				dig.addSetNext(root + "/datasources/datasource-oda/dataobject-oda/item", "addDataObjectItem");
				
		dig.addSetNext(root + "/datasources/datasource-oda", "addDataSource");
	}


	private void clean(){
		model = MondrianToUOlapConverter.convertFromMondrianToUOlap(model);
		
		
		if (model.getXMLASchema() != null)
			return;
		
		
		//make references from DataObject to DataSource and DataObjectItem to DataObject
		for(DataSource ds :model.getDataSources()){
			ds.getDriver().setServer(model.getSecurity().findServer(ds.getDriver().getServerId()));
			for (DataObject o :ds.getDataObjects()){
				o.setDataSource(ds);
				
				if (o.isInline()){
					o.getDatas().setDataObject(o);
				}
				
				for(DataObjectItem i : o.getColumns()){
					i.setParent(o);
				}
			}
		}
		
		for(SecurityProvider s : model.getSecurity().getSecurityProviders()){
			s.setServer(model.getSecurity().findServer(s.getServerId()));
		}
		//make references for relation and dataSource
		for(OLAPRelation r : model.getRelations()){
			r.setLeftObjectItem(model.findDataObjectItem(r.getLeftItemId()));
			r.setRightObjectItem(model.findDataObjectItem(r.getRightItemId()));
		}

		for(OLAPMeasureGroup g : model.getOLAPSchema().getMeasureGroups()){
			if (g.getLevel() != 0){
				g.setParent(model.getOLAPSchema().findMeasureGroup(g.getParentId()));
			}
		}
		//mes

		for(OLAPMeasure m : model.getOLAPSchema().getMeasures()){
			m.setOrigin(model.findDataObjectItem(m.getOriginId()));
			if (!m.getGroupId().equals("")){
				m.setGroup(model.getOLAPSchema().findMeasureGroup(m.getGroupId()));
			}
			if(m instanceof OlapDynamicMeasure) {
				for(IMeasureAggregation agg : ((OlapDynamicMeasure)m).getAggregations()) {
					if(agg instanceof ClassicAggregation) {
						((ClassicAggregation)agg).setOrigin(model.findDataObjectItem(((ClassicAggregation)agg).getOriginId()));
					}
					else if(agg instanceof LastAggregation) {
						((LastAggregation)agg).setOrigin(model.findDataObjectItem(((LastAggregation)agg).getOriginId()));
					}
				}
			}
		}
		
		
		for(OLAPDimensionGroup g : model.getOLAPSchema().getDimensionGroups()){
			if (g.getLevel() != 0){
				g.setParent(model.getOLAPSchema().findDimensionGroup(g.getParentId()));
			}
		}
		
		for(SecurityGroup g : model.getOLAPSchema().getSecurityGroups()){
			if (g.getLevel() != 0){
				g.setParent(model.getOLAPSchema().findSecurityGroup(g.getParentId()));
			}
		}
		
		//dims
		for(OLAPDimension d : model.getOLAPSchema().getDimensions()){
			for(OLAPHierarchy h : d.getHierarchies()){
				h.setParent(d);
				for(OLAPLevel l : h.getLevels()){
					l.setParent(h);
					l.setItem(model.findDataObjectItem(l.getItemId()));
					l.setLabel(model.findDataObjectItem(l.getColumnLabelId()));
					l.setClosureChildCol(model.findDataObjectItem(l.getClosureChildId()));
					l.setClosureParentCol(model.findDataObjectItem(l.getClosureParentId()));
					l.setClosureTable(model.findDataObject(l.getClosureTableId()));
					l.setOrderItem(model.findDataObjectItem(l.getOrderItemId()));
					for(Property p : l.getProperties()){
						p.setColumn(model.findDataObjectItem(p.getColumnId()));
					}
				}
			}
			if (!d.getGroupId().equals("")){
				d.setGroup(model.getOLAPSchema().findDimensionGroup(d.getGroupId()));
			}
			
		}
		organizeDimensionGroup();
		
		for(SecurityDim d : model.getOLAPSchema().getDimViews()){
			d.setDim(model.getOLAPSchema().findDimension(d.getDimId()));
			for(View v : d.getViews()){
				v.setGroup((SecurityGroup)model.getOLAPSchema().findSecurityGroup(v.getSecurityGroupId()));
				for(OLAPHierarchy h : v.getParent().getDim().getHierarchies()){
					if (h.getId().equals(v.getHierarchyId())){
						v.setHierarchy(h);
					}
				}
			}
		}
		
		//reference in cubes
		for(OLAPCube c : model.getOLAPSchema().getCubes()){
			c.setFactDataObject(model.findDataObject(c.getFactDataObjectId()));
			if (c.getType().equals("Molap"))
				c.setDataSource(model.findDataSource(c.getDataSourceId()));
			
			for(String s : c.getDimIdList()){
				c.addDim(model.getOLAPSchema().findDimension(s));
			}
			for(String s : c.getMesIdList()){
				c.addMes(model.getOLAPSchema().findMeasure(s));
			}
			for(String s : c.getMesGroupId()){
				c.addMesGroup(model.getOLAPSchema().findMeasureGroup(s));
			}
			for(String s : c.getDimGroupId()){
				c.addDimGroup(model.getOLAPSchema().findDimensionGroup(s));
			}
//			for(String s : c.getSecuIdList()){
//				c.addSecurityGroup(model.getOLAPSchema().findSecurityGroup(s));
//			}
		}
		for(VirtualCube v : model.getOLAPSchema().getVirtualCubes()){
			v.setDataSource(model.findDataSource(v.getDataSourceId()));
			for(String s : v.getSecurityGroupsId()){
				v.addSecurityGroup(model.getOLAPSchema().findSecurityGroup(s));
			}
		}
//		for(GrantView v : model.getOLAPSchema().getViews()){
//			for(GrantCube c : v.getCubesGrant()){
//				for(GrantHierarchy h : c.getHierarchiesGrant()){
//					h.setParent(c);
//				}
//			}
//		}

		for(VirtualCube c : model.getOLAPSchema().getVirtualCubes()){
			for(VirtualDimension vd : c.getVirtualDimensions()){
//				if (vd.getCubeName().equals("")){
//					vd.setShared(true);
//				}
////				else{
////					vd.setCube(model.getOLAPSchema().findCubeNamed(vd.getCubeName()));
////				}
				
				vd.setDim(model.getOLAPSchema().findDimension(vd.getDimName()));
				vd.setName(vd.getDim().getName());
			}
			for(VirtualMeasure vm : c.getVirtualMeasure()){
				vm.setCube(model.getOLAPSchema().findCube(vm.getCubeName()));
				vm.setMes(model.getOLAPSchema().findMeasure(vm.getMeasureName()));
			}
		}
		
		//check if every dimension have a dimensionview associated, if not it creates one
		for(OLAPDimension d : model.getOLAPSchema().getDimensions()){
			if (model.getOLAPSchema().findSecurityDim(d) == null){
				SecurityDim s = new SecurityDim();
				s.setName(d.getName() + " view");
				s.setDim(d);
				
				model.getOLAPSchema().addDimView(s);
			}
		}
		
		//
		for(AggregateTable a : model.getOLAPSchema().getAggregates()){
			a.setTable(model.findDataObject(a.getTableId()));
			a.setFactCountColumn(model.findDataObjectItem(a.getFactCountItemId()));
			for(AggMeasure m : a.getAggMes()){
				m.setMes(model.getOLAPSchema().findMeasure(m.getMeasureId()));
				m.setColumn(model.findDataObjectItem(m.getColumnId()));
			}
			for(AggLevel l : a.getAggLvl()){
				l.setLvl(model.getOLAPSchema().findLevel(l.getLevelId()));
				l.setColumn(model.findDataObjectItem(l.getColumnId()));
			}
		}
		
		//make reference on aggregateTable
		for(OLAPCube c: model.getOLAPSchema().getCubes()){
			for(String s : c.getAggTableId()){
				c.addAggTable(model.getOLAPSchema().findAggTableById(s));
			}
		}
		
		for(OLAPCube c : model.getOLAPSchema().getCubes()){
			for (String s : c.getNamedSetsId())
				c.addNamedSet(model.getOLAPSchema().findNamedSetById(s));
		}
		
		for(VirtualCube c : model.getOLAPSchema().getVirtualCubes()){
			for (String s : c.getNamedSetsId())
				c.addNamedSet(model.getOLAPSchema().findNamedSetById(s));
		}
	}

	public DigesterFasd(String path, ClassLoader classLoader) throws FileNotFoundException, Exception {
		
		FileReader f = new FileReader(path);
		
		Digester dig = new Digester();
		dig.setClassLoader(classLoader);
		dig.setValidating(false);
		System.out.println("entered");
		
		createCallbacks(dig);
		
		try {
			model = (FAModel) dig.parse(f);

			clean();
			
		} catch (SAXException e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());			
		} finally {
		}
	}
	
	public DigesterFasd(String path) throws FileNotFoundException, Exception {
		FileReader f = new FileReader(path);
		
		Digester dig = new Digester();
		dig.setValidating(false);
		System.out.println("entered");
		
		createCallbacks(dig);
		
		try {
			model = (FAModel) dig.parse(f);

			clean();
			
		} catch (SAXException e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());			
		} finally {
		}
	}
	
	
	public DigesterFasd(InputStream stream) throws FileNotFoundException, Exception {
		
		Digester dig = new Digester();
		dig.setValidating(false);
		System.out.println("entered");
		
		createCallbacks(dig);
		
		try {
			model = (FAModel) dig.parse(stream);

			clean();
			
		} catch (SAXException e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());			
		} finally {
		}
	}
	
	public DigesterFasd(InputStream stream, ClassLoader classLoader) throws FileNotFoundException, Exception {
		
		Digester dig = new Digester();
		dig.setClassLoader(classLoader);
		dig.setValidating(false);
		System.out.println("entered");
		
		createCallbacks(dig);
		
		try {
			model = (FAModel) dig.parse(stream);

			clean();
			
		} catch (SAXException e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());			
		} finally {
		}
	}

	private OLAPDimensionGroup findParent(OLAPDimensionGroup gr){
		for(OLAPDimensionGroup g : model.getOLAPSchema().getDimensionGroups()){
			if (gr.getParentId().equals(g.getId()))
				return g;
		}
		return null;
	}
	
	private void organizeDimensionGroup(){
		List<OLAPDimensionGroup> gToRemove = new ArrayList<OLAPDimensionGroup>();
		for(OLAPDimensionGroup g :model.getOLAPSchema().getDimensionGroups()){
			g.setParent(findParent(g));
			if (g.getLevel() > 0)
				gToRemove.add(g);
		}
		
		for(OLAPDimensionGroup g : gToRemove){
			model.getOLAPSchema().getDimensionGroups().remove(g);
		}
		
	
	}
	
	

	public FAModel getFAModel() {	
		
		return model;
	}
}
