package bpm.vanilla.api.controller;

import java.util.Optional;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import bpm.vanilla.api.controller.commons.APIResponse;
import bpm.vanilla.api.core.APIAction;
import bpm.vanilla.api.core.IAPIManager.IAPIType;
import bpm.vanilla.api.core.IAPIManager.RepositoriesMethod;
import bpm.vanilla.api.core.exception.VanillaApiError;
import bpm.vanilla.api.core.exception.VanillaApiException;
import bpm.vanilla.api.core.remote.APICommunicator;
import bpm.vanilla.api.service.user.RepositoryService;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;

@RestController
public class RepositoriesController {

	private APICommunicator communicator;
	private RepositoryService repositoryService;

	public RepositoriesController() {
		this.communicator = new APICommunicator();
		this.repositoryService = new RepositoryService();
	}

	@GetMapping("/Items")
	public APIResponse getInDirectory(@RequestParam int repoID, @RequestParam int grpID, @RequestParam(required = false) Optional<Integer> dirID) {
		String result = null;
		int value = dirID.orElse(-1);
		try {
			result = communicator.executeAction(new APIAction(IAPIType.REPOSITORIES, RepositoriesMethod.GET_ITEMS, repoID, grpID, value));

		} catch (Exception e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.FILE_NOT_FOUND);
		}
		result = manageError(result);
		ObjectMapper mapper = new ObjectMapper();
		JsonNode actualObj = null;
		try {
			actualObj = mapper.readTree(result);
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return new APIResponse(actualObj);

	}

	@GetMapping("/AllItems")
	public APIResponse getAllItems(@RequestParam int repoID, @RequestParam int grpID, @RequestParam(required = false) Optional<Integer> dirID) {
		String result = null;
		int value = dirID.orElse(-1);
		try {
			result = communicator.executeAction(new APIAction(IAPIType.REPOSITORIES, RepositoriesMethod.GET_ALL_ITEMS, repoID, grpID, value));

		} catch (Exception e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.FILE_NOT_FOUND);
		}
		result = manageError(result);
		ObjectMapper mapper = new ObjectMapper();
		JsonNode actualObj = null;
		try {
			actualObj = mapper.readTree(result);
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return new APIResponse(actualObj);
	}

	@PostMapping("/directory")
	public APIResponse getDirectoryByName(@RequestBody DirectoryForm directory) {
		RepositoryDirectory dir = repositoryService.getDirectoryByName(directory.repositoryId, directory.groupId, directory.parentId, directory.directoryName);
		return new APIResponse(dir);
	}

	@PostMapping("/directory/add")
	public APIResponse addDirectory(@RequestBody DirectoryForm directory) {
		repositoryService.addDirectory(directory.repositoryId, directory.groupId, directory.parentId, directory.directoryName);
		return new APIResponse("The directory has been created.");
	}
	
	@PostMapping("/directory/group/add")
	public APIResponse addGroupToDirectory(@RequestBody GroupDirectoryForm groupDirectory) {
		repositoryService.addGroupToDirectory(groupDirectory.repositoryId, groupDirectory.groupId, groupDirectory.directoryId);
		return new APIResponse("The group has been added to the directory.");
	}

	public String manageError(String result) {
		if (result.contains("ERROR bpm.vanilla.api.runtime : ")) {
			String tmp = result.replace("ERROR bpm.vanilla.api.runtime : ", "");
			tmp = tmp.substring(0, tmp.indexOf(" -"));
			throw new VanillaApiException(VanillaApiError.getVanillaApiErrorByCode(Integer.parseInt(tmp)));
		}
		else {
			return result;
		}
	}

	static class DirectoryForm {
		public int repositoryId;
		public int groupId;
		public int parentId;
		public String directoryName;
	}

	static class GroupDirectoryForm {
		public int repositoryId;
		public int groupId;
		public int directoryId;
	}
}
