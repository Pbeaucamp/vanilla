package bpm.gateway.core.veolia.patrimoine.xls;

import bpm.gateway.core.veolia.VEHelper;

public enum ListeCaptageTypecaptage {

	A_BARBACANES("à barbacanes"),
	A_BUSE_SIMPLE("à buse Simple"),
	A_CREPINE("à crépine"),
	A_DRAINS("à drains"),
	GALERIE_DRAINANTE_HORIZONTALE("galerie drainante horizontale"),
	NR("NR");
    private final String value;

    ListeCaptageTypecaptage(String v) {
        value = v;
    }
	
    public static String getValue(String table, String champ, ListeCaptageTypecaptage v, boolean required) {
    	if (required && v == null) {
    		throw new IllegalArgumentException("Table " + table + " - Champs " + champ + " - Valeur 'Null' ou non permise pour la liste.");
    	}
    	return v != null ? v.value : null;
    }
    
    public static ListeCaptageTypecaptage fromValue(String table, String champ, String v) {
    	if (v == null) {
    		System.out.println("Table " + table + " - Champs " + champ + " - Valeur 'Null' non permise.");
    		return ListeCaptageTypecaptage.NR;
    	}
    	
        for (ListeCaptageTypecaptage c: ListeCaptageTypecaptage.values()) {
        	if (VEHelper.checkEnum(c.value, v)) {
        		return c;
        	}
        }
		System.out.println("Table " + table + " - Champs " + champ + " - Mauvaise valeur '" + v + "'");
		return ListeCaptageTypecaptage.NR;
    }

}
