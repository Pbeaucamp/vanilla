package bpm.united.olap.runtime.query.improver;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.eclipse.datatools.connectivity.oda.IQuery;

import bpm.metadata.birt.oda.runtime.ConnectionPool;
import bpm.metadata.layer.business.IBusinessModel;
import bpm.metadata.layer.business.IBusinessPackage;
import bpm.metadata.layer.business.IBusinessTable;
import bpm.metadata.layer.logical.IDataStreamElement;
import bpm.united.olap.api.datasource.DataObject;
import bpm.united.olap.api.datasource.DataObjectItem;
import bpm.united.olap.api.datasource.Relation;
import bpm.united.olap.api.model.Level;
import bpm.united.olap.api.model.Member;
import bpm.united.olap.api.runtime.DataCellIdentifier2;
import bpm.united.olap.api.runtime.DataStorage;
import bpm.united.olap.api.runtime.IRuntimeContext;
import bpm.united.olap.api.runtime.model.ICubeInstance;
import bpm.vanilla.platform.core.beans.data.OdaInput;
import bpm.vanilla.platform.logging.IVanillaLogger;

/**
 * A class to improve FMDT query
 * @author LCA
 *
 */
public class FMDTQueryImprover2 extends AbstractQueryImprover implements IQueryImprover {
//	private List<Member> members = new ArrayList<Member>();
//	private List<Level> usedLevel = new ArrayList<Level>();
//	private List<DataObjectItem> levelColumns = new ArrayList<DataObjectItem>();
//	private HashMap<DataObjectItem, List<Object>> parentFilters = new HashMap<DataObjectItem, List<Object>>();
//	private List<Relation> relations = new ArrayList<Relation>();
//	private HashMap<DataObjectItem, Integer> levelIndexes = new HashMap<DataObjectItem, Integer>();
//	private HashMap<DataObjectItem, Level> levelFkMapping = new HashMap<DataObjectItem, Level>();
	//the original FMDT Query
	private Element originalQuery;

//	private IVanillaLogger logger;
	
//	private String improvedQuery;
//	private IDataLocator improvedDataLocator;
//	private IRuntimeContext runtimeContext;
	
