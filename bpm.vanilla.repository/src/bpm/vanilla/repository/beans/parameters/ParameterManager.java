package bpm.vanilla.repository.beans.parameters;

import java.util.List;

import bpm.vanilla.platform.core.repository.ILinkedParameter;
import bpm.vanilla.platform.core.repository.Parameter;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class ParameterManager {
	private ParameterDAO parameterDao;

	public ParameterDAO getParameterDao() {
		return parameterDao;
	}

	public void setParameterDao(ParameterDAO parameterDao) {
		this.parameterDao = parameterDao;
	}

	public void addParameter(Parameter p) {
		parameterDao.save(p);
	}

	public void deleteParameter(Parameter p) {
		parameterDao.delete(p);
	}

	public List<Parameter> getParametesFor(RepositoryItem item) {
		return parameterDao.findDirectoryItemId(item.getId());
	}

	public Parameter getForId(int id) {
		return parameterDao.getParameter(id);
	}

	public List<Parameter> getParametesForDirectoryItemId(int dirItID) {
		return parameterDao.findDirectoryItemId(dirItID);
	}

	public void update(Parameter param) {

		parameterDao.update(param);

		List<ILinkedParameter> l = parameterDao.getLinkedParameterForParameterId(param.getId());

		for (ILinkedParameter p : l) {
			boolean stillExists = false;
			for (ILinkedParameter lp : param.getRequestecParameters()) {
				if (lp.getId() == p.getId()) {
					stillExists = true;
					break;
				}
			}

			if (!stillExists) {
				parameterDao.delete(p);
			}
		}

		for (ILinkedParameter p : param.getRequestecParameters()) {
			parameterDao.saveorupdate(p);
		}

	}

}
