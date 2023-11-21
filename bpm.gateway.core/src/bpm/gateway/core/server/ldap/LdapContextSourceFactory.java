package bpm.gateway.core.server.ldap;

import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.support.LdapContextSource;

public class LdapContextSourceFactory {
	
	/**
	 * create a ContextSource for a LDAP server
	 * @param url (ex : ldap://www.openldap.com:389)
	 * @param base (ex : dc=OpenLDAP,dc=org)
	 * @param userDn : can be null for anynimous access
	 * @param password : set only if the userDn is not null
	 * @return
	 * @throws Exception
	 */
	public static ContextSource getLdapContextSource(String url, String base, String userDn, String password) throws Exception {
		LdapContextSource ldapContextSource = new LdapContextSource();
		ldapContextSource.setUrl(url);
		ldapContextSource.setBase(base);
		
		if (userDn != null && !userDn.trim().equals("")){
			ldapContextSource.setUserDn(userDn);
			ldapContextSource.setPassword(password);
		}
		
		ldapContextSource.afterPropertiesSet();
		
		return ldapContextSource;
	}
}
