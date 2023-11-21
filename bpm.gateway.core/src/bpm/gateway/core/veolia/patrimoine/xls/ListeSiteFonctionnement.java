package bpm.gateway.core.veolia.patrimoine.xls;

import bpm.gateway.core.veolia.VEHelper;

public enum ListeSiteFonctionnement {

	ANNUEL("Annuel"),
	SAISONNIER("Saisonnier"),
	SECOURS("Secours"),
	NON_UTILISE("Non utilisé"),
	HORS_SERVICE("Hors service"),
	NR("NR");
    private final String value;

    ListeSiteFonctionnement(String v) {
        value = v;
    }
	
    public static String getValue(String table, String champ, ListeSiteFonctionnement v, boolean required) {
    	if (required && v == null) {
    		throw new IllegalArgumentException("Table " + table + " - Champs " + champ + " - Valeur 'Null' ou non permise pour la liste.");
    	}
    	return v != null ? v.value : null;
    }
    
    public static ListeSiteFonctionnement fromValue(String table, String champ, String v) {
    	if (v == null) {
    		System.out.println("Table " + table + " - Champs " + champ + " - Valeur 'Null' non permise.");
    		return ListeSiteFonctionnement.NR;
    	}
    	
        for (ListeSiteFonctionnement c: ListeSiteFonctionnement.values()) {
        	if (VEHelper.checkEnum(c.value, v)) {
        		return c;
        	}
        }
		System.out.println("Table " + table + " - Champs " + champ + " - Mauvaise valeur '" + v + "'");
		return ListeSiteFonctionnement.NR;
    }

}
