package bpm.vanilla.platform.core.beans;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import bpm.vanilla.platform.core.beans.Setting.SettingType;

public class Settings implements Serializable {

	private static final long serialVersionUID = 1L;

	private HashMap<SettingType, Setting> settings;

	public Settings() {
	}

	public Settings(List<Setting> settings) {
		this.settings = new HashMap<SettingType, Setting>();
		if (settings != null) {
			for (Setting setting : settings) {
				this.settings.put(setting.getSettingType(), setting);
			}
		}
	}

	public HashMap<SettingType, Setting> getSettings() {
		return settings;
	}

	public String getSetting(SettingType settingType) {
		Setting setting = settings.get(settingType);
		return setting != null ? setting.getValue() : "";
	}

	public int getSettingAsInteger(SettingType settingType) {
		Setting setting = settings.get(settingType);
		return setting != null ? setting.getValueAsInteger() : 0;
	}

	public boolean getSettingAsBoolean(SettingType settingType) {
		Setting setting = settings.get(settingType);
		return setting != null ? setting.getValueAsBoolean() : false;
	}

	public void updateSetting(SettingType settingType, Object value) {
		Setting setting = settings.get(settingType);
		if (setting == null) {
			setting = new Setting(settingType, value);
			settings.put(settingType, setting);
		}
		else {
			setting.setValue(value);
		}
	}
}
