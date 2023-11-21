package bpm.fa.api.olap;

import java.util.ArrayList;
import java.util.List;

import org.fasd.olap.ICubeView;

import bpm.united.olap.api.runtime.IRuntimeContext;

/**
 * 
 * @author ereynard
 *
 * type :
 * 	- 1 : load on startup (TODO)
 *  - 2 : load on connection
 *  - 3 : load on query (TODO)
 *  
 *  TODO :
 *  	- HashMap for dimensions/measures
 */
public interface OLAPStructure {
	

	/**
	 * 
	 * @param name path to fasd file
	 * @return OLAPCube
	 * TODO : load on *
	 * TODO : roles
	 */
	public OLAPCube createCube(String path, IRuntimeContext runtimeContext) throws Exception;
	
	/**
	 * 
	 * @param name path to fasd file
	 * @param cubeName name of the cube
	 * @return OLAPCube
	 * TODO : load on *
	 * TODO : roles
	 */
	public OLAPCube createCube(String path, List<ICubeView> lstCubeViews, IRuntimeContext runtimeContext) throws Exception;
	
	/**
	 * 
	 * @return
	 */
	public String getCubeName();
	
	/**
	 *
	 * @return the Dimensions of the cube (without the measures)
	 */
	public ArrayList<Dimension> getDimensions();

	/**
	 *
	 * @return returns the Measures group of the cube
	 */
	public ArrayList<MeasureGroup> getMeasures();
	
	
	public List<Measure> getAllMeasures();
	
	/**
	 * Executes specified query
	 * @param str Query to execute
	 * @param showProps Do we show properties
	 * @return {@link OLAPResult} query result
	 */
	public OLAPResult executeQuery(OLAPCube cube, String str, boolean showProps, IRuntimeContext runtimeContext);
	
	/**
	 * 
	 * @param memb
	 * @return true if some childs have been added to memb
	 */
	public boolean addChilds(OLAPMember memb, IRuntimeContext ctx) throws Exception;

	public OLAPMember getOLAPMember(String uname);

	public OLAPCube createCube(IRuntimeContext runtimeContext) throws Exception;
	
}
