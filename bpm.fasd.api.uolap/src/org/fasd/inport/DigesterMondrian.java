package org.fasd.inport;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.digester3.Digester;
import org.fasd.datasource.DataInline;
import org.fasd.datasource.DataObject;
import org.fasd.datasource.DataObjectItem;
import org.fasd.datasource.DataRow;
import org.fasd.datasource.DataSource;
import org.fasd.datasource.DataSourceConnection;
import org.fasd.datasource.DataView;
import org.fasd.inport.mondrian.beans.ColDef;
import org.fasd.inport.mondrian.beans.CubeGrantBean;
import org.fasd.inport.mondrian.beans.HierarchyGrantBean;
import org.fasd.inport.mondrian.beans.InlineBean;
import org.fasd.inport.mondrian.beans.MemberGrantBean;
import org.fasd.inport.mondrian.beans.RoleGrantBean;
import org.fasd.inport.mondrian.beans.SchemaGrantBean;
import org.fasd.olap.DimUsage;
import org.fasd.olap.FAModel;
import org.fasd.olap.Joint;
import org.fasd.olap.NamedSet;
import org.fasd.olap.OLAPCube;
import org.fasd.olap.OLAPDimension;
import org.fasd.olap.OLAPHierarchy;
import org.fasd.olap.OLAPLevel;
import org.fasd.olap.OLAPMeasure;
import org.fasd.olap.OLAPRelation;
import org.fasd.olap.OLAPSchema;
import org.fasd.olap.Parameter;
import org.fasd.olap.Property;
import org.fasd.olap.UserDefinedFunction;
import org.fasd.olap.aggregate.AggExclude;
import org.fasd.olap.aggregate.AggLevel;
import org.fasd.olap.aggregate.AggMeasure;
import org.fasd.olap.aggregate.AggPattern;
import org.fasd.olap.aggregate.AggregateTable;
import org.fasd.olap.aggregate.MondrianAgg;
import org.fasd.olap.aggregate.MondrianAgg.MondrianAggFKey;
import org.fasd.olap.aggregate.MondrianAgg.MondrianAggLvl;
import org.fasd.olap.aggregate.MondrianAgg.MondrianAggMes;
import org.fasd.olap.virtual.VirtualCube;
import org.fasd.olap.virtual.VirtualDimension;
import org.fasd.olap.virtual.VirtualMeasure;
import org.fasd.security.SecurityDim;
import org.fasd.security.SecurityGroup;
import org.fasd.security.View;




public class DigesterMondrian{
	private OLAPSchema schema;
	private FAModel model = new FAModel();
	private DataSource dataSource = new DataSource();
	private List<OLAPRelation> relations = new ArrayList<OLAPRelation>();
	/*
	 * schema 
	 * 	dimension
	 * 		hierarchy
	 * 			table
	 * 			level (bis)
	 * 
	 * 	cube
	 * 		table
	 * 		dimensionusage
	 * 		mesure
	 */
	
