package org.fasd.olap;

import java.io.FileNotFoundException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.fasd.datasource.DataObject;
import org.fasd.datasource.DataObjectItem;
import org.fasd.datasource.DataSourceConnection;
import org.fasd.utils.trees.TreeDim;
import org.fasd.utils.trees.TreeHierarchy;
import org.fasd.utils.trees.TreeMember;
import org.fasd.utils.trees.TreeParent;

import xmldesigner.internal.DimensionTree;
import xmldesigner.parse.XMLParser;
import xmldesigner.parse.item.DataXML;
import xmldesigner.xpath.Xpath;
import bpm.connection.manager.connection.ConnectionManager;
import bpm.connection.manager.connection.jdbc.VanillaJdbcConnection;
import bpm.connection.manager.connection.jdbc.VanillaPreparedStatement;


public class OLAPDimension extends OLAPElement {
	private static int counter = 0;
	
	public static final String[] LOADING_METHOD= new String[]{"cube_startup", "server_startup", "cube_open", "on_demand"};
	public static int CUBE_STARTUP = 0;
	public static int SERVER_STARTUP = 1;
	public static int CUBE_OPEN = 2;
	public static int ON_DEMAND = 3;
	
	
	
	
	
	public static void resetCounter(){
		counter =0;
	}
	
	private ArrayList<OLAPHierarchy> hieras = new ArrayList<OLAPHierarchy>();
	
	private String desc = "";
	private String groupId = "";
	private boolean date = false;
	private String loadMethod = "cube_startup";
//	private boolean hasAll = true;
//	private String allMemberName = "";
	private String order = "";
//	private String widget = "";
	private String properties = "";
	private String caption = "";
	
	private String foreignKey = "";
	
	private boolean geolocalisable = false;
	
	private OLAPDimensionGroup group;
	
	private OLAPSchema parent;
	
	private boolean isOneColumnDate;
	
	public OLAPSchema getParent() {
		return parent;
	}

	public void setParent(OLAPSchema parent) {
		this.parent = parent;
	}

	public OLAPDimension() {
		super("");
		counter++;
		id = "g" + String.valueOf(counter);
	}
	
	public OLAPDimension(String name){
		super(name);
		counter++;
		id = "g" + String.valueOf(counter);
	}

	/**
	 * @return the geolocalisable
	 */
	public boolean isGeolocalisable() {
		return geolocalisable;
	}

	/**
	 * @param geolocalisable the geolocalisable to set
	 */
	public void setGeolocalisable(boolean geolocalisable) {
		this.geolocalisable = geolocalisable;
	}
	
	public void setGeolocalisable(String geolocalisable) {
		try{
			this.geolocalisable = Boolean.parseBoolean(geolocalisable);
		}catch(Exception ex){
			
		}
	}

	public void addHierarchy(OLAPHierarchy hiera) {
		hieras.add(hiera);
		hiera.setParent(this);
	}
	
	public ArrayList<OLAPHierarchy> getHierarchies() {
		return hieras;
	}
	
	public void removeHierarchy(String name) {
		for (int i=0; i < hieras.size(); i++) {
			if (hieras.get(i).getName().equals(name)) {
				hieras.remove(i);
				break;
			}
		}
	}
	
	public void setId(String id) {
		this.id = id;
		int i = Integer.parseInt(id.substring(1));
		if (i > counter){
			counter = i + 1;
		}
	}
	
	public void removeHierarchy(OLAPHierarchy hiera){
		hieras.remove(hiera);
	}
	
	public String getXML(DataObject factObject) {
		String buf = "";//"    <Dimension name=\"" + getName() +"\">\n";
		buf +="        <Dimension name=\""+ getName() + "\"";
//		if (hieras.get(0) != null && hieras.get(0).getForeignKey(factObject) != null)
//			buf += " foreignKey=\"" + hieras.get(0).getForeignKey(factObject).getOrigin() + "\"";
//		else if (!foreignKey.trim().equals(""))
//			buf += " foreignKey=\"" + foreignKey + "\"";
		
		if (!caption.trim().equals("")){
			buf += " caption=\"" + caption + "\"";
		}
		
		buf += ">\n";
		
		for (int i=0; i < hieras.size(); i++) {
			buf += hieras.get(i).getXML(factObject);
		}
		buf += "        </Dimension>\n";
		return buf;
	}
	
