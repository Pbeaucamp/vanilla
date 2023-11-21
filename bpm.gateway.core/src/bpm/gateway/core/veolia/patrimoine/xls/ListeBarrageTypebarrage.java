package bpm.gateway.core.veolia.patrimoine.xls;

import bpm.gateway.core.veolia.VEHelper;

public enum ListeBarrageTypebarrage {

	BARRAGE_POIDS("Barrage poids"),
	REMBLAI_EN_ENROCHEMENTS("Remblai en enrochements"),
	BARRAGE_POIDS_EN_TERRE("Barrage poids en terre"),
	BARRAGE_VOUTE("Barrage Voûte"),
	NR("NR");
    private final String value;

    ListeBarrageTypebarrage(String v) {
        value = v;
    }
	
    public static String getValue(String table, String champ, ListeBarrageTypebarrage v, boolean required) {
    	if (required && v == null) {
    		throw new IllegalArgumentException("Table " + table + " - Champs " + champ + " - Valeur 'Null' ou non permise pour la liste.");
    	}
    	return v != null ? v.value : null;
    }
    
    public static ListeBarrageTypebarrage fromValue(String table, String champ, String v) {
    	if (v == null) {
    		System.out.println("Table " + table + " - Champs " + champ + " - Valeur 'Null' non permise.");
    		return ListeBarrageTypebarrage.NR;
    	}
    	
        for (ListeBarrageTypebarrage c: ListeBarrageTypebarrage.values()) {
        	if (VEHelper.checkEnum(c.value, v)) {
        		return c;
        	}
        }
		System.out.println("Table " + table + " - Champs " + champ + " - Mauvaise valeur '" + v + "'");
		return ListeBarrageTypebarrage.NR;
    }

}
