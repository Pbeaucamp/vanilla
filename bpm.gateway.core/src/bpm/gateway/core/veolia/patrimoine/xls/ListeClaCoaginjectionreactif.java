package bpm.gateway.core.veolia.patrimoine.xls;

import bpm.gateway.core.veolia.VEHelper;

public enum ListeClaCoaginjectionreactif {

	CHLORURE_FERRIQUE("Chlorure ferrique"),
	SULFATE_DALUMINE("Sulfate d'alumine"),
	CHLORURE_FERRIQUE_SULFATE_DALUMINE_EN_MELANGE("Chlorure ferrique + sulfate d'alumine en mélange"),
	NR("NR");
    private final String value;

    ListeClaCoaginjectionreactif(String v) {
        value = v;
    }
	
    public static String getValue(String table, String champ, ListeClaCoaginjectionreactif v, boolean required) {
    	if (required && v == null) {
    		throw new IllegalArgumentException("Table " + table + " - Champs " + champ + " - Valeur 'Null' ou non permise pour la liste.");
    	}
    	return v != null ? v.value : null;
    }
    
    public static ListeClaCoaginjectionreactif fromValue(String table, String champ, String v) {
    	if (v == null) {
    		System.out.println("Table " + table + " - Champs " + champ + " - Valeur 'Null' non permise.");
    		return ListeClaCoaginjectionreactif.NR;
    	}
    	
        for (ListeClaCoaginjectionreactif c: ListeClaCoaginjectionreactif.values()) {
        	if (VEHelper.checkEnum(c.value, v)) {
        		return c;
        	}
        }
		System.out.println("Table " + table + " - Champs " + champ + " - Mauvaise valeur '" + v + "'");
		return ListeClaCoaginjectionreactif.NR;
    }

}
