package bpm.united.olap.api.communication;

import bpm.united.olap.api.data.IExternalQueryIdentifier;
import bpm.united.olap.api.model.impl.Projection;
import bpm.united.olap.api.result.OlapResult;
import bpm.united.olap.api.result.impl.ValueResultCell;
import bpm.united.olap.api.runtime.IRuntimeContext;

public interface IRuntimeService {

//	void setUrl(String url);
	
	OlapResult executeQuery(String mdx, String schemaId, String cubeName, boolean computeDatas, IRuntimeContext runtimeContext) throws Exception;
	
	/**
	 * this method is done to execute an Uolap query from a FMDT
	 * it is specific to deal with XML deserialisation issues from xstream
	 * because it cannot handle big amount of datas
	 * 
	 * @param mdx
	 * @param schemaId
	 * @param cubeName
	 * @param computeDatas
	 * @param runtimeContext
	 * @return
	 * @throws Exception
	 */
	OlapResult executeQueryForFmdt(String mdx, String schemaId, String cubeName, boolean computeDatas, IRuntimeContext runtimeContext) throws Exception;
	
	OlapResult executeQuery(String mdx, String schemaId, String cubeName, Integer limit, boolean computeDatas, IRuntimeContext runtimeContext) throws Exception;
	
	OlapResult drillthrough(ValueResultCell cell, String schemaId, String cubeName, IRuntimeContext runtimeContext) throws Exception;
	
	/**
	 * Drillthrough on a cube with a projection
	 * @param cell
	 * @param schemaId
	 * @param cubeName
	 * @param runtimeContext
	 * @param projection
	 * @return
	 * @throws Exception
	 */
	OlapResult drillthrough(ValueResultCell cell, String schemaId, String cubeName, IRuntimeContext runtimeContext, Projection projection) throws Exception;
	
	void setIsInStreamMode(boolean isInStreamMode);
	
	void preload(String mdx, String schemaId, String cubeName, IRuntimeContext runtimeContext) throws Exception;
	
	/**
	 * Execute an OlapQuery for a Fmdt model based on an Uolap schema
	 * @param identifier
	 * @param schemaId
	 * @param cubeName
	 * @param runtimeContext
	 * @return
	 * @throws Exception
	 */
	OlapResult executeFMDTQuery(IExternalQueryIdentifier identifier, String schemaId, String cubeName, IRuntimeContext runtimeContext) throws Exception;
	
	/**
	 * A method to calculate and persists extrapolation values
	 * @param schemaId
	 * @param cubeName
	 * @param projection
	 * @param ctx
	 * @return
	 * @throws Exception
	 */
	String createExtrapolation(String schemaId, String cubeName, Projection projection, IRuntimeContext ctx) throws Exception;
	
	/**
	 * Execute the query for an extrapolation
	 * The data have to be created first (see createExtrapolation)
	 * 
	 * @param mdx
	 * @param schemaId
	 * @param cubeName
	 * @param limit
	 * @param computeDatas
	 * @param runtimeContext
	 * @param projection
	 * @return
	 * @throws Exception
	 */
	OlapResult executeQueryForExtrapolationProjection(String mdx, String schemaId, String cubeName, Integer limit, boolean computeDatas, IRuntimeContext runtimeContext, Projection projection) throws Exception;
}
