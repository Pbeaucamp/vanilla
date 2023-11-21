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
 * <p>Java class for typesConvoyeursListe.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="typesConvoyeursListe">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="� bande"/>
 *     &lt;enumeration value="� vis"/>
 *     &lt;enumeration value="NR"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "typesConvoyeursListe")
@XmlEnum
public enum TypesConvoyeursListe {

    @XmlEnumValue("\u00c0 bande")
    A_BANDE("\u00c0 bande"),
    @XmlEnumValue("\u00c0 vis")
    A_VIS("\u00c0 vis"),
    NR("NR");
    private final String value;

    TypesConvoyeursListe(String v) {
        value = v;
    }
	
    public static String getValue(String table, String champ, TypesConvoyeursListe v) {
    	if (v == null) {
    		throw new IllegalArgumentException("Table " + table + " - Champs " + champ + " - Valeur 'Null' ou non permise pour la liste.");
    	}
    	return v.value;
    }
    
    public static TypesConvoyeursListe fromValue(String table, String champ, String v) {
    	if (v == null) {
    		System.out.println("Table " + table + " - Champs " + champ + " - Valeur 'Null' non permise.");
    		return TypesConvoyeursListe.NR;
    	}
    	
        for (TypesConvoyeursListe c: TypesConvoyeursListe.values()) {
        	if (VEHelper.checkEnum(c.value, v)) {
        		return c;
        	}
        }
		System.out.println("Table " + table + " - Champs " + champ + " - Mauvaise valeur '" + v + "'");
		return TypesConvoyeursListe.NR;
    }

}
