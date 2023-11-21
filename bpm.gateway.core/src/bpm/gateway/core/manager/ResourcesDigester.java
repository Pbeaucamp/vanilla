package bpm.gateway.core.manager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.digester.Digester;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import bpm.gateway.core.Server;
import bpm.gateway.core.server.d4c.D4CConnection;
import bpm.gateway.core.server.d4c.D4CServer;
import bpm.gateway.core.server.database.DataBaseConnection;
import bpm.gateway.core.server.database.DataBaseServer;
import bpm.gateway.core.server.database.nosql.cassandra.CassandraConnection;
import bpm.gateway.core.server.database.nosql.cassandra.CassandraServer;
import bpm.gateway.core.server.database.nosql.hbase.HBaseConnection;
import bpm.gateway.core.server.database.nosql.hbase.HBaseServer;
import bpm.gateway.core.server.database.nosql.mongodb.MongoDbConnection;
import bpm.gateway.core.server.database.nosql.mongodb.MongoDbServer;
import bpm.gateway.core.server.ldap.LdapConnection;
import bpm.gateway.core.server.ldap.LdapServer;
import bpm.gateway.core.server.userdefined.Variable;



/**
 * This class can read a xml doc and restore a GatewayDocument
 * @author LCA
 *
 */
public class ResourcesDigester {
	private class MyErrorHandler implements ErrorHandler{

		public void error(SAXParseException arg0) throws SAXException {
			arg0.printStackTrace();
			throw new SAXException("erreur de parse", arg0);
		}

		public void fatalError(SAXParseException arg0) throws SAXException {
			arg0.printStackTrace();
			throw new SAXException("fatal error", arg0);
		}


		public void warning(SAXParseException arg0) throws SAXException {
			arg0.printStackTrace();
			throw new SAXException("warning", arg0);
		}
		
	}

	private Digester dig;
	private List<Server> servers; 
	
	public ResourcesDigester(File file) throws IOException, SAXException{
		dig = new Digester();
		dig.setErrorHandler(new MyErrorHandler());
		createCallbacks();
		servers = (List<Server>)dig.parse(file);
		
	}
	
	
	public List<Server> getServers(){
		return servers;
	}
	
	public ResourcesDigester(InputStream inputStream) throws IOException, SAXException{
		dig = new Digester();
		dig.setErrorHandler(new MyErrorHandler());
		createCallbacks();
		
		servers = (List<Server>)dig.parse(inputStream);
		
	}
	
	
	
