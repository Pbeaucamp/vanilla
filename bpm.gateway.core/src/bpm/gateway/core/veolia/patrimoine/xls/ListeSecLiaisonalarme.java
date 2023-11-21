package bpm.gateway.core.veolia.patrimoine.xls;

import bpm.gateway.core.veolia.VEHelper;

public enum ListeSecLiaisonalarme {

	ALARME_RELIEE_EXPLOITANT("alarme reliée exploitant"),
	ALARME_RELIEE_GARDIENNAGE_PRIVE("alarme reliée gardiennage privé"),
	NR("NR");
    private final String value;

    ListeSecLiaisonalarme(String v) {
        value = v;
    }
	
    public static String getValue(String table, String champ, ListeSecLiaisonalarme v, boolean required) {
    	if (required && v == null) {
    		throw new IllegalArgumentException("Table " + table + " - Champs " + champ + " - Valeur 'Null' ou non permise pour la liste.");
    	}
    	return v != null ? v.value : null;
    }
    
    public static ListeSecLiaisonalarme fromValue(String table, String champ, String v) {
    	if (v == null) {
    		System.out.println("Table " + table + " - Champs " + champ + " - Valeur 'Null' non permise.");
    		return ListeSecLiaisonalarme.NR;
    	}
    	
        for (ListeSecLiaisonalarme c: ListeSecLiaisonalarme.values()) {
        	if (VEHelper.checkEnum(c.value, v)) {
        		return c;
        	}
        }
		System.out.println("Table " + table + " - Champs " + champ + " - Mauvaise valeur '" + v + "'");
		return ListeSecLiaisonalarme.NR;
    }

}
