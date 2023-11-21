package bpm.gateway.core.veolia.patrimoine.xls;

import bpm.gateway.core.veolia.VEHelper;

public enum ListeProtectioncathodiqueTypeposte {

	COURANT_IMPOSE("Courant imposé"),
	ANODES_SACRIFICIELLES("Anodes sacrificielles"),
	ELECTRODE_DE_REFERENCE("Electrode de référence"),
	NR("NR");
    private final String value;

    ListeProtectioncathodiqueTypeposte(String v) {
        value = v;
    }
	
    public static String getValue(String table, String champ, ListeProtectioncathodiqueTypeposte v, boolean required) {
    	if (required && v == null) {
    		throw new IllegalArgumentException("Table " + table + " - Champs " + champ + " - Valeur 'Null' ou non permise pour la liste.");
    	}
    	return v != null ? v.value : null;
    }
    
    public static ListeProtectioncathodiqueTypeposte fromValue(String table, String champ, String v) {
    	if (v == null) {
    		System.out.println("Table " + table + " - Champs " + champ + " - Valeur 'Null' non permise.");
    		return ListeProtectioncathodiqueTypeposte.NR;
    	}
    	
        for (ListeProtectioncathodiqueTypeposte c: ListeProtectioncathodiqueTypeposte.values()) {
        	if (VEHelper.checkEnum(c.value, v)) {
        		return c;
        	}
        }
		System.out.println("Table " + table + " - Champs " + champ + " - Mauvaise valeur '" + v + "'");
		return ListeProtectioncathodiqueTypeposte.NR;
    }

}
