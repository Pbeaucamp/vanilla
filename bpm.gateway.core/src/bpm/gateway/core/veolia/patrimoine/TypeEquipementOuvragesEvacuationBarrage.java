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
 * <p>Classe Java pour typeEquipementOuvragesEvacuationBarrage complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="typeEquipementOuvragesEvacuationBarrage">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="eqptouvevacuation_eqptsocle_id" type="{}equipementIDType"/>
 *         &lt;element name="eqptouvevacuation_descriptif" type="{}stringRestricted3000Type"/>
 *         &lt;element name="eqptouvevacuation_diametre" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="eqptouvevacuation_dimension" type="{}stringRestricted255Type" minOccurs="0"/>
 *         &lt;element name="eqptouvevacuation_pressionnominale" type="{}pressionsNominalesListe"/>
 *         &lt;element name="eqptouvevacuation_type" type="{}TypeEvacuationListe"/>
 *         &lt;element name="eqptouvevacuation_typevanne" type="{}TypeVanneEvacuationListe"/>
 *         &lt;element name="eqptouvevacuation_tigeguidage" type="{}TigeGuidageListe"/>
 *         &lt;element name="eqptouvevacuation_grille" type="{}GrilleEvacuationListe"/>
 *         &lt;element name="eqptouvevacuation_verin" type="{}stringRestricted255Type"/>
 *         &lt;element name="eqptouvevacuation_dimensionverin" type="{}stringRestricted255Type"/>
 *         &lt;element name="eqptouvevacuation_pjfichetechniqueclapet" type="{}stringRestricted3000Type"/>
 *         &lt;element name="eqptouvevacuation_pjfichetechniquevanne" type="{}stringRestricted3000Type"/>
 *         &lt;element name="eqptouvevacuation_pjfichetechniqueverin" type="{}stringRestricted3000Type"/>
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
@Table (name = PatrimoineDAO.PAT_EQPT_OUVRAGESEVACUATIONBARRAGE)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "typeEquipementOuvragesEvacuationBarrage", propOrder = {
    "eqptouvevacuationEqptsocleId",
    "eqptouvevacuationDescriptif",
    "eqptouvevacuationDiametre",
    "eqptouvevacuationDimension",
    "eqptouvevacuationPressionnominale",
    "eqptouvevacuationType",
    "eqptouvevacuationTypevanne",
    "eqptouvevacuationTigeguidage",
    "eqptouvevacuationGrille",
    "eqptouvevacuationVerin",
    "eqptouvevacuationDimensionverin",
    "eqptouvevacuationPjfichetechniqueclapet",
    "eqptouvevacuationPjfichetechniquevanne",
    "eqptouvevacuationPjfichetechniqueverin"
})
public class TypeEquipementOuvragesEvacuationBarrage {

