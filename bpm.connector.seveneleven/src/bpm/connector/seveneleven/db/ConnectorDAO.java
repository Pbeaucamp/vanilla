package bpm.connector.seveneleven.db;

import bpm.connector.seveneleven.model.Sales;
import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class ConnectorDAO extends HibernateDaoSupport {

	public void save(Sales sales) {
		getHibernateTemplate().save(sales);
	}
}
