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
 * <p>Java class for typeCompteur complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="typeCompteur">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="compteur_ve_id" type="{}StringRestricted255Type"/>
 *         &lt;element name="compteur_pointfourniture_id" type="{}pointFournitureIDType"/>
 *         &lt;element name="compteur_statut" type="{}statutsCompteurListe"/>
 *         &lt;element name="compteur_sn" type="{}StringRestricted255Type"/>
 *         &lt;element name="compteur_marque" type="{}StringRestricted255Type"/>
 *         &lt;element name="compteur_modele" type="{}StringRestricted255Type"/>
 *         &lt;element name="compteur_type" type="{}typesCompteursListe"/>
 *         &lt;element name="compteur_diametre" type="{}diametresCompteurListe"/>
 *         &lt;element name="compteur_classe" type="{}StringRestricted255Type" minOccurs="0"/>
 *         &lt;element name="compteur_combine" type="{}ouiNonListe"/>
 *         &lt;element name="compteur_longueur" type="{}StringRestricted255Type" minOccurs="0"/>
 *         &lt;element name="compteur_anneefabrication" type="{http://www.w3.org/2001/XMLSchema}gYear"/>
 *         &lt;element name="compteur_datepose" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *         &lt;element name="compteur_datedepose" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="compteur_module" type="{}ouiNonListe"/>
 *         &lt;element name="compteur_marquemodule" type="{}StringRestricted255Type" minOccurs="0"/>
 *         &lt;element name="compteur_posemodule" type="{}posesModuleCompteurListe" minOccurs="0"/>
 *         &lt;element name="compteur_systemereleve" type="{}systemesReleveListe" minOccurs="0"/>
 *         &lt;element name="compteur_protocolecommreleve" type="{}StringRestricted255Type" minOccurs="0"/>
 *         &lt;element name="compteur_longueuronde" type="{}longueursOndeListe" minOccurs="0"/>
 *         &lt;element name="compteur_anneeposemodule" type="{http://www.w3.org/2001/XMLSchema}gYear" minOccurs="0"/>
 *         &lt;element name="compteur_typereleve" type="{}typesRelevesListe"/>
 *         &lt;element name="compteur_pj" type="{}StringRestricted255Type" minOccurs="0"/>
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
@Table (name = AbonnesDAO.TYPE_COMPTEUR)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "typeCompteur", propOrder = {
    "compteurVeId",
    "compteurPointfournitureId",
    "compteurStatut",
    "compteurSn",
    "compteurMarque",
    "compteurModele",
    "compteurType",
    "compteurDiametre",
    "compteurClasse",
    "compteurCombine",
    "compteurLongueur",
    "compteurAnneefabrication",
    "compteurDatepose",
    "compteurDatedepose",
    "compteurModule",
    "compteurMarquemodule",
    "compteurPosemodule",
    "compteurSystemereleve",
    "compteurProtocolecommreleve",
    "compteurLongueuronde",
    "compteurAnneeposemodule",
    "compteurTypereleve",
    "compteurPj"
})
@XmlRootElement(name = AbonnesDAO.ROOT_COMPTEUR)
public class TypeCompteur {
	
