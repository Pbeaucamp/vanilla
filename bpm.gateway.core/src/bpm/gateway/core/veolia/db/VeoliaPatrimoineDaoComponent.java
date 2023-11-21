package bpm.gateway.core.veolia.db;

import java.util.Properties;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

import bpm.gateway.core.veolia.abonnes.AbonnesDAO;
import bpm.gateway.core.veolia.patrimoine.PatrimoineDAO;

public class VeoliaPatrimoineDaoComponent {

	public static final String BEAN_SCHEDULER = "patrimoineDAO";

	private PatrimoineDAO patrimoineDao;

	public void init(String driver, String dbUrl, String username, String password) throws Exception {
		ClassPathResource configFile = new ClassPathResource("/bpm/gateway/core/veolia/db/patrimoine_context.xml", VeoliaPatrimoineDaoComponent.class.getClassLoader());
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
			patrimoineDao = (PatrimoineDAO) factory.getBean(BEAN_SCHEDULER);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public PatrimoineDAO getPatrimoineDao() {
		return patrimoineDao;
	}
}
