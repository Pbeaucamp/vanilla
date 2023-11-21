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
 * <p>Java class for typeEquipementCentrifugeuse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="typeEquipementCentrifugeuse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="eqptcentrifugeuse_eqptsocle_id" type="{}equipementIDType"/>
 *         &lt;element name="eqptcentrifugeuse_descriptif" type="{}stringRestricted3000Type"/>
 *         &lt;element name="eqptcentrifugeuse_puissance" type="{}decimalDeuxChiffres"/>
 *         &lt;element name="eqptcentrifugeuse_vitesserotation" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="eqptcentrifugeuse_pdseqpt" type="{}decimalDeuxChiffres"/>
 *         &lt;element name="eqptcentrifugeuse_capacitenominale" type="{}decimalDeuxChiffres"/>
 *         &lt;element name="eqptcentrifugeuse_diametre" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="eqptcentrifugeuse_pjmaintenance" type="{}stringRestricted3000Type" minOccurs="0"/>
 *         &lt;element name="eqptcentrifugeuse_pjfichetechnique" type="{}stringRestricted3000Type" minOccurs="0"/>
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
@Table (name = PatrimoineDAO.PAT_EQPT_CENTRIFUGEUSE)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "typeEquipementCentrifugeuse", propOrder = {
    "eqptcentrifugeuseEqptsocleId",
    "eqptcentrifugeuseDescriptif",
    "eqptcentrifugeusePuissance",
    "eqptcentrifugeuseVitesserotation",
    "eqptcentrifugeusePdseqpt",
    "eqptcentrifugeuseCapacitenominale",
    "eqptcentrifugeuseDiametre",
    "eqptcentrifugeusePjmaintenance",
    "eqptcentrifugeusePjfichetechnique",
    "eqptcentrifugeuseDaterenoupartiel"
})
public class TypeEquipementCentrifugeuse {

