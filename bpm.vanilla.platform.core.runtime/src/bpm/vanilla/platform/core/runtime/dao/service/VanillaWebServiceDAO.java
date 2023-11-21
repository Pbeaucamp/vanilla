package bpm.vanilla.platform.core.runtime.dao.service;

import java.util.List;

import bpm.vanilla.platform.core.beans.service.ServiceTransformationDefinition;
import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class VanillaWebServiceDAO extends HibernateDaoSupport {
	
	public void saveServiceDefinition(ServiceTransformationDefinition definition) {
		getHibernateTemplate().save(definition);
	}

	public void deleteServiceDefinition(int definitionId) {
		getHibernateTemplate().delete(getServiceDefinition(definitionId));
	}
	
	public void updateServiceDefinition(ServiceTransformationDefinition definition) {
		getHibernateTemplate().update(definition);
	}
	
	public ServiceTransformationDefinition getServiceDefinition(int definitionId) {
		return (ServiceTransformationDefinition) getHibernateTemplate().find("from ServiceTransformationDefinition where id = " + definitionId).get(0);
	}
	
	@SuppressWarnings("unchecked")
	public List<ServiceTransformationDefinition> getServiceDefinitions(){
		return (List<ServiceTransformationDefinition>) getHibernateTemplate().find("from ServiceTransformationDefinition");
	}
}
