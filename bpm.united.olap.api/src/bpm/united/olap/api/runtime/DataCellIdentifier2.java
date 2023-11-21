package bpm.united.olap.api.runtime;

import java.io.Serializable;
import java.util.List;

import org.eclipse.emf.ecore.EObject;

import bpm.united.olap.api.data.IExternalQueryIdentifier;
import bpm.united.olap.api.model.ElementDefinition;
import bpm.united.olap.api.model.Measure;
import bpm.united.olap.api.model.Member;
import bpm.united.olap.api.result.DrillThroughIdentifier;

/**
 * A DataCellIdentifier is meant to identify Value cell on a OLAPQuery result.
 * 
 * It is composed by a list of {Member, Measure} to know where they belong in the DataStorage
 * and eventually a list of Members that act in the where clause from tle OLAP Query
 * 
 * the WhereMembers cannot belong to a hierarchy used within the Intersections
 * 
 * A DataCellIdentifier should always generate a uniqueKey whatever are the 
 * the order on its Intersection and whereMembers.
 * 
 * If a Grid perform a Swap axes, the DataCellIDentifier should be changed,
 * but if they are, the new ones MUST have the same Key
 * 
 * 
 * @author ludo
 *
 */
public interface DataCellIdentifier2 extends EObject,Serializable{
	/**
	 * the intersection may only be Measures or Members
	 * @return
	 */
	public List<ElementDefinition> getIntersections();
	
	public void setIntersections(List<ElementDefinition> intersections);
	
//	/**
//	 * 
//	 * @return the md5 encoded query from the fact table that has generated this identifier
//	 */
//	public String getRealQueryKeyPart();
//	public void setRealQueryKeyPart(String md5EffectiveQuery);
	
	public List<Member> getWhereMembers();
	
	
	public void setWhereMembers(List<Member> whereMembers);
	
	
	/**
	 * create a uniqueIdentifier for this DataCellIdentifier
	 * @return
	 */
	public String getKey();
	
	/**
	 * dump datas in logger
	 */
	public void dump();

	public void addIntersection(ElementDefinition el);

	public void addWhereMember(Member el);
	
	public void setMeasure(Measure measure);
	
	public Measure getMeasure();
	
	/**
	 * Get a light identifier to be part of the OlapResult
	 * @return
	 */
	public DrillThroughIdentifier getDrillThroughIdentifier();
	
	/**
	 * Return an identifier to use in FMDT and ODA
	 * @return
	 */
	public IExternalQueryIdentifier getExternalQueryIdentifier();
}
