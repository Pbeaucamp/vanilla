package bpm.vanilla.repository.beans;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.FileSystemResource;

import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.repository.beans.alert.ActionDAO;
import bpm.vanilla.repository.beans.alert.AlertDAO;
import bpm.vanilla.repository.beans.alert.ConditionDAO;
import bpm.vanilla.repository.beans.alert.SmtpDAO;
import bpm.vanilla.repository.beans.alert.SubscriberDAO;
import bpm.vanilla.repository.beans.annotations.CommentDAO;
import bpm.vanilla.repository.beans.datasource.DataSourceDAO;
import bpm.vanilla.repository.beans.datasprovider.DatasProviderDAO;
import bpm.vanilla.repository.beans.datasprovider.ItemsDPDAO;
import bpm.vanilla.repository.beans.directory.DirectoryDAO;
import bpm.vanilla.repository.beans.directory.item.ItemDAO;
import bpm.vanilla.repository.beans.directory.item.LinkedDocumentDAO;
import bpm.vanilla.repository.beans.historique.HistoricDAO;
import bpm.vanilla.repository.beans.historique.ItemInstanceDAO;
import bpm.vanilla.repository.beans.historique.ReportHistoDAO;
import bpm.vanilla.repository.beans.historique.SecurityReportHistoDAO;
import bpm.vanilla.repository.beans.impact.DataSourceImpactDAO;
import bpm.vanilla.repository.beans.impact.DirectoryItemDependanceDAO;
import bpm.vanilla.repository.beans.impact.RelationalImpactDAO;
import bpm.vanilla.repository.beans.log.LogDAO;
import bpm.vanilla.repository.beans.meta.MetaDAO;
import bpm.vanilla.repository.beans.model.ItemModelDAO;
import bpm.vanilla.repository.beans.model.TemplateDAO;
import bpm.vanilla.repository.beans.parameters.ParameterDAO;
import bpm.vanilla.repository.beans.parameters.ParameterManager;
import bpm.vanilla.repository.beans.security.RunnableGroupDAO;
import bpm.vanilla.repository.beans.security.SecuredDirectoryDAO;
import bpm.vanilla.repository.beans.security.SecuredLinkedDAO;
import bpm.vanilla.repository.beans.security.SecuredObjectDAO;
import bpm.vanilla.repository.beans.validation.ValidationDAO;
import bpm.vanilla.repository.beans.versionning.LockDAO;
import bpm.vanilla.repository.beans.watchlist.WatchListDAO;

public class RepositoryDaoComponent {

	private DirectoryItemDependanceDAO dependanciesDao;
	private SecuredObjectDAO securedObjectDao;
	private SecuredDirectoryDAO securedDirectoryDao;
	private RunnableGroupDAO runnableGroupDao;
	private SecuredLinkedDAO securedLinkedDao;
	private LinkedDocumentDAO linkedDocumentDao;
	private DirectoryDAO directoryDao;
	private ItemDAO itemDao;
	private HistoricDAO historicDao;
	private ReportHistoDAO reportHistoDao;
	private ItemsDPDAO itemsDpDao;
	private ParameterDAO parameterDao;
	private LockDAO lockDao;
	private DataSourceImpactDAO datasourceImpactDao;
	private DataSourceDAO datasourceDao;
	private DatasProviderDAO dataProviderDao;
	private CommentDAO commentDao;
	private WatchListDAO watchListDao;
	private SecurityReportHistoDAO securityReportHistoDao;
	private AlertDAO alertDao;
	private ConditionDAO conditionDao;
	private SubscriberDAO subscriberDao;
	private ActionDAO actionDao;
	// private EventDAO eventDao;
	private SmtpDAO smtpDao;
	private RelationalImpactDAO relationalImpactDAO;
	private ItemInstanceDAO itemInstanceDAO;
	private bpm.vanilla.repository.beans.comments.CommentDAO commentDefinitionDAO;
	private ValidationDAO validationDAO;
	private TemplateDAO templateDAO;
	private MetaDAO metaDAO;
	
	//Check first if you cannot add it in global DAO before creating another one
	private GlobalDAO globalDAO;

	private ParameterManager parameterMgr;

	private LogDAO logDao;

	private ItemModelDAO itemModelDao;

	private int repositoryId;

	public RepositoryDaoComponent(int repositoryId) {
		this.repositoryId = repositoryId;
	}

