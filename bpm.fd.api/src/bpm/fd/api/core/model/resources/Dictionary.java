package bpm.fd.api.core.model.resources;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.fd.api.core.model.Versionning;
import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.components.definition.chart.ComponentChartDefinition;
import bpm.fd.api.core.model.components.definition.chart.IChartData;
import bpm.fd.api.core.model.components.definition.olap.ComponentFaView;
import bpm.fd.api.core.model.components.definition.report.ComponentKpi;
import bpm.fd.api.core.model.components.definition.report.ComponentReport;
import bpm.fd.api.core.model.datas.DataSet;
import bpm.fd.api.core.model.datas.DataSource;
import bpm.fd.api.core.model.resources.exception.DictionaryException;

public class Dictionary {

	public static final String PROPERTY_CONTENT_CHANGED = "bpm.fd.api.core.model.resources.Dictionary.eventContentChanged";
	public static final String PROPERTY_COMPONENT_ADDED = "bpm.fd.api.core.model.resources.Dictionary.eventComponentAdded";
	public static final String PROPERTY_COMPONENT_REMOVED = "bpm.fd.api.core.model.resources.Dictionary.eventComponentRemoved";
	public static final String PROPERTY_COMPONENT_CHANGED = "bpm.fd.api.core.model.resources.Dictionary.eventComponentChanged";
	public static final String PROPERTY_DATASOURCE_CHANGED = "bpm.fd.api.core.model.resources.Dictionary.eventDataSourceChanged";
	public static final String PROPERTY_DATASET_CHANGED = "bpm.fd.api.core.model.resources.Dictionary.eventDataSetChanged";;
	
	
	private String name;

	private Properties originInfos;
	
	private List<IComponentDefinition> components = new ArrayList<IComponentDefinition>();
	private List<DataSource> datasources = new ArrayList<DataSource>();
	private List<DataSet> datasets = new ArrayList<DataSet>();
	
	private PropertyChangeSupport listeners = new PropertyChangeSupport(this);
	private List<Palette> palettes = new ArrayList<Palette>();
	
	
	public Dictionary(){
		
	}
	
