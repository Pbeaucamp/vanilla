package bpm.gateway.core.veolia.patrimoine.xls;

import bpm.gateway.core.veolia.VEHelper;

public enum ListeContratSecteur {
	VAL_00("00"),
	VAL_01("01"),
	VAL_02("02"),
	VAL_03("03"),
	VAL_04("04"),
	VAL_05("05"),
	VAL_06("06"),
	VAL_07("07"),
	VAL_08("08"),
	VAL_09("09"),
	VAL_10("10"),
	VAL_11("11"),
	VAL_12("12");
    private final String value;

    ListeContratSecteur(String v) {
        value = v;
    }
	
    public static String getValue(String table, String champ, ListeContratSecteur v, boolean required) {
    	if (required && v == null) {
    		throw new IllegalArgumentException("Table " + table + " - Champs " + champ + " - Valeur 'Null' ou non permise pour la liste.");
    	}
    	return v != null ? v.value : null;
    }
    
    public static ListeContratSecteur fromValue(String table, String champ, String v) {
    	if (v == null) {
    		System.out.println("Table " + table + " - Champs " + champ + " - Valeur 'Null' non permise.");
    		return null;
    	}
    	
        for (ListeContratSecteur c: ListeContratSecteur.values()) {
        	if (VEHelper.checkEnum(c.value, v)) {
        		return c;
        	}
        }
		System.out.println("Table " + table + " - Champs " + champ + " - Mauvaise valeur '" + v + "'");
		return null;
    }

}
