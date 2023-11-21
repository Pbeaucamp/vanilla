package bpm.gateway.core.veolia.patrimoine.xls;

import bpm.gateway.core.veolia.VEHelper;

public enum ListeBachesolPosition {

	ENTERRE("Enterré"),
	SEMI_ENTERRE("Semi-Enterré"),
	SUR_LE_SOL("Sur le sol"),
	NR("NR");
    private final String value;

    ListeBachesolPosition(String v) {
        value = v;
    }
	
    public static String getValue(String table, String champ, ListeBachesolPosition v, boolean required) {
    	if (required && v == null) {
    		throw new IllegalArgumentException("Table " + table + " - Champs " + champ + " - Valeur 'Null' ou non permise pour la liste.");
    	}
    	return v != null ? v.value : null;
    }
    
    public static ListeBachesolPosition fromValue(String table, String champ, String v) {
    	if (v == null) {
    		System.out.println("Table " + table + " - Champs " + champ + " - Valeur 'Null' non permise.");
    		return ListeBachesolPosition.NR;
    	}
    	
        for (ListeBachesolPosition c: ListeBachesolPosition.values()) {
        	if (VEHelper.checkEnum(c.value, v)) {
        		return c;
        	}
        }
		System.out.println("Table " + table + " - Champs " + champ + " - Mauvaise valeur '" + v + "'");
		return ListeBachesolPosition.NR;
    }

}
