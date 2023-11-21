package bpm.united.olap.api.result;

import java.io.Serializable;
import java.util.List;

/**
 * Represents a light identifier to be added to valueResultCells.
 * @author Marc Lanquetin
 *
 */
public interface DrillThroughIdentifier extends Serializable{

	/**
	 * Unames for the intersection items
	 * @return
	 */
	List<String> getIntersections();
	
	/**
	 * Uname for the measure
	 * @return
	 */
	String getMeasure();
	
	/**
	 * Unames for the where elements
	 * @return
	 */
	List<String> getWheres();
	
	void setIntersections(List<String> intersections);
	
	void setMeasure(String measure);
	
	void setWheres(List<String> wheres);
}