	public void activate() {
		try {
			init();
		} catch (Exception e) {
			Logger.getLogger(RepositoryDaoComponent.class).error("Unable to init VanillaDaoComponent - " + e.getMessage(), e);
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	private void init() throws Exception {

		ClassPathXmlApplicationContext factory = new ClassPathXmlApplicationContext("/bpm/vanilla/repository/beans/repository_context.xml") {

			protected void initBeanDefinitionReader(XmlBeanDefinitionReader reader) {
				super.initBeanDefinitionReader(reader);
				reader.setValidationMode(XmlBeanDefinitionReader.VALIDATION_NONE);
				// This the important line and available since Equinox 3.7
				ClassLoader loader = RepositoryDaoComponent.this.getClass().getClassLoader();
				reader.setBeanClassLoader(loader);
			}

			@Override
			protected void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
				String filePath = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_REPOSITORY_FILE + repositoryId);

				PropertyPlaceholderConfigurer cfg = new PropertyPlaceholderConfigurer();
				FileSystemResource resource = new FileSystemResource(filePath);
				cfg.setLocation(resource);

				cfg.postProcessBeanFactory(beanFactory);
			}
		};

		// String filePath = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_REPOSITORY_FILE + repositoryId);
		// // ClassPathResource configFile = new ClassPathResource("/bpm/vanilla/repository/beans/repository_context.xml", this.getClass().getClassLoader());
		// // XmlBeanFactory factory = new XmlBeanFactory(configFile);
		//
		// PropertyPlaceholderConfigurer cfg = new PropertyPlaceholderConfigurer();
		// FileSystemResource resource = new FileSystemResource(filePath);
		// cfg.setLocation(resource);
		// factory.addBeanFactoryPostProcessor(cfg);
		// cfg.postProcessBeanFactory(factory.getBeanFactory());
		// // cfg.postProcessBeanFactory(factory);

		int limitNumberVersion = 0;
		try {
			limitNumberVersion = Integer.parseInt(ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_LIMIT_VERSION_MODEL));
		} catch (Exception e) {
		}

		try {
			dependanciesDao = (DirectoryItemDependanceDAO) factory.getBean("directoryItemDependanceDAO");
		} catch (Exception ex) {
			throw new Exception("ServiceBrowse Unable to load SpringBean directoryItemDAO", ex);
		}

		try {
			parameterMgr = (ParameterManager) factory.getBean("parameterManager");
		} catch (Exception ex) {
			throw new Exception("ServiceAdmin Unable to load SpringBean parameterManager", ex);
		}

		try {
			historicDao = (HistoricDAO) factory.getBean("historicDAO");
		} catch (Exception ex) {
			throw new Exception("ServiceAdmin Unable to load SpringBean historicDAO", ex);
		}

		try {
			directoryDao = (DirectoryDAO) factory.getBean("directoryDAO");
		} catch (Exception ex) {
			throw new Exception("ServiceAdmin Unable to load SpringBean directoryDAO", ex);
		}

		try {
			itemDao = (ItemDAO) factory.getBean("itemDAO");
		} catch (Exception ex) {
			throw new Exception("ServiceAdmin Unable to load SpringBean itemDAO", ex);
		}

		try {
			linkedDocumentDao = (LinkedDocumentDAO) factory.getBean("linkedDocumentDAO");
		} catch (Exception ex) {
			throw new Exception("ServiceAdmin Unable to load SpringBean linkedDocumentDAO", ex);
		}

		try {
			securedLinkedDao = (SecuredLinkedDAO) factory.getBean("securedLinkedDAO");
		} catch (Exception ex) {
			throw new Exception("ServiceAdmin Unable to load SpringBean securedLinkedDAO", ex);
		}

		try {
			runnableGroupDao = (RunnableGroupDAO) factory.getBean("runnableGroupDAO");
		} catch (Exception ex) {
			throw new Exception("ServiceAdmin Unable to load SpringBean runnableGroupDAO", ex);
		}

		try {
			itemsDpDao = (ItemsDPDAO) factory.getBean("itemsDPDAO");
		} catch (Exception ex) {
			throw new Exception("ServiceAdmin Unable to load SpringBean itemsDPDAO", ex);
		}

		try {
			securedObjectDao = (SecuredObjectDAO) factory.getBean("securedObjectDAO");
		} catch (Exception ex) {
			throw new Exception("ServiceAdmin Unable to load SpringBean securedObjectDAO", ex);
		}

