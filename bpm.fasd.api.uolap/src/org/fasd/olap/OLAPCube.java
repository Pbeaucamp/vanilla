package org.fasd.olap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;


import org.fasd.datasource.DataObject;
import org.fasd.datasource.DataObjectItem;
import org.fasd.datasource.DataSource;
import org.fasd.datasource.DataSourceConnection;
import org.fasd.datasource.DatasourceOda;
import org.fasd.datasource.IConnection;
import org.fasd.inport.DigesterMolapMapping;
import org.fasd.molap.MOLAPMappingContext;
import org.fasd.olap.aggregate.AggPattern;
import org.fasd.olap.aggregate.AggregateTable;
import org.fasd.olap.aggregate.MondrianAgg;
import org.fasd.olap.list.ListDimension;
import org.fasd.olap.list.ListMeasure;
import org.fasd.olap.list.ListSecurityGroup;
import org.fasd.security.SecurityGroup;
import org.fasd.utils.ActionGetPath;
import org.fasd.utils.Path;
import org.fasd.utils.Zipper;

public class OLAPCube extends OLAPElement implements ICube{
	private static int counter = 0;
	public static void resetCounter(){
		counter =0;
	}
	
	private DataSource dataSource;
	
	private String factDataObjectId = "";
	private DataObject factDataObject;

	private String description = "";
	private String type = "Rolap"; //molap or rolap
//	private String physicalName = "";
//	private String location = "";
	private String dataSourceId;
	
	private String tableName = "";
	
	private String defaultMeasure = "";
	private boolean enable = true;
	private boolean cache = true;
	
	private OLAPSchema parent;
	
	private List<DataObjectItem> listIndex = new ArrayList<DataObjectItem>();
	
	private List<String> dimIdList = new ArrayList<String>();
	private List<String> mesIdList = new ArrayList<String>();
	private List<String> secuIdList = new ArrayList<String>();
	
	private List<DimUsage> dimUsages = new ArrayList<DimUsage>();
	private ListSecurityGroup secuList = new ListSecurityGroup();
	
	//private List<OLAPDimension> dims = new ArrayList<OLAPDimension>();
	//private List<OLAPMeasure> mes = new ArrayList<OLAPMeasure>();
	private ListMeasure mes = new ListMeasure();
	private ListDimension dims = new ListDimension();
	
	private HashMap<String, OLAPDimensionGroup> dimGroups = new HashMap<String, OLAPDimensionGroup>();
	private HashMap<String, OLAPMeasureGroup> mesGroups = new HashMap<String, OLAPMeasureGroup>();
	
	private List<AggregateTable> aggTables = new ArrayList<AggregateTable>();
	private List<String> aggTablesId = new ArrayList<String>();
	
	private List<MondrianAgg> mondrianAgg = new ArrayList<MondrianAgg>();
	private List<AggPattern> aggPattern = new ArrayList<AggPattern>();
	private List<String> namedSetId = new ArrayList<String>();
	private List<NamedSet> namedSet = new ArrayList<NamedSet>();
	
	
	private List<CubeView> cubeViews = new ArrayList<CubeView>();
	
	
	private List<Drill> drills = new ArrayList<Drill>();
	
	private List<String> openedDimensions = new ArrayList<String>();
	
	public OLAPCube(String name, String table) {
		super(name);
		factDataObjectId = table;
		counter++;
		id = "i" + String.valueOf(counter);
	}
	
	public void addOpenedDimension(String dimName){
		openedDimensions.add(dimName);
	}
	
	public void removeOpenedDimension(String dimName){
		openedDimensions.remove(dimName);
	}
	
	public void swapDimensions(OLAPDimension d1, OLAPDimension d2){
		dims.swap(d1, d2);
		
		Integer i1 = null;
		Integer i2 = null;
		int i = 0;
		for(String s : openedDimensions){
			if (s.equals(d1.getName())){
				i1 = i;
			}
			if (s.equals(d2.getName())){
				i2 = i;
			}
			i++;
		}
		
		if (i1 != null && i2 != null){
			openedDimensions.set(i1, d2.getName());
			openedDimensions.set(i2, d1.getName());
		}
	}
	
	/**
	 * just used for parsing MondrianFile
	 * @param du
	 */
	public void addDimUsage(DimUsage du){
		dimUsages.add(du);
	}
	
	/**
	 * just used for parsing MondrianFile
	 * @param du
	 */
	public List<DimUsage> getDimUsages(){
		return dimUsages;
	}
	
	public List<Drill> getDrills(){
		return drills;
	}
	
