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
 * <p>Classe Java pour typeEquipementReacteur complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="typeEquipementReacteur">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="eqptreacteur_eqptsocle_id" type="{}equipementIDType"/>
 *         &lt;element name="eqptreacteur_descriptif" type="{}stringRestricted3000Type"/>
 *         &lt;element name="eqptreacteur_hauteur" type="{}decimalDeuxChiffres"/>
 *         &lt;element name="eqptreacteur_diametremax" type="{}decimalDeuxChiffres"/>
 *         &lt;element name="eqptreacteur_materiauconstituantext" type="{}materiauxConstituantsListe"/>
 *         &lt;element name="eqptreacteur_volume" type="{}decimalDeuxChiffres"/>
 *         &lt;element name="eqptreacteur_debit" type="{}decimalDeuxChiffres"/>
 *         &lt;element name="eqptreacteur_hauteurlisuspension" type="{}decimalDeuxChiffres" minOccurs="0"/>
 *         &lt;element name="eqptreacteur_hauteurlitrepos" type="{}decimalDeuxChiffres" minOccurs="0"/>
 *         &lt;element name="eqptreacteur_typeplancher" type="{}stringRestricted255Type"/>
 *         &lt;element name="eqptreacteur_pied" type="{}decimalDeuxChiffres"/>
 *         &lt;element name="eqptreacteur_coneinferieur" type="{}stringRestricted255Type"/>
 *         &lt;element name="eqptreacteur_materiauconstituantconeinf" type="{}materiauxConstituantsListe"/>
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
@Table (name = PatrimoineDAO.PAT_EQPT_REACTEUR)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "typeEquipementReacteur", propOrder = {
    "eqptreacteurEqptsocleId",
    "eqptreacteurDescriptif",
    "eqptreacteurHauteur",
    "eqptreacteurDiametremax",
    "eqptreacteurMateriauconstituantext",
    "eqptreacteurVolume",
    "eqptreacteurDebit",
    "eqptreacteurHauteurlisuspension",
    "eqptreacteurHauteurlitrepos",
    "eqptreacteurTypeplancher",
    "eqptreacteurPied",
    "eqptreacteurConeinferieur",
    "eqptreacteurMateriauconstituantconeinf"
})
public class TypeEquipementReacteur {

