package bpm.gwt.commons.shared;

import java.util.List;

import bpm.vanilla.platform.core.beans.VanillaLocale;

import com.google.gwt.user.client.rpc.IsSerializable;

public class InfoConnection implements IsSerializable {

	private boolean useKeycloak;
	private String keycloakKey;
	private String keycloakConfigUrl;
	
	private String vanillaUrl;
	private String customLogo;
	private boolean includeFastConnect;
	private List<VanillaLocale> locales;
	
	public InfoConnection() { }
	
	public boolean useKeycloak() {
		return useKeycloak;
	}
	
	public void setUseKeycloak(boolean useKeycloak) {
		this.useKeycloak = useKeycloak;
	}
	
	public String getKeycloakKey() {
		return keycloakKey;
	}
	
	public void setKeycloakKey(String keycloakKey) {
		this.keycloakKey = keycloakKey;
	}
	
	public String getKeycloakConfigUrl() {
		return keycloakConfigUrl;
	}
	
	public void setKeycloakConfigUrl(String keycloakConfigUrl) {
		this.keycloakConfigUrl = keycloakConfigUrl;
	}
	
	public String getVanillaUrl() {
		return vanillaUrl;
	}

	public void setVanillaUrl(String vanillaUrl) {
		this.vanillaUrl = vanillaUrl;
	}

	public String getCustomLogo() {
		return customLogo;
	}

	public void setCustomLogo(String customLogo) {
		this.customLogo = customLogo;
	}

	public boolean isIncludeFastConnect() {
		return includeFastConnect;
	}

	public void setIncludeFastConnect(boolean includeFastConnect) {
		this.includeFastConnect = includeFastConnect;
	}

	public void setLocales(List<VanillaLocale> locales) {
		this.locales = locales;
	}

	public List<VanillaLocale> getLocales() {
		return locales;
	}

}
