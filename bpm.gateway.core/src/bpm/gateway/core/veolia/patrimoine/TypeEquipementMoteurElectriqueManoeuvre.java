//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.8-b130911.1802 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2020.01.29 à 03:15:22 PM CET 
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
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour typeEquipementMoteurElectriqueManoeuvre complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="typeEquipementMoteurElectriqueManoeuvre">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="eqptmoteurelecmanoeuv_eqptsocle_id" type="{}equipementIDType"/>
 *         &lt;element name="eqptmoteurelecmanoeuv_descriptif" type="{}stringRestricted3000Type"/>
 *         &lt;element name="eqptmoteurelecmanoeuv_pressionnominale" type="{}pressionsNominalesListe" minOccurs="0"/>
 *         &lt;element name="eqptmoteurelecmanoeuv_puissance" type="{}decimalDeuxChiffres"/>
 *         &lt;element name="eqptmoteurelecmanoeuv_tensionnominale" type="{}tensionsNominalesListe" minOccurs="0"/>
 *         &lt;element name="eqptmoteurelecmanoeuv_accuhydpressionepreuve" type="{}decimalDeuxChiffres"/>
 *         &lt;element name="eqptmoteurelecmanoeuv_pjmaintenance" type="{}stringRestricted3000Type" minOccurs="0"/>
 *         &lt;element name="eqptmoteurelecmanoeuv_pjfichetechnique" type="{}stringRestricted3000Type"/>
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
@Table (name = PatrimoineDAO.PAT_EQPT_MOTEURELECTRIQUEMANOEUVRE)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "typeEquipementMoteurElectriqueManoeuvre", propOrder = {
    "eqptmoteurelecmanoeuvEqptsocleId",
    "eqptmoteurelecmanoeuvDescriptif",
    "eqptmoteurelecmanoeuvPressionnominale",
    "eqptmoteurelecmanoeuvPuissance",
    "eqptmoteurelecmanoeuvTensionnominale",
    "eqptmoteurelecmanoeuvAccuhydpressionepreuve",
    "eqptmoteurelecmanoeuvPjmaintenance",
    "eqptmoteurelecmanoeuvPjfichetechnique"
})
public class TypeEquipementMoteurElectriqueManoeuvre {

