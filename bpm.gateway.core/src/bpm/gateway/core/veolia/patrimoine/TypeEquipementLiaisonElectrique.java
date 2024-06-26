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
 * <p>Java class for typeEquipementLiaisonElectrique complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="typeEquipementLiaisonElectrique">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="eqptliaisonele_eqptsocle_id" type="{}equipementIDType"/>
 *         &lt;element name="eqptliaisonele_descriptif" type="{}stringRestricted3000Type"/>
 *         &lt;element name="eqptliaisonele_fonctioncable" type="{}fonctionsCablesListe"/>
 *         &lt;element name="eqptliaisonele_longueur" type="{}decimalDeuxChiffres" minOccurs="0"/>
 *         &lt;element name="eqptliaisonele_intensite" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="eqptliaisonele_tensionnominale" type="{}tensionsNominalesListe" minOccurs="0"/>
 *         &lt;element name="eqptliaisonele_typecable" type="{}stringRestricted255Type" minOccurs="0"/>
 *         &lt;element name="eqptliaisonele_sectioncable" type="{}stringRestricted3000Type" minOccurs="0"/>
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
@Table (name = PatrimoineDAO.PAT_EQPT_LIAISONELECTRIQUE)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "typeEquipementLiaisonElectrique", propOrder = {
    "eqptliaisoneleEqptsocleId",
    "eqptliaisoneleDescriptif",
    "eqptliaisoneleFonctioncable",
    "eqptliaisoneleLongueur",
    "eqptliaisoneleIntensite",
    "eqptliaisoneleTensionnominale",
    "eqptliaisoneleTypecable",
    "eqptliaisoneleSectioncable"
})
public class TypeEquipementLiaisonElectrique {

