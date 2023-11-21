package bpm.vanilla.api.controller;

import java.io.InputStream;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import bpm.vanilla.api.controller.commons.APIResponse;
import bpm.vanilla.api.core.APIAction;
import bpm.vanilla.api.core.IAPIManager.FWRMethod;
import bpm.vanilla.api.core.IAPIManager.IAPIType;
import bpm.vanilla.api.core.exception.VanillaApiError;
import bpm.vanilla.api.core.exception.VanillaApiException;
import bpm.vanilla.api.core.remote.APICommunicator;
import bpm.vanilla.platform.core.beans.ged.constant.Formats;

@RestController
public class FWRController {

	private APICommunicator communicator;

	public FWRController() {
		this.communicator = new APICommunicator();
	}

	@RequestMapping(value = "/report/preview", method = RequestMethod.POST)
	public void previewReport(@RequestParam String repositoryID, @RequestParam String groupID, @RequestParam String userLogin, @RequestParam String format, @RequestBody String reportSheet, HttpServletResponse response) {
		try {
			// InputStream inputStream = new FileInputStream(new
			// File("C:/Users/svi/Desktop/FichePaie/FeuillePayeTese-Sept-2019-SVI.pdf"));
			// //load the file
			response.setHeader("Content-disposition", "filename=previewReport." + format);
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
			InputStream inputStream = communicator.executeStreamAction(new APIAction(IAPIType.FWR, FWRMethod.PREVIEW_REPORT, repositoryID, groupID, userLogin, format, reportSheet));
			IOUtils.copy(inputStream, response.getOutputStream());
			response.flushBuffer();
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: Change error
			throw new VanillaApiException(VanillaApiError.UNABLE_PREVIEW_REPORT);
		}
	}

	@GetMapping("/fwr/reports")
	public APIResponse getFWRReports(@RequestParam String repositoryID, @RequestParam String groupID) {
		String result = null;
		try {
			result = communicator.executeAction(new APIAction(IAPIType.FWR, FWRMethod.GET_FWR_REPORTS, repositoryID, groupID));

		} catch (Exception e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.UNABLE_GET_FWR_REPORTS);
		}
		result = manageError(result);
		return new APIResponse(parseToJSON(result));
	}

	@PutMapping("/report/save")
	public APIResponse saveReport(@RequestParam String repositoryID, @RequestParam String groupID, @RequestParam String userLogin, @RequestParam Integer directoryID, @RequestParam Optional<Integer> reportID, @RequestParam String format, @RequestBody String reportSheet) {
		String result = null;
		int sendedReportID = reportID.orElse(-1);
		try {
			result = communicator.executeAction(new APIAction(IAPIType.FWR, FWRMethod.SAVE_REPORT, repositoryID, groupID, userLogin, directoryID, sendedReportID, format, reportSheet));

		} catch (Exception e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.UNABLE_SAVE_REPORT);
		}
		result = manageError(result);
		return new APIResponse(parseToJSON(result));
	}

	@GetMapping("/report/load")
	public APIResponse loadReport(@RequestParam String repositoryID, @RequestParam String groupID, @RequestParam Integer reportID) {
		String result = null;
		try {
			result = communicator.executeAction(new APIAction(IAPIType.FWR, FWRMethod.LOAD_REPORT, repositoryID, groupID, reportID));

		} catch (Exception e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.UNABLE_LOAD_REPORT);
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
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return actualObj;
	}
}
