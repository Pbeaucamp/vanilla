package bpm.gateway.runtime2.transformations.inputs;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.datatools.connectivity.oda.IQuery;
import org.eclipse.datatools.connectivity.oda.IResultSet;
import org.fasd.olap.FAModel;
import org.fasd.olap.ICube;
import org.fasd.olap.OLAPCube;
import org.fasd.olap.OLAPDimension;
import org.fasd.olap.OLAPHierarchy;
import org.fasd.olap.OLAPLevel;

import bpm.dataprovider.odainput.consumer.QueryHelper;
import bpm.gateway.core.transformations.olap.OlapDimensionExtractor;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.internal.Row;
import bpm.gateway.runtime2.internal.RowFactory;
import bpm.vanilla.platform.core.beans.data.OdaInput;

/**
 * extract a Cube Dimension as a stream
 * @author ludo
 *
 */
public class RunOlapDimensionExtractor2 extends RuntimeStep{

	private IQuery odaQuery;
	private IResultSet odaResultSet;
	private List<Integer> resultSetIndex = new ArrayList<Integer>();
	public RunOlapDimensionExtractor2(OlapDimensionExtractor transformation, int bufferSize) {
		super(null, transformation, bufferSize);
	}

	@Override
	public void init(Object adapter) throws Exception {
		OlapDimensionExtractor tr = (OlapDimensionExtractor)getTransformation();
		
		
		info("Looking for Level within the FASD model...");
		FAModel model = tr.getDocument().getOlapHelper().getModel(tr);
		OLAPCube cube = null;
		for(ICube c : model.getCubes()){
			if ( c instanceof OLAPCube && c.getName().equals(tr.getCubeName())){
				cube = (OLAPCube)c;
				break;
			}
		}
		

		
				
		if (cube == null){
			throw new Exception ("Cube not found in model");
		}
		
		
		List<OLAPLevel> olaplevels = new ArrayList<OLAPLevel>();
		
		for(OLAPDimension dim : cube.getDims()){
			if (dim.getName().equals(tr.getDimensionName())){
				info("Found Dimension " + dim.getName());
				for(OLAPHierarchy hiera : dim.getHierarchies()){
					if (hiera.isSnowFlakes()){
						throw new Exception("Snowflakes Hierarchy are not supported");
					}
					if (hiera.getName().equals(tr.getHierarchieName())){
						info("Found Hierarchy " + hiera.getName());
						for(String s : tr.getLevelNames()){
							
							for(OLAPLevel lvl : hiera.getLevels()){
								if (lvl.getName().equals(s)){
									olaplevels.add(lvl);
									info("Found Level " + lvl.getName());
									break;
								}
							}
						}
						break;
					}
				}
			}
		}
		
		/*
		 * reorder the levels
		 */
		
		Collections.sort(olaplevels, new Comparator<OLAPLevel>() {
			public int compare(OLAPLevel arg0, OLAPLevel arg1) {
				return new Integer(arg0.getNb()).compareTo(arg1.getNb());
			}
		});
		
		/*
		 * defining the level indicex
		 */
		for(OLAPLevel l : olaplevels){
			int i = l.getItem().getParent().getColumns().indexOf(l.getItem());
			
			if (i == -1){
				throw new Exception("Unable to find index for level " + l.getName());
			}
			resultSetIndex.add(i + 1);
			info("Level " + l.getName() + " located at index " + (i+1) + " within the odaQuery");
		}
		
		
		/*
		 * creating oda resources
		 */
		OdaInput odaInput = null;
		try{
			odaInput = tr.getDocument().getOlapHelper().getOdaInput(tr);
			if (odaInput == null){
				throw new Exception("Unexpected problem - no OdaInput have been created");
			}
		}catch(Exception ex){
			error(ex.getMessage(), ex);
			throw ex;
		}
		
		info("OdaInput created from the Hierarchy OdaTable");
		
		try{
			odaQuery = QueryHelper.buildquery(odaInput);
		}catch(Exception ex){
			error(ex.getMessage(), ex);
			throw ex;
		}
		info("IQuery created from the OdaInput : " + odaQuery.getEffectiveQueryText());
		
		
		
			
		isInited = true;
		info(" inited");
	}

	

	@Override
	public void performRow() throws Exception {
		if (odaResultSet == null){
			try{
				info("Executing Oda Query...");
				odaResultSet = odaQuery.executeQuery();
			}catch(Exception ex){
				error("Failed to execute oda Query - " + ex.getMessage(), ex);
				throw ex;
			}
		}
		
		if (odaResultSet.next()){
			Row row = RowFactory.createRow(this);
			int j = 0;
			for(Integer i : resultSetIndex){
				Object o = null;
				
				try{
					o = odaResultSet.getObject(i);
				}catch(UnsupportedOperationException ex){
					o = odaResultSet.getString(i);
				}
				row.set(j++, o);
			}
			
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
		
		if (odaResultSet != null){
			try {
				odaResultSet.close();
				info("Closed IResultSet");
			} catch (Exception e) {
				error("Failed to close IResultSet - " + e.getMessage(), e);
			}
			odaResultSet = null;
		}
		
		if (odaQuery != null){
			try {
				odaQuery.close();
				info("Closed IQuery");
			} catch (Exception e) {
				error("Failed to close IQuery - " + e.getMessage(), e);
			}finally{
				try {
					QueryHelper.removeQuery(odaQuery);
					QueryHelper.closeConnectionFor(odaQuery);
				}
				catch (Exception e) {
					error("Failed to release IQuery from QueryHelper - " + e.getMessage(), e);
				}
				odaQuery = null;
				
			}
		}
		info(" resources released");
		
	}
	
}
