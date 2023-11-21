package bpm.gateway.core.veolia.patrimoine.xls;

import bpm.gateway.core.veolia.VEHelper;

public enum ListePomPeriodefonctionnement {

	ANNUEL("Annuel"),
	HIVERNALE("Hivernale"),
	ESTIVALE("Estivale"),
	NR("NR");
    private final String value;

    ListePomPeriodefonctionnement(String v) {
        value = v;
    }
	
    public static String getValue(String table, String champ, ListePomPeriodefonctionnement v, boolean required) {
    	if (required && v == null) {
    		throw new IllegalArgumentException("Table " + table + " - Champs " + champ + " - Valeur 'Null' ou non permise pour la liste.");
    	}
    	return v != null ? v.value : null;
    }
    
    public static ListePomPeriodefonctionnement fromValue(String table, String champ, String v) {
    	if (v == null) {
    		System.out.println("Table " + table + " - Champs " + champ + " - Valeur 'Null' non permise.");
    		return ListePomPeriodefonctionnement.NR;
    	}
    	
        for (ListePomPeriodefonctionnement c: ListePomPeriodefonctionnement.values()) {
        	if (VEHelper.checkEnum(c.value, v)) {
        		return c;
        	}
        }
		System.out.println("Table " + table + " - Champs " + champ + " - Mauvaise valeur '" + v + "'");
		return ListePomPeriodefonctionnement.NR;
    }

}
