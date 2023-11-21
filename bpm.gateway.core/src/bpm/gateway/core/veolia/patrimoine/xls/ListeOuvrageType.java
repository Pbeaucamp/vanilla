package bpm.gateway.core.veolia.patrimoine.xls;

import bpm.gateway.core.veolia.VEHelper;

public enum ListeOuvrageType {

	RESERVOIR_SUR_TOUR("Réservoir sur tour"),
	BACHE_AU_SOL("Bâche au sol"),
	STATION_DE_POMPAGE("Station de pompage"),
	CHLORATION("Chloration"),
	CHAMBRE_DE_COMPTAGE("Chambre de comptage"),
	PROTECTION_CATHODIQUE("Protection cathodique"),
	DISPOSITIFS_DE_MESURES_EN_RESEAU("Dispositifs de mesures en réseau"),
	VANNES_DE_REGULATION("Vannes de régulation"),
	FICTIF("Fictif"),
	BARRAGE("Barrage"),
	CARRIERE("Carrière"),
	USINE("Usine"),
	PRE_BARRAGE("Pré-barrage"),
	LOGEMENT("Logement"),
	CAPTAGE("Captage"),
	LAGUNE("Lagune"),
	PRISE_DEAU("Prise d'eau"),
	TARIFS_JAUNE_ET_VERT("Tarifs Jaune et Vert"),
	NR("NR");
    private final String value;

    ListeOuvrageType(String v) {
        value = v;
    }
	
    public static String getValue(String table, String champ, ListeOuvrageType v, boolean required) {
    	if (required && v == null) {
    		throw new IllegalArgumentException("Table " + table + " - Champs " + champ + " - Valeur 'Null' ou non permise pour la liste.");
    	}
    	return v != null ? v.value : null;
    }
    
    public static ListeOuvrageType fromValue(String table, String champ, String v) {
    	if (v == null) {
    		System.out.println("Table " + table + " - Champs " + champ + " - Valeur 'Null' non permise.");
    		return ListeOuvrageType.NR;
    	}
    	
        for (ListeOuvrageType c: ListeOuvrageType.values()) {
        	if (VEHelper.checkEnum(c.value, v)) {
        		return c;
        	}
        }
		System.out.println("Table " + table + " - Champs " + champ + " - Mauvaise valeur '" + v + "'");
		return ListeOuvrageType.NR;
    }

}
