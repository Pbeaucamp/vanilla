//
// Ce fichier a �t� g�n�r� par l'impl�mentation de r�f�rence JavaTM Architecture for XML Binding (JAXB), v2.2.8-b130911.1802 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apport�e � ce fichier sera perdue lors de la recompilation du sch�ma source. 
// G�n�r� le : 2019.04.23 � 03:25:45 PM CEST 
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
 * <p>Classe Java pour typeEquipementVidange complex type.
 * 
 * <p>Le fragment de sch�ma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="typeEquipementVidange">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="eqptvidange_eqptsocle_id" type="{}equipementIDType"/>
 *         &lt;element name="eqptvidange_descriptif" type="{}stringRestricted3000Type"/>
 *         &lt;element name="eqptvidange_diametre" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="eqptvidange_pressionnominale" type="{}pressionsNominalesListe"/>
 *         &lt;element name="eqptvidange_dimension" type="{}stringRestricted255Type" minOccurs="0"/>
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
@Table (name = PatrimoineDAO.PAT_EQPT_VIDANGE)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "typeEquipementVidange", propOrder = {
    "eqptvidangeEqptsocleId",
    "eqptvidangeDescriptif",
    "eqptvidangeDiametre",
    "eqptvidangePressionnominale",
    "eqptvidangeDimension"
})
public class TypeEquipementVidange {

	@Id
	@Column(name = "eqptvidange_id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@XmlTransient
    protected int id;
	@Transient
    @XmlElement(name = "eqptvidange_eqptsocle_id", required = true)
    protected String eqptvidangeEqptsocleId;
	@Transient
    @XmlElement(name = "eqptvidange_descriptif", required = true, defaultValue = "NR")
    protected String eqptvidangeDescriptif;
	@Transient
    @XmlElement(name = "eqptvidange_diametre")
    protected Integer eqptvidangeDiametre;
	@Transient
    @XmlElement(name = "eqptvidange_pressionnominale", required = true)
    @XmlSchemaType(name = "string")
    protected PressionsNominalesListe eqptvidangePressionnominale;
	@Column(name = "eqptvidange_dimension")
    @XmlElement(name = "eqptvidange_dimension")
    protected String eqptvidangeDimension;
	@Column(name = "id_chg")
	@XmlTransient
	private Integer idChg;

    /**
     * Obtient la valeur de la propri�t� eqptvidangeEqptsocleId.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptvidange_eqptsocle_id")
    public String getEqptvidangeEqptsocleId() {
    	if (eqptvidangeEqptsocleId == null) {
    		manageNullException("eqptvidange_eqptsocle_id");
    	}
        return eqptvidangeEqptsocleId;
    }

    /**
     * D�finit la valeur de la propri�t� eqptvidangeEqptsocleId.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEqptvidangeEqptsocleId(String value) {
        this.eqptvidangeEqptsocleId = value;
    }

    /**
     * Obtient la valeur de la propri�t� eqptvidangeDescriptif.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptvidange_descriptif")
    public String getEqptvidangeDescriptif() {
    	if (eqptvidangeDescriptif == null) {
    		manageNullException("eqptvidange_descriptif");
    	}
        return eqptvidangeDescriptif;
    }

    /**
     * D�finit la valeur de la propri�t� eqptvidangeDescriptif.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEqptvidangeDescriptif(String value) {
        this.eqptvidangeDescriptif = value;
    }

    /**
     * Obtient la valeur de la propri�t� eqptvidangeDiametre.
     * 
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptvidange_diametre")
    public Integer getEqptvidangeDiametre() {
    	if (eqptvidangeDiametre == null) {
    		manageNullException("eqptvidange_diametre");
    	}
        return eqptvidangeDiametre;
    }

    /**
     * D�finit la valeur de la propri�t� eqptvidangeDiametre.
     * 
     */
    public void setEqptvidangeDiametre(Integer value) {
        this.eqptvidangeDiametre = value;
    }

    /**
     * Obtient la valeur de la propri�t� eqptvidangePressionnominale.
     * 
     * @return
     *     possible object is
     *     {@link PressionsNominalesListe }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptvidange_pressionnominale")
    public String getEqptvidangePressionnominale() {
		return PressionsNominalesListe.getValue(PatrimoineDAO.PAT_EQPT_VIDANGE, "eqptvidange_pressionnominale", eqptvidangePressionnominale, true);
    }

    /**
     * D�finit la valeur de la propri�t� eqptvidangePressionnominale.
     * 
     * @param value
     *     allowed object is
     *     {@link PressionsNominalesListe }
     *     
     */
    public void setEqptvidangePressionnominale(String value) {
    	this.eqptvidangePressionnominale = PressionsNominalesListe.fromValue(PatrimoineDAO.PAT_EQPT_VIDANGE, "eqptvidange_pressionnominale", value);
    }

    /**
     * Obtient la valeur de la propri�t� eqptvidangeDimension.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEqptvidangeDimension() {
        return eqptvidangeDimension;
    }

    /**
     * D�finit la valeur de la propri�t� eqptvidangeDimension.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEqptvidangeDimension(String value) {
        this.eqptvidangeDimension = value;
    }

	public void setIdChg(Integer idChg) {
		this.idChg = idChg;
	}

    private void manageNullException(String champ) {
    	throw new IllegalArgumentException("Table " + PatrimoineDAO.PAT_EQPT_VIDANGE + " - Champs " + champ + " - Valeur 'Null' ou non permise par le XSD.");
	}

}
