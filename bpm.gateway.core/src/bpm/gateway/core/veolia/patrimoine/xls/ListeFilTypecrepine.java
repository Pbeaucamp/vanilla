package bpm.gateway.core.veolia.patrimoine.xls;

import bpm.gateway.core.veolia.VEHelper;

public enum ListeFilTypecrepine {

	TUBES_CREPINES("Tubes crépinés"),
	BUSELURES("Buselures"),
	NR("NR");
    private final String value;

    ListeFilTypecrepine(String v) {
        value = v;
    }
	
    public static String getValue(String table, String champ, ListeFilTypecrepine v, boolean required) {
    	if (required && v == null) {
    		throw new IllegalArgumentException("Table " + table + " - Champs " + champ + " - Valeur 'Null' ou non permise pour la liste.");
    	}
    	return v != null ? v.value : null;
    }
    
    public static ListeFilTypecrepine fromValue(String table, String champ, String v) {
    	if (v == null) {
    		System.out.println("Table " + table + " - Champs " + champ + " - Valeur 'Null' non permise.");
    		return ListeFilTypecrepine.NR;
    	}
    	
        for (ListeFilTypecrepine c: ListeFilTypecrepine.values()) {
        	if (VEHelper.checkEnum(c.value, v)) {
        		return c;
        	}
        }
		System.out.println("Table " + table + " - Champs " + champ + " - Mauvaise valeur '" + v + "'");
		return ListeFilTypecrepine.NR;
    }

}
