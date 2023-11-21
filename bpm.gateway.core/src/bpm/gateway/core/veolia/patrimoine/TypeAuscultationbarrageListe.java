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
 * <p>Classe Java pour TypeAuscultationbarrageListe.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * <p>
 * <pre>
 * &lt;simpleType name="TypeAuscultationbarrageListe">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Comparateur"/>
 *     &lt;enumeration value="Extensomètre"/>
 *     &lt;enumeration value="Repère topo"/>
 *     &lt;enumeration value="Vinchon"/>
 *     &lt;enumeration value="Fissuromètre"/>
 *     &lt;enumeration value="Drain"/>
 *     &lt;enumeration value="Appareil topographique"/>
 *     &lt;enumeration value="Piézomètre"/>
 *     &lt;enumeration value="Pied à coulisse"/>
 *     &lt;enumeration value="NR"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "TypeAuscultationbarrageListe")
@XmlEnum
public enum TypeAuscultationbarrageListe {

    @XmlEnumValue("Comparateur")
    COMPARATEUR("Comparateur"),
    @XmlEnumValue("Extensom\u00e8tre")
    EXTENSOMETRE("Extensom\u00e8tre"),
    @XmlEnumValue("Rep\u00e8re topo")
    REPERE_TOPO("Rep\u00e8re topo"),
    @XmlEnumValue("Vinchon")
    VINCHON("Vinchon"),
    @XmlEnumValue("Fissurom\u00e8tre")
    FISSUROMETRE("Fissurom\u00e8tre"),
    @XmlEnumValue("Drain")
    DRAIN("Drain"),
    @XmlEnumValue("Appareil topographique")
    APPAREIL_TOPOGRAPHIQUE("Appareil topographique"),
    @XmlEnumValue("Pi\u00e9zom\u00e8tre")
    PIEZOMETRE("Pi\u00e9zom\u00e8tre"),
    @XmlEnumValue("Pied \u00e0 coulisse")
    PIED_A_COULISSE("Pied \u00e0 coulisse"),
    NR("NR");
    private final String value;

    TypeAuscultationbarrageListe(String v) {
        value = v;
    }
	
    public static String getValue(String table, String champ, TypeAuscultationbarrageListe v, boolean required) {
    	if (required && v == null) {
    		throw new IllegalArgumentException("Table " + table + " - Champs " + champ + " - Valeur 'Null' ou non permise pour la liste.");
    	}
    	return v != null ? v.value : null;
    }
    
    public static TypeAuscultationbarrageListe fromValue(String table, String champ, String v) {
    	if (v == null) {
    		System.out.println("Table " + table + " - Champs " + champ + " - Valeur 'Null' non permise.");
    		return TypeAuscultationbarrageListe.NR;
    	}
    	
        for (TypeAuscultationbarrageListe c: TypeAuscultationbarrageListe.values()) {
        	if (VEHelper.checkEnum(c.value, v)) {
        		return c;
        	}
        }
		System.out.println("Table " + table + " - Champs " + champ + " - Mauvaise valeur '" + v + "'");
		return TypeAuscultationbarrageListe.NR;
    }

}