	public void addDrill(Drill d){
		drills.add(d);
	}
	
	public void removeDrill(Drill d){
		drills.remove(d);
	}
	public OLAPCube() {
		super("");
		counter++;
		id = "i" + String.valueOf(counter);
	}
	
	/**
	 * in case of a molap cube, the fact table is ths one in the original datasource
	 * @return the fact Table
	 */
	public DataObject getFactDataObject() {
		return factDataObject;
	}
	
	public void setId(String id) {
		this.id = id;
		int i = Integer.parseInt(id.substring(1));
		if (i > counter){
			counter = i + 1;
		}
	}

	public void setFactDataObject(DataObject factTable) {
		this.factDataObject = factTable;
		if (factDataObject != null){
			this.factDataObjectId = factTable.getId();
			dataSource = factTable.getDataSource();
		}
			
//		update(this,factTable);
	}

//	/**
//	 * folder containing the files for a molap cube
//	 * @return
//	 */
//	public String getLocation() {
//		return location;
//	}
//
//	public void setLocation(String location) {
//		this.location = location;
////		update(this, location);
//	}

//	/**
//	 * name of molap cube
//	 * @return
//	 */
//	public String getPhysicalName() {
//		return physicalName;
//	}
//
//	public void setPhysicalName(String physicalName) {
//		this.physicalName = physicalName;
////		update(this, physicalName);
//	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		if (description != null)
			this.description = description;
		else this.description = "";
//		update(this, description);
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		if (type != null)
			this.type = type;
		else 
			this.type = "Rolap";
		
