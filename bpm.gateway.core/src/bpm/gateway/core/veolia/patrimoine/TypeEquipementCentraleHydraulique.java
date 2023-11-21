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
 * <p>Java class for typeEquipementCentraleHydraulique complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="typeEquipementCentraleHydraulique">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="eqptcentralehydrau_eqptsocle_id" type="{}equipementIDType"/>
 *         &lt;element name="eqptcentralehydrau_descriptif" type="{}stringRestricted3000Type"/>
 *         &lt;element name="eqptcentralehydrau_pressionnominale" type="{}pressionsNominalesListe" minOccurs="0"/>
 *         &lt;element name="eqptcentralehydrau_puissance" type="{}decimalDeuxChiffres"/>
 *         &lt;element name="eqptcentralehydrau_tensionnominale" type="{}tensionsNominalesListe" minOccurs="0"/>
 *         &lt;element name="eqptcentralehydrau_bacretention" type="{}ouiNonListe"/>
 *         &lt;element name="eqptcentralehydrau_capacitecuve" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="eqptcentralehydrau_volume" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="eqptcentralehydrau_accuhydpressionnominale" type="{}decimalDeuxChiffres"/>
 *         &lt;element name="eqptcentralehydrau_accuhydpressionepreuve" type="{}decimalDeuxChiffres" minOccurs="1"/>
 *         &lt;element name="eqptcentralehydrau_pjmaintenance" type="{}stringRestricted3000Type" minOccurs="0"/>
 *         &lt;element name="eqptcentralehydrau_pjfichetechnique" type="{}stringRestricted3000Type" minOccurs="1"/>
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
@Table (name = PatrimoineDAO.PAT_EQPT_CENTRALEHYDRAULIQUE)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "typeEquipementCentraleHydraulique", propOrder = {
    "eqptcentralehydrauEqptsocleId",
    "eqptcentralehydrauDescriptif",
    "eqptcentralehydrauPressionnominale",
    "eqptcentralehydrauPuissance",
    "eqptcentralehydrauTensionnominale",
    "eqptcentralehydrauBacretention",
    "eqptcentralehydrauCapacitecuve",
    "eqptcentralehydrauVolume",
    "eqptcentralehydrauAccuhydpressionnominale",
    "eqptcentralehydrauAccuhydpressionepreuve",
    "eqptcentralehydrauPjmaintenance",
    "eqptcentralehydrauPjfichetechnique"
})
public class TypeEquipementCentraleHydraulique {

