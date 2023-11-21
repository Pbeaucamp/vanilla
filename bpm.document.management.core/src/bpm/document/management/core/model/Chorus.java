package bpm.document.management.core.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import bpm.document.management.core.model.PastellData.ObjectType;
import bpm.document.management.core.model.aklademat.PastellFile;
import bpm.document.management.core.model.aklademat.PastellFile.FileType;

public class Chorus implements IAdminDematObject {

	public static final String DEFAULT_FORM_NAME = "DefaultChorusForm";
	
	public static final String TAG_ROOT_LIST = "factures";
	public static final String TAG_ROOT = "facture";
	
	// public static final String TAG_IDENTIFIANT = "nom";
	// public static final String TAG_FOURNISSEUR = "fournisseur";
	// public static final String TAG_DEBITEUR = "debiteur";
	// public static final String TAG_SERVICE_EXECUTANT = "service_executant";
	// public static final String TAG_MONTANTHT = "montantht";
	// public static final String TAG_MONTANTTTC = "montantttc";
	// public static final String TAG_NET_A_PAYER = "net_a_payer";
	// public static final String TAG_DATE_EMISSION = "date_emission";
	// public static final String TAG_DEVISE = "devise";
	// public static final String TAG_MONTANT_TAXE = "montant_taxe";
	// public static final String TAG_NUM_FACTURE = "num_facture";
	// public static final String TAG_NUM_ENGAGEMENT = "num_engagement";
	// public static final String TAG_NUM_MARCHE = "num_marche";
	//
	// public static final String KEYS_MONTANTHT = "Total HT";
	// public static final String KEYS_NET_A_PAYER = "NET A PAYER;NETA PAYER";
	// public static final String KEYS_MONTANT_TAXE = "TVA 20,00%;T.V.A 20,00%;TVA 20,00 %";
	// public static final String KEYS_NUM_MARCHE = "Marché n°;Marché";
	
	public static final String TYPE_FACTURE = "Facture";
	public static final String TYPE_AVOIR = "Avoir";

	public static final String KEYS_MONTANTTTC = "Total TTC;Total T.T.C;Montant T.T.C.;Montants TTC;TTC EUR:;Total TTC €;NET A PAYER TTC;Net TTC (€)";
	public static final String KEYS_MONTANTHT = "Total HT;Total hors taxes;Montant HT;H.T.;H.T;Total net HT;Total HT net";
	public static final String KEYS_MONTANTTVA = "Total TVA;Montants TVA;Montant TVA;TVA:;Montant T.V.A.;TVA (20%);TVA";
	public static final String KEYS_NUM_FACTURE = "Facture N°;Facture N';Facture N;Numéro;Référence Pièce;Facture;Facture No;No Facture";
	public static final String KEYS_NUM_ENGAGEMENT = "Affaire n°;Engagement n°;Affaire n'";
	public static final String KEYS_IDENTIFIANT = "Siret;siiet";
	public static final String KEYS_SERVICE_EXECUTANT = "service";
	public static final String KEYS_IDENTIFIANT_FOURN = "Siret;siiet";
	public static final String KEYS_TYPE_FACTURE = TYPE_FACTURE+";"+TYPE_AVOIR;
	public static final String KEYS_DATE_FACTURE = "Date facture;Date de facture;Date de facturation";
	public static final String KEYS_DATE_DEPOT = "Date dépot;date d'émission;date d'expédition;date de dépot";
	public static final String KEYS_NOM_FOURNISSEUR = "Fournisseur;Emetteur";
	public static final String KEYS_DESTINATAIRE = "Code client;code destinaire";

