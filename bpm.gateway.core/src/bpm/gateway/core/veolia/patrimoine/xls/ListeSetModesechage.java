package bpm.gateway.core.veolia.patrimoine.xls;

import bpm.gateway.core.veolia.VEHelper;

public enum ListeSetModesechage {

	VENTILATION("Ventilation"),
	RETOURNEMENT("Retournement"),
	NR("NR");
    private final String value;

    ListeSetModesechage(String v) {
        value = v;
    }
	
    public static String getValue(String table, String champ, ListeSetModesechage v, boolean required) {
    	if (required && v == null) {
    		throw new IllegalArgumentException("Table " + table + " - Champs " + champ + " - Valeur 'Null' ou non permise pour la liste.");
    	}
    	return v != null ? v.value : null;
    }
    
    public static ListeSetModesechage fromValue(String table, String champ, String v) {
    	if (v == null) {
    		System.out.println("Table " + table + " - Champs " + champ + " - Valeur 'Null' non permise.");
    		return ListeSetModesechage.NR;
    	}
    	
        for (ListeSetModesechage c: ListeSetModesechage.values()) {
        	if (VEHelper.checkEnum(c.value, v)) {
        		return c;
        	}
        }
		System.out.println("Table " + table + " - Champs " + champ + " - Mauvaise valeur '" + v + "'");
		return ListeSetModesechage.NR;
    }

}
