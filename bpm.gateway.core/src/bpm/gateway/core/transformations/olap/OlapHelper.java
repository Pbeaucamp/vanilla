package bpm.gateway.core.transformations.olap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.eclipse.datatools.connectivity.oda.IQuery;
import org.eclipse.datatools.connectivity.oda.IResultSet;
import org.eclipse.datatools.connectivity.oda.OdaException;
import org.fasd.datasource.DataObjectItem;
import org.fasd.datasource.DataObjectOda;
import org.fasd.datasource.DatasourceOda;
import org.fasd.inport.DigesterFasd;
import org.fasd.olap.FAModel;
import org.fasd.olap.ICube;
import org.fasd.olap.OLAPCube;
import org.fasd.olap.OLAPDimension;
import org.fasd.olap.OLAPHierarchy;
import org.fasd.olap.OLAPLevel;

import bpm.dataprovider.odainput.consumer.QueryHelper;
import bpm.gateway.core.DefaultStreamDescriptor;
import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.StreamElement;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.transformations.inputs.odaconsumer.OdaHelper;
import bpm.vanilla.platform.core.IObjectIdentifier;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.beans.data.OdaInput;
import bpm.vanilla.platform.core.impl.ObjectIdentifier;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class OlapHelper {

	private static HashMap<IObjectIdentifier, FAModel> loadedModels = new HashMap<IObjectIdentifier, FAModel>(); 
	
	
	private IRepositoryContext repContext;
	public OlapHelper(IRepositoryContext repContext){
		this.repContext = repContext;
	}
	
	
	
	
	public StreamDescriptor getDescriptor(OlapDimensionExtractor tr) throws Exception{
		FAModel model = null;
		
		model = getModel(tr);
		
		
		if (model == null){
			model = loadModel(tr);
		}
		
		
//		List<String> cubeNames = new ArrayList<String>();
		OLAPCube cube = null;
		for(ICube c : model.getCubes()){
			if ( c instanceof OLAPCube && c.getName().equals(tr.getCubeName())){
				cube = (OLAPCube)c;
				break;
			}
		}
		
		if (cube == null){
			return null;
		}
		
		DefaultStreamDescriptor desc = new DefaultStreamDescriptor();
		
		for(OLAPDimension dim : cube.getDims()){
			if (dim.getName().equals(tr.getDimensionName())){
				for(OLAPHierarchy hiera : dim.getHierarchies()){
					if (hiera.getName().equals(tr.getHierarchieName())){
						for(String s : tr.getLevelNames()){
							
							for(OLAPLevel lvl : hiera.getLevels()){
								if (lvl.getName().equals(s)){
									DataObjectItem it = lvl.getItem();
									StreamElement col = new StreamElement();
									col.name = it.getOrigin();
//									col.type = Integer.parseInt(s)it.getType();
									col.typeName = it.getSqlType();
									col.className = it.getClasse();
									col.transfoName = tr.getName();
									col.originTransfo = tr.getName();
									desc.addColumn(col);
								}
							}
						}
					}
				}
			}
		}
		return desc;
	}
	
	public StreamDescriptor getDescriptor(IOlap tr) throws Exception{
		
		FAModel model = null;
		
		model = getModel(tr);
		
		
		if (model == null){
			model = loadModel(tr);
		}
		
		
//		List<String> cubeNames = new ArrayList<String>();
		OLAPCube cube = null;
		for(ICube c : model.getCubes()){
			if ( c instanceof OLAPCube && c.getName().equals(tr.getCubeName())){
				cube = (OLAPCube)c;
				break;
			}
		}
		
		if (cube == null){
			return null;
		}
		
		DataObjectOda factTable = (DataObjectOda)cube.getFactDataObject();
		OdaInput odaInput = getOdaInput(factTable);
		
		StreamDescriptor desc = OdaHelper.createDescriptor((Transformation)tr, odaInput);
		return desc;
		

		
	}
	
	public FAModel getModel(IOlap tr)throws Exception{
		if (repContext == null){
			throw new Exception("Missing RepositoryContext - you are not connected to Vanilla");
		}
		for(IObjectIdentifier l : loadedModels.keySet()){
			if (l.getRepositoryId() == repContext.getRepository().getId() && tr.getDirectoryItemId() == (Integer)l.getDirectoryItemId()){
				return loadedModels.get(l);
			}
		}
		
		return loadModel(tr);
	}
	
	private IRepositoryApi createSock() throws Exception{
		if (repContext == null){
			throw new Exception("Cannot use Metadata without a IRepositoryContext");
		}
		
		IRepositoryApi sock = new RemoteRepositoryApi(repContext);
		
		return sock;
	}
	private FAModel loadModel(IOlap tr) throws Exception{
		IRepositoryApi sock = createSock();
		RepositoryItem item = null;
		String fasdXml = null;
		try {
			item = sock.getRepositoryService().getDirectoryItem(tr.getDirectoryItemId());
			fasdXml = sock.getRepositoryService().loadModel(item);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Error when loading FASD model from Repository:" + e.getMessage(), e);
		}
		
				
		DigesterFasd dig = null;
		try {
			dig = new DigesterFasd(IOUtils.toInputStream(fasdXml, "UTF-8"));
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Error when parsing FASD xml :" + e.getMessage(), e);
		}
		FAModel model = dig.getFAModel();
		loadedModels.put(new ObjectIdentifier(repContext.getRepository().getId(), tr.getDirectoryItemId()), model);
		
		return model;
	}
	
	public List<String> getCubeNames(boolean useCache, IOlap tr) throws Exception{
		FAModel model = null;
		
		if (useCache){
			model = getModel(tr);
		}
		
		if (model == null){
			model = loadModel(tr);
		}
		
		
		List<String> cubeNames = new ArrayList<String>();
		for(ICube c : model.getCubes()){
			if ( c instanceof OLAPCube){
				cubeNames.add(c.getName());
			}
		}
		
		
		return cubeNames;
	}
	
	
	public List<String> getDimensionNames(boolean useCache, IOlap tr) throws Exception{
		
		FAModel model = null;
		
		if (useCache){
			model = getModel(tr);
		}
		
		if (model == null){
			model = loadModel(tr);
		}
		
		
		
		OLAPCube cube = null;
		
		for(OLAPCube c : model.getOLAPSchema().getCubes()){
			if (c.getName().equals(tr.getCubeName())){
				cube = c;
				break;
			}
		}
		
		
		if (cube == null){
			throw new Exception("Unable to find cube " + tr.getCubeName() + " in OLAP Schema");
		}
		
		
		List<String> dimNames = new ArrayList<String>();
		for (OLAPDimension dim : cube.getDims()){
			dimNames.add(dim.getName());
		}
		
		return dimNames;
	}
	
	
	
	public List<String> getLevelNames(boolean useCache, IOlapDimensionable tr) throws Exception{
		
		FAModel model = null;
		
		if (useCache){
			model = getModel(tr);
		}
		
		if (model == null){
			model = loadModel(tr);
		}
		
		
		//look for the cube
		OLAPCube cube = null;
		for(OLAPCube c : model.getOLAPSchema().getCubes()){
			if (c.getName().equals(tr.getCubeName())){
				cube = c;
				break;
			}
		}
		
		if (cube == null){
			throw new Exception("Unable to find cube " + tr.getCubeName() + " in OLAP Schema");
		}
		
		//look for the dimension
		OLAPDimension dimension = null;
		for (OLAPDimension dim : cube.getDims()){
			if (dim.getName().equals(tr.getDimensionName())){
				dimension = dim;
				break;
			}
		}
		if (dimension == null){
			throw new Exception("Unable to find dimension " + tr.getDimensionName()+ " in OLAP cube " + cube.getName());
		}
		
		
		
		
		OLAPHierarchy hierarchy = null;
		for(OLAPHierarchy h : dimension.getHierarchies()){
			if (h.getName().equals(tr.getHierarchieName())){
				hierarchy = h;
				break;
			}
		}
		
		if (hierarchy == null){
			throw new Exception("Unable to find hierarchy" + tr.getHierarchieName() + " in Dimension" + dimension.getName());
		}
		
		List<String> lvlNames = new ArrayList<String>();
		for(OLAPLevel l : hierarchy.getLevels()){
			lvlNames.add(l.getName());
			
		}
		
		return lvlNames;
	}
	
	
	public List<String> getLevelValues(boolean useCache, OlapFactExtractorTranformation tr, DimensionFilter filter, String levelName) throws Exception{
		FAModel model = null;
		
		if (useCache){
			model = getModel(tr);
		}
		
		if (model == null){
			model = loadModel(tr);
		}
		
		
		//look for the cube
		OLAPCube cube = null;
		for(OLAPCube c : model.getOLAPSchema().getCubes()){
			if (c.getName().equals(tr.getCubeName())){
				cube = c;
				break;
			}
		}
		
		if (cube == null){
			throw new Exception("Unable to find cube " + tr.getCubeName() + " in OLAP Schema");
		}
		
		//look for the dimension
		OLAPDimension dimension = null;
		for (OLAPDimension dim : cube.getDims()){
			if (dim.getName().equals(tr.getDimensionName())){
				dimension = dim;
				break;
			}
		}
		if (dimension == null){
			throw new Exception("Unable to find dimension " + tr.getDimensionName()+ " in OLAP cube " + cube.getName());
		}
		
		OLAPHierarchy hierarchy = null;
		for(OLAPHierarchy h : dimension.getHierarchies()){
			if (h.getName().equals(tr.getHierarchieName())){
				hierarchy = h;
				break;
			}
		}
		
		if (hierarchy == null){
			throw new Exception("Unable to find hierarchy" + tr.getHierarchieName() + " in Dimension" + dimension.getName());
		}
		
		
		
		
		StringBuffer whereClause = new StringBuffer();
		
		DataObjectItem levelColumn = null;
		boolean first = true;

		for(OLAPLevel l : hierarchy.getLevels()){
			if (l.getName().equals(levelName)){
				levelColumn = l.getItem();
			}
			else{
				int pos =0;
				FilterClause actualClause = filter.getFilter(levelName);
				
				for(int i = 0; i < filter.getFilters().indexOf(actualClause); i++){
					
					if (filter.getFilters().get(i).getLevelName().equals(l.getName())){
						
						if (first){
							first = false;
						}
						else{
							whereClause.append(" and ");
						}
						whereClause.append(l.getItem().getOrigin() + "='" + filter.getFilters().get(i).getValue() + "'");
					}
					pos++;
				}
			}
			
		}
		DataObjectOda odaTable = (DataObjectOda) levelColumn.getParent();
		int levelIndex = odaTable.getColumns().indexOf(levelColumn);
				
		IQuery query = QueryHelper.buildquery(getOdaInput(odaTable));
		
		query.setMaxRows(0);
		
		IResultSet rs = query.executeQuery();

		List<String> values = new ArrayList<String>();
		while(rs.next()){
			values.add(rs.getString(levelIndex + 1));
		}
		
		rs.close();
		query.close();
		QueryHelper.closeConnectionFor(query);
		QueryHelper.removeQuery(query);
		
		return values;
		
	}
	
	
	
	
//	public String getExtractQuery(boolean useCache, OlapFactExtractorTranformation tr, DimensionFilter filter) throws Exception{
//		FAModel model = null;
//		
//		if (useCache){
//			model = getModelFromCache(tr);
//		}
//		
//		if (model == null){
//			model = loadModel(tr);
//		}
//		
//		
//		//look for the cube
//		OLAPCube cube = null;
//		for(OLAPCube c : model.getOLAPSchema().getCubes()){
//			if (c.getName().equals(tr.getCubeName())){
//				cube = c;
//				break;
//			}
//		}
//		
//		if (cube == null){
//			throw new Exception("Unable to find cube " + tr.getCubeName() + " in OLAP Schema");
//		}
//		
//		//look for the dimension
//		OLAPDimension dimension = null;
//		for (OLAPDimension dim : cube.getDims()){
//			if (dim.getName().equals(tr.getDimensionName())){
//				dimension = dim;
//				break;
//			}
//		}
//		if (dimension == null){
//			throw new Exception("Unable to find dimension " + tr.getDimensionName()+ " in OLAP cube " + cube.getName());
//		}
//		OLAPHierarchy hierarchy = null;
//		for(OLAPHierarchy h : dimension.getHierarchies()){
//			if (h.getName().equals(tr.getHierarchieName())){
//				hierarchy = h;
//				break;
//			}
//		}
//		
//		if (hierarchy == null){
//			throw new Exception("Unable to find hierarchy" + tr.getHierarchieName() + " in Dimension" + dimension.getName());
//		}
//		
//		StringBuffer whereClause = new StringBuffer();
//		
//		
//		boolean first = true;
//
//		for(OLAPLevel l : hierarchy.getLevels()){
//			
//			FilterClause actualClause = filter.getFilter(l.getName());
//			
//			for(int i = 0; i <= filter.getFilters().indexOf(actualClause); i++){
//				
//				if (filter.getFilters().get(i).getLevelName().equals(l.getName())){
//					if (filter.getFilters().get(i).getValue() !=  null){
//						if (first){
//							first = false;
//						}
//						else {
//							whereClause.append(" and ");
//						}
//						whereClause.append(l.getItem().getOrigin() + "='" + filter.getFilters().get(i).getValue() + "'");
//					}
//					
//				}
//				
//			}
//			
//		}
//		
//	
//		OLAPRelation link = null;
//		
//		for(OLAPRelation r : model.getRelations()){
//			if (r.getLeftObject() == cube.getFactDataObject() && 
//					hierarchy.getLevels().get(0).getItem().getParent() == r.getRightObject() ||
//				r.getRightObject() == cube.getFactDataObject() && 
//				hierarchy.getLevels().get(0).getItem().getParent() == r.getLeftObject()){
//				link = r;
//				break;
//			}
//		}
//		
//		
//		StringBuffer mainQuery = new StringBuffer();
//		mainQuery.append(cube.getFactDataObject().getSelectStatement());
////		mainQuery.append(" where ");
//		
//		//no link => we assume that this dimension is a degenerated one
//		//cube be a bad cube definition
//		if (link == null){
//			mainQuery.append(" where " + whereClause);
//			
//		}
//		else if (link.getRightObject() == cube.getFactDataObject()){
//			mainQuery.append(" where " + link.getRightObject().getPhysicalName()+ "." + link.getRightObjectItem().getOrigin() + " in ");
//			mainQuery.append(" (select distinct " + link.getLeftObject().getPhysicalName()+ "." + link.getLeftObjectItem().getOrigin() + " from " + link.getLeftObject().getPhysicalName());
//			if (whereClause.length() > 0){
//				mainQuery.append(" where " + whereClause);
//			}
//			mainQuery.append( ")");
//		}
//		else{
//			mainQuery.append(" where " + link.getLeftObject().getPhysicalName()+ "." + link.getLeftObjectItem().getOrigin() + " in ");
//			mainQuery.append(" (select distinct " + link.getRightObject().getPhysicalName()+ "." + link.getRightObjectItem().getOrigin() + " from " + link.getRightObject().getPhysicalName());
//			if (whereClause.length() > 0){
//				mainQuery.append(" where " + whereClause);
//			}
//			mainQuery.append( ")");
//		}
//		
//		
//		
//		return mainQuery.toString();
//	}

	public List<String> getHierarchyNames(boolean useCache, IOlapDimensionable tr) throws Exception{
		FAModel model = null;
		
		if (useCache){
			model = getModel(tr);
		}
		
		if (model == null){
			model = loadModel(tr);
		}
		
		
		//look for the cube
		OLAPCube cube = null;
		for(OLAPCube c : model.getOLAPSchema().getCubes()){
			if (c.getName().equals(tr.getCubeName())){
				cube = c;
				break;
			}
		}
		
		if (cube == null){
			throw new Exception("Unable to find cube " + tr.getCubeName() + " in OLAP Schema");
		}
		
		//look for the dimension
		OLAPDimension dimension = null;
		for (OLAPDimension dim : cube.getDims()){
			if (dim.getName().equals(tr.getDimensionName())){
				dimension = dim;
				break;
			}
		}
		if (dimension == null){
			throw new Exception("Unable to find dimension " + tr.getDimensionName()+ " in OLAP cube " + cube.getName());
		}
		
		List<String> hNames = new ArrayList<String>();
		for(OLAPHierarchy h : dimension.getHierarchies()){
			hNames.add(h.getName());
		}
		
		
		return hNames;
	}
	
	
	/**
	 * this class load the FASD model and build an OdaInput on the
	 * Table used for the Hierarchy.
	 * If the Hierarchy is a snowflakes, an exeception is thrown
	 * @param stream
	 * @return
	 * @throws Exception
	 */
	public OdaInput getOdaInput(OlapDimensionExtractor stream) throws Exception{
		FAModel model = getModel(stream);
		
		OLAPCube cube = null;
		for(ICube c : model.getCubes()){
			if ( c instanceof OLAPCube && c.getName().equals(stream.getCubeName())){
				cube = (OLAPCube)c;
				break;
			}
		}
		

		
				
		if (cube == null){
			throw new Exception ("Cube not found in model");
		}
		
		
		DataObjectOda table = null;
		
		for(OLAPDimension dim : cube.getDims()){
			if (dim.getName().equals(stream.getDimensionName())){
				for(OLAPHierarchy hiera : dim.getHierarchies()){
					if (hiera.isSnowFlakes()){
						throw new Exception("Snowflakes Hierarchy are not supported");
					}
					if (hiera.getName().equals(stream.getHierarchieName())){
						for(String s : stream.getLevelNames()){
							
							for(OLAPLevel lvl : hiera.getLevels()){
								if (lvl.getName().equals(s)){
									table = (DataObjectOda)lvl.getItem().getParent();
									break;
								}
							}
							if (table != null){
								break;
							}
						}
						break;
					}
				}
				break;
			}
		}
		
		return getOdaInput(table);
	}
	
	public List<List<Object>> getValues(OlapDimensionExtractor stream) throws Exception{
		List<List<Object>> values = new ArrayList<List<Object>>();
//		Connection con = null;
		/*
		 * init JDBC connnection
		 */
		
		FAModel model = getModel(stream);
		
		OLAPCube cube = null;
		for(ICube c : model.getCubes()){
			if ( c instanceof OLAPCube && c.getName().equals(stream.getCubeName())){
				cube = (OLAPCube)c;
				break;
			}
		}
		

		
				
		if (cube == null){
			throw new Exception ("Cube not found in model");
		}
		
		
		List<OLAPLevel> olaplevels = new ArrayList<OLAPLevel>();
		
		for(OLAPDimension dim : cube.getDims()){
			if (dim.getName().equals(stream.getDimensionName())){
				for(OLAPHierarchy hiera : dim.getHierarchies()){
					if (hiera.isSnowFlakes()){
						throw new Exception("Snowflakes Hierarchy are not supported");
					}
					if (hiera.getName().equals(stream.getHierarchieName())){
						for(String s : stream.getLevelNames()){
							
							for(OLAPLevel lvl : hiera.getLevels()){
								if (lvl.getName().equals(s)){
									olaplevels.add(lvl);
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
		
		
		DataObjectOda table = (DataObjectOda)olaplevels.get(0).getItem().getParent();
		
		bpm.vanilla.platform.core.beans.data.OdaInput odaInput = getOdaInput(table);
		
		IQuery query = null;
		try{
			query = QueryHelper.buildquery(odaInput);
		}catch(Exception ex){
			throw new Exception("Unable to build Oda IQuery - " + ex.getMessage(), ex);
		}
		
		
		IResultSet rs = null;
		try{
			rs = query.executeQuery();
			while(rs.next()){
				
				List<Object> row = new ArrayList<Object>();
				
				for(OLAPLevel l : olaplevels){
					
					int levelIndex = table.getColumns().indexOf(l.getItem());
					
					try{
						row.add(rs.getObject(levelIndex + 1));
					}catch(UnsupportedOperationException ex){
						row.add(rs.getString(levelIndex + 1));
					}
					
				}
				values.add(row);
				
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			if (rs != null){
				try{
					rs.close();
				}catch(Exception ex){}
			}
			try{
				query.close();
				QueryHelper.removeQuery(query);
				QueryHelper.closeConnectionFor(query);
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
		
		
		
		

		
		
		return values;
	}

	public List<List<Object>> getCountDistinctFieldValues(StreamElement field, OlapDimensionExtractor stream) throws Exception {
		List<List<Object>> valuesFinal = new ArrayList<List<Object>>();
		HashMap<String, Integer> distinctCount = new HashMap<String, Integer>();
		try {
			FAModel model =loadModel(stream);
			
			OLAPCube cube = null;
			for(ICube c : model.getCubes()){
				if ( c instanceof OLAPCube && c.getName().equals(stream.getCubeName())){
					cube = (OLAPCube)c;
					break;
				}
			}
			
			if (cube == null){
				throw new Exception ("Cube not found in model");
			}
	
			List<String> values = new ArrayList<String>();
	
			DataObjectItem item = null;
			for(OLAPDimension dim : cube.getDims()){
				for(OLAPHierarchy hier : dim.getHierarchies()){
					for(OLAPLevel level : hier.getLevels()){
						if(level.getName().equalsIgnoreCase(field.name)){
							item = (DataObjectItem)level.getItem();
						}
					}
				}
			}

			DataObjectOda odaTable = (DataObjectOda) item.getParent();
			
			OdaInput input = new OdaInput();
			input.setDatasetPublicProperties(odaTable.getPublicProperties());
			input.setDatasetPrivateProperties(odaTable.getPrivateProperties());
			input.setDatasourcePublicProperties(((DatasourceOda)odaTable.getDataSource()).getPublicProperties());
			input.setDatasourcePrivateProperties(((DatasourceOda)odaTable.getDataSource()).getPrivateProperties());
			input.setOdaExtensionDataSourceId(((DatasourceOda)odaTable.getDataSource()).getOdaDatasourceExtensionId());
			input.setOdaExtensionId(((DatasourceOda)odaTable.getDataSource()).getOdaExtensionId());
			input.setQueryText(odaTable.getQueryText());
			input.setName(((DatasourceOda)odaTable.getDataSource()).getOdaDatasourceExtensionId());
			
			IQuery query = QueryHelper.buildquery(input);
			
			IResultSet rs = query.executeQuery();
			
			while(rs.next()) {
				try {
					String val = rs.getString(field.name);
					values.add(val);
					if(!distinctCount.keySet().contains(val)) {
						distinctCount.put(val,1);
					}
					else {
						distinctCount.put(val, distinctCount.get(val) + 1);
					}
				} catch (Exception e) {
					e.printStackTrace();
					rs.close();
					query.close();
					QueryHelper.closeConnectionFor(query);
					QueryHelper.removeQuery(query);
					throw new Exception(e.getMessage());
				}
			}
			rs.close();
			query.close();
			QueryHelper.closeConnectionFor(query);
			QueryHelper.removeQuery(query);
		} catch (OdaException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		for(String key : distinctCount.keySet()) {
			List<Object> vals = new ArrayList<Object>();
			vals.add(key);
			vals.add(distinctCount.get(key));
			valuesFinal.add(vals);
		}
		
		return valuesFinal;
	}


	public bpm.vanilla.platform.core.beans.data.OdaInput getOdaInput(DataObjectOda odaTable){
		bpm.vanilla.platform.core.beans.data.OdaInput input = new bpm.vanilla.platform.core.beans.data.OdaInput();
		input.setDatasetPublicProperties(odaTable.getPublicProperties());
		input.setDatasetPrivateProperties(odaTable.getPrivateProperties());
		input.setDatasourcePublicProperties(((DatasourceOda)odaTable.getDataSource()).getPublicProperties());
		input.setDatasourcePrivateProperties(((DatasourceOda)odaTable.getDataSource()).getPrivateProperties());
		
		input.setOdaExtensionDataSourceId(((DatasourceOda)odaTable.getDataSource()).getOdaDatasourceExtensionId());
		input.setOdaExtensionId(((DatasourceOda)odaTable.getDataSource()).getOdaExtensionId());
		
		input.setQueryText(odaTable.getQueryText());
		
		input.setName(((DatasourceOda)odaTable.getDataSource()).getOdaDatasourceExtensionId());

		return input;
	}
}
