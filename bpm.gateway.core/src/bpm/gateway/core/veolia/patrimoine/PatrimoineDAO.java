package bpm.gateway.core.veolia.patrimoine;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;

import bpm.gateway.core.veolia.DateChargement;
import bpm.gateway.core.veolia.LogInsertXML;
import bpm.gateway.core.veolia.LogXML;
import bpm.gateway.core.veolia.ReflectionHelper;
import bpm.gateway.core.veolia.patrimoine.Patrimoine.EquipementsAerothermes;
import bpm.gateway.core.veolia.patrimoine.Patrimoine.EquipementsAgitateurs;
import bpm.gateway.core.veolia.patrimoine.Patrimoine.EquipementsAppareilsLevage;
import bpm.gateway.core.veolia.patrimoine.Patrimoine.EquipementsArmoiresElectriques;
import bpm.gateway.core.veolia.patrimoine.Patrimoine.EquipementsAspirateursBoues;
import bpm.gateway.core.veolia.patrimoine.Patrimoine.EquipementsAutomates;
import bpm.gateway.core.veolia.patrimoine.Patrimoine.EquipementsBacsRetention;
import bpm.gateway.core.veolia.patrimoine.Patrimoine.EquipementsBallons;
import bpm.gateway.core.veolia.patrimoine.Patrimoine.EquipementsBennes;
import bpm.gateway.core.veolia.patrimoine.Patrimoine.EquipementsBoitesBoue;
import bpm.gateway.core.veolia.patrimoine.Patrimoine.EquipementsBouteillesChlore;
import bpm.gateway.core.veolia.patrimoine.Patrimoine.EquipementsBuselures;
import bpm.gateway.core.veolia.patrimoine.Patrimoine.EquipementsCaillebotis;
import bpm.gateway.core.veolia.patrimoine.Patrimoine.EquipementsCanauxComptage;
import bpm.gateway.core.veolia.patrimoine.Patrimoine.EquipementsCapteurs;
import bpm.gateway.core.veolia.patrimoine.Patrimoine.EquipementsCellulesPosteHT;
import bpm.gateway.core.veolia.patrimoine.Patrimoine.EquipementsCentralesHydrauliques;
import bpm.gateway.core.veolia.patrimoine.Patrimoine.EquipementsCentrifugeuses;
import bpm.gateway.core.veolia.patrimoine.Patrimoine.EquipementsChariotsElevateurs;
import bpm.gateway.core.veolia.patrimoine.Patrimoine.EquipementsChlorometres;
import bpm.gateway.core.veolia.patrimoine.Patrimoine.EquipementsClapets;
import bpm.gateway.core.veolia.patrimoine.Patrimoine.EquipementsClotures;
import bpm.gateway.core.veolia.patrimoine.Patrimoine.EquipementsCollecteurs;
import bpm.gateway.core.veolia.patrimoine.Patrimoine.EquipementsColonnesForage;
import bpm.gateway.core.veolia.patrimoine.Patrimoine.EquipementsColonnesMontantes;
import bpm.gateway.core.veolia.patrimoine.Patrimoine.EquipementsComptages;
import bpm.gateway.core.veolia.patrimoine.Patrimoine.EquipementsCompteursElectriques;
import bpm.gateway.core.veolia.patrimoine.Patrimoine.EquipementsCondensateurs;
import bpm.gateway.core.veolia.patrimoine.Patrimoine.EquipementsConvoyeursTransporteurs;
import bpm.gateway.core.veolia.patrimoine.Patrimoine.EquipementsCrepines;
import bpm.gateway.core.veolia.patrimoine.Patrimoine.EquipementsDemarreursElectriques;
import bpm.gateway.core.veolia.patrimoine.Patrimoine.EquipementsDeshumidificateurs;
import bpm.gateway.core.veolia.patrimoine.Patrimoine.EquipementsDestructeurOzone;
import bpm.gateway.core.veolia.patrimoine.Patrimoine.EquipementsDisconnecteurs;
import bpm.gateway.core.veolia.patrimoine.Patrimoine.EquipementsEchangeursChaleur;
import bpm.gateway.core.veolia.patrimoine.Patrimoine.EquipementsEchelles;
import bpm.gateway.core.veolia.patrimoine.Patrimoine.EquipementsElectrodesReference;
import bpm.gateway.core.veolia.patrimoine.Patrimoine.EquipementsEnregistreurs;
import bpm.gateway.core.veolia.patrimoine.Patrimoine.EquipementsEspacesVert;
import bpm.gateway.core.veolia.patrimoine.Patrimoine.EquipementsExtracteursAir;
import bpm.gateway.core.veolia.patrimoine.Patrimoine.EquipementsFenetres;
import bpm.gateway.core.veolia.patrimoine.Patrimoine.EquipementsFiltresFermes;
import bpm.gateway.core.veolia.patrimoine.Patrimoine.EquipementsGardesCorps;
import bpm.gateway.core.veolia.patrimoine.Patrimoine.EquipementsGenerateursUV;
import bpm.gateway.core.veolia.patrimoine.Patrimoine.EquipementsGrappins;
import bpm.gateway.core.veolia.patrimoine.Patrimoine.EquipementsGroupesAir;
import bpm.gateway.core.veolia.patrimoine.Patrimoine.EquipementsGroupesDosage;
import bpm.gateway.core.veolia.patrimoine.Patrimoine.EquipementsGroupesElectrogenes;
import bpm.gateway.core.veolia.patrimoine.Patrimoine.EquipementsGroupesMalaxeurs;
import bpm.gateway.core.veolia.patrimoine.Patrimoine.EquipementsGroupesRefroidissement;
import bpm.gateway.core.veolia.patrimoine.Patrimoine.EquipementsHerses;
import bpm.gateway.core.veolia.patrimoine.Patrimoine.EquipementsHydrocyclones;
import bpm.gateway.core.veolia.patrimoine.Patrimoine.EquipementsHydroejecteurs;
import bpm.gateway.core.veolia.patrimoine.Patrimoine.EquipementsLiaisonsElectriques;
import bpm.gateway.core.veolia.patrimoine.Patrimoine.EquipementsMassesAnodiques;
import bpm.gateway.core.veolia.patrimoine.Patrimoine.EquipementsMaterielsLaboratoire;
import bpm.gateway.core.veolia.patrimoine.Patrimoine.EquipementsMelangeursStatiques;
import bpm.gateway.core.veolia.patrimoine.Patrimoine.EquipementsMembranesUF;
import bpm.gateway.core.veolia.patrimoine.Patrimoine.EquipementsModulesLamellaires;
import bpm.gateway.core.veolia.patrimoine.Patrimoine.EquipementsOnduleurs;
import bpm.gateway.core.veolia.patrimoine.Patrimoine.EquipementsOzoneurs;
import bpm.gateway.core.veolia.patrimoine.Patrimoine.EquipementsPompes;
import bpm.gateway.core.veolia.patrimoine.Patrimoine.EquipementsPontsRacleurs;
import bpm.gateway.core.veolia.patrimoine.Patrimoine.EquipementsPortails;
import bpm.gateway.core.veolia.patrimoine.Patrimoine.EquipementsPortes;
import bpm.gateway.core.veolia.patrimoine.Patrimoine.EquipementsPortesSectionnelles;
import bpm.gateway.core.veolia.patrimoine.Patrimoine.EquipementsPortillons;
import bpm.gateway.core.veolia.patrimoine.Patrimoine.EquipementsPrefiltres;
import bpm.gateway.core.veolia.patrimoine.Patrimoine.EquipementsPreleveursEchantillonneurs;
import bpm.gateway.core.veolia.patrimoine.Patrimoine.EquipementsPressesBoue;
import bpm.gateway.core.veolia.patrimoine.Patrimoine.EquipementsProtectionsIncendie;
import bpm.gateway.core.veolia.patrimoine.Patrimoine.EquipementsRetournementsTerres;
import bpm.gateway.core.veolia.patrimoine.Patrimoine.EquipementsSecheursAir;
import bpm.gateway.core.veolia.patrimoine.Patrimoine.EquipementsSilos;
import bpm.gateway.core.veolia.patrimoine.Patrimoine.EquipementsSocle;
import bpm.gateway.core.veolia.patrimoine.Patrimoine.EquipementsSoupapes;
import bpm.gateway.core.veolia.patrimoine.Patrimoine.EquipementsStationsTeletransmission;
import bpm.gateway.core.veolia.patrimoine.Patrimoine.EquipementsTransformateursHT;
import bpm.gateway.core.veolia.patrimoine.Patrimoine.EquipementsTransformateursRedresseurs;
import bpm.gateway.core.veolia.patrimoine.Patrimoine.EquipementsTrappesAcces;
import bpm.gateway.core.veolia.patrimoine.Patrimoine.EquipementsTubesCrepine;
import bpm.gateway.core.veolia.patrimoine.Patrimoine.EquipementsTurbinesHydroelectriques;
import bpm.gateway.core.veolia.patrimoine.Patrimoine.EquipementsTuyauteries;
import bpm.gateway.core.veolia.patrimoine.Patrimoine.EquipementsVannes;
import bpm.gateway.core.veolia.patrimoine.Patrimoine.EquipementsVannesRegulation;
import bpm.gateway.core.veolia.patrimoine.Patrimoine.EquipementsVariateursVitesse;
import bpm.gateway.core.veolia.patrimoine.Patrimoine.EquipementsVerins;
import bpm.gateway.core.veolia.patrimoine.Patrimoine.EquipementsVisTransfert;
import bpm.gateway.core.veolia.patrimoine.Patrimoine.EquipementsVoiries;
import bpm.gateway.core.veolia.patrimoine.Patrimoine.Interventions;
import bpm.gateway.core.veolia.patrimoine.xls.Contrat;
import bpm.gateway.core.veolia.patrimoine.xls.OuvrageBar;
import bpm.gateway.core.veolia.patrimoine.xls.OuvrageBas;
import bpm.gateway.core.veolia.patrimoine.xls.OuvrageCap;
import bpm.gateway.core.veolia.patrimoine.xls.OuvrageCar;
import bpm.gateway.core.veolia.patrimoine.xls.OuvrageCpt;
import bpm.gateway.core.veolia.patrimoine.xls.OuvrageLag;
import bpm.gateway.core.veolia.patrimoine.xls.OuvrageLog;
import bpm.gateway.core.veolia.patrimoine.xls.OuvragePba;
import bpm.gateway.core.veolia.patrimoine.xls.OuvragePca;
import bpm.gateway.core.veolia.patrimoine.xls.OuvragePre;
import bpm.gateway.core.veolia.patrimoine.xls.OuvrageRst;
import bpm.gateway.core.veolia.patrimoine.xls.OuvrageSocle;
import bpm.gateway.core.veolia.patrimoine.xls.OuvrageStp;
import bpm.gateway.core.veolia.patrimoine.xls.OuvrageUsi;
import bpm.gateway.core.veolia.patrimoine.xls.PatrimoineXls;
import bpm.gateway.core.veolia.patrimoine.xls.Site;
import bpm.gateway.core.veolia.patrimoine.xls.UniteCar;
import bpm.gateway.core.veolia.patrimoine.xls.UniteChl;
import bpm.gateway.core.veolia.patrimoine.xls.UniteCla;
import bpm.gateway.core.veolia.patrimoine.xls.UniteCpt;
import bpm.gateway.core.veolia.patrimoine.xls.UniteEle;
import bpm.gateway.core.veolia.patrimoine.xls.UniteEsv;
import bpm.gateway.core.veolia.patrimoine.xls.UniteFil;
import bpm.gateway.core.veolia.patrimoine.xls.UniteGci;
import bpm.gateway.core.veolia.patrimoine.xls.UniteHyd;
import bpm.gateway.core.veolia.patrimoine.xls.UniteMin;
import bpm.gateway.core.veolia.patrimoine.xls.UniteOxy;
import bpm.gateway.core.veolia.patrimoine.xls.UnitePom;
import bpm.gateway.core.veolia.patrimoine.xls.UniteRec;
import bpm.gateway.core.veolia.patrimoine.xls.UniteReg;
import bpm.gateway.core.veolia.patrimoine.xls.UniteSec;
import bpm.gateway.core.veolia.patrimoine.xls.UniteSet;
import bpm.gateway.core.veolia.patrimoine.xls.UniteSocle;
import bpm.gateway.core.veolia.patrimoine.xls.UniteSpe;
import bpm.gateway.core.veolia.patrimoine.xls.UniteTlg;
import bpm.gateway.core.veolia.patrimoine.xls.UniteTrt;
import bpm.gateway.core.veolia.patrimoine.xls.UniteUlt;
import bpm.vanilla.platform.core.beans.resources.ClassDefinition;
import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class PatrimoineDAO extends HibernateDaoSupport {

	public static final String PAT_EQPT_SOCLE = "patrimoine_eqpt_socle";
	public static final String PAT_EQPT_AGITATEUR = "patrimoine_eqpt_agitateur";
	public static final String PAT_EQPT_BACRETENTION = "patrimoine_eqpt_bacretention";
	public static final String PAT_EQPT_CLAPET = "patrimoine_eqpt_clapet";
	public static final String PAT_EQPT_COMPTAGE = "patrimoine_eqpt_comptage";
	public static final String PAT_EQPT_POMPE = "patrimoine_eqpt_pompe";
	public static final String PAT_EQPT_PORTAIL = "patrimoine_eqpt_portail";
	public static final String PAT_EQPT_SILO = "patrimoine_eqpt_silo";
	public static final String PAT_EQPT_VANNE = "patrimoine_eqpt_vanne";

	public static final String PAT_COMPOSANT = "patrimoine_composant";
	public static final String PAT_EQPT_GROUPEELECTROGENE = "patrimoine_eqpt_groupeelec";
	public static final String PAT_EQPT_GROUPEDOSAGE = "patrimoine_eqpt_groupedosage";
	public static final String PAT_EQPT_BALLONREGULATION = "patrimoine_eqpt_ballonregulation";
	public static final String PAT_EQPT_VANNEREGULATION = "patrimoine_eqpt_vanneregulation";
	public static final String PAT_EQPT_GENERATEURUV = "patrimoine_eqpt_generateuruv";
	public static final String PAT_EQPT_TRANSFORMATEURHT = "patrimoine_eqpt_transfoht";
	public static final String PAT_EQPT_APPAREILLEVAGE = "patrimoine_eqpt_applevage";
	public static final String PAT_EQPT_TURBINEHYDROELECTRIQUE = "patrimoine_eqpt_turbinehydroelec";
	public static final String PAT_EQPT_GROUPEAIR = "patrimoine_eqpt_groupeair";
	public static final String PAT_EQPT_OZONEUR = "patrimoine_eqpt_ozoneur";
	public static final String PAT_EQPT_COLONNEFORAGE = "patrimoine_eqpt_colonneforage";
	public static final String PAT_EQPT_VARIATEURVITESSE = "patrimoine_eqpt_variateurvitesse";
	public static final String PAT_EQPT_PONTRACLEUR = "patrimoine_eqpt_pontracleur";
	public static final String PAT_EQPT_CHLOROMETRE = "patrimoine_eqpt_chlorometre";
	public static final String PAT_EQPT_TUYAUTERIE = "patrimoine_eqpt_tuyauterie";
	public static final String PAT_EQPT_PORTESECTIONNELLE = "patrimoine_eqpt_portesectionnelle";
	public static final String PAT_EQPT_CLOTURE = "patrimoine_eqpt_cloture";
	public static final String PAT_EQPT_PRESSEBOUE = "patrimoine_eqpt_presseboue";
	public static final String PAT_EQPT_STATIONTELETRANSMISSION = "patrimoine_eqpt_stationteletr";
	public static final String PAT_EQPT_CENTRALEHYDRAULIQUE = "patrimoine_eqpt_centralehydrau";
	public static final String PAT_EQPT_CAPTEUR = "patrimoine_eqpt_capteur";
	public static final String PAT_EQPT_ANTIINTRUSION = "patrimoine_eqpt_eqptantiint";
	public static final String PAT_EQPT_CHARIOTELEVATEUR = "patrimoine_eqpt_chariot";
	public static final String PAT_EQPT_TRAPPEACCES = "patrimoine_eqpt_trappeacces";
	public static final String PAT_EQPT_FILTREFERME = "patrimoine_eqpt_filtreferme";
	public static final String PAT_EQPT_FILTRE_Y = "patrimoine_eqpt_filtre_y";
	public static final String PAT_EQPT_AUTOMATE = "patrimoine_eqpt_automate";
	public static final String PAT_EQPT_HYDROCYCLONE = "patrimoine_eqpt_hydrocyclone";
	public static final String PAT_EQPT_VOIRIE = "patrimoine_eqpt_voirie";
	public static final String PAT_EQPT_INTERVENTION = "patrimoine_intervention";
	public static final String PAT_EQPT_CONVOYEURTRANSPORTEUR = "patrimoine_eqpt_convtransp";
	public static final String PAT_EQPT_GROUPEREFROIDISSEMENT = "patrimoine_eqpt_grouperefroi";
	public static final String PAT_EQPT_HERSE = "patrimoine_eqpt_herse";
	public static final String PAT_EQPT_ARMOIREELECTRIQUE = "patrimoine_eqpt_armoireelec";
	public static final String PAT_EQPT_PORTILLON = "patrimoine_eqpt_portillon";
	public static final String PAT_EQPT_CENTRIFUGEUSE = "patrimoine_eqpt_centrifugeuse";
	public static final String PAT_EQPT_BOITEBOUE = "patrimoine_eqpt_boiteboue";
	public static final String PAT_EQPT_FENETRE = "patrimoine_eqpt_fenetre";
	public static final String PAT_EQPT_COLONNEMONTANTE = "patrimoine_eqpt_colonnemontante";
	public static final String PAT_EQPT_PORTE = "patrimoine_eqpt_porte";
	public static final String PAT_EQPT_CELLULEPOSTEHT = "patrimoine_eqpt_celluleposteht";
	public static final String PAT_EQPT_PREFILTRE = "patrimoine_eqpt_prefiltres";
	public static final String PAT_EQPT_MODULELAMELLAIRE = "patrimoine_eqpt_modulelamellaire";
	public static final String PAT_EQPT_MEMBRANEUF = "patrimoine_eqpt_membraneuf";
	public static final String PAT_EQPT_MELANGEURSTATIQUE = "patrimoine_eqpt_melangeurstatique";
	public static final String PAT_EQPT_DESTRUCTEUROZONE = "patrimoine_eqpt_destozone";
	public static final String PAT_EQPT_GARDECORPS = "patrimoine_eqpt_gardecorps";
	public static final String PAT_EQPT_LIAISONELECTRIQUE = "patrimoine_eqpt_liaisonele";
	public static final String PAT_EQPT_TRANSFORMATEURREDRESSEUR = "patrimoine_eqpt_transformredress";
	public static final String PAT_EQPT_DISCONNECTEUR = "patrimoine_eqpt_disconnecteur";
	public static final String PAT_EQPT_CANALCOMPTAGE = "patrimoine_eqpt_canalcomptage";
	public static final String PAT_EQPT_ONDULEUR = "patrimoine_eqpt_onduleur";
	public static final String PAT_EQPT_ENREGISTREUR = "patrimoine_eqpt_enregistreur";
	public static final String PAT_EQPT_CONDENSATEUR = "patrimoine_eqpt_condensateur";
	public static final String PAT_EQPT_SECHEURAIR = "patrimoine_eqpt_secheurair";
	public static final String PAT_EQPT_PRELEVEURECHANTILLONNEUR = "patrimoine_eqpt_preleveurechantillon";
	public static final String PAT_EQPT_COLLECTEUR = "patrimoine_eqpt_collecteur";

	public static final String PAT_EQPT_GROUPEMALAXEUR = "patrimoine_eqpt_groupemalaxeur";
	public static final String PAT_EQPT_ASPIRATEURBOUES = "patrimoine_eqpt_aspiboue";
	public static final String PAT_EQPT_SOUPAPE = "patrimoine_eqpt_soupape";
	public static final String PAT_EQPT_VISTRANSFERT = "patrimoine_eqpt_vistransfert";
	public static final String PAT_EQPT_BUSELURE = "patrimoine_eqpt_buselure";

	public static final String PAT_EQPT_CREPINE = "patrimoine_eqpt_crepine";
	public static final String PAT_EQPT_HYDROEJECTEUR = "patrimoine_eqpt_hydroejecteur";
	public static final String PAT_EQPT_BOUTEILLECHLORE = "patrimoine_eqpt_bouteillechlore";
	public static final String PAT_EQPT_CAILLEBOTIS = "patrimoine_eqpt_caillebotis";
	public static final String PAT_EQPT_MATERIELLABORATOIRE = "patrimoine_eqpt_materiellabo";
	public static final String PAT_EQPT_AEROTHERME = "patrimoine_eqpt_aerotherme";
	public static final String PAT_EQPT_EXTRACTEURAIR = "patrimoine_eqpt_extractair";
	public static final String PAT_EQPT_ECHELLE = "patrimoine_eqpt_echelle";
	public static final String PAT_EQPT_TUBECREPINE = "patrimoine_eqpt_tubecrepine";
	public static final String PAT_EQPT_DEMARREURELECTRIQUE = "patrimoine_eqpt_demarreurelec";

	public static final String PAT_EQPT_COMPTEURELECTRIQUE = "patrimoine_eqpt_cptelec";
	public static final String PAT_EQPT_ECHANGEURCHALEUR = "patrimoine_eqpt_echangechaleur";
	public static final String PAT_EQPT_PROTECTIONINCENDIE = "patrimoine_eqpt_protecincendie";
	public static final String PAT_EQPT_RETOURNEMENTTERRES = "patrimoine_eqpt_retourneterre";
	public static final String PAT_EQPT_ESPACEVERT = "patrimoine_eqpt_espacevert";
	public static final String PAT_EQPT_MASSEANODIQUE = "patrimoine_eqpt_massanodique";

	public static final String PAT_EQPT_BENNE = "patrimoine_eqpt_benne";
	public static final String PAT_EQPT_DESHUMIDIFICATEUR = "patrimoine_eqpt_deshumidificateur";
	public static final String PAT_EQPT_ELECTRODEREFERENCE = "patrimoine_eqpt_elecref";
	public static final String PAT_EQPT_GRAPPIN = "patrimoine_eqpt_grappin";
	public static final String PAT_EQPT_VERIN = "patrimoine_eqpt_verin";
	public static final String PAT_EQPT_STABILISATEURECOULEMENT = "patrimoine_eqpt_stabecoul";
	public static final String PAT_EQPT_SYSTEMEDIFFUSION = "patrimoine_eqpt_systemediff";
	public static final String PAT_EQPT_VENTOUSE = "patrimoine_eqpt_ventouse";
	public static final String PAT_EQPT_VIDANGE = "patrimoine_eqpt_vidange";
	
	public static final String PAT_EQPT_AUSCULTATIONBARRAGE = "patrimoine_eqpt_auscultationbarrage";
	public static final String PAT_EQPT_MOTEURELECTRIQUEMANOEUVRE = "patrimoine_eqpt_moteurelectriquemanoeuvre";
	public static final String PAT_EQPT_POINTPRELEVEMENT = "patrimoine_eqpt_pointprelevement";
	public static final String PAT_EQPT_PRELOCALISATEUR = "patrimoine_eqpt_prelocalisateur";
	public static final String PAT_EQPT_OUVRAGESEVACUATIONBARRAGE = "patrimoine_eqpt_ouvragesevacuationbarrage";
	public static final String PAT_EQPT_REACTEUR = "patrimoine_eqpt_reacteur";

	// XLSX PART
	public static final String CONTRAT = "patrimoine_contrat";
	public static final String SITE = "patrimoine_site";

	public static final String OUVRAGE_SOCLE = "patrimoine_ouvrage_socle";
	public static final String OUVRAGE_BAR = "patrimoine_ouvrage_bar";
	public static final String OUVRAGE_USI = "patrimoine_ouvrage_usi";
	public static final String OUVRAGE_LOG = "patrimoine_ouvrage_log";
	public static final String OUVRAGE_CAR = "patrimoine_ouvrage_car";
	public static final String OUVRAGE_LAG = "patrimoine_ouvrage_lag";
	public static final String OUVRAGE_RST = "patrimoine_ouvrage_rst";
	public static final String OUVRAGE_BAS = "patrimoine_ouvrage_bas";
	public static final String OUVRAGE_STP = "patrimoine_ouvrage_stp";
	public static final String OUVRAGE_PCA = "patrimoine_ouvrage_pca";
	public static final String OUVRAGE_CAP = "patrimoine_ouvrage_cap";
	public static final String OUVRAGE_PBA = "patrimoine_ouvrage_pba";
	public static final String OUVRAGE_CPT = "patrimoine_ouvrage_cpt";
	public static final String OUVRAGE_PRE = "patrimoine_ouvrage_pre";

	public static final String UNITE_CAR = "patrimoine_unite_car";
	public static final String UNITE_CHL = "patrimoine_unite_chl";
	public static final String UNITE_CLA = "patrimoine_unite_cla";
	public static final String UNITE_CPT = "patrimoine_unite_cpt";
	public static final String UNITE_ELE = "patrimoine_unite_ele";
	public static final String UNITE_ESV = "patrimoine_unite_esv";
	public static final String UNITE_FIL = "patrimoine_unite_fil";
	public static final String UNITE_GCI = "patrimoine_unite_gci";
	public static final String UNITE_HYD = "patrimoine_unite_hyd";
	public static final String UNITE_MIN = "patrimoine_unite_min";
	public static final String UNITE_OXY = "patrimoine_unite_oxy";
	public static final String UNITE_POM = "patrimoine_unite_pom";
	public static final String UNITE_REC = "patrimoine_unite_rec";
	public static final String UNITE_REG = "patrimoine_unite_reg";
	public static final String UNITE_SEC = "patrimoine_unite_sec";
	public static final String UNITE_SET = "patrimoine_unite_set";
	public static final String UNITE_SOCLE = "patrimoine_unite_socle";
	public static final String UNITE_SPE = "patrimoine_unite_spe";
	public static final String UNITE_TLG = "patrimoine_unite_tlg";
	public static final String UNITE_TRT = "patrimoine_unite_trt";
	public static final String UNITE_ULT = "patrimoine_unite_ult";

	public void save(DateChargement dateChargement, Patrimoine patrimoine, ClassDefinition mainClass, String fileName) {
		List<LogXML> logs = new ArrayList<>();

		Integer idChg = (Integer) getHibernateTemplate().save(dateChargement);

		try {
			LogInsertXML logInsert = new LogInsertXML(PAT_EQPT_SOCLE, fileName, idChg);
			if (patrimoine.getEquipementsSocle() != null) {
				if (patrimoine.getEquipementsSocle().getEquipementSocle() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getEquipementsSocle().getClass().getName(), mainClass);

					for (TypeEquipementSocle item : patrimoine.getEquipementsSocle().getEquipementSocle()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getEqptsocleVeId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(PAT_EQPT_AGITATEUR, fileName, idChg);
			if (patrimoine.getEquipementsAgitateurs() != null) {
				if (patrimoine.getEquipementsAgitateurs().getEquipementAgitateur() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getEquipementsAgitateurs().getClass().getName(), mainClass);

					for (TypeEquipementAgitateur item : patrimoine.getEquipementsAgitateurs().getEquipementAgitateur()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getEqptagitateurEqptsocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(PAT_EQPT_BACRETENTION, fileName, idChg);
			if (patrimoine.getEquipementsBacsRetention() != null) {
				if (patrimoine.getEquipementsBacsRetention().getEquipementBacRetention() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getEquipementsBacsRetention().getClass().getName(), mainClass);

					for (TypeEquipementBacRetention item : patrimoine.getEquipementsBacsRetention().getEquipementBacRetention()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getEqptbacEqptsocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(PAT_EQPT_CLAPET, fileName, idChg);
			if (patrimoine.getEquipementsClapets() != null) {
				if (patrimoine.getEquipementsClapets().getEquipementClapet() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getEquipementsClapets().getClass().getName(), mainClass);

					for (TypeEquipementClapet item : patrimoine.getEquipementsClapets().getEquipementClapet()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getEqptclapetEqptsocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(PAT_EQPT_COMPTAGE, fileName, idChg);
			if (patrimoine.getEquipementsComptages() != null) {
				if (patrimoine.getEquipementsComptages().getEquipementComptage() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getEquipementsComptages().getClass().getName(), mainClass);

					for (TypeEquipementComptage item : patrimoine.getEquipementsComptages().getEquipementComptage()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getEqptcomptageEqptsocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(PAT_EQPT_POMPE, fileName, idChg);
			if (patrimoine.getEquipementsPompes() != null) {
				if (patrimoine.getEquipementsPompes().getEquipementPompe() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getEquipementsPompes().getClass().getName(), mainClass);

					for (TypeEquipementPompe item : patrimoine.getEquipementsPompes().getEquipementPompe()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getEqptpompeEqptsocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(PAT_EQPT_PORTAIL, fileName, idChg);
			if (patrimoine.getEquipementsPortails() != null) {
				if (patrimoine.getEquipementsPortails().getEquipementPortail() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getEquipementsPortails().getClass().getName(), mainClass);

					for (TypeEquipementPortail item : patrimoine.getEquipementsPortails().getEquipementPortail()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getEqptportailEqptsocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(PAT_EQPT_SILO, fileName, idChg);
			if (patrimoine.getEquipementsSilos() != null) {
				if (patrimoine.getEquipementsSilos().getEquipementSilo() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getEquipementsSilos().getClass().getName(), mainClass);

					for (TypeEquipementSilo item : patrimoine.getEquipementsSilos().getEquipementSilo()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getEqptsiloEqptsocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(PAT_EQPT_VANNE, fileName, idChg);
			if (patrimoine.getEquipementsVannes() != null) {
				if (patrimoine.getEquipementsVannes().getEquipementVanne() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getEquipementsVannes().getClass().getName(), mainClass);

					for (TypeEquipementVanne item : patrimoine.getEquipementsVannes().getEquipementVanne()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getEqptvanneEqptsocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(PAT_COMPOSANT, fileName, idChg);
			if (patrimoine.getComposants() != null) {
				if (patrimoine.getComposants().getComposant() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getComposants().getClass().getName(), mainClass);

					for (TypeComposant item : patrimoine.getComposants().getComposant()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getComposantEqptsocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(PAT_EQPT_GROUPEELECTROGENE, fileName, idChg);
			if (patrimoine.getEquipementsGroupesElectrogenes() != null) {
				if (patrimoine.getEquipementsGroupesElectrogenes().getEquipementGroupeElectrogene() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getEquipementsGroupesElectrogenes().getClass().getName(), mainClass);

					for (TypeEquipementGroupeElectrogene item : patrimoine.getEquipementsGroupesElectrogenes().getEquipementGroupeElectrogene()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getEqptgroupeelecEqptsocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(PAT_EQPT_GROUPEDOSAGE, fileName, idChg);
			if (patrimoine.getEquipementsGroupesDosage() != null) {
				if (patrimoine.getEquipementsGroupesDosage().getEquipementGroupeDosage() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getEquipementsGroupesDosage().getClass().getName(), mainClass);

					for (TypeEquipementGroupeDosage item : patrimoine.getEquipementsGroupesDosage().getEquipementGroupeDosage()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getEqptgrpdosageEqptsocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(PAT_EQPT_BALLONREGULATION, fileName, idChg);
			if (patrimoine.getEquipementsBallons() != null) {
				if (patrimoine.getEquipementsBallons().getEquipementBallon() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getEquipementsBallons().getClass().getName(), mainClass);

					for (TypeEquipementBallon item : patrimoine.getEquipementsBallons().getEquipementBallon()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getEqptballonregulEqptsocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(PAT_EQPT_VANNEREGULATION, fileName, idChg);
			if (patrimoine.getEquipementsVannesRegulation() != null) {
				if (patrimoine.getEquipementsVannesRegulation().getEquipementVanneRegulation() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getEquipementsVannesRegulation().getClass().getName(), mainClass);

					for (TypeEquipementVanneRegulation item : patrimoine.getEquipementsVannesRegulation().getEquipementVanneRegulation()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getEqptvanneregulEqptsocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(PAT_EQPT_GENERATEURUV, fileName, idChg);
			if (patrimoine.getEquipementsGenerateursUV() != null) {
				if (patrimoine.getEquipementsGenerateursUV().getEquipementGenerateurUV() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getEquipementsGenerateursUV().getClass().getName(), mainClass);

					for (TypeEquipementGenerateurUV item : patrimoine.getEquipementsGenerateursUV().getEquipementGenerateurUV()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getEqptgenerateuruvEqptsocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(PAT_EQPT_TRANSFORMATEURHT, fileName, idChg);
			if (patrimoine.getEquipementsTransformateursHT() != null) {
				if (patrimoine.getEquipementsTransformateursHT().getEquipementTransformateurHT() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getEquipementsTransformateursHT().getClass().getName(), mainClass);

					for (TypeEquipementTransformateurHT item : patrimoine.getEquipementsTransformateursHT().getEquipementTransformateurHT()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getEqpttransfohtEqptsocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(PAT_EQPT_APPAREILLEVAGE, fileName, idChg);
			if (patrimoine.getEquipementsAppareilsLevage() != null) {
				if (patrimoine.getEquipementsAppareilsLevage().getEquipementAppareilLevage() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getEquipementsAppareilsLevage().getClass().getName(), mainClass);

					for (TypeEquipementAppareilLevage item : patrimoine.getEquipementsAppareilsLevage().getEquipementAppareilLevage()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getEqptapplevageEqptsocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(PAT_EQPT_TURBINEHYDROELECTRIQUE, fileName, idChg);
			if (patrimoine.getEquipementsTurbinesHydroelectriques() != null) {
				if (patrimoine.getEquipementsTurbinesHydroelectriques().getEquipementTurbineHydroelectrique() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getEquipementsTurbinesHydroelectriques().getClass().getName(), mainClass);

					for (TypeEquipementTurbineHydroelectrique item : patrimoine.getEquipementsTurbinesHydroelectriques().getEquipementTurbineHydroelectrique()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getEqptturbhydeleEqptsocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(PAT_EQPT_GROUPEAIR, fileName, idChg);
			if (patrimoine.getEquipementsGroupesAir() != null) {
				if (patrimoine.getEquipementsGroupesAir().getEquipementGroupeAir() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getEquipementsGroupesAir().getClass().getName(), mainClass);

					for (TypeEquipementGroupeAir item : patrimoine.getEquipementsGroupesAir().getEquipementGroupeAir()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getEqptgroupeairEqptsocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(PAT_EQPT_OZONEUR, fileName, idChg);
			if (patrimoine.getEquipementsOzoneurs() != null) {
				if (patrimoine.getEquipementsOzoneurs().getEquipementOzoneur() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getEquipementsOzoneurs().getClass().getName(), mainClass);

					for (TypeEquipementOzoneur item : patrimoine.getEquipementsOzoneurs().getEquipementOzoneur()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getEqptozoneurEqptsocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(PAT_EQPT_COLONNEFORAGE, fileName, idChg);
			if (patrimoine.getEquipementsColonnesForage() != null) {
				if (patrimoine.getEquipementsColonnesForage().getEquipementColonneForage() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getEquipementsColonnesForage().getClass().getName(), mainClass);

					for (TypeEquipementColonneForage item : patrimoine.getEquipementsColonnesForage().getEquipementColonneForage()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getEqptcolonneforageEqptsocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(PAT_EQPT_VARIATEURVITESSE, fileName, idChg);
			if (patrimoine.getEquipementsVariateursVitesse() != null) {
				if (patrimoine.getEquipementsVariateursVitesse().getEquipementVariateurVitesse() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getEquipementsVariateursVitesse().getClass().getName(), mainClass);

					for (TypeEquipementVariateurVitesse item : patrimoine.getEquipementsVariateursVitesse().getEquipementVariateurVitesse()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getEqptvarvitesseEqptsocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(PAT_EQPT_PONTRACLEUR, fileName, idChg);
			if (patrimoine.getEquipementsPontsRacleurs() != null) {
				if (patrimoine.getEquipementsPontsRacleurs().getEquipementPontRacleur() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getEquipementsPontsRacleurs().getClass().getName(), mainClass);

					for (TypeEquipementPontRacleur item : patrimoine.getEquipementsPontsRacleurs().getEquipementPontRacleur()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getEqptpontracleurEqptsocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(PAT_EQPT_CHLOROMETRE, fileName, idChg);
			if (patrimoine.getEquipementsChlorometres() != null) {
				if (patrimoine.getEquipementsChlorometres().getEquipementChlorometre() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getEquipementsChlorometres().getClass().getName(), mainClass);

					for (TypeEquipementChlorometre item : patrimoine.getEquipementsChlorometres().getEquipementChlorometre()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getEqptchlorometreEqptsocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(PAT_EQPT_TUYAUTERIE, fileName, idChg);
			if (patrimoine.getEquipementsTuyauteries() != null) {
				if (patrimoine.getEquipementsTuyauteries().getEquipementTuyauterie() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getEquipementsTuyauteries().getClass().getName(), mainClass);

					for (TypeEquipementTuyauterie item : patrimoine.getEquipementsTuyauteries().getEquipementTuyauterie()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getEqpttuyauterieEqptsocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(PAT_EQPT_PORTESECTIONNELLE, fileName, idChg);
			if (patrimoine.getEquipementsPortesSectionnelles() != null) {
				if (patrimoine.getEquipementsPortesSectionnelles().getEquipementPorteSectionnelle() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getEquipementsPortesSectionnelles().getClass().getName(), mainClass);

					for (TypeEquipementPorteSectionnelle item : patrimoine.getEquipementsPortesSectionnelles().getEquipementPorteSectionnelle()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getEqptportesectionEqptsocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(PAT_EQPT_CLOTURE, fileName, idChg);
			if (patrimoine.getEquipementsClotures() != null) {
				if (patrimoine.getEquipementsClotures().getEquipementCloture() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getEquipementsClotures().getClass().getName(), mainClass);

					for (TypeEquipementCloture item : patrimoine.getEquipementsClotures().getEquipementCloture()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getEqptclotureEqptsocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(PAT_EQPT_PRESSEBOUE, fileName, idChg);
			if (patrimoine.getEquipementsPressesBoue() != null) {
				if (patrimoine.getEquipementsPressesBoue().getEquipementPresseBoue() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getEquipementsPressesBoue().getClass().getName(), mainClass);

					for (TypeEquipementPresseBoue item : patrimoine.getEquipementsPressesBoue().getEquipementPresseBoue()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getEqptpresseboueEqptsocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(PAT_EQPT_STATIONTELETRANSMISSION, fileName, idChg);
			if (patrimoine.getEquipementsStationsTeletransmission() != null) {
				if (patrimoine.getEquipementsStationsTeletransmission().getEquipementStationTeletransmission() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getEquipementsStationsTeletransmission().getClass().getName(), mainClass);

					for (TypeEquipementStationTeletransmission item : patrimoine.getEquipementsStationsTeletransmission().getEquipementStationTeletransmission()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getEqptstationteletrEqptsocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(PAT_EQPT_CENTRALEHYDRAULIQUE, fileName, idChg);
			if (patrimoine.getEquipementsCentralesHydrauliques() != null) {
				if (patrimoine.getEquipementsCentralesHydrauliques().getEquipementCentraleHydraulique() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getEquipementsCentralesHydrauliques().getClass().getName(), mainClass);

					for (TypeEquipementCentraleHydraulique item : patrimoine.getEquipementsCentralesHydrauliques().getEquipementCentraleHydraulique()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getEqptcentralehydrauEqptsocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(PAT_EQPT_CAPTEUR, fileName, idChg);
			if (patrimoine.getEquipementsCapteurs() != null) {
				if (patrimoine.getEquipementsCapteurs().getEquipementCapteur() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getEquipementsCapteurs().getClass().getName(), mainClass);

					for (TypeEquipementCapteur item : patrimoine.getEquipementsCapteurs().getEquipementCapteur()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getEqptcapteurEqptsocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(PAT_EQPT_ANTIINTRUSION, fileName, idChg);
			if (patrimoine.getEquipementsAntiIntrusions() != null) {
				if (patrimoine.getEquipementsAntiIntrusions().getEquipementAntiIntrusion() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getEquipementsAntiIntrusions().getClass().getName(), mainClass);

					for (TypeEquipementAntiIntrusion item : patrimoine.getEquipementsAntiIntrusions().getEquipementAntiIntrusion()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getEqptantiintEqptsocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(PAT_EQPT_CHARIOTELEVATEUR, fileName, idChg);
			if (patrimoine.getEquipementsChariotsElevateurs() != null) {
				if (patrimoine.getEquipementsChariotsElevateurs().getEquipementChariotElevateur() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getEquipementsChariotsElevateurs().getClass().getName(), mainClass);

					for (TypeEquipementChariotElevateur item : patrimoine.getEquipementsChariotsElevateurs().getEquipementChariotElevateur()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getEqptchariotEqptsocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(PAT_EQPT_TRAPPEACCES, fileName, idChg);
			if (patrimoine.getEquipementsTrappesAcces() != null) {
				if (patrimoine.getEquipementsTrappesAcces().getEquipementTrappeAcces() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getEquipementsTrappesAcces().getClass().getName(), mainClass);

					for (TypeEquipementTrappeAcces item : patrimoine.getEquipementsTrappesAcces().getEquipementTrappeAcces()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getEqpttrappeaccesEqptsocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(PAT_EQPT_FILTREFERME, fileName, idChg);
			if (patrimoine.getEquipementsFiltresFermes() != null) {
				if (patrimoine.getEquipementsFiltresFermes().getEquipementFiltreFerme() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getEquipementsFiltresFermes().getClass().getName(), mainClass);

					for (TypeEquipementFiltreFerme item : patrimoine.getEquipementsFiltresFermes().getEquipementFiltreFerme()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getEqptfiltrefermeEqptsocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(PAT_EQPT_FILTRE_Y, fileName, idChg);
			if (patrimoine.getEquipementsFiltresY() != null) {
				if (patrimoine.getEquipementsFiltresY().getEquipementFiltreY() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getEquipementsFiltresY().getClass().getName(), mainClass);

					for (TypeEquipementFiltreY item : patrimoine.getEquipementsFiltresY().getEquipementFiltreY()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getEqptfiltreyEqptsocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(PAT_EQPT_AUTOMATE, fileName, idChg);
			if (patrimoine.getEquipementsAutomates() != null) {
				if (patrimoine.getEquipementsAutomates().getEquipementAutomate() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getEquipementsAutomates().getClass().getName(), mainClass);

					for (TypeEquipementAutomate item : patrimoine.getEquipementsAutomates().getEquipementAutomate()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getEqptautomprogindusEqptsocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(PAT_EQPT_HYDROCYCLONE, fileName, idChg);
			if (patrimoine.getEquipementsHydrocyclones() != null) {
				if (patrimoine.getEquipementsHydrocyclones().getEquipementHydrocyclone() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getEquipementsHydrocyclones().getClass().getName(), mainClass);

					for (TypeEquipementHydrocyclone item : patrimoine.getEquipementsHydrocyclones().getEquipementHydrocyclone()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getEqpthydrocyclEqptsocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(PAT_EQPT_VOIRIE, fileName, idChg);
			if (patrimoine.getEquipementsVoiries() != null) {
				if (patrimoine.getEquipementsVoiries().getEquipementVoirie() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getEquipementsVoiries().getClass().getName(), mainClass);

					for (TypeEquipementVoirie item : patrimoine.getEquipementsVoiries().getEquipementVoirie()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getEqptvoirieEqptsocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(PAT_EQPT_INTERVENTION, fileName, idChg);
			if (patrimoine.getInterventions() != null) {
				if (patrimoine.getInterventions().getIntervention() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getInterventions().getClass().getName(), mainClass);

					for (TypeIntervention item : patrimoine.getInterventions().getIntervention()) {
						item.setIdChg(idChg);
						String interventionId = item.getInterventionEqptsocleId();
						if (interventionId.equals("NR")) {
							interventionId = item.getInterventionUnitesocleId();
						}
						saveObject(item, classDef, logInsert, logs, interventionId);
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(PAT_EQPT_CONVOYEURTRANSPORTEUR, fileName, idChg);
			if (patrimoine.getEquipementsConvoyeursTransporteurs() != null) {
				if (patrimoine.getEquipementsConvoyeursTransporteurs().getEquipementConvoyeurTransporteur() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getEquipementsConvoyeursTransporteurs().getClass().getName(), mainClass);

					for (TypeEquipementConvoyeurTransporteur item : patrimoine.getEquipementsConvoyeursTransporteurs().getEquipementConvoyeurTransporteur()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getEqptconvtranspEqptsocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(PAT_EQPT_GROUPEREFROIDISSEMENT, fileName, idChg);
			if (patrimoine.getEquipementsGroupesRefroidissement() != null) {
				if (patrimoine.getEquipementsGroupesRefroidissement().getEquipementGroupeRefroidissement() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getEquipementsGroupesRefroidissement().getClass().getName(), mainClass);

					for (TypeEquipementGroupeRefroidissement item : patrimoine.getEquipementsGroupesRefroidissement().getEquipementGroupeRefroidissement()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getEqptgrouperefroiEqptsocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(PAT_EQPT_HERSE, fileName, idChg);
			if (patrimoine.getEquipementsHerses() != null) {
				if (patrimoine.getEquipementsHerses().getEquipementHerse() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getEquipementsHerses().getClass().getName(), mainClass);

					for (TypeEquipementHerse item : patrimoine.getEquipementsHerses().getEquipementHerse()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getEqptherseEqptsocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(PAT_EQPT_ARMOIREELECTRIQUE, fileName, idChg);
			if (patrimoine.getEquipementsArmoiresElectriques() != null) {
				if (patrimoine.getEquipementsArmoiresElectriques().getEquipementArmoireElectrique() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getEquipementsArmoiresElectriques().getClass().getName(), mainClass);

					for (TypeEquipementArmoireElectrique item : patrimoine.getEquipementsArmoiresElectriques().getEquipementArmoireElectrique()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getEqptarmoireelecEqptsocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(PAT_EQPT_PORTILLON, fileName, idChg);
			if (patrimoine.getEquipementsPortillons() != null) {
				if (patrimoine.getEquipementsPortillons().getEquipementPortillon() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getEquipementsPortillons().getClass().getName(), mainClass);

					for (TypeEquipementPortillon item : patrimoine.getEquipementsPortillons().getEquipementPortillon()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getEqptportillonEqptsocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(PAT_EQPT_CENTRIFUGEUSE, fileName, idChg);
			if (patrimoine.getEquipementsCentrifugeuses() != null) {
				if (patrimoine.getEquipementsCentrifugeuses().getEquipementCentrifugeuse() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getEquipementsCentrifugeuses().getClass().getName(), mainClass);

					for (TypeEquipementCentrifugeuse item : patrimoine.getEquipementsCentrifugeuses().getEquipementCentrifugeuse()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getEqptcentrifugeuseEqptsocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(PAT_EQPT_BOITEBOUE, fileName, idChg);
			if (patrimoine.getEquipementsBoitesBoue() != null) {
				if (patrimoine.getEquipementsBoitesBoue().getEquipementBoiteBoue() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getEquipementsBoitesBoue().getClass().getName(), mainClass);

					for (TypeEquipementBoiteBoue item : patrimoine.getEquipementsBoitesBoue().getEquipementBoiteBoue()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getEqptboiteboueEqptsocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(PAT_EQPT_FENETRE, fileName, idChg);
			if (patrimoine.getEquipementsFenetres() != null) {
				if (patrimoine.getEquipementsFenetres().getEquipementFenetre() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getEquipementsFenetres().getClass().getName(), mainClass);

					for (TypeEquipementFenetre item : patrimoine.getEquipementsFenetres().getEquipementFenetre()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getEqptfenetreEqptsocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(PAT_EQPT_COLONNEMONTANTE, fileName, idChg);
			if (patrimoine.getEquipementsColonnesMontantes() != null) {
				if (patrimoine.getEquipementsColonnesMontantes().getEquipementColonneMontante() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getEquipementsColonnesMontantes().getClass().getName(), mainClass);

					for (TypeEquipementColonneMontante item : patrimoine.getEquipementsColonnesMontantes().getEquipementColonneMontante()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getEqptcolonnemontanteEqptsocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(PAT_EQPT_PORTE, fileName, idChg);
			if (patrimoine.getEquipementsPortes() != null) {
				if (patrimoine.getEquipementsPortes().getEquipementPorte() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getEquipementsPortes().getClass().getName(), mainClass);

					for (TypeEquipementPorte item : patrimoine.getEquipementsPortes().getEquipementPorte()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getEqptporteEqptsocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(PAT_EQPT_CELLULEPOSTEHT, fileName, idChg);
			if (patrimoine.getEquipementsCellulesPosteHT() != null) {
				if (patrimoine.getEquipementsCellulesPosteHT().getEquipementCellulePosteHT() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getEquipementsCellulesPosteHT().getClass().getName(), mainClass);

					for (TypeEquipementCellulePosteHT item : patrimoine.getEquipementsCellulesPosteHT().getEquipementCellulePosteHT()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getEqptcellulepostehtEqptsocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(PAT_EQPT_PREFILTRE, fileName, idChg);
			if (patrimoine.getEquipementsPrefiltres() != null) {
				if (patrimoine.getEquipementsPrefiltres().getEquipementPrefiltre() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getEquipementsPrefiltres().getClass().getName(), mainClass);

					for (TypeEquipementPrefiltre item : patrimoine.getEquipementsPrefiltres().getEquipementPrefiltre()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getEqptprefiltreEqptsocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(PAT_EQPT_MODULELAMELLAIRE, fileName, idChg);
			if (patrimoine.getEquipementsModulesLamellaires() != null) {
				if (patrimoine.getEquipementsModulesLamellaires().getEquipementModuleLamellaire() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getEquipementsModulesLamellaires().getClass().getName(), mainClass);

					for (TypeEquipementModuleLamellaire item : patrimoine.getEquipementsModulesLamellaires().getEquipementModuleLamellaire()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getEqptmodulelamellEqptsocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(PAT_EQPT_MEMBRANEUF, fileName, idChg);
			if (patrimoine.getEquipementsMembranesUF() != null) {
				if (patrimoine.getEquipementsMembranesUF().getEquipementMembraneUF() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getEquipementsMembranesUF().getClass().getName(), mainClass);

					for (TypeEquipementMembraneUF item : patrimoine.getEquipementsMembranesUF().getEquipementMembraneUF()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getEqptmembraneufEqptsocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(PAT_EQPT_MELANGEURSTATIQUE, fileName, idChg);
			if (patrimoine.getEquipementsMelangeursStatiques() != null) {
				if (patrimoine.getEquipementsMelangeursStatiques().getEquipementMelangeurStatique() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getEquipementsMelangeursStatiques().getClass().getName(), mainClass);

					for (TypeEquipementMelangeurStatique item : patrimoine.getEquipementsMelangeursStatiques().getEquipementMelangeurStatique()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getEqptmelangeurstatEqptsocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(PAT_EQPT_DESTRUCTEUROZONE, fileName, idChg);
			if (patrimoine.getEquipementsDestructeurOzone() != null) {
				if (patrimoine.getEquipementsDestructeurOzone().getEquipementDestructeurOzone() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getEquipementsDestructeurOzone().getClass().getName(), mainClass);

					for (TypeEquipementDestructeurOzone item : patrimoine.getEquipementsDestructeurOzone().getEquipementDestructeurOzone()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getEqptdestozoneEqptsocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(PAT_EQPT_GARDECORPS, fileName, idChg);
			if (patrimoine.getEquipementsGardesCorps() != null) {
				if (patrimoine.getEquipementsGardesCorps().getEquipementGardeCorps() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getEquipementsGardesCorps().getClass().getName(), mainClass);

					for (TypeEquipementGardeCorps item : patrimoine.getEquipementsGardesCorps().getEquipementGardeCorps()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getEqptgardecorpsEqptsocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(PAT_EQPT_LIAISONELECTRIQUE, fileName, idChg);
			if (patrimoine.getEquipementsLiaisonsElectriques() != null) {
				if (patrimoine.getEquipementsLiaisonsElectriques().getEquipementLiaisonElectrique() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getEquipementsLiaisonsElectriques().getClass().getName(), mainClass);

					for (TypeEquipementLiaisonElectrique item : patrimoine.getEquipementsLiaisonsElectriques().getEquipementLiaisonElectrique()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getEqptliaisoneleEqptsocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(PAT_EQPT_TRANSFORMATEURREDRESSEUR, fileName, idChg);
			if (patrimoine.getEquipementsTransformateursRedresseurs() != null) {
				if (patrimoine.getEquipementsTransformateursRedresseurs().getEquipementTransformateurRedresseur() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getEquipementsTransformateursRedresseurs().getClass().getName(), mainClass);

					for (TypeEquipementTransformateurRedresseur item : patrimoine.getEquipementsTransformateursRedresseurs().getEquipementTransformateurRedresseur()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getEqpttransfredrEqptsocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(PAT_EQPT_DISCONNECTEUR, fileName, idChg);
			if (patrimoine.getEquipementsDisconnecteurs() != null) {
				if (patrimoine.getEquipementsDisconnecteurs().getEquipementDisconnecteur() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getEquipementsDisconnecteurs().getClass().getName(), mainClass);

					for (TypeEquipementDisconnecteur item : patrimoine.getEquipementsDisconnecteurs().getEquipementDisconnecteur()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getEqptdisconEqptsocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(PAT_EQPT_CANALCOMPTAGE, fileName, idChg);
			if (patrimoine.getEquipementsCanauxComptage() != null) {
				if (patrimoine.getEquipementsCanauxComptage().getEquipementCanalComptage() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getEquipementsCanauxComptage().getClass().getName(), mainClass);

					for (TypeEquipementCanalComptage item : patrimoine.getEquipementsCanauxComptage().getEquipementCanalComptage()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getEqptcanalcomptageEqptsocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(PAT_EQPT_ONDULEUR, fileName, idChg);
			if (patrimoine.getEquipementsOnduleurs() != null) {
				if (patrimoine.getEquipementsOnduleurs().getEquipementOnduleur() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getEquipementsOnduleurs().getClass().getName(), mainClass);

					for (TypeEquipementOnduleur item : patrimoine.getEquipementsOnduleurs().getEquipementOnduleur()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getEqptonduleurEqptsocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(PAT_EQPT_ENREGISTREUR, fileName, idChg);
			if (patrimoine.getEquipementsEnregistreurs() != null) {
				if (patrimoine.getEquipementsEnregistreurs().getEquipementEnregistreur() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getEquipementsEnregistreurs().getClass().getName(), mainClass);

					for (TypeEquipementEnregistreur item : patrimoine.getEquipementsEnregistreurs().getEquipementEnregistreur()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getEqptenregistEqptsocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(PAT_EQPT_CONDENSATEUR, fileName, idChg);
			if (patrimoine.getEquipementsCondensateurs() != null) {
				if (patrimoine.getEquipementsCondensateurs().getEquipementCondensateur() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getEquipementsCondensateurs().getClass().getName(), mainClass);

					for (TypeEquipementCondensateur item : patrimoine.getEquipementsCondensateurs().getEquipementCondensateur()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getEqptcondensateurEqptsocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(PAT_EQPT_SECHEURAIR, fileName, idChg);
			if (patrimoine.getEquipementsSecheursAir() != null) {
				if (patrimoine.getEquipementsSecheursAir().getEquipementSecheurAir() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getEquipementsSecheursAir().getClass().getName(), mainClass);

					for (TypeEquipementSecheurAir item : patrimoine.getEquipementsSecheursAir().getEquipementSecheurAir()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getEqptsecheurairEqptsocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(PAT_EQPT_PRELEVEURECHANTILLONNEUR, fileName, idChg);
			if (patrimoine.getEquipementsPreleveursEchantillonneurs() != null) {
				if (patrimoine.getEquipementsPreleveursEchantillonneurs().getEquipementPreleveurEchantillonneur() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getEquipementsPreleveursEchantillonneurs().getClass().getName(), mainClass);

					for (TypeEquipementPreleveurEchantillonneur item : patrimoine.getEquipementsPreleveursEchantillonneurs().getEquipementPreleveurEchantillonneur()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getEqptprelevechantEqptsocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(PAT_EQPT_COLLECTEUR, fileName, idChg);
			if (patrimoine.getEquipementsCollecteurs() != null) {
				if (patrimoine.getEquipementsCollecteurs().getEquipementCollecteur() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getEquipementsCollecteurs().getClass().getName(), mainClass);

					for (TypeEquipementCollecteur item : patrimoine.getEquipementsCollecteurs().getEquipementCollecteur()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getEqptcollecteurEqptsocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(PAT_EQPT_GROUPEMALAXEUR, fileName, idChg);
			if (patrimoine.getEquipementsGroupesMalaxeurs() != null) {
				if (patrimoine.getEquipementsGroupesMalaxeurs().getEquipementGroupeMalaxeur() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getEquipementsGroupesMalaxeurs().getClass().getName(), mainClass);

					for (TypeEquipementGroupeMalaxeur item : patrimoine.getEquipementsGroupesMalaxeurs().getEquipementGroupeMalaxeur()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getEqptgroupemalaxEqptsocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(PAT_EQPT_ASPIRATEURBOUES, fileName, idChg);
			if (patrimoine.getEquipementsAspirateursBoues() != null) {
				if (patrimoine.getEquipementsAspirateursBoues().getEquipementAspirateurBoues() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getEquipementsAspirateursBoues().getClass().getName(), mainClass);

					for (TypeEquipementAspirateurBoues item : patrimoine.getEquipementsAspirateursBoues().getEquipementAspirateurBoues()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getEqptaspiboueEqptsocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(PAT_EQPT_SOUPAPE, fileName, idChg);
			if (patrimoine.getEquipementsSoupapes() != null) {
				if (patrimoine.getEquipementsSoupapes().getEquipementSoupape() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getEquipementsSoupapes().getClass().getName(), mainClass);

					for (TypeEquipementSoupape item : patrimoine.getEquipementsSoupapes().getEquipementSoupape()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getEqptsoupapeEqptsocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(PAT_EQPT_VISTRANSFERT, fileName, idChg);
			if (patrimoine.getEquipementsVisTransfert() != null) {
				if (patrimoine.getEquipementsVisTransfert().getEquipementVisTransfert() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getEquipementsVisTransfert().getClass().getName(), mainClass);

					for (TypeEquipementVisTransfert item : patrimoine.getEquipementsVisTransfert().getEquipementVisTransfert()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getEqptvistransfertEqptsocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(PAT_EQPT_BUSELURE, fileName, idChg);
			if (patrimoine.getEquipementsBuselures() != null) {
				if (patrimoine.getEquipementsBuselures().getEquipementBuselure() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getEquipementsBuselures().getClass().getName(), mainClass);

					for (TypeEquipementBuselure item : patrimoine.getEquipementsBuselures().getEquipementBuselure()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getEqptbuselureEqptsocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(PAT_EQPT_CREPINE, fileName, idChg);
			if (patrimoine.getEquipementsCrepines() != null) {
				if (patrimoine.getEquipementsCrepines().getEquipementCrepine() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getEquipementsCrepines().getClass().getName(), mainClass);

					for (TypeEquipementCrepine item : patrimoine.getEquipementsCrepines().getEquipementCrepine()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getEqptcrepineEqptsocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(PAT_EQPT_HYDROEJECTEUR, fileName, idChg);
			if (patrimoine.getEquipementsHydroejecteurs() != null) {
				if (patrimoine.getEquipementsHydroejecteurs().getEquipementHydroejecteur() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getEquipementsHydroejecteurs().getClass().getName(), mainClass);

					for (TypeEquipementHydroejecteur item : patrimoine.getEquipementsHydroejecteurs().getEquipementHydroejecteur()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getEqpthydroejecEqptsocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(PAT_EQPT_BOUTEILLECHLORE, fileName, idChg);
			if (patrimoine.getEquipementsBouteillesChlore() != null) {
				if (patrimoine.getEquipementsBouteillesChlore().getEquipementBouteilleChlore() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getEquipementsBouteillesChlore().getClass().getName(), mainClass);

					for (TypeEquipementBouteilleChlore item : patrimoine.getEquipementsBouteillesChlore().getEquipementBouteilleChlore()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getEqptbouttankchloreEqptsocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(PAT_EQPT_CAILLEBOTIS, fileName, idChg);
			if (patrimoine.getEquipementsCaillebotis() != null) {
				if (patrimoine.getEquipementsCaillebotis().getEquipementCaillebotis() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getEquipementsCaillebotis().getClass().getName(), mainClass);

					for (TypeEquipementCaillebotis item : patrimoine.getEquipementsCaillebotis().getEquipementCaillebotis()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getEqptcaillebotisEqptsocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(PAT_EQPT_MATERIELLABORATOIRE, fileName, idChg);
			if (patrimoine.getEquipementsMaterielsLaboratoire() != null) {
				if (patrimoine.getEquipementsMaterielsLaboratoire().getEquipementMaterielLaboratoire() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getEquipementsMaterielsLaboratoire().getClass().getName(), mainClass);

					for (TypeEquipementMaterielLaboratoire item : patrimoine.getEquipementsMaterielsLaboratoire().getEquipementMaterielLaboratoire()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getEqptmaterlaboEqptsocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(PAT_EQPT_AEROTHERME, fileName, idChg);
			if (patrimoine.getEquipementsAerothermes() != null) {
				if (patrimoine.getEquipementsAerothermes().getEquipementAerotherme() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getEquipementsAerothermes().getClass().getName(), mainClass);

					for (TypeEquipementAerotherme item : patrimoine.getEquipementsAerothermes().getEquipementAerotherme()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getEqptaerothermeEqptsocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(PAT_EQPT_EXTRACTEURAIR, fileName, idChg);
			if (patrimoine.getEquipementsExtracteursAir() != null) {
				if (patrimoine.getEquipementsExtracteursAir().getEquipementExtracteurAir() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getEquipementsExtracteursAir().getClass().getName(), mainClass);

					for (TypeEquipementExtracteurAir item : patrimoine.getEquipementsExtracteursAir().getEquipementExtracteurAir()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getEqptextractairEqptsocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(PAT_EQPT_ECHELLE, fileName, idChg);
			if (patrimoine.getEquipementsEchelles() != null) {
				if (patrimoine.getEquipementsEchelles().getEquipementEchelle() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getEquipementsEchelles().getClass().getName(), mainClass);

					for (TypeEquipementEchelle item : patrimoine.getEquipementsEchelles().getEquipementEchelle()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getEqptechelleEqptsocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(PAT_EQPT_TUBECREPINE, fileName, idChg);
			if (patrimoine.getEquipementsTubesCrepine() != null) {
				if (patrimoine.getEquipementsTubesCrepine().getEquipementTubeCrepine() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getEquipementsTubesCrepine().getClass().getName(), mainClass);

					for (TypeEquipementTubeCrepine item : patrimoine.getEquipementsTubesCrepine().getEquipementTubeCrepine()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getEqpttubecrepineEqptsocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(PAT_EQPT_DEMARREURELECTRIQUE, fileName, idChg);
			if (patrimoine.getEquipementsDemarreursElectriques() != null) {
				if (patrimoine.getEquipementsDemarreursElectriques().getEquipementDemarreurElectrique() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getEquipementsDemarreursElectriques().getClass().getName(), mainClass);

					for (TypeEquipementDemarreurElectrique item : patrimoine.getEquipementsDemarreursElectriques().getEquipementDemarreurElectrique()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getEqptdemelecEqptsocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(PAT_EQPT_COMPTEURELECTRIQUE, fileName, idChg);
			if (patrimoine.getEquipementsCompteursElectriques() != null) {
				if (patrimoine.getEquipementsCompteursElectriques().getEquipementCompteurElectrique() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getEquipementsCompteursElectriques().getClass().getName(), mainClass);

					for (TypeEquipementCompteurElectrique item : patrimoine.getEquipementsCompteursElectriques().getEquipementCompteurElectrique()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getEqptcptelecEqptsocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(PAT_EQPT_ECHANGEURCHALEUR, fileName, idChg);
			if (patrimoine.getEquipementsEchangeursChaleur() != null) {
				if (patrimoine.getEquipementsEchangeursChaleur().getEquipementEchangeurChaleur() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getEquipementsEchangeursChaleur().getClass().getName(), mainClass);

					for (TypeEquipementEchangeurChaleur item : patrimoine.getEquipementsEchangeursChaleur().getEquipementEchangeurChaleur()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getEqptechangechalEqptsocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(PAT_EQPT_PROTECTIONINCENDIE, fileName, idChg);
			if (patrimoine.getEquipementsProtectionsIncendie() != null) {
				if (patrimoine.getEquipementsProtectionsIncendie().getEquipementProtectionIncendie() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getEquipementsProtectionsIncendie().getClass().getName(), mainClass);

					for (TypeEquipementProtectionIncendie item : patrimoine.getEquipementsProtectionsIncendie().getEquipementProtectionIncendie()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getEqptprotecincendEqptsocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(PAT_EQPT_RETOURNEMENTTERRES, fileName, idChg);
			if (patrimoine.getEquipementsRetournementsTerres() != null) {
				if (patrimoine.getEquipementsRetournementsTerres().getEquipementRetournementTerres() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getEquipementsRetournementsTerres().getClass().getName(), mainClass);

					for (TypeEquipementRetournementTerres item : patrimoine.getEquipementsRetournementsTerres().getEquipementRetournementTerres()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getEqptretterreEqptsocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(PAT_EQPT_ESPACEVERT, fileName, idChg);
			if (patrimoine.getEquipementsEspacesVert() != null) {
				if (patrimoine.getEquipementsEspacesVert().getEquipementEspaceVert() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getEquipementsEspacesVert().getClass().getName(), mainClass);

					for (TypeEquipementEspaceVert item : patrimoine.getEquipementsEspacesVert().getEquipementEspaceVert()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getEqptespvertEqptsocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(PAT_EQPT_MASSEANODIQUE, fileName, idChg);
			if (patrimoine.getEquipementsMassesAnodiques() != null) {
				if (patrimoine.getEquipementsMassesAnodiques().getEquipementMasseAnodique() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getEquipementsMassesAnodiques().getClass().getName(), mainClass);

					for (TypeEquipementMasseAnodique item : patrimoine.getEquipementsMassesAnodiques().getEquipementMasseAnodique()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getEqptmasseanodEqptsocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(PAT_EQPT_BENNE, fileName, idChg);
			if (patrimoine.getEquipementsBennes() != null) {
				if (patrimoine.getEquipementsBennes().getEquipementBenne() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getEquipementsBennes().getClass().getName(), mainClass);

					for (TypeEquipementBenne item : patrimoine.getEquipementsBennes().getEquipementBenne()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getEqptbenneEqptsocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(PAT_EQPT_DESHUMIDIFICATEUR, fileName, idChg);
			if (patrimoine.getEquipementsDeshumidificateurs() != null) {
				if (patrimoine.getEquipementsDeshumidificateurs().getEquipementDeshumidificateur() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getEquipementsDeshumidificateurs().getClass().getName(), mainClass);

					for (TypeEquipementDeshumidificateur item : patrimoine.getEquipementsDeshumidificateurs().getEquipementDeshumidificateur()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getEqptdeshumidificateurEqptsocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(PAT_EQPT_ELECTRODEREFERENCE, fileName, idChg);
			if (patrimoine.getEquipementsElectrodesReference() != null) {
				if (patrimoine.getEquipementsElectrodesReference().getEquipementElectrodeReference() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getEquipementsElectrodesReference().getClass().getName(), mainClass);

					for (TypeEquipementElectrodeReference item : patrimoine.getEquipementsElectrodesReference().getEquipementElectrodeReference()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getEqptelecrefEqptsocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(PAT_EQPT_GRAPPIN, fileName, idChg);
			if (patrimoine.getEquipementsGrappins() != null) {
				if (patrimoine.getEquipementsGrappins().getEquipementGrappin() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getEquipementsGrappins().getClass().getName(), mainClass);

					for (TypeEquipementGrappin item : patrimoine.getEquipementsGrappins().getEquipementGrappin()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getEqptgrappinEqptsocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(PAT_EQPT_VERIN, fileName, idChg);
			if (patrimoine.getEquipementsVerins() != null) {
				if (patrimoine.getEquipementsVerins().getEquipementVerin() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getEquipementsVerins().getClass().getName(), mainClass);

					for (TypeEquipementVerin item : patrimoine.getEquipementsVerins().getEquipementVerin()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getEqptverinEqptsocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(PAT_EQPT_STABILISATEURECOULEMENT, fileName, idChg);
			if (patrimoine.getEquipementsStabilisateursEcoulement() != null) {
				if (patrimoine.getEquipementsStabilisateursEcoulement().getEquipementStabilisateurEcoulement() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getEquipementsStabilisateursEcoulement().getClass().getName(), mainClass);

					for (TypeEquipementStabilisateurEcoulement item : patrimoine.getEquipementsStabilisateursEcoulement().getEquipementStabilisateurEcoulement()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getEqptstabecoulEqptsocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(PAT_EQPT_SYSTEMEDIFFUSION, fileName, idChg);
			if (patrimoine.getEquipementsSystemeDiffusion() != null) {
				if (patrimoine.getEquipementsSystemeDiffusion().getEquipementSystemeDiffusion() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getEquipementsSystemeDiffusion().getClass().getName(), mainClass);

					for (TypeEquipementSystemeDiffusion item : patrimoine.getEquipementsSystemeDiffusion().getEquipementSystemeDiffusion()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getEqptsystemediffEqptsocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(PAT_EQPT_VENTOUSE, fileName, idChg);
			if (patrimoine.getEquipementsVentouses() != null) {
				if (patrimoine.getEquipementsVentouses().getEquipementVentouse() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getEquipementsVentouses().getClass().getName(), mainClass);

					for (TypeEquipementVentouse item : patrimoine.getEquipementsVentouses().getEquipementVentouse()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getEqptventouseEqptsocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(PAT_EQPT_VIDANGE, fileName, idChg);
			if (patrimoine.getEquipementsVidanges() != null) {
				if (patrimoine.getEquipementsVidanges().getEquipementVidange() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getEquipementsVidanges().getClass().getName(), mainClass);

					for (TypeEquipementVidange item : patrimoine.getEquipementsVidanges().getEquipementVidange()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getEqptvidangeEqptsocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(PAT_EQPT_AUSCULTATIONBARRAGE, fileName, idChg);
			if (patrimoine.getEquipementsAuscultationsBarrages() != null) {
				if (patrimoine.getEquipementsAuscultationsBarrages().getEquipementAuscultationBarrage() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getEquipementsAuscultationsBarrages().getClass().getName(), mainClass);

					for (TypeEquipementAuscultationBarrage item : patrimoine.getEquipementsAuscultationsBarrages().getEquipementAuscultationBarrage()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getEqptauscultationbarEqptsocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(PAT_EQPT_MOTEURELECTRIQUEMANOEUVRE, fileName, idChg);
			if (patrimoine.getEquipementsMoteursElectriquesManoeuvres() != null) {
				if (patrimoine.getEquipementsMoteursElectriquesManoeuvres().getEquipementMoteurElectriqueManoeuvre() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getEquipementsMoteursElectriquesManoeuvres().getClass().getName(), mainClass);

					for (TypeEquipementMoteurElectriqueManoeuvre item : patrimoine.getEquipementsMoteursElectriquesManoeuvres().getEquipementMoteurElectriqueManoeuvre()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getEqptmoteurelecmanoeuvEqptsocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(PAT_EQPT_POINTPRELEVEMENT, fileName, idChg);
			if (patrimoine.getEquipementsPointsprelevement() != null) {
				if (patrimoine.getEquipementsPointsprelevement().getEquipementPointPrelevement() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getEquipementsPointsprelevement().getClass().getName(), mainClass);

					for (TypeEquipementPointPrelevement item : patrimoine.getEquipementsPointsprelevement().getEquipementPointPrelevement()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getEqptpointprelevEqptsocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(PAT_EQPT_PRELOCALISATEUR, fileName, idChg);
			if (patrimoine.getEquipementsPrelocalisateur() != null) {
				if (patrimoine.getEquipementsPrelocalisateur().getEquipementPrelocalisateur() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getEquipementsPrelocalisateur().getClass().getName(), mainClass);

					for (TypeEquipementPrelocalisateur item : patrimoine.getEquipementsPrelocalisateur().getEquipementPrelocalisateur()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getEqptprelocalisateurEqptsocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(PAT_EQPT_OUVRAGESEVACUATIONBARRAGE, fileName, idChg);
			if (patrimoine.getEquipementsOuvragesEvacuationBarrage() != null) {
				if (patrimoine.getEquipementsOuvragesEvacuationBarrage().getEquipementsOuvrageEvacuationBarrage() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getEquipementsOuvragesEvacuationBarrage().getClass().getName(), mainClass);

					for (TypeEquipementOuvragesEvacuationBarrage item : patrimoine.getEquipementsOuvragesEvacuationBarrage().getEquipementsOuvrageEvacuationBarrage()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getEqptouvevacuationEqptsocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(PAT_EQPT_REACTEUR, fileName, idChg);
			if (patrimoine.getEquipementsReacteur() != null) {
				if (patrimoine.getEquipementsReacteur().getEquipementReacteur() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getEquipementsReacteur().getClass().getName(), mainClass);

					for (TypeEquipementReacteur item : patrimoine.getEquipementsReacteur().getEquipementReacteur()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getEqptreacteurEqptsocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);
		} catch (Exception e) {
			e.printStackTrace();

			logs.add(new LogXML("ALL", fileName, "", "XML non conforme, il est impossible de l'intégrer", idChg));
		}

		for (int i = 0; i < logs.size(); i++) {
			getHibernateTemplate().saveBatch(logs.get(i), i == (logs.size() - 1));
		}
	}

	public void saveXls(DateChargement dateChargement, PatrimoineXls patrimoine, ClassDefinition mainClass, String fileName, List<LogXML> logs) {
		Integer idChg = (Integer) getHibernateTemplate().save(dateChargement);

		if (logs != null) {
			for (LogXML log : logs) {
				log.setIdChg(idChg);
			}
		}

		try {
			LogInsertXML logInsert = new LogInsertXML(CONTRAT, fileName, idChg);
			if (patrimoine.getPatContrats() != null) {
				if (patrimoine.getPatContrats().getContrats() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getPatContrats().getClass().getName(), mainClass);

					for (Contrat item : patrimoine.getPatContrats().getContrats()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getContratVeId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(SITE, fileName, idChg);
			if (patrimoine.getPatSites() != null) {
				if (patrimoine.getPatSites().getSites() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getPatSites().getClass().getName(), mainClass);

					for (Site item : patrimoine.getPatSites().getSites()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getSiteVEID());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(OUVRAGE_SOCLE, fileName, idChg);
			if (patrimoine.getPatOuvrageSocles() != null) {
				if (patrimoine.getPatOuvrageSocles().getOuvrageSocles() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getPatOuvrageSocles().getClass().getName(), mainClass);

					for (OuvrageSocle item : patrimoine.getPatOuvrageSocles().getOuvrageSocles()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getOuvragesocleVeId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(OUVRAGE_BAR, fileName, idChg);
			if (patrimoine.getPatOuvrageBars() != null) {
				if (patrimoine.getPatOuvrageBars().getOuvrageBars() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getPatOuvrageBars().getClass().getName(), mainClass);

					for (OuvrageBar item : patrimoine.getPatOuvrageBars().getOuvrageBars()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getOuvragebarOuvragesocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(OUVRAGE_USI, fileName, idChg);
			if (patrimoine.getPatOuvrageUsis() != null) {
				if (patrimoine.getPatOuvrageUsis().getOuvrageUsis() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getPatOuvrageUsis().getClass().getName(), mainClass);

					for (OuvrageUsi item : patrimoine.getPatOuvrageUsis().getOuvrageUsis()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getOuvrageusiOuvragesocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(OUVRAGE_LOG, fileName, idChg);
			if (patrimoine.getPatOuvrageLogs() != null) {
				if (patrimoine.getPatOuvrageLogs().getOuvrageLogs() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getPatOuvrageLogs().getClass().getName(), mainClass);

					for (OuvrageLog item : patrimoine.getPatOuvrageLogs().getOuvrageLogs()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getOuvragelogOuvragesocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(OUVRAGE_CAR, fileName, idChg);
			if (patrimoine.getPatOuvrageCars() != null) {
				if (patrimoine.getPatOuvrageCars().getOuvrageCars() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getPatOuvrageCars().getClass().getName(), mainClass);

					for (OuvrageCar item : patrimoine.getPatOuvrageCars().getOuvrageCars()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getOuvragecarOuvragesocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(OUVRAGE_LAG, fileName, idChg);
			if (patrimoine.getPatOuvrageLags() != null) {
				if (patrimoine.getPatOuvrageLags().getOuvrageLags() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getPatOuvrageLags().getClass().getName(), mainClass);

					for (OuvrageLag item : patrimoine.getPatOuvrageLags().getOuvrageLags()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getOuvragelagOuvragesocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(OUVRAGE_RST, fileName, idChg);
			if (patrimoine.getPatOuvrageRsts() != null) {
				if (patrimoine.getPatOuvrageRsts().getOuvrageRsts() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getPatOuvrageRsts().getClass().getName(), mainClass);

					for (OuvrageRst item : patrimoine.getPatOuvrageRsts().getOuvrageRsts()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getOuvragerstOuvragesocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(OUVRAGE_BAS, fileName, idChg);
			if (patrimoine.getPatOuvrageBass() != null) {
				if (patrimoine.getPatOuvrageBass().getOuvrageBass() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getPatOuvrageBass().getClass().getName(), mainClass);

					for (OuvrageBas item : patrimoine.getPatOuvrageBass().getOuvrageBass()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getOuvragebasOuvragesocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(OUVRAGE_STP, fileName, idChg);
			if (patrimoine.getPatOuvrageStps() != null) {
				if (patrimoine.getPatOuvrageStps().getOuvrageStps() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getPatOuvrageStps().getClass().getName(), mainClass);

					for (OuvrageStp item : patrimoine.getPatOuvrageStps().getOuvrageStps()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getOuvragestpOuvragesocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(OUVRAGE_PCA, fileName, idChg);
			if (patrimoine.getPatOuvragePcas() != null) {
				if (patrimoine.getPatOuvragePcas().getOuvragePcas() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getPatOuvragePcas().getClass().getName(), mainClass);

					for (OuvragePca item : patrimoine.getPatOuvragePcas().getOuvragePcas()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getOuvragepcaOuvragesocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(OUVRAGE_CAP, fileName, idChg);
			if (patrimoine.getPatOuvrageCaps() != null) {
				if (patrimoine.getPatOuvrageCaps().getOuvrageCaps() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getPatOuvrageCaps().getClass().getName(), mainClass);

					for (OuvrageCap item : patrimoine.getPatOuvrageCaps().getOuvrageCaps()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getOuvragecapOuvragesocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(OUVRAGE_PBA, fileName, idChg);
			if (patrimoine.getPatOuvragePbas() != null) {
				if (patrimoine.getPatOuvragePbas().getOuvragePbas() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getPatOuvragePbas().getClass().getName(), mainClass);

					for (OuvragePba item : patrimoine.getPatOuvragePbas().getOuvragePbas()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getOuvragepbaOuvragesocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(OUVRAGE_CPT, fileName, idChg);
			if (patrimoine.getPatOuvrageCpts() != null) {
				if (patrimoine.getPatOuvrageCpts().getOuvrageCpts() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getPatOuvrageCpts().getClass().getName(), mainClass);

					for (OuvrageCpt item : patrimoine.getPatOuvrageCpts().getOuvrageCpts()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getOuvragecptOuvragesocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(OUVRAGE_PRE, fileName, idChg);
			if (patrimoine.getPatOuvragePres() != null) {
				if (patrimoine.getPatOuvragePres().getOuvragePres() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getPatOuvragePres().getClass().getName(), mainClass);

					for (OuvragePre item : patrimoine.getPatOuvragePres().getOuvragePres()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getOuvragepreOuvragesocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(UNITE_SOCLE, fileName, idChg);
			if (patrimoine.getPatUniteSocles() != null) {
				if (patrimoine.getPatUniteSocles().getUniteSocles() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getPatUniteSocles().getClass().getName(), mainClass);

					for (UniteSocle item : patrimoine.getPatUniteSocles().getUniteSocles()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getUnitesocleVeId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(UNITE_CAR, fileName, idChg);
			if (patrimoine.getPatUniteCars() != null) {
				if (patrimoine.getPatUniteCars().getUniteCars() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getPatUniteCars().getClass().getName(), mainClass);

					for (UniteCar item : patrimoine.getPatUniteCars().getUniteCars()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getUnitecarUnitesocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(UNITE_CHL, fileName, idChg);
			if (patrimoine.getPatUniteChls() != null) {
				if (patrimoine.getPatUniteChls().getUniteChls() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getPatUniteChls().getClass().getName(), mainClass);

					for (UniteChl item : patrimoine.getPatUniteChls().getUniteChls()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getUnitechlUnitesocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(UNITE_CLA, fileName, idChg);
			if (patrimoine.getPatUniteClas() != null) {
				if (patrimoine.getPatUniteClas().getUniteClas() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getPatUniteClas().getClass().getName(), mainClass);

					for (UniteCla item : patrimoine.getPatUniteClas().getUniteClas()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getUniteclaUnitesocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(UNITE_CPT, fileName, idChg);
			if (patrimoine.getPatUniteCpts() != null) {
				if (patrimoine.getPatUniteCpts().getUniteCpts() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getPatUniteCpts().getClass().getName(), mainClass);

					for (UniteCpt item : patrimoine.getPatUniteCpts().getUniteCpts()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getUnitecptUnitesocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(UNITE_ELE, fileName, idChg);
			if (patrimoine.getPatUniteEles() != null) {
				if (patrimoine.getPatUniteEles().getUniteEles() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getPatUniteEles().getClass().getName(), mainClass);

					for (UniteEle item : patrimoine.getPatUniteEles().getUniteEles()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getUniteeleUnitesocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(UNITE_ESV, fileName, idChg);
			if (patrimoine.getPatUniteEsvs() != null) {
				if (patrimoine.getPatUniteEsvs().getUniteEsvs() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getPatUniteEsvs().getClass().getName(), mainClass);

					for (UniteEsv item : patrimoine.getPatUniteEsvs().getUniteEsvs()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getUniteesvUnitesocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(UNITE_FIL, fileName, idChg);
			if (patrimoine.getPatUniteFils() != null) {
				if (patrimoine.getPatUniteFils().getUniteFils() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getPatUniteFils().getClass().getName(), mainClass);

					for (UniteFil item : patrimoine.getPatUniteFils().getUniteFils()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getUnitefilUnitesocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(UNITE_GCI, fileName, idChg);
			if (patrimoine.getPatUniteGcis() != null) {
				if (patrimoine.getPatUniteGcis().getUniteGcis() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getPatUniteGcis().getClass().getName(), mainClass);

					for (UniteGci item : patrimoine.getPatUniteGcis().getUniteGcis()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getUnitegciUnitesocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(UNITE_HYD, fileName, idChg);
			if (patrimoine.getPatUniteHyds() != null) {
				if (patrimoine.getPatUniteHyds().getUniteHyds() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getPatUniteHyds().getClass().getName(), mainClass);

					for (UniteHyd item : patrimoine.getPatUniteHyds().getUniteHyds()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getUnitehydUnitesocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(UNITE_MIN, fileName, idChg);
			if (patrimoine.getPatUniteMins() != null) {
				if (patrimoine.getPatUniteMins().getUniteMins() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getPatUniteMins().getClass().getName(), mainClass);

					for (UniteMin item : patrimoine.getPatUniteMins().getUniteMins()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getUniteminUnitesocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(UNITE_OXY, fileName, idChg);
			if (patrimoine.getPatUniteOxys() != null) {
				if (patrimoine.getPatUniteOxys().getUniteOxys() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getPatUniteOxys().getClass().getName(), mainClass);

					for (UniteOxy item : patrimoine.getPatUniteOxys().getUniteOxys()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getUniteoxyUnitesocle_id());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(UNITE_POM, fileName, idChg);
			if (patrimoine.getPatUnitePoms() != null) {
				if (patrimoine.getPatUnitePoms().getUnitePoms() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getPatUnitePoms().getClass().getName(), mainClass);

					for (UnitePom item : patrimoine.getPatUnitePoms().getUnitePoms()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getUnitepomUnitesocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(UNITE_REC, fileName, idChg);
			if (patrimoine.getPatUniteRecs() != null) {
				if (patrimoine.getPatUniteRecs().getUniteRecs() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getPatUniteRecs().getClass().getName(), mainClass);

					for (UniteRec item : patrimoine.getPatUniteRecs().getUniteRecs()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getUniterecUnitesocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(UNITE_REG, fileName, idChg);
			if (patrimoine.getPatUniteRegs() != null) {
				if (patrimoine.getPatUniteRegs().getUniteRegs() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getPatUniteRegs().getClass().getName(), mainClass);

					for (UniteReg item : patrimoine.getPatUniteRegs().getUniteRegs()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getUniteregUnitesocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(UNITE_SEC, fileName, idChg);
			if (patrimoine.getPatUniteSecs() != null) {
				if (patrimoine.getPatUniteSecs().getUniteSecs() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getPatUniteSecs().getClass().getName(), mainClass);

					for (UniteSec item : patrimoine.getPatUniteSecs().getUniteSecs()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getUnitesecUnitesocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(UNITE_SET, fileName, idChg);
			if (patrimoine.getPatUniteSets() != null) {
				if (patrimoine.getPatUniteSets().getUniteSets() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getPatUniteSets().getClass().getName(), mainClass);

					for (UniteSet item : patrimoine.getPatUniteSets().getUniteSets()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getUnitesetUnitesocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(UNITE_SPE, fileName, idChg);
			if (patrimoine.getPatUniteSpes() != null) {
				if (patrimoine.getPatUniteSpes().getUniteSpes() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getPatUniteSpes().getClass().getName(), mainClass);

					for (UniteSpe item : patrimoine.getPatUniteSpes().getUniteSpes()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getUnitespeUnitesocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(UNITE_TLG, fileName, idChg);
			if (patrimoine.getPatUniteTlgs() != null) {
				if (patrimoine.getPatUniteTlgs().getUniteTlgs() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getPatUniteTlgs().getClass().getName(), mainClass);

					for (UniteTlg item : patrimoine.getPatUniteTlgs().getUniteTlgs()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getUnitetlgUnitesocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(UNITE_TRT, fileName, idChg);
			if (patrimoine.getPatUniteTrts() != null) {
				if (patrimoine.getPatUniteTrts().getUniteTrts() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getPatUniteTrts().getClass().getName(), mainClass);

					for (UniteTrt item : patrimoine.getPatUniteTrts().getUniteTrts()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getUnitetrtUnitesocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);

			logInsert = new LogInsertXML(UNITE_ULT, fileName, idChg);
			if (patrimoine.getPatUniteUlts() != null) {
				if (patrimoine.getPatUniteUlts().getUniteUlts() != null) {
					ClassDefinition classDef = ReflectionHelper.getSelectedClassDefinition(patrimoine.getPatUniteUlts().getClass().getName(), mainClass);

					for (UniteUlt item : patrimoine.getPatUniteUlts().getUniteUlts()) {
						item.setIdChg(idChg);
						saveObject(item, classDef, logInsert, logs, item.getUniteultUnitesocleId());
					}
				}
			}
			getHibernateTemplate().save(logInsert);
		} catch (Exception e) {
			e.printStackTrace();

			logs.add(new LogXML("ALL", fileName, "", "XML non conforme, il est impossible de l'intégrer", idChg));
		}

		for (int i = 0; i < logs.size(); i++) {
			getHibernateTemplate().saveBatch(logs.get(i), i == (logs.size() - 1));
		}
	}

	private void saveObject(Object item, ClassDefinition classDefinition, LogInsertXML logInsert, List<LogXML> logs, String lineId) {
		logInsert.incrementIn();
		try {
			ReflectionHelper.testRules(classDefinition, item);

			getHibernateTemplate().save(item);
			logInsert.incrementOut();
		} catch (Exception e) {
			manageException(logs, logInsert.getTableName(), logInsert.getContractName(), lineId, logInsert.getIdChg(), e);
			logInsert.incrementError();
		}
	}

	private void manageException(List<LogXML> logs, String tableName, String fileName, String lineId, Integer idChg, Exception e) {
		String message = "";
		if (e.getCause() != null && e.getCause().getCause() != null && e.getCause().getCause() instanceof IllegalArgumentException) {
			IllegalArgumentException ex = (IllegalArgumentException) e.getCause().getCause();
			e = ex;

			message = e.getMessage();
		}
		else if (e.getCause() != null) {
			message = e.getCause().getMessage();
		}
		else {
			message = e.getMessage();
		}

		e.printStackTrace();
		logs.add(new LogXML(tableName, fileName, lineId, message, idChg));
	}

	public Patrimoine getPatrimoine(String beginDate, String endDate, String query) {
		return buildPatrimoineWithParam(query);
	}

	@SuppressWarnings("unchecked")
	public Patrimoine buildPatrimoineWithParam(String contrat) {
		Patrimoine patrimoine = new Patrimoine();

		// -----------------------------------------------------//
		// patrimoine_eqpt_socle
		// -----------------------------------------------------//

		StringBuffer buf = new StringBuffer();
		buf.append("SELECT e.* ");
		buf.append(" FROM patrimoine_contrat c, patrimoine_site s, patrimoine_ouvrage_socle o, patrimoine_unite_socle u, patrimoine_eqpt_socle e ");
		buf.append("WHERE c.contrat_id = s.site_contrat_id_dwh ");
		buf.append("AND s.site_id = o.ouvragesocle_site_id_dwh ");
		buf.append("AND o.ouvragesocle_id = u.unitesocle_ouvragesocle_id_dwh ");
		buf.append("AND u.unitesocle_id = e.eqptsocle_idunite_dwh ");
		buf.append("AND c.contrat_ve_id LIKE '" + contrat + "' ");
		buf.append("AND e.eqptsocle_id IN  ");
		buf.append("	( ");
		buf.append("	SELECT t1.eqptsocle_id   ");
		buf.append("	FROM patrimoine_eqpt_socle t1 ");
		buf.append("	WHERE (t1.eqptsocle_ve_id, t1.id_chg) IN ");
		buf.append("		( ");
		buf.append("		SELECT t.eqptsocle_ve_id, MAX(t.id_chg) as id_chg  ");
		buf.append("		FROM patrimoine_eqpt_socle t ");
		buf.append("		GROUP BY t.eqptsocle_ve_id ");
		buf.append("		) ");
		buf.append("	)  ");

		Session session = getHibernateTemplate().getCurrentSession();
		List<TypeEquipementSocle> equipementSocles = session.createSQLQuery(buf.toString()).addEntity(TypeEquipementSocle.class).list();
		session.close();
		System.out.println("Insert " + equipementSocles.size() + " lines of patrimoine_eqpt_socle");

		EquipementsSocle equipementSocle = new EquipementsSocle();
		equipementSocle.setEquipementSocle(equipementSocles);
		patrimoine.setEquipementsSocle(equipementSocle);

		// -----------------------------------------------------//
		// patrimoine_eqpt_aerotherme
		// -----------------------------------------------------//

		buf = new StringBuffer();
		buf.append("SELECT es.* ");
		buf.append(" FROM patrimoine_contrat c, patrimoine_site s, patrimoine_ouvrage_socle o, patrimoine_unite_socle u, patrimoine_eqpt_socle e, patrimoine_eqpt_aerotherme es ");
		buf.append("WHERE c.contrat_id = s.site_contrat_id_dwh ");
		buf.append("AND s.site_id = o.ouvragesocle_site_id_dwh ");
		buf.append("AND o.ouvragesocle_id = u.unitesocle_ouvragesocle_id_dwh ");
		buf.append("AND u.unitesocle_id = e.eqptsocle_idunite_dwh ");
		buf.append("AND e.eqptsocle_id = es.eqptaerotherme_eqptsocle_id_dwh ");
		buf.append("AND c.contrat_ve_id LIKE '" + contrat + "' ");
		buf.append("AND es.eqptaerotherme_id IN  ");
		buf.append("	( ");
		buf.append("	SELECT t1.eqptaerotherme_id   ");
		buf.append("	FROM patrimoine_eqpt_aerotherme t1 ");
		buf.append("	WHERE (t1.eqptaerotherme_eqptsocle_id, t1.id_chg) IN ");
		buf.append("		( ");
		buf.append("		SELECT t.eqptaerotherme_eqptsocle_id, MAX(t.id_chg) as id_chg  ");
		buf.append("		FROM patrimoine_eqpt_aerotherme t ");
		buf.append("		GROUP BY t.eqptaerotherme_eqptsocle_id ");
		buf.append("		) ");
		buf.append("	)  ");

		session = getHibernateTemplate().getCurrentSession();
		List<TypeEquipementAerotherme> equipementAerothermes = session.createSQLQuery(buf.toString()).addEntity(TypeEquipementAerotherme.class).list();
		session.close();
		System.out.println("Insert " + equipementAerothermes.size() + " lines of patrimoine_eqpt_aerotherme");

		EquipementsAerothermes equipementAerotherme = new EquipementsAerothermes();
		equipementAerotherme.setEquipementAerotherme(equipementAerothermes);
		patrimoine.setEquipementsAerothermes(equipementAerotherme);

		// -----------------------------------------------------//
		// patrimoine_eqpt_agitateur
		// -----------------------------------------------------//

		buf = new StringBuffer();
		buf.append("SELECT es.* ");
		buf.append(" FROM patrimoine_contrat c, patrimoine_site s, patrimoine_ouvrage_socle o, patrimoine_unite_socle u, patrimoine_eqpt_socle e, patrimoine_eqpt_agitateur es ");
		buf.append("WHERE c.contrat_id = s.site_contrat_id_dwh ");
		buf.append("AND s.site_id = o.ouvragesocle_site_id_dwh ");
		buf.append("AND o.ouvragesocle_id = u.unitesocle_ouvragesocle_id_dwh ");
		buf.append("AND u.unitesocle_id = e.eqptsocle_idunite_dwh ");
		buf.append("AND e.eqptsocle_id = es.eqptagitateur_eqptsocle_id_dwh ");
		buf.append("AND c.contrat_ve_id LIKE '" + contrat + "' ");
		buf.append("AND es.eqptagitateur_id IN  ");
		buf.append("	( ");
		buf.append("	SELECT t1.eqptagitateur_id   ");
		buf.append("	FROM patrimoine_eqpt_agitateur t1 ");
		buf.append("	WHERE (t1.eqptagitateur_eqptsocle_id, t1.id_chg) IN ");
		buf.append("		( ");
		buf.append("		SELECT t.eqptagitateur_eqptsocle_id, MAX(t.id_chg) as id_chg  ");
		buf.append("		FROM patrimoine_eqpt_agitateur t ");
		buf.append("		GROUP BY t.eqptagitateur_eqptsocle_id ");
		buf.append("		) ");
		buf.append("	)  ");

		session = getHibernateTemplate().getCurrentSession();
		List<TypeEquipementAgitateur> equipementAgitateurs = session.createSQLQuery(buf.toString()).addEntity(TypeEquipementAgitateur.class).list();
		session.close();
		System.out.println("Insert " + equipementAgitateurs.size() + " lines of patrimoine_eqpt_agitateur");

		EquipementsAgitateurs equipementAgitateur = new EquipementsAgitateurs();
		equipementAgitateur.setEquipementAgitateur(equipementAgitateurs);
		patrimoine.setEquipementsAgitateurs(equipementAgitateur);

		// -----------------------------------------------------//
		// patrimoine_eqpt_applevage
		// -----------------------------------------------------//

		buf = new StringBuffer();
		buf.append("SELECT es.* ");
		buf.append(" FROM patrimoine_contrat c, patrimoine_site s, patrimoine_ouvrage_socle o, patrimoine_unite_socle u, patrimoine_eqpt_socle e, patrimoine_eqpt_applevage es ");
		buf.append("WHERE c.contrat_id = s.site_contrat_id_dwh ");
		buf.append("AND s.site_id = o.ouvragesocle_site_id_dwh ");
		buf.append("AND o.ouvragesocle_id = u.unitesocle_ouvragesocle_id_dwh ");
		buf.append("AND u.unitesocle_id = e.eqptsocle_idunite_dwh ");
		buf.append("AND e.eqptsocle_id = es.eqptapplevage_eqptsocle_id_dwh ");
		buf.append("AND c.contrat_ve_id LIKE '" + contrat + "' ");
		buf.append("AND es.eqptapplevage_id IN  ");
		buf.append("	( ");
		buf.append("	SELECT t1.eqptapplevage_id   ");
		buf.append("	FROM patrimoine_eqpt_applevage t1 ");
		buf.append("	WHERE (t1.eqptapplevage_eqptsocle_id, t1.id_chg) IN ");
		buf.append("		( ");
		buf.append("		SELECT t.eqptapplevage_eqptsocle_id, MAX(t.id_chg) as id_chg  ");
		buf.append("		FROM patrimoine_eqpt_applevage t ");
		buf.append("		GROUP BY t.eqptapplevage_eqptsocle_id ");
		buf.append("		)  ");
		buf.append("	)  ");

		session = getHibernateTemplate().getCurrentSession();
		List<TypeEquipementAppareilLevage> equipementAppareilLevages = session.createSQLQuery(buf.toString()).addEntity(TypeEquipementAppareilLevage.class).list();
		session.close();
		System.out.println("Insert " + equipementAppareilLevages.size() + " lines of patrimoine_eqpt_applevage");

		EquipementsAppareilsLevage equipementAppareilLevage = new EquipementsAppareilsLevage();
		equipementAppareilLevage.setEquipementAppareilLevage(equipementAppareilLevages);
		patrimoine.setEquipementsAppareilsLevage(equipementAppareilLevage);

		// -----------------------------------------------------//
		// patrimoine_eqpt_armoireelec
		// -----------------------------------------------------//

		buf = new StringBuffer();
		buf.append("SELECT es.* ");
		buf.append(" FROM patrimoine_contrat c, patrimoine_site s, patrimoine_ouvrage_socle o, patrimoine_unite_socle u, patrimoine_eqpt_socle e, patrimoine_eqpt_armoireelec es ");
		buf.append("WHERE c.contrat_id = s.site_contrat_id_dwh ");
		buf.append("AND s.site_id = o.ouvragesocle_site_id_dwh ");
		buf.append("AND o.ouvragesocle_id = u.unitesocle_ouvragesocle_id_dwh ");
		buf.append("AND u.unitesocle_id = e.eqptsocle_idunite_dwh ");
		buf.append("AND e.eqptsocle_id = es.eqptarmoireelec_eqptsocle_id_dwh ");
		buf.append("AND c.contrat_ve_id LIKE '" + contrat + "' ");
		buf.append("AND es.eqptarmoireelec_id IN  ");
		buf.append("	( ");
		buf.append("	SELECT t1.eqptarmoireelec_id   ");
		buf.append("	FROM patrimoine_eqpt_armoireelec t1 ");
		buf.append("	WHERE (t1.eqptarmoireelec_eqptsocle_id, t1.id_chg) IN ");
		buf.append("		( ");
		buf.append("		SELECT t.eqptarmoireelec_eqptsocle_id, MAX(t.id_chg) as id_chg  ");
		buf.append("		FROM patrimoine_eqpt_armoireelec t ");
		buf.append("		GROUP BY t.eqptarmoireelec_eqptsocle_id ");
		buf.append("		) ");
		buf.append("	)  ");

		session = getHibernateTemplate().getCurrentSession();
		List<TypeEquipementArmoireElectrique> equipementArmoireElectriques = session.createSQLQuery(buf.toString()).addEntity(TypeEquipementArmoireElectrique.class).list();
		session.close();
		System.out.println("Insert " + equipementArmoireElectriques.size() + " lines of patrimoine_eqpt_armoireelec");

		EquipementsArmoiresElectriques equipementArmoireElectrique = new EquipementsArmoiresElectriques();
		equipementArmoireElectrique.setEquipementArmoireElectrique(equipementArmoireElectriques);
		patrimoine.setEquipementsArmoiresElectriques(equipementArmoireElectrique);

		// -----------------------------------------------------//
		// patrimoine_eqpt_aspiboue
		// -----------------------------------------------------//

		buf = new StringBuffer();
		buf.append("SELECT es.* ");
		buf.append(" FROM patrimoine_contrat c, patrimoine_site s, patrimoine_ouvrage_socle o, patrimoine_unite_socle u, patrimoine_eqpt_socle e, patrimoine_eqpt_aspiboue es ");
		buf.append("WHERE c.contrat_id = s.site_contrat_id_dwh ");
		buf.append("AND s.site_id = o.ouvragesocle_site_id_dwh ");
		buf.append("AND o.ouvragesocle_id = u.unitesocle_ouvragesocle_id_dwh ");
		buf.append("AND u.unitesocle_id = e.eqptsocle_idunite_dwh ");
		buf.append("AND e.eqptsocle_id = es.eqptaspiboue_eqptsocle_id_dwh ");
		buf.append("AND c.contrat_ve_id LIKE '" + contrat + "' ");
		buf.append("AND es.eqptaspiboue_id IN  ");
		buf.append("	( ");
		buf.append("	SELECT t1.eqptaspiboue_id   ");
		buf.append("	FROM patrimoine_eqpt_aspiboue t1 ");
		buf.append("	WHERE (t1.eqptaspiboue_eqptsocle_id, t1.id_chg) IN ");
		buf.append("		( ");
		buf.append("		SELECT t.eqptaspiboue_eqptsocle_id, MAX(t.id_chg) as id_chg  ");
		buf.append("		FROM patrimoine_eqpt_aspiboue t ");
		buf.append("		GROUP BY t.eqptaspiboue_eqptsocle_id ");
		buf.append("		) ");
		buf.append("	)  ");

		session = getHibernateTemplate().getCurrentSession();
		List<TypeEquipementAspirateurBoues> equipementAspirateurBouess = session.createSQLQuery(buf.toString()).addEntity(TypeEquipementAspirateurBoues.class).list();
		session.close();
		System.out.println("Insert " + equipementAspirateurBouess.size() + " lines of patrimoine_eqpt_aspiboue");

		EquipementsAspirateursBoues equipementAspirateurBoues = new EquipementsAspirateursBoues();
		equipementAspirateurBoues.setEquipementAspirateurBoues(equipementAspirateurBouess);
		patrimoine.setEquipementsAspirateursBoues(equipementAspirateurBoues);

		// -----------------------------------------------------//
		// patrimoine_eqpt_automate
		// -----------------------------------------------------//

		buf = new StringBuffer();
		buf.append("SELECT es.* ");
		buf.append(" FROM patrimoine_contrat c, patrimoine_site s, patrimoine_ouvrage_socle o, patrimoine_unite_socle u, patrimoine_eqpt_socle e, patrimoine_eqpt_automate es ");
		buf.append("WHERE c.contrat_id = s.site_contrat_id_dwh ");
		buf.append("AND s.site_id = o.ouvragesocle_site_id_dwh ");
		buf.append("AND o.ouvragesocle_id = u.unitesocle_ouvragesocle_id_dwh ");
		buf.append("AND u.unitesocle_id = e.eqptsocle_idunite_dwh ");
		buf.append("AND e.eqptsocle_id = es.eqptautomprogindus_eqptsocle_id_dwh ");
		buf.append("AND c.contrat_ve_id LIKE '" + contrat + "' ");
		buf.append("AND es.eqptautomprogindus_id IN  ");
		buf.append("	( ");
		buf.append("	SELECT t1.eqptautomprogindus_id   ");
		buf.append("	FROM patrimoine_eqpt_automate t1 ");
		buf.append("	WHERE (t1.eqptautomprogindus_eqptsocle_id, t1.id_chg) IN ");
		buf.append("		( ");
		buf.append("		SELECT t.eqptautomprogindus_eqptsocle_id, MAX(t.id_chg) as id_chg  ");
		buf.append("		FROM patrimoine_eqpt_automate t ");
		buf.append("		GROUP BY t.eqptautomprogindus_eqptsocle_id ");
		buf.append("		) ");
		buf.append("	)  ");

		session = getHibernateTemplate().getCurrentSession();
		List<TypeEquipementAutomate> equipementAutomates = session.createSQLQuery(buf.toString()).addEntity(TypeEquipementAutomate.class).list();
		session.close();
		System.out.println("Insert " + equipementAutomates.size() + " lines of patrimoine_eqpt_automate");

		EquipementsAutomates equipementAutomate = new EquipementsAutomates();
		equipementAutomate.setEquipementAutomate(equipementAutomates);
		patrimoine.setEquipementsAutomates(equipementAutomate);

		// -----------------------------------------------------//
		// patrimoine_eqpt_bacretention
		// -----------------------------------------------------//

		buf = new StringBuffer();
		buf.append("SELECT es.* ");
		buf.append(" FROM patrimoine_contrat c, patrimoine_site s, patrimoine_ouvrage_socle o, patrimoine_unite_socle u, patrimoine_eqpt_socle e, patrimoine_eqpt_bacretention es ");
		buf.append("WHERE c.contrat_id = s.site_contrat_id_dwh ");
		buf.append("AND s.site_id = o.ouvragesocle_site_id_dwh ");
		buf.append("AND o.ouvragesocle_id = u.unitesocle_ouvragesocle_id_dwh ");
		buf.append("AND u.unitesocle_id = e.eqptsocle_idunite_dwh ");
		buf.append("AND e.eqptsocle_id = es.eqptbac_eqptsocle_id_dwh ");
		buf.append("AND c.contrat_ve_id LIKE '" + contrat + "' ");
		buf.append("AND es.eqptbac_id IN  ");
		buf.append("	( ");
		buf.append("	SELECT t1.eqptbac_id   ");
		buf.append("	FROM patrimoine_eqpt_bacretention t1 ");
		buf.append("	WHERE (t1.eqptbac_eqptsocle_id, t1.id_chg) IN ");
		buf.append("		( ");
		buf.append("		SELECT t.eqptbac_eqptsocle_id, MAX(t.id_chg) as id_chg  ");
		buf.append("		FROM patrimoine_eqpt_bacretention t ");
		buf.append("		GROUP BY t.eqptbac_eqptsocle_id ");
		buf.append("		)  ");
		buf.append("	)  ");

		session = getHibernateTemplate().getCurrentSession();
		List<TypeEquipementBacRetention> equipementBacRetentions = session.createSQLQuery(buf.toString()).addEntity(TypeEquipementBacRetention.class).list();
		session.close();
		System.out.println("Insert " + equipementBacRetentions.size() + " lines of patrimoine_eqpt_bacretention");

		EquipementsBacsRetention equipementBacRetention = new EquipementsBacsRetention();
		equipementBacRetention.setEquipementBacRetention(equipementBacRetentions);
		patrimoine.setEquipementsBacsRetention(equipementBacRetention);

		// -----------------------------------------------------//
		// patrimoine_eqpt_ballonregulation
		// -----------------------------------------------------//

		buf = new StringBuffer();
		buf.append("SELECT es.* ");
		buf.append(" FROM patrimoine_contrat c, patrimoine_site s, patrimoine_ouvrage_socle o, patrimoine_unite_socle u, patrimoine_eqpt_socle e, patrimoine_eqpt_ballonregulation es ");
		buf.append("WHERE c.contrat_id = s.site_contrat_id_dwh ");
		buf.append("AND s.site_id = o.ouvragesocle_site_id_dwh ");
		buf.append("AND o.ouvragesocle_id = u.unitesocle_ouvragesocle_id_dwh ");
		buf.append("AND u.unitesocle_id = e.eqptsocle_idunite_dwh ");
		buf.append("AND e.eqptsocle_id = es.eqptballonregul_eqptsocle_id_dwh ");
		buf.append("AND c.contrat_ve_id LIKE '" + contrat + "' ");
		buf.append("AND es.eqptballonregul_id IN  ");
		buf.append("	( ");
		buf.append("	SELECT t1.eqptballonregul_id   ");
		buf.append("	FROM patrimoine_eqpt_ballonregulation t1 ");
		buf.append("	WHERE (t1.eqptballonregul_eqptsocle_id, t1.id_chg) IN ");
		buf.append("		( ");
		buf.append("		SELECT t.eqptballonregul_eqptsocle_id, MAX(t.id_chg) as id_chg  ");
		buf.append("		FROM patrimoine_eqpt_ballonregulation t ");
		buf.append("		GROUP BY t.eqptballonregul_eqptsocle_id ");
		buf.append("		)  ");
		buf.append("	)  ");

		session = getHibernateTemplate().getCurrentSession();
		List<TypeEquipementBallon> equipementBallonRegulations = session.createSQLQuery(buf.toString()).addEntity(TypeEquipementBallon.class).list();
		session.close();
		System.out.println("Insert " + equipementBallonRegulations.size() + " lines of patrimoine_eqpt_ballonregulation");

		EquipementsBallons equipementBallonRegulation = new EquipementsBallons();
		equipementBallonRegulation.setEquipementBallon(equipementBallonRegulations);
		patrimoine.setEquipementsBallons(equipementBallonRegulation);

		// -----------------------------------------------------//
		// patrimoine_eqpt_benne
		// -----------------------------------------------------//

		buf = new StringBuffer();
		buf.append("SELECT es.* ");
		buf.append(" FROM patrimoine_contrat c, patrimoine_site s, patrimoine_ouvrage_socle o, patrimoine_unite_socle u, patrimoine_eqpt_socle e, patrimoine_eqpt_benne es ");
		buf.append("WHERE c.contrat_id = s.site_contrat_id_dwh ");
		buf.append("AND s.site_id = o.ouvragesocle_site_id_dwh ");
		buf.append("AND o.ouvragesocle_id = u.unitesocle_ouvragesocle_id_dwh ");
		buf.append("AND u.unitesocle_id = e.eqptsocle_idunite_dwh ");
		buf.append("AND e.eqptsocle_id = es.eqptbenne_eqptsocle_id_dwh ");
		buf.append("AND c.contrat_ve_id LIKE '" + contrat + "' ");
		buf.append("AND es.eqptbenne_id IN  ");
		buf.append("	( ");
		buf.append("	SELECT t1.eqptbenne_id   ");
		buf.append("	FROM patrimoine_eqpt_benne t1 ");
		buf.append("	WHERE (t1.eqptbenne_eqptsocle_id, t1.id_chg) IN ");
		buf.append("		( ");
		buf.append("		SELECT t.eqptbenne_eqptsocle_id, MAX(t.id_chg) as id_chg  ");
		buf.append("		FROM patrimoine_eqpt_benne t ");
		buf.append("		GROUP BY t.eqptbenne_eqptsocle_id ");
		buf.append("		)  ");
		buf.append("	)  ");

		session = getHibernateTemplate().getCurrentSession();
		List<TypeEquipementBenne> equipementBennes = session.createSQLQuery(buf.toString()).addEntity(TypeEquipementBenne.class).list();
		session.close();
		System.out.println("Insert " + equipementBennes.size() + " lines of patrimoine_eqpt_benne");

		EquipementsBennes equipementBenne = new EquipementsBennes();
		equipementBenne.setEquipementBenne(equipementBennes);
		patrimoine.setEquipementsBennes(equipementBenne);

		// -----------------------------------------------------//
		// patrimoine_eqpt_boiteboue
		// -----------------------------------------------------//

		buf = new StringBuffer();
		buf.append("SELECT es.* ");
		buf.append(" FROM patrimoine_contrat c, patrimoine_site s, patrimoine_ouvrage_socle o, patrimoine_unite_socle u, patrimoine_eqpt_socle e, patrimoine_eqpt_boiteboue es ");
		buf.append("WHERE c.contrat_id = s.site_contrat_id_dwh ");
		buf.append("AND s.site_id = o.ouvragesocle_site_id_dwh ");
		buf.append("AND o.ouvragesocle_id = u.unitesocle_ouvragesocle_id_dwh ");
		buf.append("AND u.unitesocle_id = e.eqptsocle_idunite_dwh ");
		buf.append("AND e.eqptsocle_id = es.eqptboiteboue_eqptsocle_id_dwh ");
		buf.append("AND c.contrat_ve_id LIKE '" + contrat + "' ");
		buf.append("AND es.eqptboiteboue_id IN  ");
		buf.append("	( ");
		buf.append("	SELECT t1.eqptboiteboue_id   ");
		buf.append("	FROM patrimoine_eqpt_boiteboue t1 ");
		buf.append("	WHERE (t1.eqptboiteboue_eqptsocle_id, t1.id_chg) IN ");
		buf.append("		( ");
		buf.append("		SELECT t.eqptboiteboue_eqptsocle_id, MAX(t.id_chg) as id_chg  ");
		buf.append("		FROM patrimoine_eqpt_boiteboue t ");
		buf.append("		GROUP BY t.eqptboiteboue_eqptsocle_id ");
		buf.append("		)  ");
		buf.append("	)  ");

		session = getHibernateTemplate().getCurrentSession();
		List<TypeEquipementBoiteBoue> equipementBoiteBoues = session.createSQLQuery(buf.toString()).addEntity(TypeEquipementBoiteBoue.class).list();
		session.close();
		System.out.println("Insert " + equipementBoiteBoues.size() + " lines of patrimoine_eqpt_boiteboue");

		EquipementsBoitesBoue equipementBoiteBoue = new EquipementsBoitesBoue();
		equipementBoiteBoue.setEquipementBoiteBoue(equipementBoiteBoues);
		patrimoine.setEquipementsBoitesBoue(equipementBoiteBoue);

		// -----------------------------------------------------//
		// patrimoine_eqpt_bouteillechlore
		// -----------------------------------------------------//

		buf = new StringBuffer();
		buf.append("SELECT es.* ");
		buf.append(" FROM patrimoine_contrat c, patrimoine_site s, patrimoine_ouvrage_socle o, patrimoine_unite_socle u, patrimoine_eqpt_socle e, patrimoine_eqpt_bouteillechlore es ");
		buf.append("WHERE c.contrat_id = s.site_contrat_id_dwh ");
		buf.append("AND s.site_id = o.ouvragesocle_site_id_dwh ");
		buf.append("AND o.ouvragesocle_id = u.unitesocle_ouvragesocle_id_dwh ");
		buf.append("AND u.unitesocle_id = e.eqptsocle_idunite_dwh ");
		buf.append("AND e.eqptsocle_id = es.eqptbouttankchlore_eqptsocle_id_dwh ");
		buf.append("AND c.contrat_ve_id LIKE '" + contrat + "' ");
		buf.append("AND es.eqptbouttankchlore_id IN  ");
		buf.append("	( ");
		buf.append("	SELECT t1.eqptbouttankchlore_id   ");
		buf.append("	FROM patrimoine_eqpt_bouteillechlore t1 ");
		buf.append("	WHERE (t1.eqptbouttankchlore_eqptsocle_id, t1.id_chg) IN ");
		buf.append("		( ");
		buf.append("		SELECT t.eqptbouttankchlore_eqptsocle_id, MAX(t.id_chg) as id_chg  ");
		buf.append("		FROM patrimoine_eqpt_bouteillechlore t ");
		buf.append("		GROUP BY t.eqptbouttankchlore_eqptsocle_id ");
		buf.append("		)  ");
		buf.append("	)  ");

		session = getHibernateTemplate().getCurrentSession();
		List<TypeEquipementBouteilleChlore> equipementBouteilleChlores = session.createSQLQuery(buf.toString()).addEntity(TypeEquipementBouteilleChlore.class).list();
		session.close();
		System.out.println("Insert " + equipementBouteilleChlores.size() + " lines of patrimoine_eqpt_bouteillechlore");

		EquipementsBouteillesChlore equipementBouteilleChlore = new EquipementsBouteillesChlore();
		equipementBouteilleChlore.setEquipementBouteilleChlore(equipementBouteilleChlores);
		patrimoine.setEquipementsBouteillesChlore(equipementBouteilleChlore);

		// -----------------------------------------------------//
		// patrimoine_eqpt_buselure
		// -----------------------------------------------------//

		buf = new StringBuffer();
		buf.append("SELECT es.* ");
		buf.append(" FROM patrimoine_contrat c, patrimoine_site s, patrimoine_ouvrage_socle o, patrimoine_unite_socle u, patrimoine_eqpt_socle e, patrimoine_eqpt_buselure es ");
		buf.append("WHERE c.contrat_id = s.site_contrat_id_dwh ");
		buf.append("AND s.site_id = o.ouvragesocle_site_id_dwh ");
		buf.append("AND o.ouvragesocle_id = u.unitesocle_ouvragesocle_id_dwh ");
		buf.append("AND u.unitesocle_id = e.eqptsocle_idunite_dwh ");
		buf.append("AND e.eqptsocle_id = es.eqptbuselure_eqptsocle_id_dwh ");
		buf.append("AND c.contrat_ve_id LIKE '" + contrat + "' ");
		buf.append("AND es.eqptbuselure_id IN  ");
		buf.append("	( ");
		buf.append("	SELECT t1.eqptbuselure_id   ");
		buf.append("	FROM patrimoine_eqpt_buselure t1 ");
		buf.append("	WHERE (t1.eqptbuselure_eqptsocle_id, t1.id_chg) IN ");
		buf.append("		( ");
		buf.append("		SELECT t.eqptbuselure_eqptsocle_id, MAX(t.id_chg) as id_chg  ");
		buf.append("		FROM patrimoine_eqpt_buselure t ");
		buf.append("		GROUP BY t.eqptbuselure_eqptsocle_id ");
		buf.append("		)  ");
		buf.append("	)  ");

		session = getHibernateTemplate().getCurrentSession();
		List<TypeEquipementBuselure> equipementBuselures = session.createSQLQuery(buf.toString()).addEntity(TypeEquipementBuselure.class).list();
		session.close();
		System.out.println("Insert " + equipementBuselures.size() + " lines of patrimoine_eqpt_buselure");

		EquipementsBuselures equipementBuselure = new EquipementsBuselures();
		equipementBuselure.setEquipementBuselure(equipementBuselures);
		patrimoine.setEquipementsBuselures(equipementBuselure);

		// -----------------------------------------------------//
		// patrimoine_eqpt_caillebotis
		// -----------------------------------------------------//

		buf = new StringBuffer();
		buf.append("SELECT es.* ");
		buf.append(" FROM patrimoine_contrat c, patrimoine_site s, patrimoine_ouvrage_socle o, patrimoine_unite_socle u, patrimoine_eqpt_socle e, patrimoine_eqpt_caillebotis es ");
		buf.append("WHERE c.contrat_id = s.site_contrat_id_dwh ");
		buf.append("AND s.site_id = o.ouvragesocle_site_id_dwh ");
		buf.append("AND o.ouvragesocle_id = u.unitesocle_ouvragesocle_id_dwh ");
		buf.append("AND u.unitesocle_id = e.eqptsocle_idunite_dwh ");
		buf.append("AND e.eqptsocle_id = es.eqptcaillebotis_eqptsocle_id_dwh ");
		buf.append("AND c.contrat_ve_id LIKE '" + contrat + "' ");
		buf.append("AND es.eqptcaillebotis_id IN  ");
		buf.append("	( ");
		buf.append("	SELECT t1.eqptcaillebotis_id   ");
		buf.append("	FROM patrimoine_eqpt_caillebotis t1 ");
		buf.append("	WHERE (t1.eqptcaillebotis_eqptsocle_id, t1.id_chg) IN ");
		buf.append("		( ");
		buf.append("		SELECT t.eqptcaillebotis_eqptsocle_id, MAX(t.id_chg) as id_chg  ");
		buf.append("		FROM patrimoine_eqpt_caillebotis t ");
		buf.append("		GROUP BY t.eqptcaillebotis_eqptsocle_id ");
		buf.append("		)  ");
		buf.append("	)  ");

		session = getHibernateTemplate().getCurrentSession();
		List<TypeEquipementCaillebotis> equipementCaillebotiss = session.createSQLQuery(buf.toString()).addEntity(TypeEquipementCaillebotis.class).list();
		session.close();
		System.out.println("Insert " + equipementCaillebotiss.size() + " lines of patrimoine_eqpt_caillebotis");

		EquipementsCaillebotis equipementCaillebotis = new EquipementsCaillebotis();
		equipementCaillebotis.setEquipementCaillebotis(equipementCaillebotiss);
		patrimoine.setEquipementsCaillebotis(equipementCaillebotis);

		// -----------------------------------------------------//
		// patrimoine_eqpt_canalcomptage
		// -----------------------------------------------------//

		buf = new StringBuffer();
		buf.append("SELECT es.* ");
		buf.append(" FROM patrimoine_contrat c, patrimoine_site s, patrimoine_ouvrage_socle o, patrimoine_unite_socle u, patrimoine_eqpt_socle e, patrimoine_eqpt_canalcomptage es ");
		buf.append("WHERE c.contrat_id = s.site_contrat_id_dwh ");
		buf.append("AND s.site_id = o.ouvragesocle_site_id_dwh ");
		buf.append("AND o.ouvragesocle_id = u.unitesocle_ouvragesocle_id_dwh ");
		buf.append("AND u.unitesocle_id = e.eqptsocle_idunite_dwh ");
		buf.append("AND e.eqptsocle_id = es.eqptcanalcomptage_eqptsocle_id_dwh ");
		buf.append("AND c.contrat_ve_id LIKE '" + contrat + "' ");
		buf.append("AND es.eqptcanalcomptage_id IN  ");
		buf.append("	( ");
		buf.append("	SELECT t1.eqptcanalcomptage_id   ");
		buf.append("	FROM patrimoine_eqpt_canalcomptage t1 ");
		buf.append("	WHERE (t1.eqptcanalcomptage_eqptsocle_id, t1.id_chg) IN ");
		buf.append("		( ");
		buf.append("		SELECT t.eqptcanalcomptage_eqptsocle_id, MAX(t.id_chg) as id_chg  ");
		buf.append("		FROM patrimoine_eqpt_canalcomptage t ");
		buf.append("		GROUP BY t.eqptcanalcomptage_eqptsocle_id ");
		buf.append("		)  ");
		buf.append("	)  ");

		session = getHibernateTemplate().getCurrentSession();
		List<TypeEquipementCanalComptage> equipementCanalComptages = session.createSQLQuery(buf.toString()).addEntity(TypeEquipementCanalComptage.class).list();
		session.close();
		System.out.println("Insert " + equipementCanalComptages.size() + " lines of patrimoine_eqpt_canalcomptage");

		EquipementsCanauxComptage equipementCanalComptage = new EquipementsCanauxComptage();
		equipementCanalComptage.setEquipementCanalComptage(equipementCanalComptages);
		patrimoine.setEquipementsCanauxComptage(equipementCanalComptage);

		// -----------------------------------------------------//
		// patrimoine_eqpt_capteur
		// -----------------------------------------------------//

		buf = new StringBuffer();
		buf.append("SELECT es.* ");
		buf.append(" FROM patrimoine_contrat c, patrimoine_site s, patrimoine_ouvrage_socle o, patrimoine_unite_socle u, patrimoine_eqpt_socle e, patrimoine_eqpt_capteur es ");
		buf.append("WHERE c.contrat_id = s.site_contrat_id_dwh ");
		buf.append("AND s.site_id = o.ouvragesocle_site_id_dwh ");
		buf.append("AND o.ouvragesocle_id = u.unitesocle_ouvragesocle_id_dwh ");
		buf.append("AND u.unitesocle_id = e.eqptsocle_idunite_dwh ");
		buf.append("AND e.eqptsocle_id = es.eqptcapteur_eqptsocle_id_dwh ");
		buf.append("AND c.contrat_ve_id LIKE '" + contrat + "' ");
		buf.append("AND es.eqptcapteur_id IN  ");
		buf.append("	( ");
		buf.append("	SELECT t1.eqptcapteur_id   ");
		buf.append("	FROM patrimoine_eqpt_capteur t1 ");
		buf.append("	WHERE (t1.eqptcapteur_eqptsocle_id, t1.id_chg) IN ");
		buf.append("		( ");
		buf.append("		SELECT t.eqptcapteur_eqptsocle_id, MAX(t.id_chg) as id_chg  ");
		buf.append("		FROM patrimoine_eqpt_capteur t ");
		buf.append("		GROUP BY t.eqptcapteur_eqptsocle_id ");
		buf.append("		)  ");
		buf.append("	)  ");

		session = getHibernateTemplate().getCurrentSession();
		List<TypeEquipementCapteur> equipementCapteurs = session.createSQLQuery(buf.toString()).addEntity(TypeEquipementCapteur.class).list();
		session.close();
		System.out.println("Insert " + equipementCapteurs.size() + " lines of patrimoine_eqpt_capteur");

		EquipementsCapteurs equipementCapteur = new EquipementsCapteurs();
		equipementCapteur.setEquipementCapteur(equipementCapteurs);
		patrimoine.setEquipementsCapteurs(equipementCapteur);

		// -----------------------------------------------------//
		// patrimoine_eqpt_celluleposteht
		// -----------------------------------------------------//

		buf = new StringBuffer();
		buf.append("SELECT es.* ");
		buf.append(" FROM patrimoine_contrat c, patrimoine_site s, patrimoine_ouvrage_socle o, patrimoine_unite_socle u, patrimoine_eqpt_socle e, patrimoine_eqpt_celluleposteht es ");
		buf.append("WHERE c.contrat_id = s.site_contrat_id_dwh ");
		buf.append("AND s.site_id = o.ouvragesocle_site_id_dwh ");
		buf.append("AND o.ouvragesocle_id = u.unitesocle_ouvragesocle_id_dwh ");
		buf.append("AND u.unitesocle_id = e.eqptsocle_idunite_dwh ");
		buf.append("AND e.eqptsocle_id = es.eqptcelluleposteht_eqptsocle_id_dwh ");
		buf.append("AND c.contrat_ve_id LIKE '" + contrat + "' ");
		buf.append("AND es.eqptcelluleposteht_id IN  ");
		buf.append("	( ");
		buf.append("	SELECT t1.eqptcelluleposteht_id   ");
		buf.append("	FROM patrimoine_eqpt_celluleposteht t1 ");
		buf.append("	WHERE (t1.eqptcelluleposteht_eqptsocle_id, t1.id_chg) IN ");
		buf.append("		( ");
		buf.append("		SELECT t.eqptcelluleposteht_eqptsocle_id, MAX(t.id_chg) as id_chg  ");
		buf.append("		FROM patrimoine_eqpt_celluleposteht t ");
		buf.append("		GROUP BY t.eqptcelluleposteht_eqptsocle_id ");
		buf.append("		)  ");
		buf.append("	)  ");

		session = getHibernateTemplate().getCurrentSession();
		List<TypeEquipementCellulePosteHT> equipementCellulePosteHTs = session.createSQLQuery(buf.toString()).addEntity(TypeEquipementCellulePosteHT.class).list();
		session.close();
		System.out.println("Insert " + equipementCellulePosteHTs.size() + " lines of patrimoine_eqpt_celluleposteht");

		EquipementsCellulesPosteHT equipementCellulePosteHT = new EquipementsCellulesPosteHT();
		equipementCellulePosteHT.setEquipementCellulePosteHT(equipementCellulePosteHTs);
		patrimoine.setEquipementsCellulesPosteHT(equipementCellulePosteHT);

		// -----------------------------------------------------//
		// patrimoine_eqpt_centralehydrau
		// -----------------------------------------------------//

		buf = new StringBuffer();
		buf.append("SELECT es.* ");
		buf.append(" FROM patrimoine_contrat c, patrimoine_site s, patrimoine_ouvrage_socle o, patrimoine_unite_socle u, patrimoine_eqpt_socle e, patrimoine_eqpt_centralehydrau es ");
		buf.append("WHERE c.contrat_id = s.site_contrat_id_dwh ");
		buf.append("AND s.site_id = o.ouvragesocle_site_id_dwh ");
		buf.append("AND o.ouvragesocle_id = u.unitesocle_ouvragesocle_id_dwh ");
		buf.append("AND u.unitesocle_id = e.eqptsocle_idunite_dwh ");
		buf.append("AND e.eqptsocle_id = es.eqptcentralehydrau_eqptsocle_id_dwh ");
		buf.append("AND c.contrat_ve_id LIKE '" + contrat + "' ");
		buf.append("AND es.eqptcentralehydrau_id IN  ");
		buf.append("	( ");
		buf.append("	SELECT t1.eqptcentralehydrau_id   ");
		buf.append("	FROM patrimoine_eqpt_centralehydrau t1 ");
		buf.append("	WHERE (t1.eqptcentralehydrau_eqptsocle_id, t1.id_chg) IN ");
		buf.append("		( ");
		buf.append("		SELECT t.eqptcentralehydrau_eqptsocle_id, MAX(t.id_chg) as id_chg  ");
		buf.append("		FROM patrimoine_eqpt_centralehydrau t ");
		buf.append("		GROUP BY t.eqptcentralehydrau_eqptsocle_id ");
		buf.append("		)  ");
		buf.append("	)  ");

		session = getHibernateTemplate().getCurrentSession();
		List<TypeEquipementCentraleHydraulique> equipementCentraleHydrauliques = session.createSQLQuery(buf.toString()).addEntity(TypeEquipementCentraleHydraulique.class).list();
		session.close();
		System.out.println("Insert " + equipementCentraleHydrauliques.size() + " lines of patrimoine_eqpt_centralehydrau");

		EquipementsCentralesHydrauliques equipementCentraleHydraulique = new EquipementsCentralesHydrauliques();
		equipementCentraleHydraulique.setEquipementCentraleHydraulique(equipementCentraleHydrauliques);
		patrimoine.setEquipementsCentralesHydrauliques(equipementCentraleHydraulique);

		// -----------------------------------------------------//
		// patrimoine_eqpt_centrifugeuse
		// -----------------------------------------------------//

		buf = new StringBuffer();
		buf.append("SELECT es.* ");
		buf.append(" FROM patrimoine_contrat c, patrimoine_site s, patrimoine_ouvrage_socle o, patrimoine_unite_socle u, patrimoine_eqpt_socle e, patrimoine_eqpt_centrifugeuse es ");
		buf.append("WHERE c.contrat_id = s.site_contrat_id_dwh ");
		buf.append("AND s.site_id = o.ouvragesocle_site_id_dwh ");
		buf.append("AND o.ouvragesocle_id = u.unitesocle_ouvragesocle_id_dwh ");
		buf.append("AND u.unitesocle_id = e.eqptsocle_idunite_dwh ");
		buf.append("AND e.eqptsocle_id = es.eqptcentrifugeuse_eqptsocle_id_dwh ");
		buf.append("AND c.contrat_ve_id LIKE '" + contrat + "' ");
		buf.append("AND es.eqptcentrifugeuse_id IN  ");
		buf.append("	( ");
		buf.append("	SELECT t1.eqptcentrifugeuse_id   ");
		buf.append("	FROM patrimoine_eqpt_centrifugeuse t1 ");
		buf.append("	WHERE (t1.eqptcentrifugeuse_eqptsocle_id, t1.id_chg) IN ");
		buf.append("		( ");
		buf.append("		SELECT t.eqptcentrifugeuse_eqptsocle_id, MAX(t.id_chg) as id_chg  ");
		buf.append("		FROM patrimoine_eqpt_centrifugeuse t ");
		buf.append("		GROUP BY t.eqptcentrifugeuse_eqptsocle_id ");
		buf.append("		)  ");
		buf.append("	)  ");

		session = getHibernateTemplate().getCurrentSession();
		List<TypeEquipementCentrifugeuse> equipementCentrifugeuses = session.createSQLQuery(buf.toString()).addEntity(TypeEquipementCentrifugeuse.class).list();
		session.close();
		System.out.println("Insert " + equipementCentrifugeuses.size() + " lines of patrimoine_eqpt_centrifugeuse");

		EquipementsCentrifugeuses equipementCentrifugeuse = new EquipementsCentrifugeuses();
		equipementCentrifugeuse.setEquipementCentrifugeuse(equipementCentrifugeuses);
		patrimoine.setEquipementsCentrifugeuses(equipementCentrifugeuse);

		// -----------------------------------------------------//
		// patrimoine_eqpt_chariot
		// -----------------------------------------------------//

		buf = new StringBuffer();
		buf.append("SELECT es.* ");
		buf.append(" FROM patrimoine_contrat c, patrimoine_site s, patrimoine_ouvrage_socle o, patrimoine_unite_socle u, patrimoine_eqpt_socle e, patrimoine_eqpt_chariot es ");
		buf.append("WHERE c.contrat_id = s.site_contrat_id_dwh ");
		buf.append("AND s.site_id = o.ouvragesocle_site_id_dwh ");
		buf.append("AND o.ouvragesocle_id = u.unitesocle_ouvragesocle_id_dwh ");
		buf.append("AND u.unitesocle_id = e.eqptsocle_idunite_dwh ");
		buf.append("AND e.eqptsocle_id = es.eqptchariot_eqptsocle_id_dwh ");
		buf.append("AND c.contrat_ve_id LIKE '" + contrat + "' ");
		buf.append("AND es.eqptchariot_id IN  ");
		buf.append("	( ");
		buf.append("	SELECT t1.eqptchariot_id   ");
		buf.append("	FROM patrimoine_eqpt_chariot t1 ");
		buf.append("	WHERE (t1.eqptchariot_eqptsocle_id, t1.id_chg) IN ");
		buf.append("		( ");
		buf.append("		SELECT t.eqptchariot_eqptsocle_id, MAX(t.id_chg) as id_chg  ");
		buf.append("		FROM patrimoine_eqpt_chariot t ");
		buf.append("		GROUP BY t.eqptchariot_eqptsocle_id ");
		buf.append("		)  ");
		buf.append("	)  ");

		session = getHibernateTemplate().getCurrentSession();
		List<TypeEquipementChariotElevateur> equipementChariotElevateurs = session.createSQLQuery(buf.toString()).addEntity(TypeEquipementChariotElevateur.class).list();
		session.close();
		System.out.println("Insert " + equipementChariotElevateurs.size() + " lines of patrimoine_eqpt_chariot");

		EquipementsChariotsElevateurs equipementChariotElevateur = new EquipementsChariotsElevateurs();
		equipementChariotElevateur.setEquipementChariotElevateur(equipementChariotElevateurs);
		patrimoine.setEquipementsChariotsElevateurs(equipementChariotElevateur);

		// -----------------------------------------------------//
		// patrimoine_eqpt_chlorometre
		// -----------------------------------------------------//

		buf = new StringBuffer();
		buf.append("SELECT es.* ");
		buf.append(" FROM patrimoine_contrat c, patrimoine_site s, patrimoine_ouvrage_socle o, patrimoine_unite_socle u, patrimoine_eqpt_socle e, patrimoine_eqpt_chlorometre es ");
		buf.append("WHERE c.contrat_id = s.site_contrat_id_dwh ");
		buf.append("AND s.site_id = o.ouvragesocle_site_id_dwh ");
		buf.append("AND o.ouvragesocle_id = u.unitesocle_ouvragesocle_id_dwh ");
		buf.append("AND u.unitesocle_id = e.eqptsocle_idunite_dwh ");
		buf.append("AND e.eqptsocle_id = es.eqptchlorometre_eqptsocle_id_dwh ");
		buf.append("AND c.contrat_ve_id LIKE '" + contrat + "' ");
		buf.append("AND es.eqptchlorometre_id IN  ");
		buf.append("	( ");
		buf.append("	SELECT t1.eqptchlorometre_id   ");
		buf.append("	FROM patrimoine_eqpt_chlorometre t1 ");
		buf.append("	WHERE (t1.eqptchlorometre_eqptsocle_id, t1.id_chg) IN ");
		buf.append("		( ");
		buf.append("		SELECT t.eqptchlorometre_eqptsocle_id, MAX(t.id_chg) as id_chg  ");
		buf.append("		FROM patrimoine_eqpt_chlorometre t ");
		buf.append("		GROUP BY t.eqptchlorometre_eqptsocle_id ");
		buf.append("		)  ");
		buf.append("	)  ");

		session = getHibernateTemplate().getCurrentSession();
		List<TypeEquipementChlorometre> equipementChlorometres = session.createSQLQuery(buf.toString()).addEntity(TypeEquipementChlorometre.class).list();
		session.close();
		System.out.println("Insert " + equipementChlorometres.size() + " lines of patrimoine_eqpt_chlorometre");

		EquipementsChlorometres equipementChlorometre = new EquipementsChlorometres();
		equipementChlorometre.setEquipementChlorometre(equipementChlorometres);
		patrimoine.setEquipementsChlorometres(equipementChlorometre);

		// -----------------------------------------------------//
		// patrimoine_eqpt_clapet
		// -----------------------------------------------------//

		buf = new StringBuffer();
		buf.append("SELECT es.* ");
		buf.append(" FROM patrimoine_contrat c, patrimoine_site s, patrimoine_ouvrage_socle o, patrimoine_unite_socle u, patrimoine_eqpt_socle e, patrimoine_eqpt_clapet es ");
		buf.append("WHERE c.contrat_id = s.site_contrat_id_dwh ");
		buf.append("AND s.site_id = o.ouvragesocle_site_id_dwh ");
		buf.append("AND o.ouvragesocle_id = u.unitesocle_ouvragesocle_id_dwh ");
		buf.append("AND u.unitesocle_id = e.eqptsocle_idunite_dwh ");
		buf.append("AND e.eqptsocle_id = es.eqptclapet_eqptsocle_id_dwh ");
		buf.append("AND c.contrat_ve_id LIKE '" + contrat + "' ");
		buf.append("AND es.eqptclapet_id IN  ");
		buf.append("	( ");
		buf.append("	SELECT t1.eqptclapet_id   ");
		buf.append("	FROM patrimoine_eqpt_clapet t1 ");
		buf.append("	WHERE (t1.eqptclapet_eqptsocle_id, t1.id_chg) IN ");
		buf.append("		( ");
		buf.append("		SELECT t.eqptclapet_eqptsocle_id, MAX(t.id_chg) as id_chg  ");
		buf.append("		FROM patrimoine_eqpt_clapet t ");
		buf.append("		GROUP BY t.eqptclapet_eqptsocle_id ");
		buf.append("		)  ");
		buf.append("	)  ");

		session = getHibernateTemplate().getCurrentSession();
		List<TypeEquipementClapet> equipementClapets = session.createSQLQuery(buf.toString()).addEntity(TypeEquipementClapet.class).list();
		session.close();
		System.out.println("Insert " + equipementClapets.size() + " lines of patrimoine_eqpt_clapet");

		EquipementsClapets equipementClapet = new EquipementsClapets();
		equipementClapet.setEquipementClapet(equipementClapets);
		patrimoine.setEquipementsClapets(equipementClapet);

		// -----------------------------------------------------//
		// patrimoine_eqpt_cloture
		// -----------------------------------------------------//

		buf = new StringBuffer();
		buf.append("SELECT es.* ");
		buf.append(" FROM patrimoine_contrat c, patrimoine_site s, patrimoine_ouvrage_socle o, patrimoine_unite_socle u, patrimoine_eqpt_socle e, patrimoine_eqpt_cloture es ");
		buf.append("WHERE c.contrat_id = s.site_contrat_id_dwh ");
		buf.append("AND s.site_id = o.ouvragesocle_site_id_dwh ");
		buf.append("AND o.ouvragesocle_id = u.unitesocle_ouvragesocle_id_dwh ");
		buf.append("AND u.unitesocle_id = e.eqptsocle_idunite_dwh ");
		buf.append("AND e.eqptsocle_id = es.eqptcloture_eqptsocle_id_dwh ");
		buf.append("AND c.contrat_ve_id LIKE '" + contrat + "' ");
		buf.append("AND es.eqptcloture_id IN  ");
		buf.append("	( ");
		buf.append("	SELECT t1.eqptcloture_id   ");
		buf.append("	FROM patrimoine_eqpt_cloture t1 ");
		buf.append("	WHERE (t1.eqptcloture_eqptsocle_id, t1.id_chg) IN ");
		buf.append("		( ");
		buf.append("		SELECT t.eqptcloture_eqptsocle_id, MAX(t.id_chg) as id_chg  ");
		buf.append("		FROM patrimoine_eqpt_cloture t ");
		buf.append("		GROUP BY t.eqptcloture_eqptsocle_id ");
		buf.append("		)  ");
		buf.append("	)  ");

		session = getHibernateTemplate().getCurrentSession();
		List<TypeEquipementCloture> equipementClotures = session.createSQLQuery(buf.toString()).addEntity(TypeEquipementCloture.class).list();
		session.close();
		System.out.println("Insert " + equipementClotures.size() + " lines of patrimoine_eqpt_cloture");

		EquipementsClotures equipementCloture = new EquipementsClotures();
		equipementCloture.setEquipementCloture(equipementClotures);
		patrimoine.setEquipementsClotures(equipementCloture);

		// -----------------------------------------------------//
		// patrimoine_eqpt_collecteur
		// -----------------------------------------------------//

		buf = new StringBuffer();
		buf.append("SELECT es.* ");
		buf.append(" FROM patrimoine_contrat c, patrimoine_site s, patrimoine_ouvrage_socle o, patrimoine_unite_socle u, patrimoine_eqpt_socle e, patrimoine_eqpt_collecteur es ");
		buf.append("WHERE c.contrat_id = s.site_contrat_id_dwh ");
		buf.append("AND s.site_id = o.ouvragesocle_site_id_dwh ");
		buf.append("AND o.ouvragesocle_id = u.unitesocle_ouvragesocle_id_dwh ");
		buf.append("AND u.unitesocle_id = e.eqptsocle_idunite_dwh ");
		buf.append("AND e.eqptsocle_id = es.eqptcollecteur_eqptsocle_id_dwh ");
		buf.append("AND c.contrat_ve_id LIKE '" + contrat + "' ");
		buf.append("AND es.eqptcollecteur_id IN  ");
		buf.append("	( ");
		buf.append("	SELECT t1.eqptcollecteur_id   ");
		buf.append("	FROM patrimoine_eqpt_collecteur t1 ");
		buf.append("	WHERE (t1.eqptcollecteur_eqptsocle_id, t1.id_chg) IN ");
		buf.append("		( ");
		buf.append("		SELECT t.eqptcollecteur_eqptsocle_id, MAX(t.id_chg) as id_chg  ");
		buf.append("		FROM patrimoine_eqpt_collecteur t ");
		buf.append("		GROUP BY t.eqptcollecteur_eqptsocle_id ");
		buf.append("		)  ");
		buf.append("	)  ");

		session = getHibernateTemplate().getCurrentSession();
		List<TypeEquipementCollecteur> equipementCollecteurs = session.createSQLQuery(buf.toString()).addEntity(TypeEquipementCollecteur.class).list();
		session.close();
		System.out.println("Insert " + equipementCollecteurs.size() + " lines of patrimoine_eqpt_collecteur");

		EquipementsCollecteurs equipementCollecteur = new EquipementsCollecteurs();
		equipementCollecteur.setEquipementCollecteur(equipementCollecteurs);
		patrimoine.setEquipementsCollecteurs(equipementCollecteur);

		// -----------------------------------------------------//
		// patrimoine_eqpt_colonneforage
		// -----------------------------------------------------//

		buf = new StringBuffer();
		buf.append("SELECT es.* ");
		buf.append(" FROM patrimoine_contrat c, patrimoine_site s, patrimoine_ouvrage_socle o, patrimoine_unite_socle u, patrimoine_eqpt_socle e, patrimoine_eqpt_colonneforage es ");
		buf.append("WHERE c.contrat_id = s.site_contrat_id_dwh ");
		buf.append("AND s.site_id = o.ouvragesocle_site_id_dwh ");
		buf.append("AND o.ouvragesocle_id = u.unitesocle_ouvragesocle_id_dwh ");
		buf.append("AND u.unitesocle_id = e.eqptsocle_idunite_dwh ");
		buf.append("AND e.eqptsocle_id = es.eqptcolonneforage_eqptsocle_id_dwh ");
		buf.append("AND c.contrat_ve_id LIKE '" + contrat + "' ");
		buf.append("AND es.eqptcolonneforage_id IN  ");
		buf.append("	( ");
		buf.append("	SELECT t1.eqptcolonneforage_id   ");
		buf.append("	FROM patrimoine_eqpt_colonneforage t1 ");
		buf.append("	WHERE (t1.eqptcolonneforage_eqptsocle_id, t1.id_chg) IN ");
		buf.append("		( ");
		buf.append("		SELECT t.eqptcolonneforage_eqptsocle_id, MAX(t.id_chg) as id_chg  ");
		buf.append("		FROM patrimoine_eqpt_colonneforage t ");
		buf.append("		GROUP BY t.eqptcolonneforage_eqptsocle_id ");
		buf.append("		)  ");
		buf.append("	)  ");

		session = getHibernateTemplate().getCurrentSession();
		List<TypeEquipementColonneForage> equipementColonneForages = session.createSQLQuery(buf.toString()).addEntity(TypeEquipementColonneForage.class).list();
		session.close();
		System.out.println("Insert " + equipementColonneForages.size() + " lines of patrimoine_eqpt_colonneforage");

		EquipementsColonnesForage equipementColonneForage = new EquipementsColonnesForage();
		equipementColonneForage.setEquipementColonneForage(equipementColonneForages);
		patrimoine.setEquipementsColonnesForage(equipementColonneForage);

		// -----------------------------------------------------//
		// patrimoine_eqpt_colonnemontante
		// -----------------------------------------------------//

		buf = new StringBuffer();
		buf.append("SELECT es.* ");
		buf.append(" FROM patrimoine_contrat c, patrimoine_site s, patrimoine_ouvrage_socle o, patrimoine_unite_socle u, patrimoine_eqpt_socle e, patrimoine_eqpt_colonnemontante es ");
		buf.append("WHERE c.contrat_id = s.site_contrat_id_dwh ");
		buf.append("AND s.site_id = o.ouvragesocle_site_id_dwh ");
		buf.append("AND o.ouvragesocle_id = u.unitesocle_ouvragesocle_id_dwh ");
		buf.append("AND u.unitesocle_id = e.eqptsocle_idunite_dwh ");
		buf.append("AND e.eqptsocle_id = es.eqptcolonnemontante_eqptsocle_id_dwh ");
		buf.append("AND c.contrat_ve_id LIKE '" + contrat + "' ");
		buf.append("AND es.eqptcolonnemontante_id IN  ");
		buf.append("	( ");
		buf.append("	SELECT t1.eqptcolonnemontante_id   ");
		buf.append("	FROM patrimoine_eqpt_colonnemontante t1 ");
		buf.append("	WHERE (t1.eqptcolonnemontante_eqptsocle_id, t1.id_chg) IN ");
		buf.append("		( ");
		buf.append("		SELECT t.eqptcolonnemontante_eqptsocle_id, MAX(t.id_chg) as id_chg  ");
		buf.append("		FROM patrimoine_eqpt_colonnemontante t ");
		buf.append("		GROUP BY t.eqptcolonnemontante_eqptsocle_id ");
		buf.append("		)  ");
		buf.append("	)  ");

		session = getHibernateTemplate().getCurrentSession();
		List<TypeEquipementColonneMontante> equipementColonneMontantes = session.createSQLQuery(buf.toString()).addEntity(TypeEquipementColonneMontante.class).list();
		session.close();
		System.out.println("Insert " + equipementColonneMontantes.size() + " lines of patrimoine_eqpt_colonnemontante");

		EquipementsColonnesMontantes equipementColonneMontante = new EquipementsColonnesMontantes();
		equipementColonneMontante.setEquipementColonneMontante(equipementColonneMontantes);
		patrimoine.setEquipementsColonnesMontantes(equipementColonneMontante);

		// -----------------------------------------------------//
		// patrimoine_eqpt_comptage
		// -----------------------------------------------------//

		buf = new StringBuffer();
		buf.append("SELECT es.* ");
		buf.append(" FROM patrimoine_contrat c, patrimoine_site s, patrimoine_ouvrage_socle o, patrimoine_unite_socle u, patrimoine_eqpt_socle e, patrimoine_eqpt_comptage es ");
		buf.append("WHERE c.contrat_id = s.site_contrat_id_dwh ");
		buf.append("AND s.site_id = o.ouvragesocle_site_id_dwh ");
		buf.append("AND o.ouvragesocle_id = u.unitesocle_ouvragesocle_id_dwh ");
		buf.append("AND u.unitesocle_id = e.eqptsocle_idunite_dwh ");
		buf.append("AND e.eqptsocle_id = es.eqptcomptage_eqptsocle_id_dwh ");
		buf.append("AND c.contrat_ve_id LIKE '" + contrat + "' ");
		buf.append("AND es.eqptcomptage_id IN  ");
		buf.append("	( ");
		buf.append("	SELECT t1.eqptcomptage_id   ");
		buf.append("	FROM patrimoine_eqpt_comptage t1 ");
		buf.append("	WHERE (t1.eqptcomptage_eqptsocle_id, t1.id_chg) IN ");
		buf.append("		( ");
		buf.append("		SELECT t.eqptcomptage_eqptsocle_id, MAX(t.id_chg) as id_chg  ");
		buf.append("		FROM patrimoine_eqpt_comptage t ");
		buf.append("		GROUP BY t.eqptcomptage_eqptsocle_id ");
		buf.append("		)  ");
		buf.append("	)  ");

		session = getHibernateTemplate().getCurrentSession();
		List<TypeEquipementComptage> equipementComptages = session.createSQLQuery(buf.toString()).addEntity(TypeEquipementComptage.class).list();
		session.close();
		System.out.println("Insert " + equipementComptages.size() + " lines of patrimoine_eqpt_comptage");

		EquipementsComptages equipementComptage = new EquipementsComptages();
		equipementComptage.setEquipementComptage(equipementComptages);
		patrimoine.setEquipementsComptages(equipementComptage);

		// -----------------------------------------------------//
		// patrimoine_eqpt_condensateur
		// -----------------------------------------------------//

		buf = new StringBuffer();
		buf.append("SELECT es.* ");
		buf.append(" FROM patrimoine_contrat c, patrimoine_site s, patrimoine_ouvrage_socle o, patrimoine_unite_socle u, patrimoine_eqpt_socle e, patrimoine_eqpt_condensateur es ");
		buf.append("WHERE c.contrat_id = s.site_contrat_id_dwh ");
		buf.append("AND s.site_id = o.ouvragesocle_site_id_dwh ");
		buf.append("AND o.ouvragesocle_id = u.unitesocle_ouvragesocle_id_dwh ");
		buf.append("AND u.unitesocle_id = e.eqptsocle_idunite_dwh ");
		buf.append("AND e.eqptsocle_id = es.eqptcondensateur_eqptsocle_id_dwh ");
		buf.append("AND c.contrat_ve_id LIKE '" + contrat + "' ");
		buf.append("AND es.eqptcondensateur_id IN  ");
		buf.append("	( ");
		buf.append("	SELECT t1.eqptcondensateur_id   ");
		buf.append("	FROM patrimoine_eqpt_condensateur t1 ");
		buf.append("	WHERE (t1.eqptcondensateur_eqptsocle_id, t1.id_chg) IN ");
		buf.append("		( ");
		buf.append("		SELECT t.eqptcondensateur_eqptsocle_id, MAX(t.id_chg) as id_chg  ");
		buf.append("		FROM patrimoine_eqpt_condensateur t ");
		buf.append("		GROUP BY t.eqptcondensateur_eqptsocle_id ");
		buf.append("		)  ");
		buf.append("	)  ");

		session = getHibernateTemplate().getCurrentSession();
		List<TypeEquipementCondensateur> equipementCondensateurs = session.createSQLQuery(buf.toString()).addEntity(TypeEquipementCondensateur.class).list();
		session.close();
		System.out.println("Insert " + equipementCondensateurs.size() + " lines of patrimoine_eqpt_condensateur");

		EquipementsCondensateurs equipementCondensateur = new EquipementsCondensateurs();
		equipementCondensateur.setEquipementCondensateur(equipementCondensateurs);
		patrimoine.setEquipementsCondensateurs(equipementCondensateur);

		// -----------------------------------------------------//
		// patrimoine_eqpt_convtransp
		// -----------------------------------------------------//

		buf = new StringBuffer();
		buf.append("SELECT es.* ");
		buf.append(" FROM patrimoine_contrat c, patrimoine_site s, patrimoine_ouvrage_socle o, patrimoine_unite_socle u, patrimoine_eqpt_socle e, patrimoine_eqpt_convtransp es ");
		buf.append("WHERE c.contrat_id = s.site_contrat_id_dwh ");
		buf.append("AND s.site_id = o.ouvragesocle_site_id_dwh ");
		buf.append("AND o.ouvragesocle_id = u.unitesocle_ouvragesocle_id_dwh ");
		buf.append("AND u.unitesocle_id = e.eqptsocle_idunite_dwh ");
		buf.append("AND e.eqptsocle_id = es.eqptconvtransp_eqptsocle_id_dwh ");
		buf.append("AND c.contrat_ve_id LIKE '" + contrat + "' ");
		buf.append("AND es.eqptconvtransp_id IN  ");
		buf.append("	( ");
		buf.append("	SELECT t1.eqptconvtransp_id   ");
		buf.append("	FROM patrimoine_eqpt_convtransp t1 ");
		buf.append("	WHERE (t1.eqptconvtransp_eqptsocle_id, t1.id_chg) IN ");
		buf.append("		( ");
		buf.append("		SELECT t.eqptconvtransp_eqptsocle_id, MAX(t.id_chg) as id_chg  ");
		buf.append("		FROM patrimoine_eqpt_convtransp t ");
		buf.append("		GROUP BY t.eqptconvtransp_eqptsocle_id ");
		buf.append("		)  ");
		buf.append("	)  ");

		session = getHibernateTemplate().getCurrentSession();
		List<TypeEquipementConvoyeurTransporteur> equipementConvoyeurTransporteurs = session.createSQLQuery(buf.toString()).addEntity(TypeEquipementConvoyeurTransporteur.class).list();
		session.close();
		System.out.println("Insert " + equipementConvoyeurTransporteurs.size() + " lines of patrimoine_eqpt_convtransp");

		EquipementsConvoyeursTransporteurs equipementConvoyeurTransporteur = new EquipementsConvoyeursTransporteurs();
		equipementConvoyeurTransporteur.setEquipementConvoyeurTransporteur(equipementConvoyeurTransporteurs);
		patrimoine.setEquipementsConvoyeursTransporteurs(equipementConvoyeurTransporteur);

		// -----------------------------------------------------//
		// patrimoine_eqpt_cptelec
		// -----------------------------------------------------//

		buf = new StringBuffer();
		buf.append("SELECT es.* ");
		buf.append(" FROM patrimoine_contrat c, patrimoine_site s, patrimoine_ouvrage_socle o, patrimoine_unite_socle u, patrimoine_eqpt_socle e, patrimoine_eqpt_cptelec es ");
		buf.append("WHERE c.contrat_id = s.site_contrat_id_dwh ");
		buf.append("AND s.site_id = o.ouvragesocle_site_id_dwh ");
		buf.append("AND o.ouvragesocle_id = u.unitesocle_ouvragesocle_id_dwh ");
		buf.append("AND u.unitesocle_id = e.eqptsocle_idunite_dwh ");
		buf.append("AND e.eqptsocle_id = es.eqptcptelec_eqptsocle_id_dwh ");
		buf.append("AND c.contrat_ve_id LIKE '" + contrat + "' ");
		buf.append("AND es.eqptcptelec_id IN  ");
		buf.append("	( ");
		buf.append("	SELECT t1.eqptcptelec_id   ");
		buf.append("	FROM patrimoine_eqpt_cptelec t1 ");
		buf.append("	WHERE (t1.eqptcptelec_eqptsocle_id, t1.id_chg) IN ");
		buf.append("		( ");
		buf.append("		SELECT t.eqptcptelec_eqptsocle_id, MAX(t.id_chg) as id_chg  ");
		buf.append("		FROM patrimoine_eqpt_cptelec t ");
		buf.append("		GROUP BY t.eqptcptelec_eqptsocle_id ");
		buf.append("		)  ");
		buf.append("	)  ");

		session = getHibernateTemplate().getCurrentSession();
		List<TypeEquipementCompteurElectrique> equipementCompteurElectriques = session.createSQLQuery(buf.toString()).addEntity(TypeEquipementCompteurElectrique.class).list();
		session.close();
		System.out.println("Insert " + equipementCompteurElectriques.size() + " lines of patrimoine_eqpt_cptelec");

		EquipementsCompteursElectriques equipementCompteurElectrique = new EquipementsCompteursElectriques();
		equipementCompteurElectrique.setEquipementCompteurElectrique(equipementCompteurElectriques);
		patrimoine.setEquipementsCompteursElectriques(equipementCompteurElectrique);

		// -----------------------------------------------------//
		// patrimoine_eqpt_crepine
		// -----------------------------------------------------//

		buf = new StringBuffer();
		buf.append("SELECT es.* ");
		buf.append(" FROM patrimoine_contrat c, patrimoine_site s, patrimoine_ouvrage_socle o, patrimoine_unite_socle u, patrimoine_eqpt_socle e, patrimoine_eqpt_crepine es ");
		buf.append("WHERE c.contrat_id = s.site_contrat_id_dwh ");
		buf.append("AND s.site_id = o.ouvragesocle_site_id_dwh ");
		buf.append("AND o.ouvragesocle_id = u.unitesocle_ouvragesocle_id_dwh ");
		buf.append("AND u.unitesocle_id = e.eqptsocle_idunite_dwh ");
		buf.append("AND e.eqptsocle_id = es.eqptcrepine_eqptsocle_id_dwh ");
		buf.append("AND c.contrat_ve_id LIKE '" + contrat + "' ");
		buf.append("AND es.eqptcrepine_id IN  ");
		buf.append("	( ");
		buf.append("	SELECT t1.eqptcrepine_id   ");
		buf.append("	FROM patrimoine_eqpt_crepine t1 ");
		buf.append("	WHERE (t1.eqptcrepine_eqptsocle_id, t1.id_chg) IN ");
		buf.append("		( ");
		buf.append("		SELECT t.eqptcrepine_eqptsocle_id, MAX(t.id_chg) as id_chg  ");
		buf.append("		FROM patrimoine_eqpt_crepine t ");
		buf.append("		GROUP BY t.eqptcrepine_eqptsocle_id ");
		buf.append("		)  ");
		buf.append("	)  ");

		session = getHibernateTemplate().getCurrentSession();
		List<TypeEquipementCrepine> equipementCrepines = session.createSQLQuery(buf.toString()).addEntity(TypeEquipementCrepine.class).list();
		session.close();
		System.out.println("Insert " + equipementCrepines.size() + " lines of patrimoine_eqpt_crepine");

		EquipementsCrepines equipementCrepine = new EquipementsCrepines();
		equipementCrepine.setEquipementCrepine(equipementCrepines);
		patrimoine.setEquipementsCrepines(equipementCrepine);

		// -----------------------------------------------------//
		// patrimoine_eqpt_demarreurelec
		// -----------------------------------------------------//

		buf = new StringBuffer();
		buf.append("SELECT es.* ");
		buf.append(" FROM patrimoine_contrat c, patrimoine_site s, patrimoine_ouvrage_socle o, patrimoine_unite_socle u, patrimoine_eqpt_socle e, patrimoine_eqpt_demarreurelec es ");
		buf.append("WHERE c.contrat_id = s.site_contrat_id_dwh ");
		buf.append("AND s.site_id = o.ouvragesocle_site_id_dwh ");
		buf.append("AND o.ouvragesocle_id = u.unitesocle_ouvragesocle_id_dwh ");
		buf.append("AND u.unitesocle_id = e.eqptsocle_idunite_dwh ");
		buf.append("AND e.eqptsocle_id = es.eqptdemelec_eqptsocle_id_dwh ");
		buf.append("AND c.contrat_ve_id LIKE '" + contrat + "' ");
		buf.append("AND es.eqptdemelec_id IN  ");
		buf.append("	( ");
		buf.append("	SELECT t1.eqptdemelec_id   ");
		buf.append("	FROM patrimoine_eqpt_demarreurelec t1 ");
		buf.append("	WHERE (t1.eqptdemelec_eqptsocle_id, t1.id_chg) IN ");
		buf.append("		( ");
		buf.append("		SELECT t.eqptdemelec_eqptsocle_id, MAX(t.id_chg) as id_chg  ");
		buf.append("		FROM patrimoine_eqpt_demarreurelec t ");
		buf.append("		GROUP BY t.eqptdemelec_eqptsocle_id ");
		buf.append("		)  ");
		buf.append("	)  ");

		session = getHibernateTemplate().getCurrentSession();
		List<TypeEquipementDemarreurElectrique> equipementDemarreurElectriques = session.createSQLQuery(buf.toString()).addEntity(TypeEquipementDemarreurElectrique.class).list();
		session.close();
		System.out.println("Insert " + equipementDemarreurElectriques.size() + " lines of patrimoine_eqpt_demarreurelec");

		EquipementsDemarreursElectriques equipementDemarreurElectrique = new EquipementsDemarreursElectriques();
		equipementDemarreurElectrique.setEquipementDemarreurElectrique(equipementDemarreurElectriques);
		patrimoine.setEquipementsDemarreursElectriques(equipementDemarreurElectrique);

		// -----------------------------------------------------//
		// patrimoine_eqpt_deshumidificateur
		// -----------------------------------------------------//

		buf = new StringBuffer();
		buf.append("SELECT es.* ");
		buf.append(" FROM patrimoine_contrat c, patrimoine_site s, patrimoine_ouvrage_socle o, patrimoine_unite_socle u, patrimoine_eqpt_socle e, patrimoine_eqpt_deshumidificateur es ");
		buf.append("WHERE c.contrat_id = s.site_contrat_id_dwh ");
		buf.append("AND s.site_id = o.ouvragesocle_site_id_dwh ");
		buf.append("AND o.ouvragesocle_id = u.unitesocle_ouvragesocle_id_dwh ");
		buf.append("AND u.unitesocle_id = e.eqptsocle_idunite_dwh ");
		buf.append("AND e.eqptsocle_id = es.eqptdeshumidificateur_eqptsocle_id_dwh ");
		buf.append("AND c.contrat_ve_id LIKE '" + contrat + "' ");
		buf.append("AND es.eqptdeshumidificateur_id IN  ");
		buf.append("	( ");
		buf.append("	SELECT t1.eqptdeshumidificateur_id   ");
		buf.append("	FROM patrimoine_eqpt_deshumidificateur t1 ");
		buf.append("	WHERE (t1.eqptdeshumidificateur_eqptsocle_id, t1.id_chg) IN ");
		buf.append("		( ");
		buf.append("		SELECT t.eqptdeshumidificateur_eqptsocle_id, MAX(t.id_chg) as id_chg  ");
		buf.append("		FROM patrimoine_eqpt_deshumidificateur t ");
		buf.append("		GROUP BY t.eqptdeshumidificateur_eqptsocle_id ");
		buf.append("		)  ");
		buf.append("	)  ");

		session = getHibernateTemplate().getCurrentSession();
		List<TypeEquipementDeshumidificateur> equipementDeshumidificateurs = session.createSQLQuery(buf.toString()).addEntity(TypeEquipementDeshumidificateur.class).list();
		session.close();
		System.out.println("Insert " + equipementDeshumidificateurs.size() + " lines of patrimoine_eqpt_deshumidificateur");

		EquipementsDeshumidificateurs equipementDeshumidificateur = new EquipementsDeshumidificateurs();
		equipementDeshumidificateur.setEquipementDeshumidificateur(equipementDeshumidificateurs);
		patrimoine.setEquipementsDeshumidificateurs(equipementDeshumidificateur);

		// -----------------------------------------------------//
		// patrimoine_eqpt_destozone
		// -----------------------------------------------------//

		buf = new StringBuffer();
		buf.append("SELECT es.* ");
		buf.append(" FROM patrimoine_contrat c, patrimoine_site s, patrimoine_ouvrage_socle o, patrimoine_unite_socle u, patrimoine_eqpt_socle e, patrimoine_eqpt_destozone es ");
		buf.append("WHERE c.contrat_id = s.site_contrat_id_dwh ");
		buf.append("AND s.site_id = o.ouvragesocle_site_id_dwh ");
		buf.append("AND o.ouvragesocle_id = u.unitesocle_ouvragesocle_id_dwh ");
		buf.append("AND u.unitesocle_id = e.eqptsocle_idunite_dwh ");
		buf.append("AND e.eqptsocle_id = es.eqptdestozone_eqptsocle_id_dwh ");
		buf.append("AND c.contrat_ve_id LIKE '" + contrat + "' ");
		buf.append("AND es.eqptdestozone_id IN  ");
		buf.append("	( ");
		buf.append("	SELECT t1.eqptdestozone_id   ");
		buf.append("	FROM patrimoine_eqpt_destozone t1 ");
		buf.append("	WHERE (t1.eqptdestozone_eqptsocle_id, t1.id_chg) IN ");
		buf.append("		( ");
		buf.append("		SELECT t.eqptdestozone_eqptsocle_id, MAX(t.id_chg) as id_chg  ");
		buf.append("		FROM patrimoine_eqpt_destozone t ");
		buf.append("		GROUP BY t.eqptdestozone_eqptsocle_id ");
		buf.append("		)  ");
		buf.append("	)  ");

		session = getHibernateTemplate().getCurrentSession();
		List<TypeEquipementDestructeurOzone> equipementDestructeurOzones = session.createSQLQuery(buf.toString()).addEntity(TypeEquipementDestructeurOzone.class).list();
		session.close();
		System.out.println("Insert " + equipementDestructeurOzones.size() + " lines of patrimoine_eqpt_destozone");

		EquipementsDestructeurOzone equipementDestructeurOzone = new EquipementsDestructeurOzone();
		equipementDestructeurOzone.setEquipementDestructeurOzone(equipementDestructeurOzones);
		patrimoine.setEquipementsDestructeurOzone(equipementDestructeurOzone);

		// -----------------------------------------------------//
		// patrimoine_eqpt_disconnecteur
		// -----------------------------------------------------//

		buf = new StringBuffer();
		buf.append("SELECT es.* ");
		buf.append(" FROM patrimoine_contrat c, patrimoine_site s, patrimoine_ouvrage_socle o, patrimoine_unite_socle u, patrimoine_eqpt_socle e, patrimoine_eqpt_disconnecteur es ");
		buf.append("WHERE c.contrat_id = s.site_contrat_id_dwh ");
		buf.append("AND s.site_id = o.ouvragesocle_site_id_dwh ");
		buf.append("AND o.ouvragesocle_id = u.unitesocle_ouvragesocle_id_dwh ");
		buf.append("AND u.unitesocle_id = e.eqptsocle_idunite_dwh ");
		buf.append("AND e.eqptsocle_id = es.eqptdiscon_eqptsocle_id_dwh ");
		buf.append("AND c.contrat_ve_id LIKE '" + contrat + "' ");
		buf.append("AND es.eqptdiscon_id IN  ");
		buf.append("	( ");
		buf.append("	SELECT t1.eqptdiscon_id   ");
		buf.append("	FROM patrimoine_eqpt_disconnecteur t1 ");
		buf.append("	WHERE (t1.eqptdiscon_eqptsocle_id, t1.id_chg) IN ");
		buf.append("		( ");
		buf.append("		SELECT t.eqptdiscon_eqptsocle_id, MAX(t.id_chg) as id_chg  ");
		buf.append("		FROM patrimoine_eqpt_disconnecteur t ");
		buf.append("		GROUP BY t.eqptdiscon_eqptsocle_id ");
		buf.append("		)  ");
		buf.append("	)  ");

		session = getHibernateTemplate().getCurrentSession();
		List<TypeEquipementDisconnecteur> equipementDisconnecteurs = session.createSQLQuery(buf.toString()).addEntity(TypeEquipementDisconnecteur.class).list();
		session.close();
		System.out.println("Insert " + equipementDisconnecteurs.size() + " lines of patrimoine_eqpt_disconnecteur");

		EquipementsDisconnecteurs equipementDisconnecteur = new EquipementsDisconnecteurs();
		equipementDisconnecteur.setEquipementDisconnecteur(equipementDisconnecteurs);
		patrimoine.setEquipementsDisconnecteurs(equipementDisconnecteur);

		// -----------------------------------------------------//
		// patrimoine_eqpt_echangechaleur
		// -----------------------------------------------------//

		buf = new StringBuffer();
		buf.append("SELECT es.* ");
		buf.append(" FROM patrimoine_contrat c, patrimoine_site s, patrimoine_ouvrage_socle o, patrimoine_unite_socle u, patrimoine_eqpt_socle e, patrimoine_eqpt_echangechaleur es ");
		buf.append("WHERE c.contrat_id = s.site_contrat_id_dwh ");
		buf.append("AND s.site_id = o.ouvragesocle_site_id_dwh ");
		buf.append("AND o.ouvragesocle_id = u.unitesocle_ouvragesocle_id_dwh ");
		buf.append("AND u.unitesocle_id = e.eqptsocle_idunite_dwh ");
		buf.append("AND e.eqptsocle_id = es.eqptechangechal_eqptsocle_id_dwh ");
		buf.append("AND c.contrat_ve_id LIKE '" + contrat + "' ");
		buf.append("AND es.eqptechangechal_id IN  ");
		buf.append("	( ");
		buf.append("	SELECT t1.eqptechangechal_id   ");
		buf.append("	FROM patrimoine_eqpt_echangechaleur t1 ");
		buf.append("	WHERE (t1.eqptechangechal_eqptsocle_id, t1.id_chg) IN ");
		buf.append("		( ");
		buf.append("		SELECT t.eqptechangechal_eqptsocle_id, MAX(t.id_chg) as id_chg  ");
		buf.append("		FROM patrimoine_eqpt_echangechaleur t ");
		buf.append("		GROUP BY t.eqptechangechal_eqptsocle_id ");
		buf.append("		)  ");
		buf.append("	)  ");

		session = getHibernateTemplate().getCurrentSession();
		List<TypeEquipementEchangeurChaleur> equipementEchangeurChaleurs = session.createSQLQuery(buf.toString()).addEntity(TypeEquipementEchangeurChaleur.class).list();
		session.close();
		System.out.println("Insert " + equipementEchangeurChaleurs.size() + " lines of patrimoine_eqpt_echangechaleur");

		EquipementsEchangeursChaleur equipementEchangeurChaleur = new EquipementsEchangeursChaleur();
		equipementEchangeurChaleur.setEquipementEchangeurChaleur(equipementEchangeurChaleurs);
		patrimoine.setEquipementsEchangeursChaleur(equipementEchangeurChaleur);

		// -----------------------------------------------------//
		// patrimoine_eqpt_echelle
		// -----------------------------------------------------//

		buf = new StringBuffer();
		buf.append("SELECT es.* ");
		buf.append(" FROM patrimoine_contrat c, patrimoine_site s, patrimoine_ouvrage_socle o, patrimoine_unite_socle u, patrimoine_eqpt_socle e, patrimoine_eqpt_echelle es ");
		buf.append("WHERE c.contrat_id = s.site_contrat_id_dwh ");
		buf.append("AND s.site_id = o.ouvragesocle_site_id_dwh ");
		buf.append("AND o.ouvragesocle_id = u.unitesocle_ouvragesocle_id_dwh ");
		buf.append("AND u.unitesocle_id = e.eqptsocle_idunite_dwh ");
		buf.append("AND e.eqptsocle_id = es.eqptechelle_eqptsocle_id_dwh ");
		buf.append("AND c.contrat_ve_id LIKE '" + contrat + "' ");
		buf.append("AND es.eqptechelle_id IN  ");
		buf.append("	( ");
		buf.append("	SELECT t1.eqptechelle_id   ");
		buf.append("	FROM patrimoine_eqpt_echelle t1 ");
		buf.append("	WHERE (t1.eqptechelle_eqptsocle_id, t1.id_chg) IN ");
		buf.append("		( ");
		buf.append("		SELECT t.eqptechelle_eqptsocle_id, MAX(t.id_chg) as id_chg  ");
		buf.append("		FROM patrimoine_eqpt_echelle t ");
		buf.append("		GROUP BY t.eqptechelle_eqptsocle_id ");
		buf.append("		)  ");
		buf.append("	)  ");

		session = getHibernateTemplate().getCurrentSession();
		List<TypeEquipementEchelle> equipementEchelles = session.createSQLQuery(buf.toString()).addEntity(TypeEquipementEchelle.class).list();
		session.close();
		System.out.println("Insert " + equipementEchelles.size() + " lines of patrimoine_eqpt_echelle");

		EquipementsEchelles equipementEchelle = new EquipementsEchelles();
		equipementEchelle.setEquipementEchelle(equipementEchelles);
		patrimoine.setEquipementsEchelles(equipementEchelle);

		// -----------------------------------------------------//
		// patrimoine_eqpt_elecref
		// -----------------------------------------------------//

		buf = new StringBuffer();
		buf.append("SELECT es.* ");
		buf.append(" FROM patrimoine_contrat c, patrimoine_site s, patrimoine_ouvrage_socle o, patrimoine_unite_socle u, patrimoine_eqpt_socle e, patrimoine_eqpt_elecref es ");
		buf.append("WHERE c.contrat_id = s.site_contrat_id_dwh ");
		buf.append("AND s.site_id = o.ouvragesocle_site_id_dwh ");
		buf.append("AND o.ouvragesocle_id = u.unitesocle_ouvragesocle_id_dwh ");
		buf.append("AND u.unitesocle_id = e.eqptsocle_idunite_dwh ");
		buf.append("AND e.eqptsocle_id = es.eqptelecref_eqptsocle_id_dwh ");
		buf.append("AND c.contrat_ve_id LIKE '" + contrat + "' ");
		buf.append("AND es.eqptelecref_id IN  ");
		buf.append("	( ");
		buf.append("	SELECT t1.eqptelecref_id   ");
		buf.append("	FROM patrimoine_eqpt_elecref t1 ");
		buf.append("	WHERE (t1.eqptelecref_eqptsocle_id, t1.id_chg) IN ");
		buf.append("		( ");
		buf.append("		SELECT t.eqptelecref_eqptsocle_id, MAX(t.id_chg) as id_chg  ");
		buf.append("		FROM patrimoine_eqpt_elecref t ");
		buf.append("		GROUP BY t.eqptelecref_eqptsocle_id ");
		buf.append("		)  ");
		buf.append("	)  ");

		session = getHibernateTemplate().getCurrentSession();
		List<TypeEquipementElectrodeReference> equipementElectrodeReferences = session.createSQLQuery(buf.toString()).addEntity(TypeEquipementElectrodeReference.class).list();
		session.close();
		System.out.println("Insert " + equipementElectrodeReferences.size() + " lines of patrimoine_eqpt_elecref");

		EquipementsElectrodesReference equipementElectrodeReference = new EquipementsElectrodesReference();
		equipementElectrodeReference.setEquipementElectrodeReference(equipementElectrodeReferences);
		patrimoine.setEquipementsElectrodesReference(equipementElectrodeReference);

		// -----------------------------------------------------//
		// patrimoine_eqpt_enregistreur
		// -----------------------------------------------------//

		buf = new StringBuffer();
		buf.append("SELECT es.* ");
		buf.append(" FROM patrimoine_contrat c, patrimoine_site s, patrimoine_ouvrage_socle o, patrimoine_unite_socle u, patrimoine_eqpt_socle e, patrimoine_eqpt_enregistreur es ");
		buf.append("WHERE c.contrat_id = s.site_contrat_id_dwh ");
		buf.append("AND s.site_id = o.ouvragesocle_site_id_dwh ");
		buf.append("AND o.ouvragesocle_id = u.unitesocle_ouvragesocle_id_dwh ");
		buf.append("AND u.unitesocle_id = e.eqptsocle_idunite_dwh ");
		buf.append("AND e.eqptsocle_id = es.eqptenregist_eqptsocle_id_dwh ");
		buf.append("AND c.contrat_ve_id LIKE '" + contrat + "' ");
		buf.append("AND es.eqptenregist_id IN  ");
		buf.append("	( ");
		buf.append("	SELECT t1.eqptenregist_id   ");
		buf.append("	FROM patrimoine_eqpt_enregistreur t1 ");
		buf.append("	WHERE (t1.eqptenregist_eqptsocle_id, t1.id_chg) IN ");
		buf.append("		( ");
		buf.append("		SELECT t.eqptenregist_eqptsocle_id, MAX(t.id_chg) as id_chg  ");
		buf.append("		FROM patrimoine_eqpt_enregistreur t ");
		buf.append("		GROUP BY t.eqptenregist_eqptsocle_id ");
		buf.append("		)  ");
		buf.append("	)  ");

		session = getHibernateTemplate().getCurrentSession();
		List<TypeEquipementEnregistreur> equipementEnregistreurs = session.createSQLQuery(buf.toString()).addEntity(TypeEquipementEnregistreur.class).list();
		session.close();
		System.out.println("Insert " + equipementEnregistreurs.size() + " lines of patrimoine_eqpt_enregistreur");

		EquipementsEnregistreurs equipementEnregistreur = new EquipementsEnregistreurs();
		equipementEnregistreur.setEquipementEnregistreur(equipementEnregistreurs);
		patrimoine.setEquipementsEnregistreurs(equipementEnregistreur);

		// -----------------------------------------------------//
		// patrimoine_eqpt_espacevert
		// -----------------------------------------------------//

		buf = new StringBuffer();
		buf.append("SELECT es.* ");
		buf.append(" FROM patrimoine_contrat c, patrimoine_site s, patrimoine_ouvrage_socle o, patrimoine_unite_socle u, patrimoine_eqpt_socle e, patrimoine_eqpt_espacevert es ");
		buf.append("WHERE c.contrat_id = s.site_contrat_id_dwh ");
		buf.append("AND s.site_id = o.ouvragesocle_site_id_dwh ");
		buf.append("AND o.ouvragesocle_id = u.unitesocle_ouvragesocle_id_dwh ");
		buf.append("AND u.unitesocle_id = e.eqptsocle_idunite_dwh ");
		buf.append("AND e.eqptsocle_id = es.eqptespvert_eqptsocle_id_dwh ");
		buf.append("AND c.contrat_ve_id LIKE '" + contrat + "' ");
		buf.append("AND es.eqptespvert_id IN  ");
		buf.append("	( ");
		buf.append("	SELECT t1.eqptespvert_id   ");
		buf.append("	FROM patrimoine_eqpt_espacevert t1 ");
		buf.append("	WHERE (t1.eqptespvert_eqptsocle_id, t1.id_chg) IN ");
		buf.append("		( ");
		buf.append("		SELECT t.eqptespvert_eqptsocle_id, MAX(t.id_chg) as id_chg  ");
		buf.append("		FROM patrimoine_eqpt_espacevert t ");
		buf.append("		GROUP BY t.eqptespvert_eqptsocle_id ");
		buf.append("		)  ");
		buf.append("	)  ");

		session = getHibernateTemplate().getCurrentSession();
		List<TypeEquipementEspaceVert> equipementEspaceVerts = session.createSQLQuery(buf.toString()).addEntity(TypeEquipementEspaceVert.class).list();
		session.close();
		System.out.println("Insert " + equipementEspaceVerts.size() + " lines of patrimoine_eqpt_espacevert");

		EquipementsEspacesVert equipementEspaceVert = new EquipementsEspacesVert();
		equipementEspaceVert.setEquipementEspaceVert(equipementEspaceVerts);
		patrimoine.setEquipementsEspacesVert(equipementEspaceVert);

		// -----------------------------------------------------//
		// patrimoine_eqpt_extractair
		// -----------------------------------------------------//

		buf = new StringBuffer();
		buf.append("SELECT es.* ");
		buf.append(" FROM patrimoine_contrat c, patrimoine_site s, patrimoine_ouvrage_socle o, patrimoine_unite_socle u, patrimoine_eqpt_socle e, patrimoine_eqpt_extractair es ");
		buf.append("WHERE c.contrat_id = s.site_contrat_id_dwh ");
		buf.append("AND s.site_id = o.ouvragesocle_site_id_dwh ");
		buf.append("AND o.ouvragesocle_id = u.unitesocle_ouvragesocle_id_dwh ");
		buf.append("AND u.unitesocle_id = e.eqptsocle_idunite_dwh ");
		buf.append("AND e.eqptsocle_id = es.eqptextractair_eqptsocle_id_dwh ");
		buf.append("AND c.contrat_ve_id LIKE '" + contrat + "' ");
		buf.append("AND es.eqptextractair_id IN  ");
		buf.append("	( ");
		buf.append("	SELECT t1.eqptextractair_id   ");
		buf.append("	FROM patrimoine_eqpt_extractair t1 ");
		buf.append("	WHERE (t1.eqptextractair_eqptsocle_id, t1.id_chg) IN ");
		buf.append("		( ");
		buf.append("		SELECT t.eqptextractair_eqptsocle_id, MAX(t.id_chg) as id_chg  ");
		buf.append("		FROM patrimoine_eqpt_extractair t ");
		buf.append("		GROUP BY t.eqptextractair_eqptsocle_id ");
		buf.append("		)  ");
		buf.append("	)  ");

		session = getHibernateTemplate().getCurrentSession();
		List<TypeEquipementExtracteurAir> equipementExtracteurAirs = session.createSQLQuery(buf.toString()).addEntity(TypeEquipementExtracteurAir.class).list();
		session.close();
		System.out.println("Insert " + equipementExtracteurAirs.size() + " lines of patrimoine_eqpt_extractair");

		EquipementsExtracteursAir equipementExtracteurAir = new EquipementsExtracteursAir();
		equipementExtracteurAir.setEquipementExtracteurAir(equipementExtracteurAirs);
		patrimoine.setEquipementsExtracteursAir(equipementExtracteurAir);

		// -----------------------------------------------------//
		// patrimoine_eqpt_fenetre
		// -----------------------------------------------------//

		buf = new StringBuffer();
		buf.append("SELECT es.* ");
		buf.append(" FROM patrimoine_contrat c, patrimoine_site s, patrimoine_ouvrage_socle o, patrimoine_unite_socle u, patrimoine_eqpt_socle e, patrimoine_eqpt_fenetre es ");
		buf.append("WHERE c.contrat_id = s.site_contrat_id_dwh ");
		buf.append("AND s.site_id = o.ouvragesocle_site_id_dwh ");
		buf.append("AND o.ouvragesocle_id = u.unitesocle_ouvragesocle_id_dwh ");
		buf.append("AND u.unitesocle_id = e.eqptsocle_idunite_dwh ");
		buf.append("AND e.eqptsocle_id = es.eqptfenetre_eqptsocle_id_dwh ");
		buf.append("AND c.contrat_ve_id LIKE '" + contrat + "' ");
		buf.append("AND es.eqptfenetre_id IN  ");
		buf.append("	( ");
		buf.append("	SELECT t1.eqptfenetre_id   ");
		buf.append("	FROM patrimoine_eqpt_fenetre t1 ");
		buf.append("	WHERE (t1.eqptfenetre_eqptsocle_id, t1.id_chg) IN ");
		buf.append("		( ");
		buf.append("		SELECT t.eqptfenetre_eqptsocle_id, MAX(t.id_chg) as id_chg  ");
		buf.append("		FROM patrimoine_eqpt_fenetre t ");
		buf.append("		GROUP BY t.eqptfenetre_eqptsocle_id ");
		buf.append("		)  ");
		buf.append("	)  ");

		session = getHibernateTemplate().getCurrentSession();
		List<TypeEquipementFenetre> equipementFenetres = session.createSQLQuery(buf.toString()).addEntity(TypeEquipementFenetre.class).list();
		session.close();
		System.out.println("Insert " + equipementFenetres.size() + " lines of patrimoine_eqpt_fenetre");

		EquipementsFenetres equipementFenetre = new EquipementsFenetres();
		equipementFenetre.setEquipementFenetre(equipementFenetres);
		patrimoine.setEquipementsFenetres(equipementFenetre);

		// -----------------------------------------------------//
		// patrimoine_eqpt_filtreferme
		// -----------------------------------------------------//

		buf = new StringBuffer();
		buf.append("SELECT es.* ");
		buf.append(" FROM patrimoine_contrat c, patrimoine_site s, patrimoine_ouvrage_socle o, patrimoine_unite_socle u, patrimoine_eqpt_socle e, patrimoine_eqpt_filtreferme es ");
		buf.append("WHERE c.contrat_id = s.site_contrat_id_dwh ");
		buf.append("AND s.site_id = o.ouvragesocle_site_id_dwh ");
		buf.append("AND o.ouvragesocle_id = u.unitesocle_ouvragesocle_id_dwh ");
		buf.append("AND u.unitesocle_id = e.eqptsocle_idunite_dwh ");
		buf.append("AND e.eqptsocle_id = es.eqptfiltreferme_eqptsocle_id_dwh ");
		buf.append("AND c.contrat_ve_id LIKE '" + contrat + "' ");
		buf.append("AND es.eqptfiltreferme_id IN  ");
		buf.append("	( ");
		buf.append("	SELECT t1.eqptfiltreferme_id   ");
		buf.append("	FROM patrimoine_eqpt_filtreferme t1 ");
		buf.append("	WHERE (t1.eqptfiltreferme_eqptsocle_id, t1.id_chg) IN ");
		buf.append("		( ");
		buf.append("		SELECT t.eqptfiltreferme_eqptsocle_id, MAX(t.id_chg) as id_chg  ");
		buf.append("		FROM patrimoine_eqpt_filtreferme t ");
		buf.append("		GROUP BY t.eqptfiltreferme_eqptsocle_id ");
		buf.append("		)  ");
		buf.append("	)  ");

		session = getHibernateTemplate().getCurrentSession();
		List<TypeEquipementFiltreFerme> equipementFiltreFermes = session.createSQLQuery(buf.toString()).addEntity(TypeEquipementFiltreFerme.class).list();
		session.close();
		System.out.println("Insert " + equipementFiltreFermes.size() + " lines of patrimoine_eqpt_filtreferme");

		EquipementsFiltresFermes equipementFiltreFerme = new EquipementsFiltresFermes();
		equipementFiltreFerme.setEquipementFiltreFerme(equipementFiltreFermes);
		patrimoine.setEquipementsFiltresFermes(equipementFiltreFerme);

		// -----------------------------------------------------//
		// patrimoine_eqpt_gardecorps
		// -----------------------------------------------------//

		buf = new StringBuffer();
		buf.append("SELECT es.* ");
		buf.append(" FROM patrimoine_contrat c, patrimoine_site s, patrimoine_ouvrage_socle o, patrimoine_unite_socle u, patrimoine_eqpt_socle e, patrimoine_eqpt_gardecorps es ");
		buf.append("WHERE c.contrat_id = s.site_contrat_id_dwh ");
		buf.append("AND s.site_id = o.ouvragesocle_site_id_dwh ");
		buf.append("AND o.ouvragesocle_id = u.unitesocle_ouvragesocle_id_dwh ");
		buf.append("AND u.unitesocle_id = e.eqptsocle_idunite_dwh ");
		buf.append("AND e.eqptsocle_id = es.eqptgardecorps_eqptsocle_id_dwh ");
		buf.append("AND c.contrat_ve_id LIKE '" + contrat + "' ");
		buf.append("AND es.eqptgardecorps_id IN  ");
		buf.append("	( ");
		buf.append("	SELECT t1.eqptgardecorps_id   ");
		buf.append("	FROM patrimoine_eqpt_gardecorps t1 ");
		buf.append("	WHERE (t1.eqptgardecorps_eqptsocle_id, t1.id_chg) IN ");
		buf.append("		( ");
		buf.append("		SELECT t.eqptgardecorps_eqptsocle_id, MAX(t.id_chg) as id_chg  ");
		buf.append("		FROM patrimoine_eqpt_gardecorps t ");
		buf.append("		GROUP BY t.eqptgardecorps_eqptsocle_id ");
		buf.append("		)  ");
		buf.append("	)  ");

		session = getHibernateTemplate().getCurrentSession();
		List<TypeEquipementGardeCorps> equipementGardeCorpss = session.createSQLQuery(buf.toString()).addEntity(TypeEquipementGardeCorps.class).list();
		session.close();
		System.out.println("Insert " + equipementGardeCorpss.size() + " lines of patrimoine_eqpt_gardecorps");

		EquipementsGardesCorps equipementGardeCorps = new EquipementsGardesCorps();
		equipementGardeCorps.setEquipementGardeCorps(equipementGardeCorpss);
		patrimoine.setEquipementsGardesCorps(equipementGardeCorps);

		// -----------------------------------------------------//
		// patrimoine_eqpt_generateuruv
		// -----------------------------------------------------//

		buf = new StringBuffer();
		buf.append("SELECT es.* ");
		buf.append(" FROM patrimoine_contrat c, patrimoine_site s, patrimoine_ouvrage_socle o, patrimoine_unite_socle u, patrimoine_eqpt_socle e, patrimoine_eqpt_generateuruv es ");
		buf.append("WHERE c.contrat_id = s.site_contrat_id_dwh ");
		buf.append("AND s.site_id = o.ouvragesocle_site_id_dwh ");
		buf.append("AND o.ouvragesocle_id = u.unitesocle_ouvragesocle_id_dwh ");
		buf.append("AND u.unitesocle_id = e.eqptsocle_idunite_dwh ");
		buf.append("AND e.eqptsocle_id = es.eqptgenerateuruv_eqptsocle_id_dwh ");
		buf.append("AND c.contrat_ve_id LIKE '" + contrat + "' ");
		buf.append("AND es.eqptgenerateuruv_id IN  ");
		buf.append("	( ");
		buf.append("	SELECT t1.eqptgenerateuruv_id   ");
		buf.append("	FROM patrimoine_eqpt_generateuruv t1 ");
		buf.append("	WHERE (t1.eqptgenerateuruv_eqptsocle_id, t1.id_chg) IN ");
		buf.append("		( ");
		buf.append("		SELECT t.eqptgenerateuruv_eqptsocle_id, MAX(t.id_chg) as id_chg  ");
		buf.append("		FROM patrimoine_eqpt_generateuruv t ");
		buf.append("		GROUP BY t.eqptgenerateuruv_eqptsocle_id ");
		buf.append("		)  ");
		buf.append("	)  ");

		session = getHibernateTemplate().getCurrentSession();
		List<TypeEquipementGenerateurUV> equipementGenerateurUVs = session.createSQLQuery(buf.toString()).addEntity(TypeEquipementGenerateurUV.class).list();
		session.close();
		System.out.println("Insert " + equipementGenerateurUVs.size() + " lines of patrimoine_eqpt_generateuruv");

		EquipementsGenerateursUV equipementGenerateurUV = new EquipementsGenerateursUV();
		equipementGenerateurUV.setEquipementGenerateurUV(equipementGenerateurUVs);
		patrimoine.setEquipementsGenerateursUV(equipementGenerateurUV);

		// -----------------------------------------------------//
		// patrimoine_eqpt_grappin
		// -----------------------------------------------------//

		buf = new StringBuffer();
		buf.append("SELECT es.* ");
		buf.append(" FROM patrimoine_contrat c, patrimoine_site s, patrimoine_ouvrage_socle o, patrimoine_unite_socle u, patrimoine_eqpt_socle e, patrimoine_eqpt_grappin es ");
		buf.append("WHERE c.contrat_id = s.site_contrat_id_dwh ");
		buf.append("AND s.site_id = o.ouvragesocle_site_id_dwh ");
		buf.append("AND o.ouvragesocle_id = u.unitesocle_ouvragesocle_id_dwh ");
		buf.append("AND u.unitesocle_id = e.eqptsocle_idunite_dwh ");
		buf.append("AND e.eqptsocle_id = es.eqptgrappin_eqptsocle_id_dwh ");
		buf.append("AND c.contrat_ve_id LIKE '" + contrat + "' ");
		buf.append("AND es.eqptgrappin_id IN  ");
		buf.append("	( ");
		buf.append("	SELECT t1.eqptgrappin_id   ");
		buf.append("	FROM patrimoine_eqpt_grappin t1 ");
		buf.append("	WHERE (t1.eqptgrappin_eqptsocle_id, t1.id_chg) IN ");
		buf.append("		( ");
		buf.append("		SELECT t.eqptgrappin_eqptsocle_id, MAX(t.id_chg) as id_chg  ");
		buf.append("		FROM patrimoine_eqpt_grappin t ");
		buf.append("		GROUP BY t.eqptgrappin_eqptsocle_id ");
		buf.append("		)  ");
		buf.append("	)  ");

		session = getHibernateTemplate().getCurrentSession();
		List<TypeEquipementGrappin> equipementGrappins = session.createSQLQuery(buf.toString()).addEntity(TypeEquipementGrappin.class).list();
		session.close();
		System.out.println("Insert " + equipementGrappins.size() + " lines of patrimoine_eqpt_grappin");

		EquipementsGrappins equipementGrappin = new EquipementsGrappins();
		equipementGrappin.setEquipementGrappin(equipementGrappins);
		patrimoine.setEquipementsGrappins(equipementGrappin);

		// -----------------------------------------------------//
		// patrimoine_eqpt_groupeair
		// -----------------------------------------------------//

		buf = new StringBuffer();
		buf.append("SELECT es.* ");
		buf.append(" FROM patrimoine_contrat c, patrimoine_site s, patrimoine_ouvrage_socle o, patrimoine_unite_socle u, patrimoine_eqpt_socle e, patrimoine_eqpt_groupeair es ");
		buf.append("WHERE c.contrat_id = s.site_contrat_id_dwh ");
		buf.append("AND s.site_id = o.ouvragesocle_site_id_dwh ");
		buf.append("AND o.ouvragesocle_id = u.unitesocle_ouvragesocle_id_dwh ");
		buf.append("AND u.unitesocle_id = e.eqptsocle_idunite_dwh ");
		buf.append("AND e.eqptsocle_id = es.eqptgroupeair_eqptsocle_id_dwh ");
		buf.append("AND c.contrat_ve_id LIKE '" + contrat + "' ");
		buf.append("AND es.eqptgroupeair_id IN  ");
		buf.append("	( ");
		buf.append("	SELECT t1.eqptgroupeair_id   ");
		buf.append("	FROM patrimoine_eqpt_groupeair t1 ");
		buf.append("	WHERE (t1.eqptgroupeair_eqptsocle_id, t1.id_chg) IN ");
		buf.append("		( ");
		buf.append("		SELECT t.eqptgroupeair_eqptsocle_id, MAX(t.id_chg) as id_chg  ");
		buf.append("		FROM patrimoine_eqpt_groupeair t ");
		buf.append("		GROUP BY t.eqptgroupeair_eqptsocle_id ");
		buf.append("		)  ");
		buf.append("	)  ");

		session = getHibernateTemplate().getCurrentSession();
		List<TypeEquipementGroupeAir> equipementGroupeAirs = session.createSQLQuery(buf.toString()).addEntity(TypeEquipementGroupeAir.class).list();
		session.close();
		System.out.println("Insert " + equipementGroupeAirs.size() + " lines of patrimoine_eqpt_groupeair");

		EquipementsGroupesAir equipementGroupeAir = new EquipementsGroupesAir();
		equipementGroupeAir.setEquipementGroupeAir(equipementGroupeAirs);
		patrimoine.setEquipementsGroupesAir(equipementGroupeAir);

		// -----------------------------------------------------//
		// patrimoine_eqpt_groupedosage
		// -----------------------------------------------------//

		buf = new StringBuffer();
		buf.append("SELECT es.* ");
		buf.append(" FROM patrimoine_contrat c, patrimoine_site s, patrimoine_ouvrage_socle o, patrimoine_unite_socle u, patrimoine_eqpt_socle e, patrimoine_eqpt_groupedosage es ");
		buf.append("WHERE c.contrat_id = s.site_contrat_id_dwh ");
		buf.append("AND s.site_id = o.ouvragesocle_site_id_dwh ");
		buf.append("AND o.ouvragesocle_id = u.unitesocle_ouvragesocle_id_dwh ");
		buf.append("AND u.unitesocle_id = e.eqptsocle_idunite_dwh ");
		buf.append("AND e.eqptsocle_id = es.eqptgrpdosage_eqptsocle_id_dwh ");
		buf.append("AND c.contrat_ve_id LIKE '" + contrat + "' ");
		buf.append("AND es.eqptgrpdosage_id IN  ");
		buf.append("	( ");
		buf.append("	SELECT t1.eqptgrpdosage_id   ");
		buf.append("	FROM patrimoine_eqpt_groupedosage t1 ");
		buf.append("	WHERE (t1.eqptgrpdosage_eqptsocle_id, t1.id_chg) IN ");
		buf.append("		( ");
		buf.append("		SELECT t.eqptgrpdosage_eqptsocle_id, MAX(t.id_chg) as id_chg  ");
		buf.append("		FROM patrimoine_eqpt_groupedosage t ");
		buf.append("		GROUP BY t.eqptgrpdosage_eqptsocle_id ");
		buf.append("		)  ");
		buf.append("	)  ");

		session = getHibernateTemplate().getCurrentSession();
		List<TypeEquipementGroupeDosage> equipementGroupeDosages = session.createSQLQuery(buf.toString()).addEntity(TypeEquipementGroupeDosage.class).list();
		session.close();
		System.out.println("Insert " + equipementGroupeDosages.size() + " lines of patrimoine_eqpt_groupedosage");

		EquipementsGroupesDosage equipementGroupeDosage = new EquipementsGroupesDosage();
		equipementGroupeDosage.setEquipementGroupeDosage(equipementGroupeDosages);
		patrimoine.setEquipementsGroupesDosage(equipementGroupeDosage);

		// -----------------------------------------------------//
		// patrimoine_eqpt_groupeelec
		// -----------------------------------------------------//

		buf = new StringBuffer();
		buf.append("SELECT es.* ");
		buf.append(" FROM patrimoine_contrat c, patrimoine_site s, patrimoine_ouvrage_socle o, patrimoine_unite_socle u, patrimoine_eqpt_socle e, patrimoine_eqpt_groupeelec es ");
		buf.append("WHERE c.contrat_id = s.site_contrat_id_dwh ");
		buf.append("AND s.site_id = o.ouvragesocle_site_id_dwh ");
		buf.append("AND o.ouvragesocle_id = u.unitesocle_ouvragesocle_id_dwh ");
		buf.append("AND u.unitesocle_id = e.eqptsocle_idunite_dwh ");
		buf.append("AND e.eqptsocle_id = es.eqptgroupeelec_eqptsocle_id_dwh ");
		buf.append("AND c.contrat_ve_id LIKE '" + contrat + "' ");
		buf.append("AND es.eqptgroupeelec_id IN  ");
		buf.append("	( ");
		buf.append("	SELECT t1.eqptgroupeelec_id   ");
		buf.append("	FROM patrimoine_eqpt_groupeelec t1 ");
		buf.append("	WHERE (t1.eqptgroupeelec_eqptsocle_id, t1.id_chg) IN ");
		buf.append("		( ");
		buf.append("		SELECT t.eqptgroupeelec_eqptsocle_id, MAX(t.id_chg) as id_chg  ");
		buf.append("		FROM patrimoine_eqpt_groupeelec t ");
		buf.append("		GROUP BY t.eqptgroupeelec_eqptsocle_id ");
		buf.append("		)  ");
		buf.append("	)  ");

		session = getHibernateTemplate().getCurrentSession();
		List<TypeEquipementGroupeElectrogene> equipementGroupeElectrogenes = session.createSQLQuery(buf.toString()).addEntity(TypeEquipementGroupeElectrogene.class).list();
		session.close();
		System.out.println("Insert " + equipementGroupeElectrogenes.size() + " lines of patrimoine_eqpt_groupeelec");

		EquipementsGroupesElectrogenes equipementGroupeElectrogene = new EquipementsGroupesElectrogenes();
		equipementGroupeElectrogene.setEquipementGroupeElectrogene(equipementGroupeElectrogenes);
		patrimoine.setEquipementsGroupesElectrogenes(equipementGroupeElectrogene);

		// -----------------------------------------------------//
		// patrimoine_eqpt_groupemalaxeur
		// -----------------------------------------------------//

		buf = new StringBuffer();
		buf.append("SELECT es.* ");
		buf.append(" FROM patrimoine_contrat c, patrimoine_site s, patrimoine_ouvrage_socle o, patrimoine_unite_socle u, patrimoine_eqpt_socle e, patrimoine_eqpt_groupemalaxeur es ");
		buf.append("WHERE c.contrat_id = s.site_contrat_id_dwh ");
		buf.append("AND s.site_id = o.ouvragesocle_site_id_dwh ");
		buf.append("AND o.ouvragesocle_id = u.unitesocle_ouvragesocle_id_dwh ");
		buf.append("AND u.unitesocle_id = e.eqptsocle_idunite_dwh ");
		buf.append("AND e.eqptsocle_id = es.eqptgroupemalax_eqptsocle_id_dwh ");
		buf.append("AND c.contrat_ve_id LIKE '" + contrat + "' ");
		buf.append("AND es.eqptgroupemalax_id IN  ");
		buf.append("	( ");
		buf.append("	SELECT t1.eqptgroupemalax_id   ");
		buf.append("	FROM patrimoine_eqpt_groupemalaxeur t1 ");
		buf.append("	WHERE (t1.eqptgroupemalax_eqptsocle_id, t1.id_chg) IN ");
		buf.append("		( ");
		buf.append("		SELECT t.eqptgroupemalax_eqptsocle_id, MAX(t.id_chg) as id_chg  ");
		buf.append("		FROM patrimoine_eqpt_groupemalaxeur t ");
		buf.append("		GROUP BY t.eqptgroupemalax_eqptsocle_id ");
		buf.append("		)  ");
		buf.append("	)  ");

		session = getHibernateTemplate().getCurrentSession();
		List<TypeEquipementGroupeMalaxeur> equipementGroupeMalaxeurs = session.createSQLQuery(buf.toString()).addEntity(TypeEquipementGroupeMalaxeur.class).list();
		session.close();
		System.out.println("Insert " + equipementGroupeMalaxeurs.size() + " lines of patrimoine_eqpt_groupemalaxeur");

		EquipementsGroupesMalaxeurs equipementGroupeMalaxeur = new EquipementsGroupesMalaxeurs();
		equipementGroupeMalaxeur.setEquipementGroupeMalaxeur(equipementGroupeMalaxeurs);
		patrimoine.setEquipementsGroupesMalaxeurs(equipementGroupeMalaxeur);

		// -----------------------------------------------------//
		// patrimoine_eqpt_grouperefroi
		// -----------------------------------------------------//

		buf = new StringBuffer();
		buf.append("SELECT es.* ");
		buf.append(" FROM patrimoine_contrat c, patrimoine_site s, patrimoine_ouvrage_socle o, patrimoine_unite_socle u, patrimoine_eqpt_socle e, patrimoine_eqpt_grouperefroi es ");
		buf.append("WHERE c.contrat_id = s.site_contrat_id_dwh ");
		buf.append("AND s.site_id = o.ouvragesocle_site_id_dwh ");
		buf.append("AND o.ouvragesocle_id = u.unitesocle_ouvragesocle_id_dwh ");
		buf.append("AND u.unitesocle_id = e.eqptsocle_idunite_dwh ");
		buf.append("AND e.eqptsocle_id = es.eqptgrouperefroi_eqptsocle_id_dwh ");
		buf.append("AND c.contrat_ve_id LIKE '" + contrat + "' ");
		buf.append("AND es.eqptgrouperefroi_id IN  ");
		buf.append("	( ");
		buf.append("	SELECT t1.eqptgrouperefroi_id   ");
		buf.append("	FROM patrimoine_eqpt_grouperefroi t1 ");
		buf.append("	WHERE (t1.eqptgrouperefroi_eqptsocle_id, t1.id_chg) IN ");
		buf.append("		( ");
		buf.append("		SELECT t.eqptgrouperefroi_eqptsocle_id, MAX(t.id_chg) as id_chg  ");
		buf.append("		FROM patrimoine_eqpt_grouperefroi t ");
		buf.append("		GROUP BY t.eqptgrouperefroi_eqptsocle_id ");
		buf.append("		)  ");
		buf.append("	)  ");

		session = getHibernateTemplate().getCurrentSession();
		List<TypeEquipementGroupeRefroidissement> equipementGroupeRefroidissements = session.createSQLQuery(buf.toString()).addEntity(TypeEquipementGroupeRefroidissement.class).list();
		session.close();
		System.out.println("Insert " + equipementGroupeRefroidissements.size() + " lines of patrimoine_eqpt_grouperefroi");

		EquipementsGroupesRefroidissement equipementGroupeRefroidissement = new EquipementsGroupesRefroidissement();
		equipementGroupeRefroidissement.setEquipementGroupeRefroidissement(equipementGroupeRefroidissements);
		patrimoine.setEquipementsGroupesRefroidissement(equipementGroupeRefroidissement);

		// -----------------------------------------------------//
		// patrimoine_eqpt_herse
		// -----------------------------------------------------//

		buf = new StringBuffer();
		buf.append("SELECT es.* ");
		buf.append(" FROM patrimoine_contrat c, patrimoine_site s, patrimoine_ouvrage_socle o, patrimoine_unite_socle u, patrimoine_eqpt_socle e, patrimoine_eqpt_herse es ");
		buf.append("WHERE c.contrat_id = s.site_contrat_id_dwh ");
		buf.append("AND s.site_id = o.ouvragesocle_site_id_dwh ");
		buf.append("AND o.ouvragesocle_id = u.unitesocle_ouvragesocle_id_dwh ");
		buf.append("AND u.unitesocle_id = e.eqptsocle_idunite_dwh ");
		buf.append("AND e.eqptsocle_id = es.eqptherse_eqptsocle_id_dwh ");
		buf.append("AND c.contrat_ve_id LIKE '" + contrat + "' ");
		buf.append("AND es.eqptherse_id IN  ");
		buf.append("	( ");
		buf.append("	SELECT t1.eqptherse_id   ");
		buf.append("	FROM patrimoine_eqpt_herse t1 ");
		buf.append("	WHERE (t1.eqptherse_eqptsocle_id, t1.id_chg) IN ");
		buf.append("		( ");
		buf.append("		SELECT t.eqptherse_eqptsocle_id, MAX(t.id_chg) as id_chg  ");
		buf.append("		FROM patrimoine_eqpt_herse t ");
		buf.append("		GROUP BY t.eqptherse_eqptsocle_id ");
		buf.append("		)  ");
		buf.append("	)  ");

		session = getHibernateTemplate().getCurrentSession();
		List<TypeEquipementHerse> equipementHerses = session.createSQLQuery(buf.toString()).addEntity(TypeEquipementHerse.class).list();
		session.close();
		System.out.println("Insert " + equipementHerses.size() + " lines of patrimoine_eqpt_herse");

		EquipementsHerses equipementHerse = new EquipementsHerses();
		equipementHerse.setEquipementHerse(equipementHerses);
		patrimoine.setEquipementsHerses(equipementHerse);

		// -----------------------------------------------------//
		// patrimoine_eqpt_hydrocyclone
		// -----------------------------------------------------//

		buf = new StringBuffer();
		buf.append("SELECT es.* ");
		buf.append(" FROM patrimoine_contrat c, patrimoine_site s, patrimoine_ouvrage_socle o, patrimoine_unite_socle u, patrimoine_eqpt_socle e, patrimoine_eqpt_hydrocyclone es ");
		buf.append("WHERE c.contrat_id = s.site_contrat_id_dwh ");
		buf.append("AND s.site_id = o.ouvragesocle_site_id_dwh ");
		buf.append("AND o.ouvragesocle_id = u.unitesocle_ouvragesocle_id_dwh ");
		buf.append("AND u.unitesocle_id = e.eqptsocle_idunite_dwh ");
		buf.append("AND e.eqptsocle_id = es.eqpthydrocycl_eqptsocle_id_dwh ");
		buf.append("AND c.contrat_ve_id LIKE '" + contrat + "' ");
		buf.append("AND es.eqpthydrocycl_id IN  ");
		buf.append("	( ");
		buf.append("	SELECT t1.eqpthydrocycl_id   ");
		buf.append("	FROM patrimoine_eqpt_hydrocyclone t1 ");
		buf.append("	WHERE (t1.eqpthydrocycl_eqptsocle_id, t1.id_chg) IN ");
		buf.append("		( ");
		buf.append("		SELECT t.eqpthydrocycl_eqptsocle_id, MAX(t.id_chg) as id_chg  ");
		buf.append("		FROM patrimoine_eqpt_hydrocyclone t ");
		buf.append("		GROUP BY t.eqpthydrocycl_eqptsocle_id ");
		buf.append("		)  ");
		buf.append("	)  ");

		session = getHibernateTemplate().getCurrentSession();
		List<TypeEquipementHydrocyclone> equipementHydrocyclones = session.createSQLQuery(buf.toString()).addEntity(TypeEquipementHydrocyclone.class).list();
		session.close();
		System.out.println("Insert " + equipementHydrocyclones.size() + " lines of patrimoine_eqpt_hydrocyclone");

		EquipementsHydrocyclones equipementHydrocyclone = new EquipementsHydrocyclones();
		equipementHydrocyclone.setEquipementHydrocyclone(equipementHydrocyclones);
		patrimoine.setEquipementsHydrocyclones(equipementHydrocyclone);

		// -----------------------------------------------------//
		// patrimoine_eqpt_hydroejecteur
		// -----------------------------------------------------//

		buf = new StringBuffer();
		buf.append("SELECT es.* ");
		buf.append(" FROM patrimoine_contrat c, patrimoine_site s, patrimoine_ouvrage_socle o, patrimoine_unite_socle u, patrimoine_eqpt_socle e, patrimoine_eqpt_hydroejecteur es ");
		buf.append("WHERE c.contrat_id = s.site_contrat_id_dwh ");
		buf.append("AND s.site_id = o.ouvragesocle_site_id_dwh ");
		buf.append("AND o.ouvragesocle_id = u.unitesocle_ouvragesocle_id_dwh ");
		buf.append("AND u.unitesocle_id = e.eqptsocle_idunite_dwh ");
		buf.append("AND e.eqptsocle_id = es.eqpthydroejec_eqptsocle_id_dwh ");
		buf.append("AND c.contrat_ve_id LIKE '" + contrat + "' ");
		buf.append("AND es.eqpthydroejec_id IN  ");
		buf.append("	( ");
		buf.append("	SELECT t1.eqpthydroejec_id   ");
		buf.append("	FROM patrimoine_eqpt_hydroejecteur t1 ");
		buf.append("	WHERE (t1.eqpthydroejec_eqptsocle_id, t1.id_chg) IN ");
		buf.append("		( ");
		buf.append("		SELECT t.eqpthydroejec_eqptsocle_id, MAX(t.id_chg) as id_chg  ");
		buf.append("		FROM patrimoine_eqpt_hydroejecteur t ");
		buf.append("		GROUP BY t.eqpthydroejec_eqptsocle_id ");
		buf.append("		)  ");
		buf.append("	)  ");

		session = getHibernateTemplate().getCurrentSession();
		List<TypeEquipementHydroejecteur> equipementHydroejecteurs = session.createSQLQuery(buf.toString()).addEntity(TypeEquipementHydroejecteur.class).list();
		session.close();
		System.out.println("Insert " + equipementHydroejecteurs.size() + " lines of patrimoine_eqpt_hydroejecteur");

		EquipementsHydroejecteurs equipementHydroejecteur = new EquipementsHydroejecteurs();
		equipementHydroejecteur.setEquipementHydroejecteur(equipementHydroejecteurs);
		patrimoine.setEquipementsHydroejecteurs(equipementHydroejecteur);

		// -----------------------------------------------------//
		// patrimoine_eqpt_liaisonele
		// -----------------------------------------------------//

		buf = new StringBuffer();
		buf.append("SELECT es.* ");
		buf.append(" FROM patrimoine_contrat c, patrimoine_site s, patrimoine_ouvrage_socle o, patrimoine_unite_socle u, patrimoine_eqpt_socle e, patrimoine_eqpt_liaisonele es ");
		buf.append("WHERE c.contrat_id = s.site_contrat_id_dwh ");
		buf.append("AND s.site_id = o.ouvragesocle_site_id_dwh ");
		buf.append("AND o.ouvragesocle_id = u.unitesocle_ouvragesocle_id_dwh ");
		buf.append("AND u.unitesocle_id = e.eqptsocle_idunite_dwh ");
		buf.append("AND e.eqptsocle_id = es.eqptliaisonele_eqptsocle_id_dwh ");
		buf.append("AND c.contrat_ve_id LIKE '" + contrat + "' ");
		buf.append("AND es.eqptliaisonele_id IN  ");
		buf.append("	( ");
		buf.append("	SELECT t1.eqptliaisonele_id   ");
		buf.append("	FROM patrimoine_eqpt_liaisonele t1 ");
		buf.append("	WHERE (t1.eqptliaisonele_eqptsocle_id, t1.id_chg) IN ");
		buf.append("		( ");
		buf.append("		SELECT t.eqptliaisonele_eqptsocle_id, MAX(t.id_chg) as id_chg  ");
		buf.append("		FROM patrimoine_eqpt_liaisonele t ");
		buf.append("		GROUP BY t.eqptliaisonele_eqptsocle_id ");
		buf.append("		)  ");
		buf.append("	)  ");

		session = getHibernateTemplate().getCurrentSession();
		List<TypeEquipementLiaisonElectrique> equipementLiaisonElectriques = session.createSQLQuery(buf.toString()).addEntity(TypeEquipementLiaisonElectrique.class).list();
		session.close();
		System.out.println("Insert " + equipementLiaisonElectriques.size() + " lines of patrimoine_eqpt_liaisonele");

		EquipementsLiaisonsElectriques equipementLiaisonElectrique = new EquipementsLiaisonsElectriques();
		equipementLiaisonElectrique.setEquipementLiaisonElectrique(equipementLiaisonElectriques);
		patrimoine.setEquipementsLiaisonsElectriques(equipementLiaisonElectrique);

		// -----------------------------------------------------//
		// patrimoine_eqpt_massanodique
		// -----------------------------------------------------//

		buf = new StringBuffer();
		buf.append("SELECT es.* ");
		buf.append(" FROM patrimoine_contrat c, patrimoine_site s, patrimoine_ouvrage_socle o, patrimoine_unite_socle u, patrimoine_eqpt_socle e, patrimoine_eqpt_massanodique es ");
		buf.append("WHERE c.contrat_id = s.site_contrat_id_dwh ");
		buf.append("AND s.site_id = o.ouvragesocle_site_id_dwh ");
		buf.append("AND o.ouvragesocle_id = u.unitesocle_ouvragesocle_id_dwh ");
		buf.append("AND u.unitesocle_id = e.eqptsocle_idunite_dwh ");
		buf.append("AND e.eqptsocle_id = es.eqptmasseanod_eqptsocle_id_dwh ");
		buf.append("AND c.contrat_ve_id LIKE '" + contrat + "' ");
		buf.append("AND es.eqptmasseanod_id IN  ");
		buf.append("	( ");
		buf.append("	SELECT t1.eqptmasseanod_id   ");
		buf.append("	FROM patrimoine_eqpt_massanodique t1 ");
		buf.append("	WHERE (t1.eqptmasseanod_eqptsocle_id, t1.id_chg) IN ");
		buf.append("		( ");
		buf.append("		SELECT t.eqptmasseanod_eqptsocle_id, MAX(t.id_chg) as id_chg  ");
		buf.append("		FROM patrimoine_eqpt_massanodique t ");
		buf.append("		GROUP BY t.eqptmasseanod_eqptsocle_id ");
		buf.append("		)  ");
		buf.append("	)  ");

		session = getHibernateTemplate().getCurrentSession();
		List<TypeEquipementMasseAnodique> equipementMasseAnodiques = session.createSQLQuery(buf.toString()).addEntity(TypeEquipementMasseAnodique.class).list();
		session.close();
		System.out.println("Insert " + equipementMasseAnodiques.size() + " lines of patrimoine_eqpt_massanodique");

		EquipementsMassesAnodiques equipementMasseAnodique = new EquipementsMassesAnodiques();
		equipementMasseAnodique.setEquipementMasseAnodique(equipementMasseAnodiques);
		patrimoine.setEquipementsMassesAnodiques(equipementMasseAnodique);

		// -----------------------------------------------------//
		// patrimoine_eqpt_materiellabo
		// -----------------------------------------------------//

		buf = new StringBuffer();
		buf.append("SELECT es.* ");
		buf.append(" FROM patrimoine_contrat c, patrimoine_site s, patrimoine_ouvrage_socle o, patrimoine_unite_socle u, patrimoine_eqpt_socle e, patrimoine_eqpt_materiellabo es ");
		buf.append("WHERE c.contrat_id = s.site_contrat_id_dwh ");
		buf.append("AND s.site_id = o.ouvragesocle_site_id_dwh ");
		buf.append("AND o.ouvragesocle_id = u.unitesocle_ouvragesocle_id_dwh ");
		buf.append("AND u.unitesocle_id = e.eqptsocle_idunite_dwh ");
		buf.append("AND e.eqptsocle_id = es.eqptmaterlabo_eqptsocle_id_dwh ");
		buf.append("AND c.contrat_ve_id LIKE '" + contrat + "' ");
		buf.append("AND es.eqptmaterlabo_id IN  ");
		buf.append("	( ");
		buf.append("	SELECT t1.eqptmaterlabo_id   ");
		buf.append("	FROM patrimoine_eqpt_materiellabo t1 ");
		buf.append("	WHERE (t1.eqptmaterlabo_eqptsocle_id, t1.id_chg) IN ");
		buf.append("		( ");
		buf.append("		SELECT t.eqptmaterlabo_eqptsocle_id, MAX(t.id_chg) as id_chg  ");
		buf.append("		FROM patrimoine_eqpt_materiellabo t ");
		buf.append("		GROUP BY t.eqptmaterlabo_eqptsocle_id ");
		buf.append("		)  ");
		buf.append("	)  ");

		session = getHibernateTemplate().getCurrentSession();
		List<TypeEquipementMaterielLaboratoire> equipementMaterielLaboratoires = session.createSQLQuery(buf.toString()).addEntity(TypeEquipementMaterielLaboratoire.class).list();
		session.close();
		System.out.println("Insert " + equipementMaterielLaboratoires.size() + " lines of patrimoine_eqpt_materiellabo");

		EquipementsMaterielsLaboratoire equipementMaterielLaboratoire = new EquipementsMaterielsLaboratoire();
		equipementMaterielLaboratoire.setEquipementMaterielLaboratoire(equipementMaterielLaboratoires);
		patrimoine.setEquipementsMaterielsLaboratoire(equipementMaterielLaboratoire);

		// -----------------------------------------------------//
		// patrimoine_eqpt_melangeurstatique
		// -----------------------------------------------------//

		buf = new StringBuffer();
		buf.append("SELECT es.* ");
		buf.append(" FROM patrimoine_contrat c, patrimoine_site s, patrimoine_ouvrage_socle o, patrimoine_unite_socle u, patrimoine_eqpt_socle e, patrimoine_eqpt_melangeurstatique es ");
		buf.append("WHERE c.contrat_id = s.site_contrat_id_dwh ");
		buf.append("AND s.site_id = o.ouvragesocle_site_id_dwh ");
		buf.append("AND o.ouvragesocle_id = u.unitesocle_ouvragesocle_id_dwh ");
		buf.append("AND u.unitesocle_id = e.eqptsocle_idunite_dwh ");
		buf.append("AND e.eqptsocle_id = es.eqptmelangeurstat_eqptsocle_id_dwh ");
		buf.append("AND c.contrat_ve_id LIKE '" + contrat + "' ");
		buf.append("AND es.eqptmelangeurstat_id IN  ");
		buf.append("	( ");
		buf.append("	SELECT t1.eqptmelangeurstat_id   ");
		buf.append("	FROM patrimoine_eqpt_melangeurstatique t1 ");
		buf.append("	WHERE (t1.eqptmelangeurstat_eqptsocle_id, t1.id_chg) IN ");
		buf.append("		( ");
		buf.append("		SELECT t.eqptmelangeurstat_eqptsocle_id, MAX(t.id_chg) as id_chg  ");
		buf.append("		FROM patrimoine_eqpt_melangeurstatique t ");
		buf.append("		GROUP BY t.eqptmelangeurstat_eqptsocle_id ");
		buf.append("		)  ");
		buf.append("	)  ");

		session = getHibernateTemplate().getCurrentSession();
		List<TypeEquipementMelangeurStatique> equipementMelangeurStatiques = session.createSQLQuery(buf.toString()).addEntity(TypeEquipementMelangeurStatique.class).list();
		session.close();
		System.out.println("Insert " + equipementMelangeurStatiques.size() + " lines of patrimoine_eqpt_melangeurstatique");

		EquipementsMelangeursStatiques equipementMelangeurStatique = new EquipementsMelangeursStatiques();
		equipementMelangeurStatique.setEquipementMelangeurStatique(equipementMelangeurStatiques);
		patrimoine.setEquipementsMelangeursStatiques(equipementMelangeurStatique);

		// -----------------------------------------------------//
		// patrimoine_eqpt_membraneuf
		// -----------------------------------------------------//

		buf = new StringBuffer();
		buf.append("SELECT es.* ");
		buf.append(" FROM patrimoine_contrat c, patrimoine_site s, patrimoine_ouvrage_socle o, patrimoine_unite_socle u, patrimoine_eqpt_socle e, patrimoine_eqpt_membraneuf es ");
		buf.append("WHERE c.contrat_id = s.site_contrat_id_dwh ");
		buf.append("AND s.site_id = o.ouvragesocle_site_id_dwh ");
		buf.append("AND o.ouvragesocle_id = u.unitesocle_ouvragesocle_id_dwh ");
		buf.append("AND u.unitesocle_id = e.eqptsocle_idunite_dwh ");
		buf.append("AND e.eqptsocle_id = es.eqptmembraneuf_eqptsocle_id_dwh ");
		buf.append("AND c.contrat_ve_id LIKE '" + contrat + "' ");
		buf.append("AND es.eqptmembraneuf_id IN  ");
		buf.append("	( ");
		buf.append("	SELECT t1.eqptmembraneuf_id   ");
		buf.append("	FROM patrimoine_eqpt_membraneuf t1 ");
		buf.append("	WHERE (t1.eqptmembraneuf_eqptsocle_id, t1.id_chg) IN ");
		buf.append("		( ");
		buf.append("		SELECT t.eqptmembraneuf_eqptsocle_id, MAX(t.id_chg) as id_chg  ");
		buf.append("		FROM patrimoine_eqpt_membraneuf t ");
		buf.append("		GROUP BY t.eqptmembraneuf_eqptsocle_id ");
		buf.append("		)  ");
		buf.append("	)  ");

		session = getHibernateTemplate().getCurrentSession();
		List<TypeEquipementMembraneUF> equipementMembraneUFs = session.createSQLQuery(buf.toString()).addEntity(TypeEquipementMembraneUF.class).list();
		session.close();
		System.out.println("Insert " + equipementMembraneUFs.size() + " lines of patrimoine_eqpt_membraneuf");

		EquipementsMembranesUF equipementMembraneUF = new EquipementsMembranesUF();
		equipementMembraneUF.setEquipementMembraneUF(equipementMembraneUFs);
		patrimoine.setEquipementsMembranesUF(equipementMembraneUF);

		// -----------------------------------------------------//
		// patrimoine_eqpt_modulelamellaire
		// -----------------------------------------------------//

		buf = new StringBuffer();
		buf.append("SELECT es.* ");
		buf.append(" FROM patrimoine_contrat c, patrimoine_site s, patrimoine_ouvrage_socle o, patrimoine_unite_socle u, patrimoine_eqpt_socle e, patrimoine_eqpt_modulelamellaire es ");
		buf.append("WHERE c.contrat_id = s.site_contrat_id_dwh ");
		buf.append("AND s.site_id = o.ouvragesocle_site_id_dwh ");
		buf.append("AND o.ouvragesocle_id = u.unitesocle_ouvragesocle_id_dwh ");
		buf.append("AND u.unitesocle_id = e.eqptsocle_idunite_dwh ");
		buf.append("AND e.eqptsocle_id = es.eqptmodulelamell_eqptsocle_id_dwh ");
		buf.append("AND c.contrat_ve_id LIKE '" + contrat + "' ");
		buf.append("AND es.eqptmodulelamell_id IN  ");
		buf.append("	( ");
		buf.append("	SELECT t1.eqptmodulelamell_id   ");
		buf.append("	FROM patrimoine_eqpt_modulelamellaire t1 ");
		buf.append("	WHERE (t1.eqptmodulelamell_eqptsocle_id, t1.id_chg) IN ");
		buf.append("		( ");
		buf.append("		SELECT t.eqptmodulelamell_eqptsocle_id, MAX(t.id_chg) as id_chg  ");
		buf.append("		FROM patrimoine_eqpt_modulelamellaire t ");
		buf.append("		GROUP BY t.eqptmodulelamell_eqptsocle_id ");
		buf.append("		)  ");
		buf.append("	)  ");

		session = getHibernateTemplate().getCurrentSession();
		List<TypeEquipementModuleLamellaire> equipementModuleLamellaires = session.createSQLQuery(buf.toString()).addEntity(TypeEquipementModuleLamellaire.class).list();
		session.close();
		System.out.println("Insert " + equipementModuleLamellaires.size() + " lines of patrimoine_eqpt_modulelamellaire");

		EquipementsModulesLamellaires equipementModuleLamellaire = new EquipementsModulesLamellaires();
		equipementModuleLamellaire.setEquipementModuleLamellaire(equipementModuleLamellaires);
		patrimoine.setEquipementsModulesLamellaires(equipementModuleLamellaire);

		// -----------------------------------------------------//
		// patrimoine_eqpt_onduleur
		// -----------------------------------------------------//

		buf = new StringBuffer();
		buf.append("SELECT es.* ");
		buf.append(" FROM patrimoine_contrat c, patrimoine_site s, patrimoine_ouvrage_socle o, patrimoine_unite_socle u, patrimoine_eqpt_socle e, patrimoine_eqpt_onduleur es ");
		buf.append("WHERE c.contrat_id = s.site_contrat_id_dwh ");
		buf.append("AND s.site_id = o.ouvragesocle_site_id_dwh ");
		buf.append("AND o.ouvragesocle_id = u.unitesocle_ouvragesocle_id_dwh ");
		buf.append("AND u.unitesocle_id = e.eqptsocle_idunite_dwh ");
		buf.append("AND e.eqptsocle_id = es.eqptonduleur_eqptsocle_id_dwh ");
		buf.append("AND c.contrat_ve_id LIKE '" + contrat + "' ");
		buf.append("AND es.eqptonduleur_id IN  ");
		buf.append("	( ");
		buf.append("	SELECT t1.eqptonduleur_id   ");
		buf.append("	FROM patrimoine_eqpt_onduleur t1 ");
		buf.append("	WHERE (t1.eqptonduleur_eqptsocle_id, t1.id_chg) IN ");
		buf.append("		( ");
		buf.append("		SELECT t.eqptonduleur_eqptsocle_id, MAX(t.id_chg) as id_chg  ");
		buf.append("		FROM patrimoine_eqpt_onduleur t ");
		buf.append("		GROUP BY t.eqptonduleur_eqptsocle_id ");
		buf.append("		)  ");
		buf.append("	)  ");

		session = getHibernateTemplate().getCurrentSession();
		List<TypeEquipementOnduleur> equipementOnduleurs = session.createSQLQuery(buf.toString()).addEntity(TypeEquipementOnduleur.class).list();
		session.close();
		System.out.println("Insert " + equipementOnduleurs.size() + " lines of patrimoine_eqpt_onduleur");

		EquipementsOnduleurs equipementOnduleur = new EquipementsOnduleurs();
		equipementOnduleur.setEquipementOnduleur(equipementOnduleurs);
		patrimoine.setEquipementsOnduleurs(equipementOnduleur);

		// -----------------------------------------------------//
		// patrimoine_eqpt_ozoneur
		// -----------------------------------------------------//

		buf = new StringBuffer();
		buf.append("SELECT es.* ");
		buf.append(" FROM patrimoine_contrat c, patrimoine_site s, patrimoine_ouvrage_socle o, patrimoine_unite_socle u, patrimoine_eqpt_socle e, patrimoine_eqpt_ozoneur es ");
		buf.append("WHERE c.contrat_id = s.site_contrat_id_dwh ");
		buf.append("AND s.site_id = o.ouvragesocle_site_id_dwh ");
		buf.append("AND o.ouvragesocle_id = u.unitesocle_ouvragesocle_id_dwh ");
		buf.append("AND u.unitesocle_id = e.eqptsocle_idunite_dwh ");
		buf.append("AND e.eqptsocle_id = es.eqptozoneur_eqptsocle_id_dwh ");
		buf.append("AND c.contrat_ve_id LIKE '" + contrat + "' ");
		buf.append("AND es.eqptozoneur_id IN  ");
		buf.append("	( ");
		buf.append("	SELECT t1.eqptozoneur_id   ");
		buf.append("	FROM patrimoine_eqpt_ozoneur t1 ");
		buf.append("	WHERE (t1.eqptozoneur_eqptsocle_id, t1.id_chg) IN ");
		buf.append("		( ");
		buf.append("		SELECT t.eqptozoneur_eqptsocle_id, MAX(t.id_chg) as id_chg  ");
		buf.append("		FROM patrimoine_eqpt_ozoneur t ");
		buf.append("		GROUP BY t.eqptozoneur_eqptsocle_id ");
		buf.append("		)  ");
		buf.append("	)  ");

		session = getHibernateTemplate().getCurrentSession();
		List<TypeEquipementOzoneur> equipementOzoneurs = session.createSQLQuery(buf.toString()).addEntity(TypeEquipementOzoneur.class).list();
		session.close();
		System.out.println("Insert " + equipementOzoneurs.size() + " lines of patrimoine_eqpt_ozoneur");

		EquipementsOzoneurs equipementOzoneur = new EquipementsOzoneurs();
		equipementOzoneur.setEquipementOzoneur(equipementOzoneurs);
		patrimoine.setEquipementsOzoneurs(equipementOzoneur);

		// -----------------------------------------------------//
		// patrimoine_eqpt_pompe
		// -----------------------------------------------------//

		buf = new StringBuffer();
		buf.append("SELECT es.* ");
		buf.append(" FROM patrimoine_contrat c, patrimoine_site s, patrimoine_ouvrage_socle o, patrimoine_unite_socle u, patrimoine_eqpt_socle e, patrimoine_eqpt_pompe es ");
		buf.append("WHERE c.contrat_id = s.site_contrat_id_dwh ");
		buf.append("AND s.site_id = o.ouvragesocle_site_id_dwh ");
		buf.append("AND o.ouvragesocle_id = u.unitesocle_ouvragesocle_id_dwh ");
		buf.append("AND u.unitesocle_id = e.eqptsocle_idunite_dwh ");
		buf.append("AND e.eqptsocle_id = es.eqptpompe_eqptsocle_id_dwh ");
		buf.append("AND c.contrat_ve_id LIKE '" + contrat + "' ");
		buf.append("AND es.eqptpompe_id IN  ");
		buf.append("	( ");
		buf.append("	SELECT t1.eqptpompe_id   ");
		buf.append("	FROM patrimoine_eqpt_pompe t1 ");
		buf.append("	WHERE (t1.eqptpompe_eqptsocle_id, t1.id_chg) IN ");
		buf.append("		( ");
		buf.append("		SELECT t.eqptpompe_eqptsocle_id, MAX(t.id_chg) as id_chg  ");
		buf.append("		FROM patrimoine_eqpt_pompe t ");
		buf.append("		GROUP BY t.eqptpompe_eqptsocle_id ");
		buf.append("		)  ");
		buf.append("	)  ");

		session = getHibernateTemplate().getCurrentSession();
		List<TypeEquipementPompe> equipementPompes = session.createSQLQuery(buf.toString()).addEntity(TypeEquipementPompe.class).list();
		session.close();
		System.out.println("Insert " + equipementPompes.size() + " lines of patrimoine_eqpt_pompe");

		EquipementsPompes equipementPompe = new EquipementsPompes();
		equipementPompe.setEquipementPompe(equipementPompes);
		patrimoine.setEquipementsPompes(equipementPompe);

		// -----------------------------------------------------//
		// patrimoine_eqpt_pontracleur
		// -----------------------------------------------------//

		buf = new StringBuffer();
		buf.append("SELECT es.* ");
		buf.append(" FROM patrimoine_contrat c, patrimoine_site s, patrimoine_ouvrage_socle o, patrimoine_unite_socle u, patrimoine_eqpt_socle e, patrimoine_eqpt_pontracleur es ");
		buf.append("WHERE c.contrat_id = s.site_contrat_id_dwh ");
		buf.append("AND s.site_id = o.ouvragesocle_site_id_dwh ");
		buf.append("AND o.ouvragesocle_id = u.unitesocle_ouvragesocle_id_dwh ");
		buf.append("AND u.unitesocle_id = e.eqptsocle_idunite_dwh ");
		buf.append("AND e.eqptsocle_id = es.eqptpontracleur_eqptsocle_id_dwh ");
		buf.append("AND c.contrat_ve_id LIKE '" + contrat + "' ");
		buf.append("AND es.eqptpontracleur_id IN  ");
		buf.append("	( ");
		buf.append("	SELECT t1.eqptpontracleur_id   ");
		buf.append("	FROM patrimoine_eqpt_pontracleur t1 ");
		buf.append("	WHERE (t1.eqptpontracleur_eqptsocle_id, t1.id_chg) IN ");
		buf.append("		( ");
		buf.append("		SELECT t.eqptpontracleur_eqptsocle_id, MAX(t.id_chg) as id_chg  ");
		buf.append("		FROM patrimoine_eqpt_pontracleur t ");
		buf.append("		GROUP BY t.eqptpontracleur_eqptsocle_id ");
		buf.append("		)  ");
		buf.append("	)  ");

		session = getHibernateTemplate().getCurrentSession();
		List<TypeEquipementPontRacleur> equipementPontRacleurs = session.createSQLQuery(buf.toString()).addEntity(TypeEquipementPontRacleur.class).list();
		session.close();
		System.out.println("Insert " + equipementPontRacleurs.size() + " lines of patrimoine_eqpt_pontracleur");

		EquipementsPontsRacleurs equipementPontRacleur = new EquipementsPontsRacleurs();
		equipementPontRacleur.setEquipementPontRacleur(equipementPontRacleurs);
		patrimoine.setEquipementsPontsRacleurs(equipementPontRacleur);

		// -----------------------------------------------------//
		// patrimoine_eqpt_portail
		// -----------------------------------------------------//

		buf = new StringBuffer();
		buf.append("SELECT es.* ");
		buf.append(" FROM patrimoine_contrat c, patrimoine_site s, patrimoine_ouvrage_socle o, patrimoine_unite_socle u, patrimoine_eqpt_socle e, patrimoine_eqpt_portail es ");
		buf.append("WHERE c.contrat_id = s.site_contrat_id_dwh ");
		buf.append("AND s.site_id = o.ouvragesocle_site_id_dwh ");
		buf.append("AND o.ouvragesocle_id = u.unitesocle_ouvragesocle_id_dwh ");
		buf.append("AND u.unitesocle_id = e.eqptsocle_idunite_dwh ");
		buf.append("AND e.eqptsocle_id = es.eqptportail_eqptsocle_id_dwh ");
		buf.append("AND c.contrat_ve_id LIKE '" + contrat + "' ");
		buf.append("AND es.eqptportail_id IN  ");
		buf.append("	( ");
		buf.append("	SELECT t1.eqptportail_id   ");
		buf.append("	FROM patrimoine_eqpt_portail t1 ");
		buf.append("	WHERE (t1.eqptportail_eqptsocle_id, t1.id_chg) IN ");
		buf.append("		( ");
		buf.append("		SELECT t.eqptportail_eqptsocle_id, MAX(t.id_chg) as id_chg  ");
		buf.append("		FROM patrimoine_eqpt_portail t ");
		buf.append("		GROUP BY t.eqptportail_eqptsocle_id ");
		buf.append("		)  ");
		buf.append("	)  ");

		session = getHibernateTemplate().getCurrentSession();
		List<TypeEquipementPortail> equipementPortails = session.createSQLQuery(buf.toString()).addEntity(TypeEquipementPortail.class).list();
		session.close();
		System.out.println("Insert " + equipementPortails.size() + " lines of patrimoine_eqpt_portail");

		EquipementsPortails equipementPortail = new EquipementsPortails();
		equipementPortail.setEquipementPortail(equipementPortails);
		patrimoine.setEquipementsPortails(equipementPortail);

		// -----------------------------------------------------//
		// patrimoine_eqpt_porte
		// -----------------------------------------------------//

		buf = new StringBuffer();
		buf.append("SELECT es.* ");
		buf.append(" FROM patrimoine_contrat c, patrimoine_site s, patrimoine_ouvrage_socle o, patrimoine_unite_socle u, patrimoine_eqpt_socle e, patrimoine_eqpt_porte es ");
		buf.append("WHERE c.contrat_id = s.site_contrat_id_dwh ");
		buf.append("AND s.site_id = o.ouvragesocle_site_id_dwh ");
		buf.append("AND o.ouvragesocle_id = u.unitesocle_ouvragesocle_id_dwh ");
		buf.append("AND u.unitesocle_id = e.eqptsocle_idunite_dwh ");
		buf.append("AND e.eqptsocle_id = es.eqptporte_eqptsocle_id_dwh ");
		buf.append("AND c.contrat_ve_id LIKE '" + contrat + "' ");
		buf.append("AND es.eqptporte_id IN  ");
		buf.append("	( ");
		buf.append("	SELECT t1.eqptporte_id   ");
		buf.append("	FROM patrimoine_eqpt_porte t1 ");
		buf.append("	WHERE (t1.eqptporte_eqptsocle_id, t1.id_chg) IN ");
		buf.append("		( ");
		buf.append("		SELECT t.eqptporte_eqptsocle_id, MAX(t.id_chg) as id_chg  ");
		buf.append("		FROM patrimoine_eqpt_porte t ");
		buf.append("		GROUP BY t.eqptporte_eqptsocle_id ");
		buf.append("		)  ");
		buf.append("	)  ");

		session = getHibernateTemplate().getCurrentSession();
		List<TypeEquipementPorte> equipementPortes = session.createSQLQuery(buf.toString()).addEntity(TypeEquipementPorte.class).list();
		session.close();
		System.out.println("Insert " + equipementPortes.size() + " lines of patrimoine_eqpt_porte");

		EquipementsPortes equipementPorte = new EquipementsPortes();
		equipementPorte.setEquipementPorte(equipementPortes);
		patrimoine.setEquipementsPortes(equipementPorte);

		// -----------------------------------------------------//
		// patrimoine_eqpt_portesectionnelle
		// -----------------------------------------------------//

		buf = new StringBuffer();
		buf.append("SELECT es.* ");
		buf.append(" FROM patrimoine_contrat c, patrimoine_site s, patrimoine_ouvrage_socle o, patrimoine_unite_socle u, patrimoine_eqpt_socle e, patrimoine_eqpt_portesectionnelle es ");
		buf.append("WHERE c.contrat_id = s.site_contrat_id_dwh ");
		buf.append("AND s.site_id = o.ouvragesocle_site_id_dwh ");
		buf.append("AND o.ouvragesocle_id = u.unitesocle_ouvragesocle_id_dwh ");
		buf.append("AND u.unitesocle_id = e.eqptsocle_idunite_dwh ");
		buf.append("AND e.eqptsocle_id = es.eqptportesection_eqptsocle_id_dwh ");
		buf.append("AND c.contrat_ve_id LIKE '" + contrat + "' ");
		buf.append("AND es.eqptportesection_id IN  ");
		buf.append("	( ");
		buf.append("	SELECT t1.eqptportesection_id   ");
		buf.append("	FROM patrimoine_eqpt_portesectionnelle t1 ");
		buf.append("	WHERE (t1.eqptportesection_eqptsocle_id, t1.id_chg) IN ");
		buf.append("		( ");
		buf.append("		SELECT t.eqptportesection_eqptsocle_id, MAX(t.id_chg) as id_chg  ");
		buf.append("		FROM patrimoine_eqpt_portesectionnelle t ");
		buf.append("		GROUP BY t.eqptportesection_eqptsocle_id ");
		buf.append("		)  ");
		buf.append("	)  ");

		session = getHibernateTemplate().getCurrentSession();
		List<TypeEquipementPorteSectionnelle> equipementPorteSectionnelles = session.createSQLQuery(buf.toString()).addEntity(TypeEquipementPorteSectionnelle.class).list();
		session.close();
		System.out.println("Insert " + equipementPorteSectionnelles.size() + " lines of patrimoine_eqpt_portesectionnelle");

		EquipementsPortesSectionnelles equipementPorteSectionnelle = new EquipementsPortesSectionnelles();
		equipementPorteSectionnelle.setEquipementPorteSectionnelle(equipementPorteSectionnelles);
		patrimoine.setEquipementsPortesSectionnelles(equipementPorteSectionnelle);

		// -----------------------------------------------------//
		// patrimoine_eqpt_portillon
		// -----------------------------------------------------//

		buf = new StringBuffer();
		buf.append("SELECT es.* ");
		buf.append(" FROM patrimoine_contrat c, patrimoine_site s, patrimoine_ouvrage_socle o, patrimoine_unite_socle u, patrimoine_eqpt_socle e, patrimoine_eqpt_portillon es ");
		buf.append("WHERE c.contrat_id = s.site_contrat_id_dwh ");
		buf.append("AND s.site_id = o.ouvragesocle_site_id_dwh ");
		buf.append("AND o.ouvragesocle_id = u.unitesocle_ouvragesocle_id_dwh ");
		buf.append("AND u.unitesocle_id = e.eqptsocle_idunite_dwh ");
		buf.append("AND e.eqptsocle_id = es.eqptportillon_eqptsocle_id_dwh ");
		buf.append("AND c.contrat_ve_id LIKE '" + contrat + "' ");
		buf.append("AND es.eqptportillon_id IN  ");
		buf.append("	( ");
		buf.append("	SELECT t1.eqptportillon_id   ");
		buf.append("	FROM patrimoine_eqpt_portillon t1 ");
		buf.append("	WHERE (t1.eqptportillon_eqptsocle_id, t1.id_chg) IN ");
		buf.append("		( ");
		buf.append("		SELECT t.eqptportillon_eqptsocle_id, MAX(t.id_chg) as id_chg  ");
		buf.append("		FROM patrimoine_eqpt_portillon t ");
		buf.append("		GROUP BY t.eqptportillon_eqptsocle_id ");
		buf.append("		)  ");
		buf.append("	)  ");

		session = getHibernateTemplate().getCurrentSession();
		List<TypeEquipementPortillon> equipementPortillons = session.createSQLQuery(buf.toString()).addEntity(TypeEquipementPortillon.class).list();
		session.close();
		System.out.println("Insert " + equipementPortillons.size() + " lines of patrimoine_eqpt_portillon");

		EquipementsPortillons equipementPortillon = new EquipementsPortillons();
		equipementPortillon.setEquipementPortillon(equipementPortillons);
		patrimoine.setEquipementsPortillons(equipementPortillon);

		// -----------------------------------------------------//
		// patrimoine_eqpt_prefiltres
		// -----------------------------------------------------//

		buf = new StringBuffer();
		buf.append("SELECT es.* ");
		buf.append(" FROM patrimoine_contrat c, patrimoine_site s, patrimoine_ouvrage_socle o, patrimoine_unite_socle u, patrimoine_eqpt_socle e, patrimoine_eqpt_prefiltres es ");
		buf.append("WHERE c.contrat_id = s.site_contrat_id_dwh ");
		buf.append("AND s.site_id = o.ouvragesocle_site_id_dwh ");
		buf.append("AND o.ouvragesocle_id = u.unitesocle_ouvragesocle_id_dwh ");
		buf.append("AND u.unitesocle_id = e.eqptsocle_idunite_dwh ");
		buf.append("AND e.eqptsocle_id = es.eqptprefiltre_eqptsocle_id_dwh ");
		buf.append("AND c.contrat_ve_id LIKE '" + contrat + "' ");
		buf.append("AND es.eqptprefiltre_id IN  ");
		buf.append("	( ");
		buf.append("	SELECT t1.eqptprefiltre_id   ");
		buf.append("	FROM patrimoine_eqpt_prefiltres t1 ");
		buf.append("	WHERE (t1.eqptprefiltre_eqptsocle_id, t1.id_chg) IN ");
		buf.append("		( ");
		buf.append("		SELECT t.eqptprefiltre_eqptsocle_id, MAX(t.id_chg) as id_chg  ");
		buf.append("		FROM patrimoine_eqpt_prefiltres t ");
		buf.append("		GROUP BY t.eqptprefiltre_eqptsocle_id ");
		buf.append("		)  ");
		buf.append("	)  ");

		session = getHibernateTemplate().getCurrentSession();
		List<TypeEquipementPrefiltre> equipementPrefiltres = session.createSQLQuery(buf.toString()).addEntity(TypeEquipementPrefiltre.class).list();
		session.close();
		System.out.println("Insert " + equipementPrefiltres.size() + " lines of patrimoine_eqpt_prefiltres");

		EquipementsPrefiltres equipementPrefiltre = new EquipementsPrefiltres();
		equipementPrefiltre.setEquipementPrefiltre(equipementPrefiltres);
		patrimoine.setEquipementsPrefiltres(equipementPrefiltre);

		// -----------------------------------------------------//
		// patrimoine_eqpt_preleveurechantillon
		// -----------------------------------------------------//

		buf = new StringBuffer();
		buf.append("SELECT es.* ");
		buf.append(" FROM patrimoine_contrat c, patrimoine_site s, patrimoine_ouvrage_socle o, patrimoine_unite_socle u, patrimoine_eqpt_socle e, patrimoine_eqpt_preleveurechantillon es ");
		buf.append("WHERE c.contrat_id = s.site_contrat_id_dwh ");
		buf.append("AND s.site_id = o.ouvragesocle_site_id_dwh ");
		buf.append("AND o.ouvragesocle_id = u.unitesocle_ouvragesocle_id_dwh ");
		buf.append("AND u.unitesocle_id = e.eqptsocle_idunite_dwh ");
		buf.append("AND e.eqptsocle_id = es.eqptprelevechant_eqptsocle_id_dwh ");
		buf.append("AND c.contrat_ve_id LIKE '" + contrat + "' ");
		buf.append("AND es.eqptprelevechant_id IN  ");
		buf.append("	( ");
		buf.append("	SELECT t1.eqptprelevechant_id   ");
		buf.append("	FROM patrimoine_eqpt_preleveurechantillon t1 ");
		buf.append("	WHERE (t1.eqptprelevechant_eqptsocle_id, t1.id_chg) IN ");
		buf.append("		( ");
		buf.append("		SELECT t.eqptprelevechant_eqptsocle_id, MAX(t.id_chg) as id_chg  ");
		buf.append("		FROM patrimoine_eqpt_preleveurechantillon t ");
		buf.append("		GROUP BY t.eqptprelevechant_eqptsocle_id ");
		buf.append("		)  ");
		buf.append("	)  ");

		session = getHibernateTemplate().getCurrentSession();
		List<TypeEquipementPreleveurEchantillonneur> equipementPreleveurEchantillonneurs = session.createSQLQuery(buf.toString()).addEntity(TypeEquipementPreleveurEchantillonneur.class).list();
		session.close();
		System.out.println("Insert " + equipementPreleveurEchantillonneurs.size() + " lines of patrimoine_eqpt_preleveurechantillon");

		EquipementsPreleveursEchantillonneurs equipementPreleveurEchantillonneur = new EquipementsPreleveursEchantillonneurs();
		equipementPreleveurEchantillonneur.setEquipementPreleveurEchantillonneur(equipementPreleveurEchantillonneurs);
		patrimoine.setEquipementsPreleveursEchantillonneurs(equipementPreleveurEchantillonneur);

		// -----------------------------------------------------//
		// patrimoine_eqpt_presseboue
		// -----------------------------------------------------//

		buf = new StringBuffer();
		buf.append("SELECT es.* ");
		buf.append(" FROM patrimoine_contrat c, patrimoine_site s, patrimoine_ouvrage_socle o, patrimoine_unite_socle u, patrimoine_eqpt_socle e, patrimoine_eqpt_presseboue es ");
		buf.append("WHERE c.contrat_id = s.site_contrat_id_dwh ");
		buf.append("AND s.site_id = o.ouvragesocle_site_id_dwh ");
		buf.append("AND o.ouvragesocle_id = u.unitesocle_ouvragesocle_id_dwh ");
		buf.append("AND u.unitesocle_id = e.eqptsocle_idunite_dwh ");
		buf.append("AND e.eqptsocle_id = es.eqptpresseboue_eqptsocle_id_dwh ");
		buf.append("AND c.contrat_ve_id LIKE '" + contrat + "' ");
		buf.append("AND es.eqptpresseboue_id IN  ");
		buf.append("	( ");
		buf.append("	SELECT t1.eqptpresseboue_id   ");
		buf.append("	FROM patrimoine_eqpt_presseboue t1 ");
		buf.append("	WHERE (t1.eqptpresseboue_eqptsocle_id, t1.id_chg) IN ");
		buf.append("		( ");
		buf.append("		SELECT t.eqptpresseboue_eqptsocle_id, MAX(t.id_chg) as id_chg  ");
		buf.append("		FROM patrimoine_eqpt_presseboue t ");
		buf.append("		GROUP BY t.eqptpresseboue_eqptsocle_id ");
		buf.append("		)  ");
		buf.append("	)  ");

		session = getHibernateTemplate().getCurrentSession();
		List<TypeEquipementPresseBoue> equipementPresseBoues = session.createSQLQuery(buf.toString()).addEntity(TypeEquipementPresseBoue.class).list();
		session.close();
		System.out.println("Insert " + equipementPresseBoues.size() + " lines of patrimoine_eqpt_presseboue");

		EquipementsPressesBoue equipementPresseBoue = new EquipementsPressesBoue();
		equipementPresseBoue.setEquipementPresseBoue(equipementPresseBoues);
		patrimoine.setEquipementsPressesBoue(equipementPresseBoue);

		// -----------------------------------------------------//
		// patrimoine_eqpt_protecincendie
		// -----------------------------------------------------//

		buf = new StringBuffer();
		buf.append("SELECT es.* ");
		buf.append(" FROM patrimoine_contrat c, patrimoine_site s, patrimoine_ouvrage_socle o, patrimoine_unite_socle u, patrimoine_eqpt_socle e, patrimoine_eqpt_protecincendie es ");
		buf.append("WHERE c.contrat_id = s.site_contrat_id_dwh ");
		buf.append("AND s.site_id = o.ouvragesocle_site_id_dwh ");
		buf.append("AND o.ouvragesocle_id = u.unitesocle_ouvragesocle_id_dwh ");
		buf.append("AND u.unitesocle_id = e.eqptsocle_idunite_dwh ");
		buf.append("AND e.eqptsocle_id = es.eqptprotecincend_eqptsocle_id_dwh ");
		buf.append("AND c.contrat_ve_id LIKE '" + contrat + "' ");
		buf.append("AND es.eqptprotecincend_id IN  ");
		buf.append("	( ");
		buf.append("	SELECT t1.eqptprotecincend_id   ");
		buf.append("	FROM patrimoine_eqpt_protecincendie t1 ");
		buf.append("	WHERE (t1.eqptprotecincend_eqptsocle_id, t1.id_chg) IN ");
		buf.append("		( ");
		buf.append("		SELECT t.eqptprotecincend_eqptsocle_id, MAX(t.id_chg) as id_chg  ");
		buf.append("		FROM patrimoine_eqpt_protecincendie t ");
		buf.append("		GROUP BY t.eqptprotecincend_eqptsocle_id ");
		buf.append("		)  ");
		buf.append("	)  ");

		session = getHibernateTemplate().getCurrentSession();
		List<TypeEquipementProtectionIncendie> equipementProtectionIncendies = session.createSQLQuery(buf.toString()).addEntity(TypeEquipementProtectionIncendie.class).list();
		session.close();
		System.out.println("Insert " + equipementProtectionIncendies.size() + " lines of patrimoine_eqpt_protecincendie");

		EquipementsProtectionsIncendie equipementProtectionIncendie = new EquipementsProtectionsIncendie();
		equipementProtectionIncendie.setEquipementProtectionIncendie(equipementProtectionIncendies);
		patrimoine.setEquipementsProtectionsIncendie(equipementProtectionIncendie);

		// -----------------------------------------------------//
		// patrimoine_eqpt_retourneterre
		// -----------------------------------------------------//

		buf = new StringBuffer();
		buf.append("SELECT es.* ");
		buf.append(" FROM patrimoine_contrat c, patrimoine_site s, patrimoine_ouvrage_socle o, patrimoine_unite_socle u, patrimoine_eqpt_socle e, patrimoine_eqpt_retourneterre es ");
		buf.append("WHERE c.contrat_id = s.site_contrat_id_dwh ");
		buf.append("AND s.site_id = o.ouvragesocle_site_id_dwh ");
		buf.append("AND o.ouvragesocle_id = u.unitesocle_ouvragesocle_id_dwh ");
		buf.append("AND u.unitesocle_id = e.eqptsocle_idunite_dwh ");
		buf.append("AND e.eqptsocle_id = es.eqptretterre_eqptsocle_id_dwh ");
		buf.append("AND c.contrat_ve_id LIKE '" + contrat + "' ");
		buf.append("AND es.eqptretterre_id IN  ");
		buf.append("	( ");
		buf.append("	SELECT t1.eqptretterre_id   ");
		buf.append("	FROM patrimoine_eqpt_retourneterre t1 ");
		buf.append("	WHERE (t1.eqptretterre_eqptsocle_id, t1.id_chg) IN ");
		buf.append("		( ");
		buf.append("		SELECT t.eqptretterre_eqptsocle_id, MAX(t.id_chg) as id_chg  ");
		buf.append("		FROM patrimoine_eqpt_retourneterre t ");
		buf.append("		GROUP BY t.eqptretterre_eqptsocle_id ");
		buf.append("		)  ");
		buf.append("	)  ");

		session = getHibernateTemplate().getCurrentSession();
		List<TypeEquipementRetournementTerres> equipementRetournementTerress = session.createSQLQuery(buf.toString()).addEntity(TypeEquipementRetournementTerres.class).list();
		session.close();
		System.out.println("Insert " + equipementRetournementTerress.size() + " lines of patrimoine_eqpt_retourneterre");

		EquipementsRetournementsTerres equipementRetournementTerres = new EquipementsRetournementsTerres();
		equipementRetournementTerres.setEquipementRetournementTerres(equipementRetournementTerress);
		patrimoine.setEquipementsRetournementsTerres(equipementRetournementTerres);

		// -----------------------------------------------------//
		// patrimoine_eqpt_secheurair
		// -----------------------------------------------------//

		buf = new StringBuffer();
		buf.append("SELECT es.* ");
		buf.append(" FROM patrimoine_contrat c, patrimoine_site s, patrimoine_ouvrage_socle o, patrimoine_unite_socle u, patrimoine_eqpt_socle e, patrimoine_eqpt_secheurair es ");
		buf.append("WHERE c.contrat_id = s.site_contrat_id_dwh ");
		buf.append("AND s.site_id = o.ouvragesocle_site_id_dwh ");
		buf.append("AND o.ouvragesocle_id = u.unitesocle_ouvragesocle_id_dwh ");
		buf.append("AND u.unitesocle_id = e.eqptsocle_idunite_dwh ");
		buf.append("AND e.eqptsocle_id = es.eqptsecheurair_eqptsocle_id_dwh ");
		buf.append("AND c.contrat_ve_id LIKE '" + contrat + "' ");
		buf.append("AND es.eqptsecheurair_id IN  ");
		buf.append("	( ");
		buf.append("	SELECT t1.eqptsecheurair_id   ");
		buf.append("	FROM patrimoine_eqpt_secheurair t1 ");
		buf.append("	WHERE (t1.eqptsecheurair_eqptsocle_id, t1.id_chg) IN ");
		buf.append("		( ");
		buf.append("		SELECT t.eqptsecheurair_eqptsocle_id, MAX(t.id_chg) as id_chg  ");
		buf.append("		FROM patrimoine_eqpt_secheurair t ");
		buf.append("		GROUP BY t.eqptsecheurair_eqptsocle_id ");
		buf.append("		)  ");
		buf.append("	)  ");

		session = getHibernateTemplate().getCurrentSession();
		List<TypeEquipementSecheurAir> equipementSecheurAirs = session.createSQLQuery(buf.toString()).addEntity(TypeEquipementSecheurAir.class).list();
		session.close();
		System.out.println("Insert " + equipementSecheurAirs.size() + " lines of patrimoine_eqpt_secheurair");

		EquipementsSecheursAir equipementSecheurAir = new EquipementsSecheursAir();
		equipementSecheurAir.setEquipementSecheurAir(equipementSecheurAirs);
		patrimoine.setEquipementsSecheursAir(equipementSecheurAir);

		// -----------------------------------------------------//
		// patrimoine_eqpt_silo
		// -----------------------------------------------------//

		buf = new StringBuffer();
		buf.append("SELECT es.* ");
		buf.append(" FROM patrimoine_contrat c, patrimoine_site s, patrimoine_ouvrage_socle o, patrimoine_unite_socle u, patrimoine_eqpt_socle e, patrimoine_eqpt_silo es ");
		buf.append("WHERE c.contrat_id = s.site_contrat_id_dwh ");
		buf.append("AND s.site_id = o.ouvragesocle_site_id_dwh ");
		buf.append("AND o.ouvragesocle_id = u.unitesocle_ouvragesocle_id_dwh ");
		buf.append("AND u.unitesocle_id = e.eqptsocle_idunite_dwh ");
		buf.append("AND e.eqptsocle_id = es.eqptsilo_eqptsocle_id_dwh ");
		buf.append("AND c.contrat_ve_id LIKE '" + contrat + "' ");
		buf.append("AND es.eqptsilo_id IN  ");
		buf.append("	( ");
		buf.append("	SELECT t1.eqptsilo_id   ");
		buf.append("	FROM patrimoine_eqpt_silo t1 ");
		buf.append("	WHERE (t1.eqptsilo_eqptsocle_id, t1.id_chg) IN ");
		buf.append("		( ");
		buf.append("		SELECT t.eqptsilo_eqptsocle_id, MAX(t.id_chg) as id_chg  ");
		buf.append("		FROM patrimoine_eqpt_silo t ");
		buf.append("		GROUP BY t.eqptsilo_eqptsocle_id ");
		buf.append("		)  ");
		buf.append("	)  ");

		session = getHibernateTemplate().getCurrentSession();
		List<TypeEquipementSilo> equipementSilos = session.createSQLQuery(buf.toString()).addEntity(TypeEquipementSilo.class).list();
		session.close();
		System.out.println("Insert " + equipementSilos.size() + " lines of patrimoine_eqpt_silo");

		EquipementsSilos equipementSilo = new EquipementsSilos();
		equipementSilo.setEquipementSilo(equipementSilos);
		patrimoine.setEquipementsSilos(equipementSilo);

		// -----------------------------------------------------//
		// patrimoine_eqpt_soupape
		// -----------------------------------------------------//

		buf = new StringBuffer();
		buf.append("SELECT es.* ");
		buf.append(" FROM patrimoine_contrat c, patrimoine_site s, patrimoine_ouvrage_socle o, patrimoine_unite_socle u, patrimoine_eqpt_socle e, patrimoine_eqpt_soupape es ");
		buf.append("WHERE c.contrat_id = s.site_contrat_id_dwh ");
		buf.append("AND s.site_id = o.ouvragesocle_site_id_dwh ");
		buf.append("AND o.ouvragesocle_id = u.unitesocle_ouvragesocle_id_dwh ");
		buf.append("AND u.unitesocle_id = e.eqptsocle_idunite_dwh ");
		buf.append("AND e.eqptsocle_id = es.eqptsoupape_eqptsocle_id_dwh ");
		buf.append("AND c.contrat_ve_id LIKE '" + contrat + "' ");
		buf.append("AND es.eqptsoupape_id IN  ");
		buf.append("	( ");
		buf.append("	SELECT t1.eqptsoupape_id   ");
		buf.append("	FROM patrimoine_eqpt_soupape t1 ");
		buf.append("	WHERE (t1.eqptsoupape_eqptsocle_id, t1.id_chg) IN ");
		buf.append("		( ");
		buf.append("		SELECT t.eqptsoupape_eqptsocle_id, MAX(t.id_chg) as id_chg  ");
		buf.append("		FROM patrimoine_eqpt_soupape t ");
		buf.append("		GROUP BY t.eqptsoupape_eqptsocle_id ");
		buf.append("		)  ");
		buf.append("	)  ");

		session = getHibernateTemplate().getCurrentSession();
		List<TypeEquipementSoupape> equipementSoupapes = session.createSQLQuery(buf.toString()).addEntity(TypeEquipementSoupape.class).list();
		session.close();
		System.out.println("Insert " + equipementSoupapes.size() + " lines of patrimoine_eqpt_soupape");

		EquipementsSoupapes equipementSoupape = new EquipementsSoupapes();
		equipementSoupape.setEquipementSoupape(equipementSoupapes);
		patrimoine.setEquipementsSoupapes(equipementSoupape);

		// -----------------------------------------------------//
		// patrimoine_eqpt_stationteletr
		// -----------------------------------------------------//

		buf = new StringBuffer();
		buf.append("SELECT es.* ");
		buf.append(" FROM patrimoine_contrat c, patrimoine_site s, patrimoine_ouvrage_socle o, patrimoine_unite_socle u, patrimoine_eqpt_socle e, patrimoine_eqpt_stationteletr es ");
		buf.append("WHERE c.contrat_id = s.site_contrat_id_dwh ");
		buf.append("AND s.site_id = o.ouvragesocle_site_id_dwh ");
		buf.append("AND o.ouvragesocle_id = u.unitesocle_ouvragesocle_id_dwh ");
		buf.append("AND u.unitesocle_id = e.eqptsocle_idunite_dwh ");
		buf.append("AND e.eqptsocle_id = es.eqptstationteletr_eqptsocle_id_dwh ");
		buf.append("AND c.contrat_ve_id LIKE '" + contrat + "' ");
		buf.append("AND es.eqptstationteletr_id IN  ");
		buf.append("	( ");
		buf.append("	SELECT t1.eqptstationteletr_id   ");
		buf.append("	FROM patrimoine_eqpt_stationteletr t1 ");
		buf.append("	WHERE (t1.eqptstationteletr_eqptsocle_id, t1.id_chg) IN ");
		buf.append("		( ");
		buf.append("		SELECT t.eqptstationteletr_eqptsocle_id, MAX(t.id_chg) as id_chg  ");
		buf.append("		FROM patrimoine_eqpt_stationteletr t ");
		buf.append("		GROUP BY t.eqptstationteletr_eqptsocle_id ");
		buf.append("		)  ");
		buf.append("	)  ");

		session = getHibernateTemplate().getCurrentSession();
		List<TypeEquipementStationTeletransmission> equipementStationTeletransmissions = session.createSQLQuery(buf.toString()).addEntity(TypeEquipementStationTeletransmission.class).list();
		session.close();
		System.out.println("Insert " + equipementStationTeletransmissions.size() + " lines of patrimoine_eqpt_stationteletr");

		EquipementsStationsTeletransmission equipementStationTeletransmission = new EquipementsStationsTeletransmission();
		equipementStationTeletransmission.setEquipementStationTeletransmission(equipementStationTeletransmissions);
		patrimoine.setEquipementsStationsTeletransmission(equipementStationTeletransmission);

		// -----------------------------------------------------//
		// patrimoine_eqpt_transfoht
		// -----------------------------------------------------//

		buf = new StringBuffer();
		buf.append("SELECT es.* ");
		buf.append(" FROM patrimoine_contrat c, patrimoine_site s, patrimoine_ouvrage_socle o, patrimoine_unite_socle u, patrimoine_eqpt_socle e, patrimoine_eqpt_transfoht es ");
		buf.append("WHERE c.contrat_id = s.site_contrat_id_dwh ");
		buf.append("AND s.site_id = o.ouvragesocle_site_id_dwh ");
		buf.append("AND o.ouvragesocle_id = u.unitesocle_ouvragesocle_id_dwh ");
		buf.append("AND u.unitesocle_id = e.eqptsocle_idunite_dwh ");
		buf.append("AND e.eqptsocle_id = es.eqpttransfoht_eqptsocle_id_dwh ");
		buf.append("AND c.contrat_ve_id LIKE '" + contrat + "' ");
		buf.append("AND es.eqpttransfoht_id IN  ");
		buf.append("	( ");
		buf.append("	SELECT t1.eqpttransfoht_id   ");
		buf.append("	FROM patrimoine_eqpt_transfoht t1 ");
		buf.append("	WHERE (t1.eqpttransfoht_eqptsocle_id, t1.id_chg) IN ");
		buf.append("		( ");
		buf.append("		SELECT t.eqpttransfoht_eqptsocle_id, MAX(t.id_chg) as id_chg  ");
		buf.append("		FROM patrimoine_eqpt_transfoht t ");
		buf.append("		GROUP BY t.eqpttransfoht_eqptsocle_id ");
		buf.append("		)  ");
		buf.append("	)  ");

		session = getHibernateTemplate().getCurrentSession();
		List<TypeEquipementTransformateurHT> equipementTransformateurHTs = session.createSQLQuery(buf.toString()).addEntity(TypeEquipementTransformateurHT.class).list();
		session.close();
		System.out.println("Insert " + equipementTransformateurHTs.size() + " lines of patrimoine_eqpt_transfoht");

		EquipementsTransformateursHT equipementTransformateurHT = new EquipementsTransformateursHT();
		equipementTransformateurHT.setEquipementTransformateurHT(equipementTransformateurHTs);
		patrimoine.setEquipementsTransformateursHT(equipementTransformateurHT);

		// -----------------------------------------------------//
		// patrimoine_eqpt_transformredress
		// -----------------------------------------------------//

		buf = new StringBuffer();
		buf.append("SELECT es.* ");
		buf.append(" FROM patrimoine_contrat c, patrimoine_site s, patrimoine_ouvrage_socle o, patrimoine_unite_socle u, patrimoine_eqpt_socle e, patrimoine_eqpt_transformredress es ");
		buf.append("WHERE c.contrat_id = s.site_contrat_id_dwh ");
		buf.append("AND s.site_id = o.ouvragesocle_site_id_dwh ");
		buf.append("AND o.ouvragesocle_id = u.unitesocle_ouvragesocle_id_dwh ");
		buf.append("AND u.unitesocle_id = e.eqptsocle_idunite_dwh ");
		buf.append("AND e.eqptsocle_id = es.eqpttransfredr_eqptsocle_id_dwh ");
		buf.append("AND c.contrat_ve_id LIKE '" + contrat + "' ");
		buf.append("AND es.eqpttransfredr_id IN  ");
		buf.append("	( ");
		buf.append("	SELECT t1.eqpttransfredr_id   ");
		buf.append("	FROM patrimoine_eqpt_transformredress t1 ");
		buf.append("	WHERE (t1.eqpttransfredr_eqptsocle_id, t1.id_chg) IN ");
		buf.append("		( ");
		buf.append("		SELECT t.eqpttransfredr_eqptsocle_id, MAX(t.id_chg) as id_chg  ");
		buf.append("		FROM patrimoine_eqpt_transformredress t ");
		buf.append("		GROUP BY t.eqpttransfredr_eqptsocle_id ");
		buf.append("		)  ");
		buf.append("	)  ");

		session = getHibernateTemplate().getCurrentSession();
		List<TypeEquipementTransformateurRedresseur> equipementTransformateurRedresseurs = session.createSQLQuery(buf.toString()).addEntity(TypeEquipementTransformateurRedresseur.class).list();
		session.close();
		System.out.println("Insert " + equipementTransformateurRedresseurs.size() + " lines of patrimoine_eqpt_transformredress");

		EquipementsTransformateursRedresseurs equipementTransformateurRedresseur = new EquipementsTransformateursRedresseurs();
		equipementTransformateurRedresseur.setEquipementTransformateurRedresseur(equipementTransformateurRedresseurs);
		patrimoine.setEquipementsTransformateursRedresseurs(equipementTransformateurRedresseur);

		// -----------------------------------------------------//
		// patrimoine_eqpt_trappeacces
		// -----------------------------------------------------//

		buf = new StringBuffer();
		buf.append("SELECT es.* ");
		buf.append(" FROM patrimoine_contrat c, patrimoine_site s, patrimoine_ouvrage_socle o, patrimoine_unite_socle u, patrimoine_eqpt_socle e, patrimoine_eqpt_trappeacces es ");
		buf.append("WHERE c.contrat_id = s.site_contrat_id_dwh ");
		buf.append("AND s.site_id = o.ouvragesocle_site_id_dwh ");
		buf.append("AND o.ouvragesocle_id = u.unitesocle_ouvragesocle_id_dwh ");
		buf.append("AND u.unitesocle_id = e.eqptsocle_idunite_dwh ");
		buf.append("AND e.eqptsocle_id = es.eqpttrappeacces_eqptsocle_id_dwh ");
		buf.append("AND c.contrat_ve_id LIKE '" + contrat + "' ");
		buf.append("AND es.eqpttrappeacces_id IN  ");
		buf.append("	( ");
		buf.append("	SELECT t1.eqpttrappeacces_id   ");
		buf.append("	FROM patrimoine_eqpt_trappeacces t1 ");
		buf.append("	WHERE (t1.eqpttrappeacces_eqptsocle_id, t1.id_chg) IN ");
		buf.append("		( ");
		buf.append("		SELECT t.eqpttrappeacces_eqptsocle_id, MAX(t.id_chg) as id_chg  ");
		buf.append("		FROM patrimoine_eqpt_trappeacces t ");
		buf.append("		GROUP BY t.eqpttrappeacces_eqptsocle_id ");
		buf.append("		)  ");
		buf.append("	)  ");

		session = getHibernateTemplate().getCurrentSession();
		List<TypeEquipementTrappeAcces> equipementTrappeAccess = session.createSQLQuery(buf.toString()).addEntity(TypeEquipementTrappeAcces.class).list();
		session.close();
		System.out.println("Insert " + equipementTrappeAccess.size() + " lines of patrimoine_eqpt_trappeacces");

		EquipementsTrappesAcces equipementTrappeAcces = new EquipementsTrappesAcces();
		equipementTrappeAcces.setEquipementTrappeAcces(equipementTrappeAccess);
		patrimoine.setEquipementsTrappesAcces(equipementTrappeAcces);

		// -----------------------------------------------------//
		// patrimoine_eqpt_tubecrepine
		// -----------------------------------------------------//

		buf = new StringBuffer();
		buf.append("SELECT es.* ");
		buf.append(" FROM patrimoine_contrat c, patrimoine_site s, patrimoine_ouvrage_socle o, patrimoine_unite_socle u, patrimoine_eqpt_socle e, patrimoine_eqpt_tubecrepine es ");
		buf.append("WHERE c.contrat_id = s.site_contrat_id_dwh ");
		buf.append("AND s.site_id = o.ouvragesocle_site_id_dwh ");
		buf.append("AND o.ouvragesocle_id = u.unitesocle_ouvragesocle_id_dwh ");
		buf.append("AND u.unitesocle_id = e.eqptsocle_idunite_dwh ");
		buf.append("AND e.eqptsocle_id = es.eqpttubecrepine_eqptsocle_id_dwh ");
		buf.append("AND c.contrat_ve_id LIKE '" + contrat + "' ");
		buf.append("AND es.eqpttubecrepine_id IN  ");
		buf.append("	( ");
		buf.append("	SELECT t1.eqpttubecrepine_id   ");
		buf.append("	FROM patrimoine_eqpt_tubecrepine t1 ");
		buf.append("	WHERE (t1.eqpttubecrepine_eqptsocle_id, t1.id_chg) IN ");
		buf.append("		( ");
		buf.append("		SELECT t.eqpttubecrepine_eqptsocle_id, MAX(t.id_chg) as id_chg  ");
		buf.append("		FROM patrimoine_eqpt_tubecrepine t ");
		buf.append("		GROUP BY t.eqpttubecrepine_eqptsocle_id ");
		buf.append("		)  ");
		buf.append("	)  ");

		session = getHibernateTemplate().getCurrentSession();
		List<TypeEquipementTubeCrepine> equipementTubeCrepines = session.createSQLQuery(buf.toString()).addEntity(TypeEquipementTubeCrepine.class).list();
		session.close();
		System.out.println("Insert " + equipementTubeCrepines.size() + " lines of patrimoine_eqpt_tubecrepine");

		EquipementsTubesCrepine equipementTubeCrepine = new EquipementsTubesCrepine();
		equipementTubeCrepine.setEquipementTubeCrepine(equipementTubeCrepines);
		patrimoine.setEquipementsTubesCrepine(equipementTubeCrepine);

		// -----------------------------------------------------//
		// patrimoine_eqpt_turbinehydroelec
		// -----------------------------------------------------//

		buf = new StringBuffer();
		buf.append("SELECT es.* ");
		buf.append(" FROM patrimoine_contrat c, patrimoine_site s, patrimoine_ouvrage_socle o, patrimoine_unite_socle u, patrimoine_eqpt_socle e, patrimoine_eqpt_turbinehydroelec es ");
		buf.append("WHERE c.contrat_id = s.site_contrat_id_dwh ");
		buf.append("AND s.site_id = o.ouvragesocle_site_id_dwh ");
		buf.append("AND o.ouvragesocle_id = u.unitesocle_ouvragesocle_id_dwh ");
		buf.append("AND u.unitesocle_id = e.eqptsocle_idunite_dwh ");
		buf.append("AND e.eqptsocle_id = es.eqptturbhydele_eqptsocle_id_dwh ");
		buf.append("AND c.contrat_ve_id LIKE '" + contrat + "' ");
		buf.append("AND es.eqptturbhydele_id IN  ");
		buf.append("	( ");
		buf.append("	SELECT t1.eqptturbhydele_id   ");
		buf.append("	FROM patrimoine_eqpt_turbinehydroelec t1 ");
		buf.append("	WHERE (t1.eqptturbhydele_eqptsocle_id, t1.id_chg) IN ");
		buf.append("		( ");
		buf.append("		SELECT t.eqptturbhydele_eqptsocle_id, MAX(t.id_chg) as id_chg  ");
		buf.append("		FROM patrimoine_eqpt_turbinehydroelec t ");
		buf.append("		GROUP BY t.eqptturbhydele_eqptsocle_id ");
		buf.append("		)  ");
		buf.append("	)  ");

		session = getHibernateTemplate().getCurrentSession();
		List<TypeEquipementTurbineHydroelectrique> equipementTurbineHydroelectriques = session.createSQLQuery(buf.toString()).addEntity(TypeEquipementTurbineHydroelectrique.class).list();
		session.close();
		System.out.println("Insert " + equipementTurbineHydroelectriques.size() + " lines of patrimoine_eqpt_turbinehydroelec");

		EquipementsTurbinesHydroelectriques equipementTurbineHydroelectrique = new EquipementsTurbinesHydroelectriques();
		equipementTurbineHydroelectrique.setEquipementTurbineHydroelectrique(equipementTurbineHydroelectriques);
		patrimoine.setEquipementsTurbinesHydroelectriques(equipementTurbineHydroelectrique);

		// -----------------------------------------------------//
		// patrimoine_eqpt_tuyauterie
		// -----------------------------------------------------//

		buf = new StringBuffer();
		buf.append("SELECT es.* ");
		buf.append(" FROM patrimoine_contrat c, patrimoine_site s, patrimoine_ouvrage_socle o, patrimoine_unite_socle u, patrimoine_eqpt_socle e, patrimoine_eqpt_tuyauterie es ");
		buf.append("WHERE c.contrat_id = s.site_contrat_id_dwh ");
		buf.append("AND s.site_id = o.ouvragesocle_site_id_dwh ");
		buf.append("AND o.ouvragesocle_id = u.unitesocle_ouvragesocle_id_dwh ");
		buf.append("AND u.unitesocle_id = e.eqptsocle_idunite_dwh ");
		buf.append("AND e.eqptsocle_id = es.eqpttuyauterie_eqptsocle_id_dwh ");
		buf.append("AND c.contrat_ve_id LIKE '" + contrat + "' ");
		buf.append("AND es.eqpttuyauterie_id IN  ");
		buf.append("	( ");
		buf.append("	SELECT t1.eqpttuyauterie_id   ");
		buf.append("	FROM patrimoine_eqpt_tuyauterie t1 ");
		buf.append("	WHERE (t1.eqpttuyauterie_eqptsocle_id, t1.id_chg) IN ");
		buf.append("		( ");
		buf.append("		SELECT t.eqpttuyauterie_eqptsocle_id, MAX(t.id_chg) as id_chg  ");
		buf.append("		FROM patrimoine_eqpt_tuyauterie t ");
		buf.append("		GROUP BY t.eqpttuyauterie_eqptsocle_id ");
		buf.append("		)  ");
		buf.append("	)  ");

		session = getHibernateTemplate().getCurrentSession();
		List<TypeEquipementTuyauterie> equipementTuyauteries = session.createSQLQuery(buf.toString()).addEntity(TypeEquipementTuyauterie.class).list();
		session.close();
		System.out.println("Insert " + equipementTuyauteries.size() + " lines of patrimoine_eqpt_tuyauterie");

		EquipementsTuyauteries equipementTuyauterie = new EquipementsTuyauteries();
		equipementTuyauterie.setEquipementTuyauterie(equipementTuyauteries);
		patrimoine.setEquipementsTuyauteries(equipementTuyauterie);

		// -----------------------------------------------------//
		// patrimoine_eqpt_vanne
		// -----------------------------------------------------//

		buf = new StringBuffer();
		buf.append("SELECT es.* ");
		buf.append(" FROM patrimoine_contrat c, patrimoine_site s, patrimoine_ouvrage_socle o, patrimoine_unite_socle u, patrimoine_eqpt_socle e, patrimoine_eqpt_vanne es ");
		buf.append("WHERE c.contrat_id = s.site_contrat_id_dwh ");
		buf.append("AND s.site_id = o.ouvragesocle_site_id_dwh ");
		buf.append("AND o.ouvragesocle_id = u.unitesocle_ouvragesocle_id_dwh ");
		buf.append("AND u.unitesocle_id = e.eqptsocle_idunite_dwh ");
		buf.append("AND e.eqptsocle_id = es.eqptvanne_eqptsocle_id_dwh ");
		buf.append("AND c.contrat_ve_id LIKE '" + contrat + "' ");
		buf.append("AND es.eqptvanne_id IN  ");
		buf.append("	( ");
		buf.append("	SELECT t1.eqptvanne_id   ");
		buf.append("	FROM patrimoine_eqpt_vanne t1 ");
		buf.append("	WHERE (t1.eqptvanne_eqptsocle_id, t1.id_chg) IN ");
		buf.append("		( ");
		buf.append("		SELECT t.eqptvanne_eqptsocle_id, MAX(t.id_chg) as id_chg  ");
		buf.append("		FROM patrimoine_eqpt_vanne t ");
		buf.append("		GROUP BY t.eqptvanne_eqptsocle_id ");
		buf.append("		)  ");
		buf.append("	)  ");

		session = getHibernateTemplate().getCurrentSession();
		List<TypeEquipementVanne> equipementVannes = session.createSQLQuery(buf.toString()).addEntity(TypeEquipementVanne.class).list();
		session.close();
		System.out.println("Insert " + equipementVannes.size() + " lines of patrimoine_eqpt_vanne");

		EquipementsVannes equipementVanne = new EquipementsVannes();
		equipementVanne.setEquipementVanne(equipementVannes);
		patrimoine.setEquipementsVannes(equipementVanne);

		// -----------------------------------------------------//
		// patrimoine_eqpt_vanneregulation
		// -----------------------------------------------------//

		buf = new StringBuffer();
		buf.append("SELECT es.* ");
		buf.append(" FROM patrimoine_contrat c, patrimoine_site s, patrimoine_ouvrage_socle o, patrimoine_unite_socle u, patrimoine_eqpt_socle e, patrimoine_eqpt_vanneregulation es ");
		buf.append("WHERE c.contrat_id = s.site_contrat_id_dwh ");
		buf.append("AND s.site_id = o.ouvragesocle_site_id_dwh ");
		buf.append("AND o.ouvragesocle_id = u.unitesocle_ouvragesocle_id_dwh ");
		buf.append("AND u.unitesocle_id = e.eqptsocle_idunite_dwh ");
		buf.append("AND e.eqptsocle_id = es.eqptvanneregul_eqptsocle_id_dwh ");
		buf.append("AND c.contrat_ve_id LIKE '" + contrat + "' ");
		buf.append("AND es.eqptvanneregul_id IN  ");
		buf.append("	( ");
		buf.append("	SELECT t1.eqptvanneregul_id   ");
		buf.append("	FROM patrimoine_eqpt_vanneregulation t1 ");
		buf.append("	WHERE (t1.eqptvanneregul_eqptsocle_id, t1.id_chg) IN ");
		buf.append("		( ");
		buf.append("		SELECT t.eqptvanneregul_eqptsocle_id, MAX(t.id_chg) as id_chg  ");
		buf.append("		FROM patrimoine_eqpt_vanneregulation t ");
		buf.append("		GROUP BY t.eqptvanneregul_eqptsocle_id ");
		buf.append("		)  ");
		buf.append("	)  ");

		session = getHibernateTemplate().getCurrentSession();
		List<TypeEquipementVanneRegulation> equipementVanneRegulations = session.createSQLQuery(buf.toString()).addEntity(TypeEquipementVanneRegulation.class).list();
		session.close();
		System.out.println("Insert " + equipementVanneRegulations.size() + " lines of patrimoine_eqpt_vanneregulation");

		EquipementsVannesRegulation equipementVanneRegulation = new EquipementsVannesRegulation();
		equipementVanneRegulation.setEquipementVanneRegulation(equipementVanneRegulations);
		patrimoine.setEquipementsVannesRegulation(equipementVanneRegulation);

		// -----------------------------------------------------//
		// patrimoine_eqpt_variateurvitesse
		// -----------------------------------------------------//

		buf = new StringBuffer();
		buf.append("SELECT es.* ");
		buf.append(" FROM patrimoine_contrat c, patrimoine_site s, patrimoine_ouvrage_socle o, patrimoine_unite_socle u, patrimoine_eqpt_socle e, patrimoine_eqpt_variateurvitesse es ");
		buf.append("WHERE c.contrat_id = s.site_contrat_id_dwh ");
		buf.append("AND s.site_id = o.ouvragesocle_site_id_dwh ");
		buf.append("AND o.ouvragesocle_id = u.unitesocle_ouvragesocle_id_dwh ");
		buf.append("AND u.unitesocle_id = e.eqptsocle_idunite_dwh ");
		buf.append("AND e.eqptsocle_id = es.eqptvarvitesse_eqptsocle_id_dwh ");
		buf.append("AND c.contrat_ve_id LIKE '" + contrat + "' ");
		buf.append("AND es.eqptvarvitesse_id IN  ");
		buf.append("	( ");
		buf.append("	SELECT t1.eqptvarvitesse_id   ");
		buf.append("	FROM patrimoine_eqpt_variateurvitesse t1 ");
		buf.append("	WHERE (t1.eqptvarvitesse_eqptsocle_id, t1.id_chg) IN ");
		buf.append("		( ");
		buf.append("		SELECT t.eqptvarvitesse_eqptsocle_id, MAX(t.id_chg) as id_chg  ");
		buf.append("		FROM patrimoine_eqpt_variateurvitesse t ");
		buf.append("		GROUP BY t.eqptvarvitesse_eqptsocle_id ");
		buf.append("		)  ");
		buf.append("	)  ");

		session = getHibernateTemplate().getCurrentSession();
		List<TypeEquipementVariateurVitesse> equipementVariateurVitesses = session.createSQLQuery(buf.toString()).addEntity(TypeEquipementVariateurVitesse.class).list();
		session.close();
		System.out.println("Insert " + equipementVariateurVitesses.size() + " lines of patrimoine_eqpt_variateurvitesse");

		EquipementsVariateursVitesse equipementVariateurVitesse = new EquipementsVariateursVitesse();
		equipementVariateurVitesse.setEquipementVariateurVitesse(equipementVariateurVitesses);
		patrimoine.setEquipementsVariateursVitesse(equipementVariateurVitesse);

		// -----------------------------------------------------//
		// patrimoine_eqpt_verin
		// -----------------------------------------------------//

		buf = new StringBuffer();
		buf.append("SELECT es.* ");
		buf.append(" FROM patrimoine_contrat c, patrimoine_site s, patrimoine_ouvrage_socle o, patrimoine_unite_socle u, patrimoine_eqpt_socle e, patrimoine_eqpt_verin es ");
		buf.append("WHERE c.contrat_id = s.site_contrat_id_dwh ");
		buf.append("AND s.site_id = o.ouvragesocle_site_id_dwh ");
		buf.append("AND o.ouvragesocle_id = u.unitesocle_ouvragesocle_id_dwh ");
		buf.append("AND u.unitesocle_id = e.eqptsocle_idunite_dwh ");
		buf.append("AND e.eqptsocle_id = es.eqptverin_eqptsocle_id_dwh ");
		buf.append("AND c.contrat_ve_id LIKE '" + contrat + "' ");
		buf.append("AND es.eqptverin_id IN  ");
		buf.append("	( ");
		buf.append("	SELECT t1.eqptverin_id   ");
		buf.append("	FROM patrimoine_eqpt_verin t1 ");
		buf.append("	WHERE (t1.eqptverin_eqptsocle_id, t1.id_chg) IN ");
		buf.append("		( ");
		buf.append("		SELECT t.eqptverin_eqptsocle_id, MAX(t.id_chg) as id_chg  ");
		buf.append("		FROM patrimoine_eqpt_verin t ");
		buf.append("		GROUP BY t.eqptverin_eqptsocle_id ");
		buf.append("		)  ");
		buf.append("	)  ");

		session = getHibernateTemplate().getCurrentSession();
		List<TypeEquipementVerin> equipementVerins = session.createSQLQuery(buf.toString()).addEntity(TypeEquipementVerin.class).list();
		session.close();
		System.out.println("Insert " + equipementVerins.size() + " lines of patrimoine_eqpt_verin");

		EquipementsVerins equipementVerin = new EquipementsVerins();
		equipementVerin.setEquipementVerin(equipementVerins);
		patrimoine.setEquipementsVerins(equipementVerin);

		// -----------------------------------------------------//
		// patrimoine_eqpt_vistransfert
		// -----------------------------------------------------//

		buf = new StringBuffer();
		buf.append("SELECT es.* ");
		buf.append(" FROM patrimoine_contrat c, patrimoine_site s, patrimoine_ouvrage_socle o, patrimoine_unite_socle u, patrimoine_eqpt_socle e, patrimoine_eqpt_vistransfert es ");
		buf.append("WHERE c.contrat_id = s.site_contrat_id_dwh ");
		buf.append("AND s.site_id = o.ouvragesocle_site_id_dwh ");
		buf.append("AND o.ouvragesocle_id = u.unitesocle_ouvragesocle_id_dwh ");
		buf.append("AND u.unitesocle_id = e.eqptsocle_idunite_dwh ");
		buf.append("AND e.eqptsocle_id = es.eqptvistransfert_eqptsocle_id_dwh ");
		buf.append("AND c.contrat_ve_id LIKE '" + contrat + "' ");
		buf.append("AND es.eqptvistransfert_id IN  ");
		buf.append("	( ");
		buf.append("	SELECT t1.eqptvistransfert_id   ");
		buf.append("	FROM patrimoine_eqpt_vistransfert t1 ");
		buf.append("	WHERE (t1.eqptvistransfert_eqptsocle_id, t1.id_chg) IN ");
		buf.append("		( ");
		buf.append("		SELECT t.eqptvistransfert_eqptsocle_id, MAX(t.id_chg) as id_chg  ");
		buf.append("		FROM patrimoine_eqpt_vistransfert t ");
		buf.append("		GROUP BY t.eqptvistransfert_eqptsocle_id ");
		buf.append("		)  ");
		buf.append("	)  ");

		session = getHibernateTemplate().getCurrentSession();
		List<TypeEquipementVisTransfert> equipementVisTransferts = session.createSQLQuery(buf.toString()).addEntity(TypeEquipementVisTransfert.class).list();
		session.close();
		System.out.println("Insert " + equipementVisTransferts.size() + " lines of patrimoine_eqpt_vistransfert");

		EquipementsVisTransfert equipementVisTransfert = new EquipementsVisTransfert();
		equipementVisTransfert.setEquipementVisTransfert(equipementVisTransferts);
		patrimoine.setEquipementsVisTransfert(equipementVisTransfert);

		// -----------------------------------------------------//
		// patrimoine_eqpt_voirie
		// -----------------------------------------------------//

		buf = new StringBuffer();
		buf.append("SELECT es.* ");
		buf.append(" FROM patrimoine_contrat c, patrimoine_site s, patrimoine_ouvrage_socle o, patrimoine_unite_socle u, patrimoine_eqpt_socle e, patrimoine_eqpt_voirie es ");
		buf.append("WHERE c.contrat_id = s.site_contrat_id_dwh ");
		buf.append("AND s.site_id = o.ouvragesocle_site_id_dwh ");
		buf.append("AND o.ouvragesocle_id = u.unitesocle_ouvragesocle_id_dwh ");
		buf.append("AND u.unitesocle_id = e.eqptsocle_idunite_dwh ");
		buf.append("AND e.eqptsocle_id = es.eqptvoirie_eqptsocle_id_dwh ");
		buf.append("AND c.contrat_ve_id LIKE '" + contrat + "' ");
		buf.append("AND es.eqptvoirie_id IN  ");
		buf.append("	( ");
		buf.append("	SELECT t1.eqptvoirie_id   ");
		buf.append("	FROM patrimoine_eqpt_voirie t1 ");
		buf.append("	WHERE (t1.eqptvoirie_eqptsocle_id, t1.id_chg) IN ");
		buf.append("		( ");
		buf.append("		SELECT t.eqptvoirie_eqptsocle_id, MAX(t.id_chg) as id_chg  ");
		buf.append("		FROM patrimoine_eqpt_voirie t ");
		buf.append("		GROUP BY t.eqptvoirie_eqptsocle_id ");
		buf.append("		)  ");
		buf.append("	)  ");

		session = getHibernateTemplate().getCurrentSession();
		List<TypeEquipementVoirie> equipementVoiries = session.createSQLQuery(buf.toString()).addEntity(TypeEquipementVoirie.class).list();
		session.close();
		System.out.println("Insert " + equipementVoiries.size() + " lines of patrimoine_eqpt_voirie");

		EquipementsVoiries equipementVoirie = new EquipementsVoiries();
		equipementVoirie.setEquipementVoirie(equipementVoiries);
		patrimoine.setEquipementsVoiries(equipementVoirie);

		// -----------------------------------------------------//
		// patrimoine_intervention
		// -----------------------------------------------------//

		buf = new StringBuffer();
		buf.append("SELECT i.* ");
		buf.append(" FROM patrimoine_contrat c, patrimoine_site s, patrimoine_ouvrage_socle o, patrimoine_unite_socle u, patrimoine_eqpt_socle e, patrimoine_intervention i ");
		buf.append("WHERE c.contrat_id = s.site_contrat_id_dwh ");
		buf.append("AND s.site_id = o.ouvragesocle_site_id_dwh ");
		buf.append("AND o.ouvragesocle_id = u.unitesocle_ouvragesocle_id_dwh ");
		buf.append("AND u.unitesocle_id = i.intervention_unitesocle_id_dwh ");
		buf.append("AND e.eqptsocle_id = i.intervention_eqptsocle_id_dwh ");
		buf.append("AND c.contrat_ve_id LIKE '" + contrat + "' ");
		buf.append("AND i.intervention_id IN  ");
		buf.append("	( ");
		buf.append("	SELECT t1.intervention_id   ");
		buf.append("	FROM patrimoine_intervention t1 ");
		buf.append("	WHERE (t1.intervention_unitesocle_id, t1.intervention_eqptsocle_id, t1.id_chg) IN ");
		buf.append("		( ");
		buf.append("		SELECT t.intervention_unitesocle_id, t.intervention_eqptsocle_id, MAX(t.id_chg) as id_chg  ");
		buf.append("		FROM patrimoine_intervention t ");
		buf.append("		GROUP BY t.intervention_unitesocle_id, t.intervention_eqptsocle_id ");
		buf.append("		)  ");
		buf.append("	)  ");

		session = getHibernateTemplate().getCurrentSession();
		List<TypeIntervention> interventions = session.createSQLQuery(buf.toString()).addEntity(TypeIntervention.class).list();
		session.close();
		System.out.println("Insert " + interventions.size() + " lines of patrimoine_intervention");

		Interventions intervention = new Interventions();
		intervention.setIntervention(interventions);
		patrimoine.setInterventions(intervention);

		return patrimoine;
	}

}
