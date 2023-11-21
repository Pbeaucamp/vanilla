package bpm.gateway.core.veolia.patrimoine.xls;

import bpm.gateway.core.veolia.VEHelper;

public enum ListeBarrageClasse {

	A("A"),
	B("B"),
	C("C"),
	NR("NR");
    private final String value;

    ListeBarrageClasse(String v) {
        value = v;
    }
	
    public static String getValue(String table, String champ, ListeBarrageClasse v, boolean required) {
    	if (required && v == null) {
    		throw new IllegalArgumentException("Table " + table + " - Champs " + champ + " - Valeur 'Null' ou non permise pour la liste.");
    	}
    	return v != null ? v.value : null;
    }
    
    public static ListeBarrageClasse fromValue(String table, String champ, String v) {
    	if (v == null) {
    		System.out.println("Table " + table + " - Champs " + champ + " - Valeur 'Null' non permise.");
    		return ListeBarrageClasse.NR;
    	}
    	
        for (ListeBarrageClasse c: ListeBarrageClasse.values()) {
        	if (VEHelper.checkEnum(c.value, v)) {
        		return c;
        	}
        }
		System.out.println("Table " + table + " - Champs " + champ + " - Mauvaise valeur '" + v + "'");
		return ListeBarrageClasse.NR;
    }

}
