//
// Ce fichier a �t� g�n�r� par l'impl�mentation de r�f�rence JavaTM Architecture for XML Binding (JAXB), v2.2.8-b130911.1802 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apport�e � ce fichier sera perdue lors de la recompilation du sch�ma source. 
// G�n�r� le : 2021.11.18 � 11:43:38 AM CET 
//


package bpm.vanillahub.runtime.specifics.onisep;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import bpm.vanillahub.runtime.specifics.IExportCSV;


/**
 * <p>Classe Java pour anonymous complex type.
 * 
 * <p>Le fragment de sch�ma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="id_ideo1" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="libelle" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
    "id",
    "idIdeo1",
    "libelle"
})
public class Statut implements IExportCSV {

    @XmlElement(required = true)
    protected String id;
    @XmlElement(name = "id_ideo1")
    protected int idIdeo1;
    @XmlElement(required = true)
    protected String libelle;

    /**
     * Obtient la valeur de la propri�t� id.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * D�finit la valeur de la propri�t� id.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Obtient la valeur de la propri�t� idIdeo1.
     * 
     */
    public int getIdIdeo1() {
        return idIdeo1;
    }

    /**
     * D�finit la valeur de la propri�t� idIdeo1.
     * 
     */
    public void setIdIdeo1(int value) {
        this.idIdeo1 = value;
    }

    /**
     * Obtient la valeur de la propri�t� libelle.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLibelle() {
        return libelle;
    }

    /**
     * D�finit la valeur de la propri�t� libelle.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLibelle(String value) {
        this.libelle = value;
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
			line.add("parentId");
			line.add("id");
			line.add("idIdeo1");
			line.add("libelle");
			lines.add(line);
			
			items.put(keyId, lines);
		}
		
		List<Object> line = new ArrayList<Object>();
		line.add(parentId);
		line.add(id);
		line.add(idIdeo1);
		line.add(libelle);

		List<List<Object>> values = items.get(keyId);
		values.add(line);
	}

}
