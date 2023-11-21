package bpm.gateway.core.veolia.patrimoine.xls;

import bpm.gateway.core.veolia.VEHelper;

public enum ListeRegType {

	STABILISATEUR_AMONT("Stabilisateur amont"),
	STABILISATEUR_AVAL("Stabilisateur aval"),
	STABILISATEUR_AMONT_AVAL("Stabilisateur amont-aval"),
	STABILISATEUR_AMONT_BI_ETAGE("Stabilisateur amont bi-étagé"),
	STABILISATEUR_AVAL_BI_ETAGE("Stabilisateur aval bi-étagé"),
	LIMITEUR_DE_DEBIT("Limiteur de débit"),
	ROBINET_ALTIMETRIQUE("Robinet altimétrique"),
	VANNE_DE_SURVITESSE("Vanne de survitesse"),
	CLAPET("Clapet"),
	DISCONNECTEUR("Disconnecteur"),
	SOUPAPE_DE_DECHARGE("Soupape de décharge"),
	REDUCTEUR_DE_PRESSION("Réducteur de pression"),
	LIMITEUR_DE_PRESSION("Limiteur de pression"),
	NR("NR");
    private final String value;

    ListeRegType(String v) {
        value = v;
    }
	
    public static String getValue(String table, String champ, ListeRegType v, boolean required) {
    	if (required && v == null) {
    		throw new IllegalArgumentException("Table " + table + " - Champs " + champ + " - Valeur 'Null' ou non permise pour la liste.");
    	}
    	return v != null ? v.value : null;
    }
    
    public static ListeRegType fromValue(String table, String champ, String v) {
    	if (v == null) {
    		System.out.println("Table " + table + " - Champs " + champ + " - Valeur 'Null' non permise.");
    		return ListeRegType.NR;
    	}
    	
        for (ListeRegType c: ListeRegType.values()) {
        	if (VEHelper.checkEnum(c.value, v)) {
        		return c;
        	}
        }
		System.out.println("Table " + table + " - Champs " + champ + " - Mauvaise valeur '" + v + "'");
		return ListeRegType.NR;
    }

}