	private void createCallbacks(){
		dig.setValidating(false);
		
		String root = "sharedResources";
		
		dig.addObjectCreate(root, ArrayList.class);
		
		/*
		 * DataBase server and connections
		 */
		dig.addObjectCreate(root + "/dataBaseServer", DataBaseServer.class);
		dig.addCallMethod(root + "/dataBaseServer/name", "setName", 0);
		dig.addCallMethod(root + "/dataBaseServer/description", "setDescription", 0);
			dig.addObjectCreate(root + "/dataBaseServer/dataBaseConnection", DataBaseConnection.class);
			dig.addCallMethod(root + "/dataBaseServer/dataBaseConnection/name", "setName", 0);
			dig.addCallMethod(root + "/dataBaseServer/dataBaseConnection/description", "setDescription", 0);
			dig.addCallMethod(root + "/dataBaseServer/dataBaseConnection/host", "setHost", 0);
			dig.addCallMethod(root + "/dataBaseServer/dataBaseConnection/port", "setPort", 0);
			dig.addCallMethod(root + "/dataBaseServer/dataBaseConnection/dataBaseName", "setDataBaseName", 0);
			dig.addCallMethod(root + "/dataBaseServer/dataBaseConnection/login", "setLogin", 0);
			dig.addCallMethod(root + "/dataBaseServer/dataBaseConnection/password", "setPassword", 0);
			dig.addCallMethod(root + "/dataBaseServer/dataBaseConnection/driverName", "setDriverName", 0);
			
			dig.addCallMethod(root + "/dataBaseServer/dataBaseConnection/useFullUrl", "setUseFullUrl", 0);
			dig.addCallMethod(root + "/dataBaseServer/dataBaseConnection/fullUrl", "setFullUrl", 0);
		
			
			dig.addSetNext(root + "/dataBaseServer/dataBaseConnection", "addConnection");
		dig.addSetNext(root + "/dataBaseServer", "add");
		
//		/*
//		 * vanillaServer
//		 */
//		dig.addObjectCreate(root + "/vanillaServer", VanillaServer.class);
//		dig.addCallMethod(root + "/vanillaServer/name", "setName", 0);
//		dig.addCallMethod(root + "/vanillaServer/description", "setDescription", 0);
//		dig.addCallMethod(root + "/vanillaServer/vanillaConnection/login", "setLogin", 0);
//		dig.addCallMethod(root + "/vanillaServer/vanillaConnection/password", "setPassword", 0);
//		dig.addCallMethod(root + "/vanillaServer/vanillaConnection/url", "setUrl", 0);
//		dig.addSetNext(root + "/vanillaServer", "add");
		
//		/*
//		 * RepositoryServer
//		 */
//		dig.addObjectCreate(root + "/repositoryServer", RepositoryServer.class);
//		dig.addCallMethod(root + "/repositoryServer/name", "setName", 0);
//		dig.addCallMethod(root + "/repositoryServer/description", "setDescription", 0);
//		dig.addCallMethod(root + "/repositoryServer/repositoryConnection/login", "setLogin", 0);
//		dig.addCallMethod(root + "/repositoryServer/repositoryConnection/password", "setPassword", 0);
//		dig.addCallMethod(root + "/repositoryServer/repositoryConnection/url", "setUrl", 0);
//		dig.addSetNext(root + "/repositoryServer", "add");
		
//		s
		
		/*
		 * LDAP server and connections
		 */
		dig.addObjectCreate(root + "/ldapServer", LdapServer.class);
		dig.addCallMethod(root + "/ldapServer/name", "setName", 0);
		dig.addCallMethod(root + "/ldapServer/description", "setDescription", 0);
			dig.addObjectCreate(root + "/ldapServer/ldapConnection", LdapConnection.class);
			dig.addCallMethod(root + "/ldapServer/ldapConnection/name", "setName", 0);
			dig.addCallMethod(root + "/ldapServer/ldapConnection/description", "setDescription", 0);
			dig.addCallMethod(root + "/ldapServer/ldapConnection/host", "setHost", 0);
			dig.addCallMethod(root + "/ldapServer/ldapConnection/port", "setPort", 0);
			dig.addCallMethod(root + "/ldapServer/ldapConnection/base", "setBase", 0);
			dig.addCallMethod(root + "/ldapServer/ldapConnection/userDn", "setUserDn", 0);
			dig.addCallMethod(root + "/ldapServer/ldapConnection/password", "setPassword", 0);
			dig.addSetNext(root + "/ldapServer/ldapConnection", "addConnection");
		dig.addSetNext(root + "/ldapServer", "add");
		
		/*
		 * Cassandra server and connections
		 */
		dig.addObjectCreate(root + "/cassandraServer", CassandraServer.class);
		dig.addCallMethod(root + "/cassandraServer/name", "setName", 0);
		dig.addCallMethod(root + "/cassandraServer/description", "setDescription", 0);
			dig.addObjectCreate(root + "/cassandraServer/cassandraConnection", CassandraConnection.class);
			dig.addCallMethod(root + "/cassandraServer/cassandraConnection/name", "setName", 0);
			dig.addCallMethod(root + "/cassandraServer/cassandraConnection/host", "setHost", 0);
			dig.addCallMethod(root + "/cassandraServer/cassandraConnection/port", "setPort", 0);
			dig.addCallMethod(root + "/cassandraServer/cassandraConnection/keyspace", "setKeyspace", 0);
			dig.addCallMethod(root + "/cassandraServer/cassandraConnection/login", "setUsername", 0);
			dig.addCallMethod(root + "/cassandraServer/cassandraConnection/password", "setPassword", 0);
			dig.addSetNext(root + "/cassandraServer/cassandraConnection", "setCurrentConnection");
		dig.addSetNext(root + "/cassandraServer", "add");
		
		/*
		 * HBase server and connections
		 */
		dig.addObjectCreate(root + "/hbaseServer", HBaseServer.class);
		dig.addCallMethod(root + "/hbaseServer/name", "setName", 0);
		dig.addCallMethod(root + "/hbaseServer/description", "setDescription", 0);
			dig.addObjectCreate(root + "/hbaseServer/hbaseConnection", HBaseConnection.class);
			dig.addCallMethod(root + "/hbaseServer/hbaseConnection/name", "setName", 0);
			dig.addCallMethod(root + "/hbaseServer/hbaseConnection/configurationFile", "setConfigurationFileUrl", 0);
//			dig.addCallMethod(root + "/hbaseServer/hbaseConnection/login", "setLogin", 0);
//			dig.addCallMethod(root + "/hbaseServer/hbaseConnection/password", "setPassword", 0);
			dig.addSetNext(root + "/hbaseServer/hbaseConnection", "setCurrentConnection");
		dig.addSetNext(root + "/hbaseServer", "add");
		
		/*
		 * D4C server and connections
		 */
		dig.addObjectCreate(root + "/servers/d4cServer", D4CServer.class);
		dig.addCallMethod(root + "/servers/d4cServer/name", "setName", 0);
		dig.addCallMethod(root + "/servers/d4cServer/description", "setDescription", 0);
			dig.addObjectCreate(root + "/servers/d4cServer/d4cConnection", D4CConnection.class);
			dig.addCallMethod(root + "/servers/d4cServer/d4cConnection/name", "setName", 0);
			dig.addCallMethod(root + "/servers/d4cServer/d4cConnection/url", "setUrl", 0);
			dig.addCallMethod(root + "/servers/d4cServer/d4cConnection/org", "setOrg", 0);
			dig.addCallMethod(root + "/servers/d4cServer/d4cConnection/apiKey", "setApiKey", 0);
			dig.addSetNext(root + "/servers/d4cServer/d4cConnection", "setCurrentConnection");
		dig.addSetNext(root + "/servers/d4cServer", "addServer");
		
		/*
		 * Mongo server and connections
		 */
		dig.addObjectCreate(root + "/servers/mongoDbServer", MongoDbServer.class);
		dig.addCallMethod(root + "/servers/mongoDbServer/name", "setName", 0);
		dig.addCallMethod(root + "/servers/mongoDbServer/description", "setDescription", 0);
			dig.addObjectCreate(root + "/servers/mongoDbServer/mongoDbConnection", MongoDbConnection.class);
			dig.addCallMethod(root + "/servers/mongoDbServer/mongoDbConnection/name", "setName", 0);
			dig.addCallMethod(root + "/servers/mongoDbServer/mongoDbConnection/host", "setHost", 0);
			dig.addCallMethod(root + "/servers/mongoDbServer/mongoDbConnection/port", "setPort", 0);
			dig.addCallMethod(root + "/servers/mongoDbServer/mongoDbConnection/login", "setLogin", 0);
			dig.addCallMethod(root + "/servers/mongoDbServer/mongoDbConnection/password", "setPassword", 0);
			dig.addSetNext(root + "/servers/mongoDbServer/mongoDbConnection", "setCurrentConnection");
		dig.addSetNext(root + "/servers/mongoDbServer", "addServer");
		
		/*
		 * Variable
		 */
		dig.addObjectCreate(root + "/variable", Variable.class);
		dig.addCallMethod(root + "/variable/name", "setName", 0);
		dig.addCallMethod(root + "/variable/value", "setValue", 0);
		dig.addCallMethod(root + "/variable/scope", "setScope", 0);
		dig.addCallMethod(root + "/variable/dataType", "setType", 0);
		dig.addSetNext(root + "/variable", "add");
				
	}
}

