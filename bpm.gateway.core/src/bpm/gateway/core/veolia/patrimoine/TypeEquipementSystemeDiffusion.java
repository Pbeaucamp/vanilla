//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.8-b130911.1802 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2019.04.23 à 03:25:45 PM CEST 
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
 * <p>Classe Java pour typeEquipementSystemeDiffusion complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="typeEquipementSystemeDiffusion">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="eqptsystemediff_eqptsocle_id" type="{}equipementIDType"/>
 *         &lt;element name="eqptsystemediff_descriptif" type="{}stringRestricted3000Type"/>
 *         &lt;element name="eqptsystemediff_reference" type="{}stringRestricted255Type" minOccurs="0"/>
 *         &lt;element name="eqptsystemediff_nature" type="{}stringRestricted255Type" minOccurs="0"/>
 *         &lt;element name="eqptsystemediff_diametrecana" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
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
@Table (name = PatrimoineDAO.PAT_EQPT_SYSTEMEDIFFUSION)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "typeEquipementSystemeDiffusion", propOrder = {
    "eqptsystemediffEqptsocleId",
    "eqptsystemediffDescriptif",
    "eqptsystemediffReference",
    "eqptsystemediffNature",
    "eqptsystemediffDiametrecana"
})
public class TypeEquipementSystemeDiffusion {

	@Id
	@Column(name = "eqptsystemediff_id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@XmlTransient
    protected int id;
	@Transient
    @XmlElement(name = "eqptsystemediff_eqptsocle_id", required = true)
    protected String eqptsystemediffEqptsocleId;
	@Transient
    @XmlElement(name = "eqptsystemediff_descriptif", required = true, defaultValue = "NR")
    protected String eqptsystemediffDescriptif;
	@Column(name = "eqptsystemediff_reference")
    @XmlElement(name = "eqptsystemediff_reference")
    protected String eqptsystemediffReference;
    @Column(name = "eqptsystemediff_nature")
    @XmlElement(name = "eqptsystemediff_nature")
    protected String eqptsystemediffNature;
    @Column(name = "eqptsystemediff_diametrecana")
    @XmlElement(name = "eqptsystemediff_diametrecana")
    protected Integer eqptsystemediffDiametrecana;
	@Column(name = "id_chg")
	@XmlTransient
	private Integer idChg;

    /**
     * Obtient la valeur de la propriété eqptsystemediffEqptsocleId.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptsystemediff_eqptsocle_id")
    public String getEqptsystemediffEqptsocleId() {
    	if (eqptsystemediffEqptsocleId == null) {
    		manageNullException("eqptsystemediff_eqptsocle_id");
    	}
        return eqptsystemediffEqptsocleId;
    }

    /**
     * Définit la valeur de la propriété eqptsystemediffEqptsocleId.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEqptsystemediffEqptsocleId(String value) {
        this.eqptsystemediffEqptsocleId = value;
    }

    /**
     * Obtient la valeur de la propriété eqptsystemediffDescriptif.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptsystemediff_descriptif")
    public String getEqptsystemediffDescriptif() {
    	if (eqptsystemediffDescriptif == null) {
    		manageNullException("eqptsystemediff_descriptif");
    	}
        return eqptsystemediffDescriptif;
    }

    /**
     * Définit la valeur de la propriété eqptsystemediffDescriptif.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEqptsystemediffDescriptif(String value) {
        this.eqptsystemediffDescriptif = value;
    }

    /**
     * Obtient la valeur de la propriété eqptsystemediffReference.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEqptsystemediffReference() {
        return eqptsystemediffReference;
    }

    /**
     * Définit la valeur de la propriété eqptsystemediffReference.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEqptsystemediffReference(String value) {
        this.eqptsystemediffReference = value;
    }

    /**
     * Obtient la valeur de la propriété eqptsystemediffNature.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEqptsystemediffNature() {
        return eqptsystemediffNature;
    }

    /**
     * Définit la valeur de la propriété eqptsystemediffNature.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEqptsystemediffNature(String value) {
        this.eqptsystemediffNature = value;
    }

    /**
     * Obtient la valeur de la propriété eqptsystemediffDiametrecana.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getEqptsystemediffDiametrecana() {
        return eqptsystemediffDiametrecana;
    }

    /**
     * Définit la valeur de la propriété eqptsystemediffDiametrecana.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setEqptsystemediffDiametrecana(Integer value) {
        this.eqptsystemediffDiametrecana = value;
    }

	public void setIdChg(Integer idChg) {
		this.idChg = idChg;
	}

    private void manageNullException(String champ) {
    	throw new IllegalArgumentException("Table " + PatrimoineDAO.PAT_EQPT_SYSTEMEDIFFUSION + " - Champs " + champ + " - Valeur 'Null' ou non permise par le XSD.");
	}

}
