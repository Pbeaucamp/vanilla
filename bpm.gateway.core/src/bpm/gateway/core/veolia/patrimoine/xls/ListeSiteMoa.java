package bpm.gateway.core.veolia.patrimoine.xls;

import bpm.gateway.core.veolia.VEHelper;

public enum ListeSiteMoa {

	HORS_VENDEE_EAU("Hors Vendée Eau"),
	SIAEP_DU_MARAIS_BRETON_ET_DES_ILES("SIAEP du Marais Breton et des Iles"),
	SIAEP_DE_LA_VALLEE_DU_JAUNAY("SIAEP de la Vallée du Jaunay"),
	SIAEP_DES_OLONNES_ET_DU_TALMONDAIS("SIAEP des Olonnes et du Talmondais"),
	SIAEP_DE_LA_HAUTE_VALLEE_DE_LA_VIE("SIAEP de la Haute Vallée de la Vie"),
	SIAEP_VALLEE_DU_MARILLET("SIAEP Vallée du Marillet"),
	SIAEP_PLAINE_ET_GRAON("SIAEP Plaine et Graon"),
	SIAEP_VALLEE_DE_LA_SEVRE("SIAEP Vallée de la Sèvre"),
	SIAEP_DES_DEUX_MAINES("SIAEP des Deux Maines"),
	SIAEP_DE_LANGLE_GUIGNARD("SIAEP de l'Angle Guignard"),
	SIAEP_DE_ROCHEREAU("SIAEP de Rochereau"),
	SIAEP_DE_LA_FORET_DE_MERVENT("SIAEP de la Forêt de Mervent"),
	SIAEP_DE_LA_FORET_DE_MERVENT_FONTENAY_LE_COMTE("SIAEP de la Forêt de Mervent - Fontenay le Comte"),
	VENDEE_EAU("Vendée Eau"),
	NR("NR");
    private final String value;

    ListeSiteMoa(String v) {
        value = v;
    }
	
    public static String getValue(String table, String champ, ListeSiteMoa v, boolean required) {
    	if (required && v == null) {
    		throw new IllegalArgumentException("Table " + table + " - Champs " + champ + " - Valeur 'Null' ou non permise pour la liste.");
    	}
    	return v != null ? v.value : null;
    }
    
    public static ListeSiteMoa fromValue(String table, String champ, String v) {
    	if (v == null) {
    		System.out.println("Table " + table + " - Champs " + champ + " - Valeur 'Null' non permise.");
    		return ListeSiteMoa.NR;
    	}
    	
        for (ListeSiteMoa c: ListeSiteMoa.values()) {
        	if (VEHelper.checkEnum(c.value, v)) {
        		return c;
        	}
        }
		System.out.println("Table " + table + " - Champs " + champ + " - Mauvaise valeur '" + v + "'");
		return ListeSiteMoa.NR;
    }

}
