package bpm.vanilla.api.controller;

import java.io.InputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import bpm.vanilla.api.controller.commons.APIResponse;
import bpm.vanilla.api.core.APIAction;
import bpm.vanilla.api.core.IAPIManager.IAPIType;
import bpm.vanilla.api.core.IAPIManager.ViewerMethod;
import bpm.vanilla.api.core.exception.VanillaApiError;
import bpm.vanilla.api.core.exception.VanillaApiException;
import bpm.vanilla.api.core.remote.APICommunicator;
import bpm.vanilla.platform.core.beans.ged.constant.Formats;

@RestController
public class ViewerController {

	private APICommunicator communicator;

	public ViewerController() {
		this.communicator = new APICommunicator();
	}
	
	@ExceptionHandler
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public void handle(HttpMessageNotReadableException e) {
		System.out.println("Returning HTTP 400 Bad Request");
		e.printStackTrace();
//	    logger.warn("Returning HTTP 400 Bad Request", e);
	}

	@PostMapping("/viewers")
	public APIResponse loadViewers(@RequestParam String repositoryID, @RequestParam String groupID, @RequestParam String mode, @RequestBody String openedDirs) {
		String result = null;
		try {
			result = communicator.executeAction(new APIAction(IAPIType.VIEWER, ViewerMethod.LOAD_VIEWERS, repositoryID, groupID, mode, openedDirs));
		} catch (Exception e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.UNABLE_GET_MODELS);
		}
		result = manageError(result);
		return new APIResponse(parseToJSON(result));
	}

	@GetMapping("/viewer/informations")
	public APIResponse getViewerInformation(@RequestParam String repositoryID, @RequestParam String groupID, @RequestParam String userLogin, @RequestParam String reportID) {
		String result = null;
		try {
			result = communicator.executeAction(new APIAction(IAPIType.VIEWER, ViewerMethod.GET_VIEWER_INFORMATION, repositoryID, groupID, userLogin, reportID));
		} catch (Exception e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.UNABLE_GET_MODELS);
		}
		result = manageError(result);
		return new APIResponse(parseToJSON(result));
	}

	@RequestMapping(value = "/viewer/open", method = RequestMethod.POST)
	public void download(@RequestParam String repositoryID, @RequestParam String groupID, @RequestParam String userLogin, @RequestParam String reportID, @RequestParam String format, @RequestBody String groupParameters, HttpServletResponse response) {
		try {
			// InputStream inputStream = new FileInputStream(new
			// File("C:/Users/svi/Desktop/FichePaie/FeuillePayeTese-Sept-2019-SVI.pdf"));
			// //load the file
			response.setHeader("Content-disposition", "filename=ReportID" + reportID + "." + format);
			if (format != null) {
				String mime = "";
				for (Formats f : Formats.values()) {
					if (f.getExtension().equals(format)) {
						mime = f.getMime();
						break;
					}
				}
				if (!mime.equals("")) {
					response.setContentType(mime);
				}
			}
			InputStream inputStream = communicator.executeStreamAction(new APIAction(IAPIType.VIEWER, ViewerMethod.OPEN_VIEWER, repositoryID, groupID, userLogin, reportID, format, groupParameters));
			IOUtils.copy(inputStream, response.getOutputStream());
			response.flushBuffer();
		} catch (Exception e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.UNABLE_GET_MODELS);
		}
	}

	@PutMapping("/viewer/historize")
	public APIResponse historizeViewer(@RequestParam String repositoryID, @RequestParam String groupID, @RequestParam String userLogin, @RequestParam String reportID, @RequestBody String histoConfig) {
		String result = null;
		try {
			result = communicator.executeAction(new APIAction(IAPIType.VIEWER, ViewerMethod.HISTORIZE_VIEWER, repositoryID, groupID, userLogin, reportID, histoConfig));
		} catch (Exception e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.UNABLE_GET_MODELS);
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

	@PutMapping("/viewer/config/save")
	public APIResponse saveViewerConfig(@RequestParam String repositoryID, @RequestParam String groupID, @RequestParam String userLogin, @RequestParam String reportID, @RequestParam String configName, @RequestParam String configDescription, @RequestBody String groupParameters) {
		String result = null;
		try {
			result = communicator.executeAction(new APIAction(IAPIType.VIEWER, ViewerMethod.SAVE_VIEWER_CONFIG, repositoryID, groupID, userLogin, reportID, configName, configDescription, groupParameters));
		} catch (Exception e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.UNABLE_GET_MODELS);
		}
		result = manageError(result);
		return new APIResponse(parseToJSON(result));
	}

	public JsonNode parseToJSON(String s) {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode actualObj = null;
		try {
			actualObj = mapper.readTree(s);
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return actualObj;
	}
}
