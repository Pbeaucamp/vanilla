package bpm.gateway.core.veolia.patrimoine.xls;

import bpm.gateway.core.veolia.VEHelper;

public enum ListeOuvrageEtat {

	FONCTIONNEL("Fonctionnel"),
	DEGRADE("Dégradé"),
	A_REHABILITER("A réhabiliter"),
	NR("NR");
    private final String value;

    ListeOuvrageEtat(String v) {
        value = v;
    }
	
    public static String getValue(String table, String champ, ListeOuvrageEtat v, boolean required) {
    	if (required && v == null) {
    		throw new IllegalArgumentException("Table " + table + " - Champs " + champ + " - Valeur 'Null' ou non permise pour la liste.");
    	}
    	return v != null ? v.value : null;
    }
    
    public static ListeOuvrageEtat fromValue(String table, String champ, String v) {
    	if (v == null) {
    		System.out.println("Table " + table + " - Champs " + champ + " - Valeur 'Null' non permise.");
    		return ListeOuvrageEtat.NR;
    	}
    	
        for (ListeOuvrageEtat c: ListeOuvrageEtat.values()) {
        	if (VEHelper.checkEnum(c.value, v)) {
        		return c;
        	}
        }
		System.out.println("Table " + table + " - Champs " + champ + " - Mauvaise valeur '" + v + "'");
		return ListeOuvrageEtat.NR;
    }

}