		if (type.equals("Rolap"))
			dataSource = null;
		
	}

	public void addDim(OLAPDimension d){
		if (d != null)
		{
			dims.add(d);
		}
		
	}
	/**
	 * 
	 * @return the dimension to open at startup in the right order
	 */
	public List<OLAPDimension> getStartupDimensions(){
		List<OLAPDimension> l = new ArrayList<OLAPDimension>();
		
		for(String s : openedDimensions){
			for(OLAPDimension d : getDims()){
				if (d.getName().equals(s)){
					l.add(d);
				}
			}
		}
		
		return l;
	}
	
	public void removeDim(OLAPDimension d){
		dims.remove(d);
		
		for(String s : openedDimensions){
			if (d.getName().equals(s)){
				openedDimensions.remove(s);
				break;
			}
		}
//		update(this, dims);
	}
	
	public void addDimId(String s){
		dimIdList.add(s);
//		update(this, s);
	}
	
	public void addSecuId(String s){
		secuIdList.add(s);
	}
	
	public void addMes(OLAPMeasure m){
		mes.add(m);
	}
	
	public void removeMes(OLAPMeasure m){
		mes.remove(m);
	}
	
	public void addMesId(String s){
		mesIdList.add(s);
	}
	
		

	public String getXML() {
		String buf = "    <Cube name=\"" + super.getName() + "\"";
		if (!cache)
			buf += " cache=\"false\"";
		if(!enable)
			buf += " enable=\"false\"";
		if (! defaultMeasure.trim().equals(""))
			buf += " defaultMeasure=\"" + defaultMeasure + "\"";
		
		buf += ">\n";
		if (factDataObject != null){
			if (factDataObject.isView()){
				if (factDataObject.isView()){
					
					buf += "        <View alias=\""+ factDataObject.getName() + "\" >\n";
					buf += "            <SQL dialect=\"generic\" >\n";
					buf += "            " + factDataObject.getSelectStatement().replace("<", "&lt;") + " \n";
					buf += "            </SQL>\n";
					
					buf += "        </View>\n";
				}
				
				
			}else{
				if (aggTables.size() > 0){
					
					String tableName = factDataObject.getPhysicalName();
					
					if (tableName.contains(".")){
						buf += "        <Table name=\""+ tableName.substring( tableName.indexOf(".") + 1) + "\"";
						buf += " schema=\"" + tableName.substring(0, tableName.indexOf(".")) + "\" ";
					}
					else{
						buf += "        <Table name=\""+ tableName + "\"";

					}
					
					
					buf += ">\n";
					for(AggregateTable a : aggTables){
						List<OLAPRelation> fk = new ArrayList<OLAPRelation>();
						for(OLAPRelation r : getParent().getParent().getRelations()){
							if (r.isUsingTable(a.getTable()) && r.isUsingTable(factDataObject)){
								fk.add(r);
							}
						}
						
						
						buf += a.getXML(fk);
					}
					
					for(AggPattern p : aggPattern){
						p.getXML();
					}
					
					
					buf += "        </Table>\n";
				}
				else{
					

//						buf += "        <Table name=\""+ factDataObject.getShortName() + "\"";
//						if (factDataObject.getDataSource().getDriver().getSchemaName() != null && !factDataObject.getDataSource().getDriver().getSchemaName().trim().equals("")){
//							buf += " schema=\"" + factDataObject.getDataSource().getDriver().getSchemaName() + "\"";
//						}
					
					
					String tableName = factDataObject.getPhysicalName();
					
					if (tableName.contains(".")){
						buf += "        <Table name=\""+ tableName.substring( tableName.indexOf(".") + 1) + "\"";
						buf += " schema=\"" + tableName.substring(0, tableName.indexOf(".")) + "\" ";
					}
					else{
						buf += "        <Table name=\""+ tableName + "\"";
					}
					
					buf += "/>\n";
				}
			}
			
		}
			
		else{
			if (aggTables.size() > 0){
				
//				buf += "        <Table name=\""+ tableName + "\">\n";
				
				String tableName = factDataObject.getPhysicalName();
				
				if (tableName.contains(".")){
					buf += "        <Table name=\""+ tableName.substring( tableName.indexOf(".") + 1) + "\"";
					buf += " schema=\"" + tableName.substring(0, tableName.indexOf(".")) + "\" ";
				}
				else{
					buf += "        <Table name=\""+ tableName + "\"";

				}
				
				buf += "/>\n";
				for(AggregateTable a : aggTables){
					List<OLAPRelation> fk = new ArrayList<OLAPRelation>();
					for(OLAPRelation r : getParent().getParent().getRelations()){
						if (r.isUsingTable(a.getTable()) && r.isUsingTable(factDataObject)){
							fk.add(r);
						}
					}
					buf += a.getXML(fk);
				}
				
				for(AggPattern p : aggPattern){
					p.getXML();
				}
				
				buf += "        </Table>\n"; 
			}
			else{
				buf += "        <Table name=\""+ tableName + "\"/>\n";
			}
			
		}
			

		
		
		for (OLAPDimension d : dims.getList()){
			if (!d.isDegenerated(factDataObject))
				buf += d.getUsageXML(factDataObject);
			else{
				buf += d.getXML(factDataObject);
			}
			
		}
		
		for (OLAPDimensionGroup g : dimGroups.values()){
			for(OLAPDimension d : g.getDimensions()){
				if (!isInDimUsage(d))
					if (!d.isDegenerated(factDataObject))
						buf += d.getUsageXML(factDataObject);
					else{
						buf += d.getXML(factDataObject);
					}
			}
		}
		
//		for(DimUsage d : dimUsages){
//			
//			//XXX creqte reference to schema
//			OLAPDimension dim = getParent().findDimensionByName(d.getSource());
//			dim.setForeignKey(d.getFkey());
//			buf += dim.getXML(factDataObject);
//			dim.setForeignKey("");
//		}
		
//		measures
		for (OLAPMeasure m : mes.getList()){
			if (m.getType().equals("physical")){
				if ("last".equals(m.getAggregator())) {
					
					for(OLAPDimension d : getDims()){
						try{
							if (d.getName().equals(m.getLastTimeDimensionName())){
								OLAPLevel l = d.getHierarchies().get(0).getLevels().get(d.getHierarchies().get(0).getLevels().size() - 1);
								buf += m.getXMLForLastAggregator(getParent().getParent(), l.getName());
								break;
							}
						}catch(Exception ex){
							ex.printStackTrace();
						}
						
					}
					
				}
				else{
					buf += m.getXML(getParent().getParent());
				}
			}
				
		}
		
		for (OLAPMeasureGroup g : mesGroups.values()){
			for(OLAPMeasure m : g.getMeasures()){
				if (m.getType().equals("physical")){
					if ("last".equals(m.getAggregator())) {
						
						for(OLAPDimension d : getDims()){
							try{
								if (d.getName().equals(m.getLastTimeDimensionName())){
									OLAPLevel l = d.getHierarchies().get(0).getLevels().get(d.getHierarchies().get(0).getLevels().size() - 1);
									buf += m.getXMLForLastAggregator(getParent().getParent(), l.getName());
									break;
								}
							}catch(Exception ex){
								ex.printStackTrace();
							}
							
						}
						
					}
					else{
						buf += m.getXML(getParent().getParent());
					}
						
				}
					
			}
		}
		
		//calculated measures
		for (OLAPMeasure m : mes.getList()){
			if (m.getType().equals("calculated"))
				buf += m.getXML(getParent().getParent());
		}
		
		for (OLAPMeasureGroup g : mesGroups.values()){
			for(OLAPMeasure m : g.getMeasures()){
				if (m.getType().equals("calculated"))
					buf += m.getXML(getParent().getParent());
			}
		}

		for(NamedSet n : namedSet)
			if (!n.isGlobal())
				buf += n.getXML();
		
		
		
		buf += "    </Cube>\n";
		
		return buf;
	}
	
	public String getFAXML() {
		String buf = "    <Cube-item>\n";
		buf += "        <id>" + super.getId() + "</id>\n";
		buf += "        <name>" + super.getName() + "</name>\n";
		buf += "        <defaultMeasure>" + defaultMeasure + "</defaultMeasure>\n";
		buf += "        <enabled>" + enable + "</enabled>\n";
		buf += "        <cached>" + cache + "</cached>\n";
		buf += "        <description>" + description + "</description>\n";
		buf += "        <type>" + type + "</type>\n";
//		buf += "        <physical-name>" + physicalName + "</physical-name>\n";
//		buf += "        <cube-location>" + location + "</cube-location>\n";
		buf += "        <fact-dataobject-id>" + factDataObjectId + "</fact-dataobject-id>\n";
		buf += "        <datasource-id>";
		if (dataSource != null){
			buf +=dataSource.getId();
		}
		buf += "</datasource-id>\n";
		
		for(OLAPDimensionGroup g : dimGroups.values()){
			buf += "        <dimension-group-id>" + g.getId() + "</dimension-group-id>\n";
		}
		
		for(OLAPMeasureGroup m : mesGroups.values()){
			buf += "        <measure-group-id>" + m.getId() + "</measure-group-id>\n";
		}
		
		for(OLAPDimension d : dims.getList()){
			buf += "        <dimension-item-id>" + d.getId() + "</dimension-item-id>\n";
		}
		
		for(OLAPMeasure m : mes.getList()){
			buf += "        <measure-item-id>" + m.getId() + "</measure-item-id>\n";
		}
		
		for(SecurityGroup s : secuList.getList()){
			buf += "        <security-group-item-id>" + s.getId() + "</security-group-item-id>\n";
		}
		
		for(AggregateTable agg : aggTables){
			buf += "        <aggregate-item-id>" + agg.getId() + "</aggregate-item-id>\n";
		}
		
		for(AggPattern p : aggPattern){
			p.getFAXML();
		}
			
		for(NamedSet ns : namedSet){
			buf += "        <namedSet-id>" + ns.getId()+ "</namedSet-id>\n"; 
		}
		
		for(CubeView v : cubeViews){
			buf += v.getXml();
		}
		
		
		for(Drill d : drills){
			buf += d.getXml();
		}
		
		
		for(String s : openedDimensions){
			buf +="        <startupDimension>" + s + "</startupDimension>\n";
		}
		
		if (defaultMeasure != null && !"".equals(defaultMeasure)){
			buf +="        <defaultMeasure>" + defaultMeasure + "</defaultMeasure>\n";
		}
		buf += "    </Cube-item>\n";
		
		return buf;
	}

	public boolean isInDimUsage(OLAPDimension d){
		
		for(DimUsage u:dimUsages){
			if (u.getSource().equals(d.getName())){
				return true;
			}
		}
		return false;
	}
	
	public boolean isInDimId(OLAPDimension d){
		
		for(String s :dimIdList){
			if (s.equals(d.getName())){
				return true;
			}
		}
		return false;
	}
	
	public List<String> getDimIdList() {
		return dimIdList;
	}
	
	public List<String> getSecuIdList() {
		return secuIdList;
	}

	public List<OLAPDimension> getDims() {
		return dims.getList();
	}

	public List<OLAPMeasure> getMes() {
		return mes.getList();
	}

	public List<String> getMesIdList() {
		return mesIdList;
	}

	public String getFactDataObjectId() {
		return factDataObjectId;
	}
	
	public void setFactDataObjectId(String id){
		factDataObjectId = id;
//		update(this, id);
	}
	
	public void addDimGroup(OLAPDimensionGroup dg){
		dimGroups.put(dg.getId(), dg);
//		update(this, dg);
	}
	
	public void addDimGroup(String gId){
		dimGroups.put(gId, null);
//		update(this, dimGroups);
	}
	
	public void removeDimGroup(OLAPDimensionGroup dg){
		dimGroups.remove(dg.getId());
//		update(this, dg);
	}

	public void addMesGroup(OLAPMeasureGroup dg){
		mesGroups.put(dg.getId(), dg);
//		update(this, dg);
	}
	
	public void addMesGroup(String gId){
		mesGroups.put(gId, null);
//		update(this, mesGroups);
	}
	
	public void removeMesGroup(OLAPMeasureGroup dg){
		mesGroups.remove(dg.getId());
//		update(this, dg);
	}

	public Set<String> getDimGroupId(){
		return dimGroups.keySet();
	}
	
	public Set<String> getMesGroupId(){
		return mesGroups.keySet();
	}
	
	public Collection<OLAPDimensionGroup> getDimGroups(){
		return dimGroups.values();
	}
	
	public Collection<OLAPMeasureGroup> getMesGroups(){
		return mesGroups.values();
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public OLAPSchema getParent() {
		return parent;
	}

	public void setParent(OLAPSchema parent) {
		this.parent = parent;
	}
	
	public DataSource getDataSource(){
		if (type.equals("Rolap") && factDataObject != null)
			return factDataObject.getDataSource();
		if (type.equals("Molap"))
			return dataSource;
		
		return null;
	}
	
	
	
	
//	/**
//	 * build te requested molap DataSource and set it in the cube
//	 * @return
//	 * @throws FileNotFoundException
//	 * @throws Exception
//	 */
//	public DataSource buildMOLAPDataSource() throws FileNotFoundException, Exception{
//		
//		HashMap<DataObject, List<DataObjectItem> > sources = findItems();
//
//		HashMap<DataObject, DataObject> map = new HashMap<DataObject, DataObject>();
//		
//		DataSource dataSource = new DataSource();
//		dataSource.setDSName("DataSource for MOLAP cube :" + getName());
//		
//		//DataSourceConnection
//		//TODO to be set with the h2 params
//		DataSourceConnection dsConn = new DataSourceConnection();
//		dataSource.setDriver(dsConn);
//		
//		dsConn.setDriver("org.h2.Driver");
//		
//		dsConn.setName("MOLAP");
//		dsConn.setPass("");
//		dsConn.setUser("sa");
//		dsConn.setType("Molap");
//		dsConn.setUrl("jdbc:h2:" + location + "\\" + physicalName +";IGNORECASE=TRUE" );
//		
//		for(DataObject t : sources.keySet()){
//			DataObject n = new DataObject();
//			n.setDataObjectType(t.getDataObjectType());
//			n.setDesc(t.getDesc());
//			n.setName(t.getName());
//			n.setSelectStatement("SELECT * FROM " + n.getName());
//			n.setServer(t.getServer());
//			n.setTransName(t.getTransName());
//			dataSource.addDataObject(n);
//			map.put(n,t);
//			
//			for(DataObjectItem i : sources.get(t)){
//				DataObjectItem it = new DataObjectItem();
//				it.setClasse(i.getClasse());
//				it.setDesc(i.getDesc());
//				it.setName(i.getName());
//				it.setOrigin(i.getOrigin());
//				it.setSqlType(i.getSqlType());
//				it.setType(i.getType());
//				
//				n.addDataObjectItem(it);
//			}
//		}
//		
//		//set the DataSource
//		this.dataSource = dataSource;
//		
//		createDataBase();
//
//		//generate the filoe mapping between real database and the generated one
//		PrintWriter pw = new PrintWriter(location + "\\mapping.xml");
//		StringBuffer buf = new StringBuffer();
//		buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\n");
//		buf.append("<mapping>\n");
//		for(DataObject o : map.keySet()){
//			buf.append("    <mapping-item>\n");
//			buf.append("        <dataobject-id>" + map.get(o).getId() + "</dataobject-id>\n");
//			buf.append("        <molap-dataobject-id>" + o.getId() + "</molap-dataobject-id>\n");
//			buf.append("    </mapping-item>\n");
//		}
//		buf.append("</mapping>\n");
//		
//		pw.write(buf.toString());
//		System.out.println(buf.toString());
//		pw.close();
//		
//		setDataSource(dataSource);
//		return dataSource;
//	}
	
	
	private HashMap<DataObject, List<DataObjectItem> > findItems(){
		HashMap<DataObject, List<DataObjectItem> > sources = new HashMap<DataObject, List<DataObjectItem> >();
		
		//facttable
		List<DataObjectItem> l = new ArrayList<DataObjectItem>();
		for(OLAPMeasure i: getMes()){
			if (i.getOrigin() != null)
				l.add(i.getOrigin());			}
		
		sources.put(getFactDataObject(), l);
		
		//dimensions
		List<DataObject> objects = new ArrayList<DataObject>();
		objects.add(getFactDataObject());

		for(OLAPDimension d : getDims()){
			for(OLAPHierarchy h : d.getHierarchies()){
				for(OLAPLevel lvl : h.getLevels()){
					
					if (!objects.contains(lvl.getItem().getParent()))
						objects.add(lvl.getItem().getParent());
					
					if (!l.contains(lvl.getItem())){
						l.add(lvl.getItem());
					}
				}
			}
		}
		//relations
		ActionGetPath a = new ActionGetPath(getParent().getParent(), objects.toArray(new DataObject[objects.size()]));
		a.run();
		Path path = a.getPath();
		
		for (DataObjectItem i : path.getUsedItems()){
			if (!l.contains(i))
				l.add(i);
			
			if (!objects.contains(i.getParent()))
				objects.add(i.getParent());
		}
		
		//build the map
		for(DataObject o : objects){
			sources.put(o, new ArrayList<DataObjectItem>());
		}
		
		for(DataObjectItem i : l){
			sources.get(i.getParent()).add(i);
			listIndex.add(i);
		}
			
		return sources;
	}

	
	public void setDataSource(DataSource molapDataSource) {
		this.dataSource = molapDataSource;
		if (dataSource == null)
			dataSourceId = "";
		else
			dataSourceId = molapDataSource.getId();
	}

//	/**
//	 * create the molap database
//	 * @throws FileNotFoundException
//	 * @throws Exception
//	 */
//	private void createDataBase() throws FileNotFoundException, Exception{
//		DataSourceConnection con = dataSource.getDriver();
//		
//		StringBuffer buf = new StringBuffer();
//		
//		for(DataObject table : dataSource.getDataObjects()){
//			
//			buf.append("DROP TABLE IF EXISTS " + table.getPhysicalName() + ";\n");
//			buf.append("CREATE TABLE " + table.getPhysicalName() + "");
//			
//			boolean first = true;
//			
//			for(DataObjectItem col : table.getColumns()){
//				if (col.getType().equals("physical")){
//					if (first){
//						first = ! first;
//						buf.append(" (" + col.getOrigin()+"");
//						
//
//					}
//					else{
//						buf.append(", " + col.getOrigin() + "");
//						
//					}
//					if (java.lang.Number.class.isAssignableFrom(Class.forName(col.getClasse())))
//						buf.append(" " + "NUMERIC");
//					
//					else if (java.util.Date.class.isAssignableFrom(Class.forName(col.getClasse()))){
//						buf.append(" " + "TIMESTAMP");
//					}
//					else {
//						buf.append(" " + "VARCHAR");
//					}
//					
//				}
//								
//			}
//			buf.append(");\n\n");
//		}
//		
//		//create index
////		for (DataObjectItem it : listIndex){
////			System.out.println(it.getParent().getPhysicalName());
////			System.out.println(it.getParent().getPhysicalName().substring(it.getParent().getPhysicalName().lastIndexOf('.')));
////			buf.append("DROP INDEX IF EXISTS " + "IDX_" + it.getParent().getPhysicalName().substring(it.getParent().getPhysicalName().lastIndexOf('.') + 1) + "_" +it.getOrigin() + ";\n" );
////			buf.append("CREATE INDEX IDX_" + it.getParent().getPhysicalName() + "_" + it.getOrigin() + " ON " + it.getParent().getPhysicalName() + "(" + it.getOrigin() + ");\n");
////		}
//	
//		//fill DataBase
//		con.connectAll();
//		Connection sock = con.getConnection().getConnection();
//		Statement stmt = sock.createStatement();
//		System.out.println(buf.toString());
//		stmt.execute(buf.toString());
//		
//		stmt.close();
//		sock.close();
//		
//		
//	}

//	public void refresh() throws FileNotFoundException, Exception{
//		HashMap<DataObject, DataObject> cor = new HashMap<DataObject, DataObject>();
//		MOLAPMappingContext context = new DigesterMolapMapping(location + "\\mapping.xml").getContext();
//		for(DataObject o : dataSource.getDataObjects()){
//			cor.put(o, getParent().getParent().findDataObject(context.getOriginalId(o.getId())));
//		}
//		
//		buildCSVFiles(cor);
//		fillDatabase();
//	}
	
	
	
//	/**
//	 * fill the database with the datas contained in the original database
//	 * the mapping between DataSources DataObjects come from mapping.xml 
//	 * which is generated during the database creation
//	 * @throws FileNotFoundException
//	 * @throws Exception
//	 */
//	public void fillDatabase() throws FileNotFoundException, Exception{
//		if (dataSource != null){
//			MOLAPMappingContext context = new DigesterMolapMapping(location + "\\mapping.xml").getContext();
//			
//			HashMap<String, String> links = context.getMap();
//			
//			HashMap<DataObject, DataObject> map = new HashMap<DataObject, DataObject>();
//			for(String s : links.keySet()){
//				map.put(dataSource.findDataObject(s), getParent().getParent().findDataObject(links.get(s)));
//			}
//			
//			
//			
//			dataSource.getDriver().connectAll();
//			Connection c = dataSource.getDriver().getConnection().getConnection();
//			Statement st = c.createStatement();
//			
//			for(DataObject o : map.keySet()){
//				StringBuffer sql = new StringBuffer();
//				st.executeUpdate("TRUNCATE TABLE \"" + o.getPhysicalName() + "\"");
//				
//				sql.append("SELECT ");
//				boolean first = true;
//				for(DataObjectItem i : map.get(o).getColumns()){
//					if (i.getType().equals("physical")){
//						if (first){
//							first = ! first;
//							sql.append("\"" + i.getOrigin() + "\"");
//						}
//						else{
//							sql.append(", \"" + i.getOrigin() + "\"");
//						}
//					}
//					
//				}
//				sql.append(" FROM \"" + map.get(o).getPhysicalName() + "\"");
//				System.out.println(sql.toString());
//				map.get(o).getDataSource().getDriver().connectAll();
//				
//				Connection sock = map.get(o).getDataSource().getDriver().getConnection().getConnection();
//				Statement stmt = sock.createStatement();
//				ResultSet rs = stmt.executeQuery(sql.toString());
//				
//				
//				
//				while(rs.next()){
//					first = true;
//					StringBuffer inserts = new StringBuffer();
//					for(DataObjectItem i : o.getColumns()){
//						if (first){
//							first = ! first;
//							inserts.append("('" + rs.getString(i.getOrigin()) + "'");
//						}
//						else{
//							inserts.append(",'" + rs.getString(i.getOrigin()) + "'");
//						}
//					}
//					inserts.append(")");
//					String buf ="";
//					buf += "INSERT INTO \"" + o.getPhysicalName() + "\"";
//					buf += " VALUES " + inserts.toString();
//					System.out.println(buf);
//
//					System.out.println("execute result :" + st.executeUpdate(buf));
//
//				}
//				rs.close();
//				stmt.close();
//				sock.close();
//			}
//			st.close();
//			c.close();
//			
//		}
//		
//		
//	}

	public String getDataSourceId() {
		return dataSourceId;
	}

	public void setDataSourceId(String dataSourceId) {
		this.dataSourceId = dataSourceId;
	}
//
//	public void zipCube() throws IOException{
//		File file = new File(location + "\\" + physicalName);
//		FileWriter fw = new FileWriter(file);
//		fw.write(getParent().getParent().getFAXML());
//		fw.close();
//
//		Zipper zipper = new Zipper();
//		zipper.zip(location, physicalName);
//	}
	
	
	


//	public List<SecurityGroup> getSecurityGroups() {
//		return secuList.getList();
//	}

//	public void addSecurityGroup(SecurityGroup d) {
//		secuList.add(d);
//		d.addCube(this);
//	}

//	public void removeSecurityGroup(SecurityGroup d) {
//		secuList.remove(d);
//		d.removeCube(this);
//	}
	
	public void addAggTable(AggregateTable agg){
		if (!aggTables.contains(agg)){
			aggTables.add(agg);
		}
	}
	
	public void removeAggTable(AggregateTable agg){
		aggTables.remove(agg);
	}
	public List<AggregateTable> getAggTables(){
		return aggTables;
	}
	
	public void addAggtableId(String id){
		aggTablesId.add(id);
	}
	
	public List<String> getAggTableId(){
		return aggTablesId;
	}
	
	
	public void addMondrianAgg(MondrianAgg agg){
		mondrianAgg.add(agg);
	}
	
	public List<MondrianAgg> getMondrianAgg(){
		return mondrianAgg;
	}

	public void addNamedSetId(String id){
		namedSetId.add(id);
	}
	
	public void addNamedSet(NamedSet ns){
		if (ns != null)
			namedSet.add(ns);
	}

	public List<NamedSet> getNamedSets() {
		return namedSet;
	}

	public List<String> getNamedSetsId() {
		return namedSetId;
	}

	public boolean isCache() {
		return cache;
	}

	public void setCache(boolean cache) {
		this.cache = cache;
	}

	public String getDefaultMeasure() {
		return defaultMeasure;
	}

	public void setDefaultMeasure(String defaultMeasure) {
		this.defaultMeasure = defaultMeasure;
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}
	public void setCache(String cache) {
		this.cache = Boolean.parseBoolean(cache);
	}
	public void setEnable(String enable) {
		this.enable = Boolean.parseBoolean(enable);
	}
	
	public List<AggPattern> getAggPattern(){
		return aggPattern;
	}
	
	public void addAggPattern(AggPattern agg){
		aggPattern.add(agg);
	}

	public OLAPHierarchy findHierarchyNamed(String hiera) {
		for(OLAPDimension d : dims.getList()){
			if (d.getName().equals(hiera) && d.getHierarchies().size() > 0)
				return d.getHierarchies().get(0);
			
			for(OLAPHierarchy h : d.getHierarchies()){
				if (h.getName().equals(hiera))
					return h;
			}
		}
		return null;
	}

	public IConnection getConnection() {
		if (getDataSource() != null)
			return getDataSource().getDriver();
		else
			return null;
	}
	
	
///**
// * build the modlap H2 files in the path folder
// * @param path
// * @return
// * @throws FileNotFoundException
// * @throws Exception
// */	
//public DataSource buildMOLAPDataSource(String path) throws FileNotFoundException, Exception{
//		
//		HashMap<DataObject, List<DataObjectItem> > sources = findItems();
//
//		HashMap<DataObject, DataObject> map = new HashMap<DataObject, DataObject>();
//		
//		DataSource dataSource = new DataSource();
//		dataSource.setDSName("DataSource for MOLAP cube :" + getName());
//		
//		//DataSourceConnection
//		//TODO to be set with the h2 params
//		DataSourceConnection dsConn = new DataSourceConnection();
//		dataSource.setDriver(dsConn);
//		
//		dsConn.setDriver("org.h2.Driver");
//		
//		dsConn.setName("MOLAP");
//		dsConn.setPass("");
//		dsConn.setUser("sa");
//		dsConn.setType("Molap");
//		dsConn.setUrl("jdbc:h2:" + path + "\\" + physicalName +";IGNORECASE=TRUE" );
//		
//		for(DataObject t : sources.keySet()){
//			DataObject n = new DataObject();
//			n.setDataObjectType(t.getDataObjectType());
//			n.setDesc(t.getDesc());
//			n.setName(t.getName());
//			n.setSelectStatement("SELECT * FROM " + n.getName());
//			n.setServer(t.getServer());
//			n.setTransName(t.getTransName());
//			dataSource.addDataObject(n);
//			map.put(n,t);
//			
//			for(DataObjectItem i : sources.get(t)){
//				DataObjectItem it = new DataObjectItem();
//				it.setClasse(i.getClasse());
//				it.setDesc(i.getDesc());
//				it.setName(i.getName());
//				it.setOrigin(i.getOrigin());
//				it.setSqlType(i.getSqlType());
//				it.setType(i.getType());
//				
//				n.addDataObjectItem(it);
//			}
//		}
//		
//		//set the DataSource
//		this.dataSource = dataSource;
//		
//		createDataBase();
//
//		//generate the filoe mapping between real database and the generated one
//		PrintWriter pw = new PrintWriter(path + "\\mapping.xml");
//		StringBuffer buf = new StringBuffer();
//		buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\n");
//		buf.append("<mapping>\n");
//		for(DataObject o : map.keySet()){
//			buf.append("    <mapping-item>\n");
//			buf.append("        <dataobject-id>" + map.get(o).getId() + "</dataobject-id>\n");
//			buf.append("        <molap-dataobject-id>" + o.getId() + "</molap-dataobject-id>\n");
//			buf.append("    </mapping-item>\n");
//		}
//		buf.append("</mapping>\n");
//		
//		pw.write(buf.toString());
//		System.out.println(buf.toString());
//		pw.close();
//		
//		setDataSource(dataSource);
//		return dataSource;
//	}

	public List<ICubeView> getCubeViews() {
		return (List)cubeViews;
	}
	
	public void addCubeView(CubeView view){
		cubeViews.add(view);
	}
	
	public void removeCubeView(CubeView view){
		cubeViews.remove(view);
	}

	public String getFAProvider() {
		return "mondrian";
	}

	public String getFAType() {
		if(parent.getParent().getDataSources().get(0) instanceof DatasourceOda) {
			return "utdolap";
		}
		return "mondrian";
	}

	public void clearDims() {
		dims.clear();
	}
} 