	public FMDTQueryImprover2(IVanillaLogger logger, IRuntimeContext runtimeContext) {
		super( logger,runtimeContext);
	}
	
//	private void extractLevelDataForOtherDataSource(Member member, ICubeInstance cubeInstance, DataCellIdentifier2 dataCellIdentifier) throws QueryImproverException{
//		Level lvl = member.getParentLevel();
//		if (lvl == null){
//			extractRootElementDatas(member, cubeInstance, dataCellIdentifier);
//			return;
//		}
//	
//		DataObjectItem col = cubeInstance.getFactTable().getItems().get(cubeInstance.getDataLocator().getResultSetIndex(lvl)); 
//		if (!levelColumns.contains(col)){
//			levelColumns.add(col);
//		}
//		if (!levelIndexes.containsKey(col)){
//			levelIndexes.put(col, levelIndexes.size());	
//		}
//		if (!levelIndexes.containsKey(lvl.getItem())){
//			levelIndexes.put(lvl.getItem(), levelIndexes.size());
//		}
//		if (!levelColumns.contains(lvl.getItem())){
//			levelColumns.add(lvl.getItem());
//		}
//		if (!levelFkMapping.containsKey(col)){
//			levelFkMapping.put(col, lvl);
//		}
//		
//		
//		if (!member.isChildsLoaded()){
//			try {
//				cubeInstance.getHierarchyExtractor(member.getParentHierarchy()).getChilds(member, runtimeContext);
//			} catch (Exception e) {
//				
//				e.printStackTrace();
//			}
//		}
//		for(Member m : member.getSubMembers()){
//			extractParentFilter(m, cubeInstance);
//		}
//		
//	}
//	
//	private void extractRootElementDatas(Member member, ICubeInstance cubeInstance, DataCellIdentifier2 dataCellIdentifier) throws QueryImproverException{
//		
//		Level lvl = member.getParentHierarchy().getLevels().get(0);
//		
//		
//		
//		if (lvl.getItem().getParent().getParent() == cubeInstance.getFactTable().getParent()){
//			if (!levelColumns.contains(lvl.getItem())){
//				levelColumns.add(lvl.getItem());
//			}
//			if (levelIndexes.get(lvl.getItem()) == null){
//				levelIndexes.put(lvl.getItem(), levelIndexes.size());
//			}
//			if(lvl.getOrderItem() != null) {
//				if (levelIndexes.get(lvl.getOrderItem()) == null){
//					levelIndexes.put(lvl.getOrderItem(), levelIndexes.size());
//				}
//				if (!levelColumns.contains(lvl.getOrderItem())){
//					levelColumns.add(lvl.getOrderItem());
//				}
//			}
//						
//			
//			extractRelations(member.getParentHierarchy(), lvl, cubeInstance);
//			//TODO generate where clause
//		}
//		else{
//			DataObjectItem col = cubeInstance.getFactTable().getItems().get(cubeInstance.getDataLocator().getResultSetIndex(lvl)); 
//			if (!levelColumns.contains(col)){
//				levelColumns.add(col);
//			}
//			if (!levelIndexes.containsKey(col)){
//				levelIndexes.put(col, levelIndexes.size());	
//			}
//			
//			
//			
//			if (!member.isChildsLoaded()){
//				try {
//					cubeInstance.getHierarchyExtractor(member.getParentHierarchy()).getChilds(member, runtimeContext);
//				} catch (Exception e) {
//					
//					e.printStackTrace();
//				}
//			}
//			for(Member m : member.getSubMembers()){
//				extractParentFilter(m, cubeInstance);
//			}
//		}
//		
//		
//		
//	}
	
//	@Override
//	public void improveQuery(OdaInput odaInput, IQuery query, DataStorage storage, ICubeInstance cubeInstance, DataCellIdentifier2 possibleId) throws Exception {
//		improvedQuery = null;
//		improvedDataLocator = null;
//		
//			
//		//find all foreign keys we have to keep in the query
//		for(ElementDefinition elem : possibleId.getIntersections()) {
//			if(elem instanceof Member) {
//				Member mem = (Member) elem;
//				members.add(mem);
//				
//				if (mem.getParentLevel() == null){
//					extractRootElementDatas(mem, cubeInstance, possibleId);
//				}
//				else{
//					try{
//						extractLevelData(mem, cubeInstance, possibleId);
//					}catch(QueryImproverException ex){
//						logger.warn(ex.getMessage());
//						logger.warn("The optimized Query will be using all foreign keys on he fact table because of the heteregenous datas providers");
//						
//						extractLevelDataForOtherDataSource(mem, cubeInstance, possibleId);
//					}
//				}
//				
//				
//				
//				
//			}
//		}
//		
//		//add the level from the where clause
//		for(Member m : possibleId.getWhereMembers()){
//			try{
//				extractLevelData(m, cubeInstance, possibleId);
//			}catch(QueryImproverException ex){
//				throw ex;
//			}
//		}
//		
//		/*
//		 * DataLocator creation
//		 */		
//		//measureMapping
//		HashMap<DataObjectItem, Integer> measureIndexes = new HashMap<DataObjectItem, Integer>();
//		HashMap<DataObjectItem, String> measureAggregations = new HashMap<DataObjectItem, String>();
//		setMeasureInformations(possibleId.getMeasure(), cubeInstance, measureIndexes, measureAggregations, possibleId);
//
//		
//		//find foreign keys for wheres elements 
//		HashMap<DataObjectItem, List<Object>> wheresKeys = new HashMap<DataObjectItem, List<Object>>();
//		for(Member elem : possibleId.getWhereMembers()) {
//				
//			List<Object> keys = cubeInstance.getHierarchyExtractor(elem.getParentHierarchy()).getMemberForeignKeys(elem, runtimeContext);
//			if(keys != null && keys.size() > 0) {
//				
//				DataObjectItem col = cubeInstance.getFactTable().getItems().get(cubeInstance.getDataLocator().getResultSetIndex(elem));
//				
//				
//				if(wheresKeys.get(col) != null) {
//					wheresKeys.get(col).addAll(keys);
//				}
//				else {
//					wheresKeys.put(col, keys);
//				}
//			}
//		}
//		
//		String fmdtQuery = odaInput.getQueryText();
//		originalQuery = DocumentHelper.parseText(fmdtQuery).getRootElement();
//		String sql = generateSql(cubeInstance, possibleId, measureIndexes, measureAggregations, wheresKeys);
//		
//		
//
//		measureIndexes = new HashMap<DataObjectItem, Integer>();
//		measureIndexes.put(possibleId.getMeasure().getItem(), levelIndexes.size());
//		
//		for(DataObjectItem i : levelIndexes.keySet()){
//			if (i.getParent().getParent() != cubeInstance.getFactTable().getParent()){
//				measureIndexes.put(possibleId.getMeasure().getItem(), measureIndexes.get(possibleId.getMeasure().getItem()) - 1);
//			}
//		}
//		
//		improvedQuery = sql;
//		improvedDataLocator = new ImprovedDataLocator(measureIndexes, levelIndexes, levelFkMapping);
//		
//
//		logger.debug("Transform sql query : ");
//		logger.debug("From : " + fmdtQuery);
//		logger.debug("To : " + improvedQuery);
//	}
	
