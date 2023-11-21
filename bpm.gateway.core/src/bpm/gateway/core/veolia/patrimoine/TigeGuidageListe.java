//
// Ce fichier a �t� g�n�r� par l'impl�mentation de r�f�rence JavaTM Architecture for XML Binding (JAXB), v2.2.8-b130911.1802 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apport�e � ce fichier sera perdue lors de la recompilation du sch�ma source. 
// G�n�r� le : 2020.01.29 � 03:15:22 PM CET 
//


package bpm.gateway.core.veolia.patrimoine;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

import bpm.gateway.core.veolia.VEHelper;


/**
 * <p>Classe Java pour TigeGuidageListe.
 * 
 * <p>Le fragment de sch�ma suivant indique le contenu attendu figurant dans cette classe.
 * <p>
 * <pre>
 * &lt;simpleType name="TigeGuidageListe">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="v�rins"/>
 *     &lt;enumeration value="cr�maill�res"/>
 *     &lt;enumeration value="bringueballe"/>
 *     &lt;enumeration value="NR"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "TigeGuidageListe")
@XmlEnum
public enum TigeGuidageListe {

    @XmlEnumValue("v\u00e9rins")
    VERINS("v\u00e9rins"),
    @XmlEnumValue("cr\u00e9maill\u00e8res")
    CREMAILLERES("cr\u00e9maill\u00e8res"),
    @XmlEnumValue("bringueballe")
    BRINGUEBALLE("bringueballe"),
    NR("NR");
    private final String value;

    TigeGuidageListe(String v) {
        value = v;
    }
	
    public static String getValue(String table, String champ, TigeGuidageListe v, boolean required) {
    	if (required && v == null) {
    		throw new IllegalArgumentException("Table " + table + " - Champs " + champ + " - Valeur 'Null' ou non permise pour la liste.");
    	}
    	return v != null ? v.value : null;
    }
    
    public static TigeGuidageListe fromValue(String table, String champ, String v) {
    	if (v == null) {
    		System.out.println("Table " + table + " - Champs " + champ + " - Valeur 'Null' non permise.");
    		return TigeGuidageListe.NR;
    	}
    	
        for (TigeGuidageListe c: TigeGuidageListe.values()) {
        	if (VEHelper.checkEnum(c.value, v)) {
        		return c;
        	}
        }
		System.out.println("Table " + table + " - Champs " + champ + " - Mauvaise valeur '" + v + "'");
		return TigeGuidageListe.NR;
    }

}
