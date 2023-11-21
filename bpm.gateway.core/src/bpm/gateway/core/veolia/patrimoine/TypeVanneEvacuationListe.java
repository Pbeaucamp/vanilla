//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.8-b130911.1802 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2020.01.29 à 03:15:22 PM CET 
//


package bpm.gateway.core.veolia.patrimoine;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

import bpm.gateway.core.veolia.VEHelper;


/**
 * <p>Classe Java pour TypeVanneEvacuationListe.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * <p>
 * <pre>
 * &lt;simpleType name="TypeVanneEvacuationListe">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="murale"/>
 *     &lt;enumeration value="secteur"/>
 *     &lt;enumeration value="jet-creux"/>
 *     &lt;enumeration value="NR"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "TypeVanneEvacuationListe")
@XmlEnum
public enum TypeVanneEvacuationListe {

    @XmlEnumValue("murale")
    MURALE("murale"),
    @XmlEnumValue("secteur")
    SECTEUR("secteur"),
    @XmlEnumValue("jet-creux")
    JET_CREUX("jet-creux"),
    NR("NR");
    private final String value;

    TypeVanneEvacuationListe(String v) {
        value = v;
    }
	
    public static String getValue(String table, String champ, TypeVanneEvacuationListe v, boolean required) {
    	if (required && v == null) {
    		throw new IllegalArgumentException("Table " + table + " - Champs " + champ + " - Valeur 'Null' ou non permise pour la liste.");
    	}
    	return v != null ? v.value : null;
    }
    
    public static TypeVanneEvacuationListe fromValue(String table, String champ, String v) {
    	if (v == null) {
    		System.out.println("Table " + table + " - Champs " + champ + " - Valeur 'Null' non permise.");
    		return TypeVanneEvacuationListe.NR;
    	}
    	
        for (TypeVanneEvacuationListe c: TypeVanneEvacuationListe.values()) {
        	if (VEHelper.checkEnum(c.value, v)) {
        		return c;
        	}
        }
		System.out.println("Table " + table + " - Champs " + champ + " - Mauvaise valeur '" + v + "'");
		return TypeVanneEvacuationListe.NR;
    }

}
