package org.fasd.aggwizard;

import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.jface.action.Action;
import org.fasd.datasource.DataObject;
import org.fasd.datasource.DataSourceConnection;

import bpm.studio.jdbc.management.config.IConstants;
import bpm.studio.jdbc.management.model.DriverInfo;
import bpm.studio.jdbc.management.model.ListDriver;

public class KettleAction extends Action {
	private String fileName;
	private String insert;
	private DataObject destination;

	public KettleAction(String fileName, String insert, DataObject destination) {
		this.fileName = fileName;
		this.insert = insert;
		this.destination = destination;

	}

	public void run() {

		DataSourceConnection sock = destination.getDataSource().getDriver();
		int i = sock.getUrl().indexOf("://") + 3; //$NON-NLS-1$
		sock.getUrl().substring(i, sock.getUrl().indexOf(":", i + 1)); //$NON-NLS-1$
		String type = null;

		// look for a match with the driver Name
		try {
			for (DriverInfo di : ListDriver.getInstance(IConstants.getJdbcDriverXmlFile()).getDriversInfo()) {
				if (!di.getUrlPrefix().equals("") && sock.getUrl().startsWith(di.getUrlPrefix())) { //$NON-NLS-1$
					type = di.getName();
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		String host = sock.getUrl().substring(i, sock.getUrl().indexOf(":", i + 1)); //$NON-NLS-1$
		String port = sock.getUrl().substring(sock.getUrl().indexOf(":", i + 1)); //$NON-NLS-1$
		String database = port.substring(port.indexOf("/") + 1); //$NON-NLS-1$
		port = port.substring(1, port.indexOf("/")); //$NON-NLS-1$

		String username = sock.getUser();
		String password = sock.getPass();

		String select = insert.substring(insert.indexOf("SELECT")); //$NON-NLS-1$

		StringBuffer buf = new StringBuffer();

		buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"); //$NON-NLS-1$
		buf.append("<transformation>\n"); //$NON-NLS-1$
		buf.append("  <info>\n"); //$NON-NLS-1$
		buf.append("    <name>TransfoKettle</name>\n"); //$NON-NLS-1$
		buf.append("    <description/>\n"); //$NON-NLS-1$
		buf.append("    <extended_description/>\n"); //$NON-NLS-1$
		buf.append("    <trans_version/>\n"); //$NON-NLS-1$
		buf.append("    <directory>&#47;</directory>\n"); //$NON-NLS-1$
		buf.append("    <log>\n"); //$NON-NLS-1$
		buf.append("      <read/>\n"); //$NON-NLS-1$
		buf.append("      <write/>\n"); //$NON-NLS-1$
		buf.append("      <input/>\n"); //$NON-NLS-1$
		buf.append("      <output/>\n"); //$NON-NLS-1$
		buf.append("      <update/>\n"); //$NON-NLS-1$
		buf.append("      <rejected/>\n"); //$NON-NLS-1$
		buf.append("      <connection/>\n"); //$NON-NLS-1$
		buf.append("      <table/>\n"); //$NON-NLS-1$
		buf.append("      <use_batchid>Y</use_batchid>\n"); //$NON-NLS-1$
		buf.append("      <use_logfield>N</use_logfield>\n"); //$NON-NLS-1$
		buf.append("    </log>\n"); //$NON-NLS-1$
		buf.append("    <maxdate>\n"); //$NON-NLS-1$
		buf.append("      <connection/>\n"); //$NON-NLS-1$
		buf.append("      <table/>\n"); //$NON-NLS-1$
		buf.append("      <field/>\n"); //$NON-NLS-1$
		buf.append("      <offset>0.0</offset>\n"); //$NON-NLS-1$
		buf.append("      <maxdiff>0.0</maxdiff>\n"); //$NON-NLS-1$
		buf.append("    </maxdate>\n"); //$NON-NLS-1$
		buf.append("    <size_rowset>10000</size_rowset>\n"); //$NON-NLS-1$
		buf.append("    <sleep_time_empty>50</sleep_time_empty>\n"); //$NON-NLS-1$
		buf.append("    <sleep_time_full>50</sleep_time_full>\n"); //$NON-NLS-1$
		buf.append("    <unique_connections>N</unique_connections>\n"); //$NON-NLS-1$
		buf.append("    <feedback_shown>Y</feedback_shown>\n"); //$NON-NLS-1$
		buf.append("    <feedback_size>50000</feedback_size>\n"); //$NON-NLS-1$
		buf.append("    <using_thread_priorities>N</using_thread_priorities>\n"); //$NON-NLS-1$
		buf.append("    <shared_objects_file/>\n"); //$NON-NLS-1$
		buf.append("    <dependencies>\n"); //$NON-NLS-1$
		buf.append("    </dependencies>\n"); //$NON-NLS-1$
		buf.append("    <partitionschemas>\n"); //$NON-NLS-1$
		buf.append("    </partitionschemas>\n"); //$NON-NLS-1$
		buf.append("    <slaveservers>\n"); //$NON-NLS-1$
		buf.append("    </slaveservers>\n"); //$NON-NLS-1$
		buf.append("    <clusterschemas>\n"); //$NON-NLS-1$
		buf.append("    </clusterschemas>\n"); //$NON-NLS-1$
		buf.append("  <modified_user>-</modified_user>\n"); //$NON-NLS-1$
		buf.append("  <modified_date>2008&#47;09&#47;23 11:52:39.776</modified_date>\n"); //$NON-NLS-1$
		buf.append("  </info>\n"); //$NON-NLS-1$
		buf.append("  <notepads>\n"); //$NON-NLS-1$
		buf.append("  </notepads>\n"); //$NON-NLS-1$
		buf.append("  <connection>\n"); //$NON-NLS-1$
		buf.append("    <name>fasdConnection</name>\n"); //$NON-NLS-1$
		buf.append("    <server>" + host + "</server>\n"); //$NON-NLS-1$ //$NON-NLS-2$
		buf.append("    <type>" + type + "</type>\n"); //$NON-NLS-1$ //$NON-NLS-2$
		buf.append("    <access>Native</access>\n"); //$NON-NLS-1$
		buf.append("    <database>" + database + "</database>\n"); //$NON-NLS-1$ //$NON-NLS-2$
		buf.append("    <port>" + port + "</port>\n"); //$NON-NLS-1$ //$NON-NLS-2$
		buf.append("    <username>" + username + "</username>\n"); //$NON-NLS-1$ //$NON-NLS-2$
		buf.append("    <password>" + password + "</password>\n"); //$NON-NLS-1$ //$NON-NLS-2$
		buf.append("    <servername/>\n"); //$NON-NLS-1$
		buf.append("    <data_tablespace/>\n"); //$NON-NLS-1$
		buf.append("    <index_tablespace/>\n"); //$NON-NLS-1$
		buf.append("    <attributes>\n"); //$NON-NLS-1$
		buf.append("      <attribute><code>EXTRA_OPTION_MYSQL.defaultFetchSize</code><attribute>500</attribute></attribute>\n"); //$NON-NLS-1$
		buf.append("      <attribute><code>EXTRA_OPTION_MYSQL.useCursorFetch</code><attribute>true</attribute></attribute>\n"); //$NON-NLS-1$
		buf.append("      <attribute><code>FORCE_IDENTIFIERS_TO_LOWERCASE</code><attribute>N</attribute></attribute>\n"); //$NON-NLS-1$
		buf.append("      <attribute><code>FORCE_IDENTIFIERS_TO_UPPERCASE</code><attribute>N</attribute></attribute>\n"); //$NON-NLS-1$
		buf.append("      <attribute><code>INITIAL_POOL_SIZE</code><attribute>5</attribute></attribute>\n"); //$NON-NLS-1$
		buf.append("      <attribute><code>IS_CLUSTERED</code><attribute>N</attribute></attribute>\n"); //$NON-NLS-1$
		buf.append("      <attribute><code>MAXIMUM_POOL_SIZE</code><attribute>10</attribute></attribute>\n"); //$NON-NLS-1$
		buf.append("      <attribute><code>MSSQL_DOUBLE_DECIMAL_SEPARATOR</code><attribute>N</attribute></attribute>\n"); //$NON-NLS-1$
		buf.append("      <attribute><code>PORT_NUMBER</code><attribute>3306</attribute></attribute>\n"); //$NON-NLS-1$
		buf.append("      <attribute><code>QUOTE_ALL_FIELDS</code><attribute>N</attribute></attribute>\n"); //$NON-NLS-1$
		buf.append("      <attribute><code>STREAM_RESULTS</code><attribute>Y</attribute></attribute>\n"); //$NON-NLS-1$
		buf.append("      <attribute><code>USE_POOLING</code><attribute>N</attribute></attribute>\n"); //$NON-NLS-1$
		buf.append("    </attributes>\n"); //$NON-NLS-1$
		buf.append("  </connection>\n"); //$NON-NLS-1$
		buf.append("  <order>\n"); //$NON-NLS-1$
		buf.append("  <hop> <from>Extraction depuis table</from><to>Insertion dans table</to><enabled>Y</enabled> </hop>  <hop> <from>Supression contenu</from><to>Extraction depuis table</to><enabled>Y</enabled> </hop>  </order>\n"); //$NON-NLS-1$
		buf.append("  <step>\n"); //$NON-NLS-1$
		buf.append("    <name>Extraction depuis table</name>\n"); //$NON-NLS-1$
		buf.append("    <type>TableInput</type>\n"); //$NON-NLS-1$
		buf.append("    <description/>\n"); //$NON-NLS-1$
		buf.append("    <distribute>Y</distribute>\n"); //$NON-NLS-1$
		buf.append("    <copies>1</copies>\n"); //$NON-NLS-1$
		buf.append("         <partitioning>\n"); //$NON-NLS-1$
		buf.append("           <method>none</method>\n"); //$NON-NLS-1$
		buf.append("           <schema_name/>\n"); //$NON-NLS-1$
		buf.append("           </partitioning>\n"); //$NON-NLS-1$
		buf.append("    <connection>fasdConnection</connection>\n"); //$NON-NLS-1$
		buf.append("    <sql>" + select.replace(">", "&gt;").replace("<", "&lt;") + "</sql>\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
		buf.append("    <limit>0</limit>\n"); //$NON-NLS-1$
		buf.append("    <lookup/>\n"); //$NON-NLS-1$
		buf.append("    <execute_each_row>N</execute_each_row>\n"); //$NON-NLS-1$
		buf.append("    <variables_active>N</variables_active>\n"); //$NON-NLS-1$
		buf.append("    <lazy_conversion_active>N</lazy_conversion_active>\n"); //$NON-NLS-1$
		buf.append("     <cluster_schema/>\n"); //$NON-NLS-1$
		buf.append(" <remotesteps>   <input>   </input>   <output>   </output> </remotesteps>    <GUI>\n"); //$NON-NLS-1$
		buf.append("      <xloc>152</xloc>\n"); //$NON-NLS-1$
		buf.append("      <yloc>212</yloc>\n"); //$NON-NLS-1$
		buf.append("      <draw>Y</draw>\n"); //$NON-NLS-1$
		buf.append("      </GUI>\n"); //$NON-NLS-1$
		buf.append("    </step>\n"); //$NON-NLS-1$

		buf.append("  <step>\n"); //$NON-NLS-1$
		buf.append("    <name>Insertion dans table</name>\n"); //$NON-NLS-1$
		buf.append("    <type>TableOutput</type>\n"); //$NON-NLS-1$
		buf.append("    <description/>\n"); //$NON-NLS-1$
		buf.append("    <distribute>Y</distribute>\n"); //$NON-NLS-1$
		buf.append("    <copies>1</copies>\n"); //$NON-NLS-1$
		buf.append("         <partitioning>\n"); //$NON-NLS-1$
		buf.append("           <method>none</method>\n"); //$NON-NLS-1$
		buf.append("           <schema_name/>\n"); //$NON-NLS-1$
		buf.append("           </partitioning>\n"); //$NON-NLS-1$
		buf.append("    <connection>fasdConnection</connection>\n"); //$NON-NLS-1$
		buf.append("    <schema/>\n"); //$NON-NLS-1$
		buf.append("    <table>" + destination.getShortName() + "</table>\n"); //$NON-NLS-1$ //$NON-NLS-2$
		buf.append("    <commit>100</commit>\n"); //$NON-NLS-1$
		buf.append("    <truncate>N</truncate>\n"); //$NON-NLS-1$
		buf.append("    <ignore_errors>N</ignore_errors>\n"); //$NON-NLS-1$
		buf.append("    <use_batch>Y</use_batch>\n"); //$NON-NLS-1$
		buf.append("    <partitioning_enabled>N</partitioning_enabled>\n"); //$NON-NLS-1$
		buf.append("    <partitioning_field/>\n"); //$NON-NLS-1$
		buf.append("    <partitioning_daily>N</partitioning_daily>\n"); //$NON-NLS-1$
		buf.append("    <partitioning_monthly>Y</partitioning_monthly>\n"); //$NON-NLS-1$
		buf.append("    <tablename_in_field>N</tablename_in_field>\n"); //$NON-NLS-1$
		buf.append("    <tablename_field/>\n"); //$NON-NLS-1$
		buf.append("    <tablename_in_table>Y</tablename_in_table>\n"); //$NON-NLS-1$
		buf.append("    <return_keys>N</return_keys>\n"); //$NON-NLS-1$
		buf.append("    <return_field/>\n"); //$NON-NLS-1$
		buf.append("     <cluster_schema/>\n"); //$NON-NLS-1$
		buf.append(" <remotesteps>   <input>   </input>   <output>   </output> </remotesteps>    <GUI>\n"); //$NON-NLS-1$
		buf.append("      <xloc>291</xloc>\n"); //$NON-NLS-1$
		buf.append("      <yloc>320</yloc>\n"); //$NON-NLS-1$
		buf.append("      <draw>Y</draw>\n"); //$NON-NLS-1$
		buf.append("      </GUI>\n"); //$NON-NLS-1$
		buf.append("    </step>\n"); //$NON-NLS-1$

		buf.append("  <step>\n"); //$NON-NLS-1$
		buf.append("    <name>Supression contenu</name>\n"); //$NON-NLS-1$
		buf.append("    <type>ExecSQL</type>\n"); //$NON-NLS-1$
		buf.append("    <description/>\n"); //$NON-NLS-1$
		buf.append("    <distribute>Y</distribute>\n"); //$NON-NLS-1$
		buf.append("    <copies>1</copies>\n"); //$NON-NLS-1$
		buf.append("         <partitioning>\n"); //$NON-NLS-1$
		buf.append("           <method>none</method>\n"); //$NON-NLS-1$
		buf.append("           <schema_name/>\n"); //$NON-NLS-1$
		buf.append("           </partitioning>\n"); //$NON-NLS-1$
		buf.append("    <connection>fasdConnection</connection>\n"); //$NON-NLS-1$
		buf.append("    <execute_each_row>N</execute_each_row>\n"); //$NON-NLS-1$
		buf.append("    <replace_variables>N</replace_variables>\n"); //$NON-NLS-1$
		buf.append("    <sql>DELETE FROM " + destination.getShortName() + "</sql>\n"); //$NON-NLS-1$ //$NON-NLS-2$
		buf.append("    <insert_field/>\n"); //$NON-NLS-1$
		buf.append("    <update_field/>\n"); //$NON-NLS-1$
		buf.append("    <delete_field/>\n"); //$NON-NLS-1$
		buf.append("    <read_field/>\n"); //$NON-NLS-1$
		buf.append("    <arguments>\n"); //$NON-NLS-1$
		buf.append("    </arguments>\n"); //$NON-NLS-1$
		buf.append("     <cluster_schema/>\n"); //$NON-NLS-1$
		buf.append(" <remotesteps>   <input>   </input>   <output>   </output> </remotesteps>    <GUI>\n"); //$NON-NLS-1$
		buf.append("      <xloc>285</xloc>\n"); //$NON-NLS-1$
		buf.append("      <yloc>73</yloc>\n"); //$NON-NLS-1$
		buf.append("      <draw>Y</draw>\n"); //$NON-NLS-1$
		buf.append("      </GUI>\n"); //$NON-NLS-1$
		buf.append("    </step>\n"); //$NON-NLS-1$

		buf.append("  <step_error_handling>\n"); //$NON-NLS-1$
		buf.append("  </step_error_handling>\n"); //$NON-NLS-1$
		buf.append("   <slave-step-copy-partition-distribution>\n"); //$NON-NLS-1$
		buf.append("</slave-step-copy-partition-distribution>\n"); //$NON-NLS-1$
		buf.append("   <slave_transformation>N</slave_transformation>\n"); //$NON-NLS-1$
		buf.append("</transformation>"); //$NON-NLS-1$

		FileWriter fw = null;
		try {
			if (fileName.endsWith(".ktr")) { //$NON-NLS-1$
				fw = new FileWriter(fileName);
			} else {
				fw = new FileWriter(fileName + ".ktr"); //$NON-NLS-1$
			}

			fw.write(buf.toString());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fw != null) {
				try {
					fw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}
}