	@Id
	@Column(name = "eqptliaisonele_id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@XmlTransient
    protected int id;
	@Transient
    @XmlElement(name = "eqptliaisonele_eqptsocle_id", required = true)
    protected String eqptliaisoneleEqptsocleId;
	@Transient
    @XmlElement(name = "eqptliaisonele_descriptif", required = true, defaultValue = "NR")
    protected String eqptliaisoneleDescriptif;
	@Transient
    @XmlElement(name = "eqptliaisonele_fonctioncable", required = true)
    protected FonctionsCablesListe eqptliaisoneleFonctioncable;
	@Column(name = "eqptliaisonele_longueur")
    @XmlElement(name = "eqptliaisonele_longueur")
    protected BigDecimal eqptliaisoneleLongueur;
	@Column(name = "eqptliaisonele_intensite")
    @XmlElement(name = "eqptliaisonele_intensite")
    protected Integer eqptliaisoneleIntensite;
	@Transient
    @XmlElement(name = "eqptliaisonele_tensionnominale")
    protected TensionsNominalesListe eqptliaisoneleTensionnominale;
	@Column(name = "eqptliaisonele_typecable")
    @XmlElement(name = "eqptliaisonele_typecable")
    protected String eqptliaisoneleTypecable;
	@Column(name = "eqptliaisonele_sectioncable")
    @XmlElement(name = "eqptliaisonele_sectioncable")
    protected Integer eqptliaisoneleSectioncable;
	@Column(name = "id_chg")
	@XmlTransient
	private Integer idChg;

    /**
     * Gets the value of the eqptliaisoneleEqptsocleId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptliaisonele_eqptsocle_id")
    public String getEqptliaisoneleEqptsocleId() {
    	if (eqptliaisoneleEqptsocleId == null) {
    		manageNullException("eqptliaisonele_eqptsocle_id");
    	}
        return eqptliaisoneleEqptsocleId;
    }

    /**
     * Sets the value of the eqptliaisoneleEqptsocleId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEqptliaisoneleEqptsocleId(String value) {
        this.eqptliaisoneleEqptsocleId = value;
    }

    /**
     * Gets the value of the eqptliaisoneleDescriptif property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptliaisonele_descriptif")
    public String getEqptliaisoneleDescriptif() {
    	if (eqptliaisoneleDescriptif == null) {
    		manageNullException("eqptliaisonele_descriptif");
    	}
        return eqptliaisoneleDescriptif;
    }

    /**
     * Sets the value of the eqptliaisoneleDescriptif property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEqptliaisoneleDescriptif(String value) {
        this.eqptliaisoneleDescriptif = value;
    }

    /**
     * Gets the value of the eqptliaisoneleFonctioncable property.
     * 
     * @return
     *     possible object is
     *     {@link FonctionsCablesListe }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptliaisonele_fonctioncable")
    public String getEqptliaisoneleFonctioncable() {
        return FonctionsCablesListe.getValue(PatrimoineDAO.PAT_EQPT_LIAISONELECTRIQUE, "eqptliaisonele_fonctioncable", eqptliaisoneleFonctioncable);
    }

    /**
     * Sets the value of the eqptliaisoneleFonctioncable property.
     * 
     * @param value
     *     allowed object is
     *     {@link FonctionsCablesListe }
     *     
     */
    public void setEqptliaisoneleFonctioncable(String value) {
        this.eqptliaisoneleFonctioncable = FonctionsCablesListe.fromValue(PatrimoineDAO.PAT_EQPT_LIAISONELECTRIQUE, "eqptliaisonele_fonctioncable", value);
    }

    /**
     * Gets the value of the eqptliaisoneleLongueur property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getEqptliaisoneleLongueur() {
        return eqptliaisoneleLongueur;
    }

    /**
     * Sets the value of the eqptliaisoneleLongueur property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setEqptliaisoneleLongueur(BigDecimal value) {
        this.eqptliaisoneleLongueur = value;
    }

    /**
     * Gets the value of the eqptliaisoneleIntensite property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getEqptliaisoneleIntensite() {
        return eqptliaisoneleIntensite;
    }

    /**
     * Sets the value of the eqptliaisoneleIntensite property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setEqptliaisoneleIntensite(Integer value) {
        this.eqptliaisoneleIntensite = value;
    }

    /**
     * Gets the value of the eqptliaisoneleTensionnominale property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Access(AccessType.PROPERTY)
	@Column(name = "eqptliaisonele_tensionnominale")
    public String getEqptliaisoneleTensionnominale() {
    	return TensionsNominalesListe.getValue(PatrimoineDAO.PAT_EQPT_LIAISONELECTRIQUE, "eqptliaisonele_tensionnominale", eqptliaisoneleTensionnominale, false);
    }

    /**
     * Sets the value of the eqptliaisoneleTensionnominale property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEqptliaisoneleTensionnominale(String value) {
    	this.eqptliaisoneleTensionnominale = TensionsNominalesListe.fromValue(PatrimoineDAO.PAT_EQPT_LIAISONELECTRIQUE, "eqptliaisonele_tensionnominale", value);
    }

    /**
     * Gets the value of the eqptliaisoneleTypecable property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEqptliaisoneleTypecable() {
        return eqptliaisoneleTypecable;
    }

    /**
     * Sets the value of the eqptliaisoneleTypecable property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEqptliaisoneleTypecable(String value) {
        this.eqptliaisoneleTypecable = value;
    }

    /**
     * Gets the value of the eqptliaisoneleSectioncable property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Integer getEqptliaisoneleSectioncable() {
        return eqptliaisoneleSectioncable;
    }

    /**
     * Sets the value of the eqptliaisoneleSectioncable property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEqptliaisoneleSectioncable(Integer value) {
        this.eqptliaisoneleSectioncable = value;
    }

	public void setIdChg(Integer idChg) {
		this.idChg = idChg;
	}

    private void manageNullException(String champ) {
    	throw new IllegalArgumentException("Table " + PatrimoineDAO.PAT_EQPT_LIAISONELECTRIQUE + " - Champs " + champ + " - Valeur 'Null' ou non permise par le XSD.");
	}

}
