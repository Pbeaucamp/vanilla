package bpm.gateway.core.veolia.patrimoine.xls;

import bpm.gateway.core.veolia.VEHelper;

public enum ListeUniteNature {

	PRODUCTION("Production"),
	DISTRIBUTION("Distribution"),
	NON_FONCTIONNEL("Non fonctionnel"),
	NR("NR");
    private final String value;

    ListeUniteNature(String v) {
        value = v;
    }
	
    public static String getValue(String table, String champ, ListeUniteNature v, boolean required) {
    	if (required && v == null) {
    		throw new IllegalArgumentException("Table " + table + " - Champs " + champ + " - Valeur 'Null' ou non permise pour la liste.");
    	}
    	return v != null ? v.value : null;
    }
    
    public static ListeUniteNature fromValue(String table, String champ, String v) {
    	if (v == null) {
    		System.out.println("Table " + table + " - Champs " + champ + " - Valeur 'Null' non permise.");
    		return ListeUniteNature.NR;
    	}
    	
        for (ListeUniteNature c: ListeUniteNature.values()) {
        	if (VEHelper.checkEnum(c.value, v)) {
        		return c;
        	}
        }
		System.out.println("Table " + table + " - Champs " + champ + " - Mauvaise valeur '" + v + "'");
		return ListeUniteNature.NR;
    }

}
