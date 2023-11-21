package bpm.gateway.core.veolia.patrimoine.xls;

import bpm.gateway.core.veolia.VEHelper;

public enum ListeUniteType {

	ESPACES_VERTS("Espaces verts"),
	SECURITE("S�curit�"),
	GENIE_CIVIL("G�nie-Civil"),
	ELECTRICITE("Electricit�"),
	HUISSERIES("Huisseries"),
	HYDRAULIQUE("Hydraulique"),
	COMPTAGE("Comptage"),
	REGULATION("R�gulation"),
	METROLOGIE("M�trologie"),
	TELEGESTION("T�l�gestion"),
	VENTILATION("Ventilation"),
	EQUIPEMENTS_PEDAGOGIQUES("Equipements p�dagogiques"),
	POMPAGE_EXHAURE("Pompage / Exhaure"),
	CHLORATION_STERILISATION("Chloration / St�rilisation"),
	PROTECTION_CATHODIQUE("Protection cathodique"),
	FLOTTATION_DECANTATION("Flottation / d�cantation"),
	CONDITIONNEMENT_MINERALISATION("Conditionnement/Min�ralisation"),
	FILTRATION("Filtration"),
	OXYDATION("Oxydation"),
	ULTRAFILTRATION_ULTRA_VIOLET("Ultrafiltration/Ultra violet"),
	RECIRCULATION("Recirculation"),
	COLLECTE_EAUX_DE_PROCESS("Collecte eaux de process"),
	TRAITEMENT_TERRES_DESHYDRATATION("Traitement terres/d�shydratation"),
	SECHAGE_TERRES("S�chage terres"),
	REACTIFS("R�actifs"),
	INFORMATIQUE("Informatique"),
	AIR_DE_SERVICE("Air de service"),
	EAU_DE_SERVICE("Eau de service"),
	LABORATOIRE("Laboratoire"),
	ENERGIE_RENOUVELABLE("Energie renouvelable"),
	UTILITES("Utilit�s"),
	EQUIPEMENTS_DIVERS("Equipements divers"),
	STATION_DE_PRODUCTION_DELECTRICITE("Station de Production d'�lectricit�"),
	AUTOMATISME("Automatisme"),
	VANTELLERIE("Vantellerie"),
	EAUX_USEES("Eaux us�es"),
	EAUX_PLUVIALES("Eaux pluviales"),
	CHARBON("Charbon"),
	STATION_LIMNIMETRIQUE("Station Limnim�trique"),
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
