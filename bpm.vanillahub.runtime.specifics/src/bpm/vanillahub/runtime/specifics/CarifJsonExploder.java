package bpm.vanillahub.runtime.specifics;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * This class is design to explode a JSON coming from AGEFMA Carif
 * 
 * Separate in 3 file - organismeformation - actionformation - sessionformation
 * 
 */
public class CarifJsonExploder {
	
//	private static final String DATE = "0705202112";
//
//	private static final String SOURCE = "D:/DATA/Test/0426/agefma_data_" + DATE + ".json";
//	private static final String CIBLE_ORGANISMES = "D:/DATA/Test/0426/organismes_" + DATE + ".json";
//	private static final String CIBLE_DOMAINES = "D:/DATA/Test/0426/domaines_" + DATE + ".json";
//	private static final String CIBLE_ACTIONS = "D:/DATA/Test/0426/actions_" + DATE + ".json";
//	private static final String CIBLE_SESSIONS = "D:/DATA/Test/0426/sessions_" + DATE + ".json";
//	private static final String CIBLE_SPECIALITES = "D:/DATA/Test/0426/specialites_" + DATE + ".json";
//	private static final String CIBLE_NIVEAU_SORTIE_AF = "D:/DATA/Test/0426/niveau_sorties_af_" + DATE + ".json";

	private static final String ORGANISMES = "organismeformation";
	private static final String DOMAINES = "domaineformation";
	private static final String ACTIONS = "actionformation";
	private static final String SESSIONS = "sessionformation";
	private static final String SPECIALITES = "specialite_af";
	private static final String NIVEAU_SORTIE_AF = "niveau_sortie_af";

	private static final String KEY_NO_SIRET = "no_siret";
	private static final String KEY_NO_SITE = "no_site";
	private static final String KEY_CO_ACTION_FORMATION = "co_action_formation";

	private static final String KEY_PARENT = "parent_id";
	
	private static final String KEY_ARRAY_ACTIONS_NIVEAU_ENTREE_AF = "niveau_entree_af";
	private static final String KEY_ARRAY_SESSION_CONTRAT_SF = "contrat_sf";
	private static final String KEY_ARRAY_SESSION_FINANCEMENT_SF = "financement_sf";
	private static final String KEY_ARRAY_SESSION_MESURES_SF = "mesures_sf";
	private static final String KEY_ARRAY_SESSION_PUBLIC_SF = "public_sf";
	private static final String KEY_ARRAY_SESSION_TYPE_FORMATION_SF = "type_formation_sf";

