package bpm.united.olap.runtime.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.datatools.connectivity.oda.IQuery;
import org.eclipse.datatools.connectivity.oda.IResultSet;

import bpm.united.olap.api.model.Measure;
import bpm.united.olap.api.model.impl.ProjectionMeasure;
import bpm.united.olap.api.runtime.DataCell;
import bpm.united.olap.api.runtime.DataCellIdentifier2;
import bpm.united.olap.api.runtime.DataStorage;
import bpm.united.olap.api.runtime.IRuntimeContext;
import bpm.united.olap.api.runtime.model.ICubeInstance;
import bpm.united.olap.api.runtime.model.IDataLocator;
import bpm.united.olap.runtime.data.AdvancedDataMapper2;
import bpm.united.olap.runtime.data.IDataMapper;
import bpm.united.olap.runtime.query.improver.JDBCQueryImprover2;
import bpm.united.olap.runtime.query.improver.QueryImproverException;
import bpm.vanilla.platform.logging.IVanillaLogger;

public class JDBCQueryHelper extends QueryHelper {

	private List<DataCellIdentifier2> failurePossibleIds = new ArrayList<DataCellIdentifier2>();
	
	public JDBCQueryHelper(IVanillaLogger logger, IRuntimeContext runtimeContext) {
		super(logger, runtimeContext);
	}

	@Override
	public DataStorage executeQuery(ICubeInstance cubeInstance, DataStorage storage, int limit) throws Exception {
		
		if(hasLastMeasure(storage)) {
			return super.executeQuery(cubeInstance, storage, limit);
		}
		
		
		
//		List<DataCellIdentifier2> possibleIds = storage.getPossibleIds();
		
		//create and execute an improved query for each possible id
		for(DataCellIdentifier2 possibleId : storage.getPossibleIds().keySet()) {
			JDBCQueryImprover2 queryImprover = new JDBCQueryImprover2(logger, runtimeContext);
			createInput(cubeInstance.getFactTable().getParent());
			input.setQueryText(query);
			IQuery queryOda = getOdaQuery(input, input.getQueryText());
			
			try{
				if(possibleId.getMeasure() instanceof ProjectionMeasure) {
					throw new QueryImproverException("Impossible to improve projection calculation");
				}
				queryImprover.improveQuery(input, queryOda, storage, cubeInstance, possibleId);
				
				
				String improvedQuery = queryImprover.getImprovedQuery();
				IDataLocator improvedDataLocator = queryImprover.getImprovedDataLocator();
				
				IDataMapper mapper = new AdvancedDataMapper2(storage, cubeInstance, improvedDataLocator, runtimeContext);
				
				//execute query
				/*
				 * release the old IQuery
				 */
				
				queryOda.close();
				bpm.dataprovider.odainput.consumer.QueryHelper.closeConnectionFor(queryOda);
				bpm.dataprovider.odainput.consumer.QueryHelper.removeQuery(queryOda);
				
				input.setQueryText(improvedQuery);
				queryOda = getOdaQuery(input, improvedQuery);
				IResultSet rs = null;

				try {
					rs = OdaQueryRunner.runQuery(queryOda);
					while(rs.next()) {
						DataCell cell = mapper.mapData(rs, possibleId);
						Measure mes = possibleId.getMeasure();
						
						if(cell != null && !cell.isCalculated()) {
						
							HashMap<String, Double> values = findMeasureValues(mes, cell, rs, cubeInstance, improvedDataLocator);
							
							if(values != null) {
								for(String uname : values.keySet()) {
									cell.addValue(uname, values.get(uname));
								}
								
								cell.getCalculation().makeCalculDuringQuery(true);
							}
						}
						
						else {
//							System.out.println("didn't find cell");
						}
						
					}
				} catch (Exception e) {
					queryOda.close();
					if(rs != null) {
						rs.close();
					}
					bpm.dataprovider.odainput.consumer.QueryHelper.closeConnectionFor(queryOda);
					bpm.dataprovider.odainput.consumer.QueryHelper.removeQuery(queryOda);
					e.printStackTrace();
					throw new QueryImproverException("The execution of the improved query failed");
				}
				
				queryOda.close();
				rs.close();
				bpm.dataprovider.odainput.consumer.QueryHelper.closeConnectionFor(queryOda);
				bpm.dataprovider.odainput.consumer.QueryHelper.removeQuery(queryOda);
			}catch(QueryImproverException ex){
				logger.warn(ex.getMessage());
				failurePossibleIds.add(possibleId);
			}catch(Exception ex){
				logger.error(ex.getMessage(), ex);
				failurePossibleIds.add(possibleId);
			}
			
		}
		
		
		
		
		if (!failurePossibleIds.isEmpty()){
			logger.warn("Some query couldn't been improved, the datas will be collected without any improvements");
			
			List<DataCellIdentifier2> toRemove = new ArrayList<DataCellIdentifier2>();
			
			for(DataCellIdentifier2 id : storage.getPossibleIds().keySet()) {
				if(!failurePossibleIds.contains(id)) {
					toRemove.add(id);
				}
			}
			
			for(DataCellIdentifier2 id : toRemove) {
				storage.getPossibleIds().remove(id);
			}
			
//			List<DataCellIdentifier2> ids = storage.getPossibleIds();
//			ids.removeAll(failurePossibleIds);
//			storage.setPossibleIds(ids);
			executeNoImprovedQuery(cubeInstance, storage, limit);
		}
		cubeInstance.clearLevelDatasInMemory();
		return storage;
	}
	
	

}
