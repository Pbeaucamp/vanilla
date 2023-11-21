package bpm.gateway.runtime2.transformations.inputs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.datatools.connectivity.oda.IQuery;
import org.eclipse.datatools.connectivity.oda.IResultSet;
import org.fasd.datasource.DataObject;
import org.fasd.datasource.DataObjectOda;
import org.fasd.olap.FAModel;
import org.fasd.olap.OLAPCube;
import org.fasd.olap.OLAPDimension;
import org.fasd.olap.OLAPHierarchy;
import org.fasd.olap.OLAPLevel;
import org.fasd.olap.OLAPRelation;

import bpm.dataprovider.odainput.consumer.QueryHelper;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.transformations.olap.DimensionFilter;
import bpm.gateway.core.transformations.olap.FilterClause;
import bpm.gateway.core.transformations.olap.OlapFactExtractorTranformation;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.internal.Row;
import bpm.gateway.runtime2.internal.RowFactory;

public class RunOlapFactExtractor extends RuntimeStep{
	
	/*
	 * foreignKeys key all foreignKeys from each filter
	 */
	private HashMap<RuntimeStep, List<Object>> foreignKeys = new HashMap<RuntimeStep, List<Object>>();
	private Integer factForeignKeyIndex = null;
	private RuntimeStep trashOutput;
	
	
	private IQuery factQuery;
	private IResultSet factResultSet;
	
	public RunOlapFactExtractor(Transformation transformation, int bufferSize) {
		super(null, transformation, bufferSize);
		
	}

