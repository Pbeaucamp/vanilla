package bpm.vanilla.api.controller;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import bpm.vanilla.api.controller.commons.APIResponse;
import bpm.vanilla.api.core.APIAction;
import bpm.vanilla.api.core.IAPIManager.IAPIType;
import bpm.vanilla.api.core.IAPIManager.KPIMethod;
import bpm.vanilla.api.core.exception.VanillaApiError;
import bpm.vanilla.api.core.exception.VanillaApiException;
import bpm.vanilla.api.core.remote.APICommunicator;

@RestController
public class KPIController {

	private APICommunicator communicator;

	public KPIController() {
		this.communicator = new APICommunicator();
	}
	
	private APIResponse buildResponse(KPIMethod method, Object... parameters) {
		String result = null;
		try {
			result = communicator.executeAction(new APIAction(IAPIType.KPI, method, parameters));
		} catch (Exception e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.FILE_NOT_FOUND);
		}
		
		return new APIResponse(result);
	}

	@GetMapping("/axis")
	public APIResponse getAxis() {
		return buildResponse(KPIMethod.GET_AXIS);
	}

	// Tmp
	@GetMapping("/getobservatories")
	public APIResponse getObservatories() {
		return buildResponse(KPIMethod.GET_OBSERVATORIES);
	}

	@GetMapping("/group/{groupId}/observatories")
	public APIResponse getObservatoriesByGroup(@PathVariable int groupId) {
		return buildResponse(KPIMethod.GET_OBSERVATORIES_BY_GROUP, groupId);
	}

	@GetMapping("/user/{login}/observatories")
	public APIResponse getObservatoriesByUser(@PathVariable String login) {
		return buildResponse(KPIMethod.GET_OBSERVATORIES_BY_USER, login);
	}

	@GetMapping("/themes")
	public APIResponse getTheme() {
		return buildResponse(KPIMethod.GET_THEMES);
	}

	@GetMapping("/group/{groupId}/themes")
	public APIResponse getThemesByGroup(@PathVariable int groupId) {
		return buildResponse(KPIMethod.GET_THEMES_BY_GROUP, groupId);
	}

	@GetMapping("/user/{login}/themes")
	public APIResponse getThemesByUser(@PathVariable String login) {
		return buildResponse(KPIMethod.GET_THEMES_BY_USER, login);
	}

	@GetMapping("/user/{login}/kpis")
	public APIResponse getKpiListByUser(@PathVariable String login) {
		return buildResponse(KPIMethod.GET_KPIS_BY_USER, login);
	}

	@GetMapping("/group/{groupId}/kpis")
	public APIResponse getKpiListByGroup(@PathVariable int groupId) {
		return buildResponse(KPIMethod.GET_KPIS_BY_GROUP, groupId);
	}

	@GetMapping("/observatory/{observatoryID}/kpis")
	public APIResponse getKpiListByObservatory(@PathVariable int observatoryID) {
		return buildResponse(KPIMethod.GET_KPIS_BY_OBSERVATORY, observatoryID);
	}

	@GetMapping("/observatory/{observatoryID}/themes")
	public APIResponse getThemeByObservatory(@PathVariable int observatoryID) {
		return buildResponse(KPIMethod.GET_THEMES_BY_OBSERVATORY, observatoryID);
	}

	@GetMapping("/theme/{themeID}/kpis")
	public APIResponse getKpiListByTheme(@PathVariable int themeID) {
		return buildResponse(KPIMethod.GET_KPIS_BY_THEME, themeID);
	}

	@SuppressWarnings("deprecation")
	@GetMapping("/group/{groupId}/kpi/{kpiID}/value")
	public APIResponse getKPIValuebyID(@PathVariable int groupId, @PathVariable int kpiID, @RequestParam(name = "date", required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") Date date) {
		if (date != null) {
			date.setMonth((date.getMonth() % 12) + 1);
		}
		else {
			date = new Date();
		}
		
		return buildResponse(KPIMethod.GET_KPI_VALUE, kpiID, new Date(), groupId);
	}

	@GetMapping("/group/{groupId}/kpi/{kpiID}/values")
	public APIResponse getKPIValuesbyId(@PathVariable int groupId, @PathVariable int kpiID, @RequestParam(name = "date", required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") Date date) {
		if (date == null) {
			date = new Date();
		}
		return buildResponse(KPIMethod.GET_KPI_VALUES, kpiID, date, groupId);
	}

	@GetMapping("/kpi/{kpiID}/axis")
	public APIResponse getKPIAxis(@PathVariable int kpiID) {
		return buildResponse(KPIMethod.GET_KPI_AXIS, kpiID);
	}

	@GetMapping("/axis/{axisID}/members")
	public APIResponse getAxisMembers(@PathVariable int axisID) {
		return buildResponse(KPIMethod.GET_AXIS_MEMBERS, axisID);
	}

	@GetMapping("/group/{groupId}/kpi/{kpiID}/axis/{axisID}/values")
	public APIResponse getKpiAxisValues(@PathVariable int groupId, @PathVariable int kpiID, @PathVariable int axisID, @RequestParam(name = "date", required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") Date date) {
		if (date == null) {
			date = new Date();
		}
		return buildResponse(KPIMethod.GET_GROUP_AXIS_VALUES, kpiID, axisID, date, groupId);
	}

	@GetMapping("/group/{groupId}/kpi/{kpiID}/axis/{axisID}/value")
	public APIResponse getKpiAxisValue(@PathVariable int groupId, @PathVariable int kpiID, @PathVariable int axisID, @RequestParam(name = "date", required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") Date date) {
		if (date == null) {
			date = new Date();
		}
		return buildResponse(KPIMethod.GET_GROUP_AXIS_VALUE, kpiID, axisID, date, groupId);
	}

}
