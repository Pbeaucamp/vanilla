//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.01.02 at 10:47:34 AM CET 
//


package bpm.gateway.core.veolia.patrimoine;

import java.math.BigDecimal;

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
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for typeEquipementDestructeurOzone complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="typeEquipementDestructeurOzone">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="eqptdestozone_eqptsocle_id" type="{}equipementIDType"/>
 *         &lt;element name="eqptdestozone_descriptif" type="{}stringRestricted3000Type"/>
 *         &lt;element name="eqptdestozone_debitnominal" type="{}decimalDeuxChiffres"/>
 *         &lt;element name="eqptdestozone_tensionnominale" type="{}tensionsNominalesListe"/>
 *         &lt;element name="eqptdestozone_typedestructeur" type="{}typesDestructeursOzoneListe"/>
 *         &lt;element name="eqptdestozone_puissance" type="{}decimalDeuxChiffres"/>
 *         &lt;element name="eqptdestozone_pjmaintenance" type="{}decimalDeuxChiffres" minOccurs="0"/>
 *         &lt;element name="eqptdestozone_pjfichetechnique" type="{}stringRestricted3000Type" minOccurs="0"/>
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
@Table (name = PatrimoineDAO.PAT_EQPT_DESTRUCTEUROZONE)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "typeEquipementDestructeurOzone", propOrder = {
    "eqptdestozoneEqptsocleId",
    "eqptdestozoneDescriptif",
    "eqptdestozoneDebitnominal",
    "eqptdestozoneTensionnominale",
    "eqptdestozoneTypedestructeur",
    "eqptdestozonePuissance",
    "eqptdestozonePjmaintenance",
    "eqptdestozonePjfichetechnique"
})
public class TypeEquipementDestructeurOzone {

