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
 * <p>Java class for fonctionsCablesListe.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="fonctionsCablesListe">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Commande BT"/>
 *     &lt;enumeration value="Courant faible"/>
 *     &lt;enumeration value="Puissance BT"/>
 *     &lt;enumeration value="Puissance HT"/>
 *     &lt;enumeration value="NR"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "fonctionsCablesListe")
@XmlEnum
public enum FonctionsCablesListe {

    @XmlEnumValue("Commande BT")
    COMMANDE_BT("Commande BT"),
    @XmlEnumValue("Courant faible")
    COURANT_FAIBLE("Courant faible"),
    @XmlEnumValue("Puissance BT")
    PUISSANCE_BT("Puissance BT"),
    @XmlEnumValue("Puissance HT")
    PUISSANCE_HT("Puissance HT"),
    NR("NR");
    private final String value;

    FonctionsCablesListe(String v) {
        value = v;
    }
	
    public static String getValue(String table, String champ, FonctionsCablesListe v) {
    	if (v == null) {
    		throw new IllegalArgumentException("Table " + table + " - Champs " + champ + " - Valeur 'Null' ou non permise pour la liste.");
    	}
    	return v.value;
    }

    public static FonctionsCablesListe fromValue(String table, String champ, String v) {
    	if (v == null) {
    		System.out.println("Table " + table + " - Champs " + champ + " - Valeur 'Null' non permise.");
    		return FonctionsCablesListe.NR;
    	}
    	
        for (FonctionsCablesListe c: FonctionsCablesListe.values()) {
        	if (VEHelper.checkEnum(c.value, v)) {
        		return c;
        	}
        }
		System.out.println("Table " + table + " - Champs " + champ + " - Mauvaise valeur '" + v + "'");
		return FonctionsCablesListe.NR;
    }

}
