package bpm.vanilla.api.controller;

import java.util.Optional;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
import bpm.vanilla.api.core.IAPIManager.OLAPMethod;
import bpm.vanilla.api.core.exception.VanillaApiError;
import bpm.vanilla.api.core.exception.VanillaApiException;
import bpm.vanilla.api.core.remote.APICommunicator;

@RestController
public class OLAPController {

	private APICommunicator communicator;

	public OLAPController() {
		this.communicator = new APICommunicator();
	}

	@GetMapping("/cubes")
	public APIResponse getCubes(@RequestParam Integer repositoryID, @RequestParam Integer groupID) {
		String result = null;
		try {
			result = communicator.executeAction(new APIAction(IAPIType.OLAP, OLAPMethod.GET_CUBES, repositoryID, groupID));

		} catch (Exception e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.UNABLE_GET_CUBES);
		}
		result = manageError(result);
		return new APIResponse(parseToJSON(result));
	}

	@PostMapping("/cubeExplorer/executeQuery")
	public APIResponse executeCubeQuery(@RequestParam Integer repositoryID, @RequestParam Integer groupID, @RequestParam int fasdID, @RequestParam String cubeName, @RequestBody String query) {
		String result = null;
		try {
			result = communicator.executeAction(new APIAction(IAPIType.OLAP, OLAPMethod.EXECUTE_CUBE_QUERY, repositoryID, groupID, fasdID, cubeName, query));

		} catch (Exception e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.CUBE_NOT_FOUND);
		}
		result = manageError(result);
		return new APIResponse(parseToJSON(result));
	}

	@GetMapping("/cubeExplorer/view/info")
	public APIResponse getCubeView(@RequestParam Integer repositoryID, @RequestParam Integer groupID, @RequestParam int fasdID, @RequestParam String cubeName, @RequestParam(required = false) Optional<Integer> viewID) {
		String result = null;
		int sendedViewID = viewID.orElse(-1);
		try {
			result = communicator.executeAction(new APIAction(IAPIType.OLAP, OLAPMethod.GET_CUBE_VIEW, repositoryID, groupID, fasdID, cubeName, sendedViewID));

		} catch (Exception e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.CUBE_VIEW_NOT_FOUND);
		}
		result = manageError(result);
		return new APIResponse(parseToJSON(result));
	}

	@PutMapping("/cubeExplorer/view/save")
	public APIResponse saveCubeView(@RequestParam Integer repositoryID, @RequestParam Integer groupID, @RequestParam int fasdID, @RequestParam String cubeName, @RequestParam(required = false) Optional<Integer> viewID, @RequestParam String viewName, @RequestParam boolean export, @RequestBody String query) {
		String result = null;
		int sendedViewID = viewID.orElse(-1);
		try {
			result = communicator.executeAction(new APIAction(IAPIType.OLAP, OLAPMethod.SAVE_CUBE_VIEW, repositoryID, groupID, fasdID, cubeName, sendedViewID, viewName, export, query));

		} catch (Exception e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.UNABLE_SAVE_VIEW);
		}
		result = manageError(result);
		return new APIResponse(parseToJSON(result));
	}

	@PostMapping("/cubeExplorer/details")
	public APIResponse getDetails(@RequestParam Integer repositoryID, @RequestParam Integer groupID, @RequestParam int fasdID, @RequestParam String cubeName, @RequestParam String drillThroughSQL, @RequestBody String query) {
		String result = null;
		try {
			result = communicator.executeAction(new APIAction(IAPIType.OLAP, OLAPMethod.GET_DETAILS, repositoryID, groupID, fasdID, cubeName, drillThroughSQL, query));

		} catch (Exception e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.UNABLE_GET_DETAILS);
		}
		result = manageError(result);
		return new APIResponse(parseToJSON(result));
	}

	@GetMapping("/cubeExplorer/dimension/search")
	public APIResponse searchOnDimensions(@RequestParam Integer repositoryID, @RequestParam Integer groupID, @RequestParam int fasdID, @RequestParam String cubeName, @RequestParam String word) {
		String result = null;
		try {
			result = communicator.executeAction(new APIAction(IAPIType.OLAP, OLAPMethod.SEARCH_ON_DIMENSIONS, repositoryID, groupID, fasdID, cubeName, word));

		} catch (Exception e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.UNABLE_SEARCH_DIMENSION);
		}
		result = manageError(result);
		return new APIResponse(parseToJSON(result));
	}

	@GetMapping("/cubeExplorer/parameters/info")
	public APIResponse getParameters(@RequestParam Integer repositoryID, @RequestParam Integer groupID, @RequestParam int fasdID, @RequestParam String cubeName, @RequestParam int viewID) {
		String result = null;
		try {
			result = communicator.executeAction(new APIAction(IAPIType.OLAP, OLAPMethod.GET_PARAMETERS, repositoryID, groupID, fasdID, cubeName, viewID));

		} catch (Exception e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.UNABLE_GET_PARAMETERS);
		}
		result = manageError(result);
		return new APIResponse(parseToJSON(result));
	}

	@PostMapping("/cubeExplorer/parameters/set")
	public APIResponse setParameters(@RequestParam Integer repositoryID, @RequestParam Integer groupID, @RequestParam int fasdID, @RequestParam String cubeName, @RequestParam int viewID, @RequestBody String parameters) {
		String result = null;
		try {
			result = communicator.executeAction(new APIAction(IAPIType.OLAP, OLAPMethod.SET_PARAMETERS, repositoryID, groupID, fasdID, cubeName, viewID, parameters));

		} catch (Exception e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.UNABLE_SET_PARAMETERS);
		}
		result = manageError(result);
		return new APIResponse(parseToJSON(result));
	}

	public String manageError(String result) {
		if (result.contains("{\"error\": ")) {
			String tmp = result.replace("{\"error\": ", "");
			tmp = tmp.substring(0, tmp.indexOf("}"));
			throw new VanillaApiException(VanillaApiError.getVanillaApiErrorByCode(Integer.parseInt(tmp)));
		}
		else {
			return result;
		}
	}

	public JsonNode parseToJSON(String s) {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode actualObj = null;
		try {
			actualObj = mapper.readTree(s);
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return actualObj;
	}
}