	@Id
	@Column(name = "eqptcentrifugeuse_id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@XmlTransient
    protected int id;
	@Transient
    @XmlElement(name = "eqptcentrifugeuse_eqptsocle_id", required = true)
    protected String eqptcentrifugeuseEqptsocleId;
	@Transient
    @XmlElement(name = "eqptcentrifugeuse_descriptif", required = true, defaultValue = "NR")
    protected String eqptcentrifugeuseDescriptif;
	@Transient
    @XmlElement(name = "eqptcentrifugeuse_puissance", required = true)
    protected BigDecimal eqptcentrifugeusePuissance;
	@Transient
    @XmlElement(name = "eqptcentrifugeuse_vitesserotation")
    protected Integer eqptcentrifugeuseVitesserotation;
	@Transient
    @XmlElement(name = "eqptcentrifugeuse_pdseqpt", required = true)
    protected BigDecimal eqptcentrifugeusePdseqpt;
	@Transient
    @XmlElement(name = "eqptcentrifugeuse_capacitenominale", required = true)
    protected BigDecimal eqptcentrifugeuseCapacitenominale;
	@Column(name = "eqptcentrifugeuse_diametre")
    @XmlElement(name = "eqptcentrifugeuse_diametre")
    protected Integer eqptcentrifugeuseDiametre;
	@Column(name = "eqptcentrifugeuse_pjmaintenance")
    @XmlElement(name = "eqptcentrifugeuse_pjmaintenance")
    protected String eqptcentrifugeusePjmaintenance;
	@Column(name = "eqptcentrifugeuse_pjfichetechnique")
    @XmlElement(name = "eqptcentrifugeuse_pjfichetechnique")
    protected String eqptcentrifugeusePjfichetechnique;
	@Transient
    @XmlElement(name = "eqptcentrifugeuse_daterenoupartiel")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar eqptcentrifugeuseDaterenoupartiel;
	@Column(name = "id_chg")
	@XmlTransient
	private Integer idChg;

    /**
     * Gets the value of the eqptcentrifugeuseEqptsocleId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptcentrifugeuse_eqptsocle_id")
    public String getEqptcentrifugeuseEqptsocleId() {
    	if (eqptcentrifugeuseEqptsocleId == null) {
    		manageNullException("eqptcentrifugeuse_eqptsocle_id");
    	}
        return eqptcentrifugeuseEqptsocleId;
    }

    /**
     * Sets the value of the eqptcentrifugeuseEqptsocleId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEqptcentrifugeuseEqptsocleId(String value) {
        this.eqptcentrifugeuseEqptsocleId = value;
    }

    /**
     * Gets the value of the eqptcentrifugeuseDescriptif property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptcentrifugeuse_descriptif")
    public String getEqptcentrifugeuseDescriptif() {
    	if (eqptcentrifugeuseDescriptif == null) {
    		manageNullException("eqptcentrifugeuse_descriptif");
    	}
        return eqptcentrifugeuseDescriptif;
    }

    /**
     * Sets the value of the eqptcentrifugeuseDescriptif property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEqptcentrifugeuseDescriptif(String value) {
        this.eqptcentrifugeuseDescriptif = value;
    }

    /**
     * Gets the value of the eqptcentrifugeusePuissance property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptcentrifugeuse_puissance")
    public BigDecimal getEqptcentrifugeusePuissance() {
    	if (eqptcentrifugeusePuissance == null) {
    		manageNullException("eqptcentrifugeuse_puissance");
    	}
        return eqptcentrifugeusePuissance;
    }

    /**
     * Sets the value of the eqptcentrifugeusePuissance property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setEqptcentrifugeusePuissance(BigDecimal value) {
        this.eqptcentrifugeusePuissance = value;
    }

    /**
     * Gets the value of the eqptcentrifugeuseVitesserotation property.
     * 
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptcentrifugeuse_vitesserotation")
    public int getEqptcentrifugeuseVitesserotation() {
    	if (eqptcentrifugeuseVitesserotation == null) {
    		manageNullException("eqptcentrifugeuse_vitesserotation");
    	}
        return eqptcentrifugeuseVitesserotation;
    }

    /**
     * Sets the value of the eqptcentrifugeuseVitesserotation property.
     * 
     */
    public void setEqptcentrifugeuseVitesserotation(int value) {
        this.eqptcentrifugeuseVitesserotation = value;
    }

    /**
     * Gets the value of the eqptcentrifugeusePdseqpt property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptcentrifugeuse_pdseqpt")
    public BigDecimal getEqptcentrifugeusePdseqpt() {
    	if (eqptcentrifugeusePdseqpt == null) {
    		manageNullException("eqptcentrifugeuse_pdseqpt");
    	}
        return eqptcentrifugeusePdseqpt;
    }

    /**
     * Sets the value of the eqptcentrifugeusePdseqpt property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setEqptcentrifugeusePdseqpt(BigDecimal value) {
        this.eqptcentrifugeusePdseqpt = value;
    }

    /**
     * Gets the value of the eqptcentrifugeuseCapacitenominale property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptcentrifugeuse_capacitenominale")
    public BigDecimal getEqptcentrifugeuseCapacitenominale() {
    	if (eqptcentrifugeuseCapacitenominale == null) {
    		manageNullException("eqptcentrifugeuse_capacitenominale");
    	}
        return eqptcentrifugeuseCapacitenominale;
    }

    /**
     * Sets the value of the eqptcentrifugeuseCapacitenominale property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setEqptcentrifugeuseCapacitenominale(BigDecimal value) {
        this.eqptcentrifugeuseCapacitenominale = value;
    }

    /**
     * Gets the value of the eqptcentrifugeuseDiametre property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getEqptcentrifugeuseDiametre() {
        return eqptcentrifugeuseDiametre;
    }

    /**
     * Sets the value of the eqptcentrifugeuseDiametre property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setEqptcentrifugeuseDiametre(Integer value) {
        this.eqptcentrifugeuseDiametre = value;
    }

    /**
     * Gets the value of the eqptcentrifugeusePjmaintenance property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEqptcentrifugeusePjmaintenance() {
        return eqptcentrifugeusePjmaintenance;
    }

    /**
     * Sets the value of the eqptcentrifugeusePjmaintenance property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEqptcentrifugeusePjmaintenance(String value) {
        this.eqptcentrifugeusePjmaintenance = value;
    }

    /**
     * Gets the value of the eqptcentrifugeusePjfichetechnique property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEqptcentrifugeusePjfichetechnique() {
        return eqptcentrifugeusePjfichetechnique;
    }

    /**
     * Sets the value of the eqptcentrifugeusePjfichetechnique property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEqptcentrifugeusePjfichetechnique(String value) {
        this.eqptcentrifugeusePjfichetechnique = value;
    }

	@Access(AccessType.PROPERTY)
	@Column(name = "eqptcentrifugeuse_daterenoupartiel")
    public Date getEqptcentrifugeuseDaterenoupartiel() {
        return VEHelper.toDate(eqptcentrifugeuseDaterenoupartiel);
    }

    public void setEqptcentrifugeuseDaterenoupartiel(XMLGregorianCalendar value) {
        this.eqptcentrifugeuseDaterenoupartiel = value;
    }

	public void setIdChg(Integer idChg) {
		this.idChg = idChg;
	}

    private void manageNullException(String champ) {
    	throw new IllegalArgumentException("Table " + PatrimoineDAO.PAT_EQPT_CENTRIFUGEUSE + " - Champs " + champ + " - Valeur 'Null' ou non permise par le XSD.");
	}

}
