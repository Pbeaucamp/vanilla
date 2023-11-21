package bpm.workflow.runtime.resources.servers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.digester3.Digester;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;


/**
 * Digester of the list of the servers which are in the workspace (and load it from resources/)
 * @author CHARBONNIER, MARTIN
 *
 */
public class ListServerDigester {
	
	private Digester dig = new Digester();
	private StringBuffer error=   new StringBuffer();
	
	
	private ListServerDigester(){
		dig.setValidating(false);
		dig.setErrorHandler(new MyErrorHandler());
		createCallbacks();
	}
	
	
	private ListServerDigester(ClassLoader classLoader){
		if (classLoader != null){
			dig.setClassLoader(classLoader);
		}
		
		dig.setValidating(false);
		dig.setErrorHandler(new MyErrorHandler());
		createCallbacks();
	}
	
	private void createCallbacks(){
		String root = "resources/listServer";
		
		dig.addObjectCreate(root, ListServer.class);
			dig.addCallMethod(root + "/name", "setName", 0);
			dig.addCallMethod(root + "/id", "setId", 0);
			
			
			
//			dig.addObjectCreate(root + "/vanillaRepositoryServer", BiRepository.class);
//				dig.addCallMethod(root + "/vanillaRepositoryServer/id", "setId", 0);
//				dig.addCallMethod(root + "/vanillaRepositoryServer/name", "setName", 0);
//				dig.addCallMethod(root + "/vanillaRepositoryServer/url", "setUrl", 0);
//				dig.addCallMethod(root + "/vanillaRepositoryServer/login", "setLogin", 0);
//				dig.addCallMethod(root + "/vanillaRepositoryServer/password", "setPassword", 0);
//			dig.addSetNext(root + "/vanillaRepositoryServer", "addServer");
			

			dig.addObjectCreate(root + "/dataBaseServer", DataBaseServer.class);
				dig.addCallMethod(root + "/dataBaseServer/id", "setId", 0);
				dig.addCallMethod(root + "/dataBaseServer/name", "setName", 0);
				dig.addCallMethod(root + "/dataBaseServer/url", "setUrl", 0);
				dig.addCallMethod(root + "/dataBaseServer/login", "setLogin", 0);
				dig.addCallMethod(root + "/dataBaseServer/password", "setPassword", 0);
				dig.addCallMethod(root + "/dataBaseServer/dataBaseName", "setDataBaseName", 0);
				dig.addCallMethod(root + "/dataBaseServer/jdbcDriver", "setJdbcDriver", 0);
				dig.addCallMethod(root + "/dataBaseServer/port", "setPort", 0);
				dig.addCallMethod(root + "/dataBaseServer/schemaName", "setSchemaName", 0);
			dig.addSetNext(root + "/dataBaseServer", "addServer");
			
			dig.addObjectCreate(root + "/freemetricsServer", FreemetricServer.class);
			dig.addCallMethod(root + "/freemetricsServer/id", "setId", 0);
			dig.addCallMethod(root + "/freemetricsServer/name", "setName", 0);
			dig.addCallMethod(root + "/freemetricsServer/url", "setUrl", 0);
			dig.addCallMethod(root + "/freemetricsServer/login", "setLogin", 0);
			dig.addCallMethod(root + "/freemetricsServer/password", "setPassword", 0);
			dig.addCallMethod(root + "/freemetricsServer/dataBaseName", "setDataBaseName", 0);
			dig.addCallMethod(root + "/freemetricsServer/jdbcDriver", "setJdbcDriver", 0);
			dig.addCallMethod(root + "/freemetricsServer/port", "setPort", 0);
			dig.addCallMethod(root + "/freemetricsServer/freemtricsLogin", "setFmLogin", 0);
			dig.addCallMethod(root + "/freemetricsServer/freemtricsPassword", "setFmPassword", 0);
			
		dig.addSetNext(root + "/freemetricsServer", "addServer");

			dig.addObjectCreate(root + "/mailServer", ServerMail.class);
				dig.addCallMethod(root + "/mailServer/id", "setId", 0);
				dig.addCallMethod(root + "/mailServer/name", "setName", 0);
				dig.addCallMethod(root + "/mailServer/url", "setUrl", 0);
				dig.addCallMethod(root + "/mailServer/login", "setLogin", 0);
				dig.addCallMethod(root + "/mailServer/password", "setPassword", 0);
			dig.addSetNext(root + "/mailServer", "addServer");
			
			dig.addObjectCreate(root + "/fileserver", FileServer.class);
			dig.addCallMethod(root + "/fileserver/id", "setId", 0);
			dig.addCallMethod(root + "/fileserver/name", "setName", 0);
			dig.addCallMethod(root + "/fileserver/url", "setUrl", 0);
			dig.addCallMethod(root + "/fileserver/login", "setLogin", 0);
			dig.addCallMethod(root + "/fileserver/password", "setPassword", 0);
			dig.addCallMethod(root + "/fileserver/typeserver", "setTypeServ", 0);
			dig.addCallMethod(root + "/fileserver/repertoiredef", "setRepertoireDef", 0);
			dig.addCallMethod(root + "/fileserver/port", "setPort", 0);
			dig.addCallMethod(root + "/fileserver/keypath", "setKeyPath", 0);
			dig.addSetNext(root + "/fileserver", "addServer");
			
//			dig.addObjectCreate(root + "/securityServer", SecurityServer.class);
//				dig.addCallMethod(root + "/securityServer/id", "setId", 0);
//				dig.addCallMethod(root + "/securityServer/name", "setName", 0);
//				dig.addCallMethod(root + "/securityServer/url", "setUrl", 0);
//				dig.addCallMethod(root + "/securityServer/login", "setLogin", 0);
//				dig.addCallMethod(root + "/securityServer/password", "setPassword", 0);
//			dig.addSetNext(root + "/securityServer", "addServer");
//			
//					
//			dig.addObjectCreate(root + "/gatewayServer", ServerGateway.class);
//				dig.addCallMethod(root + "/gatewayServer/id", "setId", 0);
//				dig.addCallMethod(root + "/gatewayServer/name", "setName", 0);
//				dig.addCallMethod(root + "/gatewayServer/url", "setUrl", 0);
//			dig.addSetNext(root + "/gatewayServer", "addServer");
//			
//			
//			dig.addObjectCreate(root + "/runtimeServer", VanillaRuntimeServer.class);
//				dig.addCallMethod(root + "/runtimeServer/id", "setId", 0);
//				dig.addCallMethod(root + "/runtimeServer/name", "setName", 0);
//				dig.addCallMethod(root + "/runtimeServer/url", "setUrl", 0);
//				dig.addCallMethod(root + "/runtimeServer/login", "setLogin", 0);
//				dig.addCallMethod(root + "/runtimeServer/password", "setPassword", 0);
//
//				dig.addCallMethod(root + "/runtimeServer/serverType", "setType", 0);
//			dig.addSetNext(root + "/runtimeServer", "addServer");
	}
	
	
	/**
	 * 
	 * @param classLoader
	 * @param filePath
	 * @return the list<Server> from a file path
	 * @throws IOException
	 * @throws SAXException
	 */
	public static ListServer getListServer(ClassLoader classLoader, String filePath) throws IOException, SAXException{
		ListServerDigester dig = new ListServerDigester(classLoader);
		ListServer m = (ListServer)dig.dig.parse(new File(filePath));
		
		purgeModel(m, dig.error);
		return m;
	}
	
	/**
	 * 
	 * @param classLoader
	 * @param inputStream
	 * @return the list<Server> from an inputstream
	 * @throws IOException
	 * @throws SAXException
	 */
	public static ListServer getModel(ClassLoader classLoader, InputStream inputStream) throws IOException, SAXException{
		ListServerDigester dig = new ListServerDigester(classLoader);
		
		ListServer m = (ListServer)dig.dig.parse(inputStream);
		
		purgeModel(m, dig.error);
		return m;
	}
	
	/**
	 * Purge the list<Server>
	 * @param servers
	 * @param buf
	 */
	private static void purgeModel(ListServer servers, StringBuffer buf){
//		if (servers != null){
//			for(Server r : servers.getServers()){
//				if (r instanceof BiRepository){
//					try {
//						((BiRepository)r).setRepositoryConnection();
//					} catch (Exception e) {
//						e.printStackTrace();
//						buf.append(" - Error while building the Server named "  +  r.getName() + " : "+ e.getMessage() + "\n\n");
//					}
//				}
//			}
//		}
		
	}
	
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

}
