package bpm.vanilla.platform.core.beans.report;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * a simple Class to hold possible Connection for DataSources
 * 
 * ere IMPORTANt update : the datasource are not the datasource definitions in 
 * fmdt but of FWR for example 
 * 
 * @author ludo
 *
 */
public class AlternateDataSourceHolder implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private HashMap<String, List<String>> holder = new HashMap<String, List<String>>();
	
	public AlternateDataSourceHolder(){}
	
	public AlternateDataSourceHolder(HashMap<String, List<String>> holder){ this.holder = holder;}
	
	public List<String> getDataSourceNames(){
		List<String> l = new ArrayList<String>(holder.keySet());
		Collections.sort(l);
		return l;
	}
	
	public List<String> getAlternateConnections(String dataSourceName){
		if (holder.get(dataSourceName) == null){
			return new ArrayList<String>();
		}
		List<String> l = new ArrayList<String>(holder.get(dataSourceName));
		Collections.sort(l);
		return l;
	}
	
	public boolean isEmpty(){
		return holder.keySet().isEmpty();
	}
	
	public int getCount() {
		return holder.keySet().size();
	}
	
	/**
	 * needed by ere to extract and transform this in the portal
	 * @return
	 */
	public HashMap<String, List<String>> getInternalDataHolder() {
		return holder;
	}
}
