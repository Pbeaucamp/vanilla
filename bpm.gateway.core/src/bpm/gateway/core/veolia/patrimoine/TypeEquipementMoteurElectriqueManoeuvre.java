//
// Ce fichier a �t� g�n�r� par l'impl�mentation de r�f�rence JavaTM Architecture for XML Binding (JAXB), v2.2.8-b130911.1802 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apport�e � ce fichier sera perdue lors de la recompilation du sch�ma source. 
// G�n�r� le : 2020.01.29 � 03:15:22 PM CET 
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
 * <p>Le fragment de sch�ma suivant indique le contenu attendu figurant dans cette classe.
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
     * Obtient la valeur de la propri�t� eqptmoteurelecmanoeuvEqptsocleId.
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
     * D�finit la valeur de la propri�t� eqptmoteurelecmanoeuvEqptsocleId.
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
     * Obtient la valeur de la propri�t� eqptmoteurelecmanoeuvDescriptif.
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
     * D�finit la valeur de la propri�t� eqptmoteurelecmanoeuvDescriptif.
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
     * Obtient la valeur de la propri�t� eqptmoteurelecmanoeuvPressionnominale.
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
     * D�finit la valeur de la propri�t� eqptmoteurelecmanoeuvPressionnominale.
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
     * Obtient la valeur de la propri�t� eqptmoteurelecmanoeuvPuissance.
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
     * D�finit la valeur de la propri�t� eqptmoteurelecmanoeuvPuissance.
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
     * Obtient la valeur de la propri�t� eqptmoteurelecmanoeuvTensionnominale.
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
     * D�finit la valeur de la propri�t� eqptmoteurelecmanoeuvTensionnominale.
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
     * Obtient la valeur de la propri�t� eqptmoteurelecmanoeuvAccuhydpressionepreuve.
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
     * D�finit la valeur de la propri�t� eqptmoteurelecmanoeuvAccuhydpressionepreuve.
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
     * Obtient la valeur de la propri�t� eqptmoteurelecmanoeuvPjmaintenance.
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
     * D�finit la valeur de la propri�t� eqptmoteurelecmanoeuvPjmaintenance.
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
     * Obtient la valeur de la propri�t� eqptmoteurelecmanoeuvPjfichetechnique.
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
     * D�finit la valeur de la propri�t� eqptmoteurelecmanoeuvPjfichetechnique.
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