	@Override
	public void improveQuery(OdaInput odaInput, IQuery query,
			DataStorage storage, ICubeInstance cubeInstance,
			DataCellIdentifier2 possibleId) throws Exception {
		
	
		try {
			originalQuery = DocumentHelper.parseText(odaInput.getQueryText()).getRootElement();
			super.improveQuery(odaInput, query, storage, cubeInstance, possibleId);
		} catch (Exception e) {
			throw new QueryImproverException("Error while improving the query", e);
		}
	}

	
	private String findDataStreamName(DataObjectItem col, DataObject factTable) throws Exception{
		//first we look in the factTable
		List<Element> originalSelect = originalQuery.element("sqlQuery").elements("select");
		for(Element e : originalSelect){
			if (e.element("dataStreamElementName").getText().equals(col.getName()) || e.element("dataStreamElementName").getText().endsWith("." + col.getName())){
				return e.element("dataStreamName").getText();
			}
		}
		
		originalSelect = originalQuery.element("sqlQuery").elements("agg");
		for(Element e : originalSelect){
			if (e.element("col").getText().equals(col.getName()) || e.element("col").getText().endsWith("." + col.getName())){
				return e.element("dataStreamName").getText();
			}
		}
		
		//we check from the Level origins
		for(Level l : getUsedLevel()){
			if (l.getItem().getParent() == factTable){
				continue;
			}
			originalSelect = DocumentHelper.parseText(l.getItem().getParent().getQueryText()).getRootElement().element("sqlQuery").elements("select");
			for(Element e : originalSelect){
				if (e.element("dataStreamElementName").getText().equals(col.getName()) || e.element("dataStreamElementName").getText().endsWith("." + col.getName())){
					return e.element("dataStreamName").getText();
				}
			}
		}
		return null;
	}
	
