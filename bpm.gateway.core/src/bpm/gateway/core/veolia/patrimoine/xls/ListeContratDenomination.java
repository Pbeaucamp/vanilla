package bpm.gateway.core.veolia.patrimoine.xls;

import bpm.gateway.core.veolia.VEHelper;

public enum ListeContratDenomination {

	SECTEUR_DU_MARAIS_BRETON_ET_DES_ILES("Secteur du Marais Breton et des Iles"),
	SECTEUR_VALLEE_DU_JAUNAY("Secteur Vallée du Jaunay"),
	SECTEUR_DES_OLONNES_ET_DU_TALMONDAIS("Secteur des Olonnes et du Talmondais"),
	SECTEUR_DE_LA_HAUTE_VALLEE_DE_LA_VIE("Secteur de la Haute Vallée de la Vie"),
	SECTEUR_VALLEE_DU_MARILLET("Secteur Vallée du Marillet"),
	SECTEUR_PLAINE_ET_GRAON("Secteur Plaine et Graon"),
	SECTEUR_VALLEE_DE_LA_SEVRE("Secteur Vallée de la Sèvre"),
	SECTEUR_DES_DEUX_MAINES("Secteur des Deux Maines"),
	SECTEUR_DE_LANGLE_GUIGNARD("Secteur de l'Angle Guignard"),
	SECTEUR_DE_ROCHEREAU("Secteur de Rochereau"),
	SECTEUR_DE_LA_FORET_DE_MERVENT("Secteur de la Forêt de Mervent"),
	FONTENAY_LE_COMTE("Fontenay le Comte"),
	CAPTAGE_DE_LA_VERIE("Captage de la Vérie"),
	CAPTAGES_DE_VILLENEUVE("Captages de Villeneuve"),
	CAPTAGE_DE_SAINTE_GERMAINE("Captage de Sainte Germaine"),
	PRISE_DEAU_DES_MARTYRS("Prise d'eau des Martyrs"),
	CAPTAGES_DE_LA_RENAUDIERE("Captages de la Renaudière"),
	CAPTAGES_DE_LA_BONNINIERE_FONTEBERT_ET_THOUARSAIS("Captages de La Bonninière, Fontebert et Thouarsais"),
	CAPTAGES_DE_LA_POMMERAIE_ET_DU_TAIL("Captages de La Pommeraie et du Tail"),
	CAPTAGES_DE_LESSON_ET_SAINT_MARTIN_DES_FONTAINES("Captages de Lesson et Saint Martin des Fontaines"),
	CAPTAGES_DU_GROS_NOYER("Captages du Gros Noyer"),
	BARRAGE_ET_USINE_DU_JAUNAY("Barrage et Usine du Jaunay"),
	BARRAGES_ET_USINE_DE_SORINFINFARINE("Barrages et Usine de Sorin-Finfarine"),
	BARRAGE_ET_USINE_DAPREMONT("Barrage et Usine d'Apremont"),
	BARRAGES_DE_LA_MOINIE_ET_DU_MARILLET_ET_USINE_DU_MARILLET("Barrages de la Moinie et du Marillet et Usine du Marillet"),
	BARRAGE_ET_USINE_DU_GRAON("Barrage et Usine du Graon"),
	BARRAGE_ET_USINE_DE_LA_BULTIERE("Barrage et Usine de la Bultière"),
	BARRAGES_DE_LA_VOURAIE_ET_DE_LANGLE_GUIGNARD_ET_USINE_DE_LANGLE_GUIGNARD("Barrages de la Vouraie et de l'Angle Guignard et Usine de l'Angle Guignard"),
	BARRAGE_ET_USINE_DE_ROCHEREAU("Barrage et Usine de Rochereau"),
	BARRAGES_DE_ALBERT_PIERRE_BRUNE_ET_MERVENT_ET_USINE_DE_LA_BALINGUE("Barrages de Albert, Pierre Brune et Mervent et Usine de la Balingue");
    private final String value;

    ListeContratDenomination(String v) {
        value = v;
    }
	
    public static String getValue(String table, String champ, ListeContratDenomination v, boolean required) {
    	if (required && v == null) {
    		throw new IllegalArgumentException("Table " + table + " - Champs " + champ + " - Valeur 'Null' ou non permise pour la liste.");
    	}
    	return v != null ? v.value : null;
    }
    
    public static ListeContratDenomination fromValue(String table, String champ, String v) {
    	if (v == null) {
    		System.out.println("Table " + table + " - Champs " + champ + " - Valeur 'Null' non permise.");
    		return null;
    	}
    	
        for (ListeContratDenomination c: ListeContratDenomination.values()) {
        	if (VEHelper.checkEnum(c.value, v)) {
        		return c;
        	}
        }
		System.out.println("Table " + table + " - Champs " + champ + " - Mauvaise valeur '" + v + "'");
		return null;
    }

}
