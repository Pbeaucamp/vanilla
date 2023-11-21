//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.8-b130911.1802 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2021.11.18 à 11:54:14 AM CET 
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
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="code_librairie" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="titre_publication" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="editeur" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="annee" type="{http://www.w3.org/2001/XMLSchema}short"/>
 *         &lt;element name="collection" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
    "codeLibrairie",
    "titrePublication",
    "editeur",
    "annee",
    "collection"
})
public class Publication implements IExportCSV {

    @XmlElement(name = "code_librairie")
    protected long codeLibrairie;
    @XmlElement(name = "titre_publication", required = true)
    protected String titrePublication;
    @XmlElement(required = true)
    protected String editeur;
    protected short annee;
    @XmlElement(required = true)
    protected String collection;

    /**
     * Obtient la valeur de la propriété codeLibrairie.
     * 
     */
    public long getCodeLibrairie() {
        return codeLibrairie;
    }

    /**
     * Définit la valeur de la propriété codeLibrairie.
     * 
     */
    public void setCodeLibrairie(long value) {
        this.codeLibrairie = value;
    }

    /**
     * Obtient la valeur de la propriété titrePublication.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTitrePublication() {
        return titrePublication;
    }

    /**
     * Définit la valeur de la propriété titrePublication.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTitrePublication(String value) {
        this.titrePublication = value;
    }

    /**
     * Obtient la valeur de la propriété editeur.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEditeur() {
        return editeur;
    }

    /**
     * Définit la valeur de la propriété editeur.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEditeur(String value) {
        this.editeur = value;
    }

    /**
     * Obtient la valeur de la propriété annee.
     * 
     */
    public short getAnnee() {
        return annee;
    }

    /**
     * Définit la valeur de la propriété annee.
     * 
     */
    public void setAnnee(short value) {
        this.annee = value;
    }

    /**
     * Obtient la valeur de la propriété collection.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCollection() {
        return collection;
    }

    /**
     * Définit la valeur de la propriété collection.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCollection(String value) {
        this.collection = value;
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
			line.add("codeLibrairie");
			line.add("titrePublication");
			line.add("editeur");
			line.add("annee");
			line.add("collection");
			lines.add(line);
			
			items.put(keyId, lines);
		}
		
		List<Object> line = new ArrayList<Object>();
		line.add(parentId);
		line.add(codeLibrairie);
		line.add(titrePublication);
		line.add(editeur);
		line.add(annee);
		line.add(collection);

		List<List<Object>> values = items.get(keyId);
		values.add(line);
	}

}
