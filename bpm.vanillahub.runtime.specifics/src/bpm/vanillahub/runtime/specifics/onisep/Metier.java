//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.8-b130911.1802 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2021.11.17 à 03:11:20 PM CET 
//


package bpm.vanillahub.runtime.specifics.onisep;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import bpm.vanillahub.runtime.specifics.IExportCSV;


/**
 * <p>Classe Java pour anonymous complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="identifiant" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="nom_metier" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="libelle_feminin" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="libelle_masculin" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="synonymes">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="synonyme" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="romesV3">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="romeV3" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="competences" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="condition_travail" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="nature_travail" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="acces_metier" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="vie_professionnelle" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="accroche_metier" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="formats_courts">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="format_court" maxOccurs="unbounded" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="type" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="libelle" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="descriptif" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="statuts">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="statut" maxOccurs="unbounded" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="id_ideo1" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *                             &lt;element name="libelle" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="metiers_associes">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="metier_associe" maxOccurs="unbounded" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="libelle" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="niveau_acces_min">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="libelle" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="formations_min_requise">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="formation_min_requise" maxOccurs="unbounded" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="libelle" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="publications">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="publication" maxOccurs="unbounded" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="code_librairie" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *                             &lt;element name="titre_publication" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="editeur" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="annee" type="{http://www.w3.org/2001/XMLSchema}short"/>
 *                             &lt;element name="collection" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="sources_numeriques">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="source" maxOccurs="unbounded" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="valeur" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="commentaire" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="secteurs_activite">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="secteur_activite" maxOccurs="unbounded" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="libelle" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="centres_interet">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="centre_interet" maxOccurs="unbounded" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="libelle" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "identifiant",
    "nomMetier",
    "libelleFeminin",
    "libelleMasculin",
    "synonymes",
    "romesV3",
    "competences",
    "conditionTravail",
    "natureTravail",
    "accesMetier",
    "vieProfessionnelle",
    "accrocheMetier",
    "formatsCourts",
    "statuts",
    "metiersAssocies",
    "niveauAccesMin",
    "formationsMinRequise",
    "publications",
    "sourcesNumeriques",
    "secteursActivite",
    "centresInteret"
})
public class Metier implements IExportCSV {

    @XmlElement(required = true)
    protected String identifiant;
    @XmlElement(name = "nom_metier", required = true)
    protected String nomMetier;
    @XmlElement(name = "libelle_feminin", required = true)
    protected String libelleFeminin;
    @XmlElement(name = "libelle_masculin", required = true)
    protected String libelleMasculin;
    @XmlElement(required = true)
    protected Synonymes synonymes;
    @XmlElement(required = true)
    protected RomesV3 romesV3;
    @XmlElement(required = true)
    protected String competences;
    @XmlElement(name = "condition_travail", required = true)
    protected String conditionTravail;
    @XmlElement(name = "nature_travail", required = true)
    protected String natureTravail;
    @XmlElement(name = "acces_metier", required = true)
    protected String accesMetier;
    @XmlElement(name = "vie_professionnelle", required = true)
    protected String vieProfessionnelle;
    @XmlElement(name = "accroche_metier", required = true)
    protected String accrocheMetier;
    @XmlElement(name = "formats_courts", required = true)
    protected FormatsCourts formatsCourts;
    @XmlElement(required = true)
    protected Statuts statuts;
    @XmlElement(name = "metiers_associes", required = true)
    protected MetiersAssocies metiersAssocies;
    @XmlElement(name = "niveau_acces_min", required = true)
    protected NiveauAccesMin niveauAccesMin;
    @XmlElement(name = "formations_min_requise", required = true)
    protected FormationsMinRequise formationsMinRequise;
    @XmlElement(required = true)
    protected Publications publications;
    @XmlElement(name = "sources_numeriques", required = true)
    protected SourcesNumeriques sourcesNumeriques;
    @XmlElement(name = "secteurs_activite", required = true)
    protected SecteursActivite secteursActivite;
    @XmlElement(name = "centres_interet", required = true)
    protected CentresInteret centresInteret;

    /**
     * Obtient la valeur de la propriété identifiant.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdentifiant() {
        return identifiant;
    }

    /**
     * Définit la valeur de la propriété identifiant.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdentifiant(String value) {
        this.identifiant = value;
    }

    /**
     * Obtient la valeur de la propriété nomMetier.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNomMetier() {
        return nomMetier;
    }

    /**
     * Définit la valeur de la propriété nomMetier.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNomMetier(String value) {
        this.nomMetier = value;
    }

    /**
     * Obtient la valeur de la propriété libelleFeminin.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLibelleFeminin() {
        return libelleFeminin;
    }

    /**
     * Définit la valeur de la propriété libelleFeminin.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLibelleFeminin(String value) {
        this.libelleFeminin = value;
    }

    /**
     * Obtient la valeur de la propriété libelleMasculin.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLibelleMasculin() {
        return libelleMasculin;
    }

    /**
     * Définit la valeur de la propriété libelleMasculin.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLibelleMasculin(String value) {
        this.libelleMasculin = value;
    }

    /**
     * Obtient la valeur de la propriété synonymes.
     * 
     * @return
     *     possible object is
     *     {@link Synonymes }
     *     
     */
    public Synonymes getSynonymes() {
        return synonymes;
    }

    /**
     * Définit la valeur de la propriété synonymes.
     * 
     * @param value
     *     allowed object is
     *     {@link Synonymes }
     *     
     */
    public void setSynonymes(Synonymes value) {
        this.synonymes = value;
    }

    /**
     * Obtient la valeur de la propriété romesV3.
     * 
     * @return
     *     possible object is
     *     {@link RomesV3 }
     *     
     */
    public RomesV3 getRomesV3() {
        return romesV3;
    }

    /**
     * Définit la valeur de la propriété romesV3.
     * 
     * @param value
     *     allowed object is
     *     {@link RomesV3 }
     *     
     */
    public void setRomesV3(RomesV3 value) {
        this.romesV3 = value;
    }

    /**
     * Obtient la valeur de la propriété competences.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCompetences() {
        return competences;
    }

    /**
     * Définit la valeur de la propriété competences.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCompetences(String value) {
        this.competences = value;
    }

    /**
     * Obtient la valeur de la propriété conditionTravail.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getConditionTravail() {
        return conditionTravail;
    }

    /**
     * Définit la valeur de la propriété conditionTravail.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setConditionTravail(String value) {
        this.conditionTravail = value;
    }

    /**
     * Obtient la valeur de la propriété natureTravail.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNatureTravail() {
        return natureTravail;
    }

    /**
     * Définit la valeur de la propriété natureTravail.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNatureTravail(String value) {
        this.natureTravail = value;
    }

    /**
     * Obtient la valeur de la propriété accesMetier.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAccesMetier() {
        return accesMetier;
    }

    /**
     * Définit la valeur de la propriété accesMetier.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAccesMetier(String value) {
        this.accesMetier = value;
    }

    /**
     * Obtient la valeur de la propriété vieProfessionnelle.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVieProfessionnelle() {
        return vieProfessionnelle;
    }

    /**
     * Définit la valeur de la propriété vieProfessionnelle.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVieProfessionnelle(String value) {
        this.vieProfessionnelle = value;
    }

    /**
     * Obtient la valeur de la propriété accrocheMetier.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAccrocheMetier() {
        return accrocheMetier;
    }

    /**
     * Définit la valeur de la propriété accrocheMetier.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAccrocheMetier(String value) {
        this.accrocheMetier = value;
    }

    /**
     * Obtient la valeur de la propriété formatsCourts.
     * 
     * @return
     *     possible object is
     *     {@link FormatsCourts }
     *     
     */
    public FormatsCourts getFormatsCourts() {
        return formatsCourts;
    }

    /**
     * Définit la valeur de la propriété formatsCourts.
     * 
     * @param value
     *     allowed object is
     *     {@link FormatsCourts }
     *     
     */
    public void setFormatsCourts(FormatsCourts value) {
        this.formatsCourts = value;
    }

    /**
     * Obtient la valeur de la propriété statuts.
     * 
     * @return
     *     possible object is
     *     {@link Statuts }
     *     
     */
    public Statuts getStatuts() {
        return statuts;
    }

    /**
     * Définit la valeur de la propriété statuts.
     * 
     * @param value
     *     allowed object is
     *     {@link Statuts }
     *     
     */
    public void setStatuts(Statuts value) {
        this.statuts = value;
    }

    /**
     * Obtient la valeur de la propriété metiersAssocies.
     * 
     * @return
     *     possible object is
     *     {@link MetiersAssocies }
     *     
     */
    public MetiersAssocies getMetiersAssocies() {
        return metiersAssocies;
    }

    /**
     * Définit la valeur de la propriété metiersAssocies.
     * 
     * @param value
     *     allowed object is
     *     {@link MetiersAssocies }
     *     
     */
    public void setMetiersAssocies(MetiersAssocies value) {
        this.metiersAssocies = value;
    }

    /**
     * Obtient la valeur de la propriété niveauAccesMin.
     * 
     * @return
     *     possible object is
     *     {@link NiveauAccesMin }
     *     
     */
    public NiveauAccesMin getNiveauAccesMin() {
        return niveauAccesMin;
    }

    /**
     * Définit la valeur de la propriété niveauAccesMin.
     * 
     * @param value
     *     allowed object is
     *     {@link NiveauAccesMin }
     *     
     */
    public void setNiveauAccesMin(NiveauAccesMin value) {
        this.niveauAccesMin = value;
    }

    /**
     * Obtient la valeur de la propriété formationsMinRequise.
     * 
     * @return
     *     possible object is
     *     {@link FormationsMinRequise }
     *     
     */
    public FormationsMinRequise getFormationsMinRequise() {
        return formationsMinRequise;
    }

    /**
     * Définit la valeur de la propriété formationsMinRequise.
     * 
     * @param value
     *     allowed object is
     *     {@link FormationsMinRequise }
     *     
     */
    public void setFormationsMinRequise(FormationsMinRequise value) {
        this.formationsMinRequise = value;
    }

    /**
     * Obtient la valeur de la propriété publications.
     * 
     * @return
     *     possible object is
     *     {@link Publications }
     *     
     */
    public Publications getPublications() {
        return publications;
    }

    /**
     * Définit la valeur de la propriété publications.
     * 
     * @param value
     *     allowed object is
     *     {@link Publications }
     *     
     */
    public void setPublications(Publications value) {
        this.publications = value;
    }

    /**
     * Obtient la valeur de la propriété sourcesNumeriques.
     * 
     * @return
     *     possible object is
     *     {@link SourcesNumeriques }
     *     
     */
    public SourcesNumeriques getSourcesNumeriques() {
        return sourcesNumeriques;
    }

    /**
     * Définit la valeur de la propriété sourcesNumeriques.
     * 
     * @param value
     *     allowed object is
     *     {@link SourcesNumeriques }
     *     
     */
    public void setSourcesNumeriques(SourcesNumeriques value) {
        this.sourcesNumeriques = value;
    }

    /**
     * Obtient la valeur de la propriété secteursActivite.
     * 
     * @return
     *     possible object is
     *     {@link SecteursActivite }
     *     
     */
    public SecteursActivite getSecteursActivite() {
        return secteursActivite;
    }

    /**
     * Définit la valeur de la propriété secteursActivite.
     * 
     * @param value
     *     allowed object is
     *     {@link SecteursActivite }
     *     
     */
    public void setSecteursActivite(SecteursActivite value) {
        this.secteursActivite = value;
    }

    /**
     * Obtient la valeur de la propriété centresInteret.
     * 
     * @return
     *     possible object is
     *     {@link CentresInteret }
     *     
     */
    public CentresInteret getCentresInteret() {
        return centresInteret;
    }

    /**
     * Définit la valeur de la propriété centresInteret.
     * 
     * @param value
     *     allowed object is
     *     {@link CentresInteret }
     *     
     */
    public void setCentresInteret(CentresInteret value) {
        this.centresInteret = value;
    }

	@Override
	public String getClassKeyID() {
		return getClass().getName();
	}

	@Override
	public void buildCSV(String parentId, HashMap<String, List<List<Object>>> items) {
		String keyId = getClassKeyID();
		if (items.get(keyId) == null) {
			List<List<Object>> lines = new ArrayList<List<Object>>();
			
			List<Object> line = new ArrayList<Object>();
			line.add("identifiant");
			line.add("nomMetier");
			line.add("libelleFeminin");
			line.add("libelleMasculin");
			line.add("conditionTravail");
			line.add("natureTravail");
			line.add("accesMetier");
			line.add("vieProfessionnelle");
			line.add("accrocheMetier");
			line.add("niveauAccesMin_id");
			line.add("niveauAccesMin_libelle");
			lines.add(line);
			
			items.put(keyId, lines);
		}
		
		List<Object> line = new ArrayList<Object>();
		line.add(identifiant);
		line.add(nomMetier);
		line.add(libelleFeminin);
		line.add(libelleMasculin);
		line.add(conditionTravail);
		line.add(natureTravail);
		line.add(accesMetier);
		line.add(vieProfessionnelle);
		line.add(accrocheMetier);
		
		if (niveauAccesMin != null) {
			line.add(niveauAccesMin.getId());
			line.add(niveauAccesMin.getLibelle());
		}
		else {
			line.add("");
			line.add("");
		}

		List<List<Object>> values = items.get(keyId);
		values.add(line);
		
		if (romesV3 != null) {
			romesV3.buildCSV(identifiant, items);
		}
		
		if (synonymes != null) {
			synonymes.buildCSV(identifiant, items);
		}
		
		if (formatsCourts != null) {
			formatsCourts.buildCSV(identifiant, items);
		}
		
		if (statuts != null) {
			statuts.buildCSV(identifiant, items);
		}
		
		if (metiersAssocies != null) {
			metiersAssocies.buildCSV(identifiant, items);
		}
		
		if (formationsMinRequise != null) {
			formationsMinRequise.buildCSV(identifiant, items);
		}
		
		if (publications != null) {
			publications.buildCSV(identifiant, items);
		}
		
		if (sourcesNumeriques != null) {
			sourcesNumeriques.buildCSV(identifiant, items);
		}
		
		if (secteursActivite != null) {
			secteursActivite.buildCSV(identifiant, items);
		}
		
		if (centresInteret != null) {
			centresInteret.buildCSV(identifiant, items);
		}
		
		//Parsing competence's HTML
		if (competences != null) {
			try {
				String compKeyId = "competences";
				if (items.get(compKeyId) == null) {
					List<List<Object>> compLines = new ArrayList<List<Object>>();
				    
					List<Object> compLine = new ArrayList<Object>();
					compLine.add("parentId");
					compLine.add("title");
					compLine.add("description");
					compLines.add(compLine);
					
					items.put(compKeyId, compLines);
				}
				
				
				Document doc = Jsoup.parse(competences);

				Elements titles = doc.select("h5");
				Elements descriptions = doc.select("p");
				for (int i = 0; i < titles.size(); i++) {
					Element titleEl = titles.get(i);
					Element descriptionEl = descriptions.get(i);
					
					String title = titleEl.text();
					String description = descriptionEl.text();
					
					List<Object> compLine = new ArrayList<Object>();
					compLine.add(identifiant);
					compLine.add(title);
					compLine.add(description);
	
					List<List<Object>> compValues = items.get(compKeyId);
					compValues.add(compLine);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
