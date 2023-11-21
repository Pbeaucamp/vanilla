package bpm.gateway.core.server.ldap;

import java.util.List;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.ldap.LdapName;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.LdapTemplate;

import bpm.gateway.core.DocumentGateway;
import bpm.gateway.core.GatewayObject;
import bpm.gateway.core.IServerConnection;
import bpm.gateway.core.Server;
import bpm.gateway.core.exception.JdbcException;
import bpm.gateway.core.exception.ServerException;

public class LdapConnection extends GatewayObject implements IServerConnection{
	private Server server;
	
	private String host;
	private String port;
	private String base;
	private String userDn;
	private String password;
	
	private ContextSource context;
	private LdapTemplate ldapTemplate;
	
	@Override
	public void connect(DocumentGateway document) throws ServerException {
		String url = "ldap://" + host + ":" + port;
//		if (context == null){
			try {
				context = LdapContextSourceFactory.getLdapContextSource(url, base, userDn, password);
				
			} catch (Exception e) {
				throw new ServerException("Unable to create LdapContext", e, server);
			}
//		}
		
//		if (ldapTemplate == null){
			ldapTemplate = new LdapTemplate();
			ldapTemplate.setContextSource(context);
//		}
		
	}
	
	public LdapTemplate getLdapTemplate(){
		return ldapTemplate;
	}

	public void disconnect() throws JdbcException {
		ldapTemplate = null;
		context = null;
		
	}

	public Element getElement() {
		Element el = DocumentHelper.createElement("ldapConnection");
		el.addElement("name").setText(getName());
		el.addElement("description").setText(getDescription());
		
		el.addElement("host").setText(getHost());
		el.addElement("port").setText(getPort());
		el.addElement("base").setText(getBase());
		el.addElement("userDn").setText(getUserDn());
		el.addElement("password").setText(getPassword());

		return el;
	}

	

	public Server getServer() {
		return server;
	}

	public boolean isOpened() {
		return ldapTemplate != null;
	}

	public boolean isSet() {
		return base != null && !base.trim().equals("") && host != null && !host.trim().equals("") && port != null && !port.trim().equals("");
	}

	/**
	 * @return the host
	 */
	public final String getHost() {
		return host;
	}

	/**
	 * @param url the host to set
	 */
	public final void setHost(String host) {
		this.host = host;
	}

	
	/**
	 * @return the host
	 */
	public final String getPort() {
		return port;
	}

	/**
	 * @param url the host to set
	 */
	public final void setPort(String port) {
		this.port = port;
	}

	
	/**
	 * @return the base
	 */
	public final String getBase() {
		return base;
	}

	/**
	 * @param base the base to set
	 */
	public final void setBase(String base) {
		this.base = base;
	}

	/**
	 * @return the userDn
	 */
	public final String getUserDn() {
		return userDn;
	}

	/**
	 * @param userDn the userDn to set
	 */
	public final void setUserDn(String userDn) {
		this.userDn = userDn;
	}

	/**
	 * @return the password
	 */
	public final String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public final void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @param server the server to set
	 */
	public final void setServer(Server server) {
		this.server = server;
	}

	
	
	@SuppressWarnings("unchecked")
	public List<String> getListNode(String nodeName){
//		ldapTemplate.se
		return ldapTemplate.list(nodeName);
	}
	
	
	
	
	static public void main (String[] args){
		LdapConnection sock = new LdapConnection();
		
		sock.setHost("192.168.1.45");
		sock.setPort("10389");
		sock.setUserDn("uid=admin,ou=system");
		sock.setPassword("secret");
		sock.setBase("ou=system");
		try {
			sock.connect(null);
			
			Object o = sock.ldapTemplate.search("ou=groups", "cn=Sales", new AttributesMapper(){

				public Object mapFromAttributes(Attributes att) throws NamingException {
					return  att.get("uniquemember").get();
				}
				
			});
			

		}catch(Exception e){
			e.printStackTrace();
		}
		
		
	}

	public Attributes getAttributes(final String dn) {
		DirContextAdapter o = (DirContextAdapter)ldapTemplate.lookup(dn, new String[] {"*", "+"}, new AttributesMapper<DirContextAdapter>() {

			@Override
			public DirContextAdapter mapFromAttributes(Attributes arg0) throws NamingException {
				DirContextAdapter adapter = new DirContextAdapter(arg0, new LdapName(dn));
				return adapter;
			}
		});
		return o.getAttributes();
	}
	
	public String getAutoDocumentationDetails() {
		StringBuffer buf = new StringBuffer();
		
	
		buf.append("Host : " + getHost() + "\n");
		buf.append("Port : " + getPort() + "\n");
		buf.append("BaseDn : " + getBase() + "\n");
		
		buf.append("UserDn : " + getUserDn() + "\n");
		buf.append("Password : " + getPassword().replaceAll(".", "*") + "\n");
		return buf.toString();
	}
}
