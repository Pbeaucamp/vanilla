package bpm.metadata.layer.physical;

import java.util.List;

public interface ITable {
	
	public String getNameWithoutShema();

	/**
	 * return the columns of the ITable
	 * @return
	 */
	public List<IColumn> getColumns();
	
	/**
	 * return the Physical tableName
	 * @return
	 */
	public String getName();
	
	
	/**
	 * get the connection
	 * @return
	 */
	public IConnection getConnection();

	public IColumn getElementNamed(String originName);

	public boolean isQuery();
}
