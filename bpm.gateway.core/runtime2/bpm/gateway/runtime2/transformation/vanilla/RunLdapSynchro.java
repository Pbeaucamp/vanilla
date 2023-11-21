package bpm.gateway.runtime2.transformation.vanilla;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;

import org.springframework.ldap.core.AttributesMapper;

import bpm.gateway.core.Transformation;
import bpm.gateway.core.server.ldap.LdapConnection;
import bpm.gateway.core.transformations.vanilla.VanillaLdapSynchro;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.UserGroup;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.utils.MD5Helper;
//TODO: Pagination https://extendit.us/ldap-search-and-pagination-java
public class RunLdapSynchro extends RuntimeStep{

	private IVanillaAPI vanillaApi;
	private LdapConnection ldapCon;
	
	private HashMap<String, String> groupNameId;
	
	public RunLdapSynchro(IRepositoryContext repositoryContext, Transformation transformation, int bufferSize) throws Exception{
		super(repositoryContext, transformation, bufferSize);
		if (getRepositoryContext() == null){
			throw new Exception("Cannot use a VanillaSecurity step without a VanillaContext. You must be connected to Vanilla.");
		}
	}

	@Override
	public void init(Object adapter) throws Exception {
		VanillaLdapSynchro transfo = (VanillaLdapSynchro)getTransformation();
		
		vanillaApi = new RemoteVanillaPlatform(getRepositoryContext().getVanillaContext());
		
		
		ldapCon = (LdapConnection)transfo.getLdapServer().getCurrentConnection(adapter);
		if (!ldapCon.isOpened()){
			try{
				ldapCon.connect(null);
				info(" connected to LDAP Server");
			}catch(Exception e){
				error(" error when connection to LDAP server", e);
				throw e;
			}
		}
		
	}

