package bpm.vanillahub.runtime.run.transform;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "pdv_liste")
@XmlAccessorType(XmlAccessType.FIELD)
public class PointDeVentes {
	
	@XmlElement(name = "pdv")
	protected List<PointDeVente> pointDeVentes;
	
	public List<PointDeVente> getPointDeVentes() {
		return pointDeVentes;
	}
	
	public void setPointDeVentes(List<PointDeVente> pointDeVentes) {
		this.pointDeVentes = pointDeVentes;
	}

}
