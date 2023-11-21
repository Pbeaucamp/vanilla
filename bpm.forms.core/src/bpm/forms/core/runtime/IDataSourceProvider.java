package bpm.forms.core.runtime;

import javax.sql.DataSource;

/**
 * simple interface to access the DataSource that will store the submited values 
 * @author ludo
 *
 */
public interface IDataSourceProvider {
	public DataSource getDataSource();
}
