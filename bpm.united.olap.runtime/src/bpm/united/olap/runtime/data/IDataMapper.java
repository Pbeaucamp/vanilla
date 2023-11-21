package bpm.united.olap.runtime.data;

import java.util.List;

import org.eclipse.datatools.connectivity.oda.IResultSet;
import org.eclipse.datatools.connectivity.oda.OdaException;

import bpm.united.olap.api.model.Member;
import bpm.united.olap.api.runtime.DataCell;
import bpm.united.olap.api.runtime.DataCellIdentifier2;

/**
 * Used to map a data in the dataStorage 
 * @author Marc Lanquetin
 *
 */
public interface IDataMapper {

	/**
	 * Map data for a possibleId
	 * @param rs
	 * @param possibleId
	 * @throws Exception 
	 */
	DataCell mapData(IResultSet rs, DataCellIdentifier2 possibleId) throws Exception;

	boolean checkWhereClause(IResultSet rs, List<Member> wheres) throws OdaException, Exception;

	int compareMemberWithResultSetValue(Member actualMember, int i, IResultSet rs) throws Exception;

	int compareClosureMemberWithResultSetValue(Member actualMember, IResultSet rs, int levelIndex) throws Exception;
	
	
	
}
