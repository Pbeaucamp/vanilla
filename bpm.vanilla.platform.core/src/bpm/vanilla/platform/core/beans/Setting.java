package bpm.vanilla.platform.core.beans;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Setting implements Serializable {

	private static final long serialVersionUID = 1L;

	public enum SettingType {
		USER_MAX_INACTIVITY_DELAY(0), 
		AUTHENTICATION_MAX_TRY(1), 
		AUTHENTICATION_INACTIVATION_DELAY(2), 
		SEND_MAIL_IF_TOO_MANY_TRY(3), 
		MAIL_FIRST_LOGIN_VALIDITY_DELAY(4), 
		MAIL_CHANGE_PASSWORD_VALIDITY_DELAY(5), 
		CHANGE_PASSWORD_DELAY(6), 
		NEED_ADMIN_TO_CHANGE_PASSWORD(7), 
		MIN_CHAR_PER_PASSWORD(8), 
		MIN_NUMBER_PER_PASSWORD(9), 
		MESSAGE_FAILED_CONNECTION(10), 
		USE_WEB_KEYBOARD(11), 
		USE_OTP(12);

		private int type;

		private static Map<Integer, SettingType> map = new HashMap<Integer, SettingType>();
		static {
			for (SettingType actionType : SettingType.values()) {
				map.put(actionType.getType(), actionType);
			}
		}

		private SettingType(int type) {
			this.type = type;
		}

		public int getType() {
			return type;
		}

		public static SettingType valueOf(int type) {
			return map.get(type);
		}
	}

	private int id;
	private SettingType type;
	private String value;

	public Setting() {
	}

	public Setting(SettingType type, Object value) {
		this.type = type;
		this.value = value.toString();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public SettingType getSettingType() {
		return type;
	}

	public int getType() {
		return type.getType();
	}

	public void setType(int type) {
		this.type = SettingType.valueOf(type);
	}

	public String getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value.toString();
	}

	public int getValueAsInteger() {
		try {
			return value != null ? Integer.parseInt(value) : 0;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	public boolean getValueAsBoolean() {
		try {
			return value != null ? Boolean.parseBoolean(value) : false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
