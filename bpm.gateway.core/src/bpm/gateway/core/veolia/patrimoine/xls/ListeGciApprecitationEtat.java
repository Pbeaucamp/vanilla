package bpm.gateway.core.veolia.patrimoine.xls;

import bpm.gateway.core.veolia.VEHelper;

public enum ListeGciApprecitationEtat {

	BON_ETAT("Bon état"),
	DEGRADE("Dégradé"),
	VETUSTE("Vétuste"),
	NR("NR");
    private final String value;

    ListeGciApprecitationEtat(String v) {
        value = v;
    }
	
    public static String getValue(String table, String champ, ListeGciApprecitationEtat v, boolean required) {
    	if (required && v == null) {
    		throw new IllegalArgumentException("Table " + table + " - Champs " + champ + " - Valeur 'Null' ou non permise pour la liste.");
    	}
    	return v != null ? v.value : null;
    }
    
    public static ListeGciApprecitationEtat fromValue(String table, String champ, String v) {
    	if (v == null) {
    		System.out.println("Table " + table + " - Champs " + champ + " - Valeur 'Null' non permise.");
    		return ListeGciApprecitationEtat.NR;
    	}
    	
        for (ListeGciApprecitationEtat c: ListeGciApprecitationEtat.values()) {
        	if (VEHelper.checkEnum(c.value, v)) {
        		return c;
        	}
        }
		System.out.println("Table " + table + " - Champs " + champ + " - Mauvaise valeur '" + v + "'");
		return ListeGciApprecitationEtat.NR;
    }

}
