package bpm.metadata.layer.business;

import java.util.List;
import java.util.Locale;

import bpm.metadata.MetaData;
import bpm.metadata.layer.logical.Relation;


public interface IBusinessModel {
	
	/**
	 * return the list of BusinessPacakge
	 * @return
	 */
	public List<IBusinessPackage> getBusinessPackages(String groupName);
	
	
	public List<Locale> getLocales();
	
	/**
	 * return the BusinessPackge named by name atribute
	 * or null if it doesnt exist
	 * @param name
	 * @return
	 */
	public IBusinessPackage getBusinessPackage(String name, String groupName);
	
	/**
	 * return the BusinessModel name
	 * @return
	 */
	public String getName();


	public void removeRelation(Relation t);
	
	/**
	 * 
	 * @return the Full Model owing this businessModel
	 */
	public MetaData getModel();
	
	public void setRelationStrategies(List<RelationStrategy> relationStrategies);

	public List<RelationStrategy> getRelationStrategies();
	
	public void addRelationStrategy(RelationStrategy relationStrategy);

	public RelationStrategy getRelationStrategy(String strat);
	
	public List<IBusinessTable> getBusinessTables();
	
	public List<Relation> getRelations();


	public Relation getRelationByKey(String rel);
}
