package bpm.vanillahub.runtime.run.transform;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "pdv")
public class PointDeVente {

	private long id;
	private String latitude;
	private String longitude;
	private String cp;
	private String pop;
	private String adresse;
	private String ville;

	private List<Prix> prix;

	public PointDeVente() {
	}

	public long getId() {
		return id;
	}

    @XmlAttribute(name = "id")
	public void setId(long id) {
		this.id = id;
	}

	public String getLatitude() {
		return latitude;
	}

    @XmlAttribute(name = "latitude")
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

    @XmlAttribute(name = "longitude")
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getCp() {
		return cp;
	}

    @XmlAttribute(name = "cp")
	public void setCp(String cp) {
		this.cp = cp;
	}

	public String getPop() {
		return pop;
	}

    @XmlAttribute(name = "pop")
	public void setPop(String pop) {
		this.pop = pop;
	}

	public String getAdresse() {
		return adresse;
	}

    @XmlElement(name = "adresse")
	public void setAdresse(String adresse) {
		this.adresse = adresse;
	}

	public String getVille() {
		return ville;
	}

    @XmlElement(name = "ville")
	public void setVille(String ville) {
		this.ville = ville;
	}

	public List<Prix> getPrix() {
		return prix;
	}

	public void setPrix(List<Prix> prix) {
		this.prix = prix;
	}

}
