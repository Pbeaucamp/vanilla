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
 * <p>Java class for typesDiffuseursListe.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="typesDiffuseursListe">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Poreux"/>
 *     &lt;enumeration value="Bougie"/>
 *     &lt;enumeration value="Turbine"/>
 *     &lt;enumeration value="NR"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "typesDiffuseursListe")
@XmlEnum
public enum TypesDiffuseursListe {

    @XmlEnumValue("Poreux")
    POREUX("Poreux"),
    @XmlEnumValue("Bougie")
    BOUGIE("Bougie"),
    @XmlEnumValue("Turbine")
    TURBINE("Turbine"),
    NR("NR");
    private final String value;

    TypesDiffuseursListe(String v) {
        value = v;
    }

    public static String getValue(String table, String champ, TypesDiffuseursListe v) {
    	if (v == null) {
    		throw new IllegalArgumentException("Table " + table + " - Champs " + champ + " - Valeur 'Null' ou non permise pour la liste.");
    	}
    	return v.value;
    }
    
    public static TypesDiffuseursListe fromValue(String table, String champ, String v) {
    	if (v == null) {
    		System.out.println("Table " + table + " - Champs " + champ + " - Valeur 'Null' non permise.");
    		return TypesDiffuseursListe.NR;
    	}
    	
        for (TypesDiffuseursListe c: TypesDiffuseursListe.values()) {
        	if (VEHelper.checkEnum(c.value, v)) {
        		return c;
        	}
        }
		System.out.println("Table " + table + " - Champs " + champ + " - Mauvaise valeur '" + v + "'");
		return TypesDiffuseursListe.NR;
    }

}
