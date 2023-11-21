package bpm.gateway.core.transformations.vanilla;

import java.util.HashMap;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.gateway.core.AbstractTransformation;
import bpm.gateway.core.DefaultStreamDescriptor;
import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.core.manager.ResourceManager;
import bpm.gateway.core.server.ldap.LdapServer;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.transformation.vanilla.RunLdapSynchro;
import bpm.vanilla.platform.core.IRepositoryContext;

public class VanillaLdapSynchro extends AbstractTransformation{
	public static final int IX_USER_SURNAME = 0;
	public static final int IX_USER_FUNCTION = 1;
	public static final int IX_USER_PHONE = 2;
	public static final int IX_USER_CELLULAR = 3;
	public static final int IX_USER_SKYPENAME = 4;
	public static final int IX_USER_SKYPENUMBER = 5;
	public static final int IX_USER_BUSINESSMAIL = 6;
	public static final int IX_USER_PRIVATEMAIL = 7;
	public static final int IX_USER_IMAGE = 8;
	public static final int IX_USER_LOGIN = 9;
	public static final int IX_USER_NAME = 10;

	public static final String[] IX_USER_FIELD_NAME = new String[]{
		"Surname", "Function", "PhoneNumber", "CellularNumber",
		"SkypeName", "SkypeNumber", "BusinessMail",
		"PrivateMail", "Image", "Login", "Name"
	};
	
	
	private String ldapGroupDn = "ou=groups";
	private String ldapUsersDn = "ou=users";
	
	private String ldapGroupNodeName = "ou";
	private String ldapUserNodeName = "cn";
	private String groupMemberNodeName = "uniquemember";
	
	private String groupFilter;
	private String groupAttribute;
	private String userMemberNodeName;
	
	private LdapServer ldapServer;

	
	
	private HashMap<Integer, String> userAttributeMapping = new HashMap<Integer, String>();
	
	private StreamDescriptor descriptor = new DefaultStreamDescriptor();
	
	public VanillaLdapSynchro(){
		userAttributeMapping.put(IX_USER_SURNAME, null);
		userAttributeMapping.put(IX_USER_FUNCTION, null);
		userAttributeMapping.put(IX_USER_PHONE, null);
		userAttributeMapping.put(IX_USER_CELLULAR, null);
		userAttributeMapping.put(IX_USER_SKYPENAME, null);
		userAttributeMapping.put(IX_USER_SKYPENUMBER, null);
		userAttributeMapping.put(IX_USER_BUSINESSMAIL, null);
		userAttributeMapping.put(IX_USER_PRIVATEMAIL, null);
		userAttributeMapping.put(IX_USER_IMAGE, null);
		userAttributeMapping.put(IX_USER_LOGIN, null);
		userAttributeMapping.put(IX_USER_NAME, null);
		
	}

	@Override
	public StreamDescriptor getDescriptor(Transformation transfo) throws ServerException {
		return descriptor;
	}

	/**
	 * @return the ldapGroupDn
	 */
	public String getLdapGroupDn() {
		return ldapGroupDn;
	}

	/**
	 * @param ldapGroupDn the ldapGroupDn to set
	 */
	public void setLdapGroupDn(String ldapGroupDn) {
		this.ldapGroupDn = ldapGroupDn;
	}

	/**
	 * @return the ldapUsersDn
	 */
	public String getLdapUsersDn() {
		return ldapUsersDn;
	}

	/**
	 * @param ldapUsersDn the ldapUsersDn to set
	 */
	public void setLdapUsersDn(String ldapUsersDn) {
		this.ldapUsersDn = ldapUsersDn;
	}

	/**
	 * @return the ldapGroupNodeName
	 */
	public String getLdapGroupNodeName() {
		return ldapGroupNodeName;
	}

	/**
	 * @param ldapGroupNodeName the ldapGroupNodeName to set
	 */
	public void setLdapGroupNodeName(String ldapGroupNodeName) {
		this.ldapGroupNodeName = ldapGroupNodeName;
	}

	/**
	 * @return the ldapUserNodeName
	 */
	public String getLdapUserNodeName() {
		return ldapUserNodeName;
	}

	/**
	 * @param ldapUserNodeName the ldapUserNodeName to set
	 */
	public void setLdapUserNodeName(String ldapUserNodeName) {
		this.ldapUserNodeName = ldapUserNodeName;
	}

	/**
	 * @return the ldapServer
	 */
	public LdapServer getLdapServer() {
		return ldapServer;
	}

	/**
	 * @param ldapServer the ldapServer to set
	 */
	public void setLdapServer(Object ldapServer) {
		if (ldapServer == null){
			this.ldapServer = null;
			return;
		}
		if (ldapServer instanceof String){
			this.ldapServer = (LdapServer)ResourceManager.getInstance().getServer((String)ldapServer);
		}
		else{
			this.ldapServer = (LdapServer)ldapServer;
		}
		
	}



	
	