	@Override
	public void init(Object adapter) throws Exception {
		OlapFactExtractorTranformation tr = (OlapFactExtractorTranformation)getTransformation();
		FAModel model = tr.getDocument().getOlapHelper().getModel(tr);
		
		OLAPCube olapCube = null;
		OLAPHierarchy olapHierarchy = null;
		DataObject hierarchyTable = null;
		for(OLAPCube c : model.getOLAPSchema().getCubes()){
			if (c.getName().equals(tr.getCubeName())){
				olapCube = c;
				info("Found Cube named " + c.getName() + " within FASD schema");
				
				
				for(OLAPDimension d : c.getDims()){
					if (d.getName().equals(tr.getDimensionName())){
						
						for(OLAPHierarchy h : d.getHierarchies()){
							if (h.getName().equals(tr.getHierarchieName())){
								olapHierarchy = h;
								if (h.isSnowFlakes()){
									throw new Exception("Snowflakes Hierarchies are not supported");
								}
								hierarchyTable = h.getLevels().get(0).getItem().getParent();
								break;
							}
						}
						
					}
				}
				
				
				break;
			}
		}
		if (olapCube == null){
			throw new Exception("Unable to find Cube named " + tr.getCubeName() + " within the FASD model");
		}
		
		/*
		 * factQuery
		 */
		factQuery = QueryHelper.buildquery(tr.getDocument().getOlapHelper().getOdaInput((DataObjectOda)olapCube.getFactDataObject()));
		info("Created FactTable Oda IQuery : " + factQuery.getEffectiveQueryText());
		
		/*
		 * building Map
		 */
		
		HashMap<RuntimeStep, HashMap<Integer, String>> outputIndexMap = new HashMap<RuntimeStep, HashMap<Integer, String>>();
		for(RuntimeStep step : getOutputs()){
			outputIndexMap.put(step, new HashMap<Integer, String>());
			for(DimensionFilter f : tr.getDimensionFilters()){
				if (f.getOutputName().equals(step.getTransformation().getName())){
					for(FilterClause fc : f.getFilters()){
						if (fc.getValue() == null){
							continue;
						}
						for(OLAPLevel l : olapHierarchy.getLevels()){
							if (l.getName().equals(fc.getLevelName())){
								int index = hierarchyTable.getColumns().indexOf(l.getItem());
								if (index < 0){
									throw new Exception("Unable to find the column matching for filter " + fc.getLevelName() + "=" + fc.getValue());
								}
								
								outputIndexMap.get(step).put(index, fc.getValue());
								break;
							}
						}
						
					}
					
				}
			}
		}
		
		/*
		 * foreigKey
		 */
		Integer hieraForeignKeyIndex = null;
		for(OLAPRelation r : model.getRelations()){
			if (r.isUsingTable(olapCube.getFactDataObject()) && r.isUsingTable(hierarchyTable)){
				if (r.getLeftObject() == olapCube.getFactDataObject()){
					factForeignKeyIndex = olapCube.getFactDataObject().getColumns().indexOf(r.getLeftObjectItem());
					hieraForeignKeyIndex = hierarchyTable.getColumns().indexOf(r.getRightObjectItem());
				}
				else{
					factForeignKeyIndex = olapCube.getFactDataObject().getColumns().indexOf(r.getRightObjectItem());
					hieraForeignKeyIndex = hierarchyTable.getColumns().indexOf(r.getLeftObjectItem());
				}
				break;
			}
		}
		if (hieraForeignKeyIndex == null){
			throw new Exception("The Hierarchy is degenerated, this is not supported");

		}
	
		info("Hierarchy ForeignKey index=" + hieraForeignKeyIndex);
		info("FactTable ForeignKey index=" + factForeignKeyIndex);
		
		
		
		info("Gathering Hierarchy foreignKeys...");
		
		IQuery hieraQuery = QueryHelper.buildquery(tr.getDocument().getOlapHelper().getOdaInput((DataObjectOda)hierarchyTable));
		info("Oda query to extract Hierarchy datas : " + hieraQuery.getEffectiveQueryText());
		info("Executing Hierarchy Query....");
		IResultSet rs = null; 
		
		
		try{
			rs = hieraQuery.executeQuery();
			info("Executed hierarchy Query");
			while(rs.next()){
				for(RuntimeStep step : outputIndexMap.keySet()){
					boolean rowMatch = true;
					if (outputIndexMap.get(step) == null || outputIndexMap.get(step).isEmpty()){
						rowMatch = false;
						continue;
					}
					for(Integer i : outputIndexMap.get(step).keySet()){
						
						String value = rs.getString(i + 1);
						if (!value.equals(outputIndexMap.get(step).get(i))){
							rowMatch = false;
							break;
						}
						
					}
					if (rowMatch){
						if (foreignKeys.get(step) == null){
							foreignKeys.put(step, new ArrayList<Object>());
						}
						foreignKeys.get(step).add(rs.getString(hieraForeignKeyIndex + 1));
					}
					
					
				}
			}
			info("Hierarchy foreignKeys gathered");
		}catch(Exception ex){
			error("Failed to Gather Hierarchy ForeignKeys - " + ex.getMessage(), ex);
			throw ex;
		}finally{
			if (rs != null){
				rs.close();
			}
			if (hieraQuery != null){
				hieraQuery.close();
				QueryHelper.removeQuery(hieraQuery);
			}
			
			
		}
		isInited = true;
		info(" inited");
	}

	@Override
	public void performRow() throws Exception {

		if (factResultSet == null){
			info("Executing Oda Query on Cube Fact's table....");
			try{
				factResultSet = factQuery.executeQuery();
				info("Fact Table Oda Query's successfully executed");
			}catch(Exception ex){
				error("Failed to execute Oda IQuery - " + ex.getMessage(), ex);
				throw ex;
			}
		}
		
		if (factResultSet.next()){
			Row row = RowFactory.createRow(this);
			
			for(int i = 0; i < row.getMeta().getSize(); i++){
				try{
					row.set(i, factResultSet.getObject(i + 1));
				}catch(Exception ex){
					row.set(i, factResultSet.getString(i + 1));
				}
			}
			readedRows++;
			writeRow(row);
		}
		else{
			if (!areInputsAlive()){
				if (areInputStepAllProcessed()){
					if (inputEmpty()){
						setEnd();
					}
				}
			}

		}
	}

	@Override
	public void releaseResources() {
		
		
	}
	
	private void disptachRow(Row row) throws Exception{
		boolean writed = false;
		for(RuntimeStep output : foreignKeys.keySet()){
			List<Object> values = foreignKeys.get(output);
			if (values.contains(row.get(factForeignKeyIndex))){
				output.insertRow(row, this);
				writed = true;
				//
			}
		}
		
		if (!writed && trashOutput != null){
			trashOutput.insertRow(row, this);
		}
		
	}
}