	@Override
	public void performRow() throws Exception {

		final VanillaLdapSynchro transfo = (VanillaLdapSynchro) getTransformation();
		/*
		 * Users
		 */
		List<User> vanillaUsers = vanillaApi.getVanillaSecurityManager().getUsers();

		String filter = transfo.getLdapUserNodeName() + "=*";
//		if(transfo.getGroupFilter() != null && !transfo.getGroupFilter().isEmpty()) {
//			filter = transfo.getGroupFilter();
//		}

		List<User> ldapUsers = (List) ldapCon.getLdapTemplate().search(transfo.getLdapUsersDn(), filter, new AttributesMapper() {
			public Object mapFromAttributes(Attributes att) throws NamingException {
				User u = new User();

				// u.setName((String)att.get(transfo.getLdapUserNodeName()).get());

				for(int i = 0; i < VanillaLdapSynchro.IX_USER_FIELD_NAME.length; i++) {
					if(transfo.getUserAttribute(i) != null) {
						if(att.get(transfo.getUserAttribute(i)) == null) {
							continue;
						}
						String attributeValue = (String) att.get(transfo.getUserAttribute(i)).get();

						switch(i) {
							case VanillaLdapSynchro.IX_USER_BUSINESSMAIL:
								u.setBusinessMail(attributeValue);
								break;
							case VanillaLdapSynchro.IX_USER_CELLULAR:
								u.setCellular(attributeValue);
								break;
							case VanillaLdapSynchro.IX_USER_FUNCTION:
								u.setFunction(attributeValue);
								break;
							case VanillaLdapSynchro.IX_USER_IMAGE:
								u.setImage(attributeValue);
								break;
							case VanillaLdapSynchro.IX_USER_PHONE:
								u.setTelephone(attributeValue);
								break;
							case VanillaLdapSynchro.IX_USER_PRIVATEMAIL:
								u.setPrivateMail(attributeValue);
								break;
							case VanillaLdapSynchro.IX_USER_SKYPENAME:
								u.setSkypeName(attributeValue);
								break;
							case VanillaLdapSynchro.IX_USER_SKYPENUMBER:
								u.setSkypeNumber(attributeValue);
								break;
							case VanillaLdapSynchro.IX_USER_SURNAME:
								u.setSurname(attributeValue);
								break;
							case VanillaLdapSynchro.IX_USER_LOGIN:
								u.setLogin(attributeValue);
								u.setPassword(MD5Helper.encode(u.getLogin()));
								break;
							case VanillaLdapSynchro.IX_USER_NAME:
								u.setName(attributeValue);
								break;
						}

					}
				}

				return u;
			}

		});

		/*
		 * Groups
		 */
		List<Group> vanillaGroups = vanillaApi.getVanillaSecurityManager().getGroups();

		filter = transfo.getLdapGroupNodeName() + "=*";
		if(transfo.getGroupFilter() != null && !transfo.getGroupFilter().isEmpty()) {
			filter = transfo.getGroupFilter();
		}
		
		groupNameId = new HashMap<String, String>();

		List<Group> ldapGroups = (List) ldapCon.getLdapTemplate().search(transfo.getLdapGroupDn(), filter, new AttributesMapper() {
			public Object mapFromAttributes(Attributes att) throws NamingException {
				Group g = new Group();
				String name;
				try {
					name = (String) att.get(transfo.getGroupAttribute()).get();
				} catch (Exception e) {
					name = "UNDEFINED";
				}
				
				groupNameId.put(name.replace("'", "''"),(String)att.get(transfo.getLdapGroupNodeName()).get());
				
				g.setName(name.replace("'", "''"));
				return g;
			}

		});

		for(Group g : ldapGroups) {
			info("Treating group : " + g.getName());
			boolean exists = false;
			for(Group vg : vanillaGroups) {
				if(vg.getName().equals(g.getName())) {
					exists = true;
					g.setId(vg.getId());
					break;
				}
			}

			if(!exists) {
				g.setId(vanillaApi.getVanillaSecurityManager().addGroup(g));
				writedRows++;
			}
		}

		/*
		 * assoc
		 */

		for(Group g : ldapGroups) {
			List<NamingEnumeration<String>> assocEnum = (List<NamingEnumeration<String>>) ldapCon.getLdapTemplate().search(transfo.getLdapGroupDn(), transfo.getLdapGroupNodeName() + "=" + groupNameId.get(g.getName()), new AttributesMapper() {
				public Object mapFromAttributes(Attributes att) throws NamingException {
					try {
						Object o = att.get(transfo.getLdapGroupMemberNodeName()).getAll();
						return (NamingEnumeration<String>) o;
					} catch(Exception e) {
						return null;
					}
				}

			});

			List<String> assoc = new ArrayList<String>();
			for(NamingEnumeration<String> n : assocEnum) {
				if(n == null) {
					continue;
				}
				while(n.hasMore()) {
					assoc.add(n.next());
				}
			}

			for(String uid : assoc) {
				for(User u : ldapUsers) {
					String uName = null;
					try {
						if(uid.contains("=") && uid.contains(",")) {
							uName = uid.substring(uid.indexOf("=") + 1, uid.indexOf(","));
						}
						else if(uid.contains("=")) {
							uName = uid.substring(uid.indexOf("=") + 1);
						}
						else {
							uName = uid.trim();
						}
						if(uName.equals(u.getLogin())) {						
							info("Treating user : " + u.getName());
							boolean exists = false;
							for(User vu : vanillaUsers) {
								if(vu.getName().equals(u.getName())) {
									u.setId(vu.getId());
									exists = true;
									break;
								}
							}

							if(!exists) {
								try {
									u.setId(vanillaApi.getVanillaSecurityManager().addUser(u));

									vanillaApi.getVanillaSecurityManager().updateUser(u);
									writedRows++;
								} catch(Exception e) {
									warn("problem when creating user " + u.getName());
								}

							}
							
							UserGroup ug = new UserGroup();
							ug.setUserId(u.getId());
							ug.setGroupId(g.getId());
							vanillaApi.getVanillaSecurityManager().addUserGroup(ug);
						}
					} catch(Exception e) {
						error(e.getMessage(), e);
					}
				}
			}
		}

		setEnd();

	
////		final VanillaLdapSynchro transfo = (VanillaLdapSynchro)getTransformation();
////		/*
////		 * Users
////		 */
////		List<User> vanillaUsers = vanillaApi.getVanillaSecurityManager().getUsers();
////		List<User> ldapUsers = (List) ldapCon.getLdapTemplate().search(transfo.getLdapUsersDn(), transfo.getLdapUserNodeName()+ "=*", new AttributesMapper(){
////
////				public Object mapFromAttributes(Attributes att) throws NamingException {
////					User u = new User();
////					
////					u.setName((String)att.get(transfo.getLdapUserNodeName()).get());
////					u.setPassword(MD5Helper.encode(u.getName()));
////					
////					
////					for(int i =0; i < VanillaLdapSynchro.IX_USER_FIELD_NAME.length; i++){
////						if (transfo.getUserAttribute(i) != null ){
////							
////							if (att.get(transfo.getUserAttribute(i)) == null){
////								continue;
////							}
////							String attributeValue = (String)att.get(transfo.getUserAttribute(i)).get();
////							
////							switch(i){
////							case VanillaLdapSynchro.IX_USER_BUSINESSMAIL:
////								u.setBusinessMail(attributeValue);
////								break;
////							case VanillaLdapSynchro.IX_USER_CELLULAR:
////								u.setCellular(attributeValue);
////								break;
////							case VanillaLdapSynchro.IX_USER_FUNCTION:
////								u.setFunction(attributeValue);
////								break;
////							case VanillaLdapSynchro.IX_USER_IMAGE:
////								u.setImage(attributeValue);
////								break;
////							case VanillaLdapSynchro.IX_USER_PHONE:
////								u.setTelephone(attributeValue);
////								break;
////							case VanillaLdapSynchro.IX_USER_PRIVATEMAIL:
////								u.setPrivateMail(attributeValue);
////								break;
////							case VanillaLdapSynchro.IX_USER_SKYPENAME:
////								u.setSkypeName(attributeValue);
////								break;
////							case VanillaLdapSynchro.IX_USER_SKYPENUMBER:
////								u.setSkypeNumber(attributeValue);
////								break;
////							case VanillaLdapSynchro.IX_USER_SURNAME:
////								u.setSurname(attributeValue);
////								break;
////							case VanillaLdapSynchro.IX_USER_LOGIN:
////								u.setLogin(attributeValue);
////								break;
////								
////							}
////							
////						}
////					}
////					
////					
////					return  u;
////				}
////				
////			});
////		
////		
////		for(User u : ldapUsers){
////			boolean exists = false;
////			for(User vu : vanillaUsers){
////				if (vu.getName().equals(u.getName())){
////					u.setId(vu.getId());
////					exists = true;
////					break;
////				}
////			}
////			
////			if (!exists){
////				try{
////					u.setId(vanillaApi.getVanillaSecurityManager().addUser(u));
////					
////					vanillaApi.getVanillaSecurityManager().updateUser(u);
////					writedRows++;
////				}catch(Exception e){
////					warn("problem when creating user " + u.getName());
////				}
////				
////			}
////		}
////		
////		/*
////		 * Groups
////		 */
////		List<Group> vanillaGroups = vanillaApi.getVanillaSecurityManager().getGroups();
////		List<Group> ldapGroups = (List) ldapCon.getLdapTemplate().search(transfo.getLdapGroupDn(), transfo.getLdapGroupNodeName() + "=*", new AttributesMapper(){
////
////				public Object mapFromAttributes(Attributes att) throws NamingException {
////					
////					Group g = new Group();
////					g.setName((String)att.get(transfo.getLdapGroupNodeName()).get());
////					return  g;
////				}
////				
////			});
////		
////		
////		for(Group g : ldapGroups){
////			boolean exists = false;
////			for(Group vg : vanillaGroups){
////				if (vg.getName().equals(g.getName())){
////					exists = true;
////					g.setId(vg.getId());
////					break;
////				}
////			}
////			
////			if (!exists){
////				g.setId(vanillaApi.getVanillaSecurityManager().addGroup(g));
////				writedRows++;
////			}
////		}
////		
////		/*
////		 * assoc
////		 */
////		
////		for(Group g : ldapGroups){
////			List<NamingEnumeration<String>> assocEnum = (List<NamingEnumeration<String>>) ldapCon.getLdapTemplate().search(transfo.getLdapGroupDn(), transfo.getLdapGroupNodeName() + "=" + g.getName(), new AttributesMapper(){
////
////				public Object mapFromAttributes(Attributes att) throws NamingException {
////					Object o =  att.get(transfo.getLdapGroupMemberNodeName()).getAll();
////					return (NamingEnumeration<String>)o;
////				}
////				
////			});
////			
////			List<String> assoc  = new ArrayList<String>();
////			for(NamingEnumeration<String> n : assocEnum){
////				while(n.hasMore()){
////					assoc.add(n.next());
////				}
////			}
////			
////			for(String uid : assoc){
////				for(User u : ldapUsers){
////					String uName = null;
////					try{
////						if (uid.contains("=") && uid.contains(",")){
////							uName = uid.substring(uid.indexOf("=") + 1, uid.indexOf(","));
////						}
////						else if (uid.contains("=")){
////							uName = uid.substring(uid.indexOf("=") + 1);
////						}
////						else {
////							uName = uid.trim();
////						}
////						if (uName.equals(u.getName())){
////							UserGroup ug = new UserGroup();
////							ug.setUserId(u.getId());
////							ug.setGroupId(g.getId());
////							vanillaApi.getVanillaSecurityManager().addUserGroup(ug);
////						}
////					}catch(Exception e){
////						error(e.getMessage(),e);
////					}
////				}
////			}
////		}
////		
////		setEnd();
//		
//		final VanillaLdapSynchro transfo = (VanillaLdapSynchro)getTransformation();
//		
//		//Add or update group		
//		List<Group> vanillaGroups = vanillaApi.getVanillaSecurityManager().getGroups();
//		List<Group> ldapGroups = (List) ldapCon.getLdapTemplate().search(transfo.getLdapGroupDn(), transfo.getLdapGroupNodeName() + "=*", new AttributesMapper(){
//
//				public Object mapFromAttributes(Attributes att) throws NamingException {
//					
//					Group g = new Group();
//					g.setName((String)att.get(transfo.getLdapGroupNodeName()).get());
//					return  g;
//				}
//				
//			});
//		
//		
//		for(Group g : ldapGroups){
//			boolean exists = false;
//			for(Group vg : vanillaGroups){
//				if (vg.getName().equals(g.getName())){
//					exists = true;
//					g.setId(vg.getId());
//					break;
//				}
//			}
//			
//			if (!exists){
//				g.setId(vanillaApi.getVanillaSecurityManager().addGroup(g));
//				writedRows++;
//			}
//		}
//		
//		/*
//		 * Users
//		 */
//		List<User> vanillaUsers = vanillaApi.getVanillaSecurityManager().getUsers();
//		List<User> ldapUsers = (List) ldapCon.getLdapTemplate().search(transfo.getLdapUsersDn(), transfo.getLdapUserNodeName()+ "=*", new AttributesMapper(){
//
//				public Object mapFromAttributes(Attributes att) throws NamingException {
//					User u = new User();
//					
//					u.setName((String)att.get(transfo.getLdapUserNodeName()).get());
//					u.setLogin((String)att.get(transfo.getLdapUserNodeName()).get());
//					u.setPassword(u.getName());
//					
//					
//					for(int i =0; i < VanillaLdapSynchro.IX_USER_FIELD_NAME.length; i++){
//						if (transfo.getUserAttribute(i) != null ){
//							
//							if (att.get(transfo.getUserAttribute(i)) == null){
//								continue;
//							}
//							String attributeValue = (String)att.get(transfo.getUserAttribute(i)).get();
//							
//							switch(i){
//							case VanillaLdapSynchro.IX_USER_BUSINESSMAIL:
//								u.setBusinessMail(attributeValue);
//								break;
//							case VanillaLdapSynchro.IX_USER_CELLULAR:
//								u.setCellular(attributeValue);
//								break;
//							case VanillaLdapSynchro.IX_USER_FUNCTION:
//								u.setFunction(attributeValue);
//								break;
//							case VanillaLdapSynchro.IX_USER_IMAGE:
//								u.setImage(attributeValue);
//								break;
//							case VanillaLdapSynchro.IX_USER_PHONE:
//								u.setTelephone(attributeValue);
//								break;
//							case VanillaLdapSynchro.IX_USER_PRIVATEMAIL:
//								u.setPrivateMail(attributeValue);
//								break;
//							case VanillaLdapSynchro.IX_USER_SKYPENAME:
//								u.setSkypeName(attributeValue);
//								break;
//							case VanillaLdapSynchro.IX_USER_SKYPENUMBER:
//								u.setSkypeNumber(attributeValue);
//								break;
//							case VanillaLdapSynchro.IX_USER_SURNAME:
//								u.setSurname(attributeValue);
//								break;
//								
//							}
//							
//						}
//					}
//					
//					
//					return  u;
//				}
//				
//			});
//		
//		for(User u : ldapUsers){
//			boolean exists = false;
//			for(User vu : vanillaUsers){
//				if (vu.getName().equals(u.getName())){
//					u.setId(vu.getId());
//					exists = true;
//					break;
//				}
//			}
//			
////			if (!exists){
////				try{
////					u.setId(vanillaApi.getVanillaSecurityManager().addUser(u));
////					writedRows++;
////				}catch(Exception e){
////					warn("problem when creating user " + u.getName());
////				}
////				
////			}
//		}
//		
//		for(Group g : ldapGroups){
//			List<NamingEnumeration<String>> assocEnum = (List<NamingEnumeration<String>>) ldapCon.getLdapTemplate().search(transfo.getLdapGroupDn(), transfo.getLdapGroupNodeName() + "=" + g.getName(), new AttributesMapper(){
//
//				public Object mapFromAttributes(Attributes att) throws NamingException {
//					Object o =  att.get(transfo.getLdapGroupMemberNodeName()).getAll();
//					return (NamingEnumeration<String>)o;
//				}
//				
//			});
//			
//			List<String> assoc  = new ArrayList<String>();
//			for(NamingEnumeration<String> n : assocEnum){
//				while(n.hasMore()){
//					assoc.add(n.next());
//				}
//			}
//			
//			for(String uid : assoc){
//				for(User u : ldapUsers){
//					String uName = null;
//					try{
//						if (uid.contains("=") && uid.contains(",")){
//							uName = uid.substring(uid.indexOf("=") + 1, uid.indexOf(","));
//						}
//						else if (uid.contains("=")){
//							uName = uid.substring(uid.indexOf("=") + 1);
//						}
//						else {
//							uName = uid.trim();
//						}
//						if (uName.equals(u.getName())){
//							UserGroup ug = null;
//				
//							if(u.getId() == null || u.getId() <= 0 ) {
//								ug = new UserGroup();
//								u.setId(vanillaApi.getVanillaSecurityManager().addUser(u));
//								ug.setUserId(u.getId());
//								ug.setGroupId(g.getId());						
//								vanillaApi.getVanillaSecurityManager().addUserGroup(ug);
//								writedRows++;
//							}
//							else {
//								try {
//									ug = vanillaApi.getVanillaSecurityManager().getUserGroup(u.getId(), g.getId());
//									if(ug == null) {
//										ug = new UserGroup();
//										ug.setUserId(u.getId());
//										ug.setGroupId(g.getId());						
//										vanillaApi.getVanillaSecurityManager().addUserGroup(ug);
//										writedRows++;
//									}
//								} catch (Exception e) {
//									if(ug == null) {
//										ug = new UserGroup();
//										ug.setUserId(u.getId());
//										ug.setGroupId(g.getId());						
//										vanillaApi.getVanillaSecurityManager().addUserGroup(ug);
//										writedRows++;
//									}
//								}
//
//							}
//						}
//					}catch(Exception e){
//						error(e.getMessage(),e);
//					}
//				}
//			}
//		}
//		
//		setEnd();
//		
	}

	@Override
	public void releaseResources() {
		// TODO Auto-generated method stub
		
	}

}