	public String getUsageXML(DataObject factObject){
		String buf = "";
		//if (!isDegenerated(factObject)){
			buf +="        <DimensionUsage name=\""+ getName() + "\" source=\"" + getName() + "\"";
			if (hieras.get(0) != null && hieras.get(0).getForeignKey(factObject) != null)
				buf += " foreignKey=\"" + hieras.get(0).getForeignKey(factObject).getOrigin() + "\"";
			else if (!foreignKey.trim().equals(""))
				buf += " foreignKey=\"" + foreignKey + "\"";
			else if (isDegenerated(factObject)){
				buf += " foreignKey=\"" + hieras.get(0).getLevels().get(0).getItem().getOrigin() + "\"";
			}
			
			if (!caption.trim().equals(""))
				buf += " caption=\"" + caption + "\"";
			buf +="/>\n";
//		}
//		else{
//			buf += "        <DimensionUsage name=\""+ getName() + "\" source=\"" + getName() + "\"";
//			buf += " foreignKey=\"" + hieras.get(0).getForeignKey(factObject).getOrigin() + "\"";
//		}
		
		return buf;
	}
	
	public boolean isDegenerated(DataObject factObject) {
		for(OLAPHierarchy h : hieras){
			for(OLAPLevel l : h.getLevels()){
				if (l.getItem() != null && l.getItem().getParent() != factObject)
					return false;
			}
		}
		return true;
	}

	public String getFAXML() {
		String buf = "    <Dimension-item>\n";
		buf += "        <id>" + super.getId() + "</id>\n";
		buf += "        <name>" + getName() + "</name>\n";
		buf += "        <caption>" + caption + "</caption>\n";
		buf += "        <description>" + desc + "</description>\n";
		buf += "        <dimension-group-id>" + groupId + "</dimension-group-id>\n";
		buf += "        <IsDate>" + date + "</IsDate>\n";
		buf += "        <LoadMethod>" + loadMethod + "</LoadMethod>\n";
//		buf += "        <hasAll>" + hasAll + "</hasAll>\n";
//		buf += "        <allMemberName>" + allMemberName + "</allMemberName>\n";
		buf += "        <order>" + order + "</order>\n";
//		buf += "        <Widget>" + widget + "</Widget>\n";
		buf += "        <properties>" + properties + "</properties>\n";
		
		buf += "        <geolicalisable>" + isGeolocalisable() + "</geolicalisable>\n";
		buf += "		<isOneColumnDate>" + isOneColumnDate + "</isOneColumnDate>\n";
		for (int i=0; i < hieras.size(); i++) {
			buf += hieras.get(i).getFAXML();
		}
		
		buf += "    </Dimension-item>\n";
		
		return buf;
	}

//	public String getAllMemberName() {
//		return allMemberName;
//	}
//
//	public void setAllMemberName(String allMemberName) {
//		this.allMemberName = allMemberName;
//	}

	public boolean isDate() {
		return date;
	}

