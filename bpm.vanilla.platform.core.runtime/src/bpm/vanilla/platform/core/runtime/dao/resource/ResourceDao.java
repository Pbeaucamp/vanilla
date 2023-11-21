package bpm.vanilla.platform.core.runtime.dao.resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.thoughtworks.xstream.XStream;

import bpm.vanilla.platform.core.beans.resources.ClassRule;
import bpm.vanilla.platform.core.beans.resources.IRule;
import bpm.vanilla.platform.core.beans.resources.Resource;
import bpm.vanilla.platform.core.beans.resources.Resource.TypeResource;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanilla.platform.hibernate.HibernateDaoSupport;
import bpm.vanilla.platform.logging.IVanillaLogger;
import bpm.workflow.commons.beans.ResourceWrapper;
import bpm.workflow.commons.utils.VariableHelper;

public class ResourceDao extends HibernateDaoSupport {

	private XStream xstream = new XStream();

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

	public void testVariable(IVanillaLogger logger, Locale locale, Variable value) throws Exception {
		List<Variable> currentVariables = getVariables();
		VariableHelper.testVariable(logger, locale, value, currentVariables);
	}
	
	@SuppressWarnings("unchecked")
	public List<? extends Resource> getResources(TypeResource type) {
		List<ResourceWrapper> items = getHibernateTemplate().find("from ResourceWrapper where type=" + type.getType());
		return parse(items);
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

	public ClassRule addOrUpdateClassRule(ClassRule classRule) {
		build(classRule);
		
		if (classRule.getId() > 0) {
			getHibernateTemplate().update(classRule);
		}
		else {
			Integer resourceId = (Integer) getHibernateTemplate().save(classRule);
			classRule.setId(resourceId);
		}
		return classRule;
	}

	@SuppressWarnings("unchecked")
	public List<ClassRule> getClassRule(String identifiant) {
		List<ClassRule> items = getHibernateTemplate().find("from ClassRule where mainClassIdentifiant='" + identifiant + "' order by id asc");
		return parseRules(items);
	}

	private void build(ClassRule item) {
		String model = xstream.toXML(item.getRule());
		item.setModel(model);
	}

	private List<ClassRule> parseRules(List<ClassRule> items) {
		if (items != null) {
			for (ClassRule item : items) {
				if (item.getModel() != null && !item.getModel().isEmpty()) {
					IRule rule = (IRule) xstream.fromXML(item.getModel());
					item.setRule(rule);
				}
			}
		}

		return items;
	}
}
