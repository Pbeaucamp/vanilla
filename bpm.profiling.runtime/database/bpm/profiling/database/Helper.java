package bpm.profiling.database;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

public class Helper {
	private static Helper instance = null;
//	private static String resourceFile = null;
	private ConnectionManager connectionManager;
	private AnalysisManager queryManager;
	private String url, user, password, driverClass; 
	
	private Helper() throws Exception {
		try{
			ClassPathResource configFile = new ClassPathResource("/bpm/profiling/runtime/bpm.profiling.context.xml", Helper.class.getClassLoader());
			XmlBeanFactory factory = new XmlBeanFactory(configFile);
			
			try{
				PropertyPlaceholderConfigurer cfg = new PropertyPlaceholderConfigurer();
				cfg.setLocation(new FileSystemResource(System.getProperty("bpm.vanilla.configurationFile")));
				// now actually do the replacement
				cfg.postProcessBeanFactory(factory);
			}catch(Exception ex){
				ex.printStackTrace();
			}

			
			connectionManager = (ConnectionManager) factory.getBean("connectionManager");
			queryManager = (AnalysisManager) factory.getBean("analysisManager");
			
			
			if (factory.getBean("dataSource") instanceof org.apache.commons.dbcp.BasicDataSource){
				org.apache.commons.dbcp.BasicDataSource ds = (org.apache.commons.dbcp.BasicDataSource)factory.getBean("dataSource");
				
				url = ds.getUrl();
				user = ds.getUsername();
				password = ds.getPassword();
				driverClass = ds.getDriverClassName();
			}
			else if (factory.getBean("dataSource") instanceof com.mchange.v2.c3p0.ComboPooledDataSource){
				com.mchange.v2.c3p0.ComboPooledDataSource ds = (com.mchange.v2.c3p0.ComboPooledDataSource)factory.getBean("dataSource");
				
				url = ds.getJdbcUrl();
				user = ds.getUser();
				password = ds.getPassword();
				driverClass = ds.getDriverClass();
			}
			
		}catch(Throwable x){
			x.printStackTrace();
			throw new Exception(x);
		}
	
	}
//	public static void main(String[] args) {
//		System.setProperty("bpm.vanilla.configurationFile", "./resources/vanilla_config.properties");
//		try{
//			 try{
//	        	 DOMConfigurator.configure("./resources/log.xml"); 	        	 
//	        	 
//	         }catch(Exception e){
//	        	 e.printStackTrace();
//	         }
//	         
//			init("./resources/profilingContext.xml");
//			new Helper();
//		}catch(Exception ex){
//			ex.printStackTrace();
//		}
//	}
	
//	/**
//	 * rebuild the spring beans and set the dataBase properteis values from
//	 * the parameter
//	 * 
//	 * here is the list of the properties names :
//	 * driverClassName, username, password, url
//	 * @param p
//	 */
//	private Helper(Properties p){
//		FileSystemResource res = new FileSystemResource(resourceFile);
//		XmlBeanFactory factory = new XmlBeanFactory(res);
//		
//		org.apache.commons.dbcp.BasicDataSource ds = (org.apache.commons.dbcp.BasicDataSource)factory.getBean("dataSource");
//		
//		if (p.getProperty("driverClassName") != null){
//			ds.setDriverClassName(p.getProperty("driverClassName"));
//		}
//		
//		if (p.getProperty("username") != null){
//			ds.setUsername(p.getProperty("username"));
//		}
//
//		if (p.getProperty("password") != null){
//			ds.setPassword(p.getProperty("password"));
//		}
//		
//		if (p.getProperty("url") != null){
//			ds.setUrl(p.getProperty("url"));
//		}
//
////		Object b = factory.getBean("sessionFactory");
////		((org.springframework.orm.hibernate3.LocalSessionFactoryBean)b).setDataSource(ds);
//			
////		factory.configureBean(b, beanName)
//		
//		
//		connectionManager = (ConnectionManager) factory.getBean("connectionManager");
//		queryManager = (AnalysisManager) factory.getBean("analysisManager");
//	}
	
	
//	public static void init(String resourceFile){
//		Helper.resourceFile = resourceFile;
//	}
	
//	public static Helper getInstance(Properties p) throws Exception{
//		if (resourceFile == null){
//			throw new Exception("bpm.profiling.database.Helper have not been inited");
//		}
//		
//		instance = new Helper(p);
//		
//		
//		return instance;
//	}
	
	
	public static Helper getInstance() throws Exception{
//		if (resourceFile == null){
//			throw new Exception("bpm.profiling.database.Helper have not been inited");
//		}
		
		if (instance == null){
			instance = new Helper();
		}
		
		return instance;
		
	}


//	public static final String getResourceFile() {
//		return resourceFile;
//	}

	public final ConnectionManager getConnectionManager() {
		return connectionManager;
	}

	public final AnalysisManager getAnalysisManager() {
		return queryManager;
	}

	public final String getUrl() {
		return url;
	}

	public final String getUser() {
		return user;
	}

	public final String getPassword() {
		return password;
	}

	public final String getDriverClass() {
		return driverClass;
	}

	
}

