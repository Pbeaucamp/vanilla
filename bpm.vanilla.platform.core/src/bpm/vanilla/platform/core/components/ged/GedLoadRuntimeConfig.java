package bpm.vanilla.platform.core.components.ged;

import bpm.vanilla.platform.core.beans.ged.GedDocument;

/**
 * config for loading ged documents
 * @author manu
 *
 */
public class GedLoadRuntimeConfig {

	private GedDocument definition;
	private int userId;
	private int versionToLoad;
	
	public GedLoadRuntimeConfig() {
	}
	
	public GedLoadRuntimeConfig(GedDocument definition, int userId) {
		this.definition = definition;
		this.userId = userId;
	}
	
	
	public GedLoadRuntimeConfig(GedDocument definition, int userId, int versionToLoad) {
		this.definition = definition;
		this.userId = userId;
		this.versionToLoad = versionToLoad;
	}

	public GedDocument getDefinition() {
		return definition;
	}

	public int getUserId() {
		return userId;
	}
	
	public int getVersionToLoad() {
		return versionToLoad;
	}
}
