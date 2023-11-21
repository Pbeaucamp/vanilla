//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.8-b130911.1802 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2021.11.17 à 03:11:20 PM CET 
//


package bpm.vanillahub.runtime.specifics.onisep;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the bpm.vanillahub.runtime.specifics.onisep package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _PublicationsPublication_QNAME = new QName("", "publication");
    private final static QName _MetiersAssociesMetierAssocie_QNAME = new QName("", "metier_associe");
    private final static QName _StatutsStatut_QNAME = new QName("", "statut");
    private final static QName _CentresInteretCentreInteret_QNAME = new QName("", "centre_interet");
    private final static QName _RomesV3RomeV3_QNAME = new QName("", "romeV3");
    private final static QName _FormationsMinRequiseFormationMinRequise_QNAME = new QName("", "formation_min_requise");
    private final static QName _SynonymesSynonyme_QNAME = new QName("", "synonyme");
    private final static QName _SourcesNumeriquesSource_QNAME = new QName("", "source");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: bpm.vanillahub.runtime.specifics.onisep
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Metiers }
     * 
     */
    public Metiers createMetiers() {
        return new Metiers();
    }

    /**
     * Create an instance of {@link Metier }
     * 
     */
    public Metier createMetier() {
        return new Metier();
    }

    /**
     * Create an instance of {@link Synonymes }
     * 
     */
    public Synonymes createSynonymes() {
        return new Synonymes();
    }

    /**
     * Create an instance of {@link RomesV3 }
     * 
     */
    public RomesV3 createRomesV3() {
        return new RomesV3();
    }

    /**
     * Create an instance of {@link FormatsCourts }
     * 
     */
    public FormatsCourts createFormatsCourts() {
        return new FormatsCourts();
    }

    /**
     * Create an instance of {@link Statuts }
     * 
     */
    public Statuts createStatuts() {
        return new Statuts();
    }

    /**
     * Create an instance of {@link MetiersAssocies }
     * 
     */
    public MetiersAssocies createMetiersAssocies() {
        return new MetiersAssocies();
    }

    /**
     * Create an instance of {@link NiveauAccesMin }
     * 
     */
    public NiveauAccesMin createNiveauAccesMin() {
        return new NiveauAccesMin();
    }

    /**
     * Create an instance of {@link FormationsMinRequise }
     * 
     */
    public FormationsMinRequise createFormationsMinRequise() {
        return new FormationsMinRequise();
    }

    /**
     * Create an instance of {@link Publications }
     * 
     */
    public Publications createPublications() {
        return new Publications();
    }

    /**
     * Create an instance of {@link SourcesNumeriques }
     * 
     */
    public SourcesNumeriques createSourcesNumeriques() {
        return new SourcesNumeriques();
    }

    /**
     * Create an instance of {@link SecteursActivite }
     * 
     */
    public SecteursActivite createSecteursActivite() {
        return new SecteursActivite();
    }

    /**
     * Create an instance of {@link CentresInteret }
     * 
     */
    public CentresInteret createCentresInteret() {
        return new CentresInteret();
    }

    /**
     * Create an instance of {@link CentreInteret }
     * 
     */
    public CentreInteret createCentreInteret() {
        return new CentreInteret();
    }

    /**
     * Create an instance of {@link SecteurActivite }
     * 
     */
    public SecteurActivite createSecteurActivite() {
        return new SecteurActivite();
    }

    /**
     * Create an instance of {@link Source }
     * 
     */
    public Source createSource() {
        return new Source();
    }

    /**
     * Create an instance of {@link Publication }
     * 
     */
    public Publication createPublication() {
        return new Publication();
    }

    /**
     * Create an instance of {@link FormationMinRequise }
     * 
     */
    public FormationMinRequise createFormationMinRequise() {
        return new FormationMinRequise();
    }

    /**
     * Create an instance of {@link MetierAssocie }
     * 
     */
    public MetierAssocie createMetierAssocie() {
        return new MetierAssocie();
    }

    /**
     * Create an instance of {@link Statut }
     * 
     */
    public Statut createStatut() {
        return new Statut();
    }

    /**
     * Create an instance of {@link FormatCourt }
     * 
     */
    public FormatCourt createFormatCourt() {
        return new FormatCourt();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Publication }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "publication", scope = Publications.class)
    public JAXBElement<Publication> createPublicationsPublication(Publication value) {
        return new JAXBElement<Publication>(_PublicationsPublication_QNAME, Publication.class, Publications.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MetierAssocie }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "metier_associe", scope = MetiersAssocies.class)
    public JAXBElement<MetierAssocie> createMetiersAssociesMetierAssocie(MetierAssocie value) {
        return new JAXBElement<MetierAssocie>(_MetiersAssociesMetierAssocie_QNAME, MetierAssocie.class, MetiersAssocies.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Statut }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "statut", scope = Statuts.class)
    public JAXBElement<Statut> createStatutsStatut(Statut value) {
        return new JAXBElement<Statut>(_StatutsStatut_QNAME, Statut.class, Statuts.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CentreInteret }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "centre_interet", scope = CentresInteret.class)
    public JAXBElement<CentreInteret> createCentresInteretCentreInteret(CentreInteret value) {
        return new JAXBElement<CentreInteret>(_CentresInteretCentreInteret_QNAME, CentreInteret.class, CentresInteret.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "romeV3", scope = RomesV3 .class)
    public JAXBElement<String> createRomesV3RomeV3(String value) {
        return new JAXBElement<String>(_RomesV3RomeV3_QNAME, String.class, RomesV3 .class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FormationMinRequise }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "formation_min_requise", scope = FormationsMinRequise.class)
    public JAXBElement<FormationMinRequise> createFormationsMinRequiseFormationMinRequise(FormationMinRequise value) {
        return new JAXBElement<FormationMinRequise>(_FormationsMinRequiseFormationMinRequise_QNAME, FormationMinRequise.class, FormationsMinRequise.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "synonyme", scope = Synonymes.class)
    public JAXBElement<String> createSynonymesSynonyme(String value) {
        return new JAXBElement<String>(_SynonymesSynonyme_QNAME, String.class, Synonymes.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Source }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "source", scope = SourcesNumeriques.class)
    public JAXBElement<Source> createSourcesNumeriquesSource(Source value) {
        return new JAXBElement<Source>(_SourcesNumeriquesSource_QNAME, Source.class, SourcesNumeriques.class, value);
    }

}
