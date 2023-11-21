package bpm.connector.seveneleven.db;

import java.util.Properties;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;

public class ConnectorDaoComponent {

	public static final String CONNECTOR_DAO = "connectorDao";

	private static ConnectorDAO connectorDao;

	private static ConnectorDAO init(String driver, String dbUrl, String username, String password) throws Exception {
		ClassPathResource configFile = new ClassPathResource("/bpm/connector/seveneleven/db/connector_context.xml", ConnectorDaoComponent.class.getClassLoader());
		XmlBeanFactory factory = new XmlBeanFactory(configFile);

		Properties props = new Properties();
		props.put("tsbn.database.driverClassName", driver);
		props.put("tsbn.database.jdbcUrl", dbUrl);
		props.put("tsbn.database.userName", username);
		props.put("tsbn.database.password", password);
		props.put("tsbn.database.dialect", DialectHelper.getDialect(driver));

		PropertyPlaceholderConfigurer cfg = new PropertyPlaceholderConfigurer();
		cfg.setProperties(props);
		cfg.postProcessBeanFactory(factory);

		return (ConnectorDAO) factory.getBean(CONNECTOR_DAO);
	}

	public static ConnectorDAO getConnectorDao(String driver, String dbUrl, String username, String password) throws Exception {
		if (connectorDao == null) {
			connectorDao = init(driver, dbUrl, username, password);
		}
		return connectorDao;
	}
}