		try {
			securedDirectoryDao = (SecuredDirectoryDAO) factory.getBean("securedDirectoryDAO");
		} catch (Exception ex) {
			throw new Exception("ServiceAdmin Unable to load SpringBean securedDirectoryDAO", ex);
		}

		try {
			reportHistoDao = (ReportHistoDAO) factory.getBean("histoReportDAO");
		} catch (Exception ex) {
			throw new Exception("ServiceAdmin Unable to load SpringBean reportHistoDao", ex);
		}

		try {
			parameterDao = (ParameterDAO) factory.getBean("parameterDAO");
		} catch (Exception ex) {
			throw new Exception("ServiceAdmin Unable to load SpringBean parameterDAO", ex);
		}

		try {
			lockDao = (LockDAO) factory.getBean("lockDAO");
		} catch (Exception ex) {
			throw new Exception("ServiceAdmin Unable to load SpringBean lockDAO", ex);
		}

		try {
			logDao = (LogDAO) factory.getBean("logDAO");
		} catch (Exception ex) {
			throw new Exception("ServiceAdmin Unable to load SpringBean logDAO", ex);
		}

		try {
			itemModelDao = (ItemModelDAO) factory.getBean("itemModelDAO");
			itemModelDao.setLimitNumberVersion(limitNumberVersion);
		} catch (Exception ex) {
			throw new Exception("ServiceAdmin Unable to load SpringBean itemModelDAO", ex);
		}

		try {
			datasourceImpactDao = (DataSourceImpactDAO) factory.getBean("dataSourceImpactDAO");
		} catch (Exception ex) {
			throw new Exception("ServiceAdmin Unable to load SpringBean dataSourceImpactDAO", ex);
		}

		try {
			datasourceDao = (DataSourceDAO) factory.getBean("dataSourceDAO");
		} catch (Exception ex) {
			throw new Exception("ServiceAdmin Unable to load SpringBean dataSourceDAO", ex);
		}

		try {
			dataProviderDao = (DatasProviderDAO) factory.getBean("datasProviderDAO");
		} catch (Exception ex) {
			throw new Exception("ServiceAdmin Unable to load SpringBean datasProviderDAO", ex);
		}

		try {
			commentDao = (CommentDAO) factory.getBean("commentDAO");
		} catch (Exception ex) {
			throw new Exception("ServiceAdmin Unable to load SpringBean commentDAO", ex);
		}

		try {
			watchListDao = (WatchListDAO) factory.getBean("watchListDAO");
		} catch (Exception ex) {
			throw new Exception("ServiceAdmin Unable to load SpringBean watchListDAO", ex);
		}

		try {
			securityReportHistoDao = (SecurityReportHistoDAO) factory.getBean("securityHistoReportDAO");
		} catch (Exception ex) {
			throw new Exception("ServiceAdmin Unable to load SpringBean securityHistoReportDAO", ex);
		}

		try {
			alertDao = (AlertDAO) factory.getBean("alertDAO");
		} catch (Exception ex) {
			throw new Exception("AlertService Unable to load SpringBean alertDAO", ex);
		}

		try {
			conditionDao = (ConditionDAO) factory.getBean("conditionDAO");
		} catch (Exception ex) {
			throw new Exception("AlertService Unable to load SpringBean conditionDAO", ex);
		}

		try {
			subscriberDao = (SubscriberDAO) factory.getBean("subscriberDAO");
		} catch (Exception ex) {
			throw new Exception("AlertService Unable to load SpringBean subscriberDAO", ex);
		}

		try {
			actionDao = (ActionDAO) factory.getBean("actionDAO");
		} catch (Exception ex) {
			throw new Exception("AlertService Unable to load SpringBean actionDAO", ex);
		}

		// try {
		// eventDao = (EventDAO) factory.getBean("eventDAO");
		// } catch (Exception ex) {
		// throw new Exception("AlertService Unable to load SpringBean eventDAO", ex);
		// }

		try {
			smtpDao = (SmtpDAO) factory.getBean("smtpDAO");
		} catch (Exception ex) {
			throw new Exception("AlertService Unable to load SpringBean smtpDAO", ex);
		}

		try {
			relationalImpactDAO = (RelationalImpactDAO) factory.getBean("relationalImpactDAO");
		} catch (Exception ex) {
			throw new Exception("AlertService Unable to load SpringBean relationalImpactDAO", ex);
		}

