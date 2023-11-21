package bpm.gateway.core.veolia.db;

import java.util.Properties;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

import bpm.gateway.core.veolia.abonnes.AbonnesDAO;

public class VeoliaAbonnesDaoComponent {

	public static final String BEAN_SCHEDULER_ABONNE = "abonneDAO";

	private AbonnesDAO abonnesDao;

	public void init(String driver, String dbUrl, String username, String password) throws Exception {
		ClassPathResource configFile = new ClassPathResource("/bpm/gateway/core/veolia/db/abonnes_context.xml", VeoliaAbonnesDaoComponent.class.getClassLoader());
		XmlBeanFactory factory = new XmlBeanFactory(configFile);

		Properties props = new Properties();
		props.put("veolia.database.driverClassName", driver);
		props.put("veolia.database.jdbcUrl", dbUrl);
		props.put("veolia.database.userName", username);
		props.put("veolia.database.password", password);
		props.put("veolia.database.dialect", DialectHelper.getDialect(driver));
		
		PropertyPlaceholderConfigurer cfg = new PropertyPlaceholderConfigurer();
		FileSystemResource resource = new FileSystemResource(System.getProperty("bpm.vanilla.configurationFile"));
		cfg.setLocation(resource);
		cfg.setProperties(props);
		cfg.postProcessBeanFactory(factory);
		
		try {
			abonnesDao = (AbonnesDAO) factory.getBean(BEAN_SCHEDULER_ABONNE);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public AbonnesDAO getAbonnesDao() {
		return abonnesDao;
	}
}
