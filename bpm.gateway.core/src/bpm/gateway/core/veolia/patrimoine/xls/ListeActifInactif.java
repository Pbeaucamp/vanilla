package bpm.gateway.core.veolia.patrimoine.xls;

import bpm.gateway.core.veolia.VEHelper;

public enum ListeActifInactif {

	ACTIF("Actif"),
	INACTIF("Inactif"),
	NR("NR");
    private final String value;

    ListeActifInactif(String v) {
        value = v;
    }
	
    public static String getValue(String table, String champ, ListeActifInactif v, boolean required) {
    	if (required && v == null) {
    		throw new IllegalArgumentException("Table " + table + " - Champs " + champ + " - Valeur 'Null' ou non permise pour la liste.");
    	}
    	return v != null ? v.value : null;
    }
    
    public static ListeActifInactif fromValue(String table, String champ, String v) {
    	if (v == null) {
    		System.out.println("Table " + table + " - Champs " + champ + " - Valeur 'Null' non permise.");
    		return ListeActifInactif.NR;
    	}
    	
        for (ListeActifInactif c: ListeActifInactif.values()) {
        	if (VEHelper.checkEnum(c.value, v)) {
        		return c;
        	}
        }
		System.out.println("Table " + table + " - Champs " + champ + " - Mauvaise valeur '" + v + "'");
		return ListeActifInactif.NR;
    }

}