	public void firePropertyChange(String propertyName, Object oldValue, Object newValue){
		listeners.firePropertyChange(propertyName, oldValue, newValue);
	}
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		listeners.addPropertyChangeListener(listener);
		
	}
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		listeners.removePropertyChangeListener(listener);
		
	}
	public void addPalette(Palette p){
		if (p != null){
			palettes.add(p);
			firePropertyChange(PROPERTY_CONTENT_CHANGED, null, p);
		}
	}
	
	public void removePalette(Palette p){
		if (p != null){
			palettes.remove(p);
			
			for(IComponentDefinition d : getComponents(ComponentChartDefinition.class)){
				if (d.getDatas() != null && ((IChartData)d.getDatas()).getColorPalette() == p){
					((IChartData)d.getDatas()).setColorPalette(null);
				}
			}
			
			firePropertyChange(PROPERTY_CONTENT_CHANGED, null, p);
		}
	}
	
	public List<Palette> getPalettes(){
		return new ArrayList<Palette>(palettes);
	}
	public String getName() {
		return name;
	}

	/**
	 * @return the originInfos
	 */
	public Properties getOriginInfos() {
		return originInfos;
	}


	/**
	 * @param originInfos the originInfos to set
	 */
	public void setOriginInfos(Properties originInfos) {
		this.originInfos = originInfos;
	}


	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the components
	 */
	public List<IComponentDefinition> getComponents() {
		return new ArrayList<IComponentDefinition>(components);
	}
	
	/**
	 * @return the components
	 */
	public List<IComponentDefinition> getComponents(Class<? extends IComponentDefinition> componentClass) {
		List<IComponentDefinition> l = new ArrayList<IComponentDefinition>();
		for(IComponentDefinition d : getComponents()){
			if (d.getClass() == componentClass){
				l.add(d);
			}
		}
		return l;
	}
	
	/**
	 * add The component to the directory if not already presents
	 * if component is null, return false and dont not add it
	 * 
	 * the datasource is added if it not already present
	 * 
	 * @param component
	 * @return true if the component is added
	 * @throws DictionaryException : if a component with the same name already is present
	 */
	public boolean addComponent(IComponentDefinition component) throws DictionaryException{
		if (component == null){
			return false;
		}
		
		
		for(IComponentDefinition c : getComponents()){
			if (c.getName().equals(component.getName())){
				throw new DictionaryException("The dictionary " + getName() + " already contains a Component named " + component.getName());
			}
			else if ( c == component){
				return false;
			}
		}
		
//		boolean addDataSource = true;
//		for(DataSource ds : getDatasources()){
//			if (ds == component.getDataSource()){
//				addDataSource = false;
//			}
//		}
//		
//		if (addDataSource){
//			addDataSource(component.getDataSource());
//		}
		boolean b = components.add(component); 
		if (b){
			firePropertyChange(PROPERTY_COMPONENT_ADDED, null, component);
		}
		return b; 

	}

	
	public boolean removeComponent(IComponentDefinition component){
		boolean b = components.remove(component);
		if (b){
			firePropertyChange(PROPERTY_COMPONENT_REMOVED, null, component);
		}
		return b;
	}
	
	public void addDataSource(DataSource dataSource) throws  DictionaryException{
		for(DataSource d : getDatasources()){
			if (d.getName().equals(dataSource.getName())){
				throw new DictionaryException("The dictionary " + getName() + " already contains a DataSet named " + dataSource.getName());
			}
		}
		datasources.add(dataSource);
		firePropertyChange(PROPERTY_CONTENT_CHANGED, null, dataSource);
	}
	
	public void removeDataSource(DataSource dataSource){
		datasources.remove(dataSource);
		firePropertyChange(PROPERTY_CONTENT_CHANGED, null, dataSource);
	}
	
	/**
	 * @return the datasources
	 */
	public List<DataSource> getDatasources() {
		return new ArrayList<DataSource>(datasources);
	}

	public List<DataSet> getDatasets() {
	
		return new ArrayList<DataSet>(datasets);
	}

	public void addDataSet(DataSet ds)  throws  DictionaryException{
		if (!datasets.contains(ds)){
			for(DataSet d : getDatasets()){
				if (d.getName().equals(ds.getName())){
					throw new DictionaryException("The dictionary " + getName() + " already contains a DataSet named " + ds.getName());
				}
			}
			this.datasets.add(ds);
			firePropertyChange(PROPERTY_CONTENT_CHANGED, null, ds);
		}
		
		
	}
	
	public void removeDataSet(DataSet ds) {
		this.datasets.remove(ds);
		ds.setDataSourceName(null);
		firePropertyChange(PROPERTY_CONTENT_CHANGED, null, ds);
		
	}
	
	public Element getElement(){
		Element e = DocumentHelper.createElement("dictionary");
		e.addAttribute("version", Versionning.MODEL_VERSION);
		e.addAttribute("name", getName());
		
		for(Palette p : getPalettes()){
			Element palelem = DocumentHelper.createElement("palette");
			palelem.addAttribute("name", getName());
			for(String s : p.getKeys()){
				String val = p.getColor(s);
				if (val != null){
					palelem.addElement("color").addAttribute("key", s).addAttribute("value", val);
				}
			}
			e.add(palelem);
		}
		
		for(DataSource d : getDatasources()){
			e.add(d.getElement());
		}
		
		for(DataSet d : getDatasets()){
			e.add(d.getElement());
		}
		
		for(IComponentDefinition c : getComponents()){
			e.add(c.getElement());
		}
		
		/*
		 * inject Dictionary Dependancies 
		 * 
		 * TODO: create an interface for object that can provide a dependancie
		 * and pass throught it with � Collection storing them
		 * to allow plugability of components
		 */
		Element depElement = e.addElement("dependancies");
		for(DataSource ds : getDatasources()){
			if (ds.getOdaExtensionId().equals("bpm.metadata.birt.oda.runtime")){
				try{
					int dirItid = Integer.parseInt(ds.getProperties().getProperty("DIRECTORY_ITEM_ID"));
					depElement.addElement("dependantDirectoryItemId").setText("" + dirItid);
				}catch(Exception ex){
					ex.printStackTrace();
				}
				
			}
			else if (ds.getOdaExtensionId().equals("bpm.fwr.oda.runtime")){
				try{
					int dirItid = Integer.parseInt(ds.getProperties().getProperty("FWREPORT_ID"));
					depElement.addElement("dependantDirectoryItemId").setText("" + dirItid);
				}catch(Exception ex){
					ex.printStackTrace();
				}
				
			}
			else if (ds.getOdaExtensionId().equals("bpm.excel.oda.runtime")){
				try{
					int dirItid = Integer.parseInt(ds.getProperties().getProperty("repository.item.id"));
					depElement.addElement("dependantDirectoryItemId").setText("" + dirItid);
				}catch(Exception ex){
					ex.printStackTrace();
				}
				
			}
			else if (ds.getOdaExtensionId().equals("bpm.csv.oda.runtime")){
				try{
					int dirItid = Integer.parseInt(ds.getProperties().getProperty("repository.item.id"));
					depElement.addElement("dependantDirectoryItemId").setText("" + dirItid);
				}catch(Exception ex){
					ex.printStackTrace();
				}
				
			}
		}
		for(IComponentDefinition def : getComponents()){
			if (def instanceof ComponentReport){
				
				depElement.addElement("dependantDirectoryItemId").setText("" + ((ComponentReport)def).getDirectoryItemId());
			}
			else if (def instanceof ComponentFaView){
				depElement.addElement("dependantDirectoryItemId").setText("" + ((ComponentFaView)def).getDirectoryItemId());
			}
			else if (def instanceof ComponentKpi){
				depElement.addElement("dependantDirectoryItemId").setText("" + ((ComponentKpi)def).getDirectoryItemId());
			}
		}
		
		return e;
	}

	public DataSource getDatasource(String dataSourceName) {
		for(DataSource ds : datasources){
			if (ds.getName().equals(dataSourceName)){
				return ds;
			}
		}
		return null;
		
	}

	/**
	 * 
	 * @param componentName
	 * @return the component with the given name or null
	 */
	public IComponentDefinition getComponent(String componentName) {
		for(IComponentDefinition def : components){
			if (def.getName().equals(componentName)){
				return def;
			}
		}
		return null;
	}
	
	
	/**
	 * 
	 * @param dataSource
	 * @return all DataSet coming from the given DataSource
	 */
	public List<DataSet> getDataSetsFor(DataSource dataSource){
		List<DataSet> dataSets = new ArrayList<DataSet>();
		
		for(DataSet ds : getDatasets()){
			if (dataSource != null && ds.getDataSourceName().equals(dataSource.getName())){
				dataSets.add(ds);
			}
		}
		return dataSets;
	}
	
	/**
	 * 
	 * @param dataSourceName
	 * @return all DataSet coming from the given DataSourceName
	 */
	public List<DataSet> getDataSetsFor(String dataSourceName){
		List<DataSet> dataSets = new ArrayList<DataSet>();
		
		for(DataSet ds : getDatasets()){
			if (dataSourceName != null && ds.getDataSourceName().equals(dataSourceName)){
				dataSets.add(ds);
			}
		}
		return dataSets;
	}

	public Palette getPalette(String name) {
		for(Palette p : getPalettes()){
			if (p.getName().equals(name)){
				return p;
			}
		}
		return null;
	}
}