	/**
	 * This program take 3 arguments
	 *  1. source file path
	 *  2. target folder path
	 * 
	 * @param args
	 * @throws Exception 
	 */
	public static void carif(String[] args) throws Exception {
		if (args.length < 3) {
			throw new Exception("Arguments are not valid. Should be type, source, target.");
		}
		
		String source = args[1];
		String target = args[2];
		
		//We add a slash at the end if not present
		target = target.endsWith("/") ? target : target+ "/" ;
		
		String cibleOrganismes = target + "organismes" + ".json";
		String cibleDomaines = target + "domaines" + ".json";
		String cibleActions = target + "actions" + ".json";
		String cibleSessions = target + "sessions" + ".json";
		String cibleSpecialites = target + "specialites" + ".json";
		String cibleNiveauSortieAf = target + "niveau_sorties_af" + ".json";
		
		try (FileInputStream inputStream = new FileInputStream(new File(source))) {
			explodeJson(inputStream, cibleOrganismes, cibleDomaines, cibleActions, cibleSessions, cibleSpecialites, cibleNiveauSortieAf);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static final void explodeJson(InputStream inputStream, String cibleOrganismes, String cibleDomaines, String cibleActions, String cibleSessions, String cibleSpecialites, String cibleNiveauSortiesAf) throws JSONException {
		Reader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);

		JSONArray newArrayOrganismes = new JSONArray();
		JSONArray newArrayDomaines = new JSONArray();
		JSONArray newArrayActions = new JSONArray();
		JSONArray newArraySessions = new JSONArray();
		JSONArray newArraySpecialites = new JSONArray();
		JSONArray newArrayNiveauSortiesAf = new JSONArray();

		JSONObject carif = new JSONObject(new JSONTokener(inputStreamReader));

		JSONArray organismes = carif.optJSONArray(ORGANISMES);
		if (organismes != null) {
			for (int i = 0; i < organismes.length(); i++) {
				JSONObject organisme = organismes.getJSONObject(i);
				
				String noSiret = organisme.getString(KEY_NO_SIRET);
				String noSite = organisme.getString(KEY_NO_SITE);
				String keyOrganisme = noSiret + noSite;

				JSONArray domaines = organisme.optJSONArray(DOMAINES);
				if (domaines != null) {
					for (int j = 0; j < domaines.length(); j++) {

						JSONObject domaine = domaines.getJSONObject(j);
						domaine.put(KEY_PARENT, keyOrganisme);
						newArrayDomaines.put(domaine);
					}
				}

				JSONArray actions = organisme.optJSONArray(ACTIONS);
				if (actions != null) {
					for (int j = 0; j < actions.length(); j++) {

						JSONObject action = actions.getJSONObject(j);
						
						String coActionFormation = action.getString(KEY_CO_ACTION_FORMATION);
						String keyAction = noSite + coActionFormation;

						JSONArray sessions = action.optJSONArray(SESSIONS);
						if (sessions != null) {
							for (int h = 0; h < sessions.length(); h++) {

								JSONObject session = sessions.getJSONObject(h);

								session.put(KEY_PARENT, keyAction);
								
								//We manage arrays to make them flat
								JSONArray array = session.optJSONArray(KEY_ARRAY_SESSION_CONTRAT_SF);
								StringBuffer buf = new StringBuffer();
								if (array != null) {
									for (int k = 0; k < array.length(); k++) {
										String value = array.getString(k);
										buf.append(k > 0 ? ";" + value : value);
									}
								}
								session.put(KEY_ARRAY_SESSION_CONTRAT_SF, buf.toString());
								
								array = session.optJSONArray(KEY_ARRAY_SESSION_FINANCEMENT_SF);
								buf = new StringBuffer();
								if (array != null) {
									for (int k = 0; k < array.length(); k++) {
										String value = array.getString(k);
										buf.append(k > 0 ? ";" + value : value);
									}
								}
								session.put(KEY_ARRAY_SESSION_FINANCEMENT_SF, buf.toString());
								
								array = session.optJSONArray(KEY_ARRAY_SESSION_MESURES_SF);
								buf = new StringBuffer();
								if (array != null) {
									for (int k = 0; k < array.length(); k++) {
										String value = array.getString(k);
										buf.append(k > 0 ? ";" + value : value);
									}
								}
								session.put(KEY_ARRAY_SESSION_MESURES_SF, buf.toString());
								
								array = session.optJSONArray(KEY_ARRAY_SESSION_PUBLIC_SF);
								buf = new StringBuffer();
								if (array != null) {
									for (int k = 0; k < array.length(); k++) {
										String value = array.getString(k);
										buf.append(k > 0 ? ";" + value : value);
									}
								}
								session.put(KEY_ARRAY_SESSION_PUBLIC_SF, buf.toString());
								
								array = session.optJSONArray(KEY_ARRAY_SESSION_TYPE_FORMATION_SF);
								buf = new StringBuffer();
								if (array != null) {
									for (int k = 0; k < array.length(); k++) {
										String value = array.getString(k);
										buf.append(k > 0 ? ";" + value : value);
									}
								}
								session.put(KEY_ARRAY_SESSION_TYPE_FORMATION_SF, buf.toString());
								
								newArraySessions.put(session);
							}
						}

						JSONArray niveauSorties = action.optJSONArray(NIVEAU_SORTIE_AF);
						if (niveauSorties != null) {
							for (int h = 0; h < niveauSorties.length(); h++) {

								JSONObject niveauSortie = niveauSorties.getJSONObject(h);

								niveauSortie.put(KEY_PARENT, keyAction);
								newArrayNiveauSortiesAf.put(niveauSortie);
							}
						}

						JSONArray specialites = action.optJSONArray(SPECIALITES);
						if (specialites != null) {
							for (int h = 0; h < specialites.length(); h++) {

								JSONObject specialite = specialites.getJSONObject(h);

								specialite.put(KEY_PARENT, keyAction);
								newArraySpecialites.put(specialite);
							}
						}
						
						action.put(KEY_PARENT, keyOrganisme);
						action.remove(SESSIONS);
						action.remove(NIVEAU_SORTIE_AF);
						action.remove(SPECIALITES);
						
						//We manage arrays to make them flat
						JSONArray niveauEntreeAf = action.optJSONArray(KEY_ARRAY_ACTIONS_NIVEAU_ENTREE_AF);
						StringBuffer buf = new StringBuffer();
						if (niveauEntreeAf != null) {
							for (int h = 0; h < niveauEntreeAf.length(); h++) {
								String value = niveauEntreeAf.getString(h);
								buf.append(h > 0 ? ";" + value : value);
							}
						}
						action.put(KEY_ARRAY_ACTIONS_NIVEAU_ENTREE_AF, buf.toString());
						
						newArrayActions.put(action);
					}
				}

				organisme.remove(ACTIONS);
				organisme.remove(DOMAINES);
				newArrayOrganismes.put(organisme);
			}
		}

		JSONObject newOrganismes = new JSONObject();
		newOrganismes.put(ORGANISMES, newArrayOrganismes);

		// Write JSON file
		try (Writer writer = new OutputStreamWriter(new FileOutputStream(cibleOrganismes), StandardCharsets.UTF_8)) {
			// We can write any JSONArray or JSONObject instance to the file
			writer.write(newOrganismes.toString(4));
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

		JSONObject newDomaines = new JSONObject();
		newDomaines.put(DOMAINES, newArrayDomaines);

		// Write JSON file
		try (Writer writer = new OutputStreamWriter(new FileOutputStream(cibleDomaines), StandardCharsets.UTF_8)) {
			// We can write any JSONArray or JSONObject instance to the file
			writer.write(newDomaines.toString(4));
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

		JSONObject newActions = new JSONObject();
		newActions.put(ACTIONS, newArrayActions);

		// Write JSON file
		try (Writer writer = new OutputStreamWriter(new FileOutputStream(cibleActions), StandardCharsets.UTF_8)) {
			// We can write any JSONArray or JSONObject instance to the file
			writer.write(newActions.toString(4));
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

		JSONObject newSessions = new JSONObject();
		newSessions.put(SESSIONS, newArraySessions);

		// Write JSON file
		try (Writer writer = new OutputStreamWriter(new FileOutputStream(cibleSessions), StandardCharsets.UTF_8)) {
			// We can write any JSONArray or JSONObject instance to the file
			writer.write(newSessions.toString(4));
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

		JSONObject newNiveauSortiesAf = new JSONObject();
		newNiveauSortiesAf.put(NIVEAU_SORTIE_AF, newArrayNiveauSortiesAf);

		// Write JSON file
		try (Writer writer = new OutputStreamWriter(new FileOutputStream(cibleNiveauSortiesAf), StandardCharsets.UTF_8)) {
			// We can write any JSONArray or JSONObject instance to the file
			writer.write(newNiveauSortiesAf.toString(4));
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

		JSONObject newSpecialites = new JSONObject();
		newSpecialites.put(SPECIALITES, newArraySpecialites);

		// Write JSON file
		try (Writer writer = new OutputStreamWriter(new FileOutputStream(cibleSpecialites), StandardCharsets.UTF_8)) {
			// We can write any JSONArray or JSONObject instance to the file
			writer.write(newSpecialites.toString(4));
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
