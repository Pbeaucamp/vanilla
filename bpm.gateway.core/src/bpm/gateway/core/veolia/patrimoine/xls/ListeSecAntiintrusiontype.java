package bpm.gateway.core.veolia.patrimoine.xls;

import bpm.gateway.core.veolia.VEHelper;

public enum ListeSecAntiintrusiontype {

	ALARME("alarme"),
	FERMETURE_PHYSIQUE("fermeture physique"),
	CONTACT("Contact"),
	CADENAS_SUR_CRENOLINE("Cadenas sur crenoline"),
	NR("NR");
    private final String value;

    ListeSecAntiintrusiontype(String v) {
        value = v;
    }
	
    public static String getValue(String table, String champ, ListeSecAntiintrusiontype v, boolean required) {
    	if (required && v == null) {
    		throw new IllegalArgumentException("Table " + table + " - Champs " + champ + " - Valeur 'Null' ou non permise pour la liste.");
    	}
    	return v != null ? v.value : null;
    }
    
    public static ListeSecAntiintrusiontype fromValue(String table, String champ, String v) {
    	if (v == null) {
    		System.out.println("Table " + table + " - Champs " + champ + " - Valeur 'Null' non permise.");
    		return ListeSecAntiintrusiontype.NR;
    	}
    	
        for (ListeSecAntiintrusiontype c: ListeSecAntiintrusiontype.values()) {
        	if (VEHelper.checkEnum(c.value, v)) {
        		return c;
        	}
        }
		System.out.println("Table " + table + " - Champs " + champ + " - Mauvaise valeur '" + v + "'");
		return ListeSecAntiintrusiontype.NR;
    }

}
