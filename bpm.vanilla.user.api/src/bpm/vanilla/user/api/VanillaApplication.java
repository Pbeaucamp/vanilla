package bpm.vanilla.user.api;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.user.api.service.GroupService;
import bpm.vanilla.user.api.service.RepositoryService;
import bpm.vanilla.user.api.service.UserService;
import bpm.vanilla.platform.core.beans.UserGroup;
import bpm.vanilla.platform.core.beans.UserRep;

public class VanillaApplication {
	public static void main (String[] args) throws Exception{
		System.out.println("Hello world !");
		
		IVanillaContext vanillaCtx = new BaseVanillaContext("http://localhost:9292", "system", "system");
		IVanillaAPI vanillaApi = new RemoteVanillaPlatform(vanillaCtx);
		
		UserService userService = new UserService(vanillaApi);
		GroupService groupService = new GroupService(vanillaApi);
		RepositoryService repositoryService = new RepositoryService(vanillaApi);
		//User myuser = vanillaApi.getVanillaSecurityManager().getUserById(4);
		//vanillaApi.getVanillaSecurityManager().delUser(myuser);  delete aussi les User group liés à l'User
		
		

		
		

		List <Repository> repos = repositoryService.getRepositories();
		List<UserGroup> userGroups = vanillaApi.getVanillaSecurityManager().getUserGroups();
		List<Group> groups = groupService.getGroups();
		List<User> users = userService.getUsers();
		System.out.println("Groups List : "+ groupService.getGroups());
		System.out.println("Repositories List : "+ repositoryService.getRepositories());
		System.out.println("User groups : " + vanillaApi.getVanillaSecurityManager().getUserGroups());

		//userService.addUser("MyUser", "MyLogin", "MyPassword");
		affUsers(users);
		System.out.println("#########################");
		affGroups(groups);
		System.out.println("#########################");
		affUserGroups(userGroups, users, groups);
		
		List<UserRep> userReps = vanillaApi.getVanillaRepositoryManager().getUserRepByRepositoryId(repos.get(0).getId());
		affUserReps(userReps,users,repos);

		//groupService.addUserToGroup(6, 3);
		//repositoryService.addUserToRepository(6, 1);
		
		
		//userReps = vanillaApi.getVanillaRepositoryManager().getUserRepByRepositoryId(repos.get(0).getId());
		//groups = groupService.getGroups();
		//affUserGroups(userGroups, users, groups);
		//affUserReps(userReps,users,repos);

		
	}
	
	public static void affUsers(List<User> users) {
		System.out.println("Users : ");
		if (users != null) {
			for (User user : users) {
				System.out.println("---------------------------------------------");
				System.out.println("User id : "+user.getId());
				System.out.println("Username : "+user.getName());
				System.out.println("User login : "+user.getLogin());
				System.out.println("User password: "+user.getPassword());
			}
		}
	}
	
	public static void affGroups(List<Group> groups) {
		System.out.println("Groups : ");
		if (groups != null) {
			for (Group group : groups) {
				System.out.println("---------------------------------------------");
				System.out.println("Group id : "+group.getId());
				System.out.println("Group name : "+group.getName());
			}
		}
	}
	
	public static void affUserGroups(List<UserGroup> userGroups, List<User> users ,List<Group> groups) {
		System.out.println("UserGroups : ");
		if (userGroups != null) {
			for (UserGroup userGroup : userGroups) {
				System.out.println("---------------------------------------------");
				int userID = userGroup.getUserId();
				int groupID = userGroup.getGroupId();
				List <User> fUser =  users.stream().filter(user -> user.getId() == userID).collect(Collectors.toList());
				List <Group> fGroup =  groups.stream().filter(group -> group.getId() == groupID).collect(Collectors.toList());
				System.out.println("Filtered user : " + fUser);
				System.out.println("Filtered group : " + fGroup);
				if ( fUser.size() > 0 && fGroup.size() > 0  ) {
					System.out.println("UserGroup user id : " + userGroup.getUserId() + " | User login : " + fUser.get(0).getLogin() + " | id : " +fUser.get(0).getId() ); 
					System.out.println("UserGroup group id: " + userGroup.getGroupId()+ " | Group name : " + fGroup.get(0).getName() + " | id : " +fGroup.get(0).getId() );
				} else {
					System.out.println("Bad user id ("+userGroup.getUserId()+") or group id ("+userGroup.getGroupId()+")");
				}
			}
		}
	}
	
	
	public static void affUserReps(List<UserRep> userReps, List<User> users, List<Repository> repos) {
		System.out.println("UserRep : ");
		if (userReps != null) {
			for (UserRep userRep : userReps) {
				System.out.println("---------------------------------------------");
				int userID = userRep.getUserId();
				int repoID = userRep.getRepositoryId();
				List <User> fUser =  users.stream().filter(user -> user.getId() == userID).collect(Collectors.toList());
				List<Repository> fRepos =  repos.stream().filter(group -> group.getId() == repoID).collect(Collectors.toList());
				System.out.println("Filtered user : " + fUser);
				System.out.println("Filtered group : " + fRepos);
				if ( fUser.size() > 0 && fRepos.size() > 0  ) {
					System.out.println("UserRep user id : " + userRep.getUserId() + " | User login : " + fUser.get(0).getLogin() + " | id : " +fUser.get(0).getId() ); 
					System.out.println("UserRep repo id: " + userRep.getRepositoryId()+ " | repo name : " + fRepos.get(0).getName() + " | id : " +fRepos.get(0).getId() );
				} else {
					System.out.println("Bad user id ("+userRep.getUserId()+") or group id ("+userRep.getRepositoryId()+")");
				}
			}
		}
	}
}