	protected String generateSql(ICubeInstance cubeInstance,
			DataCellIdentifier2 possibleId,
			HashMap<DataObjectItem, Integer> measureIndexes,
			HashMap<DataObjectItem, String> measureAggregations,
			HashMap<DataObjectItem, List<Object>> wheresKeys) throws Exception{
		

		Element rootDoc = DocumentHelper.createElement("freeMetaDataQuery");
		Element root = rootDoc.addElement("sqlQuery");
		//doc.setRootElement(root);
		root.addElement("distinct").setText("false");
		root.addElement("limit").setText("0");
		
		//select
//		List<Element> originalSelect = originalQuery.element("sqlQuery").elements("select");
		List<DataObjectItem> selectItems = getLevelColumns();
		for(DataObjectItem i : selectItems){
			
			if (i.getParent().getParent() != cubeInstance.getFactTable().getParent()){
				continue;
			}
			
			String dsName = findDataStreamName(i, cubeInstance.getFactTable());
			if(dsName != null) { 
				Element select = root.addElement("select");
				select.addElement("dataStreamElementName").setText(i.getName());
				select.addElement("dataStreamName").setText(dsName);
			}
		}
		
		for(Relation r : getRelations()){
			
			
			if (r.getLeftItem().getParent().getParent() == cubeInstance.getFactTable().getParent() && r.getRightItem().getParent().getParent() == cubeInstance.getFactTable().getParent()){
				Element filter = root.addElement("relationClause");
				filter.addElement("leftDataStreamElement").setText(r.getLeftItem().getName());
				filter.addElement("leftDataStream").setText(findDataStreamName(r.getLeftItem(), cubeInstance.getFactTable()));

				filter.addElement("rightDataStreamElement").setText(r.getRightItem().getName());
//				try {
					filter.addElement("rightDataStream").setText(findDataStreamName(r.getRightItem(), cubeInstance.getFactTable()));
//				} catch(Exception e) {
//					e.printStackTrace();
//				}

				
				
			}
			else{
				//mixed DataSources
				//an exception should have been thrown
				throw new QueryImproverException("Cannot improve FMDT if another DataSource is needed");
			}	
			
		}
		//where clause for paretFilter from Level members
		for(DataObjectItem i : getParentFilters().keySet()){
			if (!getParentFilters().get(i).isEmpty()){
				Element where = root.addElement("sqlqueryfilter");
				String dataStreamName = findDataStreamName(i, cubeInstance.getFactTable());
				where.addElement("dataStream").setText(dataStreamName);
				where.addElement("dataStreamElement").setText(i.getName());
				Element values = where.addElement("query");
				StringBuilder buf = new StringBuilder();
				
				//find column origin
				IDataStreamElement originName = null;
				Properties p = new Properties();
				p.putAll(cubeInstance.getFactTable().getParent().getPublicProperties());
				p.putAll(cubeInstance.getFactTable().getParent().getPrivateProperties());
				LOOP:for(IBusinessModel model : ConnectionPool.getConnection(p)) {			
					for(IBusinessPackage pac : model.getBusinessPackages("none")) {
						for(IBusinessTable t : pac.getBusinessTables("none")) {
							for(IDataStreamElement e : t.getColumns("none")) {
								if(e.getName().endsWith(i.getName())) {
									originName = e;
									break LOOP;
								}
							}
							
						}
					}					
				}
				
				
				
//				buf.append(" LIKE '%' and replace(" + i.getName().replace("SERPERM_LIBELLE", "LIBELLE_PERMANENT_") + ",'''','') in (");
				
				//Vu avec MLA - :: TEXT ne marche pas sur oracle
//				buf.append("::TEXT LIKE '%' and replace(cast(" + "`" + originName.getDataStream().getName() + "`." + originName.getOrigin().getShortName() + " as TEXT),'''','') in (");
				if (originName.getOrigin().getClassName().toLowerCase().contains("string")) {
					buf.append(" LIKE '%' and replace(" + "`" + originName.getDataStream().getName() + "`." + originName.getOrigin().getShortName() + ",'''','') in (");
				}
				else {
					buf.append(" LIKE '%' and replace(cast(" + "`" + originName.getDataStream().getName() + "`." + originName.getOrigin().getShortName() + " as TEXT),'''','') in (");	
				}
				boolean first = true;
				for(Object o : getParentFilters().get(i)) {
					if(first) {
						first = false;
					}
					else {
						buf.append(",");
					}
					buf.append("'" + o.toString() + "'");
//					values.addElement("value").setText(o.toString());
				}
				buf.append(")");
				values.setText(buf.toString());
			}
		}
		//where clause from MDX where
		List<DataObjectItem> usedWhereItems = new ArrayList<DataObjectItem>();
		for(DataObjectItem i: wheresKeys.keySet()) {
			if (i.getParent().getParent() != cubeInstance.getFactTable().getParent()){
				continue;
			}
			if (!wheresKeys.get(i).isEmpty()){
				Element where = root.addElement("where");
				where.addElement("dataStream").setText(findDataStreamName(i, cubeInstance.getFactTable()));
				where.addElement("dataStreamElement").setText(i.getName());
				Element values = where.addElement("values");
				for(Object obbj : wheresKeys.get(i)) {
					values.addElement("value").setText(obbj.toString());
				}
				
			}
			usedWhereItems.add(i);
		}		
		
//		for(DataObjectItem i : getParentFilters().keySet()){
//			if(!usedWhereItems.contains(i)) {
//				if (getParentFilters().get(i) != null && !getParentFilters().get(i).isEmpty()){
//					
//					Element where = root.addElement("where");
//					where.addElement("dataStream").setText(findDataStreamName(i, cubeInstance.getFactTable()));
//					where.addElement("dataStreamElement").setText(i.getName());
//					Element values = where.addElement("values");
//					
//					for(Object o : getParentFilters().get(i)) {
//						if(wheresKeys.get(i) == null || !wheresKeys.get(i).contains(o)) {
//							values.addElement("value").setText(o.toString());
//						}
//					}
//					for (Member wh : possibleId.getWhereMembers()) {
//						if(wh.getParentLevel().getItem().getId().equals(i.getId())) {
//							if(!getParentFilters().get(i).contains(wh.getName())) {
//								values.addElement("value").setText(wh.getName());
//							}
//						}
//					}	
//				}
//			}
//		}
		
		//first add fmdtagg 
		for(DataObjectItem mesUname : measureIndexes.keySet()) {
			if(measureAggregations.get(mesUname).equals("FMDT-AGG")) {
				Element aggElem = findFmdtAggregation(mesUname);
				
				Element elem = root.addElement("agg");
				elem.addElement("function").setText(aggElem.elementText("function"));
				elem.addElement("col").setText(aggElem.elementText("col"));
				elem.addElement("outputName").setText(aggElem.elementText("outputName"));
				elem.addElement("dataStreamName").setText(aggElem.elementText("dataStreamName"));
				
				Element grpsElem = aggElem.element("groups");
				if(grpsElem != null) {
					Element aggGroups = elem.addElement("groups");
					
					for(Element grpElem : (List<Element>) grpsElem.elements("groupBy")) {
						Element aggGrp = aggGroups.addElement("groupBy");
						aggGrp.addElement("col").setText(grpElem.elementText("col"));
						aggGrp.addElement("dataStreamName").setText(grpElem.elementText("dataStreamName"));
					}
					
				}
				
			}
		}
		
		
		//add aggregation
		//add aggregation
		
		for(DataObjectItem mesUname : measureIndexes.keySet()) {
			
			if (measureAggregations.get(mesUname).equals("first") || measureAggregations.get(mesUname).equals("last")){
				Element sel = root.addElement("select");
				
				
				sel.addElement("dataStream").setText(findDataStreamName(mesUname, cubeInstance.getFactTable()));
				sel.addElement("dataStreamElement").setText(mesUname.getName());
				
			}
			
			else if (!measureAggregations.get(mesUname).equals("FMDT-AGG")){
				Element el = root.addElement("agg");
				el.addElement("function").setText(measureAggregations.get(mesUname).toLowerCase().replace("distinct-count", "DISTINCT COUNT"));
				el.addElement("col").setText(mesUname.getName());
				el.addElement("outputName").setText(mesUname.getName());
				
				el.addElement("dataStreamName").setText(findDataStreamName(mesUname, cubeInstance.getFactTable()));
				el.addElement("col").setText(mesUname.getName());
	
				
				//group bys
				Element groups = el.addElement("groups");
				for(DataObjectItem i : getLevelColumns()){
					if (i.getParent().getParent() != cubeInstance.getFactTable().getParent()){
						continue;
					}
					String dsName = findDataStreamName(i, cubeInstance.getFactTable());
					if(dsName != null) {
						Element group = groups.addElement("groupBy");
						group.addElement("col").setText(i.getName());
						group.addElement("dataStreamName").setText(dsName);
					}
					
				}
			}
			
			
		}

		try{
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			OutputFormat output = OutputFormat.createPrettyPrint();
			output.setTrimText(false);
			XMLWriter writer = new XMLWriter(bos,output);
			writer.write(rootDoc);
			writer.close();
			
			return bos.toString("UTF-8");
		}catch(Exception ex){
			throw new Exception("Error when generation FMDT Query Xml - " + ex.getMessage(), ex);
		}
		
		
	}

