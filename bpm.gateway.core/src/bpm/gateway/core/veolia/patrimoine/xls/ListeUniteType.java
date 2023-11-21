package bpm.gateway.core.veolia.patrimoine.xls;

import bpm.gateway.core.veolia.VEHelper;

public enum ListeUniteType {

	ESPACES_VERTS("Espaces verts"),
	SECURITE("Sécurité"),
	GENIE_CIVIL("Génie-Civil"),
	ELECTRICITE("Electricité"),
	HUISSERIES("Huisseries"),
	HYDRAULIQUE("Hydraulique"),
	COMPTAGE("Comptage"),
	REGULATION("Régulation"),
	METROLOGIE("Métrologie"),
	TELEGESTION("Télégestion"),
	VENTILATION("Ventilation"),
	EQUIPEMENTS_PEDAGOGIQUES("Equipements pédagogiques"),
	POMPAGE_EXHAURE("Pompage / Exhaure"),
	CHLORATION_STERILISATION("Chloration / Stérilisation"),
	PROTECTION_CATHODIQUE("Protection cathodique"),
	FLOTTATION_DECANTATION("Flottation / décantation"),
	CONDITIONNEMENT_MINERALISATION("Conditionnement/Minéralisation"),
	FILTRATION("Filtration"),
	OXYDATION("Oxydation"),
	ULTRAFILTRATION_ULTRA_VIOLET("Ultrafiltration/Ultra violet"),
	RECIRCULATION("Recirculation"),
	COLLECTE_EAUX_DE_PROCESS("Collecte eaux de process"),
	TRAITEMENT_TERRES_DESHYDRATATION("Traitement terres/déshydratation"),
	SECHAGE_TERRES("Séchage terres"),
	REACTIFS("Réactifs"),
	INFORMATIQUE("Informatique"),
	AIR_DE_SERVICE("Air de service"),
	EAU_DE_SERVICE("Eau de service"),
	LABORATOIRE("Laboratoire"),
	ENERGIE_RENOUVELABLE("Energie renouvelable"),
	UTILITES("Utilités"),
	EQUIPEMENTS_DIVERS("Equipements divers"),
	STATION_DE_PRODUCTION_DELECTRICITE("Station de Production d'électricité"),
	AUTOMATISME("Automatisme"),
	VANTELLERIE("Vantellerie"),
	EAUX_USEES("Eaux usées"),
	EAUX_PLUVIALES("Eaux pluviales"),
	CHARBON("Charbon"),
	STATION_LIMNIMETRIQUE("Station Limnimétrique"),
	MANUTENTION("Manutention"),
	SERRURERIE("Serrurerie"),
	PROTECTION_HYDRAULIQUE("Protection hydraulique"),
	NR("NR");
    private final String value;

    ListeUniteType(String v) {
        value = v;
    }
	
    public static String getValue(String table, String champ, ListeUniteType v, boolean required) {
    	if (required && v == null) {
    		throw new IllegalArgumentException("Table " + table + " - Champs " + champ + " - Valeur 'Null' ou non permise pour la liste.");
    	}
    	return v != null ? v.value : null;
    }
    
    public static ListeUniteType fromValue(String table, String champ, String v) {
    	if (v == null) {
    		System.out.println("Table " + table + " - Champs " + champ + " - Valeur 'Null' non permise.");
    		return ListeUniteType.NR;
    	}
    	
        for (ListeUniteType c: ListeUniteType.values()) {
        	if (VEHelper.checkEnum(c.value, v)) {
        		return c;
        	}
        }
		System.out.println("Table " + table + " - Champs " + champ + " - Mauvaise valeur '" + v + "'");
		return ListeUniteType.NR;
    }

}
