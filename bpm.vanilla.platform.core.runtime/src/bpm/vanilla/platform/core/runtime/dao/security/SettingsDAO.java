package bpm.vanilla.platform.core.runtime.dao.security;

import java.util.List;

import bpm.vanilla.platform.core.beans.Setting;
import bpm.vanilla.platform.core.beans.Settings;
import bpm.vanilla.platform.core.beans.Setting.SettingType;
import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class SettingsDAO extends HibernateDaoSupport {

	public void saveOrUpdate(Settings settings) {
		for (Setting setting : settings.getSettings().values()) {
			if (setting.getId() > 0) {
				getHibernateTemplate().update(setting);
			}
			else {
				getHibernateTemplate().save(setting);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public Settings getSettings() {
		List<Setting> l = (List<Setting>) getHibernateTemplate().find("from Setting");
		return new Settings(l);
	}

	@SuppressWarnings("unchecked")
	public int getMaxNbConnection() {
		List<Setting> l = (List<Setting>) getHibernateTemplate().find("from Setting where type=" + SettingType.AUTHENTICATION_MAX_TRY.getType());
		if (!l.isEmpty()) {
			return l.get(0).getValueAsInteger();
		}
		return 5;
	}

	@SuppressWarnings("unchecked")
	public int getInactivationDelay() {
		List<Setting> l = (List<Setting>) getHibernateTemplate().find("from Setting where type=" + SettingType.AUTHENTICATION_INACTIVATION_DELAY.getType());
		if (!l.isEmpty()) {
			return l.get(0).getValueAsInteger();
		}
		return 180000;
	}

	@SuppressWarnings("unchecked")
	public String getMessageFailedConnection() {
		List<Setting> l = (List<Setting>) getHibernateTemplate().find("from Setting where type=" + SettingType.MESSAGE_FAILED_CONNECTION.getType());
		if (!l.isEmpty()) {
			return l.get(0).getValue();
		}
		return null;
	}
	
//	@SuppressWarnings("unchecked")
//	public Setting getSettingByType(SettingType type) {
//		List<Setting> l = (List<Setting>) getHibernateTemplate().find("from Setting where type=" + type.getType());
//		if (l.size()>0){
//			return l.get(0);
//		}
//		return null;
//	}
}
