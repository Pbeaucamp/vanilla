package bpm.metadata.layer.business;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import bpm.metadata.ISecurizable;
import bpm.metadata.MetaData;
import bpm.metadata.layer.logical.AbstractDataSource;
import bpm.metadata.layer.logical.IDataStream;
import bpm.metadata.layer.logical.IDataStreamElement;
import bpm.metadata.layer.logical.Relation;
import bpm.metadata.resource.IResource;
import bpm.united.olap.api.tools.AlphanumComparator;

public class BusinessModel implements IBusinessModel, ISecurizable{
	private String name;	
	private String description = "";
	
	private HashMap<String, IBusinessTable> tables = new HashMap<String, IBusinessTable>();
	private List<IResource> resources = new ArrayList<IResource>();
	private HashMap<String, IBusinessPackage> packages = new HashMap<String, IBusinessPackage>();
	private List<Relation> relations = new ArrayList<Relation>();
	
	private HashMap<String, Boolean> granted = new HashMap<String, Boolean>();

	
	private HashMap<Locale, String> outputName = new HashMap<Locale, String>();
	
	private MetaData model;
	
	private List<RelationStrategy> relationStrategies = new ArrayList<RelationStrategy>();
	
	public void setModel(MetaData model){
		this.model = model;
	}
	
	public MetaData getModel(){
		return model;
	}
	
	public List<Locale> getLocales(){
		return model.getLocales();
		
	}
	public String getOuputName(Locale l){
		return outputName.get(l) != null && !outputName.get(l).equals("") ? outputName.get(l) : name;
	}
	
	public String getOuputName(){
		return outputName.get(Locale.getDefault()) != null && !outputName.get(Locale.getDefault()).equals("") ? outputName.get(Locale.getDefault()) : name;
	}
	
	public void setOutputName(Locale l, String value){
		outputName.put(l, value);
	}
	public void setOutputName(String country, String language, String value){
		outputName.put(new Locale(language, country), value);
	}
	
	public boolean isGrantedFor(String groupName){
		if (groupName == null){
			return true;
		}
		for(String s : granted.keySet()){
			if (groupName.equals(s)){
				return granted.get(s);
			}
		}
		
		return false;
	}
	public void setGranted(String groupName, boolean value) {
		for(String k : granted.keySet()){
			if (k.equals(groupName)){
				this.granted.put(k, value);
				return;
			}
		}
		this.granted.put(groupName, value);
		
	}
	
	/**
	 * 
	 * This method is just left for old Metadata model. However it will not be used for new model
	 * 
	 * @param groupName
	 * @param value
	 */
	@Deprecated
	public void setGranted(String groupName, String value) {
		setGranted(groupName, Boolean.parseBoolean(value));
	}
	
	public void setGroupsGranted(String groups){
		if(!groups.isEmpty()){
			String[] arrayGroups = groups.split(",");
			for(String gr : arrayGroups){
				setGranted(gr, true);
			}
		}
	}
	
