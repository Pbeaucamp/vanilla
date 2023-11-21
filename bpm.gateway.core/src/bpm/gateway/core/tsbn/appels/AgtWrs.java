//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.06.16 at 11:44:33 AM CEST 
//
package bpm.gateway.core.tsbn.appels;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * <p>
 * Java class for anonymous complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence minOccurs="0">
 *         &lt;element name="agt_sta" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="typ_sta" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="dur_sta" type="{http://www.w3.org/2001/XMLSchema}float" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="id_struct" use="required" type="{http://www.w3.org/2001/XMLSchema}IDREF" />
 *       &lt;attribute name="id_agent" use="required" type="{http://www.w3.org/2001/XMLSchema}IDREF" />
 *       &lt;attribute name="tps_tot" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "agtSta" })
public class AgtWrs {

	@XmlElement(name = "agt_sta")
	protected List<AgtSta> agtSta;
	@XmlAttribute(name = "id_struct", required = true)
	@XmlIDREF
	@XmlSchemaType(name = "IDREF")
	protected Object idStruct;
	@XmlAttribute(name = "id_agent", required = true)
	@XmlIDREF
	@XmlSchemaType(name = "IDREF")
	protected Object idAgent;
	@XmlAttribute(name = "tps_tot")
	protected String tpsTot;
	@XmlAttribute(name = "dat_deb")
	@XmlSchemaType(name = "dateTime")
	protected XMLGregorianCalendar datDeb;
	@XmlAttribute(name = "dat_fin")
	@XmlSchemaType(name = "dateTime")
	protected XMLGregorianCalendar datFin;
	@XmlAttribute(name = "agt_fct")
	protected String agtFct;

	/**
	 * Gets the value of the agtSta property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the agtSta property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getAgtSta().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link AgtSta }
	 * 
	 * 
	 */
	public List<AgtSta> getAgtSta() {
		if (agtSta == null) {
			agtSta = new ArrayList<AgtSta>();
		}
		return this.agtSta;
	}

	// Not used, for hibernate
	public void setAgtSta(List<AgtSta> agtSta) {
		this.agtSta = agtSta;
	}

	/**
	 * Gets the value of the idStruct property.
	 * 
	 * @return possible object is {@link Object }
	 * 
	 */
	public Object getIdStruct() {
		return ((Structures) idStruct).getIdStruct();
	}

	/**
	 * Sets the value of the idStruct property.
	 * 
	 * @param value
	 *            allowed object is {@link Object }
	 * 
	 */
	public void setIdStruct(Object value) {
		if (value instanceof String) {
			// Do nothing
		}
		else {
			this.idStruct = value;
		}
	}

    /**
     * Gets the value of the idAgent property.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getIdAgent() {
        return idAgent != null ? ((Agents) idAgent).getIdAgent() : null;
    }

    /**
     * Sets the value of the idAgent property.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setIdAgent(Object value) {
    	if(!(value instanceof String)) {
    		this.idAgent = value;
    	}
    }

	/**
	 * Gets the value of the tpsTot property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getTpsTot() {
		return tpsTot;
	}

	/**
	 * Sets the value of the tpsTot property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setTpsTot(String value) {
		this.tpsTot = value;
	}

	/**
	 * Gets the value of the datDeb property.
	 * 
	 * @return possible object is {@link XMLGregorianCalendar }
	 * 
	 */
	public Date getDatDeb() {
		return toDate(datDeb);
	}

	/**
	 * Sets the value of the datDeb property.
	 * 
	 * @param value
	 *            allowed object is {@link XMLGregorianCalendar }
	 * 
	 */
	public void setDatDeb(XMLGregorianCalendar value) {
		this.datDeb = value;
	}

	// Not used, for hibernate
	public void setDatDeb(Date value) {
	}

	/**
	 * Gets the value of the datFin property.
	 * 
	 * @return possible object is {@link XMLGregorianCalendar }
	 * 
	 */
	public Date getDatFin() {
		return toDate(datFin);
	}

	/**
	 * Sets the value of the datFin property.
	 * 
	 * @param value
	 *            allowed object is {@link XMLGregorianCalendar }
	 * 
	 */
	public void setDatFin(XMLGregorianCalendar value) {
		this.datFin = value;
	}

	// Not used, for hibernate
	public void setDatFin(Date value) {
	}

	/**
	 * Gets the value of the agtFct property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getAgtFct() {
		return agtFct;
	}

	/**
	 * Sets the value of the agtFct property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setAgtFct(String value) {
		this.agtFct = value;
	}

	private Date toDate(XMLGregorianCalendar calendar) {
		if (calendar == null) {
			return null;
		}
		return calendar.toGregorianCalendar().getTime();
	}

	public void setIdSamu(String idSamu) {
		if (agtSta != null) {
			for (AgtSta agtSt : agtSta) {
				agtSt.setIdSamu(idSamu);
			}
		}
	}

}
