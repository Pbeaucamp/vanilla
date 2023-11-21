package bpm.vanilla.repository.beans.security;

import java.sql.ResultSet;
import java.util.Collection;
import java.util.List;

import bpm.connection.manager.connection.ConnectionManager;
import bpm.connection.manager.connection.jdbc.VanillaJdbcConnection;
import bpm.connection.manager.connection.jdbc.VanillaPreparedStatement;
import bpm.vanilla.platform.hibernate.HibernateDaoSupport;
import bpm.vanilla.repository.beans.InfoConnections;
import bpm.vanilla.repository.beans.JdbcConnectionProvider;

public class RunnableGroupDAO extends HibernateDaoSupport {
	
	public void addRunnableGroup(RunnableGroup r) {
		getHibernateTemplate().save(r);
	}

	public void removeRunnableGroup(RunnableGroup r) {
		getHibernateTemplate().delete(r);
	}

	@SuppressWarnings("unchecked")
	public Collection<RunnableGroup> getAllowedGroups(int directoryItem) {
		return getHibernateTemplate().find("from RunnableGroup where directoryItemId=" + directoryItem);
	}

	public boolean get(int repositoryId, int directoryItemId, int groupId) throws Exception {
		InfoConnections info = JdbcConnectionProvider.getInstance().getInfoConnection(repositoryId);
		
		VanillaJdbcConnection connection = ConnectionManager.getInstance().getJdbcConnection(info.getRepositoryDBUrl(), info.getLogin(), info.getPassword(), info.getDriver());
		
		VanillaPreparedStatement statement = connection.createStatement();
		ResultSet resultSet = statement.executeQuery("select secRun.id as runId"
				+ ", secRun.item_id as runItem"
				+ ", secRun.group_id as runGroup"
				+ " from sec_runnable_object secRun where secRun.item_id=" + directoryItemId 
				+ " and secRun.group_id=" + groupId);
		
		boolean canRun = resultSet.next();

		resultSet.close();
		statement.close();
		ConnectionManager.getInstance().returnJdbcConnection(connection);
		
		return canRun;
		
	}

	@SuppressWarnings("unchecked")
	public RunnableGroup find(int directoryItemId, int groupid) {
		List<RunnableGroup> l = getHibernateTemplate().find("from RunnableGroup where directoryItemId=" + directoryItemId + " and groupId=" + groupid);
		if (l.isEmpty()) {
			return null;
		}
		return (RunnableGroup) l.get(0);
	}

	@SuppressWarnings("unchecked")
	public Collection<RunnableGroup> getByGroupId(int groupId) {
		return getHibernateTemplate().find("from RunnableGroup where groupId=" + groupId);
	}

	public void delete(RunnableGroup r) {
		getHibernateTemplate().delete(r);

	}
}
