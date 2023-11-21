package bpm.vanillahub.runtime.utils;

import bpm.vanilla.platform.core.utils.CommunicatorHelper;

public class PoleEmploiHelper {

	private static final String EMPLOI_API_URL = "https://api.emploi-store.fr";
	private static final String EMPLOI_SEARCH = "/partenaire/offresdemploi/v2/offres/search?";
	
//	private static final String TOKEN = "baf9ffde-7966-4cde-8eb8-480b1cb94480";
//	private static final String token = "60YBVC5jPpCaNbLt3RTjoO_afx0";
	
	public static void main(String[] args) throws Exception {
//		String token = OAuth2Helper.requestAccessToken();
//		
//		PoleEmploiHelper helper = new PoleEmploiHelper();
//		String offres = helper.getOffres(token, "commune=94071");
//		if (offres != null) {
//			System.out.println(offres);
//		}
//		else {
//			System.out.println("No jobs found");
//		}
	}
	
	public String getOffres(String token, String parameters) throws Exception {
		String json = null;
		try {
			json = CommunicatorHelper.sendGetMessageWithAuth(EMPLOI_API_URL, EMPLOI_SEARCH + parameters, token);
			if (json == null || json.isEmpty()) {
				throw new Exception(EMPLOI_API_URL + " is not available.");
			}

			return json;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Unable to get job offers from Pole Emploi at url '" + EMPLOI_API_URL + "': " + e.getMessage());
		}
	}
}
