package bpm.fa.api.olap;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.fasd.olap.Drill;

import bpm.fa.api.item.ItemElement;
import bpm.fa.api.item.ItemValue;
import bpm.fa.api.item.Parameter;
import bpm.fa.api.olap.projection.Projection;
import bpm.fa.api.olap.query.MissingLastTimeDimensionException;
import bpm.fa.api.olap.query.WhereClauseException;
import bpm.fa.api.repository.RepositoryCubeView;
import bpm.united.olap.api.datasource.Relation;
import bpm.united.olap.api.model.Member;

/**
 * Interface for cube manipulation
 * 
 * @author ereynard
 * 
 * TODO :
 * 	- expand mode
 * 	- add static
 * 	- add calculated measures
 *  - modified
 */
public interface OLAPCube {
	
	/**
	 * Executes query
	 * @return an OLAPResult
	 */
	public OLAPResult doQuery() throws Exception ;
	
	
	
//	/**
//	 * refresh the OlapResult content without executing the query
//	 * it must be used when showing hiding Totals
//	 * @param onlyRefreshResult
//	 * @return
//	 * @throws Exception
//	 */
//	public OLAPResult doRefreshResultQuery() throws Exception ;
	
	/**
	 * Executes the given MdX query
	 * @return an OLAPResult
	 */
	public OLAPResult doQuery(String mdxQuery) throws Exception ;
	
	/**
	 * Executes query with a row limitation on the fact table
	 * @param limit
	 * @return
	 * @throws Exception
	 */
	public OLAPResult doQuery(int limit) throws Exception;
	
	/**
	 * Executes the mdxQuery with a row limitation on the fact table
	 * @param mdxQuery
	 * @param limit
	 * @return
	 * @throws Exception
	 */
	public OLAPResult doQuery(String mdxQuery, int limit) throws Exception;
	
	/**
	 * Get last query result
	 * @return an OLAPResult representing last query
	 */
	public OLAPResult getLastResult();
	
	/**
	 * Get List of Dimensions
	 * @return {@link Collection} and {@link Dimension}
	 */
	public Collection<Dimension> getDimensions();
	
	/**
	 * Get List of Measures
	 * @return {@link Collection} and {@link MeasureGroup}
	 */
	public Collection<MeasureGroup> getMeasures();

	/**
	 * Restores query to default one.
	 */
	public void restore();
	
	/**
	 * Swaps Rows and Columns
	 */
	public void swapAxes();
	
	/**
	 * Undo (if possible) previous action
	 */
	public void undo();
	
	/**
	 * Redo (if possible) next action
	 */
	public void redo();
	
	/**
	 * Get Show Properties
	 * @return
	 */
	public boolean getShowProperties();
	
	/**
	 * Set Show properties
	 * @param show
	 */
	public void setshowProperties(boolean show);
	
	/**
	 * Get Show Empty
	 * @return
	 */
	public boolean getShowEmpty();
	
	/**
	 * Set Show Empty result
	 * @param show
	 */
	public void setshowEmpty(boolean show);
	
	/**
	 * Is state active
	 * @return
	 */
	public boolean getStateActive();
	
	/**
	 * Set state active 
	 * @param active
	 */
	public void setStateActive(boolean active);
	
	/**
	 * Get active filters
	 * @return
	 */
	public Collection<String> getFilters();
	
	/**
	 * Drill up to current item (ie: delete items under him)
	 * @param e {@link ItemElement}
	 */
	public void drillup(ItemElement e);
	
	/**
	 * Drill down on selected item
	 * @param e {@link ItemElement}
	 * @return true if some members have been added to the Tree
	 */
	public boolean drilldown(ItemElement e);
	
	/**
	 * Drill Through on selected Value
	 * @param v {@link ItemValue}
	 * @param level, how many levels down, query can be *very* slow, hint : 2 is good
	 * @return {@link OLAPResult} result set for the drillthrough
	 * @throws SQLException 
	 * @throws Exception 
	 */
	public OLAPResult drillthrough(ItemValue v, int level) throws SQLException, Exception;
	
