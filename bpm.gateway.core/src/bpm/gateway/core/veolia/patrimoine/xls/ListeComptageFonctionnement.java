package bpm.gateway.core.veolia.patrimoine.xls;

import bpm.gateway.core.veolia.VEHelper;

public enum ListeComptageFonctionnement {

	ANG_DEM("ANG > DEM"),
	ANG_FME("ANG > FME"),
	ANG_PLG("ANG > PLG"),
	ANG_RCH("ANG > RCH"),
	ANG_VAM("ANG > VAM"),
	Atlanticeau_HVV("Atlantic'eau > HVV"),
	Atlanticeau_VAS("Atlantic'eau > VAS"),
	Atlanticeau_MBI("Atlantic'eau> MBI"),
	DEM_ANG("DEM > ANG"),
	DEM_Atlanticeau("DEM > Atlantic'eau"),
	DEM_HVV("DEM > HVV"),
	DEM_RCH("DEM > RCH"),
	DEM_VAS("DEM > VAS"),
	DEM_VAM("DEM<>VAM"),
	FME__ANG("FME > ANG"),
	FME_Fontenay_le_Comte("FME > Fontenay le Comte"),
	FME_PLG("FME > PLG"),
	FME_RCH("FME > RCH"),
	FME_SDE_17("FME > SDE 17"),
	FME_SDE17("FME > SDE17"),
	FME_SE_Gatine("FME > SE Gâtine"),
	FME_ANG("FME>ANG"),
	Fontenay_le_Comte_FME("Fontenay le Comte > FME "),
	HVV_Atlanticeau("HVV > Atlantic'eau"),
	HVV_MBI("HVV > MBI"),
	HVV_VAJ("HVV > VAJ"),
	La_Roche_sur_Yon_HVV("La Roche sur Yon > HVV"),
	La_Roche_sur_Yon_VAM("La Roche sur Yon > VAM"),
	MBI_HVV("MBI > HVV"),
	MBI_VAJ("MBI > VAJ"),
	OTA_VAJ("OTA > VAJ"),
	PLG_ANG("PLG > ANG"),
	PLG_FME("PLG > FME"),
	PLG_La_Roche_sur_Yon("PLG > La Roche sur Yon"),
	PLG_OTA("PLG > OTA"),
	PLG_SDE_17("PLG > SDE 18"),
	PLG_VAJ("PLG > VAJ"),
	PLG_VAM("PLG > VAM"),
	RCH_ANG("RCH > ANG"),
	RCH_DEM("RCH > DEM"),
	RCH_SVL_79("RCH > SVL 80"),
	RCH_VAS("RCH > VAS"),
	Region_Ouest_Cholet_VAS("Région Ouest Cholet > VAS"),
	SDE_17_FME("SDE 17 > FME"),
	secto("secto"),
	SVL_79_VAS("SVL 79 > VAS"),
	VAJ_HVV("VAJ > HVV"),
	VAJ_MBI("VAJ > MBI"),
	VAJ_OTA("VAJ > OTA"),
	VAJ_PLG("VAJ > PLG"),
	VAM_ANG("VAM > ANG"),
	VAM_HVV("VAM > HVV"),
	VAM_La_Roche_sur_Yon("VAM > La Roche sur Yon "),
	VAM_PLG("VAM > PLG"),
	VAS_Atlanticeau("VAS > Atlantic'eau"),
	VAS_DEM("VAS > DEM"),
	VAS_Region_Ouest_Cholet("VAS > Région Ouest Cholet"),
	NR("NR");
    private final String value;

    ListeComptageFonctionnement(String v) {
        value = v;
    }
	
    public static String getValue(String table, String champ, ListeComptageFonctionnement v, boolean required) {
    	if (required && v == null) {
    		throw new IllegalArgumentException("Table " + table + " - Champs " + champ + " - Valeur 'Null' ou non permise pour la liste.");
    	}
    	return v != null ? v.value : null;
    }
    
    public static ListeComptageFonctionnement fromValue(String table, String champ, String v) {
    	if (v == null) {
    		System.out.println("Table " + table + " - Champs " + champ + " - Valeur 'Null' non permise.");
    		return ListeComptageFonctionnement.NR;
    	}
    	
        for (ListeComptageFonctionnement c: ListeComptageFonctionnement.values()) {
        	if (VEHelper.checkEnum(c.value, v)) {
        		return c;
        	}
        }
		System.out.println("Table " + table + " - Champs " + champ + " - Mauvaise valeur '" + v + "'");
		return ListeComptageFonctionnement.NR;
    }

}
