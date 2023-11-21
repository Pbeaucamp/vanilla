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
 * <p>Classe Java pour positionsPointPrelevementListe.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * <p>
 * <pre>
 * &lt;simpleType name="positionsPointPrelevementListe">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Regard"/>
 *     &lt;enumeration value="Evier avec appareil portatif"/>
 *     &lt;enumeration value="NR"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "positionsPointPrelevementListe")
@XmlEnum
public enum PositionsPointPrelevementListe {

    @XmlEnumValue("Regard")
    REGARD("Regard"),
    @XmlEnumValue("Evier avec appareil portatif")
    EVIER_AVEC_APPAREIL_PORTATIF("Evier avec appareil portatif"),
    NR("NR");
    private final String value;

    PositionsPointPrelevementListe(String v) {
        value = v;
    }
	
    public static String getValue(String table, String champ, PositionsPointPrelevementListe v, boolean required) {
    	if (required && v == null) {
    		throw new IllegalArgumentException("Table " + table + " - Champs " + champ + " - Valeur 'Null' ou non permise pour la liste.");
    	}
    	return v != null ? v.value : null;
    }
    
    public static PositionsPointPrelevementListe fromValue(String table, String champ, String v) {
    	if (v == null) {
    		System.out.println("Table " + table + " - Champs " + champ + " - Valeur 'Null' non permise.");
    		return PositionsPointPrelevementListe.NR;
    	}
    	
        for (PositionsPointPrelevementListe c: PositionsPointPrelevementListe.values()) {
        	if (VEHelper.checkEnum(c.value, v)) {
        		return c;
        	}
        }
		System.out.println("Table " + table + " - Champs " + champ + " - Mauvaise valeur '" + v + "'");
		return PositionsPointPrelevementListe.NR;
    }

}
