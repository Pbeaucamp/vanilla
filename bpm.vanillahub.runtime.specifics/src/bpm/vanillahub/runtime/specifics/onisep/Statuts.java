//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.8-b130911.1802 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2021.11.18 à 11:43:38 AM CET 
//


package bpm.vanillahub.runtime.specifics.onisep;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
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
 *         &lt;element name="statut" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="id_ideo1" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *                   &lt;element name="libelle" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
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
    "statut"
})
public class Statuts implements IExportCSV {

    protected List<Statut> statut;

    /**
     * Gets the value of the statut property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the statut property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getStatut().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Statut }
     * 
     * 
     */
    public List<Statut> getStatut() {
        if (statut == null) {
            statut = new ArrayList<Statut>();
        }
        return this.statut;
    }
	
	@Override
	public String getClassKeyID() {
		return getClass().getName();
	}

	@Override
	public void buildCSV(String parentId, HashMap<String, List<List<Object>>> items) {
		String keyId = getClassKeyID();
		if (items.get(keyId) == null) {
			items.put(keyId, new ArrayList<List<Object>>());
		}
		
		if (statut != null) {
			for (Statut item : statut)  {
				item.buildCSV(parentId, items);
			}
		}
	}

}
