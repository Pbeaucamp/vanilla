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
 * <p>Java class for typeEquipementGroupeDosage complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="typeEquipementGroupeDosage">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="eqptgrpdosage_eqptsocle_id" type="{}equipementIDType"/>
 *         &lt;element name="eqptgrpdosage_descriptif" type="{}stringRestricted3000Type"/>
 *         &lt;element name="eqptgrpdosage_type" type="{}typesGroupesDosageListe"/>
 *         &lt;element name="eqptgrpdosage_capacitedosage" type="{}decimalDeuxChiffres"/>
 *         &lt;element name="eqptgrpdosage_capacitedosageliq" type="{}decimalDeuxChiffres" minOccurs="0"/>
 *         &lt;element name="eqptgrpdosage_debitmin" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="eqptgrpdosage_debitmax" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="eqptgrpdosage_puissance" type="{}decimalDeuxChiffres"/>
 *         &lt;element name="eqptgrpdosage_tensionnominale" type="{}tensionsNominalesListe"/>
 *         &lt;element name="eqptgrpdosage_vitrotation" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="eqptgrpdosage_capacitecuve" type="{}decimalDeuxChiffres"/>
 *         &lt;element name="eqptgrpdosage_visdoseuselongueur" type="{}decimalDeuxChiffres"/>
 *         &lt;element name="eqptgrpdosage_visdoseusediametre" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="eqptgrpdosage_visdoseusematconstituant" type="{}materiauxConstituantsListe"/>
 *         &lt;element name="eqptgrpdosage_sondedetection" type="{}ouiNonListe"/>
 *         &lt;element name="eqptgrpdosage_antimarche" type="{}ouiNonListe"/>
 *         &lt;element name="eqptgrpdosage_prepamateriau" type="{}stringRestricted255Type"/>
 *         &lt;element name="eqptgrpdosage_prepavolume" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="eqptgrpdosage_prepadetectionniveau" type="{}detectionsNiveauxListe"/>
 *         &lt;element name="eqptgrpdosage_pressionnominale" type="{}pressionsNominalesListe" minOccurs="0"/>
 *         &lt;element name="eqptgrpdosage_typepompedosage" type="{}typesPompesDosageListe"/>
 *         &lt;element name="eqptgrpdosage_typeraccordement" type="{}typesraccordementsGroupesDosageListe" minOccurs="0"/>
 *         &lt;element name="eqptgrpdosage_materiautete" type="{}stringRestricted255Type" minOccurs="0"/>
 *         &lt;element name="eqptgrpdosage_materiaujoint" type="{}materiauxJointsListe"/>
 *         &lt;element name="eqptgrpdosage_pointinjection" type="{}stringRestricted255Type"/>
 *         &lt;element name="eqptgrpdosage_positionpompe" type="{}positionsPompesGroupesDosageListe"/>
 *         &lt;element name="eqptgrpdosage_coffretmateriau" type="{}materiauxConstituantsListe" minOccurs="0"/>
 *         &lt;element name="eqptgrpdosage_mesdebitligneinj" type="{}ouiNonListe"/>
 *         &lt;element name="eqptgrpdosage_reamorcage" type="{}ouiNonListe" minOccurs="0"/>
 *         &lt;element name="eqptgrpdosage_ballonamortisseur" type="{}ouiNonListe" minOccurs="0"/>
 *         &lt;element name="eqptgrpdosage_pjmaintenance" type="{}stringRestricted3000Type" minOccurs="0"/>
 *         &lt;element name="eqptgrpdosage_pjetalonnage" type="{}stringRestricted3000Type" minOccurs="0"/>
 *         &lt;element name="eqptgrpdosage_pjfichetechnique" type="{}stringRestricted3000Type" minOccurs="0"/>
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
@Table (name = PatrimoineDAO.PAT_EQPT_GROUPEDOSAGE)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "typeEquipementGroupeDosage", propOrder = {
    "eqptgrpdosageEqptsocleId",
    "eqptgrpdosageDescriptif",
    "eqptgrpdosageType",
    "eqptgrpdosageCapacitedosage",
    "eqptgrpdosageCapacitedosageliq",
    "eqptgrpdosageDebitmin",
    "eqptgrpdosageDebitmax",
    "eqptgrpdosagePuissance",
    "eqptgrpdosageTensionnominale",
    "eqptgrpdosageVitrotation",
    "eqptgrpdosageCapacitecuve",
    "eqptgrpdosageVisdoseuselongueur",
    "eqptgrpdosageVisdoseusediametre",
    "eqptgrpdosageVisdoseusematconstituant",
    "eqptgrpdosageSondedetection",
    "eqptgrpdosageAntimarche",
    "eqptgrpdosagePrepamateriau",
    "eqptgrpdosagePrepavolume",
    "eqptgrpdosagePrepadetectionniveau",
    "eqptgrpdosagePressionnominale",
    "eqptgrpdosageTypepompedosage",
    "eqptgrpdosageTyperaccordement",
    "eqptgrpdosageMateriautete",
    "eqptgrpdosageMateriaujoint",
    "eqptgrpdosagePointinjection",
    "eqptgrpdosagePositionpompe",
    "eqptgrpdosageCoffretmateriau",
    "eqptgrpdosageMesdebitligneinj",
    "eqptgrpdosageReamorcage",
    "eqptgrpdosageBallonamortisseur",
    "eqptgrpdosagePjmaintenance",
    "eqptgrpdosagePjetalonnage",
    "eqptgrpdosagePjfichetechnique"
})
public class TypeEquipementGroupeDosage {

