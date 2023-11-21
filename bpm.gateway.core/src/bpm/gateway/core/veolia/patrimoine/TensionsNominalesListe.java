//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.01.02 at 02:32:39 PM CET 
//


package bpm.gateway.core.veolia.patrimoine;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

import bpm.gateway.core.veolia.VEHelper;


/**
 * <p>Java class for tensionsNominalesListe.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="tensionsNominalesListe">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="12"/>
 *     &lt;enumeration value="24"/>
 *     &lt;enumeration value="48"/>
 *     &lt;enumeration value="120-130"/>
 *     &lt;enumeration value="230-240"/>
 *     &lt;enumeration value="380-400"/>
 *     &lt;enumeration value="400"/>
 *     &lt;enumeration value="420"/>
 *     &lt;enumeration value="500"/>
 *     &lt;enumeration value="660"/>
 *     &lt;enumeration value="1200"/>
 *     &lt;enumeration value="2000"/>
 *     &lt;enumeration value="3300"/>
 *     &lt;enumeration value="5500"/>
 *     &lt;enumeration value="10000"/>
 *     &lt;enumeration value="15000"/>
 *     &lt;enumeration value="20000"/>
 *     &lt;enumeration value="999999999"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "tensionsNominalesListe")
@XmlEnum
public enum TensionsNominalesListe {

    @XmlEnumValue("12")
    VALUE_1("12"),
    @XmlEnumValue("24")
    VALUE_2("24"),
    @XmlEnumValue("48")
    VALUE_3("48"),
    @XmlEnumValue("120-130")
    VALUE_4("120-130"),
    @XmlEnumValue("230-240")
    VALUE_5("230-240"),
    @XmlEnumValue("380-400")
    VALUE_6("380-400"),
    @XmlEnumValue("400")
    VALUE_7("400"),
    @XmlEnumValue("420")
    VALUE_8("420"),
    @XmlEnumValue("500")
    VALUE_9("500"),
    @XmlEnumValue("660")
    VALUE_10("660"),
    @XmlEnumValue("1200")
    VALUE_11("1200"),
    @XmlEnumValue("2000")
    VALUE_12("2000"),
    @XmlEnumValue("3300")
    VALUE_13("3300"),
    @XmlEnumValue("5500")
    VALUE_14("5500"),
    @XmlEnumValue("10000")
    VALUE_15("10000"),
    @XmlEnumValue("15000")
    VALUE_16("15000"),
    @XmlEnumValue("20000")
    VALUE_17("20000"),
    @XmlEnumValue("999999999")
    VALUE_18("999999999");
    private final String value;

    TensionsNominalesListe(String v) {
        value = v;
    }
	
    public static String getValue(String table, String champ, TensionsNominalesListe v, boolean required) {
    	if (required && v == null) {
    		throw new IllegalArgumentException("Table " + table + " - Champs " + champ + " - Valeur 'Null' ou non permise pour la liste.");
    	}
    	return v != null ? v.value : null;
    }
    
    public static TensionsNominalesListe fromValue(String table, String champ, String v) {
    	if (v == null) {
    		System.out.println("Table " + table + " - Champs " + champ + " - Valeur 'Null' non permise.");
    		return TensionsNominalesListe.VALUE_17;
    	}
    	
        for (TensionsNominalesListe c: TensionsNominalesListe.values()) {
        	if (VEHelper.checkEnum(c.value, v)) {
        		return c;
        	}
        }
		System.out.println("Table " + table + " - Champs " + champ + " - Mauvaise valeur '" + v + "'");
		return TensionsNominalesListe.VALUE_17;
    }

}
