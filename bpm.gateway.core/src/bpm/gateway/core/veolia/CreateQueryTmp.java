package bpm.gateway.core.veolia;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreateQueryTmp {
	
	//ABONNES
//	private static final String FILE = "C:/BPM/Clients/VendeeEau/Query/VE_BPM_ABO_QueryExportContrat2.sql";
//	private static final String JAVA_CLASS = "GRC";
	//PATRIMOINE
	private static final String FILE = "C:/BPM/Clients/VendeeEau/Query/VE_BPM_PAT_QueryExportContrat2.sql";
	private static final String JAVA_CLASS = "Patrimoine";

	public static void main(String[] args) throws IOException {
//		extractTables();
		buildQueries();
	}

	private static void buildQueries() throws IOException {
		HashMap<String, String> replacements = buildReplacements();
		HashMap<String, String> ignores = buildIgnores();
		
		String content = new String(Files.readAllBytes(Paths.get(FILE)));

		//Abonnes
//		StringBuffer buf = new StringBuffer(JAVA_CLASS + " " + JAVA_CLASS.toLowerCase() + " = new " + JAVA_CLASS + "();\n\n");
		//Patrimoine
		StringBuffer buf = new StringBuffer("@SuppressWarnings(\"unchecked\")\n");
		buf.append("public Patrimoine buildPatrimoineWithParam(String contrat) {\n");
		buf.append(JAVA_CLASS + " " + JAVA_CLASS.toLowerCase() + " = new " + JAVA_CLASS + "();\n\n");
				
		String[] queries = content.split("-- table : ");
		for (int i = 1; i < queries.length; i++) {
			String query = queries[i];

			String[] queriesPart = query.split("-{3,}");

			String selectPart = queriesPart[0];
			String tableAlias = selectPart.substring(selectPart.indexOf(" "), selectPart.indexOf(".")).replace("\\r", "").replace("\\n", "").trim();
			String tableName = null;
			
//			Pattern pattern = Pattern.compile("FROM [\\w]+ ([\\w]+)");
			Pattern pattern = Pattern.compile("FROM (([\\w]+) [\\w]+)(?:, (([\\w]+) [\\w]+)){0,}");
			Matcher match = pattern.matcher(query);
			while (match.find()) {
				String group = match.group(0);
				if (group != null && !group.isEmpty()) {
					String[] elements = group.split(",");
					for (String element : elements) {
						String[] subElements = element.split(" ");
						String table = subElements[1];
						String alias = subElements[2];
						
						if (alias.equals(tableAlias)) {
							tableName = table;
							break;
						}
					}
				}
				
				if (tableName != null) {
					break;
				}
			}
			
			if (includeTable(tableName, ignores)) {
			
				buf.append("//-----------------------------------------------------//\n");
				buf.append("//" + tableName + "\n");
				buf.append("//-----------------------------------------------------//\n\n");
	
				selectPart = selectPart.replaceAll("SELECT([\\D|\\d]*[^\\t ])FROM", "SELECT " + tableAlias + ".*\n FROM");
	
				buf.append(i == 1 || tableName.equals("patrimoine_eqpt_socle") ? "StringBuffer buf = new StringBuffer();\n" : "buf = new StringBuffer();\n");
				String linesSelect[] = selectPart.split("\\r?\\n");
				for (int j=1; j<linesSelect.length; j++) {
					String line = linesSelect[j];
					if (!line.trim().isEmpty()) {
						buf.append("buf.append(\"" + buildLine(line) + " \");\n");
					}
				}
	
				String paramPart = queriesPart[1];
				paramPart = paramPart.replace("'05'", "'\" + secteur + \"'");
				paramPart = paramPart.replace("'VEOLIA'", "'\" + exploitant + \"'");
				paramPart = paramPart.replace("'%'", "'\" + contrat + \"'");
				
				String linesParam[] = paramPart.split("\\r?\\n");
				for (String line : linesParam) {
					if (!line.trim().isEmpty()) {
						buf.append("buf.append(\"" + buildLine(line) + " \");\n");
					}
				}
	
				String subqueryPart = queriesPart[2];
				String linesSubquery[] = subqueryPart.split("\\r?\\n");
				for (String line : linesSubquery) {
					if (!line.trim().isEmpty()) {
						buf.append("buf.append(\"" + buildLine(line) + " \");\n");
					}
				}
				
				String javaClass = replacements.get(tableName);
				//Abonnes
//				String setter = javaClass.replace("Type", "") + "s";
//				String list = setter.substring(0, 1).toLowerCase() + setter.substring(1, setter.length());
				//Patrimoine
				String setter = javaClass.replace("Type", "");
				String object = setter.substring(0, 1).toLowerCase() + setter.substring(1, setter.length());
				String list = object + "s";
	
				buf.append("\n");
				buf.append(i == 1 || tableName.equals("patrimoine_eqpt_socle") ? "Session session = getHibernateTemplate().getCurrentSession();\n" : "session = getHibernateTemplate().getCurrentSession();\n");
				buf.append("List<" + javaClass + "> " + list + " = session.createSQLQuery(buf.toString()).addEntity(" + javaClass + ".class).list();\n");
				buf.append("session.close();\n");
				buf.append("System.out.println(\"Insert \" + " + list + ".size() + \" lines of " + tableName + "\");\n\n");

				//Abonnes
//				buf.append(JAVA_CLASS.toLowerCase() + ".set" + setter + "(new " + setter + "(" + list + "));\n\n\n");
				//Patrimoine
				String adaptation = setter + "s";
				adaptation = adaptForPatrimoine(adaptation);
				
				buf.append(adaptation + " " + object + " = new " + adaptation + "();\n");
				buf.append(object + ".set" + setter + "(" + list + ");\n");
				buf.append(JAVA_CLASS.toLowerCase() + ".set" + adaptation + "(" + object + ");\n\n\n");
				
//				EquipementsSocle equipementSocle = new EquipementsSocle();
//				equipementSocle.setEquipementSocle(equipementSocles);
//				patrimoine.setEquipementsSocle(equipementSocle);
			}
		}
		
		buf.append("return " + JAVA_CLASS.toLowerCase() + ";\n");
		buf.append("}");
		System.out.println(buf.toString());
	}
	
	private static String adaptForPatrimoine(String item) {
		item = item.replace("Equipement", "Equipements");
		item = item.replace("Appareil", "Appareils");
		item = item.replace("Armoire", "Armoires");
		item = item.replace("Aspirateur", "Aspirateurs");
		item = item.replace("Bac", "Bacs");
		item = item.replace("Ballon", "Ballons");
		item = item.replace("Boite", "Boites");
		item = item.replace("Bouteille", "Bouteilles");
		item = item.replace("Canal", "Canaux");
		item = item.replace("Cellule", "Cellules");
		item = item.replace("Centrale", "Centrales");
		item = item.replace("Chariot", "Chariots");
		item = item.replace("Colonne", "Colonnes");
		item = item.replace("Convoyeur", "Convoyeurs");
		item = item.replace("Compteur", "Compteurs");
		item = item.replace("Demarreur", "Demarreurs");
		item = item.replace("Echangeur", "Echangeurs");
		item = item.replace("Electrode", "Electrodes");
		item = item.replace("Espace", "Espaces");
		item = item.replace("Extracteur", "Extracteurs");
		item = item.replace("Filtre", "Filtres");
		item = item.replace("Garde", "Gardes");
		item = item.replace("Generateur", "Generateurs");
		item = item.replace("Groupe", "Groupes");
		item = item.replace("Liaison", "Liaisons");
		item = item.replace("Masse", "Masses");
		item = item.replace("Materiel", "Materiels");
		item = item.replace("Melangeur", "Melangeurs");
		item = item.replace("Membrane", "Membranes");
		item = item.replace("Module", "Modules");
		item = item.replace("Pont", "Ponts");
		item = item.replace("Porte", "Portes");
		item = item.replace("Preleveur", "Preleveurs");
		item = item.replace("Presse", "Presses");
		item = item.replace("Protection", "Protections");
		item = item.replace("Retournement", "Retournements");
		item = item.replace("Secheur", "Secheurs");
		item = item.replace("Station", "Stations");
		item = item.replace("Transformateur", "Transformateurs");
		item = item.replace("Trappe", "Trappes");
		item = item.replace("Tube", "Tubes");
		item = item.replace("Turbine", "Turbines");
		item = item.replace("Vanne", "Vannes");
		item = item.replace("Variateur", "Variateurs");
		
		//inverse
		item = item.replace("Socles", "Socle");
		item = item.replace("Levages", "Levage");
		item = item.replace("Bouess", "Boues");
		item = item.replace("Retentions", "Retention");
		item = item.replace("Regulations", "Regulation");
		item = item.replace("Boues", "Boue");
		item = item.replace("Chlores", "Chlore");
		item = item.replace("Caillebotiss", "Caillebotis");
		item = item.replace("CanauxComptages", "CanauxComptage");
		item = item.replace("PosteHTs", "PosteHT");
		item = item.replace("Forages", "Forage");
		item = item.replace("Ozones", "Ozone");
		item = item.replace("Chaleurs", "Chaleur");
		item = item.replace("References", "Reference");
		item = item.replace("Verts", "Vert");
		item = item.replace("Airs", "Air");
		item = item.replace("Corpss", "Corps");
		item = item.replace("UVs", "UV");
		item = item.replace("Dosages", "Dosage");
		item = item.replace("Refroidissements", "Refroidissement");
		item = item.replace("Laboratoires", "Laboratoire");
		item = item.replace("UFs", "UF");
		item = item.replace("Portess", "Portes");
		item = item.replace("Incendies", "Incendie");
		item = item.replace("Terress", "Terres");
		item = item.replace("Teletransmissions", "Teletransmission");
		item = item.replace("HTs", "HT");
		item = item.replace("Access", "Acces");
		item = item.replace("Vanness", "Vannes");
		item = item.replace("Vitesses", "Vitesse");
		item = item.replace("Transferts", "Transfert");
		
		return item;
	}

	private static boolean includeTable(String selectedTableName, HashMap<String, String> ignores) {
		for (String tableName : ignores.values()) {
			if (selectedTableName.equals(tableName)) {
				return false;
			}
		}
		return true;
	}

	private static String buildLine(String line) {
//		Pattern pattern = Pattern.compile("(_([\\w]))");
//		Matcher match = pattern.matcher(line);
//		while (match.find()) {
//
//			String param = match.group(1);
//			String letter = match.group(2);
//			line = line.replaceAll(param, letter.toUpperCase());
//		}

		return line;
	}
	
	public static HashMap<String, String> buildReplacements() {
		HashMap<String, String> replacements = new HashMap<>();
		replacements.put("abonnes_contrataep", "TypeContratAep");
		replacements.put("abonnes_contratabt", "TypeContratAbt");
		replacements.put("abonnes_contratac", "TypeContratAc");
		replacements.put("abonnes_abonne", "TypeAbonne");
		replacements.put("abonnes_pointfourniture", "TypePointFourniture");
		replacements.put("abonnes_branchement", "TypeBranchement");
		replacements.put("abonnes_compteur", "TypeCompteur");
		replacements.put("abonnes_releve", "TypeReleve");
		replacements.put("abonnes_facture", "TypeFacture");
		replacements.put("abonnes_lignefacture", "TypeLigneFacture");
		replacements.put("abonnes_intervention", "TypeIntervention");
		
		replacements.put("patrimoine_contrat", "Contrat");
		replacements.put("patrimoine_site", "Site");
		replacements.put("patrimoine_ouvrage_socle", "OuvrageSocle");
		replacements.put("patrimoine_ouvrage_bar", "OuvrageBar");
		replacements.put("patrimoine_ouvrage_bas", "OuvrageBas");
		replacements.put("patrimoine_ouvrage_cap", "OuvrageCap");
		replacements.put("patrimoine_ouvrage_car", "OuvrageCar");
		replacements.put("patrimoine_ouvrage_chl", "OuvrageChl");
		replacements.put("patrimoine_ouvrage_cpt", "OuvrageCpt");
		replacements.put("patrimoine_ouvrage_lag", "OuvrageLag");
		replacements.put("patrimoine_ouvrage_log", "OuvrageLog");
		replacements.put("patrimoine_ouvrage_pba", "OuvragePba");
		replacements.put("patrimoine_ouvrage_pca", "OuvragePca");
		replacements.put("patrimoine_ouvrage_pre", "OuvragePre");
		replacements.put("patrimoine_ouvrage_rst", "OuvrageRst");
		replacements.put("patrimoine_ouvrage_stp", "OuvrageStp");
		replacements.put("patrimoine_ouvrage_usi", "OuvrageUsi");
		replacements.put("patrimoine_unite_socle", "UniteSocle");
		replacements.put("patrimoine_unite_car", "UniteCar");
		replacements.put("patrimoine_unite_chl", "UniteChl");
		replacements.put("patrimoine_unite_cla", "UniteCla");
		replacements.put("patrimoine_unite_cpt", "UniteCpt");
		replacements.put("patrimoine_unite_ele", "UniteEle");
		replacements.put("patrimoine_unite_esv", "UniteEsv");
		replacements.put("patrimoine_unite_fil", "UniteFil");
		replacements.put("patrimoine_unite_gci", "UniteGci");
		replacements.put("patrimoine_unite_hyd", "UniteHyd");
		replacements.put("patrimoine_unite_min", "UniteMin");
		replacements.put("patrimoine_unite_oxy", "UniteOxy");
		replacements.put("patrimoine_unite_pom", "UnitePom");
		replacements.put("patrimoine_unite_rec", "UniteRec");
		replacements.put("patrimoine_unite_reg", "UniteReg");
		replacements.put("patrimoine_unite_sec", "UniteSec");
		replacements.put("patrimoine_unite_set", "UniteSet");
		replacements.put("patrimoine_unite_spe", "UniteSpe");
		replacements.put("patrimoine_unite_tlg", "UniteTlg");
		replacements.put("patrimoine_unite_trt", "UniteTrt");
		replacements.put("patrimoine_unite_ult", "UniteUlt");
		replacements.put("patrimoine_eqpt_socle", "TypeEquipementSocle");
		replacements.put("patrimoine_eqpt_aerotherme", "TypeEquipementAerotherme");
		replacements.put("patrimoine_eqpt_agitateur", "TypeEquipementAgitateur");
		replacements.put("patrimoine_eqpt_applevage", "TypeEquipementAppareilLevage");
		replacements.put("patrimoine_eqpt_armoireelec", "TypeEquipementArmoireElectrique");
		replacements.put("patrimoine_eqpt_aspiboue", "TypeEquipementAspirateurBoues");
		replacements.put("patrimoine_eqpt_automate", "TypeEquipementAutomate");
		replacements.put("patrimoine_eqpt_bacretention", "TypeEquipementBacRetention");
		replacements.put("patrimoine_eqpt_ballonregulation", "TypeEquipementBallonRegulation");
		replacements.put("patrimoine_eqpt_benne", "TypeEquipementBenne");
		replacements.put("patrimoine_eqpt_boiteboue", "TypeEquipementBoiteBoue");
		replacements.put("patrimoine_eqpt_bouteillechlore", "TypeEquipementBouteilleChlore");
		replacements.put("patrimoine_eqpt_buselure", "TypeEquipementBuselure");
		replacements.put("patrimoine_eqpt_caillebotis", "TypeEquipementCaillebotis");
		replacements.put("patrimoine_eqpt_canalcomptage", "TypeEquipementCanalComptage");
		replacements.put("patrimoine_eqpt_capteur", "TypeEquipementCapteur");
		replacements.put("patrimoine_eqpt_celluleposteht", "TypeEquipementCellulePosteHT");
		replacements.put("patrimoine_eqpt_centralehydrau", "TypeEquipementCentraleHydraulique");
		replacements.put("patrimoine_eqpt_centrifugeuse", "TypeEquipementCentrifugeuse");
		replacements.put("patrimoine_eqpt_chariot", "TypeEquipementChariotElevateur");
		replacements.put("patrimoine_eqpt_chlorometre", "TypeEquipementChlorometre");
		replacements.put("patrimoine_eqpt_clapet", "TypeEquipementClapet");
		replacements.put("patrimoine_eqpt_cloture", "TypeEquipementCloture");
		replacements.put("patrimoine_eqpt_collecteur", "TypeEquipementCollecteur");
		replacements.put("patrimoine_eqpt_colonneforage", "TypeEquipementColonneForage");
		replacements.put("patrimoine_eqpt_colonnemontante", "TypeEquipementColonneMontante");
		replacements.put("patrimoine_eqpt_comptage", "TypeEquipementComptage");
		replacements.put("patrimoine_eqpt_condensateur", "TypeEquipementCondensateur");
		replacements.put("patrimoine_eqpt_convtransp", "TypeEquipementConvoyeurTransporteur");
		replacements.put("patrimoine_eqpt_cptelec", "TypeEquipementCompteurElectrique");
		replacements.put("patrimoine_eqpt_crepine", "TypeEquipementCrepine");
		replacements.put("patrimoine_eqpt_demarreurelec", "TypeEquipementDemarreurElectrique");
		replacements.put("patrimoine_eqpt_deshumidificateur", "TypeEquipementDeshumidificateur");
		replacements.put("patrimoine_eqpt_destozone", "TypeEquipementDestructeurOzone");
		replacements.put("patrimoine_eqpt_disconnecteur", "TypeEquipementDisconnecteur");
		replacements.put("patrimoine_eqpt_echangechaleur", "TypeEquipementEchangeurChaleur");
		replacements.put("patrimoine_eqpt_echelle", "TypeEquipementEchelle");
		replacements.put("patrimoine_eqpt_elecref", "TypeEquipementElectrodeReference");
		replacements.put("patrimoine_eqpt_enregistreur", "TypeEquipementEnregistreur");
		replacements.put("patrimoine_eqpt_espacevert", "TypeEquipementEspaceVert");
		replacements.put("patrimoine_eqpt_extractair", "TypeEquipementExtracteurAir");
		replacements.put("patrimoine_eqpt_fenetre", "TypeEquipementFenetre");
		replacements.put("patrimoine_eqpt_filtreferme", "TypeEquipementFiltreFerme");
		replacements.put("patrimoine_eqpt_gardecorps", "TypeEquipementGardeCorps");
		replacements.put("patrimoine_eqpt_generateuruv", "TypeEquipementGenerateurUV");
		replacements.put("patrimoine_eqpt_grappin", "TypeEquipementGrappin");
		replacements.put("patrimoine_eqpt_groupeair", "TypeEquipementGroupeAir");
		replacements.put("patrimoine_eqpt_groupedosage", "TypeEquipementGroupeDosage");
		replacements.put("patrimoine_eqpt_groupeelec", "TypeEquipementGroupeElectrogene");
		replacements.put("patrimoine_eqpt_groupemalaxeur", "TypeEquipementGroupeMalaxeur");
		replacements.put("patrimoine_eqpt_grouperefroi", "TypeEquipementGroupeRefroidissement");
		replacements.put("patrimoine_eqpt_herse", "TypeEquipementHerse");
		replacements.put("patrimoine_eqpt_hydrocyclone", "TypeEquipementHydrocyclone");
		replacements.put("patrimoine_eqpt_hydroejecteur", "TypeEquipementHydroejecteur");
		replacements.put("patrimoine_eqpt_liaisonele", "TypeEquipementLiaisonElectrique");
		replacements.put("patrimoine_eqpt_massanodique", "TypeEquipementMasseAnodique");
		replacements.put("patrimoine_eqpt_materiellabo", "TypeEquipementMaterielLaboratoire");
		replacements.put("patrimoine_eqpt_melangeurstatique", "TypeEquipementMelangeurStatique");
		replacements.put("patrimoine_eqpt_membraneuf", "TypeEquipementMembraneUF");
		replacements.put("patrimoine_eqpt_modulelamellaire", "TypeEquipementModuleLamellaire");
		replacements.put("patrimoine_eqpt_onduleur", "TypeEquipementOnduleur");
		replacements.put("patrimoine_eqpt_ozoneur", "TypeEquipementOzoneur");
		replacements.put("patrimoine_eqpt_pompe", "TypeEquipementPompe");
		replacements.put("patrimoine_eqpt_pontracleur", "TypeEquipementPontRacleur");
		replacements.put("patrimoine_eqpt_portail", "TypeEquipementPortail");
		replacements.put("patrimoine_eqpt_porte", "TypeEquipementPorte");
		replacements.put("patrimoine_eqpt_portesectionnelle", "TypeEquipementPorteSectionnelle");
		replacements.put("patrimoine_eqpt_portillon", "TypeEquipementPortillon");
		replacements.put("patrimoine_eqpt_prefiltres", "TypeEquipementPrefiltre");
		replacements.put("patrimoine_eqpt_preleveurechantillon", "TypeEquipementPreleveurEchantillonneur");
		replacements.put("patrimoine_eqpt_presseboue", "TypeEquipementPresseBoue");
		replacements.put("patrimoine_eqpt_protecincendie", "TypeEquipementProtectionIncendie");
		replacements.put("patrimoine_eqpt_retourneterre", "TypeEquipementRetournementTerres");
		replacements.put("patrimoine_eqpt_secheurair", "TypeEquipementSecheurAir");
		replacements.put("patrimoine_eqpt_silo", "TypeEquipementSilo");
		replacements.put("patrimoine_eqpt_soupape", "TypeEquipementSoupape");
		replacements.put("patrimoine_eqpt_stationteletr", "TypeEquipementStationTeletransmission");
		replacements.put("patrimoine_eqpt_transfoht", "TypeEquipementTransformateurHT");
		replacements.put("patrimoine_eqpt_transformredress", "TypeEquipementTransformateurRedresseur");
		replacements.put("patrimoine_eqpt_trappeacces", "TypeEquipementTrappeAcces");
		replacements.put("patrimoine_eqpt_tubecrepine", "TypeEquipementTubeCrepine");
		replacements.put("patrimoine_eqpt_turbinehydroelec", "TypeEquipementTurbineHydroelectrique");
		replacements.put("patrimoine_eqpt_tuyauterie", "TypeEquipementTuyauterie");
		replacements.put("patrimoine_eqpt_vanne", "TypeEquipementVanne");
		replacements.put("patrimoine_eqpt_vanneregulation", "TypeEquipementVanneRegulation");
		replacements.put("patrimoine_eqpt_variateurvitesse", "TypeEquipementVariateurVitesse");
		replacements.put("patrimoine_eqpt_verrin", "TypeEquipementVerin");
		replacements.put("patrimoine_eqpt_vistransfert", "TypeEquipementVisTransfert");
		replacements.put("patrimoine_eqpt_voirie", "TypeEquipementVoirie");
		replacements.put("patrimoine_intervention", "TypeIntervention");

		return replacements;
	}
	
	public static HashMap<String, String> buildIgnores() {
		HashMap<String, String> ignores = new HashMap<>();
		ignores.put("Contrat", "patrimoine_contrat");
		ignores.put("OuvrageBar", "patrimoine_ouvrage_bar");
		ignores.put("OuvrageBas", "patrimoine_ouvrage_bas");
		ignores.put("OuvrageCap", "patrimoine_ouvrage_cap");
		ignores.put("OuvrageCar", "patrimoine_ouvrage_car");
		ignores.put("OuvrageChl", "patrimoine_ouvrage_chl");
		ignores.put("OuvrageCpt", "patrimoine_ouvrage_cpt");
		ignores.put("OuvrageLag", "patrimoine_ouvrage_lag");
		ignores.put("OuvrageLog", "patrimoine_ouvrage_log");
		ignores.put("OuvragePba", "patrimoine_ouvrage_pba");
		ignores.put("OuvragePca", "patrimoine_ouvrage_pca");
		ignores.put("OuvragePre", "patrimoine_ouvrage_pre");
		ignores.put("OuvrageRst", "patrimoine_ouvrage_rst");
		ignores.put("OuvrageSocle", "patrimoine_ouvrage_socle");
		ignores.put("OuvrageStp", "patrimoine_ouvrage_stp");
		ignores.put("OuvrageUsi", "patrimoine_ouvrage_usi");
		ignores.put("Site", "patrimoine_site");
		ignores.put("UniteCar", "patrimoine_unite_car");
		ignores.put("UniteChl", "patrimoine_unite_chl");
		ignores.put("UniteCla", "patrimoine_unite_cla");
		ignores.put("UniteCpt", "patrimoine_unite_cpt");
		ignores.put("UniteEle", "patrimoine_unite_ele");
		ignores.put("UniteEsv", "patrimoine_unite_esv");
		ignores.put("UniteFil", "patrimoine_unite_fil");
		ignores.put("UniteGci", "patrimoine_unite_gci");
		ignores.put("UniteHyd", "patrimoine_unite_hyd");
		ignores.put("UniteMin", "patrimoine_unite_min");
		ignores.put("UniteOxy", "patrimoine_unite_oxy");
		ignores.put("UnitePom", "patrimoine_unite_pom");
		ignores.put("UniteRec", "patrimoine_unite_rec");
		ignores.put("UniteReg", "patrimoine_unite_reg");
		ignores.put("UniteSec", "patrimoine_unite_sec");
		ignores.put("UniteSet", "patrimoine_unite_set");
		ignores.put("UniteSocle", "patrimoine_unite_socle");
		ignores.put("UniteSpe", "patrimoine_unite_spe");
		ignores.put("UniteTlg", "patrimoine_unite_tlg");
		ignores.put("UniteTrt", "patrimoine_unite_trt");
		ignores.put("UniteUlt", "patrimoine_unite_ult");
		return ignores;
	}

	private static void extractTables() throws IOException {
		String content = new String(Files.readAllBytes(Paths.get(FILE)));

		List<String> tables = new ArrayList<>();

		Pattern pattern = Pattern.compile("FROM (([\\w]+) [\\w]+)(?:, (([\\w]+) [\\w]+)){0,}");
		Matcher match = pattern.matcher(content);
		while (match.find()) {
			String group = match.group(0);
			if (group != null && !group.isEmpty()) {
				String[] elements = group.split(",");
				for (String element : elements) {
					String[] subElements = element.split(" ");
					String tableName = subElements[1];

					boolean found = false;
					for (String table : tables) {
						if (table.equals(tableName)) {
							found = true;
							break;
						}
					}

					if (!found) {
						tables.add(tableName);
					}
				}
			}
		}

		StringBuffer buf = new StringBuffer("HashMap<String, String> replacements = new HashMap<>();\n");
		for (String table : tables) {
			buf.append("replacements.put(\"" + table + "\", \"\");\n");
		}
		buf.append("return replacements;");

		System.out.println(buf.toString());
	}
}
