package bpm.freemetrics.api.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;

import org.jasypt.intf.service.JasyptStatelessService;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

import bpm.freemetrics.api.features.actions.ActionManager;
import bpm.freemetrics.api.features.alerts.AlertConditionManager;
import bpm.freemetrics.api.features.alerts.AlertManager;
import bpm.freemetrics.api.features.alerts.MetricWithAlertManager;
import bpm.freemetrics.api.features.favorites.UserActionManager;
import bpm.freemetrics.api.features.favorites.UserAlertePreferenceManager;
import bpm.freemetrics.api.features.favorites.UserPreferenceManager;
import bpm.freemetrics.api.features.favorites.WatchListManager;
import bpm.freemetrics.api.features.forum.ForumManager;
import bpm.freemetrics.api.features.infos.FMDatasourceManager;
import bpm.freemetrics.api.features.infos.OrganisationManager;
import bpm.freemetrics.api.features.infos.PropertiesManager;
import bpm.freemetrics.api.features.infos.TypeMetricManager;
import bpm.freemetrics.api.features.infos.TypeOrganisationManager;
import bpm.freemetrics.api.features.infos.UnitManager;
import bpm.freemetrics.api.manager.routing.DataSourceContextHolder;
import bpm.freemetrics.api.manager.routing.DataSourceRouter;
import bpm.freemetrics.api.organisation.application.ApplicationHierarchyManager;
import bpm.freemetrics.api.organisation.application.ApplicationManager;
import bpm.freemetrics.api.organisation.application.AssocApplicationAssocAppMetricManager;
import bpm.freemetrics.api.organisation.application.Decompo_TerritoireManager;
import bpm.freemetrics.api.organisation.application.TypeDecompTerritoireManager;
import bpm.freemetrics.api.organisation.application.TypeTerritoireManager;
import bpm.freemetrics.api.organisation.dashOrTheme.DashboardRelationManager;
import bpm.freemetrics.api.organisation.dashOrTheme.SubThemeManager;
import bpm.freemetrics.api.organisation.dashOrTheme.ThemeManager;
import bpm.freemetrics.api.organisation.group.GroupManager;
import bpm.freemetrics.api.organisation.group.TypeCollectiviteManager;
import bpm.freemetrics.api.organisation.metrics.Assoc_Compteur_IndicateurManager;
import bpm.freemetrics.api.organisation.metrics.MetricInteractionManager;
import bpm.freemetrics.api.organisation.metrics.MetricManager;
import bpm.freemetrics.api.organisation.metrics.MetricValueListManager;
import bpm.freemetrics.api.organisation.metrics.MetricValuesManager;
import bpm.freemetrics.api.organisation.observatoire.ObservatoireManager;
import bpm.freemetrics.api.organisation.relations.appl_group.FmApplication_GroupsManager;
import bpm.freemetrics.api.organisation.relations.appl_metric.Assoc_Application_MetricManager;
import bpm.freemetrics.api.organisation.relations.appl_typeApp.Assoc_Terr_Type_Dec_TerrManager;
import bpm.freemetrics.api.organisation.relations.dash_metric.FmDashboard_MetricManager;
import bpm.freemetrics.api.organisation.relations.user_group.FmUser_GroupsManager;
import bpm.freemetrics.api.security.FmRoleManager;
import bpm.freemetrics.api.security.FmUserManager;
import bpm.freemetrics.api.security.relations.FmUser_RolesManager;


public class FactoryManager {
	public static final int SQL = 0;

	public static final int DATASOURCES = 1;

	private static String resourceFile = null;
	private static Integer osType = null;
	private XmlBeanFactory beanFactory;

//	private static List<String> dataBaseTypeNames = new ArrayList<String>();

	private static FactoryManager factory = null;

	private static IManager manager = null;	

	private static int userId = -1;