	public HashMap<String, Boolean> getGrants(){
		return granted;
	}
	
	
	public void setName(String name){
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
	
	public void setDescription(String description){
		this.description = description;
	}
	
	public String getDescription(){
		return description;
	}
	
	public List<Relation> getRelations(){
		return relations;
	}
	
	public List<IBusinessTable> getBusinessTables(){
		List<IBusinessTable> tab = new ArrayList<IBusinessTable>();
		tab.addAll(tables.values());
		Collections.sort(tab, new Comparator<IBusinessTable>() {
			@Override
			public int compare(IBusinessTable o1, IBusinessTable o2) {
				return o1.getName().compareToIgnoreCase(o2.getName());
			}		
		});
		return tab;
	}
	
	public void addBusinessTable(IBusinessTable businessTable){
		if (businessTable == null){
			return;
		}
		tables.put(businessTable.getName(), businessTable);
		businessTable.setBusinessModel(this);
		updateRelations(false);
	}
	
	public void removeBusinessTable(IBusinessTable table){
		tables.remove(table.getName());
	}
	
	public void addResource(IResource r){
		if (r == null){
			return;
		}
		if (resources.contains(r)){
			return;
		}
		resources.add(r);
	}
	
	public void removeResource(IResource r){
		resources.remove(r);
	}
	
	public List<IResource> getResources(){
		return resources;
	}
	
	public void addBusinessPackage(IBusinessPackage bpackage){
		if (bpackage == null){
			return;
		}
		packages.put(bpackage.getName(), bpackage);
		bpackage.setBusinessModel(this);
	}
	
	public String getXml(){
		StringBuffer buf = new StringBuffer();
		
		buf.append("    <businessModel>\n");
		buf.append("        <name>" + name + "</name>\n");
		buf.append("        <description>" + description + "</description>\n");
		
		for(IBusinessTable t : tables.values()){
			buf.append(t.getXml());
		}
		
		for(Relation r : relations){
			buf.append(r.getXml());
		}
		
		for(RelationStrategy r : relationStrategies) {
			buf.append(r.getXml());
		}
		
		for(IResource r : resources){
			buf.append(r.getXml());
		}
		
		for(IBusinessPackage p : packages.values()){
			buf.append(p.getXml());
		}
		
		
		//grants
		buf.append("        <groupNames>");
		boolean first = true;
		for(String s : granted.keySet()){
			if(granted.get(s)){
				if(first){
					first = false;
				}
				else {
					buf.append(",");
				}
				buf.append(s);
			}
		}
		buf.append("</groupNames>\n");
		
		//outputname
		for(Locale l : outputName.keySet()){
			buf.append("            <outputname>\n");
			buf.append("                <country>" + l.getCountry() + "</country>\n");
			buf.append("                <language>" + l.getLanguage() + "</language>\n");
			buf.append("                <value>" + outputName.get(l) + "</value>\n");
			buf.append("            </outputname>\n");
		}
		buf.append("    </businessModel>\n");
		
		
		return buf.toString();
	}

	public List<IBusinessPackage> getBusinessPackages(String groupName) {
		List<IBusinessPackage> l = new ArrayList<IBusinessPackage>();
		if (groupName.equals("none")){
			l.addAll(packages.values());
		}
		else {
			for(IBusinessPackage b : packages.values()){
				if (b.isGrantedFor(groupName)){
					l.add(b);
				}
			}
		}
		Collections.sort(l, new Comparator<IBusinessPackage>() {
			@Override
			public int compare(IBusinessPackage o1, IBusinessPackage o2) {
				AlphanumComparator comp = new AlphanumComparator();
				return comp.compare(o1.getName(), o2.getName());
			}
		});
		return l;
	}
	
	
	public IBusinessTable getBusinessTable(String s) {
		for(IBusinessTable t : tables.values()){
			if (t.getName().equals(s)){
				return t;
			}
		}
		
		return null;
	}

	public IResource getResource(String s) {
		for(IResource t : resources){
			if (t.getName().equals(s)){
				return t;
			}
		}
		
		return null;
	}

	public IBusinessPackage getBusinessPackage(String name, String groupName) {
		if (groupName.equals("none")){
			return packages.get(name);
		}
		
		if (packages.get(name) != null && packages.get(name).isGrantedFor(groupName)){
			return packages.get(name);
		}
		return null;
		
	}

	public void removePackage(String packageName) {
		packages.remove(packageName);
		
	}
	
	private List<IDataStream> getUsedDataStreams(IBusinessTable t){
		List<IDataStream> streams = new ArrayList<IDataStream>();
		for(IDataStreamElement e : t.getColumns("none")){
			if (!streams.contains(e.getDataStream())){
				streams.add(e.getDataStream());
			}
		}
		for(IBusinessTable c : t.getChilds("none")){
			for(IDataStream s : getUsedDataStreams(c)){
				if (!streams.contains(s)){
					streams.add(s);
				}
			}
		}
		return streams;
	}
	
	public void updateRelations(boolean clear){
		
		
		
		//we get the IDataStreams involved in the model
		List<IDataStream> streams = new ArrayList<IDataStream>();
		for(IBusinessTable t : tables.values()){
				for(IDataStream e : getUsedDataStreams(t)){
					if (!streams.contains(e)){
						streams.add(e);
					}
				}
			
		}
		
		//we get all the different datasources
		List<AbstractDataSource> ds = new ArrayList<AbstractDataSource>();
		for(IDataStream t : streams){
			if (t!= null && !ds.contains(t.getDataSource())){
				ds.add((AbstractDataSource)t.getDataSource());
			}
		}
		
		
		
		for(AbstractDataSource d : ds){
			
			for(Relation r : d.getRelations(streams)){
				
				if (clear){
					for(Relation _r : relations){
						if (_r.getLeftTable() == null && _r.getLeftTableName().equals(r.getLeftTable().getName())
								&&
								_r.getRightTable() == null && _r.getRightTableName().equals(r.getRightTable().getName())){
							
							_r.setDataStreams(d);
						}
					}
					
				}
				
				if (! clear){
					addRelation(r.copy());
				}
				
			}
		}
		
	}
	
	public void addRelation(Relation r){
		boolean isIn = false;
		if (r == null){
			return;
		}
		
		for(Relation rel : relations){
			if ((rel.getRightTable() == null || rel.getLeftTable() == null)
				||
				(rel.getLeftTable() == null || r.getLeftTable() == null)){
				
				if ((rel.getRightTableName().equals(r.getRightTableName()) && rel.getLeftTableName().equals(r.getLeftTableName())
						||
					 rel.getRightTableName().equals(r.getLeftTableName()) && rel.getLeftTableName().equals(r.getRightTableName()))){
							
					isIn = true;

					break;
				}
				
			}
			
						
			else if ((rel.getRightTable() == r.getRightTable() && rel.getLeftTable() == r.getLeftTable()) ||
				(rel.getRightTable() == r.getLeftTable() && rel.getLeftTable() == r.getRightTable())){
				isIn = true;
				break;
			}
		}
		
		if (!isIn && !relations.contains(r)){
			relations.add(r);
		}
	}
	
	/**
	 * return true if the relation r is in the BusinessModel
	 * @param r
	 * @return
	 */
	public boolean containsRelation(Relation r){
		return relations.contains(r);
	}

	public void removeRelation(Relation t) {
		relations.remove(t);
		
	}

	public void setRelationStrategies(List<RelationStrategy> relationStrategies) {
		this.relationStrategies = relationStrategies;
	}

	public List<RelationStrategy> getRelationStrategies() {
		return relationStrategies;
	}
	
	public void addRelationStrategy(RelationStrategy relationStrategy) {
		if(relationStrategies == null) {
			relationStrategies = new ArrayList<RelationStrategy>();
		}
		relationStrategies.add(relationStrategy);
	}

	@Override
	public RelationStrategy getRelationStrategy(String strat) {
		for(RelationStrategy rel : relationStrategies) {
			if(rel.getName().equals(strat)) {
				return rel;
			}
		}
		return null;
	}

	@Override
	public Relation getRelationByKey(String rel) {
		
		for(Relation r : relations) {
			if(r.getRelationKey().equals(rel)) {
				return r;
			}
		}
		
		return null;
	}
	
}