	public DigesterMondrian(String path) throws FileNotFoundException, Exception {
		FileReader f = new FileReader(path);
		
		Digester dig = new Digester();
		dig.setValidating(false);
		System.out.println("entered");
		String root = "Schema";
		
		dig.addObjectCreate(root, OLAPSchema.class);
			dig.addSetProperties(root + "/UserDefinedFunction", "defaultRole", "defaultRole");
			dig.addSetProperties(root + "/UserDefinedFunction", "measureCaption", "measureCaption");
		//userdefinedfunction
		dig.addObjectCreate(root + "/UserDefinedFunction", UserDefinedFunction.class);
			dig.addSetProperties(root + "/UserDefinedFunction", "name", "name");
			dig.addSetProperties(root + "/UserDefinedFunction", "class", "className");
		dig.addSetNext(root + "/UserDefinedFunction", "addUserDefinedFunction");
	
	
		//named set for all Cubes
		dig.addObjectCreate(root + "/NamedSet", NamedSet.class);
			dig.addSetProperties(root + "/NamedSet", "name", "name");
			dig.addCallMethod(root + "/NamedSet/Formula", "setFormula", 0);
			dig.addCallMethod(root + "/NamedSet", "setGlobal");
		dig.addSetNext(root + "/NamedSet", "addNamedSet");
		
		//normal dims
		dig.addObjectCreate(root + "/Dimension", OLAPDimension.class);
		dig.addSetProperties(root + "/Dimension", "name", "name");
		dig.addSetProperties(root + "/Dimension", "caption", "caption");
		dig.addCallMethod(root + "/Dimension/type", "setDate", 0);
		
		dig.addSetProperties(root + "/Dimension", "foreignKey", "foreignKey");

			dig.addObjectCreate(root + "/Dimension/Hierarchy", OLAPHierarchy.class);
			dig.addSetProperties(root + "/Dimension/Hierarchy", "name", "name");
			dig.addSetProperties(root + "/Dimension/Hierarchy", "caption", "caption");
			dig.addSetProperties(root + "/Dimension/Hierarchy", "allMemberCaption", "allMemberCaption");
			dig.addSetProperties(root + "/Dimension/Hierarchy", "memberReaderClass", "memberReaderClass");
			dig.addSetProperties(root + "/Dimension/Hierarchy", "defaultMember", "defaultMember");
			dig.addSetProperties(root + "/Dimension/Hierarchy", "hasAll", "hasAll");
			dig.addSetProperties(root + "/Dimension/Hierarchy", "allMemberName", "allMember");
			dig.addSetProperties(root + "/Dimension/Hierarchy", "primaryKey", "primaryKey");
			dig.addSetProperties(root + "/Dimension/Hierarchy", "primaryKeyTable", "primaryKeyTable");
			dig.addSetProperties(root + "/Dimension/Hierarchy/Table", "name", "tableName");
		
			//view insteadof Table
			dig.addObjectCreate(root + "/Dimension/Hierarchy/View", DataView.class);
				dig.addSetProperties(root + "/Dimension/Hierarchy/View", "alias", "name");
				dig.addCallMethod(root + "/Dimension/Hierarchy/View/SQL", "addDialect", 2);
				dig.addCallParam(root + "/Dimension/Hierarchy/View/SQL", 0, "dialect");
				dig.addCallParam(root + "/Dimension/Hierarchy/View/SQL", 1);
			dig.addSetNext(root + "/Dimension/Hierarchy/View", "setView");
			
			//inlinetable
			dig.addObjectCreate(root + "/Dimension/Hierarchy/InlineTable", InlineBean.class);
				dig.addSetProperties(root + "/Dimension/Hierarchy/InlineTable", "alias", "alias");
				dig.addObjectCreate(root + "/Dimension/Hierarchy/InlineTable/ColumnDefs/ColumnDef", ColDef.class);
					dig.addSetProperties(root + "/Dimension/Hierarchy/InlineTable/ColumnDefs/ColumnDef", "name", "name");
					dig.addSetProperties(root + "/Dimension/Hierarchy/InlineTable/ColumnDefs/ColumnDef", "type", "type");
				dig.addSetNext(root + "/Dimension/Hierarchy/InlineTable/ColumnDefs/ColumnDef", "addColDef");
				
				dig.addObjectCreate(root + "/Dimension/Hierarchy/InlineTable/Rows", DataInline.class);
					dig.addObjectCreate(root + "/Dimension/Hierarchy/InlineTable/Rows/Row", DataRow.class);
						dig.addCallMethod(root + "/Dimension/Hierarchy/InlineTable/Rows/Row/Value", "addValue", 2);
						dig.addCallParam(root + "/Dimension/Hierarchy/InlineTable/Rows/Row/Value", 0, "column");
						dig.addCallParam(root + "/Dimension/Hierarchy/InlineTable/Rows/Row/Value", 1);
					dig.addSetNext(root + "/Dimension/Hierarchy/InlineTable/Rows/Row", "addRow");
				dig.addSetNext(root + "/Dimension/Hierarchy/InlineTable/Rows", "setDatas");
				
			dig.addSetNext(root + "/Dimension/Hierarchy/InlineTable", "setInlineBean");
			//joint
			dig.addObjectCreate(root + "/Dimension/Hierarchy/Join", Joint.class);
			dig.addObjectCreate(root + "/Dimension/Hierarchy/Join/Table", Joint.class);
			dig.addSetProperties(root + "/Dimension/Hierarchy/Join","leftKey", "leftKey");
			dig.addSetProperties(root + "/Dimension/Hierarchy/Join","leftAlias", "leftAlias");
			dig.addSetProperties(root + "/Dimension/Hierarchy/Join","rightKey", "rightKey");
			dig.addSetProperties(root + "/Dimension/Hierarchy/Join","rightAlias", "rightAlias");
			dig.addSetProperties(root + "/Dimension/Hierarchy/Join/Table","name", "tableName");
			dig.addSetNext(root + "/Dimension/Hierarchy/Join/Table", "addChild");
			dig.addSetNext(root + "/Dimension/Hierarchy/Join", "addJoint");
			
			dig.addObjectCreate(root + "/Dimension/Hierarchy/Join/Join", Joint.class);
			dig.addObjectCreate(root + "/Dimension/Hierarchy/Join/Join/Table", Joint.class);
			dig.addSetProperties(root + "/Dimension/Hierarchy/Join/Join","leftKey", "leftKey");
			dig.addSetProperties(root + "/Dimension/Hierarchy/Join/Join","leftAlias", "leftAlias");
			dig.addSetProperties(root + "/Dimension/Hierarchy/Join/Join","rightKey", "rightKey");
			dig.addSetProperties(root + "/Dimension/Hierarchy/Join/Join","rightAlias", "rightAlias");
			dig.addSetProperties(root + "/Dimension/Hierarchy/Join/Join/Table","name", "tableName");
			dig.addSetNext(root + "/Dimension/Hierarchy/Join/Join/Table", "addChild");
			dig.addSetNext(root + "/Dimension/Hierarchy/Join/Join", "addChild");
			
			dig.addObjectCreate(root + "/Dimension/Hierarchy/Join/Join/Join", Joint.class);
			dig.addObjectCreate(root + "/Dimension/Hierarchy/Join/Join/Join/Table", Joint.class);
			dig.addSetProperties(root + "/Dimension/Hierarchy/Join/Join/Join","leftKey", "leftKey");
			dig.addSetProperties(root + "/Dimension/Hierarchy/Join/Join/Join","leftAlias", "leftAlias");
			dig.addSetProperties(root + "/Dimension/Hierarchy/Join/Join/Join","rightKey", "rightKey");
			dig.addSetProperties(root + "/Dimension/Hierarchy/Join/Join/Join","rightAlias", "rightAlias");
			dig.addSetProperties(root + "/Dimension/Hierarchy/Join/Join/Join/Table","name", "tableName");
			dig.addSetNext(root + "/Dimension/Hierarchy/Join/Join/Join/Table", "addChild");
			dig.addSetNext(root + "/Dimension/Hierarchy/Join/Join/Join", "addChild");
			
			dig.addObjectCreate(root + "/Dimension/Hierarchy/Join/Join/Join/Join", Joint.class);
			dig.addObjectCreate(root + "/Dimension/Hierarchy/Join/Join/Join/Join/Table", Joint.class);
			dig.addSetProperties(root + "/Dimension/Hierarchy/Join/Join/Join/Join","leftKey", "leftKey");
			dig.addSetProperties(root + "/Dimension/Hierarchy/Join/Join/Join/Join","leftAlias", "leftAlias");
			dig.addSetProperties(root + "/Dimension/Hierarchy/Join/Join/Join/Join","rightKey", "rightKey");
			dig.addSetProperties(root + "/Dimension/Hierarchy/Join/Join/Join/Join","rightAlias", "rightAlias");
			dig.addSetProperties(root + "/Dimension/Hierarchy/Join/Join/Join/Join/Table","name", "tableName");
			dig.addSetNext(root + "/Dimension/Hierarchy/Join/Join/Join/Join/Table", "addChild");
			dig.addSetNext(root + "/Dimension/Hierarchy/Join/Join/Join/Join", "addChild");
							
			dig.addObjectCreate(root + "/Dimension/Hierarchy/Join/Join/Join/Join/Join", Joint.class);
			dig.addObjectCreate(root + "/Dimension/Hierarchy/Join/Join/Join/Join/Join/Table", Joint.class);
			dig.addSetProperties(root + "/Dimension/Hierarchy/Join/Join/Join/Join/Join","leftKey", "leftKey");
			dig.addSetProperties(root + "/Dimension/Hierarchy/Join/Join/Join/Join/Join","leftAlias", "leftAlias");
			dig.addSetProperties(root + "/Dimension/Hierarchy/Join/Join/Join/Join/Join","rightKey", "rightKey");
			dig.addSetProperties(root + "/Dimension/Hierarchy/Join/Join/Join/Join/Join","rightAlias", "rightAlias");
			dig.addSetProperties(root + "/Dimension/Hierarchy/Join/Join/Join/Join/Join/Table","name", "tableName");
			dig.addSetNext(root + "/Dimension/Hierarchy/Join/Join/Join/Join/Join/Table", "addChild");
			dig.addSetNext(root + "/Dimension/Hierarchy/Join/Join/Join/Join/Join", "addChild");

			dig.addObjectCreate(root + "/Dimension/Hierarchy/Join/Join/Join/Join/Join/Join", Joint.class);
			dig.addObjectCreate(root + "/Dimension/Hierarchy/Join/Join/Join/Join/Join/Join/Table", Joint.class);
			dig.addSetProperties(root + "/Dimension/Hierarchy/Join/Join/Join/Join/Join/Join","leftKey", "leftKey");
			dig.addSetProperties(root + "/Dimension/Hierarchy/Join/Join/Join/Join/Join/Join","leftAlias", "leftAlias");
			dig.addSetProperties(root + "/Dimension/Hierarchy/Join/Join/Join/Join/Join/Join","rightKey", "rightKey");
			dig.addSetProperties(root + "/Dimension/Hierarchy/Join/Join/Join/Join/Join/Join","rightAlias", "rightAlias");
			dig.addSetProperties(root + "/Dimension/Hierarchy/Join/Join/Join/Join/Join/Join/Table","name", "tableName");
			dig.addSetNext(root + "/Dimension/Hierarchy/Join/Join/Join/Join/Join/Join/Table", "addChild");
			dig.addSetNext(root + "/Dimension/Hierarchy/Join/Join/Join/Join/Join/Join", "addChild");

			dig.addObjectCreate(root + "/Dimension/Hierarchy/Join/Join/Join/Join/Join/Join/Join", Joint.class);
			dig.addObjectCreate(root + "/Dimension/Hierarchy/Join/Join/Join/Join/Join/Join/Join/Table", Joint.class);
			dig.addSetProperties(root + "/Dimension/Hierarchy/Join/Join/Join/Join/Join/Join/Join","leftKey", "leftKey");
			dig.addSetProperties(root + "/Dimension/Hierarchy/Join/Join/Join/Join/Join/Join/Join","leftAlias", "leftAlias");
			dig.addSetProperties(root + "/Dimension/Hierarchy/Join/Join/Join/Join/Join/Join/Join","rightKey", "rightKey");
			dig.addSetProperties(root + "/Dimension/Hierarchy/Join/Join/Join/Join/Join/Join/Join","rightAlias", "rightAlias");
			dig.addSetProperties(root + "/Dimension/Hierarchy/Join/Join/Join/Join/Join/Join/Join/Table","name", "tableName");
			dig.addSetNext(root + "/Dimension/Hierarchy/Join/Join/Join/Join/Join/Join/Join/Table", "addChild");
			dig.addSetNext(root + "/Dimension/Hierarchy/Join/Join/Join/Join/Join/Join/Join", "addChild");

			dig.addObjectCreate(root + "/Dimension/Hierarchy/Join/Join/Join/Join/Join/Join/Join/Join", Joint.class);
			dig.addObjectCreate(root + "/Dimension/Hierarchy/Join/Join/Join/Join/Join/Join/Join/Join/Table", Joint.class);
			dig.addSetProperties(root + "/Dimension/Hierarchy/Join/Join/Join/Join/Join/Join/Join/Join","leftKey", "leftKey");
			dig.addSetProperties(root + "/Dimension/Hierarchy/Join/Join/Join/Join/Join/Join/Join/Join","leftAlias", "leftAlias");
			dig.addSetProperties(root + "/Dimension/Hierarchy/Join/Join/Join/Join/Join/Join/Join/Join","rightKey", "rightKey");
			dig.addSetProperties(root + "/Dimension/Hierarchy/Join/Join/Join/Join/Join/Join/Join/Join","rightAlias", "rightAlias");
			dig.addSetProperties(root + "/Dimension/Hierarchy/Join/Join/Join/Join/Join/Join/Join/Join/Table","name", "tableName");
			dig.addSetNext(root + "/Dimension/Hierarchy/Join/Join/Join/Join/Join/Join/Join/Join/Table", "addChild");
			dig.addSetNext(root + "/Dimension/Hierarchy/Join/Join/Join/Join/Join/Join/Join/Join", "addChild");

			dig.addObjectCreate(root + "/Dimension/Hierarchy/Join/Join/Join/Join/Join/Join/Join/Join/Join", Joint.class);
			dig.addObjectCreate(root + "/Dimension/Hierarchy/Join/Join/Join/Join/Join/Join/Join/Join/Join/Table", Joint.class);
			dig.addSetProperties(root + "/Dimension/Hierarchy/Join/Join/Join/Join/Join/Join/Join/Join/Join","leftKey", "leftKey");
			dig.addSetProperties(root + "/Dimension/Hierarchy/Join/Join/Join/Join/Join/Join/Join/Join/Join","leftAlias", "leftAlias");
			dig.addSetProperties(root + "/Dimension/Hierarchy/Join/Join/Join/Join/Join/Join/Join/Join/Join","rightKey", "rightKey");
			dig.addSetProperties(root + "/Dimension/Hierarchy/Join/Join/Join/Join/Join/Join/Join/Join/Join","rightAlias", "rightAlias");
			dig.addSetProperties(root + "/Dimension/Hierarchy/Join/Join/Join/Join/Join/Join/Join/Join/Join/Table","name", "tableName");
			dig.addSetNext(root + "/Dimension/Hierarchy/Join/Join/Join/Join/Join/Join/Join/Join/Join/Table", "addChild");
			dig.addSetNext(root + "/Dimension/Hierarchy/Join/Join/Join/Join/Join/Join/Join/Join/Join", "addChild");

			dig.addObjectCreate(root + "/Dimension/Hierarchy/Join/Join/Join/Join/Join/Join/Join/Join/Join", Joint.class);
			dig.addObjectCreate(root + "/Dimension/Hierarchy/Join/Join/Join/Join/Join/Join/Join/Join/Join/Table", Joint.class);
			dig.addSetProperties(root + "/Dimension/Hierarchy/Join/Join/Join/Join/Join/Join/Join/Join/Join","leftKey", "leftKey");
			dig.addSetProperties(root + "/Dimension/Hierarchy/Join/Join/Join/Join/Join/Join/Join/Join/Join","leftAlias", "leftAlias");
			dig.addSetProperties(root + "/Dimension/Hierarchy/Join/Join/Join/Join/Join/Join/Join/Join/Join","rightKey", "rightKey");
			dig.addSetProperties(root + "/Dimension/Hierarchy/Join/Join/Join/Join/Join/Join/Join/Join/Join","rightAlias", "rightAlias");
			dig.addSetProperties(root + "/Dimension/Hierarchy/Join/Join/Join/Join/Join/Join/Join/Join/Join/Table","name", "tableName");
			dig.addSetNext(root + "/Dimension/Hierarchy/Join/Join/Join/Join/Join/Join/Join/Join/Join/Table", "addChild");
			dig.addSetNext(root + "/Dimension/Hierarchy/Join/Join/Join/Join/Join/Join/Join/Join/Join", "addChild");

			
				dig.addObjectCreate(root + "/Dimension/Hierarchy/Level", OLAPLevel.class);
				dig.addSetProperties(root + "/Dimension/Hierarchy/Level", "name", "name");
				dig.addSetProperties(root + "/Dimension/Hierarchy/Level", "caption", "caption");
				dig.addSetProperties(root + "/Dimension/Hierarchy/Level", "captionColumn", "captionColumn");
				dig.addSetProperties(root + "/Dimension/Hierarchy/Level", "formatter", "formatter");
				dig.addSetProperties(root + "/Dimension/Hierarchy/Level", "ordinalColumn", "ordinalColumn");
				dig.addSetProperties(root + "/Dimension/Hierarchy/Level", "approxRowCount", "approxRowCount");
				dig.addSetProperties(root + "/Dimension/Hierarchy/Level", "column", "columnName");
				dig.addSetProperties(root + "/Dimension/Hierarchy/Level", "uniqueMembers", "uniquemb");
				dig.addSetProperties(root + "/Dimension/Hierarchy/Level", "type", "type");
				dig.addSetProperties(root + "/Dimension/Hierarchy/Level", "levelType", "levelType");
				dig.addSetProperties(root + "/Dimension/Hierarchy/Level", "hideMemberIf", "hideMemberIf");
				dig.addSetProperties(root + "/Dimension/Hierarchy/Level", "parentColumn", "parentColumnId");
				dig.addSetProperties(root + "/Dimension/Hierarchy/Level", "nullParentValue", "nullParentValue");
				dig.addSetProperties(root + "/Dimension/Hierarchy/Level", "table", "tableId");
				dig.addSetProperties(root + "/Dimension/Hierarchy/Level/Closure", "parentColumn", "closureParentId");
				dig.addSetProperties(root + "/Dimension/Hierarchy/Level/Closure", "childColumn", "closureChildId");
				dig.addSetProperties(root + "/Dimension/Hierarchy/Level/Closure/Table", "name", "closureTableId");
				
				dig.addCallMethod(root + "/Dimension/Hierarchy/Level/KeyExpression/SQL", "addKeyExpression", 2);
				dig.addCallParam(root + "/Dimension/Hierarchy/Level/KeyExpression/SQL", 0, "dialect");
				dig.addCallParam(root + "/Dimension/Hierarchy/Level/KeyExpression/SQL", 1);
				
				dig.addObjectCreate(root + "/Dimension/Hierarchy/Level/Property", Property.class);
					dig.addCallMethod(root + "/Dimension/Hierarchy/Level/Property", "setName", 1);
					dig.addCallParam(root + "/Dimension/Hierarchy/Level/Property", 0, "name");
					dig.addCallMethod(root + "/Dimension/Hierarchy/Level/Property", "setCaption", 1);
					dig.addCallParam(root + "/Dimension/Hierarchy/Level/Property", 0, "caption");
					dig.addCallMethod(root + "/Dimension/Hierarchy/Level/Property", "setColumnId", 1);
					dig.addCallParam(root + "/Dimension/Hierarchy/Level/Property", 0, "column");
					dig.addCallMethod(root + "/Dimension/Hierarchy/Level/Property", "setType", 1);
					dig.addCallParam(root + "/Dimension/Hierarchy/Level/Property", 0, "type");
					dig.addCallMethod(root + "/Dimension/Hierarchy/Level/Property", "setFormatter", 1);
					dig.addCallParam(root + "/Dimension/Hierarchy/Level/Property", 0, "formatter");
				dig.addSetNext(root + "/Dimension/Hierarchy/Level/Property", "addProperty");
				
				dig.addSetNext(root + "/Dimension/Hierarchy/Level", "addLevel");
			
				
				dig.addObjectCreate(root + "/Dimension/Hierarchy/Parameter", Parameter.class);
					dig.addSetProperties(root + "/Dimension/Hierarchy/Parameter", "name", "name");
					dig.addSetProperties(root + "/Dimension/Hierarchy/Parameter", "type", "type");
					dig.addSetProperties(root + "/Dimension/Hierarchy/Parameter", "description", "description");
					dig.addSetProperties(root + "/Dimension/Hierarchy/Parameter", "modifiable", "modifable");
					dig.addSetProperties(root + "/Dimension/Hierarchy/Parameter", "defaultValue", "defaultValue");
					
				dig.addSetNext(root + "/Dimension/Hierarchy/Parameter", "addParameter");
				
			dig.addSetNext(root + "/Dimension/Hierarchy", "addHierarchy");
		dig.addSetNext(root + "/Dimension", "addDimension");
			
			
		
	//	dig.addSetNext(root + "/Cube/Dimension", "addDimension");
		
		//cubes
		dig.addObjectCreate(root + "/Cube", OLAPCube.class);
			dig.addSetProperties(root + "/Cube", "name", "name");
			dig.addSetProperties(root + "/Cube", "defaultMeasure", "defaultMeasure");
			dig.addSetProperties(root + "/Cube", "enable", "enable");
			dig.addSetProperties(root + "/Cube", "cache", "cache");
			dig.addSetProperties(root + "/Cube/Table", "name", "tableName");
			
//			view insteadof FactTable
			dig.addObjectCreate(root + "/Cube/View", DataView.class);
				dig.addSetProperties(root + "/Cube/View", "alias", "name");
				dig.addCallMethod(root + "/Cube/View/SQL", "addDialect", 2);
				dig.addCallParam(root + "/Cube/View/SQL", 0, "dialect");
				dig.addCallParam(root + "/Cube/View/SQL", 1);
				
			dig.addSetNext(root + "/Cube/View", "setFactDataObject");


			
			
		//dimensionusage
		dig.addObjectCreate(root + "/Cube/DimensionUsage",DimUsage.class);
			dig.addSetProperties(root + "/Cube/DimensionUsage", "name", "name");
			dig.addSetProperties(root + "/Cube/DimensionUsage", "caption", "caption");
			dig.addSetProperties(root + "/Cube/DimensionUsage", "source", "source");
			dig.addSetProperties(root + "/Cube/DimensionUsage", "foreignKey", "fkey");
		dig.addSetNext(root + "/Cube/DimensionUsage", "addDimUsage");
		
//		//inside dims
		dig.addObjectCreate(root + "/Cube/Dimension", OLAPDimension.class);
			dig.addSetProperties(root + "/Cube/Dimension", "name", "name");
			dig.addSetProperties(root + "/Cube/Dimension", "caption", "caption");
			dig.addCallMethod(root + "/Cube/Dimension/type", "setDate", 0);
			dig.addSetProperties(root + "/Cube/Dimension", "foreignKey", "foreignKey");
			
			dig.addObjectCreate(root + "/Cube/Dimension/Hierarchy", OLAPHierarchy.class);
				dig.addSetProperties(root + "/Cube/Dimension/Hierarchy", "name", "name");
				dig.addSetProperties(root + "/Cube/Dimension/Hierarchy", "caption", "caption");
				dig.addSetProperties(root + "/Cube/Dimension/Hierarchy", "allMemberCaption", "allMemberCaption");
				dig.addSetProperties(root + "/Cube/Dimension/Hierarchy", "memberReaderClass", "memberReaderClass");
				dig.addSetProperties(root + "/Cube/Dimension/Hierarchy", "defaultMember", "defaultMember");
				dig.addSetProperties(root + "/Cube/Dimension/Hierarchy", "hasAll", "hasAll");
				dig.addSetProperties(root + "/Cube/Dimension/Hierarchy", "allMemberName", "allMember");
				dig.addSetProperties(root + "/Cube/Dimension/Hierarchy", "primaryKey", "primaryKey");
				dig.addSetProperties(root + "/Cube/Dimension/Hierarchy", "primaryKeyTable", "primaryKeyTable");
				dig.addSetProperties(root + "/Cube/Dimension/Hierarchy/Table", "name", "tableName");
				//dig.addSetProperties(root + "/Cube/Dimension/Hierarchy", "primaryKeyTable", "table");
			
				//view insteadof Table
				dig.addObjectCreate(root + "/Cube/Dimension/Hierarchy/View", DataView.class);
					dig.addSetProperties(root + "/Cube/Dimension/Hierarchy/View", "alias", "name");
					dig.addCallMethod(root + "/Cube/Dimension/Hierarchy/View/SQL", "addDialect", 2);
					dig.addCallParam(root + "/Cube/Dimension/Hierarchy/View/SQL", 0, "dialect");
					dig.addCallParam(root + "/Cube/Dimension/Hierarchy/View/SQL", 1);
				dig.addSetNext(root + "/Cube/Dimension/Hierarchy/View", "setView");

				
				
//				joint
				dig.addObjectCreate(root + "/Cube/Dimension/Hierarchy/Join", Joint.class);
				dig.addObjectCreate(root + "/Cube/Dimension/Hierarchy/Join/Table", Joint.class);
				dig.addSetProperties(root + "/Cube/Dimension/Hierarchy/Join","leftKey", "leftKey");
				dig.addSetProperties(root + "/Cube/Dimension/Hierarchy/Join","leftAlias", "leftAlias");
				dig.addSetProperties(root + "/Cube/Dimension/Hierarchy/Join","rightKey", "rightKey");
				dig.addSetProperties(root + "/Cube/Dimension/Hierarchy/Join","rightAlias", "rightAlias");
				dig.addSetProperties(root + "/Cube/Dimension/Hierarchy/Join/Table","name", "tableName");
				dig.addSetNext(root + "/Cube/Dimension/Hierarchy/Join/Table", "addChild");
				dig.addSetNext(root + "/Cube/Dimension/Hierarchy/Join", "addJoint");
				
				dig.addObjectCreate(root + "/Cube/Dimension/Hierarchy/Join/Join", Joint.class);
				dig.addObjectCreate(root + "/Cube/Dimension/Hierarchy/Join/Join/Table", Joint.class);
				dig.addSetProperties(root + "/Cube/Dimension/Hierarchy/Join/Join","leftKey", "leftKey");
				dig.addSetProperties(root + "/Cube/Dimension/Hierarchy/Join/Join","leftAlias", "leftAlias");
				dig.addSetProperties(root + "/Cube/Dimension/Hierarchy/Join/Join","rightKey", "rightKey");
				dig.addSetProperties(root + "/Cube/Dimension/Hierarchy/Join/Join","rightAlias", "rightAlias");
				dig.addSetProperties(root + "/Cube/Dimension/Hierarchy/Join/Join/Table","name", "tableName");
				dig.addSetNext(root + "/Cube/Dimension/Hierarchy/Join/Join/Table", "addChild");
				dig.addSetNext(root + "/Cube/Dimension/Hierarchy/Join/Join", "addChild");
				
				dig.addObjectCreate(root + "/Cube/Dimension/Hierarchy/Join/Join/Join", Joint.class);
				dig.addObjectCreate(root + "/Cube/Dimension/Hierarchy/Join/Join/Join/Table", Joint.class);
				dig.addSetProperties(root + "/Cube/Dimension/Hierarchy/Join/Join/Join","leftKey", "leftKey");
				dig.addSetProperties(root + "/Cube/Dimension/Hierarchy/Join/Join/Join","leftAlias", "leftAlias");
				dig.addSetProperties(root + "/Cube/Dimension/Hierarchy/Join/Join/Join","rightKey", "rightKey");
				dig.addSetProperties(root + "/Cube/Dimension/Hierarchy/Join/Join/Join","rightAlias", "rightAlias");
				dig.addSetProperties(root + "/Cube/Dimension/Hierarchy/Join/Join/Join/Table","name", "tableName");
				dig.addSetNext(root + "/Cube/Dimension/Hierarchy/Join/Join/Join/Table", "addChild");
				dig.addSetNext(root + "/Cube/Dimension/Hierarchy/Join/Join/Join", "addChild");
				
				dig.addObjectCreate(root + "/Cube/Dimension/Hierarchy/Join/Join/Join/Join", Joint.class);
				dig.addObjectCreate(root + "/Cube/Dimension/Hierarchy/Join/Join/Join/Join/Table", Joint.class);
				dig.addSetProperties(root + "/Cube/Dimension/Hierarchy/Join/Join/Join/Join","leftKey", "leftKey");
				dig.addSetProperties(root + "/Cube/Dimension/Hierarchy/Join/Join/Join/Join","leftAlias", "leftAlias");
				dig.addSetProperties(root + "/Cube/Dimension/Hierarchy/Join/Join/Join/Join","rightKey", "rightKey");
				dig.addSetProperties(root + "/Cube/Dimension/Hierarchy/Join/Join/Join/Join","rightAlias", "rightAlias");
				dig.addSetProperties(root + "/Cube/Dimension/Hierarchy/Join/Join/Join/Join/Table","name", "tableName");
				dig.addSetNext(root + "/Cube/Dimension/Hierarchy/Join/Join/Join/Join/Table", "addChild");
				dig.addSetNext(root + "/Cube/Dimension/Hierarchy/Join/Join/Join/Join", "addChild");
								
				dig.addObjectCreate(root + "/Cube/Dimension/Hierarchy/Join/Join/Join/Join/Join", Joint.class);
				dig.addObjectCreate(root + "/Cube/Dimension/Hierarchy/Join/Join/Join/Join/Join/Table", Joint.class);
				dig.addSetProperties(root + "/Cube/Dimension/Hierarchy/Join/Join/Join/Join/Join","leftKey", "leftKey");
				dig.addSetProperties(root + "/Cube/Dimension/Hierarchy/Join/Join/Join/Join/Join","leftAlias", "leftAlias");
				dig.addSetProperties(root + "/Cube/Dimension/Hierarchy/Join/Join/Join/Join/Join","rightKey", "rightKey");
				dig.addSetProperties(root + "/Cube/Dimension/Hierarchy/Join/Join/Join/Join/Join","rightAlias", "rightAlias");
				dig.addSetProperties(root + "/Cube/Dimension/Hierarchy/Join/Join/Join/Join/Join/Table","name", "tableName");
				dig.addSetNext(root + "/Cube/Dimension/Hierarchy/Join/Join/Join/Join/Join/Table", "addChild");
				dig.addSetNext(root + "/Cube/Dimension/Hierarchy/Join/Join/Join/Join/Join", "addChild");

				dig.addObjectCreate(root + "/Cube/Dimension/Hierarchy/Join/Join/Join/Join/Join/Join", Joint.class);
				dig.addObjectCreate(root + "/Cube/Dimension/Hierarchy/Join/Join/Join/Join/Join/Join/Table", Joint.class);
				dig.addSetProperties(root + "/Cube/Dimension/Hierarchy/Join/Join/Join/Join/Join/Join","leftKey", "leftKey");
				dig.addSetProperties(root + "/Cube/Dimension/Hierarchy/Join/Join/Join/Join/Join/Join","leftAlias", "leftAlias");
				dig.addSetProperties(root + "/Cube/Dimension/Hierarchy/Join/Join/Join/Join/Join/Join","rightKey", "rightKey");
				dig.addSetProperties(root + "/Cube/Dimension/Hierarchy/Join/Join/Join/Join/Join/Join","rightAlias", "rightAlias");
				dig.addSetProperties(root + "/Cube/Dimension/Hierarchy/Join/Join/Join/Join/Join/Join/Table","name", "tableName");
				dig.addSetNext(root + "/Cube/Dimension/Hierarchy/Join/Join/Join/Join/Join/Join/Table", "addChild");
				dig.addSetNext(root + "/Cube/Dimension/Hierarchy/Join/Join/Join/Join/Join/Join", "addChild");

				dig.addObjectCreate(root + "/Cube/Dimension/Hierarchy/Join/Join/Join/Join/Join/Join/Join", Joint.class);
				dig.addObjectCreate(root + "/Cube/Dimension/Hierarchy/Join/Join/Join/Join/Join/Join/Join/Table", Joint.class);
				dig.addSetProperties(root + "/Cube/Dimension/Hierarchy/Join/Join/Join/Join/Join/Join/Join","leftKey", "leftKey");
				dig.addSetProperties(root + "/Cube/Dimension/Hierarchy/Join/Join/Join/Join/Join/Join/Join","leftAlias", "leftAlias");
				dig.addSetProperties(root + "/Cube/Dimension/Hierarchy/Join/Join/Join/Join/Join/Join/Join","rightKey", "rightKey");
				dig.addSetProperties(root + "/Cube/Dimension/Hierarchy/Join/Join/Join/Join/Join/Join/Join","rightAlias", "rightAlias");
				dig.addSetProperties(root + "/Cube/Dimension/Hierarchy/Join/Join/Join/Join/Join/Join/Join/Table","name", "tableName");
				dig.addSetNext(root + "/Cube/Dimension/Hierarchy/Join/Join/Join/Join/Join/Join/Join/Table", "addChild");
				dig.addSetNext(root + "/Cube/Dimension/Hierarchy/Join/Join/Join/Join/Join/Join/Join", "addChild");

				dig.addObjectCreate(root + "/Cube/Dimension/Hierarchy/Join/Join/Join/Join/Join/Join/Join/Join", Joint.class);
				dig.addObjectCreate(root + "/Cube/Dimension/Hierarchy/Join/Join/Join/Join/Join/Join/Join/Join/Table", Joint.class);
				dig.addSetProperties(root + "/Cube/Dimension/Hierarchy/Join/Join/Join/Join/Join/Join/Join/Join","leftKey", "leftKey");
				dig.addSetProperties(root + "/Cube/Dimension/Hierarchy/Join/Join/Join/Join/Join/Join/Join/Join","leftAlias", "leftAlias");
				dig.addSetProperties(root + "/Cube/Dimension/Hierarchy/Join/Join/Join/Join/Join/Join/Join/Join","rightKey", "rightKey");
				dig.addSetProperties(root + "/Cube/Dimension/Hierarchy/Join/Join/Join/Join/Join/Join/Join/Join","rightAlias", "rightAlias");
				dig.addSetProperties(root + "/Cube/Dimension/Hierarchy/Join/Join/Join/Join/Join/Join/Join/Join/Table","name", "tableName");
				dig.addSetNext(root + "/Cube/Dimension/Hierarchy/Join/Join/Join/Join/Join/Join/Join/Join/Table", "addChild");
				dig.addSetNext(root + "/Cube/Dimension/Hierarchy/Join/Join/Join/Join/Join/Join/Join/Join", "addChild");

				dig.addObjectCreate(root + "/Cube/Dimension/Hierarchy/Join/Join/Join/Join/Join/Join/Join/Join/Join", Joint.class);
				dig.addObjectCreate(root + "/Cube/Dimension/Hierarchy/Join/Join/Join/Join/Join/Join/Join/Join/Join/Table", Joint.class);
				dig.addSetProperties(root + "/Cube/Dimension/Hierarchy/Join/Join/Join/Join/Join/Join/Join/Join/Join","leftKey", "leftKey");
				dig.addSetProperties(root + "/Cube/Dimension/Hierarchy/Join/Join/Join/Join/Join/Join/Join/Join/Join","leftAlias", "leftAlias");
				dig.addSetProperties(root + "/Cube/Dimension/Hierarchy/Join/Join/Join/Join/Join/Join/Join/Join/Join","rightKey", "rightKey");
				dig.addSetProperties(root + "/Cube/Dimension/Hierarchy/Join/Join/Join/Join/Join/Join/Join/Join/Join","rightAlias", "rightAlias");
				dig.addSetProperties(root + "/Cube/Dimension/Hierarchy/Join/Join/Join/Join/Join/Join/Join/Join/Join/Table","name", "tableName");
				dig.addSetNext(root + "/Cube/Dimension/Hierarchy/Join/Join/Join/Join/Join/Join/Join/Join/Join/Table", "addChild");
				dig.addSetNext(root + "/Cube/Dimension/Hierarchy/Join/Join/Join/Join/Join/Join/Join/Join/Join", "addChild");

				dig.addObjectCreate(root + "/Cube/Dimension/Hierarchy/Join/Join/Join/Join/Join/Join/Join/Join/Join", Joint.class);
				dig.addObjectCreate(root + "/Cube/Dimension/Hierarchy/Join/Join/Join/Join/Join/Join/Join/Join/Join/Table", Joint.class);
				dig.addSetProperties(root + "/Cube/Dimension/Hierarchy/Join/Join/Join/Join/Join/Join/Join/Join/Join","leftKey", "leftKey");
				dig.addSetProperties(root + "/Cube/Dimension/Hierarchy/Join/Join/Join/Join/Join/Join/Join/Join/Join","leftAlias", "leftAlias");
				dig.addSetProperties(root + "/Cube/Dimension/Hierarchy/Join/Join/Join/Join/Join/Join/Join/Join/Join","rightKey", "rightKey");
				dig.addSetProperties(root + "/Cube/Dimension/Hierarchy/Join/Join/Join/Join/Join/Join/Join/Join/Join","rightAlias", "rightAlias");
				dig.addSetProperties(root + "/Cube/Dimension/Hierarchy/Join/Join/Join/Join/Join/Join/Join/Join/Join/Table","name", "tableName");
				dig.addSetNext(root + "/Cube/Dimension/Hierarchy/Join/Join/Join/Join/Join/Join/Join/Join/Join/Table", "addChild");
				dig.addSetNext(root + "/Cube/Dimension/Hierarchy/Join/Join/Join/Join/Join/Join/Join/Join/Join", "addChild");

				
				
				dig.addObjectCreate(root + "/Cube/Dimension/Hierarchy/Level", OLAPLevel.class);
					dig.addSetProperties(root + "/Cube/Dimension/Hierarchy/Level", "name", "name");
					dig.addSetProperties(root + "/Cube/Dimension/Hierarchy/Level", "ordinalColumn", "ordinalColumn");
					dig.addSetProperties(root + "/Cube/Dimension/Hierarchy/Level", "caption", "caption");
					dig.addSetProperties(root + "/Cube/Dimension/Hierarchy/Level", "captionColumn", "captionColumn");
					dig.addSetProperties(root + "/Cube/Dimension/Hierarchy/Level", "formatter", "formatter");					
					dig.addSetProperties(root + "/Cube/Dimension/Hierarchy/Level", "table", "tableId");
					dig.addSetProperties(root + "/Cube/Dimension/Hierarchy/Level", "hideMemberIf", "hideMemberIf");
					dig.addSetProperties(root + "/Cube/Dimension/Hierarchy/Level", "column", "columnName");
					dig.addSetProperties(root + "/Cube/Dimension/Hierarchy/Level", "uniqueMembers", "uniquemb");
					dig.addSetProperties(root + "/Cube/Dimension/Hierarchy/Level", "type", "type");
					dig.addSetProperties(root + "/Cube/Dimension/Hierarchy/Level", "levelType", "levelType");
					dig.addSetProperties(root + "/Cube/Dimension/Hierarchy/Level", "approxRowCount", "approxRowCount");
					
					dig.addCallMethod(root + "/Cube/Dimension/Hierarchy/Level", "setParentColumnId", 1);
					dig.addCallParam(root + "/Cube/Dimension/Hierarchy/Level", 0, "parentColumn");
					
					dig.addCallMethod(root + "/Cube/Dimension/Hierarchy/Level/KeyExpression/SQL", "addKeyExpression", 2);
					dig.addCallParam(root + "/Cube/Dimension/Hierarchy/Level/KeyExpression/SQL", 0, "dialect");
					dig.addCallParam(root + "/Cube/Dimension/Hierarchy/Level/KeyExpression/SQL", 1);
					
					dig.addSetProperties(root + "/Cube/Dimension/Hierarchy/Level", "nullParentValue", "nullParentValue");
					dig.addCallMethod(root + "/Cube/Dimension/Hierarchy/Level/Closure", "setClosureParentId", 1);
					dig.addCallParam(root + "/Cube/Dimension/Hierarchy/Level/Closure", 0, "parentColumn");
					
					dig.addSetProperties(root + "/Cube/Dimension/Hierarchy/Level/Closure", "childColumn", "closureChildId");
					dig.addSetProperties(root + "/Cube/Dimension/Hierarchy/Level/Closure/Table", "name", "closureTableId");

					dig.addObjectCreate(root + "/Cube/Dimension/Hierarchy/Level/Property", Property.class);
						dig.addCallMethod(root + "/Cube/Dimension/Hierarchy/Level/Property", "setName", 1);
						dig.addCallParam(root + "/Cube/Dimension/Hierarchy/Level/Property", 0, "name");
						dig.addCallMethod(root + "/Dimension/Hierarchy/Level/Property", "setCaption", 1);
						dig.addCallParam(root + "/Dimension/Hierarchy/Level/Property", 0, "caption");
						dig.addCallMethod(root + "/Cube/Dimension/Hierarchy/Level/Property", "setColumnId", 1);
						dig.addCallParam(root + "/Cube/Dimension/Hierarchy/Level/Property", 0, "column");
						dig.addCallMethod(root + "/Cube/Dimension/Hierarchy/Level/Property", "setType", 1);
						dig.addCallParam(root + "/Cube/Dimension/Hierarchy/Level/Property", 0, "type");
						dig.addCallMethod(root + "/Cube/Dimension/Hierarchy/Level/Property", "setFormatter", 1);
						dig.addCallParam(root + "/Cube/Dimension/Hierarchy/Level/Property", 0, "formatter");
					dig.addSetNext(root + "/Cube/Dimension/Hierarchy/Level/Property", "addProperty");
					
				dig.addSetNext(root + "/Cube/Dimension/Hierarchy/Level", "addLevel");
				
				
				dig.addObjectCreate(root + "/Cube/Dimension/Hierarchy/Parameter", Parameter.class);
					dig.addSetProperties(root + "/Cube/Dimension/Hierarchy/Parameter", "name", "name");
					dig.addSetProperties(root + "/Cube/Dimension/Hierarchy/Parameter", "type", "type");
					dig.addSetProperties(root + "/Cube/Dimension/Hierarchy/Parameter", "description", "description");
					dig.addSetProperties(root + "/Cube/Dimension/Hierarchy/Parameter", "modifiable", "modifable");
					dig.addSetProperties(root + "/Cube/Dimension/Hierarchy/Parameter", "defaultValue", "defaultValue");
				dig.addSetNext(root + "/Cube/Dimension/Hierarchy/Parameter", "addParameter");
				
			dig.addSetNext(root + "/Cube/Dimension/Hierarchy", "addHierarchy");
		dig.addSetNext(root + "/Cube/Dimension", "addDim");
		
			
			//measures
			dig.addObjectCreate(root + "/Cube/Measure", OLAPMeasure.class);
				dig.addSetProperties(root + "/Cube/Measure", "name", "name");
				dig.addSetProperties(root + "/Cube/Measure", "caption", "caption");
				dig.addSetProperties(root + "/Cube/Measure", "column", "columnName");
				dig.addSetProperties(root + "/Cube/Measure", "visible", "visible");
				dig.addSetProperties(root + "/Cube/Measure", "datatype", "dataType");
				dig.addSetProperties(root + "/Cube/Measure", "formatter", "formatter");
				dig.addSetProperties(root + "/Cube/Measure", "aggregator", "aggregator");
				dig.addSetProperties(root + "/Cube/Measure", "formatString", "formatstr");
				
				dig.addCallMethod(root + "/Cube/Measure/MeasureExpression/SQL", "addExpression", 2);
				dig.addCallParam(root + "/Cube/Measure/MeasureExpression/SQL", 0, "dialect");
				dig.addCallParam(root + "/Cube/Measure/MeasureExpression/SQL", 1);
				
				dig.addCallMethod(root + "/Cube/Measure/CalculatedMemberProperty","addPropExpression",2);
				dig.addCallParam(root + "/Cube/Measure/CalculatedMemberProperty/", 0, "name");
				dig.addCallParam(root + "/Cube/Measure/CalculatedMemberProperty/", 1, "expression");
				dig.addCallParam(root + "/Cube/Measure/CalculatedMemberProperty/", 1, "value");

				
			dig.addSetNext(root + "/Cube/Measure", "addMes");
//			
//			//calculatedMeasures
			dig.addObjectCreate(root + "/Cube/CalculatedMember", OLAPMeasure.class);
				dig.addSetProperties(root + "/Cube/CalculatedMember", "name", "name");
				dig.addSetProperties(root + "/Cube/CalculatedMember", "visible", "visible");
				dig.addSetProperties(root + "/Cube/CalculatedMember", "datatype", "dataType");
				dig.addSetProperties(root + "/Cube/CalculatedMember", "formatter", "formatter");
				dig.addSetProperties(root + "/Cube/CalculatedMember", "dimension", "dimensionName");
				
				dig.addCallMethod(root + "/Cube/CalculatedMember/CalculatedMemberProperty","addPropExpression",2);
				dig.addCallParam(root + "/Cube/CalculatedMember/CalculatedMemberProperty/", 0, "name");
				//dig.addCallMethod(root + "/Cube/CalculatedMember/CalculatedMemberProperty","setPropExpression",1);
				dig.addCallParam(root + "/Cube/CalculatedMember/CalculatedMemberProperty/", 1, "expression");
				dig.addCallParam(root + "/Cube/CalculatedMember/CalculatedMemberProperty/", 1, "value");
				
				dig.addCallMethod(root + "/Cube/CalculatedMember/Formula", "setFormula", 0);
				
				
				
			dig.addSetNext(root + "/Cube/CalculatedMember", "addMes");
			
			
			//aggregate tables
			dig.addObjectCreate(root + "/Cube/Table/AggName", MondrianAgg.class);
				dig.addSetProperties(root + "/Cube/Table/AggName", "name", "name");
				dig.addSetProperties(root + "/Cube/Table/AggName/AggFactCount","column","factCount");
				
				dig.addCallMethod(root + "/Cube/Table/AggName/AggMeasure", "addAggMes", 2);
				dig.addCallParam(root + "/Cube/Table/AggName/AggMeasure", 0, "name");
				dig.addCallParam(root + "/Cube/Table/AggName/AggMeasure", 1, "column");
				
				dig.addCallMethod(root + "/Cube/Table/AggName/AggLevel", "addAggLvl", 2);				
				dig.addCallParam(root + "/Cube/Table/AggName/AggLevel", 0, "name");
				dig.addCallParam(root + "/Cube/Table/AggName/AggLevel", 1, "column");

				dig.addCallMethod(root + "/Cube/Table/AggName/AggForeignKey", "addAggFKey", 2);				
				dig.addCallParam(root + "/Cube/Table/AggName/AggForeignKey", 0, "factColumn");
				dig.addCallParam(root + "/Cube/Table/AggName/AggForeignKey", 1, "aggColumn");

				
				
			dig.addSetNext(root + "/Cube/Table/AggName", "addMondrianAgg");
			
//			aggregatespattern
			dig.addObjectCreate(root + "/Cube/Table/AggPattern", AggPattern.class);
				dig.addSetProperties(root + "/Cube/Table/AggPattern", "pattern", "pattern");
				
				dig.addObjectCreate(root + "/Cube/Table/AggPattern/AggExclude", AggExclude.class);
					dig.addSetProperties(root + "/Cube/Table/AggPattern/AggExclude", "pattern", "pattern");
					dig.addSetProperties(root + "/Cube/Table/AggPattern/AggExclude", "name", "name");
					dig.addSetProperties(root + "/Cube/Table/AggPattern/AggExclude", "ignorecase", "ignoreCase");
				dig.addSetNext(root + "/Cube/Table/AggPattern/AggExclude", "addExcluded");
			dig.addSetNext(root + "/Cube/Table/AggPattern", "addAggPattern");
			
			
			//namedset
			dig.addObjectCreate(root + "/Cube/NamedSet", NamedSet.class);
				dig.addSetProperties(root + "/Cube/NamedSet", "name", "name");
				dig.addCallMethod(root + "/Cube/NamedSet/Formula", "setFormula", 0);
			dig.addSetNext(root + "/Cube/NamedSet", "addNamedSet");
			
			
		dig.addSetNext(root + "/Cube", "addCube");
		
		dig.addObjectCreate(root + "/VirtualCube", VirtualCube.class);
			dig.addSetProperties(root + "/VirtualCube", "name", "name");
			dig.addSetProperties(root + "/VirtualCube", "caption", "caption");
			dig.addSetProperties(root + "/VirtualCube", "enable", "enable");
			dig.addSetProperties(root + "/VirtualCube", "cache", "cache");
			
			dig.addObjectCreate(root + "/VirtualCube/VirtualCubeDimension", VirtualDimension.class);
				dig.addSetProperties(root + "/VirtualCube/VirtualCubeDimension", "cubeName", "cubeName");
				dig.addSetProperties(root + "/VirtualCube/VirtualCubeDimension", "name", "dimName");
				dig.addSetProperties(root + "/VirtualCube/VirtualCubeDimension", "visible", "visible");
			dig.addSetNext(root + "/VirtualCube/VirtualCubeDimension", "addVirtualDimension");
			
			dig.addObjectCreate(root + "/VirtualCube/CalculatedMember", OLAPMeasure.class);
				dig.addSetProperties(root + "/VirtualCube/CalculatedMember", "name", "name");
				dig.addSetProperties(root + "/VirtualCube/CalculatedMember", "visible", "visible");
				dig.addSetProperties(root + "/VirtualCube/CalculatedMember", "datatype", "dataType");
				dig.addSetProperties(root + "/VirtualCube/CalculatedMember", "formatter", "formatter");
				dig.addSetProperties(root + "/VirtualCube/CalculatedMember", "dimension", "dimensionName");
				dig.addCallMethod(root + "/VirtualCube/CalculatedMember/CalculatedMemberProperty","setFormatstr",1);
				dig.addCallParam(root + "/VirtualCube/CalculatedMember/CalculatedMemberProperty/", 0, "value");
				dig.addCallMethod(root + "/VirtualCube/CalculatedMember/Formula", "setFormula", 0);
			dig.addSetNext(root + "/VirtualCube/CalculatedMember", "addCalcMeasure");
			
			dig.addObjectCreate(root + "/VirtualCube/VirtualCubeMeasure", VirtualMeasure.class);
				dig.addSetProperties(root + "/VirtualCube/VirtualCubeMeasure", "name", "measureName");
				dig.addSetProperties(root + "/VirtualCube/VirtualCubeMeasure", "cubeName", "cubeName");
			dig.addSetNext(root + "/VirtualCube/VirtualCubeMeasure", "addVirtualMeasure");
//			dig.addObjectCreate(root + "/VirtualCube/CalculatedMember", VirtualMeasure.class);
//				dig.addSetProperties(root + "/VirtualCube/CalculatedMember", "cubeName", "cubeName");
//				dig.addSetProperties(root + "/VirtualCube/CalculatedMember", "name", "measureName");
//			dig.addSetNext(root + "/VirtualCube/CalculatedMember", "addVirtualMeasure");

			
			
			dig.addObjectCreate(root + "/VirtualCube/NamedSet", NamedSet.class);
				dig.addSetProperties(root + "/VirtualCube/NamedSet", "name", "name");
				dig.addCallMethod(root + "/VirtualCube/NamedSet/Formula", "setFormula", 0);
			dig.addSetNext(root + "/VirtualCube/NamedSet", "addNamedSet");
			
		dig.addSetNext(root + "/VirtualCube", "addVirtualCube");
		
		
		//security
		dig.addObjectCreate(root + "/Role", RoleGrantBean.class);
			dig.addSetProperties(root + "/Role", "name", "name");
			dig.addObjectCreate(root + "/Role/SchemaGrant", SchemaGrantBean.class);
			dig.addSetProperties(root + "/Role/SchemaGrant", "access", "access");
				dig.addObjectCreate(root + "/Role/SchemaGrant/CubeGrant", CubeGrantBean.class);
				dig.addSetProperties(root + "/Role/SchemaGrant/CubeGrant", "cube", "cube");
				dig.addSetProperties(root + "/Role/SchemaGrant/CubeGrant", "access", "access");
					dig.addObjectCreate(root + "/Role/SchemaGrant/CubeGrant/HierarchyGrant", HierarchyGrantBean.class);
					dig.addSetProperties(root + "/Role/SchemaGrant/CubeGrant/HierarchyGrant", "hierarchy", "hiera");
					dig.addSetProperties(root + "/Role/SchemaGrant/CubeGrant/HierarchyGrant", "topLevel", "topLevel");
					dig.addSetProperties(root + "/Role/SchemaGrant/CubeGrant/HierarchyGrant", "bottomLevel", "bottomLevel");
					dig.addSetProperties(root + "/Role/SchemaGrant/CubeGrant/HierarchyGrant", "access", "access");
						dig.addObjectCreate(root + "/Role/SchemaGrant/CubeGrant/HierarchyGrant/MemberGrant", MemberGrantBean.class);
							dig.addSetProperties(root + "/Role/SchemaGrant/CubeGrant/HierarchyGrant/MemberGrant", "member", "member");
							dig.addSetProperties(root + "/Role/SchemaGrant/CubeGrant/HierarchyGrant/MemberGrant", "access", "access");
						dig.addSetNext(root + "/Role/SchemaGrant/CubeGrant/HierarchyGrant/MemberGrant", "addMember");
						
					dig.addSetNext(root + "/Role/SchemaGrant/CubeGrant/HierarchyGrant", "addHiera");
				dig.addSetNext(root + "/Role/SchemaGrant/CubeGrant", "addCubeGrant");
			dig.addSetNext(root + "/Role/SchemaGrant", "setSchema");
		dig.addSetNext(root + "/Role", "addMondrianRole");
		
		try {
			
			//build Schema
			schema = (OLAPSchema) dig.parse(f);
			model.setOLAPSchema(schema);
			
			for(OLAPCube cube : schema.getCubes()){
				//old new
				HashMap<OLAPDimension, OLAPDimension> dims = new HashMap<OLAPDimension, OLAPDimension>();
				for(OLAPDimension d : cube.getDims()){
					OLAPDimension dim = schema.findDimensionByName(d.getName());
					if (dim == null)
						schema.addDimension(d);
					else{
						dims.put(d, dim);
					}
				}
				for(OLAPDimension d : dims.keySet()){
					cube.removeDim(d);
					cube.addDim(dims.get(d));
				}

				for(OLAPMeasure m  :cube.getMes()){
//					boolean exists = false;
//					for(OLAPMeasure mm : schema.getMeasures()){
//						if (mm.getColumnName().equals(m.getColumnName())){
//							exists = true;
//							break;
//						}
//					}
//					if ((m.getType().equals("physical") && !exists) || (!m.getType().equals("physical")))
						schema.addMeasure(m);
				}
				for(DimUsage d : cube.getDimUsages()){
					OLAPDimension dim = schema.findDimensionByName(d.getSource());
					cube.addDim(dim);
				}
			}
			
			//getting calculated Measure from virtualcubes
			for(VirtualCube v : model.getOLAPSchema().getVirtualCubes()){
				for(OLAPMeasure m : v.getCalcMeasure()){
					model.getOLAPSchema().addMeasure(m);
				}
			}
			
			
			buildDataSource(schema);
			for(OLAPCube c : schema.getCubes()){
				for(OLAPDimension d : c.getDims()){
					for(OLAPHierarchy h : d.getHierarchies()){
						for(OLAPLevel l : h.getLevels()){
							if (l.getItem() == null){
								l.setItem(c.getFactDataObject().findItemNamed(l.getColumnName()));
								l.setOrderItem(c.getFactDataObject().findItemNamed(l.getOrdinalColumn()));
							}
						}
					}
				}
				buildRelations(c);
			}
				
			
			addJoinedLevelsCols();
			for(OLAPCube c : model.getOLAPSchema().getCubes())
				c.setDataSource(dataSource);
			
//			build select statement
			for(DataObject o : dataSource.getDataObjects()){
				boolean first = true;
				String s = "SELECT ";
				for(DataObjectItem i : o.getColumns()){
					if (first){
						first = !first;
						s += i.getOrigin();
					}
					else{
						s += ", " + i.getOrigin();
					}
				}
				o.setSelectStatement(s + " FROM " + o.getName());
			}
			
			
			//make reference on measure and dimension inside virttualcube
			for(VirtualCube vc : model.getOLAPSchema().getVirtualCubes()){
				
				vc.setDataSource(dataSource);
				
				for(VirtualDimension vd : vc.getVirtualDimensions()){
//					if (vd.getCubeName().equals("")){
//						vd.setShared(true);
//					}
////					else{
////						vd.setCube(model.getOLAPSchema().findCubeNamed(vd.getCubeName()));
////					}
					
					vd.setDim(model.getOLAPSchema().findDimensionByName(vd.getDimName()));
					
				}
				
				for(VirtualMeasure vm : vc.getVirtualMeasure()){
					vm.setCube(model.getOLAPSchema().findCubeNamed(vm.getCubeName()));
					if (vm.getCube() == null)
						break;
					for(OLAPMeasure m : vm.getCube().getMes()){
						if (m.getName().equals(vm.getMeasureName())){
							vm.setMes(m);
							break;
						}
					}
				}
				
				
			}
			//building FASDAggregateObjects for all Cube
			for(OLAPCube c : model.getOLAPSchema().getCubes()){
				for(MondrianAgg m :c.getMondrianAgg()){
					AggregateTable agg = new AggregateTable();
					agg.setFactCountColumn(dataSource.findItemNamed(m.getName()));
					agg.setFactCountColumn(dataSource.findItemNamed(m.getFactCount()));
					agg.setTable(dataSource.findDataObjectNamed(m.getName()));
					c.addAggTable(agg);
					model.getOLAPSchema().addAggregate(agg);
					
					for(MondrianAggLvl l :m.getLvl()){
						AggLevel aggL = new AggLevel();
						aggL.setColumn(dataSource.findDataObjectNamed(m.getName()).findItemNamed(l.getColumn()));
						aggL.setLvl(model.getOLAPSchema().findLevelNamed(l.getLevelName()));
						agg.addAggLevel(aggL);
					}
					
					for(MondrianAggMes t :m.getMes()){
						AggMeasure aggM = new AggMeasure();
						aggM.setColumn(dataSource.findDataObjectNamed(m.getName()).findItemNamed(t.getColumn()));
						aggM.setMes(model.getOLAPSchema().findMeasureNamed(t.getMesName()));
						agg.addAggMeasure(aggM);
					}
					
					for(MondrianAggFKey key : m.getFKey()){
						DataObject fact = c.getFactDataObject();
						DataObject aggT = agg.getTable();
						
						DataObjectItem factCol = fact.findItemNamed(key.getFactCol()); 
						DataObjectItem aggCol = aggT.findItemNamed(key.getAggCol()); 
						OLAPRelation r = new OLAPRelation();
						r.setLeftObjectItem(factCol);
						r.setRightObjectItem(aggCol);
						model.addRelation(r);
					}

				}
			}
			
			for(OLAPDimension d : model.getOLAPSchema().getDimensions()){
				for(OLAPHierarchy h : d.getHierarchies()){
					for(OLAPLevel l : h.getLevels()){
						for(Property p : l.getProperties()){
							DataObjectItem it = l.getItem().getParent().findItemNamed(p.getColumnId());
							
							//XXX: jnr bug with Snowflakes and Properties
							//properties arent dont generate their columns in the buidDatasource method
							//must look for where to add the roight code
							if (it == null){
								it = new DataObjectItem();
								it.setClasse("java.lang.Number");
								l.getItem().getParent().addDataObjectItem(it);
								it.setName(p.getColumnId());
								it.setOrigin(p.getColumnId());
								
							}
							p.setColumn(it);
							
							
						}
					}
				}
			}
			
			//namedSet
			for(OLAPCube c : model.getOLAPSchema().getCubes()){
				for(NamedSet ns : c.getNamedSets()){
					model.getOLAPSchema().addNamedSet(ns);
				}
				
			}

			
			//build SecurityGrants from Mondrian beans
			//build dimensionView
			for(OLAPDimension d : model.getOLAPSchema().getDimensions()){
				SecurityDim s = model.getOLAPSchema().findSecurityDim(d);
				if (s == null){
					s = new SecurityDim();
					s.setDim(d);
					model.getOLAPSchema().addDimView(s);
				}
			}
			
			//build roles
			for(RoleGrantBean r : model.getOLAPSchema().getMondrianRoles()){
				SecurityGroup g = model.getOLAPSchema().findSecurityGroupNamed(r.getName());
				if (g == null){
					g = new SecurityGroup(r.getName());
					model.getOLAPSchema().addSecurityGroup(g);
				}
				
				for(CubeGrantBean c :  r.getSchema().getCubes()){
					OLAPCube cube = model.getOLAPSchema().findCubeNamed(c.getCube());
//					if (cube != null){
//						cube.addSecurityGroup(g);
//					}
					
					for(HierarchyGrantBean h : c.getHieras()){
						OLAPHierarchy hiera = cube.findHierarchyNamed(h.getHiera()/*.substring(1, h.getHiera().length() - 1)*/);
						if (hiera != null){
							View v = new View();
							v.setGroup(g);
							v.setHierarchy(hiera);
							v.setName("viewName");
							SecurityDim sd = model.getOLAPSchema().findSecurityDim(hiera.getParent());
							sd.addView(v);
							if (h.getAccess().equals("all")){
								v.setAllowFullAccess(true);
							}
							else if (h.getAccess().equals("none")){
								v.setAllowFullAccess(false);
							}
							else{
								
								for(MemberGrantBean m : h.getMembers()){
									v.addMember(m.getMember());
									if (m.getAccess().equals("none")){
										v.setAllowFullAccess(true);
									}
									else{
										v.setAllowFullAccess(false);
									}
								}
							}
						}
						
					}
				}
			}
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			//System.out.println("NB Dims = " + schema.getDimensions().size());
		}
	}
	
