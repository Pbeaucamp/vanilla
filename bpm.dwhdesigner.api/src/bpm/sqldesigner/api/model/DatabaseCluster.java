package bpm.sqldesigner.api.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

import bpm.sqldesigner.api.database.DataBaseConnection;
import bpm.sqldesigner.api.document.DocumentSnapshot;
import bpm.sqldesigner.api.document.SchemaView;

public class DatabaseCluster extends Node {

	protected TreeMap<String, Catalog> catalogs = new TreeMap<String, Catalog>();
	protected TypesList typesLists = new TypesList();
	private String productName;
	private Date date = new Date();
	DataBaseConnection databaseConnection = null;
	private String fileName = "";

	
	private List<SchemaView> schemaViews = new ArrayList<SchemaView>();
	private List<DocumentSnapshot> snapShots = new ArrayList<DocumentSnapshot>();
	
	
	public void addSchemaView(SchemaView view){
		if (view != null){
			schemaViews.add(view);
			getListeners().firePropertyChange(PROPERTY_ADD, null, view);
		}
		
	}
	
	public List<SchemaView> getSchemaViews(){
		return new ArrayList<SchemaView>(schemaViews);
	}
	
	public void removeSchemaView(SchemaView view){
		schemaViews.remove(view);
		getListeners().firePropertyChange(PROPERTY_ADD, null, view);
	}
	
	
	public void addDocumentSnapshot(DocumentSnapshot view) throws Exception{
		if (view != null){
			
			for(DocumentSnapshot s : snapShots){
				if (s.getName().equals(view.getName())){
					throw new Exception("The DatabaseCuster " + getName() + " already contains a DwhView named " + view.getName());
				}
			}
			
			view.setDatabaseCluster(this);
			snapShots.add(view);
			getListeners().firePropertyChange(PROPERTY_ADD, null, view);
		}
		
	}
	
	public List<DocumentSnapshot> getDocumentSnapshots(){
		return new ArrayList<DocumentSnapshot>(snapShots);
	}
	
	public void removeDocumentSnapshot(DocumentSnapshot view){
		snapShots.remove(view);
		getListeners().firePropertyChange(PROPERTY_ADD, null, view);
	}
	
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public DatabaseCluster(Date date) {
		this.date = date;
	}

	public DatabaseCluster() {
	}

	public void addCatalog(Catalog catalog) {
		catalogs.put(catalog.getName(), catalog);
	}

	public Catalog getCalalog(String cName) {
		return catalogs.get(cName);
	}

	public TreeMap<String, Catalog> getCatalogs() {
		return catalogs;
	}

	public TypesList getTypesLists() {
		return typesLists;
	}

	public void setTypesLists(TypesList typesLists) {
		this.typesLists = typesLists;
	}

	public void setProductName(String databaseProductName) {
		productName = databaseProductName;
	}

	public String getProductName() {
		return productName;
	}

	@Override
	public Object[] getChildren() {
		
		List l = new ArrayList(catalogs.values());
		l.addAll(getDocumentSnapshots());
		return l.toArray(new Object[l.size()]);
	}

	public void setDatabaseConnection(DataBaseConnection dataBaseConnection) {
		databaseConnection = dataBaseConnection;
	}

	@Override
	public DataBaseConnection getDatabaseConnection() {
		return databaseConnection;

	}

	public void removeCatalog(Catalog catalog) {
		catalogs.remove(catalog.getName());
	}

	@Override
	public DatabaseCluster getCluster() {
		return this;
	}

	public void removeCatalog(String name) {
		catalogs.remove(name);
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public SchemaView getSchemaView(Schema o) {
		for(SchemaView v : schemaViews){
			if (v.getSchema() == o){
				return v;
			}
		}
		SchemaView view = new SchemaView(o);
		view.setName(o.getName());
		addSchemaView(view);
		
		return view;
	}

	public SchemaView getSchemaView(Catalog o) {
		for(SchemaView v : schemaViews){
			if (v.getSchema().getCatalog() == o){
				return v;
			}
		}
		SchemaView view = new SchemaView(o);
		view.setName(o.getName());
		addSchemaView(view);
		
		return view;
	}

}
