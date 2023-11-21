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
 * Java class for modesPaiementListe.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * <p>
 * 
 * <pre>
 * &lt;simpleType name="modesPaiementListe">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Pr�l�vement"/>
 *     &lt;enumeration value="TIP DOM"/>
 *     &lt;enumeration value="TIP Ch�que"/>
 *     &lt;enumeration value="Mandat cash"/>
 *     &lt;enumeration value="Num�raire"/>
 *     &lt;enumeration value="CB"/>
 *     &lt;enumeration value="Ch�que"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "modesPaiementListe")
@XmlEnum
public enum ModesPaiementListe {

	@XmlEnumValue("Pr\u00e9l\u00e8vement")
	PRELEVEMENT("Pr\u00e9l\u00e8vement"), 
	@XmlEnumValue("TIP DOM")
	TIP_DOM("TIP DOM"), 
	@XmlEnumValue("TIP Ch\u00e8que")
	TIP_CHEQUE("TIP Ch\u00e8que"), 
	@XmlEnumValue("Mandat cash")
	MANDAT_CASH("Mandat cash"), 
	@XmlEnumValue("Num\u00e9raire")
	NUMERAIRE("Num\u00e9raire"), 
	CB("CB"), 
	@XmlEnumValue("Ch\u00e8que")
	CHEQUE("Ch\u00e8que"), 
    @XmlEnumValue("Virement bancaire")
    VIREMENT_BANCAIRE("Virement bancaire"),
	@XmlEnumValue("NR")
	NR("NR");
	private final String value;

	ModesPaiementListe(String v) {
		value = v;
	}
	
    public static String getValue(String table, String champ, ModesPaiementListe v) {
    	if (v == null) {
    		throw new IllegalArgumentException("Table " + table + " - Champs " + champ + " - Valeur 'Null' ou non permise pour la liste.");
    	}
    	return v.value;
    }

	public static ModesPaiementListe fromValue(String table, String champ, String v) {
    	if (v == null) {
    		System.out.println("Table " + table + " - Champs " + champ + " - Valeur 'Null' non permise.");
    		return ModesPaiementListe.NR;
    	}

		for (ModesPaiementListe c : ModesPaiementListe.values()) {
			if (VEHelper.checkEnum(c.value, v)) {
				return c;
			}
		}
		System.out.println("Table " + table + " - Champs " + champ + " - Mauvaise valeur '" + v + "'");
		return ModesPaiementListe.NR;
	}

}
