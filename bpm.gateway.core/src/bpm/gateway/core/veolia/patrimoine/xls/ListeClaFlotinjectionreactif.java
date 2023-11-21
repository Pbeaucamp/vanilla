package bpm.gateway.core.veolia.patrimoine.xls;

import bpm.gateway.core.veolia.VEHelper;

public enum ListeClaFlotinjectionreactif {

	POLYMERE("Polymère"),
	MICRO_SABLE("Micro-sable"),
	CHLORURE_FERRIQUE("Chlorure ferrique"),
	FLOCULANT("Floculant"),
	NR("NR");
    private final String value;

    ListeClaFlotinjectionreactif(String v) {
        value = v;
    }
	
    public static String getValue(String table, String champ, ListeClaFlotinjectionreactif v, boolean required) {
    	if (required && v == null) {
    		throw new IllegalArgumentException("Table " + table + " - Champs " + champ + " - Valeur 'Null' ou non permise pour la liste.");
    	}
    	return v != null ? v.value : null;
    }
    
    public static ListeClaFlotinjectionreactif fromValue(String table, String champ, String v) {
    	if (v == null) {
    		System.out.println("Table " + table + " - Champs " + champ + " - Valeur 'Null' non permise.");
    		return ListeClaFlotinjectionreactif.NR;
    	}
    	
        for (ListeClaFlotinjectionreactif c: ListeClaFlotinjectionreactif.values()) {
        	if (VEHelper.checkEnum(c.value, v)) {
        		return c;
        	}
        }
		System.out.println("Table " + table + " - Champs " + champ + " - Mauvaise valeur '" + v + "'");
		return ListeClaFlotinjectionreactif.NR;
    }

}
