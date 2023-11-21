//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.8-b130911.1802 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2019.04.23 à 03:25:45 PM CEST 
//


package bpm.gateway.core.veolia.patrimoine;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour typeEquipementFiltreY complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="typeEquipementFiltreY">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="eqptfiltrey_eqptsocle_id" type="{}equipementIDType"/>
 *         &lt;element name="eqptfiltrey_descriptif" type="{}stringRestricted3000Type"/>
 *         &lt;element name="eqptfiltrey_diametre" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="eqptfiltrey_pressionnominale" type="{}pressionsNominalesListe"/>
 *         &lt;element name="eqptfiltrey_mailletamis" type="{}stringRestricted255Type" minOccurs="0"/>
 *         &lt;element name="eqptfiltrey_dimension" type="{}stringRestricted255Type" minOccurs="0"/>
 *         &lt;element name="eqptfiltrey_kitpurge" type="{}ouiNonListe" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@Entity
@Access(AccessType.FIELD)
@Table (name = PatrimoineDAO.PAT_EQPT_FILTRE_Y)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "typeEquipementFiltreY", propOrder = {
    "eqptfiltreyEqptsocleId",
    "eqptfiltreyDescriptif",
    "eqptfiltreyDiametre",
    "eqptfiltreyPressionnominale",
    "eqptfiltreyMailletamis",
    "eqptfiltreyDimension",
    "eqptfiltreyKitpurge"
})
public class TypeEquipementFiltreY {

	@Id
	@Column(name = "eqptfiltrey_id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@XmlTransient
    protected int id;
	@Transient
    @XmlElement(name = "eqptfiltrey_eqptsocle_id", required = true)
    protected String eqptfiltreyEqptsocleId;
	@Transient
    @XmlElement(name = "eqptfiltrey_descriptif", required = true, defaultValue = "NR")
    protected String eqptfiltreyDescriptif;
	@Transient
    @XmlElement(name = "eqptfiltrey_diametre")
    protected Integer eqptfiltreyDiametre;
	@Transient
    @XmlElement(name = "eqptfiltrey_pressionnominale", required = true)
    @XmlSchemaType(name = "string")
    protected PressionsNominalesListe eqptfiltreyPressionnominale;
	@Column(name = "eqptfiltrey_mailletamis")
    @XmlElement(name = "eqptfiltrey_mailletamis")
    protected String eqptfiltreyMailletamis;
	@Column(name = "eqptfiltrey_dimension")
    @XmlElement(name = "eqptfiltrey_dimension")
    protected String eqptfiltreyDimension;
	@Transient
    @XmlElement(name = "eqptfiltrey_kitpurge")
    @XmlSchemaType(name = "string")
    protected OuiNonListe eqptfiltreyKitpurge;
	@Column(name = "id_chg")
	@XmlTransient
	private Integer idChg;

    /**
     * Obtient la valeur de la propriété eqptfiltreyEqptsocleId.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptfiltrey_eqptsocle_id")
    public String getEqptfiltreyEqptsocleId() {
    	if (eqptfiltreyEqptsocleId == null) {
    		manageNullException("eqptfiltrey_eqptsocle_id");
    	}
        return eqptfiltreyEqptsocleId;
    }

    /**
     * Définit la valeur de la propriété eqptfiltreyEqptsocleId.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEqptfiltreyEqptsocleId(String value) {
        this.eqptfiltreyEqptsocleId = value;
    }

    /**
     * Obtient la valeur de la propriété eqptfiltreyDescriptif.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptfiltrey_descriptif")
    public String getEqptfiltreyDescriptif() {
		if (eqptfiltreyDescriptif == null) {
    		manageNullException("eqptfiltrey_descriptif");
    	}
        return eqptfiltreyDescriptif;
    }

    /**
     * Définit la valeur de la propriété eqptfiltreyDescriptif.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEqptfiltreyDescriptif(String value) {
        this.eqptfiltreyDescriptif = value;
    }

    /**
     * Obtient la valeur de la propriété eqptfiltreyDiametre.
     * 
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptfiltrey_diametre")
    public Integer getEqptfiltreyDiametre() {
		if (eqptfiltreyDiametre == null) {
    		manageNullException("eqptfiltrey_diametre");
    	}
        return eqptfiltreyDiametre;
    }

    /**
     * Définit la valeur de la propriété eqptfiltreyDiametre.
     * 
     */
    public void setEqptfiltreyDiametre(Integer value) {
        this.eqptfiltreyDiametre = value;
    }

    /**
     * Obtient la valeur de la propriété eqptfiltreyPressionnominale.
     * 
     * @return
     *     possible object is
     *     {@link PressionsNominalesListe }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptfiltrey_pressionnominale")
    public String getEqptfiltreyPressionnominale() {
        return PressionsNominalesListe.getValue(PatrimoineDAO.PAT_EQPT_FILTRE_Y, "eqptfiltrey_pressionnominale", eqptfiltreyPressionnominale, true);
    }

    /**
     * Définit la valeur de la propriété eqptfiltreyPressionnominale.
     * 
     * @param value
     *     allowed object is
     *     {@link PressionsNominalesListe }
     *     
     */
    public void setEqptfiltreyPressionnominale(String value) {
        this.eqptfiltreyPressionnominale = PressionsNominalesListe.fromValue(PatrimoineDAO.PAT_EQPT_FILTRE_Y, "eqptfiltrey_pressionnominale", value);
    }

    /**
     * Obtient la valeur de la propriété eqptfiltreyMailletamis.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEqptfiltreyMailletamis() {
        return eqptfiltreyMailletamis;
    }

    /**
     * Définit la valeur de la propriété eqptfiltreyMailletamis.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEqptfiltreyMailletamis(String value) {
        this.eqptfiltreyMailletamis = value;
    }

    /**
     * Obtient la valeur de la propriété eqptfiltreyDimension.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEqptfiltreyDimension() {
        return eqptfiltreyDimension;
    }

    /**
     * Définit la valeur de la propriété eqptfiltreyDimension.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEqptfiltreyDimension(String value) {
        this.eqptfiltreyDimension = value;
    }

    /**
     * Obtient la valeur de la propriété eqptfiltreyKitpurge.
     * 
     * @return
     *     possible object is
     *     {@link OuiNonListe }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptfiltrey_kitpurge")
    public String getEqptfiltreyKitpurge() {
        return OuiNonListe.getValue(PatrimoineDAO.PAT_EQPT_FILTRE_Y, "eqptfiltrey_kitpurge", eqptfiltreyKitpurge, false);
    }

    /**
     * Définit la valeur de la propriété eqptfiltreyKitpurge.
     * 
     * @param value
     *     allowed object is
     *     {@link OuiNonListe }
     *     
     */
    public void setEqptfiltreyKitpurge(String value) {
        this.eqptfiltreyKitpurge = OuiNonListe.fromValue(PatrimoineDAO.PAT_EQPT_FILTRE_Y, "eqptfiltrey_kitpurge", value);
    }

	public void setIdChg(Integer idChg) {
		this.idChg = idChg;
	}

    private void manageNullException(String champ) {
    	throw new IllegalArgumentException("Table " + PatrimoineDAO.PAT_EQPT_FILTRE_Y + " - Champs " + champ + " - Valeur 'Null' ou non permise par le XSD.");
	}
}
