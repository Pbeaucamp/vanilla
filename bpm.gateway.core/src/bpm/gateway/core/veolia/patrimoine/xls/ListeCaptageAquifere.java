package bpm.gateway.core.veolia.patrimoine.xls;

import bpm.gateway.core.veolia.VEHelper;

public enum ListeCaptageAquifere {

	CALCAIRES_GRESEAUX_DOLOMITIQUES_DU_LIAS_INFERIEUR("Calcaires gr�seaux dolomitiques du Lias Inf�rieur"),
	SOCLE_GRANITIQUE_DE_TYPE_FISSURE("Socle granitique de type fissur�"),
	ARGILE_SABLEUX_ET_ARENE_GRANITIQUE_SOUSJACENTE("Argile sableux et ar�ne granitique sous-jacente"),
	ALTERNANCE_DE_GRANITES_ET_CORNEENNES_FRACTUREES("Alternance de granites et corn�ennes fractur�es"),
	CALCAIRES_GRESEAUX_DAGE_EOCENE("Calcaires gr�seaux d'�ge �oc�ne"),
	ALTERITE_SCHISTEUSE_SOUSJACENT("Alt�rite schisteuse sous-jacent"),
	ALTERITE_SCHISTEUSE_SOUSJACENT_ET_SCHISTE_FISSURE("alt�rite schisteuse sous-jacent et schiste fissur�"),
	FORMATIONS_ISSUES_DE_LALTERATION_DES_RHYOLITES_ET_IGNIMBRITES("Formations issues de l'alt�ration des rhyolites et ignimbrites"),
	SCHISTE_ALTERE_ET_FISSURE("Schiste alt�r� et fissur�"),
	CALCAIRES_FISSURES_DU_LIAS_INFERIEUR("Calcaires fissur�s du Lias inf�rieur"),
	SCHISTE_ALTERE_ET_FISSURE_SOUS_JACENT_AU_CALCAIRE("Schiste alt�r� et fissur� sous jacent au calcaire"),
	CALCAIRES_GRESEAUX_DU_LIAS_INFERIEUR("Calcaires gr�seaux du Lias Inf�rieur"),
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
