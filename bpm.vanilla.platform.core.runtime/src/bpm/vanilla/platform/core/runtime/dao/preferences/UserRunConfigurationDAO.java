package bpm.vanilla.platform.core.runtime.dao.preferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bpm.vanilla.platform.core.IObjectIdentifier;
import bpm.vanilla.platform.core.beans.UserRunConfiguration;
import bpm.vanilla.platform.core.beans.UserRunConfigurationParameter;
import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class UserRunConfigurationDAO extends HibernateDaoSupport {

	public int save(UserRunConfiguration userRunConfiguration) {
		List<UserRunConfigurationBean> beans = createBeansFromRunConfig(userRunConfiguration);
		getHibernateTemplate().saveOrUpdateAll(beans);
		return beans.get(0).getRunId();
	}

	public void delete(UserRunConfiguration userRunConfiguration) {
		List<UserRunConfigurationBean> beans = createBeansFromRunConfig(userRunConfiguration);
		getHibernateTemplate().deleteAll(beans);
	}

	public UserRunConfiguration getUserRunConfigurationById(int id) {
		return createRunConfigFromBean(getHibernateTemplate().find("from UserRunConfigurationBean u where u.runId = " + id));
	}

	public List<UserRunConfiguration> getUserRunConfigurations() {
		return createRunConfigsFromBeans(getHibernateTemplate().find("from UserRunConfigurationBean"));
	}

	public List<UserRunConfiguration> getUserRunConfigurationByUserId(int userId) {
		return createRunConfigsFromBeans(getHibernateTemplate().find("from UserRunConfigurationBean u where u.userId = " + userId));
	}

	public List<UserRunConfiguration> getUserRunConfigurationByUserIdObjectId(int userId, IObjectIdentifier objectId) {
		return createRunConfigsFromBeans(getHibernateTemplate().find("from UserRunConfigurationBean u where u.userId = " + userId + " AND u.repId = " + objectId.getRepositoryId() + " AND u.itemId = " + objectId.getDirectoryItemId()));
	}

	public void update(UserRunConfiguration userRunConfiguration) {
		List<UserRunConfigurationBean> beans = createBeansFromRunConfig(userRunConfiguration);
		getHibernateTemplate().saveOrUpdateAll(beans);
	}
	
	private UserRunConfiguration createRunConfigFromBean(List<UserRunConfigurationBean> beans) {
		UserRunConfiguration config = new UserRunConfiguration();
		HashMap<String, UserRunConfigurationParameter> mapParams = new HashMap<String, UserRunConfigurationParameter>();
		boolean first = true;
		for(UserRunConfigurationBean bean : beans) {
			if(first) {
				config.setIdItem(bean.getItemId());
				config.setIdUser(bean.getUserId());
				config.setIdRepository(bean.getRepId());
				config.setName(bean.getRunName());
				config.setRunId(bean.getRunId());
				config.setDescription(bean.getDescription());
				first = false;
			}
			if(mapParams.get(bean.getParamName()) == null) {
				mapParams.put(bean.getParamName(), new UserRunConfigurationParameter());
				mapParams.get(bean.getParamName()).setId(bean.getId());
				mapParams.get(bean.getParamName()).setName(bean.getParamName());
			}
			mapParams.get(bean.getParamName()).addValue(bean.getParamValue());
		}
		config.setParameters(new ArrayList<UserRunConfigurationParameter>(mapParams.values()));
		
		return config;
	}
	
	private List<UserRunConfiguration> createRunConfigsFromBeans(List<UserRunConfigurationBean> beans) {
		List<UserRunConfiguration> configs = new ArrayList<UserRunConfiguration>();
		
		HashMap<Integer, List<UserRunConfigurationBean>> mapRunIdBeans = new HashMap<Integer, List<UserRunConfigurationBean>>();
		for(UserRunConfigurationBean bean : beans) {
			int runid = bean.getRunId();
			if(!mapRunIdBeans.keySet().contains(runid)) {
				mapRunIdBeans.put(runid, new ArrayList<UserRunConfigurationBean>());
			}
			mapRunIdBeans.get(runid).add(bean);
		}
		
		for(List<UserRunConfigurationBean> mapbeans : mapRunIdBeans.values()) {
			configs.add(createRunConfigFromBean(mapbeans));
		}
		
		return configs;
	}
	
	private List<UserRunConfigurationBean> createBeansFromRunConfig(UserRunConfiguration config) {
		List<UserRunConfigurationBean> beans = new ArrayList<UserRunConfigurationBean>();
		for(UserRunConfigurationParameter param : config.getParameters()) {
			for(String val : param.getValues()) {
				UserRunConfigurationBean bean = new UserRunConfigurationBean();
				bean.setId(param.getId());
				bean.setItemId(config.getIdItem());
				bean.setParamName(param.getName());
				bean.setParamValue(val);
				bean.setRepId(config.getIdRepository());
				bean.setDescription(config.getDescription());
				int runid = config.getRunId();
				if(runid < 0) {
					getHibernateTemplate().flush();
					List<Integer> res = getHibernateTemplate().find("select max(u.runId) from UserRunConfigurationBean u");
					if(!res.isEmpty() && res.get(0) != null) {
						runid = res.get(0) + 1;
					}
					else {
						runid = 0;
					}
				}
				bean.setRunId(runid);
				bean.setRunName(config.getName());
				bean.setUserId(config.getIdUser());
				beans.add(bean);
			}
		}
		return beans;
	}
	
	private List<UserRunConfigurationBean> createBeansFromRunConfigs(List<UserRunConfiguration> configs) {
		List<UserRunConfigurationBean> beans = new ArrayList<UserRunConfigurationBean>();
		for(UserRunConfiguration config : configs) {
			beans.addAll(createBeansFromRunConfig(config));
		}
		return beans;
	}
}