	public static final String P_CPP_ID_FACTURE_CPP = "id_facture_cpp";
	public static final String P_CPP_STATUT_CPP = "statut_cpp";
	public static final String P_CPP_STATUT_CIBLE = "statut_cible_liste";
	public static final String P_CPP_FOURNISSEUR = "fournisseur";
	public static final String P_CPP_DESTINATAIRE = "destinataire";
	public static final String P_CPP_SIRET = "siret";
	public static final String P_CPP_SERVICE_DESTINATAIRE = "service_destinataire";
	public static final String P_CPP_SERVICE_DESTINATAIRE_CODE = "service_destinataire_code";
	public static final String P_CPP_TYPE_FACTURE = "type_facture";
	public static final String P_CPP_NO_FACTURE = "no_facture";
	public static final String P_CPP_DATE_FACTURE = "date_facture";
	public static final String P_CPP_DATE_DEPOT = "date_depot";
	public static final String P_CPP_MONTANT_TTC = "montant_ttc";
	public static final String P_CPP_TYPE_IDENTIFIANT = "type_identifiant";
	public static final String P_CPP_FOURNISSEUR_RAISON_SOCIALE = "fournisseur_raison_sociale";
	public static final String P_CPP_DATE_MISE_A_DISPO = "date_mise_a_dispo";
	public static final String P_CPP_DATE_FIN_SUSPENSION = "date_fin_suspension";
	public static final String P_CPP_DATE_PASSAGE_STATUT = "date_passage_statut";
	public static final String P_CPP_IS_CPP = "is_cpp";
	public static final String P_CPP_TYPE_INTEGRATION = "type_integration";
	public static final String P_CPP_FACTURE_NUMERO_ENGAGEMENT = "facture_numero_engagement";
	public static final String P_CPP_FACTURE_CADRE = "facture_cadre";
	public static final String P_CPP_ENVOI_VISA = "envoi_visa";
	public static final String P_CPP_IPARAPHEUR_TYPE = "iparapheur_type";
	public static final String P_CPP_IPARAPHEUR_SOUS_TYPE = "iparapheur_sous_type";
	public static final String P_CPP_ENVOI_GED = "envoi_ged";
	public static final String P_CPP_ENVOI_SAE = "envoi_sae";
	public static final String P_CPP_CHECK_MISE_A_DISPO_GF = "check_mise_a_dispo_gf";
	public static final String P_CPP_ENVOI_AUTO = "envoi_auto";
	
	//Not used in Chorus (for cocktail)
	public static final String P_MONTANT_HT = "montant_ht";
	public static final String P_MONTANT_TVA = "montant_tva";
	public static final String P_COMMENTAIRE = "commentaire";

	public static final String[] P_CPP_PARAMS = { P_CPP_ID_FACTURE_CPP, P_CPP_STATUT_CPP, P_CPP_STATUT_CIBLE, P_CPP_FOURNISSEUR, P_CPP_DESTINATAIRE, P_CPP_SIRET, P_CPP_SERVICE_DESTINATAIRE, P_CPP_SERVICE_DESTINATAIRE_CODE, P_CPP_TYPE_FACTURE, P_CPP_NO_FACTURE, P_CPP_DATE_FACTURE, P_CPP_DATE_DEPOT, P_CPP_MONTANT_TTC, P_CPP_TYPE_IDENTIFIANT, P_CPP_FOURNISSEUR_RAISON_SOCIALE, P_CPP_DATE_MISE_A_DISPO, P_CPP_DATE_FIN_SUSPENSION, P_CPP_DATE_PASSAGE_STATUT, P_CPP_IS_CPP, P_CPP_TYPE_INTEGRATION, P_CPP_FACTURE_NUMERO_ENGAGEMENT, P_CPP_FACTURE_CADRE, P_CPP_ENVOI_VISA, P_CPP_IPARAPHEUR_TYPE, P_CPP_IPARAPHEUR_SOUS_TYPE, 
		P_CPP_ENVOI_GED, P_CPP_ENVOI_SAE, P_CPP_CHECK_MISE_A_DISPO_GF, P_CPP_ENVOI_AUTO, P_MONTANT_HT, P_MONTANT_TVA };

	// Multiple files
	public static final String P_CPP_FICHIER_FACTURE = "fichier_facture";
	public static final String P_CPP_FICHIER_FACTURE_PDF = "fichier_facture_pdf";
	public static final String P_CPP_FACTURE_PJ_01 = "facture_pj_01";
	public static final String P_CPP_FACTURE_PJ_02 = "facture_pj_02";
	
