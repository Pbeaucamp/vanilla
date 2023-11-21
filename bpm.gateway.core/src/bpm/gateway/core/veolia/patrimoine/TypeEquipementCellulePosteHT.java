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
 * <p>Java class for typeEquipementCellulePosteHT complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="typeEquipementCellulePosteHT">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="eqptcelluleposteht_eqptsocle_id" type="{}equipementIDType"/>
 *         &lt;element name="eqptcelluleposteht_descriptif" type="{}stringRestricted3000Type"/>
 *         &lt;element name="eqptcelluleposteht_fonctioncelluleht" type="{}fonctionsCellulesHTListe" minOccurs="0"/>
 *         &lt;element name="eqptcelluleposteht_tensionnominale" type="{}tensionsNominalesListe" minOccurs="0"/>
 *         &lt;element name="eqptcelluleposteht_pilotabledistance" type="{}pilotablesDistanceListe"/>
 *         &lt;element name="eqptcelluleposteht_nbcellule" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="1"/>
 *         &lt;element name="eqptcelluleposteht_pjmaintenance" type="{}stringRestricted3000Type" minOccurs="0"/>
 *         &lt;element name="eqptcelluleposteht_pjfichetechnique" type="{}stringRestricted3000Type" minOccurs="1"/>
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
@Table (name = PatrimoineDAO.PAT_EQPT_CELLULEPOSTEHT)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "typeEquipementCellulePosteHT", propOrder = {
    "eqptcellulepostehtEqptsocleId",
    "eqptcellulepostehtDescriptif",
    "eqptcellulepostehtFonctioncelluleht",
    "eqptcellulepostehtTensionnominale",
    "eqptcellulepostehtPilotabledistance",
    "eqptcellulepostehtNbcellule",
    "eqptcellulepostehtPjmaintenance",
    "eqptcellulepostehtPjfichetechnique"
})
public class TypeEquipementCellulePosteHT {