	@Id
	@Column(name = "eqptdestozone_id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@XmlTransient
    protected int id;
	@Transient
    @XmlElement(name = "eqptdestozone_eqptsocle_id", required = true)
    protected String eqptdestozoneEqptsocleId;
	@Transient
    @XmlElement(name = "eqptdestozone_descriptif", required = true, defaultValue = "NR")
    protected String eqptdestozoneDescriptif;
	@Transient
    @XmlElement(name = "eqptdestozone_debitnominal", required = true)
    protected BigDecimal eqptdestozoneDebitnominal;
	@Transient
    @XmlElement(name = "eqptdestozone_tensionnominale", required = true)
    protected TensionsNominalesListe eqptdestozoneTensionnominale;
	@Transient
    @XmlElement(name = "eqptdestozone_typedestructeur", required = true)
    protected TypesDestructeursOzoneListe eqptdestozoneTypedestructeur;
	@Transient
    @XmlElement(name = "eqptdestozone_puissance", required = true)
    protected BigDecimal eqptdestozonePuissance;
	@Column(name = "eqptdestozone_pjmaintenance")
    @XmlElement(name = "eqptdestozone_pjmaintenance")
    protected BigDecimal eqptdestozonePjmaintenance;
	@Column(name = "eqptdestozone_pjfichetechnique")
    @XmlElement(name = "eqptdestozone_pjfichetechnique")
    protected String eqptdestozonePjfichetechnique;
	@Column(name = "id_chg")
	@XmlTransient
	private Integer idChg;

    /**
     * Gets the value of the eqptdestozoneEqptsocleId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptdestozone_eqptsocle_id")
    public String getEqptdestozoneEqptsocleId() {
    	if (eqptdestozoneEqptsocleId == null) {
    		manageNullException("eqptdestozone_eqptsocle_id");
    	}
        return eqptdestozoneEqptsocleId;
    }

    /**
     * Sets the value of the eqptdestozoneEqptsocleId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEqptdestozoneEqptsocleId(String value) {
        this.eqptdestozoneEqptsocleId = value;
    }

    /**
     * Gets the value of the eqptdestozoneDescriptif property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptdestozone_descriptif")
    public String getEqptdestozoneDescriptif() {
    	if (eqptdestozoneDescriptif == null) {
    		manageNullException("eqptdestozone_descriptif");
    	}
        return eqptdestozoneDescriptif;
    }

    /**
     * Sets the value of the eqptdestozoneDescriptif property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEqptdestozoneDescriptif(String value) {
        this.eqptdestozoneDescriptif = value;
    }

    /**
     * Gets the value of the eqptdestozoneDebitnominal property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptdestozone_debitnominal")
    public BigDecimal getEqptdestozoneDebitnominal() {
    	if (eqptdestozoneDebitnominal == null) {
    		manageNullException("eqptdestozone_debitnominal");
    	}
        return eqptdestozoneDebitnominal;
    }

    /**
     * Sets the value of the eqptdestozoneDebitnominal property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setEqptdestozoneDebitnominal(BigDecimal value) {
        this.eqptdestozoneDebitnominal = value;
    }

    /**
     * Gets the value of the eqptdestozoneTensionnominale property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptdestozone_tensionnominale")
    public String getEqptdestozoneTensionnominale() {
        return TensionsNominalesListe.getValue(PatrimoineDAO.PAT_EQPT_DESTRUCTEUROZONE, "eqptdestozone_tensionnominale", eqptdestozoneTensionnominale, true);
    }

    /**
     * Sets the value of the eqptdestozoneTensionnominale property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEqptdestozoneTensionnominale(String value) {
        this.eqptdestozoneTensionnominale = TensionsNominalesListe.fromValue(PatrimoineDAO.PAT_EQPT_DESTRUCTEUROZONE, "eqptdestozone_tensionnominale", value);
    }

    /**
     * Gets the value of the eqptdestozoneTypedestructeur property.
     * 
     * @return
     *     possible object is
     *     {@link TypesDestructeursOzoneListe }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptdestozone_typedestructeur")
    public String getEqptdestozoneTypedestructeur() {
        return TypesDestructeursOzoneListe.getValue(PatrimoineDAO.PAT_EQPT_DESTRUCTEUROZONE, "eqptdestozone_typedestructeur", eqptdestozoneTypedestructeur);
    }

    /**
     * Sets the value of the eqptdestozoneTypedestructeur property.
     * 
     * @param value
     *     allowed object is
     *     {@link TypesDestructeursOzoneListe }
     *     
     */
    public void setEqptdestozoneTypedestructeur(String value) {
        this.eqptdestozoneTypedestructeur = TypesDestructeursOzoneListe.fromValue(PatrimoineDAO.PAT_EQPT_DESTRUCTEUROZONE, "eqptdestozone_typedestructeur", value);
    }

    /**
     * Gets the value of the eqptdestozonePuissance property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptdestozone_puissance")
    public BigDecimal getEqptdestozonePuissance() {
    	if (eqptdestozonePuissance == null) {
    		manageNullException("eqptdestozone_puissance");
    	}
        return eqptdestozonePuissance;
    }

    /**
     * Sets the value of the eqptdestozonePuissance property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setEqptdestozonePuissance(BigDecimal value) {
        this.eqptdestozonePuissance = value;
    }

    /**
     * Gets the value of the eqptdestozonePjmaintenance property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getEqptdestozonePjmaintenance() {
        return eqptdestozonePjmaintenance;
    }

    /**
     * Sets the value of the eqptdestozonePjmaintenance property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setEqptdestozonePjmaintenance(BigDecimal value) {
        this.eqptdestozonePjmaintenance = value;
    }

    /**
     * Gets the value of the eqptdestozonePjfichetechnique property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEqptdestozonePjfichetechnique() {
        return eqptdestozonePjfichetechnique;
    }

    /**
     * Sets the value of the eqptdestozonePjfichetechnique property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEqptdestozonePjfichetechnique(String value) {
        this.eqptdestozonePjfichetechnique = value;
    }

	public void setIdChg(Integer idChg) {
		this.idChg = idChg;
	}

    private void manageNullException(String champ) {
    	throw new IllegalArgumentException("Table " + PatrimoineDAO.PAT_EQPT_DESTRUCTEUROZONE + " - Champs " + champ + " - Valeur 'Null' ou non permise par le XSD.");
	}

}
