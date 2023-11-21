package bpm.united.olap.runtime.model;

import java.util.List;

import bpm.united.olap.api.datasource.DataObject;
import bpm.united.olap.api.datasource.DataObjectItem;
import bpm.united.olap.api.datasource.Relation;

/**
 * Find the path between two dataObject
 * @author Marc Lanquetin
 *
 */
public interface PathFinder {

	List<Relation> findPath(DataObject table1, DataObject table2);

	/**
	 * Find columns used in relations for this dataObject
	 * @param parent
	 * @return
	 */
	List<DataObjectItem> findUsedColumns(DataObject parent);
	
}
