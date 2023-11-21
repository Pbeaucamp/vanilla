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
 * <p>Java class for typesCrepinesListe.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="typesCrepinesListe">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Fentes horizontales"/>
 *     &lt;enumeration value="Fentes verticales"/>
 *     &lt;enumeration value="Fil enroul�"/>
 *     &lt;enumeration value="NR"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "typesCrepinesListe")
@XmlEnum
public enum TypesCrepinesListe {

    @XmlEnumValue("Fentes horizontales")
    FENTES_HORIZONTALES("Fentes horizontales"),
    @XmlEnumValue("Fentes verticales")
    FENTES_VERTICALES("Fentes verticales"),
    @XmlEnumValue("Fil enroul\u00e9")
    FIL_ENROULE("Fil enroul\u00e9"),
    NR("NR");
    private final String value;

    TypesCrepinesListe(String v) {
        value = v;
    }
	
    public static String getValue(String table, String champ, TypesCrepinesListe v, boolean required) {
    	if (required && v == null) {
    		throw new IllegalArgumentException("Table " + table + " - Champs " + champ + " - Valeur 'Null' ou non permise pour la liste.");
    	}
    	return v != null ? v.value : null;
    }
    
    public static TypesCrepinesListe fromValue(String table, String champ, String v) {
    	if (v == null) {
    		System.out.println("Table " + table + " - Champs " + champ + " - Valeur 'Null' non permise.");
    		return TypesCrepinesListe.NR;
    	}
    	
        for (TypesCrepinesListe c: TypesCrepinesListe.values()) {
        	if (VEHelper.checkEnum(c.value, v)) {
        		return c;
        	}
        }
		System.out.println("Table " + table + " - Champs " + champ + " - Mauvaise valeur '" + v + "'");
		return TypesCrepinesListe.NR;
    }

}
