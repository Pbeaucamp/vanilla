package bpm.gateway.core.veolia.patrimoine.xls;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import bpm.gateway.core.veolia.patrimoine.PatrimoineDAO;

@Entity
@Access(AccessType.FIELD)
@Table (name = PatrimoineDAO.UNITE_ULT)
public class UniteUlt {

	@Id
	@Column(name = "uniteult_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Transient
	private String uniteultUnitesocleId;

	//TODO: Liste
	@Transient
	private String uniteultTypemembrane;

	@Transient
	private Double uniteultNbmodule;

	@Transient
	private Double uniteultNbprefiltre;

	@Transient
	private Double uniteultNbblocultra;

	@Transient
	private String uniteultModefiltration;

	@Transient
	private Double uniteultFluxfiltration;

	@Transient
	private Double uniteultPressiontransmembranaire;

	@Transient
	private Double uniteultNbpompegavage;

	@Transient
	private String uniteultTypecontroleintegrite;

	@Transient
	private Double uniteultSurfacemembtot;

	//TODO: Liste
	@Transient
	private String uniteultPrefiltre;

	@Column(name = "uniteult_seuilcoupure")
	private String uniteultSeuilcoupure;

	@Column(name = "uniteult_nettoyageprefiltre")
	private String uniteultNettoyageprefiltre;

	@Column(name = "id_chg")
	private Integer idChg;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "uniteult_unitesocle_id")
	public String getUniteultUnitesocleId() {
		if (uniteultUnitesocleId == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_ULT + " - Champs uniteult_unitesocle_id - Valeur 'Null' non permise.");
		}
		return uniteultUnitesocleId;
	}

	public void setUniteultUnitesocleId(String uniteultUnitesocleId) {
		this.uniteultUnitesocleId = uniteultUnitesocleId;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "uniteult_typemembrane")
	public String getUniteultTypemembrane() {
		if (uniteultTypemembrane == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_ULT + " - Champs uniteult_typemembrane - Valeur 'Null' non permise.");
		}
		return uniteultTypemembrane;
	}

	public void setUniteultTypemembrane(String uniteultTypemembrane) {
		this.uniteultTypemembrane = uniteultTypemembrane;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "uniteult_nbmodule")
	public Double getUniteultNbmodule() {
		if (uniteultNbmodule == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_ULT + " - Champs uniteult_nbmodule - Valeur 'Null' non permise.");
		}
		return uniteultNbmodule;
	}

	public void setUniteultNbmodule(Double uniteultNbmodule) {
		this.uniteultNbmodule = uniteultNbmodule;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "uniteult_nbprefiltre")
	public Double getUniteultNbprefiltre() {
		if (uniteultNbprefiltre == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_ULT + " - Champs uniteult_nbprefiltre - Valeur 'Null' non permise.");
		}
		return uniteultNbprefiltre;
	}

	public void setUniteultNbprefiltre(Double uniteultNbprefiltre) {
		this.uniteultNbprefiltre = uniteultNbprefiltre;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "uniteult_nbblocultra")
	public Double getUniteultNbblocultra() {
		if (uniteultNbblocultra == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_ULT + " - Champs uniteult_nbblocultra - Valeur 'Null' non permise.");
		}
		return uniteultNbblocultra;
	}

	public void setUniteultNbblocultra(Double uniteultNbblocultra) {
		this.uniteultNbblocultra = uniteultNbblocultra;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "uniteult_modefiltration")
	public String getUniteultModefiltration() {
		if (uniteultModefiltration == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_ULT + " - Champs uniteult_modefiltration - Valeur 'Null' non permise.");
		}
		return uniteultModefiltration;
	}

	public void setUniteultModefiltration(String uniteultModefiltration) {
		this.uniteultModefiltration = uniteultModefiltration;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "uniteult_fluxfiltration")
	public Double getUniteultFluxfiltration() {
		if (uniteultFluxfiltration == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_ULT + " - Champs uniteult_fluxfiltration - Valeur 'Null' non permise.");
		}
		return uniteultFluxfiltration;
	}

	public void setUniteultFluxfiltration(Double uniteultFluxfiltration) {
		this.uniteultFluxfiltration = uniteultFluxfiltration;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "uniteult_pressiontransmembranaire")
	public Double getUniteultPressiontransmembranaire() {
		if (uniteultPressiontransmembranaire == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_ULT + " - Champs uniteult_pressiontransmembranaire - Valeur 'Null' non permise.");
		}
		return uniteultPressiontransmembranaire;
	}

	public void setUniteultPressiontransmembranaire(Double uniteultPressiontransmembranaire) {
		this.uniteultPressiontransmembranaire = uniteultPressiontransmembranaire;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "uniteult_nbpompegavage")
	public Double getUniteultNbpompegavage() {
		if (uniteultNbpompegavage == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_ULT + " - Champs uniteult_nbpompegavage - Valeur 'Null' non permise.");
		}
		return uniteultNbpompegavage;
	}

	public void setUniteultNbpompegavage(Double uniteultNbpompegavage) {
		this.uniteultNbpompegavage = uniteultNbpompegavage;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "uniteult_typecontroleintegrite")
	public String getUniteultTypecontroleintegrite() {
		if (uniteultTypecontroleintegrite == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_ULT + " - Champs uniteult_typecontroleintegrite - Valeur 'Null' non permise.");
		}
		return uniteultTypecontroleintegrite;
	}

	public void setUniteultTypecontroleintegrite(String uniteultTypecontroleintegrite) {
		this.uniteultTypecontroleintegrite = uniteultTypecontroleintegrite;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "uniteult_surfacemembtot")
	public Double getUniteultSurfacemembtot() {
		if (uniteultSurfacemembtot == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_ULT + " - Champs uniteult_surfacemembtot - Valeur 'Null' non permise.");
		}
		return uniteultSurfacemembtot;
	}

	public void setUniteultSurfacemembtot(Double uniteultSurfacemembtot) {
		this.uniteultSurfacemembtot = uniteultSurfacemembtot;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "uniteult_prefiltre")
	public String getUniteultPrefiltre() {
		if (uniteultPrefiltre == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_ULT + " - Champs uniteult_prefiltre - Valeur 'Null' non permise.");
		}
		return uniteultPrefiltre;
	}

	public void setUniteultPrefiltre(String uniteultPrefiltre) {
		this.uniteultPrefiltre = uniteultPrefiltre;
	}

	public String getUniteultSeuilcoupure() {
		return uniteultSeuilcoupure;
	}

	public void setUniteultSeuilcoupure(String uniteultSeuilcoupure) {
		this.uniteultSeuilcoupure = uniteultSeuilcoupure;
	}

	public String getUniteultNettoyageprefiltre() {
		return uniteultNettoyageprefiltre;
	}

	public void setUniteultNettoyageprefiltre(String uniteultNettoyageprefiltre) {
		this.uniteultNettoyageprefiltre = uniteultNettoyageprefiltre;
	}

	public Integer getIdChg() {
		return idChg;
	}

	public void setIdChg(Integer idChg) {
		this.idChg = idChg;
	}
}