	@Id
	@Column(name = "eqptouvevacuation_id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@XmlTransient
    protected int id;
	@Transient
    @XmlElement(name = "eqptouvevacuation_eqptsocle_id", required = true)
    protected String eqptouvevacuationEqptsocleId;
	@Transient
    @XmlElement(name = "eqptouvevacuation_descriptif", required = true, defaultValue = "NR")
    protected String eqptouvevacuationDescriptif;
	@Column(name = "eqptouvevacuation_diametre")
    @XmlElement(name = "eqptouvevacuation_diametre")
    protected Integer eqptouvevacuationDiametre;
	@Column(name = "eqptouvevacuation_dimension")
    @XmlElement(name = "eqptouvevacuation_dimension")
    protected String eqptouvevacuationDimension;
	@Transient
    @XmlElement(name = "eqptouvevacuation_pressionnominale", required = true, defaultValue = "NR")
    @XmlSchemaType(name = "string")
    protected PressionsNominalesListe eqptouvevacuationPressionnominale;
	@Transient
    @XmlElement(name = "eqptouvevacuation_type", required = true, defaultValue = "NR")
    @XmlSchemaType(name = "string")
    protected TypeEvacuationListe eqptouvevacuationType;
	@Transient
    @XmlElement(name = "eqptouvevacuation_typevanne", required = true)
    @XmlSchemaType(name = "string")
    protected TypeVanneEvacuationListe eqptouvevacuationTypevanne;
	@Transient
    @XmlElement(name = "eqptouvevacuation_tigeguidage", required = true, defaultValue = "NR")
    @XmlSchemaType(name = "string")
    protected TigeGuidageListe eqptouvevacuationTigeguidage;
	@Transient
    @XmlElement(name = "eqptouvevacuation_grille", required = true, defaultValue = "NR")
    @XmlSchemaType(name = "string")
    protected GrilleEvacuationListe eqptouvevacuationGrille;
	@Transient
    @XmlElement(name = "eqptouvevacuation_verin", required = true, defaultValue = "NR")
    protected String eqptouvevacuationVerin;
	@Transient
    @XmlElement(name = "eqptouvevacuation_dimensionverin", required = true, defaultValue = "NR")
    protected String eqptouvevacuationDimensionverin;
	@Transient
    @XmlElement(name = "eqptouvevacuation_pjfichetechniqueclapet", required = true, defaultValue = "NR")
    protected String eqptouvevacuationPjfichetechniqueclapet;
	@Transient
    @XmlElement(name = "eqptouvevacuation_pjfichetechniquevanne", required = true, defaultValue = "NR")
    protected String eqptouvevacuationPjfichetechniquevanne;
	@Transient
    @XmlElement(name = "eqptouvevacuation_pjfichetechniqueverin", required = true, defaultValue = "NR")
    protected String eqptouvevacuationPjfichetechniqueverin;
	@Column(name = "id_chg")
	@XmlTransient
	private Integer idChg;

    /**
     * Obtient la valeur de la propriété eqptouvevacuationEqptsocleId.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptouvevacuation_eqptsocle_id")
    public String getEqptouvevacuationEqptsocleId() {
		if (eqptouvevacuationEqptsocleId == null) {
    		manageNullException("eqptouvevacuation_eqptsocle_id");
    	}
        return eqptouvevacuationEqptsocleId;
    }

    /**
     * Définit la valeur de la propriété eqptouvevacuationEqptsocleId.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEqptouvevacuationEqptsocleId(String value) {
        this.eqptouvevacuationEqptsocleId = value;
    }

    /**
     * Obtient la valeur de la propriété eqptouvevacuationDescriptif.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptouvevacuation_descriptif")
    public String getEqptouvevacuationDescriptif() {
		if (eqptouvevacuationDescriptif == null) {
    		manageNullException("eqptouvevacuation_descriptif");
    	}
        return eqptouvevacuationDescriptif;
    }

    /**
     * Définit la valeur de la propriété eqptouvevacuationDescriptif.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEqptouvevacuationDescriptif(String value) {
        this.eqptouvevacuationDescriptif = value;
    }

    /**
     * Obtient la valeur de la propriété eqptouvevacuationDiametre.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getEqptouvevacuationDiametre() {
        return eqptouvevacuationDiametre;
    }

    /**
     * Définit la valeur de la propriété eqptouvevacuationDiametre.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setEqptouvevacuationDiametre(Integer value) {
        this.eqptouvevacuationDiametre = value;
    }

    /**
     * Obtient la valeur de la propriété eqptouvevacuationDimension.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEqptouvevacuationDimension() {
        return eqptouvevacuationDimension;
    }

    /**
     * Définit la valeur de la propriété eqptouvevacuationDimension.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEqptouvevacuationDimension(String value) {
        this.eqptouvevacuationDimension = value;
    }

    /**
     * Obtient la valeur de la propriété eqptouvevacuationPressionnominale.
     * 
     * @return
     *     possible object is
     *     {@link PressionsNominalesListe }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptouvevacuation_pressionnominale")
    public String getEqptouvevacuationPressionnominale() {
        return PressionsNominalesListe.getValue(PatrimoineDAO.PAT_EQPT_OUVRAGESEVACUATIONBARRAGE, "eqptouvevacuation_pressionnominale", eqptouvevacuationPressionnominale, true);
    }

    /**
     * Définit la valeur de la propriété eqptouvevacuationPressionnominale.
     * 
     * @param value
     *     allowed object is
     *     {@link PressionsNominalesListe }
     *     
     */
    public void setEqptouvevacuationPressionnominale(String value) {
        this.eqptouvevacuationPressionnominale = PressionsNominalesListe.fromValue(PatrimoineDAO.PAT_EQPT_OUVRAGESEVACUATIONBARRAGE, "eqptouvevacuation_pressionnominale", value);
    }

    /**
     * Obtient la valeur de la propriété eqptouvevacuationType.
     * 
     * @return
     *     possible object is
     *     {@link TypeEvacuationListe }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptouvevacuation_type")
    public String getEqptouvevacuationType() {
        return TypeEvacuationListe.getValue(PatrimoineDAO.PAT_EQPT_OUVRAGESEVACUATIONBARRAGE, "eqptouvevacuation_type", eqptouvevacuationType, true);
    }

    /**
     * Définit la valeur de la propriété eqptouvevacuationType.
     * 
     * @param value
     *     allowed object is
     *     {@link TypeEvacuationListe }
     *     
     */
    public void setEqptouvevacuationType(String value) {
        this.eqptouvevacuationType = TypeEvacuationListe.fromValue(PatrimoineDAO.PAT_EQPT_OUVRAGESEVACUATIONBARRAGE, "eqptouvevacuation_type", value);
    }

    /**
     * Obtient la valeur de la propriété eqptouvevacuationTypevanne.
     * 
     * @return
     *     possible object is
     *     {@link TypeVanneEvacuationListe }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptouvevacuation_typevanne")
    public String getEqptouvevacuationTypevanne() {
        return TypeVanneEvacuationListe.getValue(PatrimoineDAO.PAT_EQPT_OUVRAGESEVACUATIONBARRAGE, "eqptouvevacuation_typevanne", eqptouvevacuationTypevanne, true);
	}

    /**
     * Définit la valeur de la propriété eqptouvevacuationTypevanne.
     * 
     * @param value
     *     allowed object is
     *     {@link TypeVanneEvacuationListe }
     *     
     */
    public void setEqptouvevacuationTypevanne(String value) {
        this.eqptouvevacuationTypevanne = TypeVanneEvacuationListe.fromValue(PatrimoineDAO.PAT_EQPT_OUVRAGESEVACUATIONBARRAGE, "eqptouvevacuation_typevanne", value);
	}

    /**
     * Obtient la valeur de la propriété eqptouvevacuationTigeguidage.
     * 
     * @return
     *     possible object is
     *     {@link TigeGuidageListe }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptouvevacuation_tigeguidage")
    public String getEqptouvevacuationTigeguidage() {
        return TigeGuidageListe.getValue(PatrimoineDAO.PAT_EQPT_OUVRAGESEVACUATIONBARRAGE, "eqptouvevacuation_tigeguidage", eqptouvevacuationTigeguidage, true);
    }

    /**
     * Définit la valeur de la propriété eqptouvevacuationTigeguidage.
     * 
     * @param value
     *     allowed object is
     *     {@link TigeGuidageListe }
     *     
     */
    public void setEqptouvevacuationTigeguidage(String value) {
        this.eqptouvevacuationTigeguidage = TigeGuidageListe.fromValue(PatrimoineDAO.PAT_EQPT_OUVRAGESEVACUATIONBARRAGE, "eqptouvevacuation_tigeguidage", value);
    }

    /**
     * Obtient la valeur de la propriété eqptouvevacuationGrille.
     * 
     * @return
     *     possible object is
     *     {@link GrilleEvacuationListe }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptouvevacuation_grille")
    public String getEqptouvevacuationGrille() {
        return GrilleEvacuationListe.getValue(PatrimoineDAO.PAT_EQPT_OUVRAGESEVACUATIONBARRAGE, "eqptouvevacuation_grille", eqptouvevacuationGrille, true);
    }

    /**
     * Définit la valeur de la propriété eqptouvevacuationGrille.
     * 
     * @param value
     *     allowed object is
     *     {@link GrilleEvacuationListe }
     *     
     */
    public void setEqptouvevacuationGrille(String value) {
        this.eqptouvevacuationGrille = GrilleEvacuationListe.fromValue(PatrimoineDAO.PAT_EQPT_OUVRAGESEVACUATIONBARRAGE, "eqptouvevacuation_grille", value);
    }

    /**
     * Obtient la valeur de la propriété eqptouvevacuationVerin.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptouvevacuation_verin")
    public String getEqptouvevacuationVerin() {
		if (eqptouvevacuationVerin == null) {
    		manageNullException("eqptouvevacuation_verin");
    	}
        return eqptouvevacuationVerin;
    }

    /**
     * Définit la valeur de la propriété eqptouvevacuationVerin.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEqptouvevacuationVerin(String value) {
        this.eqptouvevacuationVerin = value;
    }

    /**
     * Obtient la valeur de la propriété eqptouvevacuationDimensionverin.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptouvevacuation_dimensionverin")
    public String getEqptouvevacuationDimensionverin() {
		if (eqptouvevacuationDimensionverin == null) {
    		manageNullException("eqptouvevacuation_dimensionverin");
    	}
        return eqptouvevacuationDimensionverin;
    }

    /**
     * Définit la valeur de la propriété eqptouvevacuationDimensionverin.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEqptouvevacuationDimensionverin(String value) {
        this.eqptouvevacuationDimensionverin = value;
    }

    /**
     * Obtient la valeur de la propriété eqptouvevacuationPjfichetechniqueclapet.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptouvevacuation_pjfichetechniqueclapet")
    public String getEqptouvevacuationPjfichetechniqueclapet() {
		if (eqptouvevacuationPjfichetechniqueclapet == null) {
    		manageNullException("eqptouvevacuation_pjfichetechniqueclapet");
    	}
        return eqptouvevacuationPjfichetechniqueclapet;
    }

    /**
     * Définit la valeur de la propriété eqptouvevacuationPjfichetechniqueclapet.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEqptouvevacuationPjfichetechniqueclapet(String value) {
        this.eqptouvevacuationPjfichetechniqueclapet = value;
    }

    /**
     * Obtient la valeur de la propriété eqptouvevacuationPjfichetechniquevanne.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptouvevacuation_pjfichetechniquevanne")
    public String getEqptouvevacuationPjfichetechniquevanne() {
		if (eqptouvevacuationPjfichetechniquevanne == null) {
    		manageNullException("eqptouvevacuation_pjfichetechniquevanne");
    	}
        return eqptouvevacuationPjfichetechniquevanne;
    }

    /**
     * Définit la valeur de la propriété eqptouvevacuationPjfichetechniquevanne.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEqptouvevacuationPjfichetechniquevanne(String value) {
        this.eqptouvevacuationPjfichetechniquevanne = value;
    }

    /**
     * Obtient la valeur de la propriété eqptouvevacuationPjfichetechniqueverin.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptouvevacuation_pjfichetechniqueverin")
    public String getEqptouvevacuationPjfichetechniqueverin() {
		if (eqptouvevacuationPjfichetechniqueverin == null) {
    		manageNullException("eqptouvevacuation_pjfichetechniqueverin");
    	}
        return eqptouvevacuationPjfichetechniqueverin;
    }

    /**
     * Définit la valeur de la propriété eqptouvevacuationPjfichetechniqueverin.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEqptouvevacuationPjfichetechniqueverin(String value) {
        this.eqptouvevacuationPjfichetechniqueverin = value;
    }

	public void setIdChg(Integer idChg) {
		this.idChg = idChg;
	}

    private void manageNullException(String champ) {
    	throw new IllegalArgumentException("Table " + PatrimoineDAO.PAT_EQPT_OUVRAGESEVACUATIONBARRAGE + " - Champs " + champ + " - Valeur 'Null' ou non permise par le XSD.");
	}

}