	private Element findFmdtAggregation(DataObjectItem mesUname)  throws Exception {
		Document doc = DocumentHelper.parseText(mesUname.getParent().getQueryText());
		
		Element rootElem = doc.getRootElement();
		
		for(Element aggElem : (List<Element>) rootElem.element("sqlQuery").elements("agg")) {
			String elemName = aggElem.elementText("outputName");
			if(elemName != null && elemName.equals(mesUname.getName())) {
				return aggElem;
			}
		}
		
		return null;
	}

	public boolean isFmdtAggregation(Member member, ICubeInstance cubeInstance, DataCellIdentifier2 dataCellIdentifier, Level lvl) throws Exception {
		//FIXME : look in the original fmdtQuery if its an aggregation
		
		DataObjectItem searchedItem = lvl.getItem();
		
		Document doc = DocumentHelper.parseText(lvl.getItem().getParent().getQueryText());
		
		Element rootElem = doc.getRootElement();
		
		for(Element aggElem : (List<Element>) rootElem.element("sqlQuery").elements("agg")) {
			String elemName = aggElem.elementText("outputName");
			if(elemName != null && elemName.equals(searchedItem.getName())) {
				return true;
			}
		}
		
		return false;
	}


	
	
	
//	/**
//	 * 
//	 * @param member
//	 * @param cubeInstance
//	 * @throws Exception if the Level DataObject (or a previous Level ) is not on a JDBC DataObject
//	 */
//	private void extractLevelData(Member member, ICubeInstance cubeInstance, DataCellIdentifier2 dataCellIdentifier) throws QueryImproverException{
//		
//		if (member.getParentLevel() == null){
//			return;
//		}
//		Level lvl = member.getParentLevel();
//		
//		while(lvl != null) {
////			int index = cubeInstance.getDataLocator().getResultSetIndex(lvl);
//			
//			if (!lvl.getItem().getParent().getParent().getDatasourceExtensionId().equals(FactoryQueryHelper.FMDT)){
//				throw new QueryImproverException("We cannot improve the FMDT query because the Level " + lvl.getParentHierarchy().getUname() + "." + lvl.getUname() + " is not on FMDT dataSource", dataCellIdentifier);
//			}
//
//			for(DataObjectItem it : levelColumns){
//				if (it.getParent().getParent() != lvl.getItem().getParent().getParent()){
//					throw new QueryImproverException("We cannot improve the FMDT query because the Level " + lvl.getParentHierarchy().getUname() + "." + lvl.getUname() + " has a different datasource than others Hierarchy's Levels", dataCellIdentifier);
//				}
//			}
//			
//			if (levelIndexes.get(lvl.getItem()) == null){
//				levelIndexes.put(lvl.getItem(), levelIndexes.size());
//			}
//			
//			if (!levelColumns.contains(lvl.getItem())){
//				levelColumns.add(lvl.getItem());
//			}
//			if(lvl.getOrderItem() != null) {
//				if (levelIndexes.get(lvl.getOrderItem()) == null){
//					levelIndexes.put(lvl.getOrderItem(), levelIndexes.size());
//				}
//				if (!levelColumns.contains(lvl.getOrderItem())){
//					levelColumns.add(lvl.getOrderItem());
//				}
//			}
//			
//			if (!usedLevel.contains(lvl)){
//				usedLevel.add(lvl);
//			}
//			lvl = lvl.getParentLevel();
//			
//		}
//		if (!levelColumns.contains(member.getParentLevel().getItem())){
//			levelColumns.add(member.getParentLevel().getItem());
//		}
//		if (!usedLevel.contains(member.getParentLevel())){
//			usedLevel.add(member.getParentLevel());
//		}
//		
//		
//		if (member.getParentMember() != null){
//			extractParentFilter(member.getParentMember(), cubeInstance);
//		}
//		extractRelations(member.getParentHierarchy(), member.getParentLevel(), cubeInstance);
//
//	}
//	
//	
//	private void extractRelations(Hierarchy hiera, Level lvl, ICubeInstance cubeInstance){
//		DataObject lvlDataObject = null;
//		
//		List<Relation> path = new ArrayList<Relation>();
//		
//		if (lvl == null){
//			lvlDataObject = hiera.getLevels().get(0).getItem().getParent();
//			
//		}
//		else{
//			lvlDataObject = lvl.getItem().getParent();
//			
//			
//			
//			//get path between previousLevel and lvlTable
//			for(Level l : usedLevel){
//				if (l.getParentHierarchy() == hiera && l != lvl && l.getItem().getParent() != lvlDataObject){
//					try{
//						path.addAll(cubeInstance.getDataLocator().getPath(l.getItem().getParent(), lvlDataObject));
//					}catch(Exception ex){
//						ex.printStackTrace();
//					}
//				}
//			}
//			
//		}
//		
//		//get path between factTable and lvlTable
//		try{
//			List<Relation> l = cubeInstance.getDataLocator().getPath(cubeInstance.getFactTable(), lvlDataObject);
//			if (l != null){
//				path.addAll(l);
//			}
//			
//		}catch(Exception ex){
//			ex.printStackTrace();
//		}
//		
//		//add the relations
//		for(Relation r : path){
//			if (!relations.contains(r)){
//				relations.add(r);
//			}
//		}
//	
//		
//	}
//	
//	/**
//	 * put the values FK for the give member within this.parentFilter 
//	 */
//	private void extractParentFilter(Member member, ICubeInstance cubeInstance){
//		if (member == null){
//			return;
//		}
//		if (member.getParentLevel() == null){
//			return;
//		}
//		try{
//			DataObjectItem colKey = cubeInstance.getFactTable().getItems().get(cubeInstance.getDataLocator().getResultSetIndex(member));
//			
//			if (member.getParentLevel() instanceof ClosureLevel){
//				colKey = ((ClosureLevel)member.getParentLevel()).getParentItem();
//			}
//			
//			List<Object> values = new ArrayList<Object>();
//			values.addAll(getChildForeignKeys(cubeInstance, member));
//			if (parentFilters.get(colKey) == null){
//				parentFilters.put(colKey, values);
//			}
//			else{
//				for(Object v : values){
//					if (!parentFilters.get(colKey).contains(v)){
//						parentFilters.get(colKey).add(v);
//					}
//				}
//			}
//			
//			
//		}catch(Exception ex){
//			ex.printStackTrace();
//		}
//	}
//	
//	
//	private List<Object> getChildForeignKeys(ICubeInstance cubeInstance, Member mb) throws Exception{
//		List<Object> values = new ArrayList<Object>();
//		
//		values.addAll(cubeInstance.getHierarchyExtractor(mb.getParentHierarchy()).getMemberForeignKeys(mb, runtimeContext));
//		
//		if (!mb.isChildsLoaded()){
//			cubeInstance.getHierarchyExtractor(mb.getParentHierarchy()).getChilds(mb, runtimeContext);
//		}
//		if (mb.getParentHierarchy().isIsClosureHierarchy()){
//			for(Member m : mb.getSubMembers()){
//				values.addAll(getChildForeignKeys(cubeInstance, m));
//			}
//		}
//		
//		return values;
//	}
}
