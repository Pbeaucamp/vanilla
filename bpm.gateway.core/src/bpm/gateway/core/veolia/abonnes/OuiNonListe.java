//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.08.04 at 11:03:05 AM CEST 
//

package bpm.gateway.core.veolia.abonnes;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

import bpm.gateway.core.veolia.VEHelper;

/**
 * <p>
 * Java class for ouiNonListe.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * <p>
 * 
 * <pre>
 * &lt;simpleType name="ouiNonListe">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Oui"/>
 *     &lt;enumeration value="Non"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ouiNonListe")
@XmlEnum
public enum OuiNonListe {

	@XmlEnumValue("Oui")
	OUI("Oui"), 
	@XmlEnumValue("Non")
	NON("Non"), 
	@XmlEnumValue("NR")
	NR("NR");
	private final String value;

	OuiNonListe(String v) {
		value = v;
	}
	
    public static String getValue(String table, String champ, OuiNonListe v, boolean required) {
    	if (required && v == null) {
    		throw new IllegalArgumentException("Table " + table + " - Champs " + champ + " - Valeur 'Null' ou non permise pour la liste.");
    	}
    	return v != null ? v.value : null;
    }

	public static OuiNonListe fromValue(String table, String champ, String v, boolean required) {
    	if (required && v == null) {
    		System.out.println("Table " + table + " - Champs " + champ + " - Valeur 'Null' non permise.");
    		return OuiNonListe.NR;
    	}
    	
    	if (v == null) {
    		return null;
    	}

		for (OuiNonListe c : OuiNonListe.values()) {
			if (VEHelper.checkEnum(c.value, v)) {
				return c;
			}
		}
		System.out.println("Table " + table + " - Champs " + champ + " - Mauvaise valeur '" + v + "'");
		return OuiNonListe.NR;
	}

}
