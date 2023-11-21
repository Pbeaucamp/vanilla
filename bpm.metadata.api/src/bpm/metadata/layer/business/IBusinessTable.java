package bpm.metadata.layer.business;

import java.util.Collection;
import java.util.List;

import bpm.metadata.layer.logical.IDataStreamElement;
import bpm.metadata.resource.IFilter;

public interface IBusinessTable {

	/**
	 * return the BusinessTabel name
	 * @return
	 */
	public String getName();
	
	/**
	 * set the Name of this BusinessTable
	 * @param name
	 */
	public void setName(String name);
	
	/**
	 * retun the description
	 * @return
	 */
	public String getDescription();
	
	/**
	 * return columns of the BusinessTables
	 * @return
	 */
	public Collection<IDataStreamElement> getColumns(String groupName);
	
	
	/**
	 * 
	 * @param groupName
	 * @param colName
	 * @return the IDataStreamElement with the given name is it is available for
	 * the groupName
	 * @throws GrantException
	 */
	public IDataStreamElement getColumn(String groupName, String colName)throws GrantException;

	/**
	 * 
	 * @return the BusinessModel owning this BusinessTable
	 */
	public BusinessModel getModel();
	
	
	/**
	 * set the model owning this BusinessTable
	 * @param model
	 */
	public void setBusinessModel(BusinessModel model);

	/**
	 * 
	 * @return the XML reprensetation of the BusinessTable
	 */
	public String getXml();

	public void setDescription(String text);

	/**
	 * give/remove the access for the given Groupname
	 * @param groupName
	 * @param b
	 */
	public void setGranted(String groupName, boolean b);

	/**
	 * return true if it is granted for the given groupName
	 * @param s
	 * @return
	 */
	public boolean isGrantedFor(String s);
	
	
	/**
	 * return the parent or null
	 * @return
	 */
	public IBusinessTable getParent();
	
	/**
	 * return the child list
	 * @return
	 */
	public List<IBusinessTable> getChilds(String groupName);
	
	/**
	 * return the BusinessTable with the specified name
	 * @param name
	 * @param groupName
	 * @return
	 */
	public IBusinessTable getChildNamed(String name, String groupName);
	
	
	/**
	 * add a filter for the given group
	 * @param groupName
	 * @param f
	 */
	public void addFilter(String groupName, IFilter f);
	
	/**
	 * remove the given filter for the given group
	 * @param groupName
	 * @param f
	 */
	public void removeFilter(String groupName, IFilter f);
	
	/**
	 * remove the filter for all groups
	 * @param f
	 */
	public void removeFilter(IFilter f);
	
	/**
	 * 
	 * @param groupName
	 * @return the filters for the specified from this Businesstable 
	 */
	public List<IFilter> getFilterFor(String groupName);
	
	/**
	 * 
	 * @return the filters from this Businesstable
	 */
	public List<IFilter> getFilters();

	/**
	 * remove the given IDataStreamElement from the businesstable
	 * @param dataStreamElement
	 */
	public void removeColumn(IDataStreamElement dataStreamElement);
	
	
	/**
	 * 
	 * @param groupName
	 * @return the IDataStreamElement with defined order in the model
	 */
	public List<IDataStreamElement> getColumnsOrdered(String groupName);
	
	/**
	 * 
	 * @return the positionX for the relation View representation
	 */
	public int getPositionX();
	
	/**
	 * set the positionX for the relation View representation
	 * @param positionX
	 */
	public void setPositionX(int positionX);
	
	/**
	 * set the positionX for the relation View representation
	 * @param positionX
	 */
	public void setPositionX(String positionX);
	
	/**
	 * 
	 * @return the YPosition for the relation View representation
	 */
	public int getPositionY();

	/**
	 * set the YPosition for the relation View representation
	 * @param positionY
	 */
	public void setPositionY(int positionY);
	
	/**
	 * 
	 * @return true if this BusinessTable can be explored
	 */
	public boolean isDrillable();
	
	/**
	 * a BusinessTable Drillable may be explored from a client
	 * @param isDrillable
	 */
	public void setIsDrillable(boolean isDrillable);
	
	/**
	 * 
	 * @return true if the table can be updated (only if drillable)
	 */
	public boolean isEditable();
	
	/**
	 * Set a table as editable (needs to be drillable also)
	 * @param editable
	 */
	public void setEditable(boolean editable);
}
