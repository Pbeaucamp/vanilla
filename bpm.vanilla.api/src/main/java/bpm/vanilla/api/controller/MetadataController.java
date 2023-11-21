package bpm.vanilla.api.controller;

import java.util.List;

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
import bpm.vanilla.api.core.IAPIManager.MetadataMethod;
import bpm.vanilla.api.core.exception.VanillaApiError;
import bpm.vanilla.api.core.exception.VanillaApiException;
import bpm.vanilla.api.core.remote.APICommunicator;

@RestController
public class MetadataController {

	private APICommunicator communicator;

	public MetadataController() {
		this.communicator = new APICommunicator();
	}

	@GetMapping("/metadatas")
	public APIResponse getMetadatas(@RequestParam String repositoryID, @RequestParam String groupID) {
		String result = null;
		try {
			result = communicator.executeAction(new APIAction(IAPIType.METADATA, MetadataMethod.GET_METADATAS, repositoryID, groupID));

		} catch (Exception e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.UNABLE_GET_METADATAS);
		}
		/*
		 * result = manageError(result); List<String> list = new
		 * ArrayList<String>( Arrays.asList(result.split(", ")) ); return new
		 * APIResponse(list);
		 */
		result = manageError(result);
		return new APIResponse(parseToJSON(result));

	}

	@GetMapping("/metadata/info")
	public APIResponse loadMetadata(@RequestParam String repositoryID, @RequestParam String groupID, @RequestParam int metadataID) {
		String result = null;
		try {
			result = communicator.executeAction(new APIAction(IAPIType.METADATA, MetadataMethod.LOAD_METADATA, repositoryID, groupID, metadataID));

		} catch (Exception e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.UNABLE_LOAD_METADATA);
		}
		result = manageError(result);
		return new APIResponse(parseToJSON(result));

	}

	@GetMapping("/models")
	public APIResponse getBusinessModels(@RequestParam String repositoryID, @RequestParam String groupID, @RequestParam String metadataName) {
		String result = null;
		try {
			result = communicator.executeAction(new APIAction(IAPIType.METADATA, MetadataMethod.GET_BUSINESS_MODELS, repositoryID, groupID, metadataName));
		} catch (Exception e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.UNABLE_GET_MODELS);
		}
		result = manageError(result);
		return new APIResponse(parseToJSON(result));
	}

	@GetMapping("/packages")
	public APIResponse getBusinessPacakages(@RequestParam String repositoryID, @RequestParam String groupID, @RequestParam String metadataName, @RequestParam String modelName) {
		String result;
		try {
			result = communicator.executeAction(new APIAction(IAPIType.METADATA, MetadataMethod.GET_BUSINESS_PACKAGES, repositoryID, groupID, metadataName, modelName));
		} catch (Exception e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.UNABLE_GET_PACKAGES);
		}
		/*
		 * result = manageError(result); List<String> list = new
		 * ArrayList<String>( Arrays.asList(result.split(", ")) ); return new
		 * APIResponse(list);
		 */
		result = manageError(result);
		return new APIResponse(parseToJSON(result));
	}

	@GetMapping("/tables")
	public APIResponse getBusinessTables(@RequestParam String repositoryID, @RequestParam String groupID, @RequestParam String metadataName, @RequestParam String modelName, @RequestParam String packageName) {
		String result;
		try {
			result = communicator.executeAction(new APIAction(IAPIType.METADATA, MetadataMethod.GET_BUSINESS_TABLES, repositoryID, groupID, metadataName, modelName, packageName));
		} catch (Exception e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.UNABLE_GET_TABLES);
		}
		/*
		 * result = manageError(result); List<String> list = new
		 * ArrayList<String>( Arrays.asList(result.split(", ")) ); return new
		 * APIResponse(list);
		 */
		result = manageError(result);
		return new APIResponse(parseToJSON(result));
	}

	@GetMapping("/columns")
	public APIResponse getColumns(@RequestParam String repositoryID, @RequestParam String groupID, @RequestParam String metadataName, @RequestParam String modelName, @RequestParam String packageName, @RequestParam String tableName) {
		String result;
		try {
			result = communicator.executeAction(new APIAction(IAPIType.METADATA, MetadataMethod.GET_COLUMNS, repositoryID, groupID, metadataName, modelName, packageName, tableName));
		} catch (Exception e) {
			throw new VanillaApiException(VanillaApiError.UNABLE_GET_COLUMN);
		}
		/*
		 * result = manageError(result); List<String> list = new
		 * ArrayList<String>( Arrays.asList(result.split(", ")) ); return new
		 * APIResponse(list);
		 */
		result = manageError(result);
		return new APIResponse(parseToJSON(result));
	}

	@GetMapping("/values")
	public APIResponse getColumn(@RequestParam String repositoryID, @RequestParam String groupID, @RequestParam String metadataName, @RequestParam String modelName, @RequestParam String packageName, @RequestParam String tableName, @RequestParam String columnName) {
		String result;
		try {
			result = communicator.executeAction(new APIAction(IAPIType.METADATA, MetadataMethod.GET_COLUMN, repositoryID, groupID, metadataName, modelName, packageName, tableName, columnName));
		} catch (Exception e) {
			throw new VanillaApiException(VanillaApiError.UNABLE_GET_COLUMN_VALUES);
		}
		/*
		 * result = manageError(result); List<String> list = new
		 * ArrayList<String>( Arrays.asList(result.split(", ")) ); return new
		 * APIResponse(list);
		 */
		result = manageError(result);
		return new APIResponse(parseToJSON(result));
	}

	@GetMapping("/tables/columns")
	public APIResponse getBusinessTablesAndColumns(@RequestParam String repositoryID, @RequestParam String groupID, @RequestParam String metadataName, @RequestParam String modelName, @RequestParam String packageName) {
		String result;
		try {
			result = communicator.executeAction(new APIAction(IAPIType.METADATA, MetadataMethod.GET_TABLES_AND_COLUMNS, repositoryID, groupID, metadataName, modelName, packageName));
		} catch (Exception e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.UNABLE_GET_TABLES);
		}
		result = manageError(result);
		return new APIResponse(parseToJSON(result));
	}

	@GetMapping("/queries/saved")
	public APIResponse getPackageQueries(@RequestParam String repositoryID, @RequestParam String groupID, @RequestParam String metadataName, @RequestParam String modelName, @RequestParam String packageName) {
		String result;
		try {
			result = communicator.executeAction(new APIAction(IAPIType.METADATA, MetadataMethod.GET_BUSINESS_PACKAGE_QUERIES, repositoryID, groupID, metadataName, modelName, packageName));
		} catch (Exception e) {
			throw new VanillaApiException(VanillaApiError.BUSINESS_PACKAGE_NOT_FOUND);
		}
		/*
		 * result = manageError(result); List<String> list = new
		 * ArrayList<String>( Arrays.asList(result.split(", ")) ); return new
		 * APIResponse(list);
		 */
		result = manageError(result);
		return new APIResponse(parseToJSON(result));
	}

	@GetMapping("/query/saved")
	public APIResponse getSavedQueryData(@RequestParam String repositoryID, @RequestParam String groupID, @RequestParam String metadataName, @RequestParam String modelName, @RequestParam String packageName, @RequestParam String queryName) {
		String result;
		try {
			result = communicator.executeAction(new APIAction(IAPIType.METADATA, MetadataMethod.GET_SAVED_QUERY_DATA, repositoryID, groupID, metadataName, modelName, packageName, queryName));
		} catch (Exception e) {
			throw new VanillaApiException(VanillaApiError.BUSINESS_PACKAGE_NOT_FOUND);
		}
		/*
		 * result = manageError(result); List<String> list = new
		 * ArrayList<String>( Arrays.asList(result.split(";")) ); QueryData
		 * query = new QueryData(); query.queryDistinct =
		 * Boolean.parseBoolean(list.get(0)); query.queryLimit =
		 * Integer.parseInt(list.get(1)); query.columns =
		 * Arrays.asList(list.get(2).split(",")); return new APIResponse(query);
		 */
		result = manageError(result);
		return new APIResponse(parseToJSON(result));
	}

	@GetMapping("/query/result")
	public APIResponse getQueryResult(@RequestParam String repositoryID, @RequestParam String groupID, @RequestParam String metadataName, @RequestParam String modelName, @RequestParam String packageName, @RequestParam String columns, @RequestParam String queryLimit, @RequestParam String queryDistinct) {
		String result;
		try {
			result = communicator.executeAction(new APIAction(IAPIType.METADATA, MetadataMethod.GET_QUERY_RESULT, repositoryID, groupID, metadataName, modelName, packageName, columns, queryLimit, queryDistinct));
		} catch (Exception e) {
			throw new VanillaApiException(VanillaApiError.BUSINESS_PACKAGE_NOT_FOUND);
		}
		/*
		 * result = manageError(result); //List<String> list = new
		 * ArrayList<String>( Arrays.asList(result.split(", ")) ); return new
		 * APIResponse(result);
		 */
		result = manageError(result);
		return new APIResponse(parseToJSON(result));
	}

	@GetMapping("/query/sql")
	public APIResponse getQuerySQL(@RequestParam String repositoryID, @RequestParam String groupID, @RequestParam String metadataName, @RequestParam String modelName, @RequestParam String packageName, @RequestParam String columns, @RequestParam String queryLimit, @RequestParam String queryDistinct) {
		String result;
		try {
			result = communicator.executeAction(new APIAction(IAPIType.METADATA, MetadataMethod.GET_QUERY_SQL, repositoryID, groupID, metadataName, modelName, packageName, columns, queryLimit, queryDistinct));
		} catch (Exception e) {
			throw new VanillaApiException(VanillaApiError.BUSINESS_PACKAGE_NOT_FOUND);
		}
		result = manageError(result);
		return new APIResponse(parseToJSON(result));
	}

	@PostMapping("/query/save")
	public APIResponse saveQuery(@RequestBody QueryForm queryForm) {
		String result;
		try {
			result = communicator.executeAction(new APIAction(IAPIType.METADATA, MetadataMethod.SAVE_QUERY, queryForm.repositoryID, queryForm.groupID, queryForm.metadataName, queryForm.modelName, queryForm.packageName, queryForm.columns, queryForm.queryName, queryForm.queryDesc, queryForm.queryLimit, queryForm.queryDistinct));
		} catch (Exception e) {
			throw new VanillaApiException(VanillaApiError.BUSINESS_PACKAGE_NOT_FOUND);
		}
		result = manageError(result);
		// List<String> list = new ArrayList<String>(
		// Arrays.asList(result.split(", ")) );
		return new APIResponse(result);
	}

	static class QueryForm {
		public String repositoryID;
		public String groupID;
		public String metadataName;
		public String modelName;
		public String packageName;
		public String columns;
		public String queryName;
		public String queryDesc;
		public String queryLimit;
		public String queryDistinct;
	};

	static class QueryData {
		public Boolean queryDistinct;
		public int queryLimit;
		public List<String> columns;

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
