package bpm.united.olap.runtime.data;

import java.util.HashMap;
import java.util.List;

import bpm.united.olap.api.runtime.DataCell;
import bpm.united.olap.api.runtime.DataCellIdentifier2;
import bpm.united.olap.api.runtime.DataStorage;
import bpm.united.olap.api.runtime.RunResult;
import bpm.vanilla.platform.logging.IVanillaLogger;

/**
 * Used to generate an object which contains the value cells
 * @author Marc Lanquetin
 *
 */
public interface IDataStorageGenerator {

	/**
	 * Generate the data object from axis and slicer
	 * @param result
	 * @return a dataStorage item
	 */
	DataStorage generateDataStorage(RunResult result, IVanillaLogger logger);
	
	HashMap<DataCellIdentifier2, List<DataCellIdentifier2>> createPossibleId(List<DataCell> cells);

	int findNbCol(List<DataCell> dataCells);
}