	public static String DEFAULT_DATASOURCES;

	
	private FactoryManager(){

		
			ClassPathResource res = new ClassPathResource("freeMetricsContext.xml");
			
//			FileSystemResource res = new FileSystemResource(resourceFile + "/Ressources/freeMetricsContext.xml");
			beanFactory = new XmlBeanFactory(res);


			try{
				PropertyPlaceholderConfigurer cfg = new PropertyPlaceholderConfigurer();
				cfg.setLocation(new FileSystemResource(System.getProperty("bpm.vanilla.configurationFile")));
				// now actually do the replacement
				cfg.postProcessBeanFactory(beanFactory);
			}catch(Exception ex){
				ex.printStackTrace();
			}
	
			if (beanFactory.getBean("dataSource") instanceof org.apache.commons.dbcp.BasicDataSource){
				org.apache.commons.dbcp.BasicDataSource ds = (org.apache.commons.dbcp.BasicDataSource)beanFactory.getBean("dataSourceMySql");
				String pwd = ds.getPassword();
				if (pwd != null && !"".equalsIgnoreCase(pwd) && (pwd.startsWith("ENC(") && pwd.endsWith(")"))) {
					
					String p = pwd.substring(4, pwd.length() - 1);
					ds.setPassword(decript(p, "biplatform"));
				}
				else {
					ds.setPassword(pwd);
				}
			}
			else if (beanFactory.getBean("dataSource") instanceof com.mchange.v2.c3p0.ComboPooledDataSource){
				com.mchange.v2.c3p0.ComboPooledDataSource ds = (com.mchange.v2.c3p0.ComboPooledDataSource)beanFactory.getBean("dataSource");
				String pwd = ds.getPassword();
				if (pwd != null && !"".equalsIgnoreCase(pwd) && (pwd.startsWith("ENC(") && pwd.endsWith(")"))) {
					
					String p = pwd.substring(4, pwd.length() - 1);
					ds.setPassword(decript(p, "biplatform"));
				}
				else {
					ds.setPassword(pwd);
				}
			}
			


//		for(PropertyValue p : beanFactory.getBeanDefinition("dataSource").getPropertyValues().getPropertyValues()){
//
//			if (p.getName().equals("targetDataSources") && p.getValue().getClass() == org.springframework.beans.factory.support.ManagedMap.class){
//				org.springframework.beans.factory.support.ManagedMap map = (org.springframework.beans.factory.support.ManagedMap)p.getValue();
//
//				for(Object o : map.keySet()){
//					dataBaseTypeNames.add(((TypedStringValue)o).getValue());
//				}				
//			}			
//		}

		instanciateManager();
	}


