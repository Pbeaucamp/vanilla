//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.8-b130911.1802 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2020.01.29 à 03:15:22 PM CET 
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
 * <p>Classe Java pour typeEquipementAuscultationBarrage complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="typeEquipementAuscultationBarrage">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="eqptauscultationbar_eqptsocle_id" type="{}equipementIDType"/>
 *         &lt;element name="eqptauscultationbar_descriptif" type="{}stringRestricted3000Type"/>
 *         &lt;element name="eqptauscultationbar_type" type="{}TypeAuscultationbarrageListe"/>
 *         &lt;element name="eqptauscultationbar_supportcomm" type="{}supportsCommunicationListe"/>
 *         &lt;element name="eqptauscultationbar_capacite" type="{}stringRestricted3000Type"/>
 *         &lt;element name="eqptauscultationbar_nbentreesortietor" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="eqptauscultationbar_pjfichetechnique" type="{}stringRestricted3000Type"/>
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
@Table (name = PatrimoineDAO.PAT_EQPT_AUSCULTATIONBARRAGE)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "typeEquipementAuscultationBarrage", propOrder = {
    "eqptauscultationbarEqptsocleId",
    "eqptauscultationbarDescriptif",
    "eqptauscultationbarType",
    "eqptauscultationbarSupportcomm",
    "eqptauscultationbarCapacite",
    "eqptauscultationbarNbentreesortietor",
    "eqptauscultationbarPjfichetechnique"
})
public class TypeEquipementAuscultationBarrage {


