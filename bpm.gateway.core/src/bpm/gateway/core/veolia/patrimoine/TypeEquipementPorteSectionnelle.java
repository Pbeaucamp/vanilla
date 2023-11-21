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
 * <p>Java class for typeEquipementPorteSectionnelle complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="typeEquipementPorteSectionnelle">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="eqptportesection_eqptsocle_id" type="{}equipementIDType"/>
 *         &lt;element name="eqptportesection_descriptif" type="{}stringRestricted3000Type" minOccurs="0"/>
 *         &lt;element name="eqptportesection_electrique" type="{}ouiNonListe"/>
 *         &lt;element name="eqptportesection_largeur" type="{}decimalDeuxChiffres" minOccurs="0"/>
 *         &lt;element name="eqptportesection_hauteur" type="{}decimalDeuxChiffres" minOccurs="0"/>
 *         &lt;element name="eqptportesection_materiauconstituant" type="{}materiauxConstituantsListe" minOccurs="0"/>
 *         &lt;element name="eqptportesection_etat" type="{}etatsListe" minOccurs="0"/>
 *         &lt;element name="eqptportesection_dateconformitesecurite" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="eqptportesection_conformitesecurite" type="{}ouiNonListe" minOccurs="0"/>
 *         &lt;element name="eqptportesection_badge" type="{}ouiNonListe"/>
 *         &lt;element name="eqptportesection_digicode" type="{}ouiNonListe" minOccurs="0"/>
 *         &lt;element name="eqptportesection_puissance" type="{}decimalDeuxChiffres" minOccurs="0"/>
 *         &lt;element name="eqptportesection_pjmaintenance" type="{}stringRestricted3000Type" minOccurs="0"/>
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
@Table (name = PatrimoineDAO.PAT_EQPT_PORTESECTIONNELLE)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "typeEquipementPorteSectionnelle", propOrder = {
    "eqptportesectionEqptsocleId",
    "eqptportesectionDescriptif",
    "eqptportesectionElectrique",
    "eqptportesectionLargeur",
    "eqptportesectionHauteur",
    "eqptportesectionMateriauconstituant",
    "eqptportesectionEtat",
    "eqptportesectionDateconformitesecurite",
    "eqptportesectionConformitesecurite",
    "eqptportesectionBadge",
    "eqptportesectionDigicode",
    "eqptportesectionPuissance",
    "eqptportesectionPjmaintenance"
})
public class TypeEquipementPorteSectionnelle {