	private void instanciateManager() {
		manager = new SQLManager(				
				(GroupManager) beanFactory.getBean("fmGroupManager"),
				(FmUserManager) beanFactory.getBean("fmUserManager"),
				(FmUser_GroupsManager) beanFactory.getBean("fmUsersGroupsManager"),
				(Assoc_Application_MetricManager) beanFactory.getBean("assoc_Application_MetricManager"),
				(FmUser_RolesManager) beanFactory.getBean("fmUsersRolesManager"),
				(FmApplication_GroupsManager) beanFactory.getBean("fmApplication_GroupsManager"),
				(ApplicationManager) beanFactory.getBean("applicationManager"),
//				(DashboardManager) beanFactory.getBean("dashboardManager"),
				(MetricManager) beanFactory.getBean("metricManager"),
				(FmRoleManager) beanFactory.getBean("fmRoleManager"),
				(FmDashboard_MetricManager)beanFactory.getBean("fmDashboard_MetricManager"),
				(MetricValuesManager)beanFactory.getBean("metricValuesManager"),
				(MetricInteractionManager)beanFactory.getBean("metricInteractionManager"),
				(MetricWithAlertManager)beanFactory.getBean("metricWithAlertManager"),
				(AlertConditionManager)beanFactory.getBean("alertConditionManager"),
				(ActionManager)beanFactory.getBean("actionManager"),
				(AlertManager)beanFactory.getBean("alertManager"),
				(PropertiesManager)beanFactory.getBean("propertiesManager"),
				(ForumManager)beanFactory.getBean("forumManager"),
				(ThemeManager) beanFactory.getBean("themeManager"),
				(SubThemeManager) beanFactory.getBean("subThemeManager"),
				(WatchListManager) beanFactory.getBean("watchListManager"),
				(UserPreferenceManager) beanFactory.getBean("userPreferenceManager"),
				(UserAlertePreferenceManager) beanFactory.getBean("userAlertePreferenceManager"),
				(UserActionManager)beanFactory.getBean("userActionManager"),
				(MetricValueListManager)beanFactory.getBean("metricValueListManager"),
				(UnitManager)beanFactory.getBean("unitManager"),
				(TypeCollectiviteManager) beanFactory.getBean("typeCollectiviteManager"),
				(OrganisationManager) beanFactory.getBean("organisationManager"),
				(TypeOrganisationManager) beanFactory.getBean("typeOrganisationManager"),
				(Assoc_Terr_Type_Dec_TerrManager)beanFactory.getBean("assoc_Terr_Type_Dec_TerrManager"),
				(TypeTerritoireManager) beanFactory.getBean("typeTerritoireManager"),
				(TypeDecompTerritoireManager) beanFactory.getBean("typeDecompTerritoireManager"),
				(Decompo_TerritoireManager) beanFactory.getBean("decompo_TerritoireManager"),
				(TypeMetricManager) beanFactory.getBean("typeMetricManager"),
				(DashboardRelationManager) beanFactory.getBean("dashboardRelationManager"),
				(Assoc_Compteur_IndicateurManager) beanFactory.getBean("assoc_Compteur_IndicateurManager"),
				(FMDatasourceManager) beanFactory.getBean("fmDatasourceManager"),
				(ObservatoireManager) beanFactory.getBean("obsManager"),
				(ApplicationHierarchyManager) beanFactory.getBean("applicationHierarchyManager"),
				(AssocApplicationAssocAppMetricManager) beanFactory.getBean("assocApplicationAssocAppMetricManager")
		);
	}
	public static FactoryManager getInstance() throws FactoryManagerException{
		if (!isInited()){
			throw new FactoryManagerException("The Factorymanager is not inited");
		}

		if (factory == null){
			factory = new FactoryManager();
		}

		return factory;
	}

//	/**
//	 * for redefining the connection parameters at runtime
//	 * 
//	 * keys are : driverClassName, username,password,url
//	 * @param prop
//	 * @param the freemetricsContext.xml file full name
//	 */
//	private static FactoryManager getInstance(Properties p, String xmlContextName) throws FactoryManagerException{
//		if (!isInited()){
//			throw new FactoryManagerException("The Factorymanager is not inited");
//		}
//
//		if (factory == null){
//			factory = new FactoryManager(p, xmlContextName);
//		}
//
//		return factory;
//	}

	public Object getBean(String beanName){
		return beanFactory.getBean(beanName);
	}
	
	private static boolean isInited(){
		return true;
	}

	/**
	 * the fileName of the spring xml configuration file
	 * @param resourceFile
	 */
	public static void init(String resourceFile, int osType){
		FactoryManager.resourceFile = resourceFile;

		FactoryManager.osType = osType;
	}

//	public IManager getManager(int type) throws FactoryManagerException{
//	switch (type){
//	case SQL:


//	return mgr;
//	}

//	throw new FactoryManagerException("The specified Manager type doesnt exist"); 
//	}

//	@Deprecated
//	public static List<String> getDataBaseName() throws FactoryManagerException{
////		if (!isInited()){
////			throw new FactoryManagerException("The Factorymanager is not inited");
////		}
////
////		if (factory == null){
////			factory = new FactoryManager();
////		}
//
////		return dataBaseTypeNames;
//		return new ArrayList<String>();
//	}

//	public IDSManager getDSManager(int type) throws FactoryManagerException {
//		switch (type) {
//		case DATASOURCES:
//
//			IDSManager dsMgr = new DSManager(
//					(DataSourceRouter)beanFactory.getBean("dataSource"));
//
//			return dsMgr;
//		}
//
//		throw new FactoryManagerException("The specified Manager type doesnt exist"); 
//
//	}

