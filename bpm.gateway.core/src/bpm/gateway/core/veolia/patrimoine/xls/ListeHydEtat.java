package bpm.gateway.core.veolia.patrimoine.xls;

import bpm.gateway.core.veolia.VEHelper;

public enum ListeHydEtat {

	BON_ETAT("Bon état"),
	DEGRADE("Dégradé"),
	VETUSTE("Vétuste"),
	NR("NR");
    private final String value;

    ListeHydEtat(String v) {
        value = v;
    }
	
    public static String getValue(String table, String champ, ListeHydEtat v, boolean required) {
    	if (required && v == null) {
    		throw new IllegalArgumentException("Table " + table + " - Champs " + champ + " - Valeur 'Null' ou non permise pour la liste.");
    	}
    	return v != null ? v.value : null;
    }
    
    public static ListeHydEtat fromValue(String table, String champ, String v) {
    	if (v == null) {
    		System.out.println("Table " + table + " - Champs " + champ + " - Valeur 'Null' non permise.");
    		return ListeHydEtat.NR;
    	}
    	
        for (ListeHydEtat c: ListeHydEtat.values()) {
        	if (VEHelper.checkEnum(c.value, v)) {
        		return c;
        	}
        }
		System.out.println("Table " + table + " - Champs " + champ + " - Mauvaise valeur '" + v + "'");
		return ListeHydEtat.NR;
    }

}
