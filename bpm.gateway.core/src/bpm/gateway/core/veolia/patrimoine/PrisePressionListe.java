//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.8-b130911.1802 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2019.04.23 à 03:25:45 PM CEST 
//


package bpm.gateway.core.veolia.patrimoine;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

import bpm.gateway.core.veolia.VEHelper;


/**
 * <p>Classe Java pour prisePressionListe.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * <p>
 * <pre>
 * &lt;simpleType name="prisePressionListe">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Amont"/>
 *     &lt;enumeration value="Aval"/>
 *     &lt;enumeration value="Amont/Aval"/>
 *     &lt;enumeration value="Non"/>
 *     &lt;enumeration value="NR"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "prisePressionListe")
@XmlEnum
public enum PrisePressionListe {

    @XmlEnumValue("Amont")
    AMONT("Amont"),
    @XmlEnumValue("Aval")
    AVAL("Aval"),
    @XmlEnumValue("Amont/Aval")
    AMONT_AVAL("Amont/Aval"),
    @XmlEnumValue("Non")
    NON("Non"),
    NR("NR");
    private final String value;

    PrisePressionListe(String v) {
        value = v;
    }
	
    public static String getValue(String table, String champ, PrisePressionListe v, boolean required) {
    	if (required && v == null) {
    		throw new IllegalArgumentException("Table " + table + " - Champs " + champ + " - Valeur 'Null' ou non permise pour la liste.");
    	}
    	return v != null ? v.value : null;
    }
    
    public static PrisePressionListe fromValue(String table, String champ, String v) {
    	if (v == null) {
    		System.out.println("Table " + table + " - Champs " + champ + " - Valeur 'Null' non permise.");
    		return PrisePressionListe.NR;
    	}
    	
        for (PrisePressionListe c: PrisePressionListe.values()) {
        	if (VEHelper.checkEnum(c.value, v)) {
        		return c;
        	}
        }
		System.out.println("Table " + table + " - Champs " + champ + " - Mauvaise valeur '" + v + "'");
		return PrisePressionListe.NR;
    }
}
