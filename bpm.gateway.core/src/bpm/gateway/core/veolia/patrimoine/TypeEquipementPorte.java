//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.01.02 at 10:47:34 AM CET 
//


package bpm.gateway.core.veolia.patrimoine;

import java.math.BigDecimal;
import java.util.Date;

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
import javax.xml.datatype.XMLGregorianCalendar;

import bpm.gateway.core.veolia.VEHelper;


/**
 * <p>Java class for typeEquipementPorte complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="typeEquipementPorte">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="eqptporte_eqptsocle_id" type="{}equipementIDType"/>
 *         &lt;element name="eqptporte_descriptif" type="{}stringRestricted3000Type" minOccurs="0"/>
 *         &lt;element name="eqptporte_largeur" type="{}decimalDeuxChiffres" minOccurs="0"/>
 *         &lt;element name="eqptporte_hauteur" type="{}decimalDeuxChiffres" minOccurs="0"/>
 *         &lt;element name="eqptporte_materiauconstituant" type="{}materiauxConstituantsListe" minOccurs="0"/>
 *         &lt;element name="eqptporte_dateconformitesecurite" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="eqptporte_conformitesecurite" type="{}ouiNonListe" minOccurs="0"/>
 *         &lt;element name="eqptporte_badge" type="{}ouiNonListe"/>
 *         &lt;element name="eqptporte_digicode" type="{}ouiNonListe"/>
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
@Table (name = PatrimoineDAO.PAT_EQPT_PORTE)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "typeEquipementPorte", propOrder = {
    "eqptporteEqptsocleId",
    "eqptporteDescriptif",
    "eqptporteLargeur",
    "eqptporteHauteur",
    "eqptporteMateriauconstituant",
    "eqptporteDateconformitesecurite",
    "eqptporteConformitesecurite",
    "eqptporteBadge",
    "eqptporteDigicode"
})
public class TypeEquipementPorte {

	@Id
	@Column(name = "eqptporte_id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@XmlTransient
    protected int id;
	@Transient
    @XmlElement(name = "eqptporte_eqptsocle_id", required = true)
    protected String eqptporteEqptsocleId;
	@Column(name = "eqptporte_descriptif")
    @XmlElement(name = "eqptporte_descriptif")
    protected String eqptporteDescriptif;
	@Column(name = "eqptporte_largeur")
    @XmlElement(name = "eqptporte_largeur")
    protected BigDecimal eqptporteLargeur;
	@Column(name = "eqptporte_hauteur")
    @XmlElement(name = "eqptporte_hauteur")
    protected BigDecimal eqptporteHauteur;
	@Transient
    @XmlElement(name = "eqptporte_materiauconstituant")
    protected MateriauxConstituantsListe eqptporteMateriauconstituant;
	@Transient
    @XmlElement(name = "eqptporte_dateconformitesecurite")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar eqptporteDateconformitesecurite;
	@Transient
    @XmlElement(name = "eqptporte_conformitesecurite")
    protected OuiNonListe eqptporteConformitesecurite;
	@Transient
    @XmlElement(name = "eqptporte_badge", required = true)
    protected OuiNonListe eqptporteBadge;
	@Transient
    @XmlElement(name = "eqptporte_digicode", required = true)
    protected OuiNonListe eqptporteDigicode;
	@Column(name = "id_chg")
	@XmlTransient
	private Integer idChg;

    /**
     * Gets the value of the eqptporteEqptsocleId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptporte_eqptsocle_id")
    public String getEqptporteEqptsocleId() {
    	if (eqptporteEqptsocleId == null) {
    		manageNullException("eqptporte_eqptsocle_id");
    	}
        return eqptporteEqptsocleId;
    }

    /**
     * Sets the value of the eqptporteEqptsocleId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEqptporteEqptsocleId(String value) {
        this.eqptporteEqptsocleId = value;
    }

    /**
     * Gets the value of the eqptporteDescriptif property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEqptporteDescriptif() {
        return eqptporteDescriptif;
    }

    /**
     * Sets the value of the eqptporteDescriptif property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEqptporteDescriptif(String value) {
        this.eqptporteDescriptif = value;
    }

    /**
     * Gets the value of the eqptporteLargeur property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getEqptporteLargeur() {
        return eqptporteLargeur;
    }

    /**
     * Sets the value of the eqptporteLargeur property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setEqptporteLargeur(BigDecimal value) {
        this.eqptporteLargeur = value;
    }

    /**
     * Gets the value of the eqptporteHauteur property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getEqptporteHauteur() {
        return eqptporteHauteur;
    }

    /**
     * Sets the value of the eqptporteHauteur property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setEqptporteHauteur(BigDecimal value) {
        this.eqptporteHauteur = value;
    }

    /**
     * Gets the value of the eqptporteMateriauconstituant property.
     * 
     * @return
     *     possible object is
     *     {@link MateriauxConstituantsListe }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptporte_materiauconstituant")
    public String getEqptporteMateriauconstituant() {
        return MateriauxConstituantsListe.getValue(PatrimoineDAO.PAT_EQPT_PORTE, "eqptporte_materiauconstituant", eqptporteMateriauconstituant, false);
    }

    /**
     * Sets the value of the eqptporteMateriauconstituant property.
     * 
     * @param value
     *     allowed object is
     *     {@link MateriauxConstituantsListe }
     *     
     */
    public void setEqptporteMateriauconstituant(String value) {
        this.eqptporteMateriauconstituant = MateriauxConstituantsListe.fromValue(PatrimoineDAO.PAT_EQPT_PORTE, "eqptporte_materiauconstituant", value);
    }

    /**
     * Gets the value of the eqptporteDateconformitesecurite property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptporte_dateconformitesecurite")
    public Date getEqptporteDateconformitesecurite() {
        return VEHelper.toDate(eqptporteDateconformitesecurite);
    }

    /**
     * Sets the value of the eqptporteDateconformitesecurite property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setEqptporteDateconformitesecurite(XMLGregorianCalendar value) {
        this.eqptporteDateconformitesecurite = value;
    }

    /**
     * Gets the value of the eqptporteConformitesecurite property.
     * 
     * @return
     *     possible object is
     *     {@link OuiNonListe }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptporte_conformitesecurite")
    public String getEqptporteConformitesecurite() {
        return OuiNonListe.getValue(PatrimoineDAO.PAT_EQPT_PORTE, "eqptporte_conformitesecurite", eqptporteConformitesecurite, false);
    }

    /**
     * Sets the value of the eqptporteConformitesecurite property.
     * 
     * @param value
     *     allowed object is
     *     {@link OuiNonListe }
     *     
     */
    public void setEqptporteConformitesecurite(String value) {
        this.eqptporteConformitesecurite = OuiNonListe.fromValue(PatrimoineDAO.PAT_EQPT_PORTE, "eqptporte_conformitesecurite", value);
    }

    /**
     * Gets the value of the eqptporteBadge property.
     * 
     * @return
     *     possible object is
     *     {@link OuiNonListe }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptporte_badge")
    public String getEqptporteBadge() {
        return OuiNonListe.getValue(PatrimoineDAO.PAT_EQPT_PORTE, "eqptporte_badge", eqptporteBadge, true);
    }

    /**
     * Sets the value of the eqptporteBadge property.
     * 
     * @param value
     *     allowed object is
     *     {@link OuiNonListe }
     *     
     */
    public void setEqptporteBadge(String value) {
        this.eqptporteBadge = OuiNonListe.fromValue(PatrimoineDAO.PAT_EQPT_PORTE, "eqptporte_badge", value);
    }

    /**
     * Gets the value of the eqptporteDigicode property.
     * 
     * @return
     *     possible object is
     *     {@link OuiNonListe }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptporte_digicode")
    public String getEqptporteDigicode() {
        return OuiNonListe.getValue(PatrimoineDAO.PAT_EQPT_PORTE, "eqptporte_digicode", eqptporteDigicode, true);
    }

    /**
     * Sets the value of the eqptporteDigicode property.
     * 
     * @param value
     *     allowed object is
     *     {@link OuiNonListe }
     *     
     */
    public void setEqptporteDigicode(String value) {
        this.eqptporteDigicode = OuiNonListe.fromValue(PatrimoineDAO.PAT_EQPT_PORTE, "eqptporte_digicode", value);
    }

	public void setIdChg(Integer idChg) {
		this.idChg = idChg;
	}

    private void manageNullException(String champ) {
    	throw new IllegalArgumentException("Table " + PatrimoineDAO.PAT_EQPT_PORTE + " - Champs " + champ + " - Valeur 'Null' ou non permise par le XSD.");
	}

}
