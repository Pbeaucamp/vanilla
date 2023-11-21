//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.01.02 at 10:47:34 AM CET 
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
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for typeEquipementEnregistreur complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="typeEquipementEnregistreur">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="eqptenregist_eqptsocle_id" type="{}equipementIDType"/>
 *         &lt;element name="eqptenregist_descriptif" type="{}stringRestricted3000Type"/>
 *         &lt;element name="eqptenregist_parametremesdet" type="{}parametresMesuresListe"/>
 *         &lt;element name="eqptenregist_tensionnominale" type="{}tensionsNominalesListe"/>
 *         &lt;element name="eqptenregist_nbvoies" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="eqptenregist_typesignal" type="{}typesSignauxListe"/>
 *         &lt;element name="eqptenregist_pjmaintenance" type="{}stringRestricted3000Type" minOccurs="0"/>
 *         &lt;element name="eqptenregist_pjsuivimetrologique" type="{}stringRestricted3000Type" minOccurs="0"/>
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
@Table (name = PatrimoineDAO.PAT_EQPT_ENREGISTREUR)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "typeEquipementEnregistreur", propOrder = {
    "eqptenregistEqptsocleId",
    "eqptenregistDescriptif",
    "eqptenregistParametremesdet",
    "eqptenregistTensionnominale",
    "eqptenregistNbvoies",
    "eqptenregistTypesignal",
    "eqptenregistPjmaintenance",
    "eqptenregistPjsuivimetrologique"
})
public class TypeEquipementEnregistreur {

	@Id
	@Column(name = "eqptenregist_id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@XmlTransient
    protected int id;
	@Transient
    @XmlElement(name = "eqptenregist_eqptsocle_id", required = true)
    protected String eqptenregistEqptsocleId;
	@Transient
    @XmlElement(name = "eqptenregist_descriptif", required = true, defaultValue = "NR")
    protected String eqptenregistDescriptif;
	@Transient
    @XmlElement(name = "eqptenregist_parametremesdet", required = true)
    protected ParametresMesuresListe eqptenregistParametremesdet;
	@Transient
    @XmlElement(name = "eqptenregist_tensionnominale", required = true)
    protected TensionsNominalesListe eqptenregistTensionnominale;
	@Transient
    @XmlElement(name = "eqptenregist_nbvoies")
    protected Integer eqptenregistNbvoies;
	@Transient
    @XmlElement(name = "eqptenregist_typesignal", required = true)
    protected String eqptenregistTypesignal;
	@Column(name = "eqptenregist_pjmaintenance")
    @XmlElement(name = "eqptenregist_pjmaintenance")
    protected String eqptenregistPjmaintenance;
	@Column(name = "eqptenregist_pjsuivimetrologique")
    @XmlElement(name = "eqptenregist_pjsuivimetrologique")
    protected String eqptenregistPjsuivimetrologique;
	@Column(name = "id_chg")
	@XmlTransient
	private Integer idChg;

    /**
     * Gets the value of the eqptenregistEqptsocleId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptenregist_eqptsocle_id")
    public String getEqptenregistEqptsocleId() {
    	if (eqptenregistEqptsocleId == null) {
    		manageNullException("eqptenregist_eqptsocle_id");
    	}
        return eqptenregistEqptsocleId;
    }

    /**
     * Sets the value of the eqptenregistEqptsocleId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEqptenregistEqptsocleId(String value) {
        this.eqptenregistEqptsocleId = value;
    }

    /**
     * Gets the value of the eqptenregistDescriptif property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptenregist_descriptif")
    public String getEqptenregistDescriptif() {
    	if (eqptenregistDescriptif == null) {
    		manageNullException("eqptenregist_descriptif");
    	}
        return eqptenregistDescriptif;
    }

    /**
     * Sets the value of the eqptenregistDescriptif property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEqptenregistDescriptif(String value) {
        this.eqptenregistDescriptif = value;
    }

    /**
     * Gets the value of the eqptenregistParametremesdet property.
     * 
     * @return
     *     possible object is
     *     {@link ParametresMesuresListe }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptenregist_parametremesdet")
    public String getEqptenregistParametremesdet() {
        return ParametresMesuresListe.getValue(PatrimoineDAO.PAT_EQPT_ENREGISTREUR, "eqptenregist_parametremesdet", eqptenregistParametremesdet, true);
    }

    /**
     * Sets the value of the eqptenregistParametremesdet property.
     * 
     * @param value
     *     allowed object is
     *     {@link ParametresMesuresListe }
     *     
     */
    public void setEqptenregistParametremesdet(String value) {
        this.eqptenregistParametremesdet = ParametresMesuresListe.fromValue(PatrimoineDAO.PAT_EQPT_ENREGISTREUR, "eqptenregist_parametremesdet", value);
    }

    /**
     * Gets the value of the eqptenregistTensionnominale property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptenregist_tensionnominale")
    public String getEqptenregistTensionnominale() {
        return TensionsNominalesListe.getValue(PatrimoineDAO.PAT_EQPT_ENREGISTREUR, "eqptenregist_tensionnominale", eqptenregistTensionnominale, true);
    }

    /**
     * Sets the value of the eqptenregistTensionnominale property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEqptenregistTensionnominale(String value) {
        this.eqptenregistTensionnominale = TensionsNominalesListe.fromValue(PatrimoineDAO.PAT_EQPT_ENREGISTREUR, "eqptenregist_tensionnominale", value);
    }

    /**
     * Gets the value of the eqptenregistNbvoies property.
     * 
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptenregist_nbvoies")
    public int getEqptenregistNbvoies() {
    	if (eqptenregistNbvoies == null) {
    		manageNullException("eqptenregist_nbvoies");
    	}
        return eqptenregistNbvoies;
    }

    /**
     * Sets the value of the eqptenregistNbvoies property.
     * 
     */
    public void setEqptenregistNbvoies(int value) {
        this.eqptenregistNbvoies = value;
    }

    /**
     * Gets the value of the eqptenregistTypesignal property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptenregist_typesignal")
    public String getEqptenregistTypesignal() {
    	if (eqptenregistTypesignal == null) {
    		manageNullException("eqptenregist_typesignal");
    	}
        return eqptenregistTypesignal;
    }

    /**
     * Sets the value of the eqptenregistTypesignal property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEqptenregistTypesignal(String value) {
        this.eqptenregistTypesignal = value;
    }

    /**
     * Gets the value of the eqptenregistPjmaintenance property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEqptenregistPjmaintenance() {
        return eqptenregistPjmaintenance;
    }

    /**
     * Sets the value of the eqptenregistPjmaintenance property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEqptenregistPjmaintenance(String value) {
        this.eqptenregistPjmaintenance = value;
    }

    /**
     * Gets the value of the eqptenregistPjsuivimetrologique property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEqptenregistPjsuivimetrologique() {
        return eqptenregistPjsuivimetrologique;
    }

    /**
     * Sets the value of the eqptenregistPjsuivimetrologique property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEqptenregistPjsuivimetrologique(String value) {
        this.eqptenregistPjsuivimetrologique = value;
    }

	public void setIdChg(Integer idChg) {
		this.idChg = idChg;
	}

    private void manageNullException(String champ) {
    	throw new IllegalArgumentException("Table " + PatrimoineDAO.PAT_EQPT_ENREGISTREUR + " - Champs " + champ + " - Valeur 'Null' ou non permise par le XSD.");
	}

}
