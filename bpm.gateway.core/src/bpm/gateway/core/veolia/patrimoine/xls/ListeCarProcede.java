package bpm.gateway.core.veolia.patrimoine.xls;

import bpm.gateway.core.veolia.VEHelper;

public enum ListeCarProcede {

	FILTRATION_SUR_SABLE("Filtration sur sable"),
	LIT_FLUIDISE("Lit fluidisé"),
	NR("NR");
    private final String value;

    ListeCarProcede(String v) {
        value = v;
    }
	
    public static String getValue(String table, String champ, ListeCarProcede v, boolean required) {
    	if (required && v == null) {
    		throw new IllegalArgumentException("Table " + table + " - Champs " + champ + " - Valeur 'Null' ou non permise pour la liste.");
    	}
    	return v != null ? v.value : null;
    }
    
    public static ListeCarProcede fromValue(String table, String champ, String v) {
    	if (v == null) {
    		System.out.println("Table " + table + " - Champs " + champ + " - Valeur 'Null' non permise.");
    		return ListeCarProcede.NR;
    	}
    	
        for (ListeCarProcede c: ListeCarProcede.values()) {
        	if (VEHelper.checkEnum(c.value, v)) {
        		return c;
        	}
        }
		System.out.println("Table " + table + " - Champs " + champ + " - Mauvaise valeur '" + v + "'");
		return ListeCarProcede.NR;
    }

}
