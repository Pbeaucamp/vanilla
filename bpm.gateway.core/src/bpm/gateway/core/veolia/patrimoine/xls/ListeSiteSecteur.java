package bpm.gateway.core.veolia.patrimoine.xls;

import bpm.gateway.core.veolia.VEHelper;

public enum ListeSiteSecteur {

	HVE("HVE"),
	MBI("MBI"),
	VAJ("VAJ"),
	OTA("OTA"),
	HVV("HVV"),
	VAM("VAM"),
	PLG("PLG"),
	VAS("VAS"),
	DEM("DEM"),
	ANG("ANG"),
	RCH("RCH"),
	FME("FME"),
	FLC("FLC"),
	NR("NR");
    private final String value;

    ListeSiteSecteur(String v) {
        value = v;
    }
	
    public static String getValue(String table, String champ, ListeSiteSecteur v, boolean required) {
    	if (required && v == null) {
    		throw new IllegalArgumentException("Table " + table + " - Champs " + champ + " - Valeur 'Null' ou non permise pour la liste.");
    	}
    	return v != null ? v.value : null;
    }
    
    public static ListeSiteSecteur fromValue(String table, String champ, String v) {
    	if (v == null) {
    		System.out.println("Table " + table + " - Champs " + champ + " - Valeur 'Null' non permise.");
    		return ListeSiteSecteur.NR;
    	}
    	
        for (ListeSiteSecteur c: ListeSiteSecteur.values()) {
        	if (VEHelper.checkEnum(c.value, v)) {
        		return c;
        	}
        }
		System.out.println("Table " + table + " - Champs " + champ + " - Mauvaise valeur '" + v + "'");
		return ListeSiteSecteur.NR;
    }

}
