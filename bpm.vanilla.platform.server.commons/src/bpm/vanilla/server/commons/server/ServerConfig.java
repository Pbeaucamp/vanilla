package bpm.vanilla.server.commons.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ServerConfig {
	public static final String MAXIMUM_SIMULTAEOUS_TASKS = "bpm.vanilla.server.commons.server.maximumRunningTasks";
	public static final String POOL_CLASS_NAME = "bpm.vanilla.server.commons.pool";
	public static final String VANILLA_SERVER_URL = "bpm.vanilla.server.url";
	public static final String HISTORIZATION_FOLDER = "bpm.vanilla.server.historizationfolder";

	private int maxTasks;
	private String poolClassName;
	private String vanillaUrl;
	private String historizationFolder;
	private boolean needManageHistoric;

	protected List<String> propertyNames = new ArrayList<String>();

	{
		propertyNames.add(MAXIMUM_SIMULTAEOUS_TASKS);
		propertyNames.add(POOL_CLASS_NAME);
		propertyNames.add(VANILLA_SERVER_URL);
		propertyNames.add(HISTORIZATION_FOLDER);
	}

	public ServerConfig(Properties prop) {
		setMaxTasks(prop.getProperty(MAXIMUM_SIMULTAEOUS_TASKS));
		setPoolClassName(prop.getProperty(POOL_CLASS_NAME));
		setVanillaUrl(prop.getProperty(VANILLA_SERVER_URL));
		setHistorizationFolder(prop.getProperty(HISTORIZATION_FOLDER));
	}

	/**
	 * @return the historizationFolder
	 */
	public String getHistorizationFolder() {
		return historizationFolder;
	}

	/**
	 * @param historizationFolder
	 *            the historizationFolder to set
	 */
	private void setHistorizationFolder(String historizationFolder) {
		this.historizationFolder = historizationFolder;
	}

	private void setVanillaUrl(String property) {
		this.vanillaUrl = property;

	}

	/**
	 * @return the vanillaUrl
	 */
	public String getVanillaUrl() {
		return vanillaUrl;
	}

	/**
	 * @return the poolClassName
	 */
	public String getPoolClassName() {
		return poolClassName;
	}

	/**
	 * @param poolClassName
	 *            the poolClassName to set
	 */
	private void setPoolClassName(String poolClassName) {
		this.poolClassName = poolClassName;
	}

	/**
	 * @param maxTasks
	 *            the maxTasks to set
	 */
	private void setMaxTasks(String maxTasks) {
		try {
			int i = Integer.parseInt(maxTasks);
			this.maxTasks = i;
		} catch (Exception e) {
			this.maxTasks = 1;
		}
	}

	/**
	 * @return the maxTasks
	 */
	public int getMaxTasks() {
		return maxTasks;
	}

	public List<String> getPropertiesName() {
		return propertyNames;
	}

	public boolean isNeedManageHistoric() {
		return needManageHistoric;
	}

	public String getPropertyValue(String name) {
		if (MAXIMUM_SIMULTAEOUS_TASKS.equals(name)) {
			return getMaxTasks() + "";
		}
		if (POOL_CLASS_NAME.equals(name)) {
			return getPoolClassName() + "";
		}
		if (VANILLA_SERVER_URL.equals(name)) {
			return vanillaUrl;
		}
		if (HISTORIZATION_FOLDER.equals(name)) {
			return historizationFolder;
		}
		return null;
	}

	public String toString() {
		StringBuilder buf = new StringBuilder();
		for (String s : getPropertiesName()) {
			buf.append(s + " = " + getPropertyValue(s) + "\n");
		}
		return buf.toString();
	}
}
