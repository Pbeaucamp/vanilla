/**
 * 
 */
package bpm.freemetrics.api.manager;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author ansybelgarde
 *
 */
public interface IDSManager {

	Connection getDefaultConnection() throws SQLException;

}
