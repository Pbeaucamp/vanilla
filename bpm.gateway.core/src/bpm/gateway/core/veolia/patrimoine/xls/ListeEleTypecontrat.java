package bpm.gateway.core.veolia.patrimoine.xls;

import bpm.gateway.core.veolia.VEHelper;

public enum ListeEleTypecontrat {

	BLEU("Bleu"),
	JAUNE("Jaune"),
	VERT("Vert"),
	NR("NR");
    private final String value;

    ListeEleTypecontrat(String v) {
        value = v;
    }
	
    public static String getValue(String table, String champ, ListeEleTypecontrat v, boolean required) {
    	if (required && v == null) {
    		throw new IllegalArgumentException("Table " + table + " - Champs " + champ + " - Valeur 'Null' ou non permise pour la liste.");
    	}
    	return v != null ? v.value : null;
    }
    
    public static ListeEleTypecontrat fromValue(String table, String champ, String v) {
    	if (v == null) {
    		System.out.println("Table " + table + " - Champs " + champ + " - Valeur 'Null' non permise.");
    		return ListeEleTypecontrat.NR;
    	}
    	
        for (ListeEleTypecontrat c: ListeEleTypecontrat.values()) {
        	if (VEHelper.checkEnum(c.value, v)) {
        		return c;
        	}
        }
		System.out.println("Table " + table + " - Champs " + champ + " - Mauvaise valeur '" + v + "'");
		return ListeEleTypecontrat.NR;
    }

}