	public static IManager getManager() throws FactoryManagerException{
		if (!isInited()){
			throw new FactoryManagerException("The Factorymanager is not inited");
		}

		if (factory == null){
			factory = new FactoryManager();
		}

//		String mapKey = null;
//		for(String s : dataBaseTypeNames){
//			if (s.equals(key)){
//				mapKey = key;
//				break;
//			}
//		}
//
//		if (mapKey == null){
//			throw new FactoryManagerException("The DataBase named " + key + " is not present in the configuration file");
//		}
//
//		DataSourceContextHolder.setDataBase(mapKey);
//		DEFAULT_DATASOURCES = mapKey;


		return manager;
	}


	/**
	 * for redefining the connection parameters at runtime
	 * 
	 * keys are : driverClassName, username,password,url
	 * @param prop
	 */
	private FactoryManager(Properties prop, String fullName){
//		if(osType.equals(Tools.OS_TYPE_MAC)){
//			FileSystemResource res = new FileSystemResource(fullName);
//			beanFactory = new XmlBeanFactory(res);
//			for(String s : beanFactory.getBeanDefinitionNames()){
//				System.out.println(s);
//			}	
//
////			bpm.birep.admin.manager.FactoryManager.init("");
//
//		}else if(osType.equals(Tools.OS_TYPE_LINUX)){
//			FileSystemResource res = new FileSystemResource(fullName);
//			beanFactory = new XmlBeanFactory(res);
//			for(String s : beanFactory.getBeanDefinitionNames()){
//				System.out.println(s);
//			}
////			bpm.birep.admin.manager.FactoryManager.init("./");
//		}else{
//			FileSystemResource res = new FileSystemResource(fullName);
//			beanFactory = new XmlBeanFactory(res);
//			for(String s : beanFactory.getBeanDefinitionNames()){
//				System.out.println(s);
//			}
////			bpm.birep.admin.manager.FactoryManager.init(resourceFile);			
//		}
		
		
		ClassPathResource res = new ClassPathResource("freeMetricsContext.xml");
		
//		FileSystemResource res = new FileSystemResource(resourceFile + "/Ressources/freeMetricsContext.xml");
		beanFactory = new XmlBeanFactory(res);


		try{
			PropertyPlaceholderConfigurer cfg = new PropertyPlaceholderConfigurer();
			cfg.setLocation(new FileSystemResource(System.getProperty("bpm.vanilla.configurationFile")));
			// now actually do the replacement
			cfg.postProcessBeanFactory(beanFactory);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		

//		for(PropertyValue p : beanFactory.getBeanDefinition("dataSource").getPropertyValues().getPropertyValues()){
//
//			if (p.getName().equals("targetDataSources") && p.getValue().getClass() == org.springframework.beans.factory.support.ManagedMap.class){
//				org.springframework.beans.factory.support.ManagedMap map = (org.springframework.beans.factory.support.ManagedMap)p.getValue();
//
//				for(Object o : map.keySet()){
//					dataBaseTypeNames.add(((TypedStringValue)o).getValue());
//				}				
//			}			
//		}


		org.apache.commons.dbcp.BasicDataSource ds = (org.apache.commons.dbcp.BasicDataSource)beanFactory.getBean("dataSourceMySql");

		if (prop.getProperty("driverClassName") != null){
			ds.setDriverClassName(prop.getProperty("driverClassName"));
		}

		if (prop.getProperty("username") != null){
			ds.setUsername(prop.getProperty("username"));
		}

		if (prop.getProperty("password") != null){
			ds.setPassword(prop.getProperty("password"));
		}

		if (prop.getProperty("url") != null){
			ds.setUrl(prop.getProperty("url"));
		}

		instanciateManager();
	}
	
	public static String decript(String input, String password) {
        
        JasyptStatelessService service = new JasyptStatelessService();
        
        String result =
            service.decrypt(
                    input, 
                    password,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null);
        ;
    
        return result;
	}
	
//	public static void main(String[] args) {
//		String r = decript("nsOoZiOUTQNnX0A+Y+QcAixnX+2QAoQ2", "biplatform");
//		System.out.println(r);
//	}


}