	//Status
	public static final String SUSPENDUE = "SUSPENDUE";
	public static final String MISE_A_DISPOSITION = "MISE_A_DISPOSITION";
	public static final String A_RECYCLER = "A_RECYCLER";
	public static final String REJETEE = "REJETEE";
	public static final String SERVICE_FAIT = "SERVICE_FAIT";
	public static final String MANDATEE = "MANDATEE";
	public static final String MISE_A_DISPOSITION_COMPTABLE = "MISE_A_DISPOSITION_COMPTABLE";
	public static final String COMPTABILISEE = "COMPTABILISEE";
	public static final String MISE_EN_PAIEMENT = "MISE_EN_PAIEMENT";
	public static final String SERVICE_FAIT_ET_MANDATEE = "SERVICE_FAIT;MANDATEE";
	
	//Taux TVA
	public static final String TVA2_1 = "2.1"; // tva 2.1% en France, 0.9% en Corse
	public static final String TVA5_5 = "5.5"; // tva 5.5%
	public static final String TVA10 = "10"; // tva 10% 
	public static final String TVA13 = "13"; // tva 13%
	public static final String TVA20 = "20"; // tva 20%
	public static final String TVA8_5 = "8.5"; // tva 8.5%
	public static final String TVA7 = "7"; // tva 7%
	public static final String TVA20_6 = "20.6"; // tva 20.6%
	public static final String TVA19_6 = "19.6"; // tva 19.6%
	public static final String TVA0 = "0"; // tva 0
	public static final String TVA0_9 = "0.9"; // tva 0.9% en Corse
	public static final String[] P_CPP_TVAS = {TVA0, TVA0_9, TVA2_1, TVA5_5, TVA7, TVA8_5, TVA10, TVA13, TVA19_6, TVA20, TVA20_6};
	
	private static final long serialVersionUID = 1L;

	private AkladematAdminEntity<Chorus> parent;

	private HashMap<String, PastellData> metadata = new HashMap<String, PastellData>();
	private HashMap<FileType, List<PastellFile>> files = new LinkedHashMap<FileType, List<PastellFile>>();
	
	private List<Integer> validators;
	
	private Cocktail cocktail;
	
	private Date dateCertification;
	
	public Chorus() {
	}

	@Override
	@SuppressWarnings("unchecked")
	public void setParent(AkladematAdminEntity<? extends IAdminDematObject> parent) {
		this.parent = (AkladematAdminEntity<Chorus>) parent;
	}

	public AkladematAdminEntity<Chorus> getParent() {
		return parent;
	}

	public void setMetadata(HashMap<String, PastellData> metadata) {
		this.metadata = metadata;
	}

	public HashMap<String, PastellData> getMetadata() {
		return metadata;
	}

	public void addData(String key, PastellData data) {
		if (metadata == null) {
			metadata = new HashMap<String, PastellData>();
		}
		metadata.put(key, data);
	}

	public void addData(String key, String value) {
		addData(key, new PastellData(ObjectType.STRING, value));
	}

	public String getIdFactureCpp() {
		return getStringValue(P_CPP_ID_FACTURE_CPP);
	}

	public String getFournisseur() {
		return getStringValue(P_CPP_FOURNISSEUR);
	}

	public String getRaisonSocialeFournisseur() {
		return getStringValue(P_CPP_FOURNISSEUR_RAISON_SOCIALE);
	}

	public String getDestinataire() {
		return getStringValue(P_CPP_DESTINATAIRE);
	}
	
	public String getSiret() {
		return getStringValue(P_CPP_SIRET);
	}

	public String getServiceDestinataire() {
		return getStringValue(P_CPP_SERVICE_DESTINATAIRE);
	}

	public String getCodeServiceDestinataire() {
		return getStringValue(P_CPP_SERVICE_DESTINATAIRE_CODE);
	}

	public String getNumeroFacture() {
		return getStringValue(P_CPP_NO_FACTURE);
	}

	public Date getDateFacture() throws Exception {
		return getDateValue(P_CPP_DATE_FACTURE);
	}

	public Date getDateDepot() throws Exception {
		return getDateValue(P_CPP_DATE_DEPOT);
	}

	public String getDateDepotAsString() {
		return getStringValue(P_CPP_DATE_DEPOT);
	}

	public Double getMontantTTC() {
		return getDoubleValue(P_CPP_MONTANT_TTC);
	}

	public String getFactureNumeroEngagement() {
		return getStringValue(P_CPP_FACTURE_NUMERO_ENGAGEMENT);
	}
	
	public String getCadreFacturation() {
		return getStringValue(P_CPP_FACTURE_CADRE);
	}
	
