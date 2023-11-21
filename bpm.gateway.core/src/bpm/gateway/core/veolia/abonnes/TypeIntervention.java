//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.11.28 at 01:19:07 PM CET 
//


package bpm.gateway.core.veolia.abonnes;

import java.util.Date;

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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;

import bpm.gateway.core.veolia.VEHelper;


/**
 * <p>Java class for typeIntervention complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="typeIntervention">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="intervention_ve_id" type="{}StringRestricted255Type"/>
 *         &lt;element name="intervention_contratabt_id" type="{}StringRestricted255Type"/>
 *         &lt;element name="intervention_demande_id" type="{}StringRestricted255Type" minOccurs="0"/>
 *         &lt;element name="intervention_typeintitule" type="{}StringRestricted255Type"/>
 *         &lt;element name="intervention_typedescriptif" type="{}StringRestricted255Type"/>
 *         &lt;element name="intervention_etat" type="{}etatsInterventionsListe"/>
 *         &lt;element name="intervention_motifannulation" type="{}StringRestricted255Type" minOccurs="0"/>
 *         &lt;element name="intervention_dateouverturefiche" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *         &lt;element name="intervention_dateplanifiee" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *         &lt;element name="intervention_dateffective" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="intervention_commentaireintervention" type="{}StringRestricted255Type" minOccurs="0"/>
 *         &lt;element name="intervention_facturationintervention" type="{}ouiNonListe"/>
 *         &lt;element name="intervention_bonintervention" type="{}StringRestricted255Type" minOccurs="0"/>
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
@Table (name = AbonnesDAO.TYPE_INTERVENTION)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "typeIntervention", propOrder = {
    "interventionVeId",
    "interventionContratabtId",
    "interventionDemandeId",
    "interventionTypeintitule",
    "interventionTypedescriptif",
    "interventionEtat",
    "interventionMotifannulation",
    "interventionDateouverturefiche",
    "interventionDateplanifiee",
    "interventionDateeffective",
    "interventionCommentaireintervention",
    "interventionFacturationintervention",
    "interventionBonintervention"
})
@XmlRootElement(name = AbonnesDAO.ROOT_INTERVENTION)
public class TypeIntervention {

	@Id
	@Column(name = "intervention_id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@XmlTransient
    protected int id;
	@Transient
    @XmlElement(name = "intervention_ve_id", required = true)
    protected String interventionVeId;
	@Transient
    @XmlElement(name = "intervention_contratabt_id", required = true)
    protected String interventionContratabtId;
	@Transient
    @XmlElement(name = "intervention_demande_id")
    protected String interventionDemandeId;
	@Transient
    @XmlElement(name = "intervention_typeintitule", required = true)
    protected String interventionTypeintitule;
	@Transient
    @XmlElement(name = "intervention_typedescriptif", required = true)
    protected String interventionTypedescriptif;
	@Transient
    @XmlElement(name = "intervention_etat", required = true)
    protected EtatsInterventionsListe interventionEtat;
	@Column(name = "intervention_motifannulation")
    @XmlElement(name = "intervention_motifannulation")
    protected String interventionMotifannulation;
	@Transient
    @XmlElement(name = "intervention_dateouverturefiche", required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar interventionDateouverturefiche;
	@Transient
    @XmlElement(name = "intervention_dateplanifiee", required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar interventionDateplanifiee;
	@Transient
    @XmlElement(name = "intervention_dateeffective")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar interventionDateeffective;
	@Column(name = "intervention_commentaireintervention")
    @XmlElement(name = "intervention_commentaireintervention")
    protected String interventionCommentaireintervention;
	@Transient
    @XmlElement(name = "intervention_facturationintervention", required = true)
    protected OuiNonListe interventionFacturationintervention;
	@Column(name = "intervention_bonintervention")
    @XmlElement(name = "intervention_bonintervention")
    protected String interventionBonintervention;
	@Column(name = "id_chg")
	@XmlTransient
	private Integer idChg;

    /**
     * Gets the value of the interventionVeId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "intervention_ve_id")
    public String getInterventionVeId() {
    	if (interventionVeId == null) {
    		throw new IllegalArgumentException("Table " + AbonnesDAO.TYPE_INTERVENTION + " - Champs intervention_ve_id - Valeur 'Null' ou non permise par le XSD.");
    	}
        return interventionVeId;
    }

    /**
     * Sets the value of the interventionVeId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInterventionVeId(String value) {
        this.interventionVeId = value != null ? value : "";
    }

    /**
     * Gets the value of the interventionContratabtId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "intervention_contratabt_id")
    public String getInterventionContratabtId() {
    	if (interventionContratabtId == null) {
    		throw new IllegalArgumentException("Table " + AbonnesDAO.TYPE_INTERVENTION + " - Champs intervention_contratabt_id - Valeur 'Null' ou non permise par le XSD.");
    	}
        return interventionContratabtId;
    }

    /**
     * Sets the value of the interventionContratabtId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInterventionContratabtId(String value) {
        this.interventionContratabtId = value != null ? value : "";
    }

    /**
     * Gets the value of the interventionDemandeId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "intervention_demande_id")
    public String getInterventionDemandeId() {
        return interventionDemandeId != null && interventionDemandeId.isEmpty() ? null : interventionDemandeId;
    }

    /**
     * Sets the value of the interventionDemandeId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInterventionDemandeId(String value) {
        this.interventionDemandeId = value;
    }

    /**
     * Gets the value of the interventionTypeintitule property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "intervention_typeintitule")
    public String getInterventionTypeintitule() {
    	if (interventionTypeintitule == null) {
    		throw new IllegalArgumentException("Table " + AbonnesDAO.TYPE_INTERVENTION + " - Champs intervention_typeintitule - Valeur 'Null' ou non permise par le XSD.");
    	}
        return interventionTypeintitule;
    }

    /**
     * Sets the value of the interventionTypeintitule property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInterventionTypeintitule(String value) {
        this.interventionTypeintitule = value != null ? value : "";
    }

    /**
     * Gets the value of the interventionTypedescriptif property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "intervention_typedescriptif")
    public String getInterventionTypedescriptif() {
    	if (interventionTypedescriptif == null) {
    		throw new IllegalArgumentException("Table " + AbonnesDAO.TYPE_INTERVENTION + " - Champs intervention_typedescriptif - Valeur 'Null' ou non permise par le XSD.");
    	}
        return interventionTypedescriptif;
    }

    /**
     * Sets the value of the interventionTypedescriptif property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInterventionTypedescriptif(String value) {
        this.interventionTypedescriptif = value != null ? value : "";
    }

    /**
     * Gets the value of the interventionEtat property.
     * 
     * @return
     *     possible object is
     *     {@link EtatsInterventionsListe }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "intervention_etat")
    public String getInterventionEtat() {
        return EtatsInterventionsListe.getValue(AbonnesDAO.TYPE_INTERVENTION, "intervention_etat", interventionEtat);
    }

    /**
     * Sets the value of the interventionEtat property.
     * 
     * @param value
     *     allowed object is
     *     {@link EtatsInterventionsListe }
     *     
     */
    public void setInterventionEtat(String value) {
        this.interventionEtat = EtatsInterventionsListe.fromValue(AbonnesDAO.TYPE_INTERVENTION, "intervention_etat", value);
    }

    /**
     * Gets the value of the interventionMotifannulation property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInterventionMotifannulation() {
        return interventionMotifannulation;
    }

    /**
     * Sets the value of the interventionMotifannulation property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInterventionMotifannulation(String value) {
        this.interventionMotifannulation = value;
    }

    /**
     * Gets the value of the interventionDateouverturefiche property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "intervention_dateouverturefiche")
    public Date getInterventionDateouverturefiche() {
    	if (interventionDateouverturefiche == null) {
    		throw new IllegalArgumentException("Table " + AbonnesDAO.TYPE_INTERVENTION + " - Champs intervention_dateouverturefiche - Valeur 'Null' ou non permise par le XSD.");
    	}
        return VEHelper.toDate(interventionDateouverturefiche);
    }

    /**
     * Sets the value of the interventionDateouverturefiche property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setInterventionDateouverturefiche(XMLGregorianCalendar value) {
        this.interventionDateouverturefiche = value;
    }

	/**
	 * Sets the value of the interventionDateouverturefiche property from hibernate.
	 * 
	 * @param value
	 *            allowed object is {@link XMLGregorianCalendar }
	 * 
	 */
	public void setInterventionDateouverturefiche(Date value) {
		this.interventionDateouverturefiche = VEHelper.fromDate(value);
	}

    /**
     * Gets the value of the interventionDateplanifiee property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "intervention_dateplanifiee")
    public Date getInterventionDateplanifiee() {
    	if (interventionDateplanifiee == null) {
    		throw new IllegalArgumentException("Table " + AbonnesDAO.TYPE_INTERVENTION + " - Champs intervention_dateplanifiee - Valeur 'Null' ou non permise par le XSD.");
    	}
        return VEHelper.toDate(interventionDateplanifiee);
    }

    /**
     * Sets the value of the interventionDateplanifiee property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setInterventionDateplanifiee(XMLGregorianCalendar value) {
        this.interventionDateplanifiee = value;
    }

	/**
	 * Sets the value of the interventionDateplanifiee property from hibernate.
	 * 
	 * @param value
	 *            allowed object is {@link XMLGregorianCalendar }
	 * 
	 */
	public void setInterventionDateplanifiee(Date value) {
		this.interventionDateplanifiee = VEHelper.fromDate(value);
	}

    /**
     * Gets the value of the interventionDateffective property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "intervention_dateeffective")
    public Date getInterventionDateeffective() {
        return VEHelper.toDate(interventionDateeffective);
    }

    /**
     * Sets the value of the interventionDateffective property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setInterventionDateeffective(XMLGregorianCalendar value) {
        this.interventionDateeffective = value;
    }

	/**
	 * Sets the value of the interventionDateffective property from hibernate.
	 * 
	 * @param value
	 *            allowed object is {@link XMLGregorianCalendar }
	 * 
	 */
	public void setInterventionDateeffective(Date value) {
		this.interventionDateeffective = VEHelper.fromDate(value);
	}

    /**
     * Gets the value of the interventionCommentaireintervention property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInterventionCommentaireintervention() {
        return interventionCommentaireintervention;
    }

    /**
     * Sets the value of the interventionCommentaireintervention property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInterventionCommentaireintervention(String value) {
        this.interventionCommentaireintervention = value;
    }

    /**
     * Gets the value of the interventionFacturationintervention property.
     * 
     * @return
     *     possible object is
     *     {@link OuiNonListe }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "intervention_facturationintervention")
    public String getInterventionFacturationintervention() {
        return OuiNonListe.getValue(AbonnesDAO.TYPE_INTERVENTION, "intervention_facturationintervention", interventionFacturationintervention, true);
    }

    /**
     * Sets the value of the interventionFacturationintervention property.
     * 
     * @param value
     *     allowed object is
     *     {@link OuiNonListe }
     *     
     */
    public void setInterventionFacturationintervention(String value) {
        this.interventionFacturationintervention = OuiNonListe.fromValue(AbonnesDAO.TYPE_INTERVENTION, "intervention_facturationintervention", value, true);
    }

    /**
     * Gets the value of the interventionBonintervention property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInterventionBonintervention() {
        return interventionBonintervention;
    }

    /**
     * Sets the value of the interventionBonintervention property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInterventionBonintervention(String value) {
        this.interventionBonintervention = value;
    }

	public void setIdChg(Integer idChg) {
		this.idChg = idChg;
	}

}
