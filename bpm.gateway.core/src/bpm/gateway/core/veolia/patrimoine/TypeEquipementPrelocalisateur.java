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
 * <p>Classe Java pour typeEquipementPrelocalisateur complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="typeEquipementPrelocalisateur">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="eqptprelocalisateur_eqptsocle_id" type="{}equipementIDType"/>
 *         &lt;element name="eqptprelocalisateur_descriptif" type="{}stringRestricted3000Type"/>
 *         &lt;element name="eqptprelocalisateur_cable" type="{}CablePrelocalisateurListe"/>
 *         &lt;element name="eqptprelocalisateur_profmini" type="{}decimalDeuxChiffres"/>
 *         &lt;element name="eqptprelocalisateur_profmaxi" type="{}decimalDeuxChiffres"/>
 *         &lt;element name="eqptprelocalisateur_position" type="{}PositionPrelocalisateurListe"/>
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
@Table (name = PatrimoineDAO.PAT_EQPT_PRELOCALISATEUR)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "typeEquipementPrelocalisateur", propOrder = {
    "eqptprelocalisateurEqptsocleId",
    "eqptprelocalisateurDescriptif",
    "eqptprelocalisateurCable",
    "eqptprelocalisateurProfmini",
    "eqptprelocalisateurProfmaxi",
    "eqptprelocalisateurPosition"
})
public class TypeEquipementPrelocalisateur {

	@Id
	@Column(name = "eqptprelocalisateur_id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@XmlTransient
    protected int id;
	@Transient
    @XmlElement(name = "eqptprelocalisateur_eqptsocle_id", required = true)
    protected String eqptprelocalisateurEqptsocleId;
	@Transient
    @XmlElement(name = "eqptprelocalisateur_descriptif", required = true, defaultValue = "NR")
    protected String eqptprelocalisateurDescriptif;
	@Transient
    @XmlElement(name = "eqptprelocalisateur_cable", required = true)
    @XmlSchemaType(name = "string")
    protected CablePrelocalisateurListe eqptprelocalisateurCable;
	@Transient
    @XmlElement(name = "eqptprelocalisateur_profmini", required = true, defaultValue = "999999999.99")
    protected BigDecimal eqptprelocalisateurProfmini;
	@Transient
    @XmlElement(name = "eqptprelocalisateur_profmaxi", required = true, defaultValue = "999999999.99")
    protected BigDecimal eqptprelocalisateurProfmaxi;
	@Transient
    @XmlElement(name = "eqptprelocalisateur_position", required = true)
    @XmlSchemaType(name = "string")
    protected PositionPrelocalisateurListe eqptprelocalisateurPosition;
	@Column(name = "id_chg")
	@XmlTransient
	private Integer idChg;

    /**
     * Obtient la valeur de la propriété eqptprelocalisateurEqptsocleId.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptprelocalisateur_eqptsocle_id")
    public String getEqptprelocalisateurEqptsocleId() {
		if (eqptprelocalisateurEqptsocleId == null) {
    		manageNullException("eqptprelocalisateur_eqptsocle_id");
    	}
        return eqptprelocalisateurEqptsocleId;
    }

    /**
     * Définit la valeur de la propriété eqptprelocalisateurEqptsocleId.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEqptprelocalisateurEqptsocleId(String value) {
        this.eqptprelocalisateurEqptsocleId = value;
    }

    /**
     * Obtient la valeur de la propriété eqptprelocalisateurDescriptif.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptprelocalisateur_descriptif")
    public String getEqptprelocalisateurDescriptif() {
		if (eqptprelocalisateurDescriptif == null) {
    		manageNullException("eqptprelocalisateur_descriptif");
    	}
        return eqptprelocalisateurDescriptif;
    }

    /**
     * Définit la valeur de la propriété eqptprelocalisateurDescriptif.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEqptprelocalisateurDescriptif(String value) {
        this.eqptprelocalisateurDescriptif = value;
    }

    /**
     * Obtient la valeur de la propriété eqptprelocalisateurCable.
     * 
     * @return
     *     possible object is
     *     {@link CablePrelocalisateurListe }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptprelocalisateur_cable")
    public String getEqptprelocalisateurCable() {
        return CablePrelocalisateurListe.getValue(PatrimoineDAO.PAT_EQPT_PRELOCALISATEUR, "eqptprelocalisateur_cable", eqptprelocalisateurCable, true);
    }

    /**
     * Définit la valeur de la propriété eqptprelocalisateurCable.
     * 
     * @param value
     *     allowed object is
     *     {@link CablePrelocalisateurListe }
     *     
     */
    public void setEqptprelocalisateurCable(String value) {
        this.eqptprelocalisateurCable = CablePrelocalisateurListe.fromValue(PatrimoineDAO.PAT_EQPT_PRELOCALISATEUR, "eqptprelocalisateur_cable", value);
    }

    /**
     * Obtient la valeur de la propriété eqptprelocalisateurProfmini.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptprelocalisateur_profmini")
    public BigDecimal getEqptprelocalisateurProfmini() {
		if (eqptprelocalisateurProfmini == null) {
    		manageNullException("eqptprelocalisateur_profmini");
    	}
        return eqptprelocalisateurProfmini;
    }

    /**
     * Définit la valeur de la propriété eqptprelocalisateurProfmini.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setEqptprelocalisateurProfmini(BigDecimal value) {
        this.eqptprelocalisateurProfmini = value;
    }

    /**
     * Obtient la valeur de la propriété eqptprelocalisateurProfmaxi.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptprelocalisateur_profmaxi")
    public BigDecimal getEqptprelocalisateurProfmaxi() {
		if (eqptprelocalisateurProfmaxi == null) {
    		manageNullException("eqptprelocalisateur_profmaxi");
    	}
        return eqptprelocalisateurProfmaxi;
    }

    /**
     * Définit la valeur de la propriété eqptprelocalisateurProfmaxi.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setEqptprelocalisateurProfmaxi(BigDecimal value) {
        this.eqptprelocalisateurProfmaxi = value;
    }

    /**
     * Obtient la valeur de la propriété eqptprelocalisateurPosition.
     * 
     * @return
     *     possible object is
     *     {@link PositionPrelocalisateurListe }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptprelocalisateur_position")
    public String getEqptprelocalisateurPosition() {
        return PositionPrelocalisateurListe.getValue(PatrimoineDAO.PAT_EQPT_PRELOCALISATEUR, "eqptprelocalisateur_position", eqptprelocalisateurPosition, true);
    }

    /**
     * Définit la valeur de la propriété eqptprelocalisateurPosition.
     * 
     * @param value
     *     allowed object is
     *     {@link PositionPrelocalisateurListe }
     *     
     */
    public void setEqptprelocalisateurPosition(String value) {
        this.eqptprelocalisateurPosition = PositionPrelocalisateurListe.fromValue(PatrimoineDAO.PAT_EQPT_PRELOCALISATEUR, "eqptprelocalisateur_position", value);
    }

	public void setIdChg(Integer idChg) {
		this.idChg = idChg;
	}

    private void manageNullException(String champ) {
    	throw new IllegalArgumentException("Table " + PatrimoineDAO.PAT_EQPT_PRELOCALISATEUR + " - Champs " + champ + " - Valeur 'Null' ou non permise par le XSD.");
	}

}
