package bpm.gateway.core.veolia.patrimoine.xls;

import bpm.gateway.core.veolia.VEHelper;

public enum ListeTlgTarif {

	JAUNE("Jaune"),
	VERT("Vert"),
	GROS_CONSOMMATEUR("Gros consommateur"),
	NR("NR");
    private final String value;

    ListeTlgTarif(String v) {
        value = v;
    }
	
    public static String getValue(String table, String champ, ListeTlgTarif v, boolean required) {
    	if (required && v == null) {
    		throw new IllegalArgumentException("Table " + table + " - Champs " + champ + " - Valeur 'Null' ou non permise pour la liste.");
    	}
    	return v != null ? v.value : null;
    }
    
    public static ListeTlgTarif fromValue(String table, String champ, String v) {
    	if (v == null) {
    		System.out.println("Table " + table + " - Champs " + champ + " - Valeur 'Null' non permise.");
    		return ListeTlgTarif.NR;
    	}
    	
        for (ListeTlgTarif c: ListeTlgTarif.values()) {
        	if (VEHelper.checkEnum(c.value, v)) {
        		return c;
        	}
        }
		System.out.println("Table " + table + " - Champs " + champ + " - Mauvaise valeur '" + v + "'");
		return ListeTlgTarif.NR;
    }

}
