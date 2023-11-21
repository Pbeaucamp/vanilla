package bpm.gateway.core.tsbn.db;

import java.util.Properties;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

import bpm.gateway.core.tsbn.affaires.DwhAffDAO;

public class TsbnAffDaoComponent {

	public static final String BEAN_SCHEDULER_DWH_AFF = "dwhAffDAO";

	private DwhAffDAO dwhAffDao;

	public void init(String driver, String dbUrl, String username, String password) throws Exception {
		ClassPathResource configFile = new ClassPathResource("/bpm/gateway/core/tsbn/db/bpm.tsbn.aff.context.xml", TsbnAffDaoComponent.class.getClassLoader());
		XmlBeanFactory factory = new XmlBeanFactory(configFile);

		Properties props = new Properties();
		props.put("tsbn.database.driverClassName", driver);
		props.put("tsbn.database.jdbcUrl", dbUrl);
		props.put("tsbn.database.userName", username);
		props.put("tsbn.database.password", password);
		props.put("tsbn.database.dialect", DialectHelper.getDialect(driver));
		
		PropertyPlaceholderConfigurer cfg = new PropertyPlaceholderConfigurer();
		FileSystemResource resource = new FileSystemResource(System.getProperty("bpm.vanilla.configurationFile"));
		cfg.setLocation(resource);
		cfg.setProperties(props);
		cfg.postProcessBeanFactory(factory);
		
		try {
			dwhAffDao = (DwhAffDAO) factory.getBean(BEAN_SCHEDULER_DWH_AFF);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public DwhAffDAO getDwhAffDao() {
		return dwhAffDao;
	}
}