	@Id
	@Column(name = "eqptauscultationbar_id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@XmlTransient
    protected int id;
	@Transient
    @XmlElement(name = "eqptauscultationbar_eqptsocle_id", required = true)
    protected String eqptauscultationbarEqptsocleId;
	@Transient
    @XmlElement(name = "eqptauscultationbar_descriptif", required = true, defaultValue = "NR")
    protected String eqptauscultationbarDescriptif;
	@Transient
    @XmlElement(name = "eqptauscultationbar_type", required = true)
    @XmlSchemaType(name = "string")
    protected TypeAuscultationbarrageListe eqptauscultationbarType;
	@Transient
    @XmlElement(name = "eqptauscultationbar_supportcomm", required = true)
    @XmlSchemaType(name = "string")
    protected SupportsCommunicationListe eqptauscultationbarSupportcomm;
	@Transient
    @XmlElement(name = "eqptauscultationbar_capacite", required = true, defaultValue = "NR")
    protected String eqptauscultationbarCapacite;
	@Column(name = "eqptauscultationbar_nbentreesortietor")
    @XmlElement(name = "eqptauscultationbar_nbentreesortietor", defaultValue = "999999999")
    protected Integer eqptauscultationbarNbentreesortietor;
	@Transient
    @XmlElement(name = "eqptauscultationbar_pjfichetechnique", required = true, defaultValue = "NR")
    protected String eqptauscultationbarPjfichetechnique;
	@Column(name = "id_chg")
	@XmlTransient
	private Integer idChg;

    /**
     * Obtient la valeur de la propriété eqptauscultationbarEqptsocleId.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptauscultationbar_eqptsocle_id")
    public String getEqptauscultationbarEqptsocleId() {
		if (eqptauscultationbarEqptsocleId == null) {
    		manageNullException("eqptauscultationbar_eqptsocle_id");
    	}
        return eqptauscultationbarEqptsocleId;
    }

    /**
     * Définit la valeur de la propriété eqptauscultationbarEqptsocleId.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEqptauscultationbarEqptsocleId(String value) {
        this.eqptauscultationbarEqptsocleId = value;
    }

    /**
     * Obtient la valeur de la propriété eqptauscultationbarDescriptif.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptauscultationbar_descriptif")
    public String getEqptauscultationbarDescriptif() {
		if (eqptauscultationbarDescriptif == null) {
    		manageNullException("eqptauscultationbar_descriptif");
    	}
        return eqptauscultationbarDescriptif;
    }

    /**
     * Définit la valeur de la propriété eqptauscultationbarDescriptif.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEqptauscultationbarDescriptif(String value) {
        this.eqptauscultationbarDescriptif = value;
    }

    /**
     * Obtient la valeur de la propriété eqptauscultationbarType.
     * 
     * @return
     *     possible object is
     *     {@link TypeAuscultationbarrageListe }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptauscultationbar_type")
    public String getEqptauscultationbarType() {
        return TypeAuscultationbarrageListe.getValue(PatrimoineDAO.PAT_EQPT_AUSCULTATIONBARRAGE, "eqptauscultationbar_type", eqptauscultationbarType, true);
    }

    /**
     * Définit la valeur de la propriété eqptauscultationbarType.
     * 
     * @param value
     *     allowed object is
     *     {@link TypeAuscultationbarrageListe }
     *     
     */
    public void setEqptauscultationbarType(String value) {
        this.eqptauscultationbarType = TypeAuscultationbarrageListe.fromValue(PatrimoineDAO.PAT_EQPT_AUSCULTATIONBARRAGE, "eqptauscultationbar_type", value);
    }

    /**
     * Obtient la valeur de la propriété eqptauscultationbarSupportcomm.
     * 
     * @return
     *     possible object is
     *     {@link SupportsCommunicationListe }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptauscultationbar_supportcomm")
    public String getEqptauscultationbarSupportcomm() {
        return SupportsCommunicationListe.getValue(PatrimoineDAO.PAT_EQPT_AUSCULTATIONBARRAGE, "eqptauscultationbar_supportcomm", eqptauscultationbarSupportcomm, true);
    }

    /**
     * Définit la valeur de la propriété eqptauscultationbarSupportcomm.
     * 
     * @param value
     *     allowed object is
     *     {@link SupportsCommunicationListe }
     *     
     */
    public void setEqptauscultationbarSupportcomm(String value) {
        this.eqptauscultationbarSupportcomm = SupportsCommunicationListe.fromValue(PatrimoineDAO.PAT_EQPT_AUSCULTATIONBARRAGE, "eqptauscultationbar_supportcomm", value);
    }

    /**
     * Obtient la valeur de la propriété eqptauscultationbarCapacite.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptauscultationbar_capacite")
    public String getEqptauscultationbarCapacite() {
		if (eqptauscultationbarCapacite == null) {
    		manageNullException("eqptauscultationbar_capacite");
    	}
        return eqptauscultationbarCapacite;
    }

    /**
     * Définit la valeur de la propriété eqptauscultationbarCapacite.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEqptauscultationbarCapacite(String value) {
        this.eqptauscultationbarCapacite = value;
    }

    /**
     * Obtient la valeur de la propriété eqptauscultationbarNbentreesortietor.
     * 
     */
    public Integer getEqptauscultationbarNbentreesortietor() {
        return eqptauscultationbarNbentreesortietor;
    }

    /**
     * Définit la valeur de la propriété eqptauscultationbarNbentreesortietor.
     * 
     */
    public void setEqptauscultationbarNbentreesortietor(Integer value) {
        this.eqptauscultationbarNbentreesortietor = value;
    }

    /**
     * Obtient la valeur de la propriété eqptauscultationbarPjfichetechnique.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptauscultationbar_pjfichetechnique")
    public String getEqptauscultationbarPjfichetechnique() {
		if (eqptauscultationbarPjfichetechnique == null) {
    		manageNullException("eqptauscultationbar_pjfichetechnique");
    	}
        return eqptauscultationbarPjfichetechnique;
    }

    /**
     * Définit la valeur de la propriété eqptauscultationbarPjfichetechnique.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEqptauscultationbarPjfichetechnique(String value) {
        this.eqptauscultationbarPjfichetechnique = value;
    }

	public void setIdChg(Integer idChg) {
		this.idChg = idChg;
	}

    private void manageNullException(String champ) {
    	throw new IllegalArgumentException("Table " + PatrimoineDAO.PAT_EQPT_AUSCULTATIONBARRAGE + " - Champs " + champ + " - Valeur 'Null' ou non permise par le XSD.");
	}

}
