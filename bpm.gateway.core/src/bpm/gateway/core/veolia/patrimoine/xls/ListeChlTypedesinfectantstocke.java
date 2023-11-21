package bpm.gateway.core.veolia.patrimoine.xls;

import bpm.gateway.core.veolia.VEHelper;

public enum ListeChlTypedesinfectantstocke {

	LIQUIDE("Liquide"),
	GAZEUX("Gazeux"),
	NR("NR");
;
    private final String value;

    ListeChlTypedesinfectantstocke(String v) {
        value = v;
    }
	
    public static String getValue(String table, String champ, ListeChlTypedesinfectantstocke v, boolean required) {
    	if (required && v == null) {
    		throw new IllegalArgumentException("Table " + table + " - Champs " + champ + " - Valeur 'Null' ou non permise pour la liste.");
    	}
    	return v != null ? v.value : null;
    }
    
    public static ListeChlTypedesinfectantstocke fromValue(String table, String champ, String v) {
    	if (v == null) {
    		System.out.println("Table " + table + " - Champs " + champ + " - Valeur 'Null' non permise.");
    		return ListeChlTypedesinfectantstocke.NR;
    	}
    	
        for (ListeChlTypedesinfectantstocke c: ListeChlTypedesinfectantstocke.values()) {
        	if (VEHelper.checkEnum(c.value, v)) {
        		return c;
        	}
        }
		System.out.println("Table " + table + " - Champs " + champ + " - Mauvaise valeur '" + v + "'");
		return ListeChlTypedesinfectantstocke.NR;
    }

}