	public OLAPResult drillthrough(ItemValue v, int level, Projection projection) throws SQLException, Exception;
	
	public List<String> getDrillsUrl(ItemValue v) ;
	
	
	
	/**
	 * Add an Element to query, element contains Col or Row
	 * @param e {@link ItemElement}
	 * @throws WhereClauseException 
	 */
	public void add(ItemElement e) throws WhereClauseException;
	
	/**
	 * Add an Element to query, element contains Col or Row
	 * @param e {@link ItemElement}
	 * @param after {@link ItemElement}
	 * @throws WhereClauseException 
	 * @throws MissingLastTimeDimensionException 
	 */
	public void addAfter(ItemElement e, ItemElement after) throws WhereClauseException, MissingLastTimeDimensionException;
	
	/**
	 * Add an Element to query, element contains Col or Row
	 * @param e {@link ItemElement}
	 * @param before {@link ItemElement}
	 * @throws WhereClauseException 
	 * @throws MissingLastTimeDimensionException 
	 */
	public void addBefore(ItemElement e, ItemElement before) throws WhereClauseException, MissingLastTimeDimensionException;
	
	/**
	 * Add an Element to query's where
	 * @param e {@link ItemElement}
	 * @throws WhereClauseException 
	 */
	public void addWhere(ItemElement e) throws WhereClauseException;

	/**
	 * Remove a single Element
	 * @param e {@link ItemElement}
	 */
	public boolean remove(ItemElement e);
	
	/**
	 * Remove a Level
	 * @param l {@link ItemElement}
	 */
	public void removeLevel(ItemElement l);
	
	/**
	 * Remove Element from where
	 * @param e {@link ItemElement}
	 */
	public void removeWhere(ItemElement e);
	
	/**
	 * Moves an Element to query, element contains Col or Row
	 * @param e {@link ItemElement}
	 * @param after {@link ItemElement}
	 */
	public void moveAfter(ItemElement e, ItemElement after);
	
	/**
	 * Moves an Element to query, element contains Col or Row
	 * @param e {@link ItemElement}
	 * @param before {@link ItemElement}
	 */
	public void moveBefore(ItemElement e, ItemElement before);

	
	/**
	 * Return the XML of the state of the Cube
	 * (ie fasdFile, MDX elements, cubeId)
	 */
	public RepositoryCubeView getView();
	
	/**
	 * set the view
	 * @param view
	 */
	public void setView(RepositoryCubeView view);
	
	/**
	 * LCA
	 * return true if some childs have been added
	 * @param memb
	 * @param h
	 * @throws Exception 
	 */
	public boolean addChilds(OLAPMember memb, Hierarchy h) throws Exception;
	
	/**
	 * return  MDX query for the current cube view  
	 * @return
	 * @throws Exception 
	 */
	public String getQuery() throws Exception;
	

	/**
	 * Replace in the current OLAPQuery the measure with the given name a measure using only 
	 * the Last member of the last level on the given TimeDimension
	 * The created measure will be names : Last measureName
	 * 
	 * To replace the created measure by its original measure,
	 * call unsetMeasureUseOnlyLastLevelMember(measureName) (measureName must the name of the measure
	 * present in the cube and not "Last measureName"
	 * 
	 * @param measureName
	 * @param timeHierarchyName: if null, the first Hierarchy will be used
	 * @param timeDimensionName 
	 * @throws Exception : if the measureName cannot be found in the cube structure,
	 * if the timeDimensionName dimension cannot be found in the cube,
	 * if the timeHierarchyName is not null and cannot be found  in its dimension
	 */
	public void setMeasureUseOnlyLastLevelMember(String measureName, String timeHierarchyName, String timeDimensionName) throws Exception;

	/**
	 * Add a topx
	 * @param topx the topx to add
	 */
	public void addTopx(Topx topx);

