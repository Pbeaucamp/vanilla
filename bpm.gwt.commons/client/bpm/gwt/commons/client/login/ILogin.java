package bpm.gwt.commons.client.login;

import org.realityforge.gwt.keycloak.Keycloak;

import bpm.gwt.commons.shared.InfoUser;

public interface ILogin {

	public void login(InfoUser infoUser, Keycloak keycloak);
}
