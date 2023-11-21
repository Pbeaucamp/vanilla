package bpm.gateway.core.veolia.patrimoine.xls;

import bpm.gateway.core.veolia.VEHelper;

public enum ListeCaptageAquifere {

	CALCAIRES_GRESEAUX_DOLOMITIQUES_DU_LIAS_INFERIEUR("Calcaires gréseaux dolomitiques du Lias Inférieur"),
	SOCLE_GRANITIQUE_DE_TYPE_FISSURE("Socle granitique de type fissuré"),
	ARGILE_SABLEUX_ET_ARENE_GRANITIQUE_SOUSJACENTE("Argile sableux et arène granitique sous-jacente"),
	ALTERNANCE_DE_GRANITES_ET_CORNEENNES_FRACTUREES("Alternance de granites et cornéennes fracturées"),
	CALCAIRES_GRESEAUX_DAGE_EOCENE("Calcaires gréseaux d'âge éocène"),
	ALTERITE_SCHISTEUSE_SOUSJACENT("Altérite schisteuse sous-jacent"),
	ALTERITE_SCHISTEUSE_SOUSJACENT_ET_SCHISTE_FISSURE("altérite schisteuse sous-jacent et schiste fissuré"),
	FORMATIONS_ISSUES_DE_LALTERATION_DES_RHYOLITES_ET_IGNIMBRITES("Formations issues de l'altération des rhyolites et ignimbrites"),
	SCHISTE_ALTERE_ET_FISSURE("Schiste altéré et fissuré"),
	CALCAIRES_FISSURES_DU_LIAS_INFERIEUR("Calcaires fissurés du Lias inférieur"),
	SCHISTE_ALTERE_ET_FISSURE_SOUS_JACENT_AU_CALCAIRE("Schiste altéré et fissuré sous jacent au calcaire"),
	CALCAIRES_GRESEAUX_DU_LIAS_INFERIEUR("Calcaires gréseaux du Lias Inférieur"),
	NR("NR");
    private final String value;

    ListeCaptageAquifere(String v) {
        value = v;
    }
	
    public static String getValue(String table, String champ, ListeCaptageAquifere v, boolean required) {
    	if (required && v == null) {
    		throw new IllegalArgumentException("Table " + table + " - Champs " + champ + " - Valeur 'Null' ou non permise pour la liste.");
    	}
    	return v != null ? v.value : null;
    }
    
    public static ListeCaptageAquifere fromValue(String table, String champ, String v) {
    	if (v == null) {
    		System.out.println("Table " + table + " - Champs " + champ + " - Valeur 'Null' non permise.");
    		return ListeCaptageAquifere.NR;
    	}
    	
        for (ListeCaptageAquifere c: ListeCaptageAquifere.values()) {
        	if (VEHelper.checkEnum(c.value, v)) {
        		return c;
        	}
        }
		System.out.println("Table " + table + " - Champs " + champ + " - Mauvaise valeur '" + v + "'");
		return ListeCaptageAquifere.NR;
    }

}
