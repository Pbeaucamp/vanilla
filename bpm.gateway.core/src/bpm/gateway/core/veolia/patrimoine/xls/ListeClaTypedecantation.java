package bpm.gateway.core.veolia.patrimoine.xls;

import bpm.gateway.core.veolia.VEHelper;

public enum ListeClaTypedecantation {

	LAMELLAIRE("Lamellaire"),
	LIT_FLUIDISE("Lit fluidisé"),
	A_FLOCS_LESTES("A flocs lestés"),
	NR("NR");
    private final String value;

    ListeClaTypedecantation(String v) {
        value = v;
    }
	
    public static String getValue(String table, String champ, ListeClaTypedecantation v, boolean required) {
    	if (required && v == null) {
    		throw new IllegalArgumentException("Table " + table + " - Champs " + champ + " - Valeur 'Null' ou non permise pour la liste.");
    	}
    	return v != null ? v.value : null;
    }
    
    public static ListeClaTypedecantation fromValue(String table, String champ, String v) {
    	if (v == null) {
    		System.out.println("Table " + table + " - Champs " + champ + " - Valeur 'Null' non permise.");
    		return ListeClaTypedecantation.NR;
    	}
    	
        for (ListeClaTypedecantation c: ListeClaTypedecantation.values()) {
        	if (VEHelper.checkEnum(c.value, v)) {
        		return c;
        	}
        }
		System.out.println("Table " + table + " - Champs " + champ + " - Mauvaise valeur '" + v + "'");
		return ListeClaTypedecantation.NR;
    }

}
