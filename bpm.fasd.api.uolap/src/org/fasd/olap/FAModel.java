package org.fasd.olap;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import org.fasd.datasource.DataObject;
import org.fasd.datasource.DataObjectItem;
import org.fasd.datasource.DataSource;
import org.fasd.datasource.list.ListDataSource;
import org.fasd.datasource.list.ListRelation;
import org.fasd.security.Security;
import org.fasd.security.SecurityGroup;
import org.fasd.xmla.ISchema;
import org.fasd.xmla.XMLASchema;





public class FAModel extends Observable{
	private static final String SCHEMA_VERSION = "4.0";
	private ISchema schema ;//= new OLAPSchema();
	private String uuid;
	
	private DocumentProperties documentProperties = new DocumentProperties();
	//private ArrayList<DataSource> drivers = new ArrayList<DataSource>();
	private ListDataSource drivers = new ListDataSource();
	
	//private List<OLAPRelation> relations = new ArrayList<OLAPRelation>();
	private ListRelation relations = new ListRelation();
	
	private Security security = new Security();
	
	private String name;
	
	private int id;
	
	private boolean isSynchronized = false;

	private List<String> dependancies = new ArrayList<String>();
	
	private PreloadConfig preloadConfig;
	
	public void addDependancies(String directoryItemId){
		dependancies.add(directoryItemId);
	}
	
//XXX	
//	/**
//	 * @return the uuid
//	 */
//	public String getUuid() {
//		return uuid;
//	}
//
//
//	/**
//	 * @param uuid the uuid to set
//	 */
//	public void setUuid(String uuid) {
//		this.uuid = uuid;
//	}


	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public void setId(String id) {
		this.id = Integer.parseInt(id);
	}

	public boolean isSynchronized() {
		return isSynchronized;
	}

	public void setSynchronized(boolean isSynchronized) {
		this.isSynchronized = isSynchronized;
	}
	
	public void setSynchronized(String isSynchronized) {
		this.isSynchronized = Boolean.parseBoolean(isSynchronized);
	}

	public FAModel(OLAPSchema schema, String name) throws Exception {
		this.schema = schema;
		this.name = name;
		schema.setParent(this);
	}
	
	public FAModel() throws Exception {
	}

	public List<DataSource> getDataSources() {
		return drivers.getList();
	}
	
	public void addDataSource(DataSource d) {
		drivers.add(d);
//		update(this, d);
	}

	//XXX not used??
	public void removeSQLDriver(String name) {
//		update(this, name);
	}

	public void removeSQLDriver(DataSource d) {
		System.out.println("going to remove...");
		drivers.remove(d);
//		update(this, d);
	}
	
	public OLAPSchema getOLAPSchema() {
		if (schema instanceof OLAPSchema)
			return (OLAPSchema)schema;
		return null;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setOLAPSchema(OLAPSchema sch) {
		
		this.schema = sch;
		((OLAPSchema)schema).setParent(this);
//		update(this, sch);
	}
	
	public DocumentProperties getDocumentProperties(){
		return documentProperties;
	}
	
	public void setDocumentProperties(DocumentProperties docProp){
		this.documentProperties=docProp;
//		update(this, docProp);
	}
	

	
	public String getFAXML() {
		String tmp = //"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\n" +
				     "<freeanalysis-schema>\n";
		tmp += "    <version>" + SCHEMA_VERSION + "</version>\n";
		tmp += "    <id>" + id + "</id>\n";
		
		//XXX
//		if (getUuid() != null){
//			tmp += "    <uuid>" + getUuid() + "</uuid>\n";	
//		}
		
		tmp += "    <isSynchronized>" + isSynchronized + "</isSynchronized>\n";
		
		tmp += documentProperties.getFAXML();
		
		
		for(String s : dependancies){
			tmp += "    <dependsOf>" + s + "</dependsOf>";
		}
		
		tmp += "    <datasources>\n";
		
		for (int i=0; i < drivers.getList().size(); i++) {
			//XXX TOFIX
			tmp += drivers.getList().get(i).getFAXML();
		}
		
		tmp += "    </datasources>\n";
		
		
		tmp += "    <object-relation>\n";
		for(OLAPRelation r : relations.getList()){
			tmp += r.getFAXML();
		}
		tmp += "    </object-relation>\n";
		
		if (security != null)
			tmp += security.getFAXML();
		
		if(preloadConfig != null) {
			tmp += preloadConfig.getXml();
		}
		
		tmp += schema.getFAXML();
		
		
		
		tmp += "</freeanalysis-schema>\n\n";
		
		return tmp;
	}

	@Override
	public synchronized void setChanged() {
		super.setChanged();
		notifyObservers();
	}

	/**
	 * search the data object item with the given id
	 * @param id
	 * @return
	 */
	public DataObjectItem findDataObjectItem(String id) {
		for(DataSource ds : drivers.getList()){
			for(DataObject o : ds.getDataObjects()){
				for(DataObjectItem i : o.getColumns()){
					if (i.getId().equals(id)){
						return i;
					}
				}
			}
		}
		return null;
	}

	/**
	 * search the data object with the given id
	 * @param id
	 * @return
	 */
	public DataObject findDataObject(String id) {
		for(DataSource d : drivers.getList()){
			for(DataObject o : d.getDataObjects()){
				if (o.getId().equals(id))
					return o;
			}
		}
		return null;
	}
	
	public void addRelation(OLAPRelation rel){
		relations.add(rel);
//		update(this, rel);
	}
	
	public void removeRelation(OLAPRelation rel){
		relations.remove(rel);
//		update(this, rel);
	}
	
	public List<OLAPRelation> getRelations(){
		return relations.getList();
	}

	public void setSecurity(Security security){
		this.security = security;
	}

	/**
	 * return the datasource with the given id
	 * @param id
	 * @return
	 */
	public DataSource findDataSource(String id) {
		for(DataSource d : drivers.getList()){
			if (d.getId().equals(id))
				return d;
		}
		return null;
	}

	public Security getSecurity(){
		return security;
	}

	public ListDataSource getListDataSource() {
		return drivers;
	}

	public ListRelation getListRelations() {
		return relations;
	}

	public String exportToMondrian(){
		StringBuffer buf = new StringBuffer();
		buf.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n\n");
		buf.append(((OLAPSchema)schema).getXML());
		return buf.toString();
	}
	
	public List<ICube> getCubes(){
		return schema.getICubes();
	}
	
	public List<ICube> getCubes(SecurityGroup secu){
		return ((OLAPSchema)schema).getCubes(secu);
	}
	
	public XMLASchema getXMLASchema(){
		if (schema instanceof XMLASchema)
			return(XMLASchema)schema;
		return null;
	}
	
	public void setXMLASchema(XMLASchema sch){
		schema = sch;
	}
	
	public ISchema getSchema(){
		return schema;
	}


	public void setPreloadConfig(PreloadConfig preloadConfig) {
		this.preloadConfig = preloadConfig;
	}


	public PreloadConfig getPreloadConfig() {
		return preloadConfig;
	}
}
