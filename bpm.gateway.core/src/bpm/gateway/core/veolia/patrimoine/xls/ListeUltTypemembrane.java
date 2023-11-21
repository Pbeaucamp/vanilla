package bpm.gateway.core.veolia.patrimoine.xls;

import bpm.gateway.core.veolia.VEHelper;

public enum ListeUltTypemembrane {

	POLYETHERSULFONE("Polyethersulfone"),
	ACETATE_DE_CELLULOSE("Acétate de cellulose"),
	NR("NR");
    private final String value;

    ListeUltTypemembrane(String v) {
        value = v;
    }
	
    public static String getValue(String table, String champ, ListeUltTypemembrane v, boolean required) {
    	if (required && v == null) {
    		throw new IllegalArgumentException("Table " + table + " - Champs " + champ + " - Valeur 'Null' ou non permise pour la liste.");
    	}
    	return v != null ? v.value : null;
    }
    
    public static ListeUltTypemembrane fromValue(String table, String champ, String v) {
    	if (v == null) {
    		System.out.println("Table " + table + " - Champs " + champ + " - Valeur 'Null' non permise.");
    		return ListeUltTypemembrane.NR;
    	}
    	
        for (ListeUltTypemembrane c: ListeUltTypemembrane.values()) {
        	if (VEHelper.checkEnum(c.value, v)) {
        		return c;
        	}
        }
		System.out.println("Table " + table + " - Champs " + champ + " - Mauvaise valeur '" + v + "'");
		return ListeUltTypemembrane.NR;
    }

}
