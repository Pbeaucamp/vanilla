package bpm.gateway.core.veolia.patrimoine.xls;

import bpm.gateway.core.veolia.VEHelper;

public enum ListeCarTypeextraction {

	VANNE_EXTRACTION("Vanne extraction"),
	ASPIRATEUR("Aspirateur"),
	HYDROCYCLONE("Hydrocyclone"),
	NR("NR");
    private final String value;

    ListeCarTypeextraction(String v) {
        value = v;
    }
	
    public static String getValue(String table, String champ, ListeCarTypeextraction v, boolean required) {
    	if (required && v == null) {
    		throw new IllegalArgumentException("Table " + table + " - Champs " + champ + " - Valeur 'Null' ou non permise pour la liste.");
    	}
    	return v != null ? v.value : null;
    }
    
    public static ListeCarTypeextraction fromValue(String table, String champ, String v) {
    	if (v == null) {
    		System.out.println("Table " + table + " - Champs " + champ + " - Valeur 'Null' non permise.");
    		return ListeCarTypeextraction.NR;
    	}
    	
        for (ListeCarTypeextraction c: ListeCarTypeextraction.values()) {
        	if (VEHelper.checkEnum(c.value, v)) {
        		return c;
        	}
        }
		System.out.println("Table " + table + " - Champs " + champ + " - Mauvaise valeur '" + v + "'");
		return ListeCarTypeextraction.NR;
    }

}
