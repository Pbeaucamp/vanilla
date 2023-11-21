package bpm.gateway.core.veolia.patrimoine.xls;

import bpm.gateway.core.veolia.VEHelper;

public enum ListeTlgPositionnement {

	ARMOIRE_DEPORTEE("Armoire déportée"),
	CHAMBRE_LT("Chambre LT"),
	INTERIEUR_OUVRAGE("Intérieur ouvrage"),
	NR("NR");
    private final String value;

    ListeTlgPositionnement(String v) {
        value = v;
    }
	
    public static String getValue(String table, String champ, ListeTlgPositionnement v, boolean required) {
    	if (required && v == null) {
    		throw new IllegalArgumentException("Table " + table + " - Champs " + champ + " - Valeur 'Null' ou non permise pour la liste.");
    	}
    	return v != null ? v.value : null;
    }
    
    public static ListeTlgPositionnement fromValue(String table, String champ, String v) {
    	if (v == null) {
    		System.out.println("Table " + table + " - Champs " + champ + " - Valeur 'Null' non permise.");
    		return ListeTlgPositionnement.NR;
    	}
    	
        for (ListeTlgPositionnement c: ListeTlgPositionnement.values()) {
        	if (VEHelper.checkEnum(c.value, v)) {
        		return c;
        	}
        }
		System.out.println("Table " + table + " - Champs " + champ + " - Mauvaise valeur '" + v + "'");
		return ListeTlgPositionnement.NR;
    }

}
