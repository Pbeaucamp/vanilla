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
 * <p>Java class for typeEquipementVerin complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="typeEquipementVerin">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="eqptverin_eqptsocle_id" type="{}equipementIDType"/>
 *         &lt;element name="eqptverin_descriptif" type="{}stringRestricted3000Type"/>
 *         &lt;element name="eqptverin_pjmaintenance" type="{}stringRestricted3000Type" minOccurs="0"/>
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
@Table (name = PatrimoineDAO.PAT_EQPT_VERIN)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "typeEquipementVerin", propOrder = {
    "eqptverinEqptsocleId",
    "eqptverinDescriptif",
    "eqptverinPjmaintenance"
})
public class TypeEquipementVerin {

	@Id
	@Column(name = "eqptverin_id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@XmlTransient
    protected int id;
	@Transient
    @XmlElement(name = "eqptverin_eqptsocle_id", required = true)
    protected String eqptverinEqptsocleId;
	@Transient
    @XmlElement(name = "eqptverin_descriptif", required = true, defaultValue = "NR")
    protected String eqptverinDescriptif;
	@Column(name = "eqptverin_pjmaintenance")
    @XmlElement(name = "eqptverin_pjmaintenance")
    protected String eqptverinPjmaintenance;
	@Column(name = "id_chg")
	@XmlTransient
	private Integer idChg;

    /**
     * Gets the value of the eqptverinEqptsocleId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptverin_eqptsocle_id")
    public String getEqptverinEqptsocleId() {
    	if (eqptverinEqptsocleId == null) {
    		manageNullException("eqptverin_eqptsocle_id");
    	}
        return eqptverinEqptsocleId;
    }

    /**
     * Sets the value of the eqptverinEqptsocleId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEqptverinEqptsocleId(String value) {
        this.eqptverinEqptsocleId = value;
    }

    /**
     * Gets the value of the eqptverinDescriptif property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptverin_descriptif")
    public String getEqptverinDescriptif() {
    	if (eqptverinDescriptif == null) {
    		manageNullException("eqptverin_descriptif");
    	}
        return eqptverinDescriptif;
    }

    /**
     * Sets the value of the eqptverinDescriptif property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEqptverinDescriptif(String value) {
        this.eqptverinDescriptif = value;
    }

    /**
     * Gets the value of the eqptverinPjmaintenance property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEqptverinPjmaintenance() {
        return eqptverinPjmaintenance;
    }

    /**
     * Sets the value of the eqptverinPjmaintenance property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEqptverinPjmaintenance(String value) {
        this.eqptverinPjmaintenance = value;
    }

	public void setIdChg(Integer idChg) {
		this.idChg = idChg;
	}

    private void manageNullException(String champ) {
    	throw new IllegalArgumentException("Table " + PatrimoineDAO.PAT_EQPT_VERIN + " - Champs " + champ + " - Valeur 'Null' ou non permise par le XSD.");
	}

}
