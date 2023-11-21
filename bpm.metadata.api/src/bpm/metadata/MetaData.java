package bpm.metadata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

import bpm.metadata.layer.business.BusinessModel;
import bpm.metadata.layer.business.IBusinessModel;
import bpm.metadata.layer.business.IBusinessPackage;
import bpm.metadata.layer.logical.AbstractDataSource;
import bpm.metadata.layer.logical.IDataSource;
import bpm.metadata.layer.logical.MultiDSRelation;
import bpm.metadata.layer.logical.olap.UnitedOlapDatasource;
import bpm.metadata.layer.physical.olap.UnitedOlapConnection;
import bpm.metadata.misc.Type;
import bpm.metadata.resource.IResource;
import bpm.metadata.scripting.Script;
import bpm.metadata.scripting.Variable;

public class MetaData {
	private DocumentProperties properties = new DocumentProperties();

	private HashMap<String, AbstractDataSource> dataSources = new HashMap<String, AbstractDataSource>();
	private HashMap<String, IBusinessModel> models = new HashMap<String, IBusinessModel>();
	private List<MultiDSRelation> multiRelations = new ArrayList<MultiDSRelation>();

	private List<Locale> locales = new ArrayList<Locale>();
	private List<String> dependencies = new ArrayList<String>();

	private List<Variable> variables = new ArrayList<Variable>();
	private List<Script> scripts = new ArrayList<Script>();

	private List<Type> availablesTypes = new ArrayList<Type>();
	
	private Integer d4cServerId;
	private String d4cOrganisation;

	private boolean built = false;

	public boolean isBuilt() {
		return built;
	}

	protected void setBuilt() {
		this.built = true;
	}

	/**
	 * add the given to the MetaData variable List if v== null or a variable
	 * with the same Name already exists do nothing
	 */
	public void addVariable(Variable v) {
		if (v == null) {
			return;
		}

		for (Variable _v : getVariables()) {
			if (_v.getName().equals(v.getName())) {
				return;
			}
		}

		variables.add(v);
	}

	/**
	 * 
	 * @return all teh defined variable usable in a script
	 */
	public List<Variable> getVariables() {
		return new ArrayList<Variable>(variables);
	}

	/**
	 * remove the given Variable
	 * 
	 * @param v
	 */
	public void removeVariable(Variable v) {
		variables.remove(v);
	}

	/**
	 * add the given scriot to the MetaData Script List if v== null or a Script
	 * with the same Name already exists do nothing
	 */
	public void addScript(Script v) {
		if (v == null) {
			return;
		}

		for (Script _v : getScripts()) {
			if (_v.getName().equals(v.getName())) {
				return;
			}
		}

		scripts.add(v);
	}

	/**
	 * 
	 * @return all teh defined Scripts
	 */
	public List<Script> getScripts() {
		return new ArrayList<Script>(scripts);
	}

	/**
	 * remove the given Script
	 * 
	 * @param v
	 */
	public void removeScript(Script v) {
		scripts.remove(v);
	}

	/**
	 * 
	 * @return all the availables Types
	 */
	public List<Type> getTypes() {
		return new ArrayList<Type>(availablesTypes);
	}

	/**
	 * Add a Type
	 */
	public void addType(Type type) {
		if (!availablesTypes.contains(type)) {
			this.availablesTypes.add(type);
		}
	}

	/**
	 * Remove the given Type
	 * 
	 * @param type
	 */
	public void removeType(Type type) {
		this.availablesTypes.remove(type);
	}

	public List<String> getDepencies() {
		return dependencies;
	}

	public void addDependencies(String directoryItemId) {
		boolean exists = false;
		for (String s : dependencies) {
			if (s.equals(directoryItemId)) {
				exists = true;
				break;
			}
		}

		if (!exists) {
			dependencies.add(directoryItemId);
		}
	}

	public MetaData() {
		locales.add(new Locale(Locale.getDefault().getLanguage()));
	}

	public List<Locale> getLocales() {
		return locales;
	}

	public void addLocale(String language) {
		addLocale(new Locale(language));
	}

	public void addLocale(Locale l) {
		if (!locales.contains(l)) {
			locales.add(l);
		}

	}

	public void removeLocale(Locale l) {
		locales.remove(l);
	}

	private HashMap<String, IResource> resources = new LinkedHashMap<String, IResource>();

	public void addResource(IResource r) {
		resources.put(r.getName(), r);
	}

	public void delResource(IResource r) {
		resources.remove(r.getName());
		for (IBusinessModel m : models.values()) {
			if (((BusinessModel) m).getResources().contains(r)) {
				((BusinessModel) m).removeResource(r);
			}
			for (IBusinessPackage p : m.getBusinessPackages("none")) {
				if (p.getResources().contains(r)) {
					p.removeResource(r);
				}
			}
		}
	}

	public void addDataSource(AbstractDataSource dataSource) {
		dataSources.put(dataSource.getName(), dataSource);
		dataSource.setMetaDataModel(this);

		if (dataSource instanceof UnitedOlapDatasource) {
			// if
			// (((OlapConnection)((OLAPDataSource)dataSource).getConnection()).getType()
			// == OlapConnection.REPOSITORY ){
			// addDependencies(((OlapConnection)((OLAPDataSource)dataSource).getConnection()).getDirectoryItemId());
			// }
			addDependencies(((UnitedOlapConnection) ((UnitedOlapDatasource) dataSource).getConnection()).getIdentifier().getDirectoryItemId() + "");
		}
	}

	public Collection<AbstractDataSource> getDataSources() {
		return dataSources.values();
	}

	public Collection<IBusinessModel> getBusinessModels() {
		return models.values();
	}

