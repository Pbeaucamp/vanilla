package bpm.vanilla.repository.beans.parameters;

import java.util.List;

import bpm.vanilla.platform.core.repository.ILinkedParameter;
import bpm.vanilla.platform.core.repository.Parameter;
import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class ParameterDAO extends HibernateDaoSupport {

	@SuppressWarnings("unchecked")
	public List<Parameter> findDirectoryItemId(int directoryItemId) {
		List<Parameter> l = getHibernateTemplate().find("from Parameter where directoryItemId=" + directoryItemId);

		for (Parameter p : l) {
			List<ILinkedParameter> lk = getLinkedParameterForParameterId(p.getId());
			p.setLinkedParameters(lk);
		}

		return l;
	}

	@SuppressWarnings("unchecked")
	public Parameter getParameter(int id) {
		List<Parameter> l = getHibernateTemplate().find("from Parameter where id=" + id);
		if (l.isEmpty()) {
			return null;
		}

		for (Parameter p : l) {
			List<ILinkedParameter> lk = getLinkedParameterForParameterId(p.getId());
			p.setLinkedParameters(lk);

		}

		return l.get(0);
	}

	@SuppressWarnings("unchecked")
	public List<ILinkedParameter> getLinkedParameterForParameterId(int parameterId) {
		List<ILinkedParameter> l = getHibernateTemplate().find("from ILinkedParameter where providedParameterId=" + parameterId);

		for (ILinkedParameter p : l) {
			if (p.getProviderParameterId() > 0) {
				List h = getHibernateTemplate().find("from Parameter where id=" + p.getProviderParameterId());
				if (!h.isEmpty()) {
					p.setProviderParameterName(((Parameter) h.get(0)).getName());
				}
			}
		}
		return l;
	}

	public int save(Parameter d) {
		int i = (Integer) getHibernateTemplate().save(d);
		d.setId(i);
		return i;
	}

	public void delete(Parameter d) {
		getHibernateTemplate().delete(d);
	}

	public void delete(ILinkedParameter p) {
		getHibernateTemplate().delete(p);

	}

	public void saveorupdate(ILinkedParameter p) {
		getHibernateTemplate().saveOrUpdate(p);

	}

	public void update(Parameter param) {
		getHibernateTemplate().update(param);

	}

}
