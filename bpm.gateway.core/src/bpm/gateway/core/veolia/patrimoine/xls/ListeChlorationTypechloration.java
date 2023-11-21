package bpm.gateway.core.veolia.patrimoine.xls;

import bpm.gateway.core.veolia.VEHelper;

public enum ListeChlorationTypechloration {

	CHLORE_AZEUX("Chlore gazeux"),
	JAVEL("Javel"),
	NR("NR");
    private final String value;

    ListeChlorationTypechloration(String v) {
        value = v;
    }
	
    public static String getValue(String table, String champ, ListeChlorationTypechloration v, boolean required) {
    	if (required && v == null) {
    		throw new IllegalArgumentException("Table " + table + " - Champs " + champ + " - Valeur 'Null' ou non permise pour la liste.");
    	}
    	return v != null ? v.value : null;
    }
    
    public static ListeChlorationTypechloration fromValue(String table, String champ, String v) {
    	if (v == null) {
    		System.out.println("Table " + table + " - Champs " + champ + " - Valeur 'Null' non permise.");
    		return ListeChlorationTypechloration.NR;
    	}
    	
        for (ListeChlorationTypechloration c: ListeChlorationTypechloration.values()) {
        	if (VEHelper.checkEnum(c.value, v)) {
        		return c;
        	}
        }
		System.out.println("Table " + table + " - Champs " + champ + " - Mauvaise valeur '" + v + "'");
		return ListeChlorationTypechloration.NR;
    }

}
