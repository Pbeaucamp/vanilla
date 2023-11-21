package bpm.united.olap.api.data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * Represents an external (for FMDT or ODA)
 * @author Marc Lanquetin
 *
 */
public interface IExternalQueryIdentifier extends Serializable {

	/**
	 * Can contains level or measure unames
	 * @return
	 */
	public HashMap<String, String> getSelectElements();
	
	/**
	 * Can contains Member unames
	 * @return
	 */
	public List<String> getWhereElements();
	
}
