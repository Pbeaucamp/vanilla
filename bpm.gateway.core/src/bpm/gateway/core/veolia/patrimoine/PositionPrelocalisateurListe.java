//
// Ce fichier a �t� g�n�r� par l'impl�mentation de r�f�rence JavaTM Architecture for XML Binding (JAXB), v2.2.8-b130911.1802 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apport�e � ce fichier sera perdue lors de la recompilation du sch�ma source. 
// G�n�r� le : 2020.01.29 � 03:15:22 PM CET 
//


package bpm.gateway.core.veolia.patrimoine;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

import bpm.gateway.core.veolia.VEHelper;


/**
 * <p>Classe Java pour PositionPrelocalisateurListe.
 * 
 * <p>Le fragment de sch�ma suivant indique le contenu attendu figurant dans cette classe.
 * <p>
 * <pre>
 * &lt;simpleType name="PositionPrelocalisateurListe">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Regard"/>
 *     &lt;enumeration value="Borne compacte"/>
 *     &lt;enumeration value="Sous bouche � cl�"/>
 *     &lt;enumeration value="NR"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "PositionPrelocalisateurListe")
@XmlEnum
public enum PositionPrelocalisateurListe {

    @XmlEnumValue("Regard")
    REGARD("Regard"),
    @XmlEnumValue("Borne compacte")
    BORNE_COMPACTE("Borne compacte"),
    @XmlEnumValue("Sous bouche \u00e0 cl\u00e9")
    SOUS_BOUCHE_A_CLE("Sous bouche \u00e0 cl\u00e9"),
    NR("NR");
    private final String value;

    PositionPrelocalisateurListe(String v) {
        value = v;
    }
	
    public static String getValue(String table, String champ, PositionPrelocalisateurListe v, boolean required) {
    	if (required && v == null) {
    		throw new IllegalArgumentException("Table " + table + " - Champs " + champ + " - Valeur 'Null' ou non permise pour la liste.");
    	}
    	return v != null ? v.value : null;
    }
    
    public static PositionPrelocalisateurListe fromValue(String table, String champ, String v) {
    	if (v == null) {
    		System.out.println("Table " + table + " - Champs " + champ + " - Valeur 'Null' non permise.");
    		return PositionPrelocalisateurListe.NR;
    	}
    	
        for (PositionPrelocalisateurListe c: PositionPrelocalisateurListe.values()) {
        	if (VEHelper.checkEnum(c.value, v)) {
        		return c;
        	}
        }
		System.out.println("Table " + table + " - Champs " + champ + " - Mauvaise valeur '" + v + "'");
		return PositionPrelocalisateurListe.NR;
    }

}
