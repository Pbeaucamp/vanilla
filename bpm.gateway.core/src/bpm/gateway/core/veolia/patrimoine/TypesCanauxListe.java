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
 * <p>Java class for typesCanauxListe.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="typesCanauxListe">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Lame déversante"/>
 *     &lt;enumeration value="Rectangulaire"/>
 *     &lt;enumeration value="Triangulaire"/>
 *     &lt;enumeration value="Venturi"/>
 *     &lt;enumeration value="NR"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "typesCanauxListe")
@XmlEnum
public enum TypesCanauxListe {

    @XmlEnumValue("Lame d\u00e9versante")
    LAME_DEVERSANTE("Lame d\u00e9versante"),
    @XmlEnumValue("Rectangulaire")
    RECTANGULAIRE("Rectangulaire"),
    @XmlEnumValue("Triangulaire")
    TRIANGULAIRE("Triangulaire"),
    @XmlEnumValue("Venturi")
    VENTURI("Venturi"),
    NR("NR");
    private final String value;

    TypesCanauxListe(String v) {
        value = v;
    }
	
    public static String getValue(String table, String champ, TypesCanauxListe v) {
    	if (v == null) {
    		throw new IllegalArgumentException("Table " + table + " - Champs " + champ + " - Valeur 'Null' ou non permise pour la liste.");
    	}
    	return v.value;
    }
    
    public static TypesCanauxListe fromValue(String table, String champ, String v) {
    	if (v == null) {
    		System.out.println("Table " + table + " - Champs " + champ + " - Valeur 'Null' non permise.");
    		return TypesCanauxListe.NR;
    	}
    	
        for (TypesCanauxListe c: TypesCanauxListe.values()) {
        	if (VEHelper.checkEnum(c.value, v)) {
        		return c;
        	}
        }
		System.out.println("Table " + table + " - Champs " + champ + " - Mauvaise valeur '" + v + "'");
		return TypesCanauxListe.NR;
    }

}
