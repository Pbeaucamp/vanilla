package bpm.united.olap.runtime.engine;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.apache.log4j.Logger;

import bpm.dataprovider.odainput.consumer.QueryHelper;
import bpm.mdx.parser.ANTLRStringStreamCaseInsensitive;
import bpm.mdx.parser.MdxLexer;
import bpm.mdx.parser.MdxParser;
import bpm.mdx.parser.MdxTree;
import bpm.mdx.parser.MdxParser.mdx_statement_return;
import bpm.mdx.parser.result.RootItem;
import bpm.united.olap.api.cache.CacheKey;
import bpm.united.olap.api.model.impl.Projection;
import bpm.united.olap.api.result.OlapResult;
import bpm.united.olap.api.runtime.DataCell;
import bpm.united.olap.api.runtime.DataStorage;
import bpm.united.olap.api.runtime.IRuntimeContext;
import bpm.united.olap.api.runtime.RunResult;
import bpm.united.olap.api.runtime.calculation.ICalculation;
import bpm.united.olap.api.runtime.model.ICubeInstance;
import bpm.united.olap.runtime.cache.ICacheServer;
import bpm.united.olap.runtime.data.DataStorageGenerator;
import bpm.united.olap.runtime.data.IDataStorageGenerator;
import bpm.united.olap.runtime.parser.FactoryMdxEvaluator;
import bpm.united.olap.runtime.parser.IMdxEvaluator;
import bpm.united.olap.runtime.parser.MdxEvaluator;
import bpm.united.olap.runtime.parser.calculation.TopCountCalculation;
import bpm.united.olap.runtime.query.FactoryQueryHelper;
import bpm.united.olap.runtime.query.IQueryHelper;
import bpm.united.olap.runtime.query.ProjectionQueryHelper;
import bpm.united.olap.runtime.result.HierarchizedOlapResultGenerator;
import bpm.united.olap.runtime.result.IOlapResultGenerator;
import bpm.united.olap.runtime.tools.CachedCellsUtil;
import bpm.vanilla.platform.core.IObjectIdentifier;
import bpm.vanilla.platform.logging.IVanillaLogger;

public class OlapRunner implements IOlapRunner {

	private ICubeInstance cubeInstance;
	
	private IVanillaLogger logger;
	private ICacheServer server;
	private IQueryHelper helper;
	private int limit;
	private IObjectIdentifier objectIdentifier;
	
	private IQueryHelper projectionHelper;
	
	private Projection projection;
	
	private boolean isInStreamMode = false;
	private IRuntimeContext runtimeContext;
	public OlapRunner(IObjectIdentifier objectIdentifier, ICubeInstance cubeInstance, IVanillaLogger logger, ICacheServer cacheServer, IRuntimeContext runtimeContext) {
		this.cubeInstance = cubeInstance;
		this.objectIdentifier = objectIdentifier;
		this.logger = logger;
//		helper = new QueryHelper(logger, runtimeContext);
		helper = FactoryQueryHelper.getQueryHelper(logger, runtimeContext, cubeInstance.getFactTable().getParent().getDatasourceExtensionId());
		server = cacheServer;
		helper.setCacheServer(server);
		this.runtimeContext = runtimeContext;
	}
	
	public OlapRunner(IObjectIdentifier objectIdentifier, ICubeInstance cubeInstance, IVanillaLogger logger, ICacheServer cacheServer, int limit, IRuntimeContext runtimeContext) {
		this.cubeInstance = cubeInstance;
		this.objectIdentifier = objectIdentifier;
		this.logger = logger;
		helper = FactoryQueryHelper.getQueryHelper(logger, runtimeContext, cubeInstance.getFactTable().getParent().getDatasourceExtensionId());
		server = cacheServer;
		helper.setCacheServer(server);
		this.limit = limit;
		this.runtimeContext = runtimeContext;
	}
	
	public OlapRunner(IObjectIdentifier objectIdentifier, ICubeInstance cubeInstance, IVanillaLogger logger, ICacheServer cacheServer, int limit, IRuntimeContext runtimeContext, Projection projection) {
		this(objectIdentifier, cubeInstance, logger, cacheServer, limit, runtimeContext);
		this.projection = projection;
		
		projectionHelper = helper;
		
		//FIXME : Create a custom helper for the projection query
		helper = new ProjectionQueryHelper(logger, runtimeContext, projection, projectionHelper);
		helper.setCacheServer(server);
	}
	