	@Id
	@Column(name = "eqptreacteur_id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@XmlTransient
    protected int id;
	@Transient
    @XmlElement(name = "eqptreacteur_eqptsocle_id", required = true)
    protected String eqptreacteurEqptsocleId;
	@Transient
    @XmlElement(name = "eqptreacteur_descriptif", required = true, defaultValue = "NR")
    protected String eqptreacteurDescriptif;
	@Transient
    @XmlElement(name = "eqptreacteur_hauteur", required = true, defaultValue = "999999999.99")
    protected BigDecimal eqptreacteurHauteur;
	@Transient
    @XmlElement(name = "eqptreacteur_diametremax", required = true, defaultValue = "999999999.99")
    protected BigDecimal eqptreacteurDiametremax;
	@Transient
    @XmlElement(name = "eqptreacteur_materiauconstituantext", required = true, defaultValue = "NR")
    @XmlSchemaType(name = "string")
    protected MateriauxConstituantsListe eqptreacteurMateriauconstituantext;
	@Transient
    @XmlElement(name = "eqptreacteur_volume", required = true, defaultValue = "999999999.99")
    protected BigDecimal eqptreacteurVolume;
	@Transient
    @XmlElement(name = "eqptreacteur_debit", required = true, defaultValue = "999999999.99")
    protected BigDecimal eqptreacteurDebit;
	@Column(name = "eqptreacteur_hauteurlisuspension")
    @XmlElement(name = "eqptreacteur_hauteurlisuspension")
    protected BigDecimal eqptreacteurHauteurlisuspension;
	@Column(name = "eqptreacteur_hauteurlitrepos")
    @XmlElement(name = "eqptreacteur_hauteurlitrepos")
    protected BigDecimal eqptreacteurHauteurlitrepos;
	@Transient
    @XmlElement(name = "eqptreacteur_typeplancher", required = true, defaultValue = "NR")
    protected String eqptreacteurTypeplancher;
	@Transient
    @XmlElement(name = "eqptreacteur_pied", required = true, defaultValue = "999999999.99")
    protected BigDecimal eqptreacteurPied;
	@Transient
    @XmlElement(name = "eqptreacteur_coneinferieur", required = true, defaultValue = "NR")
    protected String eqptreacteurConeinferieur;
	@Transient
    @XmlElement(name = "eqptreacteur_materiauconstituantconeinf", required = true, defaultValue = "NR")
    @XmlSchemaType(name = "string")
    protected MateriauxConstituantsListe eqptreacteurMateriauconstituantconeinf;
	@Column(name = "id_chg")
	@XmlTransient
	private Integer idChg;

    /**
     * Obtient la valeur de la propriété eqptreacteurEqptsocleId.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptreacteur_eqptsocle_id")
    public String getEqptreacteurEqptsocleId() {
		if (eqptreacteurEqptsocleId == null) {
    		manageNullException("eqptreacteur_eqptsocle_id");
    	}
        return eqptreacteurEqptsocleId;
    }

    /**
     * Définit la valeur de la propriété eqptreacteurEqptsocleId.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEqptreacteurEqptsocleId(String value) {
        this.eqptreacteurEqptsocleId = value;
    }

    /**
     * Obtient la valeur de la propriété eqptreacteurDescriptif.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptreacteur_descriptif")
    public String getEqptreacteurDescriptif() {
		if (eqptreacteurDescriptif == null) {
    		manageNullException("eqptreacteur_descriptif");
    	}
        return eqptreacteurDescriptif;
    }

    /**
     * Définit la valeur de la propriété eqptreacteurDescriptif.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEqptreacteurDescriptif(String value) {
        this.eqptreacteurDescriptif = value;
    }

    /**
     * Obtient la valeur de la propriété eqptreacteurHauteur.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptreacteur_hauteur")
    public BigDecimal getEqptreacteurHauteur() {
		if (eqptreacteurHauteur == null) {
    		manageNullException("eqptreacteur_hauteur");
    	}
        return eqptreacteurHauteur;
    }

    /**
     * Définit la valeur de la propriété eqptreacteurHauteur.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setEqptreacteurHauteur(BigDecimal value) {
        this.eqptreacteurHauteur = value;
    }

    /**
     * Obtient la valeur de la propriété eqptreacteurDiametremax.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptreacteur_diametremax")
    public BigDecimal getEqptreacteurDiametremax() {
		if (eqptreacteurDiametremax == null) {
    		manageNullException("eqptreacteur_diametremax");
    	}
        return eqptreacteurDiametremax;
    }

    /**
     * Définit la valeur de la propriété eqptreacteurDiametremax.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setEqptreacteurDiametremax(BigDecimal value) {
        this.eqptreacteurDiametremax = value;
    }

    /**
     * Obtient la valeur de la propriété eqptreacteurMateriauconstituantext.
     * 
     * @return
     *     possible object is
     *     {@link MateriauxConstituantsListe }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptreacteur_materiauconstituantext")
    public String getEqptreacteurMateriauconstituantext() {
        return MateriauxConstituantsListe.getValue(PatrimoineDAO.PAT_EQPT_REACTEUR, "eqptreacteur_materiauconstituantext", eqptreacteurMateriauconstituantext, true);
    }

    /**
     * Définit la valeur de la propriété eqptreacteurMateriauconstituantext.
     * 
     * @param value
     *     allowed object is
     *     {@link MateriauxConstituantsListe }
     *     
     */
    public void setEqptreacteurMateriauconstituantext(String value) {
        this.eqptreacteurMateriauconstituantext = MateriauxConstituantsListe.fromValue(PatrimoineDAO.PAT_EQPT_REACTEUR, "eqptreacteur_materiauconstituantext", value);
    }

    /**
     * Obtient la valeur de la propriété eqptreacteurVolume.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptreacteur_volume")
    public BigDecimal getEqptreacteurVolume() {
		if (eqptreacteurVolume == null) {
    		manageNullException("eqptreacteur_volume");
    	}
        return eqptreacteurVolume;
    }

    /**
     * Définit la valeur de la propriété eqptreacteurVolume.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setEqptreacteurVolume(BigDecimal value) {
        this.eqptreacteurVolume = value;
    }

    /**
     * Obtient la valeur de la propriété eqptreacteurDebit.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptreacteur_debit")
    public BigDecimal getEqptreacteurDebit() {
		if (eqptreacteurDebit == null) {
    		manageNullException("eqptreacteur_debit");
    	}
        return eqptreacteurDebit;
    }

    /**
     * Définit la valeur de la propriété eqptreacteurDebit.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setEqptreacteurDebit(BigDecimal value) {
        this.eqptreacteurDebit = value;
    }

    /**
     * Obtient la valeur de la propriété eqptreacteurHauteurlisuspension.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getEqptreacteurHauteurlisuspension() {
        return eqptreacteurHauteurlisuspension;
    }

    /**
     * Définit la valeur de la propriété eqptreacteurHauteurlisuspension.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setEqptreacteurHauteurlisuspension(BigDecimal value) {
        this.eqptreacteurHauteurlisuspension = value;
    }

    /**
     * Obtient la valeur de la propriété eqptreacteurHauteurlitrepos.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getEqptreacteurHauteurlitrepos() {
        return eqptreacteurHauteurlitrepos;
    }

    /**
     * Définit la valeur de la propriété eqptreacteurHauteurlitrepos.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setEqptreacteurHauteurlitrepos(BigDecimal value) {
        this.eqptreacteurHauteurlitrepos = value;
    }

    /**
     * Obtient la valeur de la propriété eqptreacteurTypeplancher.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptreacteur_typeplancher")
    public String getEqptreacteurTypeplancher() {
		if (eqptreacteurTypeplancher == null) {
    		manageNullException("eqptreacteur_typeplancher");
    	}
        return eqptreacteurTypeplancher;
    }

    /**
     * Définit la valeur de la propriété eqptreacteurTypeplancher.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEqptreacteurTypeplancher(String value) {
        this.eqptreacteurTypeplancher = value;
    }

    /**
     * Obtient la valeur de la propriété eqptreacteurPied.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptreacteur_pied")
    public BigDecimal getEqptreacteurPied() {
		if (eqptreacteurPied == null) {
    		manageNullException("eqptreacteur_pied");
    	}
        return eqptreacteurPied;
    }

    /**
     * Définit la valeur de la propriété eqptreacteurPied.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setEqptreacteurPied(BigDecimal value) {
        this.eqptreacteurPied = value;
    }

    /**
     * Obtient la valeur de la propriété eqptreacteurConeinferieur.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptreacteur_coneinferieur")
    public String getEqptreacteurConeinferieur() {
		if (eqptreacteurConeinferieur == null) {
    		manageNullException("eqptreacteur_coneinferieur");
    	}
        return eqptreacteurConeinferieur;
    }

    /**
     * Définit la valeur de la propriété eqptreacteurConeinferieur.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEqptreacteurConeinferieur(String value) {
        this.eqptreacteurConeinferieur = value;
    }

    /**
     * Obtient la valeur de la propriété eqptreacteurMateriauconstituantconeinf.
     * 
     * @return
     *     possible object is
     *     {@link MateriauxConstituantsListe }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptreacteur_materiauconstituantconeinf")
    public String getEqptreacteurMateriauconstituantconeinf() {
        return MateriauxConstituantsListe.getValue(PatrimoineDAO.PAT_EQPT_REACTEUR, "eqptreacteur_materiauconstituantconeinf", eqptreacteurMateriauconstituantconeinf, true);
    }

    /**
     * Définit la valeur de la propriété eqptreacteurMateriauconstituantconeinf.
     * 
     * @param value
     *     allowed object is
     *     {@link MateriauxConstituantsListe }
     *     
     */
    public void setEqptreacteurMateriauconstituantconeinf(String value) {
        this.eqptreacteurMateriauconstituantconeinf = MateriauxConstituantsListe.fromValue(PatrimoineDAO.PAT_EQPT_REACTEUR, "eqptreacteur_materiauconstituantconeinf", value);
    }

	public void setIdChg(Integer idChg) {
		this.idChg = idChg;
	}

    private void manageNullException(String champ) {
    	throw new IllegalArgumentException("Table " + PatrimoineDAO.PAT_EQPT_REACTEUR + " - Champs " + champ + " - Valeur 'Null' ou non permise par le XSD.");
	}

}