	/**
	 * Replace in the current OLAPQuery a measure defined as using only lastLevelMember
	 * created by setMeasureUseOnlyLastLevelMember(String measureName, String timeHierarchyName, String timeDimensionName) throws Exception;
	 * 
	 * 
	 * 
	 * @param originalMeasureName : the original Name of the measure from which is derived the last
	 * meaure taht your are currently replacing
	 * @throws Exception : if the measure named originalMeasureName cannot be found in the Cube 
	 */
	public void unsetMeasureUseOnlyLastLevelMember(String originalMeasureName) throws Exception;

	/**
	 * Remove a topx
	 * @param topx the topx to remove
	 */
	public void removeTopx(Topx topx);
	
	/**
	 * return topx list
	 * @return
	 */
	public List<Topx> getTopx();
	
	/**
	 * return personal names
	 * @return
	 */
	public HashMap<String, String> getPersonalNames();
	
	/**
	 * remove a personal name
	 * @param uname the uname of the personal name to remove
	 */
	public void removePersonalName(String uname);
	
	/**
	 * add a personal name
	 * @param uname the uname of the personal name to add
	 * @param pname the personal name corresponding to the uname
	 */
	public void addPersonalName(String uname, String pname);
	
	/**
	 * return all percent measures
	 * @return
	 */
	public HashMap<String, Boolean> getPercentMeasures();
	
	/**
	 * remove a percent measure
	 * @param measureName the measure uname
	 */
	public void removePercentMeasure(String measureName);

	/**
	 * add a percent measure
	 * @param measureName the measure uname
	 */
	public void addPercentMeasure(String measureName, boolean showMeasure);
	
	/**
	 * set if the cube show totals rows and cols
	 * @param showTotals
	 * 
	 * 
	 * 
	 */
	public void setShowTotals(boolean showTotals);
	
	/**
	 * return true if the cube show totals, return false instead
	 * @return
	 */
	public boolean isShowTotals();

	/**
	 * return a list of child elements
	 * @param uname
	 * @return
	 */
	public HashMap<String, String> findChildsForReporter(String uname);
	
	/**
	 * return the title of this report
	 * @return
	 */
	public String getReportTitle();
	
	/**
	 * set the title for this report
	 * @param reportTitle
	 */
	public void setReportTitle(String reportTitle);
	
	/**
	 * set the view prompts
	 * @param parameters
	 */
	public void setParameters(List<Parameter> parameters);
	
	/**
	 * get the view prompts
	 * @return
	 */
	public List<Parameter> getParameters();
	
	/**
	 * add a view prompt
	 * @param name
	 * @param value
	 */
	public void addParameter(String name, String value, String level);

	/**
	 * A method to find cube levels
	 * @return
	 */
	public LinkedHashMap<String, LinkedHashMap<String, String>> getLevels();

	/**
	 * Get a list of value for a level
	 * @param level
	 * @return
	 */
	public List<String> getParametersValues(String level);
	
	/**
	 * Search the word for the selected levels or in all dimensions if level is null
	 * @param word
	 * @param level
	 * @return
	 */
	public List<String> searchOnDimensions(String word, String level);
	
	/**
	 * Get the olapQuery
	 * @return the cube olapQuery
	 */
	public OLAPQuery getMdx();


	/**
	 * 
	 * @param uname
	 * @return the olapMember with the given uname
	 */
	public OLAPMember findOLAPMember(String uname);

	/**
	 * close the cube, all operation perfomed on the cube after this method has been called will fail
	 */
	public void close();



	public String getSchemaId();
	
	public List<Relation> getRelations();
	
	public void setProjection(Projection projection);
	
	public void setApplyProjection(boolean applyProjection);
	
	public OLAPResult getLastProjectionResult();
	
	public Projection createForecastData() throws Exception;
	
	public Member refreshTimeDimension() throws Exception;



	boolean canBeRemoved(ItemElement item) throws Exception;
	
	public List<Drill> getDrills();
	
	public void setSorting(HashMap<String, String> sortElements);
}
