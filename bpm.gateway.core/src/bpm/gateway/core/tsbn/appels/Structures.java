//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.06.16 at 11:44:33 AM CEST 
//


package bpm.gateway.core.tsbn.appels;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="id_struct" use="required" type="{http://www.w3.org/2001/XMLSchema}ID" />
 *       &lt;attribute name="region" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="c15" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="struct_lab" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="com" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="com_lab" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="typ_struct" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="ctx_ext" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="fct_ext" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
public class Structures {

    @XmlAttribute(name = "id_struct", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected String idStruct;
    @XmlAttribute(name = "region")
    protected String region;
    @XmlAttribute(name = "c15")
    protected String c15;
    @XmlAttribute(name = "struct_lab")
    protected String structLab;
    @XmlAttribute(name = "com")
    protected String com;
    @XmlAttribute(name = "com_lab")
    protected String comLab;
    @XmlAttribute(name = "typ_struct")
    protected String typStruct;
    @XmlAttribute(name = "ctx_ext", required = true)
    protected String ctxExt;
    @XmlAttribute(name = "ctx_alt")
    protected String ctxAlt;
    @XmlAttribute(name = "fct_ext", required = true)
    protected String fctExt;

    /**
     * Gets the value of the idStruct property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdStruct() {
        return idStruct;
    }

    /**
     * Sets the value of the idStruct property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdStruct(String value) {
        this.idStruct = value;
    }

    /**
     * Gets the value of the region property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRegion() {
        return region;
    }

    /**
     * Sets the value of the region property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRegion(String value) {
        this.region = value;
    }

    /**
     * Gets the value of the c15 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getC15() {
        return c15;
    }

    /**
     * Sets the value of the c15 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setC15(String value) {
        this.c15 = value;
    }

    /**
     * Gets the value of the structLab property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStructLab() {
        return structLab;
    }

    /**
     * Sets the value of the structLab property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStructLab(String value) {
        this.structLab = value;
    }

    /**
     * Gets the value of the com property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCom() {
        return com;
    }

    /**
     * Sets the value of the com property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCom(String value) {
        this.com = value;
    }

    /**
     * Gets the value of the comLab property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getComLab() {
        return comLab;
    }

    /**
     * Sets the value of the comLab property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setComLab(String value) {
        this.comLab = value;
    }

    /**
     * Gets the value of the typStruct property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTypStruct() {
        return typStruct;
    }

    /**
     * Sets the value of the typStruct property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTypStruct(String value) {
        this.typStruct = value;
    }

    /**
     * Gets the value of the ctxExt property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCtxExt() {
        return ctxExt;
    }

    /**
     * Sets the value of the ctxExt property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCtxExt(String value) {
        this.ctxExt = value;
    }

    /**
     * Gets the value of the ctxAlt property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCtxAlt() {
        return ctxAlt;
    }

    /**
     * Sets the value of the ctxAlt property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCtxAlt(String value) {
        this.ctxAlt = value;
    }

    /**
     * Gets the value of the fctExt property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFctExt() {
        return fctExt;
    }

    /**
     * Sets the value of the fctExt property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFctExt(String value) {
        this.fctExt = value;
    }

	public void setIdSamu(String idSamu) {
//		String currentId = getIdStruct();
//		
//		String prefix = idSamu + "-";
//		if (!currentId.startsWith(prefix)) {
//			setIdStruct(prefix + currentId);
//		}
	}

}