	private void addJoinedLevelsCols() {
		for(OLAPDimension d : schema.getDimensions()){
			for(OLAPHierarchy h : d.getHierarchies()){
				if (h.getJoints().size() > 0){
					for(OLAPLevel l : h.getLevels()){
						DataObject table = dataSource.findDataObjectNamed(l.getTableId());
						if (table == null){
							table = new DataObject();
							table.setName(l.getTableId());
							table.setDataObjectType("dimension");
							dataSource.addDataObject(table);
						}
						
						DataObjectItem it = table.findItemNamed(l.getColumnName());
						if (it == null){
							it = new DataObjectItem();
							it.setClasse("java.lang.Object");
							it.setName(l.getColumnName());
							it.setOrigin(l.getColumnName());
							
							table.addDataObjectItem(it);
							
						}
						l.setItem(it);
					}
				}
			}
		}
		
	}

	public FAModel getModel() {
		return model;
	}
	
	public DataSource getDataSource(){
		return dataSource;
	}
	
	private void buildDataSource(OLAPSchema schema){
		dataSource.setDSName("Dummy");
		dataSource.setDriver(new DataSourceConnection());
		
		for(OLAPCube c : schema.getCubes()){
			DataObject fact = null;
			
			if (c.getFactDataObject() instanceof DataView){
				fact = c.getFactDataObject();
				String select = ((DataView)fact).getSql("generic");
				dataSource.addDataObject(fact);
				fact.setSelectStatement(select);
				fact.setDataObjectType("fact");
			}
			else{
				fact = dataSource.findDataObjectNamed(c.getTableName());
				
				if (fact == null){
					fact = new DataObject();
					fact.setName(c.getTableName());
					fact.setDataObjectType("fact");
					dataSource.addDataObject(fact);
				}
					
				c.setFactDataObject(fact);
				
			}
			
			
			
			
			//dimUsage
			for(DimUsage d : c.getDimUsages()){
				DataObjectItem fk = fact.findItemNamed(d.getFkey());
				
				if (fk == null){
					fk = new DataObjectItem();
					
					fk.setName(d.getFkey());
					fk.setOrigin(d.getFkey());
					fk.setClasse("java.lang.Object");
					fact.addDataObjectItem(fk);
				}
				
			}
			
			//measure
			for(OLAPMeasure m : c.getMes()){
				
				if (m.getExpressions().size() > 0){
					DataObjectItem col = fact.findItemNamed(m.getName());
					if (col== null){
						col = new DataObjectItem();
						col.setClasse("java.lang.Number");
						fact.addDataObjectItem(col);
						col.setOrigin(m.getExpressions().get("generic"));
						col.setName(m.getName());
						
					}
				}
				
				if (!m.getColumnName().equals("")){
					DataObjectItem col = fact.findItemNamed(m.getColumnName());
					
					if (col== null){
						col = new DataObjectItem();
						col.setClasse("java.lang.Number");
						fact.addDataObjectItem(col);
						col.setName(m.getColumnName());
						col.setOrigin(m.getColumnName());
					}
						
					m.setType("physical");
					m.setOrigin(col);
				}
				else{
					m.setType("calculated");
				}
				
			}
		}
		
		
		
		for(OLAPDimension d : schema.getDimensions()){
			//inside cube dim definition
			if (!d.getForeignKey().equals("")){
				for(OLAPCube c : schema.getCubes()){
					if (c.getDims().contains(d)){
						DataObjectItem col = c.getFactDataObject().findItemNamed(d.getForeignKey()) ;
						if (col == null){
							col = new DataObjectItem();
							col.setName(d.getForeignKey());
							col.setOrigin(d.getForeignKey());
							col.setClasse("java.lang.Object");
							c.getFactDataObject().addDataObjectItem(col);
						}
					}
				}
			}
			else{
				for(OLAPCube c : schema.getCubes()){
					if (c.getDims().contains(d)){
						for(OLAPHierarchy h : d.getHierarchies()){
							if (h.getTableName().equals("") && h.getPrimaryKeyTable().equals("") && h.getInlineBean() == null){
								
								for(OLAPLevel l : h.getLevels()){
									DataObjectItem col = c.getFactDataObject().findItemNamed(l.getColumnName()) ;
									if (!l.getName().equals("") && col == null){
										col = new DataObjectItem();
										col.setClasse("java.lang.Object");
										col.setName(l.getColumnName());
										col.setOrigin(l.getColumnName());
										c.getFactDataObject().addDataObjectItem(col);
									}
									
									for(Property p : l.getProperties()){
										System.out.println("property1");
										DataObjectItem it = c.getFactDataObject().findItemNamed(p.getColumnId());
										if (!p.getName().equals("") && it == null){
											it = new DataObjectItem();
											it.setClasse("java.lang.Object");
											it.setName(p.getColumnId());
											it.setOrigin(p.getColumnId());
											c.getFactDataObject().addDataObjectItem(it);
											p.setColumn(it);
										}
									}
								}
							}
						}
					}
				}
			}
			//all dimensions
			for(OLAPHierarchy h : d.getHierarchies()){
				if (h.getView() != null){
					dataSource.addDataObject(h.getView());
				}
				//inlinetable
				if (h.getInlineBean() != null){
					InlineBean bean = h.getInlineBean();
					DataObject t = dataSource.findDataObjectNamed(bean.getAlias());
					if (t == null)
						t = new DataObject(bean.getAlias());
					
					for(ColDef cd : bean.getColDef()){
						DataObjectItem it = t.findItemNamed(cd.getName());
						if (it == null){
							it = new DataObjectItem();
							it.setName(cd.getName());
							it.setOrigin(cd.getName());
							it.setAttribut("U");
							it.setClasse("java.lang.Object");
							it.setSqlType(cd.getType());
							t.addDataObjectItem(it);
						}
					}
					t.setInline(true);
					t.setDatas(bean.getDatas());
					dataSource.addDataObject(t);
				}
				
				//without join
				if (!h.getTableName().equals("") || h.getInlineBean() != null || h.getView()!= null){
					DataObject dTable ;
					
					if (h.getInlineBean() != null){
						dTable = dataSource.findDataObjectNamed(h.getInlineBean().getAlias());
					}
					else if (h.getView() != null){
						dTable = h.getView();
					}
					else
						dTable = dataSource.findDataObjectNamed(h.getTableName());
					
					if (dTable == null){
						dTable = new DataObject();
						dTable.setName(h.getTableName());
						dTable.setDataObjectType("dimension");
						dataSource.addDataObject(dTable);
					}
					
					
					
					DataObjectItem fk = dTable.findItemNamed(h.getPrimaryKey());
					if (fk == null){
						fk = new DataObjectItem();
						fk.setName(h.getPrimaryKey());
						fk.setOrigin(h.getPrimaryKey());
						fk.setClasse("java.lang.Object");
						dTable.addDataObjectItem(fk);
					}

					for(OLAPLevel l : h.getLevels()){
						
						if (l.getKeyExpressions().size() > 0){
							DataObjectItem col = dTable.findItemNamed(l.getName());
							if (l.getColumnName().equals("") && col == null){
								col = new DataObjectItem();
								col.setName(l.getName());
								col.setOrigin(l.getKeyExpressions().get("generic"));
								col.setClasse("java.lang.Object");
								dTable.addDataObjectItem(col);
							}
						}
						if (!l.getOrdinalColumn().equals("")){
							DataObjectItem col = dTable.findItemNamed(l.getOrdinalColumn());
							if (col == null){
								col = new DataObjectItem();
								col.setName(l.getOrdinalColumn());
								col.setOrigin(l.getOrdinalColumn());
								col.setClasse("java.lang.Object");
								dTable.addDataObjectItem(col);
								l.setOrderItem(col);
							}
						}
						
						
						DataObjectItem col = dTable.findItemNamed(l.getColumnName());
						if (!l.getColumnName().equals("") && col == null){
							col = new DataObjectItem();
							col.setName(l.getColumnName());
							col.setOrigin(l.getColumnName());
							col.setClasse("java.lang.Object");
							dTable.addDataObjectItem(col);
						}
						
						for(Property p : l.getProperties()){
							//System.out.println("property");
							DataObjectItem it = dTable.findItemNamed(p.getColumnId());
							if (!p.getName().equals("") && it == null){
								it = new DataObjectItem();
								it.setClasse("java.lang.Object");
								it.setName(p.getColumnId());
								it.setOrigin(p.getColumnId());
								dTable.addDataObjectItem(it);
								p.setColumn(it);
							}
						}
																	
						l.setItem(col);
						
						if (!l.getClosureParentId().equals("")){
							DataObjectItem c = dTable.findItemNamed(l.getClosureParentId());
							if ( c == null){
								c = new DataObjectItem();
								c.setName(l.getClosureParentId());
								c.setOrigin(l.getClosureParentId());
								c.setClasse("java.lang.Object");
								dTable.addDataObjectItem(c);
							}
							
							l.setClosureParentCol(c);
							
							OLAPRelation r = new OLAPRelation();
							r.setLeftObjectItem(c);
							r.setRightObjectItem(col);
							if (!isInRelation(c, col)){
								relations.add(r);
							}
									
							
							if (!l.getClosureTableId().equals("")){
								DataObject closTable = dataSource.findDataObjectNamed(l.getClosureTableId());
								if (closTable == null){
									closTable = new DataObject();
									closTable.setName(l.getClosureTableId());
									closTable.setDataObjectType("closure");
									dataSource.addDataObject(closTable);
								}
								
								l.setClosureTable(closTable);
								
								DataObjectItem child = closTable.findItemNamed(l.getClosureChildId());
								if (child == null){
									child = new DataObjectItem();
									child.setName(l.getClosureChildId());
									child.setOrigin(l.getClosureChildId());
									child.setClasse("java.lang.Object");
									closTable.addDataObjectItem(child);
								}
								
								l.setClosureChildCol(child);
								
								DataObjectItem par = closTable.findItemNamed(l.getClosureParentId());
								if (par == null){
									par = new DataObjectItem();
									par.setName(l.getClosureParentId());
									par.setOrigin(l.getClosureParentId());
									par.setClasse("java.lang.Object");
									closTable.addDataObjectItem(par);
								}
								
								l.setClosureParentCol(par);
								
								OLAPRelation rr = new OLAPRelation();
								rr.setLeftObjectItem(par);
								rr.setRightObjectItem(child);
								
								if (!isInRelation(par, child)){
									relations.add(rr);
								}
									
								
							}
						}
					}
				}
				//with join
				else if (!h.getPrimaryKeyTable().equals("")){
					DataObject t = dataSource.findDataObjectNamed(h.getPrimaryKeyTable());
					if (t == null){
						t = new DataObject();
						t.setName(h.getPrimaryKeyTable());
						dataSource.addDataObject(t);
					}
					
					DataObjectItem pk = t.findItemNamed(h.getPrimaryKey());
					if (pk == null){
						pk = new DataObjectItem();
						pk.setName(h.getPrimaryKey());
						pk.setOrigin(h.getPrimaryKey());
						pk.setClasse("java.lang.Object");
						t.addDataObjectItem(pk);
					}
					h.setPrimaryKeyTableCol(pk);
					
				
					for(Joint j : h.getJoints()){
						j.buildTables();
						if (j.getTableName().equals("") && j.getLeftAlias().equals("")){
							
							DataObject o = j.findTable(j.getChilds().get(0).getTableName());
							if (dataSource.findDataObjectNamed(o.getName()) == null)
								dataSource.addDataObject(o);
							DataObjectItem i = o.findItemNamed(j.getLeftKey());
							if (i == null){
								i = new DataObjectItem();
								i.setName(j.getLeftKey());
								i.setOrigin(i.getName());
								i.setClasse("java.lang.Object");
								o.addDataObjectItem(i);
								
							}
							
						}
						if (j.getTableName().equals("") && j.getRightAlias().equals("")){
							DataObject o = j.findTable(j.getChilds().get(1).getTableName());
							if (dataSource.findDataObjectNamed(o.getName()) == null)
								dataSource.addDataObject(o);
							DataObjectItem i = o.findItemNamed(j.getRightKey());
							if (i == null){
								i = new DataObjectItem();
								i.setName(j.getRightKey());
								i.setOrigin(i.getName());
								i.setClasse("java.lang.Object");
								o.addDataObjectItem(i);
								
							}
						}
						j.buildAllRelations();
					}
					for(DataObject o : Joint.list){
						DataObject table = dataSource.findDataObjectNamed(o.getName());
						if (table == null){
							table = new DataObject();
							dataSource.addDataObject(table);
							if (o.getDataObjectType().equals(""))
								table.setDataObjectType("java.lang.Object");
							else
								table.setDataObjectType(o.getDataObjectType());
							table.setName(o.getName());
						}
						else{
							for(DataObjectItem i : o.getColumns()){
								if (table.findItemNamed(i.getName()) == null)
									table.addDataObjectItem(i);
							}
						}
					}
					for(OLAPRelation r : Joint.rels){
						if (!isInRelation(r.getLeftObjectItem(), r.getRightObjectItem())){
							relations.add(r);
						}
								
					}
					
				}
				
			}
		}
		
		List<DataObject> lo = new ArrayList<DataObject>();
		for(DataObject o : dataSource.getDataObjects()){
			if (o.getName().trim().equals(""))
				lo.add(o);
		}
		for(DataObject o : lo){
			dataSource.removeDataObject(o);
		}
		
		for(DataObject o : dataSource.getDataObjects()){
			List<DataObjectItem> l = new ArrayList<DataObjectItem>();
			
			for(DataObjectItem i : o.getColumns()){
				if (i.getOrigin().trim().equals(""))
					l.add(i);
			}
			
			for(DataObjectItem i : l){
				i.getParent().removeItem(i);
			}
		}
		
		//build aggregate table
		for(OLAPCube c : model.getOLAPSchema().getCubes()){
			for(MondrianAgg a : c.getMondrianAgg()){
				DataObject table = dataSource.findDataObjectNamed(a.getName());
				if (table == null){
					table = new DataObject(a.getName());
					dataSource.addDataObject(table);
				}
				
				//factcount column
				DataObjectItem item = table.findItemNamed(a.getFactCount());
				if (item == null){
					item = new DataObjectItem(a.getFactCount());
					table.addDataObjectItem(item);
					item.setClasse("java.lang.Number");
					item.setOrigin(a.getFactCount());
				}

				
				for(MondrianAggLvl l : a.getLvl()){
					DataObjectItem col = table.findItemNamed(l.getColumn());
					if (col == null){
						col = new DataObjectItem(l.getColumn());
						table.addDataObjectItem(col);
						col.setClasse("java.lang.Object");
						col.setOrigin(l.getColumn());
					}
					
				}
				for(MondrianAggMes m : a.getMes()){
					DataObjectItem col = table.findItemNamed(m.getColumn());
					if (col == null){
						col = new DataObjectItem(m.getColumn());
						table.addDataObjectItem(col);
						col.setClasse("java.lang.Number");
						col.setOrigin(m.getColumn());
					}
				}
				for(MondrianAggFKey key : a.getFKey()){
					DataObject fact = c.getFactDataObject();
					
					DataObjectItem factCol = fact.findItemNamed(key.getFactCol()); 
					if (factCol == null){
						factCol = new DataObjectItem(key.getFactCol());
						factCol.setClasse("java.lang.Object");
						factCol.setOrigin(key.getFactCol());
						fact.addDataObjectItem(factCol);
					}
					
					DataObjectItem aggCol =table.findItemNamed(key.getAggCol()); 
					if (aggCol == null){
						aggCol = new DataObjectItem(key.getAggCol());
						aggCol.setClasse("java.lang.Object");
						aggCol.setOrigin(key.getAggCol());
						table.addDataObjectItem(aggCol);
					}

				}
			}
		}
	}
	
