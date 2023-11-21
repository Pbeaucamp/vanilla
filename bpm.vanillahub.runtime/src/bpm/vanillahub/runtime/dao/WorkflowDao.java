package bpm.vanillahub.runtime.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import bpm.vanilla.platform.core.beans.DataSort;
import bpm.vanilla.platform.core.beans.DataWithCount;
import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Resource;
import bpm.vanilla.platform.hibernate.HibernateDaoSupport;
import bpm.vanillahub.runtime.ComponentVanillaHub;
import bpm.workflow.commons.beans.ActivityLog;
import bpm.workflow.commons.beans.ItemResourceLink;
import bpm.workflow.commons.beans.Schedule;
import bpm.workflow.commons.beans.Workflow;
import bpm.workflow.commons.beans.WorkflowInstance;
import bpm.workflow.commons.beans.WorkflowModel;
import bpm.workflow.commons.utils.SchedulerUtils;

import com.thoughtworks.xstream.XStream;

public class WorkflowDao extends HibernateDaoSupport {

	private XStream xstream = new XStream();
	private ComponentVanillaHub component;

	public void setComponent(ComponentVanillaHub component) {
		this.component = component;
	}

	public List<Workflow> getWorkflows(boolean lightWeight) {
		return getWorkflows(null, 0, 1000, lightWeight, null).getItems();
	}

	public DataWithCount<Workflow> getWorkflows(String query, int firstResult, int length, boolean lightWeight, DataSort sort) {
		List<Object> parameters = new ArrayList<Object>();
		
		StringBuffer buf = new StringBuffer();
		buf.append("FROM Workflow");
		if (query != null && !query.isEmpty()) {
			buf.append(" WHERE name LIKE ?");
			parameters.add("%" + query + "%");
		}
		
		if (sort != null) {
			buf.append(" ORDER BY " + sort.getColumn() + " " + (sort.isAscending() ? "ASC" : "DESC"));
		}
		
		long itemsCount = getHibernateTemplate().count("SELECT count(*) " + buf.toString(), parameters);
		
		List<Workflow> items = getHibernateTemplate().findWithPag(buf.toString(), firstResult, firstResult + length, parameters);
		buildWorkflow(items, lightWeight);
		return new DataWithCount<Workflow>(items, itemsCount);
	}

	@SuppressWarnings("unchecked")
	public Workflow getWorkflow(int id, boolean lightWeight) {
		List<Workflow> items = getHibernateTemplate().find("from Workflow where id=" + id);
		buildWorkflow(items, lightWeight);
		if (items.isEmpty()) {
			return null;
		}
		return items.get(0);
	}

	public Workflow manageWorkflow(Workflow currentWorkflow, boolean modify) {
		if (modify) {
			return update(currentWorkflow);
		}
		else {
			return add(currentWorkflow);
		}
	}

	private Workflow add(Workflow item) {
		build(item);
		Integer itemId = (Integer) getHibernateTemplate().save(item);
		item.setId(itemId);
		saveOrUpdateSchedule(item);
		return item;
	}

	private Workflow update(Workflow item) {
		build(item);
		getHibernateTemplate().update(item);
		saveOrUpdateSchedule(item);
		return item;
	}

	public void delete(Workflow item) {
		if (item.getSchedule() != null && item.getSchedule().getId() > 0) {
			delete(item.getSchedule());
		}

		deleteInstances(item.getId());

		getHibernateTemplate().delete(item);
	}

	public Workflow duplicate(int workflowId, String name) {
		Workflow workflow = getWorkflow(workflowId, false);
		workflow.setId(0);
		workflow.setName(name);
		return manageWorkflow(workflow, false);
	}

	@SuppressWarnings("unchecked")
	public List<WorkflowInstance> getInstances(int workflowId, boolean lightWeight) {
		List<WorkflowInstance> items = getHibernateTemplate().find("from WorkflowInstance where workflowId=" + workflowId + " ORDER BY endDate DESC", 50);
		if (!lightWeight) {
			buildInstance(items);
		}
		return items;
	}

	@SuppressWarnings("unchecked")
	public WorkflowInstance getLastInstance(int workflowId, boolean lightWeight) {
		List<WorkflowInstance> items = getHibernateTemplate().find("from WorkflowInstance where workflowId=" + workflowId + " ORDER BY endDate DESC", 1);
		if (!lightWeight) {
			buildInstance(items);
		}
		return items != null && !items.isEmpty() ? items.get(0) : null;
	}

	@SuppressWarnings("unchecked")
	public WorkflowInstance getInstance(int id, boolean lightWeight) {
		List<WorkflowInstance> items = getHibernateTemplate().find("from WorkflowInstance where id=" + id);
		if (!lightWeight) {
			buildInstance(items);
		}
		if (items.isEmpty()) {
			return null;
		}
		return items.get(0);
	}

	public List<ActivityLog> getInstanceLogs(int instanceId) {
		WorkflowInstance instance = getInstance(instanceId, false);
		return instance.getActivityLogs();
	}

	public WorkflowInstance add(WorkflowInstance item) {
		build(item);
		Integer id = (Integer) getHibernateTemplate().save(item);
		item.setId(id);
		return item;
	}

