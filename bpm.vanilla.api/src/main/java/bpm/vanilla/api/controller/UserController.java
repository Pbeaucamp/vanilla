package bpm.vanilla.api.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import bpm.vanilla.api.controller.commons.APIResponse;
import bpm.vanilla.api.service.user.UserService;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;

@RestController
public class UserController {

	private UserService userService;

	public UserController() {
		this.userService = new UserService();
	}

	@GetMapping("/users")
	public APIResponse getUsers() {
		List<User> users = userService.getUsers();
		return new APIResponse(users);
	}

	@PostMapping("/user/create")
	public APIResponse createUser(@RequestBody UserForm userForm) {
		User user = userService.addUser(userForm.name, userForm.login, userForm.password, userForm.mail);
		return new APIResponse(user);
	}

	@DeleteMapping("/user/{login}/remove")
	public APIResponse removeUser(@PathVariable String login) {
		String response = userService.removeUser(login);
		return new APIResponse(response);
	}

	@GetMapping("/user/{login}/groups")
	public APIResponse getUserGroups(@PathVariable String login) {
		List<Group> groups = userService.getUserGroups(login);
		return new APIResponse(groups);
	}

	@GetMapping("/user/{login}/repositories")
	public APIResponse getUserRepositories(@PathVariable String login) {
		List<Repository> repositories = userService.getUserRepositories(login);
		return new APIResponse(repositories);
	}

	@PutMapping("/user/{login}/password")
	public APIResponse modifyUserPassword(@PathVariable String login, @RequestBody Password password) {
		return new APIResponse(userService.modifyUserPassword(login, password.password));
	}

	static class UserForm {
		public String name;
		public String login;
		public String password;
		public String mail;
	}

	static class Password {
		public String password;
	}

}