	public String getTypeFacture() {
		return getStringValue(P_CPP_TYPE_FACTURE);
	}

	private String getStringValue(String key) {
		if (metadata != null && metadata.get(key) != null) {
			PastellData data = metadata.get(key);
			return data.getValueAsString();
		}
		return null;
	}

	public Double getDoubleValue(String key) {
		if (metadata != null && metadata.get(key) != null) {
			PastellData data = metadata.get(key);
			return data.getValueAsDouble();
		}
		return null;
	}

	private Date getDateValue(String key) throws Exception {
		if (metadata != null && metadata.get(key) != null) {
			PastellData data = metadata.get(key);
			return data.getValueAsDate();
		}
		return null;
	}

	public Map<String, PastellData> getAllOtherMetadata() {
		Map<String, PastellData> result = new HashMap<String, PastellData>();
		for (String key : metadata.keySet()) {
			boolean found = false;
			for (String param : P_CPP_PARAMS) {
				if (key.equals(param)) {
					found = true;
					break;
				}
			}
			
			if (found) {
				continue;
			}

			result.put(key, metadata.get(key));
		}
		return result;
	}
	
	public List<FileType> getFileTypes() {
		return files != null ? new ArrayList<FileType>(files.keySet()) : null;
	}
	
	public List<PastellFile> getFiles(FileType type) {
		return files != null ? files.get(type) : null;
	}
	
	public void addFile(PastellFile file) {
		List<PastellFile> tmpFiles = files.get(file.getType());
		if (tmpFiles == null) {
			tmpFiles = new ArrayList<PastellFile>();
		}
		tmpFiles.add(file);
		files.put(file.getType(), tmpFiles);
	}
	
	public List<Integer> getValidators() {
		return validators;
	}

	public void setValidators(List<Integer> validators) {
		this.validators = validators;
	}
	
	public static HashMap<String, String> getKeysMap(){
		HashMap<String, String> keys = new HashMap<String, String>();
		keys.put(Chorus.P_CPP_MONTANT_TTC, Chorus.KEYS_MONTANTTTC);
		keys.put(Chorus.P_CPP_FACTURE_NUMERO_ENGAGEMENT, Chorus.KEYS_NUM_ENGAGEMENT);
		keys.put(Chorus.P_CPP_NO_FACTURE, Chorus.KEYS_NUM_FACTURE);
		keys.put(Chorus.P_CPP_SIRET, Chorus.KEYS_IDENTIFIANT);
		keys.put(Chorus.P_CPP_SERVICE_DESTINATAIRE, Chorus.KEYS_SERVICE_EXECUTANT);
		keys.put(Chorus.P_CPP_DESTINATAIRE, Chorus.KEYS_DESTINATAIRE);
		keys.put(Chorus.P_CPP_FOURNISSEUR, Chorus.KEYS_IDENTIFIANT_FOURN);
		keys.put(Chorus.P_CPP_TYPE_FACTURE, Chorus.KEYS_TYPE_FACTURE);
		keys.put(Chorus.P_CPP_DATE_FACTURE, Chorus.KEYS_DATE_FACTURE);
		keys.put(Chorus.P_CPP_DATE_DEPOT, Chorus.KEYS_DATE_DEPOT);
		keys.put(Chorus.P_CPP_FOURNISSEUR_RAISON_SOCIALE, Chorus.KEYS_NOM_FOURNISSEUR);
		keys.put(Chorus.P_CPP_TYPE_IDENTIFIANT, Chorus.KEYS_IDENTIFIANT);
		keys.put(Chorus.P_MONTANT_HT, Chorus.KEYS_MONTANTHT);
		keys.put(Chorus.P_MONTANT_TVA, Chorus.KEYS_MONTANTTVA);
		return keys;
		
		
	}

	public Cocktail getCocktail() {
		return cocktail;
	}

	public void setCocktail(Cocktail cocktail) {
		this.cocktail = cocktail;
		if(!cocktail.getMetadata().isEmpty()){
			cocktail.fillChorus(this);
		}
		
	}

	public Date getDateCertification() {
		return dateCertification;
	}

	public void setDateCertification(Date dateCertification) {
		this.dateCertification = dateCertification;
	}
}
