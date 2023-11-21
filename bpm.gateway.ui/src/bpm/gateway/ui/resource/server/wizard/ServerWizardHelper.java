package bpm.gateway.ui.resource.server.wizard;

import java.util.Properties;

import bpm.gateway.core.IServerConnection;
import bpm.gateway.core.Server;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.core.server.d4c.D4CConnection;
import bpm.gateway.core.server.d4c.D4CServer;
import bpm.gateway.core.server.database.DataBaseConnection;
import bpm.gateway.core.server.database.DataBaseFactory;
import bpm.gateway.core.server.database.nosql.cassandra.CassandraConnection;
import bpm.gateway.core.server.database.nosql.cassandra.CassandraServer;
import bpm.gateway.core.server.database.nosql.hbase.HBaseConnection;
import bpm.gateway.core.server.database.nosql.hbase.HBaseServer;
import bpm.gateway.core.server.database.nosql.mongodb.MongoDbConnection;
import bpm.gateway.core.server.database.nosql.mongodb.MongoDbServer;
import bpm.gateway.core.server.file.VanillaFileServer;
import bpm.gateway.core.server.ldap.LdapConnection;
import bpm.gateway.core.server.ldap.LdapServer;

public class ServerWizardHelper {

	protected static Server createServer(ServerWizard wizard) throws ServerException {
		Properties generalProperties = ((ServerTypePage) wizard.getPage(ServerWizard.GENERAL_PAGE_NAME)).getValues();

		if (generalProperties.get("type").equals(Server.DATABASE_TYPE)) { //$NON-NLS-1$
			DataBaseConnection socket = ((ConnectionPage) wizard.getPage(ServerWizard.SQL_CONNECTION_PAGE_NAME)).getDataBaseConnection();

			return DataBaseFactory.create(generalProperties.getProperty("name"), generalProperties.getProperty("description"), socket); //$NON-NLS-1$ //$NON-NLS-2$
		}

		if (generalProperties.get("type").equals(Server.FREEMETRICS_TYPE)) { //$NON-NLS-1$

			String login = ((ConnectionPage) wizard.getPage(ServerWizard.SQL_CONNECTION_PAGE_NAME)).getFmLogin();
			String password = ((ConnectionPage) wizard.getPage(ServerWizard.SQL_CONNECTION_PAGE_NAME)).getFmPassword();

			generalProperties.setProperty("fmLogin", login); //$NON-NLS-1$
			generalProperties.setProperty("fmPassword", password); //$NON-NLS-1$

			DataBaseConnection socket = ((ConnectionPage) wizard.getPage(ServerWizard.SQL_CONNECTION_PAGE_NAME)).getDataBaseConnection();

			return DataBaseFactory.createFreemetrics(generalProperties.getProperty("fmLogin"), generalProperties.getProperty("fmPassword"), generalProperties.getProperty("name"), generalProperties.getProperty("description"), socket); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		}

		if (generalProperties.get("type").equals(Server.FILE_TYPE)) { //$NON-NLS-1$

			Properties repositoryProperties = ((RepositoryPage) wizard.getPage(ServerWizard.VANILLA_FILE_CONNECTION_PAGE_NAME)).getValues();
			VanillaFileServer server = new VanillaFileServer(generalProperties.getProperty("name"), //$NON-NLS-1$
					generalProperties.getProperty("description"), //$NON-NLS-1$
					repositoryProperties.getProperty("url"), //$NON-NLS-1$
					repositoryProperties.getProperty("login"), //$NON-NLS-1$
					repositoryProperties.getProperty("password"), //$NON-NLS-1$
					repositoryProperties.getProperty("repdefId")); //$NON-NLS-1$

			return server;

		}

		if (generalProperties.get("type").equals(Server.LDAP_TYPE)) { //$NON-NLS-1$
			Properties p = ((LdapPage) wizard.getPage(ServerWizard.LDAP_CONNECTION_PAGE_NAME)).getValues();

			LdapConnection sock = new LdapConnection();
			sock.setBase(p.getProperty("base")); //$NON-NLS-1$
			sock.setHost(p.getProperty("host")); //$NON-NLS-1$
			sock.setPort(p.getProperty("port")); //$NON-NLS-1$
			sock.setUserDn(p.getProperty("userDn")); //$NON-NLS-1$
			sock.setPassword(p.getProperty("password")); //$NON-NLS-1$
			sock.setName(p.getProperty("name")); //$NON-NLS-1$

			LdapServer server = new LdapServer();
			server.setName(generalProperties.getProperty("name")); //$NON-NLS-1$
			server.setDescription(generalProperties.getProperty("description")); //$NON-NLS-1$
			server.addConnection(sock);
			server.setCurrentConnection(sock);

			return server;
		}

		if (generalProperties.get("type").equals(Server.CASSANDRA_TYPE)) { //$NON-NLS-1$
			CassandraConnection p = ((CassandraConnectionPage) wizard.getPage(ServerWizard.CASSANDRA_CONNECTION_PAGE_NAME)).getConnection();

			CassandraServer server = new CassandraServer();
			server.setName(generalProperties.getProperty("name")); //$NON-NLS-1$
			server.setDescription(generalProperties.getProperty("description")); //$NON-NLS-1$
			server.setCurrentConnection(p);

			return server;
		}

		if (generalProperties.get("type").equals(Server.HBASE_TYPE)) { //$NON-NLS-1$
			HBaseConnection p = ((HBaseConnectionPage) wizard.getPage(ServerWizard.HBASE_CONNECTION_PAGE_NAME)).getConnection();

			HBaseServer server = new HBaseServer();
			server.setName(generalProperties.getProperty("name")); //$NON-NLS-1$
			server.setDescription(generalProperties.getProperty("description")); //$NON-NLS-1$
			server.setCurrentConnection(p);

			return server;
		}
		if (generalProperties.get("type").equals(Server.MONGODB_TYPE)) { //$NON-NLS-1$
			MongoDbConnection p = ((MongoDbConnectionPage) wizard.getPage(ServerWizard.MONGODB_CONNECTION_PAGE_NAME)).getConnection();

			MongoDbServer server = new MongoDbServer();
			server.setName(generalProperties.getProperty("name")); //$NON-NLS-1$
			server.setDescription(generalProperties.getProperty("description")); //$NON-NLS-1$
			server.setCurrentConnection(p);

			return server;
		}
		if (generalProperties.get("type").equals(Server.D4C_TYPE)) { //$NON-NLS-1$
			D4CConnection p = ((D4CConnectionPage) wizard.getPage(ServerWizard.D4C_CONNECTION_PAGE_NAME)).getConnection();

			D4CServer server = new D4CServer();
			server.setName(generalProperties.getProperty("name")); //$NON-NLS-1$
			server.setDescription(generalProperties.getProperty("description")); //$NON-NLS-1$
			server.setCurrentConnection(p);

			return server;
		}

		return null;

	}

	protected static IServerConnection createConnection(ServerWizard wizard) {
		DataBaseConnection socket = ((ConnectionPage) wizard.getPage(ServerWizard.SQL_CONNECTION_PAGE_NAME)).getDataBaseConnection();
		return socket;
	}
}
