//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.01.02 at 10:47:34 AM CET 
//


package bpm.gateway.core.veolia.patrimoine;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

import bpm.gateway.core.veolia.VEHelper;


/**
 * <p>Java class for naturesBiensListe.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="naturesBiensListe">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Bien propre (exploitant)"/>
 *     &lt;enumeration value="Bien de retour"/>
 *     &lt;enumeration value="Bien de reprise"/>
 *     &lt;enumeration value="NR"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "naturesBiensListe")
@XmlEnum
public enum NaturesBiensListe {

    @XmlEnumValue("Bien propre (exploitant)")
    BIEN_PROPRE_EXPLOITANT("Bien propre (exploitant)"),
    @XmlEnumValue("Bien de retour")
    BIEN_DE_RETOUR("Bien de retour"),
    @XmlEnumValue("Bien de reprise")
    BIEN_DE_REPRISE("Bien de reprise"),
    NR("NR");
    private final String value;

    NaturesBiensListe(String v) {
        value = v;
    }
	
    public static String getValue(String table, String champ, NaturesBiensListe v) {
    	if (v == null) {
    		throw new IllegalArgumentException("Table " + table + " - Champs " + champ + " - Valeur 'Null' ou non permise pour la liste.");
    	}
    	return v.value;
    }
    
    public static NaturesBiensListe fromValue(String table, String champ, String v) {
    	if (v == null) {
    		System.out.println("Table " + table + " - Champs " + champ + " - Valeur 'Null' non permise.");
    		return NaturesBiensListe.NR;
    	}
    	
        for (NaturesBiensListe c: NaturesBiensListe.values()) {
        	if (VEHelper.checkEnum(c.value, v)) {
        		return c;
        	}
        }
		System.out.println("Table " + table + " - Champs " + champ + " - Mauvaise valeur '" + v + "'");
		return NaturesBiensListe.NR;
    }

}
