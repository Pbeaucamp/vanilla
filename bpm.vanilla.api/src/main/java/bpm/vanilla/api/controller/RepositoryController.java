package bpm.vanilla.api.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import bpm.vanilla.api.controller.commons.APIResponse;
import bpm.vanilla.api.service.user.RepositoryService;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.UserRep;

@RestController
public class RepositoryController {

	private RepositoryService repositoryService;

	public RepositoryController() {
		this.repositoryService = new RepositoryService();
	}

	@GetMapping("/repositories")
	public APIResponse getRepositories() {
		List<Repository> repositories = repositoryService.getRepositories();
		return new APIResponse(repositories);
	}

	@PostMapping("/user/repository/add")
	public APIResponse addUserToRepository(@RequestBody RepositoryForm repositoryForm) {
		UserRep userRep = repositoryService.addUserToRepository(repositoryForm.userLogin, repositoryForm.repositoryID);
		return new APIResponse(userRep);
	}

	static class RepositoryForm {
		public String userLogin;
		public String repositoryID;
	}
}
