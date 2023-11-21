package bpm.vanillahub.runtime.run.transform;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "prix")
public class Prix {

	private long id;
	private String nom;
	private String maj;
	private Double valeur;

	public long getId() {
		return id;
	}

    @XmlAttribute(name = "id")
	public void setId(long id) {
		this.id = id;
	}

	public String getNom() {
		return nom;
	}

    @XmlAttribute(name = "nom")
	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getMaj() {
		return maj;
	}

    @XmlAttribute(name = "maj")
	public void setMaj(String maj) {
		this.maj = maj;
	}

	public Double getValeur() {
		return valeur;
	}

    @XmlAttribute(name = "valeur")
	public void setValeur(Double valeur) {
		this.valeur = valeur;
	}

}
