//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.11.28 at 01:19:07 PM CET 
//


package bpm.gateway.core.veolia.abonnes;

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
 * <p>Java class for typeBranchement complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="typeBranchement">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="branchement_ve_id" type="{}branchementIDType"/>
 *         &lt;element name="branchement_anneepose" type="{http://www.w3.org/2001/XMLSchema}gYear" minOccurs="0"/>
 *         &lt;element name="branchement_natureavantraccord" type="{}naturesAvantRaccordListe" minOccurs="0"/>
 *         &lt;element name="branchement_nbptfourniture" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="branchement_infocompl" type="{}StringRestricted255Type" minOccurs="0"/>
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
@Table (name = AbonnesDAO.TYPE_BRANCHEMENT)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "typeBranchement", propOrder = {
    "branchementVeId",
    "branchementAnneepose",
    "branchementNatureavantraccord",
    "branchementNbptfourniture",
    "branchementInfocompl"
})
@XmlRootElement(name = AbonnesDAO.ROOT_BRANCHEMENT)
public class TypeBranchement {
	
	@Id
	@Column(name = "branchement_id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@XmlTransient
    protected int id;
	@Transient
    @XmlElement(name = "branchement_ve_id", required = true)
    protected String branchementVeId;
	@Transient
    @XmlElement(name = "branchement_anneepose")
    @XmlSchemaType(name = "gYear")
    protected XMLGregorianCalendar branchementAnneepose;
	@Transient
    @XmlElement(name = "branchement_natureavantraccord")
    protected NaturesAvantRaccordListe branchementNatureavantraccord;
	@Transient
    @XmlElement(name = "branchement_nbptfourniture")
    protected Integer branchementNbptfourniture;
	@Column(name = "branchement_infocompl")
    @XmlElement(name = "branchement_infocompl")
    protected String branchementInfocompl;
	@Column(name = "id_chg")
	@XmlTransient
	private Integer idChg;

    /**
     * Gets the value of the branchementVeId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "branchement_ve_id")
    public String getBranchementVeId() {
    	if (branchementVeId == null) {
    		throw new IllegalArgumentException("Table " + AbonnesDAO.TYPE_BRANCHEMENT + " - Champs branchement_ve_id - Valeur 'Null' ou non permise par le XSD.");
    	}
        return branchementVeId;
    }

    /**
     * Sets the value of the branchementVeId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBranchementVeId(String value) {
        this.branchementVeId = value != null ? value : "";
    }

    /**
     * Gets the value of the branchementAnneepose property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    @Access(AccessType.PROPERTY)
	@Column(name = "branchement_anneepose")
    public Integer getBranchementAnneepose() {
    	return VEHelper.extractYear(branchementAnneepose);
    }

    /**
     * Sets the value of the branchementAnneepose property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setBranchementAnneepose(XMLGregorianCalendar value) {
        this.branchementAnneepose = value;
    }

    /**
     * Sets the value of the branchementAnneepose property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setBranchementAnneepose(Integer value) {
        this.branchementAnneepose = VEHelper.getYear(value);
    }
    
    /**
     * Sets the value of the branchementAnneepose property from hibernate.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setBranchementAnneepose(String value) {
    	this.branchementAnneepose = VEHelper.getYear(value);
    }

    /**
     * Gets the value of the branchementNatureavantraccord property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "branchement_natureavantraccord")
    public String getBranchementNatureavantraccord() {
        return NaturesAvantRaccordListe.getValue(AbonnesDAO.TYPE_BRANCHEMENT, "branchement_natureavantraccord", branchementNatureavantraccord, false);
    }

    /**
     * Sets the value of the branchementNatureavantraccord property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBranchementNatureavantraccord(String value) {
        this.branchementNatureavantraccord = NaturesAvantRaccordListe.fromValue(AbonnesDAO.TYPE_BRANCHEMENT, "branchement_natureavantraccord", value, false);
    }

    /**
     * Gets the value of the branchementNbptfourniture property.
     * 
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "branchement_nbptfourniture")
    public int getBranchementNbptfourniture() {
    	if (branchementNbptfourniture == null) {
    		throw new IllegalArgumentException("Table " + AbonnesDAO.TYPE_BRANCHEMENT + " - Champs branchement_nbptfourniture - Valeur 'Null' ou non permise par le XSD.");
    	}
        return branchementNbptfourniture;
    }

    /**
     * Sets the value of the branchementNbptfourniture property.
     * 
     */
    public void setBranchementNbptfourniture(int value) {
        this.branchementNbptfourniture = value;
    }

    /**
     * Gets the value of the branchementInfocompl property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBranchementInfocompl() {
        return branchementInfocompl;
    }

    /**
     * Sets the value of the branchementInfocompl property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBranchementInfocompl(String value) {
        this.branchementInfocompl = value;
    }

	public void setIdChg(Integer idChg) {
		this.idChg = idChg;
	}

}
