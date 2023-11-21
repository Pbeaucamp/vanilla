package bpm.forms.dao;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.osgi.service.component.ComponentContext;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import bpm.forms.dao.managers.DatabaseDefinitionService;
import bpm.forms.dao.managers.DatabaseInstanceService;
import bpm.forms.dao.managers.DatabaseServiceProvider;

/**
 * This Component is used to perform storage in a DataBase. The database is
 * configured in bpm/forms/dao/bpm.forms.context.xml file
 * 
 * It implements the bpm.forms.core.design.IServiceProvider and
 * bpm.forms.core.runtime.IDataSourceProvider.
 * 
 * It loads at startup bpm.forms.core.tools.IFactoryModelElement form the
 * registered services. So the bpm.forms.model plugin needs to be started before
 * this one.
 * 
 * 
 * This plugins register bpm.forms.core.design.IServiceProvider and
 * bpm.forms.core.runtime.IDataSourceProvider
 * 
 * 
 * 
 * @author ludo
 * 
 */
public class VanillaFormsDaoComponent extends DatabaseServiceProvider {

	private ClassPathXmlApplicationContext factory;

	public VanillaFormsDaoComponent() throws Exception {
		super();

	}

	synchronized protected void init() throws Exception {

		if (isInited()) {
			return;
		}
		Logger.getLogger(getClass()).info("initing SpringBeans ...");
		// ClassPathResource configFile = new
		// ClassPathResource("/bpm/forms/dao/bpm.forms.context.xml",
		// VanillaFormsDaoComponent.class.getClassLoader());
		// this.factory = new XmlBeanFactory(configFile);
		//
		// try {
		// PropertyPlaceholderConfigurer cfg = new
		// PropertyPlaceholderConfigurer();
		//
		// String s = System.getProperty("bpm.vanilla.configurationFile");
		// if (s == null) {
		// Logger.getLogger(getClass()).warn("The property bpm.vanilla.configurationFile is not specified.");
		//
		// cfg.setLocation(new
		// ClassPathResource("/bpm/forms/dao/bpm.forms.properties",
		// VanillaFormsDaoComponent.class.getClassLoader()));
		// Logger.getLogger(getClass()).info("Using the default bpm.forms.properties within bpm.forms.dao bundle");
		// }
		// else if (!new File(s).exists()) {
		// Logger.getLogger(getClass()).warn("No configuration file found at " +
		// s);
		// cfg.setLocation(new
		// ClassPathResource("/bpm/forms/dao/bpm.forms.properties",
		// VanillaFormsDaoComponent.class.getClassLoader()));
		// Logger.getLogger(getClass()).info("Using the default bpm.forms.properties within bpm.forms.dao bundle");
		// }
		// else {
		// Logger.getLogger(getClass()).info("Using the " + s +
		// " to configure bpm.forms.dao bundle");
		// cfg.setLocation(new FileSystemResource(s));
		// }
		//
		// // now actually do the replacement
		// cfg.postProcessBeanFactory(factory);
		// } catch (Exception ex) {
		// Logger.getLogger(getClass()).warn("Unable to use Spring postProcessBeanFactory - "
		// + ex.getMessage(), ex);
		// }

		this.factory = new ClassPathXmlApplicationContext("/bpm/forms/dao/bpm.forms.context.xml") {

			protected void initBeanDefinitionReader(XmlBeanDefinitionReader reader) {
				super.initBeanDefinitionReader(reader);
				reader.setValidationMode(XmlBeanDefinitionReader.VALIDATION_NONE);
				// This the important line and available since Equinox 3.7
				ClassLoader loader = VanillaFormsDaoComponent.this.getClass().getClassLoader();
				// ClassLoader loader =
				// bundle.adapt(BundleWiring.class).getClassLoader();
				reader.setBeanClassLoader(loader);
			}
		};

		setDefinitionManager((DatabaseDefinitionService) factory.getBean(BEAN_DEFINITION_MANAGER_ID));
		setRuntimeManager((DatabaseInstanceService) factory.getBean(BEAN_RUNTIME_MANAGER_ID));
		setDataSource((DataSource) factory.getBean(BEAN_DATASOURCE));

		Logger.getLogger(getClass()).info("Spring Beans inited");
		setInited();
	}

	public void desactivate(ComponentContext ctx) {
		Logger.getLogger(getClass()).info("Deactivate VanillaFormsDaoComponent");

		factory.close();
		factory.destroy();
		
		factory = null;

		// DatabaseInstanceService runtimeManager = getRuntimeManager();
		// if (runtimeManager != null) {
		// runtimeManager.stop();
		// runtimeManager = null;
		// Logger.getLogger(getClass()).info("Forms -> Deactivate DatabaseInstanceService");
		// }
		//
		// DatabaseDefinitionService definitionManager = getDefinitionManager();
		// if (definitionManager != null) {
		// definitionManager.stop();
		// definitionManager = null;
		// Logger.getLogger(getClass()).info("Forms -> Deactivate DatabaseDefinitionService");
		// }
	}
}