	@Id
	@Column(name = "eqptmoteurelecmanoeuv_id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@XmlTransient
    protected int id;
	@Transient
    @XmlElement(name = "eqptmoteurelecmanoeuv_eqptsocle_id", required = true)
    protected String eqptmoteurelecmanoeuvEqptsocleId;
	@Transient
    @XmlElement(name = "eqptmoteurelecmanoeuv_descriptif", required = true, defaultValue = "NR")
    protected String eqptmoteurelecmanoeuvDescriptif;
	@Transient
    @XmlElement(name = "eqptmoteurelecmanoeuv_pressionnominale")
    @XmlSchemaType(name = "string")
    protected PressionsNominalesListe eqptmoteurelecmanoeuvPressionnominale;
	@Transient
    @XmlElement(name = "eqptmoteurelecmanoeuv_puissance", required = true, defaultValue = "999999999.99")
    protected BigDecimal eqptmoteurelecmanoeuvPuissance;
	@Transient
    @XmlElement(name = "eqptmoteurelecmanoeuv_tensionnominale")
    @XmlSchemaType(name = "string")
    protected TensionsNominalesListe eqptmoteurelecmanoeuvTensionnominale;
	@Transient
    @XmlElement(name = "eqptmoteurelecmanoeuv_accuhydpressionepreuve", required = true, defaultValue = "999999999.99")
    protected BigDecimal eqptmoteurelecmanoeuvAccuhydpressionepreuve;
	@Column(name = "eqptmoteurelecmanoeuv_pjmaintenance")
    @XmlElement(name = "eqptmoteurelecmanoeuv_pjmaintenance")
    protected String eqptmoteurelecmanoeuvPjmaintenance;
	@Transient
    @XmlElement(name = "eqptmoteurelecmanoeuv_pjfichetechnique", required = true, defaultValue = "NR")
    protected String eqptmoteurelecmanoeuvPjfichetechnique;
	@Column(name = "id_chg")
	@XmlTransient
	private Integer idChg;

    /**
     * Obtient la valeur de la propriété eqptmoteurelecmanoeuvEqptsocleId.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptmoteurelecmanoeuv_eqptsocle_id")
    public String getEqptmoteurelecmanoeuvEqptsocleId() {
		if (eqptmoteurelecmanoeuvEqptsocleId == null) {
    		manageNullException("eqptmoteurelecmanoeuv_eqptsocle_id");
    	}
        return eqptmoteurelecmanoeuvEqptsocleId;
    }

    /**
     * Définit la valeur de la propriété eqptmoteurelecmanoeuvEqptsocleId.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEqptmoteurelecmanoeuvEqptsocleId(String value) {
        this.eqptmoteurelecmanoeuvEqptsocleId = value;
    }

    /**
     * Obtient la valeur de la propriété eqptmoteurelecmanoeuvDescriptif.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptmoteurelecmanoeuv_descriptif")
    public String getEqptmoteurelecmanoeuvDescriptif() {
		if (eqptmoteurelecmanoeuvDescriptif == null) {
    		manageNullException("eqptmoteurelecmanoeuv_descriptif");
    	}
        return eqptmoteurelecmanoeuvDescriptif;
    }

    /**
     * Définit la valeur de la propriété eqptmoteurelecmanoeuvDescriptif.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEqptmoteurelecmanoeuvDescriptif(String value) {
        this.eqptmoteurelecmanoeuvDescriptif = value;
    }

    /**
     * Obtient la valeur de la propriété eqptmoteurelecmanoeuvPressionnominale.
     * 
     * @return
     *     possible object is
     *     {@link PressionsNominalesListe }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptmoteurelecmanoeuv_pressionnominale")
    public String getEqptmoteurelecmanoeuvPressionnominale() {
        return PressionsNominalesListe.getValue(PatrimoineDAO.PAT_EQPT_MOTEURELECTRIQUEMANOEUVRE, "eqptmoteurelecmanoeuv_pressionnominale", eqptmoteurelecmanoeuvPressionnominale, false);
    }

    /**
     * Définit la valeur de la propriété eqptmoteurelecmanoeuvPressionnominale.
     * 
     * @param value
     *     allowed object is
     *     {@link PressionsNominalesListe }
     *     
     */
    public void setEqptmoteurelecmanoeuvPressionnominale(String value) {
        this.eqptmoteurelecmanoeuvPressionnominale = PressionsNominalesListe.fromValue(PatrimoineDAO.PAT_EQPT_MOTEURELECTRIQUEMANOEUVRE, "eqptmoteurelecmanoeuv_pressionnominale", value);
    }

    /**
     * Obtient la valeur de la propriété eqptmoteurelecmanoeuvPuissance.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptmoteurelecmanoeuv_puissance")
    public BigDecimal getEqptmoteurelecmanoeuvPuissance() {
		if (eqptmoteurelecmanoeuvPuissance == null) {
    		manageNullException("eqptmoteurelecmanoeuv_puissance");
    	}
        return eqptmoteurelecmanoeuvPuissance;
    }

    /**
     * Définit la valeur de la propriété eqptmoteurelecmanoeuvPuissance.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setEqptmoteurelecmanoeuvPuissance(BigDecimal value) {
        this.eqptmoteurelecmanoeuvPuissance = value;
    }

    /**
     * Obtient la valeur de la propriété eqptmoteurelecmanoeuvTensionnominale.
     * 
     * @return
     *     possible object is
     *     {@link TensionsNominalesListe }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptmoteurelecmanoeuv_tensionnominale")
    public String getEqptmoteurelecmanoeuvTensionnominale() {
        return TensionsNominalesListe.getValue(PatrimoineDAO.PAT_EQPT_MOTEURELECTRIQUEMANOEUVRE, "eqptmoteurelecmanoeuv_tensionnominale", eqptmoteurelecmanoeuvTensionnominale, false);
    }

    /**
     * Définit la valeur de la propriété eqptmoteurelecmanoeuvTensionnominale.
     * 
     * @param value
     *     allowed object is
     *     {@link TensionsNominalesListe }
     *     
     */
    public void setEqptmoteurelecmanoeuvTensionnominale(String value) {
        this.eqptmoteurelecmanoeuvTensionnominale = TensionsNominalesListe.fromValue(PatrimoineDAO.PAT_EQPT_MOTEURELECTRIQUEMANOEUVRE, "eqptmoteurelecmanoeuv_tensionnominale", value);
    }

    /**
     * Obtient la valeur de la propriété eqptmoteurelecmanoeuvAccuhydpressionepreuve.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptmoteurelecmanoeuv_accuhydpressionepreuve")
    public BigDecimal getEqptmoteurelecmanoeuvAccuhydpressionepreuve() {
		if (eqptmoteurelecmanoeuvAccuhydpressionepreuve == null) {
    		manageNullException("eqptmoteurelecmanoeuv_accuhydpressionepreuve");
    	}
        return eqptmoteurelecmanoeuvAccuhydpressionepreuve;
    }

    /**
     * Définit la valeur de la propriété eqptmoteurelecmanoeuvAccuhydpressionepreuve.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setEqptmoteurelecmanoeuvAccuhydpressionepreuve(BigDecimal value) {
        this.eqptmoteurelecmanoeuvAccuhydpressionepreuve = value;
    }

    /**
     * Obtient la valeur de la propriété eqptmoteurelecmanoeuvPjmaintenance.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEqptmoteurelecmanoeuvPjmaintenance() {
        return eqptmoteurelecmanoeuvPjmaintenance;
    }

    /**
     * Définit la valeur de la propriété eqptmoteurelecmanoeuvPjmaintenance.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEqptmoteurelecmanoeuvPjmaintenance(String value) {
        this.eqptmoteurelecmanoeuvPjmaintenance = value;
    }

    /**
     * Obtient la valeur de la propriété eqptmoteurelecmanoeuvPjfichetechnique.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptmoteurelecmanoeuv_pjfichetechnique")
    public String getEqptmoteurelecmanoeuvPjfichetechnique() {
		if (eqptmoteurelecmanoeuvPjfichetechnique == null) {
    		manageNullException("eqptmoteurelecmanoeuv_pjfichetechnique");
    	}
        return eqptmoteurelecmanoeuvPjfichetechnique;
    }

    /**
     * Définit la valeur de la propriété eqptmoteurelecmanoeuvPjfichetechnique.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEqptmoteurelecmanoeuvPjfichetechnique(String value) {
        this.eqptmoteurelecmanoeuvPjfichetechnique = value;
    }

	public void setIdChg(Integer idChg) {
		this.idChg = idChg;
	}

    private void manageNullException(String champ) {
    	throw new IllegalArgumentException("Table " + PatrimoineDAO.PAT_EQPT_MOTEURELECTRIQUEMANOEUVRE + " - Champs " + champ + " - Valeur 'Null' ou non permise par le XSD.");
	}

}
