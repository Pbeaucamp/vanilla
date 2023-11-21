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

public class Cocktail implements IAdminDematObject {


	public static final String P_CK_ID_DEP_FACTURE = "id_dep_facture";
	public static final String P_CK_FOU_ORDRE = "fou_ordre";  // clé fournisseur
	public static final String P_CK_NUM_FACTURE_FOURNISSEUR = "num_facture_fournisseur";
	public static final String P_CK_COMMENTAIRE = "commentaire";
	public static final String P_CK_DATE_EMISSION = "date_emission";
	public static final String P_CK_DATE_RECEPTION = "date_reception";
	public static final String P_CK_EXE_ORDRE = "exe_ordre";
	public static final String P_CK_TOTAL_HT = "total_ht";
	public static final String P_CK_TOTAL_TVA = "total_tva";
	public static final String P_CK_TOTAL_TTC = "total_ttc";
	public static final String P_CK_CHAINE_DETAILS = "chaine_details";
	public static final String P_CK_ID_DEP_EJ = "id_dep_ej";  //engagement juridique
	public static final String P_CK_NUM_EJ = "num_ej";  // = TX_TVA$M_HT$M_TVA$M_TTC[:TX_TVA$M_HT$M_TVA$M_TTC]
	public static final String P_CK_TYPE_FACTURE = "type_facture";
	public static final String P_CK_ORIGINE_FACTURE = "origine_facture";
	public static final String P_CK_PERS_ID_CREATION = "pers_id_creation";

	public static final String[] P_CPP_PARAMS = { P_CK_ID_DEP_FACTURE, P_CK_FOU_ORDRE, P_CK_NUM_FACTURE_FOURNISSEUR, 
		P_CK_COMMENTAIRE, P_CK_DATE_EMISSION, P_CK_DATE_RECEPTION, P_CK_EXE_ORDRE, P_CK_DATE_EMISSION, P_CK_DATE_RECEPTION, 
		P_CK_EXE_ORDRE, P_CK_TOTAL_HT, P_CK_TOTAL_TVA, P_CK_TOTAL_TTC, P_CK_CHAINE_DETAILS, P_CK_ID_DEP_EJ, 
		P_CK_TYPE_FACTURE, P_CK_ORIGINE_FACTURE, P_CK_PERS_ID_CREATION
	};

	public static final String CK_STATUS_VISE = "VISE";
	public static final String CK_STATUS_PAYE = "PAYE";
	public static final String CK_STATUS_ANNULE = "ANNULE";
	public static final String CK_STATUS_REJETE_AGENT_COMPTABLE = "REJETE_AGENT_COMPTABLE";
	public static final String CK_STATUS_REJETE_ORDONNATEUR = "REJETE_ORDONNATEUR";
	public static final String CK_STATUS_VALIDE = "VALIDE";
	
	public enum RejectType {
		COMPTA,	ORDO;
	}
	
	private static final long serialVersionUID = 1L;
	
	private AkladematAdminEntity<Chorus> parent;

	private LinkedHashMap<String, PastellData> metadata = new LinkedHashMap<String, PastellData>();
	private HashMap<FileType, List<PastellFile>> files = new LinkedHashMap<FileType, List<PastellFile>>();
	
	
	public Cocktail() {
	}

	

	public void setMetadata(LinkedHashMap<String, PastellData> metadata) {
		this.metadata = metadata;
	}

	public HashMap<String, PastellData> getMetadata() {
		return metadata;
	}

	public void addData(String key, PastellData data) {
		if (metadata == null) {
			metadata = new LinkedHashMap<String, PastellData>();
		}
		metadata.put(key, data);
	}

	public void addData(String key, String value) {
		addData(key, new PastellData(ObjectType.STRING, value));
	}

	public String getIdFactureDep() {
		return getStringValue(P_CK_ID_DEP_FACTURE);
	}

	public String getFouOrdre() {
		return getStringValue(P_CK_FOU_ORDRE);
	}

	public String getChaineDetails() {
		return getStringValue(P_CK_CHAINE_DETAILS);
	}

	public String getCommentaire() {
		return getStringValue(P_CK_COMMENTAIRE);
	}
	
	public Date getDateEmission() throws Exception {
		return getDateValue(P_CK_DATE_EMISSION);
	}

	public Date getDateReception() throws Exception {
		return getDateValue(P_CK_DATE_RECEPTION);
	}

	public String getExeOrdre() {
		return getStringValue(P_CK_EXE_ORDRE);
	}

	public String getNumeroEngagement() {
		return getStringValue(P_CK_ID_DEP_EJ);
	}

	public String getNumFactureFour() {
		return getStringValue(P_CK_NUM_FACTURE_FOURNISSEUR);
	}

	public String getOrigineFacture() {
		return getStringValue(P_CK_ORIGINE_FACTURE);
	}

	public String getPersIdCreation() {
		return getStringValue(P_CK_PERS_ID_CREATION);
	}

	public Double getMontantHT() {
		return getDoubleValue(P_CK_TOTAL_HT);
	}