	@Override
	public OlapResult executeQuery(String mdx, boolean computeDatas) throws Exception {
		/*
		 * use cacheDisk 
		 */
		if (server.getCacheDisk() != null && computeDatas && projection == null){
			String repId = CacheKey.Unkown;
			String dirItId = CacheKey.Unkown;
			if (objectIdentifier != null){
				repId = "" + objectIdentifier.getRepositoryId();
				dirItId = "" + objectIdentifier.getDirectoryItemId();
			}
			try{
				OlapResult res = server.getCacheDisk().getCachedView(repId, dirItId, cubeInstance.getSchema().getId(), (runtimeContext == null ? "" : runtimeContext.getGroupName()), mdx);
				if (res != null){
					return res;
				}
			}catch(Exception ex){
				Logger.getLogger(getClass()).warn("Cache Disk recovery failure - " + ex.getMessage());
			}
			
		}
		
		
		
		
		logCacheInformations();
		
		logger.debug("Sql streamMode = " + isInStreamMode);
		helper.setSqlStreamMode(isInStreamMode);
		
		long quStart = new Date().getTime();
		
		logger.info("execute the query : " + mdx);
		
		long start = 0;
		long end = 0;
		
		//parse the query to create a tree
	
		RootItem resultTree = parserMdxQuery(mdx);
		logger.debug("mdx evaluator parsing time : " + (new Date().getTime() - quStart));	
		start = new Date().getTime();
		//get sorted runResult item by evaluate the tree
		IMdxEvaluator evaluator = FactoryMdxEvaluator.getMdxEvaluator(resultTree, cubeInstance, logger, runtimeContext, projection, server);//new MdxEvaluator(resultTree, cubeInstance, logger, runtimeContext);
		RunResult result = evaluator.evaluateResultTree();
		logger.debug("mdx evaluator findMember calls : " + ((MdxEvaluator)evaluator).getFindMemberCall());
		logger.debug("mdx evaluator findMember Time : " + ((MdxEvaluator)evaluator).getFindMemberTime());
		
		
		logger.debug("mdx evaluator evaluation calls : " + ((MdxEvaluator)evaluator).getEvaluationItemCalls());
		logger.debug("mdx evaluator evaluation Time : " + ((MdxEvaluator)evaluator).getEvaluationItemTime());

		
		end = new Date().getTime();
		
		logger.debug("mdx evaluator : " + (end - start));		
		
		start = new Date().getTime();
		//create the dataStorage item
		IDataStorageGenerator generator = new DataStorageGenerator();
		DataStorage storage = generator.generateDataStorage(result,logger);
		end = new Date().getTime();
		
		logger.debug("storage generation : " + (end - start));
		
		start = new Date().getTime();
		
		if (computeDatas){
			// prepare the query and return the effective query
			helper.prepareQuery(cubeInstance);
			
			
			//FIXME : DON'T FORGET !!!!
			
			LinkedHashMap<Integer, DataCell> cachedCells = CachedCellsUtil.findCachedCells(cubeInstance, storage.getDataCells(), server, helper.getEffectiveQuery());
			logger.debug("found computed cells from cache : " + cachedCells.size());
			
			List<DataCell> cells = removeCachedCellsFromQuery(cachedCells, storage);
//			List<DataCell> cells = storage.getDataCells();
//			LinkedHashMap<Integer, DataCell> cachedCells = new LinkedHashMap<Integer, DataCell>();
			logger.debug("found computed cells from cache : " + cachedCells.size());
			
			
			logger.debug("missing cells from cache : " + cells.size());
//			final LinkedHashMap<Integer, DataCell> cachedCells = CachedCellsUtil.findCachedCells(cubeInstance, storage.getDataCells(), server, helper.getEffectiveQuery());
//			logger.debug("found computed cells from cache : " + cachedCells.size());
//			List<DataCell> cells = removeCachedCellsFromQuery(cachedCells, storage);
//			logger.debug("missing cells from cache : " + cells.size());
//			storage.setPossibleIds(generator.createPossibleId(cells));
			storage.setPossibleIds(generator.createPossibleId(cells));
			
			
			storage.setNbCol(generator.findNbCol(storage.getDataCells()));
			
			logger.debug("Nb cols : " + storage.getNbCol());
			logger.debug("Max Nb cols : " + storage.getMaxNbCol());
			
			end = new Date().getTime();
			logger.debug("remove existing cells : " + (end - start));
			
			start = new Date().getTime();
			//execute the query
			try {
//				if(storage.getDataCells().size() != cachedCells.size()) {
					storage = helper.executeQuery(cubeInstance, storage, limit);
					
					QueryHelper.closeAllConnections();
					
//				}
			} catch (Exception e) {
				logger.error("execution error : ", e);
				e.printStackTrace();
				throw new Exception("An error occured during the query execution : ",e);
			}
			end = new Date().getTime();
			logger.debug("query execution time : " + (end - start));
			
			start = new Date().getTime();
//			if(server != null) {
//				refillStorageWithCachedCells(cachedCells, storage);
//			}
			end = new Date().getTime();
			logger.debug("refill storage : " + (end - start));
			
			//make calculs
			start = new Date().getTime();
			for(ICalculation calculation : evaluator.getComplexCalculations()) {
				if(calculation instanceof TopCountCalculation) {
					((TopCountCalculation)calculation).setRunResult(result);
				}
				storage.getCalculations().add(calculation);
			}
			result.setDataStorage(storage);
			storage.makeCalcul(result);
			end = new Date().getTime();
			logger.debug("calculs : " + (end - start));
			
			start = new Date().getTime();
			if(server != null) {
				Thread th = new CacherThread(storage,cachedCells, helper.getEffectiveQuery());
				th.start();
			}
			end = new Date().getTime();
			logger.debug("cache cells : " + (end - start));
		}
		else{
			result.setDataStorage(storage);
		}
		
		start = new Date().getTime();
		//create the result grid
		IOlapResultGenerator olapResultGen = new HierarchizedOlapResultGenerator();
		OlapResult olapRes = null;
		
		try{
			olapRes = olapResultGen.generateOlapResult(result);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		end = new Date().getTime();
		logger.debug("grid creation time : " + (end - start));
		
		long quEnd = new Date().getTime();
		
		logger.debug("Total time for query execution : " + (quEnd - quStart));
		
		logCacheInformations();
		
		//call the garbage collector to free memory
		result.getMdxSets().clear();
		result.setMdxSets(null);
		result = null;
		System.gc();
		
		
		
		if (server.getCacheDisk() != null && computeDatas){
			
			if (objectIdentifier == null){
				server.getCacheDisk().cacheView(CacheKey.Unkown, CacheKey.Unkown, cubeInstance.getSchema().getId(), runtimeContext.getGroupName() + "", mdx, olapRes);
			}
			else{
				
				server.getCacheDisk().cacheView(
						objectIdentifier.getRepositoryId() +"",
						objectIdentifier.getDirectoryItemId() +"",
						cubeInstance.getSchema().getId(), (runtimeContext == null ? "" : runtimeContext.getGroupName() + ""), mdx, olapRes);
			}
			
		}
		
		
		return olapRes;
	}

//	private void refillStorageWithCachedCells(LinkedHashMap<Integer, DataCell> cachedCells, DataStorage storage) {
//		int nbCol = storage.getMaxNbCol();
//		for(Integer i : cachedCells.keySet()) {
//			DataCell cell = cachedCells.get(i);
//			//TODO set good row and col
//			int row = i/nbCol;
//			int col = i%nbCol;
//			cell.setCol(col);
//			cell.setRow(row);
//			
//			storage.getDataCells().add(i, cell);
//		}
//	}

	private List<DataCell> removeCachedCellsFromQuery(LinkedHashMap<Integer, DataCell> cachedCells, DataStorage storage) {
		List<DataCell> cells = new ArrayList<DataCell>();
		int i = 0;
		for(Iterator<DataCell> iter = storage.getDataCells().iterator(); iter.hasNext();) {
			DataCell cell = iter.next();
			if(cachedCells.containsKey(i)) {
//				iter.remove();
				cell.setCalculated(true);
			}
			else {
				cells.add(cell);
			}
			i++;
		}
		return cells;
	}

	private void logCacheInformations() {
		if(server != null) {
			logger.debug("Cache server informations : ");
			logger.debug("Cache server used size : " + server.getCacheUsedSize());
			logger.debug("Cache server max size : " + server.getCacheMaxSize());
			logger.debug("Cache server item number : " + server.getCachedItemsNumber());
		}
	}

	/**
	 * Use the mdxParser to parse the query
	 * @param mdx
	 * @return
	 * @throws RecognitionException
	 */
	private RootItem parserMdxQuery(String mdx) throws RecognitionException {
		
		mdx = mdx.replace("\'", "");
		
		CharStream input = new ANTLRStringStreamCaseInsensitive(mdx);
		MdxLexer lexer = new MdxLexer(input);
		TokenStream tok = new CommonTokenStream(lexer);
		MdxParser parser = new MdxParser(tok);
		mdx_statement_return ret = parser.mdx_statement();
		CommonTree tree = (CommonTree) ret.getTree();
		CommonTreeNodeStream nodeStream = new CommonTreeNodeStream(tree);
		MdxTree walker = new MdxTree(nodeStream);
		RootItem resultTree = walker.resultTree();
		return resultTree;
	}
	
	@Override
	public void setSqlStreamMode(boolean isInStreamMode) {
		this.isInStreamMode = isInStreamMode;
	}
	
	
	private  class CacherThread extends Thread{
		DataStorage storage;
		LinkedHashMap<Integer, DataCell> cachedCells;
		String effectiveQuery;
		public CacherThread(DataStorage storage, LinkedHashMap<Integer, DataCell> cachedCells, String effectiveQuery) {
			this.storage = storage;
			this.cachedCells = cachedCells;
			this.effectiveQuery = effectiveQuery;
		}
		@Override
		public void run() {
			CachedCellsUtil.cacheCells(cubeInstance, storage.getDataCells(), server, cachedCells, effectiveQuery);
		}
	}
	
	
}