	@Id
	@Column(name = "compteur_id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@XmlTransient
    protected int id;
	@Transient
    @XmlElement(name = "compteur_ve_id", required = true)
    protected String compteurVeId;
	@Transient
    @XmlElement(name = "compteur_pointfourniture_id", required = true)
    protected String compteurPointfournitureId;
	@Transient
    @XmlElement(name = "compteur_statut", required = true)
    protected StatutsCompteurListe compteurStatut;
	@Transient
    @XmlElement(name = "compteur_sn", required = true)
    protected String compteurSn;
	@Transient
    @XmlElement(name = "compteur_marque", required = true)
    protected String compteurMarque;
	@Transient
    @XmlElement(name = "compteur_modele", required = true)
    protected String compteurModele;
	@Transient
    @XmlElement(name = "compteur_type", required = true)
    protected TypesCompteursListe compteurType;
	@Transient
    @XmlElement(name = "compteur_diametre", required = true)
    protected DiametresCompteurListe compteurDiametre;
	@Column(name = "compteur_classe")
    @XmlElement(name = "compteur_classe")
    protected String compteurClasse;
	@Transient
    @XmlElement(name = "compteur_combine", required = true)
    protected OuiNonListe compteurCombine;
	@Column(name = "compteur_longueur")
    @XmlElement(name = "compteur_longueur")
    protected String compteurLongueur;
	@Transient
    @XmlElement(name = "compteur_anneefabrication", required = true)
    @XmlSchemaType(name = "gYear")
    protected XMLGregorianCalendar compteurAnneefabrication;
	@Transient
    @XmlElement(name = "compteur_datepose", required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar compteurDatepose;
	@Transient
    @XmlElement(name = "compteur_datedepose")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar compteurDatedepose;
	@Transient
    @XmlElement(name = "compteur_module", required = true)
    protected OuiNonListe compteurModule;
	@Column(name = "compteur_marquemodule")
    @XmlElement(name = "compteur_marquemodule")
    protected String compteurMarquemodule;
	@Transient
    @XmlElement(name = "compteur_posemodule")
    protected PosesModuleCompteurListe compteurPosemodule;
	@Transient
    @XmlElement(name = "compteur_systemereleve")
    protected SystemesReleveListe compteurSystemereleve;
	@Column(name = "compteur_protocolecommreleve")
    @XmlElement(name = "compteur_protocolecommreleve")
    protected String compteurProtocolecommreleve;
	@Transient
    @XmlElement(name = "compteur_longueuronde")
    protected LongueursOndeListe compteurLongueuronde;
	@Transient
    @XmlElement(name = "compteur_anneeposemodule")
    @XmlSchemaType(name = "gYear")
    protected XMLGregorianCalendar compteurAnneeposemodule;
	@Transient
    @XmlElement(name = "compteur_typereleve", required = true)
    protected TypesRelevesListe compteurTypereleve;
	@Column(name = "compteur_pj")
    @XmlElement(name = "compteur_pj")
    protected String compteurPj;
	@Column(name = "id_chg")
	@XmlTransient
	private Integer idChg;
	
    /**
     * Gets the value of the compteurVeId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "compteur_ve_id")
    public String getCompteurVeId() {
    	if (compteurVeId == null) {
    		throw new IllegalArgumentException("Table " + AbonnesDAO.TYPE_COMPTEUR + " - Champs compteur_ve_id - Valeur 'Null' ou non permise par le XSD.");
    	}
        return compteurVeId;
    }

    /**
     * Sets the value of the compteurVeId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCompteurVeId(String value) {
        this.compteurVeId = value != null ? value : "";
    }

    /**
     * Gets the value of the compteurPointfournitureId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "compteur_pointfourniture_id")
    public String getCompteurPointfournitureId() {
    	if (compteurPointfournitureId == null) {
    		throw new IllegalArgumentException("Table " + AbonnesDAO.TYPE_COMPTEUR + " - Champs compteur_pointfourniture_id - Valeur 'Null' ou non permise par le XSD.");
    	}
        return compteurPointfournitureId;
    }

    /**
     * Sets the value of the compteurPointfournitureId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCompteurPointfournitureId(String value) {
        this.compteurPointfournitureId = value != null ? value : "";
    }

    /**
     * Gets the value of the compteurStatut property.
     * 
     * @return
     *     possible object is
     *     {@link StatutsCompteurListe }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "compteur_statut")
    public String getCompteurStatut() {
        return StatutsCompteurListe.getValue(AbonnesDAO.TYPE_COMPTEUR, "compteur_statut", compteurStatut);
    }

    /**
     * Sets the value of the compteurStatut property.
     * 
     * @param value
     *     allowed object is
     *     {@link StatutsCompteurListe }
     *     
     */
    public void setCompteurStatut(String value) {
        this.compteurStatut = StatutsCompteurListe.fromValue(AbonnesDAO.TYPE_COMPTEUR, "compteur_statut", value);
    }

    /**
     * Gets the value of the compteurSn property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "compteur_sn")
    public String getCompteurSn() {
    	if (compteurSn == null) {
    		throw new IllegalArgumentException("Table " + AbonnesDAO.TYPE_COMPTEUR + " - Champs compteur_sn - Valeur 'Null' ou non permise par le XSD.");
    	}
        return compteurSn;
    }

    /**
     * Sets the value of the compteurSn property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCompteurSn(String value) {
        this.compteurSn = value != null ? value : "";
    }

    /**
     * Gets the value of the compteurMarque property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "compteur_marque")
    public String getCompteurMarque() {
		if (compteurMarque == null) {
    		throw new IllegalArgumentException("Table " + AbonnesDAO.TYPE_COMPTEUR + " - Champs compteur_marque - Valeur 'Null' ou non permise par le XSD.");
    	}
        return compteurMarque;
    }

    /**
     * Sets the value of the compteurMarque property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCompteurMarque(String value) {
        this.compteurMarque = value != null ? value : "";
    }

    /**
     * Gets the value of the compteurModele property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "compteur_modele")
    public String getCompteurModele() {
    	if (compteurModele == null) {
    		throw new IllegalArgumentException("Table " + AbonnesDAO.TYPE_COMPTEUR + " - Champs compteur_modele - Valeur 'Null' ou non permise par le XSD.");
    	}
        return compteurModele;
    }

    /**
     * Sets the value of the compteurModele property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCompteurModele(String value) {
        this.compteurModele = value != null ? value : "";
    }

    /**
     * Gets the value of the compteurType property.
     * 
     * @return
     *     possible object is
     *     {@link TypesCompteursListe }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "compteur_type")
    public String getCompteurType() {
        return TypesCompteursListe.getValue(AbonnesDAO.TYPE_COMPTEUR, "compteur_type", compteurType);
    }

    /**
     * Sets the value of the compteurType property.
     * 
     * @param value
     *     allowed object is
     *     {@link TypesCompteursListe }
     *     
     */
    public void setCompteurType(String value) {
        this.compteurType = TypesCompteursListe.fromValue(AbonnesDAO.TYPE_COMPTEUR, "compteur_type", value);
    }

    /**
     * Gets the value of the compteurDiametre property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "compteur_diametre")
    public String getCompteurDiametre() {
        return DiametresCompteurListe.getValue(AbonnesDAO.TYPE_COMPTEUR, "compteur_diametre", compteurDiametre);
    }

    /**
     * Sets the value of the compteurDiametre property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCompteurDiametre(String value) {
        this.compteurDiametre = DiametresCompteurListe.fromValue(AbonnesDAO.TYPE_COMPTEUR, "compteur_diametre", value);
    }

    /**
     * Gets the value of the compteurClasse property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCompteurClasse() {
        return compteurClasse;
    }

    /**
     * Sets the value of the compteurClasse property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCompteurClasse(String value) {
        this.compteurClasse = value;
    }

    /**
     * Gets the value of the compteurCombine property.
     * 
     * @return
     *     possible object is
     *     {@link OuiNonListe }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "compteur_combine")
    public String getCompteurCombine() {
        return OuiNonListe.getValue(AbonnesDAO.TYPE_COMPTEUR, "compteur_combine", compteurCombine, true);
    }

    /**
     * Sets the value of the compteurCombine property.
     * 
     * @param value
     *     allowed object is
     *     {@link OuiNonListe }
     *     
     */
    public void setCompteurCombine(String value) {
        this.compteurCombine = OuiNonListe.fromValue(AbonnesDAO.TYPE_COMPTEUR, "compteur_combine", value, true);
    }

    /**
     * Gets the value of the compteurLongueur property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCompteurLongueur() {
        return compteurLongueur;
    }

    /**
     * Sets the value of the compteurLongueur property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCompteurLongueur(String value) {
        this.compteurLongueur = value;
    }

    /**
     * Gets the value of the compteurAnneefabrication property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    @Access(AccessType.PROPERTY)
	@Column(name = "compteur_anneefabrication")
    public int getCompteurAnneefabrication() {
    	if (compteurAnneefabrication == null) {
    		throw new IllegalArgumentException("Table " + AbonnesDAO.TYPE_COMPTEUR + " - Champs compteur_anneefabrication - Valeur 'Null' ou non permise par le XSD.");
    	}
        return VEHelper.extractYear(compteurAnneefabrication);
    }

    /**
     * Sets the value of the compteurAnneefabrication property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setCompteurAnneefabrication(XMLGregorianCalendar value) {
        this.compteurAnneefabrication = value;
    }
    
    /**
     * Sets the value of the compteurAnneefabrication property from hibernate.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setCompteurAnneefabrication(int value) {
    	this.compteurAnneefabrication = VEHelper.getYear(value);
    }

    /**
     * Gets the value of the compteurDatepose property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    @Access(AccessType.PROPERTY)
	@Column(name = "compteur_datepose")
    public Date getCompteurDatepose() {
    	if (compteurDatepose == null) {
    		throw new IllegalArgumentException("Table " + AbonnesDAO.TYPE_COMPTEUR + " - Champs compteur_datepose - Valeur 'Null' ou non permise par le XSD.");
    	}
        return VEHelper.toDate(compteurDatepose);
    }

    /**
     * Sets the value of the compteurDatepose property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setCompteurDatepose(XMLGregorianCalendar value) {
        this.compteurDatepose = value;
    }
    
    /**
     * Sets the value of the compteurDatepose property from hibernate.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setCompteurDatepose(Date value) {
    	this.compteurDatepose = VEHelper.fromDate(value);
    }

    /**
     * Gets the value of the compteurDatedepose property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    @Access(AccessType.PROPERTY)
	@Column(name = "compteur_datedepose")
    public Date getCompteurDatedepose() {
        return VEHelper.toDate(compteurDatedepose);
    }

    /**
     * Sets the value of the compteurDatedepose property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setCompteurDatedepose(XMLGregorianCalendar value) {
        this.compteurDatedepose = value;
    }
    
    /**
     * Sets the value of the compteurDatedepose property from hibernate.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setCompteurDatedepose(Date value) {
    	this.compteurDatedepose = VEHelper.fromDate(value);
    }

    /**
     * Gets the value of the compteurModule property.
     * 
     * @return
     *     possible object is
     *     {@link OuiNonListe }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "compteur_module")
    public String getCompteurModule() {
        return OuiNonListe.getValue(AbonnesDAO.TYPE_COMPTEUR, "compteur_module", compteurModule, true);
    }

    /**
     * Sets the value of the compteurModule property.
     * 
     * @param value
     *     allowed object is
     *     {@link OuiNonListe }
     *     
     */
    public void setCompteurModule(String value) {
        this.compteurModule = OuiNonListe.fromValue(AbonnesDAO.TYPE_COMPTEUR, "compteur_module", value, true);
    }

    /**
     * Gets the value of the compteurMarquemodule property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCompteurMarquemodule() {
        return compteurMarquemodule;
    }

    /**
     * Sets the value of the compteurMarquemodule property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCompteurMarquemodule(String value) {
        this.compteurMarquemodule = value;
    }

    /**
     * Gets the value of the compteurPosemodule property.
     * 
     * @return
     *     possible object is
     *     {@link PosesModuleCompteurListe }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "compteur_posemodule")
    public String getCompteurPosemodule() {
        return PosesModuleCompteurListe.getValue(AbonnesDAO.TYPE_COMPTEUR, "compteur_posemodule", compteurPosemodule, false);
    }

    /**
     * Sets the value of the compteurPosemodule property.
     * 
     * @param value
     *     allowed object is
     *     {@link PosesModuleCompteurListe }
     *     
     */
    public void setCompteurPosemodule(String value) {
        this.compteurPosemodule = PosesModuleCompteurListe.fromValue(AbonnesDAO.TYPE_COMPTEUR, "compteur_posemodule", value, false);
    }

    /**
     * Gets the value of the compteurSystemereleve property.
     * 
     * @return
     *     possible object is
     *     {@link SystemesReleveListe }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "compteur_systemereleve")
    public String getCompteurSystemereleve() {
        return SystemesReleveListe.getValue(AbonnesDAO.TYPE_COMPTEUR, "compteur_systemereleve", compteurSystemereleve, false);
    }

    /**
     * Sets the value of the compteurSystemereleve property.
     * 
     * @param value
     *     allowed object is
     *     {@link SystemesReleveListe }
     *     
     */
    public void setCompteurSystemereleve(String value) {
        this.compteurSystemereleve = SystemesReleveListe.fromValue(AbonnesDAO.TYPE_COMPTEUR, "compteur_systemereleve", value, false);
    }

    /**
     * Gets the value of the compteurProtocolecommreleve property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCompteurProtocolecommreleve() {
        return compteurProtocolecommreleve;
    }

    /**
     * Sets the value of the compteurProtocolecommreleve property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCompteurProtocolecommreleve(String value) {
        this.compteurProtocolecommreleve = value;
    }

    /**
     * Gets the value of the compteurLongueuronde property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "compteur_longueuronde")
    public String getCompteurLongueuronde() {
        return LongueursOndeListe.getValue(AbonnesDAO.TYPE_COMPTEUR, "compteur_longueuronde", compteurLongueuronde, false);
    }

    /**
     * Sets the value of the compteurLongueuronde property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCompteurLongueuronde(String value) {
        this.compteurLongueuronde = LongueursOndeListe.fromValue(AbonnesDAO.TYPE_COMPTEUR, "compteur_longueuronde", value, false);
    }

    /**
     * Gets the value of the compteurAnneeposemodule property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    @Access(AccessType.PROPERTY)
	@Column(name = "compteur_anneeposemodule")
    public Integer getCompteurAnneeposemodule() {
        return VEHelper.extractYear(compteurAnneeposemodule);
    }

    /**
     * Sets the value of the compteurAnneeposemodule property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setCompteurAnneeposemodule(XMLGregorianCalendar value) {
        this.compteurAnneeposemodule = value;
    }
    
    /**
     * Sets the value of the compteurAnneeposemodule property from hibernate.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setCompteurAnneeposemodule(Integer value) {
    	this.compteurAnneeposemodule = VEHelper.getYear(value);
    }

    /**
     * Gets the value of the compteurTypereleve property.
     * 
     * @return
     *     possible object is
     *     {@link TypesRelevesListe }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "compteur_typereleve")
    public String getCompteurTypereleve() {
        return TypesRelevesListe.getValue(AbonnesDAO.TYPE_COMPTEUR, "compteur_typereleve", compteurTypereleve, false);
    }

    /**
     * Sets the value of the compteurTypereleve property.
     * 
     * @param value
     *     allowed object is
     *     {@link TypesRelevesListe }
     *     
     */
    public void setCompteurTypereleve(String value) {
        this.compteurTypereleve = TypesRelevesListe.fromValue(AbonnesDAO.TYPE_COMPTEUR, "compteur_typereleve", value);
    }

    /**
     * Gets the value of the compteurPj property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCompteurPj() {
        return compteurPj;
    }

    /**
     * Sets the value of the compteurPj property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCompteurPj(String value) {
        this.compteurPj = value;
    }

	public void setIdChg(Integer idChg) {
		this.idChg = idChg;
	}

}
