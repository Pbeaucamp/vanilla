package bpm.gateway.core.veolia.patrimoine.xls;

import bpm.gateway.core.veolia.VEHelper;

public enum ListeLogementFonction {

	LOGEMENT_DE_FONCTION("Logement de fonction"),
	HABITATION("Habitation"),
	MOULIN("Moulin"),
	GRANGE("Grange"),
	NR("NR");
    private final String value;

    ListeLogementFonction(String v) {
        value = v;
    }
	
    public static String getValue(String table, String champ, ListeLogementFonction v, boolean required) {
    	if (required && v == null) {
    		throw new IllegalArgumentException("Table " + table + " - Champs " + champ + " - Valeur 'Null' ou non permise pour la liste.");
    	}
    	return v != null ? v.value : null;
    }
    
    public static ListeLogementFonction fromValue(String table, String champ, String v) {
    	if (v == null) {
    		System.out.println("Table " + table + " - Champs " + champ + " - Valeur 'Null' non permise.");
    		return ListeLogementFonction.NR;
    	}
    	
        for (ListeLogementFonction c: ListeLogementFonction.values()) {
        	if (VEHelper.checkEnum(c.value, v)) {
        		return c;
        	}
        }
		System.out.println("Table " + table + " - Champs " + champ + " - Mauvaise valeur '" + v + "'");
		return ListeLogementFonction.NR;
    }

}