	public void update(WorkflowInstance item) {
		build(item);
		getHibernateTemplate().update(item);
	}

	public void delete(WorkflowInstance item) {
		getHibernateTemplate().delete(item);
	}

	@SuppressWarnings("unchecked")
	private Schedule getSchedule(int workflowId) {
		List<Schedule> items = getHibernateTemplate().find("from Schedule where workflowId=" + workflowId);
		buildSchedule(items);
		if (items.isEmpty()) {
			return null;
		}
		return items.get(0);
	}

	private Schedule add(Schedule item) {
		Integer id = (Integer) getHibernateTemplate().save(item);
		item.setId(id);

		save(id, item.getParameters());

		return item;
	}

	private void update(Schedule item) {
		getHibernateTemplate().update(item);

		update(item.getId(), item.getParameters());
	}

	private void delete(Schedule item) {
		deleteLinks(item.getId());
		getHibernateTemplate().delete(item);
	}

	private void build(Workflow item) {
		String model = xstream.toXML(item.getWorkflowModel());
		item.setModel(model);
	}
	
	private void saveOrUpdateSchedule(Workflow item) {
		Schedule schedule = item.getSchedule();
		if (schedule != null) {
			schedule.setWorkflowId(item.getId());
			if (schedule.getId() > 0) {
				update(schedule);
			}
			else {
				add(schedule);
			}
		}
	}

	private void build(WorkflowInstance item) {
		String model = xstream.toXML(item.getActivityLogs());
		item.setModelLogs(model);
	}

	private void save(int itemId, List<? extends Resource> resources) {
		if (resources != null) {
			for (Resource resource : resources) {
				int resourceId = resource.getId();
				if (resourceId > 0) {
					resourceId = component.getResourceDao().manageResource(resource, false).getId();
				}
				getHibernateTemplate().save(new ItemResourceLink(itemId, resourceId));
			}
		}
	}

	private void update(int itemId, List<? extends Resource> resources) {
		deleteLinks(itemId);
		save(itemId, resources);
	}

	@SuppressWarnings("unchecked")
	private List<ItemResourceLink> getLinks(int itemId) {
		return (List<ItemResourceLink>) getHibernateTemplate().find("from ItemResourceLink where itemId=" + itemId);
	}

	private void deleteLinks(int itemId) {
		List<ItemResourceLink> links = getLinks(itemId);

		if (links != null) {
			for (ItemResourceLink link : links) {
				getHibernateTemplate().delete(link);
			}
		}
	}

	private void deleteInstances(int workflowId) {
		List<WorkflowInstance> instances = getInstances(workflowId, true);

		if (instances != null) {
			for (WorkflowInstance instance : instances) {
				getHibernateTemplate().delete(instance);
			}
		}
	}

	private void buildWorkflow(List<Workflow> items, boolean lightWeight) {
		if (items != null) {
			for (Workflow item : items) {
				if (item.getModel() != null && !item.getModel().isEmpty()) {

					try {
						WorkflowInstance lastRun = getLastInstance(item.getId(), lightWeight);
						item.setLastRun(lastRun);

						Schedule schedule = getSchedule(item.getId());
						item.setSchedule(schedule);

						item.setNextExecution(SchedulerUtils.getNextExecution(new Date(), item.getSchedule()));

						WorkflowModel workflowModel = (WorkflowModel) xstream.fromXML(item.getModel());
						item.setWorkflowModel(workflowModel);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

//			Collections.sort(items, new Comparator<Workflow>() {
//				@Override
//				public int compare(Workflow workflow1, Workflow workflow2) {
//					return workflow1.getName().compareTo(workflow2.getName());
//				}
//			});
		}
	}

	@SuppressWarnings("unchecked")
	private void buildInstance(List<WorkflowInstance> items) {
		if (items != null) {
			for (WorkflowInstance item : items) {
				if (item.getModelLogs() != null && !item.getModelLogs().isEmpty()) {
					List<ActivityLog> logs = (List<ActivityLog>) xstream.fromXML(item.getModelLogs());
					item.setActivityLogs(logs);
				}
			}
		}
	}

	private void buildSchedule(List<Schedule> items) {
		if (items != null) {
			for (Schedule item : items) {
				List<ItemResourceLink> links = getLinks(item.getId());
				if (links != null) {
					List<Parameter> parameters = new ArrayList<>();
					for (ItemResourceLink link : links) {
						Resource resource = component.getResourceDao().getResourceById(link.getResourceId());
						if (resource != null && resource instanceof Parameter) {
							parameters.add((Parameter) resource);
						}
					}
					item.setParameters(parameters);
				}
			}
		}
	}

	// private List<WorkflowInstance> sort(List<WorkflowInstance> instances) {
	// Collections.sort(instances, new Comparator<WorkflowInstance>() {
	//
	// @Override
	// public int compare(WorkflowInstance o1, WorkflowInstance o2) {
	// if (o2.getEndDate() != null && o1.getEndDate() != null) {
	// return o2.getEndDate().compareTo(o1.getEndDate());
	// }
	// return o2.getEndDate() == null ? -1 : 1;
	// }
	// });
	// return instances;
	// }
}
