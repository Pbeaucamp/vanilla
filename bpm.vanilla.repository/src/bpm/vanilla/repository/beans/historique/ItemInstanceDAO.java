package bpm.vanilla.repository.beans.historique;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.ItemInstance;
import bpm.vanilla.platform.core.components.gateway.GatewayRuntimeState;
import bpm.vanilla.platform.core.components.report.ReportRuntimeState;
import bpm.vanilla.platform.core.components.workflow.WorkflowInstanceState;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

import com.thoughtworks.xstream.XStream;

public class ItemInstanceDAO extends HibernateDaoSupport {

	private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

	@SuppressWarnings("unchecked")
	public List<ItemInstance> getItemInstances(int itemId, boolean lightWeight) {
		List<ItemInstance> instances = (List<ItemInstance>) getHibernateTemplate().find("FROM ItemInstance WHERE itemId = " + itemId + " ORDER BY runDate DESC");
		if (instances != null && !instances.isEmpty()) {
			if (!lightWeight) {
				for (ItemInstance instance : instances) {
					buildInstance(instance);
				}
			}
			return instances;
		}
		else {
			return new ArrayList<ItemInstance>();
		}
	}

	@SuppressWarnings("unchecked")
	public List<ItemInstance> getItemInstances(int start, int end, int itemType) {
		List<ItemInstance> instances = (List<ItemInstance>) getHibernateTemplate().find("FROM ItemInstance WHERE itemType = " + itemType + " ORDER BY runDate DESC");
		if (instances != null && !instances.isEmpty()) {
			List<ItemInstance> instancesToReturn = new ArrayList<ItemInstance>();
			for (int i = 0; i < instances.size(); i++) {
				if (i < start) {
					continue;
				}
				else if (i >= end) {
					break;
				}
				ItemInstance instance = instances.get(i);
				buildInstance(instance);
				instancesToReturn.add(instance);
			}
			return instancesToReturn;
		}
		else {
			return new ArrayList<ItemInstance>();
		}
	}

	@SuppressWarnings("unchecked")
	public List<ItemInstance> getItemInstances(int itemId, Date startDate, Date endDate, Integer groupId) {
		StringBuffer query = new StringBuffer("FROM ItemInstance WHERE itemId = " + itemId);
		if (startDate != null) {
			query.append(" AND runDate >= '" + df.format(startDate) + "'");
		}
		if (endDate != null) {
			query.append(" AND runDate <= '" + df.format(endDate) + "'");
		}
		if (groupId != null) {
			query.append(" AND groupId = " + groupId);
		}
		query.append(" ORDER BY runDate DESC");

		List<ItemInstance> instances = (List<ItemInstance>) getHibernateTemplate().find(query.toString(), 15);
		if (instances != null && !instances.isEmpty()) {
			for (ItemInstance instance : instances) {
				buildInstance(instance);
			}
			return instances;
		}
		else {
			return new ArrayList<ItemInstance>();
		}
	}

	@SuppressWarnings("unchecked")
	public ItemInstance getItemInstance(int instanceId) {
		List<ItemInstance> instances = (List<ItemInstance>) getHibernateTemplate().find("FROM ItemInstance WHERE id = " + instanceId);
		if (instances != null && !instances.isEmpty()) {
			ItemInstance instance = instances.get(0);
			buildInstance(instance);
			return instance;
		}
		else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public ItemInstance getWorkflowInstance(int itemId, String key) {
		List<ItemInstance> instances = (List<ItemInstance>) getHibernateTemplate().find("FROM ItemInstance WHERE itemId = " + itemId);
		if (instances != null && !instances.isEmpty()) {
			for (ItemInstance instance : instances) {
				buildInstance(instance);

				WorkflowInstanceState state = (WorkflowInstanceState) instance.getState();
				if (state.getProcessInstanceUUID().equals(key)) {
					return instance;
				}
			}
		}

		return null;
	}

	public int save(ItemInstance instance) throws Exception {
		buildModel(instance);
		instance.setId((Integer) getHibernateTemplate().save(instance));
		return instance.getId();
	}

	public void delete(List<ItemInstance> instances) {
		if (instances != null) {
			for (ItemInstance instance : instances) {
				delete(instance);
			}
		}
	}

	public void delete(ItemInstance instance) {
		getHibernateTemplate().delete(instance);
	}

	public void update(ItemInstance instance) {
		buildModel(instance);
		getHibernateTemplate().update(instance);
	}

	private void buildInstance(ItemInstance instance) {
		if (instance.getStateXml() != null && !instance.getStateXml().isEmpty()) {
			if (instance.getItemType() == IRepositoryApi.GTW_TYPE) {
				instance.setState((GatewayRuntimeState) new XStream().fromXML(instance.getStateXml()));
			}
			else if (instance.getItemType() == IRepositoryApi.BIW_TYPE) {
				instance.setState((WorkflowInstanceState) new XStream().fromXML(instance.getStateXml()));
			}
			else if (instance.getItemType() == IRepositoryApi.CUST_TYPE) {
				instance.setState((ReportRuntimeState) new XStream().fromXML(instance.getStateXml()));
			}
		}
	}

	private void buildModel(ItemInstance instance) {
		if (instance.getState() != null) {
			instance.setStateXml(new XStream().toXML(instance.getState()));
		}
	}

	@SuppressWarnings("unchecked")
	public HashMap<RepositoryItem, Double> getTopTenItemConsumer(Integer itemType, Date startDate, Date endDate, Integer groupId) {
		boolean first = true;

		StringBuffer query = new StringBuffer("SELECT itemId, avg(duration) FROM ItemInstance");
		if (itemType != null) {
			if (first) {
				query.append(" WHERE");
				first = false;
			}
			else {
				query.append(" AND");
			}
			query.append(" itemType = " + itemType);
		}
		if (startDate != null) {
			if (first) {
				query.append(" WHERE");
				first = false;
			}
			else {
				query.append(" AND");
			}
			query.append(" runDate >= '" + df.format(startDate) + "'");
		}
		if (endDate != null) {
			if (first) {
				query.append(" WHERE");
				first = false;
			}
			else {
				query.append(" AND");
			}
			query.append(" runDate <= '" + df.format(endDate) + "'");
		}
		if (groupId != null) {
			if (first) {
				query.append(" WHERE");
				first = false;
			}
			else {
				query.append(" AND");
			}
			query.append(" groupId = " + groupId);
		}
		query.append(" GROUP BY itemId ORDER BY avg(duration) DESC");

		List<Object[]> instances = (List<Object[]>) getHibernateTemplate().find(query.toString());

		HashMap<RepositoryItem, Double> items = new LinkedHashMap<RepositoryItem, Double>();
		if (instances != null && !instances.isEmpty()) {
			int i = 0;
			for (Object[] item : instances) {
				if (i >= 10) {
					break;
				}
				
				int itemId = (Integer) item[0];
				Double duration = (Double) item[1];

				List<RepositoryItem> l = (List<RepositoryItem>) getHibernateTemplate().find("from RepositoryItem d where id=" + itemId);
				if (l != null && !l.isEmpty()) {
					RepositoryItem repItem = l.get(0);
					items.put(repItem, duration);
					
					i++;
				}
			}

			return items;
		}
		return items;
	}
}
