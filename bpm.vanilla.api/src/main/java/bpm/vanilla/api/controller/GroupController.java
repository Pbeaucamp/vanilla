package bpm.vanilla.api.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import bpm.vanilla.api.controller.commons.APIResponse;
import bpm.vanilla.api.service.user.GroupService;
import bpm.vanilla.platform.core.beans.Group;

@RestController
public class GroupController {

	private GroupService groupService;

	public GroupController() {
		this.groupService = new GroupService();
	}

	@GetMapping("/groups")
	public APIResponse getGroups() {
		List<Group> groups = groupService.getGroups();
		return new APIResponse(groups);
	}

	@GetMapping("/group")
	public APIResponse getGroup(@RequestParam String name) {
		Group group = groupService.getGroup(name);
		return new APIResponse(group);
	}

	@PostMapping("/user/group/add")
	public APIResponse addUserToGroup(@RequestBody GroupForm groupForm) {
		String userGroup = groupService.addUserToGroup(groupForm.userLogin, groupForm.groupId);

		return new APIResponse(userGroup);
	}

	@PostMapping("/user/group/remove")
	public APIResponse removeUserFromGroup(@RequestBody GroupForm groupForm) {
		String userGroup = groupService.removeUserFromGroup(groupForm.userLogin, groupForm.groupId);

		return new APIResponse(userGroup);
	}

	@PostMapping("/group/{groupName}/create")
	public APIResponse addGroup(@PathVariable String groupName) {
		return new APIResponse(groupService.addGroup(groupName));
	}

	@DeleteMapping("/group/{groupID}/remove")
	public APIResponse removeGroup(@PathVariable String groupID) {
		return new APIResponse(groupService.removeGroup(groupID));
	}

	@PostMapping("/group/role/add")
	public APIResponse addRoleToGroup(@RequestBody RoleForm roleForm) {
		String result = groupService.addRoleToGroup(roleForm.roleName, roleForm.groupId);
		return new APIResponse(result);
	}

	static class GroupForm {
		public String userLogin;
		public int groupId;
	}

	static class RoleForm {
		public String roleName;
		public int groupId;
	}
}
