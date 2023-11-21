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
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour typeEquipementVentouse complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="typeEquipementVentouse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="eqptventouse_eqptsocle_id" type="{}equipementIDType"/>
 *         &lt;element name="eqptventouse_descriptif" type="{}stringRestricted3000Type"/>
 *         &lt;element name="eqptventouse_diametre" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="eqptventouse_pressionnominale" type="{}pressionsNominalesListe"/>
 *         &lt;element name="eqptventouse_dimension" type="{}stringRestricted255Type" minOccurs="0"/>
 *         &lt;element name="eqptventouse_type" type="{}TypesVentouseListe"/>
 *         &lt;element name="eqptventouse_pjmaintenance" type="{}stringRestricted3000Type" minOccurs="0"/>
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
@Table (name = PatrimoineDAO.PAT_EQPT_VENTOUSE)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "typeEquipementVentouse", propOrder = {
    "eqptventouseEqptsocleId",
    "eqptventouseDescriptif",
    "eqptventouseDiametre",
    "eqptventousePressionnominale",
    "eqptventouseDimension",
    "eqptventouseType",
    "eqptventousePjmaintenance"
})
public class TypeEquipementVentouse {

	@Id
	@Column(name = "eqptventouse_id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@XmlTransient
    protected int id;
	@Transient
    @XmlElement(name = "eqptventouse_eqptsocle_id", required = true)
    protected String eqptventouseEqptsocleId;
	@Transient
    @XmlElement(name = "eqptventouse_descriptif", required = true, defaultValue = "NR")
    protected String eqptventouseDescriptif;
	@Transient
    @XmlElement(name = "eqptventouse_diametre")
    protected Integer eqptventouseDiametre;
	@Transient
    @XmlElement(name = "eqptventouse_pressionnominale", required = true)
    @XmlSchemaType(name = "string")
    protected PressionsNominalesListe eqptventousePressionnominale;
	@Column(name = "eqptventouse_dimension")
    @XmlElement(name = "eqptventouse_dimension")
    protected String eqptventouseDimension;
	@Transient
    @XmlElement(name = "eqptventouse_type", required = true)
    @XmlSchemaType(name = "string")
    protected TypesVentouseListe eqptventouseType;
	@Column(name = "eqptventouse_pjmaintenance")
    @XmlElement(name = "eqptventouse_pjmaintenance")
    protected String eqptventousePjmaintenance;
	@Column(name = "id_chg")
	@XmlTransient
	private Integer idChg;

    /**
     * Obtient la valeur de la propriété eqptventouseEqptsocleId.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptventouse_eqptsocle_id")
    public String getEqptventouseEqptsocleId() {
    	if (eqptventouseEqptsocleId == null) {
    		manageNullException("eqptventouse_eqptsocle_id");
    	}
        return eqptventouseEqptsocleId;
    }

    /**
     * Définit la valeur de la propriété eqptventouseEqptsocleId.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEqptventouseEqptsocleId(String value) {
        this.eqptventouseEqptsocleId = value;
    }

    /**
     * Obtient la valeur de la propriété eqptventouseDescriptif.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptventouse_descriptif")
    public String getEqptventouseDescriptif() {
    	if (eqptventouseDescriptif == null) {
    		manageNullException("eqptventouse_descriptif");
    	}
        return eqptventouseDescriptif;
    }

    /**
     * Définit la valeur de la propriété eqptventouseDescriptif.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEqptventouseDescriptif(String value) {
        this.eqptventouseDescriptif = value;
    }

    /**
     * Obtient la valeur de la propriété eqptventouseDiametre.
     * 
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptventouse_diametre")
    public Integer getEqptventouseDiametre() {
    	if (eqptventouseDiametre == null) {
    		manageNullException("eqptventouse_diametre");
    	}
        return eqptventouseDiametre;
    }

    /**
     * Définit la valeur de la propriété eqptventouseDiametre.
     * 
     */
    public void setEqptventouseDiametre(Integer value) {
        this.eqptventouseDiametre = value;
    }

    /**
     * Obtient la valeur de la propriété eqptventousePressionnominale.
     * 
     * @return
     *     possible object is
     *     {@link PressionsNominalesListe }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptventouse_pressionnominale")
    public String getEqptventousePressionnominale() {
		return PressionsNominalesListe.getValue(PatrimoineDAO.PAT_EQPT_VENTOUSE, "eqptventouse_pressionnominale", eqptventousePressionnominale, true);
    }

    /**
     * Définit la valeur de la propriété eqptventousePressionnominale.
     * 
     * @param value
     *     allowed object is
     *     {@link PressionsNominalesListe }
     *     
     */
    public void setEqptventousePressionnominale(String value) {
    	this.eqptventousePressionnominale = PressionsNominalesListe.fromValue(PatrimoineDAO.PAT_EQPT_VENTOUSE, "eqptventouse_pressionnominale", value);
    }

    /**
     * Obtient la valeur de la propriété eqptventouseDimension.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEqptventouseDimension() {
        return eqptventouseDimension;
    }

    /**
     * Définit la valeur de la propriété eqptventouseDimension.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEqptventouseDimension(String value) {
        this.eqptventouseDimension = value;
    }

    /**
     * Obtient la valeur de la propriété eqptventouseType.
     * 
     * @return
     *     possible object is
     *     {@link TypesVentouseListe }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptventouse_type")
    public String getEqptventouseType() {
    	return TypesVentouseListe.getValue(PatrimoineDAO.PAT_EQPT_VENTOUSE, "eqptventouse_type", eqptventouseType, true);
    }

    /**
     * Définit la valeur de la propriété eqptventouseType.
     * 
     * @param value
     *     allowed object is
     *     {@link TypesVentouseListe }
     *     
     */
    public void setEqptventouseType(String value) {
    	this.eqptventouseType = TypesVentouseListe.fromValue(PatrimoineDAO.PAT_EQPT_VENTOUSE, "eqptventouse_type", value);
    }

    /**
     * Obtient la valeur de la propriété eqptventousePjmaintenance.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEqptventousePjmaintenance() {
        return eqptventousePjmaintenance;
    }

    /**
     * Définit la valeur de la propriété eqptventousePjmaintenance.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEqptventousePjmaintenance(String value) {
        this.eqptventousePjmaintenance = value;
    }

	public void setIdChg(Integer idChg) {
		this.idChg = idChg;
	}

    private void manageNullException(String champ) {
    	throw new IllegalArgumentException("Table " + PatrimoineDAO.PAT_EQPT_VENTOUSE + " - Champs " + champ + " - Valeur 'Null' ou non permise par le XSD.");
	}

}
