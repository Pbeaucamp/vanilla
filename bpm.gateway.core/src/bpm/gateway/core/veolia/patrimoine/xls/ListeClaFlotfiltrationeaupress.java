package bpm.gateway.core.veolia.patrimoine.xls;

import bpm.gateway.core.veolia.VEHelper;

public enum ListeClaFlotfiltrationeaupress {

	NETTOYAGE_AUTO("Nettoyage auto"),
	MANUEL("Manuel"),
	NR("NR");
    private final String value;

    ListeClaFlotfiltrationeaupress(String v) {
        value = v;
    }
	
    public static String getValue(String table, String champ, ListeClaFlotfiltrationeaupress v, boolean required) {
    	if (required && v == null) {
    		throw new IllegalArgumentException("Table " + table + " - Champs " + champ + " - Valeur 'Null' ou non permise pour la liste.");
    	}
    	return v != null ? v.value : null;
    }
    
    public static ListeClaFlotfiltrationeaupress fromValue(String table, String champ, String v) {
    	if (v == null) {
    		System.out.println("Table " + table + " - Champs " + champ + " - Valeur 'Null' non permise.");
    		return ListeClaFlotfiltrationeaupress.NR;
    	}
    	
        for (ListeClaFlotfiltrationeaupress c: ListeClaFlotfiltrationeaupress.values()) {
        	if (VEHelper.checkEnum(c.value, v)) {
        		return c;
        	}
        }
		System.out.println("Table " + table + " - Champs " + champ + " - Mauvaise valeur '" + v + "'");
		return ListeClaFlotfiltrationeaupress.NR;
    }

}
