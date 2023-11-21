package bpm.gateway.core.veolia.patrimoine.xls;

import bpm.gateway.core.veolia.VEHelper;

public enum ListeTrtProcede {

	CENTRIFUGEUSE("Centrifugeuse"),
	PRESSE_A_BOUE("Presse à boue"),
	NR("NR");
    private final String value;

    ListeTrtProcede(String v) {
        value = v;
    }
	
    public static String getValue(String table, String champ, ListeTrtProcede v, boolean required) {
    	if (required && v == null) {
    		throw new IllegalArgumentException("Table " + table + " - Champs " + champ + " - Valeur 'Null' ou non permise pour la liste.");
    	}
    	return v != null ? v.value : null;
    }
    
    public static ListeTrtProcede fromValue(String table, String champ, String v) {
    	if (v == null) {
    		System.out.println("Table " + table + " - Champs " + champ + " - Valeur 'Null' non permise.");
    		return ListeTrtProcede.NR;
    	}
    	
        for (ListeTrtProcede c: ListeTrtProcede.values()) {
        	if (VEHelper.checkEnum(c.value, v)) {
        		return c;
        	}
        }
		System.out.println("Table " + table + " - Champs " + champ + " - Mauvaise valeur '" + v + "'");
		return ListeTrtProcede.NR;
    }

}
