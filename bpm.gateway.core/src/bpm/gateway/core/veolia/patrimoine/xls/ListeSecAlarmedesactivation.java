package bpm.gateway.core.veolia.patrimoine.xls;

import bpm.gateway.core.veolia.VEHelper;

public enum ListeSecAlarmedesactivation {

	CLE("clé"),
	CLAVIER("clavier"),
	NR("NR");
    private final String value;

    ListeSecAlarmedesactivation(String v) {
        value = v;
    }
	
    public static String getValue(String table, String champ, ListeSecAlarmedesactivation v, boolean required) {
    	if (required && v == null) {
    		throw new IllegalArgumentException("Table " + table + " - Champs " + champ + " - Valeur 'Null' ou non permise pour la liste.");
    	}
    	return v != null ? v.value : null;
    }
    
    public static ListeSecAlarmedesactivation fromValue(String table, String champ, String v) {
    	if (v == null) {
    		System.out.println("Table " + table + " - Champs " + champ + " - Valeur 'Null' non permise.");
    		return ListeSecAlarmedesactivation.NR;
    	}
    	
        for (ListeSecAlarmedesactivation c: ListeSecAlarmedesactivation.values()) {
        	if (VEHelper.checkEnum(c.value, v)) {
        		return c;
        	}
        }
		System.out.println("Table " + table + " - Champs " + champ + " - Mauvaise valeur '" + v + "'");
		return ListeSecAlarmedesactivation.NR;
    }

}