	@Id
	@Column(name = "eqptportesection_id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@XmlTransient
    protected int id;
	@Transient
    @XmlElement(name = "eqptportesection_eqptsocle_id", required = true)
    protected String eqptportesectionEqptsocleId;
	@Column(name = "eqptportesection_descriptif")
    @XmlElement(name = "eqptportesection_descriptif")
    protected String eqptportesectionDescriptif;
	@Transient
    @XmlElement(name = "eqptportesection_electrique", required = true)
    protected OuiNonListe eqptportesectionElectrique;
	@Column(name = "eqptportesection_largeur")
    @XmlElement(name = "eqptportesection_largeur")
    protected BigDecimal eqptportesectionLargeur;
	@Column(name = "eqptportesection_hauteur")
    @XmlElement(name = "eqptportesection_hauteur")
    protected BigDecimal eqptportesectionHauteur;
	@Transient
    @XmlElement(name = "eqptportesection_materiauconstituant")
    protected MateriauxConstituantsListe eqptportesectionMateriauconstituant;
	@Transient
    @XmlElement(name = "eqptportesection_etat")
    protected EtatsListe eqptportesectionEtat;
	@Transient
    @XmlElement(name = "eqptportesection_dateconformitesecurite")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar eqptportesectionDateconformitesecurite;
	@Transient
    @XmlElement(name = "eqptportesection_conformitesecurite")
    protected OuiNonListe eqptportesectionConformitesecurite;
	@Transient
    @XmlElement(name = "eqptportesection_badge", required = true)
    protected OuiNonListe eqptportesectionBadge;
	@Transient
    @XmlElement(name = "eqptportesection_digicode", required = true)
    protected OuiNonListe eqptportesectionDigicode;
	@Column(name = "eqptportesection_puissance")
    @XmlElement(name = "eqptportesection_puissance")
    protected BigDecimal eqptportesectionPuissance;
	@Column(name = "eqptportesection_pjmaintenance")
    @XmlElement(name = "eqptportesection_pjmaintenance")
    protected String eqptportesectionPjmaintenance;
	@Column(name = "id_chg")
	@XmlTransient
	private Integer idChg;

    /**
     * Gets the value of the eqptportesectionEqptsocleId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptportesection_eqptsocle_id")
    public String getEqptportesectionEqptsocleId() {
    	if (eqptportesectionEqptsocleId == null) {
    		manageNullException("eqptportesection_eqptsocle_id");
    	}
        return eqptportesectionEqptsocleId;
    }

    /**
     * Sets the value of the eqptportesectionEqptsocleId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEqptportesectionEqptsocleId(String value) {
        this.eqptportesectionEqptsocleId = value;
    }

    /**
     * Gets the value of the eqptportesectionDescriptif property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEqptportesectionDescriptif() {
        return eqptportesectionDescriptif;
    }

    /**
     * Sets the value of the eqptportesectionDescriptif property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEqptportesectionDescriptif(String value) {
        this.eqptportesectionDescriptif = value;
    }

    /**
     * Gets the value of the eqptportesectionElectrique property.
     * 
     * @return
     *     possible object is
     *     {@link OuiNonListe }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptportesection_electrique")
    public String getEqptportesectionElectrique() {
        return OuiNonListe.getValue(PatrimoineDAO.PAT_EQPT_PORTESECTIONNELLE, "eqptportesection_electrique", eqptportesectionElectrique, true);
    }

    /**
     * Sets the value of the eqptportesectionElectrique property.
     * 
     * @param value
     *     allowed object is
     *     {@link OuiNonListe }
     *     
     */
    public void setEqptportesectionElectrique(String value) {
        this.eqptportesectionElectrique = OuiNonListe.fromValue(PatrimoineDAO.PAT_EQPT_PORTESECTIONNELLE, "eqptportesection_electrique", value);
    }

    /**
     * Gets the value of the eqptportesectionLargeur property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getEqptportesectionLargeur() {
        return eqptportesectionLargeur;
    }

    /**
     * Sets the value of the eqptportesectionLargeur property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setEqptportesectionLargeur(BigDecimal value) {
        this.eqptportesectionLargeur = value;
    }

    /**
     * Gets the value of the eqptportesectionHauteur property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getEqptportesectionHauteur() {
        return eqptportesectionHauteur;
    }

    /**
     * Sets the value of the eqptportesectionHauteur property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setEqptportesectionHauteur(BigDecimal value) {
        this.eqptportesectionHauteur = value;
    }

    /**
     * Gets the value of the eqptportesectionMateriauconstituant property.
     * 
     * @return
     *     possible object is
     *     {@link MateriauxConstituantsListe }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptportesection_materiauconstituant")
    public String getEqptportesectionMateriauconstituant() {
        return MateriauxConstituantsListe.getValue(PatrimoineDAO.PAT_EQPT_PORTESECTIONNELLE, "eqptportesection_materiauconstituant", eqptportesectionMateriauconstituant, false);
    }

    /**
     * Sets the value of the eqptportesectionMateriauconstituant property.
     * 
     * @param value
     *     allowed object is
     *     {@link MateriauxConstituantsListe }
     *     
     */
    public void setEqptportesectionMateriauconstituant(String value) {
        this.eqptportesectionMateriauconstituant = MateriauxConstituantsListe.fromValue(PatrimoineDAO.PAT_EQPT_PORTESECTIONNELLE, "eqptportesection_materiauconstituant", value);
    }

    /**
     * Gets the value of the eqptportesectionEtat property.
     * 
     * @return
     *     possible object is
     *     {@link EtatsListe }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptportesection_etat")
    public String getEqptportesectionEtat() {
        return EtatsListe.getValue(PatrimoineDAO.PAT_EQPT_PORTESECTIONNELLE, "eqptportesection_etat", eqptportesectionEtat, false);
    }

    /**
     * Sets the value of the eqptportesectionEtat property.
     * 
     * @param value
     *     allowed object is
     *     {@link EtatsListe }
     *     
     */
    public void setEqptportesectionEtat(String value) {
        this.eqptportesectionEtat = EtatsListe.fromValue(PatrimoineDAO.PAT_EQPT_PORTESECTIONNELLE, "eqptportesection_etat", value);
    }

    /**
     * Gets the value of the eqptportesectionDateconformitesecurite property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptportesection_dateconformitesecurite")
    public Date getEqptportesectionDateconformitesecurite() {
        return VEHelper.toDate(eqptportesectionDateconformitesecurite);
    }

    /**
     * Sets the value of the eqptportesectionDateconformitesecurite property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setEqptportesectionDateconformitesecurite(XMLGregorianCalendar value) {
        this.eqptportesectionDateconformitesecurite = value;
    }

    /**
     * Gets the value of the eqptportesectionConformitesecurite property.
     * 
     * @return
     *     possible object is
     *     {@link OuiNonListe }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptportesection_conformitesecurite")
    public String getEqptportesectionConformitesecurite() {
        return OuiNonListe.getValue(PatrimoineDAO.PAT_EQPT_PORTESECTIONNELLE, "eqptportesection_conformitesecurite", eqptportesectionConformitesecurite, false);
    }

    /**
     * Sets the value of the eqptportesectionConformitesecurite property.
     * 
     * @param value
     *     allowed object is
     *     {@link OuiNonListe }
     *     
     */
    public void setEqptportesectionConformitesecurite(String value) {
        this.eqptportesectionConformitesecurite = OuiNonListe.fromValue(PatrimoineDAO.PAT_EQPT_PORTESECTIONNELLE, "eqptportesection_conformitesecurite", value);
    }

    /**
     * Gets the value of the eqptportesectionBadge property.
     * 
     * @return
     *     possible object is
     *     {@link OuiNonListe }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptportesection_badge")
    public String getEqptportesectionBadge() {
        return OuiNonListe.getValue(PatrimoineDAO.PAT_EQPT_PORTESECTIONNELLE, "eqptportesection_badge", eqptportesectionBadge, true);
    }

    /**
     * Sets the value of the eqptportesectionBadge property.
     * 
     * @param value
     *     allowed object is
     *     {@link OuiNonListe }
     *     
     */
    public void setEqptportesectionBadge(String value) {
        this.eqptportesectionBadge = OuiNonListe.fromValue(PatrimoineDAO.PAT_EQPT_PORTESECTIONNELLE, "eqptportesection_badge", value);
    }

    /**
     * Gets the value of the eqptportesectionDigicode property.
     * 
     * @return
     *     possible object is
     *     {@link OuiNonListe }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptportesection_digicode")
    public String getEqptportesectionDigicode() {
        return OuiNonListe.getValue(PatrimoineDAO.PAT_EQPT_PORTESECTIONNELLE, "eqptportesection_digicode", eqptportesectionDigicode, true);
    }

    /**
     * Sets the value of the eqptportesectionDigicode property.
     * 
     * @param value
     *     allowed object is
     *     {@link OuiNonListe }
     *     
     */
    public void setEqptportesectionDigicode(String value) {
        this.eqptportesectionDigicode = OuiNonListe.fromValue(PatrimoineDAO.PAT_EQPT_PORTESECTIONNELLE, "eqptportesection_digicode", value);
    }

    /**
     * Gets the value of the eqptportesectionPuissance property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getEqptportesectionPuissance() {
        return eqptportesectionPuissance;
    }

    /**
     * Sets the value of the eqptportesectionPuissance property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setEqptportesectionPuissance(BigDecimal value) {
        this.eqptportesectionPuissance = value;
    }

    /**
     * Gets the value of the eqptportesectionPjmaintenance property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEqptportesectionPjmaintenance() {
        return eqptportesectionPjmaintenance;
    }

    /**
     * Sets the value of the eqptportesectionPjmaintenance property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEqptportesectionPjmaintenance(String value) {
        this.eqptportesectionPjmaintenance = value;
    }

	public void setIdChg(Integer idChg) {
		this.idChg = idChg;
	}

    private void manageNullException(String champ) {
    	throw new IllegalArgumentException("Table " + PatrimoineDAO.PAT_EQPT_PORTESECTIONNELLE + " - Champs " + champ + " - Valeur 'Null' ou non permise par le XSD.");
	}

}