		try {
			itemInstanceDAO = (ItemInstanceDAO) factory.getBean("itemInstanceDAO");
		} catch (Exception ex) {
			throw new Exception("ServiceAdmin Unable to load SpringBean itemInstanceDAO", ex);
		}

		try {
			commentDefinitionDAO = (bpm.vanilla.repository.beans.comments.CommentDAO) factory.getBean("commentDefinitionDAO");
		} catch (Exception ex) {
			throw new Exception("ServiceAdmin Unable to load SpringBean commentDefinitionDAO", ex);
		}

		try {
			validationDAO = (ValidationDAO) factory.getBean("validationDAO");
		} catch (Exception ex) {
			throw new Exception("ServiceAdmin Unable to load SpringBean validationDAO", ex);
		}

		try {
			templateDAO = (TemplateDAO) factory.getBean("templateDAO");
		} catch (Exception ex) {
			throw new Exception("ServiceAdmin Unable to load SpringBean templateDAO", ex);
		}

		try {
			metaDAO = (MetaDAO) factory.getBean("metaDAO");
		} catch (Exception ex) {
			throw new Exception("ServiceAdmin Unable to load SpringBean metaDAO", ex);
		}

		try {
			globalDAO = (GlobalDAO) factory.getBean("globalDAO");
		} catch (Exception ex) {
			throw new Exception("ServiceAdmin Unable to load SpringBean globalDAO", ex);
		}
	}

	public DirectoryItemDependanceDAO getDependanciesDao() {
		return dependanciesDao;
	}

	public RelationalImpactDAO getRelationalImpactDao() {
		return relationalImpactDAO;
	}

	public SecuredObjectDAO getSecuredObjectDao() {
		return securedObjectDao;
	}

	public SecuredDirectoryDAO getSecuredDirectoryDao() {
		return securedDirectoryDao;
	}

	public RunnableGroupDAO getRunnableGroupDao() {
		return runnableGroupDao;
	}

	public SecuredLinkedDAO getSecuredLinkedDao() {
		return securedLinkedDao;
	}

	public LinkedDocumentDAO getLinkedDocumentDao() {
		return linkedDocumentDao;
	}

	public DirectoryDAO getDirectoryDao() {
		return directoryDao;
	}

	public ItemDAO getItemDao() {
		return itemDao;
	}

	public HistoricDAO getHistoricDao() {
		return historicDao;
	}

	public ReportHistoDAO getReportHistoDao() {
		return reportHistoDao;
	}

	public ItemsDPDAO getItemsDpDao() {
		return itemsDpDao;
	}

	public ParameterDAO getParameterDao() {
		return parameterDao;
	}

	public LockDAO getLockDao() {
		return lockDao;
	}

	public ParameterManager getParameterMgr() {
		return parameterMgr;
	}

	public LogDAO getLogDao() {
		return logDao;
	}

	public ItemModelDAO getItemModelDao() {
		return itemModelDao;
	}

	public DataSourceImpactDAO getDatasourceImpactDao() {
		return datasourceImpactDao;
	}

	public DataSourceDAO getDatasourceDao() {
		return datasourceDao;
	}

	public DatasProviderDAO getDataproviderDao() {
		return dataProviderDao;
	}

	public CommentDAO getCommentDao() {
		return commentDao;
	}

	public WatchListDAO getWatchListDao() {
		return watchListDao;
	}

	public SecurityReportHistoDAO getSecurityReportHistoDao() {
		return securityReportHistoDao;
	}

	public AlertDAO getAlertDao() {
		return alertDao;
	}

	public SubscriberDAO getSubscriberDao() {
		return subscriberDao;
	}

	public ActionDAO getActionDao() {
		return actionDao;
	}

	// public EventDAO getEventDao() {
	// return eventDao;
	// }

	public SmtpDAO getSmtpDao() {
		return smtpDao;
	}

	public ConditionDAO getConditionDao() {
		return conditionDao;
	}

	public ItemInstanceDAO getItemInstanceDAO() {
		return itemInstanceDAO;
	}

	public bpm.vanilla.repository.beans.comments.CommentDAO getCommentDefinitionDAO() {
		return commentDefinitionDAO;
	}

	public ValidationDAO getValidationDAO() {
		return validationDAO;
	}
	
	public TemplateDAO getTemplateDAO() {
		return templateDAO;
	}
	
	public MetaDAO getMetaDAO() {
		return metaDAO;
	}
	
	public GlobalDAO getGlobalDAO() {
		return globalDAO;
	}
}