	@Id
	@Column(name = "eqptcelluleposteht_id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@XmlTransient
    protected int id;
	@Transient
    @XmlElement(name = "eqptcelluleposteht_eqptsocle_id", required = true)
    protected String eqptcellulepostehtEqptsocleId;
	@Transient
    @XmlElement(name = "eqptcelluleposteht_descriptif", required = true, defaultValue = "NR")
    protected String eqptcellulepostehtDescriptif;
	@Transient
    @XmlElement(name = "eqptcelluleposteht_fonctioncelluleht")
    protected FonctionsCellulesHTListe eqptcellulepostehtFonctioncelluleht;
	@Transient
    @XmlElement(name = "eqptcelluleposteht_tensionnominale")
    protected TensionsNominalesListe eqptcellulepostehtTensionnominale;
	@Transient
    @XmlElement(name = "eqptcelluleposteht_pilotabledistance", required = true)
    protected PilotablesDistanceListe eqptcellulepostehtPilotabledistance;
	@Transient
    @XmlElement(name = "eqptcelluleposteht_nbcellule", required = true)
    protected Integer eqptcellulepostehtNbcellule;
	@Column(name = "eqptcelluleposteht_pjmaintenance")
    @XmlElement(name = "eqptcelluleposteht_pjmaintenance")
    protected String eqptcellulepostehtPjmaintenance;
	@Transient
    @XmlElement(name = "eqptcelluleposteht_pjfichetechnique", required = true)
    protected String eqptcellulepostehtPjfichetechnique;
	@Column(name = "id_chg")
	@XmlTransient
	private Integer idChg;

    /**
     * Gets the value of the eqptcellulepostehtEqptsocleId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptcelluleposteht_eqptsocle_id")
    public String getEqptcellulepostehtEqptsocleId() {
    	if (eqptcellulepostehtEqptsocleId == null) {
    		manageNullException("eqptcelluleposteht_eqptsocle_id");
    	}
        return eqptcellulepostehtEqptsocleId;
    }

    /**
     * Sets the value of the eqptcellulepostehtEqptsocleId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEqptcellulepostehtEqptsocleId(String value) {
        this.eqptcellulepostehtEqptsocleId = value;
    }

    /**
     * Gets the value of the eqptcellulepostehtDescriptif property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptcelluleposteht_descriptif")
    public String getEqptcellulepostehtDescriptif() {
    	if (eqptcellulepostehtDescriptif == null) {
    		manageNullException("eqptcelluleposteht_descriptif");
    	}
        return eqptcellulepostehtDescriptif;
    }

    /**
     * Sets the value of the eqptcellulepostehtDescriptif property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEqptcellulepostehtDescriptif(String value) {
        this.eqptcellulepostehtDescriptif = value;
    }

    /**
     * Gets the value of the eqptcellulepostehtFonctioncelluleht property.
     * 
     * @return
     *     possible object is
     *     {@link FonctionsCellulesHTListe }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptcelluleposteht_fonctioncelluleht")
    public String getEqptcellulepostehtFonctioncelluleht() {
        return FonctionsCellulesHTListe.getValue(PatrimoineDAO.PAT_EQPT_CELLULEPOSTEHT, "eqptcelluleposteht_fonctioncelluleht", eqptcellulepostehtFonctioncelluleht, false);
    }

    /**
     * Sets the value of the eqptcellulepostehtFonctioncelluleht property.
     * 
     * @param value
     *     allowed object is
     *     {@link FonctionsCellulesHTListe }
     *     
     */
    public void setEqptcellulepostehtFonctioncelluleht(String value) {
        this.eqptcellulepostehtFonctioncelluleht = FonctionsCellulesHTListe.fromValue(PatrimoineDAO.PAT_EQPT_CELLULEPOSTEHT, "eqptcelluleposteht_fonctioncelluleht", value);
    }

    /**
     * Gets the value of the eqptcellulepostehtTensionnominale property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Access(AccessType.PROPERTY)
	@Column(name = "eqptcelluleposteht_tensionnominale")
    public String getEqptcellulepostehtTensionnominale() {
    	return TensionsNominalesListe.getValue(PatrimoineDAO.PAT_EQPT_CELLULEPOSTEHT, "eqptcelluleposteht_tensionnominale", eqptcellulepostehtTensionnominale, false);
    }

    /**
     * Sets the value of the eqptcellulepostehtTensionnominale property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEqptcellulepostehtTensionnominale(String value) {
    	this.eqptcellulepostehtTensionnominale = TensionsNominalesListe.fromValue(PatrimoineDAO.PAT_EQPT_CELLULEPOSTEHT, "eqptcelluleposteht_tensionnominale", value);
    }

    /**
     * Gets the value of the eqptcellulepostehtPilotabledistance property.
     * 
     * @return
     *     possible object is
     *     {@link PilotablesDistanceListe }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptcelluleposteht_pilotabledistance")
    public String getEqptcellulepostehtPilotabledistance() {
        return PilotablesDistanceListe.getValue(PatrimoineDAO.PAT_EQPT_CELLULEPOSTEHT, "eqptcelluleposteht_pilotabledistance", eqptcellulepostehtPilotabledistance);
    }

    /**
     * Sets the value of the eqptcellulepostehtPilotabledistance property.
     * 
     * @param value
     *     allowed object is
     *     {@link PilotablesDistanceListe }
     *     
     */
    public void setEqptcellulepostehtPilotabledistance(String value) {
        this.eqptcellulepostehtPilotabledistance = PilotablesDistanceListe.fromValue(PatrimoineDAO.PAT_EQPT_CELLULEPOSTEHT, "eqptcelluleposteht_pilotabledistance", value);
    }

    /**
     * Gets the value of the eqptcellulepostehtNbcellule property.
     * 
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptcelluleposteht_nbcellule")
    public int getEqptcellulepostehtNbcellule() {
    	if (eqptcellulepostehtNbcellule == null) {
    		manageNullException("eqptcelluleposteht_nbcellule");
    	}
        return eqptcellulepostehtNbcellule;
    }

    /**
     * Sets the value of the eqptcellulepostehtNbcellule property.
     * 
     */
    public void setEqptcellulepostehtNbcellule(int value) {
        this.eqptcellulepostehtNbcellule = value;
    }

    /**
     * Gets the value of the eqptcellulepostehtPjmaintenance property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEqptcellulepostehtPjmaintenance() {
        return eqptcellulepostehtPjmaintenance;
    }

    /**
     * Sets the value of the eqptcellulepostehtPjmaintenance property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEqptcellulepostehtPjmaintenance(String value) {
        this.eqptcellulepostehtPjmaintenance = value;
    }

    /**
     * Gets the value of the eqptcellulepostehtPjfichetechnique property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptcelluleposteht_pjfichetechnique")
    public String getEqptcellulepostehtPjfichetechnique() {
		if (eqptcellulepostehtPjfichetechnique == null) {
    		manageNullException("eqptcelluleposteht_pjfichetechnique");
    	}
        return eqptcellulepostehtPjfichetechnique;
    }

    /**
     * Sets the value of the eqptcellulepostehtPjfichetechnique property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEqptcellulepostehtPjfichetechnique(String value) {
        this.eqptcellulepostehtPjfichetechnique = value;
    }

	public void setIdChg(Integer idChg) {
		this.idChg = idChg;
	}

    private void manageNullException(String champ) {
    	throw new IllegalArgumentException("Table " + PatrimoineDAO.PAT_EQPT_CELLULEPOSTEHT + " - Champs " + champ + " - Valeur 'Null' ou non permise par le XSD.");
	}

}