	private void buildRelations(OLAPCube cube){
		for(DimUsage u : cube.getDimUsages()){
			OLAPDimension dim = schema.findDimensionByName(u.getSource());
			if(dim != null){
				for(OLAPHierarchy h : dim.getHierarchies()){
					if (!h.getTableName().equals("") || h.getView()!= null){
						
						if (h.getView()!= null){
							DataObject table = h.getView();
							DataObjectItem pk = table.findItemNamed(h.getPrimaryKey());
							OLAPRelation r = new OLAPRelation();
							r.setLeftObjectItem(cube.getFactDataObject().findItemNamed(u.getFkey()));
							r.setRightObjectItem(pk);
							
							
							if (!isInRelation(r.getRightObjectItem(), r.getLeftObjectItem())){
								relations.add(r);
							}
						}
						else{
							DataObject table = dataSource.findDataObjectNamed(h.getTableName());
							DataObjectItem pk = table.findItemNamed(h.getPrimaryKey());
							 
							OLAPRelation r = new OLAPRelation();
							r.setLeftObjectItem(cube.getFactDataObject().findItemNamed(u.getFkey()));
							r.setRightObjectItem(pk);
							
							
							if (!isInRelation(r.getRightObjectItem(), r.getLeftObjectItem())){
								relations.add(r);
							}
						}
						
							
					}
					else if (h.getInlineBean() != null){
						DataObject dd = dataSource.findDataObjectNamed(h.getInlineBean().getAlias());
						
						DataObjectItem pk = dd.findItemNamed(h.getPrimaryKey());
						OLAPRelation r = new OLAPRelation();
						r.setLeftObjectItem(cube.getFactDataObject().findItemNamed(u.getFkey()));
						r.setRightObjectItem(pk);
						
						
						if (!isInRelation(r.getRightObjectItem(), r.getLeftObjectItem())){
							relations.add(r);
						}
					}
					else{
						OLAPRelation rr = new OLAPRelation();
						rr.setLeftObjectItem(cube.getFactDataObject().findItemNamed(u.getFkey()));
						rr.setRightObjectItem(h.getPrimaryKeyTableCol());
						rr.setName(rr.getName());
						if (!isInRelation(rr.getRightObjectItem(), rr.getLeftObjectItem())){
							relations.add(rr);
						}
							
						
						for(Joint j : h.getJoints()){
							
							if (j.getTableName().equals("") && j.getLeftAlias().equals("")){
								DataObject o = j.findTable(j.getChilds().get(0).getTableName());
								DataObjectItem pk = o.findItemNamed(h.getPrimaryKey());
								OLAPRelation r = new OLAPRelation();
								r.setLeftObjectItem(cube.getFactDataObject().findItemNamed(u.getFkey()));
								r.setRightObjectItem(pk);

								if (!isInRelation(r.getRightObjectItem(), r.getLeftObjectItem())){
									relations.add(r);
								}
									
								
							}
							else if (j.getTableName().equals("") && j.getRightAlias().equals("")){
								DataObject o = j.findTable(j.getChilds().get(1).getTableName());
								DataObjectItem pk = o.findItemNamed(h.getPrimaryKey());
								OLAPRelation r = new OLAPRelation();
								r.setLeftObjectItem(cube.getFactDataObject().findItemNamed(u.getFkey()));
								r.setRightObjectItem(pk);

								if (!isInRelation(r.getRightObjectItem(), r.getLeftObjectItem())){
									relations.add(r);
								}
									
							}
						}
					}
			}
			
			}
		}
		
		for(OLAPDimension dim : cube.getDims()){
			boolean inUsage = false;
			for(DimUsage u : cube.getDimUsages()){
				inUsage |= u.getSource().equals(dim.getName());
			}
			if (!inUsage){
				for(OLAPHierarchy h : dim.getHierarchies()){
					if (!h.getTableName().equals("")){
						DataObject table = dataSource.findDataObjectNamed(h.getTableName());
						DataObjectItem pk = table.findItemNamed(h.getPrimaryKey());
						
						OLAPRelation r = new OLAPRelation();
						r.setLeftObjectItem(cube.getFactDataObject().findItemNamed(dim.getForeignKey()));
						r.setRightObjectItem(pk);

						if (!isInRelation(r.getRightObjectItem(), r.getLeftObjectItem())){
							relations.add(r);
						}
							
					}
					else{
						OLAPRelation rr = new OLAPRelation();
						rr.setLeftObjectItem(cube.getFactDataObject().findItemNamed(dim.getForeignKey()));
						rr.setRightObjectItem(h.getPrimaryKeyTableCol());

						if (!isInRelation(rr.getRightObjectItem(), rr.getLeftObjectItem())){
							relations.add(rr);
						}
							
						
						for(Joint j : h.getJoints()){
							
							if (j.getTableName().equals("") && j.getLeftAlias().equals("")){
								DataObject o = j.findTable(j.getChilds().get(0).getTableName());
								DataObjectItem pk = o.findItemNamed(h.getPrimaryKey());
								OLAPRelation r = new OLAPRelation();
								r.setLeftObjectItem(cube.getFactDataObject().findItemNamed(dim.getForeignKey()));
								r.setRightObjectItem(pk);

								if (pk != null && !isInRelation(r.getRightObjectItem(), r.getLeftObjectItem())){
									relations.add(r);
								}
									
								
							}
							else if (j.getTableName().equals("") && j.getRightAlias().equals("")){
								DataObject o = j.findTable(j.getChilds().get(1).getTableName());
								DataObjectItem pk = o.findItemNamed(h.getPrimaryKey());
								OLAPRelation r = new OLAPRelation();
								r.setLeftObjectItem(cube.getFactDataObject().findItemNamed(dim.getForeignKey()));
								r.setRightObjectItem(pk);

								if (pk != null && !isInRelation(r.getRightObjectItem(), r.getLeftObjectItem())){
									relations.add(r);
								}
									
							}
						}
					}
				}
			}
		}
		List<OLAPRelation> l = new ArrayList<OLAPRelation>();
		
		for(OLAPRelation r : relations){
			if ((r.getLeftObjectItem() == r.getRightObjectItem()) ||
					(r.getLeftObjectItem() == null) || 
					(r.getRightObjectItem() == null) ||
					(r.getRightObject() == null) ||
					(r.getLeftObject() == null) ||
					(r.getLeftObject().getDataSource() == null) ||
					(r.getRightObject().getDataSource() == null)){
				l.add(r);
			}
		}
		
		for(OLAPRelation r : l){
			relations.remove(r);
		}
	}
	
	public List<OLAPRelation> getRelations(){
		return relations;
	}
	
	private boolean isInRelation(DataObjectItem a, DataObjectItem b){
		for(OLAPRelation r : relations){
			if (r.getLeftObjectItem() == a && r.getRightObjectItem() == b)
				return true;
			if (r.getLeftObjectItem() == b && r.getRightObjectItem() == a)
				return true;
		}
		return false;
	}
}
