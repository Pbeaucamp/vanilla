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
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlTransient;
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
 *       &lt;attribute name="code_file" use="required" type="{http://www.w3.org/2001/XMLSchema}IDREF" />
 *       &lt;attribute name="id_agent" type="{http://www.w3.org/2001/XMLSchema}IDREF" />
 *       &lt;attribute name="tba" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="tna" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="tia" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="tta" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="tid" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="nb_pres" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="dur_app" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="dur_apd" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="flg_distr_1" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="flg_distr_2" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
public class AppEntrant {
	
	@XmlTransient
	private int id;

	@XmlAttribute(name = "code_file", required = true)
    @XmlIDREF
    @XmlSchemaType(name = "IDREF")
    protected Object codeFile;
    @XmlAttribute(name = "app_typ")
    protected String appType;
    @XmlAttribute(name = "id_struct")
    @XmlIDREF
    @XmlSchemaType(name = "IDREF")
    protected Object idStruct;
    @XmlAttribute(name = "id_agent")
    @XmlIDREF
    @XmlSchemaType(name = "IDREF")
    protected Object idAgent;
    @XmlAttribute(name = "tba", required = true)
    protected float tba;
    @XmlAttribute(name = "tna")
    protected Float tna;
    @XmlAttribute(name = "tia")
    protected Float tia;
    @XmlAttribute(name = "tta")
    protected Float tta;
    @XmlAttribute(name = "tid")
    protected Float tid;
    @XmlAttribute(name = "nb_pres")
    protected Float nbPres;
    @XmlAttribute(name = "dur_app")
    protected Float durApp;
    @XmlAttribute(name = "dur_apd")
    protected Float durApd;
    @XmlAttribute(name = "flg_distr_1", required = true)
    protected String flgDistr1;
    @XmlAttribute(name = "flg_distr_2", required = true)
    protected String flgDistr2;
    @XmlAttribute(name = "flg_debord")
    protected String flgDebord;
    @XmlAttribute(name = "lnk")
    protected String lnk;
    @XmlAttribute(name = "org")
    protected String org;

    public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
    
    /**
     * Gets the value of the codeFile property.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getCodeFile() {
        return codeFile != null ? ((FilesAppels) codeFile).getCodeFile() : null;
    }

    /**
     * Sets the value of the codeFile property.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setCodeFile(Object value) {
    	if(!(value instanceof String)) {
    		this.codeFile = value;
    	}
    }

    public String getAppType() {
        return appType;
    }

    public void setAppType(String value) {
        this.appType = value;
    }

    /**
     * Gets the value of the idStruct property.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getIdStruct() {
        return idStruct != null ? ((Structures) idStruct).getIdStruct() : null;
    }

    /**
     * Sets the value of the idStruct property.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setIdStruct(Object value) {
    	if(!(value instanceof String)) {
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
     * Gets the value of the tba property.
     * 
     */
    public float getTba() {
        return tba;
    }

    /**
     * Sets the value of the tba property.
     * 
     */
    public void setTba(float value) {
        this.tba = value;
    }

    /**
     * Gets the value of the tna property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Float getTna() {
        return tna;
    }

    /**
     * Sets the value of the tna property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setTna(Float value) {
        this.tna = value;
    }

    /**
     * Gets the value of the tia property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Float getTia() {
        return tia;
    }

    /**
     * Sets the value of the tia property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setTia(Float value) {
        this.tia = value;
    }

    /**
     * Gets the value of the tta property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Float getTta() {
        return tta;
    }

    /**
     * Sets the value of the tta property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setTta(Float value) {
        this.tta = value;
    }

    /**
     * Gets the value of the tid property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Float getTid() {
        return tid;
    }

    /**
     * Sets the value of the tid property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setTid(Float value) {
        this.tid = value;
    }

    /**
     * Gets the value of the nbPres property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Float getNbPres() {
        return nbPres;
    }

    /**
     * Sets the value of the nbPres property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setNbPres(Float value) {
        this.nbPres = value;
    }

    /**
     * Gets the value of the durApp property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Float getDurApp() {
        return durApp;
    }

    /**
     * Sets the value of the durApp property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setDurApp(Float value) {
        this.durApp = value;
    }

    /**
     * Gets the value of the durApd property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Float getDurApd() {
        return durApd;
    }

    /**
     * Sets the value of the durApd property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setDurApd(Float value) {
        this.durApd = value;
    }

    /**
     * Gets the value of the flgDistr1 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFlgDistr1() {
        return flgDistr1;
    }

    /**
     * Sets the value of the flgDistr1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFlgDistr1(String value) {
        this.flgDistr1 = value;
    }

    /**
     * Gets the value of the flgDistr2 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFlgDistr2() {
        return flgDistr2;
    }

    /**
     * Sets the value of the flgDistr2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFlgDistr2(String value) {
        this.flgDistr2 = value;
    }

    /**
     * Gets the value of the flgDebord property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFlgDebord() {
		return flgDebord;
	}

    /**
     * Sets the value of the flgDebord property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFlgDebord(String flgDebord) {
		this.flgDebord = flgDebord;
	}

    /**
     * Gets the value of the lnk property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLnk() {
		return lnk;
	}

    /**
     * Sets the value of the lnk property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLnk(String lnk) {
		this.lnk = lnk;
	}

    /**
     * Gets the value of the org property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrg() {
		return org;
	}

    /**
     * Sets the value of the org property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrg(String org) {
		this.org = org;
	}

	public void setIdSamu(String idSamu) {
		//Do nothing
	}

}
