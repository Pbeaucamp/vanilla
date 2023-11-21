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
 * <p>Classe Java pour typeEquipementStabilisateurEcoulement complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="typeEquipementStabilisateurEcoulement">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="eqptstabecoul_eqptsocle_id" type="{}equipementIDType"/>
 *         &lt;element name="eqptstabecoul_descriptif" type="{}stringRestricted3000Type"/>
 *         &lt;element name="eqptstabecoul_diametre" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="eqptstabecoul_pressionnominale" type="{}pressionsNominalesListe"/>
 *         &lt;element name="eqptstabecoul_dimension" type="{}stringRestricted255Type" minOccurs="0"/>
 *         &lt;element name="eqptstabecoul_materiau" type="{}materiauxConstituantsListe"/>
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
@Table (name = PatrimoineDAO.PAT_EQPT_STABILISATEURECOULEMENT)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "typeEquipementStabilisateurEcoulement", propOrder = {
    "eqptstabecoulEqptsocleId",
    "eqptstabecoulDescriptif",
    "eqptstabecoulDiametre",
    "eqptstabecoulPressionnominale",
    "eqptstabecoulDimension",
    "eqptstabecoulMateriau"
})
public class TypeEquipementStabilisateurEcoulement {

	@Id
	@Column(name = "eqptstabecoul_id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@XmlTransient
    protected int id;
	@Transient
    @XmlElement(name = "eqptstabecoul_eqptsocle_id", required = true)
    protected String eqptstabecoulEqptsocleId;
	@Transient
    @XmlElement(name = "eqptstabecoul_descriptif", required = true, defaultValue = "NR")
    protected String eqptstabecoulDescriptif;
	@Transient
    @XmlElement(name = "eqptstabecoul_diametre")
    protected Integer eqptstabecoulDiametre;
	@Transient
    @XmlElement(name = "eqptstabecoul_pressionnominale", required = true)
    @XmlSchemaType(name = "string")
    protected PressionsNominalesListe eqptstabecoulPressionnominale;
	@Column(name = "eqptstabecoul_dimension")
    @XmlElement(name = "eqptstabecoul_dimension")
    protected String eqptstabecoulDimension;
	@Transient
    @XmlElement(name = "eqptstabecoul_materiau", required = true)
    @XmlSchemaType(name = "string")
    protected MateriauxConstituantsListe eqptstabecoulMateriau;
	@Column(name = "id_chg")
	@XmlTransient
	private Integer idChg;

    /**
     * Obtient la valeur de la propriété eqptstabecoulEqptsocleId.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptstabecoul_eqptsocle_id")
    public String getEqptstabecoulEqptsocleId() {
    	if (eqptstabecoulEqptsocleId == null) {
    		manageNullException("eqptstabecoul_eqptsocle_id");
    	}
        return eqptstabecoulEqptsocleId;
    }

    /**
     * Définit la valeur de la propriété eqptstabecoulEqptsocleId.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEqptstabecoulEqptsocleId(String value) {
        this.eqptstabecoulEqptsocleId = value;
    }

    /**
     * Obtient la valeur de la propriété eqptstabecoulDescriptif.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptstabecoul_descriptif")
    public String getEqptstabecoulDescriptif() {
    	if (eqptstabecoulDescriptif == null) {
    		manageNullException("eqptstabecoul_descriptif");
    	}
        return eqptstabecoulDescriptif;
    }

    /**
     * Définit la valeur de la propriété eqptstabecoulDescriptif.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEqptstabecoulDescriptif(String value) {
        this.eqptstabecoulDescriptif = value;
    }

    /**
     * Obtient la valeur de la propriété eqptstabecoulDiametre.
     * 
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptstabecoul_diametre")
    public Integer getEqptstabecoulDiametre() {
    	if (eqptstabecoulDiametre == null) {
    		manageNullException("eqptstabecoul_diametre");
    	}
        return eqptstabecoulDiametre;
    }

    /**
     * Définit la valeur de la propriété eqptstabecoulDiametre.
     * 
     */
    public void setEqptstabecoulDiametre(Integer value) {
        this.eqptstabecoulDiametre = value;
    }

    /**
     * Obtient la valeur de la propriété eqptstabecoulPressionnominale.
     * 
     * @return
     *     possible object is
     *     {@link PressionsNominalesListe }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptstabecoul_pressionnominale")
	public String getEqptstabecoulPressionnominale() {
		return PressionsNominalesListe.getValue(PatrimoineDAO.PAT_EQPT_STABILISATEURECOULEMENT, "eqptstabecoul_pressionnominale", eqptstabecoulPressionnominale, true);
	}

    /**
     * Définit la valeur de la propriété eqptstabecoulPressionnominale.
     * 
     * @param value
     *     allowed object is
     *     {@link PressionsNominalesListe }
     *     
     */
	public void setEqptstabecoulPressionnominale(String value) {
		this.eqptstabecoulPressionnominale = PressionsNominalesListe.fromValue(PatrimoineDAO.PAT_EQPT_STABILISATEURECOULEMENT, "eqptstabecoul_pressionnominale", value);
	}

    /**
     * Obtient la valeur de la propriété eqptstabecoulDimension.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEqptstabecoulDimension() {
        return eqptstabecoulDimension;
    }

    /**
     * Définit la valeur de la propriété eqptstabecoulDimension.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEqptstabecoulDimension(String value) {
        this.eqptstabecoulDimension = value;
    }

    /**
     * Obtient la valeur de la propriété eqptstabecoulMateriau.
     * 
     * @return
     *     possible object is
     *     {@link MateriauxConstituantsListe }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptstabecoul_materiau")
    public String getEqptstabecoulMateriau() {
		return MateriauxConstituantsListe.getValue(PatrimoineDAO.PAT_EQPT_STABILISATEURECOULEMENT, "eqptstabecoul_materiau", eqptstabecoulMateriau, true);
    }

    /**
     * Définit la valeur de la propriété eqptstabecoulMateriau.
     * 
     * @param value
     *     allowed object is
     *     {@link MateriauxConstituantsListe }
     *     
     */
	public void setEqptstabecoulMateriau(String value) {
		this.eqptstabecoulMateriau = MateriauxConstituantsListe.fromValue(PatrimoineDAO.PAT_EQPT_STABILISATEURECOULEMENT, "eqptstabecoul_materiau", value);
	}

	public void setIdChg(Integer idChg) {
		this.idChg = idChg;
	}

	private void manageNullException(String champ) {
		throw new IllegalArgumentException("Table " + PatrimoineDAO.PAT_EQPT_STABILISATEURECOULEMENT + " - Champs " + champ + " - Valeur 'Null' ou non permise par le XSD.");
	}
}
