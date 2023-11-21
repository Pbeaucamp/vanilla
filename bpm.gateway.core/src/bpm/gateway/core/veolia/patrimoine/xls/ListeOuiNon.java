package bpm.gateway.core.veolia.patrimoine.xls;

import bpm.gateway.core.veolia.VEHelper;

public enum ListeOuiNon {

    OUI("Oui"),
    NON("Non"),
    NR("NR");
    private final String value;

    ListeOuiNon(String v) {
        value = v;
    }
	
    public static String getValue(String table, String champ, ListeOuiNon v, boolean required) {
    	if (required && v == null) {
    		throw new IllegalArgumentException("Table " + table + " - Champs " + champ + " - Valeur 'Null' ou non permise pour la liste.");
    	}
    	return v != null ? v.value : null;
    }
    
    public static ListeOuiNon fromValue(String table, String champ, String v) {
    	if (v == null) {
    		System.out.println("Table " + table + " - Champs " + champ + " - Valeur 'Null' non permise.");
    		return ListeOuiNon.NR;
    	}
    	
        for (ListeOuiNon c: ListeOuiNon.values()) {
        	if (VEHelper.checkEnum(c.value, v)) {
        		return c;
        	}
        }
		System.out.println("Table " + table + " - Champs " + champ + " - Mauvaise valeur '" + v + "'");
		return ListeOuiNon.NR;
    }

}
