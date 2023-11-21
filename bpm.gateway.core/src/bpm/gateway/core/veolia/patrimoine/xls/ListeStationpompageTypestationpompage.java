package bpm.gateway.core.veolia.patrimoine.xls;

import bpm.gateway.core.veolia.VEHelper;

public enum ListeStationpompageTypestationpompage {

	POMPAGE("Pompage"),
	SURPRESSION("Surpression"),
	EXHAURE("Exhaure"),
	NR("NR");
    private final String value;

    ListeStationpompageTypestationpompage(String v) {
        value = v;
    }
	
    public static String getValue(String table, String champ, ListeStationpompageTypestationpompage v, boolean required) {
    	if (required && v == null) {
    		throw new IllegalArgumentException("Table " + table + " - Champs " + champ + " - Valeur 'Null' ou non permise pour la liste.");
    	}
    	return v != null ? v.value : null;
    }
    
    public static ListeStationpompageTypestationpompage fromValue(String table, String champ, String v) {
    	if (v == null) {
    		System.out.println("Table " + table + " - Champs " + champ + " - Valeur 'Null' non permise.");
    		return ListeStationpompageTypestationpompage.NR;
    	}
    	
        for (ListeStationpompageTypestationpompage c: ListeStationpompageTypestationpompage.values()) {
        	if (VEHelper.checkEnum(c.value, v)) {
        		return c;
        	}
        }
		System.out.println("Table " + table + " - Champs " + champ + " - Mauvaise valeur '" + v + "'");
		return ListeStationpompageTypestationpompage.NR;
    }

}