	public void addBusinessModel(BusinessModel model) {
		models.put(model.getName(), model);
		model.setModel(this);
	}

	public void removeBusinessModel(IBusinessModel model) {
		models.remove(model.getName());
	}

	public String getXml(boolean forceSaving) throws Exception {
		StringBuffer buf = new StringBuffer();

		buf.append("<freeMetaData>\n");

		try {
			buf.append(properties.getXml());
		} catch (Exception e) {
			e.printStackTrace();
			if(!forceSaving) {
				throw new Exception("A problem happend when saving the properties, please check them before saving again.");
			}
		}

		try {
			for (String s : dependencies) {
				buf.append("    <dependantItemId>" + s + "</dependantItemId>\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
			if(!forceSaving) {
				throw new Exception("A problem happend when saving the dependancies, please check them before saving again.");
			}
		}

		try {
			for (Locale l : locales) {
				buf.append("    <language>" + l.getLanguage() + "</language>\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
			if(!forceSaving) {
				throw new Exception("A problem happend when saving the locales, please check them before saving again.");
			}
		}

		try {
			for (AbstractDataSource ds : dataSources.values()) {
				buf.append(ds.getXml());
			}
		} catch (Exception e) {
			e.printStackTrace();
			if(!forceSaving) {
				throw new Exception("A problem happend when saving the datasources, please check them before saving again.");
			}
		}

		try {
			for (MultiDSRelation r : multiRelations) {
				buf.append(r.getXml());
			}
		} catch (Exception e) {
			e.printStackTrace();
			if(!forceSaving) {
				throw new Exception("A problem happend when saving the multi datasources relations, please check them before saving again.");
			}
		}

		try {
			for (IBusinessModel m : models.values()) {
				buf.append(((BusinessModel) m).getXml());
			}
		} catch (Exception e) {
			e.printStackTrace();
			if(!forceSaving) {
				throw new Exception("A problem happend when saving the models, please check them before saving again.");
			}
		}

		try {
			for (IResource r : resources.values()) {
				buf.append(r.getXml());
			}
		} catch (Exception e) {
			e.printStackTrace();
			if(!forceSaving) {
				throw new Exception("A problem happend when saving the resources, please check them before saving again.");
			}
		}

		try {
			for (Variable v : getVariables()) {
				buf.append(v.getXml());
			}
		} catch (Exception e) {
			e.printStackTrace();
			if(!forceSaving) {
				throw new Exception("A problem happend when saving the variables, please check them before saving again.");
			}
		}

		try {
			for (Script v : getScripts()) {
				buf.append(v.getXml());
			}
		} catch (Exception e) {
			e.printStackTrace();
			if(!forceSaving) {
				throw new Exception("A problem happend when saving the scripts, please check them before saving again.");
			}
		}

		try {
			for (Type t : getTypes()) {
				buf.append(t.getXml());
			}
		} catch (Exception e) {
			e.printStackTrace();
			if(!forceSaving) {
				throw new Exception("A problem happend when saving the types, please check them before saving again.");
			}
		}
		
		if (d4cServerId != null) {
			buf.append("	<d4cServerId>" + d4cServerId + "</d4cServerId>\n");
		}
		
		if (d4cOrganisation != null && !d4cOrganisation.isEmpty()) {
			buf.append("	<d4cOrganisation>" + d4cOrganisation + "</d4cOrganisation>\n");
		}

		buf.append("</freeMetaData>\n");
		return buf.toString();
	}

	public void removeDataSource(IDataSource abstractDataSource) {
		dataSources.remove(abstractDataSource.getName());

	}

	/**
	 * add a relation two different DataSources
	 * 
	 * @param relation
	 */
	public void addMultiRelation(MultiDSRelation relation) {

		if (!multiRelations.contains(relation)) {
			multiRelations.add(relation);
		}
	}

	/**
	 * get the matching dataSource of the given name
	 * 
	 * @param name
	 * @return
	 */
	public IDataSource getDataSource(String name) {
		return dataSources.get(name);
	}

	public List<MultiDSRelation> getMultiDataSourceRelations() {
		return multiRelations;
	}

	/**
	 * return the list of Package containing the specified IResource
	 * 
	 * @param resource
	 * @return
	 */
	public Collection<IBusinessPackage> getPackagesContaining(IResource resource) {
		Collection<IBusinessPackage> collection = new ArrayList<IBusinessPackage>();

		for (IBusinessModel m : models.values()) {
			for (IBusinessPackage p : m.getBusinessPackages("none")) {

				if (p.getResources().contains(resource)) {
					collection.add(p);
				}
			}
		}

		return collection;
	}

	public Collection<IResource> getResources() {
		return resources.values();
	}

	public IResource getResource(String name) {
		for (String s : resources.keySet()) {
			if (s.equals(name)) {
				return resources.get(s);
			}
		}
		return null;
	}

	public DocumentProperties getProperties() {
		return properties;
	}

	public void setProperties(DocumentProperties properties) {
		this.properties = properties;
	}

	public Integer getD4cServerId() {
		return d4cServerId;
	}
	
	public void setD4cServerId(Integer d4cServerId) {
		this.d4cServerId = d4cServerId;
	}
	
	public void setD4cServerId(String d4cServerId) {
		this.d4cServerId = d4cServerId != null && !d4cServerId.isEmpty() ? Integer.parseInt(d4cServerId) : null;
	}
	
	public String getD4cOrganisation() {
		return d4cOrganisation;
	}
	
	public void setD4cOrganisation(String d4cOrganisation) {
		this.d4cOrganisation = d4cOrganisation;
	}
}
