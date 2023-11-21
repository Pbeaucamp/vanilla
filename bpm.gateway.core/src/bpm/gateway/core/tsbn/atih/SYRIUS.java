//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.08.18 at 03:51:39 PM CEST 
//


package bpm.gateway.core.tsbn.atih;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ETABLISSEMENT">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="FINESS" type="{http://www.w3.org/2001/XMLSchema}normalizedString"/>
 *                   &lt;element name="ORDRE" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *                   &lt;element name="EXTRACT" type="{http://www.w3.org/2001/XMLSchema}normalizedString"/>
 *                   &lt;element name="DATEDEBUT" type="{http://www.w3.org/2001/XMLSchema}normalizedString"/>
 *                   &lt;element name="DATEFIN" type="{http://www.w3.org/2001/XMLSchema}normalizedString"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;sequence>
 *           &lt;element name="PASSAGES">
 *             &lt;complexType>
 *               &lt;complexContent>
 *                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                   &lt;sequence maxOccurs="unbounded">
 *                     &lt;element name="PATIENT">
 *                       &lt;complexType>
 *                         &lt;complexContent>
 *                           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                             &lt;sequence>
 *                               &lt;element name="CODEGEO" type="{http://www.w3.org/2001/XMLSchema}normalizedString" minOccurs="0"/>
 *                               &lt;element name="AGE" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *                               &lt;element name="AGE_JOURS" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *                               &lt;element name="SEXE" type="{http://www.w3.org/2001/XMLSchema}normalizedString" minOccurs="0"/>
 *                               &lt;element name="ENTREE" type="{http://www.w3.org/2001/XMLSchema}normalizedString" minOccurs="0"/>
 *                               &lt;element name="MODE_ENTREE" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *                               &lt;element name="PROVENANCE" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *                               &lt;element name="TRANSPORT" type="{http://www.w3.org/2001/XMLSchema}normalizedString" minOccurs="0"/>
 *                               &lt;element name="TRANSPORT_PEC" type="{http://www.w3.org/2001/XMLSchema}normalizedString" minOccurs="0"/>
 *                               &lt;element name="MOTIF" type="{http://www.w3.org/2001/XMLSchema}normalizedString" minOccurs="0"/>
 *                               &lt;element name="GRAVITE" type="{http://www.w3.org/2001/XMLSchema}normalizedString" minOccurs="0"/>
 *                               &lt;element name="DP" type="{http://www.w3.org/2001/XMLSchema}normalizedString" minOccurs="0"/>
 *                               &lt;element name="LISTE_DA">
 *                                 &lt;complexType>
 *                                   &lt;complexContent>
 *                                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                       &lt;sequence maxOccurs="unbounded">
 *                                         &lt;element name="DA" type="{http://www.w3.org/2001/XMLSchema}normalizedString" minOccurs="0"/>
 *                                       &lt;/sequence>
 *                                     &lt;/restriction>
 *                                   &lt;/complexContent>
 *                                 &lt;/complexType>
 *                               &lt;/element>
 *                               &lt;element name="LISTE_ACTES">
 *                                 &lt;complexType>
 *                                   &lt;complexContent>
 *                                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                       &lt;sequence maxOccurs="unbounded">
 *                                         &lt;element name="ACTE" type="{http://www.w3.org/2001/XMLSchema}normalizedString" minOccurs="0"/>
 *                                       &lt;/sequence>
 *                                     &lt;/restriction>
 *                                   &lt;/complexContent>
 *                                 &lt;/complexType>
 *                               &lt;/element>
 *                               &lt;element name="SORTIE" type="{http://www.w3.org/2001/XMLSchema}normalizedString" minOccurs="0"/>
 *                               &lt;element name="MODE_SORTIE" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *                               &lt;element name="DESTINATION" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *                               &lt;element name="ORIENT" type="{http://www.w3.org/2001/XMLSchema}normalizedString" minOccurs="0"/>
 *                             &lt;/sequence>
 *                           &lt;/restriction>
 *                         &lt;/complexContent>
 *                       &lt;/complexType>
 *                     &lt;/element>
 *                   &lt;/sequence>
 *                 &lt;/restriction>
 *               &lt;/complexContent>
 *             &lt;/complexType>
 *           &lt;/element>
 *         &lt;/sequence>
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
    "etablissement",
    "passages"
})
@XmlRootElement(name = "SYRIUS")
public class SYRIUS {

    @XmlElement(name = "ETABLISSEMENT", required = true)
    protected ETABLISSEMENT etablissement;
    @XmlElement(name = "PASSAGES", required = true)
    protected PASSAGES passages;

    /**
     * Gets the value of the etablissement property.
     * 
     * @return
     *     possible object is
     *     {@link ETABLISSEMENT }
     *     
     */
    public ETABLISSEMENT getETABLISSEMENT() {
        return etablissement;
    }

    /**
     * Sets the value of the etablissement property.
     * 
     * @param value
     *     allowed object is
     *     {@link ETABLISSEMENT }
     *     
     */
    public void setETABLISSEMENT(ETABLISSEMENT value) {
        this.etablissement = value;
    }

    /**
     * Gets the value of the passages property.
     * 
     * @return
     *     possible object is
     *     {@link PASSAGES }
     *     
     */
    public PASSAGES getPASSAGES() {
        return passages;
    }

    /**
     * Sets the value of the passages property.
     * 
     * @param value
     *     allowed object is
     *     {@link PASSAGES }
     *     
     */
    public void setPASSAGES(PASSAGES value) {
        this.passages = value;
    }

}
