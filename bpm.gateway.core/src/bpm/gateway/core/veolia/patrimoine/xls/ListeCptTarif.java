package bpm.gateway.core.veolia.patrimoine.xls;

import bpm.gateway.core.veolia.VEHelper;

public enum ListeCptTarif {

	VERT("Vert"),
	JAUNE("Jaune"),
	BLEU("Bleu"),
	NR("NR");
    private final String value;

    ListeCptTarif(String v) {
        value = v;
    }
	
    public static String getValue(String table, String champ, ListeCptTarif v, boolean required) {
    	if (required && v == null) {
    		throw new IllegalArgumentException("Table " + table + " - Champs " + champ + " - Valeur 'Null' ou non permise pour la liste.");
    	}
    	return v != null ? v.value : null;
    }
    
    public static ListeCptTarif fromValue(String table, String champ, String v) {
    	if (v == null) {
    		System.out.println("Table " + table + " - Champs " + champ + " - Valeur 'Null' non permise.");
    		return ListeCptTarif.NR;
    	}
    	
        for (ListeCptTarif c: ListeCptTarif.values()) {
        	if (VEHelper.checkEnum(c.value, v)) {
        		return c;
        	}
        }
		System.out.println("Table " + table + " - Champs " + champ + " - Mauvaise valeur '" + v + "'");
		return ListeCptTarif.NR;
    }

}