	public void setDate(boolean date) {
		this.date = date;
	}
	public void setDate(String isDate){
		if (isDate.contains("Time")){
			date = true;
		}
		else if (isDate.contains("Standard")){
			date = false;
		}
		else{
			date = Boolean.valueOf(isDate);
		}
		
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

//	public boolean isHasAll() {
//		return hasAll;
//	}
//
//	public void setHasAll(String s){
//		hasAll = Boolean.valueOf(s);
//		
//	}
//	public void setHasAll(boolean hasAll) {
//		this.hasAll = hasAll;
//	}

	public String getLoadMethod() {
		return loadMethod;
	}

	public void setLoadMethod(String loadMethod) {
		this.loadMethod = loadMethod;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public String getProperties() {
		return properties;
	}

	public void setProperties(String properties) {
		this.properties = properties;
	}

//	public String getWidget() {
//		return widget;
//	}
//
//	public void setWidget(String widget) {
//		this.widget = widget;
//	}

	public String getGroupId() {
		return groupId;
	}
	
	public void setGroupId(String groupId){
		this.groupId = groupId;
	}

	public void removeFromGroup() {
		groupId = "";
	}
	
	public void setGroup(OLAPDimensionGroup g){
		if (group != null){
			group.removeDim(this);
		}
		if (g != null){
			setGroupId(g.getId());
			group = g;
		}
		else{
			setGroupId("");
			group = null;
		}
		if (group != null)
			group.addDimension(this);
	}
	
	public OLAPDimensionGroup getGroup(){
		return group;
	}

	public String getForeignKey() {
		return foreignKey;
	}

	public void setForeignKey(String foreignKey) {
		this.foreignKey = foreignKey;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public TreeParent createModel() {
		TreeParent root = new TreeParent(""); //$NON-NLS-1$
		
		TreeDim td = new TreeDim(this);
		
		for(OLAPHierarchy h : getHierarchies()){
			if (h.isSnowFlakes()){
				return null;
			}
			TreeHierarchy th = new TreeHierarchy(h);
			
			if (h.getLevels().size()>0){
				OLAPLevel l0 = h.getLevels().get(0);
				List<String> list = new ArrayList<String>();
				
				try {
					if (l0.getItem().getParent().getDataSource().getDriver().getType().equals("XML")){ //$NON-NLS-1$
						DataSourceConnection sock = l0.getItem().getParent().getDataSource().getDriver();
						DataObjectItem item = l0.getItem();
						
						String url = "file:///" + sock.getTransUrl(); //$NON-NLS-1$
						
						XMLParser parser = new XMLParser(url);
						parser.parser();
						DataXML dtd = parser.getDataXML();
						Xpath xpath = new Xpath(sock.getTransUrl(), dtd.getRoot().getElement(0).getName());
									
						DimensionTree model = new DimensionTree(dtd);
						xmldesigner.internal.TreeParent rr = model.createModel();
						xpath.setListHiera(createHiera(rr));
						xpath.addCol(item.getOrigin());

						String distinctQuery = "for $i in distinct-values(doc('" + sock.getTransUrl().replace("\\", "/") + "')//" + item.getOrigin() + ")\n"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
						distinctQuery += "return\n"; //$NON-NLS-1$
						distinctQuery += "<" + dtd.getRoot().getElement(0).getName() + ">\n"; //$NON-NLS-1$ //$NON-NLS-2$
						distinctQuery += "<" + item.getOrigin() +">\n"; //$NON-NLS-1$ //$NON-NLS-2$
						distinctQuery += "{$i}\n"; //$NON-NLS-1$
						distinctQuery += "</" + item.getOrigin() +">\n"; //$NON-NLS-1$ //$NON-NLS-2$
						distinctQuery += "</" + dtd.getRoot().getElement(0).getName() + ">\n"; //$NON-NLS-1$ //$NON-NLS-2$
						try {
							xpath.executeXquery(distinctQuery);
							xpath.modifieSortie();

							XMLParser pars = new XMLParser("Temp/sortie.xml"); //$NON-NLS-1$
							pars.parser();
							DataXML dtd2 = pars.getDataXML();
							list = xpath.listXquery(dtd2.getRoot());
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}

					}
					else{
						String request = ""; //$NON-NLS-1$
						if (! l0.isClosureNeeded()){
							request = "SELECT DISTINCT " + l0.getItem().getOrigin() + " FROM " + l0.getItem().getParent().getPhysicalName();  //$NON-NLS-1$ //$NON-NLS-2$
						}
						else{
							request = "SELECT DISTINCT " + l0.getItem().getOrigin() + " FROM " + l0.getItem().getParent().getPhysicalName(); //$NON-NLS-1$ //$NON-NLS-2$
							request += " WHERE " + l0.getClosureParentCol().getName() +"=" + l0.getNullParentValue(); //$NON-NLS-1$ //$NON-NLS-2$
						}
						
						
						l0.getItem().getParent().getDataSource().getDriver().connectAll();
						
						VanillaJdbcConnection con = l0.getItem().getParent().getDataSource().getDriver().getConnection().getConnection();
						VanillaPreparedStatement stmt = con.createStatement();
						
						
						ResultSet rs = stmt.executeQuery(request);
						
						while(rs.next()){
							if( rs.getString(1) != null)
								list.add(rs.getString(1));
						}
						rs.close();
						stmt.close();
						ConnectionManager.getInstance().returnJdbcConnection(con);
					}
				} catch (FileNotFoundException e) {
					
					e.printStackTrace();
				} catch (Exception e) {
					
					e.printStackTrace();
				}
				for(String s : list){
					TreeMember tm = new TreeMember(s, l0);
					th.addChild(tm);
					tm.createChilds();
				}

			}

			td.addChild(th);
		}
		root.addChild(td);
		return root;
	}
	/**
	 * Creation a list hierarchic of tags of file xml
	 * @param t
	 * @return
	 */
	private ArrayList<String> createHiera(xmldesigner.internal.TreeParent t)
	{
		ArrayList<String> list = new ArrayList<String>();
		list.add(t.getName());
		for(int i=0;i<t.getChildren().length;i++)
		{
			add(list,createHiera((xmldesigner.internal.TreeParent)t.getChildren(i)));
		}
		return list;
	}
	
	private List<String> add(List<String> list1,List<String> list2)
	{
		for(int i=0;i<list2.size();i++)
		{
			list1.add(list2.get(i));
		}
		return list1;
	}

	public void setOneColumnDate(boolean isOneColumnDate) {
		this.isOneColumnDate = isOneColumnDate;
	}

	public boolean isOneColumnDate() {
		return isOneColumnDate;
	}
	
	public void setOneColumnDate(String isOneColumnDate) {
		this.isOneColumnDate = Boolean.parseBoolean(isOneColumnDate);
	}
}
