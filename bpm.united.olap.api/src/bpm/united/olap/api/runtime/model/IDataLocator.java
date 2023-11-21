package bpm.united.olap.api.runtime.model;

import java.util.List;

import bpm.united.olap.api.datasource.DataObject;
import bpm.united.olap.api.datasource.DataObjectItem;
import bpm.united.olap.api.datasource.Relation;
import bpm.united.olap.api.model.Level;
import bpm.united.olap.api.model.Measure;
import bpm.united.olap.api.model.Member;

/**
 * IDataLocator must be able to find the Index from a factTable Result set for a Measure and a Member
 * 
 * It is used at runtime when reading teh factTable ResultSets to extract the Datas that are
 * requested for the DataStorage's Cells.
 * 
 * 
 * @author ludo
 *
 */
public interface IDataLocator {

	/**
	 * 
	 * @param measure
	 * @return the index in the FactTableResultSet for the given Measure or null
	 */
	public Integer getResultSetIndex(Measure measure);
	
	/**
	 * 
	 * @param member
	 * @return the index in the FactTableResultSet for the given Member
	 */
	public Integer getResultSetIndex(Member member);
	/**
	 * 
	 * @param member
	 * @return the index in the FactTableResultSet(improved) for the given Member and the given memberLevel
	 * @throws Exception 
	 */
	public Integer getResultSetIndex(Member member, int levelIndex) throws Exception;
	
	public Integer getResultSetIndexInFactTable(DataObjectItem origin);

	public Integer getResultSetIndex(Level level);
	
	public Integer getOrderResultSetIndex(Level level);

	public DataObjectItem getRelatedDataObjectItem(DataObjectItem factForeignKey, Member member) throws Exception;

	public List<Relation> getPath(DataObject parent, DataObject relatedObject) throws Exception;

	
}
