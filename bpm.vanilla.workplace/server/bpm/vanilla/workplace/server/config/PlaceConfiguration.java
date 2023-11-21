package bpm.vanilla.workplace.server.config;

import java.io.File;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;

import bpm.vanilla.workplace.server.AdminServiceImpl;
import bpm.vanilla.workplace.server.db.LogDAO;
import bpm.vanilla.workplace.server.db.PackageDAO;
import bpm.vanilla.workplace.server.db.ProjectDAO;
import bpm.vanilla.workplace.server.db.UserDAO;
import bpm.vanilla.workplace.server.db.UserPackageDAO;
import bpm.vanilla.workplace.shared.exceptions.ServiceException;

public class PlaceConfiguration {
	public static final String PACKAGE_FOLDER = "packages";
	public static final String PACKAGE_TYPE = "vanillapackage";
	
	private static Logger logger;
	private static boolean isLoggerInit = false;
	
	private static PlaceConfiguration config;
	
	private UserDAO userDao;
	private LogDAO logDao;
	private PackageDAO packageDao;
	private ProjectDAO projectDao;
	private UserPackageDAO userPackageDao;
	private XmlBeanFactory beanFactory;

	public static void init(String url){
		if (!isLoggerInit) {
			initLogger(url);
			isLoggerInit = true;
		}
	}
	
	public static PlaceConfiguration getInstance() throws ServiceException {
		if (config == null) {
			config = new PlaceConfiguration();
		}
		
		return config;
	}
	
	public PlaceConfiguration() throws ServiceException {
		logger = Logger.getLogger(AdminServiceImpl.class);
		
		ClassPathResource res = new ClassPathResource("applicationContext.xml");
		beanFactory = new XmlBeanFactory(res);
		
		if (beanFactory == null){
			logger.error("ServiceBrowse application BeanFactory not found");
			throw new ServiceException("ServiceBrowse application BeanFactory not found");
		}
		
		try{
			userDao = (UserDAO)beanFactory.getBean("userDAO");
			logger.info("ServiceChecking Load SpringBean userDAO succeed");
		}catch(Exception ex){
			logger.error("ServiceChecking Unable to load SpringBean userDAO", ex);
			throw new ServiceException("ServiceChecking Unable to load SpringBean userDAO");
		}
		
		try{
			packageDao = (PackageDAO)beanFactory.getBean("packageDAO");
			logger.info("ServiceChecking Load SpringBean packageDAO succeed");
		}catch(Exception ex){
			logger.error("ServiceChecking Unable to load SpringBean packageDAO", ex);
			throw new ServiceException("ServiceChecking Unable to load SpringBean packageDAO");
		}
		
		try{
			projectDao = (ProjectDAO)beanFactory.getBean("projectDAO");
			logger.info("ServiceChecking Load SpringBean projectDAO succeed");
		}catch(Exception ex){
			logger.error("ServiceChecking Unable to load SpringBean projectDAO", ex);
			throw new ServiceException("ServiceChecking Unable to load SpringBean projectDAO");
		}
		
		try{
			logDao = (LogDAO)beanFactory.getBean("logDAO");
			logger.info("ServiceChecking Load SpringBean logDAO succeed");
		}catch(Exception ex){
			logger.error("ServiceChecking Unable to load SpringBean logDAO", ex);
			throw new ServiceException("ServiceChecking Unable to load SpringBean logDAO");
		}
		
		try{
			userPackageDao = (UserPackageDAO)beanFactory.getBean("userPackageDAO");
			logger.info("ServiceChecking Load SpringBean userPackageDAO succeed");
		}catch(Exception ex){
			logger.error("ServiceChecking Unable to load SpringBean userPackageDAO", ex);
			throw new ServiceException("ServiceChecking Unable to load SpringBean userPackageDAO");
		}
	}
	
	private static void initLogger(String url) {
		try{
			DOMConfigurator.configure(url + "conf" + File.separator + "log.xml");
			
			logger = Logger.getLogger(PlaceConfiguration.class);
			logger.info("Log4j inited with file conf/log.xml");
		}catch(Exception ex){
			ex.printStackTrace();
		}
		logger.info("Logger has finished initalizing.");	
	}
	
	public UserDAO getUserDao() {
		return userDao;
	}
	
	public PackageDAO getPackageDao() {
		return packageDao;
	}
	
	public UserPackageDAO getUserPackageDao() {
		return userPackageDao;
	}
	
	public ProjectDAO getProjectDao() {
		return projectDao;
	}
	
	public LogDAO getLogDao() {
		return logDao;
	}
}
