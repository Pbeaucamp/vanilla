package bpm.smart.runtime.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Repository;

import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.resources.ListOfValues;
import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Resource;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanilla.platform.core.beans.resources.Resource.TypeResource;
import bpm.vanilla.platform.core.utils.MD5Helper;
import bpm.vanilla.platform.hibernate.HibernateDaoSupport;
import bpm.vanilla.platform.logging.IVanillaLogger;
import bpm.workflow.commons.beans.ItemResourceLink;
import bpm.workflow.commons.beans.ResourceWrapper;
import bpm.workflow.commons.beans.Schedule;
import bpm.workflow.commons.resources.UserWrapper;
import bpm.workflow.commons.utils.VariableHelper;

import com.thoughtworks.xstream.XStream;

@Repository
public class SmartDao extends HibernateDaoSupport {
	
	private XStream xstream = new XStream();

	@SuppressWarnings("unchecked")
	public List<? extends Resource> getResources(TypeResource type) {
		List<ResourceWrapper> items = getHibernateTemplate().find("from ResourceWrapper where type=" + type.getType());
		return parse(items);
	}

	@SuppressWarnings("unchecked")
	public Resource getResourceById(int id) {
		List<ResourceWrapper> items = getHibernateTemplate().find("from ResourceWrapper where id=" + id);
		List<Resource> resources = parse(items);
		if (resources.isEmpty()) {
			return null;
		}
		return resources.get(0);
	}

	public Resource manageResource(Resource item, boolean edit) {
		if (edit) {
			return update(item);
		}
		else {
			return add(item);
		}
	}

	private Resource add(Resource item) {
		ResourceWrapper resourceWrapper = build(item);
		Integer resourceId = (Integer) getHibernateTemplate().save(resourceWrapper);
		item.setId(resourceId);
		return item;
	}

	private Resource update(Resource item) {
		ResourceWrapper resourceWrapper = build(item);
		getHibernateTemplate().update(resourceWrapper);
		return item;
	}

	public void delete(Resource item) {
		ResourceWrapper resourceWrapper = build(item);
		getHibernateTemplate().delete(resourceWrapper);
	}

	private ResourceWrapper build(Resource item) {
		String model = xstream.toXML(item);
		return new ResourceWrapper(item.getId(), item.getTypeResource(), model);
	}

	private List<Resource> parse(List<ResourceWrapper> items) {
		List<Resource> resources = new ArrayList<>();
		if (items != null) {
			for (ResourceWrapper item : items) {
				if (item.getModel() != null && !item.getModel().isEmpty()) {
					Resource resource = (Resource) xstream.fromXML(item.getModel());
					resource.setId(item.getId());
					resources.add(resource);
				}
			}
		}

		return resources;
	}

	@SuppressWarnings("unchecked")
	public List<Variable> getVariables() {
		return (List<Variable>) getResources(TypeResource.VARIABLE);
	}

	@SuppressWarnings("unchecked")
	public List<Parameter> getParameters() {
		return (List<Parameter>) getResources(TypeResource.PARAMETER);
	}

	@SuppressWarnings("unchecked")
	public List<ListOfValues> getListOfValues() {
		return (List<ListOfValues>) getResources(TypeResource.LOV);
	}

	public List<Variable> initVariables(IVanillaLogger logger, Locale locale, List<Parameter> parameters, List<Variable> variables, List<Variable> currentVariables) {
		return VariableHelper.initVariables(logger, locale, parameters, variables, currentVariables);
	}

	public void testVariable(IVanillaLogger logger, Locale locale, Variable value) throws Exception {
		List<Variable> currentVariables = getVariables();
		VariableHelper.testVariable(logger, locale, value, currentVariables);
	}
	
	public User manageUser(User user, boolean edit) {
		String md5Password = MD5Helper.encode(user.getPassword());
		user.setPassword(md5Password);

		if (edit) {
			Resource resource = update(buildUserWrapper(user));
			user.setId(resource.getId());
			return user;
		}
		else {
			Resource resource = add(buildUserWrapper(user));
			user.setId(resource.getId());
			return user;
		}
	}

	public void delete(User user) {
		Resource resource = buildUserWrapper(user);
		delete(resource);
	}

	public User getUser(String login) throws Exception {
		List<User> users = getUser();
		if (users != null) {
			for (User user : users) {
				if (user.getLogin().equals(login)) {
					return user;
				}
			}
		}

		return null;
	}

	public User getUser(int userId) throws Exception {
		Resource resource = getResourceById(userId);
		UserWrapper wrapper = resource != null ? (UserWrapper) getResourceById(userId) : null;
		return buildUser(wrapper);
	}

	private UserWrapper buildUserWrapper(User user) {
		return new UserWrapper(user.getId(), user.getLogin(), user.getPassword(), user.getBusinessMail(), user.getFunction(), user.getCreation(), user.isSuperUser());
	}

	private User buildUser(UserWrapper wrapper) {
		if (wrapper != null) {
			User user = new User();
			user.setId(wrapper.getId());
			user.setLogin(wrapper.getName());
			user.setPassword(wrapper.getPassword());
			user.setCreation(wrapper.getCreationDate());
			user.setBusinessMail(wrapper.getEmail());
			user.setFunction(wrapper.getFonction());
			user.setLocale(wrapper.getLocale());
			user.setSuperUser(wrapper.isAdmin());
			return user;
		}
		else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<User> getUser() {
		List<User> users = new ArrayList<>();
		List<UserWrapper> wrappers = (List<UserWrapper>) getResources(TypeResource.USER);
		if (wrappers != null) {
			for (UserWrapper wrapper : wrappers) {
				users.add(buildUser(wrapper));
			}
		}
		return users;
	}

	public Schedule add(Schedule item) {
		Integer id = (Integer) getHibernateTemplate().save(item);
		item.setId(id);

		save(id, item.getParameters());

		return item;
	}

	public void update(Schedule item) {
		getHibernateTemplate().update(item);

		update(item.getId(), item.getParameters());
	}

	public void delete(Schedule item) {
		deleteLinks(item.getId());
		getHibernateTemplate().delete(item);
	}
	
	private void save(int itemId, List<? extends Resource> resources) {
		if (resources != null) {
			for (Resource resource : resources) {
				int resourceId = resource.getId();
				if (resourceId > 0) {
					resourceId = manageResource(resource, false).getId();
				}
				getHibernateTemplate().save(new ItemResourceLink(itemId, resourceId));
			}
		}
	}

	private void update(int itemId, List<? extends Resource> resources) {
		deleteLinks(itemId);
		save(itemId, resources);
	}
	
	private void deleteLinks(int itemId) {
		List<ItemResourceLink> links = getLinks(itemId);

		if (links != null) {
			for (ItemResourceLink link : links) {
				getHibernateTemplate().delete(link);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private List<ItemResourceLink> getLinks(int itemId) {
		return (List<ItemResourceLink>) getHibernateTemplate().find("from ItemResourceLink where itemId=" + itemId);
	}
	
	public void buildSchedule(List<Schedule> items) {
		if (items != null) {
			for (Schedule item : items) {
				List<ItemResourceLink> links = getLinks(item.getId());
				if (links != null) {
					List<Parameter> parameters = new ArrayList<>();
					for (ItemResourceLink link : links) {
						Resource resource = getResourceById(link.getResourceId());
						if (resource != null && resource instanceof Parameter) {
							parameters.add((Parameter) resource);
						}
					}
					item.setParameters(parameters);
				}
			}
		}
	}
}
