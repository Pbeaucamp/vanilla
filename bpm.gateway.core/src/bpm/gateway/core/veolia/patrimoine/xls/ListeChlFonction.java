package bpm.gateway.core.veolia.patrimoine.xls;

import bpm.gateway.core.veolia.VEHelper;

public enum ListeChlFonction {

	CHLORATION("Chloration"),
	RECHLORATION("Rechloration"),
	BREAKPOINT("Breakpoint"),
	NR("NR");
    private final String value;

    ListeChlFonction(String v) {
        value = v;
    }
	
    public static String getValue(String table, String champ, ListeChlFonction v, boolean required) {
    	if (required && v == null) {
    		throw new IllegalArgumentException("Table " + table + " - Champs " + champ + " - Valeur 'Null' ou non permise pour la liste.");
    	}
    	return v != null ? v.value : null;
    }
    
    public static ListeChlFonction fromValue(String table, String champ, String v) {
    	if (v == null) {
    		System.out.println("Table " + table + " - Champs " + champ + " - Valeur 'Null' non permise.");
    		return ListeChlFonction.NR;
    	}
    	
        for (ListeChlFonction c: ListeChlFonction.values()) {
        	if (VEHelper.checkEnum(c.value, v)) {
        		return c;
        	}
        }
		System.out.println("Table " + table + " - Champs " + champ + " - Mauvaise valeur '" + v + "'");
		return ListeChlFonction.NR;
    }

}
