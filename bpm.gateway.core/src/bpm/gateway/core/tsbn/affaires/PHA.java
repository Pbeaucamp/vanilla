//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.06.16 at 11:43:10 AM CEST 
//


package bpm.gateway.core.tsbn.affaires;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.datatype.XMLGregorianCalendar;


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
 *         &lt;element name="ACT" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="EPI_OBS" maxOccurs="unbounded" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;attribute name="ID_OBS" use="required" type="{http://www.w3.org/2001/XMLSchema}IDREF" />
 *                           &lt;attribute name="SRC_INFO" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="PEC_OBS" maxOccurs="unbounded" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;attribute name="ID_OBS" use="required" type="{http://www.w3.org/2001/XMLSchema}IDREF" />
 *                           &lt;attribute name="SRC_INFO" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="ID_PEC" use="required" type="{http://www.w3.org/2001/XMLSchema}IDREF" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="DEC_REF" maxOccurs="unbounded" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;attribute name="ID_DEC" use="required" type="{http://www.w3.org/2001/XMLSchema}IDREF" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="MOY_REF" maxOccurs="unbounded" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;attribute name="ID_MOY" use="required" type="{http://www.w3.org/2001/XMLSchema}IDREF" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="ETAB_REF" maxOccurs="unbounded" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;attribute name="ID_ETAB" use="required" type="{http://www.w3.org/2001/XMLSchema}IDREF" />
 *                           &lt;attribute name="TYP_REP_ETAB" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *                 &lt;attribute name="ID_ACT" use="required" type="{http://www.w3.org/2001/XMLSchema}ID" />
 *                 &lt;attribute name="DAT_DEB_ACT" use="required" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *                 &lt;attribute name="DAT_FIN_ACT" use="required" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *                 &lt;attribute name="TYP_ACT" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="TPS_TEL_IN" type="{http://www.w3.org/2001/XMLSchema}decimal" />
 *                 &lt;attribute name="TPS_TEL_OUT" type="{http://www.w3.org/2001/XMLSchema}decimal" />
 *                 &lt;attribute name="TPS_RAD_IN" type="{http://www.w3.org/2001/XMLSchema}decimal" />
 *                 &lt;attribute name="TPS_RAD_OUT" type="{http://www.w3.org/2001/XMLSchema}decimal" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="ID_PHA" use="required" type="{http://www.w3.org/2001/XMLSchema}ID" />
 *       &lt;attribute name="ID_AGT" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="DAT_DEB" use="required" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *       &lt;attribute name="DAT_FIN" use="required" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *       &lt;attribute name="TYP_PHA" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="CTX" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "act"
})
public class PHA {

    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((idPha == null) ? 0 : idPha.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(!(obj instanceof PHA))
			return false;
		PHA other = (PHA) obj;
		if(idPha == null) {
			if(other.idPha != null)
				return false;
		}
		else if(!idPha.equals(other.idPha))
			return false;
		return true;
	}

	@XmlElement(name = "ACT")
    protected List<ACT> act;
    @XmlAttribute(name = "ID_PHA", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected String idPha;
    @XmlAttribute(name = "ID_AGT", required = true)
    protected String idagt;
    @XmlAttribute(name = "DAT_DEB", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar datdeb;
    @XmlAttribute(name = "DAT_FIN", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar datfin;
    @XmlAttribute(name = "TYP_PHA", required = true)
    protected String typpha;
    @XmlAttribute(name = "CTX")
    protected String ctx;

    /**
     * Gets the value of the act property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the act property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getACT().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ACT }
     * 
     * 
     */
    public List<ACT> getAct() {
        if (act == null) {
            act = new ArrayList<ACT>();
        }
        return this.act;
    }
    
    public void setAct(List<ACT> act) {
    	this.act = act;
    }

    /**
     * Gets the value of the idpha property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdPha() {
        return idPha;
    }

    /**
     * Sets the value of the idpha property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdPha(String value) {
        this.idPha = value;
    }

    /**
     * Gets the value of the idagt property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIDAGT() {
        return idagt;
    }

    /**
     * Sets the value of the idagt property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIDAGT(String value) {
        this.idagt = value;
    }

    /**
     * Gets the value of the datdeb property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public Date getDatdeb() {
    	return toDate(datdeb);
    }

    /**
     * Sets the value of the datdeb property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDatdeb(XMLGregorianCalendar value) {
        this.datdeb = value;
    }
    
    //Not used, for hibernate
    public void setDatdeb(Date value) { }

    /**
     * Gets the value of the datfin property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public Date getDatfin() {
    	return toDate(datfin);
    }

    /**
     * Sets the value of the datfin property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDatfin(XMLGregorianCalendar value) {
        this.datfin = value;
    }
    
    //Not used, for hibernate
    public void setDatfin(Date value) { }

    /**
     * Gets the value of the typpha property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTYPPHA() {
        return typpha;
    }

    /**
     * Sets the value of the typpha property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTYPPHA(String value) {
        this.typpha = value;
    }

    /**
     * Gets the value of the ctx property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCTX() {
        return ctx;
    }

    /**
     * Sets the value of the ctx property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCTX(String value) {
        this.ctx = value;
    }

    private Date toDate(XMLGregorianCalendar calendar){
        if(calendar == null) {
            return null;
        }
        return calendar.toGregorianCalendar().getTime();
    }

	public void setIdSamu(String idSamu) {
		if (act != null) {
			for (ACT ac : act) {
				ac.setIdSamu(idSamu);
			}
		}
		
		String currentId = getIdPha();
		
		String prefix = idSamu + "-";
		if (!currentId.startsWith(prefix)) {
			setIdPha(prefix + currentId);
		}
	}

}
