package bpm.gateway.core.veolia.patrimoine.xls;

import bpm.gateway.core.veolia.VEHelper;

public enum ListeTlgModealimentation {

	EDF("EDF"),
	AUTONOME("Autonome"),
	NR("NR");
    private final String value;

    ListeTlgModealimentation(String v) {
        value = v;
    }
	
    public static String getValue(String table, String champ, ListeTlgModealimentation v, boolean required) {
    	if (required && v == null) {
    		throw new IllegalArgumentException("Table " + table + " - Champs " + champ + " - Valeur 'Null' ou non permise pour la liste.");
    	}
    	return v != null ? v.value : null;
    }
    
    public static ListeTlgModealimentation fromValue(String table, String champ, String v) {
    	if (v == null) {
    		System.out.println("Table " + table + " - Champs " + champ + " - Valeur 'Null' non permise.");
    		return ListeTlgModealimentation.NR;
    	}
    	
        for (ListeTlgModealimentation c: ListeTlgModealimentation.values()) {
        	if (VEHelper.checkEnum(c.value, v)) {
        		return c;
        	}
        }
		System.out.println("Table " + table + " - Champs " + champ + " - Mauvaise valeur '" + v + "'");
		return ListeTlgModealimentation.NR;
    }

}
