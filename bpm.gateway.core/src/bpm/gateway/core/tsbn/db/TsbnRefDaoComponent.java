package bpm.gateway.core.tsbn.db;

import java.util.Properties;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

import bpm.gateway.core.tsbn.referentiels.DwhRefDAO;

public class TsbnRefDaoComponent {

	public static final String BEAN_SCHEDULER_DWH_REF = "dwhRefDAO";

	private DwhRefDAO dwhRefDao;

	public void init(String driver, String dbUrl, String username, String password) throws Exception {
		ClassPathResource configFile = new ClassPathResource("/bpm/gateway/core/tsbn/db/bpm.tsbn.ref.context.xml", TsbnRefDaoComponent.class.getClassLoader());
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
			dwhRefDao = (DwhRefDAO) factory.getBean(BEAN_SCHEDULER_DWH_REF);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public DwhRefDAO getDwhRefDao() {
		return dwhRefDao;
	}
}
