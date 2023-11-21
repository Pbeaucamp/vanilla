//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.8-b130911.1802 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2020.01.29 à 03:15:22 PM CET 
//


package bpm.gateway.core.veolia.patrimoine;

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
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour typeEquipementPointPrelevement complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="typeEquipementPointPrelevement">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="eqptpointprelev_eqptsocle_id" type="{}equipementIDType"/>
 *         &lt;element name="eqptpointprelev_descriptif" type="{}stringRestricted3000Type"/>
 *         &lt;element name="eqptpointprelev_position" type="{}positionsPointPrelevementListe"/>
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
@Table (name = PatrimoineDAO.PAT_EQPT_POINTPRELEVEMENT)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "typeEquipementPointPrelevement", propOrder = {
    "eqptpointprelevEqptsocleId",
    "eqptpointprelevDescriptif",
    "eqptpointprelevPosition"
})
public class TypeEquipementPointPrelevement {

	@Id
	@Column(name = "eqptpointprelev_id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@XmlTransient
    protected int id;
	@Transient
    @XmlElement(name = "eqptpointprelev_eqptsocle_id", required = true)
    protected String eqptpointprelevEqptsocleId;
	@Transient
    @XmlElement(name = "eqptpointprelev_descriptif", required = true, defaultValue = "NR")
    protected String eqptpointprelevDescriptif;
	@Transient
    @XmlElement(name = "eqptpointprelev_position", required = true)
    @XmlSchemaType(name = "string")
    protected PositionsPointPrelevementListe eqptpointprelevPosition;
	@Column(name = "id_chg")
	@XmlTransient
	private Integer idChg;

    /**
     * Obtient la valeur de la propriété eqptpointprelevEqptsocleId.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptpointprelev_eqptsocle_id")
    public String getEqptpointprelevEqptsocleId() {
		if (eqptpointprelevEqptsocleId == null) {
    		manageNullException("eqptpointprelev_eqptsocle_id");
    	}
        return eqptpointprelevEqptsocleId;
    }

    /**
     * Définit la valeur de la propriété eqptpointprelevEqptsocleId.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEqptpointprelevEqptsocleId(String value) {
        this.eqptpointprelevEqptsocleId = value;
    }

    /**
     * Obtient la valeur de la propriété eqptpointprelevDescriptif.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptpointprelev_descriptif")
    public String getEqptpointprelevDescriptif() {
		if (eqptpointprelevDescriptif == null) {
    		manageNullException("eqptpointprelev_descriptif");
    	}
        return eqptpointprelevDescriptif;
    }

    /**
     * Définit la valeur de la propriété eqptpointprelevDescriptif.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEqptpointprelevDescriptif(String value) {
        this.eqptpointprelevDescriptif = value;
    }

    /**
     * Obtient la valeur de la propriété eqptpointprelevPosition.
     * 
     * @return
     *     possible object is
     *     {@link PositionsPointPrelevementListe }
     *     
     */
	@Access(AccessType.PROPERTY)
	@Column(name = "eqptpointprelev_position")
    public String getEqptpointprelevPosition() {
        return PositionsPointPrelevementListe.getValue(PatrimoineDAO.PAT_EQPT_POINTPRELEVEMENT, "eqptpointprelev_position", eqptpointprelevPosition, true);
    }

    /**
     * Définit la valeur de la propriété eqptpointprelevPosition.
     * 
     * @param value
     *     allowed object is
     *     {@link PositionsPointPrelevementListe }
     *     
     */
    public void setEqptpointprelevPosition(String value) {
        this.eqptpointprelevPosition = PositionsPointPrelevementListe.fromValue(PatrimoineDAO.PAT_EQPT_POINTPRELEVEMENT, "eqptpointprelev_position", value);
    }

	public void setIdChg(Integer idChg) {
		this.idChg = idChg;
	}

    private void manageNullException(String champ) {
    	throw new IllegalArgumentException("Table " + PatrimoineDAO.PAT_EQPT_POINTPRELEVEMENT + " - Champs " + champ + " - Valeur 'Null' ou non permise par le XSD.");
	}

}