	public Double getMontantTTC() {
		return getDoubleValue(P_CK_TOTAL_TTC);
	}
	
	public Double getMontantTVA() {
		return getDoubleValue(P_CK_TOTAL_TVA);
	}
	
	public String getTypeFacture() {
		return getStringValue(P_CK_TYPE_FACTURE);
	}

	private String getStringValue(String key) {
		if (metadata != null && metadata.get(key) != null) {
			PastellData data = metadata.get(key);
			return data.getValueAsString();
		}
		return null;
	}

	private Double getDoubleValue(String key) {
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
	
	private Integer getIntValue(String key) {
		if (metadata != null && metadata.get(key) != null) {
			PastellData data = metadata.get(key);
			return data.getValueAsInt();
		}
		return null;
	}

	public Map<String, PastellData> getAllOtherMetadata() {
		Map<String, PastellData> result = new HashMap<String, PastellData>();
		for (String key : metadata.keySet()) {
			for (String param : P_CPP_PARAMS) {
				if (key.equals(param)) {
					continue;
				}
			}

			result.put(key, metadata.get(key));
		}
		return result;
	}
	
//	public List<FileType> getFileTypes() {
//		return files != null ? new ArrayList<FileType>(files.keySet()) : null;
//	}
//	
//	public List<PastellFile> getFiles(FileType type) {
//		return files != null ? files.get(type) : null;
//	}
	
//	public void addFile(PastellFile file) {
//		List<PastellFile> tmpFiles = files.get(file.getType());
//		if (tmpFiles == null) {
//			tmpFiles = new ArrayList<PastellFile>();
//		}
//		tmpFiles.add(file);
//		files.put(file.getType(), tmpFiles);
//	}



	public void loadChorus(Chorus chorus) {
		if(chorus == null){
			addData(P_CK_TYPE_FACTURE, "");
			addData(P_CK_NUM_FACTURE_FOURNISSEUR, "");
			addData(P_CK_NUM_EJ, "");
			//addData(P_CK_FOU_ORDRE, "");
			addData(P_CK_DATE_EMISSION, new PastellData(ObjectType.DATE, null));
			addData(P_CK_DATE_RECEPTION, new PastellData(ObjectType.DATE, null));
			//addData(P_CK_ID_DEP_EJ, "");
			addData(P_CK_CHAINE_DETAILS, "");
			addData(P_CK_TOTAL_HT, "");
			addData(P_CK_TOTAL_TVA, "");
			addData(P_CK_TOTAL_TTC, "");
			addData(P_CK_COMMENTAIRE, "");
			
		} else {
			addData(P_CK_TYPE_FACTURE, chorus.getTypeFacture());
			addData(P_CK_NUM_FACTURE_FOURNISSEUR, chorus.getNumeroFacture());
			addData(P_CK_NUM_EJ, chorus.getFactureNumeroEngagement());
			//addData(P_CK_FOU_ORDRE, chorus.getFournisseur());
			try {
				addData(P_CK_DATE_EMISSION, new PastellData(ObjectType.DATE, chorus.getDateFacture()));
			} catch (Exception e) {
				e.printStackTrace();
				addData(P_CK_DATE_EMISSION, new PastellData(ObjectType.DATE, null));
			}
			try {
				addData(P_CK_DATE_RECEPTION, new PastellData(ObjectType.DATE, chorus.getDateDepot()));
			} catch (Exception e) {
				e.printStackTrace();
				addData(P_CK_DATE_RECEPTION, new PastellData(ObjectType.DATE, null));
			}
//			addData(P_CK_ID_DEP_EJ, chorus.getFactureNumeroEngagement());
			try {
				addData(P_CK_TOTAL_HT, new PastellData(ObjectType.DOUBLE, chorus.getDoubleValue(Chorus.P_MONTANT_HT)));
			} catch (Exception e) {
				addData(P_CK_TOTAL_HT, new PastellData(ObjectType.DOUBLE, 0));
			}
			try {
				addData(P_CK_TOTAL_TVA, new PastellData(ObjectType.DOUBLE, chorus.getDoubleValue(Chorus.P_MONTANT_TVA)));
			} catch (Exception e) {
				addData(P_CK_TOTAL_TVA, new PastellData(ObjectType.DOUBLE, 0));
			}
			addData(P_CK_TOTAL_TTC, new PastellData(ObjectType.DOUBLE, chorus.getMontantTTC()));
			String tvas = "";
			for(String tva : Chorus.P_CPP_TVAS){
				if(chorus.getDoubleValue(tva) != null){
					//TX_TVA$M_HT$M_TVA$M_TTC
					Double value = chorus.getDoubleValue(tva);
					Double taux = Double.parseDouble(tva);
					Double ht = (double) ((double)Math.round((value / taux * 100) * 100) / 100);
					Double ttc = (double) ((double)Math.round((ht + value) * 100) / 100);
					tvas += tva + "$" + ht + "$" + value + "$" + ttc + ":";
				}
			}
			if(!tvas.isEmpty()){
				tvas = tvas.substring(0, tvas.length()-1);
			}
			addData(P_CK_CHAINE_DETAILS, new PastellData(ObjectType.STRING, tvas));
			
			//remplissage si vide
			String[] rows = getChaineDetails().split(":");
			if(getMontantHT() == null && !getChaineDetails().isEmpty()){
				Double sum = 0.0;
				for(String row : rows){
					sum += Double.parseDouble(row.split("\\$")[1]);
				}
				addData(P_CK_TOTAL_HT, new PastellData(ObjectType.DOUBLE, sum));
			}
			if(getMontantTVA() == null && !getChaineDetails().isEmpty()){
				Double sum = 0.0;
				for(String row : rows){
					sum += Double.parseDouble(row.split("\\$")[2]);
				}
				addData(P_CK_TOTAL_TVA, new PastellData(ObjectType.DOUBLE, sum));
			}
			if(getMontantTTC() == null && !getChaineDetails().isEmpty()){
				Double sum = 0.0;
				for(String row : rows){
					sum += Double.parseDouble(row.split("\\$")[3]);
				}
				addData(P_CK_TOTAL_TTC, new PastellData(ObjectType.DOUBLE, sum));
			}
			
			addData(P_CK_COMMENTAIRE, "");
		}
		
		try {
			if(getDateReception() == null){
				addData(P_CK_DATE_RECEPTION, new PastellData(ObjectType.DATE, new Date()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



	public List<String> getPossibleValues(String key) {
		// TODO Auto-generated method stub
		List<String> res = new ArrayList<String>();
		//TEMPORAIRE
		if(key.equals(P_CK_FOU_ORDRE)){
			res.add("123");
			res.add("456");
			res.add("789");
		} else if (key.equals(P_CK_ID_DEP_EJ)){
			res.add("987654");
			res.add("123456");
//		} else if (key.equals(P_CK_ID_DEP_FACTURE)){
//			res.add("7186178617");
//			res.add("86786786786");
//			res.add("654868187");
//			res.add("485458888");
		} else if (key.equals(P_CK_TYPE_FACTURE)){
			res.add("Facture");
			res.add("Avoir");
		} 
		return res;
	}



	@Override
	@SuppressWarnings("unchecked")
	public void setParent(AkladematAdminEntity<? extends IAdminDematObject> parent) {
		this.parent = (AkladematAdminEntity<Chorus>) parent;
	}

	public AkladematAdminEntity<Chorus> getParent() {
		return parent;
	}
	
	public void fillChorus() {
		fillChorus(getParent().getObject());
	}
	
	public void fillChorus(Chorus chorus) {
	
		try {
			chorus.addData(Chorus.P_CPP_DATE_FACTURE, new PastellData(ObjectType.DATE, getDateEmission()));
			chorus.addData(Chorus.P_CPP_DATE_DEPOT, new PastellData(ObjectType.DATE, getDateReception()));
		} catch (Exception e) {
			e.printStackTrace();
			
			addData(Chorus.P_CPP_DATE_FACTURE, new PastellData(ObjectType.DATE, null));
			addData(Chorus.P_CPP_DATE_DEPOT, new PastellData(ObjectType.DATE, null));
		}
//			addData(P_CK_FOU_ORDRE, chorus.getFournisseur());
//			addData(P_CK_ID_DEP_EJ, chorus.getFactureNumeroEngagement());
		chorus.addData(Chorus.P_CPP_FACTURE_NUMERO_ENGAGEMENT, getStringValue(P_CK_NUM_EJ));
		chorus.addData(Chorus.P_CPP_NO_FACTURE, getNumFactureFour());
		try {
			chorus.addData(Chorus.P_MONTANT_HT, new PastellData(ObjectType.DOUBLE, getDoubleValue(P_CK_TOTAL_HT)));
		} catch (Exception e) {
			chorus.addData(Chorus.P_MONTANT_HT, new PastellData(ObjectType.DOUBLE, 0));
		}
		try {
			chorus.addData(Chorus.P_MONTANT_TVA, new PastellData(ObjectType.DOUBLE, getDoubleValue(P_CK_TOTAL_TVA)));
		} catch (Exception e) {
			chorus.addData(P_CK_TOTAL_TVA, new PastellData(ObjectType.DOUBLE, 0));
		}
		chorus.addData(Chorus.P_CPP_MONTANT_TTC, new PastellData(ObjectType.DOUBLE, getMontantTTC()));
		chorus.addData(Chorus.P_CPP_TYPE_FACTURE, getTypeFacture());
		
		
		String[] tvas = getChaineDetails().split(":");
		for(String tva : tvas){
			String label = tva.split("\\$")[0];
			Double value = Double.parseDouble(tva.split("\\$")[2]);
			chorus.addData(label, new PastellData(ObjectType.DOUBLE, value));
		}
		
		chorus.addData(Chorus.P_COMMENTAIRE, getStringValue(P_CK_COMMENTAIRE));
				
	}

	@Override
	public String toString() {
		return "Cocktail [metadata=" + metadata.toString() + "]";
	}
	
	
	
}
