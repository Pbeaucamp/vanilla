package bpm.vanillahub.runtime.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.thoughtworks.xstream.XStream;

import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.resources.DatabaseServer;
import bpm.vanilla.platform.core.beans.resources.ListOfValues;
import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Resource;
import bpm.vanilla.platform.core.beans.resources.Resource.TypeResource;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanilla.platform.core.utils.MD5Helper;
import bpm.vanilla.platform.hibernate.HibernateDaoSupport;
import bpm.vanilla.platform.logging.IVanillaLogger;
import bpm.vanillahub.core.beans.resources.ApplicationServer;
import bpm.vanillahub.core.beans.resources.Certificat;
import bpm.vanillahub.core.beans.resources.Connector;
import bpm.vanillahub.core.beans.resources.FileXSD;
import bpm.vanillahub.core.beans.resources.ServerMail;
import bpm.vanillahub.core.beans.resources.SocialNetworkServer;
import bpm.vanillahub.core.beans.resources.Source;
import bpm.vanillahub.runtime.run.transform.CarburantManager;
import bpm.vanillahub.runtime.run.transform.XMLManager;
import bpm.workflow.commons.beans.ResourceWrapper;
import bpm.workflow.commons.resources.Cible;
import bpm.workflow.commons.resources.UserWrapper;
import bpm.workflow.commons.utils.VariableHelper;

public class ResourceDao extends HibernateDaoSupport {

	private XStream xstream = new XStream();

	// @SuppressWarnings("unchecked")
	// private List<Resource> getResources() {
	// List<ResourceWrapper> items =
	// getHibernateTemplate().find("from Resource");
	// return parse(items);
	// }

	@SuppressWarnings("unchecked")
	public List<? extends Resource> getResources(TypeResource type) {
		if (type == TypeResource.CONNECTOR) {
			List<Connector> connectors = new ArrayList<Connector>();
//			connectors.add(new Connector(ConnectorDefinition.CONNECTOR_NAME));
			connectors.add(new Connector(XMLManager.CONNECTOR_NAME));
			connectors.add(new Connector(CarburantManager.CONNECTOR_NAME));
			return connectors;
		}
		else {
			List<ResourceWrapper> items = getHibernateTemplate().find("from ResourceWrapper where type=" + type.getType());
			return parse(items);
		}
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

	public Resource duplicate(int resourceId, String name) {
		Resource resource = getResourceById(resourceId);
		resource.setId(0);
		resource.setName(name);
		return manageResource(resource, false);
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

	@SuppressWarnings("unchecked")
	public List<Variable> getVariables() {
		return (List<Variable>) getResources(TypeResource.VARIABLE);
	}

	@SuppressWarnings("unchecked")
	public List<Parameter> getParameters() {
		return (List<Parameter>) getResources(TypeResource.PARAMETER);
	}

	@SuppressWarnings("unchecked")
	public List<Cible> getCibles() {
		return (List<Cible>) getResources(TypeResource.CIBLE);
	}

	@SuppressWarnings("unchecked")
	public List<Certificat> getCertificats() {
		return (List<Certificat>) getResources(TypeResource.CERTIFICAT);
	}

	@SuppressWarnings("unchecked")
	public List<Source> getSources() {
		return (List<Source>) getResources(TypeResource.SOURCE);
	}

	@SuppressWarnings("unchecked")
	public List<ServerMail> getServerMails() {
		return (List<ServerMail>) getResources(TypeResource.MAIL);
	}

	@SuppressWarnings("unchecked")
	public List<FileXSD> getFileXSDs() {
		return (List<FileXSD>) getResources(TypeResource.XSD);
	}

	@SuppressWarnings("unchecked")
	public List<DatabaseServer> getDatabaseServers() {
		return (List<DatabaseServer>) getResources(TypeResource.DATABASE_SERVER);
	}

	@SuppressWarnings("unchecked")
	public List<ApplicationServer> getApplicationServers() {
		return (List<ApplicationServer>) getResources(TypeResource.APPLICATION_SERVER);
	}

	@SuppressWarnings("unchecked")
	public List<SocialNetworkServer> getSocialServer() {
		return (List<SocialNetworkServer>) getResources(TypeResource.SOCIAL_SERVER);
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

	// public File addFile(Locale locale, String itemName) throws Exception {
	// return fileManager.addFile(locale, itemName);
	// }
	//
	// public File getFile(String filePath) {
	// return fileManager.getFile(filePath);
	// }
}