	@Id
	@Column(name = "eqptcentralehydrau_id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@XmlTransient
    protected int id;
	@Transient
    @XmlElement(name = "eqptcentralehydrau_eqptsocle_id", required = true)
    protected String eqptcentralehydrauEqptsocleId;
	@Transient
    @XmlElement(name = "eqptcentralehydrau_descriptif", required = true, defaultValue = "NR")
    protected String eqptcentralehydrauDescriptif;
	@Transient
    @XmlElement(name = "eqptcentralehydrau_pressionnominale")
    protected PressionsNominalesListe eqptcentralehydrauPressionnominale;
	@Transient
    @XmlElement(name = "eqptcentralehydrau_puissance", required = true)
    protected BigDecimal eqptcentralehydrauPuissance;
	@Transient
    @XmlElement(name = "eqptcentralehydrau_tensionnominale")
    protected TensionsNominalesListe eqptcentralehydrauTensionnominale;
	@Transient
    @XmlElement(name = "eqptcentralehydrau_bacretention", required = true)
    protected OuiNonListe eqptcentralehydrauBacretention;
	@Transient
    @XmlElement(name = "eqptcentralehydrau_capacitecuve")
    protected Integer eqptcentralehydrauCapacitecuve;
	@Transient
    @XmlElement(name = "eqptcentralehydrau_volume")
    protected Integer eqptcentralehydrauVolume;
	@Transient
    @XmlElement(name = "eqptcentralehydrau_accuhydpressionnominale", required = true)
    protected BigDecimal eqptcentralehydrauAccuhydpressionnominale;
	@Transient
    @XmlElement(name = "eqptcentralehydrau_accuhydpressionepreuve", required = true)
    protected BigDecimal eqptcentralehydrauAccuhydpressionepreuve;
	@Column(name = "eqptcentralehydrau_pjmaintenance")
    @XmlElement(name = "eqptcentralehydrau_pjmaintenance")
    protected String eqptcentralehydrauPjmaintenance;
	@Transient
    @XmlElement(name = "eqptcentralehydrau_pjfichetechnique", required = true)
    protected String eqptcentralehydrauPjfichetechnique;
	@Column(name = "id_chg")
	@XmlTransient
	private Integer idChg;

    /**
     * Gets the value of the eqptcentralehydrauEqptsocleId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptcentralehydrau_eqptsocle_id")
    public String getEqptcentralehydrauEqptsocleId() {
    	if (eqptcentralehydrauEqptsocleId == null) {
    		manageNullException("eqptcentralehydrau_eqptsocle_id");
    	}
        return eqptcentralehydrauEqptsocleId;
    }

    /**
     * Sets the value of the eqptcentralehydrauEqptsocleId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEqptcentralehydrauEqptsocleId(String value) {
        this.eqptcentralehydrauEqptsocleId = value;
    }

    /**
     * Gets the value of the eqptcentralehydrauDescriptif property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptcentralehydrau_descriptif")
    public String getEqptcentralehydrauDescriptif() {
    	if (eqptcentralehydrauDescriptif == null) {
    		manageNullException("eqptcentralehydrau_descriptif");
    	}
        return eqptcentralehydrauDescriptif;
    }

    /**
     * Sets the value of the eqptcentralehydrauDescriptif property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEqptcentralehydrauDescriptif(String value) {
        this.eqptcentralehydrauDescriptif = value;
    }

    /**
     * Gets the value of the eqptcentralehydrauPressionnominale property.
     * 
     * @return
     *     possible object is
     *     {@link PressionsNominalesListe }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptcentralehydrau_pressionnominale")
    public String getEqptcentralehydrauPressionnominale() {
        return PressionsNominalesListe.getValue(PatrimoineDAO.PAT_EQPT_CENTRALEHYDRAULIQUE, "eqptcentralehydrau_pressionnominale", eqptcentralehydrauPressionnominale, false);
    }

    /**
     * Sets the value of the eqptcentralehydrauPressionnominale property.
     * 
     * @param value
     *     allowed object is
     *     {@link PressionsNominalesListe }
     *     
     */
    public void setEqptcentralehydrauPressionnominale(String value) {
        this.eqptcentralehydrauPressionnominale = PressionsNominalesListe.fromValue(PatrimoineDAO.PAT_EQPT_CENTRALEHYDRAULIQUE, "eqptcentralehydrau_pressionnominale", value);
    }

    /**
     * Gets the value of the eqptcentralehydrauPuissance property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptcentralehydrau_puissance")
    public BigDecimal getEqptcentralehydrauPuissance() {
    	if (eqptcentralehydrauPuissance == null) {
    		manageNullException("eqptcentralehydrau_puissance");
    	}
        return eqptcentralehydrauPuissance;
    }

    /**
     * Sets the value of the eqptcentralehydrauPuissance property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setEqptcentralehydrauPuissance(BigDecimal value) {
        this.eqptcentralehydrauPuissance = value;
    }

    /**
     * Gets the value of the eqptcentralehydrauTensionnominale property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Access(AccessType.PROPERTY)
	@Column(name = "eqptcentralehydrau_tensionnominale")
    public String getEqptcentralehydrauTensionnominale() {
        return TensionsNominalesListe.getValue(PatrimoineDAO.PAT_EQPT_CENTRALEHYDRAULIQUE, "eqptcentralehydrau_tensionnominale", eqptcentralehydrauTensionnominale, false);
    }

    /**
     * Sets the value of the eqptcentralehydrauTensionnominale property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEqptcentralehydrauTensionnominale(String value) {
        this.eqptcentralehydrauTensionnominale = TensionsNominalesListe.fromValue(PatrimoineDAO.PAT_EQPT_CENTRALEHYDRAULIQUE, "eqptcentralehydrau_tensionnominale", value);
    }

    /**
     * Gets the value of the eqptcentralehydrauBacretention property.
     * 
     * @return
     *     possible object is
     *     {@link OuiNonListe }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptcentralehydrau_bacretention")
    public String getEqptcentralehydrauBacretention() {
        return OuiNonListe.getValue(PatrimoineDAO.PAT_EQPT_CENTRALEHYDRAULIQUE, "eqptcentralehydrau_bacretention", eqptcentralehydrauBacretention, true);
    }

    /**
     * Sets the value of the eqptcentralehydrauBacretention property.
     * 
     * @param value
     *     allowed object is
     *     {@link OuiNonListe }
     *     
     */
    public void setEqptcentralehydrauBacretention(String value) {
        this.eqptcentralehydrauBacretention = OuiNonListe.fromValue(PatrimoineDAO.PAT_EQPT_CENTRALEHYDRAULIQUE, "eqptcentralehydrau_bacretention", value);
    }

    /**
     * Gets the value of the eqptcentralehydrauCapacitecuve property.
     * 
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptcentralehydrau_capacitecuve")
    public int getEqptcentralehydrauCapacitecuve() {
    	if (eqptcentralehydrauCapacitecuve == null) {
    		manageNullException("eqptcentralehydrau_capacitecuve");
    	}
        return eqptcentralehydrauCapacitecuve;
    }

    /**
     * Sets the value of the eqptcentralehydrauCapacitecuve property.
     * 
     */
    public void setEqptcentralehydrauCapacitecuve(int value) {
        this.eqptcentralehydrauCapacitecuve = value;
    }

    /**
     * Gets the value of the eqptcentralehydrauVolume property.
     * 
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptcentralehydrau_volume")
    public int getEqptcentralehydrauVolume() {
    	if (eqptcentralehydrauVolume == null) {
    		manageNullException("eqptcentralehydrau_volume");
    	}
        return eqptcentralehydrauVolume;
    }

    /**
     * Sets the value of the eqptcentralehydrauVolume property.
     * 
     */
    public void setEqptcentralehydrauVolume(int value) {
        this.eqptcentralehydrauVolume = value;
    }

    /**
     * Gets the value of the eqptcentralehydrauAccuhydpressionnominale property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptcentralehydrau_accuhydpressionnominale")
    public BigDecimal getEqptcentralehydrauAccuhydpressionnominale() {
    	if (eqptcentralehydrauAccuhydpressionnominale == null) {
    		manageNullException("eqptcentralehydrau_accuhydpressionnominale");
    	}
        return eqptcentralehydrauAccuhydpressionnominale;
    }

    /**
     * Sets the value of the eqptcentralehydrauAccuhydpressionnominale property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setEqptcentralehydrauAccuhydpressionnominale(BigDecimal value) {
        this.eqptcentralehydrauAccuhydpressionnominale = value;
    }

    /**
     * Gets the value of the eqptcentralehydrauAccuhydpressionepreuve property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptcentralehydrau_accuhydpressionepreuve")
    public BigDecimal getEqptcentralehydrauAccuhydpressionepreuve() {
    	if (eqptcentralehydrauAccuhydpressionepreuve == null) {
    		manageNullException("eqptcentralehydrau_accuhydpressionepreuve");
    	}
        return eqptcentralehydrauAccuhydpressionepreuve;
    }

    /**
     * Sets the value of the eqptcentralehydrauAccuhydpressionepreuve property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setEqptcentralehydrauAccuhydpressionepreuve(BigDecimal value) {
        this.eqptcentralehydrauAccuhydpressionepreuve = value;
    }

    /**
     * Gets the value of the eqptcentralehydrauPjmaintenance property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEqptcentralehydrauPjmaintenance() {
        return eqptcentralehydrauPjmaintenance;
    }

    /**
     * Sets the value of the eqptcentralehydrauPjmaintenance property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEqptcentralehydrauPjmaintenance(String value) {
        this.eqptcentralehydrauPjmaintenance = value;
    }

    /**
     * Gets the value of the eqptcentralehydrauPjfichetechnique property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptcentralehydrau_pjfichetechnique")
    public String getEqptcentralehydrauPjfichetechnique() {
    	if (eqptcentralehydrauPjfichetechnique == null) {
    		manageNullException("eqptcentralehydrau_pjfichetechnique");
    	}
        return eqptcentralehydrauPjfichetechnique;
    }

    /**
     * Sets the value of the eqptcentralehydrauPjfichetechnique property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEqptcentralehydrauPjfichetechnique(String value) {
        this.eqptcentralehydrauPjfichetechnique = value;
    }

	public void setIdChg(Integer idChg) {
		this.idChg = idChg;
	}

    private void manageNullException(String champ) {
    	throw new IllegalArgumentException("Table " + PatrimoineDAO.PAT_EQPT_CENTRALEHYDRAULIQUE + " - Champs " + champ + " - Valeur 'Null' ou non permise par le XSD.");
	}

}