	@Id
	@Column(name = "eqptgrpdosage_id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@XmlTransient
    protected int id;
	@Transient
    @XmlElement(name = "eqptgrpdosage_eqptsocle_id", required = true)
    protected String eqptgrpdosageEqptsocleId;
	@Transient
    @XmlElement(name = "eqptgrpdosage_descriptif", required = true, defaultValue = "NR")
    protected String eqptgrpdosageDescriptif;
	@Transient
    @XmlElement(name = "eqptgrpdosage_type", required = true)
    protected TypesGroupesDosageListe eqptgrpdosageType;
	@Transient
    @XmlElement(name = "eqptgrpdosage_capacitedosage", required = true)
    protected BigDecimal eqptgrpdosageCapacitedosage;
	@Column(name = "eqptgrpdosage_capacitedosageliq")
    @XmlElement(name = "eqptgrpdosage_capacitedosageliq")
    protected BigDecimal eqptgrpdosageCapacitedosageliq;
	@Column(name = "eqptgrpdosage_debitmin")
    @XmlElement(name = "eqptgrpdosage_debitmin")
    protected Integer eqptgrpdosageDebitmin;
	@Column(name = "eqptgrpdosage_debitmax")
    @XmlElement(name = "eqptgrpdosage_debitmax")
    protected Integer eqptgrpdosageDebitmax;
	@Transient
    @XmlElement(name = "eqptgrpdosage_puissance", required = true)
    protected BigDecimal eqptgrpdosagePuissance;
	@Transient
    @XmlElement(name = "eqptgrpdosage_tensionnominale", required = true)
    protected TensionsNominalesListe eqptgrpdosageTensionnominale;
	@Transient
    @XmlElement(name = "eqptgrpdosage_vitrotation")
    protected Integer eqptgrpdosageVitrotation;
	@Transient
    @XmlElement(name = "eqptgrpdosage_capacitecuve", required = true)
    protected BigDecimal eqptgrpdosageCapacitecuve;
	@Transient
    @XmlElement(name = "eqptgrpdosage_visdoseuselongueur", required = true)
    protected BigDecimal eqptgrpdosageVisdoseuselongueur;
	@Transient
    @XmlElement(name = "eqptgrpdosage_visdoseusediametre")
    protected Integer eqptgrpdosageVisdoseusediametre;
	@Transient
    @XmlElement(name = "eqptgrpdosage_visdoseusematconstituant", required = true)
    protected MateriauxConstituantsListe eqptgrpdosageVisdoseusematconstituant;
	@Transient
    @XmlElement(name = "eqptgrpdosage_sondedetection", required = true)
    protected OuiNonListe eqptgrpdosageSondedetection;
	@Transient
    @XmlElement(name = "eqptgrpdosage_antimarche", required = true)
    protected OuiNonListe eqptgrpdosageAntimarche;
	@Transient
    @XmlElement(name = "eqptgrpdosage_prepamateriau", required = true)
    protected String eqptgrpdosagePrepamateriau;
	@Transient
    @XmlElement(name = "eqptgrpdosage_prepavolume")
    protected Integer eqptgrpdosagePrepavolume;
	@Transient
    @XmlElement(name = "eqptgrpdosage_prepadetectionniveau", required = true)
    protected DetectionsNiveauxListe eqptgrpdosagePrepadetectionniveau;
	@Transient
    @XmlElement(name = "eqptgrpdosage_pressionnominale")
    protected PressionsNominalesListe eqptgrpdosagePressionnominale;
	@Transient
    @XmlElement(name = "eqptgrpdosage_typepompedosage", required = true)
    protected TypesPompesDosageListe eqptgrpdosageTypepompedosage;
	@Transient
    @XmlElement(name = "eqptgrpdosage_typeraccordement")
    protected TypesraccordementsGroupesDosageListe eqptgrpdosageTyperaccordement;
	@Column(name = "eqptgrpdosage_materiautete")
    @XmlElement(name = "eqptgrpdosage_materiautete")
    protected String eqptgrpdosageMateriautete;
	@Transient
    @XmlElement(name = "eqptgrpdosage_materiaujoint", required = true)
    protected MateriauxJointsListe eqptgrpdosageMateriaujoint;
	@Transient
    @XmlElement(name = "eqptgrpdosage_pointinjection", required = true)
    protected String eqptgrpdosagePointinjection;
	@Transient
    @XmlElement(name = "eqptgrpdosage_positionpompe", required = true)
    protected PositionsPompesGroupesDosageListe eqptgrpdosagePositionpompe;
	@Transient
    @XmlElement(name = "eqptgrpdosage_coffretmateriau")
    protected MateriauxConstituantsListe eqptgrpdosageCoffretmateriau;
	@Transient
    @XmlElement(name = "eqptgrpdosage_mesdebitligneinj", required = true)
    protected OuiNonListe eqptgrpdosageMesdebitligneinj;
	@Transient
    @XmlElement(name = "eqptgrpdosage_reamorcage")
    protected OuiNonListe eqptgrpdosageReamorcage;
	@Transient
    @XmlElement(name = "eqptgrpdosage_ballonamortisseur")
    protected OuiNonListe eqptgrpdosageBallonamortisseur;
	@Column(name = "eqptgrpdosage_pjmaintenance")
    @XmlElement(name = "eqptgrpdosage_pjmaintenance")
    protected String eqptgrpdosagePjmaintenance;
	@Column(name = "eqptgrpdosage_pjetalonnage")
    @XmlElement(name = "eqptgrpdosage_pjetalonnage")
    protected String eqptgrpdosagePjetalonnage;
	@Column(name = "eqptgrpdosage_pjfichetechnique")
    @XmlElement(name = "eqptgrpdosage_pjfichetechnique")
    protected String eqptgrpdosagePjfichetechnique;
	@Column(name = "id_chg")
	@XmlTransient
	private Integer idChg;

    /**
     * Gets the value of the eqptgrpdosageEqptsocleId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptgrpdosage_eqptsocle_id")
    public String getEqptgrpdosageEqptsocleId() {
    	if (eqptgrpdosageEqptsocleId == null) {
    		manageNullException("eqptgrpdosage_eqptsocle_id");
    	}
        return eqptgrpdosageEqptsocleId;
    }

    /**
     * Sets the value of the eqptgrpdosageEqptsocleId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEqptgrpdosageEqptsocleId(String value) {
        this.eqptgrpdosageEqptsocleId = value;
    }

    /**
     * Gets the value of the eqptgrpdosageDescriptif property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptgrpdosage_descriptif")
    public String getEqptgrpdosageDescriptif() {
    	if (eqptgrpdosageDescriptif == null) {
    		manageNullException("eqptgrpdosage_descriptif");
    	}
        return eqptgrpdosageDescriptif;
    }

    /**
     * Sets the value of the eqptgrpdosageDescriptif property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEqptgrpdosageDescriptif(String value) {
        this.eqptgrpdosageDescriptif = value;
    }

    /**
     * Gets the value of the eqptgrpdosageType property.
     * 
     * @return
     *     possible object is
     *     {@link TypesGroupesDosageListe }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptgrpdosage_type")
    public String getEqptgrpdosageType() {
        return TypesGroupesDosageListe.getValue(PatrimoineDAO.PAT_EQPT_GROUPEDOSAGE, "eqptgrpdosage_type", eqptgrpdosageType);
    }

    /**
     * Sets the value of the eqptgrpdosageType property.
     * 
     * @param value
     *     allowed object is
     *     {@link TypesGroupesDosageListe }
     *     
     */
    public void setEqptgrpdosageType(String value) {
        this.eqptgrpdosageType = TypesGroupesDosageListe.fromValue(PatrimoineDAO.PAT_EQPT_GROUPEDOSAGE, "eqptgrpdosage_type", value);
    }

    /**
     * Gets the value of the eqptgrpdosageCapacitedosage property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptgrpdosage_capacitedosage")
    public BigDecimal getEqptgrpdosageCapacitedosage() {
    	if (eqptgrpdosageCapacitedosage == null) {
    		manageNullException("eqptgrpdosage_capacitedosage");
    	}
        return eqptgrpdosageCapacitedosage;
    }

    /**
     * Sets the value of the eqptgrpdosageCapacitedosage property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setEqptgrpdosageCapacitedosage(BigDecimal value) {
        this.eqptgrpdosageCapacitedosage = value;
    }

    /**
     * Gets the value of the eqptgrpdosageCapacitedosageliq property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getEqptgrpdosageCapacitedosageliq() {
        return eqptgrpdosageCapacitedosageliq;
    }

    /**
     * Sets the value of the eqptgrpdosageCapacitedosageliq property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setEqptgrpdosageCapacitedosageliq(BigDecimal value) {
        this.eqptgrpdosageCapacitedosageliq = value;
    }

    /**
     * Gets the value of the eqptgrpdosageDebitmin property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getEqptgrpdosageDebitmin() {
        return eqptgrpdosageDebitmin;
    }

    /**
     * Sets the value of the eqptgrpdosageDebitmin property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setEqptgrpdosageDebitmin(Integer value) {
        this.eqptgrpdosageDebitmin = value;
    }

    /**
     * Gets the value of the eqptgrpdosageDebitmax property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getEqptgrpdosageDebitmax() {
        return eqptgrpdosageDebitmax;
    }

    /**
     * Sets the value of the eqptgrpdosageDebitmax property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setEqptgrpdosageDebitmax(Integer value) {
        this.eqptgrpdosageDebitmax = value;
    }

    /**
     * Gets the value of the eqptgrpdosagePuissance property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptgrpdosage_puissance")
    public BigDecimal getEqptgrpdosagePuissance() {
    	if (eqptgrpdosagePuissance == null) {
    		manageNullException("eqptgrpdosage_puissance");
    	}
        return eqptgrpdosagePuissance;
    }

    /**
     * Sets the value of the eqptgrpdosagePuissance property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setEqptgrpdosagePuissance(BigDecimal value) {
        this.eqptgrpdosagePuissance = value;
    }

    /**
     * Gets the value of the eqptgrpdosageTensionnominale property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptgrpdosage_tensionnominale")
    public String getEqptgrpdosageTensionnominale() {
        return TensionsNominalesListe.getValue(PatrimoineDAO.PAT_EQPT_GROUPEDOSAGE, "eqptgrpdosage_tensionnominale", eqptgrpdosageTensionnominale, true);
    }

    /**
     * Sets the value of the eqptgrpdosageTensionnominale property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEqptgrpdosageTensionnominale(String value) {
        this.eqptgrpdosageTensionnominale = TensionsNominalesListe.fromValue(PatrimoineDAO.PAT_EQPT_GROUPEDOSAGE, "eqptgrpdosage_tensionnominale", value);
    }

    /**
     * Gets the value of the eqptgrpdosageVitrotation property.
     * 
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptgrpdosage_vitrotation")
    public int getEqptgrpdosageVitrotation() {
    	if (eqptgrpdosageVitrotation == null) {
    		manageNullException("eqptgrpdosage_vitrotation");
    	}
        return eqptgrpdosageVitrotation;
    }

    /**
     * Sets the value of the eqptgrpdosageVitrotation property.
     * 
     */
    public void setEqptgrpdosageVitrotation(int value) {
        this.eqptgrpdosageVitrotation = value;
    }

    /**
     * Gets the value of the eqptgrpdosageCapacitecuve property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptgrpdosage_capacitecuve")
    public BigDecimal getEqptgrpdosageCapacitecuve() {
    	if (eqptgrpdosageCapacitecuve == null) {
    		manageNullException("eqptgrpdosage_capacitecuve");
    	}
        return eqptgrpdosageCapacitecuve;
    }

    /**
     * Sets the value of the eqptgrpdosageCapacitecuve property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setEqptgrpdosageCapacitecuve(BigDecimal value) {
        this.eqptgrpdosageCapacitecuve = value;
    }

    /**
     * Gets the value of the eqptgrpdosageVisdoseuselongueur property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptgrpdosage_visdoseuselongueur")
    public BigDecimal getEqptgrpdosageVisdoseuselongueur() {
    	if (eqptgrpdosageVisdoseuselongueur == null) {
    		manageNullException("eqptgrpdosage_visdoseuselongueur");
    	}
        return eqptgrpdosageVisdoseuselongueur;
    }

    /**
     * Sets the value of the eqptgrpdosageVisdoseuselongueur property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setEqptgrpdosageVisdoseuselongueur(BigDecimal value) {
        this.eqptgrpdosageVisdoseuselongueur = value;
    }

    /**
     * Gets the value of the eqptgrpdosageVisdoseusediametre property.
     * 
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptgrpdosage_visdoseusediametre")
    public int getEqptgrpdosageVisdoseusediametre() {
    	if (eqptgrpdosageVisdoseusediametre == null) {
    		manageNullException("eqptgrpdosage_visdoseusediametre");
    	}
        return eqptgrpdosageVisdoseusediametre;
    }

    /**
     * Sets the value of the eqptgrpdosageVisdoseusediametre property.
     * 
     */
    public void setEqptgrpdosageVisdoseusediametre(int value) {
        this.eqptgrpdosageVisdoseusediametre = value;
    }

    /**
     * Gets the value of the eqptgrpdosageVisdoseusematconstituant property.
     * 
     * @return
     *     possible object is
     *     {@link MateriauxConstituantsListe }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptgrpdosage_visdoseusematconstituant")
    public String getEqptgrpdosageVisdoseusematconstituant() {
        return MateriauxConstituantsListe.getValue(PatrimoineDAO.PAT_EQPT_GROUPEDOSAGE, "eqptgrpdosage_visdoseusematconstituant", eqptgrpdosageVisdoseusematconstituant, true);
    }

    /**
     * Sets the value of the eqptgrpdosageVisdoseusematconstituant property.
     * 
     * @param value
     *     allowed object is
     *     {@link MateriauxConstituantsListe }
     *     
     */
    public void setEqptgrpdosageVisdoseusematconstituant(String value) {
        this.eqptgrpdosageVisdoseusematconstituant = MateriauxConstituantsListe.fromValue(PatrimoineDAO.PAT_EQPT_GROUPEDOSAGE, "eqptgrpdosage_visdoseusematconstituant", value);
    }

    /**
     * Gets the value of the eqptgrpdosageSondedetection property.
     * 
     * @return
     *     possible object is
     *     {@link OuiNonListe }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptgrpdosage_sondedetection")
    public String getEqptgrpdosageSondedetection() {
        return OuiNonListe.getValue(PatrimoineDAO.PAT_EQPT_GROUPEDOSAGE, "eqptgrpdosage_sondedetection", eqptgrpdosageSondedetection, true);
    }

    /**
     * Sets the value of the eqptgrpdosageSondedetection property.
     * 
     * @param value
     *     allowed object is
     *     {@link OuiNonListe }
     *     
     */
    public void setEqptgrpdosageSondedetection(String value) {
        this.eqptgrpdosageSondedetection = OuiNonListe.fromValue(PatrimoineDAO.PAT_EQPT_GROUPEDOSAGE, "eqptgrpdosage_sondedetection", value);
    }

    /**
     * Gets the value of the eqptgrpdosageAntimarche property.
     * 
     * @return
     *     possible object is
     *     {@link OuiNonListe }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptgrpdosage_antimarche")
    public String getEqptgrpdosageAntimarche() {
        return OuiNonListe.getValue(PatrimoineDAO.PAT_EQPT_GROUPEDOSAGE, "eqptgrpdosage_antimarche", eqptgrpdosageAntimarche, true);
    }

    /**
     * Sets the value of the eqptgrpdosageAntimarche property.
     * 
     * @param value
     *     allowed object is
     *     {@link OuiNonListe }
     *     
     */
    public void setEqptgrpdosageAntimarche(String value) {
        this.eqptgrpdosageAntimarche = OuiNonListe.fromValue(PatrimoineDAO.PAT_EQPT_GROUPEDOSAGE, "eqptgrpdosage_antimarche", value);
    }

    /**
     * Gets the value of the eqptgrpdosagePrepamateriau property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptgrpdosage_prepamateriau")
    public String getEqptgrpdosagePrepamateriau() {
    	if (eqptgrpdosagePrepamateriau == null) {
    		manageNullException("eqptgrpdosage_prepamateriau");
    	}
        return eqptgrpdosagePrepamateriau;
    }

    /**
     * Sets the value of the eqptgrpdosagePrepamateriau property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEqptgrpdosagePrepamateriau(String value) {
        this.eqptgrpdosagePrepamateriau = value;
    }

    /**
     * Gets the value of the eqptgrpdosagePrepavolume property.
     * 
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptgrpdosage_prepavolume")
    public int getEqptgrpdosagePrepavolume() {
    	if (eqptgrpdosagePrepavolume == null) {
    		manageNullException("eqptgrpdosage_prepavolume");
    	}
        return eqptgrpdosagePrepavolume;
    }

    /**
     * Sets the value of the eqptgrpdosagePrepavolume property.
     * 
     */
    public void setEqptgrpdosagePrepavolume(int value) {
        this.eqptgrpdosagePrepavolume = value;
    }

    /**
     * Gets the value of the eqptgrpdosagePrepadetectionniveau property.
     * 
     * @return
     *     possible object is
     *     {@link DetectionsNiveauxListe }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptgrpdosage_prepadetectionniveau")
    public String getEqptgrpdosagePrepadetectionniveau() {
        return DetectionsNiveauxListe.getValue(PatrimoineDAO.PAT_EQPT_GROUPEDOSAGE, "eqptgrpdosage_prepadetectionniveau", eqptgrpdosagePrepadetectionniveau);
    }

    /**
     * Sets the value of the eqptgrpdosagePrepadetectionniveau property.
     * 
     * @param value
     *     allowed object is
     *     {@link DetectionsNiveauxListe }
     *     
     */
    public void setEqptgrpdosagePrepadetectionniveau(String value) {
        this.eqptgrpdosagePrepadetectionniveau = DetectionsNiveauxListe.fromValue(PatrimoineDAO.PAT_EQPT_GROUPEDOSAGE, "eqptgrpdosage_prepadetectionniveau", value);
    }

    /**
     * Gets the value of the eqptgrpdosagePressionnominale property.
     * 
     * @return
     *     possible object is
     *     {@link PressionsNominalesListe }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptgrpdosage_pressionnominale")
    public String getEqptgrpdosagePressionominale() {
        return PressionsNominalesListe.getValue(PatrimoineDAO.PAT_EQPT_GROUPEDOSAGE, "eqptgrpdosage_pressionnominale", eqptgrpdosagePressionnominale, false);
    }

    /**
     * Sets the value of the eqptgrpdosagePressionnominale property.
     * 
     * @param value
     *     allowed object is
     *     {@link PressionsNominalesListe }
     *     
     */
    public void setEqptgrpdosagePressionominale(String value) {
        this.eqptgrpdosagePressionnominale = PressionsNominalesListe.fromValue(PatrimoineDAO.PAT_EQPT_GROUPEDOSAGE, "eqptgrpdosage_pressionnominale", value);
    }

    /**
     * Gets the value of the eqptgrpdosageTypepompedosage property.
     * 
     * @return
     *     possible object is
     *     {@link TypesPompesDosageListe }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptgrpdosage_typepompedosage")
    public String getEqptgrpdosageTypepompedosage() {
        return TypesPompesDosageListe.getValue(PatrimoineDAO.PAT_EQPT_GROUPEDOSAGE, "eqptgrpdosage_typepompedosage", eqptgrpdosageTypepompedosage);
    }

    /**
     * Sets the value of the eqptgrpdosageTypepompedosage property.
     * 
     * @param value
     *     allowed object is
     *     {@link TypesPompesDosageListe }
     *     
     */
    public void setEqptgrpdosageTypepompedosage(String value) {
        this.eqptgrpdosageTypepompedosage = TypesPompesDosageListe.fromValue(PatrimoineDAO.PAT_EQPT_GROUPEDOSAGE, "eqptgrpdosage_typepompedosage", value);
    }

    /**
     * Gets the value of the eqptgrpdosageTyperaccordement property.
     * 
     * @return
     *     possible object is
     *     {@link TypesraccordementsGroupesDosageListe }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptgrpdosage_typeraccordement")
    public String getEqptgrpdosageTyperaccordement() {
        return TypesraccordementsGroupesDosageListe.getValue(PatrimoineDAO.PAT_EQPT_GROUPEDOSAGE, "eqptgrpdosage_typeraccordement", eqptgrpdosageTyperaccordement, false);
    }

    /**
     * Sets the value of the eqptgrpdosageTyperaccordement property.
     * 
     * @param value
     *     allowed object is
     *     {@link TypesraccordementsGroupesDosageListe }
     *     
     */
    public void setEqptgrpdosageTyperaccordement(String value) {
        this.eqptgrpdosageTyperaccordement = TypesraccordementsGroupesDosageListe.fromValue(PatrimoineDAO.PAT_EQPT_GROUPEDOSAGE, "eqptgrpdosage_typeraccordement", value);
    }

    /**
     * Gets the value of the eqptgrpdosageMateriautete property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEqptgrpdosageMateriautete() {
        return eqptgrpdosageMateriautete;
    }

    /**
     * Sets the value of the eqptgrpdosageMateriautete property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEqptgrpdosageMateriautete(String value) {
        this.eqptgrpdosageMateriautete = value;
    }

    /**
     * Gets the value of the eqptgrpdosageMateriaujoint property.
     * 
     * @return
     *     possible object is
     *     {@link MateriauxJointsListe }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptgrpdosage_materiaujoint")
    public String getEqptgrpdosageMateriaujoint() {
        return MateriauxJointsListe.getValue(PatrimoineDAO.PAT_EQPT_GROUPEDOSAGE, "eqptgrpdosage_materiaujoint", eqptgrpdosageMateriaujoint);
    }

    /**
     * Sets the value of the eqptgrpdosageMateriaujoint property.
     * 
     * @param value
     *     allowed object is
     *     {@link MateriauxJointsListe }
     *     
     */
    public void setEqptgrpdosageMateriaujoint(String value) {
        this.eqptgrpdosageMateriaujoint = MateriauxJointsListe.fromValue(PatrimoineDAO.PAT_EQPT_GROUPEDOSAGE, "eqptgrpdosage_materiaujoint", value);
    }

    /**
     * Gets the value of the eqptgrpdosagePointinjection property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptgrpdosage_pointinjection")
    public String getEqptgrpdosagePointinjection() {
    	if (eqptgrpdosagePointinjection == null) {
    		manageNullException("eqptgrpdosage_pointinjection");
    	}
        return eqptgrpdosagePointinjection;
    }

    /**
     * Sets the value of the eqptgrpdosagePointinjection property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEqptgrpdosagePointinjection(String value) {
        this.eqptgrpdosagePointinjection = value;
    }

    /**
     * Gets the value of the eqptgrpdosagePositionpompe property.
     * 
     * @return
     *     possible object is
     *     {@link PositionsPompesGroupesDosageListe }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptgrpdosage_positionpompe")
    public String getEqptgrpdosagePositionpompe() {
        return PositionsPompesGroupesDosageListe.getValue(PatrimoineDAO.PAT_EQPT_GROUPEDOSAGE, "eqptgrpdosage_positionpompe", eqptgrpdosagePositionpompe);
    }

    /**
     * Sets the value of the eqptgrpdosagePositionpompe property.
     * 
     * @param value
     *     allowed object is
     *     {@link PositionsPompesGroupesDosageListe }
     *     
     */
    public void setEqptgrpdosagePositionpompe(String value) {
        this.eqptgrpdosagePositionpompe = PositionsPompesGroupesDosageListe.fromValue(PatrimoineDAO.PAT_EQPT_GROUPEDOSAGE, "eqptgrpdosage_positionpompe", value);
    }

    /**
     * Gets the value of the eqptgrpdosageCoffretmateriau property.
     * 
     * @return
     *     possible object is
     *     {@link MateriauxConstituantsListe }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptgrpdosage_coffretmateriau")
    public String getEqptgrpdosageCoffretmateriau() {
        return MateriauxConstituantsListe.getValue(PatrimoineDAO.PAT_EQPT_GROUPEDOSAGE, "eqptgrpdosage_coffretmateriau", eqptgrpdosageCoffretmateriau, false);
    }

    /**
     * Sets the value of the eqptgrpdosageCoffretmateriau property.
     * 
     * @param value
     *     allowed object is
     *     {@link MateriauxConstituantsListe }
     *     
     */
    public void setEqptgrpdosageCoffretmateriau(String value) {
        this.eqptgrpdosageCoffretmateriau = MateriauxConstituantsListe.fromValue(PatrimoineDAO.PAT_EQPT_GROUPEDOSAGE, "eqptgrpdosage_coffretmateriau", value);
    }

    /**
     * Gets the value of the eqptgrpdosageMesdebitligneinj property.
     * 
     * @return
     *     possible object is
     *     {@link OuiNonListe }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptgrpdosage_mesdebitligneinj")
    public String getEqptgrpdosageMesdebitligneinj() {
        return OuiNonListe.getValue(PatrimoineDAO.PAT_EQPT_GROUPEDOSAGE, "eqptgrpdosage_mesdebitligneinj", eqptgrpdosageMesdebitligneinj, true);
    }

    /**
     * Sets the value of the eqptgrpdosageMesdebitligneinj property.
     * 
     * @param value
     *     allowed object is
     *     {@link OuiNonListe }
     *     
     */
    public void setEqptgrpdosageMesdebitligneinj(String value) {
        this.eqptgrpdosageMesdebitligneinj = OuiNonListe.fromValue(PatrimoineDAO.PAT_EQPT_GROUPEDOSAGE, "eqptgrpdosage_mesdebitligneinj", value);
    }

    /**
     * Gets the value of the eqptgrpdosageReamorcage property.
     * 
     * @return
     *     possible object is
     *     {@link OuiNonListe }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptgrpdosage_reamorcage")
    public String getEqptgrpdosageReamorcage() {
        return OuiNonListe.getValue(PatrimoineDAO.PAT_EQPT_GROUPEDOSAGE, "eqptgrpdosage_reamorcage", eqptgrpdosageReamorcage, false);
    }

    /**
     * Sets the value of the eqptgrpdosageReamorcage property.
     * 
     * @param value
     *     allowed object is
     *     {@link OuiNonListe }
     *     
     */
    public void setEqptgrpdosageReamorcage(String value) {
        this.eqptgrpdosageReamorcage = OuiNonListe.fromValue(PatrimoineDAO.PAT_EQPT_GROUPEDOSAGE, "eqptgrpdosage_reamorcage", value);
    }

    /**
     * Gets the value of the eqptgrpdosageBallonamortisseur property.
     * 
     * @return
     *     possible object is
     *     {@link OuiNonListe }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptgrpdosage_ballonamortisseur")
    public String getEqptgrpdosageBallonamortisseur() {
        return OuiNonListe.getValue(PatrimoineDAO.PAT_EQPT_GROUPEDOSAGE, "eqptgrpdosage_ballonamortisseur", eqptgrpdosageBallonamortisseur, false);
    }

    /**
     * Sets the value of the eqptgrpdosageBallonamortisseur property.
     * 
     * @param value
     *     allowed object is
     *     {@link OuiNonListe }
     *     
     */
    public void setEqptgrpdosageBallonamortisseur(String value) {
        this.eqptgrpdosageBallonamortisseur = OuiNonListe.fromValue(PatrimoineDAO.PAT_EQPT_GROUPEDOSAGE, "eqptgrpdosage_ballonamortisseur", value);
    }

    /**
     * Gets the value of the eqptgrpdosagePjmaintenance property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEqptgrpdosagePjmaintenance() {
        return eqptgrpdosagePjmaintenance;
    }

    /**
     * Sets the value of the eqptgrpdosagePjmaintenance property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEqptgrpdosagePjmaintenance(String value) {
        this.eqptgrpdosagePjmaintenance = value;
    }

    /**
     * Gets the value of the eqptgrpdosagePjetalonnage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEqptgrpdosagePjetalonnage() {
        return eqptgrpdosagePjetalonnage;
    }

    /**
     * Sets the value of the eqptgrpdosagePjetalonnage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEqptgrpdosagePjetalonnage(String value) {
        this.eqptgrpdosagePjetalonnage = value;
    }

    /**
     * Gets the value of the eqptgrpdosagePjfichetechnique property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEqptgrpdosagePjfichetechnique() {
        return eqptgrpdosagePjfichetechnique;
    }

    /**
     * Sets the value of the eqptgrpdosagePjfichetechnique property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEqptgrpdosagePjfichetechnique(String value) {
        this.eqptgrpdosagePjfichetechnique = value;
    }

	public void setIdChg(Integer idChg) {
		this.idChg = idChg;
	}

    private void manageNullException(String champ) {
    	throw new IllegalArgumentException("Table " + PatrimoineDAO.PAT_EQPT_GROUPEDOSAGE + " - Champs " + champ + " - Valeur 'Null' ou non permise par le XSD.");
	}

}
