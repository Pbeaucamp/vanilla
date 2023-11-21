//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.01.12 at 03:19:46 PM CET 
//


package bpm.gateway.core.tsbn.affaires;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;
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
 *       &lt;attribute name="ID_EQU" use="required" type="{http://www.w3.org/2001/XMLSchema}ID" />
 *       &lt;attribute name="EQU_LAB" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /> 
 *       &lt;attribute name="ID_MOY_TSP" type="{http://www.w3.org/2001/XMLSchema}IDREF" />
 *       &lt;attribute name="ID_MOY_MED" type="{http://www.w3.org/2001/XMLSchema}IDREF" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
public class EQU {

    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((idEqu == null) ? 0 : idEqu.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(!(obj instanceof EQU))
			return false;
		EQU other = (EQU) obj;
		if(idEqu == null) {
			if(other.idEqu != null)
				return false;
		}
		else if(!idEqu.equals(other.idEqu))
			return false;
		return true;
	}

	@XmlAttribute(name = "ID_EQU", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected String idEqu;
    @XmlAttribute(name = "EQU_LAB", required = true)
    protected String equlab;
    @XmlAttribute(name = "ID_MOY_TSP")
    @XmlIDREF
    @XmlSchemaType(name = "IDREF")
    protected Object idmoytsp;
    @XmlAttribute(name = "ID_MOY_MED")
    @XmlIDREF
    @XmlSchemaType(name = "IDREF")
    protected Object idmoymed;

    /**
     * Gets the value of the idequ property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdEqu() {
        return idEqu;
    }

    /**
     * Sets the value of the idequ property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdEqu(String value) {
        this.idEqu = value;
    }

    /**
     * Gets the value of the equlab property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEQULAB() {
        return equlab;
    }

    /**
     * Sets the value of the equlab property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEQULAB(String value) {
        this.equlab = value;
    }
    
    /**
     * Gets the value of the idmoytsp property.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getIdmoytsp() {
        return idmoytsp != null ? ((MOY) idmoytsp).getIdMoy() : null;
    }

    /**
     * Sets the value of the idmoytsp property.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setIdmoytsp(Object value) {
        if(!(value instanceof String)) {
    		this.idmoytsp = value;
    	}
    }

    /**
     * Gets the value of the idmoymed property.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getIdmoymed() {
        return idmoymed != null ? ((MOY) idmoymed).getIdMoy() : null;
    }

    /**
     * Sets the value of the idmoymed property.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setIdmoymed(Object value) {
        if(!(value instanceof String)) {
    		this.idmoymed = value;
    	}
    }

	public void setIdSamu(String idSamu) {
		String currentId = getIdEqu();
		
		String prefix = idSamu + "-";
		if (!currentId.startsWith(prefix)) {
			setIdEqu(prefix + currentId);
		}
	}

}