	public String getUserAttribute(Integer k){
		for(Integer i : this.userAttributeMapping.keySet()){
			if (i.intValue() == k.intValue()){
				return userAttributeMapping.get(i);
			}
		}
		return null;
	}
	
	public void setUserAttribute(String key, String value){
		try{
			for(Integer i : this.userAttributeMapping.keySet()){
				
				if (i.intValue() == Integer.parseInt(key)){
					this.userAttributeMapping.put(i, value);
				}
			}
		}catch(NumberFormatException e){
			
		}
		
		
	}

	@Override
	public Element getElement() {
		Element e = DocumentHelper.createElement("vanillaLdapSynchro");
		e.addElement("name").setText(name);
		e.addElement("description").setText(description);
		
		
		e.addElement("positionX").setText("" + x);
		e.addElement("positionY").setText("" + y);
	
		if (ldapServer != null){
			e.addElement("ldapServerRef").setText(ldapServer.getName());
		}
		
		e.addElement("ldapGroupDn").setText(ldapGroupDn);
		e.addElement("ldapGroupNodeName").setText(ldapGroupNodeName);
		e.addElement("ldapUserNodeName").setText(ldapUserNodeName);
		e.addElement("ldapUsersDn").setText(ldapUsersDn);
		e.addElement("groupMemberNodeName").setText(groupMemberNodeName);
		if(groupFilter != null) {
			e.addElement("groupFilter").setText(groupFilter);
		}
		if(groupAttribute != null) {
			e.addElement("groupAttribute").setText(groupAttribute);
		}
		if(userMemberNodeName != null) {
			e.addElement("userMemberNodeName").setText(userMemberNodeName);
		}
		
		e.addElement("temporaryFileName").setText(getTemporaryFilename());
		e.addElement("temporarySeparator").setText(getTemporarySpliterChar() + "");
		
		/*
		 * for the mapping
		 */

	
		for(Integer i : userAttributeMapping.keySet()){
			if (userAttributeMapping.get(i) != null){
				Element a = e.addElement("userAttribute");
				a.addElement("userTableField").setText(i + "");
				a.addElement("attributeName").setText(userAttributeMapping.get(i));
			}
			
		}
		
		if (descriptor != null){
			e.add(descriptor.getElement());
		}
		return e;
	}

//	@Override
//	public TransformationRuntime getExecutioner(RuntimeEngine runtimeEngine) {
//		return new VanillaLdapSynchroRuntime(this, runtimeEngine);
//	}
	
	@Override
	public RuntimeStep getExecutioner(IRepositoryContext repositoryCtx, int bufferSize) {
		try {
			return new RunLdapSynchro(repositoryCtx, this, bufferSize);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	@Override
	public void refreshDescriptor() {
		if (!isInited()){
			return;
		}
		
		for(Transformation t : getOutputs()){
			t.refreshDescriptor();
		}
		
	}

	public Transformation copy() {
		return null;
	}

	public String getLdapGroupMemberNodeName() {
		return groupMemberNodeName;
	}
	
	public void setLdapGroupMemberNodeName(String groupMemberNodeName) {
		this.groupMemberNodeName = groupMemberNodeName;
	}

	public void setUserAttributeMapping(Integer data, String value) {
	
		for(Integer i : this.userAttributeMapping.keySet()){
			
			if (i.intValue() == data.intValue()){
				this.userAttributeMapping.put(i, value);
			}
		}
	
	}
	public String getAutoDocumentationDetails() {
		StringBuffer buf = new StringBuffer();
		
		buf.append("LdapServer : " + getLdapServer().getName() + "\n");
		buf.append("GroupMembersNodeName  : " + groupMemberNodeName + "\n");
		buf.append("Group Dn  : " + ldapGroupDn + "\n");
		buf.append("Users Dn  : " + ldapUsersDn + "\n");
		buf.append("UsersNodeName  : " + ldapUserNodeName + "\n");
		
		buf.append("AttributeMappings  :\n");
		for(Integer i : userAttributeMapping.keySet()){
			buf.append("\t" + IX_USER_FIELD_NAME[i] + " Field = " +userAttributeMapping.get(i) + "\n");
		}
		
		
		
		return buf.toString();
	}

	public void setGroupFilter(String groupFilter) {
		this.groupFilter = groupFilter;
	}

	public String getGroupFilter() {
		return groupFilter;
	}

	public void setGroupAttribute(String groupAttribute) {
		this.groupAttribute = groupAttribute;
	}

	public String getGroupAttribute() {
		return groupAttribute;
	}

	public void setUserMemberNodeName(String userMemberNodeName) {
		this.userMemberNodeName = userMemberNodeName;
	}

	public String getUserMemberNodeName() {
		return userMemberNodeName;
	}
	

}
