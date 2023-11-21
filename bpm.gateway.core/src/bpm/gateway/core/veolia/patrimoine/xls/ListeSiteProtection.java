package bpm.gateway.core.veolia.patrimoine.xls;

import bpm.gateway.core.veolia.VEHelper;

public enum ListeSiteProtection {

	PPI("PPI"),
	PPI_PPR("PPI/PPR"),
	PPI_PPR_1("PPI/PPR 2"),
	PPI_PPR_HORS_PP("PPI/PPR/Hors PP"),
	PPI_HORS_PP("PPI/Hors PP"),
	PPR("PPR"),
	PPR_ZONE_SENSIBLE("PPR Zone Sensible"),
	PPR_ZONE_COMPLEMENTAIRE("PPR Zone Complémentaire"),
	PPR_ZONE_SENSIBLE_PPE("PPR Zone Sensible / PPE"),
	PPR_ZONE_COMPLEMENTAIRE_PPE("PPR Zone Complémentaire / PPE"),
	PPR_1("PPR 1"),
	PPR_2("PPR 2"),
	PPR_PPE("PPR/PPE"),
	PPR_2_PPE("PPR 2/PPE"),
	PPR_HORS_PP("PPR/Hors PP"),
	PPE("PPE"),
	PPE_HORS_PP("PPE/Hors PP"),
	HORS_PP("Hors PP"),
	NR("NR");
    private final String value;

    ListeSiteProtection(String v) {
        value = v;
    }
	
    public static String getValue(String table, String champ, ListeSiteProtection v, boolean required) {
    	if (required && v == null) {
    		throw new IllegalArgumentException("Table " + table + " - Champs " + champ + " - Valeur 'Null' ou non permise pour la liste.");
    	}
    	return v != null ? v.value : null;
    }
    
    public static ListeSiteProtection fromValue(String table, String champ, String v) {
    	if (v == null) {
    		System.out.println("Table " + table + " - Champs " + champ + " - Valeur 'Null' non permise.");
    		return ListeSiteProtection.NR;
    	}
    	
        for (ListeSiteProtection c: ListeSiteProtection.values()) {
        	if (VEHelper.checkEnum(c.value, v)) {
        		return c;
        	}
        }
		System.out.println("Table " + table + " - Champs " + champ + " - Mauvaise valeur '" + v + "'");
		return ListeSiteProtection.NR;
    }

}
