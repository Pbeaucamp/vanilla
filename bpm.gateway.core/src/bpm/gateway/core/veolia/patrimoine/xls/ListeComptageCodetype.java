package bpm.gateway.core.veolia.patrimoine.xls;

import bpm.gateway.core.veolia.VEHelper;

public enum ListeComptageCodetype {

	CIS("CIS"),
	CHV("CHV"),
	CSE("CSE"),
	NR("NR");
    private final String value;

    ListeComptageCodetype(String v) {
        value = v;
    }
	
    public static String getValue(String table, String champ, ListeComptageCodetype v, boolean required) {
    	if (required && v == null) {
    		throw new IllegalArgumentException("Table " + table + " - Champs " + champ + " - Valeur 'Null' ou non permise pour la liste.");
    	}
    	return v != null ? v.value : null;
    }
    
    public static ListeComptageCodetype fromValue(String table, String champ, String v) {
    	if (v == null) {
    		System.out.println("Table " + table + " - Champs " + champ + " - Valeur 'Null' non permise.");
    		return ListeComptageCodetype.NR;
    	}
    	
        for (ListeComptageCodetype c: ListeComptageCodetype.values()) {
        	if (VEHelper.checkEnum(c.value, v)) {
        		return c;
        	}
        }
		System.out.println("Table " + table + " - Champs " + champ + " - Mauvaise valeur '" + v + "'");
		return ListeComptageCodetype.NR;
    }

}
